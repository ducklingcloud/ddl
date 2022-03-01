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
package net.duckling.ddl.web.agent.util;

import com.google.gson.Gson;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.UnsupportedEncodingException;
import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;

import javax.crypto.BadPaddingException;
import javax.crypto.Cipher;
import javax.crypto.IllegalBlockSizeException;
import javax.crypto.NoSuchPaddingException;
import javax.crypto.spec.SecretKeySpec;

import net.duckling.ddl.web.controller.LynxEmailResourceController;

import org.apache.commons.codec.binary.Base64;
import org.apache.log4j.Logger;
import com.google.gson.JsonObject;

public class AuthUtil {
    private final static Logger LOG = Logger.getLogger(AuthUtil.class);

    private static byte[] clientKey=null;

    public static String getAuthEmail(String auth){
        try {
            String decode = decodeAuth(auth);
            JsonObject obj = new Gson().fromJson(decode, JsonObject.class);
            String email = obj.get("email").getAsString();
            String date = obj.get("date").getAsString();
            SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
            Date d = sdf.parse(date);
            if(notExpired(d)){
                return email;
            }
        } catch (InvalidKeyException | NoSuchAlgorithmException | NoSuchPaddingException | IllegalBlockSizeException
                 | BadPaddingException | UnsupportedEncodingException e) {
            return null;
        } catch (ParseException e) {
            return null;
        }
        return null;
    }

    private static boolean notExpired(Date d) {
        long now = System.currentTimeMillis();
        long dd = d.getTime();
        //有效期前后30分钟
        if(Math.abs((now-dd))<(1000*60*30)){
            return true;
        }
        return false;
    }

    private static String decodeAuth(String auth) throws NoSuchAlgorithmException, NoSuchPaddingException, InvalidKeyException, IllegalBlockSizeException, BadPaddingException, UnsupportedEncodingException {
        SecretKeySpec spec = new SecretKeySpec(getKey(), "AES");
        Cipher cipher = Cipher.getInstance("AES");
        cipher.init(Cipher.DECRYPT_MODE, spec);
        byte[] result = cipher.doFinal(Base64.decodeBase64(auth));
        return new String(result,"UTF-8");
    }

    public static byte[] getKey(){
        if(clientKey==null){
            initClientKey();
        }
        return clientKey;
    }

    private synchronized static void initClientKey() {
        if(clientKey==null){
            try {
                InputStream in = LynxEmailResourceController.class.getResourceAsStream("/ddlclientkey");
                BufferedReader reader = new BufferedReader(new InputStreamReader(in,"utf-8"));
                String key = reader.readLine();
                reader.close();
                clientKey = Base64.decodeBase64(key);
            } catch (UnsupportedEncodingException e) {
                LOG.error("",e);
            } catch (IOException e) {
                LOG.error("",e);
            }
        }
    }
}
