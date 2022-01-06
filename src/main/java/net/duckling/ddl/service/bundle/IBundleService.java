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
package net.duckling.ddl.service.bundle;

import java.util.List;
import java.util.Map;

@Deprecated
public interface IBundleService {

    int createBundle(String title,int tid, String creator, String description);
    int updateBundle(Bundle bundle);
    List<Bundle> getBundlesInTeam(int tid, boolean status);
    int getBundleCountByTitle(int tid, String title, boolean status);
    /**
     * 解散Bundle，将进行如下操作：<br/>
     * 1) 更新Bundle记录的status，并<b>将Bundle内所有资源的bid置0</b>; <br/>
     * 2) 删除a1_bundle_item中的关联记录;<br/>
     * 3) 删除与Bundle本身关联的标签记录，并更新所有标签的计数。
     * @param bid Bundle ID
     * @param tid 团队ID
     * @return
     */
    int disbandBundle(int bid, int tid);
    /**
     * 删除Bundle，此操作仅更新Bundle以及Bundle内所有资源的status;
     * 并更新与Bundle关联的Tag的计数。并不删除任何记录
     * @param bid Bundle ID
     * @param tid 团队ID
     * @return
     */
    int deleteBundle(int bid, int tid);
    /**
     * 批量删除Bundle。此操作更新Bundle及Bundle内资源的状态标记；
     * 删除Bundle内资源与Bundle的关联关系；删除Bundle即Bundle内
     * 资源与Tag的关联关系。
     * @param tid
     * @param bids
     * @return
     */
    int batchDelete(int tid, List<Integer> bids);
    Bundle getBundle(int bid, int tid);
    boolean isInBundle(int bid, int tid, int rid);
    /**
     * 将rids中的资源添加到a1_bundle_item中，
     * 并更新相应资源的bid，以及其他基本信息。
     * 如果该rids中有资源已经被包含在其他Bundle内，则该资源将不会被加入到bid的Bundle内。
     * @param bid Bundle ID
     * @param tid 团队ID
     * @param rids 资源ID集合
     * @return
     */
    int addBundleItems(int bid, int tid, int[] rids);
    /**
     * 删除Bundle中的某些资源
     * @param bid Bundle ID
     * @param tid 团队ID
     * @param rids 资源ID集合
     * @return
     */
    int removeBundleItems(int bid, int tid, int[] rids);
    List<BundleItem> getBundleItems(int bid, int tid);
    List<BundleItem> getBundleItems(int bid, int tid,int offset,int size);
    List<BundleItem> getBundleItems(int rid);
    int reorderBundleItems(int bid, int tid, Map<Integer, Integer> orderMap);

    int createBundleAndPutItems(String uid,String title,int[] rids,int tid,String desc);
    /**
     * 更新包含资源rid的Bundle信息：将bundle的最后修改者、最后修改时间修改为该资源的
     * 最后修改者和时间。并重新计算和更新Bundle的file_type字段。
     * @param rid 资源的Rid，该资源属于Bundle
     */
    void updateBundleHasThisResource(int rid);
    /**
     * 获取Bundle及其内部资源的Rid集合
     * @param bid Bundle ID
     * @param tid 团队ID
     * @return List<Integer> rids
     */
    List<Integer> getRidsOfBundleAndItems(int bid, int tid);
    /**
     * 获取当前Bundle内顺序值的最大值，新加入Bundle的资源
     * 的顺序即为此值+1
     * @param bid
     * @param tid
     * @return
     */
    int getMaxSequenceInBundle(int bid, int tid);


    /**删除TagItem时需要处理Bundle的Tag关联关系*/
    public void removeItem(Integer rid, Integer tagId);

    /**更新Tag Title时需要更新所有与该Tag相关的Resource的TagMap*/
    public void updateAllResourceWithTagTitle(int tagId, String title);

    /**
     * 给一系列资源打上同一个标签,修改a1_tag_item并修改标签计数，
     * 若资源为Bundle，则给Bundle内所有资源打上同一个标签，
     * <b>若资源已存在该标签，则不会重复产生a1_tag_item记录</b>
     * @param tid 团队ID
     * @param tagId 标签ID
     * @param rids 资源ID集合
     * @return
     */
    public int addItems(int tid, int tagId, int[] rids);

    public int removeTagGroup(int tid, int tagGroupId);

    public int removeTagItemsByTgids(int[] tgids);

    public void deleteTagResource(int tid, int bid);
}
