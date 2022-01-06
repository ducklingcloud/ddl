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

import java.util.List;

import net.duckling.ddl.common.DDLFacade;
import net.duckling.ddl.common.Site;
import net.duckling.ddl.service.browselog.BrowseLogService;
import net.duckling.ddl.service.browselog.BrowseStat;


/**
 * @date Mar 9, 2011
 * @author xiejj@cnic.cn
 */
public class HotPagesTag extends VWBBaseTag {
    private static final long serialVersionUID = 1L;
    private static final int DAYS = 30;
    private static final int PAGES = 10;
    private int pages;
    private int days;
    private String boxStyle;
    private String digitStyle;
    @Override
    public int doVWBStart() throws Exception {
        Site site = vwbcontext.getSite();
        List<BrowseStat> stats = DDLFacade.getBean(BrowseLogService.class).getTopPageView(site.getId(),pages, days);
        if (stats.size()>0){
            String topPages="<ul class='" +boxStyle+"'>";
            for (BrowseStat stat:stats){
                topPages=topPages+"<li>";
                if (digitStyle!=null){
                    topPages=topPages+"<span class='" +digitStyle+"' >"+stat.getCount()+"</span>";
                }else{
                    topPages=topPages+"<span>"+stat.getCount()+"</span>";
                }
                topPages=topPages+"<a href='"+site.getViewURL(stat.getRid())+"'>"+stat.getTitle()+"</a>";
                topPages=topPages+"</li>";
            }
            topPages=topPages+"</ul>";
            pageContext.getOut().write(topPages);
        }
        return EVAL_PAGE;
    }

    protected void initTag(){
        super.initTag();
        this.boxStyle=null;
        this.digitStyle=null;
    }
    public void setPages(int pages) {
        int tempPages = pages;
        if (tempPages<=0){
            tempPages=PAGES;
        }
        this.pages = tempPages;
    }
    public void setDays(int days){
        int tempDays = days;
        if (tempDays<=0){
            tempDays=DAYS;
        }
        this.days=tempDays;
    }

    public void setBoxStyle(String boxStyle){
        this.boxStyle=boxStyle;
    }
    public void setDigitStyle(String digitStyle){
        this.digitStyle = digitStyle;
    }
}
