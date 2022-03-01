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

import com.google.gson.Gson;
import com.google.gson.JsonNull;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import net.duckling.ddl.common.Site;
import net.duckling.ddl.common.VWBContext;
import net.duckling.ddl.service.bundle.BundleItem;
import net.duckling.ddl.service.bundle.IBundleService;
import net.duckling.ddl.service.resource.DShortcut;
import net.duckling.ddl.service.resource.IResourceService;
import net.duckling.ddl.service.resource.Resource;
import net.duckling.ddl.service.resource.ShortcutService;
import net.duckling.ddl.service.search.ISearchService;
import net.duckling.ddl.service.url.URLGenerator;
import net.duckling.ddl.service.url.UrlPatterns;
import net.duckling.ddl.util.CommonUtils;
import net.duckling.ddl.util.JsonUtil;
import net.duckling.ddl.util.TeamQuery;
import net.duckling.ddl.web.interceptor.access.RequirePermission;

import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;



@Controller
@RequestMapping("/{teamCode}/resource")
@RequirePermission(target = "team", operation = "view")
public class LynxResourceController {

    private static final int BUNDLE_ITEM_DISPLAY_SIZE = 5;

    @Autowired
    private IResourceService resourceService;
    @Autowired
    private IBundleService bundleService;
    @Autowired
    private ShortcutService shortcutService;
    @Autowired
    private ISearchService searchService;
    @Autowired
    private URLGenerator urlGenerator;

    @RequestMapping(params="func=query")
    public void query(HttpServletRequest request,HttpServletResponse response){
        TeamQuery q = TeamQuery.buildForQuery(request);
        VWBContext context = VWBContext.createContext(request, UrlPatterns.T_TEAM_HOME);
        Site site = context.getSite();
        q.setTid(new int[]{site.getId()});
        JsonObject obj = new JsonObject();
        boolean needBold = checkBoldStyle(q);
        List<Resource> resList = queryResource(site, q);

        obj = generateJSON(context.getCurrentUID(), site, resList, needBold);
        generateShortCutJson(obj, getShortCutResouce(site, q));
        JsonUtil.write(response, obj);
    }

    /**
     * 获取置顶的资源
     * @param site
     * @param query
     * @return
     */
    private List<ShortcutDisplay> getShortCutResouce(Site site,TeamQuery query){
        if("untaged".equals(query.getFilter())){
            return Collections.emptyList();
        }
        List<DShortcut> shortcuts = shortcutService.getCollectionShortcut(query.getTid()[0], query.getTagIds());
        List<ShortcutDisplay> result = new ArrayList<ShortcutDisplay>();
        for(DShortcut ds : shortcuts){
            Resource r = resourceService.getResource(ds.getRid());
            ShortcutDisplay vo = new ShortcutDisplay(ds);
            vo.setResource(r);
            vo.setResourceURL(getResourceURL(r));
            result.add(vo);
        }
        return result;

    }

    private String getResourceURL(Resource resource){
        String url = null;
        int tid = resource.getTid();
        String pagename = String.valueOf(resource.getRid());
        if(resource.isPage()){
            url = urlGenerator.getURL(tid,UrlPatterns.T_VIEW_R, pagename, null);
        }else if(resource.isFile()){
            url = urlGenerator.getURL(tid,UrlPatterns.T_VIEW_R, pagename, null);
        }else{
            url = urlGenerator.getURL(tid,UrlPatterns.T_VIEW_R, pagename, null);
        }
        return url;
    }

    @SuppressWarnings("unchecked")
    private void generateShortCutJson(JsonObject obj,List<ShortcutDisplay> vos){
        JsonArray as = new JsonArray();
        for(ShortcutDisplay vo: vos){
            JsonObject j = new JsonObject();
            j.addProperty("sid", vo.getSid());
            j.addProperty("rid", vo.getRid());
            j.addProperty("resourceTitle", vo.getResourceTitle());
            j.addProperty("choice", vo.isChoice());
            j.addProperty("resourceType",vo.getResourceType());
            j.addProperty("resourceUrl", vo.getResourceURL());
            j.addProperty("color", vo.getColor());
            as.add(j);
        }
        obj.add("shortResouce", as);
    }
    /**
     * 当查询条件选中日期、资源类型、或者包含查询关键词时，组合内结果需要加粗显示
     * @param q 包含查询条件的TeamQuery对象
     * @return true(需要加粗显示) or false(不需要加粗显示)
     */
    private boolean checkBoldStyle(TeamQuery q){
        if(null!=q.getDate() && !"".equals(q.getDate())){
            return true;
        }
        if(null!=q.getType() && !"".equals(q.getType())){
            return true;
        }
        if(null!=q.getKeyword() && !"".equals(q.getKeyword())){
            return true;
        }
        return false;
    }


    /**
     * 根据查询条件从数据库或Sphinx查询数据
     * @param site 站点对象
     * @param q 包含查询条件的TeamQuery对象
     * @return 查询结果 Resource集合
     */
    private List<Resource> queryResource(Site site, TeamQuery q){
        List<Resource> reslist = new ArrayList<Resource>();
        List<Resource> templist = new ArrayList<Resource>();
        int displaySize = 0;
        int size = q.getSize();
        if(q.getOffset() == 0){
            q.setSize(size + TeamQuery.LOADSIZE_ON_EXTREME_CASE);
        }
        do{
            if(q.getKeyword()!=null){//关键词不为空，从Sphinx查，再从数据库取数据
                List<Long> ids = searchService.query(q, TeamQuery.QUERY_FOR_RESOURCE);
                templist = resourceService.getResourcesBySphinxID(ids);
            }else{//从数据库查
                templist = resourceService.getResource(q);
            }
            if(null == templist || templist.size() <= 0){
                break;
            }
            reslist.removeAll(templist);
            reslist.addAll(templist);
            displaySize = getActualDisplaySize(reslist);
            q.setOffset(q.getOffset()+reslist.size());
            if(displaySize == 1){
                q.setSize(q.getSize() + TeamQuery.LOADSIZE_ON_EXTREME_CASE);
            }
        }while(displaySize < size); //当可显示条数小于查询的长度，且数据没有取完
        return reslist;
    }
    /**
     * 将reslist集合中的资源转化成JSON字符串，并根据needBold决定是否需要将Bundle中查询到的资源
     * 加粗显示，即在resource对象中加入titleStyle字符串进行标示
     * @param uid 用户ID
     * @param site 站点对象
     * @param reslist 待转化的资源集合
     * @param needBold 是否加粗显示
     * @return JSON对象
     */
    private JsonObject generateJSON(String uid, Site site, List<Resource> reslist, boolean needBold){
        JsonArray array = new JsonArray();
        //合并属于Bundle的Resource
        Map<Integer, List<Resource>> bundleItemList = new HashMap<Integer, List<Resource>>();
        List<Resource> filterResList = filterResult(site, reslist, bundleItemList);
        if(filterResList!=null){
            for(Resource res:filterResList){
                JsonObject json = new Gson().toJsonTree(res).getAsJsonObject();
                addMarkedCheckedField(uid, res, json);
                addResourceURLField(res,json);
                //加载Bundle的itemList
                if(res.isBundle()){
                    json.add("children", getBundleItemList(
                        site, res.getRid(), bundleItemList.get(res.getRid()),needBold));
                }else{
                    json.add("children", JsonNull.INSTANCE);
                }
                array.add(json);
            }
        }
        JsonObject obj = new JsonObject();
        obj.addProperty("count", reslist.size());
        obj.add("array", array);
        return obj;
    }

    private void addMarkedCheckedField(String uid, Resource res, JsonObject json) {
        if(res==null)
        {
            json.addProperty("isChecked", "unchecked");
        }else{
            if(res.getMarkedUserSet().contains(uid)){
                json.addProperty("isChecked", "checked");
            }else{
                json.addProperty("isChecked", "unchecked");
            }
        }
        json.remove("markedUserSet");
    }

    /**
     * 生成资源的URL
     * @param res 资源对象
     * @param json 保存URL的json对象
     */
    private void addResourceURLField(Resource res,JsonObject json){
        if(res.isBundle()){
            json.addProperty("url",urlGenerator.getURL(res.getTid(),UrlPatterns.T_BUNDLE, res.getRid()+"", null));
        }else if(res.isFile()){
            json.addProperty("url", urlGenerator.getURL(res.getTid(),UrlPatterns.T_FILE, res.getRid()+"", null));
        }else if(res.isPage()){
            json.addProperty("url", urlGenerator.getURL(res.getTid(),UrlPatterns.T_PAGE, res.getRid()+"", null));
        }
    }

    /**
     * 将属于Bundle的资源替换成对应的bundle并去重, 将Bundle中的Resource缓存在bilist中用于前台显示
     * @param site Site对象，访问服务时用到
     * @param resList 待过滤的Resource集合
     * @param bilist 缓存Bundle与Bundle内资源的Map对象
     * @return Resource集合，包含Bundle本身以及不属于任何Bundle的Resource对象
     */
    private List<Resource> filterResult(Site site, List<Resource> resList, Map<Integer, List<Resource>> bilist){
        List<Resource> result = new ArrayList<Resource>();
        Set<Integer> bids = new HashSet<Integer>();
        if(null == resList || resList.size() <= 0){
            return null;
        }
        for(Resource res : resList){
            int bid = res.getBid();
            if(bid == 0){
                if(!res.isBundle()){
                    result.add(res);
                }else if(!bids.contains(res.getRid())){
                    result.add(res);
                    bids.add(res.getRid());
                }
            }else{
                if(bids.contains(bid)){
                    List<Resource> temp = bilist.get(bid);
                    temp = (null != temp)?temp:(new ArrayList<Resource>());
                    temp.add(res);
                    bilist.put(bid, temp);
                }else{
                    Resource r =resourceService.getResource(res.getBid(), res.getTid());
                    if(r==null){
                        continue;
                    }
                    result.add(r);
                    List<Resource> temp = new ArrayList<Resource>();
                    temp.add(res);
                    bilist.put(bid, temp);
                    bids.add(bid);
                }
            }
        }
        return result;
    }

    /**
     * 为减少数据传输量，对于Bundle内的资源，只传输前台需要的数据，包括：
     * rid, bid, itemType, fileType, title
     * @param bid Bundle ID
     * @param resList Bundle内的Resource集合
     * @return JSON数组
     */
    private JsonArray getBundleItemList(Site site, int bid, List<Resource> resList, boolean needBold){
        JsonArray result = new JsonArray();
        int orgSize = 0;
        if(null == resList){
            resList = new ArrayList<Resource>();
        }else{
            orgSize = resList.size();
        }
        List<Resource> fiveResList = getBundleItemLessThanFive(site, bid);
        // 减少数据传输量，只传输必要的字段
        for (int i = 0; i < fiveResList.size(); i++) {
            Resource res = fiveResList.get(i);
            JsonObject obj = new JsonObject();
            obj.addProperty("rid", res.getRid());
            obj.addProperty("bid", bid);
            obj.addProperty("itemType", res.getItemType());
            obj.addProperty("fileType", res.getFileType());
            obj.addProperty("title", res.getTitle());
            obj.addProperty("titleStyle", (i < orgSize && needBold) ? "titleStyle" : "");
            result.add(obj);
        }
        return result;
    }

    private List<Resource> getBundleItemLessThanFive(Site site,int bid){
        int tid = site.getId();
        List<BundleItem> bundleItem = bundleService.getBundleItems(bid, tid);
        if(!CommonUtils.isNull(bundleItem)){
            List<Long> rids = new ArrayList<Long>();
            for(int i=0;i<bundleItem.size()&&i<BUNDLE_ITEM_DISPLAY_SIZE;i++){
                rids.add((long)bundleItem.get(i).getRid());
            }
            return resourceService.getResourcesBySphinxID(rids);
        }else{
            return Collections.emptyList();
        }
    }

    /**
     * resList为Bundle内部资源集合，本方法将至多输出Bundle内5个资源的集合
     * @param site 站点对象
     * @param bid Bundle ID
     * @param resList 资源集合
     * @return
     */
    /*
      private List<Resource> getLessThanFiveResource(Site site, int bid, List<Resource> resList){
      List<Resource> result = new ArrayList<Resource>();
      int tid = site.getId();
      int existResSize = resList.size();
      if(existResSize < BUNDLE_ITEM_DISPLAY_SIZE){
      result.addAll(resList);
      List<Integer> itemRids = bundleService.getRidsOfBundleAndItems(bid, tid);
      Resource bundle = site.getResource(bid, LynxConstants.TYPE_BUNDLE);
      itemRids.remove(new Integer(bundle.getRid()));// Bundle内资源的Rid集合
      int bundleSize = itemRids.size();

      List<Integer> existRids = new ArrayList<Integer>();//resList中已存在的资源rid
      for(Resource res : resList){
      existRids.add(res.getRid());
      }
      itemRids.removeAll(existRids);//此时itemRids为没有被检索到的rid
      List<Long> loadMoreRids = new ArrayList<Long>();//需要从数据库检索的资源rid集合
      int moreRidsSize = 0;
      int itemRidsSize = itemRids.size();
      if(bundleSize > BUNDLE_ITEM_DISPLAY_SIZE){
      //组合内资源数大于前台规定的组合资源显示条数
      moreRidsSize = BUNDLE_ITEM_DISPLAY_SIZE-existResSize;
      }else{
      moreRidsSize = itemRidsSize;
      }
      for(int i=0; i<moreRidsSize; i++){
      loadMoreRids.add((long)itemRids.get(i));
      }
      result.addAll(resourceService.getResourcesBySphinxID(loadMoreRids));
      }else{
      for(int i=0; i<BUNDLE_ITEM_DISPLAY_SIZE; i++){
      result.add(resList.get(i));
      }
      }
      return result;
      }
    */
    /**
     * 判断List中经过合并后实际在页面上可显示的记录条数。
     * Bundle以及Bundle内的所有资源显示时只算一条
     * @param resList 需要进行合并计算的Resource集合
     * @return 最终显示的记录条数
     */
    private int getActualDisplaySize(List<Resource> resList){
        if(null == resList || resList.size() <= 0){
            return 0;
        }
        int bid = 0;
        int itemId = 0;
        Set<Integer> bids = new HashSet<Integer>(); //Bundle数
        Set<Integer> itemIds = new HashSet<Integer>(); //Page和File数
        for(Resource res : resList){
            bid = res.getBid();
            if(bid == 0){
                itemId = res.getRid();
                if(!res.isBundle()){
                    itemIds.add(itemId);
                }else{
                    bids.add(itemId);
                }
            }else{
                bids.add(bid);
            }
        }
        return bids.size()+itemIds.size();
    }
}
