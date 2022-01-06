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

package net.duckling.ddl.web.tag;

import java.util.Date;
import java.util.List;
import java.util.ResourceBundle;

import net.duckling.ddl.common.DDLFacade;
import net.duckling.ddl.service.browselog.BrowseLog;
import net.duckling.ddl.service.browselog.BrowseLogService;
import net.duckling.ddl.util.AoneTimeUtils;

import org.apache.commons.lang.time.DateFormatUtils;


/**
 * @date Mar 9, 2011
 * @author xiejj@cnic.cn
 */
public class VisitorTag extends VWBBaseTag {
    private static final long serialVersionUID = 1L;

    private int length;

    private int rid;

    private String boxStyle;

    public void setRid(int rid) {
        this.rid = rid;
    }

    public void setLength(int length) {
        this.length = length;
    }

    public void setBoxStyle(String boxStyle) {
        this.boxStyle = boxStyle;
    }

    protected void initTag() {
        super.initTag();
        rid = -1;
    }

    @Override
    public int doVWBStart() throws Exception {
        if (rid == -1) {
            rid = vwbcontext.getResource().getRid();
        }
        Date now = new Date();
        List<BrowseLog> browseLogs = DDLFacade.getBean(BrowseLogService.class)
                .getVisitor(rid,length);
        String html = "<ul class='" + boxStyle + "'>";
        for (BrowseLog browseLog : browseLogs) {
            html += "<li>";
            html += browseLog.getDisplayName();
            html += "<span>" + format(browseLog.getBrowseTime(), now) + "</span>";
            html += "</li>";
        }
        html = html + "</ul>";
        pageContext.getOut().write(html);
        return EVAL_PAGE;
    }

    private String format(Date visitDate, Date now) {
        ResourceBundle bundle = vwbcontext.getBundle("CoreResources");
        String formatPattern;
        if (AoneTimeUtils.isSameDay(now, visitDate)) {
            formatPattern = bundle.getString("visitdate.pattern.today");
        } else if (AoneTimeUtils.isSameYear(visitDate, now)) {
            formatPattern = bundle.getString("visitdate.pattern.thisyear");
        } else {
            formatPattern = bundle.getString("visitdate.pattern.other");
        }
        return DateFormatUtils.format(visitDate, formatPattern, pageContext.getRequest().getLocale());
    }
}
