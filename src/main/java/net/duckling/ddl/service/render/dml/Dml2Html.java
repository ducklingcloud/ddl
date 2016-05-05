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
import java.io.StringReader;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

import org.jdom.Document;
import org.jdom.Element;
import org.jdom.JDOMException;
import org.jdom.Text;
import org.jdom.input.SAXBuilder;

/**
 * Introduction Here.
 * 
 * @date 2010-3-8
 * @author 狄
 */
/*
 * html向dml转换
 */
public class Dml2Html {
    private static final String CYBERNEKO_PARSER = "org.cyberneko.html.parsers.SAXParser";
    // 主调用map 存所有元素 其中key=“元素名” value="储存下级元素都有什么的map"
    private static Map<String, AbstractParseDmlElement> hs = new HashMap<String, AbstractParseDmlElement>();

    static {
//        ParseDmlPluginElement parsedmlpluginelement = new ParseDmlPluginElement();
        ParseDmlSectionElement parsedmlsectionelement = new ParseDmlSectionElement();
        ParserDmlLinkElement parserdmllinkelement = new ParserDmlLinkElement();
        ParserDmlStyleElement parserdmlstyleelement = new ParserDmlStyleElement();
        PaserDmlCommElement paserdmlcommelement = new PaserDmlCommElement();
        PaserDmlPreElement paserdmlpreelement = new PaserDmlPreElement();
        PaserDmlSpanElement paserdmlspanelement = new PaserDmlSpanElement();
        PaserDmlHeadingElement paserdmlheadingelement = new PaserDmlHeadingElement();
        PaserDmlFormElement paserdmlformelement = new PaserDmlFormElement();

        // 主调用map赋值
        hs.put("d_link", parserdmllinkelement);
        hs.put("d_section", parsedmlsectionelement);
        hs.put("br", parserdmlstyleelement);
        hs.put("hr", parserdmlstyleelement);

        hs.put("pre", paserdmlpreelement);
        hs.put("span", paserdmlspanelement);

        hs.put("body", paserdmlcommelement);
        hs.put("b", paserdmlcommelement);
        hs.put("div", paserdmlcommelement);
        hs.put("em", paserdmlcommelement);
        hs.put("h1", paserdmlheadingelement);
        hs.put("h2", paserdmlheadingelement);
        hs.put("h3", paserdmlheadingelement);
        hs.put("h4", paserdmlheadingelement);
        hs.put("img", paserdmlcommelement);
        hs.put("li", paserdmlcommelement);
        hs.put("ol", paserdmlcommelement);
        hs.put("p", paserdmlcommelement);
        hs.put("strike", paserdmlcommelement);
        hs.put("strong", paserdmlcommelement);
        hs.put("sub", paserdmlcommelement);
        hs.put("sup", paserdmlcommelement);
        hs.put("font", paserdmlcommelement);
        hs.put("table", paserdmlcommelement);
        hs.put("tbody", paserdmlcommelement);
        hs.put("thead", paserdmlcommelement);
        hs.put("tfoot", paserdmlcommelement);
        hs.put("td", paserdmlcommelement);
        hs.put("tr", paserdmlcommelement);
        hs.put("u", paserdmlcommelement);
        hs.put("ul", paserdmlcommelement);
        hs.put("i", paserdmlcommelement);
        hs.put("th", paserdmlcommelement);
        hs.put("embed", paserdmlcommelement);
        hs.put("font", paserdmlcommelement);
        hs.put("form", paserdmlformelement);

    }

    private Dml2HtmlEngine newdml2htmlengine = new Dml2HtmlEngine();

    /*
     * 构造函数
     */
    public Dml2Html() {
    }

    /*
     * 构造函数
     */
    public Dml2Html(String dml, Dml2HtmlEngine dml2htmlengine) throws JDOMException, IOException {

        if (dml != null && !"".equals(dml)) {
            Element e = getRootElement(dml);
            this.newdml2htmlengine = dml2htmlengine;
            getChildren(e, newdml2htmlengine);
        } else {
            newdml2htmlengine.getMout().println("");
        }

    }

    /*
     * 取到body元素
     */
    private Element getRootElement(String html) throws JDOMException, IOException {
        Element belement = null;
        SAXBuilder builder = new SAXBuilder(CYBERNEKO_PARSER, true);
        if (html.startsWith("<?xml version=")) {
            String temp = "<?xml version=\"1.0\" encoding=\"UTF-8\"?>";
            html = html.substring(temp.length());
        }
        Document doc = builder.build(new StringReader(html));
        Element htmlelement = doc.getRootElement();
        if (htmlelement != null) {
            if (htmlelement.getName().equalsIgnoreCase("html")) {
                if (htmlelement.getChildren().size() > 0) {
                    belement = (Element) htmlelement.getChildren().get(0);
                } else {
                    belement = new Element("p");
                }
            }
        }
        return belement;
    }

    /*
     * 输出Text
     */
    private void printText(Object element, Dml2HtmlEngine dml2htmlengine) {
        Text t = (Text) element;
        String s = t.getText();

        if (!s.equals("")) {
            s = s.replaceAll("&", "&amp;");
            s = s.replaceAll(">", "&gt;");
            s = s.replaceAll("<", "&lt;");

            if (dml2htmlengine.getPreType() > 0) {
                dml2htmlengine.getMout().print(s);
            } else {
                s = s.replaceAll("[\\r\\n\\f\\u0085\\u2028\\u2029\\ufeff]", "");

                String pname = t.getParentElement().getName().toLowerCase();
                if (!"td".equals(pname))
                    if (s != null) {
                        while (s.length() > 0) {
                            int i = s.charAt(0);
                            if (i == 160) {
                                s = s.substring(1);
                            } else if (i == 32) {
                                s = s.substring(1);
                            } else if (i == 12288) {
                                s = s.substring(1);
                            } else {
                                break;
                            }
                        }
                    }

                /**
                 * 解决目前在xhtml标准下
                 * <p/>
                 * 元素看不到的问题 但是造成保存后空行多一个空格的问题 2011-11-10
                 */
                if ("p".equals(pname)) {
                    if (s == null || s.length() == 0 || "".equals(s)) {
                        s = "&nbsp;";
                    }
                }
                dml2htmlengine.getMout().println(s);
            }
        }
    }

    /*
     * 判断当前元素是否有子元素 并做遍历 需要特殊处理的有“br hr D_link D_puglin D_section ”
     */
    protected void getChildren(Element parentelement, Dml2HtmlEngine dml2htmlengine) throws IOException, JDOMException {
        for (Iterator i = parentelement.getContent().iterator(); i.hasNext();) {
            Object ec = i.next();
            if (ec instanceof Element) {
                Element childelement = (Element) ec;
                String cename = childelement.getName().toLowerCase();
                AbstractParseDmlElement parser = hs.get(cename);
                if (parser != null) {
                    parser.printElement(childelement, dml2htmlengine);
                } else {
                    PaserDmlCommElement paserdmlcommelement = new PaserDmlCommElement();
                    paserdmlcommelement.printElement(childelement, dml2htmlengine);
                }

            } else if (ec instanceof Text) {

                printText(ec, dml2htmlengine);
            }
        }

    }

    /*
     * 输出DML
     */
    public String getHTMLString() {
        String res = "";
        res = newdml2htmlengine.getMoutTimmer().toString();
        res = res.replaceAll("[\\r\\n\\f\\u0085\\u2028\\u2029]", "");
        return res;
    }
}
