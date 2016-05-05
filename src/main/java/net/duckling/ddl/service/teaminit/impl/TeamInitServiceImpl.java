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
package net.duckling.ddl.service.teaminit.impl;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.util.Arrays;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

import net.duckling.ddl.exception.NoEnoughSpaceException;
import net.duckling.ddl.service.file.FileVersion;
import net.duckling.ddl.service.resource.IResourceService;
import net.duckling.ddl.service.resource.ITagService;
import net.duckling.ddl.service.resource.Resource;
import net.duckling.ddl.service.resource.ResourceOperateService;
import net.duckling.ddl.service.resource.ResourceUtils;
import net.duckling.ddl.service.resource.Tag;
import net.duckling.ddl.service.teaminit.TeamInitService;

import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;


/**
 * 团队初始化工具类.
 * 
 * 
 * @author zhonghui
 *
 */
@Service
public final class TeamInitServiceImpl implements TeamInitService {
	
    @Autowired
    private IResourceService resourceService;
    @Autowired
    private ResourceOperateService resourceOperateService;
    @Autowired
    private ITagService tagService;
    @Autowired
    private static final Logger LOG = Logger.getLogger(TeamInitServiceImpl.class);
    private Resource addPageToTeam(int tid, String uid, String title, String content) {
    	Resource r = ResourceUtils.createDDoc(tid, 0, title, uid);
    	resourceOperateService.addResource(r);
    	resourceOperateService.createPageVersion(r, content);
        return r;
    }

    private FileVersion addFileToTeam(int tid, String uid, String title, InputStream in) throws NoEnoughSpaceException {
        try {
            return resourceOperateService.upload(uid,tid,0, title, in.available(), in,false,false);
        } catch (IOException e) {
            LOG.error("", e);
            return null;
        }
    }

    private Tag addTagToTeam(int tid, String uid, int tgId, String title) {
		int i = tagService.getTagTitleCount(tid, title);
        if (i != 0) {
            return tagService.getTag(tid, title);
        }
        Tag tag = tagService.getTag(tid, title);
        if (null != tag) {
            return tag;
        }
        tag = new Tag();
        tag.setCreateTime(new Date());
        tag.setCreator(uid);
        tag.setGroupId(tgId);
        tag.setTid(tid);
        tag.setCount(0);
        tag.setTitle(title);
        int tagId=tagService.createTag(tag);
        tag.setId(tagId);
        return tag;
	}

    private void addTagToPage(int tid, Resource page, Tag tag) {
		Resource res = resourceService.getResource(page.getRid(), tid);
        Map<Integer, String> tagMap = res.getTagMap();
        if (tagMap == null || tagMap.isEmpty()) {
            tagMap = new HashMap<Integer, String>();
        }
        tagMap.put(tag.getId(), tag.getTitle());
        res.setTagMap(tagMap);
        resourceService.updateResourceTagMap(Arrays.asList(new Resource[] { res }));
        tagService.addItem(tid, tag.getId(), res.getRid());
	}

    private void addTagToFile(int tid, FileVersion f, Tag tag) {
		Resource res = resourceService.getResource(f.getRid(), tid);
        Map<Integer, String> tagMap = res.getTagMap();
        if (tagMap == null || tagMap.isEmpty()) {
            tagMap = new HashMap<Integer, String>();
        }
        tagMap.put(tag.getId(), tag.getTitle());
        res.setTagMap(tagMap);
        resourceService.updateResourceTagMap(Arrays.asList(new Resource[] { res }));
        tagService.addItem(tid, tag.getId(), res.getRid());
	}

    @Override
	public void initNewTeam(int tid, String uid) {
    	InputStream in = null;
    	try {
			Resource note = addPageToTeam(tid, uid, "项目进度", p);
			Resource project = addPageToTeam(tid, uid, "会议记录", conference);
			Tag system = addTagToTeam(tid, uid, 0, "系统示例");
			addTagToPage(tid, note, system);
			addTagToPage(tid, project, system);
			String classDir = TeamInitServiceImpl.class.getResource("/").getFile();
			File cDir = new File(classDir);
			File inf = new File(cDir.getParentFile(), "teaminit/科研在线团队文档库-用户使用手册-V4.0.pdf");
			in = new FileInputStream(inf);
			FileVersion v = addFileToTeam(tid, uid, "科研在线团队文档库-用户使用手册-V4.0.pdf", in);
			addTagToFile(tid, v, system);
        } catch (FileNotFoundException e) {
            LOG.error("", e);
        } catch(Exception e){
        	LOG.error("", e);
        }finally {
            if (in != null) {
                try {
                    in.close();
                } catch (IOException e) {
                    LOG.error("", e);
                }
            }
        }
	}

	    
	    private static final String p = "<p>项目 名称：</p<p>&nbsp;</p><table width=\"80%\" border=\"1\" cellspacing=\"0\" cellpadding=\"0\">"
	        +"<tbody><tr><td width=\"101\">阶段</td><td width=\"177\">任务描述</td><td width=\"147\">时间计划</td><td width=\"131\">负责人</td><td width=\"81\">完成状态</td>"
	        +"</tr><tr><td width=\"101\">&nbsp;</td><td width=\"177\">&nbsp;</td><td width=\"147\">&nbsp;</td><td width=\"131\">&nbsp;</td><td width=\"81\">&nbsp;</td>"
	        +"</tr><tr><td width=\"101\">&nbsp;</td><td width=\"177\">&nbsp;</td><td width=\"147\">&nbsp;</td><td width=\"131\">&nbsp;</td><td width=\"81\">&nbsp;</td>"
	        +"</tr><tr><td width=\"101\">&nbsp;</td><td width=\"177\">&nbsp;</td><td width=\"147\">&nbsp;</td><td width=\"131\">&nbsp;</td><td width=\"81\">&nbsp;</td>"
	        +"</tr><tr><td width=\"101\">&nbsp;</td><td width=\"177\">&nbsp;</td><td width=\"147\">&nbsp;</td><td width=\"131\">&nbsp;</td><td width=\"81\">&nbsp;</td>"
	        +"</tr></tbody></table>" ;
	    private static final String conference = "<h3 style=\"padding: 3px 0.5em 3px 0px;\" id=\"h3_1\">概况&nbsp;</h3>"
	            +"<p style=\"margin-bottom: 0.8em;\">时间：</p>"
	            +"<p style=\"margin-bottom: 0.8em;\">参会人员：</p>"
	            +"<h3 style=\"padding: 3px 0.5em 3px 0px;\" id=\"h3_2\">会议议程</h3>"
	            +"<p style=\"margin-bottom: 0.8em;\">1、</p>"
	            +"<ul>"
	            +"  <li style=\"margin-bottom: 0.8em;\">&nbsp;</li>"
	            +"</ul>"
	            +"<p style=\"margin-bottom: 0.8em;\">2、</p>"
	            +"<ul>"
	            +"   <li style=\"margin-bottom: 0.8em;\">&nbsp;</li>"
	            +"</ul>"
	            +"<p style=\"margin-bottom: 0.8em;\">&nbsp;</p>";
}
