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

package net.duckling.ddl.service.search.impl;

import java.util.List;

import net.duckling.ddl.service.search.WeightPair;


/**
 * @author Li Zexin
 * 操作表a1_userinterest，产生用户感兴趣的其他用户
 */
public interface UserAnalysisDAO {
    /**
     * @return 返回一组用户列表，每个list元素包含用户，关注的用户，和
     * 权重（浏览关注用户创建页面词素）
     */
    List<UserInterestRecord> getBrowse();
    
    /**
     * 将经过处理后所有用户的兴趣重新存储，只负责存储功能
     */
    void creatUserInterest(final List<UserInterestRecord> recordList);

    /**
     * @param 需要查询的用户名uid
     * @return 该用户关注的所有用户
     */
    List<String> getInterest(String uid);

    /**
     * @param 查询词keyword
     * @param 发出查询的用户名uid
     * @return 该用户关注的所有用户产生的文档权重
     */
    List<WeightPair> getInterestDocWeight(String keyword, String uid);

    /**
     * @return 插入数据的数量
     */
    int creatUserSim();

    /**
     * @param 需要查询的用户名uid
     * @return 与该用户相似的所有用户
     */
    List<String> getSim(String uid);

    /**
     * @param keyword查询词
     * @param uid发出查询的用户名
     * @return 该用户相似的所有用户产生的文档权重
     */
    List<WeightPair> getSimDocWeight(String keyword,String uid);
    
}
