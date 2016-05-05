/*
 * Copyright (c) 2008-2016 Computer Network Information Center (CNIC), Chinese Academy of Sciences.
 * 
 * This file is part of Duckling project.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *   http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License. 
 *
 */

package net.duckling.ddl.web.controller;

import java.io.IOException;
import java.io.Writer;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.ResourceBundle;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import net.duckling.ddl.common.VWBContext;
import net.duckling.ddl.constant.ELayout;
import net.duckling.ddl.constant.LynxConstants;
import net.duckling.ddl.service.diff.DifferenceService;
import net.duckling.ddl.service.draft.Draft;
import net.duckling.ddl.service.draft.IDraftService;
import net.duckling.ddl.service.file.FileVersion;
import net.duckling.ddl.service.file.FileVersionService;
import net.duckling.ddl.service.grid.IGridService;
import net.duckling.ddl.service.render.RenderingService;
import net.duckling.ddl.service.render.dml.HtmlStringToDMLTranslator;
import net.duckling.ddl.service.resource.IResourceService;
import net.duckling.ddl.service.resource.ITagService;
import net.duckling.ddl.service.resource.PageHelper;
import net.duckling.ddl.service.resource.PageLock;
import net.duckling.ddl.service.resource.PageLockService;
import net.duckling.ddl.service.resource.PageRender;
import net.duckling.ddl.service.resource.PageVersion;
import net.duckling.ddl.service.resource.PageVersionService;
import net.duckling.ddl.service.resource.Resource;
import net.duckling.ddl.service.resource.ResourceOperateService;
import net.duckling.ddl.service.resource.Tag;
import net.duckling.ddl.service.resource.TeamSpaceSizeService;
import net.duckling.ddl.service.team.Team;
import net.duckling.ddl.service.team.TeamService;
import net.duckling.ddl.service.url.URLGenerator;
import net.duckling.ddl.service.url.UrlPatterns;
import net.duckling.ddl.util.CommonUtil;
import net.duckling.ddl.util.HTMLConvertUtil;
import net.duckling.ddl.util.JsonUtil;
import net.duckling.ddl.util.TextUtil;
import net.duckling.ddl.web.bean.AttachmentItem;
import net.duckling.ddl.web.interceptor.access.OnDeny;
import net.duckling.ddl.web.interceptor.access.RequirePermission;

import org.apache.commons.lang.StringEscapeUtils;
import org.apache.commons.lang.StringUtils;
import org.apache.log4j.Logger;
import org.jdom.JDOMException;
import org.json.simple.JSONObject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.ModelAndView;
import org.springframework.web.servlet.view.RedirectView;

import cn.cnic.cerc.dlog.client.WebLog;

/**
 * 编辑器和浏览的功能都在这里
 * 
 * @date 2012-05-07
 * @author Clive Lee
 */

@Controller
@RequestMapping("/{teamCode}/edit")
@RequirePermission(target = "team", operation = "edit")
public class LynxEditPageController extends BaseController {
    private static final Logger LOG = Logger.getLogger(LynxEditPageController.class);
	@Autowired
	private TeamService teamService;
    @Autowired
    private IResourceService resourceService;
    @Autowired
    private ITagService tagService;
    @Autowired
    private IGridService gridService;
    @Autowired
    private IDraftService draftService;
    @Autowired
    private DifferenceService differenceService;
    @Autowired
    private RenderingService renderingService;
    @Autowired
    private PageVersionService pageVersionService;
    @Autowired
    private PageLockService pageLockService;
    @Autowired
    private ResourceOperateService resourceOperateService;
    @Autowired
    private FileVersionService fileVersionService;
    @Autowired
    private URLGenerator urlGenerator;
    @Autowired
    private TeamSpaceSizeService teamSpaceSizeService;
    @OnDeny({ "autoSave", "save" })
    public void saveDenied(String methodName, HttpServletRequest request, HttpServletResponse response) {
        VWBContext context = VWBContext.createContext(request, UrlPatterns.PLAIN);
        ResourceBundle rb = context.getBundle("templates/default");
        writeToResponse(response, rb.getString("action.savepage.noright"));
    }

    @RequestMapping
    @WebLog(method = "loadEditMode", params = "pid,bid")
    public ModelAndView prepareEditPage(HttpServletRequest request, @RequestParam("rid") Integer rid) {
        VWBContext context = getEditPageContext(request, rid);
        int tid = VWBContext.getCurrentTid();
        int parentRid = getPatentRidFromRequest(request);
        PageRender render = getPageRender(rid, context);
        context.setWysiwygEditorMode(VWBContext.EDITOR_MODE);
        int version = getRequestVersion(request);
        if (version != VWBContext.LATEST_VERSION && version != render.getMeta().getLastVersion()) {
            request.setAttribute("version", version);
            request.setAttribute("latestVersion", render.getMeta().getLastVersion());
        }
        PageLock lock = pageLockService.getCurrentLock(getTeamId(context), rid);
        if (lock != null && (!isLockHolder(context, lock))) {
            return pageLocked(request, rid);
        }
        boolean isNewPage = isNewPage(render);
        Draft draft = getLastestDraft(context, rid);
        List<Tag> tags = tagService.getTagsNotInGroupForTeam(tid);
        ModelAndView mv = null;
        if (draft != null) {
            if (isNewPage) {
                mv = loadEditorWithContent(context, render, draft.getContent(), request.getLocale().toString());
            } else {
                mv = compareDraft(context, render, draft, parentRid);
                mv.addObject("draft", draft);
            }
        } else {
            if (isNewPage) {
                mv = loadEditorWithContent(context, render, "", request.getLocale().toString());
            } else {
                String innerHTML = getDPageContent(context, render, draft);
                mv = loadEditorWithContent(context, render, innerHTML, request.getLocale().toString());
            }
        }
        this.loadAttachmentList(mv, context, rid);
        // add by lvly增加默认标签 BEGIN
        // 增加页面选择的标签
        String useTagIds = request.getParameter("tagIds");
        if (!CommonUtil.isNullStr(useTagIds)) {
            Integer[] tagIds = CommonUtil.stringArray2IntArray(useTagIds.split(","));
            tagService.addItems(tid, CommonUtil.array2List(tagIds), rid);
            for (Integer tagId : tagIds) {
                Tag tag = tagService.getTag(tagId);
                resourceService.updateTagMap(rid, tag);
            }
        }
        
        // 增加默认姓名标签
        resourceOperateService.addDefaultTag(rid);
        Team team =teamService.getTeamByID(tid);
        // END
        Resource res = render.getMeta();
        Map<Integer, String> tagMap = (null == res) ? new HashMap<Integer, String>() : res.getTagMap();
        mv.addObject("resource", res);
        mv.addObject("tagMap", tagMap);
        mv.addObject("rid", (null == res) ? 0 : res.getRid());
        mv.addObject("parentRid", parentRid);
        mv.addObject("tags", tags);
        mv.addObject("myspace", isMyspace(team, context.getCurrentUID()));
        return mv;
    }

    private boolean isMyspace(Team team, String uid) {
        return team.isPersonalTeam() && team.getCreator().equals(uid);
    }

    private int getPatentRidFromRequest(HttpServletRequest request) {
        int parentRid = 0;
        if (StringUtils.isNotEmpty(request.getParameter("parentRid"))) {
            parentRid = Integer.parseInt(request.getParameter("parentRid"));
        }
        return parentRid;
    }

    private VWBContext getEditPageContext(HttpServletRequest request, Integer pid) {
        return VWBContext.createContext(request, UrlPatterns.T_EDIT_PAGE, pid, LynxConstants.TYPE_PAGE);
    }

    private boolean isNewPage(PageRender render) {
        return render.getMeta().getLastVersion() == LynxConstants.INITIAL_VERSION;
    }

    public ModelAndView compareDraft(VWBContext context, PageRender render, Draft autoSaveDraft, int bid) {
        ModelAndView mv = layout(ELayout.LYNX_INFO, context, "/jsp/compareDraft.jsp");
        String currContent = getDPageContentByLastVersion(context, render);
        String diffResult = getAutoSaveDiff(context, currContent, autoSaveDraft.getContent());
        mv.addObject("draft", autoSaveDraft);
        mv.addObject("bid", bid);
        mv.addObject("currHtml", currContent);
        mv.addObject("autoHtml", autoSaveDraft.getContent());
        mv.addObject("diffHtml", diffResult);
        mv.addObject("rid",render.getMeta().getRid());
        return mv;
    }

    @RequestMapping(params = "func=restoreDraft")
    public ModelAndView restoreDraft(HttpServletRequest request, @RequestParam("rid") Integer rid) {
        int bid = getPatentRidFromRequest(request);
        VWBContext context = getEditPageContext(request, rid);
        PageRender render = getPageRender(rid, context);
        String htmlText = request.getParameter("draftText");
        ModelAndView mv = loadEditorWithContent(context, render, htmlText, request.getLocale().toString());
        mv.addObject("bid", bid);
        return mv;
    }

    @RequestMapping(params = "func=unrestoreDraft")
    public ModelAndView unrestoreDraft(HttpServletRequest request, @RequestParam("rid") Integer rid) {
        VWBContext context = getEditPageContext(request, rid);
        int bid = getPatentRidFromRequest(request);
        String htmlText = request.getParameter("publishText");
        PageRender render = getPageRender(rid, context);
        ModelAndView mv = loadEditorWithContent(context, render, htmlText, request.getLocale().toString());
        mv.addObject("bid", bid);
        return mv;
    }

    @RequestMapping(params = "func=cancel")
    @WebLog(method = "cancelEditPage", params = "pid,bid")
    public ModelAndView cancel(HttpServletRequest request, @RequestParam("rid") Integer rid) throws Exception {
        VWBContext context = getEditPageContext(request, rid);
        int tid = VWBContext.getCurrentTid();
        Resource meta = resourceService.getResource(rid);
        clearAllDraft(context, meta);// 删掉自动保存和手动保存的草稿
        unlockCurrentPage(context, meta);// 解锁
        if (meta.getLastVersion() == LynxConstants.INITIAL_VERSION) {
            resourceOperateService.deleteResource(tid,rid,context.getCurrentUID());
            // lvly 2012-7-30 如果为新建页面，把所有标签关系全部干掉 BEGIN
            tagService.removeAllTagItemsOfRid(context.getSite().getId(), rid);
            // END
            return new ModelAndView(new RedirectView(urlGenerator.getURL(tid,UrlPatterns.T_VIEW_R, request.getParameter("parentRid"),null)));
        }
        return getRedirectViewByBidAndPid(context, rid,  meta.getTid());
    }

    private ModelAndView getRedirectViewByBidAndPid(VWBContext context, int rid,  int tid) {
        return new ModelAndView(new RedirectView(urlGenerator.getURL(tid,UrlPatterns.T_VIEW_R, rid+"",null)));
    }

    @RequestMapping(params = "func=autosave")
    public void autosave(HttpServletRequest request, HttpServletResponse response, @RequestParam("rid") Integer rid)
            throws Exception {
        String htmlText = request.getParameter("htmlPageText");
        String title = request.getParameter("pageTitle");
        VWBContext context = getEditPageContext(request, rid);
        PageRender render = getPageRender(rid, context);
        
        //如果文件名冲突则继续使用原有文件名
        int parentRid=render.getMeta().getBid();
        if(!resourceOperateService.canUseFileName(context.getTid(), parentRid, rid,render.getMeta().getItemType(), title)){
        	title=render.getMeta().getTitle();
        }
        
        
        ResourceBundle rb = context.getBundle("templates/default");
        String dml = convertToDML(context, htmlText);
        Resource meta = render.getMeta();
        meta.setLastEditor(context.getCurrentUID());
        meta.setLastEditorName(context.getCurrentUserName());
        meta.setLastEditTime(new Date());
        meta.setTitle(title);
        PageVersion v = PageHelper.createPageVersion(meta, title, context.getCurrentUID(),
                context.getCurrentUserName(), dml);
        render.setMeta(meta);
        render.setDetail(v);
        draftService.updateAutoSaveDraft(getTeamId(context), render, context.getCurrentUID());
        boolean lockFlag = userHaveLock(context, rid);
        JSONObject obj = new JSONObject();
        obj.put("message", rb.getString("action.autosavepage.success"));
        obj.put("pageLock", lockFlag);
        JsonUtil.writeJSONObject(response, obj);
    }

    private boolean userHaveLock(VWBContext context, int pid) {
        PageLock lock = pageLockService.getCurrentLock(getTeamId(context), pid);
        return (lock != null && context.getCurrentUID().equals(lock.getUid()));
    }

    @RequestMapping(params = "func=save")
    @WebLog(method = "saveWhenEditing", params = "rid")
    public void save(HttpServletRequest request, HttpServletResponse response, @RequestParam("rid") Integer rid)
            throws Exception {
        String htmlText = request.getParameter("htmlPageText");
        String title = request.getParameter("pageTitle");
        VWBContext context = getEditPageContext(request, rid);
        String textWithoutMetaData = convertToDML(context, htmlText);
        PageRender render = getPageRender(rid, context);
        Resource meta = render.getMeta();
        meta.setLastEditor(context.getCurrentUID());
        meta.setLastEditorName(context.getCurrentUserName());
        meta.setLastEditTime(new Date());
        meta.setTitle(title);
        PageVersion v = PageHelper.createPageVersion(meta, title, context.getCurrentUID(),
                context.getCurrentUserName(), textWithoutMetaData);
        render.setMeta(meta);
        render.setDetail(v);
        updateManualDraft(context, render);
        updatePageLockTime(context, meta);
        
        
        //保存相关文档
        saveFileReference(htmlText, rid);
        
        Date redate = meta.getLastEditTime();
        writeToResponse(response, context.getBundle("templates/default").getString("action.savepage.success") + "|"
                + redate);
        
    }
    

    @RequestMapping(params = "func=saveexit")
    @WebLog(method = "saveAndPublish", params = "rid")
    public ModelAndView saveAndExit(HttpServletRequest request, @RequestParam("rid") Integer rid) throws JDOMException, IOException {
        String htmlText = request.getParameter("fixDomStr");
        String title = request.getParameter("pageTitle");
        VWBContext context = getEditPageContext(request, rid);
        String uid = context.getCurrentUID();
        int tid = VWBContext.getCurrentTid();
        Resource meta = resourceService.getResource(rid);
        String textWithoutMetaData = convertToDML(context, htmlText);
        if (meta == null) {
            LOG.error("Can not find this pid:" + rid);
            throw new RuntimeException();
        }
        meta.setLastEditor(uid);
        if (meta.getLastVersion() == LynxConstants.INITIAL_VERSION) {
            meta.setTitle(HTMLConvertUtil.replaceLtGt(title));
            meta.setLastEditor(uid);
            meta.setLastEditorName(context.getCurrentUserName());
            if (!uid.equals(meta.getCreator())) {
                meta.setLastEditTime(new Date());
            }
            meta.setStatus(LynxConstants.STATUS_AVAILABLE);
            resourceOperateService.createPageVersion(meta, textWithoutMetaData);
        } else {
            meta.setTitle(HTMLConvertUtil.replaceLtGt(title));
            meta.setLastEditor(uid);
            meta.setLastEditorName(context.getCurrentUserName());
            meta.setLastEditTime(new Date());
            resourceOperateService.updatePageVersion(meta, textWithoutMetaData);
        }
        gridService.editItem(uid, tid, rid, LynxConstants.TYPE_PAGE);
        
        //保存相关文档
        saveFileReference(htmlText, rid);
        
        clearAllDraft(context, meta);
        unlockCurrentPage(context, meta);
        teamSpaceSizeService.resetTeamResSize(tid);
     //   updateBundle(context, rid, bid);
        return getRedirectViewByBidAndPid(context, rid, tid);
    }

 

    @RequestMapping(params = "func=preview")
    @WebLog(method = "previewEditResult", params = "rid")
    public ModelAndView preview(HttpServletRequest request, @RequestParam("rid") Integer rid) throws Exception {
        int bid = getPatentRidFromRequest(request);
        VWBContext context = getEditPageContext(request, rid);
        PageRender render = getPageRender(rid, context);
        String htmlText = getPreviewHTML(context, request.getParameter("fixDomStr"));
        String title = request.getParameter("pageTitle");
        String uid = context.getCurrentUID();
        Resource meta = render.getMeta();
        meta.setLastEditTime(new Date());
        meta.setLastEditor(uid);
        meta.setLastEditorName(context.getCurrentUserName());
        meta.setTitle(title);
        PageVersion detail = PageHelper.createPageVersion(meta, title, uid, context.getCurrentUserName(), htmlText);
        request.setAttribute("editDpage", new PageRender(meta, detail));
        htmlText = TextUtil.replaceEntities(htmlText);
        request.setAttribute("htmlText", htmlText);
        request.setAttribute("bid", bid);
        context.setRid(rid);
        context.setItemType(LynxConstants.TYPE_PAGE);
        return layout(ELayout.LYNX_INFO, context, "/PreviewContent.jsp");
    }

    @RequestMapping(params = "func=previewToEdit")
    @WebLog(method = "previewReturnEdit", params = "rid")
    public ModelAndView previewToEdit(HttpServletRequest request, @RequestParam("rid") Integer rid) throws Exception {
        int parentRid = getPatentRidFromRequest(request);
        VWBContext context = getEditPageContext(request, rid);
        PageRender render = getPageRender(rid, context);
        context.setWysiwygEditorMode(VWBContext.EDITOR_MODE);
        String innerHTML = request.getParameter("fixDomStr");
        render.getMeta().setTitle(request.getParameter("pageTitle"));
        ModelAndView mv = loadEditorWithContent(context, render, innerHTML, request.getLocale().toString());
        mv.addObject("parentRid", parentRid);
        return mv;
    }

    @RequestMapping(params = "func=pageLocked")
    public ModelAndView pageLocked(HttpServletRequest request, @RequestParam("rid") Integer rid) {
        int parentRid = getPatentRidFromRequest(request);
        VWBContext context = getEditPageContext(request, rid);
        PageLock lock = pageLockService.getCurrentLock(VWBContext.getCurrentTid(), rid);
        ModelAndView mv = layout(ELayout.LYNX_MAIN, context, "/jsp/pageLocked.jsp");
        mv.addObject("lock", lock);
        mv.addObject("parentRid", parentRid);
        return mv;
    }

    @RequestMapping(params = "func=isLockTimeOut")
    public void isLockTimeOut(HttpServletRequest request, HttpServletResponse response, @RequestParam("rid") Integer rid) {
        boolean flag = pageLockService.isLockTimeOut(VWBContext.getCurrentTid(), rid);
        PageLock lock = pageLockService.getCurrentLock(VWBContext.getCurrentTid(), rid);
        JSONObject result = new JSONObject();
        if (lock != null && !flag) {
            result.put("myLeftTime", pageLockService.getLeftTimeOfPageLock(VWBContext.getCurrentTid(), rid));
        }
        result.put("flag", flag);
        JsonUtil.writeJSONObject(response, result);
    }

    @RequestMapping(params = "func=updateLockTime")
    public void updateLockTime(HttpServletRequest request, HttpServletResponse response,
            @RequestParam("rid") Integer rid) {
        VWBContext context = getEditPageContext(request, rid);
        PageRender render = getPageRender(rid, context);
        PageLock lock = pageLockService.getCurrentLock(VWBContext.getCurrentTid(), rid);
        if (lock != null) {
            updatePageLockTime(context, render.getMeta());
        }
        JSONObject result = new JSONObject();
        result.put("status", "success");
        JsonUtil.writeJSONObject(response, result);
    }
    
    /**
     * 保存相关文档
     * @param htmlText
     * @param rid
     */
    private void saveFileReference(String htmlText, Integer rid){
    	List<Integer> fidList = new ArrayList<Integer>();
    	Pattern p = Pattern.compile("<A href=\"http://[\\da-zA-Z\\.\\-:/]+/r/(\\d+)\" target=\"_blank\" rid=\"(\\d+)\">");
    	Matcher  m = p.matcher(htmlText);
    	while(m.find()){
    		if(StringUtils.isNumeric(m.group(1))){
    			fidList.add(Integer.parseInt(m.group(1)));
    		}
    	}
    	
    	if(fidList.size() == 0){
    		return;
    	}
    	
    	//转换整型数组
    	int [] fids = new int[fidList.size()];
    	for(int i=0; i<fidList.size(); i++){
    		fids[i] = fidList.get(i);
    	}
    	
    	fileVersionService.referTo(rid,VWBContext.getCurrentTid(),fids);
    }

    private PageRender getPageRender(int rid, VWBContext context) {
        return resourceOperateService.getPageRender( VWBContext.getCurrentTid(),rid);
    }

    private boolean isLockHolder(VWBContext context, PageLock lock) {
        return lock.getUid().equals(context.getCurrentUID());
    }

    private String getAutoSaveDiff(VWBContext context, String oldHtml, String newHtml) {
        return differenceService.getDiffResult(context, oldHtml, newHtml);
    }

    private void lockCurrentPage(VWBContext context, PageRender render) {
    	pageLockService.lockPage(getTeamId(context), render.getMeta().getRid(), context.getCurrentUID(), render.getMeta()
                .getLastVersion());
    }

    private void unlockCurrentPage(VWBContext context, Resource meta) {
    	pageLockService.unlockPage(getTeamId(context), meta.getRid(), context.getCurrentUID());
    }

    private void updatePageLockTime(VWBContext context, Resource meta) {
    	pageLockService.updateLockTime(getTeamId(context), meta.getRid());
    }

    private void updateManualDraft(VWBContext context, PageRender render) {
        draftService.updateManualSaveDraft(getTeamId(context), render, context.getCurrentUID());
    }

    private ModelAndView loadEditorWithContent(VWBContext context, PageRender render, String innerHTML, String locale) {
        ModelAndView mv = layout(ELayout.LYNX_EDIT, context, "/EditContent.jsp");
        render.getDetail().setContent(StringEscapeUtils.escapeJavaScript(convertToHTML(context, innerHTML)));
        loadAttachmentList(mv, context, render.getMeta().getRid());
        lockCurrentPage(context, render);
        fillEditPageData(mv, locale, render);
        return mv;
    }

    private String getDPageContent(VWBContext context, PageRender dpage, Draft autoSaveDraft) {
        String innerHTML = "";
        if (!isNewPage(dpage)) {
            if (autoSaveDraft != null && isLastModifier(context, dpage)) {
                innerHTML = autoSaveDraft.getContent();
            } else {
                innerHTML = getDPageContentByLastVersion(context, dpage);
            }
        }
        return innerHTML;
    }

    private void fillEditPageData(ModelAndView mv, String locale, PageRender dpage) {
        mv.addObject("rid", dpage.getMeta().getRid());
        mv.addObject("lockVersion", dpage.getMeta().getLastEditTime());
        mv.addObject("editDpage", dpage);
        String title = dpage.getMeta().getTitle();
        if (title != null) {
            title = title.replaceAll("<", "&lt;");
            title = title.replaceAll(">", "&gt;");
            title = title.replaceAll("\"", "&quot;");
        }
        dpage.getMeta().setTitle(title);
        String tempLocale = locale.toLowerCase().replaceAll("_", "-");
        mv.addObject("locale", tempLocale);
    }

    private String getDPageContentByLastVersion(VWBContext context, PageRender render) {
        String innerHTML = "";
        int version = getRequestVersion(context.getHttpRequest());
        if (version != VWBContext.LATEST_VERSION) {
            PageVersion detail = pageVersionService.getPageVersion(render.getMeta().getRid(),version);
            innerHTML = detail.getContent();
        } else {
            innerHTML = render.getDetail().getContent();
        }
        return innerHTML;
    }

    private Draft getLastestDraft(VWBContext context, Integer pid) {
        return draftService.getLastestDraft(getTeamId(context), pid, context.getCurrentUID());
    }

    private boolean isLastModifier(VWBContext context, PageRender dpage) {
        return dpage.getMeta().getCreator().equals(context.getCurrentUID());
    }

    private void clearAllDraft(VWBContext context, Resource meta) {
        draftService.clearAutoSaveDraft(getTeamId(context), meta.getRid(), context.getCurrentUID());
        draftService.clearManualSaveDraft(getTeamId(context), meta.getRid(), context.getCurrentUID());
    }

    private int getTeamId(VWBContext context) {
        return context.getSite().getId();
    }

    private String getPreviewHTML(VWBContext context, String htmlText) throws JDOMException, IOException{
        String textWithoutMetaData = "";
        if (htmlText != null) {
            textWithoutMetaData = convertToDML(context, htmlText);
            textWithoutMetaData = convertToHTML(context, textWithoutMetaData);
        }
        return textWithoutMetaData;
    }

    private String convertToDML(VWBContext context, String htmlText) throws JDOMException, IOException{
        String textWithoutMetaData = "";
        if (htmlText != null) {
            try {
                textWithoutMetaData = new HtmlStringToDMLTranslator().translate(htmlText, context);
            } catch (JDOMException e) {
                LOG.error(e);
            } catch (IOException e) {
                LOG.error(e);
            }
        }
        return textWithoutMetaData;
    }

    private String convertToHTML(VWBContext context, String textWithoutMetaData) {
        return renderingService.getHTML(context, textWithoutMetaData);
    }

    private int getRequestVersion(HttpServletRequest request) {
        String version = request.getParameter("version");
        if (version != null) {
            try {
                return Integer.parseInt(version);
            } catch (NumberFormatException e) {
                LOG.warn(e.getMessage());
            }
        }
        return VWBContext.LATEST_VERSION;
    }

    private void loadAttachmentList(ModelAndView mv, VWBContext context, int pid) {
        List<FileVersion> results = fileVersionService.getFilesOfPage(pid, getTeamId(context));
        List<AttachmentItem> itemList = new ArrayList<AttachmentItem>();
        for (FileVersion att : results) {
            itemList.add(AttachmentItem.convertFromAttachment(att));
        }
        mv.addObject("attachments", itemList);
    }

    private void writeToResponse(HttpServletResponse response, String xml) {
        response.setContentType("text/html;charset=UTF-8");
        try {
            Writer wr = response.getWriter();
            wr.write(xml);
            wr.close();
        } catch (IOException e) {
            LOG.debug("Write xml to response error!", e);
        }
    }

    
	@RequestMapping(params="func=validateFileName")
	public void validateFileName(HttpServletRequest request,HttpServletResponse response){
		int rid = Integer.parseInt(StringUtils.defaultString(request.getParameter("rid"), "0"));
		JSONObject o = new JSONObject();
		if(rid ==0){
			o.put("result", false);
			o.put("message", "参数错误！");
		}else{
			String fileName = request.getParameter("fileName");
			Resource resource=resourceService.getResource(rid);
			int parentRid=resource.getBid();
			if(!resourceOperateService.canUseFileName(VWBContext.getCurrentTid(), parentRid, rid,resource.getItemType(), fileName)){
				o.put("result", false);
				o.put("message", "当前文件夹下存在重名文件！");
			}else{
				o.put("result", true);
			}
		}
		JsonUtil.writeJSONObject(response, o);
	}
    
}
