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
package net.duckling.ddl.web.bean;

import java.util.HashSet;
import java.util.Set;

import net.duckling.ddl.constant.LynxConstants;

/**
 * 文件类型
 * @author Brett
 *
 */
public class FileType {
    public final static String IMAGE = "image";
    public final static String PDF = "pdf";
    public final static String OFFICE = "office";
    public final static String TXT = "txt";
    public final static String FILE = "file";  //不支持预览的文件

    public final static int PDF_MAX_SIZE = 52428800;  //pdf文件预览时大小不能超过50M
    public final static int OFFICE_MAX_SIZE = 52428800;  //office文件预览时大小不能超过50M
    public final static int TXT_MAX_SIZE = LynxConstants.MAXFILESIZE_CODEREVIEW;  //txt文件预览时大小不能超过1M

    public FileType(String name, String [] extArr){
        this.name = name;
        this.exts = new HashSet<String>();
        for(String ext : extArr){
            exts.add(ext);
        }
    }

    public String getName() {
        return name;
    }

    public Set<String> getExts() {
        return exts;
    }

    public boolean isSupported(String ext){
        if(null==ext || "".equals(ext))
            return false;
        return exts.contains(ext);
    }

    private String name;
    private  Set<String> exts = null;
}
