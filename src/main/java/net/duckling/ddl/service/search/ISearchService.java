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
package net.duckling.ddl.service.search;

import java.util.List;

import net.duckling.ddl.util.TeamQuery;

public interface ISearchService {
    List<Long> query(TeamQuery q, String queryType);
    /**
     * 只对前60个进行优化，每次返回优化排序之后相应20个，60个之后不进行优化，每次返回20个 ，
     * @param q
     * @return
     */
    List<Long> queryPageWithOptimize(TeamQuery q);
    List<WeightPair> queryWeight(TeamQuery q, String queryType);
    int getTeamPageCount(int[] tid, String keyword);
    int getResourceCount(int[] tid, String keyword, String resourceType);
    String[] highLightTitle(String[] titles, String keyword);
    String[] highLightDigest(String[] docs,String keyword);
    List<Long> orderPageByInterest(List<WeightPair> retrieveresult, TeamQuery q);
}
