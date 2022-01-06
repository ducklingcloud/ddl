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

import java.util.Collection;
import java.util.Random;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.commons.lang.StringUtils;
import org.json.simple.JSONObject;

/**
 * Introduction Here.
 *
 * @date 2010-2-8
 * @author euniverse
 */
public final class StringUtil {
    public final static String EMAIL_REGEX = "^\\s*\\w+(?:\\.{0,1}[\\w-]+)*@[a-zA-Z0-9]+(?:[-.][a-zA-Z0-9]+)*\\.[a-zA-Z]+\\s*$";

    private StringUtil() {
    }

    public static boolean isValidEmail(String emailaddress) {
        if (emailaddress == null) {
            return false;
        }
        Pattern pattern = Pattern.compile(EMAIL_REGEX);
        Matcher matcher = pattern.matcher(emailaddress);
        return matcher.matches();
    }

    public static String getRandomString(int length) {
        StringBuffer buffer = new StringBuffer("qwertyuiopasdfghjklzxcvbnmQWERTYUIOPASDFGHJKLZXCVBNM");
        StringBuffer sb = new StringBuffer();
        Random r = new Random();
        int range = buffer.length();
        for (int i = 0; i < length; i++) {
            sb.append(buffer.charAt(r.nextInt(range)));
        }
        return sb.toString();
    }

    /**
     * Deal with the special characters such as '<', '>', '&', '"', '\n', '\r'
     */
    public static String normalizeString(String pString) {
        StringBuffer sb = new StringBuffer();
        int len = (pString != null) ? pString.length() : 0;

        for (int i = 0; i < len; i++) {
            char c = pString.charAt(i);
            sb.append(normalizeChar(c));
        }
        return sb.toString();
    }

    public static String normalizeChar(char pChar) {
        StringBuffer sb = new StringBuffer();
        switch (pChar) {
            case '<':
                sb.append("&lt;");
                break;
            case '>':
                sb.append("&gt;");
                break;
            case '&':
                sb.append("&amp;");
                break;
            case '"':
                sb.append("&quot;");
                break;
            case '\r':
            case '\n':
            case '\\':
                sb.append("&#").append(Integer.toString(pChar)).append(";");
                break;
            default:
                sb.append(pChar);
                break;
        }
        return sb.toString();
    }

    public static String getSQLInFromInt(Collection<Integer> ints) {
        StringBuilder sb = new StringBuilder();
        sb.append("(");
        for (Integer i : ints) {
            sb.append(i).append(",");
        }
        sb.delete(sb.length() - 1, sb.length());
        sb.append(")");
        return sb.toString();
    }
    /**
     * 返回一个(?,?,?,?,?)
     * @param length
     * @return
     */
    public static String getSQLInFromStr(int length) {
        StringBuilder sb = new StringBuilder();
        sb.append("(");
        for (int i=0 ;i<length;i++) {
            sb.append("?").append(",");
        }
        sb.delete(sb.length() - 1, sb.length());
        sb.append(")");
        return sb.toString();
    }

    public static String getSQLInFromInt(int[] ints) {
        StringBuilder sb = new StringBuilder();
        sb.append("(");
        for (int i : ints) {
            sb.append(i).append(",");
        }
        sb.delete(sb.length() - 1, sb.length());
        sb.append(")");
        return sb.toString();
    }

    private static final Pattern pattern = Pattern.compile("[<>:\"|?/*/\\\\]");
    /**
     * 用于判断输入字符中出现非法字符:*<>\"|?\/\\,并直接写入json
     * @param req
     * @param resp
     * @param params
     * @return
     */
    @SuppressWarnings({ "deprecation" })
    public static boolean illCharCheck(HttpServletRequest req,HttpServletResponse resp,String ...params){
        if(params==null||params.length==0){
            return false;
        }
        for(String p : params){
            String s = req.getParameter(p);
            if(StringUtils.isNotEmpty(s)&&pattern.matcher(s).find()){
                if(resp!=null){
                    JSONObject obj = new JSONObject();
                    obj.put("success", false);
                    obj.put("result", false);
                    obj.put("param", s);
                    obj.put("message", "输入字符中不能包含：<>:\"|*?/\\");
                    JsonUtil.writeJSONObject(resp, obj);
                }
                return true;
            }
        }
        return false;
    }

    @SuppressWarnings({ "deprecation" })
    public static boolean illTitle(HttpServletResponse resp,String title){
        if(StringUtils.isNotEmpty(title)&&pattern.matcher(title).find()){
            if(resp!=null){
                JSONObject obj = new JSONObject();
                obj.put("success", false);
                obj.put("result", false);
                obj.put("error", "输入字符中不能包含：<>:\"|*?/\\");
                JsonUtil.writeJSONObject(resp, obj);
            }
            return true;
        }
        return false;
    }

    /**
     * 返回一个字符串值，如果为null返回"";
     * @param value
     * @return
     */
    public static String getValue(String value) {
        return getValue(value, "");
    }

    /**
     * 返回一个字符串值，如果为null返回defaultValue;
     * @param value
     * @return
     */
    public static String getValue(String value, String defaultValue) {
        if(value == null){
            return defaultValue;
        }
        return value;
    }
}
