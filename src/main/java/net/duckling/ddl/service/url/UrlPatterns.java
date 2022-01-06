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

package net.duckling.ddl.service.url;

import java.util.Map;
import java.util.Map.Entry;

import org.springframework.util.AntPathMatcher;
import org.springframework.util.PathMatcher;

/**
 * @date Jun 22, 2011
 * @author xiejj@cnic.cn
 */
public abstract class UrlPatterns {
    public static final String PLAIN="plain";
    public static final String T_TEAM = "team";

    public static final String T_PAGE = "view";
    public static final String T_DIFF = "diff";
    public static final String T_INFO = "info";
    public static final String T_PREVIEW = "preview";
    public static final String CONFLICT ="conflict";
    public static final String T_EDIT_PAGE = "edit";
    public static final String T_SHARE_PAGE = "share";
    public static final String DELETE ="delete";
    public static final String FIND = "find";
    public static final String ERROR = "error";
    public static final String T_ATTACH ="attach";
    public static final String T_CACHABLE= "cachable";
    public static final String T_DOWNLOAD = "download";
    public static final String T_DOWNLOAD_CACHE = "downloadCache";
    public static final String T_FILE="file";
    public static final String T_INFO_FILE="infoFile";
    public static final String PORTLET = "portlet";
    public static final String SIMPLE ="simple";
    public static final String LOGIN = "login";
    public static final String LOGOUT = "logout";
    public static final String ESCIENCE_REGIST = "escienceRegist";
    public static final String CREATE_RESOURCE = "createresource";
    public static final String VIEW_COLLECTION="viewCollection";
    public static final String ADMIN ="admin";
    public static final String ACTIVITION = "activation";

    public static final String SWITCH_TEAM = "switchTeam";
    public static final String INVITE = "invite";
    public static final String CONFIG_COLLECTION="configCollection";
    public static final String CREATE_COLLECTION = "createCollection";
    public static final String T_QUICK = "quick";
    public static final String SHARE_FILE = "shareFile";
    public static final String SHARE_FILE_AGAIN = "shareAgain";
    public static final String DIRECT = "direct";
    public static final String USER = "user";
    public static final String USER_HOME = "home";
    public static final String CONTACTS = "contacts";
    public static final String BOOKMARK = "bookmark";

    public static final String RESOURCE_SHARE = "resource_share";
    public static final String PAN_SHARE = "pan_share";

    public static final String FEEDBACK = "feedback";

    public static final String QUIT_TEAM = "quitteam";

    public static final String ADMIN_AONE_FEEDBACK = "adminFeedback";

    public static final String LOGGING = "logging";
    public static final String T_RECLOGGING = "reclogging";

    public static final String CHECK_SERVER_STATE = "checkServerState";

    /*------------------------For Lynx URL System--------------------*/
    public static final String T_NINE_GRID = "nineGrid";
    public static final String T_TAG = "tag";
    public static final String DASHBOARD = "dashboard";
    public static final String MYSPACE="myspace";
    public static final String T_NOTICE = "notice";
    public static final String T_TEAM_HOME = "teamHome";
    public static final String T_BUNDLE = "bundle";
    public static final String T_ORIGINAL_IMAGE="originalImage";
    public static final String T_NAVBAR = "navbar";
    public static final String T_CONFIG_TAG = "configTag";
    public static final String T_STARTMARK = "starmark";
    public static final String T_CREATE_BUNDLE = "createBundle";
    public static final String T_RESOURCE = "resource";
    public static final String T_LIST = "files";
    public static final String T_VIEW_R = "f";
    public static final String T_COMMENT = "comment";
    public static final String COPYFILE = "copyfile";
    public static final String REGIST="regist";
    public static final String REGIST_CODE="registCode";
    public static final String DATA_SYNC = "datasync";
    public static final String JOIN_PUBLIC_TEAM = "joinPublicTeam";
    public static final String USER_GUIDE = "userguide";
    //add by lvly@2012-6-8
    public static final String T_TASK_PATTERNS="task";
    public static final String T_COPY_PATTERNS="copy";
    public static final String T_BUNDLE_BASE="bundleBase";


    public static final String GLOBAL_SEARCH = "globalSearch";
    public static final String T_SEARCH = "teamSearch";

    public static final String PICTURE_CHECK="pictrueCheckCode";
    public static final String T_CONFIG_SHORT_CUT = "configShortCut";
    public static final String TO_DHOME="toDhome";
    public static final String T_UPLOAD = "upload";
    public static final String CONFIG_TEAM = "configTeam";
    public static final String SHARE_FILE_SUCCESS = "shareFileSuccess";
    public static final String T_RECOMMEND = "recommend";
    public static final String T_FEED = "feed";
    public static final String CREATE_TEAM = "createTeam";
    public static final String T_CREATE_PAGE = "createPage";
    public static final String T_EXPORT = "export";
    public static final String T_SUBSCRIBE = "subscribe";
    public static final String T_VIEW_FILE= "viewFile";
    public static final String T_PREVIEW_OFFICE="previewOffice";

    public static final String PAN_LIST = "panList";
    public static final String PAN_VIEW = "panView";
    public static final String PAN_DOWNLOAD = "panDownload";
    public static final String PAN_PREVIEW = "panPreview";
    public static final String PAN_UPLOAD = "panUpload";
    public static final String PAN_THUMBNAILS = "panDownloadThumbnails";
    public static final String PAN_HISTORY = "panHistory";
    public static final String PAN_APPLICATION = "panApplication";

    private static final UrlPatterns _INSTANCE = new UrlPatternResolver();

    public static UrlPatterns getInstance() {
        return _INSTANCE;
    }

    private PathMatcher pathMatcher;

    public UrlPatterns() {
        pathMatcher = new AntPathMatcher();
    }

    public abstract Map<String, String> getPatterns();

    public abstract Map<String, String> getReversPatterns();

    public String findUrlPattern(String action) {
        if (action != null) {
            return getPatterns().get(action);
        }
        return null;
    }

    public Map<String, String> resolve(String url) {
        Map<String, String> reversPattern = getReversPatterns();
        for (Entry<String, String> entry:reversPattern.entrySet()){
            String key = entry.getKey();
            String pattern = entry.getValue();
            if (pathMatcher.match(pattern, url)) {
                Map<String, String> variables = pathMatcher
                        .extractUriTemplateVariables(pattern, url);
                variables.put("type", key);
                return variables;
            }

        }
        return null;
    }
}
