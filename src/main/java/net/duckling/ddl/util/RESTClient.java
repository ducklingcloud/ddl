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
package net.duckling.ddl.util;

import java.io.BufferedReader;
import java.io.File;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.URI;
import java.net.URISyntaxException;
import java.util.List;

import org.apache.http.Consts;
import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.NameValuePair;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.client.methods.HttpUriRequest;
import org.apache.http.client.methods.RequestBuilder;
import org.apache.http.entity.ContentType;
import org.apache.http.entity.mime.HttpMultipartMode;
import org.apache.http.entity.mime.MultipartEntityBuilder;
import org.apache.http.entity.mime.content.StringBody;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClients;
import org.apache.http.util.CharsetUtils;

import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;

public class RESTClient {
	
	public final static String STATUS_CODE_200 = "200";

    public JsonObject httpGet(String url, List<NameValuePair> params) throws  IOException {
        CloseableHttpClient httpclient = HttpClients.createDefault();
        try {
            RequestBuilder rb = RequestBuilder.get().setUri(new URI(url));
            for (NameValuePair hp : params) {
                rb.addParameter(hp.getName(), hp.getValue());
            }
            rb.addHeader("accept", "application/json");
            HttpUriRequest uriRequest = rb.build();
            HttpResponse response = httpclient.execute(uriRequest);
            if (response.getStatusLine().getStatusCode() != 200) {
                throw new RuntimeException("Failed : HTTP error code : " + response.getStatusLine().getStatusCode());
            }
            BufferedReader br = new BufferedReader(new InputStreamReader((response.getEntity().getContent())));
            JsonParser jp = new JsonParser();
            JsonElement je = jp.parse(br);
            return je.getAsJsonObject();
        } catch (ClientProtocolException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        } catch (URISyntaxException e) {
            e.printStackTrace();
        } finally {
            httpclient.close();
        }
        return null;
    }

    public JsonObject httpPost(String url, List<NameValuePair> params) throws IOException{
        CloseableHttpClient httpclient = HttpClients.createDefault();
        try {
            HttpPost httppost = new HttpPost(url);
            httppost.setEntity(new UrlEncodedFormEntity(params, Consts.UTF_8));
            httppost.setHeader("accept", "application/json");
            CloseableHttpResponse response = httpclient.execute(httppost);
            if (response.getStatusLine().getStatusCode() != 200) {
                throw new RuntimeException("Failed : HTTP error code : " + response.getStatusLine().getStatusCode());
            }
            BufferedReader br = new BufferedReader(new InputStreamReader((response.getEntity().getContent())));
            JsonParser jp = new JsonParser();
            JsonElement je = jp.parse(br);
            return je.getAsJsonObject();
        } catch (ClientProtocolException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }  finally {
            httpclient.close();
        }
        return null;
    }

    public JsonObject httpUpload(String url, String dataFieldName, byte[] data, List<NameValuePair> params){
        CloseableHttpClient httpclient = HttpClients.createDefault();
        try {
            HttpPost httppost = new HttpPost(url);
            
            MultipartEntityBuilder builder = MultipartEntityBuilder.create()
                    .setMode(HttpMultipartMode.BROWSER_COMPATIBLE)
                    .addBinaryBody(dataFieldName, data, ContentType.DEFAULT_BINARY, "tempfile");
            for (NameValuePair hp : params) {
                builder.addPart(hp.getName(),
                        new StringBody(hp.getValue(), ContentType.create("text/plain", Consts.UTF_8)));
            }
            HttpEntity reqEntity = builder.setCharset(CharsetUtils.get("UTF-8")).build();
            httppost.setEntity(reqEntity);
            CloseableHttpResponse response = httpclient.execute(httppost);
            if (response.getStatusLine().getStatusCode() != 200) {
                throw new RuntimeException("Failed : HTTP error code : " + response.getStatusLine().getStatusCode());
            }
            try {
                BufferedReader br = new BufferedReader(new InputStreamReader((response.getEntity().getContent())));
                JsonParser jp = new JsonParser();
                JsonElement je = jp.parse(br);
                return je.getAsJsonObject();
            } finally {
                response.close();
            }
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            try {
                httpclient.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        return null;
    }

    public File httpDownload() {
        // TODO
        return null;
    }

}
