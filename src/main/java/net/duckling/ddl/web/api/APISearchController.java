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

package net.duckling.ddl.web.api;

import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import net.duckling.ddl.common.Site;
import net.duckling.ddl.common.VWBContext;
import net.duckling.ddl.constant.LynxConstants;
import net.duckling.ddl.service.resource.IResourceService;
import net.duckling.ddl.service.resource.Resource;
import net.duckling.ddl.service.search.ISearchService;
import net.duckling.ddl.service.tobedelete.Page;
import net.duckling.ddl.service.url.UrlPatterns;
import net.duckling.ddl.util.JsonUtil;
import net.duckling.ddl.util.NumberFormatUtil;
import net.duckling.ddl.util.TeamQuery;
import net.duckling.ddl.web.interceptor.access.RequirePermission;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;


/**
 * API接口中的搜索服务
 *
 * @date 2012-12-22
 * @author zzb@cnic.cn
 */
@Controller
@RequestMapping("/api/search")
@RequirePermission(target="team", operation="view")
public class APISearchController extends APIBaseController {

    @Autowired
    private IResourceService resourceService;
    @Autowired
    private ISearchService searchService;

    private static final String CONTENT = "content";
    private static final String KEYWORD = "keyword";

    @RequestMapping
    public void service(@RequestParam(KEYWORD) String keyword,
                        HttpServletRequest request, HttpServletResponse response) {
        Site site = findSite(request);
        int tid = site.getId();
        String uid = findUser(request);
        int offset = NumberFormatUtil.parseInt(request.getParameter("offset"), 0);
        int limit = NumberFormatUtil.parseInt(request.getParameter("size"), 10);
        VWBContext context = findVWBContext(request, UrlPatterns.MYSPACE);
        TeamQuery query = buildQueryParam(keyword, limit, offset, new int[]{tid}, uid);
        doTeamSearch(response, context, query, site);
    }

    private TeamQuery buildQueryParam(String keyword, int pageSize, int offset, int[] tids, String queryer) {//lizi
        TeamQuery query = new TeamQuery();
        query.setTid(tids);
        query.setKeyword(keyword);
        query.setOffset(offset);
        query.setSize(pageSize);
        query.setQueryer(queryer);
        return query;
    }

    private void doTeamSearch(HttpServletResponse response, VWBContext context,
                              TeamQuery query, Site site) {
        JsonObject pcr = searchPageContent(context,query, site);
        JsonObject fr = searchFileInResource(query);
        JsonObject br = searchBundleInResource(context,query, site);

        JsonObject finalResult = wrapSearchResult(pcr, fr, br);
        JsonUtil.write(response, finalResult);
    }

    @SuppressWarnings("unchecked")
    private JsonObject searchFileInResource(TeamQuery query){
        query.setType(LynxConstants.TYPE_FILE);
        JsonObject fileResult = new JsonObject();
        fileResult.add(CONTENT, getResourceJSONObject(query));
        return fileResult;
    }

    private JsonArray getResourceJSONObject(TeamQuery query){
        List<Long> sphinxIds = searchService.query(query, TeamQuery.QUERY_FOR_RESOURCE);
        List<Resource> resourceList = resourceService.getResourcesBySphinxID(sphinxIds);
        if (resourceList != null && resourceList.size() > 0) {
            return JsonUtil.getJSONArrayFromResourceList(resourceList);
        }
        return null;
    }

    private JsonArray getPageResourceJSONObject(TeamQuery query){
        List<Long> sphinxIds = searchService.queryPageWithOptimize(query);
        List<Resource> pageList = resourceService.fetchDPageBasicListByPageIncrementId(sphinxIds);
        Map<Integer,Map<Integer,Resource>> pagesMap = transferPageToMap(pageList);

        List<Resource> resourceList = new ArrayList<Resource>();
        if(pageList != null && pageList.size() > 0) {
            for (Resource page : pageList){
                resourceList.add(page);
            }
        }

        if (resourceList.size() > 0) {
            return JsonUtil.getJSONArrayFromResourceList(resourceList);
        }
        return null;
    }

    private Resource getResourceFromMap(Map<Integer,Map<Integer,Resource>> pagesMap, Page page){
        Map<Integer,Resource> child = pagesMap.get(page.getTid());
        if(child==null){
            return null;
        }
        return child.get(page.getPid());
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

    @SuppressWarnings({ "unchecked"})
    private JsonObject searchBundleInResource(VWBContext context, TeamQuery query, Site site){
        query.setType(LynxConstants.TYPE_BUNDLE);
        JsonObject bundleResult = new JsonObject();
        bundleResult.add(CONTENT, getResourceJSONObject(query));
        return bundleResult;
    }

    @SuppressWarnings("unchecked")
    private JsonObject wrapSearchResult(JsonObject pcr, JsonObject fr, JsonObject br) {
        JsonObject result = new JsonObject(), empty = new JsonObject();
        result.add("pageResult", (pcr==null) ? empty : pcr.get(CONTENT));
        result.add("fileResult", (fr==null) ? empty : fr.get(CONTENT));
        result.add("bundleResult", (br==null) ? empty : br.get(CONTENT));
        return result;
    }

    @SuppressWarnings({ "unchecked"})
    private JsonObject searchPageContent(VWBContext context, TeamQuery query, Site site) {
        JsonObject bundleResult = new JsonObject();
        bundleResult.add(CONTENT, getPageResourceJSONObject(query));
        return bundleResult;
    }


    /*@RequestMapping 1.0版本，保留
      public void service(@RequestParam("q") String query,
      HttpServletRequest request, HttpServletResponse response) {
      Site site = findSite(request);
      int offset = NumberFormatUtil.parseInt(
      request.getParameter("offset"), 0);
      int limit = NumberFormatUtil.parseInt(request.getParameter("size"), 10);
      JsonObject object = searchPages(site, query, offset, limit);
      JsonUtil.write(response, object);
      }

      @SuppressWarnings("unchecked")
      private JsonObject searchPages(Site site, String query, int offset,int limit) {
      SearchService searchService = site.getSearchService();
      JsonObject result = new JsonObject();

      // 第一次查询时提供匹配的页面数
      if (offset == 0) {
      int totalCount = searchService.getTeamPageCount(site.getId(), query);
      result.put("totalCount", totalCount);
      }
      List<Long> pids = searchService.searchTeamPages(site.getId(), query,offset, limit);
      if (pids != null && pids.size() != 0) {
      JsonArray array = new JsonArray();
      List<PageMeta> pageList = site.getDpageService().fetchDPageBasicListBySphinxId(pids);
      List<String> contentList = site.getDpageService().fetchPageContentListBySphinxId(pids);
      String[] titles = new String[pageList.size()];
      String[] digests = new String[pageList.size()];
      for (int i = 0; i < titles.length; i++) {
      titles[i] = pageList.get(i).getTitle();
      digests[i] = contentList.get(i);
      }
      String[] heightTitles = searchService.highLightTitle(titles, query);
      String[] heightDigests = searchService.highLightDigest(digests,
      query);
      for (int i = 0; i < pageList.size(); i++) {
      JsonObject dpage = getPageJSONObject(heightTitles[i],
      heightDigests[i], pageList.get(i),
      site.getCollectionService().getCollection(pageList.get(i)
      .getCid()));
      array.add(dpage);
      }
      result.put("records", array);
      }
      return result;
      }

      private JsonObject getPageJSONObject(String title, String digest,
      PageMeta temp, DCollection dc) {
      JsonObject dpage = new JsonObject();
      dpage.put("id", temp.getId());
      if (dc.getTitle() != null)
      {
      dpage.put("collectionName", dc.getTitle());
      }
      else
      {
      dpage.put("collectionName", "");
      }
      dpage.put("author", temp.getCreator());
      dpage.put("title", title);
      dpage.put("lastUpdate",
      AoneTimeUtils.formatToDateTime(temp.getModifyTime()));
      dpage.put("digest", digest);
      return dpage;
      }*/

}
