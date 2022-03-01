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

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import net.duckling.ddl.common.VWBContext;
import net.duckling.ddl.common.VWBSession;
import net.duckling.ddl.constant.ELayout;
import net.duckling.ddl.constant.KeyConstants;
import net.duckling.ddl.constant.LynxConstants;
import net.duckling.ddl.service.authority.AuthorityService;
import net.duckling.ddl.service.browselog.BrowseLogService;
import net.duckling.ddl.service.bundle.Bundle;
import net.duckling.ddl.service.bundle.BundleItem;
import net.duckling.ddl.service.bundle.IBundleService;
import net.duckling.ddl.service.devent.EventDispatcher;
import net.duckling.ddl.service.file.DFileRef;
import net.duckling.ddl.service.file.FileVersion;
import net.duckling.ddl.service.file.FileVersionService;
import net.duckling.ddl.service.file.IPictureService;
import net.duckling.ddl.service.file.Picture;
import net.duckling.ddl.service.grid.IGridService;
import net.duckling.ddl.service.itemtypemap.ItemTypeMappingService;
import net.duckling.ddl.service.itemtypemap.ItemTypemapping;
import net.duckling.ddl.service.relaterec.DGridDisplay;
import net.duckling.ddl.service.relaterec.impl.RelatedRecServiceImpl;
import net.duckling.ddl.service.resource.IResourceService;
import net.duckling.ddl.service.resource.ITagService;
import net.duckling.ddl.service.resource.PageLock;
import net.duckling.ddl.service.resource.PageLockService;
import net.duckling.ddl.service.resource.PageVersion;
import net.duckling.ddl.service.resource.PageVersionService;
import net.duckling.ddl.service.resource.Resource;
import net.duckling.ddl.service.resource.ResourceOperateService;
import net.duckling.ddl.service.resource.SimpleResource;
import net.duckling.ddl.service.tobedelete.File;
import net.duckling.ddl.service.url.URLGenerator;
import net.duckling.ddl.service.url.UrlPatterns;
import net.duckling.ddl.service.user.AoneUserService;
import net.duckling.ddl.util.ArrayAndListConverter;
import net.duckling.ddl.util.CommonUtils;
import net.duckling.ddl.util.DateUtil;
import net.duckling.ddl.util.JsonUtil;
import net.duckling.ddl.util.MimeType;
import net.duckling.ddl.util.NumberFormatUtil;
import net.duckling.ddl.util.PdfStatus;
import net.duckling.ddl.util.PlainTextHelper;
import net.duckling.ddl.util.WebParamUtil;
import net.duckling.ddl.web.bean.AttachmentItem;
import net.duckling.ddl.web.bean.ConflictBundleItemHelper;
import net.duckling.ddl.web.bean.DFileRefView;
import net.duckling.ddl.web.bean.PageInfo;
import net.duckling.ddl.web.bean.PageLockDisplay;
import net.duckling.ddl.web.interceptor.access.OnDeny;
import net.duckling.ddl.web.interceptor.access.RequirePermission;

import org.apache.commons.lang.StringUtils;
import org.apache.log4j.Logger;
import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.servlet.ModelAndView;
import org.springframework.web.servlet.view.RedirectView;

import cn.cnic.cerc.dlog.client.WebLog;
import cn.cnic.esac.clb.util.HttpStatus;
import cn.vlabs.clb.api.SupportedFileFormatForOnLineViewer;

@Controller
@RequestMapping("/{teamCode}/bundle/{bid}")
@RequirePermission(target = "team", operation = "view")
public class BundleController extends BaseController {

    private static final Logger LOG = Logger.getLogger(BundleController.class);
    private static final String PDF_NOTSUPPORTED = "unsupported";
    private static final String PDF_ORIGINAL = "original_pdf";
    @Autowired
    private IResourceService resourceService;
    @Autowired
    private PageLockService pageLockService;
    @Autowired
    private ResourceOperateService resourceOperateService;
    @Autowired
    private PageVersionService pageVersionService;
    @Autowired
    private ITagService tagService;
    @Autowired
    private IBundleService bundleService;
    @Autowired
    private IGridService gridService;
    @Autowired
    private AuthorityService authorityService;
    @Autowired
    private IPictureService pictureService;
    @Autowired
    private RelatedRecServiceImpl relateRecService;
    @Autowired
    private EventDispatcher eventDispatcher;
    @Autowired
    private URLGenerator urlGenerator;
    @Autowired
    private FileVersionService fileVersionService;
    @Autowired
    private AoneUserService aoneUserService;
    @Autowired
    private BrowseLogService browseLogService;
    @Autowired
    private ItemTypeMappingService itemTypeMappingService;
    private List<Long> transferIntToLong(List<Integer> ids) {
        List<Long> result = new ArrayList<Long>();
        for (Integer i : ids) {
            result.add(new Long(i));
        }
        return result;
    }

    private boolean validateDeleteAuth(VWBContext context, Resource r) {
        String u = context.getCurrentUID();
        if (authorityService.teamAccessability(VWBContext.getCurrentTid(),
                                               VWBSession.findSession(context.getHttpRequest()),
                                               AuthorityService.ADMIN)) {
            return true;
        } else {
            if (r != null && u.equals(r.getCreator())) {
                return true;
            }
        }
        return false;
    }

    private boolean validateDeleteBundleAuth(VWBContext context, int bid) {
        String u = context.getCurrentUID();
        if (authorityService.teamAccessability(VWBContext.getCurrentTid(),
                                               VWBSession.findSession(context.getHttpRequest()),
                                               AuthorityService.ADMIN)) {
            return true;
        } else {
            boolean b = true;
            List<Integer> rIds = bundleService.getRidsOfBundleAndItems(bid,
                                                                       context.getSite().getId());
            List<Resource> resources = resourceService
                    .getResourcesBySphinxID(transferIntToLong(rIds));
            for (Resource rs : resources) {
                if (!u.equals(rs.getCreator())) {
                    b = false;
                    break;
                }
            }
            return b;
        }
    }

    @SuppressWarnings({ "unchecked" })
    @OnDeny({ "reorder", "getUnBundle", "removeBundleItem", "rename",
                    "updateBundleDesc" })
                    public void onDeny(String methodName, HttpServletRequest request,
                                       HttpServletResponse response) {
        JsonObject obj = new JsonObject();
        obj.addProperty("status", "error");
        obj.addProperty("result", "无权进行此操作！");
        response.setStatus(HttpStatus.AUTH_FAILED);
        JsonUtil.write(response, obj);
    }

    @RequestMapping
    @WebLog(method = "viewBundle", params = "bid")
    public ModelAndView display(HttpServletRequest request,
                                HttpServletResponse response, @PathVariable("bid") int bid) {
        String rid = request.getParameter("rid");
        if(StringUtils.isNotEmpty(rid)){
            String url = urlGenerator.getAbsoluteURL(VWBContext.getCurrentTid(), UrlPatterns.T_VIEW_R, rid,null);
            return new ModelAndView(new RedirectView(url));
        }else{
            ItemTypemapping itm = itemTypeMappingService.getItemTypeMapping(VWBContext.getCurrentTid(), bid, LynxConstants.TYPE_BUNDLE);
            String url = urlGenerator.getAbsoluteURL(VWBContext.getCurrentTid(), UrlPatterns.T_VIEW_R, itm.getRid()+"",null);
            return new ModelAndView(new RedirectView(url));
        }
        /*
          VWBContext context = VWBContext.createContext(request,
          UrlPatterns.T_TEAM_HOME);
          int tid = VWBContext.getCurrentTid();
          Bundle bundle = bundleService.getBundle(bid, tid);
          if (null == bundle
          || LynxConstants.STATUS_DELETE.equals(bundle.getStatus())) {
          return dealDeletedBundle(request, response, bid, context);
          }
          // 给九宫格添加权重
          gridService.clickItem(context.getCurrentUID(), tid, bundle.getBid(),
          LynxConstants.TYPE_BUNDLE);
          List<String> resTitleList = new ArrayList<String>();
          List<String> urlList = new ArrayList<String>();
          List<BundleItem> items = bundleService.getBundleItems(bid, tid);

          if (!CommonUtils.isNull(items)) {

          List<Long> rids = new ArrayList<Long>();
          for (BundleItem item : items) {
          rids.add((long) item.getRid());
          }
          ModelAndView mv = ridIsNull(request, rids, bid, tid);
          if (mv != null) {
          return mv;
          }
          List<Resource> itemsResList = resourceService
          .getResourcesBySphinxID(rids);
          mv = prepareData(request, context, resTitleList, urlList, rids,
          bundle.getBid());
          mv.addObject("items", itemsResList);
          Resource res = resourceService.getResource(bid, tid,
          LynxConstants.TYPE_BUNDLE);
          mv.addObject("bundleDesc", bundle.getDescription());
          res.setBid(bundle.getBid());
          mv.addObject("bundle", res);
          mv.addObject("resTitleList", resTitleList);
          mv.addObject("urlList", urlList);
          mv.addObject("uid", context.getCurrentUID());
          long rid = rids.get(0);
          Resource curResource = resourceService.getResource((int) rid);
          int pid = curResource.getRid();
          loadRelatedRecPagesListInBundle(mv, context, pid);
          return mv;
          }
          ModelAndView mv = getDefaultModelAndView(context);
          Resource res = resourceService.getResource(bid, tid,
          LynxConstants.TYPE_BUNDLE);
          mv.addObject("bundle", res);
          return mv;
        */
    }

    /**
     * 处理已删除的bundle，如果带rid就重定向到该resource的url上，不带抛出404错误
     *
     * @param request
     * @param response
     * @param bid
     * @param context
     * @return
     */
    private ModelAndView dealDeletedBundle(HttpServletRequest request,
                                           HttpServletResponse response, int bid, VWBContext context) {
        String rid = request.getParameter("rid");
        if (!StringUtils.isEmpty(rid)) {
            int r = Integer.parseInt(rid);
            Resource res = resourceService.getResource(r);
            if (res != null) {
                int tid = res.getTid();
                String url = null;
                if (res.getBid() > 0) {
                    url = urlGenerator.getURL(tid,UrlPatterns.T_BUNDLE, res.getBid()+"",null)
                            + "?rid=" + rid;
                } else {
                    if (res.isFile()) {
                        url = urlGenerator.getURL(tid,UrlPatterns.T_FILE,res.getRid()+"",null);
                    } else if (res.isPage()) {
                        url = urlGenerator.getURL(tid,UrlPatterns.T_PAGE,res.getRid()+"",null);
                    }
                }
                if (url != null) {
                    return new ModelAndView(new RedirectView(url));
                }
            }
        }

        notFound(request, response, true);
        return null;
    }

    /**
     * 判断请求的url是否包含rid，如果包含不做任何操作，如果不包含重定向到包含bundle的首Rid的url
     *
     * @param request
     * @param rids
     * @param bid
     * @return
     */
    private ModelAndView ridIsNull(HttpServletRequest request, List<Long> rids,
                                   int bid, int tid) {
        String ridStr = request.getParameter("rid");
        if (null == ridStr || "".equals(ridStr)) {
            String url = urlGenerator.getURL(tid,UrlPatterns.T_BUNDLE, bid+"","rid="+ rids.get(0));
            List<Resource> resList = resourceService
                    .getResourcesBySphinxID(rids);
            boolean isAllPic = true;
            if (!CommonUtils.isNull(resList)) {
                for (Resource r : resList) {
                    if (!File.isPicture(r.getTitle())) {
                        isAllPic = false;
                        break;
                    }
                }
                if (isAllPic) {
                    url += "&flow=true";
                }
            }
            return new ModelAndView(new RedirectView(url));
        } else {
            return null;
        }
    }

    @RequestMapping(params = "func=reorder")
    @WebLog(method = "reorderBundleItems", params = "bid")
    @RequirePermission(target = "team", operation = "edit")
    public void reorder(HttpServletRequest request,
                        HttpServletResponse response, @PathVariable("bid") int bid) {
        String order = request.getParameter("order");
        if (null == order || "".equals(order)) {
            return;
        }
        Map<Integer, Integer> orderMap = JsonUtil.readValue(
            order, new TypeToken<HashMap<Integer, Integer>>(){}.getType());
        bundleService.reorderBundleItems(bid, VWBContext.getCurrentTid(),
                                         orderMap);
        JsonUtil.write(response, new JsonObject());
    }

    @RequestMapping(params = "func=getUnBundle")
    @RequirePermission(target = "team", operation = "edit")
    @WebLog(method = "searchUnbundleItems", params = "bid")
    public void getUnBundle(HttpServletResponse response,
                            @PathVariable("bid") int bid, @RequestParam("title") String title,
                            @RequestParam("offset") int offset, @RequestParam("size") int size) {
        int tid = VWBContext.getCurrentTid();
        List<Resource> resList = resourceService.getUnBundleResource(bid, tid,
                                                                     title, offset, size);
        JsonArray array = new Gson().toJsonTree(resList).getAsJsonArray();
        JsonUtil.write(response, array);
    }

    @RequestMapping(params = "func=saveBundleItem")
    @WebLog(method = "addExistItemToBundle", params = "bid,selectItem[]")
    @RequirePermission(target = "team", operation = "edit")
    public void saveBundleItem(HttpServletRequest request,
                               HttpServletResponse response, @PathVariable("bid") int bid) {
        int[] rids = WebParamUtil.getIntegerValues(request, "selectItem[]");
        VWBContext context = VWBContext.createContext(request,
                                                      UrlPatterns.T_TEAM_HOME);
        int tid = context.getTid();
        JsonObject result = new JsonObject();
        if (null != rids && rids.length > 0) {
            Bundle b = bundleService.getBundle(bid, tid);
            b.setLastEditor(context.getCurrentUID());
            bundleService.updateBundle(b);
            List<Long> ridList = ArrayAndListConverter.convert2Long(rids);
            List<Resource> resList = resourceService
                    .getResourcesBySphinxID(ridList);
            Resource bundle = resourceService.getResource(bid, tid);
            int[] itemIds = putResourceIntoBundle(tid, resList, bundle);
            resourceService.updateExistBundleTagAndStarmark(bid, tid, itemIds);
            List<Resource> actualResList = resourceService
                    .getResourcesBySphinxID(ArrayAndListConverter
                                            .convert2Long(itemIds));
            JsonArray newItems = getJSONArrayOfNewBundleItems(actualResList);
            result.add("newItems", newItems);
            if (rids.length > itemIds.length) {
                JsonArray conflictItems = ConflictBundleItemHelper
                        .getJSONArrayOfConflictItems(resourceService, urlGenerator,
                                                     rids, itemIds);
                result.add("conflictItems", conflictItems);
            }
        }
        JsonUtil.write(response, result);
    }

    private int[] putResourceIntoBundle(int tid, List<Resource> resList,
                                        Resource bundle) {
        if (null == resList || resList.isEmpty()) {
            return new int[0];
        }
        IBundleService bs = bundleService;
        List<Integer> itemRids = new ArrayList<Integer>();
        for (Resource res : resList) {
            int rid = res.getRid();
            if (res.isBundle()) {
                itemRids.addAll(bs.getRidsOfBundleAndItems(res.getRid(), tid));
                itemRids.remove(new Integer(rid));
                bs.disbandBundle(res.getRid(), tid);
            } else {
                itemRids.add(rid);
            }
        }
        int size = itemRids.size();
        size = (size > 0) ? size : 0;
        int[] rids = new int[size];
        for (int i = 0; i < size; i++) {
            rids[i] = itemRids.get(i);
        }
        bs.addBundleItems(bundle.getRid(), tid, rids);
        return ConflictBundleItemHelper.getNewAddBundleItems(bs, bundle,
                                                             itemRids);
    }

    private JsonArray getJSONArrayOfNewBundleItems(List<Resource> resList) {
        if (null == resList || resList.isEmpty()) {
            return new JsonArray();
        }
        JsonArray result = new JsonArray();
        for (Resource res : resList) {
            JsonObject obj = ConflictBundleItemHelper.getJSONResourceForBundleItem(urlGenerator, res);
            result.add(obj);
        }
        return result;
    }

    @RequestMapping(params = "func=removeBundleItem")
    @WebLog(method = "removeItemFromBundle", params = "bid,rid")
    @RequirePermission(target = "team", operation = "edit")
    public void removeBundleItem(HttpServletRequest request,
                                 HttpServletResponse response, @PathVariable("bid") int bid) {
        int rid = WebParamUtil.getIntegerValue(request, "rid");
        int tid = VWBContext.getCurrentTid();
        VWBContext context = VWBContext.createContext(request,
                                                      UrlPatterns.T_TEAM_HOME);
        List<PageLock> locks = PageLockValidateUtils.getPageLockFromResource(
            new int[] { rid }, resourceService, pageLockService,
            bundleService);

        if (!locks.isEmpty()) {
            PageLockValidateUtils.pageLockMessage(locks, response,
                                                  resourceOperateService);
            return;
        }
        Bundle bundle = bundleService.getBundle(bid, tid);
        bundle.setLastEditor(context.getCurrentUID());
        bundleService.updateBundle(bundle);
        bundleService.removeBundleItems(bid, tid, new int[] { rid });
        Resource resource = resourceService.getResource(rid);
        updateTagCount(resource, tagService);
        JsonUtil.write(response, new JsonObject());
    }

    @RequestMapping(params = "func=deleteBundleItem")
    @WebLog(method = "deleteBundleItem", params = "bid,rid")
    @RequirePermission(target = "team", operation = "edit")
    public ModelAndView deleteBundleItem(HttpServletRequest request,
                                         @PathVariable("bid") int bid) {
        int rid = WebParamUtil.getIntegerValue(request, "rid");
        int tid = VWBContext.getCurrentTid();
        VWBContext context = VWBContext.createContext(request,
                                                      UrlPatterns.T_TEAM_HOME);
        IBundleService bs = bundleService;
        Bundle bundle = bs.getBundle(bid, tid);
        bundle.setLastEditor(context.getCurrentUID());
        bs.updateBundle(bundle);
        bs.removeBundleItems(bid, tid, new int[] { rid });
        Resource resource = resourceService.getResource(rid);
        if (resource.isFile()) {
            resourceOperateService.deleteResource(resource.getTid(), resource.getRid(),"");
            fileVersionService.deleteRefer(resource.getRid(), tid);
        } else {
            List<PageLock> locks = PageLockValidateUtils
                    .getPageLockFromResource(new int[] { rid },
                                             resourceService, pageLockService, bundleService);
            if (!locks.isEmpty()) {
                ModelAndView mv = layout(ELayout.LYNX_MAIN,
                                         context, "/jsp/pageLocked.jsp");
                mv.addObject("lock", locks.get(0));
                return mv;
            }
            resourceOperateService.deleteResource(resource.getTid(), resource.getRid(),context.getCurrentUID());
            fileVersionService.deleteRefer(resource.getRid(), tid);
        }
        return new ModelAndView(new RedirectView(urlGenerator.getURL(tid,UrlPatterns.T_BUNDLE, bid+"",null)));
    }

    @SuppressWarnings("unused")
    @RequestMapping(params = "func=deleteBundleItemValidate")
    public void deleteBundleItemValidate(HttpServletRequest request,
                                         HttpServletResponse response, @PathVariable("bid") int bid) {
        int rid = WebParamUtil.getIntegerValue(request, "rid");
        VWBContext context = VWBContext.createContext(request,
                                                      UrlPatterns.T_TEAM_HOME);
        Resource resource = resourceService.getResource(rid);
        JsonObject obj = new JsonObject();
        if (!validateDeleteAuth(context, resource)) {
            obj.addProperty("status", false);
        } else {
            obj.addProperty("status", true);
        }
        JsonUtil.write(response, obj);
    }

    @RequestMapping(params = "func=rename")
    @WebLog(method = "renameBundle", params = "bid")
    @RequirePermission(target = "team", operation = "edit")
    public void rename(HttpServletRequest request,
                       HttpServletResponse response, @PathVariable("bid") int bid) {
        VWBContext context = VWBContext.createContext(request,
                                                      UrlPatterns.T_TEAM_HOME);
        String title = request.getParameter("title");
        if (null != title && !"".equals(title)) {
            int tid = VWBContext.getCurrentTid();
            Bundle bundle = bundleService.getBundle(bid, tid);
            if (!StringUtils.equals(title, bundle.getTitle())) {
                bundle.setTitle(title);
                bundle.setLastEditor(context.getCurrentUID());
                bundle.setLastEditTime(new Date());
                bundleService.updateBundle(bundle);
            }
        }
        JsonUtil.write(response, new JsonObject());
    }

    @RequestMapping(params = "func=updateBundleDesc")
    @WebLog(method = "updateBundleDescription", params = "bid,description")
    @RequirePermission(target = "team", operation = "edit")
    public void updateBundleDesc(HttpServletRequest request,
                                 HttpServletResponse response, @PathVariable("bid") int bid) {
        VWBContext context = VWBContext.createContext(request,
                                                      UrlPatterns.T_TEAM_HOME);
        int tid = VWBContext.getCurrentTid();
        String description = request.getParameter("description");
        Bundle bundle = bundleService.getBundle(bid, tid);
        bundle.setDescription(description);
        bundle.setLastEditor(context.getCurrentUID());
        bundleService.updateBundle(bundle);
        JsonObject obj = new JsonObject();
        obj.addProperty("status", true);
        JsonUtil.write(response, obj);
    }

    @RequestMapping(params = "func=disbandBundle")
    @WebLog(method = "disbandBundle", params = "bid")
    @RequirePermission(target = "team", operation = "edit")
    public ModelAndView disbandBundle(HttpServletRequest request,
                                      @PathVariable("bid") int bid) {
        VWBContext context = VWBContext.createContext(request,
                                                      UrlPatterns.T_TEAM_HOME);
        int tid = VWBContext.getCurrentTid();
        SimpleResource resource = resourceService.getSimpleResource(bid,
                                                                    LynxConstants.TYPE_BUNDLE, tid);
        List<PageLock> locks = PageLockValidateUtils.getPageLockFromResource(
            new int[] { resource.getRid() }, resourceService,
            pageLockService, bundleService);
        if (!locks.isEmpty()) {
            ModelAndView mv = layout(ELayout.LYNX_MAIN, context,
                                     "/jsp/pageLocked.jsp");
            List<PageLockDisplay> lds = PageLockValidateUtils
                    .getPageLockMessage(locks, resourceOperateService);
            mv.addObject("pageLocks", lds);
            return mv;
        }
        bundleService.disbandBundle(bid, tid);
        return new ModelAndView(new RedirectView(urlGenerator.getURL(tid,UrlPatterns.T_TAG, null,null)));
    }

    @RequestMapping(params = "func=deleteBundle")
    @WebLog(method = "deleteBundle", params = "bid")
    @RequirePermission(target = "team", operation = "edit")
    public ModelAndView deleteBundle(HttpServletRequest request,
                                     @PathVariable("bid") int bid) {
        VWBContext context = VWBContext.createContext(request,
                                                      UrlPatterns.T_TEAM_HOME);
        int tid = VWBContext.getCurrentTid();
        SimpleResource resource = resourceService.getSimpleResource(bid,
                                                                    LynxConstants.TYPE_BUNDLE, tid);
        List<PageLock> locks = PageLockValidateUtils.getPageLockFromResource(
            new int[] { resource.getRid() }, resourceService,
            pageLockService, bundleService);
        if (!locks.isEmpty()) {
            ModelAndView mv = layout(ELayout.LYNX_MAIN, context,
                                     "/jsp/pageLocked.jsp");
            List<PageLockDisplay> lds = PageLockValidateUtils
                    .getPageLockMessage(locks, resourceOperateService);
            mv.addObject("pageLocks", lds);
            return mv;
        }

        List<Resource> bundleResource = getBundleResource(bid, context, tid);
        bundleService.deleteBundle(bid, tid);
        //deleteBundleEvent(bundleResource, context, tid);
        return new ModelAndView(new RedirectView(urlGenerator.getURL(tid,UrlPatterns.T_TAG, null,null)));
    }

    private List<Resource> getBundleResource(int bid, VWBContext context,
                                             int tid) {
        List<BundleItem> bis = bundleService.getBundleItems(bid, tid);
        List<Long> rids = new ArrayList<Long>();
        for (BundleItem bi : bis) {
            rids.add(new Long(bi.getRid()));
        }
        return resourceService.getResourcesBySphinxID(rids);
    }

    /**
     * 删除bundle事件
     */
    //  private void deleteBundleEvent(List<Resource> rs, VWBContext context,
    //          int tid) {
    //      if (rs == null || rs.isEmpty()) {
    //          return;
    //      }
    //      for (Resource r : rs) {
    //          if (r.isFile()) {
    //              eventDispatcher.sendFileDeleteEvent(tid, r, context.getCurrentUID());
    //          } else if (r.isPage()) {
    //              eventDispatcher.sendPageDeleteEvent(tid, r,
    //                      context.getCurrentUID());
    //          }
    //      }
    //  }

    @RequestMapping(params = "func=deleteBundleValidate")
    @RequirePermission(target = "team", operation = "edit")
    public void deleteBundleValidate(HttpServletRequest request,
                                     HttpServletResponse response, @PathVariable("bid") int bid) {
        VWBContext context = VWBContext.createContext(request,
                                                      UrlPatterns.T_TEAM_HOME);
        JsonObject obj = new JsonObject();
        if (!validateDeleteBundleAuth(context, bid)) {
            obj.addProperty("status", false);
        } else {
            obj.addProperty("status", true);
        }
        JsonUtil.write(response, obj);
    }

    private ModelAndView prepareData(HttpServletRequest request,
                                     VWBContext context, List<String> resTitleList,
                                     List<String> urlList, List<Long> rids, int bid) {
        boolean isAllPic = true;
        boolean isAllFile = true;
        boolean isFirstElement = true;
        // check if bundle item click
        Resource curResource = checkParamterResource(request, context, bid);
        List<Resource> resList = resourceService.getResourcesBySphinxID(rids);
        Resource currentCheckResource = curResource;
        for (Resource res : resList) {
            resTitleList.add(res.getTitle());
            if (isFirstElement) {
                curResource = (null == curResource) ? res : curResource;
                context.setResource(curResource.getRid());
                isFirstElement = false;
            }
            if (LynxConstants.TYPE_FILE.equals(res.getItemType())) {
                isAllPic = isAllPic ? File.isPicture(res.getTitle()) : false;
            } else {
                isAllPic = false;
                isAllFile = false;
            }
            String url = urlGenerator.getURL(res.getTid(),UrlPatterns.T_BUNDLE, bid+"","rid="+res.getRid());
            urlList.add(url);
        }
        String type = "mix";
        String viewType = request.getParameter("viewType");
        if (null != viewType && !"".equals(viewType)
            && "singlePage".equals(viewType)) {
            type = "mix";
        } else {
            type = isAllFile ? "allfile" : type;
            type = isAllPic ? "allpic" : type;
        }
        if ("mix".equals(type) || "allfile".equals(type)) {// 第二个条件是将纯文件列表模式去除
            type = curResource.getItemType();
            rids.clear();
            rids.add((long) curResource.getRid());
        }
        ModelAndView mv = prepareMultiPageData(request, context, type, rids,
                                               bid, currentCheckResource);
        mv.addObject("type", type);
        String enableDConvert = context.getContainer().getProperty(
            KeyConstants.DCONVERT_SERVICE_ENABLE);
        mv.addObject("enableDConvert", Boolean.valueOf(enableDConvert));
        return mv;
    }

    @SuppressWarnings("unused")
    private Resource checkParamterResource(HttpServletRequest request,
                                           VWBContext context, int bid) {
        String ridStr = request.getParameter("rid");
        if (null == ridStr || "".equals(ridStr)) {
            return null;
        }
        int rid = Integer.parseInt(ridStr);

        Resource resource = resourceService.getResource(rid);
        boolean exist = bundleService.isInBundle(bid,
                                                 VWBContext.getCurrentTid(), rid);
        return exist ? resource : null;
    }

    private ModelAndView prepareMultiPageData(HttpServletRequest request,
                                              VWBContext context, String type, List<Long> rids, int bid,
                                              Resource curResource) {
        if ("allpic".equals(type)) {
            return prepareDataForAllPic(context, rids, curResource,
                                        request.getParameter("flow"));
        } else if ("allfile_unused".equals(type)) {// 加上_unused是为了将纯文件列表模式去除，以后可能用到
            return prepareDataForAllFile(context, rids);
        } else if ("allfile".equals(type)
                   || LynxConstants.TYPE_FILE.equals(type)) {
            long rid = rids.get(0);
            int version = getCurrentVersion(request, context, (int) rid);
            return prepareDataForMixFile(context, (int) rid, version, bid);
        } else {
            long rid = rids.get(0);
            int version = getCurrentVersion(request, context, (int) rid);
            return prepareDataForMixPage(context, (int) rid, version);
        }
    }

    @SuppressWarnings("unused")
    private int getCurrentVersion(HttpServletRequest request,
                                  VWBContext context, int rid) {
        int version = 0;
        String verStr = request.getParameter("version");
        if (verStr != null) {
            try {
                version = Integer.parseInt(verStr);
            } catch (NumberFormatException e) {
                Resource res = resourceService.getResource(rid);
                if (null != res) {
                    version = res.getLastVersion();
                } else {
                    LOG.error("cannot find Resource by id = " + rid);
                }
            }
        }
        if (null == verStr || version <= 0) {
            Resource res = resourceService.getResource(rid);
            version = res.getLastVersion();
        }
        return version;
    }

    private ModelAndView getDefaultModelAndView(VWBContext context) {
        return layout(ELayout.LYNX_MAIN, context,
                      "/jsp/aone/tag/bundleView.jsp");
    }

    @RequestMapping(params = "func=loadMore")
    @ResponseBody
    public List<FlowBundleItem> getMorePictrue(HttpServletRequest request,
                                               @RequestParam("size") int size, @RequestParam("offset") int offset,
                                               @PathVariable("bid") int bid) {
        VWBContext context = VWBContext.createContext(request,
                                                      UrlPatterns.T_TEAM_HOME);
        return convert2FlowBundleItem(context, bundleService.getBundleItems(
            bid, VWBContext.getCurrentTid(), offset, size));
    }

    static class FlowBundleItem {
        private BundleItem item;
        private Picture pic;
        private String toUrl;
        private String showUrl;
        private String title;
        private String uploaderName;
        private String uploadTime;

        public String getUploadTime() {
            return uploadTime;
        }

        public void setUploadTime(String uploadeTime) {
            this.uploadTime = uploadeTime;
        }

        public String getTitle() {
            return title;
        }

        public void setTitle(String title) {
            this.title = title;
        }

        public String getUploaderName() {
            return uploaderName;
        }

        public void setUploaderName(String uploaderName) {
            this.uploaderName = uploaderName;
        }

        public Picture getPic() {
            return pic;
        }

        public void setPic(Picture pic) {
            this.pic = pic;
        }

        public BundleItem getItem() {
            return item;
        }

        public void setItem(BundleItem item) {
            this.item = item;
        }

        public String getToUrl() {
            return toUrl;
        }

        public void setToUrl(String toUrl) {
            this.toUrl = toUrl;
        }

        public String getShowUrl() {
            return showUrl;
        }

        public void setShowUrl(String showUrl) {
            this.showUrl = showUrl;
        }
    }

    private List<FlowBundleItem> convert2FlowBundleItem(VWBContext context,
                                                        List<BundleItem> bItems) {
        if (CommonUtils.isNull(bItems)) {
            return null;
        }
        List<FlowBundleItem> result = new ArrayList<FlowBundleItem>();
        int tid = context.getTid();
        for (BundleItem bItem : bItems) {
            FlowBundleItem fbItem = new FlowBundleItem();
            Resource res = resourceService.getResource(bItem.getRid());
            fbItem.setItem(bItem);
            fbItem.setShowUrl(urlGenerator.getURL(tid, "download",
                                                  Integer.toString(res.getRid()), "simple=true"));
            fbItem.setToUrl(urlGenerator.getURL(tid, "bundle",
                                                Integer.toString(bItem.getBid()), "rid=" + bItem.getRid()));
            SimpleResource sr = resourceService.getSimpleResource(tid,
                                                                  bItem.getRid());
            File f = null;//fileService.getFile(sr.getItemId(), bItem.getTid());
            Picture pic = pictureService.getPicture(f.getClbId(),
                                                    f.getLastVersion());
            if (pic == null) {
                pictureService.downLoadAndAddPicture(f.getClbId(),
                                                     f.getLastVersion());
                pic = pictureService.getPicture(f.getClbId(),
                                                f.getLastVersion());
            }
            fbItem.setTitle(f.getTitle());
            fbItem.setUploaderName(aoneUserService.getUserNameByID(f.getLastEditor()));
            fbItem.setUploadTime(DateUtil.getTime(f.getLastEditTime()));
            fbItem.setPic(pic);
            result.add(fbItem);
        }
        return result;
    }

    // file list, shortFileSize,tagExist,
    private ModelAndView prepareDataForAllPic(VWBContext context,
                                              List<Long> rids, Resource curResource, String flow) {
        ModelAndView mv = !CommonUtils.isNull(flow) ? layout(
            ELayout.LYNX_MAIN, context,
            "/jsp/aone/tag/bundle-flow-layout.jsp")
                : getDefaultModelAndView(context);
        String uid = context.getCurrentUID();
        List<PageInfo> pageInfoList = new ArrayList<PageInfo>();
        Map<Integer, String> imgurls = new HashMap<Integer, String>();
        List<Map<Integer, String>> tagMapList = new ArrayList<Map<Integer, String>>();
        List<Resource> resList = resourceService.getResourcesBySphinxID(rids);
        List<FileVersion> fileVersionList = fileVersionService.getFileSizeByRids(rids);
        mv.addObject("up", 1);
        for (int i = 0; i < resList.size(); i++) {
            Resource resource = resList.get(i);
            if (curResource != null
                && curResource.getRid() == resource.getRid()) {
                mv.addObject("up", i + 1);
            }
            PageInfo info = new PageInfo();
            info.setShortFileSize(NumberFormatUtil.getSizeShort(fileVersionList
                                                                .get(i).getSize()));
            Map<Integer, String> tagMap = resource.getTagMap();
            tagMapList.add(tagMap);
            info.setTagExist((null == tagMap || tagMap.isEmpty()) ? false
                             : true);
            Set<String> starmark = resource.getMarkedUserSet();
            if (null != starmark && !starmark.isEmpty()
                && starmark.contains(uid)) {
                info.setStarmark(true);
            }
            pageInfoList.add(info);
            imgurls.put(resource.getRid(),
                        urlGenerator.getURL(resource.getTid(),UrlPatterns.T_DOWNLOAD, resource.getRid()+"",null));
        }
        mv.addObject("down", resList.size());

        mv.addObject("imgurls", JsonUtil.getJSONString(imgurls));
        mv.addObject("image", resList);
        mv.addObject("resource", curResource);
        mv.addObject("pageInfo", pageInfoList);
        mv.addObject("tagMapList", tagMapList);
        return mv;
    }

    // file List, fileType List, pdfViewUrl List, downloadUrl List,
    // shortFileSize List, tag List, tagUrl List
    private ModelAndView prepareDataForAllFile(VWBContext context,
                                               List<Long> rids) {
        ModelAndView mv = this.getDefaultModelAndView(context);
        String uid = context.getCurrentUID();
        int tid = context.getTid();
        List<PageInfo> filePageInfoList = new ArrayList<PageInfo>();
        List<Map<Integer, String>> tagMapList = new ArrayList<Map<Integer, String>>();
        List<Resource> resList = resourceService.getResourcesBySphinxID(rids);
        List<FileVersion> fileVersionList = fileVersionService.getFileSizeByRids(rids);
        for (int i = 0; i < resList.size(); i++) {
            Resource res = resList.get(i);
            int fid = res.getRid();
            int version = res.getLastVersion();
            // prepare pageinfo
            PageInfo info = new PageInfo();
            info.setShortFileSize(NumberFormatUtil.getSizeShort(fileVersionList
                                                                .get(i).getSize()));
            if (version > 0) {
                info.setDownloadUrl(urlGenerator.getURL(tid, "download",
                                                        Integer.toString(fid), "type=doc&version=" + version));
            } else {
                info.setDownloadUrl(urlGenerator.getURL(tid, "download",
                                                        Integer.toString(fid), "type=doc"));
            }
            String fileType = res.getFileType();
            if (null != fileType) {
                String pdfstatus = PdfStatus.SOURCE_NOT_FOUND.toString();// 表示该类型文档的PDF不存在
                boolean supported = SupportedFileFormatForOnLineViewer
                        .isSupported(fileType);
                if ("pdf".equals(fileType)) {
                    pdfstatus = PDF_ORIGINAL;
                } else if (supported) {
                    pdfstatus = null;//fileService.queryPdfStatus(
                    //fileVersionList.get(i).getClbId(), "" + version);
                } else {
                    pdfstatus = PDF_NOTSUPPORTED;// 表示不支持该类型文档的在线显示
                }
                if (pdfstatus == PDF_NOTSUPPORTED
                    && isSupportedFileType(fileType)) { // 剔除图片的无法转换信息
                    fileType = "img";
                }
                info.setPdfstatus(pdfstatus);
                info.setSupported(supported);
            }
            info.setFileType(fileType);
            Map<Integer, String> tagMap = res.getTagMap();
            tagMapList.add(tagMap);

            info.setTagExist(null == tagMap ? false : true);
            Set<String> starmark = res.getMarkedUserSet();
            if (null != starmark && !starmark.isEmpty()
                && starmark.contains(uid)) {
                info.setStarmark(true);
            }
            filePageInfoList.add(info);
        }
        mv.addObject("resList", resList);
        mv.addObject("filePageInfo", filePageInfoList);
        mv.addObject("tagMapList", tagMapList);
        return mv;
    }

    // downloadURL, fid, fileExtend eq 'FILE', title,
    // version,pdfstatus,supported, strFileType,sizeShort,versionList
    private ModelAndView prepareDataForMixFile(VWBContext context, int rid,
                                               int version, int bid) {
        ModelAndView mv = this.getDefaultModelAndView(context);
        Resource curResource = resourceService.getResource(rid);
        int fid = curResource.getRid();
        int tid = VWBContext.getCurrentTid();
        mv.addObject("resource", curResource);
        PageInfo info = new PageInfo();
        String fileType = curResource.getFileType();
        info.setFileType(fileType);
        if (version > 0) {
            info.setDownloadUrl(urlGenerator.getURL(tid, "download",
                                                    Integer.toString(fid), "type=doc&version=" + version));
        } else {
            info.setDownloadUrl(urlGenerator.getURL(tid, "download",
                                                    Integer.toString(fid), "type=doc"));
        }
        FileVersion lastVersion = fileVersionService.getFileVersion(curResource.getRid(), tid, version);
        info.setFileExtend(getFileExtend(lastVersion.getTitle(),
                                         lastVersion.getSize()));
        info.setShortFileSize(NumberFormatUtil.getSizeShort(lastVersion
                                                            .getSize()));
        // Load Version List
        List<FileVersion> versionList = fileVersionService.getFileVersions(fid, tid);
        mv.addObject("versionList", versionList);
        loadFileReferenceList(mv, context, lastVersion.getTitle(), fid, tid);
        // 生成PDF相关参数
        File file = null;//fileService.getFile(fid, tid);
        if (null != fileType) {
            String pdfstatus = PdfStatus.SOURCE_NOT_FOUND.toString();// 表示该类型文档的PDF不存在
            boolean supported = SupportedFileFormatForOnLineViewer
                    .isSupported(fileType);
            if ("pdf".equals(fileType)) {
                pdfstatus = PDF_ORIGINAL;
            } else if (supported) {
                pdfstatus = null;//fileService.queryPdfStatus(file.getClbId(), ""
                //+ version);
            } else {
                pdfstatus = PDF_NOTSUPPORTED;// 表示不支持该类型文档的在线显示
            }
            if (pdfstatus == PDF_NOTSUPPORTED && isSupportedFileType(fileType)) { // 剔除图片的无法转换信息
                fileType = "img";
            }
            info.setPdfstatus(pdfstatus);
            info.setSupported(supported);
        }
        Set<String> starmark = curResource.getMarkedUserSet();
        if (null != starmark && !starmark.isEmpty()
            && starmark.contains(context.getCurrentUID())) {
            info.setStarmark(true);
        }
        info.setFileType(PlainTextHelper.convert2BrushClassFileType(fileType));
        mv.addObject("deleteFileURL", urlGenerator.getURL(tid,
                                                          UrlPatterns.T_BUNDLE, bid + "", "func=deleteBundleItem&rid="
                                                          + curResource.getRid()));
        mv.addObject("validateURL", urlGenerator.getURL(tid,
                                                        UrlPatterns.T_BUNDLE, bid + "",
                                                        "func=deleteBundleItemValidate&rid=" + curResource.getRid()));
        mv.addObject("uid", context.getCurrentUID());
        mv.addObject("pageInfo", info);
        return mv;
    }

    private ModelAndView prepareDataForMixPage(VWBContext context, int rid,
                                               int version) {
        Resource curResource = resourceService.getResource(rid);
        int tid = VWBContext.getCurrentTid();
        ModelAndView mv = getDefaultModelAndView(context);
        PageVersion pageVersion = pageVersionService.getPageVersion(
            curResource.getRid(), version);
        mv.addObject("pageVersion", pageVersion);
        mv.addObject("pageCurVersion", version);
        mv.addObject("pageLatestVersion", curResource.getLastVersion());
        mv.addObject("resource", curResource);
        mv.addObject("pageMeta", pageVersion);
        Set<String> starmark = curResource.getMarkedUserSet();
        if (null != starmark && !starmark.isEmpty()
            && starmark.contains(context.getCurrentUID())) {
            mv.addObject("starmark", true);
        } else {
            mv.addObject("starmark", false);
        }
        mv.addObject("uid", context.getCurrentUID());
        loadAttachmentList(mv, context, rid);
        context.setResource(rid, LynxConstants.TYPE_PAGE);
        browseLogService.resourceVisited(tid, rid, context.getCurrentUID(), context.getCurrentUserName(), LynxConstants.TYPE_PAGE);
        return mv;
    }

    private String getFileExtend(String filename, long size) {
        if (MimeType.isImage(filename)) {
            return "IMAGE";
        } else if (PlainTextHelper.isSupported(MimeType.getSuffix(filename))
                   && size < LynxConstants.MAXFILESIZE_CODEREVIEW) {// 文件超过给定大小时不直接显示
            return "TEXT";
        }
        return "FILE";
    }

    private boolean isSupportedFileType(String fileType) {
        if (null == fileType || "".equals(fileType)) {
            return false;
        }
        if (SupportedFileFormatForOnLineViewer.isSupported(fileType)) {
            return true;
        }
        if (File.isPictureFileTypeForSearch(fileType)) {
            return true;
        }
        return false;
    }

    private void loadAttachmentList(ModelAndView mv, VWBContext context, int rid) {
        List<FileVersion> results = null;//fileService.getFilesOfPage(rid,
        //VWBContext.getCurrentTid());
        List<AttachmentItem> itemList = new ArrayList<AttachmentItem>();
        for (FileVersion att : results) {
            itemList.add(AttachmentItem.convertFromAttachment(att));
        }
        mv.addObject("attachments", itemList);
    }

    private void loadFileReferenceList(ModelAndView mv, VWBContext context,
                                       String title, int fid, int tid) {
        List<DFileRef> refList = null;//fileService.getDFileReferences(fid, tid);
        List<DFileRefView> refViewList = new ArrayList<DFileRefView>();
        if (refList != null) {
            for (DFileRef ref : refList) {
                DFileRefView refview = new DFileRefView();
                if (ref.getPageRid() > 0) {
                    Resource page = resourceService.getResource(ref.getPageRid());
                    refview.setPageName(page.getTitle());
                }
                refview.setDfileRef(ref);
                refview.setFileName(title);
                refViewList.add(refview);
            }
        }
        mv.addObject("refView", refViewList);
    }

    private void loadRelatedRecPagesListInBundle(ModelAndView mv,
                                                 VWBContext context, int pid) {
        int num = 5;
        int tid = VWBContext.getCurrentTid();
        String uid = context.getCurrentUID();
        DGridDisplay dGridDisplay = relateRecService.getRelatedRecOfPage(tid,
                                                                         uid, pid, num);
        mv.addObject("relatedGrids", dGridDisplay);
    }

    private void updateTagCount(Resource res, ITagService ts) {
        Map<Integer, String> tagMap = res.getTagMap();
        if (null == tagMap || tagMap.isEmpty()) {
            return;
        }
        int tid = res.getTid();
        for (Map.Entry<Integer, String> entry : tagMap.entrySet()) {
            ts.updateTagCount(tid, entry.getKey());
        }
    }
}
