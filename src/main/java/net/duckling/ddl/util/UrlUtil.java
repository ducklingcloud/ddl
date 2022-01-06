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

import java.util.regex.Matcher;
import java.util.regex.Pattern;

import javax.servlet.http.HttpServletRequest;
/**
 * URL工具类
 * @author Clive
 *
 */
public final class UrlUtil {

    private static final int HTTPS_DEFAULT_PORT = 443;
    private static final int HTTP_DEFAULT_PORT = 80;
    /**敏感词汇，
     * %xx% 代表，只要包含xx就不行
     * %xx 代表，xx结尾就不行
     * xx% 代表，xx开始就不行
     * xx，代表，是xx就不行
     * */
    private static final String SENSITIVE=
            "%dhome%," +
            "%domain%," +
            "%institution%," +
            "%index%," +
            "%system%," +
            "%admin%," +
            "%papers%," +
            "%paper%," +
            "%escience%," +
            "%home%," +
            "%people%," +
            "%regist%" +
            ",p,%sub,pre%";

    public static final Pattern START = Pattern.compile("^\\%[a-zA-Z0-9]*$");
    public static final Pattern END = Pattern.compile("^[a-zA-Z0-9]*\\%$");
    public static final Pattern LIKE = Pattern.compile("^\\%[a-zA-Z0-9]*\\%$");

    /**包含一些敏感词信息，如果包含这次词语，就不会让你用的
     * @param str 待判定的 字符
     * @return 能用吗？
     * */
    public static boolean canUse(String str){
        String[] splits=SENSITIVE.split(",");
        for(String split:splits){
            Matcher macher = START.matcher(split);
            //前置匹配
            if(macher.find()){
                String key=macher.group().replaceAll("%", "");
                if(str.endsWith(key)){
                    return false;
                }
                continue;

            }
            macher=END.matcher(split);
            //后置匹配
            if(macher.find()){
                String key=macher.group().replaceAll("%", "");
                if(str.startsWith(key)){
                    return false;
                }
                continue;
            }
            //全部匹配
            macher=LIKE.matcher(split);
            if(macher.find()){
                String key=macher.group().replaceAll("%", "");
                if(str.contains(key)){
                    return false;
                }
                continue;
            }
            //精确匹配
            if(split.equals(str)){
                return false;
            }
        }
        return true;
    }

    private UrlUtil() {
    }
    /**
     * 构造根URL，例如：http://localhost:8080/dhome
     * @param request
     * @return
     */
    public static String getRootURL(HttpServletRequest request) {
        return getDomain(request) + request.getContextPath();
    }
    /***
     * 只获取网址，协议，之类的不要
     * @param  if the url is http://www.lvlongyun.com/people/index.html
     * @return www.lvlongyun.com
     *
     * */
    public static String getAddress(HttpServletRequest request){
        return getRootURL(request).replace(request.getScheme(), "").replace("://", "").replace(request.getContextPath(), "");
    }

    /**
     * 构造Domain，例如：http://localhost:8080
     * @param request
     * @return
     */
    public static String getDomain(HttpServletRequest request) {
        String url = request.getScheme() + "://" + request.getServerName();
        int port = request.getServerPort();
        if ((port != HTTP_DEFAULT_PORT) && (port != HTTPS_DEFAULT_PORT)) {
            url = url + ":" + port;
        }
        return url;
    }
    /**
     * 根据request URI构造完整的URL
     * @param request
     * @return
     */
    public static String getURLFromRequestURI(HttpServletRequest request) {
        String domain = getDomain(request);
        String requestURI = request.getRequestURI();
        if (requestURI == null) {
            return domain;
        }
        if (requestURI.startsWith(request.getScheme())) {
            return requestURI;
        } else if (requestURI.startsWith("/")) {
            return domain + requestURI;
        } else {
            return domain + "/" + requestURI;
        }
    }
    /**
     * 返回contextPath后面的路径
     * @param request
     * @return 不包含contextPath
     */
    public static String getURI(HttpServletRequest request) {
        return request.getRequestURI().replace(request.getContextPath(), "");
    }

    public static String changeSchemeToHttps(String oldURL,int port){
        if(oldURL==null||oldURL.length()<7){
            return oldURL;
        }
        if(!oldURL.startsWith("https")){
            if(oldURL.startsWith("http:")){
                String s = oldURL.substring(7);
                int i = s.indexOf("/", 1);
                String hostPort = s;
                String end="";
                if(i>0){
                    hostPort = s.substring(0,i);
                    end = s.substring(i);
                }
                int pindex= hostPort.indexOf(":");
                if(pindex<=0){
                    hostPort=hostPort+":"+port;
                }else{
                    hostPort=hostPort.substring(0,pindex)+":"+port;
                }
                return "https://"+hostPort+end;
            }
        }
        return oldURL;
    }

    public static String changeSchemeToHttps(String oldURL,HttpServletRequest request){
        if(oldURL==null||oldURL.length()==0){
            return oldURL;
        }
        String scheme = request.getScheme();
        if("https".equals(scheme)){
            int port = request.getLocalPort();
            return changeSchemeToHttps(oldURL, port);
        }else{
            return oldURL;
        }
    }
}
