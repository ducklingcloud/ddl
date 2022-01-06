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
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import net.duckling.ddl.constant.LynxConstants;
import net.duckling.ddl.service.resource.IResourceService;
import net.duckling.ddl.service.resource.ITagService;
import net.duckling.ddl.service.resource.Resource;
import net.duckling.ddl.service.resource.ResourceBuilder;
import net.duckling.ddl.service.resource.SimpleResource;
import net.duckling.ddl.service.resource.Tag;
import net.duckling.ddl.service.tobedelete.PageContentRender;
import net.duckling.ddl.util.CommonUtil;
import net.duckling.ddl.util.PaginationBean;
import net.duckling.ddl.util.ResourceQuery;
import net.duckling.ddl.util.TeamQuery;
import net.duckling.falcon.api.cache.ICacheService;

import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.util.CollectionUtils;


@Service
public class ResourceServiceImpl implements IResourceService {

    private static final String ITEM_SIMPLE_RESOURCE = "item-simpleresource";
    private static final String RID_SIMPLE_RESOURCE = "rid-simpleresource";
    /**
     * 团队资源数量缓存key前缀
     */
    private static final String TEAM_RESOURCE_AMOUNT = "team-resource-amount";
    private static final Logger LOG = Logger.getLogger(ResourceServiceImpl.class);

    @Autowired
    private ICacheService memcachedService;
    @Autowired
    private ResourceDAO resourceDAO;
    @Autowired
    private ResourcePathDAO resourcePathDAO;
    @Autowired
    private ITagService tagService;

    @Override
    public Resource getTree(int tid, int folderRid){
        //获取目录下所有层级的文件和目录
        List<Resource> list = resourcePathDAO.getDescendants(tid, folderRid);
        return recursiveTree(folderRid, list);
    }

    @Override
    public void batchDelete(int tid, List<Integer> rids) {
        if (null == rids) {
            return;
        }
        resourceDAO.batchDelete(rids);
        clearResourceAmountCache(tid);
    }

    @Override
    public int create(Resource res) {
        int rid = resourceDAO.create(res);
        res.setRid(rid);
        String itemKey = ResourceBuilder.getItemKey(res);
        SimpleResource s = ResourceBuilder.getSimpleResource(res);
        setItemKeyToCache(res.getTid(),itemKey, s);
        setRidIntoCache(res.getTid(),rid, s);
        clearResourceAmountCache(res.getTid());
        return rid;
    }

    @Override
    public void delete(int rid, int tid, String itemType) {
        String itemKey = ResourceBuilder.getItemKey(rid, itemType, tid);
        SimpleResource s = getSimpleResourceFromCache(tid,itemKey);
        removeSimpleResourceFromCache(tid,itemKey);
        if (s != null) {
            removeSimpleResourceFromCache(tid,s.getRid());
        }
        resourceDAO.delete(rid, tid);
        clearResourceAmountCache(tid);
    }

    @Override
    public List<Resource> getDDoc(int tid, List<Integer> rids) {
        return resourceDAO.getDDoc(tid,rids);
    }

    @Override
    public List<Resource> getFileByStartName(int tid, String name) {
        return resourceDAO.getFileByStartName(tid, name);
    }

    @Override
    public List<Resource> getFileByTitle(int tid, int parentRid, String title) {
        return resourceDAO.getFileByTitle(tid, parentRid, title);
    }

    @Override
    public List<Resource> getResourceByTitle(int tid, int parentRid, String type, String title) {
        return resourceDAO.getResourceByTitle(tid,parentRid,type,title);
    }
    @Override
    public List<Resource> getResourceByTitle(int tid, int parentRid, String type, String title,String status) {
        return resourceDAO.getResourceByTitle(tid,parentRid,type,title,status);
    }

    @Override
    public PaginationBean<Resource> getMyCreatedFiles(int tid, String uId,
                                                      int offset, int size, String order, String keyWord) {
        return resourceDAO.getMyCreatedFiles(tid, uId, offset, size, order, keyWord);
    }

    @Override
    public PaginationBean<Resource> getMyRecentFiles(int tid, String uId,
                                                     int offset, int size, String order) {
        return resourceDAO.getMyRecentFiles(tid, uId, offset, size, order);
    }

    @Override
    public Resource getResource(int rid) {
        return resourceDAO.getResource(rid);
    }

    @Override
    public Resource getResource(int rid, int tid) {
        return resourceDAO.getResourceById(rid, tid);
    }

    @Override
    public List<Resource> getResource(TeamQuery query) {
        return resourceDAO.query(query);
    }

    @Override
    public List<Resource> getResources(Collection<Integer> rids, int tid, String itemType) {
        return resourceDAO.getResources(rids, tid, itemType);
    }

    @Override
    public List<Resource> getResourcesBySphinxID(List<Long> ids) {
        return resourceDAO.getResourceByRids(ids);
    }

    @Override
    public List<Resource> getResource(List<Integer> rids) {
        if(rids==null||rids.isEmpty()){
            return new ArrayList<Resource>();
        }
        List<Long> temp=new ArrayList<Long>();
        for(int id:rids){
            temp.add((long)id);
        }
        return resourceDAO.getResourceByRids(temp);
    }

    @Override
    public Map<Integer,Resource> getResourceMap(List<Resource> resourceList) {
        Map<Integer,Resource> result=new HashMap<Integer,Resource>();
        if(resourceList==null||resourceList.isEmpty()){
            return new HashMap<Integer,Resource>();
        }
        for(Resource re:resourceList){
            result.put(re.getRid(), re);
        }
        return result;
    }

    public Map<String,List<Integer>> getResourceTypeMap(int tid,List<Integer> rids){
        if (null == rids) {
            return null;
        }
        Map<String,List<Integer>> map = new HashMap<String,List<Integer>>();
        List<Integer> fileIds = new ArrayList<Integer>();
        List<Integer> pageIds = new ArrayList<Integer>();
        List<Integer> folderIds = new ArrayList<Integer>();
        for (int rid : rids) {
            SimpleResource sr = getSimpleResourceFromCache(tid,rid);
            int tRid = 0;
            String itemType = "";
            if (null != sr) {
                tRid = sr.getRid();
                itemType = sr.getItemType();
                String itemKey = ResourceBuilder.getItemKey(tRid, itemType, tid);
                removeSimpleResourceFromCache(tid,rid);
                removeSimpleResourceFromCache(tid,itemKey);
            }

            switch (itemType) {
                case LynxConstants.TYPE_FOLDER:
                    folderIds.add(tRid);
                    break;
                case LynxConstants.TYPE_FILE:
                    fileIds.add(tRid);
                    break;
                case LynxConstants.TYPE_PAGE:
                    pageIds.add(tRid);
                    break;
                default:
                    LOG.error("Error Resource item type = " + itemType + ", while batchDelete resource");
            }
        }
        map.put(LynxConstants.TYPE_PAGE, pageIds);
        map.put(LynxConstants.TYPE_FILE, fileIds);
        map.put(LynxConstants.TYPE_FOLDER, folderIds);
        return map;
    }

    @Override
    public SimpleResource getSimpleResource(int tid,int rid) {
        SimpleResource r = getSimpleResourceFromCache(tid,rid);
        if (r == null) {
            LOG.warn("can not hit the simple resource in cache by rid:" + rid + ",tid:" + tid);
            Resource temp = getResource(rid);
            if (temp != null) {
                SimpleResource sr = ResourceBuilder.getSimpleResource(temp);
                setRidIntoCache(tid,rid, sr);
                return sr;
            }
            return null;
        }
        return r;
    }

    @Override
    public SimpleResource getSimpleResource(int rid, String itemType, int tid) {
        String itemKey = ResourceBuilder.getItemKey(rid, itemType, tid);
        SimpleResource r = getSimpleResourceFromCache(tid, itemKey);
        if (r == null) {
            LOG.warn("can not hit the simple resource in cache by itemKey:" + itemKey);
            Resource temp = getResource(rid, tid);
            if (temp != null) {
                SimpleResource sr = ResourceBuilder.getSimpleResource(temp);
                setItemKeyToCache(tid,itemKey, sr);
                return sr;
            }
            return null;
        }
        return r;
    }

    @Override
    public Set<String> getStarmarkOfResources(List<Long> rids) {
        return resourceDAO.getStarmarkOfResources(rids);
    }

    @Override
    public List<Resource> getStarmarkResource(String uid, int tid) {
        return resourceDAO.getStarmarkResource(uid, tid);
    }

    @Override
    public List<Resource> getUnBundleResource(int bid, int tid, String keyword, int offset, int size) {
        return resourceDAO.getUnBundleResource(bid, tid, keyword, offset, size);
    }

    @Override
    public void mergeAndUpdateMarkedUserSetOfBundle(int bid, int tid,List<Integer> itemRids) {
        List<Long> itemRidsLong = new ArrayList<Long>();
        for (int rid : itemRids) {
            itemRidsLong.add((long) rid);
        }
        Set<String> starmarks = getStarmarkOfResources(itemRidsLong);
        List<Resource> resList = resourceDAO.getResourceByRids(itemRidsLong);
        for (Resource res : resList) {
            res.setMarkedUserSet(starmarks);
        }
        updateMarkedUserSet(resList);
    }

    @Override
    public void mergeAndUpdateTagMapOfBundle(int bid, int tid,List<Integer> itemRids) {
        List<Resource> resList = getBundleResourceList(bid, tid, itemRids);
        Map<Integer, String> bundleTagMap = new HashMap<Integer, String>();
        for (Resource res : resList) {
            Map<Integer, String> tagMap = res.getTagMap();
            if (null != tagMap) {
                bundleTagMap.putAll(tagMap);
            }
        }
        for (Resource res : resList) {
            res.setTagMap(bundleTagMap);
        }
        updateResourceTagMap(resList);
    }

    /**
     * 将Bundle中 不包含但itemsList中包含的Tag，更新到Bundle及Bundle内已有资源上。<br/>
     * 即建立资源与Tag的关联关系(a1_tag_item)。
     *
     * @param itemsList
     *            资源集合
     * @param oldBundle
     *            代表Bundle的Resource对象
     */
    public void putNewBundleItemTag2BundleAndOldItem(List<Resource> itemsList, Resource oldBundle, List<Integer> oldItemRids) {
        if (null == itemsList) {
            return;
        }
        Map<Integer, String> bundleTagMap = oldBundle.getTagMap();
        Set<Integer> notInBundleTagId = new HashSet<Integer>();
        List<Integer> newBundleItemRids = new ArrayList<Integer>();
        for (Resource res : itemsList) {
            newBundleItemRids.add(res.getRid());
            Map<Integer, String> itemTagMap = res.getTagMap();
            if (null == itemTagMap || itemTagMap.isEmpty()) {
                continue;
            }
            for (Map.Entry<Integer, String> entry : itemTagMap.entrySet()) {
                Integer tagId = entry.getKey();
                if (null != bundleTagMap && bundleTagMap.containsKey(tagId)) {
                    continue;
                } else {
                    notInBundleTagId.add(tagId);
                }
            }
        }
        for(Integer rid:newBundleItemRids){ // 只有原有的Bundle内资源才需要更新tag关联关系
            if(oldItemRids.contains(rid)){
                oldItemRids.remove(rid);
            }
        }
        List<Integer> tagIds = new ArrayList<Integer>(notInBundleTagId);
        for (Integer rid : oldItemRids) {
            tagService.addItems(oldBundle.getTid(), tagIds, rid);
        }
    }

    @Override
    public List<Resource> queryReferableFiles(String keyword, int tid) {
        return resourceDAO.queryReferableFiles(keyword, tid);
    }

    public List<Resource> queryReferableFiles(String keyword, int[] tid, int offset, int size) {
        return resourceDAO.queryReferableFiles(keyword, tid, offset, size);
    }

    @Override
    public int queryReferableFilesCount(String keyword, int[] tid) {
        return resourceDAO.queryReferableFilesCount(keyword, tid);
    }

    @Override
    public void removeMarkedUserFromBundle(int bid, int tid, String uid, List<Integer> itemRids) {
        List<Long> itemRidsLong = new ArrayList<Long>();
        for (int rid : itemRids) {
            itemRidsLong.add((long) rid);
        }
        List<Resource> resList = resourceDAO.getResourceByRids(itemRidsLong);
        for (Resource res : resList) {
            res.getMarkedUserSet().remove(uid);
        }
        updateMarkedUserSet(resList);
    }

    @Override
    public void removeTagFromTagMapOfBundle(int bid, int tid, int tagId, List<Integer> itemRids) {
        List<Resource> resList = getBundleResourceList(bid, tid, itemRids);
        for (Resource res : resList) {
            Map<Integer, String> tagMap = res.getTagMap();
            if (null != tagMap) {
                tagMap.remove(tagId);
            }
            res.setTagMap(tagMap);
        }
        updateResourceTagMap(resList);
    }

    @Override
    public int update(Resource res) {
        int num = resourceDAO.update(res);
        clearResourceAmountCache(res.getTid());
        return num;
    }

    @Override
    public void updateBid(int bid, List<Long> rids) {
        resourceDAO.updateBid(bid, rids);
    }

    @Override
    public void updateBundleFileType(int bid, int tid) {
        String fileType = resourceDAO.getAllBundleItemFileType(bid, tid);
        resourceDAO.updateBundleFileType(bid, tid, fileType);
    }

    @Override
    public void updateExistBundleTagAndStarmark(int bid, int tid, int[] bundleItemRids) {
        // 新添加进Bundle，但tagMap还未更新的Resource对象
        List<Resource> itemsList = getResourcesBySphinxID(int2Long(bundleItemRids));
        List<Integer> oldItemRids = new ArrayList<Integer>();
        for(int rid:bundleItemRids){
            oldItemRids.add(rid);
        }
        Resource oldBundle = getResource(bid, tid);
        mergeAndUpdateMarkedUserSetOfBundle(bid, tid, oldItemRids);
        mergeAndUpdateTagMapOfBundle(bid, tid,oldItemRids);
        Resource updatedBundle = getResource(bid, tid);
        putBundleTag2NewBundleItem(itemsList, updatedBundle);
        putNewBundleItemTag2BundleAndOldItem(itemsList, oldBundle,oldItemRids);
        // 此处更新最新Tag的计数，为了防止计数错误
        if (null != updatedBundle.getTagMap()) {
            for (Map.Entry<Integer, String> entry : updatedBundle.getTagMap().entrySet()) {
                tagService.updateTagCount(tid, entry.getKey());
            }
        }
    }

    @Override
    public void updateMarkedUserSet(List<Resource> resList) {
        resourceDAO.updateMarkedUserSet(resList);
    }

    @Override
    public void updateNewBundleTagAndStarmark(int bid, int tid, int[] bundleItemRids) {
        // 新添加进Bundle，但tagMap还未更新的Resource对象
        List<Resource> itemsList = getResourcesBySphinxID(int2Long(bundleItemRids));
        Resource oldBundle = getResource(bid, tid);
        @SuppressWarnings("unchecked")
            List<Integer> oldItemRids = CollectionUtils.arrayToList(bundleItemRids);
        mergeAndUpdateMarkedUserSetOfBundle(bid, tid, oldItemRids);
        mergeAndUpdateTagMapOfBundle(bid, tid,oldItemRids);
        // 取出更新后的tagMap
        Resource updatedBundle = getResource(bid, tid);
        // 将所有标签（更新后的Bundle中的tagMap保存）添加到Bundle内的所有资源上
        putBundleTag2NewBundleItem(itemsList, updatedBundle);
        // 将Bundle新拥有的tag与Bundle关联起来
        addNewBundleTagRelationship(oldBundle, updatedBundle);
    }

    @Override
    public void updateOrderColumn(List<Resource> itemResList) {
        resourceDAO.updateOrderColumn(itemResList);
    }

    @Override
    public void updateResourceTagMap(List<Resource> resList) {
        resourceDAO.updateTagMap(resList);
    }

    @Override
    public List<Resource> fetchDPageBasicListByPageIncrementId(List<Long> rid) {
        return resourceDAO.fetchDPageBasicListByPageIncrementId(rid);
    }

    @Override
    public List<PageContentRender> fetchDPageContentByIncrementId(List<Long> rids) {
        return resourceDAO.fetchDPageContentByIncrementId(rids);
    }

    @Override
    public PaginationBean<Resource> getTeamRecentChange(int tid, int offset,
                                                        int size, String order, String keyWord) {
        return resourceDAO.getTeamRecentChange(tid, offset, size, keyWord, order);
    }

    @Override
    public PaginationBean<Resource> getResourceByFileType(int tid, String type, int offset, int size, String order,String keyWord) {
        return resourceDAO.getResourceByFileType(tid, type,offset,size,order,keyWord);
    }

    @Override
    public PaginationBean<Resource> query(ResourceQuery q) {
        return resourceDAO.query(q);
    }

    @Override
    public Map<Integer, Resource> getResourceMapByRids(List<Integer> rids) {
        return this.getResourceMap(getResource(rids));
    }

    public void updateTagMap(int newRid, Tag tag) {
        Resource resourceNew = getResource(newRid);
        Map<Integer, String> tagMap = resourceNew.getTagMap();
        if (CommonUtil.isNullArray(tagMap)) {
            tagMap = new HashMap<Integer, String>();
            resourceNew.setTagMap(tagMap);
        }
        tagMap.put(tag.getId(), tag.getTitle());
        List<Resource> resources = new ArrayList<Resource>();
        resources.add(resourceNew);
        updateResourceTagMap(resources);
    }

    @Override
    public void update(List<Resource> res) {
        resourceDAO.update(res);
    }

    @Override
    public long getTeamResourceSize(int tid) {
        Long l = (Long)memcachedService.get(getTeamSizeKey(tid));
        if(l==null){
            return updateTeamResSize(tid);
        }else{
            return l;
        }
    }

    private long getTeamResSizeFromDB(int tid){
        return resourceDAO.getTeamResourceSize(tid);
    }
    @Override
    public long updateTeamResSize(int tid){
        long n = getTeamResSizeFromDB(tid);
        memcachedService.set(getTeamSizeKey(tid), n);
        return n;
    }

    @Override
    public void resetTeamResSize(int tid) {
        memcachedService.remove(getTeamSizeKey(tid));
    }
    private String getTeamSizeKey(int tid){
        return "team-res-size-tid:"+tid;
    }

    @Override
    public void updateResourceStatus(Collection<Integer> rids, String status, int tid) {
        resourceDAO.updateResourceStatus(rids, status);
        clearResourceAmountCache(tid);
    }

    @Override
    public int getTeamResourceAmount(int tid) {
        Object cache = memcachedService.get(TEAM_RESOURCE_AMOUNT + tid);
        Integer amount = null;
        if(cache==null){
            amount = new Integer(resourceDAO.getTeamResourceAmount(tid));
            memcachedService.set(TEAM_RESOURCE_AMOUNT + tid, amount);
        }else{
            amount = (Integer)cache;
        }
        return amount;
    }

    /**
     * 由资源列表递归成资源树
     * @param folderRid
     * @param resourceList
     * @return
     */
    private Resource recursiveTree(int folderRid, List<Resource> list){
        Resource node = getFromList(folderRid, list);

        List<Resource> nodeList = getChildrensFromList(folderRid, list);
        for(Resource item : nodeList){
            //如果是文件夹，继续递归子目录文件
            Resource n = item.isFolder() ? recursiveTree(item.getRid(), list) : item;
            node.addChildren(n);

            //移除节点，增加查询速度
            list.remove(n);
        }
        return node;
    }

    /**
     * 从列表中查询资源
     * @param rid
     * @param list
     * @return
     */
    private Resource getFromList(int rid, List<Resource> list){
        Resource r = new Resource();
        for(int i = list.size()-1; i>=0 ; i--){
            Resource item = list.get(i);
            if(item.getRid() == rid){
                r = item;
                break;
            }
        }
        return r;
    }

    /**
     * 从列表中查询直系子文件和目录
     * @param rid
     * @param list
     * @return
     */
    private List<Resource> getChildrensFromList(int rid, List<Resource> list){
        List<Resource> resList = new ArrayList<Resource>();
        for(int i = list.size()-1; i>=0 ; i--){
            Resource item = list.get(i);
            if(item.getBid() == rid){
                resList.add(item);
            }
        }
        return resList;
    }

    /**
     * 作用：比较updatedBundle和oldBundle中的tagMap，将oldBundle中未包含的tag与Bundle<br>
     * 关联起来（在a1_tag_item中建立关联关系）
     *
     * @param oldBundle
     * @param updatedBundle
     */
    private void addNewBundleTagRelationship(Resource oldBundle, Resource updatedBundle) {
        Map<Integer, String> oldTagMap = oldBundle.getTagMap();
        Map<Integer, String> updatedTagMap = updatedBundle.getTagMap();
        int bundleRid = oldBundle.getRid();
        if (null != updatedTagMap && !updatedTagMap.isEmpty()) {
            List<Integer> tagIdList = new ArrayList<Integer>();
            for (Map.Entry<Integer, String> entry : updatedTagMap.entrySet()) {
                if (null != oldTagMap && oldTagMap.containsKey(entry.getKey())) {
                    continue;
                }
                tagIdList.clear();
                tagIdList.add(entry.getKey());
                tagService.addItems(oldBundle.getTid(), tagIdList, bundleRid);
            }
        }
    }

    private List<Resource> getBundleResourceList(int bid,int tid,List<Integer> itemRids) {
        List<Long> rids = new ArrayList<Long>();
        for(Integer rid:itemRids){
            rids.add(Long.valueOf(rid));
        }
        List<Resource> resList = resourceDAO.getResourceByRids(rids);
        Resource bundle = getResource(bid, tid);
        if (null != bundle) {
            resList.add(bundle);
        }
        return resList;
    }

    private SimpleResource getSimpleResourceFromCache(int tid,int rid){
        return (SimpleResource)memcachedService.get(RID_SIMPLE_RESOURCE+"."+tid+"."+rid);
    }

    private SimpleResource getSimpleResourceFromCache(int tid,String itemKey){
        return (SimpleResource)memcachedService.get(RID_SIMPLE_RESOURCE+"."+tid+"."+itemKey);
    }

    private List<Long> int2Long(int[] rids) {
        List<Long> result = new ArrayList<Long>();
        for (int i = 0; i < rids.length; i++) {
            result.add((long) rids[i]);
        }
        return result;
    }

    /**
     * 前提：Bundle中的tagMap拥有所有资源的tag，且itemsList中资源的tagMap没有更新<br/>
     * 作用：利用Bundle的tagMap，补齐itemsList中每个资源所欠缺的tag <br/>
     *
     * @param itemsList
     *            Bundle内资源对象集合
     * @param bundle
     *            代表Bundle的Resource对象，其tagMap包含最新的所有标签集合
     */
    private void putBundleTag2NewBundleItem(List<Resource> itemsList, Resource bundle) {
        Map<Integer, String> tagMapOfBundle = bundle.getTagMap();
        for (Resource res : itemsList) {
            Map<Integer, String> tagMapOfNewItem = res.getTagMap();
            List<Integer> tagIds = new ArrayList<Integer>();
            if (null == tagMapOfNewItem || tagMapOfNewItem.isEmpty()) {
                for (Map.Entry<Integer, String> entry : tagMapOfBundle.entrySet()) {
                    tagIds.add(entry.getKey());
                }
            } else {
                for (Map.Entry<Integer, String> entry : tagMapOfBundle.entrySet()) {
                    if (!tagMapOfNewItem.containsKey(entry.getKey())) {
                        tagIds.add(entry.getKey());
                    }
                }
            }
            tagService.addItems(bundle.getTid(), tagIds, res.getRid());
        }
    }
    private void removeSimpleResourceFromCache(int tid,int rid){
        memcachedService.remove(RID_SIMPLE_RESOURCE+"."+tid+"."+rid);
    }

    private void removeSimpleResourceFromCache(int tid,String itemKey){
        memcachedService.remove(RID_SIMPLE_RESOURCE+"."+tid+"."+itemKey);
    }


    private void setItemKeyToCache(int tid,String itemKey,SimpleResource sr){
        memcachedService.set(ITEM_SIMPLE_RESOURCE+"."+tid+"."+itemKey, sr);
    }

    private void setRidIntoCache(int tid, int rid,SimpleResource sr){
        memcachedService.set(RID_SIMPLE_RESOURCE+"."+tid+"."+rid, sr);
    }

    private void clearResourceAmountCache(int tid){
        memcachedService.remove(TEAM_RESOURCE_AMOUNT + tid);
    }
}
