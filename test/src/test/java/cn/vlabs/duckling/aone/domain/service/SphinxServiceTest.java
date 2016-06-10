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

package cn.vlabs.duckling.aone.domain.service;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import net.duckling.ddl.service.search.impl.SearchServiceImpl;
import net.duckling.ddl.service.tobedelete.Page;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.sphx.api.SphinxClient;
import org.sphx.api.SphinxException;
import org.sphx.api.SphinxResult;

import cn.vlabs.duckling.api.umt.rmi.user.UserService;

public class SphinxServiceTest {

    private SearchServiceImpl ss = null;

    @Before
    public void setUp() throws Exception {
        String serviceURL = "http://localhost:8080/umt/services";
        ss = new SearchServiceImpl();
        us = new UserService(serviceURL);
        ss.setHost("159.226.2.176");
        ss.setPort(9312);
    }

    /**
     * @throws java.lang.Exception
     */
    @After
    public void tearDown() throws Exception {
    }

    public void testQueryDPage() throws SphinxException {
        int cid = 65572;
        SphinxClient client = new SphinxClient();
        client.SetServer("159.226.2.176", 9312);
        client.SetLimits(0, 20);
        client.SetFilter("cid", cid, false);
        // client.SetSortMode(SphinxClient.SPH_SORT_EXTENDED,
        // "modify_time asc");
        // client.SetSortMode(SphinxClient.SPH_SORT_EXTENDED, "modify_ts desc");
        // client.SetSortMode(SphinxClient.SPH_SORT_ATTR_ASC, "modify_time");
        client.SetSortMode(SphinxClient.SPH_SORT_ATTR_DESC, "modify_time");
        // client.SetSortMode(SphinxClient.SPH_SORT_ATTR_DESC, "title");
        String keyword = "lady";
        SphinxResult result = client.Query(keyword, "aone_page_main");
        System.out.println("Keyword:" + keyword);
        List<Long> keys = new ArrayList<Long>();
        if (result != null) {
            for (int i = 0; i < result.matches.length; i++) {
                System.out.println(i + 1 + ":" + result.matches[i].docId);
                keys.add(result.matches[i].docId);
            }
        }
        // getBatchDPageBasicList(keys);
    }

    public List<Page> getBatchDPageBasicList(List<Long> ids) {
        // TODO
        String fields = " r.id as pid,c.version,r.title,r.create_time,r.creator,r.belong as cid,c.change_by as modifier,c.change_time as modify_time ";
        String from = " from vwb_resource_info r,vwb_dpage_content_info c ";
        String where = "   where r.id=c.resourceId and  c.version=(select max(cc.version) from vwb_dpage_content_info cc where cc.resourceId=r.id)  ";
        String extend = " and r.id in (";
        String sql = "select " + fields + from + where + extend;
        StringBuilder sb = new StringBuilder();
        sb.append(sql);
        for (Long _id : ids) {
            sb.append(_id.toString());
            sb.append(",");
        }
        int length = sb.toString().length();
        String realSQL = sb.substring(0, length - 1) + ")";
        System.out.println(realSQL);
        return null;
    }

    @SuppressWarnings("unused")
    private UserService us = null;

    @SuppressWarnings("unchecked")
    @Test
    public void testBuildExcerpts() throws SphinxException {
        // int cid = 65572;
        SphinxClient client = new SphinxClient();
        client.SetServer("159.226.2.209", 9312);
        // client.SetLimits(0, 20);
        // client.SetFilter("cid", cid, false);
        String keyword = "中文";
        // SphinxResult result = client.Query(keyword,"aone_page_main");

        Map opt = new HashMap();
        opt.put("before_match", "<span style='color:red'>");
        opt.put("after_match", "</span>");
        opt.put("around", 25);
        opt.put("chunk_separator", "...");

        String[] docs = { "<html><span>这是一堆中文</span></html>", "中文文化博大净胜", "Hello", "Sphinx Test what ever" };
        String[] digest = client.BuildExcerpts(docs, "aone_page_main", keyword, opt);
        if (digest != null) {
            for (int i = 0; i < digest.length; i++) {
                System.out.println(i + 1 + ":" + digest[i]);
            }
        } else
            System.out.println("No Result");
        // System.out.println("Keyword:"+keyword);
        // List<Long> keys = new ArrayList<Long>();
        // if(result!=null) {
        // for (int i = 0; i < result.matches.length; i++) {
        // System.out.println(i+1+":"+result.matches[i].docId);
        // keys.add(result.matches[i].docId);
        // }
        // }
    }

}
