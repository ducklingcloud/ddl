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

import net.duckling.ddl.service.diff.impl.DiffProvider;

import org.springframework.stereotype.Repository;


/**
 * @date Mar 21, 2011
 * @author xiejj@cnic.cn
 */

@Repository
public class DmlDiffProvider implements DiffProvider {
    public String getProviderInfo() {
        return "DMLDiffProvider";
    }

    public String myDiff(String newXMLstring, String oldXMLstring) {
        // 如果文件没有任何改变，直接返回
        if (oldXMLstring.equals(newXMLstring)) {
            return newXMLstring;
        }

        DmlTree oldTree = new DmlTree();
        oldTree.Build(oldXMLstring);
        oldTree.OutputXML();// 建树后，从树形结构遍历输出，看是否与oldXML相同。

        DmlTree newTree = new DmlTree();
        newTree.Build(newXMLstring);

        DmlDiff compareTree = new DmlDiff();
        compareTree.compareTree(oldTree.leafList, newTree.leafList);
        compareTree.fixCompare(newTree.root, oldTree.root);
        String output = newTree.OutputXML();

        return output;
    }

    public String makeDiffHtml(String oldText, String newText) throws Exception {
        return myDiff(oldText, newText);
    }
}
