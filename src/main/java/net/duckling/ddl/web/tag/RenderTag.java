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

import javax.servlet.jsp.JspException;
import javax.servlet.jsp.JspTagException;

import net.duckling.ddl.service.render.Rendable;


/**
 * 显示模式检查Tag
 * @date Feb 4, 2010
 * @author xiejj@cnic.cn
 */
public class RenderTag extends VWBBaseTag {
    private static final long serialVersionUID = 1L;

    @Override
    public int doVWBStart() throws JspException {
        try {
            if (content != null) {
                content.render(vwbcontext, pageContext);
            }
        } catch (Throwable e) {
            throw new JspTagException(e);
        }
        return SKIP_BODY;
    }

    public void setContent(Rendable content) {
        this.content = content;
    }

    protected Rendable content;
}
