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

public final class Attributes {
    private Attributes(){};
    /**
     * 全局共享的Key
     */
    public static final String APPLICATION_CONTEXT_KEY="cn.vlabs.vwb.appcontext";
    public static final String RENDER_DVIEW="RenderContext";
    public static final String MESSAGE_KEY="MessageKey";
    public static final String EXCEPTION_KEY="ExceptionKey";
    public static final String VIEW_PORT = "vwb.viewport";
    public static final String REQUEST_URL = "vwb.requesturl";
    public static final String LOGIN_FAIL_URL = "vwb.loginfailurl";
    public static final String CONTAINER_KEY="container";
    public static final String VIEWPORT_SERVICE="viewportService";
    public static final String SESSION_EXCEPTION_KEY="vwbexception";
    public static final String SITE_KEY = "serviceSite";
    public static final String REQUEST_SITE_KEY="vwb_request_site";
    public static final String URL_PARSER="__vwb_url_parser";
    public static final String VISITOR_COUNT_SERVLETCONTEXT_KEY="_vwb_site_visitor_count";
    public static final String VISITOR_SITE_COUNT="vwb.site.vistorcount";
    public static final String CHECK_SITE_FOR_USER="vwb.site.checkuser";
    public static final String CHECK_SITE_FOR_USER_ADD="add";
    public static final String CHECK_SITE_FOR_USER_APPLY="apply";
    public static final String CHECK_SITE_FOR_USER_NONE="none";
    public static final String ERROR_TYPE = "vwb.errot.type";
    public static final String TEAM_ID_FOR_JOIN_PUBLIC_TEAM = "teamId";

    public static final String UMT_ACCESS_TOKEN = "umt.access_token";
}
