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
/**
 *
 */
package net.duckling.ddl.service.mail;

import java.util.Calendar;
import java.util.Map;

import org.apache.log4j.Logger;

import net.duckling.ddl.service.mail.compile.RenderUtils;
import net.duckling.ddl.service.mail.impl.SimpleEmail;
import net.duckling.ddl.service.mail.thread.EmailSendThread;
import net.duckling.ddl.service.resource.Resource;
import net.duckling.ddl.service.user.SimpleUser;


/**
 * @author lvly
 * @since 2012-11-6
 */
public class NoticeMailHelper {
    private static final Logger LOG = Logger.getLogger(NoticeMailHelper.class);

    public static void sendRecommandMail(String[] address,Map<String,Object> map) {
        String content= RenderUtils.render(map, RenderUtils.SHARE_NOTICE_TEMP);
        String shareFrom = ((SimpleUser)map.get("shareFrom")).getName();
        String title=shareFrom+"把"+((Resource)map.get("resource")).getTitle()+"分享给了您";
        EmailSendThread.addEmail(new SimpleEmail(address,title,content,(String)map.get("fromEmail")));
    }
    public static void sendAllNoticeMail(String address, Map<String, Object> map) {
        String content =RenderUtils.render(map, RenderUtils.ALL_NOTICE_TEMP);
        Calendar cal=Calendar.getInstance();
        cal.add(Calendar.DAY_OF_MONTH, -1);
        String title="团队文档库"+(cal.get(Calendar.MONTH)+1)+"月"+(cal.get(Calendar.DAY_OF_MONTH))+"日动态汇总";
        EmailSendThread.addEmail(new SimpleEmail(address,title,content));
    }

    public static void sendAllNoticeMailWeek(String address, Map<String, Object> map) {
        String content =RenderUtils.render(map, RenderUtils.ALL_NOTICE_WEEK_TEMP);
        String title="团队文档库每周动态汇总";
        EmailSendThread.addEmail(new SimpleEmail(address,title,content));
    }

    public static void sendMentionMail(String[] address,Map<String,Object> map) {
        String content= RenderUtils.render(map, RenderUtils.MENTION_TEMP);
        String shareFrom = ((SimpleUser)map.get("shareFrom")).getName();
        String title=shareFrom+"在"+((Resource)map.get("resource")).getTitle()+"@了您";
        EmailSendThread.addEmail(new SimpleEmail(address,title,content,(String)map.get("fromEmail")));
    }
}
