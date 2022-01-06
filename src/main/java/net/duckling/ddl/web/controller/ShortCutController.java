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

import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import net.duckling.ddl.common.Site;
import net.duckling.ddl.common.VWBContext;
import net.duckling.ddl.constant.ELayout;
import net.duckling.ddl.constant.LynxConstants;
import net.duckling.ddl.service.resource.DShortcut;
import net.duckling.ddl.service.resource.IResourceService;
import net.duckling.ddl.service.resource.ITagService;
import net.duckling.ddl.service.resource.Resource;
import net.duckling.ddl.service.resource.ShortcutService;
import net.duckling.ddl.service.resource.Tag;
import net.duckling.ddl.service.search.ISearchService;
import net.duckling.ddl.service.url.UrlPatterns;
import net.duckling.ddl.util.JsonUtil;
import net.duckling.ddl.util.TeamQuery;
import net.duckling.ddl.web.interceptor.access.RequirePermission;

import org.apache.commons.lang.StringUtils;
import org.apache.log4j.Logger;
import org.json.JSONArray;
import org.json.simple.JSONObject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.servlet.ModelAndView;

/**
 * 页面置顶区的控制
 *
 * @author zhonghui
 *
 */
@Controller
@RequestMapping("/{teamCode}/configShortCut")
@RequirePermission(target = "team", operation = "edit")
public class ShortCutController extends BaseController {

    private static final Logger LOG = Logger
            .getLogger(ShortCutController.class);

    @Autowired
    private IResourceService resourceService;
    @Autowired
    private ITagService tagService;
    @Autowired
    private ShortcutService shortcutService;
    @Autowired
    private ISearchService searchService;

    /**
     * 加载默认页面
     *
     * @param request
     * @param response
     * @return
     */
    @RequestMapping()
    public ModelAndView init(HttpServletRequest request,
                             HttpServletResponse response) {
        VWBContext context = VWBContext.createContext(request,
                                                      UrlPatterns.T_TEAM_HOME);
        Site site = context.getSite();
        int tid = site.getId();
        int tgid = getTgid(request);
        List<DShortcut> cuts = shortcutService.getCollectionShortcut(tid, tgid);
        TeamQuery q = getTeamQuery(tid, tgid);
        List<Resource> orcList = resourceService.getResource(q);
        List<Resource> resList = filterResult(site, orcList);
        ModelAndView model = layout(ELayout.LYNX_MAIN, context,
                                    "/jsp/aone/collection/configShortcuts.jsp");
        model.addObject("shortcuts", ShortcutToVo(cuts, site));
        model.addObject("resources", getAllTagShortVo(cuts, resList, site));
        model.addObject("teamId", tid);
        model.addObject("tgid", tgid);
        model.addObject("tagName", getTagName(tgid, site));
        model.addObject(LynxConstants.PAGE_TITLE, "管理重点推荐");
        return model;
    }

    /**
     * 获取当前tagId的显示Title
     *
     * @param tagId
     * @param site
     * @return
     */
    private String getTagName(int tagId, Site site) {
        if (tagId <= 0) {
            return "所有文档";
        }
        Tag t = tagService.getTag(tagId);
        if (t != null) {
            return t.getTitle();
        }
        return "未知标签";

    }

    private TeamQuery getTeamQuery(int tid, int tgid) {
        TeamQuery result = new TeamQuery();
        result.setTid(new int[] { tid });
        if (tgid > 0) {
            result.setTagIds(new int[] { tgid });
        }
        result.setSize(500);
        return result;
    }

    /**
     * 将属于Bundle的资源替换成对应的bundle并去重, 将Bundle中的Resource缓存在bilist中用于前台显示
     *
     * @param site
     *            Site对象，访问服务时用到
     * @param resList
     *            待过滤的Resource集合
     * @param bilist
     *            缓存Bundle与Bundle内资源的Map对象
     * @return Resource集合，包含Bundle本身以及不属于任何Bundle的Resource对象
     */
    private List<Resource> filterResult(Site site, List<Resource> resList) {
        Map<Integer, List<Resource>> bilist = new HashMap<Integer, List<Resource>>();
        List<Resource> result = new ArrayList<Resource>();
        Set<Integer> bids = new HashSet<Integer>();
        if (null == resList || resList.size() <= 0) {
            return result;
        }
        for (Resource res : resList) {
            int bid = res.getBid();
            if (bid == 0) {
                if (!res.isBundle()) {
                    result.add(res);
                } else if (!bids.contains(res.getRid())) {
                    result.add(res);
                    bids.add(res.getRid());
                }
            } else {
                if (bids.contains(bid)) {
                    List<Resource> temp = bilist.get(bid);
                    temp = (null != temp) ? temp : (new ArrayList<Resource>());
                    temp.add(res);
                    bilist.put(bid, temp);
                } else {
                    result.add(resourceService.getResource(res.getBid(), res.getTid()));
                    List<Resource> temp = new ArrayList<Resource>();
                    temp.add(res);
                    bilist.put(bid, temp);
                    bids.add(bid);
                }
            }
        }
        return result;
    }

    private List<ShortcutDisplay> getAllTagShortVo(List<DShortcut> s,
                                                   List<Resource> resList, Site site) {
        List<ShortcutDisplay> result = new ArrayList<ShortcutDisplay>();
        Set<Integer> choiceRid = getShortcutRid(s);
        for (Resource r : resList) {
            ShortcutDisplay v = new ShortcutDisplay(r);
            if (choiceRid.contains(r.getRid())) {
                v.setChoice(true);
            }
            v.setResourceURL(getResourceURL(site, r));
            result.add(v);
        }
        return result;
    }

    private String getResourceURL(Site site, Resource resource) {
        String url = null;
        String pagename = String.valueOf(resource.getRid());
        if (resource.isPage()) {
            url = site.getURL(UrlPatterns.T_PAGE, pagename, null);
        } else if (resource.isFile()) {
            url = site.getURL(UrlPatterns.T_FILE, pagename, null);
        } else {
            url = site.getURL(UrlPatterns.T_BUNDLE, pagename, null);
        }
        return url;
    }

    /**
     * 提取List中的resorceid
     *
     * @param s
     * @return
     */
    private Set<Integer> getShortcutRid(List<DShortcut> s) {
        Set<Integer> result = new HashSet<Integer>();
        for (DShortcut d : s) {
            result.add(d.getRid());
        }
        return result;
    }

    private List<ShortcutDisplay> ShortcutToVo(List<DShortcut> s, Site site) {
        List<ShortcutDisplay> result = new ArrayList<ShortcutDisplay>();
        for (DShortcut d : s) {
            Resource r = resourceService.getResource(d.getRid());
            ShortcutDisplay vo = new ShortcutDisplay(d);
            vo.setResource(r);
            vo.setResourceURL(getResourceURL(site, r));
            result.add(vo);
        }
        return result;
    }

    private int getTgid(HttpServletRequest request) {
        String tgid = request.getParameter("tgid");
        if (StringUtils.isEmpty(tgid)) {
            return 0;
        }
        return Integer.valueOf(tgid);
    }

    @RequestMapping(params = "func=addShortcut")
    public void addShortCut(HttpServletRequest request,
                            HttpServletResponse response) {
        VWBContext context = VWBContext.createContext(request,
                                                      UrlPatterns.T_TEAM_HOME);
        Site site = context.getSite();
        int tid = site.getId();
        int tgid = getTgid(request);
        int rid = Integer.valueOf(request.getParameter("rid"));
        DShortcut ds = new DShortcut();
        ds.setTgid(tgid);
        ds.setTid(tid);
        ds.setCreatorTime(new Date());
        ds.setRid(rid);
        ds.setColor(request.getParameter("color"));
        ds.setCreator(context.getCurrentUID());
        shortcutService.addShortcut(ds);
        JSONObject json = new JSONObject();
        json.put("sid", ds.getId());
        Resource r = resourceService.getResource(ds.getRid());
        json.put("resourceType", r.getItemType());
        json.put("resourceFileType", r.getFileType());
        JsonUtil.writeJSONObject(response, json);
    }

    @RequestMapping(params = "func=delShortcut")
    public void delShortcut(HttpServletRequest request,
                            HttpServletResponse response) {
        int sid = Integer.valueOf(request.getParameter("sid"));
        boolean result = shortcutService.deleteShortCut(sid);
        JSONObject json = new JSONObject();
        json.put("result", result);
        JsonUtil.writeJSONObject(response, json);
    }

    @RequestMapping(params = "func=sortShortcut")
    public void sortShortcut(HttpServletRequest request,
                             HttpServletResponse response) {
        String[] sorts = request.getParameter("sortIds").split(",");
        List<Integer> ids = new ArrayList<Integer>();
        for (String id : sorts) {
            try {
                ids.add(Integer.parseInt(id));
            } catch (NumberFormatException e) {
                LOG.warn("排序过程中出现ID值异常：" + request.getParameter("sortIds"), e);
            }
        }
        boolean result = shortcutService.updateShortSequece(ids);
        JSONObject json = new JSONObject();
        if (!result) {
            json.put("error", "排序错误");
        }
        JsonUtil.writeJSONObject(response, json);
    }

    @RequestMapping(params = "func=changeColor")
    public void changeColor(HttpServletRequest request,
                            HttpServletResponse response) {
        int sid = Integer.valueOf(request.getParameter("sid"));
        DShortcut d = shortcutService.getDSortcutById(sid);
        JSONObject json = new JSONObject();
        if (d != null) {
            d.setColor(request.getParameter("color"));
            json.put("result", shortcutService.updateShortcuts(d));
        } else {
            json.put("result", false);
        }
        JsonUtil.writeJSONObject(response, json);
    }

    @RequestMapping(params = "func=searchResult")
    public void searchByKeyword(HttpServletRequest request,
                                HttpServletResponse response) {
        VWBContext context = VWBContext.createContext(request,
                                                      UrlPatterns.T_TEAM_HOME);
        Site site = context.getSite();
        int tid = site.getId();
        String keyword = request.getParameter("keyword");
        TeamQuery query = new TeamQuery();
        query.setTid(new int[] { tid });
        query.setSize(500);
        query.setKeyword(keyword);
        int tgid = getTgid(request);
        List<Long> ids = searchService.query(query,
                                             TeamQuery.QUERY_FOR_RESOURCE);
        List<Resource> oriList = resourceService.getResourcesBySphinxID(ids);
        List<Resource> resList = filterResult(site, oriList);
        List<DShortcut> choiceShort = shortcutService.getCollectionShortcut(
            tid, tgid);
        List<ShortcutDisplay> result = getAllTagShortVo(choiceShort, resList,
                                                        site);
        writeVoToJson(result, response);
    }

    /**
     * 重载当前tgid下的资源
     *
     * @param request
     * @param response
     */
    @RequestMapping(params = "func=reloadShortcutPan")
    public void reloadShortcutPan(HttpServletRequest request,
                                  HttpServletResponse response) {
        VWBContext context = VWBContext.createContext(request,
                                                      UrlPatterns.T_TEAM_HOME);
        Site site = context.getSite();
        int tid = site.getId();
        int tgid = getTgid(request);
        TeamQuery q = getTeamQuery(tid, tgid);
        List<Resource> oriList = resourceService.getResource(q);
        List<Resource> resList = filterResult(site, oriList);
        List<DShortcut> choiceShort = shortcutService.getCollectionShortcut(
            tid, tgid);
        List<ShortcutDisplay> result = getAllTagShortVo(choiceShort, resList,
                                                        site);
        writeVoToJson(result, response);
    }

    private void writeVoToJson(List<ShortcutDisplay> result,
                               HttpServletResponse response) {
        JSONObject json = new JSONObject();
        JSONArray re = new JSONArray();
        for (ShortcutDisplay vo : result) {
            JSONObject j = new JSONObject();
            j.put("sid", vo.getSid());
            j.put("rid", vo.getRid());
            j.put("resourceTitle", vo.getResourceTitle());
            j.put("resourceType", vo.getResourceType());
            j.put("choice", vo.isChoice());
            j.put("resourceUrl", vo.getResourceURL());
            j.put("resourceFileType", vo.getResourceFileType());
            re.put(j);
        }
        json.put("results", re);
        JsonUtil.writeJSONObject(response, json);
    }
}
