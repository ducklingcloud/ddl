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
package net.duckling.ddl.web.tag;

import java.io.IOException;

import javax.servlet.jsp.JspException;

import net.duckling.ddl.common.DDLFacade;
import net.duckling.ddl.service.render.RenderingService;
import net.duckling.ddl.service.resource.PageRender;
import net.duckling.ddl.service.resource.ResourceOperateService;

public class PageDisplayTag extends VWBBaseTag {
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	private int pid;
	private int version;
	
	public int getPid() {
		return pid;
	}
	
	public void setPid(int pid) {
		this.pid = pid;
	}
	
	public int getVersion() {
		return version;
	}

	public void setVersion(int version) {
		this.version = version;
	}
	
	protected void initTag(){
		super.initTag();
		version = -1;
	}

	@Override
	public int doVWBStart() throws JspException, IOException {
		PageRender render = null;
		int tid = vwbcontext.getSite().getId();
		if (version==-1){
			render = DDLFacade.getBean(ResourceOperateService.class).getPageRender(tid, pid);
		}else{
			render = DDLFacade.getBean(ResourceOperateService.class).getPageRender(tid, pid, version);
		}
		if (render!=null && render.getDetail()!=null){
			String html = DDLFacade.getBean(RenderingService.class).getHTML(vwbcontext, render);
			pageContext.getOut().print(html);
		}
		return SKIP_BODY;
	}
}
