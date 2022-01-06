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

public class DmlDiff {

    int moveNum = 0;
    int limitLength = 1000;
    double trPecent = 0.8;// 表格的tr结点中的所有文本的匹配精确度。
    double textPecent = 0.7;// 字符串匹配的精确度。如果精确度超过textpecent，就认为是content
    // update，否则为add。
    int borderLength = 5;// 如果newtree中的某节点文本内容长度大于borderlength，才会使用textpecent参数进行判断。否则，直接认为是add。

    public void compareTree(ArrayList<DmlNode> oldNodeList, ArrayList<DmlNode> newNodeList) {// 传入的是两棵树的叶子结点的列表。目的是得到新、老树的叶子结点的handflag以及新、老树的匹配结点对（即变量match）。
        int newNodeNum = newNodeList.size();
        int oldNodeNum = oldNodeList.size();

        boolean oldFlag[] = new boolean[oldNodeNum];// 该数组表示某节点是否已经进行判断了
        int newMatch[] = new int[newNodeNum];
        int oldMatch[] = new int[oldNodeNum];

        // 初始化Flag和match数组
        for (int i = 0; i < newNodeNum; i++) {
            newMatch[i] = -1;
        }
        for (int i = 0; i < oldNodeNum; i++) {
            oldFlag[i] = false;
            oldMatch[i] = -1;
        }
        int oldBegId = 0;
        int newBegId = 0;
        int maxLength = -1;
        for (int i = 0; i < newNodeNum; i++)// 找到新树中最长的叶子结点，从此处开始匹配。
        {
            DmlNode tmpNode = newNodeList.get(i);
            if (tmpNode.text.length() > maxLength) {
                maxLength = tmpNode.text.length();
                newBegId = i;
            }

        }
        int maxId = newBegId;
        int tmpId;// tmpId用来标注oldtree中的当前结点在oldNodelist（叶子结点序列）中的位置，maxId用来标注newtree中的当前结点在newNodelist中的位置。
        for (int i = 0; i < newNodeNum; i++) {
            tmpId = oldBegId;
            DmlNode maxNode = newNodeList.get(maxId);
            for (int j = 0; j < oldNodeNum; j++) {
                if (oldFlag[tmpId]) {
                    tmpId++;
                    tmpId = tmpId % oldNodeNum;
                    continue;
                }
                DmlNode p = oldNodeList.get(tmpId);
                if (maxNode.parentList.equalsIgnoreCase(p.parentList)) {// 对路径敏感

                    if (maxNode.isTr != p.isTr) {
                        continue;
                    }

                    if (maxNode.isTr) {
                        if (maxNode.trStr.length() > borderLength && maxNode.trStr.length() < limitLength
                            /* && maxLength < limitLength */) {// ???????????????????????
                            String stringLCS = LCS.GetLCSString(maxNode.trStr, p.trStr);
                            if ((stringLCS.length() * 2.0) / (maxNode.trStr.length() + p.trStr.length()) > trPecent) {
                                newMatch[maxId] = tmpId;
                                oldMatch[tmpId] = maxId;
                                oldFlag[tmpId] = true;
                                break;
                            }
                        } else {
                            if (maxNode.text.equals(p.text)) {
                                newMatch[maxId] = tmpId;
                                oldMatch[tmpId] = maxId;
                                oldFlag[tmpId] = true;
                                break;
                            }
                        }

                    } else {
                        if (maxNode.text.length() > borderLength && maxNode.text.length() < limitLength
                            && maxLength < limitLength) {

                            String stringLCS = LCS.GetLCSString(maxNode.text, p.text);
                            if ((stringLCS.length() * 2.0) / (maxNode.text.length() + p.text.length()) > textPecent) {
                                newMatch[maxId] = tmpId;
                                oldMatch[tmpId] = maxId;
                                oldFlag[tmpId] = true;
                                break;
                            }
                        } else {
                            if (maxNode.text.equals(p.text)) {
                                newMatch[maxId] = tmpId;
                                oldMatch[tmpId] = maxId;
                                oldFlag[tmpId] = true;
                                break;
                            }
                        }
                    }
                }
                tmpId++;
                tmpId = tmpId % oldNodeNum;
                oldBegId = tmpId;
            }
            maxId++;
            maxId = maxId % newNodeNum;
        }
        int transNewNodeNum = 0;
        int transOldNodeNum = 0;
        int transOldId[] = new int[oldNodeNum];
        int transNewId[] = new int[newNodeNum];

        ArrayList<DmlNode> transOldList = new ArrayList<DmlNode>();
        for (int i = 0; i < oldNodeNum; i++) {
            transOldId[i] = transOldNodeNum;
            DmlNode p = oldNodeList.get(i);
            if (p.isTr) {
                transOldNodeNum += p.TRNodeList.size();
                for (int j = 0; j < p.TRNodeList.size(); j++) {
                    transOldList.add(p.TRNodeList.get(j));
                }
            } else {
                transOldList.add(p);
                transOldNodeNum++;
            }
        }

        ArrayList<DmlNode> transNewList = new ArrayList<DmlNode>();
        for (int i = 0; i < newNodeNum; i++) {
            transNewId[i] = transNewNodeNum;
            DmlNode p = newNodeList.get(i);
            if (p.isTr) {
                transNewNodeNum += p.TRNodeList.size();
                for (int j = 0; j < p.TRNodeList.size(); j++) {
                    transNewList.add(p.TRNodeList.get(j));

                }
            } else {
                transNewList.add(p);
                transNewNodeNum++;
            }
        }
        int transNewMatch[] = new int[transNewNodeNum];
        int transOldMatch[] = new int[transOldNodeNum];
        for (int i = 0; i < newNodeNum; i++) {
            transNewMatch[i] = -1;
        }
        for (int i = 0; i < oldNodeNum; i++) {
            transOldMatch[i] = -1;
        }
        for (int i = 0; i < newNodeNum; i++) {
            DmlNode trNew = newNodeList.get(i);
            if (trNew.isTr) {
                if (newMatch[i] != -1) {
                    int trOldBeg = 0;
                    DmlNode trOld = oldNodeList.get(newMatch[i]);
                    if (trNew.TRNodeList.size() == trOld.TRNodeList.size()) {
                        for (int x = 0; x < trNew.TRNodeList.size(); x++) {
                            transNewMatch[transNewId[i] + x] = transOldId[newMatch[i]] + x;
                            transOldMatch[transOldId[newMatch[i]] + x] = transNewId[i] + x;
                        }
                        continue;
                    }

                    boolean trNewFlag[] = new boolean[trNew.TRNodeList.size()];
                    boolean trOldFlag[] = new boolean[trOld.TRNodeList.size()];
                    for (int j = 0; j < trNew.TRNodeList.size(); j++) {
                        trNewFlag[j] = false;
                    }
                    for (int k = 0; k < trOld.TRNodeList.size(); k++) {
                        trOldFlag[k] = false;
                    }

                    for (int j = 0; j < trNew.TRNodeList.size(); j++) {
                        for (int k = trOldBeg; k < trOld.TRNodeList.size() + trOldBeg; k++) {
                            int x = j, y = k % trOld.TRNodeList.size();
                            if (trOldFlag[y]) {
                                continue;
                            }
                            if (trNew.TRNodeList.get(x).text.equals(trOld.TRNodeList.get(y).text)) {
                                transNewMatch[transNewId[i] + x] = transOldId[newMatch[i]] + y;
                                transOldMatch[transOldId[newMatch[i]] + y] = transNewId[i] + x;
                                trNewFlag[x] = true;
                                trOldFlag[y] = true;
                                trOldBeg = ++k;
                                break;
                            }

                        }
                    }
                    int y = 0;
                    for (int x = 0; x < trNew.TRNodeList.size(); x++) {
                        if (y >= trOld.TRNodeList.size()) {
                            break;
                        }
                        if (trNewFlag[x]) {
                            y++;
                            continue;
                        }
                        if (x == 0) {
                            if (trOldFlag[y]) {
                                continue;
                            } else {
                                transNewMatch[transNewId[i] + x] = transOldId[newMatch[i]] + y;
                                transOldMatch[transOldId[newMatch[i]] + y] = transNewId[i] + x;
                                trNewFlag[x] = true;
                                trOldFlag[y] = true;
                                y++;
                            }
                        } else {
                            if (trNewFlag[x - 1] == true && trOldFlag[y] == false) {
                                transNewMatch[transNewId[i] + x] = transOldId[newMatch[i]] + y;
                                transOldMatch[transOldId[newMatch[i]] + y] = transNewId[i] + x;
                                trNewFlag[x] = true;
                                trOldFlag[y] = true;
                                y++;
                            }
                        }
                    }
                }
            } else {
                if (newMatch[i] != -1) {
                    transNewMatch[transNewId[i]] = transOldId[newMatch[i]];
                    transOldMatch[transOldId[newMatch[i]]] = transNewId[i];
                }
            }
        }

        newNodeNum = transNewNodeNum;
        oldNodeNum = transOldNodeNum;
        newMatch = transNewMatch;
        oldMatch = transOldMatch;
        oldNodeList = transOldList;
        newNodeList = transNewList;
        // 在求最大公共子序列之前，将tr变为多个td

        int lcsA[] = newMatch;
        int lcsB[] = new int[oldNodeNum];
        int lcsC[] = new int[newNodeNum];
        for (int i = 0; i < oldNodeNum; i++) {
            lcsB[i] = i;
        }
        LCS lcs = new LCS();
        int sameLength = lcs.GetLCSNode(lcsA, lcsB, lcsC);
        for (int i = 0; i < newNodeNum; i++) {
            if (newMatch[i] >= 0) {// 处理匹配上的结点。凡是匹配成功的，先将handflag赋值为move。
                if (i >= newNodeList.size())
                    continue;
                if (newMatch[i] >= oldNodeList.size())
                    continue;
                if (newNodeList.get(i).text.equals(oldNodeList.get(newMatch[i]).text)) {
                    newNodeList.get(i).match = oldNodeList.get(newMatch[i]).id;
                    newNodeList.get(i).matchNode = oldNodeList.get(newMatch[i]);
                    newNodeList.get(i).handFlag = 1;
                }
            }

        }
        for (int i = 0; i < oldNodeNum; i++) {
            if (oldMatch[i] >= 0) {// 处理匹配上的结点。凡是匹配成功的，先将handflag赋值为move。
                if (i >= oldNodeList.size())
                    continue;
                if (oldMatch[i] >= newNodeList.size())
                    continue;
                if (oldNodeList.get(i).text.equals(newNodeList.get(oldMatch[i]).text)) {
                    oldNodeList.get(i).match = newNodeList.get(oldMatch[i]).id;
                    oldNodeList.get(i).handFlag = 1;
                    oldNodeList.get(i).matchNode = newNodeList.get(oldMatch[i]);
                }
            }

        }
        for (int i = 0; i < sameLength; i++) {
            if (oldMatch[lcsC[i]] >= 0) {// 从handflag为1的结点中，分出match、move、content_update和label_update四种。
                if (oldNodeList.get(lcsC[i]).parentList.equalsIgnoreCase(newNodeList.get(oldMatch[lcsC[i]]).parentList)) {
                    if (oldNodeList.get(lcsC[i]).text.equals(newNodeList.get(oldMatch[lcsC[i]]).text)) {
                        oldNodeList.get(lcsC[i]).handFlag = 0;
                        newNodeList.get(oldMatch[lcsC[i]]).handFlag = 0;
                    } else {
                        oldNodeList.get(lcsC[i]).handFlag = 4;
                        newNodeList.get(oldMatch[lcsC[i]]).handFlag = 4;
                        oldNodeList.get(lcsC[i]).contentUpdateString = newNodeList.get(oldMatch[lcsC[i]]).text;
                        newNodeList.get(oldMatch[lcsC[i]]).contentUpdateString = oldNodeList.get(lcsC[i]).text;
                        oldNodeList.get(lcsC[i]).match = newNodeList.get(oldMatch[lcsC[i]]).id;
                        newNodeList.get(oldMatch[lcsC[i]]).match = oldNodeList.get(lcsC[i]).id;
                        oldNodeList.get(lcsC[i]).matchNode = newNodeList.get(oldMatch[lcsC[i]]);
                        newNodeList.get(oldMatch[lcsC[i]]).matchNode = oldNodeList.get(lcsC[i]);

                    }
                } else {
                    if (oldNodeList.get(lcsC[i]).text.equals(newNodeList.get(oldMatch[lcsC[i]]).text)) {
                        oldNodeList.get(lcsC[i]).handFlag = 3;
                        newNodeList.get(oldMatch[lcsC[i]]).handFlag = 3;
                    }
                }
            }
        }

        for (int i = 0; i < newNodeNum; i++) {// 处理content_update类型的结点

            if (newNodeList.get(i).handFlag == 4) {
                String result = "";
                String newStr = newNodeList.get(i).text.replaceAll("&nbsp;", "").trim();
                String oldStr = newNodeList.get(i).contentUpdateString.replaceAll("&nbsp;", "").trim();
                if (newStr == oldStr)
                    result = newStr; // //////////////公共部分如果是空格则不视为变化。
                else {
                    String lcsStr = LCS.GetLCSString(newStr, oldStr);
                    result = updataString(newStr, oldStr, lcsStr);
                }
                newNodeList.get(i).text = result;
            }
        }
    }

    private boolean isnotchar160(String str) {
        for (int i = 0; i < str.length(); i++) {
            if (str.charAt(i) != 160)
                return true;
        }
        return false;
    }

    private String updataString(String newStr, String oldStr, String lcsStr) {// 处理content_update类型的结点，输入为新、旧串和lcs公共串，返回为加入标签的新串。

        String outStr = "";
        int beg_lcs = 0, beg_new = 0, beg_old = 0;
        int i = 0;
        while (true) {
            if (beg_lcs >= lcsStr.length()) {
                if (beg_new < newStr.length()) {
                    if (isnotchar160(newStr.substring(beg_new, newStr.length()))) {
                        outStr = outStr + "<span class=\"update_add\">" + newStr.substring(beg_new, newStr.length())
                                + "</span>";
                    }
                }
                if (beg_old < oldStr.length()) {
                    if (isnotchar160(oldStr.substring(beg_old, oldStr.length()))) {
                        outStr = outStr + "<span class=\"update_del\">" + oldStr.substring(beg_old, oldStr.length())
                                + "</span>";
                    }
                }
                break;
            }
            for (i = beg_new; i < newStr.length(); i++) {
                if (lcsStr.charAt(beg_lcs) == newStr.charAt(i)) {
                    break;
                }
            }
            if (i > beg_new) {
                if (isnotchar160(newStr.substring(beg_new, i))) {
                    outStr = outStr + "<span class=\"update_add\">" + newStr.substring(beg_new, i) + "</span>";
                }
            }
            beg_new = i;

            for (i = beg_old; i < oldStr.length(); i++) {
                if (lcsStr.charAt(beg_lcs) == oldStr.charAt(i)) {
                    break;
                }
            }
            if (i > beg_old) {
                if (isnotchar160(oldStr.substring(beg_old, i))) {
                    outStr = outStr + "<span class=\"update_del\">" + oldStr.substring(beg_old, i) + "</span>";
                }
            }
            beg_old = i;

            for (i = beg_lcs; i < lcsStr.length(); i++) {
                if (i - beg_lcs + beg_new < newStr.length()) {
                    if (lcsStr.charAt(i) != newStr.charAt(i - beg_lcs + beg_new)) {
                        break;
                    }
                }
                if (i - beg_lcs + beg_old < oldStr.length()) {
                    if (lcsStr.charAt(i) != oldStr.charAt(i - beg_lcs + beg_old)) {
                        break;
                    }
                }
            }
            if (i > beg_lcs) {
                outStr = outStr + newStr.substring(beg_new, i - beg_lcs + beg_new);
            }
            beg_new = i - beg_lcs + beg_new;
            beg_old = i - beg_lcs + beg_old;
            beg_lcs = i;

        }
        return outStr;

    }

    public void addDelNode(DmlNode root) {
        // for (DmlNode p : root.son) {
        // addDelNodeDG(p);
        // }
        ArrayList<DmlNode> tmpList = new ArrayList<DmlNode>();
        for (DmlNode p : root.son) {
            tmpList.add(p);
        }
        for (DmlNode p : tmpList) {
            addDelNodeDG(p);
        }
    }

    public void addDelNodeDG(DmlNode pNode) {
        if (pNode.matchNode == null) {
            DmlNode p = pNode.parent.matchNode;
            int jieA = 0, jieB = 0;
            ArrayList<DmlNode> tmpNodeList = new ArrayList<DmlNode>();
            int i;
            for (i = 0; i < p.son.size(); i++) {
                jieA = jieB;
                if (p.son.get(i).matchNode == null) {
                    continue;
                }
                jieB = p.son.get(i).matchNode.id;
                if (jieA < pNode.id && jieB > pNode.id) {

                    for (int j = 0; j < i; j++) {
                        tmpNodeList.add(p.son.get(j));
                    }
                    tmpNodeList.add(pNode);
                    for (int j = i; j < p.son.size(); j++) {
                        tmpNodeList.add(p.son.get(j));
                    }
                    break;
                }
            }
            if (i < p.son.size()) {
                p.son = tmpNodeList;
            } else {
                p.son.add(pNode);
            }
            return;
        }
        // for (DmlNode p : pNode.son) {
        // addDelNodeDG(p);
        // }
        ArrayList<DmlNode> tmpList = new ArrayList<DmlNode>();
        for (DmlNode p : pNode.son) {
            tmpList.add(p);
        }
        for (DmlNode p : tmpList) {
            addDelNodeDG(p);
        }

    }

    public void addMoveNode(DmlNode root) {
        if (root == null)
            return;
        if (root.son.size() == 0)
            return;

        ArrayList<DmlNode> tmpList = new ArrayList<DmlNode>();
        for (DmlNode p : root.son) {
            tmpList.add(p);
        }
        for (DmlNode p : tmpList) {
            addMoveNodeDG(p);
        }

    }

    public void addMoveNodeDG(DmlNode pNode) {
        if (pNode.handFlag == 1 && pNode.allSonTextVis) {
            DmlNode p = pNode.matchNode.parent.matchNode;
            int jieA = 0, jieB = 0;
            ArrayList<DmlNode> tmpNodeList = new ArrayList<DmlNode>();
            int i;
            for (i = 0; i < p.son.size(); i++) {
                if (p.son.get(i).handFlag != 0) {
                    continue;
                }
                jieA = jieB;
                jieB = p.son.get(i).matchNode.id;
                if (jieA < pNode.matchNode.id && jieB > pNode.matchNode.id) {

                    for (int j = 0; j < i; j++) {
                        tmpNodeList.add(p.son.get(j));
                    }
                    // System.out.println(p.son.get(i-1).text);
                    // System.out.println(p.matchNode.text);

                    tmpNodeList.add(pNode.matchNode);
                    for (int j = i; j < p.son.size(); j++) {
                        tmpNodeList.add(p.son.get(j));
                    }
                    break;
                }
            }
            if (i < p.son.size()) {
                p.son = tmpNodeList;
            } else {
                p.son.add(pNode.matchNode);
            }
            pNode.matchNode.handFlag = 6;
            pNode.moveId = moveNum;
            pNode.matchNode.moveId = moveNum++;

            if (pNode.isTr) {
                pNode.matchNode.attribute.add(" moveIndex=" + moveNum + " class=movefrom style=display:none");
                pNode.attribute.add(" moveIndex=" + moveNum + " class=moveto");
            } else {
                pNode.movPre = "<div moveIndex=" + moveNum + " class=moveto>";
                pNode.movLast = "</div>";
                pNode.matchNode.movPre = "<div moveIndex=" + moveNum + " class=movefrom style=display:none>";
                pNode.matchNode.movLast = "</div>";
            }
            return;
        }
        try {
            ArrayList<DmlNode> tmpList = new ArrayList<DmlNode>();
            for (DmlNode p : pNode.son) {
                tmpList.add(p);
            }
            for (DmlNode p : tmpList) {
                addMoveNodeDG(p);
            }
        } catch (Exception e) {
        }
    }

    public void outtmp(DmlNode root) {
        root.handFlag = 0;
        for (DmlNode p : root.son) {
            outtmpDG(p);
        }

    }

    public void outtmpDG(DmlNode pNode) {

    }

    public void fixMoveLabel(DmlNode newRoot) {
        for (DmlNode p : newRoot.son) {
            fixMoveLabelDG(p);
        }
    }

    public int fixMoveLabelDG(DmlNode pNode) {
        if (pNode.handFlag != 1) {
            if (pNode.son.size() == 0) {
                return pNode.handFlag;
            }
            boolean flag = true;
            for (DmlNode p : pNode.son) {
                if (1 != fixMoveLabelDG(p)) {
                    flag = false;
                }
            }
            if (flag) {
                pNode.handFlag = 1;
            }
        }
        return pNode.handFlag;
    }

    public void fixCompare(DmlNode newRoot, DmlNode oldRoot)// 将delete和move的结点加入新树中。
    {// 匹配的结点的所有对应祖先结点都认为是匹配的。
        newRoot.handFlag = 0;
        oldRoot.handFlag = 0;
        newRoot.match = 0;
        newRoot.matchNode = oldRoot;
        oldRoot.match = 0;
        oldRoot.matchNode = newRoot;
        for (DmlNode p : newRoot.son) {
            fixCompareDG(p);
        }

        for (DmlNode p : newRoot.son) {
            addNewHandLabel(p);
        }
        for (DmlNode p : oldRoot.son) {
            addOldHandLabel(p);
        }

        addDelNode(oldRoot);
        fixMoveLabel(newRoot);// /////////////修改move label 叶子全为1，则结点为1.
        addMoveNode(newRoot);// ////////////////////////////添加移动move结点，注意考虑是否和addDelNode矛盾。
        outtmp(oldRoot);// ////////////////////////////////
        outtmp(newRoot);// ////////////////////////////////

    }

    private int fixCompareDG(DmlNode pNode) {// fixcompare的递归调用。
        if (pNode.handFlag != 2) {
            pNode.parent.match = pNode.matchNode.parent.id;
            pNode.parent.matchNode = pNode.matchNode.parent;

            pNode.matchNode.matchNode = pNode.matchNode;
            pNode.matchNode.match = pNode.id;
            pNode.matchNode.parent.matchNode = pNode.parent;
            pNode.matchNode.parent.match = pNode.parent.id;
            return 0;
        } else {
            int tmp;
            for (DmlNode p : pNode.son) {
                tmp = fixCompareDG(p);
                if (tmp != 2) {
                    if (pNode.parent == null || pNode.matchNode.parent == null)
                        continue;
                    pNode.handFlag = 0;
                    pNode.parent.match = pNode.matchNode.parent.id;
                    pNode.parent.matchNode = pNode.matchNode.parent;

                    pNode.matchNode.parent.matchNode = pNode.parent;
                    pNode.matchNode.parent.match = pNode.parent.id;
                }
            }
            return pNode.handFlag;
        }
    }

    private void addNewHandLabel(DmlNode pNode) {
        if (pNode.son.size() == 0) {
            if (pNode.handFlag == 1) {
                pNode.handLabel = "move";
            }
            if (pNode.handFlag == 2) {
                pNode.handLabel = "add";
            }
            if (pNode.handFlag == 3) {
                pNode.handLabel = "label_update";
            }
            if (pNode.handFlag == 4) {
                pNode.handLabel = "content_update";
            }
            return;
        }
        if (pNode.isPair) {
            for (DmlNode p : pNode.son) {
                addNewHandLabel(p);
            }
        }
    }

    private void addOldHandLabel(DmlNode pNode) {
        if (pNode.son.size() == 0) {
            if (pNode.handFlag == 1) {
                pNode.handLabel = "move";
            }
            if (pNode.handFlag == 2) {
                pNode.handLabel = "delete";
            }
            if (pNode.handFlag == 3) {
                pNode.handLabel = "label_update";
            }
            if (pNode.handFlag == 4) {
                pNode.handLabel = "content_update";
            }
            return;
        }
        if (pNode.isPair) {
            for (DmlNode p : pNode.son) {
                addOldHandLabel(p);
            }
        }
    }
}
