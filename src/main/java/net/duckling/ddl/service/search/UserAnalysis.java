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
/**
 *
 */
package net.duckling.ddl.service.search;

import java.util.List;

import net.duckling.ddl.service.search.impl.UserInterestRecord;



/**
 * @author lizexin
 * 获得用户所关注的用户
 */
public interface UserAnalysis {

    /**
     * 根据浏览日志，获取用户感兴趣的其他用户
     */
    void analysisUser();
    /**
     * @param 制定用户名uid
     * @return 当前用户感兴趣的前30%用户
     */
    List<String> getInterest(String uid);
    /**
     * @param keyword 查询词
     * @param uid 发出查询的用户
     * @return 发出查询的用户关注的用户产生的文档权重
     */
    List<WeightPair> getInterestDocWeight(String keyword, String uid);
    /**
     * @param interest 当前用户感兴趣用户的列表
     * @param index 被感兴趣用户在兴趣列表中的位置
     * @return 被刚兴趣的用户的权重
     */
    double getFactor(List<UserInterestRecord> interest,int index);
    /**
     * @param 制定用户名uid
     * @return 当前用户感相似的前30%用户
     */
    List<String> getSim(String uid);
    /**
     * @param keyword 查询词
     * @param uid 发出查询的用户
     * @return 发出查询的用户相似的用户产生的文档权重
     */
    List<WeightPair> getSimDocWeight(String keyword, String uid);
}
