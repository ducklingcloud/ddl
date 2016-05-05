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
package net.duckling.ddl.service.diff.daisy;

import java.util.ArrayList;

/***************
 * by liuboyun 功能一：建树 input: string（页面文本内容） * output：dml tree 功能二：树的输出 input：dml
 * tree * output：string
 ****************/
public class DmlTree {

    public ArrayList<String> deleteList = new ArrayList<String>();// 被删除结点的路径及内容
    public DmlNode root = new DmlNode();
    public ArrayList<String> info = new ArrayList<String>();// XML文件头部的<?...?>和<!...>的内容
    private String XMLString;
    public String OutputString = "";
    public int numNode = 1;// 结点个数
    public ArrayList<DmlNode> leafList = new ArrayList<DmlNode>();// 叶子结点序列
    public int deleteId = 0;

    public void Build(String inputString) {
        numNode = 1;
        String str160 = "" + (char) 160;
        XMLString = delBodyLabel(inputString).replace(str160, "");
        SetInfo();
        Create(root, XMLString);// 建树
        setLeafNode();
        setAllSonLength(root);
    }

    public boolean setAllSonLength(DmlNode p) {
        if (p.son.size() == 0) {
            if (isNotStrNull(p.text)) {
                p.allSonTextVis = true;
                return true;
            } else
                return false;
        } else {
            boolean tmp = false;
            for (DmlNode pNode : p.son) {
                if (setAllSonLength(pNode)) {
                    tmp = true;
                    p.allSonTextVis = true;
                }
            }
            return tmp;
        }

    }

    private void setLeafNode() {
        root.id = 0;
        for (DmlNode p : root.son) {
            setLeafNodeList(p, "");
        }

    }

    private void setLeafNodeList(DmlNode pNode, String pare) {// 找到叶子结点，把其加入leaflist中，并得到结点的parentlist和id。
        pNode.parentList += pare + pNode.label;
        if (pNode.label.toLowerCase().equals("tr")) // 为了表格展示效果，将tr看做是一个叶子结点。
        {
            setTRNodeList(pNode, pNode);
            leafList.add(pNode);
            pNode.isTr = true;
            return;
        }
        if (pNode.son.size() == 0) {
            leafList.add(pNode);
        }

        if (pNode.isLeaf) {
            return;
        } else {
            if (pNode.isPair) {
                for (DmlNode p : pNode.son) {
                    setLeafNodeList(p, pare + pNode.label);
                }
            }
        }
    }

    private void setTRNodeList(DmlNode trNode, DmlNode pCur) // 将tr的所有文本结点，存入TRNodeList
    {
        if (pCur.son.size() == 0) {
            trNode.TRNodeList.add(pCur);
            trNode.trStr += pCur.text;
            return;
        }
        for (DmlNode p : pCur.son) {
            setTRNodeList(trNode, p);
        }

    }

    public String OutputXML() {
        OutputString = "";
        for (String s : info) {
            OutputString += s;
        }
        for (DmlNode p : root.son) {
            OutputTree(p);
        }
        return OutputString;
    }

    private boolean isNotStrNull(String str) {
        String str160 = "";
        str160 += (char) (160);
        if ("".equals(str.replace(str160, "").trim())) {
            return false;
        }

        return true;
    }

    private void OutputTree(DmlNode pNode) {
        String tmp;
        if (pNode.isLeaf) {
            tmp = pNode.text;
            if (!pNode.handLabel.equals("") && isNotStrNull(tmp)) {
                tmp = "<span class=\"" + pNode.handLabel + "\">" + tmp + "</span>";
            }

            OutputString += pNode.movPre + tmp + pNode.movLast;
        } else {
            if (pNode.isPair) {
                if (pNode.attribute.size() > 0) {
                    tmp = "<" + pNode.label + " " + pNode.attribute.get(0) + ">";
                } else {
                    tmp = "<" + pNode.label + ">";
                }
                if (!pNode.handLabel.equals("")) {
                    if (pNode.allSonTextVis)
                        tmp = "<span class=\"" + pNode.handLabel + "\">" + tmp;
                }

                if (isNotStrNull(tmp))
                    OutputString += pNode.movPre + tmp;
                if (pNode.text.length() > 0) {
                    OutputString += pNode.text;
                }
                for (DmlNode p : pNode.son) {
                    OutputTree(p);
                }
                tmp = "</" + pNode.label + ">";
                if (!pNode.handLabel.equals("")) {
                    if (pNode.allSonTextVis)
                        tmp = tmp + "</span>";
                }

                OutputString += tmp + pNode.movLast;
            } else {
                if (pNode.attribute.size() > 0) {
                    tmp = "<" + pNode.label + " " + pNode.attribute + "/>";
                } else {
                    tmp = "<" + pNode.label + "/>";
                }
                if (!pNode.handLabel.equals("")) {
                    if (pNode.allSonTextVis)
                        tmp = "<span class=\"" + pNode.handLabel + "\">" + tmp + "</span>";
                }
                OutputString += pNode.movPre + tmp + pNode.movLast;
            }
        }
    }

    private String delBodyLabel(String bodyString) {

        String noBodyStr = bodyString;
        noBodyStr = noBodyStr.replace("<BODY>", "");
        noBodyStr = noBodyStr.replace("<body>", "");
        noBodyStr = noBodyStr.replace("</BODY>", "");
        noBodyStr = noBodyStr.replace("</body>", "");

        return noBodyStr;
    }

    private void Create(DmlNode pNode, String bodyString) {
        // 找到“<”就表示找到一个标签,目前没有考虑“\<”的情况。
        int beg = bodyString.indexOf("<");
        int end;
        if (beg == -1) {
            if (bodyString.trim().equals("")) {
                return;
            }

            DmlNode tmp = new DmlNode();
            tmp.text = bodyString.trim();
            tmp.parent = pNode;
            tmp.isLeaf = true;
            tmp.id = numNode;
            numNode++;
            pNode.son.add(tmp);

            return;
        }
        if (beg >= 0) {
            end = bodyString.indexOf(">", beg + 1);
            if (end > 0) {
                // 不成对的标签，如<br/>
                if (bodyString.charAt(end - 1) == '/') {
                    DmlNode tmp = new DmlNode();
                    tmp.id = numNode;
                    numNode++;
                    tmp.parent = pNode;
                    tmp.label = bodyString.substring(beg + 1, end - 1).trim();
                    tmp.isLeaf = false;
                    tmp.isPair = false;

                    if (beg > 0) {
                        Create(pNode, bodyString.substring(0, beg));
                    }
                    pNode.son.add(tmp);
                    if (end + 1 < bodyString.length()) {
                        Create(pNode, bodyString.substring(end + 1));
                    }
                    return;
                } else { // 成对的标签，如<tr>...</tr>
                    String tmplabel[] = bodyString.substring(beg + 1, end).split(" ", 2);
                    DmlNode tmp = new DmlNode();
                    tmp.id = numNode;
                    numNode++;
                    tmp.parent = pNode;
                    tmp.label = tmplabel[0].trim();
                    tmp.isLeaf = false;
                    tmp.isPair = true;
                    if (tmplabel.length > 1) {
                        tmp.attribute.add(tmplabel[1].trim());
                    }
                    String beglabel = "<" + tmp.label;
                    String endlabel = "</" + tmp.label;
                    int tmppos = end + 1;

                    while (true) { // 找到与标签配对的尾标签
                        int tmpend = bodyString.indexOf(endlabel, tmppos);
                        int tmpbeg = bodyString.indexOf(beglabel, tmppos);

                        if (tmpbeg == -1 && tmpend == -1) {
                            tmp.isLeaf = true;
                            tmp.parent = pNode;
                            tmp.text = bodyString;
                            pNode.son.add(tmp);
                            return;

                        }

                        if ((tmpbeg == -1 && tmpend >= 0) || (tmpbeg > tmpend && tmpend >= 0)) {
                            tmp.allText = bodyString.substring(end + 1, tmpend);

                            if (tmppos < tmpend) {
                                Create(tmp, bodyString.substring(end + 1, tmpend));
                            }

                            if (beg > 0) {
                                Create(pNode, bodyString.substring(0, beg));
                            }
                            pNode.son.add(tmp);
                            if (tmpend + endlabel.length() + 1 < bodyString.length()) {
                                Create(pNode, bodyString.substring(tmpend + endlabel.length() + 1, bodyString.length()));
                            }
                            break;
                        }
                        if (tmppos < tmpbeg + 1) {
                            tmppos = tmpbeg + 1;
                        }
                        if (tmppos < tmpend + 1) {
                            tmppos = tmpend + 1;
                        }
                        if (tmppos >= bodyString.length()) {
                            break;// 是否应加异常处理？此处标签是否匹配？
                        }
                    }
                }
            }
        }
    }

    private void SetInfo() {
        while (true) {
            int beg1 = XMLString.indexOf("<?");
            int beg2 = XMLString.indexOf("<!");
            if (beg1 == -1 && beg2 == -1) {
                break;
            }
            if (beg1 == -1) {
                beg1 = XMLString.length() + 1;
            }
            if (beg2 == -1) {
                beg2 = XMLString.length() + 1;
            }
            int end1, end2;
            if (beg1 < beg2) {
                end1 = XMLString.indexOf("?>", beg1 + 2);
                if (end1 >= 0) {
                    String tmp = XMLString.substring(beg1, end1 + 2);
                    info.add(tmp);
                }
                XMLString = XMLString.substring(end1 + 2);
            } else {
                end2 = XMLString.indexOf(">", beg2 + 1);
                if (end2 >= 0) {
                    String tmp = XMLString.substring(beg2, end2 + 1);
                    info.add(tmp);
                }
                XMLString = XMLString.substring(end2 + 1);
            }
        }

    }

}
