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
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import net.duckling.ddl.common.Site;
import net.duckling.ddl.common.VWBContext;
import net.duckling.ddl.constant.ELayout;
import net.duckling.ddl.constant.LynxConstants;
import net.duckling.ddl.service.resource.IResourceService;
import net.duckling.ddl.service.resource.PageRender;
import net.duckling.ddl.service.resource.PageVersion;
import net.duckling.ddl.service.resource.PageVersionService;
import net.duckling.ddl.service.resource.Resource;
import net.duckling.ddl.service.resource.ResourceOperateService;
import net.duckling.ddl.service.url.URLGenerator;
import net.duckling.ddl.service.url.UrlPatterns;
import net.duckling.ddl.service.user.AoneUserService;
import net.duckling.ddl.service.user.UserExt;
import net.duckling.ddl.web.interceptor.access.RequirePermission;
import net.duckling.ddl.web.tag.InsertDiffTag;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.ModelAndView;

/**
 * InfoPage Controller
 * 
 * @date 2012-05-12
 * @author Clive Lee
 */
@Controller
@RequestMapping("/{teamCode}/infoPage")
@RequirePermission(target = "team", operation = "view")
public class InfoPageController extends BaseController {

	@Autowired
	private PageVersionService pageVersionService;
	@Autowired
	private ResourceOperateService resourceOperateService;
	@Autowired
	private IResourceService resourceService;
	@Autowired
	private AoneUserService aoneUserService;
	@Autowired
	private URLGenerator urlGenerator;

	private ModelAndView doLayout(VWBContext context) {
		List<PageVersion> history = pageVersionService
				.getAllPageVersionByTIDRID(context.getResource().getTid(),
						context.getResource().getRid());
		ModelAndView mv = layout(ELayout.LYNX_INFO, context,
				"/jsp/InfoContent.jsp");
		mv.addObject("history", history);
		return mv;
	}

	private Resource getSavedViewPort(HttpServletRequest request, int rid,
			String itemType) {
		Site site = VWBContext.findSite(request);
		return resourceService.getResource(rid, site.getId());
	}

	private void loadPageMeta(ModelAndView mv, Resource vp, VWBContext c) {
		String creatorName = aoneUserService.getUserNameByID(vp.getCreator());
		String editorName = aoneUserService.getUserNameByID(vp.getLastEditor());
		mv.addObject("rid", vp.getRid());
		mv.addObject("lastversion", vp.getLastVersion());
		mv.addObject("creator", creatorName);
		mv.addObject("createTime", vp.getCreateTime());
		mv.addObject("editor", editorName);
		mv.addObject("editTime", vp.getLastEditTime());
		mv.addObject("baseURL", c.getBaseURL());
		mv.addObject("version", vp.getLastVersion());
	}

	private void loadPageVersion(Resource p, HttpServletRequest request,
			int rid, VWBContext c, ModelAndView mv) {
		int itemcount = p.getLastVersion(); // number of page versions
		int tid = VWBContext.getCurrentTid();
		List<PageVersion> versionList = null;
		int pagesize = 20;
		int startitem = itemcount;
		String parmStart = request.getParameter("start");
		if (parmStart != null) {
			startitem = Integer.parseInt(parmStart);
		} else {
			startitem = 1;
		}

		if (startitem > -1) {
			startitem = ((startitem / pagesize) * pagesize) + 1;
		}
		if (startitem > itemcount) {
			startitem = ((startitem / pagesize - 1) * pagesize) + 1;
		}
		boolean bExists = (resourceService.getResource(rid) != null);
		if (startitem < 0) {
			versionList = pageVersionService
					.getAllPageVersionByTIDRID(tid, rid);
		} else {
			versionList = pageVersionService.getVersions(rid, tid,
					startitem - 1, pagesize);
		}
		updatePageVersionEditor(versionList);
		mv.addObject("versionList", versionList);
		mv.addObject("itemcount", itemcount);
		mv.addObject("startitem", startitem);
		mv.addObject("pagesize", pagesize);
		mv.addObject("pageExist", bExists);
	}

	private void updatePageVersionEditor(List<PageVersion> versionList) {
		Set<String> uids = new HashSet<String>();
		for(PageVersion pv : versionList){
			uids.add(pv.getEditor());
		}
		List<UserExt> us = aoneUserService.getUserExtByUids(uids);
		Map<String,UserExt> maps = new HashMap<String,UserExt>();
		for(UserExt u : us){
			maps.put(u.getUid(), u);
		}
		for(PageVersion pv : versionList){
			UserExt u = maps.get(pv.getEditor());
			if(u!=null){
				pv.setEditorName(u.getName());
			}
		}
	}

	@RequestMapping(params = "func=diff")
	public ModelAndView diff(@RequestParam("rid") int rid,
			HttpServletRequest request) {
		VWBContext context = VWBContext.createContext(request,
				UrlPatterns.T_DIFF,
				getSavedViewPort(request, rid, LynxConstants.TYPE_PAGE));
		String sVersion = request.getParameter("r1");
		String sCompareTo = request.getParameter("r2");
		int iVersion = 0, iCompareTo = 0;
		if (sVersion != null) {
			iVersion = Integer.parseInt(sVersion);
		}
		if (sCompareTo != null) {
			iCompareTo = Integer.parseInt(sCompareTo);
		} else {
			int iLastVersion = context.getResource().getLastVersion();
			if (iLastVersion > 1) {
				iCompareTo = iLastVersion - 1;
			}
		}
		ModelAndView mv = doLayout(context);
		int tid = context.getSite().getId();
		PageRender render = resourceOperateService.getPageRender(tid, rid,
				iVersion);
		loadPageMeta(mv, render.getMeta(), context);
		loadPageVersion(render.getMeta(), request, rid, context, mv);
		mv.addObject("lastversion", resourceService.getResource(rid)
				.getLastVersion());
		request.setAttribute(InsertDiffTag.ATTR_OLDVERSION, iVersion);
		mv.addObject(InsertDiffTag.ATTR_OLDVERSION, iVersion);
		request.setAttribute(InsertDiffTag.ATTR_NEWVERSION, iCompareTo);
		request.setAttribute(VWBContext.CONTEXT_KEY, context);
		request.setAttribute("rid", rid);
		return mv;
	}

	@RequestMapping
	public ModelAndView info(HttpServletRequest request,
			@RequestParam("rid") int rid) {
		VWBContext context = VWBContext.createContext(request,
				UrlPatterns.T_INFO,
				getSavedViewPort(request, rid, LynxConstants.TYPE_PAGE));
		request.setAttribute(VWBContext.CONTEXT_KEY, context);
		VWBContext c = VWBContext.getContext(request);
		ModelAndView mv = doLayout(context);
		Resource vp = resourceService.getResource(rid);
		loadPageMeta(mv, vp, c);
		loadPageVersion(vp, request, rid, c, mv);
		return mv;
	}

	@RequestMapping(params = "func=recoverVersion")
	public void recoverVersion(HttpServletRequest request,
			HttpServletResponse response) throws IOException {
		int tid = Integer.parseInt(request.getParameter("tid"));
		int rid = Integer.parseInt(request.getParameter("rid"));
		int version = Integer.parseInt(request.getParameter("version"));
		resourceOperateService.recoverPageVersion(tid, rid, version);
		String pageViewURL = urlGenerator.getURL(tid,UrlPatterns.T_VIEW_FILE, rid + "", null);
		response.sendRedirect(pageViewURL);
	}

	@RequestMapping(params = "func=rename")
	@RequirePermission(target = "team", operation = "edit")
	public ModelAndView rename(@RequestParam("rid") int rid,
			HttpServletRequest request) {
		VWBContext context = VWBContext.createContext(request,
				UrlPatterns.T_EDIT_PAGE,
				getSavedViewPort(request, rid, LynxConstants.TYPE_PAGE));
		String sNewTitle = request.getParameter("renameto");
		if (sNewTitle != null && sNewTitle.trim().length() > 0) {
			PageRender page = resourceOperateService.getPageRender(
					VWBContext.getCurrentTid(), rid);
			page.getMeta().setTitle(sNewTitle);
			resourceOperateService.updatePageVersion(page.getMeta(), page
					.getDetail().getContent());
		}
		return doLayout(context);
	}

}
