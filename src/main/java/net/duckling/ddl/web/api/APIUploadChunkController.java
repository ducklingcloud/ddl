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

package net.duckling.ddl.web.api;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import net.duckling.ddl.common.VWBContext;
import net.duckling.ddl.exception.NoEnoughSpaceException;
import net.duckling.ddl.service.resource.ResourceOperateService;
import net.duckling.ddl.service.url.UrlPatterns;
import net.duckling.ddl.util.JsonUtil;
import net.duckling.ddl.web.controller.BaseController;
import net.duckling.ddl.web.interceptor.access.RequirePermission;

import org.apache.log4j.Logger;
import org.json.simple.JSONObject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.multipart.MultipartFile;

import cn.vlabs.clb.api.document.ChunkResponse;

/**
 * 大文件分块上传接口
 * 
 * @date 2014-8-18
 * @author lishanbo@cstnet.cn
 */
@Controller
@RequestMapping("/api/uploadchunk")
@RequirePermission(target = "team", operation = "edit", authenticated = true)
public class APIUploadChunkController extends BaseController {

    private static final Logger LOGGER = Logger.getLogger(APIUploadChunkController.class);

    @Autowired
    private ResourceOperateService operateService;

    /**
     * @param fileName 文件名
     * @param md5 为文件创建唯一标示
     * @param size 文件大小
     * @param parentRid 目录rid
     * @param request
     * @param response
     */
    @RequestMapping(method = RequestMethod.POST, params = "func=prepare")
    public void prepare(@RequestParam("fileName") String fileName, @RequestParam("md5") String md5,
            @RequestParam("size") long size, @RequestParam("parentRid") int parentRid, HttpServletRequest request,
            HttpServletResponse response) {
        VWBContext vwbcontext = VWBContext.createContext(request, UrlPatterns.T_ATTACH);
        String uid = vwbcontext.getCurrentUID();
        int tid = VWBContext.getCurrentTid();
        int rid = 0;
        ChunkResponse cr = null;
        try {
            Object[] result = operateService.prepareChunkUpload(uid, tid, parentRid, fileName, md5, size);
            cr = (ChunkResponse) result[0];
            rid = (int) result[1];
        } catch (NoEnoughSpaceException e) {
        	LOGGER.error(e.getMessage());
        	printResponse(e.getMessage(), false, null, response);
            return;
        }

        printUploadResult(cr,rid, response);
    }

    /**
     * 分块上传执行
     * 
     * @param file 文件块
     * @param chunkedIndex 文件块序列索引
     * @param rid 文档id
     * @param numOfBytes 文件块大小
     * @param request
     * @param response
     * @throws IOException
     */
    @RequestMapping(method = RequestMethod.POST, params = "func=execute")
    public void execute(@RequestParam("file") MultipartFile file, @RequestParam("rid") int rid,
            @RequestParam("chunkedIndex") int chunkedIndex, @RequestParam("numOfBytes") int numOfBytes,
            HttpServletRequest request, HttpServletResponse response) throws IOException {
        int tid = VWBContext.getCurrentTid();
        printUploadResult(operateService.executeChunkUpload(rid, tid, chunkedIndex, file.getBytes(), numOfBytes),
                rid, response);
    }

    /**
     * 完成分块上传
     * 
     * @param rid
     * @param request
     * @param response
     */
    @RequestMapping(method = RequestMethod.POST, params = "func=finish")
    public void finish(@RequestParam("rid") int rid, HttpServletRequest request,
            HttpServletResponse response) {
    	int tid = VWBContext.getCurrentTid();
    	printUploadResult(operateService.finishChunkUpload(rid, tid), rid,
    	        response);
    }
    
    /**
     * 输出上传结果
     * @param cr
     * @param rid
     * @param response
     */
    private void printUploadResult(ChunkResponse cr,int rid, HttpServletResponse response){
    	Map<String, Object> map = new HashMap<String, Object>();
        map.put("phase", cr.getPhase());
        map.put("statusCode", String.valueOf(cr.getStatusCode()));
        map.put("chunkIndex", String.valueOf(cr.getChunkIndex()));
        map.put("chunkSize", String.valueOf(cr.getChunkSize()));
        map.put("statusMessage", cr.getStatusMessage());
        map.put("rid", String.valueOf(rid));
        map.put("emptyChunkSet", cr.getEmptyChunkSet());
    	if(cr.isSccuessStatus()){
            printResponse(cr.getStatusMessage(), true, map, response);
        } else {
        	LOGGER.error(cr.getStatusCode() + ":" + cr.getStatusMessage());
        	printResponse(cr.getStatusMessage(), false, map, response);
        }
    }
    
    @SuppressWarnings("unchecked")
    private void printResponse(String message, Boolean success, Map<String, Object> params, HttpServletResponse response) {
        JSONObject j = new JSONObject();
        j.put("result", success);
        j.put("message", message);
        
        if(params!=null){
        	j.putAll(params);
        }
        
        JsonUtil.writeJSONObject(response, j);
    }

}