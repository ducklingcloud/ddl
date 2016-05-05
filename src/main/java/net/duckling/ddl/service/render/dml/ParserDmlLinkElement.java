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
import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.util.List;

import net.duckling.ddl.service.tobedelete.Page;

import org.apache.log4j.Logger;
import org.jdom.Attribute;
import org.jdom.Element;
import org.jdom.JDOMException;

/**
 * Introduction Here.
 * 
 * @date 2010-3-8
 * @author 狄
 */

public class ParserDmlLinkElement extends AbstractParseDmlElement {

    static final String[] cExternalLinks = { "http:", "ftp:", "https:", "mailto:", "news:", "file:", "rtsp:", "mms:",
            "ldap:", "gopher:", "nntp:", "telnet:", "wais:", "prospero:", "z39.50s", "z39.50r", "vemmi:", "imap:",
            "nfs:", "acap:", "tip:", "pop:", "dav:", "opaquelocktoken:", "sip:", "sips:", "tel:", "fax:", "modem:",
            "soap.beep:", "soap.beeps", "xmlrpc.beep", "xmlrpc.beeps", "urn:", "go:", "h323:", "ipp:", "tftp:",
            "mupdate:", "pres:", "im:", "mtqp", "smb:" };

    private boolean attachmentType = false;

    private static final Logger LOG = Logger.getLogger(ParserDmlLinkElement.class);

    public void printHref(String strAttValue, Dml2HtmlEngine dml2htmlengine, Element element) {

        String addclass = "";

        if (element.getAttribute("class") != null) {
            addclass = element.getAttribute("class").getValue();
            if (addclass != null) {
                addclass = addclass.replace("attach", "");
                addclass = addclass.replace("attachment", "");
                addclass = addclass.replace("JSP", "");
                addclass = addclass.replace("Portal", "");
                addclass = addclass.replace("NoResourceType", "");
                addclass = addclass.replace("DPage", "");
                addclass = addclass.replace("createpage", "");

                addclass = addclass.trim();

                if (!"".equals(addclass)) {
                    addclass = " " + addclass;
                }
            } else {
                addclass = "";
            }
        }

        if (isExternalLink(strAttValue)) {// 链接地址是正常地址

            dml2htmlengine.getMout().print(" href=\"" + strAttValue + "\"");

        } else if (strAttValue.startsWith("baseurl://")) {
            if (dml2htmlengine.isMwysiwygEditorMode()) {
                dml2htmlengine.getMout().print(" href=\"" + strAttValue + "\"");
            } else {
                dml2htmlengine.getMout().print(
                        " href=\"" + dml2htmlengine.getDmlcontext().getBaseUrl() + strAttValue.substring(10) + "\"");
            }
        } else if (strAttValue.startsWith("#")) {// 链接地址是页面锚点
            // edit by diyanliang 2011-2-16
            dml2htmlengine.getMout().print(" href=\"" + strAttValue + "\"");
        } else {// 链接地址是本地附件或者页面
            String herftype = element.getAttributeValue("linktype");
            if ("view".equals(herftype) || herftype == null) {
                int resourceId = 0;
                try {
                    resourceId = Integer.valueOf(strAttValue.trim());
                } catch (Exception e) {
                    String tempstrAttValue = strAttValue.trim();
                    int lastindex = tempstrAttValue.lastIndexOf('/');
                    try {
                        tempstrAttValue = tempstrAttValue.substring(lastindex + 1);
                        resourceId = Integer.valueOf(tempstrAttValue);
                    } catch (Exception e2) {
                        resourceId = 0;
                    }
                }
                if (dml2htmlengine.isMwysiwygEditorMode()) {
                    Page viewport = ((DmlContextBridge) dml2htmlengine.getDmlcontext()).getViewPort(resourceId);
                    if (viewport != null) {
                        dml2htmlengine.getMout().print(" title=\"" + viewport.getTitle() + "\" ");
                    }
                }

            }

            if (isOldAttachLink(strAttValue)) {
                strAttValue = fixClbUrl(strAttValue);
                herftype = "attach";
            }
            dml2htmlengine.getMout().print(
                    " class=\"" + herftype + "\" href=\""
                            + dml2htmlengine.getDmlcontext().getURL(herftype, strAttValue, null) + "\"");
        }
    }

    public void printAttribute(Element e, Dml2HtmlEngine dml2htmlengine) {
        List attList = e.getAttributes();
        for (int i = 0; i < attList.size(); i++) {
            String strAttName = ((Attribute) attList.get(i)).getName();
            String strAttValue = ((Attribute) attList.get(i)).getValue();

            if (strAttName.equalsIgnoreCase("href")) {
                printHref(strAttValue, dml2htmlengine, e);
            } else if (!strAttName.equalsIgnoreCase("title")) {
                dml2htmlengine.getMout().print(" " + strAttName + "=\"" + strAttValue + "\"");

            }
        }
    }

    public void printElement(Element element, Dml2HtmlEngine dml2htmlengine) {
        dml2htmlengine.getMout().print("<a ");
        printAttribute(element, dml2htmlengine);
        if (dml2htmlengine.getPreType() > 0) {
            dml2htmlengine.getMout().print(">");
        } else {
            dml2htmlengine.getMout().println(">");
        }
        try {
            d2h.getChildren(element, dml2htmlengine);
        } catch (IOException e1) {
            LOG.error(e1);
        } catch (JDOMException e1) {
            LOG.error(e1);
        }
        if (dml2htmlengine.getPreType() > 0) {
            dml2htmlengine.getMout().print("</a>");
            // 如果是附件 在后面加上附件的图片
            if (attachmentType) {
                dml2htmlengine.getMout().print(
                        "<img border=\"0\" src=\"" + dml2htmlengine.getDmlcontext().getBaseUrl()
                                + "images/attachment_small.png\" alt=\"(info)\" />");
                attachmentType = false;
            }
        } else {
            dml2htmlengine.getMout().println("</a>");
            // 如果是附件 在后面加上附件的图片
            if (attachmentType) {
                dml2htmlengine.getMout().println(
                        "<img border=\"0\" src=\"" + dml2htmlengine.getDmlcontext().getBaseUrl()
                                + "images/attachment_small.png\" alt=\"(info)\" />");
                attachmentType = false;
            }
        }
    }

    private boolean isExternalLink(String link) {
        for (int i = 0; i < cExternalLinks.length; i++) {
            if (link.startsWith(cExternalLinks[i])) {
                return true;
            }
        }

        return false;
    }

    public static String encodeName(String pagename) {
        try {
            return URLEncoder.encode(pagename, "UTF-8");
        } catch (UnsupportedEncodingException e) {
            return ("ISO-8859-1 not a supported encoding!?!  Your platform is borked.");
        }
    }

    public static String fixClbUrl(String str) {
        String restr = "";
        if (str != null && str.startsWith("attach/")) {
            restr = str.replace("attach/", "");
        }
        return restr;
    }

    public static boolean isOldAttachLink(String str) {

        return (str != null && str.startsWith("attach/"));

    }
}
