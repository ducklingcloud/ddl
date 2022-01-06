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
package net.duckling.ddl.web.api.pan;

import java.io.IOException;
import java.io.OutputStream;
import java.io.UnsupportedEncodingException;
import java.net.URLDecoder;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import net.duckling.ddl.constant.ImageSize;
import net.duckling.ddl.service.pan.PanAclUtil;
import net.duckling.ddl.service.pan.ResponseHeaderUtils;
import net.duckling.ddl.web.bean.FileType;
import net.duckling.ddl.web.bean.FileTypeHelper;
import net.duckling.ddl.web.interceptor.access.RequirePermission;
import net.duckling.meepo.api.IPanService;
import net.duckling.meepo.api.PanAcl;

import org.apache.commons.io.IOUtils;
import org.apache.commons.lang.StringUtils;
import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

import cn.cnic.cerc.dlog.client.WebLog;

import com.meepotech.sdk.MeePoException;
import com.meepotech.sdk.MeePoMeta;

@Controller
@RequestMapping("/mobile/pan/page")
@RequirePermission(authenticated=true)
public class APIPanDownloadController {
    private static Logger LOG = Logger.getLogger(APIPanDownloadController.class);
    @Autowired
    private IPanService panService;

    @WebLog(method = "mobilePanPage", params = "pid,itemType")
    @RequestMapping
    public void service(@RequestParam("pid") String rid, @RequestParam("itemType") String itemType,
                        HttpServletRequest request, HttpServletResponse response){
        String imageSize = request.getParameter("imageType");
        rid = decode(rid);
        PanAcl acl = PanAclUtil.getInstance(request);
        MeePoMeta meta = null;
        try {
            meta = panService.ls(acl, rid, true);
        } catch (MeePoException e1) {
            LOG.error(e1.getMessage());
            return;
        }
        OutputStream os = null;
        long[] re = getOffer(request, meta.size);
        long offset = re[0];
        long size =  re[1];
        long p0 = System.currentTimeMillis();
        long tmpSize = size;
        try {
            response.setCharacterEncoding("utf-8");
            String headerValue = ResponseHeaderUtils.buildResponseHeader(request, meta.name, true);
            response.setContentType("application/x-download");
            response.setHeader("Content-Disposition", headerValue);
            response.setContentLength((int) size);
            response.setHeader("Content-Length", tmpSize + "");
            os = response.getOutputStream();

            //是否为图片，盘截取的缩略图不是等比缩放，暂时先用原图
            //if(FileType.IMAGE.equals(FileTypeHelper.getName(meta.name))){
            //  panService.thumbnails(acl, rid, convertPanImageType(imageSize), os);
            //}else{
            panService.download(PanAclUtil.getInstance(request), rid, meta.version, os,offset,size);
            //}
        } catch (UnsupportedEncodingException e) {
            LOG.error("", e);
        } catch (MeePoException e) {
            LOG.error("", e);
        } catch (IOException e) {
            LOG.error("", e);
        } catch (Exception e) {
            LOG.error("", e);
        } finally {
            IOUtils.closeQuietly(os);
            long p1 = System.currentTimeMillis();
            LOG.info("Download document[name:" + meta.name + ",size:" + tmpSize + "] use time " + (p1 - p0));
        }
    }
    private long[] getOffer(HttpServletRequest request,long fileSize){
        //100-200    第100到第200字节
        //500-       第500字节到文件末尾
        //-1000      最后的1000个字节
        String range = request.getHeader("range");
        long[] result = new long[2];
        if(StringUtils.isEmpty(range)){
            result[0] = 0;
            result[1] = -1;
            return result;
        }
        range = range.toLowerCase();
        String e = range.substring("bytes ".length());
        String[] end = e.split("-");
        long begin = 0;
        long endLength = 0;
        if(e.indexOf('-')==0){
            long n = Long.parseLong(end[1]);
            begin = fileSize-n;
            endLength=fileSize-1;
        }else{
            if(end.length==1){
                begin = Long.parseLong(end[0]);
                endLength = fileSize-1;
            }else{
                begin = Long.parseLong(end[0]);
                endLength = Long.parseLong(end[1]);
            }
        }
        result[0] = begin;
        result[1] = endLength-begin+1;
        return result;
    }


    private String decode(String s) {
        try {
            return URLDecoder.decode(s, "UTF-8");
        } catch (UnsupportedEncodingException e) {
            return "";
        }
    }

    /**
     * pan图片尺寸：xs:32x32, s:64x64, m:128x128, l:640x480, xl:1024x768
     * @param imageSize
     * @return
     */
    private String convertPanImageType(String imageSize){
        if(ImageSize.BIG.equals(imageSize)){
            return "xl";
        }else if(ImageSize.SMALL.equals(imageSize)){
            return "m";
        }else{
            return "l";
        }
    }

}
