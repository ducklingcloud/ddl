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
package net.duckling.ddl.web.tag;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;

import org.apache.log4j.Logger;
import org.springframework.web.context.WebApplicationContext;
import org.springframework.web.servlet.support.RequestContextUtils;

import net.duckling.ddl.common.DDLFacade;
import net.duckling.common.DucklingProperties;
import net.duckling.ddl.constant.LynxConstants;
import net.duckling.ddl.service.browselog.BrowseLogService;
import net.duckling.ddl.service.file.FileVersion;
import net.duckling.ddl.service.file.FileVersionService;
import net.duckling.ddl.service.resource.IResourceService;
import net.duckling.ddl.service.resource.Resource;
import net.duckling.ddl.service.resource.ResourceOperateService;
import net.duckling.ddl.service.url.URLGenerator;
import net.duckling.ddl.service.url.UrlPatterns;
import net.duckling.ddl.util.FileSizeUtils;
import net.duckling.ddl.util.HtmlTemplate;
import net.duckling.ddl.util.PlainTextHelper;
import net.duckling.ddl.web.bean.ClbHelper;
import net.duckling.ddl.web.bean.DirectShowFileSaver;
import net.duckling.ddl.web.bean.FileType;
import net.duckling.ddl.web.bean.FileTypeHelper;
import cn.vlabs.clb.api.AccessForbidden;
import cn.vlabs.clb.api.document.MetaInfo;

public class FileShowTag extends VWBBaseTag {

    protected static final Logger LOGGER = Logger.getLogger(FileShowTag.class);
    public static final int PDF_MAX_SIZE = 52428800;

    /**
     *
     */
    private static final long serialVersionUID = 1L;
    private int rid;
    private int version;
    private String pdfView;
    private String theme;   //模板样式， 默认是default(路径是templates/html/viewer/default)，主题以文件夹名区分
    private String ridCode; //分享加密的rid


    public String getRidCode() {
        return ridCode;
    }

    public void setRidCode(String ridCode) {
        this.ridCode = ridCode;
    }

    /**
     * @return the fid
     */
    public int getRid() {
        return rid;
    }

    /**
     * @param fid the fid to set
     */
    public void setRid(int rid) {
        this.rid = rid;
    }

    /**
     * @return the version
     */
    public int getVersion() {
        return version;
    }

    /**
     * @param version the version to set
     */
    public void setVersion(int version) {
        this.version = version;
    }

    public String getPdfView() {
        return pdfView;
    }

    public void setPdfView(String pdfView) {
        this.pdfView = pdfView;
    }

    public String getTheme() {
        return theme == null ? "default" : theme;
    }

    public void setTheme(String theme) {
        this.theme = theme;
    }

    @Override
    public int doVWBStart() throws Exception {

        HttpServletRequest request = (HttpServletRequest) pageContext.getRequest();


        URLGenerator urlGenerator = DDLFacade.getBean(URLGenerator.class);
        Resource res =  DDLFacade.getBean(IResourceService.class).getResource(rid);

        FileVersionService ifs = DDLFacade.getBean(FileVersionService.class);
        int tid = res.getTid();
        FileVersion fv = (version == 0) ? ifs.getLatestFileVersion(rid, tid) :
                ifs.getFileVersion(rid, tid, version);

        String fileName = fv.getTitle();
        String ext = FileTypeHelper.getFileExt(fileName);
        String fileViewName = (this.pdfView == FileType.OFFICE) ? FileTypeHelper.getName(fileName)
                : FileTypeHelper.getName(fileName);

        Map<String,String> datas = new HashMap<String,String>();
        datas.put("contextPath", request.getContextPath());
        if(ridCode == null){
            datas.put("downloadURL", urlGenerator.getURL(tid, "download", Integer.toString(rid), "type=doc&version=" + fv.getVersion()));
        }else{
            int fileRid = 0;
            if(request.getParameter("rid") == null){
                fileRid = rid;
            }else{
                fileRid = Integer.parseInt(request.getParameter("rid"));
            }
            datas.put("downloadURL",
                      urlGenerator.getURL(UrlPatterns.RESOURCE_SHARE, null, null) +
                      "/" + ridCode + "?func=download&rid=" + fileRid);
        }
        datas.put("fileName", fileName);
        datas.put("ext", ext);
        datas.put("sizeShort", FileSizeUtils.getFileSize(fv.getSize()));
        datas.put("downloadMsg", getLocaleMessage(request, "ddl.download"));
        datas.put("notCompatibleMsg", getLocaleMessage(request, "ddl.tip.t11"));

        String html = "";

        if(FileType.TXT.equals(fileViewName)){
            html = txtView(fv, datas, ext, request);
        }else if(FileType.IMAGE.equals(fileViewName)){
            html = imageView(fv, datas, urlGenerator, tid, rid);
        }else if(FileType.PDF.equals(fileViewName)){
            html = pdfView(fv, datas, urlGenerator, tid, rid, request);
        }else if(FileType.OFFICE.equals(fileViewName)){
            html = officeView(fv, datas, urlGenerator, tid, rid, request);
        }else{
            html = fileView(fv, datas, ext, request);
        }

        pageContext.getOut().print(html);

        DDLFacade.getBean(BrowseLogService.class).resourceVisited(tid, rid, vwbcontext.getCurrentUID(), vwbcontext.getCurrentUserName(), LynxConstants.TYPE_FILE);;

        HtmlTemplate.clearAll();
        return SKIP_BODY;
    }

    public static String getLocaleMessage(HttpServletRequest request, String code){
        WebApplicationContext ac = RequestContextUtils.getWebApplicationContext(request);
        return ac.getMessage(code, null, RequestContextUtils.getLocale(request));
    }

    private String imageView(FileVersion fv, Map<String,String> datas, URLGenerator urlGenerator, int tid,int rid) throws IOException{
        return HtmlTemplate.merge( "viewer/"+ getTheme() +"/image.html", datas);
    }

    private String pdfView(FileVersion fv, Map<String,String> datas,URLGenerator urlGenerator, int tid,int rid, HttpServletRequest request) throws IOException{
        if(fv.getSize()>FileType.PDF_MAX_SIZE){
            return showMsg(datas, getLocaleMessage(request, "ddl.tip.t12"));
        }

        if(ridCode == null){
            datas.put("pdfUrl", urlGenerator.getURL(tid, UrlPatterns.T_VIEW_R, String.valueOf(rid), "func=onlineViewer&version=" + fv.getVersion()));
        }else{
            String ridParam = "";
            if(request.getParameter("rid") != null){
                ridParam = "&rid=" + request.getParameter("rid");
            }
            datas.put("pdfUrl",
                      urlGenerator.getURL(UrlPatterns.RESOURCE_SHARE, null, null) +  "/" + ridCode + "?func=onlineViewer" + ridParam);
        }

        return HtmlTemplate.merge( "viewer/"+ getTheme() +"/pdf.html", datas);
    }

    private String officeView(FileVersion fv, Map<String,String> datas,URLGenerator urlGenerator, int tid,int rid,HttpServletRequest request) throws IOException{
        if(fv.getSize()>FileType.OFFICE_MAX_SIZE){
            return showMsg(datas, getLocaleMessage(request, "ddl.tip.t13"));
        }

        DucklingProperties properties = DDLFacade.getBean(DucklingProperties.class);
        datas.put("clbPreviewUrl", properties.get("duckling.clb.url")+"/wopi/p?accessToken=" +
                  ClbHelper.getClbToken(fv.getClbId(), fv.getClbVersion(),properties));
        return HtmlTemplate.merge( "viewer/"+ getTheme() +"/office.html", datas);
    }

    /**
     * 不支持预览的文件
     * @param fv
     * @param datas
     * @param ext
     * @return
     * @throws IOException
     */
    private String fileView(FileVersion fv, Map<String,String> datas, String ext, HttpServletRequest request) throws IOException{
        return showMsg(datas, getLocaleMessage(request, "ddl.tip.t10"));
    }

    /**
     * 文本文件
     * @param fv
     * @return
     * @throws IOException
     */
    private String txtView(FileVersion fv, Map<String,String> datas, String ext, HttpServletRequest request) throws IOException{
        if(fv.getSize()>FileType.TXT_MAX_SIZE){
            return showMsg(datas, getLocaleMessage(request, "ddl.tip.t14"));
        }

        ResourceOperateService ros = DDLFacade.getBean(ResourceOperateService.class);
        String result = "";
        if(null != fv){
            try{
                MetaInfo mi = ros.getMetaInfo(fv.getClbId(), fv.getClbVersion()+"");
                DirectShowFileSaver fs = new DirectShowFileSaver(mi.getSize());
                ros.getContent(fv.getClbId(), fv.getClbVersion()+"", fs);
                result = formatString(fs.getFileContent());
            }catch(AccessForbidden e){
                LOG.error("文件读取错误！已被删除或无权访问！", e);
                result = "文件读取错误！有可能该文件已被删除或者您无权访问！";
            }
        }
        datas.put("strFileType", PlainTextHelper.convert2BrushClassFileType(ext));
        datas.put("content", result);
        return HtmlTemplate.merge( "viewer/"+ getTheme() +"/txt.html", datas);
    }

    private String showMsg(Map<String,String> datas, String msg){
        datas.put("msg", msg);
        return HtmlTemplate.merge( "viewer/"+ getTheme() +"/message.html", datas);
    }


    private String formatString(String sourceStr){
        String source = sourceStr;
        source = source.replaceAll("&", "&amp;");
        source = source.replaceAll("<", "&lt;");
        source = source.replaceAll(">", "&gt;");
        source = source.replaceAll("\"", "&quot;");
        return source;
    }
}
