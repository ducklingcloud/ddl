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

import java.io.BufferedReader;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.UnsupportedEncodingException;


/**
 * Introduction Here.
 * @date Feb 26, 2010
 * @author key
 */
public final class Constant {
	private Constant(){}
	/**
     * The maximum size of attachments that can be uploaded.
     */
	public static final String CLB_FILE_MAXSIZE = "clb.attachment.maxsize";
	
	/**
     * A space-separated list of attachment types which can be uploaded
     */
    public static final String CLB_ALLOWEDEXTENSIONS = "clb.attachment.allowed";
    
    /**
     * A space-separated list of attachment types which cannot be uploaded
     */
    public static final String CLB_FORDBIDDENEXTENSIONS = "clb.attachment.forbidden";
    
    /**
     * Property name for where the CLB work directory should be. If not
     * specified, reverts to ${java.tmpdir}.
     */
    public static final String CLB_WORKDIR = "clb.workDir";
    
    /**
     * Property name of the local user for where the CLB access the files. 
     */
    
    public static String version;
    
    public static final String DUCKLING_NAME = "Duckling";

    public static final String DUCKLING_VER = "3.0";

    public static final String DUCKLING_WEB = "http://duckling.escience.cn/";
    
    public static final String DUCKLING_DATEFORMAT = "duckling.default.dateformat";
    
    public static String getVersion(String basePath){
        
            if (version == null){
                readVersionFromFile(basePath);
            }
            return version;
    }
    
    public  static synchronized void readVersionFromFile(String basePath){
        if (version == null){
            String filename = basePath + "/WEB-INF/ddl.version";
            
            try {
				BufferedReader br = new BufferedReader(new InputStreamReader(new FileInputStream(filename),"utf-8"));
				String tmp = br.readLine();
				version = tmp.trim();
				br.close();
			} catch (UnsupportedEncodingException e) {
			} catch (FileNotFoundException e) {
			} catch (IOException e) {
			}
//            ProductInfo pi = VersionUtil.fromFile(filename);
//            version = pi.getProduct() + " " + pi.getVersion();
//            version = version.toUpperCase();
        }
    }
}
