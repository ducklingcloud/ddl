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
package net.duckling.ddl.util;

import java.util.HashMap;
import java.util.Map;

public final class MimeType {
    private MimeType(){}
    public static String getContentType(String suffix){
        String newPost=suffix.toLowerCase();
        String type=mappings.get(newPost);
        if (type==null){
            type="application/octet-stream";
        }

        return type;
    }

    public static String getSuffix(String fname){
        String suffix="";
        int dotPos=fname.lastIndexOf('.');
        if (dotPos!=-1){
            suffix= fname.substring(dotPos+1);
        }
        return suffix;
    }
    public static boolean isImage(String filename){
        String suffix = getSuffix(filename);
        String contentType=getContentType(suffix);
        return contentType.startsWith("image");
    }
    public static boolean isFlash(String filename){
        String suffix = getSuffix(filename);
        String contentType=getContentType(suffix);
        return contentType.endsWith("x-shockwave-flash");
    }
    private static Map<String,String> mappings;
    static{
        mappings=new HashMap<String,String>();
        mappings.put("html", "text/html");
        mappings.put("txt", "text/plain");
        mappings.put("xml", "text/xml");
        //Office
        mappings.put("doc", "application/msword");
        mappings.put("dot", "application/msword");
        mappings.put("ppt", "application/powerpoint");
        mappings.put("xls", "application/excel");
        mappings.put("docx", "application/vnd.openxmlformats-officedocument.wordprocessingml.document");
        mappings.put("dotx", "application/vnd.openxmlformats-officedocument.wordprocessingml.template");
        mappings.put("pptx", "application/vnd.openxmlformats-officedocument.presentationml.presentation");
        mappings.put("xlsx", "application/vnd.openxmlformats-officedocument.spreadsheetml.sheet");



        //Acrobat
        mappings.put("pdf", "application/pdf");
        mappings.put("ps", "application/postscript");

        //Image
        mappings.put("bmp", "image/bmp");
        mappings.put("jpg", "image/jpeg");
        mappings.put("jpeg", "image/jpeg");
        mappings.put("gif", "image/gif");
        mappings.put("png", "image/png");

        //Flash
        mappings.put("swf", "application/x-shockwave-flash");

        //Video & Audio
        mappings.put("avi", "video/x-msvideo");
        mappings.put("mpg", "video/mpeg");
        mappings.put("mpeg", "video/mpeg");
        mappings.put("mp3", "audio/x-mpeg");
        mappings.put("rm", "application/vnd.rn-realmedia");
        mappings.put("rmvb", "application/vnd.rn-realmedia");

        //Compress
        mappings.put("gz", "application/x-gzip");
        mappings.put("tar", "application/x-tar");
        mappings.put("zip", "application/zip");
        mappings.put("z", "application/x-compress");

        //Executable
        mappings.put("exe", "application/octet-stream");
        mappings.put("com", "application/octet-stream");
    }
}
