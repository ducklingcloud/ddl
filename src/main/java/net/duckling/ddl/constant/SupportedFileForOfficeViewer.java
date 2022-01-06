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
package net.duckling.ddl.constant;

import java.util.HashSet;
import java.util.Set;

public class SupportedFileForOfficeViewer {
    private static Set<String> supported=new HashSet<String>();
    static{
        //PDF
        supported.add("pdf");

        //Word
        supported.add("doc");
        supported.add("docx");
        supported.add("docm");
        supported.add("dot");
        supported.add("dotm");
        supported.add("dotx");
        supported.add("odt");

        //Excel
        supported.add("xls");
        supported.add("xlsx");
        supported.add("xlsb");
        supported.add("xlsm");
        supported.add("ods");

        //PowerPoint
        supported.add("ppt");
        supported.add("pptx");
        supported.add("odp");
        supported.add("pot");
        supported.add("potm");
        supported.add("potx");
        supported.add("pps");
        supported.add("ppsm");
        supported.add("ppsx");
        supported.add("pptm");

        //OneNote
        //supported.add("one");
        //supported.add("onetoc2");
        //supported.add("onepkg");

    }

    public static boolean isSupported(String fileformat){
        if(null==fileformat || "".equals(fileformat))
            return false;
        return supported.contains(fileformat);
    }
    public static boolean isSupportedFile(String fileName){
        if(fileName==null||fileName.length()==0){
            return false;
        }
        int index = fileName.lastIndexOf(".");
        if(index==-1||index==fileName.length()){
            return false;
        }
        return isSupported(fileName.substring(index+1).toLowerCase());
    }

    public static boolean isOfficeFile(String fileName){
        if(fileName==null||fileName.length()==0){
            return false;
        }
        int index = fileName.lastIndexOf(".");
        if(index==-1||index==fileName.length()){
            return false;
        }
        String s = fileName.substring(index+1).toLowerCase();
        if("pdf".equals(s)){
            return false;
        }
        return isSupported(s);
    }
}
