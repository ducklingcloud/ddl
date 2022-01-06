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

import javax.servlet.http.HttpServletRequest;

public class TeamQuery {

    public static final String QUERY_FOR_PAGECONTENT = "page";
    public static final String QUERY_FOR_RESOURCE = "resource";
    public static final String SORT_DATE_ASC = "dateup";
    public static final String SORT_DATE_DESC = "datedown";
    public static final String SORT_TITLE_ASC = "titleup";
    public static final String SORT_TITLE_DESC = "titledown";
    //此值为文档列表在显示极端情况下（单次取到的所有资源属于同一Bundle）每次增加的取资源条数
    public static final int LOADSIZE_ON_EXTREME_CASE = 50;
    private static final int SIZE = 1000;

    private String date; //时间段：今天，昨天 .....
    private String type;  //查询的资源类型item_type
    private int[] tagIds; //标签
    private int offset; //显示偏移量
    private int size = SIZE; //每次取的条数（Sphinx）
    private int[] tids; //当前需要搜索的团队ID
    private String keyword; //搜索的关键词
    private String filter; //过滤器（Sphinx）
    private String queryer; //查询者
    private String orderDate; //按时间排序
    private String orderTitle; //按标题排序

    public String getFilter() {
        return filter;
    }

    public void setFilter(String filter) {
        this.filter = filter;
    }

    public String getKeyword() {
        return keyword;
    }

    public void setKeyword(String keyword) {
        this.keyword = keyword;
    }

    public static TeamQuery buildForQuery(HttpServletRequest request){
        TeamQuery q = new TeamQuery();
        String keyword = request.getParameter("keyword");
        String str = request.getParameter("offset");
        if(str!=null){
            q.setOffset(Integer.parseInt(str));
        }else{
            q.setOffset(1);
        }
        q.setOffset(WebParamUtil.getIntegerValue(request,"offset"));
        q.setSize(WebParamUtil.getIntegerValue(request, "size"));
        q.setType(request.getParameter("type"));
        q.setTagIds(WebParamUtil.getIntegerValues(request,"tag"));
        String filter = request.getParameter("filter");
        q.setFilter(("all".equals(filter))?null:filter);//all表示点击所有文档，此时不需要filter
        q.setDate(request.getParameter("date"));
        q.setKeyword(keyword);
        q.setOrderTitle(request.getParameter("orderTitle"));
        q.setOrderDate(request.getParameter("orderDate"));
        return q;
    }

    /**
     * @return the tids
     */
    public int[] getTid() {
        return tids;
    }

    /**
     * @param tid the tid to set
     */
    public void setTid(int[] tids) {
        this.tids = tids;
    }

    /**
     * @return the date
     */
    public String getDate() {
        return date;
    }
    /**
     * @param date the date to set
     */
    public void setDate(String date) {
        this.date = date;
    }
    /**
     * @return the type
     */
    public String getType() {
        return type;
    }
    /**
     * @param type the type to set
     */
    public void setType(String type) {
        this.type = type;
    }
    /**
     * @return the tagIds
     */
    public int[] getTagIds() {
        return tagIds;
    }
    /**
     * @param tagIds the tagIds to set
     */
    public void setTagIds(int[] tagIds) {
        this.tagIds = tagIds;
    }
    /**
     * @return the offset
     */
    public int getOffset() {
        return offset;
    }
    /**
     * @param offset the offset to set
     */
    public void setOffset(int offset) {
        this.offset = offset;
    }
    /**
     * @return the size
     */
    public int getSize() {
        return size;
    }
    /**
     * @param size the size to set
     */
    public void setSize(int size) {
        this.size = size;
    }

    public String getQueryer() {
        return queryer;
    }

    public void setQueryer(String queryer) {
        this.queryer = queryer;
    }

    public String getOrderDate() {
        return orderDate;
    }

    public void setOrderDate(String orderDate) {
        this.orderDate = orderDate;
    }

    public String getOrderTitle() {
        return orderTitle;
    }

    public void setOrderTitle(String orderTitle) {
        this.orderTitle = orderTitle;
    }



}
