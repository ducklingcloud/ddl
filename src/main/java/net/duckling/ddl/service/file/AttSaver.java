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

package net.duckling.ddl.service.file;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;

import javax.servlet.ServletContext;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import net.duckling.ddl.util.Browser;
import net.duckling.ddl.util.MimeType;

import org.apache.commons.lang.StringUtils;
import org.apache.log4j.Logger;

//实现接口FileSaver;
public class AttSaver implements DFileSaver {
    private static final Logger LOG = Logger.getLogger(AttSaver.class);
    OutputStream out = null;
    String fileNameRe;
    HttpServletResponse m_res = null;

    HttpServletRequest m_req = null;
    private long length=-1;

    public AttSaver(HttpServletResponse res, HttpServletRequest req,String fileName) {
        this.m_res = res;
        this.m_req = req;
        this.fileNameRe=fileName;
    }

    public AttSaver(HttpServletResponse res, HttpServletRequest req) {
        this.m_res = res;
        this.m_req = req;
        this.fileNameRe=null;
    }

    public void save(String filename, InputStream in) {
        if(null!=this.fileNameRe&&!"".equals(this.fileNameRe)){
            filename=this.fileNameRe;
        }
        try {
            filename = java.net.URLDecoder.decode(filename, "UTF-8");
            String mimetype = getMimeType(m_req, filename);
            m_res.setContentType(mimetype);
            setContentLength();
            String suffix = filename.substring(filename.indexOf(".") + 1, filename.length());

            m_res.setContentType(MimeType.getContentType(suffix));
            if (filename.indexOf("swf") != -1) {
                // flash不能设置filename,否则无法播放
                m_res.setContentType("application/x-shockwave-flash");
            } else {
                m_res.setHeader("Content-Disposition", Browser.encodeFileName(m_req.getHeader("USER-AGENT"), filename));
            }

            if (out == null){
                out = m_res.getOutputStream();
            }
            if(StringUtils.isEmpty(m_req.getHeader("range"))){
                int read = 0;
                byte buf[] = new byte[4096];
                while ((read = in.read(buf, 0, 4096)) != -1) {
                    out.write(buf, 0, read);
                }
            }else{
                RangeDownload range = new RangeDownload(in);
                range.read();
            }
            if (out != null) {
                out.close();
            }
        } catch (FileNotFoundException e) {
            LOG.info("没有找到文件。");

        } catch (IOException e) {
            LOG.info("文件下载出错。",e);

        }
    }

    private void setContentLength() {
        if(m_res!=null&&length!=-1){
            m_res.setHeader("Content-Length",length+"");
        }
    }
    private static String getMimeType(HttpServletRequest req, String fileName) {
        String mimetype = null;

        if (req != null) {
            ServletContext s = req.getSession().getServletContext();

            if (s != null) {
                mimetype = s.getMimeType(fileName.toLowerCase());
            }
        }

        if (mimetype == null) {
            mimetype = "application/binary";
        }

        return mimetype;
    }

    @Override
    public void setLength(long length) {
        this.length = length;
    }
    /**
     * 断点续传<br/>
     * 现在支持100-200，500-，-1000三种形式
     * @author zhonghui
     *
     */
    private class RangeDownload{
        private long begin;
        private long endLength;
        private InputStream in;
        RangeDownload(InputStream in){
            this.in = in;
            //
            //100-200    第100到第200字节
            //500-       第500字节到文件末尾
            //-1000      最后的1000个字节
            String range = m_req.getHeader("range").toLowerCase();
            String e = range.substring("bytes ".length());
            String[] end = e.split("-");
            if(e.indexOf('-')==0){
                int n = Integer.parseInt(end[1]);
                begin = length-n;
                endLength=length-1;
            }else{
                if(end.length==1){
                    begin = Long.parseLong(end[0]);
                    endLength = length-1;
                }else{
                    begin = Long.parseLong(end[0]);
                    endLength = Long.parseLong(end[1]);
                }
            }
        }

        public void read() throws IOException{
            m_res.setStatus(206);
            setContentLength();
            String contentRange = "bytes "+begin+"-"+endLength+"/"+length;
            m_res.setHeader("Content-Range", contentRange);
            m_res.setHeader("Content-length", (endLength-begin+1)+"");
            readRang();
        }

        private void readRang() throws IOException{
            int read = 0;
            byte buf[] = new byte[4096];
            int offer=0;
            out = m_res.getOutputStream();
            while((read=in.read(buf))!=-1){
                if(begin>read+offer){
                    offer+=read;
                }else{
                    int b = 0;
                    int size = read;
                    if(begin>=offer&&begin<=(read+offer-1)){
                        b=(int)begin-offer;
                        size = read-b;
                    }
                    if(endLength<=(read+offer-1)){
                        size = read-b-(read+offer-1-(int)endLength);
                    }

                    if(endLength>=(read+offer-1)){
                        out.write(buf, b, size);
                        offer+=read;
                    }else if(endLength>offer&&endLength<=(read+offer-1)){
                        out.write(buf, b, size);
                        break;
                    }else{
                        break;
                    }
                }
            }
        }
    }
}
