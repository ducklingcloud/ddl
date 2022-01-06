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

import net.duckling.ddl.service.bundle.Bundle;

/***
 * 组合相关数据库操作
 * */
public interface BundleDAO {
    /**创建一个组合
     * @param bundle 组合实体
     *
     * **/
    int create(Bundle bundle);
    /**
     * 从a1_bundle中删除一个组合，不做其他任何操作
     * @param bid
     * @param tid
     * @return
     */
    int delete(int bid, int tid);
    /**
     * 批量删除组合，即更新状态标记
     * @param tid 团队ID
     * @param bids Bundle ID集合
     * @return
     */
    int batchDelete(int tid, List<Integer> bids);
    /**更新组合
     * @param bid 组合Id
     * @param tid 团队ID
     * @param bundle 组合实体
     * */
    int update(int bid, int tid, Bundle bundle);
    /**根据id和所属团队id取得组合
     * @param bid 组合id
     * @param tid 团队ID
     * @return Bundle实体
     * */
    Bundle getBundle(int bid, int tid);
    /**取得一个团队下的所有组合
     * @param tid 团队ID
     * @param offset 偏移量
     * @param size  数量
     * @return 队列
     * */
    List<Bundle> getBundlesOfTeam(int tid, int offset, int size);
    /**获得组合数量
     * @param title 组合名称
     * */
    int getBundleCountByTitle(int tid, String title, boolean status);
    /**
     * 检查rids集合中的资源是否已经在Bundle内。
     * @param tid 团队ID
     * @param rids 资源ID集合
     * @return rids集合内已经在Bundle内的资源rid集合
     */
    List<Integer> getRidsIfItemsInBundle(int tid, int[] rids);
}
