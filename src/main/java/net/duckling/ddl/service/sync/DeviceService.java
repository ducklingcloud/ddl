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
package net.duckling.ddl.service.sync;

import java.io.ByteArrayInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;

import org.springframework.stereotype.Service;


@Service
public class DeviceService implements IDeviceService {

    @Override
    public String generate(String arch, String uid, String macAddress) {
        StringBuilder sb = new StringBuilder();
        sb.append(arch);
        sb.append("#");
        sb.append(uid);
        sb.append("#");
        sb.append(macAddress);
        return getDigest(sb.toString());
    }

    private String getDigest(String src) {
        String md5 = null;
        try {
            MessageDigest _messageDigester = MessageDigest.getInstance("MD5");
            InputStream ins = new ByteArrayInputStream(src.getBytes());
            byte[] buf = new byte[4096];
            int n = 0;
            while (-1 != (n = ins.read(buf))) {
                _messageDigester.update(buf, 0, n);
            }
            md5 = toHex(_messageDigester.digest());
            System.out.println(md5);
        } catch (NoSuchAlgorithmException | FileNotFoundException e) {
            throw new RuntimeException("No MD5!");
        } catch (IOException e) {
            e.printStackTrace();
        }
        return md5;
    }

    public static String toHex(byte b[]) {
        StringBuilder sb = new StringBuilder();

        for (int i = 0; i < b.length; i++) {
            String s = Integer.toHexString(0xff & b[i]);
            if (s.length() < 2) {
                sb.append("0");
            }
            sb.append(s);
        }

        return sb.toString();

    }

    @Override
    public String query(String arch, String uid, String macAddress) {
        // TODO Auto-generated method stub
        return null;
    }

}
