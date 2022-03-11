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

/**
 * Introduction Here.
 * @date 2010-5-10
 * @author Fred Zhang (fred@cnic.cn)
 */
public class KeyConstants {
    private KeyConstants(){}
    public static final String SITE_CREATOR = "duckling.creator";
    public static final String SITE_DESCRIPTION = "duckling.description";
    public static final String PREF_REFER_STYLE = "duckling.referenceStyle";
    public static final String APPID_PREFIX_KEY = "app.appid.prefix";
    public static final String DLOG_APPID_KEY = "app.appid";
    public static final String TO_DHOME_URL="duckling.toDhome";

    public static final String SITE_TEMPLATE_KEY = "site.template.name";
    public static final String SITE_UMT_VO_KEY = "duckling.umt.vo";

    public static final String SITE_DOMAIN_KEY = "duckling.domain";
    public static final String SITE_BASEURL_KEY = "duckling.ddl.baseURL";
    public static final String SITE_NAME_KEY = "duckling.site.name";
    public static final String SITE_DISPLAY_NAME = "duckling.site.displayname";
    public static final String SKIN_NAME = "duckling.skin.name";
    public static final String SKIN_GLOBAL = "duckling.skin.global";
    public static final String SITE_MAIL_KEY = "email.address";
    public static final String SITE_MAIL_USERNAME = "email.username";
    public static final String SITE_MAIL_PASSWORD = "email.password";
    public static final String SITE_MAIL_FORMADDRESS = "email.fromAddress";
    public static final String SITE_MAIL_AUTH_KEY = "email.mail.smtp.auth";
    public static final String SITE_LOCAL_USER="duckling.clb.localuser";
    public static final String SITE_SMTP_HOST_KEY = "email.mail.smtp.host";
    public static final String SITE_ACCESS_OPTION_KEY = "site.access.option";
    public static final String SITE_DATE_FORMAT = "duckling.dateformat";
    public static final String PROP_DLOG="app.dlog.service";
    public static final String PROP_UMT = "duckling.umt.server";
    public static final String PROP_SEND_OFFSET = "emailnotifier.sendOffset";
    public static final String ENCODING = "duckling.encoding";

    public static final String CONTAINER_CLB_SERVICE_URL="duckling.clb.service";
    public final static String CONTAINER_UMT_APP_USER="duckling.umt.user";
    public static final String CONTAINER_UMT_APP_PASS = "duckling.umt.pass";
    public static final String SITE_LANGUAGE = "default.language";
    public static final String SITE_USER_PRINCIPAL = "site_user_principal";

    public static final String TEAM_CLB_USERNAME = "team.clb.username";
    public static final String TEAM_CLB_PASSWORD = "team.clb.password";
    public static final String TEAM_TYPE = "team.type";
    public static final String TEAM_ACCESS_TYPE = "team.accessType";
    public static final String TEAM_DEFAULT_VIEW = "team.default.view";
    public static final String TEAM_DEFAULT_MEMBER_AUTH = "team.defaultMemberAuth";

    public static final String CONTAINER_CLB_USER = "duckling.clb.aone.user";
    public static final String CONTAINER_CLB_PASSWORD = "duckling.clb.aone.password";
    public static final String CONTAINER_CLB_VERSION = "duckling.clb.aone.version";
    public static final String CONTAINER_CLB_MAINTAIN = "duckling.clb.aone.ismaintain";
    public static final String CONTAINER_CLB_ACCESS_MODE = "duckling.clb.access.mode";
    public static final String CONTAINER_CLB_TRANSPOND_FLAG = "duckling.file.proxy.gateway";

    public static final String DDL_CLIENT_ACCEPTED_IP = "ddl.client.accepted.ip";
    public static final String DCONVERT_SERVICE_ENABLE = "duckling.clb.dconvert.enable";
}
