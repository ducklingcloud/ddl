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

import java.util.List;
import java.util.Map;

import net.duckling.ddl.service.bundle.BundleItem;

/**组合和页面，文件的关系数据库操作对象*/
public interface BundleItemDAO {
    int create(BundleItem bundleItem);
    int delete(int biid);
    int deleteAllItemInBundle(int tid, int bid);
    int deleteItemsWithIds(int[] ids);
    /**
     * 删除a1_bundle_item中的rids相关的关联关系
     * @param bid Bundle ID
     * @param tid 团队ID
     * @param rids 资源ID集合
     * @return
     */
    int deleteItemsWithoutIds(int bid, int tid, int[] rids);
    int update(int id, BundleItem bundleItem);
    int addItemsToBundle(int bid, int tid, BundleItem[] items);
    BundleItem getBundleItemById(int id);
    boolean isInBundle(int bid, int tid, int rid);
    List<BundleItem> getBundleItemsByBidTid(int bid, int tid);
    List<BundleItem> getBundleItemsByRid(int rid);
    int reorderBundleItems(int bid, int tid, Map<Integer, Integer> orderMap);
    List<BundleItem> getAllBundleItem();
    /**
     * 获取当前Bundle内顺序值的最大值，新加入Bundle的资源
     * 的顺序即为此值+1
     * @param bid
     * @param tid
     * @return
     */
    int getMaxSequenceInBundle(int bid, int tid);
    /**
     * @param bid
     * @param tid
     * @param offset
     * @param size
     * @return
     */
    List<BundleItem> getBundleItemsByBidTid(int bid, int tid, int offset, int size);
    /**
     * 将bundle中排序真假add个次序
     * @param tid
     * @param bid
     * @param add
     */
    void addBundleItemSequence(int tid,int bid,int add);
}
