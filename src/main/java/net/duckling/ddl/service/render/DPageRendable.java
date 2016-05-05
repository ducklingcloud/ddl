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

package net.duckling.ddl.service.render;

import java.io.IOException;

import javax.servlet.ServletException;
import javax.servlet.jsp.PageContext;

import net.duckling.ddl.common.DDLFacade;
import net.duckling.ddl.common.VWBContext;
import net.duckling.ddl.service.resource.PageRender;
import net.duckling.ddl.service.resource.ResourceOperateService;

/**
 * Introduction Here.
 * 
 * @date Feb 26, 2010
 * @author Kevin Dong (kevin@cnic.ac.cn)
 */
public class DPageRendable implements Rendable {
    public DPageRendable(int tid, int rid) {
        this.tid = tid;
        this.rid = rid;
        this.version = -1;
    }

    public DPageRendable(int tid, int rid, int version) {
        this.tid = tid;
        this.rid = rid;
        this.version = version;
    }

    public void render(VWBContext context, PageContext pageContext) throws ServletException, IOException {
        PageRender render = null;
        if (version == -1) {
            render = DDLFacade.getBean(ResourceOperateService.class).getPageRender(tid, rid);
        } else {
            render = DDLFacade.getBean(ResourceOperateService.class).getPageRender(tid, rid, version);
        }
        if (render != null && render.getDetail() != null) {
            String html = DDLFacade.getBean(RenderingService.class).getHTML(context, render);
            pageContext.getOut().print(html);
        }
    }

    public int getResourceId() {
        return rid;
    }

    private int tid;
    private int rid;
    private int version;
}
