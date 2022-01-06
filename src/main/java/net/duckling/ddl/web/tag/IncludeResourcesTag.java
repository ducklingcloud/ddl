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
import java.util.Enumeration;
import java.util.ResourceBundle;

import javax.servlet.jsp.JspException;

import net.duckling.ddl.common.VWBContext;


/**
 * Introduction Here.
 * @date Feb 24, 2010
 * @author xiejj@cnic.cn
 */
public class IncludeResourcesTag extends VWBBaseTag {
    @Override
    public int doVWBStart() throws JspException {
        try {
            pageContext.getOut().println(getHtml());
            pageContext.getOut().flush();
            return SKIP_BODY;
        } catch (IOException e) {
            throw new JspException(e);
        }
    }

    public void setType(String type) {
        this.type = type;
    }

    private String getHtml() {
        if (type.equals(RESOURCE_JSLOCALIZEDSTRINGS)) {
            {
                return getJSLocalizedStrings(vwbcontext);
            }
        } else if (type.equals(RESOURCE_JSFUNCTION)) {
            return "/* INCLUDERESOURCES (" + type + ") */";
        }
        return "<!-- INCLUDERESOURCES (" + type + ") -->";
    }

    private static String getJSLocalizedStrings(VWBContext context) {
        StringBuffer sb = new StringBuffer();

        sb.append("var LocalizedStrings = {\n");

        ResourceBundle rb = context.getBundle("templates.default");

        boolean first = true;

        for (Enumeration en = rb.getKeys(); en.hasMoreElements();) {
            String key = (String) en.nextElement();

            if (key.startsWith("javascript")) {
                if (first) {
                    first = false;
                } else {
                    sb.append(",\n");
                }
                sb.append("\"" + key + "\":\"" + rb.getString(key) + "\"");
            }
        }
        sb.append("\n};\n");

        return (sb.toString());
    }

    private static final long serialVersionUID = 1L;

    private String type;

    private static final String RESOURCE_JSFUNCTION = "jsfunction";

    private static final String RESOURCE_JSLOCALIZEDSTRINGS = "jslocalizedstrings";
}
