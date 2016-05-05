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

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.zip.CRC32;

import net.duckling.ddl.constant.LynxConstants;
import net.duckling.ddl.service.search.ISearchService;
import net.duckling.ddl.service.search.SearchLogAnalysis;
import net.duckling.ddl.service.search.UserAnalysis;
import net.duckling.ddl.service.search.WeightPair;
import net.duckling.ddl.service.tobedelete.File;
import net.duckling.ddl.util.TeamQuery;
import net.duckling.ddl.util.TeamQueryUtil;

import org.apache.log4j.Logger;
import org.sphx.api.SphinxClient;
import org.sphx.api.SphinxException;
import org.sphx.api.SphinxResult;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
@Service
public class SearchServiceImpl implements ISearchService {
    
    private static final int AROUND = 15;
    
    private static final String ITEM_TYPE = "item_type";

    private static final int LIMIT = 120;
    private static final Logger LOG = Logger.getLogger(SearchServiceImpl.class);
    private static final int TITLE_WEIGHT = 3;
    private static final int UINX_TIME = 1000;
    private static final int MAX_COUNT = 1000;
    private static  final int MAX_REORDER_SIZE = 60;
    @Value("${duckling.sphinx.host}")
    private String host;
    @Value("${duckling.sphinx.port}")
    private int port;
    @Autowired
    private SearchLogAnalysis searchLogAnalysis;
    @Autowired
    private UserAnalysis userAnalysis;

    private SphinxClient createClient() throws SphinxException {
        SphinxClient client = new SphinxClient();
        client.SetServer(host, port);
        return client;
    }

    /**
     * @param finalDocIds
     *            应经排好序的id-weight对
     * @param q
     *            搜索查询
     * @return 返回搜索对应的20个结果
     */
    private List<Long> cut20ResultForReturn(List<Map.Entry<Long, Integer>> finalDocIds, TeamQuery q) {
        if (finalDocIds == null || q == null || finalDocIds.size() < q.getOffset()) {
            return Collections.emptyList();
        }
        if (finalDocIds.size() < (q.getOffset() + 20)) {
            finalDocIds = finalDocIds.subList(q.getOffset(), finalDocIds.size());
        } else {
            finalDocIds = finalDocIds.subList(q.getOffset(), (q.getOffset() + 20));
        }

        List<Long> Doc = new ArrayList<Long>();
        for (Entry<Long, Integer> e : finalDocIds) {
            Doc.add(e.getKey());
        }
        return Doc;
    }

    private String generateKeywords(SphinxClient client, TeamQuery q) throws SphinxException {
        StringBuilder result = new StringBuilder();
        String keyword = q.getKeyword();
        if (null != keyword && !"".equals(keyword)) {
            result.append(SphinxClient.EscapeString(keyword));
        }
        if (null != q.getType() && !"".equals(q.getType())) {
            String type = q.getType();
            if (LynxConstants.TYPE_FOLDER.equals(type) || LynxConstants.TYPE_PAGE.equals(type)
                    || LynxConstants.TYPE_FILE.equals(type)) {
                client.SetFilter(ITEM_TYPE, getCRC32Value(type), false);
            } else if (File.isOfficeFileTypeForSearch(type)) {
                client.SetFilter(ITEM_TYPE, getCRC32Value(LynxConstants.TYPE_FILE), false);
                result.append(" @filetype " + type + "|" + type + "x");
            } else if (File.isPdfFileTypeForSearch(type)) {
                client.SetFilter(ITEM_TYPE, getCRC32Value(LynxConstants.TYPE_FILE), false);
                result.append(" @filetype " + type);
            } else if (LynxConstants.SEARCH_TYPE_PICTURE.equals(type)) {
                client.SetFilter(ITEM_TYPE, getCRC32Value(LynxConstants.TYPE_FILE), false);
                result.append(" @filetype png|jpg|jpeg|gif|bmp|tiff");
            }
        }
        String filter = q.getFilter();
        if ("untaged".equals(filter)) {
            client.SetFilter("tagexist", 0, false);
        } else {
            if (q.getTagIds() != null && q.getTagIds().length != 0) {
                int len = q.getTagIds().length;
                client.SetFilter("tagexist", 1, false);
                result.append(" @tags ");
                for (int i = 0; i < len; i++) {
                    int tagId = q.getTagIds()[i];
                    result.append(tagId + " ");
                }
                result.replace(result.length() - 1, result.length(), "");
            }
        }
        return result.toString();
    }

    private long getCRC32Value(String s) {
        CRC32 c = new CRC32();
        c.update(s.getBytes());
        return c.getValue();
    }

    private String getIndexName(String queryType) {
        if ("page".equals(queryType)) {
            return "aone_page,aone_page_delta";
        } else if ("resource".equals(queryType)) {
            return "aone_resource,aone_resource_delta";
        } else {
            return null;
        }
    }

    private Map<String, Integer> getWeightOfPageFields() {
        Map<String, Integer> weightMap = new HashMap<String, Integer>();
        weightMap.put("title", TITLE_WEIGHT);
        weightMap.put("content", 1);
        weightMap.put("creator", 1);
        weightMap.put("last_editor", 1);
        weightMap.put("last_edit_time", 1);
        return weightMap;
    }

    private String[] heightKeyword(String[] titles, String keyword) {
        String[] result = null;
        try {
            SphinxClient client = createClient();
            try {
                Map<String, Object> opt = new HashMap<String, Object>();
                opt.put("before_match", "<em>");
                opt.put("after_match", "</em>");
                opt.put("limit", LIMIT);
                opt.put("around", AROUND);
                opt.put("chunk_separator", "...");
                result = client.BuildExcerpts(titles, "aone_page", keyword, opt);
            } finally {
                client.Close();
            }
        } catch (SphinxException e) {
            LOG.error(e);
            LOG.debug("Detail is ", e);
        }
        return result;
    }

    /**
     * 通过相似的用户得到文档id—weight 并且累加到docMaps 及记录各类因子优化后的权重
     * 
     * @param docMaps
     *            所有的 id-weight 键值对
     * @param q
     *            查询
     */
    private void optimizedWeightBySim2(LinkedHashMap<Long, Integer> docMaps, TeamQuery q) {
        List<WeightPair> whtPairList = userAnalysis.getSimDocWeight(q.getKeyword(), q.getQueryer());
        updateWeight(docMaps, whtPairList, 1);// 更新文档权重
    }

    /**
     * 通过文档热度 得到文档id—weight 并且累加到docMaps 及记录各类因子优化后的权重
     * 
     * @param docMaps
     *            所有的 id-weight 键值对
     * @param retrieveresult
     *            文档id的list
     */
    private void optimizeWeightByDocHot(LinkedHashMap<Long, Integer> docMaps, List<Long> retrieveresult) {
        List<WeightPair> whtPairList = searchLogAnalysis.getViewDocWeight(retrieveresult);// 获取返回结果中每个文档的文档热度
        updateWeight(docMaps, whtPairList, 1);// 更新文档权重
    }

    /**
     * 通过感兴趣的用户得到文档id—weight 并且累加到docMaps 及记录各类因子优化后的权重
     * 
     * @param docMaps
     *            所有的 id-weight 键值对
     * @param q
     *            查询
     */
    private void optimizeWeightByIterest2(LinkedHashMap<Long, Integer> docMaps, TeamQuery q) {
        // 获取当前用户感兴趣的用户产生的文档权重，查询一次数据库
        List<WeightPair> whtPairList = userAnalysis.getInterestDocWeight(q.getKeyword(), q.getQueryer());
        updateWeight(docMaps, whtPairList, 5);// 更新文档权重
    }

    /**
     * 通过用户自身的点击行为得到文档id—weight 并且累加到docMaps 及记录各类因子优化后的权重
     * 
     * @param docMaps
     *            所有的 id-weight 键值对
     * @param keyword
     *            关键词
     * @param uid
     *            当前进行查询的用户
     * @return
     */
    private void optimizeWeightByOwnClick2(LinkedHashMap<Long, Integer> docMaps, String keyword, String uid) {
        List<WeightPair> whtPairList = searchLogAnalysis.getOwnDocWeight(keyword, uid);
        updateWeight(docMaps, whtPairList, 10);// docList是个临时变量，最存储某一个因子下的文档权重对
    }

    private List<Long> retrieveResult(SphinxResult result) {
        if (result == null) {
            return null;
        }
        List<Long> keys = new ArrayList<Long>();
        for (int i = 0; i < result.matches.length; i++) {
            keys.add(result.matches[i].docId);
        }
        return keys;
    }

    private List<WeightPair> retrieveWeightResult(SphinxResult result) {
        if (result == null) {
            return null;
        }
        /*
         * 让文档相关度的值严格递减。因为利用sphnix考虑时间排序时，会出现小权重的文档排在当权重文档钱的情况
         * 通过sphnix得到的结果，其权重严格按照weight降序分布
         */
        List<WeightPair> keys = new ArrayList<WeightPair>();
        for (int i = 0; i < result.matches.length; i++) {
            int weight = result.matches[i].weight;
            for (int j = i; j < result.matches.length; j++) {
                if (result.matches[i].weight < result.matches[j].weight) {
                    weight = result.matches[j].weight;
                }
            }
            WeightPair wei = new WeightPair(result.matches[i].docId, weight);
            keys.add(wei);
        }
        return keys;
    }

    private void setDateFilter(SphinxClient client, String date) throws SphinxException {
        Long[] timeArray = TeamQueryUtil.convertTimeRange(date);
        if (timeArray[0] != 0 && timeArray[1] != 0) {
            long begin = timeArray[0] / UINX_TIME;
            long end = timeArray[1] / UINX_TIME;
            client.SetFilterRange("last_edit_time", begin, end, false);
        }
    }

    private void setMatchMode(SphinxClient client) throws SphinxException {
        client.SetRankingMode(SphinxClient.SPH_RANK_PROXIMITY_BM25);
        client.SetMatchMode(SphinxClient.SPH_MATCH_EXTENDED);
    }

    /**
     * 设置结果排序方式
     * 
     * @param client
     * @param q
     * @throws SphinxException
     */
    private void setSortMode(SphinxClient client, TeamQuery q) throws SphinxException {
        String orderTitle = q.getOrderTitle();
        String orderDate = q.getOrderDate();
        boolean titleEmpty = TeamQueryUtil.isEmpty(orderTitle);
        boolean dateEmpty = TeamQueryUtil.isEmpty(orderDate);
        if (dateEmpty && titleEmpty) {
            client.SetSortMode(SphinxClient.SPH_SORT_EXPR, "@weight + (ln(@id))");
        } else {
            String sort = "";
            if (!dateEmpty) {
                sort += "order_date ";
                sort += TeamQueryUtil.isAsc(orderDate, "date") ? "ASC" : "DESC";
            } else if (!titleEmpty) {
                sort += "order_title ";
                sort += TeamQueryUtil.isAsc(orderTitle, "title") ? "ASC" : "DESC";
            }
            sort += (("".equals(sort)) ? "" : ", ") + "@weight";
            client.SetSortMode(SphinxClient.SPH_SORT_EXTENDED, sort);
        }
    }

    // 设置搜索需要过滤的团队ID
    private void setTeamFilter(SphinxClient client, int[] tids) throws SphinxException {
        client.SetFilter("tid", tids, false);
    }

    /**
     * 该函数用来重新排序，按照docMaps中的weight降序排列，选择排序，该排序必须是稳定的，
     * 
     * @param docMaps
     *            文档-权重键值对
     * @return 按照其权重排序后的，有序结果
     */
    private List<Map.Entry<Long, Integer>> sort(LinkedHashMap<Long, Integer> docMaps) {
        List<Map.Entry<Long, Integer>> finalDoc = new ArrayList<Map.Entry<Long, Integer>>(docMaps.entrySet());
        for (int i = 0; i < finalDoc.size(); i++) {
            int max = -1;
            int temp = -1;
            for (int j = i; j < finalDoc.size(); j++) {
                if (finalDoc.get(j).getValue() > max) {
                    temp = j;
                    max = finalDoc.get(j).getValue();
                }
            }
            if (max > -1) {
                Map.Entry<Long, Integer> tempEntry = finalDoc.get(temp);
                finalDoc.remove(temp);
                finalDoc.add(i, tempEntry);
            }
        }
        return finalDoc;
    }

    private LinkedHashMap<Long, Integer> tansferListToMap(List<WeightPair> sphinxresult2) {
        LinkedHashMap<Long, Integer> result = new LinkedHashMap<Long, Integer>();
        for (WeightPair weight : sphinxresult2) {
            result.put(weight.getId(), weight.getWeight());
        }
        return result;
    }

    /**
     * @param retrieveresult
     * @return
     */
    private List<Long> tansferWeightToList(List<WeightPair> retrieveresult) {
        List<Long> result = new ArrayList<Long>();
        for (WeightPair weight : retrieveresult) {
            result.add(weight.getId());
        }
        return result;
    }

    /**
     * 将该因子下的得到的文档权重累加到全局的权重中rid-weight
     * 
     * @param docMaps
     *            全局变量，记录文档编号及其权重，将每个优化因子产生的权重累加到这里
     * @param whtPairList
     *            记录每个因子产生的id-weight对
     * @param factor
     *            每个因子的自身的权重
     */
    private void updateWeight(LinkedHashMap<Long, Integer> docMaps, List<WeightPair> whtPairList, int factor) {
        if (whtPairList == null)
            return;
        // id->weight
        for (WeightPair whtPair : whtPairList) {
            Long tmpId = whtPair.getId();
            int tmpWeight = whtPair.getWeight();
            if (docMaps.containsKey(tmpId)) {
                int origiWeight = docMaps.get(tmpId);
                docMaps.put(tmpId, origiWeight + (tmpWeight * factor));
            }
        }
    }

    @Override
    public int getResourceCount(int[] tids, String keyword, String resourceType) {
        try {
            SphinxClient client = createClient();
            try {
                setTeamFilter(client, tids);
                client.SetLimits(0, MAX_COUNT);
                String queryKeyword = SphinxClient.EscapeString(keyword);
                if (LynxConstants.TYPE_FOLDER.equals(resourceType) || LynxConstants.TYPE_FILE.equals(resourceType)
                        || LynxConstants.TYPE_PAGE.equals(resourceType)) {
                    client.SetFilter(ITEM_TYPE, getCRC32Value(resourceType), false);
                }
                SphinxResult result = client.Query(queryKeyword, "aone_resource");
                if (result != null) {
                    return result.matches.length;
                }
            } finally {
                client.Close();
            }
        } catch (SphinxException e) {
            LOG.error(e);
            LOG.debug("Detail is ", e);
        }
        return 0;
    }

    @Override
    public int getTeamPageCount(int[] tids, String keyword) {
        try {
            SphinxClient client = createClient();
            try {
                client.SetLimits(0, MAX_COUNT);
                setTeamFilter(client, tids);
                String queryKeyword = SphinxClient.EscapeString(keyword);
                SphinxResult result = client.Query(queryKeyword, "aone_page");
                if (result != null) {
                    return result.matches.length;
                }
            } finally {
                client.Close();
            }
        } catch (SphinxException e) {
            LOG.error(e);
            LOG.debug("Detail is ", e);
        }
        return 0;
    }

    @Override
    public String[] highLightDigest(String[] docs, String keyword) {
        return heightKeyword(docs, keyword);
    }

    @Override
    public String[] highLightTitle(String[] titles, String keyword) {
        return heightKeyword(titles, keyword);
    }

    /*
     * (non-Javadoc)
     * 
     * @see 从个人搜索点击、关注用户点击、相似用户点击、文档热度优化排序结果
     * 先将所有对该关键词的搜索点击记录从数据库查询出来，再通过当前用户id，关注用户id，相似用户id将相关记录过滤出来。再累加相似度，得到最终得分。
     * 优化只在前60个文档中进行<br/> 只对页面搜索进行重排序
     * 
     * @param retrieveresult sphinx搜索结果
     */
    @Override
    public List<Long> orderPageByInterest(List<WeightPair> retrieveresult, TeamQuery q) {

        if (retrieveresult == null || retrieveresult.isEmpty()) {
            return Collections.emptyList();
        }
        if (MAX_REORDER_SIZE <= q.getOffset()) {
            return tansferWeightToList(retrieveresult);
        }
        // 将pair->map方便权重累加的时候，直接通过文档id进行的操作。
        // docMaps 记录所有因子优化后 累计得到的权重
        LinkedHashMap<Long, Integer> docMaps = tansferListToMap(retrieveresult);

        String keyword = q.getKeyword();
        String uid = q.getQueryer();
        // 个人点击记录
        optimizeWeightByOwnClick2(docMaps, keyword, uid);
        // 关注用户
        optimizeWeightByIterest2(docMaps, q);
        // 相似用户
        optimizedWeightBySim2(docMaps, q);
        // 文档热度
        optimizeWeightByDocHot(docMaps, tansferWeightToList(retrieveresult));
        // 对map进行重排序
        List<Map.Entry<Long, Integer>> finalDoc = sort(docMaps);
        // 截取返回的20个文档，将 id-weight的map变成文档id的list 此id为sphnix的索引id
        List<Long> DocIdList = cut20ResultForReturn(finalDoc, q);
        return DocIdList;
    }

    @Override
    public List<Long> query(TeamQuery q, String queryType) {
        try {
            SphinxClient client = createClient();
            client.SetLimits(q.getOffset(), q.getSize());
            try {
                setTeamFilter(client, q.getTid());
                if (null != q.getDate() && !"".equals(q.getDate())) {
                    setDateFilter(client, q.getDate());
                }
                setMatchMode(client);
                client.SetFieldWeights(getWeightOfPageFields());
                setSortMode(client, q);
                String keyword = generateKeywords(client, q);
                String indexName = getIndexName(queryType);
                if (null == indexName) {
                    LOG.error("unsupported query type while search! queryType = " + queryType);
                    return null;
                }
                SphinxResult result = client.Query(keyword, indexName);
                if (null != client.GetLastError() && !"".equals(client.GetLastError())) {
                    LOG.error(client.GetLastError());
                    return null;
                }
                return retrieveResult(result);
            } finally {
                client.Close();
            }
        } catch (SphinxException e) {
            LOG.error(e);
            LOG.debug("Detail is", e);
        }
        return null;
    }

    @Override
    public List<Long> queryPageWithOptimize(TeamQuery q) {
        List<WeightPair> wei = queryWeight(q, TeamQuery.QUERY_FOR_PAGECONTENT);

        return orderPageByInterest(wei, q);
    }

    @Override
    public List<WeightPair> queryWeight(TeamQuery q, String queryType) {
        try {
            SphinxClient client = createClient();
            try {
                setTeamFilter(client, q.getTid());
                if (null != q.getDate() && !"".equals(q.getDate())) {
                    setDateFilter(client, q.getDate());
                }
                setMatchMode(client);
                client.SetFieldWeights(getWeightOfPageFields());
                setSortMode(client, q);
                String keyword = generateKeywords(client, q);
                String indexName = getIndexName(queryType);
                if (null == indexName) {
                    LOG.error("unsupported query type while search! queryType = " + queryType);
                    return null;
                }
                if ("page".equals(queryType) && MAX_REORDER_SIZE > q.getOffset()) {
                    client.SetLimits(0, MAX_REORDER_SIZE);
                } else {
                    client.SetLimits(q.getOffset(), q.getSize());
                }
                SphinxResult result = client.Query(keyword, indexName);
                if (null != client.GetLastError() && !"".equals(client.GetLastError())) {
                    LOG.error(client.GetLastError());
                    return null;
                }
                return retrieveWeightResult(result);
            } finally {
                client.Close();
            }
        } catch (SphinxException e) {
            LOG.error(e);
            LOG.debug("Detail is", e);
        }
        return null;
    }
    public void setHost(String host){
    	this.host = host;
    }
    public void setPort(int port){
    	this.port = port;
    }
}
