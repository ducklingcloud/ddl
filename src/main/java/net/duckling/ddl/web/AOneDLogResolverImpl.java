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
package net.duckling.ddl.web;

import java.util.HashMap;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;

import org.springframework.util.StringUtils;

import net.duckling.ddl.common.VWBContext;
import net.duckling.ddl.common.VWBSession;

import cn.cnic.cerc.dlog.client.WebLogResolver;

public class AOneDLogResolverImpl extends WebLogResolver {
    @Override
    public String getUserID(HttpServletRequest request) {
    	String uid = VWBSession.getCurrentUid(request);
        if (StringUtils.isEmpty(uid)) {
        	uid="guest";
        }
        return uid;
    }

    @Override
    public String getReferer(HttpServletRequest request) {
        String domain = request.getScheme() + "://" + request.getServerName();
        int port = request.getServerPort();
        int httpPort = 80;
        if (port != httpPort) {
            domain = domain + ":" + port;
        }
        domain = domain + request.getContextPath();
        String fullReferer = request.getHeader("Referer");
        if (fullReferer == null) {
            return fullReferer;
        }
        return fullReferer.replace(domain, "");
    }

    public int getTid(HttpServletRequest request) {

        if (request.getAttribute("cftid") != null) {
            int cftid = (int) request.getAttribute("cftid");
            return cftid;
        }else {
            return VWBContext.getCurrentTid();
        }
    }

    @Override
    public Map<String, String> buildFixedParameters(HttpServletRequest request) {
        Map<String, String> map = new HashMap<String, String>();
        map.put("uid", getUserID(request));
        map.put("client", getIpAddress(request));
        map.put("referer", getReferer(request));
        map.put("tid", getTid(request) + ""); // extends
        mention(map, request);
        return map;
    }
    
    private void mention(Map<String, String> map,HttpServletRequest request){
    	if(!StringUtils.isEmpty(request.getParameter("mention"))){
    		map.put("mention", "mention");
    	}
    }

}
