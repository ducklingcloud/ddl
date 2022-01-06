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
package net.duckling.ddl.service.resource.impl;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Date;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import net.duckling.ddl.service.resource.ITagService;
import net.duckling.ddl.service.resource.Resource;
import net.duckling.ddl.service.resource.Tag;
import net.duckling.ddl.service.resource.TagGroup;
import net.duckling.ddl.service.resource.TagGroupRender;
import net.duckling.ddl.service.resource.TagItem;
import net.duckling.ddl.util.PaginationBean;
import net.duckling.ddl.util.TeamQuery;

import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;


@Service
public class TagServiceImpl implements ITagService {

    private static final Logger LOG = Logger.getLogger(TagServiceImpl.class);

    @Autowired
    private TagDAO tagDAO;
    @Autowired
    private TagGroupDAO tagGroupDAO;
    @Autowired
    private TagItemDAO tagItemDAO;

    @Override
    public int createTag(Tag tag) {
        return tagDAO.create(tag);
    }

    @Override
    public int updateTag(Tag tag) {
        int tgid = tag.getId();
        if(tgid<=0){
            LOG.error("tagId should be positive while update tag with tgid="+tgid);
            return 0;
        }
        return tagDAO.update(tgid, tag);
    }

    @Override
    public int deleteTag(int tgid) {
        int successTag = tagDAO.delete(tgid);
        int successItem = tagItemDAO.deleteByTagId(tgid);
        return (successTag>0 && successItem>0)?1:-1;
    }

    @Override
    public Tag getTag(int tgid) {
        return tagDAO.getTagById(tgid);
    }

    @Override
    public Tag getTag(int tid, String title){
        return tagDAO.getTag(tid, title);
    }
    @Override
    public Tag getTag(int tid, int gid, String title) {
        return tagDAO.getTag(tid,gid, title);
    }

    @Override
    public Tag getUserNameTag(String uid){
        return tagDAO.getUserNameTag(uid);
    }

    @Override
    public int batchUpdateWithTag(int tid, int tagId, List<Long> newRids) {
        return tagItemDAO.batchUpdateWithTag(tid, tagId, newRids);
    }

    @Override
    public int addItems(int tid, List<Integer> tagIds, int rid){
        List<Long> rids=new ArrayList<Long>();
        rids.add((long) rid);
        return addItems(tid,tagIds,rids);
    }

    @Override
    public int addItems(int tid, List<Integer> tagIds, List<Long> rids){
        if(null==tagIds || tagIds.isEmpty()||null==rids || rids.isEmpty()){
            return 0;
        }
        for(Integer tagId: tagIds){
            tagItemDAO.batchUpdateWithTag(tid, tagId, rids);
            updateTagCount(tid, tagId);
        }
        return 1;
    }

    @Override
    public void addItem(int tid, int tagId, int rid){
        List<Integer> tagIds=new ArrayList<Integer>();
        tagIds.add(tagId);
        addItems(tid,tagIds,rid);
    }

    @Override
    public int removeItems(int tid, int tgid, List<Integer> rids) {
        if(null == rids || rids.isEmpty()){
            return 0;
        }
        int result = tagItemDAO.removeItems(tid, tgid, rids);
        return result;
    }

    @Override
    public List<TagItem> getTagItems(int tgid) {
        return tagItemDAO.getItemsInTags(new int[]{tgid},0,0);
    }

    @Override
    public List<TagItem> getTagItems(int[] tgids) {
        return tagItemDAO.getItemsInTags(tgids,0,0);
    }

    @Override
    public List<TagItem> getTagItems(int[] tgids, int offset, int size) {
        return tagItemDAO.getItemsInTags(tgids, offset, size);
    }

    @Override
    public int createTagGroup(TagGroup tagGroup) {
        return tagGroupDAO.create(tagGroup);
    }

    @Override
    public int updateTagGroup(int tagGroupId, TagGroup tagGroup) {
        return tagGroupDAO.update(tagGroupId, tagGroup);
    }

    @Override
    public int deleteTagGroup(int tagGroupId) {
        return tagGroupDAO.delete(tagGroupId);
    }

    @Override
    public int addTags2Group(int tagGroupId, int[] tgids) {
        return tagDAO.addTags2Group(tagGroupId, tgids);
    }

    @Override
    public int removeTagFromGroup(int tagGroupId, int[] tgids) {
        return tagDAO.removeTagsFromGroup(tagGroupId, tgids);
    }

    @Override
    public List<Tag> getTagsNotInGroupForTeam(int tid) {
        //tagGroupId=0表示没有在任何tagGroup中
        return tagDAO.getAllTagInTeamAndGroup(tid, 0);
    }

    @Override
    public List<TagGroupRender> getTagGroupsForTeam(int tid) {
        List<TagGroup> tagGroup = tagGroupDAO.getAllTagGroupByTid(tid);
        Iterator<TagGroup> tagGroupItr = tagGroup.iterator();
        List<TagGroupRender> renderList = new ArrayList<TagGroupRender>();
        while(tagGroupItr.hasNext()){
            TagGroupRender render = new TagGroupRender();
            TagGroup temp = tagGroupItr.next();
            List<Tag> tags = tagDAO.getAllTagInTeamAndGroup(tid, temp.getId());
            render.setGroup(temp);
            render.setTags(tags);
            renderList.add(render);
        }
        return renderList;
    }

    @Override
    public TagGroup getTagGroupById(int tagGroupId){
        return tagGroupDAO.getTagGroupById(tagGroupId);
    }

    @Override
    public List<TagItem> query(TeamQuery q) {
        return tagItemDAO.getItemsInTags(q.getTagIds(), q.getOffset(), q.getSize());
    }

    @Override
    public int increaseCount(int tagid,int delta) {
        return tagDAO.increaseCount(tagid,delta);
    }

    @Override
    public int decreaseCount(List<Integer> tagids, int delta){
        return tagDAO.decreaseCount(tagids, delta);
    }

    public void deleteTagItemByTagIds(int[] tagids){
        tagItemDAO.deleteByTagIds(tagids);
    }

    public void deleteTagByTagIds(int[] tgids){
        tagDAO.deleteBatch(tgids);
    }

    @Override
    public int getTagTitleCount(int tid, String title) {
        return tagDAO.getTagTitleCount(tid, title);
    }

    @Override
    public int getTagGroupTitleCount(int tid, String title) {
        return tagGroupDAO.getTagGroupTitleCount(tid, title);
    }
    @Override
    public TagGroup getTagGroupLikeTitle(int tid,String title){
        return tagGroupDAO.getTagGroupLikeTitle(tid, title);
    }

    @Override
    public List<Tag> getNotRelatedTags(int rid, int tid) {
        return tagDAO.getNotRelatedTags(rid,tid);
    }

    @Override
    public Map<String, List<Tag>> getTagGroupMap(int tid) {
        Map<Integer,String> idGroupMap = new LinkedHashMap<Integer,String>();
        Map<String, List<Tag>> tagGroupMap = new LinkedHashMap<String,List<Tag>>();
        List<TagGroup> sortedGroupList = tagGroupDAO.getAllTagGroupByTid(tid);
        for(TagGroup g:sortedGroupList){
            idGroupMap.put(g.getId(), g.getTitle());
            tagGroupMap.put(g.getTitle(), new ArrayList<Tag>());
        }
        List<Tag> tagList = tagDAO.getAllGroupTagsByTeam(tid);
        for(Tag t:tagList){
            tagGroupMap.get(idGroupMap.get(t.getGroupId())).add(t);
        }
        return tagGroupMap;
    }

    @Override
    public List<Tag> getTagsByGroupId(int groupId, int tid) {
        return tagDAO.getAllTagInTeamAndGroup(tid, groupId);
    }

    public void deleteTagItems(List<Integer> rids, Integer tagId) {
        tagItemDAO.deleteTagItem(rids,tagId);
    }

    @Override
    public boolean isItemHasTag(int rid, int existTagId) {
        return tagItemDAO.isItemHasTag(rid,existTagId);
    }

    public List<TagItem> getItems(int tagId) {
        return tagItemDAO.getItemsInTag(tagId);
    }

    @Override
    public void updateTagGroupsOrder(Integer[] tgids) {
        tagGroupDAO.updateSequence(tgids);
    }

    @Override
    public List<Tag> getTags(int[] tagids) {
        return tagDAO.getTags(tagids);
    }

    @Override
    public List<Tag> getTags(List<Integer> tagIds) {
        if(tagIds==null||tagIds.isEmpty()){
            return null;
        }
        int[] tagIdsArray=new int[tagIds.size()];
        for(int i=0;i<tagIds.size();i++){
            tagIdsArray[i]=tagIds.get(i);
        }
        return tagDAO.getTags(tagIdsArray);
    }

    @Override
    public List<Tag> getTagsForTeam(int tid) {
        return tagDAO.getAllTagInTeam(tid);
    }

    @Override
    public List<Tag> getTagsByPinyin(int tid, String pinyin){
        return tagDAO.getTagsByPinyin(tid, pinyin);
    }

    @Override
    public List<Tag> getTagsByName(int tid, String name){
        return tagDAO.getTagsByName(tid, name);
    }

    @Override
    public void removeAllTagItemsOfRid(int tid, int rid) {
        List<TagItem> tagItems=tagItemDAO.getAllTagItemOfRid(tid, rid);
        tagItemDAO.deleteAllTagItemOfRid(tid, rid);
        if(tagItems!=null&&!tagItems.isEmpty()){
            for(TagItem tagItem:tagItems){
                updateTagCount(tid,tagItem.getTgid());
            }
        }
    }

    @Override
    public int getTagCount(int tid, int tagId) {
        return tagItemDAO.getTagCount(tid,tagId);
    }

    @Override
    public void updateTagCount(int tid, int tagId) {
        int count = tagItemDAO.getTagCount(tid, tagId);
        tagDAO.updateCount(tagId, count);
    }

    public void updateTagCountByRid(int tid,int rid){
        List<TagItem> tagItemList=tagItemDAO.getAllTagItemOfRid(tid,rid);
        if(tagItemList==null||tagItemList.isEmpty()){
            return;
        }
        for(TagItem tagItem:tagItemList){
            updateTagCount(tagItem.getTid(),tagItem.getTgid());
        }
    }
    @Override
    public PaginationBean<Resource> getTeamTagFiles(int tid ,int tagId,int begin,int maxPageSize,String order, String keyWord){
        return tagItemDAO.getTeamTagFiles( tid , tagId, begin, maxPageSize, order, keyWord);
    }

    @Override
    public PaginationBean<Resource> getTeamTagFiles(int tid ,Collection<Integer> tagIds,int begin,int maxPageSize,String order, String keyWord){
        return tagItemDAO.getTeamTagFiles( tid , tagIds, begin, maxPageSize, order, keyWord);
    }

    public Tag getNameTag(int tid,String uid,String userName) {
        TagGroup nameTagGroup =getTagGroupLikeTitle(tid, "姓名");
        Tag tag = null;
        if (nameTagGroup == null) {
            nameTagGroup = new TagGroup();
            nameTagGroup.setCreator(uid);
            nameTagGroup.setSequence(0);
            nameTagGroup.setTid(tid);
            nameTagGroup.setTitle("姓名标签");
            int groupId = createTagGroup(nameTagGroup);
            tag = new Tag();
            tag.setCount(0);
            tag.setCreateTime(new Date());
            tag.setCreator(uid);
            tag.setGroupId(groupId);
            tag.setSequence(0);
            tag.setTid(tid);
            tag.setTitle(userName);
            tag.setId(createTag(tag));
        } else {
            tag = getTag(tid, nameTagGroup.getId(), userName);
            if (tag == null) {
                tag = new Tag();
                tag.setCount(0);
                tag.setCreateTime(new Date());
                tag.setCreator(uid);
                tag.setGroupId(nameTagGroup.getId());
                tag.setSequence(0);
                tag.setTid(tid);
                tag.setTitle(userName);
                tag.setId(createTag(tag));

            }
        }
        return tag;
    }

}
