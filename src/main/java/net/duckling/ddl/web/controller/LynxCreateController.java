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
import java.util.List;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import net.duckling.ddl.common.Site;
import net.duckling.ddl.common.VWBContainerImpl;
import net.duckling.ddl.common.VWBContext;
import net.duckling.ddl.common.VWBSession;
import net.duckling.ddl.constant.ELayout;
import net.duckling.ddl.constant.LynxConstants;
import net.duckling.ddl.constant.ParamConstants;
import net.duckling.ddl.service.param.IParamService;
import net.duckling.ddl.service.param.Param;
import net.duckling.ddl.service.resource.FolderPathService;
import net.duckling.ddl.service.resource.IResourceService;
import net.duckling.ddl.service.resource.ITagService;
import net.duckling.ddl.service.resource.Resource;
import net.duckling.ddl.service.resource.ResourceOperateService;
import net.duckling.ddl.service.resource.Tag;
import net.duckling.ddl.service.resource.TagGroup;
import net.duckling.ddl.service.resource.TeamSpaceSizeService;
import net.duckling.ddl.service.url.URLGenerator;
import net.duckling.ddl.service.url.UrlPatterns;
import net.duckling.ddl.service.user.AoneUserService;
import net.duckling.ddl.util.CommonUtil;
import net.duckling.ddl.util.JsonUtil;
import net.duckling.ddl.web.interceptor.access.RequirePermission;

import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.ModelAndView;
import org.springframework.web.servlet.view.RedirectView;

import cn.cnic.cerc.dlog.client.WebLog;


@Controller
@RequestMapping("/{teamCode}/quick")
@RequirePermission(target="team",operation="edit")
public class LynxCreateController extends BaseController{

    //  private static final Logger LOG = Logger.getLogger(LynxCreateController.class);

    @Autowired
    private IResourceService resourceService;
    @Autowired
    private ITagService tagService;

    @Autowired
    private IParamService paramService;
    @Autowired
    private URLGenerator urlGenerator;
    @Autowired
    private ResourceOperateService resourceOperateService;
    @Autowired
    private FolderPathService folderPathService;
    @Autowired
    private AoneUserService aoneUserService;
    @Autowired
    private TeamSpaceSizeService teamSpaceSizeService;
    @WebLog(method="makeBundle",params="title")
    @RequestMapping(params="func=makeBundle")
    public void makeBundle(HttpServletRequest request,HttpServletResponse response,
                           @RequestParam("title")String title,@RequestParam("itemKeys[]")int[] rids){
        //      VWBContext context = VWBContext.createContext(request, UrlPatterns.VIEW);
        //      IBundleService bs = bundleService;
        //      int tid = context.getSite().getId();
        //      List<PageLock> locks = PageLockValidateUtils.getPageLockFromResource(rids, resourceService, pageService,bs);
        //      if(!locks.isEmpty()){
        //          PageLockValidateUtils.pageLockMessage(locks, response, pageService);
        //          return ;
        //      }
        //      String uid = context.getCurrentUID();
        //      int bid = bs.createBundleAndPutItems(uid, title, rids, tid,null,compositeTagService);
        //      int[] actualAddRids = getBundleItemRids(bid, tid, bs, resourceService);
        //      resourceService.updateNewBundleTagAndStarmark(bid, tid, actualAddRids);
        //      Resource res = resourceService.getResource(bid, tid, LynxConstants.TYPE_BUNDLE);
        //      JsonArray array = getTagCount(res, context);
        //      JsonObject json = JSONHelper.getJSONObject(res);
        //      addMarkedCheckedField(uid, res, json);
        //      addResourceURLField(tid,res,json);
        //      json.put("tagCount", array);
        //      JsonArray conflictItems = ConflictBundleItemHelper.getJSONArrayOfConflictItems(resourceService, context, rids, actualAddRids);
        //      json.put("conflictItems", conflictItems);
        //      JSONHelper.writeJSONObject(response, json);
        throw new RuntimeException();
    }

    /*
      private void addMarkedCheckedField(String uid, Resource res, JsonObject json) {
      if(res.getMarkedUserSet().contains(uid)){
      json.put("isChecked", "checked");
      }else{
      json.put("isChecked", "unchecked");
      }
      json.remove("markedUserSet");
      }

      private void addResourceURLField(int tid,Resource res,JsonObject json){
      if(res.isBundle()){
      json.put("url",urlGenerator.getURL(tid,UrlPatterns.T_BUNDLE, res.getRid()+"", null));
      }else if(res.isFile()){
      json.put("url", urlGenerator.getURL(tid,UrlPatterns.T_FILE, res.getRid()+"", null));
      }else if(res.isPage()){
      json.put("url", urlGenerator.getURL(tid,UrlPatterns.T_PAGE, res.getRid()+"", null));
      }
      }

      private int[] getBundleItemRids(int bid, int tid, IBundleService bs, IResourceService rs){
      Resource res = rs.getResource(bid, tid, LynxConstants.TYPE_BUNDLE);
      List<Integer> tempRids = bs.getRidsOfBundleAndItems(res.getRid(), res.getTid());
      if(null != tempRids && !tempRids.isEmpty()){
      tempRids.remove(new Integer(res.getRid()));
      int size = tempRids.size();
      int[] result = new int[size];
      for(int i=0; i<size; i++){
      result[i] = tempRids.get(i);
      }
      return result;
      }else{
      return new int[0];
      }
      }
    */
    @SuppressWarnings("unused")
    private JsonArray getTagCount(Resource res, VWBContext context){
        JsonArray array = new JsonArray();
        if(null == res || null == res.getTagMap() || res.getTagMap().isEmpty()){
            return array;
        }
        Map<Integer, String> tagMap = res.getTagMap();
        int tid = res.getTid();
        for(Map.Entry<Integer, String> entry : tagMap.entrySet()){
            int tagid = entry.getKey();
            int count = tagService.getTagCount(tid, tagid);
            JsonObject obj = new JsonObject();
            obj.addProperty("id",tagid);
            obj.addProperty("count", count);
            array.add(obj);
        }
        return array;
    }
    /*
      @RequestMapping(params="func=uploadFiles")
      public ModelAndView uploadFiles(HttpServletRequest request,
      @RequestParam("bid")Integer bid) {
      VWBContext context = VWBContext.createContext(request, UrlPatterns.T_TEAM_HOME);
      ModelAndView mv = layout(ELayout.LYNX_MAIN, context,"/jsp/aone/tag/fileUploadView.jsp");
      if(bid!=0){
      mv.addObject("bundle", bundleService.getBundle(bid, context.getSite().getId()));
      }
      //add by lvly 2012-07-31 BEGIN
      String useTagIds=request.getParameter("tagIds");
      if(!CommonUtil.isNullStr(useTagIds)){
      mv.addObject("tagIds", useTagIds);
      }
      //END
      mv.addObject("bid", bid);
      mv.addObject(LynxConstants.PAGE_TITLE, "快速上传");
      return mv;
      }
    */
    @RequestMapping(params = "func=createPage")
    public ModelAndView createPage(HttpServletRequest request) {
        VWBContext context = VWBContext.createContext(request, UrlPatterns.T_TEAM_HOME);
        int tid = VWBContext.getCurrentTid();
        if(!teamSpaceSizeService.validateTeamSize(tid)){
            return layout(ELayout.LYNX_MAIN, context, "/jsp/aone/errors/noEnoughSpace.jsp");
        }
        String parentRid = request.getParameter("parentRid");
        int pRid=0;
        try{
            pRid = Integer.parseInt(parentRid);
        }catch(Exception e){};
        String name = folderPathService.getResourceName(tid,pRid,LynxConstants.TYPE_PAGE,LynxConstants.DEFAULT_DDOC_TITLE);
        String uid = VWBSession.getCurrentUid(request);
        Resource r = new Resource();
        r.setBid(pRid);
        r.setCreateTime(new Date());
        r.setCreator(uid);
        r.setLastEditor(uid);
        r.setLastEditorName(aoneUserService.getUserNameByID(uid));
        r.setLastEditTime(new Date());
        r.setTid(tid);
        r.setTitle(name);
        r.setLastVersion(0);
        r.setItemType(LynxConstants.TYPE_PAGE);
        r.setOrderType(Resource.NO_FOLDER_ORDER_TYPE);
        r.setFileType("ddoc");
        r.setStatus(LynxConstants.STATUS_UNPUBLISH);
        resourceOperateService.addResource(r);
        String redirectURL = urlGenerator.getURL(tid,UrlPatterns.T_EDIT_PAGE, r.getRid()+"",null);
        if(parentRid!=null){
            redirectURL = redirectURL+"&parentRid="+parentRid;
        }else{
            redirectURL = redirectURL+"&parentRid=0";
        }
        //add by lvly@2012-07-30 BEGIN
        String useTagIds=request.getParameter("tagIds");
        if(!CommonUtil.isNullStr(useTagIds)){
            redirectURL+="&tagIds="+useTagIds;
        }
        //END
        ModelAndView mv = new ModelAndView(new RedirectView(redirectURL));
        return mv;
    }

    /**
     * 用于上传文件页面获取标签，获取的标签包括URL中传递过来的tagId以及姓名标签
     * @param request
     * @param response
     * @param tagids URL中传递过来的tagid集合
     */
    @RequestMapping(params="func=getTag")
    public void getTag(HttpServletRequest request, HttpServletResponse response,
                       @RequestParam("tagids[]") int[] tagids){
        VWBContext context = VWBContext.createContext(request, UrlPatterns.T_TEAM_HOME);
        List<Tag> tags = tagService.getTags(tagids);
        addUserNameTag(context, tags);
        JsonArray array = new JsonArray();
        for(Tag tag : tags){
            JsonObject obj = new JsonObject();
            obj.addProperty("id", tag.getId());
            obj.addProperty("title", tag.getTitle());
            array.add(obj);
        }
        JsonUtil.write(response, array);
    }

    private void addUserNameTag(VWBContext context, List<Tag> tags){
        Param param = paramService.get(ParamConstants.UserPreferenceType.TYPE,
                                       ParamConstants.UserPreferenceType.KEY_NAME_TAG, context.getCurrentUID());
        if(null != param && param.getValue().equals(ParamConstants.UserPreferenceType.VALUE_NAME_TAG_TRUE)){
            Tag nameTag = tagService.getUserNameTag(context.getCurrentUID());
            nameTag = (null == nameTag)?createUserNameTagAndGroup(context,VWBContext.getCurrentTid()):nameTag;
            tags.add(nameTag);
        }
    }

    /**
     * 在上传文档后，添加bundle和为bundle添加标签
     * @param request
     * @param response
     * @return
     */
    /*
      @RequestMapping(params="func=afterUpload")
      public ModelAndView submit(HttpServletRequest request,HttpServletResponse response) {
      VWBContext context = VWBContext.createContext(request, UrlPatterns.T_TEAM_HOME);
      int tid = VWBContext.getCurrentTid();
      String uid = context.getCurrentUID();
      String[] ridsStr = request.getParameterValues("rids");
      //判断上传文件数
      if(ridsStr==null||ridsStr.length==0){
      return new ModelAndView(new RedirectView(urlGenerator.getURL(tid,UrlPatterns.T_TAG,null,null)));
      }
      int n =0;
      for(int i=0;i<ridsStr.length;i++){
      if(!"undefined".equals(ridsStr[i])){
      ridsStr[n]=ridsStr[i];
      n++;
      }
      }
      if(n==0){
      return new ModelAndView(new RedirectView(urlGenerator.getURL(tid,UrlPatterns.T_TAG,null,null)));
      }else if(n<ridsStr.length){
      String [] s =new String[n];
      System.arraycopy(ridsStr, 0, s, 0,n);
      ridsStr=s;
      }
      IBundleService bs = bundleService;
      //Exist Bundle id
      int[] rids = new int[ridsStr.length];
      for(int i =0;i<ridsStr.length;i++){
      rids[i]=Integer.parseInt(ridsStr[i]);
      }
      String existBundle = request.getParameter("existBundle");
      if(existBundle!=null){
      int existBundleId = Integer.parseInt(existBundle);
      Bundle b = bundleService.getBundle(existBundleId, tid);
      b.setLastEditor(context.getCurrentUID());
      bundleService.updateBundle(b);
      bs.addBundleItems(existBundleId, tid, rids);
      Resource bundle = resourceService.getResource(existBundleId, tid, LynxConstants.TYPE_BUNDLE);
      List<Integer> wantToAddRids = ArrayAndListConverter.convertInt2Integer(rids);
      int[] actualAddRids = ConflictBundleItemHelper.getNewAddBundleItems(bs, bundle, wantToAddRids);
      resourceService.updateExistBundleTagAndStarmark(existBundleId, tid, actualAddRids);
      rids = actualAddRids;
      }
      //判断是否新建bundle
      String groupCheck = request.getParameter("groupCheck");
      String groupName = request.getParameter("groupName");
      String description = request.getParameter("groupDesc");
      if("".equals(description)){
      description=null;
      }
      String tagCollection = request.getParameter("tagCollection");
      List<Integer> newRids= ArrayAndListConverter.convertInt2Integer(rids);

      if(groupCheck!=null&&!"".equals(groupCheck)&&"true".equalsIgnoreCase(groupCheck)){
      int bid = bs.createBundleAndPutItems(uid, groupName, rids, tid,description);
      Resource resource= resourceService.getResource(bid, tid,LynxConstants.TYPE_BUNDLE);
      int[] actualAddRids = ConflictBundleItemHelper.getNewAddBundleItems(bs, resource, newRids);
      newRids = ArrayAndListConverter.convertInt2Integer(actualAddRids);
      newRids.add(resource.getRid());
      addSelectTag(context,request,tid,newRids);
      resourceService.updateNewBundleTagAndStarmark(bid, tid, actualAddRids);
      return new ModelAndView(new RedirectView(urlGenerator.getURL(tid,UrlPatterns.T_BUNDLE, bid+"",null)));
      }
      int bid = WebParamUtil.getIntegerValue(request, "bid");
      if(tagCollection==null||"".equals(tagCollection)){
      if(bid!=0){
      return new ModelAndView(new RedirectView(urlGenerator.getURL(tid,UrlPatterns.T_BUNDLE, bid+"",null)));
      }else{
      addSelectTag(context, request, tid, newRids);
      return new ModelAndView(new RedirectView(urlGenerator.getURL(tid,UrlPatterns.T_TAG,null,null)));
      }
      }
      String[] tags = tagCollection.split(",");
      List<Tag> tagss = new ArrayList<Tag>();
      for(int i = 0;i<tags.length;i++){
      String tagId = tags[i];
      if(tagId == null||"".equals(tagId)){
      LOG.error("输入的标签参数错误");
      }
      else if(tagId.startsWith("true:")){
      String tagTitle = tagId.substring("true:".length());
      Tag t =tagService.getTag(Integer.parseInt(tagTitle));
      if(t!=null){
      tagss.add(t);
      }
      }else if(tagId.startsWith("false:")){
      String tagTitle = tagId.substring("false:".length());
      int tagCount = tagService.getTagGroupTitleCount(tid, tagTitle);
      Tag tag = null;
      if(tagCount==0){
      tag =new Tag();
      tag.setCreateTime(new Date());
      tag.setCount(1);
      tag.setCreator(uid);
      tag.setTitle(tagTitle);
      tag.setGroupId(0);
      tag.setTid(tid);
      int id =tagService.createTag(tag);
      tag.setId(id);
      tagss.add(tag);
      }else{
      LOG.error("要求更新的tag"+tagTitle+"已存在！");
      }
      }
      }

      for(Tag tag :tagss){
      bundleService.addItems(tid, tag.getId(), rids);
      }

      int[] fids = new int[ridsStr.length];
      for (int i = 0; i < fids.length; i++) {
      fids[i] = Integer.parseInt(ridsStr[i]);
      }

      if(bid!=0){
      return new ModelAndView(new RedirectView(urlGenerator.getURL(tid,UrlPatterns.T_BUNDLE, bid+"",null)));
      }else{
      return new ModelAndView(new RedirectView(urlGenerator.getURL(tid,UrlPatterns.T_TAG,null,null)));
      }
}
*/
private void addSelectTag(VWBContext context, HttpServletRequest request,
                          int tid, List<Integer> newRids) {
    String useTagIds=request.getParameter("tagIds");
    if(!CommonUtil.isNullStr(useTagIds)){
        Integer[] tagIds=CommonUtil.stringArray2IntArray(useTagIds.split(","));
        for(Integer rid:newRids){
            tagService.addItems(tid, CommonUtil.array2List(tagIds), rid);
            for(Integer tagId:tagIds){
                Tag tag=tagService.getTag(tagId);
                updateTagMap(context, rid, tag);
            }
        }
    }

}

private Tag createUserNameTagAndGroup(VWBContext context,int tid){
    TagGroup nameTagGroup=tagService.getTagGroupLikeTitle(tid, "姓名");
    Tag tag=null;
    if(nameTagGroup==null){
        nameTagGroup=new TagGroup();
        nameTagGroup.setCreator(context.getCurrentUID());
        nameTagGroup.setSequence(0);
        nameTagGroup.setTid(tid);
        nameTagGroup.setTitle("姓名标签");
        int groupId=tagService.createTagGroup(nameTagGroup);
        tag=new Tag();
        tag.setCount(0);
        tag.setCreateTime(new Date());
        tag.setCreator(context.getCurrentUID());
        tag.setGroupId(groupId);
        tag.setSequence(0);
        tag.setTid(tid);
        tag.setTitle(context.getCurrentUserName());
        tag.setId(tagService.createTag(tag));
    }else{
        tag=tagService.getTag(tid,nameTagGroup.getId(), context.getCurrentUserName());
        if(tag==null){
            tag=new Tag();
            tag.setCount(0);
            tag.setCreateTime(new Date());
            tag.setCreator(context.getCurrentUID());
            tag.setGroupId(nameTagGroup.getId());
            tag.setSequence(0);
            tag.setTid(tid);
            tag.setTitle(context.getCurrentUserName());
            tag.setId(tagService.createTag(tag));

        }
    }
    return tag;
}
private void updateTagMap(VWBContext context,Number newRid,Tag tag){
    Resource resourceNew=resourceService.getResource((Integer)(newRid));
    Map<Integer,String> tagMap=resourceNew.getTagMap();
    if(CommonUtil.isNullArray(tagMap)){
        tagMap=new HashMap<Integer,String>();
        resourceNew.setTagMap(tagMap);
    }
    tagMap.put(tag.getId(), tag.getTitle());
    List<Resource> resources=new ArrayList<Resource>();
    resources.add(resourceNew);
    resourceService.updateResourceTagMap(resources);
}
/**
 * 校验bundle name是否重复
 * @param request
 * @param response
 */
/*
  @RequestMapping(params="func=checkBundleName")
  public void checkBundleName(HttpServletRequest request,HttpServletResponse response){
  VWBContext context = VWBContext.createContext(request, UrlPatterns.T_TEAM_HOME);
  int tid = VWBContext.getCurrentTid();
  JsonObject o = new JsonObject();
  String title = request.getParameter("groupName");
  if(title==null||"".equals(title)){
  o.put("success", "false");
  o.put("error", "组名不能为空");
  JSONHelper.writeJSONObject(response, o);
  return;
  }
  int i=bundleService.getBundleCountByTitle(tid, title, true);
  if(i==0){
  o.put("success", "true");
  }else{
  o.put("success", "false");
  o.put("error", "输入的bundle名称"+title+"重复！");
  LOG.warn(context.getCurrentUID()+"输入的bundle名称"+title+"重复！");
  }
  JSONHelper.writeJSONObject(response, o);
  }
*/
}
