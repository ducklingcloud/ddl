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

package net.duckling.ddl.service.devent.impl;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

import net.duckling.ddl.service.comment.Comment;


/**
 * @date Mar 18, 2011
 * @author xiejj@cnic.cn
 */
public abstract class DPageDigester {
    public static final int DIGEST_LENGTH = 200;
    private static Pattern tagPattern = Pattern.compile("\\s*<\\s*([/]*)([^>]*)>\\s*");

    public static String digest(String rawContent, int length) {
        rawContent = trimHtml(rawContent);
        if (rawContent.length() < length){
            return rawContent;
        }
        return rawContent.substring(0, length);
    }

    public static String getCommentDigest(Comment comment){
        String result = "";
        if(comment.getReceiver().getId()!=0){
            result = "回复"+comment.getReceiver().getName()+":";
        }
        result += comment.getContent();
        return result;
    }

    private static String trimHtml(String html) {
        if (html != null) {
            Matcher m = tagPattern.matcher(html);
            return m.replaceAll(" ");
        } else {
            return "";
        }
    }
}
