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
package net.duckling.ddl.service.pan;

import java.io.UnsupportedEncodingException;

import javax.mail.internet.MimeUtility;
import javax.servlet.http.HttpServletRequest;

import net.duckling.meepo.api.MyURLEncoder;
import nl.bitwalker.useragentutils.Browser;
import nl.bitwalker.useragentutils.UserAgent;

public class ResponseHeaderUtils {

    public static String buildResponseHeader(HttpServletRequest request, String filename, boolean isAttach)
            throws UnsupportedEncodingException {
        String style = "";
        if (isAttach) {
            style = "attachment";
        } else {
            style = "inline";
        }
        UserAgent agent = UserAgent.parseUserAgentString(request.getHeader("User-Agent"));
        Browser group = agent.getBrowser().getGroup();
        Browser value = Browser.values()[group.ordinal()];
        String encodedFilename = MyURLEncoder.encodeURIComponent(filename);
        String headerValue = null;
        switch (value) {
        case IE:
            headerValue = style + ";filename=\"" + encodedFilename + "\"";
            break;
        case FIREFOX:
            headerValue = style + ";filename*=UTF-8''" + encodedFilename;
            break;
        case CHROME:
            encodedFilename = MimeUtility.encodeText(filename, "UTF8", "B");
            headerValue = style + ";filename=\"" + encodedFilename + "\"";
            break;
        case SAFARI:
            headerValue = style + ";filename=\"" + new String(filename.getBytes("UTF-8"), "ISO8859-1") + "\"";
            break;
        case OPERA:
            headerValue = style + ";filename*=UTF-8''" + encodedFilename;
            break;
        default:
            headerValue = style + ";filename=\"" + encodedFilename + "\"";
            break;
        }
        return headerValue;
    }
}
