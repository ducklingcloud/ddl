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
 * by liuboyun 树的结点的结构
 ****************/

public class DmlNode {

    public String label = "";// 单个标签，如table
    public ArrayList<String> attribute = new ArrayList<String>();// 属性列表
    public String text = "";// 文本内容。
    public String allText = "";// 结点的首尾标签之间的所有内容
    public DmlNode parent = null;// 父节点
    public ArrayList<DmlNode> son = new ArrayList<DmlNode>();// 儿子结点们

    public boolean isLeaf;// 是否为叶子结点
    public boolean isPair;// 标签是否成对，如<tr></tr>.
    public int id;// 先根遍历的序号，root为0。
    public String parentList = "";// 到根节点的路径
    public int match = -1;// 与另一棵树匹配的结点的id，-1为不匹配。
    public DmlNode matchNode;
    public int handFlag = 2;// 0:match 1:move 2:add 3:label update 4:content
    // update 5:delete
    public String handLabel = "";
    public ArrayList<DmlNode> delPre = new ArrayList<DmlNode>();// 结点的兄弟中被删除的，比该结点序号小的结点
    public ArrayList<DmlNode> delLast = new ArrayList<DmlNode>();// 结点的兄弟中被删除的，比该结点序号大的结点
    public String contentUpdateString = "";// 当改动为4:content update时，结点的原文本
    public boolean isTr = false; // 假设tr下层就是td
    // public String tdStr = "";
    public int moveId;
    public String movPre = "";
    public String movLast = "";
    public ArrayList<DmlNode> TRNodeList = new ArrayList<DmlNode>();
    public String trStr = "";
    public boolean allSonTextVis = false;

}
