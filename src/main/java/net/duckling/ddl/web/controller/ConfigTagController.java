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

import java.util.Date;
import java.util.List;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import net.duckling.ddl.common.Site;
import net.duckling.ddl.common.VWBContext;
import net.duckling.ddl.constant.ELayout;
import net.duckling.ddl.constant.LynxConstants;
import net.duckling.ddl.service.bundle.IBundleService;
import net.duckling.ddl.service.resource.ITagService;
import net.duckling.ddl.service.resource.Tag;
import net.duckling.ddl.service.resource.TagGroup;
import net.duckling.ddl.service.resource.TagGroupRender;
import net.duckling.ddl.service.url.UrlPatterns;
import net.duckling.ddl.util.JsonUtil;
import net.duckling.ddl.web.interceptor.access.RequirePermission;

import org.apache.log4j.Logger;
import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.ModelAndView;

import cn.cnic.cerc.dlog.client.WebLog;


@Controller
@RequestMapping("/{teamCode}/config/tag")
@RequirePermission(target = "team", operation = "edit")
public class ConfigTagController extends BaseController{
    private static final Logger LOG = Logger.getLogger(ConfigTagController.class);

    @Autowired
    private IBundleService bundleService;

    @Autowired
    private ITagService tagService;

    /**
     * 显示所有tid的tag与tagGroup
     * @param request
     * @return
     */
    @RequestMapping
    public ModelAndView display(HttpServletRequest request){
        VWBContext context = VWBContext.createContext(request, UrlPatterns.T_TEAM_HOME);
        ModelAndView mv = layout(ELayout.LYNX_MAIN, context,"/jsp/aone/tag/configTags.jsp");
        int tid = VWBContext.getCurrentTid();
        mv.addObject("tags",tagService.getTagsNotInGroupForTeam(tid));
        mv.addObject("tagGroups", tagService.getTagGroupsForTeam(tid));
        mv.addObject(LynxConstants.PAGE_TITLE, "管理标签");
        return mv;
    }
    /**
     * 为tid添加一个group
     * @param request
     * @param response
     * @return
     */
    @RequestMapping(params="func=addTagGroup")
    @WebLog(method="addTagGroup",params="newTagGroupTitle")
    public void addTagGroup(HttpServletRequest request,HttpServletResponse response){
        VWBContext context = VWBContext.createContext(request, UrlPatterns.T_TEAM_HOME);
        int tid = VWBContext.getCurrentTid();
        String creator = context.getCurrentUID();
        String groupName = request.getParameter("newTagGroupTitle");
        int i = tagService.getTagGroupTitleCount(tid, groupName);
        if(i!=0){
            errorHandler(response, groupName+"已存在，请重新输入！");
            return ;
        }
        TagGroup tagGroup = new TagGroup();
        tagGroup.setTitle(groupName);
        tagGroup.setTid(tid);
        tagGroup.setCreator(creator);
        int id =tagService.createTagGroup(tagGroup);
        JSONObject json = new JSONObject();
        json.put("id",id);
        json.put("title", tagGroup.getTitle());
        JsonUtil.writeJSONObject(response, json);
    }
    /**
     * 获取tid的所有tagGroup信息
     * @param request
     * @param response
     */
    @RequestMapping(params="func=loadGroup")
    public void loadTagGroup(HttpServletRequest request,HttpServletResponse response){
        List<TagGroupRender> groups = tagService.getTagGroupsForTeam(VWBContext.getCurrentTid());
        JSONArray array = new JSONArray();
        for(TagGroupRender renders : groups){
            TagGroup group = renders.getGroup();
            if(group!=null){
                array.add(JsonUtil.getJSONObject(group));
            }
        }

        JsonUtil.writeJSONObject(response, array);
    }
    /**
     * 更新tag的所有信息
     * @param request
     * @param response
     */
    @RequestMapping(params="func=updateTag")
    @WebLog(method="updateTag",params="existTagId,existGroupId,tagTitle")
    public void updateTag(HttpServletRequest request,HttpServletResponse response){
        String tagId = request.getParameter("existTagId");
        String title = request.getParameter("tagTitle");
        String groupId = request.getParameter("existGroupId");
        Integer tgid = 0;
        try{
            tgid = Integer.parseInt(tagId);
        }catch(NumberFormatException e){
            errorHandler(response, "输入TagId的类型不正确");
            LOG.error("输入TagId为空",e);
            return ;
        }
        Tag tag = tagService.getTag(tgid);
        if(tag == null){
            errorHandler(response, "输入TagId的标签已不存在！");
            return;
        }
        if(title==null||"".equals(title)){
            errorHandler(response, "输入的标签标题不能为空！");
            return;
        }
        tag.setTitle(title);
        Integer group = 0;
        try{
            if(groupId!=null&&!"".equals(groupId)){
                group = Integer.parseInt(groupId);
            }
        }catch(NumberFormatException e){

        }
        tag.setGroupId(group);
        tagService.updateTag(tag);
        bundleService.updateAllResourceWithTagTitle(tag.getId(), title);
    }

    /**
     * 更新TagGroup，只更新title
     * @param request
     * @param response
     */
    @RequestMapping(params="func=updateTagGroup")
    @WebLog(method="updateTagGroup",params="existGroupId,groupTitle")
    public void updateTagGroup(HttpServletRequest request,HttpServletResponse response){
        String groupId = request.getParameter("existGroupId");
        String title = request.getParameter("groupTile");
        if(groupId==null||"".equals(groupId)){
            errorHandler(response, "修改Group时GroupId不能为空");
            return ;
        }
        if(title ==null||"".equals(title)){
            errorHandler(response, "修改Group时Title不能为空");
            return ;
        }
        Integer tagGroupId = 0;
        try{
            tagGroupId = Integer.parseInt(groupId);
        }catch(NumberFormatException e){
            errorHandler(response, "修改Group时Title不能为空");
            return ;
        }
        TagGroup group = tagService.getTagGroupById(tagGroupId);
        if(group==null){
            errorHandler(response, "修改Group时此group已经不存在");
            return ;
        }
        //先获取所有tagGroupId下的tags
        group.setTitle(title);
        tagService.updateTagGroup(tagGroupId, group);

        //TODO
    }

    /**
     * 删除tag的所有信息
     * @param request
     * @param response
     */
    @RequestMapping(params="func=deleteTag")
    public void deleteTag(HttpServletRequest request,HttpServletResponse response){
        String tagId = request.getParameter("existTagId");
        Integer tgid = 0;
        try{
            if(tagId!=null&&!"".equals(tagId)){
                tgid=Integer.parseInt(tagId);
            }else{
                errorHandler(response, "输入TagId为空请重新输入");
                LOG.error("输入TagId为空");
                return ;
            }
        }catch(NumberFormatException e){
            LOG.error("获取的tagID转换成Integer错误"+tagId, e);
        }
        int i = bundleService.removeTagItemsByTgids(new int[]{tgid});
        if(i==-1){
            LOG.warn("输入TagId查询tag为空");
            errorHandler(response, "输入TagId查询tag为空请重新输入!");
            return ;
        }
    }
    /**
     * 删除group其中包括删除tag的所有信息
     * @param request
     * @param response
     */
    @RequestMapping(params="func=deleteTagGroup")
    public void deleteTagGroup(HttpServletRequest request,HttpServletResponse response){
        String requestId = request.getParameter("existTagGroupId");
        String deleteAll = request.getParameter("deleteAll");
        if(deleteAll==null||"".equals(deleteAll)){
            errorHandler(response, "未选择操作类型");
            return;
        }
        Integer groupId = 0;
        try{
            groupId = Integer.parseInt(requestId);
        }catch(NumberFormatException e){
            LOG.error("从页面获取的taggroup值转换成Integer错误"+requestId, e);
            errorHandler(response, "从页面获取的taggroup值转换成Integer错误");
            return;
        }
        if("1".equals(deleteAll)){
            deleteOnlyGroup(groupId);
        }else if("0".equals(deleteAll)){
            bundleService.removeTagGroup(VWBContext.getCurrentTid(), groupId);
        }else{
            errorHandler(response, "选择的操作类型错误！");
            return;
        }
    }

    @SuppressWarnings("unused")
    @RequestMapping(params="func=sortTagGroups")
    public void sortTagGroups(HttpServletRequest request,HttpServletResponse response,
                              @RequestParam("tagGroupIds[]")Integer[] tgids){
        if(tgids!=null && tgids.length!=0){
            tagService.updateTagGroupsOrder(tgids);
        }
        JSONObject json = new JSONObject();
        json.put("status", "success");
        JsonUtil.writeJSONObject(response, json);
    }
    /**
     * 改变一个Tag的groupId
     * @param request
     * @param response
     */
    @SuppressWarnings("unused")
    @RequestMapping(params="func=changeTagFromGroup")
    public void changeTagFromGroup(HttpServletRequest request,HttpServletResponse response,@PathVariable("teamCode") String teamCode){
        VWBContext context = VWBContext.createContext(request, UrlPatterns.T_TEAM_HOME);
        String groupId = request.getParameter("groupId");
        String tagId = request.getParameter("tagId");
        if(tagId==null||"".equals(tagId)){
            errorHandler(response, "在转化组的过程中标签不能为空！");
            return ;
        }
        int t = 0;
        try{
            t = Integer.parseInt(tagId);
        }catch(NumberFormatException e){
            errorHandler(response, "传入的标签"+tagId+"不是一个Integer类型！");
        }
        int g = 0;
        try{
            g = Integer.parseInt(groupId);
        }catch(NumberFormatException e){
            errorHandler(response, "传入的标签组"+groupId+"不是一个Integer类型！");
        }
        Tag tag =tagService.getTag(t);
        tag.setGroupId(g);
        tagService.updateTag(tag);
        //假设以前的代码确保从属关系正常
        //下列代码确保排序关系正常
        tagService.getTagsByGroupId(g,VWBContext.getCurrentTid());
        String[] sortIds=request.getParameter("sortIds").split(",");
        for(int i=0;i<sortIds.length;i++){
            Tag sortTag =tagService.getTag(Integer.parseInt(sortIds[i]));
            sortTag.setSequence(i+1);
            tagService.updateTag(sortTag);
        }


    }

    private void deleteOnlyGroup(Integer groupId) {
        List<Tag> tags = tagService.getTagsByGroupId(groupId, VWBContext.getCurrentTid());
        ITagService  service=tagService;
        for(Tag tag: tags){
            tag.setGroupId(0);
            service.updateTag(tag);
        }
        service.deleteTagGroup(groupId);
    }
    @RequestMapping(params="func=addTag")
    public void addTag(HttpServletRequest request,HttpServletResponse response){
        VWBContext context = VWBContext.createContext(request, UrlPatterns.T_TEAM_HOME);
        boolean isNewTag = Boolean.parseBoolean(request.getParameter("isNewTag"));
        String title = request.getParameter("newTagTitle");
        int i = tagService.getTagTitleCount(VWBContext.getCurrentTid(), title);
        if(i!=0){
            errorHandler(response, title+"已存在，请重新输入名称！");
            return ;
        }
        if(isNewTag){
            putItemIntoNewTag(request,response,context);
        }else{
            putItemIntoExistTag(request,response);
        }
    }

    private void putItemIntoExistTag(HttpServletRequest request,HttpServletResponse response) {
        int existTagId = Integer.parseInt(request.getParameter("existTagId"));
        Tag tag = tagService.getTag(existTagId);
        tagService.increaseCount(existTagId, 1);
        updateResourceTagField(tag);
        getTagAddResponse(response,tag,false);
    }

    private void putItemIntoNewTag(HttpServletRequest request,HttpServletResponse response,VWBContext context) {
        Site site = context.getSite();
        Tag tag = creatTag(request, context, site);
        getTagAddResponse(response, tag,true);
    }

    private Tag creatTag(HttpServletRequest request, VWBContext context,Site site) {
        Integer groupId = 0;
        try{
            String s = request.getParameter("groupId");
            if(s!=null&&!"".equals(s)){
                groupId = Integer.parseInt(request.getParameter("groupId"));
            }
        }catch(NumberFormatException e){
            LOG.error("新增标签转换数据错误", e);
        }
        String title = request.getParameter("newTagTitle");
        Tag tag = tagService.getTag(site.getId(), title);
        if(null != tag){
            return tag;
        }
        tag = new Tag();
        tag.setCreateTime(new Date());
        tag.setCreator(context.getCurrentUID());
        tag.setGroupId(groupId);
        tag.setTid(site.getId());
        tag.setCount(0);
        tag.setTitle(title);
        int tgid = tagService.createTag(tag);
        tag.setId(tgid);
        return tag;
    }

    private void updateResourceTagField(Tag tag) {
        Tag temp = tagService.getTag(tag.getTid(), tag.getTitle());
        if(null!=temp){
            return;
        }
        tagService.createTag(tag);
    }

    private void getTagAddResponse(HttpServletResponse response, Tag tag,Boolean isNewTag) {
        JSONObject json = new JSONObject();
        json.put("status", "success");
        json.put("isNewTag", isNewTag);
        json.put("currTag", JsonUtil.getJSONObject(tag));
        JsonUtil.writeJSONObject(response, json);
    }

    private void errorHandler(HttpServletResponse response,String message){
        JSONObject json = new JSONObject();
        json.put("error", message);
        JsonUtil.writeJSONObject(response, json);
    }

}
