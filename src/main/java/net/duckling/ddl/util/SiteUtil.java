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

import java.net.MalformedURLException;
import java.net.URL;
import java.util.HashSet;
import java.util.Set;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import javax.servlet.http.HttpServletRequest;

import org.apache.log4j.Logger;


/**
 * @date 2012-2-2
 * @author Clive Lee
 * @last_edit_time 2012-12-3 下午3:33:02
 * @revised 将原有VWBFilter中解析TeamCode的方法移动到此类中 (clive)
 */
public abstract class SiteUtil {
    private static final String[] URL_KEY_WORDS_BLACK_LIST = { "system", "jsp", "scripts", "dashboard", "home",
        "images", "mobile", "api", "oauth", "dataCollect", "skins", "error", "uafServices", "ddl", "uafLogin", "help",
        "logout", "diff", "dct", "direct", "copyfile","login","layout","pan","wopi","t","f","ff","activity","user","r","v1"};
    private static final String[] URL_FILTER_TYPE_SET = { ".jsp", ".ico", ".txt", ".css", ".js", ".jpg", ".png",
        ".svg", ".pdf", ".htm", ".zip", ".swf", ".ico", ".html", ".xml",".xls" };

    static class SetHolder{
        private static Set<String> blackSet;
        private static Set<String> typeSet;
        static{
            typeSet = new HashSet<String>();
            for (String typeItem : URL_FILTER_TYPE_SET) {
                typeSet.add(typeItem);
            }
            blackSet = new HashSet<String>();
            for (String blackItem : URL_KEY_WORDS_BLACK_LIST) {
                blackSet.add(blackItem);
            }
        }
    }

    private static final Logger LOGGER = Logger.getLogger(SiteUtil.class);

    public static boolean isValidateTeamCode(String source) {
        if (source == null || source.isEmpty() || source.trim().length() == 0) {
            return false;
        }
        Pattern pattern = Pattern.compile("[a-z0-9-]+");
        Matcher matcher = pattern.matcher(source);
        if(!matcher.matches()){
            return false;
        }
        for (String type : SetHolder.typeSet) {
            if (source.toLowerCase().endsWith(type)) {
                return false;
            }
        }
        return !SetHolder.blackSet.contains(source);

    }

    public static String parseTeamCode(HttpServletRequest request) {
        String teamName = null;
        URL urlObj;
        try {
            urlObj = new URL(request.getRequestURL().toString());
            String path = urlObj.getPath();
            String contextPath = request.getContextPath();
            if (contextPath != null && contextPath.length() > 0) {
                path = path.substring(contextPath.length());
                if ("/".equals(path)) {
                    path = "";
                }
            }
            if (path != null && path.length() > 0) {
                if (path.startsWith("/")) {
                    path = path.substring(1);
                }
                String[] parts = path.split("/");
                if (parts != null && parts.length > 0) {
                    if (isValidateTeamCode(parts[0])) {
                        teamName = parts[0];
                    }
                } else {
                }
            }
        } catch (MalformedURLException e) {
            LOGGER.error(e.getMessage(), e);
        }
        return teamName;
    }
}
