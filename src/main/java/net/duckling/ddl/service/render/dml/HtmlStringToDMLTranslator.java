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

package net.duckling.ddl.service.render.dml;

import java.io.IOException;


import net.duckling.ddl.common.VWBContext;

import org.apache.log4j.Logger;
import org.jdom.Document;
import org.jdom.Element;
import org.jdom.IllegalDataException;
import org.jdom.JDOMException;
import org.jdom.output.XMLOutputter;


/**
 * Introduction Here.
 * 
 * @date 2010-3-4
 * @author diyanliang@cnic.cn
 */
public class HtmlStringToDMLTranslator {
    private static final Logger LOG = Logger.getLogger(HtmlStringToDMLTranslator.class);

    @SuppressWarnings("unused")
    public String translate(String html, VWBContext vwbcontext) throws JDOMException, IOException {
        if (html.length() > 13) {
            if ((html.substring(html.length() - 13, html.length())).equalsIgnoreCase("<p>&nbsp;</p>")) {
                html = html.substring(0, html.length() - 13);
            }
        }
        if (html.length() > 15) {
            String strremove = html.substring(html.length() - 15, html.length());
            String subStr1 = strremove.substring(0, 3);
            int ich = strremove.charAt(3);
            String subStr2 = strremove.substring(4);
            if ("<p>".equalsIgnoreCase(subStr1) && "</p></body>".equalsIgnoreCase(subStr2) && ich == 160) {
                html = html.substring(0, html.length() - 15) + "</BODY>";
            }
        }
        if (html.length() > 35) {
            if ((html.substring(html.length() - 35, html.length()))
                    .equalsIgnoreCase("<p><br type=\"_moz\"></br></p></body>")) {
                html = html.substring(0, html.length() - 35) + "</BODY>";
            }
        }
        String dMlMarkup = "";
        if (!("").equals(html)) {

            Html2DmlEngine html2dmlengine = new Html2DmlEngine();
            html2dmlengine.setBaseURL(vwbcontext.getBaseURL());
            html2dmlengine.setVwbcontext(vwbcontext);
            DmlContextBridge dmlcontextbridge = new DmlContextBridge(vwbcontext);
            html2dmlengine.setDmlcontext(dmlcontextbridge);
            try {
                Html2Dml h2d = new Html2Dml(html, html2dmlengine);
                dMlMarkup = h2d.getDMLString();
            } catch (IllegalDataException e) {
                try {
                    html = fixHtml(html);
                    Html2Dml h2d = new Html2Dml(html, html2dmlengine);
                    dMlMarkup = h2d.getDMLString();
                } catch (Exception ex) {
                    LOG.error(ex);
                }
            } catch (Exception ey) {
                LOG.error(ey);
            }
        }
        return dMlMarkup;

    }

    // 如果jdom解析时候发现有非XMLCharacter的字符 在这里重新处理一下字符串
    private String fixHtml(String html) {
        String reStr = "";
        String fixStr = html;
        StringBuffer newhtml = new StringBuffer("");
        for (int i = 0; i < fixStr.length(); i++) {
            int ichar = fixStr.charAt(i);
            if (isXMLCharacter(ichar)) {
                newhtml.append(fixStr.charAt(i));
            }
        }
        reStr = newhtml.toString();
        return reStr;
    }

    private boolean isXMLCharacter(int c) {
        if (c <= 0xD7FF) {
            if (c >= 0x20) {
                return true;
            } else if (c == ' ') {
                return true;
            } else {
                return false;
            }
        }
        if (c < 0xE000) {
            return false;
        }
        if (c <= 0xFFFD) {
            return true;
        }
        if (c < 0x10000) {
            return false;
        }
        if (c <= 0x10FFFF) {
            return true;
        }
        return false;
    }

    public static String element2String(Element element) {
        Document document = new Document(element);
        XMLOutputter outputter = new XMLOutputter();
        return outputter.outputString(document);
    }

}
