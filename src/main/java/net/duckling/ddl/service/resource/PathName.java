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
package net.duckling.ddl.service.resource;

import java.util.ArrayList;
import java.util.List;

/**
 * 路径和文件名
 * 如路径/hello/world/abc.doc  path=/hello/world/abc.doc，name=abc.doc，contextPath=/hello/world, names={"hello","world","abc.doc"}
 * @author Brett
 *
 */
public class PathName {
    public static final String DELIMITER = "/";
    private String path; //全路径
    private String name; //文件名
    private String contextPath=""; //上下文路径
    private List<String> names = new ArrayList<String>(); //路径列表
    private int length;
    public PathName(){
    }

    public PathName(String path){
        path = path.trim().substring(1);
        String [] arr = path.split(DELIMITER);
        for(int i=0;i<arr.length;i++){
            String item = arr[i];
            if("".equals(item.trim())){
                continue;
            }
            this.names.add(item);
            if(i<arr.length-1){
                this.contextPath += DELIMITER + item;
            }
        }
        this.length = names.size();
        if(this.length==0){
            this.contextPath = DELIMITER;
            this.path = DELIMITER;
            this.name = "";
        }else{
            this.name = this.names.get(names.size()-1);
            this.path = this.contextPath + DELIMITER + this.name;
            if("".equals(this.contextPath)){
                this.contextPath = DELIMITER;
            }
        }
    }

    public static String appendDelimiter(String path){
        if(DELIMITER.equals(path)){
            return DELIMITER;
        }
        return path + DELIMITER;
    }

    public String getPath() {
        return path;
    }
    public void setPath(String path) {
        this.path = path;
    }
    public String getName() {
        return name;
    }
    public void setName(String name) {
        this.name = name;
    }
    public String getContextPath() {
        return contextPath;
    }
    public void setContextPath(String contextPath) {
        this.contextPath = contextPath;
    }
    public List<String> getNames() {
        return names;
    }
    public void setNames(List<String> names) {
        this.names = names;
    }
    public int getLength() {
        return length;
    }
    public void setLength(int length) {
        this.length = length;
    }

    public String toString(){
        return path;
    }
}
