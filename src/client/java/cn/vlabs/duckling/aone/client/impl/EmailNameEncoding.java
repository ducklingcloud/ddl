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
package cn.vlabs.duckling.aone.client.impl;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.UnsupportedEncodingException;
import java.security.NoSuchAlgorithmException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Random;

import javax.crypto.Cipher;
import javax.crypto.spec.SecretKeySpec;

import org.apache.commons.codec.binary.Base64;
import com.google.gson.JsonObject;

public class EmailNameEncoding {
    private byte[] key =  null;
    private Random random = new Random();

    private byte[] getKey() throws NoSuchAlgorithmException, UnsupportedEncodingException{
        if(key==null){
            initClientKey();
        }
        return key;
    }

    private synchronized void initClientKey() {
        if(key==null){
            InputStream in = EmailNameEncoding.class.getResourceAsStream("ddlclientkey");
            try {
                BufferedReader reader = new BufferedReader(new InputStreamReader(in,"UTF-8"));
                String keys = reader.readLine();
                reader.close();
                key =Base64.decodeBase64(keys);
            } catch (UnsupportedEncodingException e) {
                throw new RuntimeException("不支持的utf-8编码",e);
            } catch (IOException e) {
                throw new RuntimeException("读取ddlclientkey错误",e);
            }
        }
    }

    private String getContext(String email){
        JsonObject obj = new JsonObject();
        obj.put("email", email);
        SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        obj.put("data", format.format(new Date()));
        obj.put("random", random.nextInt());
        obj.put("random", random.nextInt());
        return obj.toString();
    }

    public String getEncryptEmail(String email){
        String context = getContext(email);

        SecretKeySpec spec;
        try {
            spec = new SecretKeySpec(getKey(), "AES");
            Cipher cipher = Cipher.getInstance("AES");
            cipher.init(Cipher.ENCRYPT_MODE, spec);
            byte[] result = cipher.doFinal(context.getBytes("utf-8"));
            return encodeByte(result);
        } catch (Exception e) {
            throw new RuntimeException("加密用户邮箱错误",e);
        }
    }

    private String encodeByte(byte[] data){
        return Base64.encodeBase64String(data);
    }

}
