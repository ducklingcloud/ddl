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
package cn.vlabs.duckling.aone.content.service;

import java.util.List;

import net.duckling.ddl.service.search.ISearchService;
import net.duckling.ddl.service.search.impl.SearchServiceImpl;
import net.duckling.ddl.util.TeamQuery;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import cn.vlabs.duckling.BaseTest;

public class SearchServiceTest extends BaseTest {
    private ISearchService searchService;

    @Before
    public void setUp() throws Exception {
        searchService =  f.getBean(SearchServiceImpl.class);
    }

    @After
    public void tearDown() throws Exception {
        searchService = null;
    }

    @Test
    public void testTeamQuery() {
        TeamQuery q = new TeamQuery();
        // int[] tgids = new int[]{3,7};
        // q.setTagIds(tgids);
        // q.setTid(4);
        q.setOffset(0);
        q.setSize(100);
        // q.setDate("thismonth");
        q.setType("Picture");
        // q.setKeyword("页面");
        // q.setFilter("untaged");
        List<Long> result = searchService.query(q, TeamQuery.QUERY_FOR_RESOURCE);
        if(result!=null)
        for (Long l : result)
            System.out.println("docId:" + l);
    }

    @Test
    public void testPageQuery() {
        TeamQuery q = new TeamQuery();
        // q.setTid(1);
        q.setOffset(0);
        q.setSize(100);
        q.setKeyword("页面");
        q.setDate("thismonth");
        List<Long> result = searchService.query(q, TeamQuery.QUERY_FOR_PAGECONTENT);
        if(result!=null){
        	for (Long l : result)
        		System.out.println("docId:" + l);
        }
    }
}
