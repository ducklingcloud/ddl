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

import java.io.UnsupportedEncodingException;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.concurrent.TimeUnit;

import javax.annotation.PostConstruct;
import javax.annotation.PreDestroy;

import org.apache.commons.codec.binary.Hex;
import org.redisson.Config;
import org.redisson.Redisson;
import org.redisson.RedissonClient;
import org.redisson.core.RBucket;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

@Service
public class ChunkUploadSessionService implements IChunkUploadSessionService {
    private static final int EXPIRE_DAYS = 30;

    private RedissonClient redisson;

    private static final String APP_PREFIX = "DDL";

    @Value("${duckling.redis.server}")
    private String host;
    @Value("${duckling.redis.port}")
    private String port;

    @PostConstruct
    public void init(){
        Config config = new Config();
        config.useSingleServer().setAddress(host+":"+port);
        config.setThreads(10);
        redisson = Redisson.create(config);
    }

    @PreDestroy
    public void destroy() {
        redisson.shutdown();
    }

    @Override
    public String create(Long clbId) {
        return create(clbId, ChunkUploadSession.APPENDING);
    }

    @Override
    public String create(Long clbId, String status) {
        String sessionId = generateSessonId(clbId);
        RBucket<ChunkUploadSession> bucket = redisson.getBucket(sessionId);
        ChunkUploadSession data = new ChunkUploadSession(clbId, status);
        bucket.set(data, EXPIRE_DAYS, TimeUnit.DAYS);
        return sessionId;
    }

    private String generateSessonId(Long clbId) {
        String s = String.format("session_id:%s:%d:%d", APP_PREFIX, clbId, System.currentTimeMillis());
        MessageDigest md;
        byte[] digest = null;
        try {
            md = MessageDigest.getInstance("MD5");
            digest = md.digest(s.getBytes("UTF-8"));
        } catch (NoSuchAlgorithmException ignore) {
        } catch (UnsupportedEncodingException ignore) {
        }

        return Hex.encodeHexString(digest);
    }

    @Override
    public ChunkUploadSession get(String sessionId) {
        RBucket<ChunkUploadSession> bucket = redisson.getBucket(sessionId);
        return bucket.get();
    }
}
