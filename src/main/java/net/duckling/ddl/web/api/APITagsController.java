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

import java.util.ArrayList;
import java.util.List;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import net.duckling.ddl.common.Site;
import net.duckling.ddl.service.resource.ITagService;
import net.duckling.ddl.service.resource.Tag;
import net.duckling.ddl.service.resource.TagGroup;
import net.duckling.ddl.service.resource.TagGroupRender;
import net.duckling.ddl.util.JsonUtil;
import net.duckling.ddl.web.interceptor.access.RequirePermission;

import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;


/**
 * API调用的服务程序
 * @date 2011-8-22
 * @author xiejj@cnic.cn
 */
@Controller
@RequestMapping("/api/tags")
@RequirePermission(target="team", operation="view")
public class APITagsController extends APIBaseController {

    private static final int ALL_DOC_ID = -1;
    private static final String ALL_DOC_NAME = "所有文件";
    private static final String ALL_DOC_DESC = "all";

    private static final int NO_DOC_ID = -2;
    //  private static final String NO_DOC_NAME = "无标签文档";
    private static final String NO_DOC_DESC = "untaged";

    @Autowired
    private ITagService tagService;
    @SuppressWarnings("unchecked")
    @RequestMapping
    public void service(HttpServletRequest request, HttpServletResponse response){
        Site site = findSite(request);
        JSONArray arrayResult = new JSONArray();

        List<Tag> allno = new ArrayList<Tag>();
        // 添加所有文档tag和无标签文档tag
        JSONObject allNo = new JSONObject();
        Tag all = new Tag();
        all.setId(ALL_DOC_ID);
        all.setTitle(ALL_DOC_NAME);
        all.setPinyin(ALL_DOC_DESC);

        //      Tag no = new Tag();
        //      no.setId(NO_DOC_ID);
        //      no.setTitle(NO_DOC_NAME);
        //      no.setPinyin(NO_DOC_DESC);

        allno.add(all);
        //      allno.add(no);

        allNo.put("groupTag", "common");
        allNo.put("common", JsonUtil.getJSONArrayFromList(allno));
        arrayResult.add(allNo);

        // 分组添加Tag
        List<TagGroupRender> tagGroupRenderList = tagService.getTagGroupsForTeam(site.getId());
        if (tagGroupRenderList!=null){
            for (TagGroupRender tagGroupRender : tagGroupRenderList){
                JSONObject jcol = new JSONObject();
                TagGroup tagGroup = tagGroupRender.getGroup();
                List<Tag> tagList = tagGroupRender.getTags();
                JSONArray arrayTag = JsonUtil.getJSONArrayFromList(tagList);
                String groupTag = tagGroup.getTitle();
                jcol.put("groupTag", groupTag);
                jcol.put(groupTag, arrayTag);
                arrayResult.add(jcol);
            }
        }
        List<Tag> noGroupTag = tagService.getTagsNotInGroupForTeam(site.getId());
        if(noGroupTag!=null&&!noGroupTag.isEmpty()){
            JSONObject jcol = new JSONObject();
            JSONArray arrayTag = JsonUtil.getJSONArrayFromList(noGroupTag);
            String groupTag = "未分类标签";
            jcol.put("groupTag", groupTag);
            jcol.put(groupTag, arrayTag);
            arrayResult.add(jcol);
        }
        JsonUtil.writeJSONObject(response, arrayResult);
    }

    //1.0版本时的接口实现
    /*    @RequestMapping
          public void service(HttpServletRequest request, HttpServletResponse response){
          Site site = VWBSite.findSite(request);
          List<Tag> tagList = tagService.getTagsForTeam(site.getId());
          JSONArray array = new JSONArray();
          if (tagList!=null){
          for (Tag tag:tagList){
          JSONObject jcol = new JSONObject();
          jcol.put("id", tag.getId());
          jcol.put("name", tag.getTitle());
          jcol.put("desc", tag.getCount()+"");
          array.add(jcol);
          }
          }
          JsonUtil.writeJSONObject(response, array);
          }*/
}
