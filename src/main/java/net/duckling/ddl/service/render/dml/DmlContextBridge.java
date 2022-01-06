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

package net.duckling.ddl.service.render.dml;

import java.util.Map;

import net.duckling.ddl.common.Site;
import net.duckling.ddl.common.VWBContext;
import net.duckling.ddl.service.tobedelete.Page;
import net.duckling.ddl.service.url.UrlPatterns;


/**
 * Introduction Here.
 *
 * @date 2010-3-8
 * @author 狄
 */
public class DmlContextBridge implements DmlContext {
    public DmlContextBridge(VWBContext context) {
        this.site = context.getSite();
    }

    private Site site;


    public String getBaseUrl() {
        String basrurl = site.getBaseURL();
        if (basrurl.charAt(basrurl.length() - 1) == '/') {
            return basrurl;
        } else {
            return basrurl + "/";
        }
    }

    public String getViewURL(int resourceid) {
        return site.getViewURL(resourceid);
    }

    public String getEditURL(int resourceid) {
        return site.getEditURL(resourceid);
    }

    public String getURL(String context, String page, String params) {
        return site.getURL(context, page, params, true);
    }

    public String getDdataSiteTableName(String tablename) {
        String sitename = "site" + site.getId() + "_";
        return sitename + tablename;
    }

    public String removeDdataSiteTableName(String tablename) {
        String sitename = "site" + site.getId() + "_";
        if (tablename.startsWith(sitename)) {
            tablename = tablename.substring(sitename.length());
        }
        return tablename;

    }

    public Page getViewPort(int resourceId) {
        return new Page();
    }

    public boolean isInternalURL(String url) {
        if (url == null) {
            return false;
        }
        if (url.startsWith(site.getAbsoluteTeamBase())) {
            return true;
        }
        if (url.startsWith(site.getRelativeTeamBase())) {
            return true;
        }
        return false;
    }

    public Map<String, String> resolve(String url) {
        if (url.startsWith(site.getAbsoluteTeamBase())) {
            url = url.substring(site.getAbsoluteTeamBase().length());
            return UrlPatterns.getInstance().resolve(url);
        } else if (url.startsWith(site.getRelativeTeamBase())) {
            url = url.substring(site.getRelativeTeamBase().length());
            return UrlPatterns.getInstance().resolve(url);
        }
        return null;
    }
}
