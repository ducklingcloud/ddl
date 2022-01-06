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
package net.duckling.ddl.web.api;

public class APICommonUtil {

    public static String jsonStringReplace(String replaceString) {
        if(replaceString == null || replaceString.length() < 1) {
            return "";
        } else {
            // 转化字符串
            String coverTemp = replaceString.replace(" ", "");
            coverTemp = coverTemp.replace("\n", "");
            coverTemp = coverTemp.replace("(", "");
            coverTemp = coverTemp.replace(")", "");
            coverTemp = coverTemp.replace("\"", "");
            return coverTemp;
        }
    }

    public static String[] jsonArrayStringReplace(String[] replaceArray) {
        if(replaceArray == null || replaceArray.length < 1) {
            //          return new String[]{""};
            return null;
        } else {
            // 转化数组
            int length = replaceArray.length;
            String[] result = new String[length];
            for(int i=0; i<length; i++) {
                result[i] = jsonStringReplace(replaceArray[i]);
            }
            return result;
        }
    }

}
