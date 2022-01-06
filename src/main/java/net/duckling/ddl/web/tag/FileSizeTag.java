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
import javax.servlet.jsp.JspWriter;
import javax.servlet.jsp.tagext.BodyContent;
import javax.servlet.jsp.tagext.BodyTag;

import net.duckling.ddl.util.FileSizeUtils;

public class FileSizeTag extends VWBBaseTag implements BodyTag{
    /**
     *
     */
    private static final long serialVersionUID = 2672752017538463666L;
    private long size;


    public void setSize(long size) {
        this.size = size;
    }


    @Override
    public int doVWBStart() throws Exception {
        return EVAL_BODY_BUFFERED;
    }

    @Override
    public int doEndTag() throws JspException {
        JspWriter j = pageContext.getOut();
        try {
            j.print(getTagConext());
        } catch (IOException e) {
            LOG.error("",e);
        }
        return super.doEndTag();
    }


    private String getTagConext() {
        return FileSizeUtils.getFileSize(size);
    }


    @Override
    public void setBodyContent(BodyContent b) {
        // TODO Auto-generated method stub

    }


    @Override
    public void doInitBody() throws JspException {
        // TODO Auto-generated method stub

    }
}
