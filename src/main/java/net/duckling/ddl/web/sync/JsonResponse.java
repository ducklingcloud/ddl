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
package net.duckling.ddl.web.sync;

import java.util.Collections;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;

import javax.servlet.http.HttpServletResponse;

import net.duckling.ddl.service.sync.FileMeta;
import net.duckling.ddl.util.JsonUtil;
import net.duckling.ddl.web.bean.Result;

import org.apache.commons.collections.CollectionUtils;


class JsonResponse {
    public static void notFound(HttpServletResponse response) {
        //TODO: fix the deprecated method.
        JsonUtil.writeJSONObject(response, new Result<String>(Result.CODE_FILE_NOT_FOUND, Result.MESSAGE_FILE_NOT_FOUND));
    }

    public static void error(HttpServletResponse response) {
        JsonUtil.writeJSONObject(response, new Result<String>(Result.CODE_ERROR, Result.MESSAGE_ERROR));
    }

    public static void paraError(HttpServletResponse response) {
        JsonUtil.writeJSONObject(response, new Result<String>(Result.CODE_PARAM_ERROR, Result.MESSAGE_PARAM_ERROR));
    }

    public static void forbidden(HttpServletResponse response) {
        JsonUtil.writeJSONObject(response, new Result<Object>(Result.CODE_NO_PERMISSION, Result.MESSAGE_NO_PERMISSION));
    }

    public static void locked(HttpServletResponse response) {
        JsonUtil.writeJSONObject(response, new Result<String>(Result.CODE_FILE_LOCKED, Result.MESSAGE_FILE_LOCKED));
    }
    
    public static void sameFileExisted(HttpServletResponse response) {
        JsonUtil.writeJSONObject(response, new Result<String>(Result.CODE_FILE_EXISTED, Result.MESSAGE_FILE_EXISTED));
    }
    
    public static void fileVersionConflict(HttpServletResponse response, FileMeta fileMeta) {
        Map<String, Object> wrap = new HashMap<String, Object>();
        wrap.put("fileMeta", fileMeta);
        JsonUtil.writeJSONObject(response, new Result<FileMeta>(Result.CODE_FILE_VERSION_CONFLICT, Result.MESSAGE_FILE_VERSION_CONFLICT, fileMeta));
    }
    
    public static void fileNameConflict(HttpServletResponse response) {
        JsonUtil.writeJSONObject(response, new Result<String>(Result.CODE_FILE_NAME_CONFLICT, Result.MESSAGE_FILE_NAME_CONFLICT));
    }
    
    public static void fileMeta(HttpServletResponse response, FileMeta meta) {
        Map<String, Object> wrap = new HashMap<String, Object>();
        wrap.put("fileMeta", meta);
        JsonUtil.writeJSONObject(response, new Result<Map<String, Object>>(wrap));
    }
    
    public static void noEnoughSapce(HttpServletResponse response) {
        JsonUtil.writeJSONObject(response, new Result<String>(Result.CODE_NO_ENOUGH_SPACE, Result.MESSAGE_NO_ENOUGH_SPACE));
    }
    
    public static void startSession(HttpServletResponse response, String sessionId, Set<Integer> chunkMap, int chunkSize) {
        Map<String, Object> map = new HashMap<String, Object>();
        map.put("session_id", sessionId);
        if (chunkMap == null) {
            chunkMap = Collections.emptySet();
        }
        map.put("chunk_map", chunkMap);
        if (chunkSize != 0) {
            map.put("chunk_size", String.valueOf(chunkSize));
        }
        JsonUtil.writeJSONObject(response, new Result<Map<String, Object>>(map));
    }
    
    public static void ackChunk(HttpServletResponse response, String sessionId, String ack, Set<Integer> chunkSet) {
        HashMap<String, Object> map = new HashMap<String, Object>();
        map.put("chunk_ack", ack);
        if (CollectionUtils.isNotEmpty(chunkSet)) {
            map.put("chunk_map", chunkSet);
        }
        
        JsonUtil.writeJSONObject(response, new Result<Map<String, Object>>(map));
    }
    
    public static void chunkUploadSessionNotFound(HttpServletResponse response, String sessionId) {
        HashMap<String, Object> map = new HashMap<String, Object>();
        map.put("sessionId", sessionId);
        map.put("status", "session_not_found");
        Result<Map<String, Object>> result = new Result<Map<String, Object>>(Result.CODE_PARAM_ERROR, Result.MESSAGE_PARAM_ERROR, map);
        JsonUtil.writeJSONObject(response, result);
    }
}
