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

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.MalformedURLException;
import java.net.URL;
import java.sql.Timestamp;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.Iterator;
import java.util.List;

import net.duckling.ddl.service.search.DocWeightRecord;
import net.duckling.ddl.service.search.SearchLogAnalysis;
import net.duckling.ddl.service.search.SearchReocrd;
import net.duckling.ddl.service.search.WeightPair;


/*
 * Update this -- TODO: take one package for JSON
 * <2022-01-06 Thu>
 */
//import org.codehaus.jackson.map.ObjectMapper;
import com.fasterxml.jackson.databind.ObjectMapper;

import org.apache.log4j.Logger;
import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

/**
 * @author Li Zexin
 *
 */
@Service
public class SearchLogAnalysisImpl implements SearchLogAnalysis {
    private static final Logger LOG = Logger.getLogger(SearchLogAnalysisImpl.class);
    private static final long SLOT = 60000;
    private List<SqlSearchRecord> clickSeq;
    @Value("${duckling.dlog.application.name}")
    private String dlogappName ;
    private List<SqlSearchRecord> log ;
    private int logLast ;
    @Autowired
    private SearchDocWeightDAO searchDocWeightDAO;
    @Autowired
    private SearchLogDAO searchLogDAO;
    private int seq ;
    private String tmpKywrd;
    private String tmpUsr;

    //删除不相关元素
    private void cleanList(List<SearchReocrd> searchRecord) {
        for(int i=0;i<searchRecord.size();i++){
            if(searchRecord.get(i).getSeq() ==3 ||searchRecord.get(i).getSeq() == 0){
                searchRecord.remove(i);
                i--;
            }
        }
    }

    private void generateMetaSequence(List<SearchReocrd> searchRecord) {
        //生成点击序列
        for(int i=0;i<searchRecord.size();i++){
            if(searchRecord.get(i).getSeq() == 2){
                int j =(i+1);
                while (j<searchRecord.size()){
                    if(searchRecord.get(j).getClient().equals(searchRecord.get(i).getClient())){
                        if(searchRecord.get(j).getFlag()!=null&&searchRecord.get(j).getFlag().equals("search")){
                            break;
                        }
                        if(!"N".equals(searchRecord.get(j).getOper_name())){
                            if(searchRecord.get(j).getPid().equals(searchRecord.get(i).getPid())
                               &&searchRecord.get(j).getType().equals(searchRecord.get(i).getType())){
                                searchRecord.get(i).setOper_name(searchRecord.get(j).getOper_name());
                            }

                            if(searchRecord.get(j).getType() !=null && searchRecord.get(j).getType().equals("bundle"))
                            {
                                long total_minute = 0;

                                try {
                                    SimpleDateFormat df = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
                                    Date begin_time = df.parse(searchRecord.get(i).getTime());
                                    Date end_time = df.parse(searchRecord.get(j).getTime());
                                    total_minute = (end_time.getTime() - begin_time.getTime())/(1000*60);
                                } catch (java.text.ParseException e) {
                                    // TODO Auto-generated catch block
                                    e.printStackTrace();
                                }

                                if(total_minute < 6){
                                    searchRecord.get(i).setOper_name(searchRecord.get(j).getOper_name());
                                }
                            }
                        }
                        break;
                    }
                    j++;
                }
            }
        }
    }

    /**
     * 从json数组中读取json数据
     * @param result打开日志文件返回的内容
     * @return Log的list形式
     * @throws IOException
     */
    private List<SearchReocrd> LogToList(String result) throws IOException{
        if(result == null ||result.isEmpty()){
            return null;
        }

        List<SearchReocrd> searchRecord = new ArrayList<SearchReocrd>();

        JSONParser parser = new JSONParser();
        try {
            JSONObject jo = (JSONObject) parser.parse(result);
            JSONArray jo1 = (JSONArray) jo.get("results");
            Iterator it = jo1.iterator();
            while (it.hasNext()) {
                int tempseq =0;
                JSONObject jsonRecord = (JSONObject) it.next();
                if (jsonRecord.containsKey("keyword")||jsonRecord.containsKey("oper_name")) {
                    if (jsonRecord.containsKey("flag") && jsonRecord.get("flag").toString().equals("search")){
                        tempseq = 1;
                    }
                    if(jsonRecord.containsKey("rank") && jsonRecord.get("rank").toString() != null && !jsonRecord.get("rank").toString().isEmpty()){
                        tempseq = 2;
                    }
                    if(jsonRecord.containsKey("oper_name") && jsonRecord.get("oper_name").toString() != null && !jsonRecord.get("oper_name").toString().isEmpty()){
                        tempseq = 3;
                    }

                    ObjectMapper mapper = new ObjectMapper();
                    SearchReocrd user = mapper.readValue(
                        jsonRecord.toJSONString(), SearchReocrd.class);
                    user.setSeq(tempseq);
                    searchRecord.add(user);
                }
            }
        } catch (Exception e) {
            LOG.error("LogToList", e);
        }

        //保证队列从seq=1的元素开始，便于后续处理
        for(int i=0;i<searchRecord.size();i++){
            if(searchRecord.get(i).getSeq() == 1){
                for(int j = 0;j<i;j++ ){
                    searchRecord.remove(0);
                }
                break;
            }
        }
        return searchRecord;
    }

    private void newQueryStart(int i){
        seq = 0;
        searchLogDAO.creatSearchedLog(clickSeq);
        clickSeq.clear();
        tmpUsr = log.get(i).getUid();
        tmpKywrd = log.get(i).getKeyword();
        log.get(i).setSeq(++seq);
        clickSeq.add(log.get(i));
    }

    private String openFile(final String path) {
        String result ="";
        if (path.contains("http://")) {
            URL url;
            InputStream in = null;
            BufferedReader br = null;
            try {
                url = new URL(path);
                in = url.openStream();
                br = new BufferedReader(new InputStreamReader(in,"utf8"));
                String line = br.readLine();
                while(line != null){
                    result += new String(line.getBytes(), "utf8");
                    line = br.readLine();
                }
                in.close();
                br.close();
            } catch (MalformedURLException e) {
                LOG.warn(e.getMessage());
            } catch (IOException e) {
                LOG.warn(e.getMessage());
            } finally {
                if(in != null) {
                    try {
                        in.close();
                        br.close();
                    } catch (IOException e) {
                        LOG.warn(e.getMessage());
                    }
                }
            }
        }
        else{
            File inputFile = new File(path);
            FileInputStream fis = null;
            BufferedReader bin = null;
            try {
                fis = new FileInputStream(inputFile);
                bin = new BufferedReader(new InputStreamReader(fis));
                String line = bin.readLine();
                while (line != null) {
                    result = result + line;
                    line = bin.readLine();
                }
                fis.close();
                bin.close();
            } catch (FileNotFoundException e){
                LOG.warn(e.getMessage());
            } catch (IOException e) {
                LOG.warn(e.getMessage());
            } finally {
                if(fis != null){
                    try {
                        fis.close();
                        bin.close();
                    } catch (IOException e) {
                        LOG.warn(e.getMessage());
                    }
                }
            }
        }
        return result;

    }

    //处理uid中含有guest的情况
    private void renameUid(List<SearchReocrd> searchRecord) {
        for(int i=0;i<searchRecord.size();i++){
            for(int j=(i+1);j<searchRecord.size();j++){
                if(searchRecord.get(i).getClient().equals(searchRecord.get(j).getClient())){
                    if(searchRecord.get(j).getUid().contains("guest")){
                        searchRecord.get(j).setUid(searchRecord.get(i).getUid());

                    }else{
                        break;
                    }
                }
            }
        }
    }

    private void resetPara(){
        log = null;
        clickSeq = null;
        logLast = 0;
        tmpUsr = null;
        tmpKywrd = null;
        seq =0;

    }

    @Override
    public void anyasisLog(){
        log = searchLogDAO.getLog();
        clickSeq = new ArrayList<SqlSearchRecord>();
        logLast =log.size()-1;
        if(logLast == -1){
            LOG.error("Today's search dlog is empty");
            return;
        }
        else{
            LOG.info("Today's raw search dlog count is "+ (logLast+1));
        }
        tmpUsr = log.get(0).getUid();
        tmpKywrd = log.get(0).getKeyword();
        seq =0;

        for (int i = 0; i <= logLast; i++) {
            if (log.get(i).getKeyword().equals(tmpKywrd) && log.get(i).getUid().equals(tmpUsr)) {
                if (log.get(i).getSeq() == 1 && (!clickSeq.isEmpty())) {
                    Timestamp nowtime = Timestamp.valueOf(log.get(i).getTime());
                    Timestamp lasttime = Timestamp.valueOf(clickSeq.get(clickSeq.size() - 1).getTime());
                    long slot = nowtime.getTime() - lasttime.getTime();
                    if (slot > SLOT) {
                        newQueryStart(i);
                    }
                }
                if (log.get(i).getSeq() != 1 || clickSeq.isEmpty()) {
                    log.get(i).setSeq(++seq);
                    clickSeq.add(log.get(i));
                }
            }
            else{
                newQueryStart(i);
            }
        }
        if(!clickSeq.isEmpty()){
            searchLogDAO.creatSearchedLog(clickSeq);
            clickSeq.clear();
        }
        //更新文档权重
        searchDocWeightDAO.updateDocWeight();
        resetPara();
    }
    @Override
    public List<DocWeightRecord> getDocWeight(String keyword){
        return searchDocWeightDAO.getDocWeight(keyword);
    }

    @Override
    public List<WeightPair> getOwnDocWeight(String keyword,String uid){
        return searchDocWeightDAO.getOwnDocWeight(keyword, uid);
    }

    @Override
    public List<WeightPair> getViewDocWeight(List<Long> rids){
        List<WeightPair> wplist = searchDocWeightDAO.getViewDocWeight(rids);
        return wplist;
    }

    @Override
    public Boolean saveLog(String start,String end) throws IOException{
        String logPath = "http://dlog.escience.cn:80/dlog2/log.json?beginDate=" + start+ "&endDate=" + end + "&appName=" + dlogappName;
        String result = openFile(logPath);
        if(result == null ||result.isEmpty()){
            return false;
        }
        //生成元点击序列
        List<SearchReocrd> searchRecord = LogToList(result);
        if(searchRecord == null || searchRecord.isEmpty()){
            return false;
        }
        generateMetaSequence(searchRecord);
        cleanList(searchRecord);
        renameUid(searchRecord);

        if(!searchRecord.isEmpty()){
            searchLogDAO.creatSearchLog(searchRecord);
        }
        return true;
    }


}
