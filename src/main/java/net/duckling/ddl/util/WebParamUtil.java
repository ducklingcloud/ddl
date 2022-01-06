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

import javax.servlet.http.HttpServletRequest;

import org.apache.log4j.Logger;

public final class WebParamUtil {
    private WebParamUtil(){}
    private static final Logger LOG = Logger.getLogger(WebParamUtil.class);

    public static Integer getIntegerValue(HttpServletRequest request,String key){
        try{
            return Integer.parseInt(request.getParameter(key));
        }catch(NumberFormatException e){
            LOG.error(e);
        }
        return null;
    }

    public static int[] getIntegerValues(HttpServletRequest request,String key) {
        String[] strarray = request.getParameterValues(key);
        if(strarray==null){
            return null;
        }
        int[] intarray = new int[strarray.length];
        try{
            for(int i=0;i<strarray.length;i++){
                intarray[i] = Integer.parseInt(strarray[i]);
            }
            return intarray;
        }catch(NumberFormatException e){
            LOG.error(e);
        }
        return null;
    }

}
