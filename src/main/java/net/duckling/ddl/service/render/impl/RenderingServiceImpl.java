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

package net.duckling.ddl.service.render.impl;

import net.duckling.ddl.common.VWBContext;
import net.duckling.ddl.service.render.DPageRendable;
import net.duckling.ddl.service.render.Rendable;
import net.duckling.ddl.service.render.RenderingService;
import net.duckling.ddl.service.render.dml.Dml2Html;
import net.duckling.ddl.service.render.dml.Dml2HtmlEngine;
import net.duckling.ddl.service.render.dml.DmlContextBridge;
import net.duckling.ddl.service.resource.IResourceService;
import net.duckling.ddl.service.resource.PageRender;
import net.duckling.ddl.service.resource.PageVersion;
import net.duckling.ddl.service.resource.PageVersionService;
import net.duckling.ddl.service.resource.Resource;

import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

/**
 * @date 2010-2-24
 * @author diyanliang@cnic.cn
 */
@Service
public class RenderingServiceImpl implements RenderingService {

	private static final Logger LOG = Logger
			.getLogger(RenderingServiceImpl.class);

	public static final int DPAGE_LATEST_VERSION = -1;
	@Autowired
	private PageVersionService pageVersionService;
	@Autowired
	private IResourceService resourceService;

	private DPageRendable getRender(int tid, int vid) {
		return new DPageRendable(tid, vid);
	}

	private String textToHTML(VWBContext context, String htmlContent) {
		String res = "";
		// 第一步 定义参数
		String viewMode = "0";
		boolean wysiwygEditorMode = false;
		// 第二步 取是command
		String strModeType = context.getWysiwygEditorMode();
		boolean modetype = context.isFullMode();
		if (modetype) {
			viewMode = "1";
		}
		if ((VWBContext.EDITOR_MODE).equals(strModeType)) {
			wysiwygEditorMode = true;
		}
		// 建立解析参数容器
		Dml2HtmlEngine d2lengine = new Dml2HtmlEngine();
		d2lengine.setMwysiwygEditorMode(wysiwygEditorMode);// 是否是fck
		d2lengine.setViewMode(viewMode);// 编辑模式还是浏览模式
		DmlContextBridge dmlcontextbridge = new DmlContextBridge(context);
		d2lengine.setDmlcontext(dmlcontextbridge);
		// 建立解析器并解析
		try {
			Dml2Html d2h = new Dml2Html(htmlContent, d2lengine);
			res = d2h.getHTMLString();
		} catch (Exception e) {
			LOG.error(e);
		}
		return res;
	}

	public Rendable createRendable(int tid, int rid) {
		Resource vp = resourceService.getResource(rid);
		if (vp != null && tid == vp.getTid()) {
			return getRender(vp.getTid(), vp.getRid());
		} else {
			return null;
		}
	}

	public String getHTML(VWBContext context, PageRender page) {
		int ver = page.getMeta().getLastVersion();
		if (ver == 0) {
			ver = DPAGE_LATEST_VERSION;
		}
		return textToHTML(context, page.getDetail().getContent());
	}

	public String getHTML(VWBContext context, Resource r) {
		int ver = r.getLastVersion();
		if (ver == 0) {
			ver = DPAGE_LATEST_VERSION;
		}
		PageVersion v = pageVersionService.getLatestPageVersion(r.getRid());
		if (v == null) {
			return (ver > 0) ? "404：资源未找到！" : "";
		}
		return textToHTML(context, v.getContent());
	}

	public String getHTML(VWBContext context, String htmlContent) {
		return this.textToHTML(context, htmlContent);
	}
}
