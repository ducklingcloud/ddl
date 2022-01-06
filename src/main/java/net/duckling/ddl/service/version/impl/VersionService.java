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
package net.duckling.ddl.service.version.impl;

import java.io.IOException;
import java.text.ParseException;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

import org.apache.commons.httpclient.HttpClient;
import org.apache.commons.httpclient.HttpException;
import org.apache.commons.httpclient.methods.PostMethod;
import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import net.duckling.ddl.common.DucklingProperties;
import net.duckling.ddl.service.version.IVersionService;
import net.duckling.ddl.service.version.Version;

@Service
public class VersionService implements IVersionService {
    private static final Logger LOG=Logger.getLogger(VersionService.class);

    @Override
    public Version get(String project, String type) {
        HttpClient dClient = new HttpClient();
        PostMethod method = new PostMethod(getDupdateUrl());
        method.setParameter("type", type);
        method.setParameter("project",project);
        try {
            dClient.executeMethod(method);
            String response = method.getResponseBodyAsString();
            Map<String,String> map = parseResponse(response);

            Version v = new Version();
            if(map.get("success").equals("true")){
                v.setVersion(map.get("version"));
                v.setDownloadUrl(map.get("downloadUrl"));
                v.setForcedUpdate(Boolean.valueOf(map.get("forcedUpdate")));
                v.setDescription(map.get("description"));
                v.setCreateTime(map.get("createTime"));
            }
            v.setSuccess(Boolean.valueOf(map.get("success")));
            return v;
        } catch (HttpException e) {
            LOG.error("", e);
        } catch (IOException e) {
            LOG.error("", e);
        } catch (ParseException e) {
            LOG.error("", e);
        }
        return null;
    }

    /**
     * 版本管理网址
     * @return
     */
    private String getDupdateUrl() {
        return config.getProperty("update.version.url");
    }

    @SuppressWarnings("unchecked")
    private Map<String, String> parseResponse(String response) throws ParseException {
        Map<String,String> result = new HashMap<String,String>();
        org.json.JSONObject obj =new org.json.JSONObject(response);
        Iterator<String> keys = obj.keys();
        String key = null;
        while(keys.hasNext()){
            key = keys.next();
            result.put(key,String.valueOf(obj.get(key)));
        }
        return result;
    }

    @Autowired
    private DucklingProperties config;

}
