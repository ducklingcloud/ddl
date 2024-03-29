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

package net.duckling.ddl.web.controller;

import java.io.UnsupportedEncodingException;
import java.net.URLDecoder;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import net.duckling.ddl.common.VWBContext;
import net.duckling.ddl.constant.ELayout;
import net.duckling.ddl.constant.LynxConstants;
import net.duckling.ddl.service.resource.IResourceService;
import net.duckling.ddl.service.resource.Resource;
import net.duckling.ddl.service.search.ISearchService;
import net.duckling.ddl.service.team.Team;
import net.duckling.ddl.service.team.TeamService;
import net.duckling.ddl.service.tobedelete.PageContentRender;
import net.duckling.ddl.service.url.URLGenerator;
import net.duckling.ddl.service.url.UrlPatterns;
import net.duckling.ddl.util.AoneTimeUtils;
import net.duckling.ddl.util.HTMLConvertUtil;
import net.duckling.ddl.util.JsonUtil;
import net.duckling.ddl.util.TeamQuery;
import net.duckling.ddl.web.interceptor.access.RequirePermission;

import org.apache.commons.lang.StringUtils;
import com.google.gson.JsonObject;
import com.google.gson.JsonArray;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.servlet.ModelAndView;

//import cn.cnic.cerc.dlog.client.WebLog;

/**
 * @date 2012-11-7
 * @author yangxiaopeng@cnic.cn
 */
@Controller
@RequestMapping("/system/search")
@RequirePermission( authenticated = true)
public class LynxSearchController extends BaseController {
    @Autowired
    private TeamService teamService;
    @Autowired
    private IResourceService resourceService;

    @Autowired
    private ISearchService searchService;
    @Autowired
    private URLGenerator urlGenerator;

    private static final String CONTENT = "content";
    private static final String KEYWORD = "keyword";
    private static final String SIZE = "size";

    private VWBContext getVWBContext(HttpServletRequest request) {
        return VWBContext.createContext(request,UrlPatterns.MYSPACE);
    }

    @RequestMapping
    //@WebLog(method="quickSearch",params=KEYWORD)
    public void quickSearch(HttpServletRequest request, HttpServletResponse response) throws UnsupportedEncodingException {
        //Step 1: Load Team information
        String keyword = URLDecoder.decode(request.getParameter(KEYWORD), "UTF-8");
        VWBContext context = getVWBContext(request);
        TeamQuery query = new TeamQuery();
        query.setTid(getTeamIdFromRequest(context, request));
        query.setKeyword(keyword);
        doTeamSearch(response, context, query);
    }

    private int[] getTeamIdFromRequest(VWBContext context, HttpServletRequest request){
        String teamName = request.getParameter("teamName");
        int tid = 0;
        if(null!=teamName && !"".equals(teamName)){
            Team team = teamService.getTeamByName(teamName);
            tid = (null!=team)?team.getId():0;
        }
        int[] result = null;
        if(tid >0){
            result = new int[]{tid};
        }else{
            List<Team> teams = teamService.getAllUserTeams(context.getCurrentUID());
            if(null == teams || teams.isEmpty()){
                result = new int[]{tid};
            }else{
                result = new int[teams.size()];
                for(int i=0; i<teams.size(); i++){
                    result[i] = teams.get(i).getId();
                }
            }
        }
        return result;
    }

    private void doTeamSearch(HttpServletResponse response, VWBContext context,
                              TeamQuery query) {
        //Step 2: Search Page Content Data and Return Wrap JSONObject
        JsonObject pcr = searchPageContent(context,query);
        //Step 3: Search File Data In Resource and Return Wrap JSONObject
        JsonObject fr = searchFileInResource(context,query);
        //Step 4: Search Page Meta Data In Resource and Return Wrap JSONObject
        JsonObject flr = searchFolderInResource(context,query);
        //Step 5: Wrap all result to one composite json object
        JsonObject finalResult = wrapSearchResult(pcr, fr, flr);
        //Step 6: Write to response stream
        JsonUtil.write(response, finalResult);
    }

    @RequestMapping(params="func=searchResult")
    //@WebLog(method="searchResult",params=KEYWORD)
    public ModelAndView searchResult(HttpServletRequest request) {
        VWBContext context = getVWBContext(request);
        String keyword = request.getParameter(KEYWORD);
        List<Team> teams =teamService.getAllUserTeams(context.getCurrentUID());
        ModelAndView mv = layout(ELayout.LYNX_MAIN, context, "/jsp/aone/search/search.jsp");
        mv.addObject(KEYWORD, keyword);
        mv.addObject("teams", teams);
        mv.addObject("currentTeam", request.getParameter("teamName"));
        mv.addObject(LynxConstants.PAGE_TITLE, "文档库搜索");
        return mv;
    }

    @RequestMapping(params="func=research")
    //@WebLog(method="search",params="keyword,flag")
    public void research(HttpServletRequest request,HttpServletResponse response) {
        VWBContext context = getVWBContext(request);
        TeamQuery query = initQueryParam(request, context);
        doTeamSearch(response, context, query);

        String flag = request.getParameter("flag");
        String keyword = request.getParameter(KEYWORD);
        JsonObject obj = new JsonObject();
        obj.addProperty("flag",flag);
        obj.addProperty(KEYWORD,keyword);
        JsonUtil.write(response, obj);
    }

    private TeamQuery initQueryParam(HttpServletRequest request, VWBContext context) {
        String keyword = request.getParameter(KEYWORD);
        int pageSize = Integer.parseInt(request.getParameter(SIZE));
        int offset = Integer.parseInt(request.getParameter("offset"));
        String queryer = context.getCurrentUID();
        int[] tids = getTeamIdFromRequest(context, request);
        TeamQuery query = buildQueryParam(keyword, pageSize, offset, tids, queryer);//lizi
        return query;
    }

    private TeamQuery buildQueryParam(String keyword, int pageSize, int offset, int[] tids, String queryer) {//lizi
        TeamQuery query = new TeamQuery();
        query.setTid(tids);
        query.setKeyword(keyword);
        query.setOffset(offset);
        query.setSize(pageSize);
        query.setQueryer(queryer);//lizi
        return query;
    }

    @RequestMapping(params="func=loadmore")
    //@WebLog(method="loadmore",params="keyword,oper_name")
    public void loadMoreData(HttpServletRequest request,HttpServletResponse response) {
        VWBContext context = getVWBContext(request);
        TeamQuery query = initQueryParam(request, context);
        String type = request.getParameter("type");
        JsonObject page = null;
        JsonObject file = null;
        JsonObject bundle = null;
        if(null==type || "".equals(type)|| LynxConstants.TYPE_PAGE.equals(type)){
            page = searchPageContent(context,query);
        }else if(LynxConstants.TYPE_FILE.equals(type)){
            file = searchFileInResource(context, query);
        }else{
            bundle = searchBundleInResource(context, query);
        }
        JsonObject finalResult = wrapSearchResult(page,file,bundle);
        JsonUtil.write(response, finalResult);

        String keyword = request.getParameter(KEYWORD);
        String opername = request.getParameter("oper_name");
        JsonObject obj = new JsonObject();
        obj.addProperty(KEYWORD,keyword);
        obj.addProperty("oper_name", opername);
        JsonUtil.write(response, obj);
    }

    private JsonObject getPageJSONObject(VWBContext context, int i, String title,String digest,Resource page) {
        JsonObject dpage = new JsonObject();
        dpage.addProperty("id", i);
        dpage.addProperty("author", page.getCreator());
        dpage.addProperty("title", title);
        dpage.addProperty("url", getURL( UrlPatterns.T_VIEW_R, page.getTid(), Integer.toString(page.getRid())));
        dpage.addProperty("modifyTime", AoneTimeUtils.formatToDateTime(page.getLastEditTime()));
        dpage.addProperty("digest", digest);
        dpage.addProperty("rid", page.getRid());
        String tagUrl=getURL( UrlPatterns.T_TAG, page.getTid(), Integer.toString(page.getRid()));
        String tagBaseURL = getURL( UrlPatterns.T_LIST, page.getTid(), Integer.toString(page.getRid()));
        dpage.addProperty("tagurl", tagUrl);
        dpage.addProperty("tagListUrl", tagUrl);
        dpage.addProperty("starmarkurl", getURL( UrlPatterns.T_STARTMARK, page.getTid(), Integer.toString(page.getRid())));
        dpage.addProperty("tagMap", getJSONFromMap(tagBaseURL, page.getTagMap()));
        dpage.addProperty("starmark", (null!=page.getMarkedUserSet() && page.getMarkedUserSet().contains(context.getCurrentUID())?true:false));
        dpage.addProperty("teamName", context.getContainer().getSite(page.getTid()).getSiteName());
        dpage.addProperty("tid", page.getTid());
        return dpage;
    }

    private JsonObject getResourceJSONObject(VWBContext context, int i, Resource resource,String keyword) {
        if(null == resource){
            return new JsonObject();
        }
        JsonObject obj = new JsonObject();
        obj.addProperty("sn", i);
        String heightKeyword = filterScript(resource.getTitle()).replace(keyword, "<em>"+keyword+"</em>");
        obj.addProperty("title", heightKeyword);
        obj.addProperty("creator", resource.getCreator());
        obj.addProperty("createTime", AoneTimeUtils.formatToDateTime(resource.getCreateTime()));
        obj.addProperty("url", getURL( UrlPatterns.T_VIEW_R, resource.getTid(), Integer.toString(resource.getRid())));
        if(resource.isFile()){
            obj.addProperty("downloadUrl", getURL( UrlPatterns.T_DOWNLOAD, resource.getTid(), Integer.toString(resource.getRid()))+"?type=doc");
        }
        String tagUrl=getURL( UrlPatterns.T_TAG, resource.getTid(), Integer.toString(resource.getRid()));
        String tagBaseURL = getURL( UrlPatterns.T_LIST, resource.getTid(), Integer.toString(resource.getRid()));
        obj.addProperty("tagurl", tagUrl);
        obj.addProperty("tagListUrl", tagBaseURL);
        obj.addProperty("starmarkurl", getURL( UrlPatterns.T_STARTMARK, resource.getTid(), Integer.toString(resource.getRid())));
        obj.addProperty("tagMap", getJSONFromMap(tagUrl, resource.getTagMap()));
        obj.addProperty("rid", resource.getRid());
        obj.addProperty("starmark", (null!=resource.getMarkedUserSet() && resource.getMarkedUserSet().contains(context.getCurrentUID())?true:false));
        obj.addProperty("teamName", context.getContainer().getSite(resource.getTid()).getSiteName());
        obj.addProperty("tid", resource.getTid());
        return obj;
    }
    Pattern pattern = Pattern.compile("<script\\s*+>", Pattern.CASE_INSENSITIVE);
    private String filterScript(String s){
        Matcher matcher = pattern.matcher(s);
        if(matcher.find()){
            return HTMLConvertUtil.replaceLtGt(s);
        }
        return s;
    }

    private String getJSONFromMap(String baseTagURL, Map<Integer, String> map){
        JsonArray array = new JsonArray();
        if(null!=map && !map.isEmpty()){

            for(Map.Entry<Integer, String> entry : map.entrySet()){
                JsonObject obj = new JsonObject();
                obj.addProperty("id", entry.getKey());
                obj.addProperty("title", entry.getValue());
                obj.addProperty("tagurl", baseTagURL);
                array.add(obj);
            }
        }
        return array.toString();
    }

    private JsonObject wrapSearchResult(JsonObject pcr, JsonObject fr, JsonObject flr) {
        JsonObject result = new JsonObject(), empty = new JsonObject();
        result.add("pageResult", (pcr==null) ? empty : pcr.get(CONTENT));
        result.add("fileResult", (fr==null) ? empty : fr.get(CONTENT));
        result.add("floderResult", (flr==null) ? empty : flr.get(CONTENT));
        result.addProperty("count", getTotalCount(pcr, fr, flr));
        result.addProperty("pageCount", getPartCount(0,pcr));
        result.addProperty("fileCount", getPartCount(0,fr));
        result.addProperty("floderCount", getPartCount(0,flr));
        return result;
    }

    private int getTotalCount(JsonObject pcr, JsonObject fr, JsonObject br) {
        int count = getPartCount(0,pcr);
        count = getPartCount(count,fr);
        count = getPartCount(count,br);
        return count;
    }

    @RequestMapping(params="func=searchlog")
    //@WebLog(method="searchclick",params="keyword,rank,pid,type")
    public void getLog(HttpServletRequest request,HttpServletResponse response) {
        int tid = Integer.parseInt(request.getParameter("tid"));
        request.setAttribute("cftid", tid);
        String rank = request.getParameter("rank");
        String type = request.getParameter("type");
        String pid = request.getParameter("pid");
        String keyword = request.getParameter(KEYWORD);
        JsonObject obj = new JsonObject();
        obj.addProperty("rank",rank);
        obj.addProperty("type",type);
        obj.addProperty("pid",pid);
        obj.addProperty(KEYWORD,keyword);
        JsonUtil.write(response, obj);
    }

    private int getPartCount(int count, JsonObject part) {
        if (part != null && part.has(SIZE)) {
            return count + Integer.parseInt(part.get(SIZE).getAsString());
        }
        return count;
    }

    private JsonObject searchFileInResource(VWBContext context, TeamQuery query){
        int fileCount = searchService.getResourceCount(query.getTid(),
                                                       query.getKeyword(), LynxConstants.TYPE_FILE);
        query.setType(LynxConstants.TYPE_FILE);
        return getResourceJSONObject(context, query, fileCount);
    }

    private JsonObject searchFolderInResource(VWBContext context, TeamQuery query){
        int fileCount = searchService.getResourceCount(query.getTid(),
                                                       query.getKeyword(), LynxConstants.TYPE_FOLDER);
        query.setType(LynxConstants.TYPE_FOLDER);
        return getResourceJSONObject(context, query, fileCount);
    }

    private JsonObject searchBundleInResource(VWBContext context, TeamQuery query){
        int bundleCount = searchService.getResourceCount(query.getTid(),
                                                         query.getKeyword(), LynxConstants.TYPE_BUNDLE);
        query.setType(LynxConstants.TYPE_BUNDLE);
        return getResourceJSONObject(context, query, bundleCount);
    }

    @SuppressWarnings("unchecked")
    private JsonObject searchPageContent(VWBContext context, TeamQuery query) {
        int totalCount = searchService.getTeamPageCount(query.getTid(), query.getKeyword());
        List<Long> pageIds = searchService.queryPageWithOptimize( query); //搜索优化
        if(pageIds!=null && pageIds.size() != 0){
            JsonObject result = new JsonObject();
            result.addProperty(SIZE, totalCount);
            JsonArray array = new JsonArray();
            //TODO
            List<Resource> pageList = resourceService.fetchDPageBasicListByPageIncrementId(pageIds);
            List<PageContentRender> contentList =resourceService.fetchDPageContentByIncrementId(pageIds);
            pageList = dataConsistent(pageList, contentList);
            String[] titles = new String[pageList.size()];
            String[] digests = new String[pageList.size()];
            for(int i=0;i<titles.length;i++) {
                titles[i]=pageList.get(i).getTitle();
                digests[i] = contentList.get(i).getContent();
            }
            String[] heightTitles = null;
            String[] heightDigests = null;
            if(!StringUtils.isBlank(query.getKeyword())){
                heightTitles = searchService.highLightTitle(titles, query.getKeyword());
                heightDigests = searchService.highLightDigest(digests, query.getKeyword());
            }else{// keyword maybe empty and throw exception
                heightTitles = titles;
                heightDigests = digests;
            }
            Map<Integer,Map<Integer,Resource>> pagesMap = transferPageToMap(pageList);
            for (int i = 0; i < pageList.size(); i++){
                Resource page =getResourceFromMap(pagesMap, pageList.get(i));
                JsonObject dpage = getPageJSONObject(context, i,heightTitles[i],heightDigests[i],page);
                array.add(dpage);
            }
            result.add(CONTENT, array);
            return result;
        }
        return null;
    }

    private Resource getResourceFromMap(Map<Integer,Map<Integer,Resource>> pagesMap, Resource page){
        Map<Integer,Resource> child = pagesMap.get(page.getTid());
        if(child==null){
            return null;
        }
        return child.get(page.getRid());
    }

    private Map<Integer,Map<Integer,Resource>> transferPageToMap(List<Resource> pages){
        Map<Integer,Map<Integer,Resource>> result = new HashMap<Integer,Map<Integer,Resource>>();
        Map<Integer,List<Integer>> pageMap = new HashMap<Integer,List<Integer>>();
        if(pages==null||pages.isEmpty()){
            return result;
        }
        for(Resource p : pages){
            List<Integer> i = pageMap.get(p.getTid());
            if(i==null){
                i = new ArrayList<Integer>();
                pageMap.put(p.getTid(), i);
            }
            i.add(p.getRid());
        }


        for(Entry<Integer, List<Integer>> entry : pageMap.entrySet()){
            List<Resource> res = resourceService.getResources(entry.getValue(), entry.getKey(), LynxConstants.TYPE_PAGE);
            Map<Integer,Resource> child = new HashMap<Integer,Resource>();
            for(Resource r : res){
                child.put(r.getRid(), r);
            }
            result.put(entry.getKey(), child);
        }
        return result;
    }


    /**
     * 消除pageList和contentList可能存在的数据不一致：contentList中的内容比pageList的少
     * @param pageList Page列表
     * @param contentList Page对应的内容列表
     * @return 新的Page列表
     */
    private List<Resource> dataConsistent(List<Resource> pageList, List<PageContentRender> contentList){
        if(isEmpty(pageList) && isEmpty(contentList)){
            return pageList;
        }
        int pagesize = pageList.size();
        int contentSize = contentList.size();
        List<Resource> newPageList = new ArrayList<Resource>();
        int j = 0;
        for(int i=0; i<pagesize; i++){
            Resource page = pageList.get(i);
            if(j<contentSize && isPair(page,contentList.get(j))){
                newPageList.add(page);
                j++;
            }
        }
        return newPageList;
    }

    private boolean isPair(Resource page, PageContentRender pcr){
        return (page.getRid() == pcr.getPid() && page.getTid() == pcr.getTid())?true:false;
    }

    private String getUrlType(Resource resource){
        if(null == resource || null == resource.getItemType()
           || "".equals(resource.getItemType())){
            return null;
        }
        String resType = resource.getItemType();
        if(LynxConstants.TYPE_FOLDER.equals(resType)){
            return UrlPatterns.T_VIEW_R;
        }else if(LynxConstants.TYPE_FILE.equals(resType)){
            return UrlPatterns.T_VIEW_R;
        }else if(LynxConstants.TYPE_PAGE.equals(resType)){
            return UrlPatterns.T_VIEW_R;
        }else{
            return null;
        }
    }

    private JsonObject getResourceJSONObject(VWBContext context, TeamQuery query, int count){
        List<Long> sphinxIds = searchService.query(query, TeamQuery.QUERY_FOR_RESOURCE);
        //      List<Long> sphinxIdstemp = site.getLynxSearchService().query(query, TeamQuery.QUERY_FOR_RESOURCE);
        //      List<Long> sphinxIds = site.getLynxSearchService().orderByInterest(sphinxIdstemp, query); //lizi
        if (sphinxIds != null && sphinxIds.size() > 0) {
            JsonObject result = new JsonObject();
            result.addProperty(SIZE, count);
            JsonArray array = new JsonArray();
            for (int i = 0; i < sphinxIds.size(); i++) {
                Resource resource = resourceService.getResource(sphinxIds.get(i).intValue());
                JsonObject resObj = getResourceJSONObject(context, i, resource,query.getKeyword());
                array.add(resObj);
            }
            result.add(CONTENT, array);
            return result;
        }
        return null;
    }


    private String getURL(String urlContext, int tid, String rid){
        return urlGenerator.getURL(tid,urlContext, rid+"",null);
    }

    private boolean isEmpty(List<?> list){
        return (null == list || list.isEmpty())?true:false;
    }
}
