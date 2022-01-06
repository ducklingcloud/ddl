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

import java.io.UnsupportedEncodingException;

import javax.mail.internet.MimeUtility;

import org.apache.log4j.Logger;

/**
 * 浏览器识别代码
 *
 * @date 2011-8-18
 * @author xiejj@cnic.cn
 */
public final class Browser {

    private final static String UTF8 = "UTF-8";
    protected static final Logger LOG = Logger.getLogger(Browser.class);

    private Browser(){}
    public static enum BrowserType {
        Chrome, Firefox, MsIE, Safari, Unknown
    };

    public static String encodeFileName(String agent, String filename) {
        BrowserType type = recognizeBrowser(agent);
        String result = "attachment;filename=\"";
        try {
            switch (type) {
                case MsIE:
                    result += encodeWithUTF8(filename);
                    break;
                case Chrome:
                    result += encodeWithISO(filename);
                    break;
                case Firefox:
                    result += encodeWithBase64(filename);
                    break;
                case Safari:
                    result += encodeWithISO(filename);
                    break;
                default:
                    result += filename;
                    break;
            }
        } catch (UnsupportedEncodingException e) {
            LOG.warn("endcode fileName error.{fileName:"+ filename +"} " + e.getMessage());
        }
        result += "\"";
        return result;
    }

    private static String encodeWithBase64(String filename)
            throws UnsupportedEncodingException {
        return MimeUtility.encodeText(filename, "UTF-8", "B");
    }
    private static String encodeWithUTF8(String filename) throws UnsupportedEncodingException{
        return java.net.URLEncoder.encode(filename, UTF8);
    }
    private static String encodeWithISO(String filename) throws UnsupportedEncodingException{
        return new String(filename.getBytes("UTF-8"),"ISO8859-1");
    }

    public static BrowserType recognizeBrowser(String agent) {
        BrowserType type = BrowserType.Unknown;
        if (agent != null) {
            agent = agent.toLowerCase();
            if (-1 != agent.indexOf("msie")) {
                type = BrowserType.MsIE;
            } else if (-1 != agent.indexOf("chrome")) {
                type = BrowserType.Chrome;
            } else if (-1 != agent.indexOf("safari")) {
                type = BrowserType.Safari;
            } else if (-1 != agent.indexOf("firefox")) {
                type = BrowserType.Firefox;
            } else if(-1!=agent.indexOf("rv:11.0")){
                type = BrowserType.MsIE;
            }
        }
        return type;
    }

}
