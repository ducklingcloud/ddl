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
package net.duckling.ddl.service.bundle.impl;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import net.duckling.ddl.constant.LynxConstants;
import net.duckling.ddl.service.bundle.Bundle;
import net.duckling.ddl.service.bundle.BundleItem;
import net.duckling.ddl.service.bundle.IBundleService;
import net.duckling.ddl.service.resource.IResourceService;
import net.duckling.ddl.service.resource.ITagService;
import net.duckling.ddl.service.resource.Resource;
import net.duckling.ddl.service.resource.ResourceBuilder;
import net.duckling.ddl.service.resource.ResourceOperateService;
import net.duckling.ddl.service.resource.SimpleResource;
import net.duckling.ddl.service.resource.Tag;
import net.duckling.ddl.service.resource.TagItem;
import net.duckling.ddl.service.user.AoneUserService;
import net.duckling.ddl.util.ArrayAndListConverter;

import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Isolation;
import org.springframework.transaction.annotation.Transactional;


@Service
public class BundleServiceImpl implements IBundleService {

    private static final Logger LOG = Logger.getLogger(BundleServiceImpl.class);
    @Autowired
    private BundleDAO bundleDAO;
    @Autowired
    private BundleItemDAO bundleItemDAO;
    @Autowired
    private IResourceService resourceService;
    @Autowired
    private AoneUserService aoneUserService;
    @Autowired
    private ResourceOperateService resourceOperateService;
    @Autowired
    private ITagService tagService;
    @Override
    public int createBundle(String title, int tid, String creator, String description) {
        Date curDate = new Date();
        Bundle bundle = new Bundle();
        bundle.setTid(tid);
        bundle.setTitle(title);
        bundle.setStatus(LynxConstants.STATUS_AVAILABLE);
        bundle.setCreator(creator);
        bundle.setCreateTime(curDate);
        bundle.setLastEditor(creator);
        bundle.setLastEditTime(curDate);
        bundle.setDescription(description);
        int bid = bundleDAO.create(bundle);
        bundle.setBid(bid);
        Resource res = ResourceBuilder.build(bundle);
        res.setCreatorName(aoneUserService.getUserNameByID(creator));
        res.setLastEditorName(aoneUserService.getUserNameByID(creator));
        resourceService.create(res);
        return bid;
    }
    public int addItems(int tid, int tagId, int[] rids) {
        List<Long> newRids = new ArrayList<Long>();
        int size = rids.length;
        for (int i = 0; i < size; i++) {
            newRids.add((long) rids[i]);
        }
        int result = tagService.batchUpdateWithTag(tid, tagId, newRids);
        tagService.updateTagCount(tid, tagId);
        return result;
    }

    public int removeTagGroup(int tid, int tagGroupId) {
        List<Tag> tagList = tagService.getTagsByGroupId(tagGroupId, tid);
        if (null == tagList || tagList.size() <= 0) {
            return tagService.deleteTagGroup(tagGroupId);
        }
        int size = tagList.size();
        int[] tgids = new int[tagList.size()];
        for (int i = 0; i < size; i++) {
            tgids[i] = tagList.get(i).getId();
        }
        // 查询标签组内的 所有标签关联的tagItem，更新这些resource的TagMap
        List<TagItem> items = tagService.getTagItems(tgids);
        if (null != items && items.size() > 0) {
            for (TagItem item : items) {
                removeItem(item.getRid(), item.getTgid());
            }
        }
        tagService.deleteTagByTagIds(tgids);
        tagService.deleteTagGroup(tagGroupId);
        return 1;
    }

    public int removeTagItemsByTgids(int[] tgids) {
        for (int i = 0; i < tgids.length; i++) {
            List<TagItem> items = tagService.getTagItems(tgids[i]);
            for (TagItem tm : items) {
                removeItem(tm.getRid(), tm.getTgid());
            }
        }
        tagService.deleteTagItemByTagIds(tgids);
        tagService.deleteTagByTagIds(tgids);
        return 1;
    }
    /**
     * 更新Tag Title时需要更新所有与该Tag相关的Resource的TagMap
     * 
     * @param tagId
     *            标签Id
     * @param title
     *            标签标题
     */
    public void updateAllResourceWithTagTitle(int tagId, String title) {
        List<TagItem> items = tagService.getItems(tagId);
        if (null != items && items.size() > 0) {
            for (TagItem item : items) {
                Resource res = resourceService.getResource(item.getRid());
                Map<Integer, String> tagMap = res.getTagMap();
                if (null != tagMap && !tagMap.isEmpty() && tagMap.containsKey(tagId)) {
                    tagMap.put(tagId, title);
                }
                res.setTagMap(tagMap);
                resourceService.updateResourceTagMap(Arrays.asList(new Resource[] { res }));
                boolean isInBundle = isInBundle(res.getBid(), res.getTid(), res.getRid());
                if (isInBundle) {
                    List<Integer> itemRids = getRidsOfBundleAndItems(res.getBid(), res.getTid());
                    resourceService.mergeAndUpdateTagMapOfBundle(res.getBid(), res.getTid(), itemRids);
                }
            }
        }
    }

    public void removeItem(Integer rid, Integer tagId) {
        Resource res = resourceService.getResource(rid);
        if (null == res) {
            LOG.error("Resource not found while remove tag item! rid=" + rid);
            return;
        }
        int tid = res.getTid();
		Map<Integer, String> tagMap = res.getTagMap();
		if (null != tagMap) {
			tagMap.remove(tagId);
		}
		res.setTagMap(tagMap);
		resourceService.updateResourceTagMap(Arrays.asList(new Resource[] { res }));
		tagService.deleteTagItems(Arrays.asList(new Integer[] { rid }), tagId);
        tagService.updateTagCount(tid, tagId);
    }
    public void deleteTagResource(int tid, int bid) {
        Resource bundleRes = resourceService.getResource(bid, tid);
        if (null != bundleRes) {
            int rid = bundleRes.getRid();
            Map<Integer, String> tagMap = bundleRes.getTagMap();
            if (tagMap != null) {
                for (Map.Entry<Integer, String> entry : tagMap.entrySet()) {
                    removeItem(rid, entry.getKey());
                    tagService.updateTagCount(bundleRes.getTid(), entry.getKey());
                }
            }
        }
    }
    @Override
    public int updateBundle(Bundle bundle) {
        Date curDate = new Date();
        bundle.setLastEditTime(curDate);
        bundleDAO.update(bundle.getBid(), bundle.getTid(), bundle);
        Resource res = ResourceBuilder.build(bundle);
        res.setLastEditor(bundle.getLastEditor());
        res.setLastEditorName(aoneUserService.getUserNameByID(bundle.getLastEditor()));
        resourceService.update(res);
        updateItemOrderColumn(bundle.getBid(), bundle.getTid());
        return bundle.getId();
    }

    @Override
    public int disbandBundle(int bid, int tid) {       
        List<BundleItem> items = bundleItemDAO.getBundleItemsByBidTid(bid, tid);
        bundleItemDAO.deleteAllItemInBundle(tid, bid);
        List<Long> rids = BundleHelper.getRidsFromBundleItems(items);
        resourceService.updateBid(0, rids);
        deleteTagResource(tid, bid);
        resourceService.delete(bid, tid, LynxConstants.TYPE_BUNDLE);
        updateItemOrderColumn(rids);
        return bundleDAO.delete(bid, tid);
    }

    @Deprecated
    @Override
    public int deleteBundle(int bid, int tid) {
        List<Integer> rids = getRidsOfBundleAndItems(bid, tid);
        Resource bundle = resourceService.getResource(bid, tid);
        int[] itemRids = new int[rids.size()];
        for (int i = 0; i < rids.size(); i++) {
            int rid = rids.get(i);
            if (rid != bundle.getRid()) {
                itemRids[i] = rids.get(i);
            }
        }
        removeBundleItems(bid, tid, itemRids);

        Map<String, List<Integer>> typeMap = resourceService.getResourceTypeMap(tid, rids);

        batchDelete(tid, typeMap.get(LynxConstants.TYPE_BUNDLE));
        deleteTagResource(tid, bid);
//        pageService.batchDelete(tid, typeMap.get(LynxConstants.TYPE_PAGE));
//        resourceOperateService.batchDeleteFile(tid, typeMap.get(LynxConstants.TYPE_FILE));
//        resourceService.batchDelete(tid, rids);
        int result = bundleDAO.delete(bid, tid);
        return result;
    }

    @Override
    public int batchDelete(int tid, List<Integer> bids) {
        if (null == bids || bids.isEmpty()) {
            return 0;
        }
        return bundleDAO.batchDelete(tid, bids);
    }

    @Override
    public Bundle getBundle(int bid, int tid) {
        return bundleDAO.getBundle(bid, tid);
    }

    @Override
    public boolean isInBundle(int bid, int tid, int rid) {
        return bundleItemDAO.isInBundle(bid, tid, rid);
    }

    @Override
    @Transactional(isolation = Isolation.REPEATABLE_READ)
    public int addBundleItems(int bid, int tid, int[] rids) {
        if (rids != null && rids.length > 0) {
            int[] canAddRids = getRidsNotInBundle(tid, rids); // 剔除rids中已经在其他bundle的rid
            int size = canAddRids.length;
            if (size <= 0) {
                return 0;
            }
            bundleItemDAO.addBundleItemSequence(tid, bid, size);
            int sequence = 0;
            BundleItem[] items = new BundleItem[size];
            List<Long> ridsLong = new ArrayList<Long>();
            for (int i = 0; i < size; i++) {
                BundleItem item = BundleHelper.buildBundleItem(tid, canAddRids[i], bid);
                item.setSequence(++sequence);
                items[i] = item;
                ridsLong.add((long) canAddRids[i]);
            }
            resourceService.updateBid(bid, ridsLong);
            int result = bundleItemDAO.addItemsToBundle(bid, tid, items);
            updateBundle(bundleDAO.getBundle(bid, tid));
            resourceService.updateBundleFileType(bid, tid);
            return result;
        }
        return 0;
    }

    private int[] getRidsNotInBundle(int tid, int[] rids) {
        int[] result = null;
        if (null != rids) {
            List<Integer> existItems = bundleDAO.getRidsIfItemsInBundle(tid, rids);
            result = new int[rids.length - existItems.size()];
            int k = 0;
            for (int rid : rids) {
                if (!existItems.contains(Integer.valueOf(rid))) {
                    result[k++] = rid;
                }
            }
        }
        return result;
    }

    @Override
    public int removeBundleItems(int bid, int tid, int[] rids) {
        if (rids != null && rids.length > 0) {
            int size = rids.length;
            List<Long> ridsLong = new ArrayList<Long>();
            for (int i = 0; i < size; i++) {
                ridsLong.add((long) rids[i]);
            }
            resourceService.updateBid(LynxConstants.DEFAULT_BID, ridsLong);
            int result = bundleItemDAO.deleteItemsWithoutIds(bid, tid, rids);
            updateBundle(bundleDAO.getBundle(bid, tid));
            resourceService.updateBundleFileType(bid, tid);
            updateItemOrderColumn(ArrayAndListConverter.convert2Long(rids));
            return result;
        }
        return 0;
    }

    @Override
    public List<BundleItem> getBundleItems(int bid, int tid) {
        return bundleItemDAO.getBundleItemsByBidTid(bid, tid);
    }

    @Override
    public List<BundleItem> getBundleItems(int bid, int tid, int offset, int size) {
        return bundleItemDAO.getBundleItemsByBidTid(bid, tid, offset, size);
    }

    @Override
    public int reorderBundleItems(int bid, int tid, Map<Integer, Integer> orderMap) {
        return bundleItemDAO.reorderBundleItems(bid, tid, orderMap);
    }

    @Override
    public List<Bundle> getBundlesInTeam(int tid, boolean status) {
        return bundleDAO.getBundlesOfTeam(tid, 0, 0);
    }

    @Override
    public int getBundleCountByTitle(int tid, String title, boolean status) {
        return bundleDAO.getBundleCountByTitle(tid, title, status);
    }

    @Override
    public List<BundleItem> getBundleItems(int rid) {
        return bundleItemDAO.getBundleItemsByRid(rid);
    }

    @Override
    public int createBundleAndPutItems(String uid, String title, 
            int[] rids, int tid, String description) {
        int bid = createBundle(title, tid, uid, description);
        SimpleResource sr[] = new SimpleResource[rids.length];
        for (int i = 0; i < rids.length; i++) {
            sr[i] = resourceService.getSimpleResource(tid,rids[i]);
        }
        saveBundleItem(bid, sr, tid);
        return bid;
    }

    private void saveBundleItem(int bid, SimpleResource[] simples, int tid) {
        Set<Integer> ridSet = new HashSet<Integer>();
        for (int i = 0; i < simples.length; i++) {
            SimpleResource sr = simples[i];
            if (sr.equalsBundleType()) {
                List<BundleItem> tempItems = getBundleItems(sr.getRid());
                if (tempItems != null) {
                    for (BundleItem b : tempItems) {
                        ridSet.add(b.getRid());
                    }
                    disbandBundle(sr.getRid(), tid);
                }
            } else {
                ridSet.add(sr.getRid());
            }
        }
        int size = ridSet.size();
        if (size > 0) {
            int[] rids = new int[ridSet.size()];
            int i = 0;
            for (Integer rid : ridSet) {
                rids[i++] = rid;
            }
            addBundleItems(bid, tid, rids);
        }
    }

    /* 
     * 更新Bundle信息
     * 依然是因为冗余信息
     */
    @Override
    public void updateBundleHasThisResource(int rid) {
        Resource item = resourceService.getResource(rid);
        if (null != item && item.getBid() != 0) {
            int bid = item.getBid();
            int tid = item.getTid();
            // 更新Bundle信息
            Bundle bundle = bundleDAO.getBundle(bid, tid);
            bundle.setLastEditor(item.getLastEditor());
            bundle.setLastEditTime(item.getLastEditTime());
            bundleDAO.update(bid, tid, bundle);
            // 更新Resource信息
            Resource bundleRes = resourceService.getResource(bid, tid);
            bundleRes.setLastEditor(item.getLastEditor());
            bundleRes.setLastEditorName(item.getLastEditorName());
            bundleRes.setLastEditTime(item.getLastEditTime());
            resourceService.update(bundleRes);
            resourceService.updateBundleFileType(bid, tid);
        }
    }

    @Override
    public List<Integer> getRidsOfBundleAndItems(int bid, int tid) {
        List<BundleItem> items = getBundleItems(bid, tid);
        Resource bundle = resourceService.getResource(bid, tid);
        List<Integer> rids = new ArrayList<Integer>();
        if (null != bundle) {
            rids.add(bundle.getRid());
        }
        for (BundleItem item : items) {
            rids.add(item.getRid());
        }
        return rids;
    }

    @Override
    public int getMaxSequenceInBundle(int bid, int tid) {
        return bundleItemDAO.getMaxSequenceInBundle(bid, tid);
    }

    /**
     * 将原来在Bundle内的资源，重置它们的排序列（orderTitle, orderDate）为自己的title, last_edit_time
     * 
     * @param rids
     */
    private void updateItemOrderColumn(List<Long> rids) {
        if (null == rids || rids.isEmpty()) {
            return;
        }
        List<Resource> items = resourceService.getResourcesBySphinxID(rids);
        for (Resource item : items) {
            item.setOrderDate(item.getLastEditTime());
            item.setOrderTitle(item.getTitle());
        }
        resourceService.updateOrderColumn(items);
    }

    /**
     * 更新Bundle内所有资源的排序列（orderTitle, orderDate）为Bundle的orderTitle和orderDate
     * 
     * @param bid
     * @param tid
     */
    private void updateItemOrderColumn(int bid, int tid) {
        Resource bundle = resourceService.getResource(bid, tid);
        List<Integer> items = getRidsOfBundleAndItems(bid, tid);
        items.remove(new Integer(bundle.getRid()));
        List<Resource> itemResList = resourceService.getResourcesBySphinxID(int2Long(items));
        updateOrderColumn(itemResList, bundle);
        resourceService.updateOrderColumn(itemResList);
    }

    private List<Long> int2Long(List<Integer> ids) {
        List<Long> result = new ArrayList<Long>();
        if (null != ids && !ids.isEmpty()) {
            for (int id : ids) {
                result.add((long) id);
            }
        }
        return result;
    }

    private void updateOrderColumn(List<Resource> items, Resource bundle) {
        if (null != items && !items.isEmpty() && null != bundle) {
            String orderTitle = bundle.getOrderTitle();
            Date orderDate = bundle.getOrderDate();
            for (Resource item : items) {
                item.setOrderTitle(orderTitle);
                item.setOrderDate(orderDate);
            }
        }
    }
}
