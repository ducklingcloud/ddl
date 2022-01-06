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
package net.duckling.ddl.web.controller.pan;

import java.io.IOException;
import java.io.OutputStream;
import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import net.duckling.ddl.common.DucklingProperties;
import net.duckling.ddl.service.pan.FileInfo;
import net.duckling.ddl.service.pan.PanAclUtil;
import net.duckling.ddl.service.pan.ResponseHeaderUtils;
import net.duckling.falcon.api.cache.ICacheService;
import net.duckling.meepo.api.IPanService;
import net.duckling.meepo.api.PanAcl;

import org.apache.commons.codec.binary.Base64;
import org.apache.commons.io.IOUtils;
import org.json.simple.JSONObject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.util.StringUtils;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.servlet.ModelAndView;
import org.springframework.web.servlet.view.RedirectView;

import cn.cnic.cerc.dlog.client.WebLog;

import com.meepotech.sdk.MeePoException;
import com.meepotech.sdk.MeePoMeta;
import com.meepotech.sdk.MeePoRevision;
import com.meepotech.sdk.PanShareInfo;

@Controller
@RequestMapping("/wopi")
public class WopiController {

    @Autowired
    private IPanService panService;
    @Autowired
    private DucklingProperties systemProperty;


    @Autowired
    private ICacheService cacheService;



    public void sendPreviewDoc(String skey, HttpServletRequest request, HttpServletResponse response) {
        OutputStream os = null;
        long p0 = System.currentTimeMillis();
        try {
            FileInfo info = (FileInfo)cacheService.get(skey);
            response.setCharacterEncoding("utf-8");
            response.setContentLength((int) info.getSize());
            response.setContentType("application/x-download");
            String headerValue = ResponseHeaderUtils.buildResponseHeader(request, info.getFileName(), true);
            response.setHeader("Content-Disposition", headerValue);
            response.setHeader("Content-Length", info.getSize() + "");
            os = response.getOutputStream();
            if(StringUtils.isEmpty(info.getPanAcl().getUmtToken())){
                //pan的分享内容提取
                panService.getShareContent(info.getRemotePath(), null, os);
            }else{
                panService.download(info.getPanAcl(), info.getRemotePath(), info.getVersion(), os);
            }
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        } catch (MeePoException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            IOUtils.closeQuietly(os);
            long p1 = System.currentTimeMillis();
            System.out.println("Send document use time " + (p1 - p0));
        }

    }

    private String encodeStr(String plainText) {
        byte[] b = plainText.getBytes();
        Base64 base64 = new Base64();
        b = base64.encode(b);
        String s = new String(b);
        return s;
    }
    @WebLog(method = "PanSSLRedirectView", params = "rid,remotePath")
    @RequestMapping("/p")
    public ModelAndView getSSLRedirectView(@RequestParam("remotePath") String remotePath, HttpServletRequest request)
            throws UnsupportedEncodingException, MeePoException {
        PanAcl acl = PanAclUtil.getInstance(request);
        String mode = request.getParameter("mode");
        MeePoMeta meta = panService.ls(acl, remotePath, true);
        String versionStr = request.getParameter("version");
        long version = 0;
        long size = meta.size;
        if(!StringUtils.isEmpty(versionStr)){
            version = Long.parseLong(versionStr);
            MeePoRevision tv = findTargetRevision(acl,remotePath,version);
            size = tv.size;
        }
        String skey = meta.modified+meta.modifiedBy+version;
        FileInfo info = new FileInfo(skey, meta.name, remotePath, size,acl.getUid(), acl.getUmtToken(), version);
        cacheService.set(skey, info);
        String accessToken = encodeStr(acl.getUid()+"#"+acl.getUmtToken());
        String url = info.getOwaServerUrl(getOwaServerDomain(), mode, getEncodedCheckFileUrl(skey), accessToken);
        return new ModelAndView(new RedirectView(url));
    }

    @WebLog(method = "PanShareSSLRedirectView", params = "panShareId")
    @RequestMapping("/s")
    public ModelAndView getShareSSLRedirectView(@RequestParam("panShareId") String panShareId, HttpServletRequest request)
            throws UnsupportedEncodingException, MeePoException {
        String mode = request.getParameter("mode");
        PanShareInfo meta = panService.getShareMeta(panShareId);
        long version = 0;
        long size = meta.getMeta().getBytes();
        String skey = meta.getMeta().getModified_millis()+""+meta.getMeta().getModified_by().getUser_id()+version;
        FileInfo info = new FileInfo(skey, meta.getName(), panShareId, size,null, null, version);
        cacheService.set(skey, info);
        String accessToken = encodeStr("addverr@ss"+"#"+"sssfsddf");
        String url = info.getOwaServerUrl(getOwaServerDomain(), mode, getEncodedCheckFileUrl(skey), accessToken);
        return new ModelAndView(new RedirectView(url));
    }




    private MeePoRevision findTargetRevision(PanAcl acl, String path, long version) throws MeePoException{
        MeePoRevision tv = null;
        if (version != 0) {
            MeePoRevision[] revisions = panService.revisions(acl, path);
            for (MeePoRevision re : revisions) {
                if (re.version == version) {
                    tv = re;
                    break;
                }
            }
        }
        return tv;
    }
    @WebLog(method = "PancheckFile", params = "rid,skey")
    @ResponseBody
    @RequestMapping("/files/{skey}")
    public JSONObject checkFile(@PathVariable("skey") String skey) {
        JSONObject obj = new JSONObject();
        FileInfo info = (FileInfo)cacheService.get(skey);
        if (info == null) {
            obj.put("error", "Null Object");
            return obj;
        }
        obj.put("BaseFileName", info.getFileName());
        obj.put("OwnerId", "clive");
        obj.put("Size", info.getSize());
        obj.put("SHA256", "");
        obj.put("Version", "GIYDCMRNGEYC2MJREAZDCORQGA5DKNZOGIZTQMBQGAVTAMB2GAYA====");
        return obj;
    }
    @WebLog(method = "PanFileContent", params = "rid,skey")
    @RequestMapping("/files/{skey}/contents")
    public void getFileContent(@PathVariable("skey") String skey, HttpServletRequest request,
                               HttpServletResponse response) {
        sendPreviewDoc(skey, request, response);
    }

    private String getEncodedCheckFileUrl(String skey) throws UnsupportedEncodingException {
        String check = URLEncoder.encode(getWopiCheckFileUrl(getServerDomain(), skey),"utf-8");
        return check;
    }

    private String getWopiCheckFileUrl(String serverDomain, String fileSn) {
        String checkURL = serverDomain + "/wopi/files/" + fileSn;
        System.out.println(checkURL);
        return checkURL;
    }

    private String getServerDomain() {
        return systemProperty.getProperty("duckling.baseURL") ;
    }

    private String getOwaServerDomain() {
        return (String) systemProperty.get("clb.wopi.domain");
    }

}
