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
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import net.duckling.ddl.common.VWBContext;
import net.duckling.ddl.common.VWBSession;
import net.duckling.ddl.constant.LynxConstants;
import net.duckling.ddl.service.authority.AuthorityService;
import net.duckling.ddl.service.bundle.BundleItem;
import net.duckling.ddl.service.bundle.IBundleService;
import net.duckling.ddl.service.export.ExportService;
import net.duckling.ddl.service.resource.IResourceService;
import net.duckling.ddl.service.resource.ITagService;
import net.duckling.ddl.service.resource.PageLock;
import net.duckling.ddl.service.resource.PageLockService;
import net.duckling.ddl.service.resource.Resource;
import net.duckling.ddl.service.resource.ResourceOperateService;
import net.duckling.ddl.service.resource.Tag;
import net.duckling.ddl.service.url.URLGenerator;
import net.duckling.ddl.service.url.UrlPatterns;
import net.duckling.ddl.util.JsonUtil;
import net.duckling.ddl.util.JSONMap;
import net.duckling.ddl.util.PinyinUtil;
import net.duckling.ddl.web.interceptor.access.OnDeny;
import net.duckling.ddl.web.interceptor.access.RequirePermission;

import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.ModelAndView;
import org.springframework.web.servlet.view.RedirectView;

//import cn.cnic.cerc.dlog.client.WebLog;
import cn.cnic.esac.clb.util.HttpStatus;

@Controller
@RequestMapping("/{teamCode}/tag")
@RequirePermission(target = "team", operation = "view")
public class TagController extends BaseController {

    @Autowired
    private AuthorityService authorityService;
    @Autowired
    private IBundleService bundleService;
    @Autowired
    private ExportService exportService;
    @Autowired
    private PageLockService pageLockService;
    @Autowired
    private ResourceOperateService resourceOperateService;
    @Autowired
    private IResourceService resourceService;
    @Autowired
    private ITagService tagService;
    @Autowired
    private URLGenerator generator;

    private void AddJsonPut(JsonArray j, Resource r){
        JSONMap map = new JSONMap();
        map.put("rid", r.getRid());
        map.put("itemType", r.getItemType());
        map.put("title", r.getTitle());
        map.put("fileType", r.getFileType());
        j.add(new Gson().toJsonTree(map));
    }

    /*private JsonArray addTagItemAndUpdateResourceTagMap(Tag tag, int[] rids){
      return ;
      }*/

    private Tag creatTag(String title, String uid,int tid) {
        Tag tag = tagService.getTag(tid, title);
        if(null != tag){
            return tag;
        }
        tag = new Tag();
        tag.setCreateTime(new Date());
        tag.setCreator(uid);
        tag.setGroupId(0);
        tag.setTid(tid);
        tag.setCount(0);
        tag.setTitle(title);
        int tgid = tagService.createTag(tag);
        tag.setId(tgid);
        return tag;
    }
    private JsonArray getJSONArrayFromTagList(List<Tag> list){
        if(null==list || list.isEmpty()){
            return new JsonArray();
        }
        JsonArray array = new JsonArray();
        for(Tag tag : list){
            JsonObject obj = new JsonObject();
            obj.addProperty("id", tag.getId());
            obj.addProperty("name", tag.getTitle());
            array.add(obj);
        }
        return array;
    }

    private JsonArray getTagAddResponse(Tag tag,int[] rids,Boolean isNewTag) {
        JsonArray array = new JsonArray();
        int size = rids.length;
        for(int i=0; i<size; i++){
            JsonObject json = new JsonObject();
            json.addProperty("isNewTag", isNewTag);
            json.addProperty("id", tag.getId());
            json.addProperty("title", tag.getTitle());
            json.addProperty("count", tag.getCount());
            json.addProperty("item_key", rids[i]);
            array.add(json);
        }
        return array;
    }

    private JsonArray getTagCount(List<Resource> resList, int tid){
        JsonArray array = new JsonArray();
        if(null==resList || resList.isEmpty()){
            return array;
        }
        Set<Integer> tagIds = new HashSet<Integer>();
        //获取resList中所有的tagId
        for(Resource res: resList){
            Map<Integer, String> tagMap = res.getTagMap();
            if(null == tagMap || tagMap.isEmpty()){
                continue;
            }
            for(Map.Entry<Integer, String> entry : tagMap.entrySet()){
                tagIds.add(entry.getKey());
            }
        }
        Iterator<Integer> itr = tagIds.iterator();
        while(itr.hasNext()){
            int tagid = itr.next();
            JsonObject obj = new JsonObject();
            obj.addProperty("id",tagid);
            obj.addProperty("value", tagService.getTagCount(tid, tagid));
            array.add(obj);
        }
        return array;
    }

    private boolean isChineseCharacter(String chars) {
        if(null == chars || chars.length() == 0) {
            return false;
        }
        char ch = chars.charAt(0);
        return (ch >= PinyinUtil.CH_START && ch <= PinyinUtil.CH_END);
    }

    private JsonArray putItemIntoExistTag(int[] rids,int existTagId) {
        Tag tag = tagService.getTag(existTagId);
        List<Integer> tempRids = new ArrayList<Integer>();
        int size = rids.length;
        for(int i=0; i<size; i++){
            boolean hasTag = tagService.isItemHasTag(rids[i],existTagId);
            if(!hasTag){
                tempRids.add(rids[i]);
            }
        }
        int tempSize = tempRids.size();
        int[] noTagRids = new int[tempSize];
        for(int i=0; i<tempSize; i++){
            noTagRids[i]=tempRids.get(i);
        }
        bundleService.addItems(tag.getTid(), existTagId, noTagRids);
        updateResourceTagField(tag, rids);
        return getTagAddResponse(tag, noTagRids,false);
    }


    private JsonObject putItemIntoNewTag(int rid,String newTagTitle,String uid,int tid) {
        Tag tag = creatTag(newTagTitle, uid, tid);
        bundleService.addItems(tag.getTid(), tag.getId(), new int[]{rid});
        updateResourceTagField(tag, new int[]{rid});
        JsonArray array = getTagAddResponse(tag, new int[]{rid}, true);
        return (JsonObject)array.get(0);
    }

    private void removeResource(Resource res, int tid, String uid,
                                ResourceOperateService ps, IBundleService bs,
                                ITagService ts, IResourceService rs,VWBContext context){
        String itemType = res.getItemType();
        int rid = res.getRid();
        //删除a1_tag, a1_tag_item中的记录
        ts.removeAllTagItemsOfRid(tid, rid);

        if(LynxConstants.TYPE_FILE.equals(itemType)){
            //删除a1_file, a1_file_version, a1_resource, a1_bundle, a1_bundle_item中的记录
            //ps.recoverFile(res.getRid(), tid);
            //删除a1_grid_item中的记录
        }else if(LynxConstants.TYPE_PAGE.equals(itemType)){
            //删除bundleItem时会更新resource中的bid,所以此操作必须在前
            if(res.getBid()!=0){
                bs.removeBundleItems(res.getBid(), tid, new int[]{rid});
            }
            //删除a1_page, a1_page_version, a1_resource中的记录
            ps.deleteResource( tid,res.getRid(),"");
        }else{
            List<BundleItem> items = bs.getBundleItems(rid, tid);
            if(null!=items && !items.isEmpty()){
                List<Long> rids = new ArrayList<Long>();
                for(BundleItem bi: items){
                    rids.add((long)bi.getRid());
                }
                List<Resource> tempList = rs.getResourcesBySphinxID(rids);
                for(Resource resource: tempList){
                    removeResource(resource,tid,uid,ps,bs,ts,rs,context);
                }
            }
            //删除a1_bundle, a1_bundle_item, a1_resource中的记录
            bs.disbandBundle(rid, tid);
        }
        updateTagCount(res, ts);
    }

    private List<Long> transferIntToLong(List<Integer> ids){
        List<Long> result = new ArrayList<Long>();
        for(Integer i : ids){
            result.add(new Long(i));
        }
        return result;
    }

    private void updateResourceTagField(Tag tag,int[] rids) {
        IResourceService rs = resourceService;
        int size = rids.length;
        for(int i=0; i<size; i++){
            Resource res = rs.getResource(rids[i]);
            Map<Integer,String> tagMap = res.getTagMap();
            if(tagMap==null || tagMap.isEmpty()){
                tagMap = new HashMap<Integer,String>();
            }
            tagMap.put(tag.getId(), tag.getTitle());
            res.setTagMap(tagMap);
            rs.updateResourceTagMap(Arrays.asList(new Resource[]{res}));
        }
    }

    private void updateResourceTagField(List<Tag> tags,int[] rids) {
        Map<Integer,String> tagMapAdd = new HashMap<Integer,String>();
        for(Tag tag:tags){
            tagMapAdd.put(tag.getId(), tag.getTitle());
        }
        List<Resource> updateList=new ArrayList<Resource>();
        for(int i=0; i<rids.length; i++){
            Resource res = resourceService.getResource(rids[i]);
            Map<Integer,String> tagMap = res.getTagMap();
            if(tagMap==null || tagMap.isEmpty()){
                tagMap = new HashMap<Integer,String>();
            }
            tagMap.putAll(tagMapAdd);
            res.setTagMap(tagMap);
            updateList.add(res);
        }
        resourceService.updateResourceTagMap(updateList);
    }

    private void updateTagCount(Resource res, ITagService ts){
        Map<Integer, String> tagMap = res.getTagMap();
        if(null == tagMap || tagMap.isEmpty()){
            return;
        }
        int tid = res.getTid();
        for(Map.Entry<Integer, String> entry : tagMap.entrySet()){
            ts.updateTagCount(tid, entry.getKey());
        }
    }

    /**
     * 校验输入的resource并将结果加入JSON中
     * @param context
     * @param resList
     * @param obj
     */
    private void validateDeleteAuth(VWBContext context,List<Resource> resList,JsonObject obj) {
        String u = context.getCurrentUID();
        boolean result = true;
        JsonArray haveAuth = new JsonArray();
        JsonArray noAuth = new JsonArray();
        if(authorityService.teamAccessability(VWBContext.getCurrentTid(),
                                              VWBSession.findSession(context.getHttpRequest()), AuthorityService.ADMIN)){
            for(Resource r : resList){
                AddJsonPut(haveAuth, r);
            }
            obj.add("auth", haveAuth);
            obj.addProperty("authValidate", true);
            return ;
        }else{
            for( Iterator<Resource> its = resList.iterator() ; its.hasNext();){
                Resource r = its.next();
                boolean b = true;
                if(r.isBundle()){
                    List<Integer> rIds=bundleService.getRidsOfBundleAndItems(r.getRid(), r.getTid());
                    List<Resource> resources = resourceService.getResourcesBySphinxID(transferIntToLong(rIds));
                    for(Resource rs : resources){
                        if(!u.equals(rs.getCreator())){
                            b = false;
                            break ;
                        }
                    }
                }else{
                    if(!u.equals(r.getCreator())){
                        b = false;
                    }
                }
                if(!b){
                    AddJsonPut(noAuth, r);
                    its.remove();
                    result = false;
                }else{
                    AddJsonPut(haveAuth, r);
                }
            }
        }
        if(result){
            obj.add("auth", haveAuth);
            obj.addProperty("authValidate", true);
        }else{
            obj.addProperty("status", false);
            obj.addProperty("authValidate", false);
            obj.add("auth", haveAuth);
            obj.add("noAuth", noAuth);
        }
    }

    @RequestMapping(params="func=add")
    @RequirePermission(target = "team", operation = "edit")
    public void addTag(HttpServletRequest request,HttpServletResponse response,
                       @RequestParam("isNewTag")boolean isNewTag,@RequestParam("rid")Integer rid){
        VWBContext context = VWBContext.createContext(request, UrlPatterns.T_TEAM_HOME);
        int tid = VWBContext.getCurrentTid();
        JsonObject json = null;
        if(isNewTag){
            String newTagTitle = request.getParameter("newTagTitle");
            json = putItemIntoNewTag(rid,newTagTitle,context.getCurrentUID(),tid);
        }else{
            int existTagId = Integer.parseInt(request.getParameter("existTagId"));
            JsonArray array = putItemIntoExistTag(new int[]{rid},existTagId);
            json = (JsonObject)array.get(0);
        }
        JsonUtil.write(response, json);
    }

    @RequestMapping(params="func=batchAdd")
    @RequirePermission(target = "team", operation = "edit")
    //@WebLog(method="addBatchTags",params="rids[],newTags[],existTags[]")
    public void batchAdd(HttpServletRequest request,HttpServletResponse response,
                         @RequestParam("rids[]")int[] rids,
                         @RequestParam(required=false,value="newTags[]")String[] newTags,
                         @RequestParam(required=false,value="existTags[]")int[] existTags){
        VWBContext context = VWBContext.createContext(request, UrlPatterns.T_TEAM_HOME);
        int tid = VWBContext.getCurrentTid();
        String uid = context.getCurrentUID();
        JsonArray array = new JsonArray();
        List<Long> ridsList=new ArrayList<Long>();
        for(long rid:rids){
            ridsList.add(rid);
        }
        if(newTags!=null){
            List<Integer> tagIds = createNewTag(newTags, tid, uid);
            List<Tag> tags = updateTagItemAndResource(rids, tid, ridsList,
                                                      tagIds);
            for(Tag tag:tags){
                array.addAll(getTagAddResponse(tag, rids,true));
            }
        }
        if(existTags!=null){
            List<Integer> existTagIds=new ArrayList<Integer>();
            for(int tempId:existTags){
                existTagIds.add(tempId);
            }
            List<Tag> tags = updateTagItemAndResource(rids, tid, ridsList,
                                                      existTagIds);
            for(Tag tag:tags){
                array.addAll(getTagAddResponse(tag, rids,false));
            }
        }
        JsonUtil.write(response, array);
    }


    private List<Tag> updateTagItemAndResource(int[] rids, int tid,
                                               List<Long> ridsList, List<Integer> existTagIds) {
        tagService.addItems(tid, existTagIds, ridsList);
        List<Tag>tags=tagService.getTags(existTagIds);
        updateResourceTagField(tags, rids);
        return tags;
    }


    private List<Integer> createNewTag(String[] newTags, int tid, String uid) {
        List<Integer> tagIds=new ArrayList<Integer>();
        for(String title:newTags){
            Tag newTag=creatTag(title, uid, tid);
            tagIds.add(newTag.getId());
        }
        return tagIds;
    }

    @RequestMapping(params="func=deleteResource")
    @RequirePermission(target="team", operation="edit")
    public void deleteResource(HttpServletRequest request, HttpServletResponse response,
                               @RequestParam("rids[]") int[] rids){
        VWBContext context = VWBContext.createContext(request, UrlPatterns.T_TEAM_HOME);
        List<PageLock> locks = PageLockValidateUtils.getPageLockFromResource(rids, resourceService,
                                                                             pageLockService,bundleService);
        if(!locks.isEmpty()){
            PageLockValidateUtils.pageLockMessage(locks, response, resourceOperateService);
            return ;
        }
        List<Long> ridsList = new ArrayList<Long>();
        for(int i=0; i<rids.length; i++){
            ridsList.add((long)rids[i]);
        }
        List<Resource> resList = resourceService.getResourcesBySphinxID(ridsList);
        JsonObject obj = new JsonObject();
        validateDeleteAuth(context,resList,obj);
        int tid = VWBContext.getCurrentTid();
        String uid = context.getCurrentUID();
        if(!resList.isEmpty()){
            for(Resource res : resList){
                removeResource(res,tid, uid, resourceOperateService, bundleService, tagService, resourceService,context);
            }
            JsonArray tagCount = getTagCount(resList, tid);
            obj.add("tagCount", tagCount);
        }
        obj.addProperty("status", true);
        JsonUtil.write(response, obj);
    }

    @RequestMapping
    //@WebLog(method="showResourceList")
    public ModelAndView display(HttpServletRequest request){
        return new ModelAndView(new RedirectView(generator.getAbsoluteURL(VWBContext.getCurrentTid(), UrlPatterns.T_LIST, null, null)));
        //      VWBContext context = VWBContext.createContext(request, UrlPatterns.T_TEAM_HOME);
        //      ModelAndView mv = layout(ELayout.LYNX_MAIN, context,"/jsp/aone/tag/tagItems.jsp");
        //      int tid = VWBContext.getCurrentTid();
        //      mv.addObject("tags",tagService.getTagsNotInGroupForTeam(tid));
        //      mv.addObject("tagGroups", tagService.getTagGroupsForTeam(tid));
        //      return mv;
    }

    @RequestMapping(params="func=download")
    public void download(HttpServletRequest request, HttpServletResponse response,
                         @PathVariable("teamCode") String tname, @RequestParam("format") String format,
                         @RequestParam("rids[]") int[] rids) {
        VWBContext context = VWBContext.createContext(request, UrlPatterns.T_TEAM_HOME);
        exportService.download(context, tname, rids, response, format);
    }

    @RequestMapping(params="func=loadTeamTags")
    @RequirePermission(target = "team", operation = "view")
    public void loadTeamTags(HttpServletRequest request,HttpServletResponse response){
        int tid = VWBContext.getCurrentTid();
        String type = request.getParameter("type");
        if(null==type || "".equals(type) || !"nogroup".equals(type)){
            JsonObject json = getTeamGroupedTagJSON(tid);
            JsonUtil.write(response, json);
        }else{
            String searchParam = request.getParameter("searchParam");
            if(null == searchParam || "".equals(searchParam)){
                JsonUtil.write(response, new JsonObject[]{});
                return;
            }
            List<Tag> allTags = new ArrayList<Tag>();
            if(isChineseCharacter(searchParam)){
                allTags = tagService.getTagsByName(tid, searchParam);
            }else{
                allTags = tagService.getTagsByPinyin(tid, searchParam);
            }
            JsonArray array = getJSONArrayFromTagList(allTags);
            JsonUtil.write(response, array);
        }
    }


    private JsonObject getTeamGroupedTagJSON(int tid) {
        JsonObject json = new JsonObject();
        Gson gson = new Gson();
        List<Tag> freeTags = tagService.getTagsNotInGroupForTeam(tid);
        Map<String,List<Tag>> tagGroupMap = tagService.getTagGroupMap(tid);
        json.add("freeTags", gson.toJsonTree(freeTags).getAsJsonArray());
        JsonArray groupArray = new JsonArray();
        for(Map.Entry<String, List<Tag>> entry : tagGroupMap.entrySet()){
            JsonObject groupJson = new JsonObject();
            groupJson.addProperty("name", entry.getKey());
            groupJson.add("tags", gson.toJsonTree(entry.getValue()).getAsJsonArray());
            groupArray.add(groupJson);
        }
        json.add("groupMap",groupArray);
        return json;
    }

    @OnDeny({"batchAdd","addTag","remove"})
    public void onDeny(String methodName, HttpServletRequest request, HttpServletResponse response){
        JsonObject obj = new JsonObject();
        obj.addProperty("status", "error");
        obj.addProperty("result", "无权进行此操作！");
        response.setStatus(HttpStatus.AUTH_FAILED);
        JsonUtil.write(response, obj);
    }

    @SuppressWarnings("unused")
    @RequestMapping(params="func=remove")
    @RequirePermission(target = "team", operation = "edit")
    //@WebLog(method="removeTagFromItem")
    public void remove(HttpServletRequest request,HttpServletResponse response,
                       @RequestParam("rid[]")int[] rids,@RequestParam("tagId")Integer tagId){
        JsonArray array = new JsonArray();
        for(int i=0; i<rids.length; i++){
            JsonObject obj = new JsonObject();
            obj.addProperty("rid", rids[i]);
            bundleService.removeItem(rids[i],tagId);
            array.add(obj);
        }
        JsonObject json = new JsonObject();
        json.add("rids", array);
        json.addProperty("tagId", tagId);
        json.addProperty("status", "success");
        JsonUtil.write(response, json);
    }


    @RequestMapping(params="func=refreshTagCount")
    public void refreshTagCount(HttpServletRequest request,HttpServletResponse response){
        int tid = VWBContext.getCurrentTid();

        tagService.getTagsNotInGroupForTeam(tid);
        List<Tag> tags=tagService.getTagsForTeam(tid);
        JsonArray j =new JsonArray();
        if(tags!=null&&!tags.isEmpty()){
            for(Tag tag:tags){
                JsonObject tagInfo=new JsonObject();
                tagInfo.addProperty("tag_id", tag.getId());
                tagInfo.addProperty("count",tag.getCount());
                j.add(tagInfo);
            }
        }
        JsonUtil.write(response, j);
    }

}
