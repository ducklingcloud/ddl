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
package net.duckling.ddl.web.bean;

import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.text.ParseException;
import java.util.Properties;

import net.duckling.ddl.util.Base64;
import net.duckling.ddl.util.HttpClientUtil;

import org.apache.commons.httpclient.HttpClient;
import org.apache.commons.httpclient.HttpException;
import org.apache.commons.httpclient.methods.PostMethod;
import org.apache.commons.httpclient.params.HttpMethodParams;
import org.apache.log4j.Logger;

public class ClbHelper {
    protected static final Logger LOGGER = Logger.getLogger(ClbHelper.class);

    public static String getClbToken(int docId, int version, Properties properties) {
        HttpClient client = HttpClientUtil.getHttpClient(LOGGER);
        PostMethod method = new PostMethod(getClbTokenUrl(properties));
        method.getParams().setParameter(HttpMethodParams.HTTP_CONTENT_CHARSET, "utf-8");
        method.addParameter("appname", properties.getProperty("duckling.clb.aone.user"));
        method.addParameter("docid", docId + "");
        method.addParameter("version", version + "");
        try {
            method.addParameter(
                "password",
                Base64.encodeBytes(properties.getProperty("duckling.clb.aone.password").getBytes("utf-8")));
        } catch (IllegalArgumentException | UnsupportedEncodingException e) {

        }
        try {
            int status = client.executeMethod(method);
            String responseString = null;
            if (status < 400) {
                responseString = method.getResponseBodyAsString();
                org.json.JSONObject j = new org.json.JSONObject(responseString);
                Object st = j.get("status");
                if ("failed".equals(st)) {
                    LOGGER.error("获取clb token失败！");
                    return null;
                } else {
                    return j.get("pf").toString();
                }
            } else {
                LOGGER.error("STAUTS:" + status + ";MESSAGE:" + responseString);
            }
        } catch (HttpException e) {
            LOGGER.error("", e);
        } catch (IOException e) {
            LOGGER.error("", e);
        } catch (ParseException e) {
            LOGGER.error("", e);
        }
        return null;
    }

    private static String getClbTokenUrl(Properties properties) {
        return properties.getProperty("duckling.clb.url")
                + "/wopi/fetch/accessToken";
    }
}
