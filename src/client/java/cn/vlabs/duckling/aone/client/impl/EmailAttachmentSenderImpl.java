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

import java.io.IOException;
import java.io.PrintWriter;
import java.io.StringWriter;
import java.io.Writer;
import java.net.URLEncoder;
import java.text.ParseException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import org.apache.commons.httpclient.HttpClient;
import org.apache.commons.httpclient.HttpException;
import org.apache.commons.httpclient.methods.PostMethod;
import org.apache.commons.httpclient.params.HttpMethodParams;
import org.json.JSONObject;

import cn.vlabs.clb.api.CLBConnection;
import cn.vlabs.clb.api.CLBPasswdInfo;
import cn.vlabs.clb.api.CLBServiceFactory;
import cn.vlabs.clb.api.document.CreateInfo;
import cn.vlabs.clb.api.document.DocumentService;
import cn.vlabs.duckling.aone.client.AttachmentInfo;
import cn.vlabs.duckling.aone.client.AttachmentPushResult;
import cn.vlabs.duckling.aone.client.IEmailAttachmentSender;
import cn.vlabs.rest.ServiceContext;
import cn.vlabs.rest.stream.StreamInfo;

/**
 *
 * @author zhonghui
 *
 */
public class EmailAttachmentSenderImpl implements IEmailAttachmentSender {
    private static String CLBID = "clbId";
    private static String FILESIZE = "fileSize";
    private static String EMAIL = "email";
    private static String FILENAME = "fileName";
    private static String FILEID = "mid";
    private static String TEAMID="teamId";

    public final static String STATUS_CODE = "statusCode";
    public final static String ERROR_MESSAGE = "message";

    private CLBConnection conn;
    private String ddlCreatFile;
    private String ddlFindFile;
    private HttpClient ddlClient;

    public EmailAttachmentSenderImpl(String clbAddress, String clbUserName, String clbPassword, String ddlAddress) {
        ServiceContext.setMaxConnection(20, 20);
        CLBPasswdInfo pwd = new CLBPasswdInfo();
        pwd.setUsername(clbUserName);
        pwd.setPassword(clbPassword);
        this.conn = new CLBConnection(clbAddress, pwd);
        this.ddlCreatFile = ddlAddress+"";
        this.ddlFindFile = ddlAddress+"?func=findEmailAttachment";
        ddlClient = new HttpClient();
        ddlClient.getHttpConnectionManager().getParams().setConnectionTimeout(30000);
    }

    @Override
    public AttachmentPushResult sendAttachToDDL(String email,int teamId,AttachmentInfo attachment) {
        AttachmentPushResult result = null;
        if (validateAttachment(attachment)) {
            result = new AttachmentPushResult();
            result.setStatusCode(AttachmentPushResult.ARGUE_ERROR);
            result.setMessage("输入参数出现异常attachment = "+attachment);
            setAttachResult(result, attachment);
            return result;
        }
        if((result=validateExsit(email,teamId, attachment))!=null){
            return result;
        }
        try {
            int docId = createFile(attachment);
            return createFileResouceInDDL(email,teamId, docId,attachment);
        } catch (Exception e) {
            result = new AttachmentPushResult();
            result.setStatusCode(AttachmentPushResult.RUNTIME_ERROR);
            result.setMessage("客户端处理时发生" + e.getMessage() + "错误,异常信息:\n"+ getStackTrace(e));
            setAttachResult(result, attachment);
            return result;
        }
    }

    public static String getStackTrace(Throwable aThrowable) {
        final Writer result = new StringWriter();
        final PrintWriter printWriter = new PrintWriter(result);
        aThrowable.printStackTrace(printWriter);
        return result.toString();
    }

    private void setAttachResult(AttachmentPushResult result,AttachmentInfo attachment){
        if(result!=null && attachment !=null){
            result.setFileName(attachment.getFileName());
            result.setMid(attachment.getFileId());
        }
    }
    /**
     * 校验附件是否在DDL中已存在
     * @param email
     * @param attachment
     * @return
     */
    private AttachmentPushResult validateExsit(String email , int teamId, AttachmentInfo attachment){
        if(attachment.getCoverFlag()){
            return null;
        }
        PostMethod method = new PostMethod(getDDLValidateIp());
        method.getParams().setParameter(HttpMethodParams.HTTP_CONTENT_CHARSET,"utf-8");
        method.addParameter(FILENAME, attachment.getFileName());
        method.addParameter(EMAIL, email);
        method.addParameter(FILESIZE, attachment.getFileSize() + "");
        method.addParameter(FILEID,attachment.getFileId());
        method.addParameter(TEAMID, teamId+"");
        AttachmentPushResult result = new AttachmentPushResult();
        result.setFileName(attachment.getFileName());
        try {
            int status  = ddlClient.executeMethod(method);
            if (status >= 200 && status < 300) {
                String responseString =method.getResponseBodyAsString();
                return dealValidateResult(responseString);
            } else {
                result.setStatusCode(AttachmentPushResult.NETWORK_ERROR);
                result.setMessage("与DDL连接发生" + status + "错误");
                return result;
            }
        } catch (HttpException e) {
            result.setStatusCode(AttachmentPushResult.NETWORK_ERROR);
            result.setMessage("与DDL连接发生" + e.getMessage() + "错误");
            return result;
        } catch (IOException e) {
            result.setStatusCode(AttachmentPushResult.IO_ERROR);
            result.setMessage("ddl的Io出现问题");
            e.printStackTrace();
            return result;
        }finally{
            method.releaseConnection();
        }
    }

    private AttachmentPushResult dealValidateResult(String response){

        try {
            Map<String,Object> resultMap = dealJsonResult(response);
            Object status = resultMap.get("statusCode");
            if (status != null) {
                int code = Integer.parseInt(status.toString());
                switch (code) {
                    case 1:
                        return null;
                    case 4:
                        AttachmentPushResult result = new AttachmentPushResult();
                        result.setStatusCode(AttachmentPushResult.FILE_EXIST);
                        result.setAttachmentURL(resultMap.get("attachmentURL").toString());
                        result.setMessage("附件已经存在");
                        return result;
                    default:
                        AttachmentPushResult result1 = new AttachmentPushResult();
                        result1.setStatusCode(AttachmentPushResult.RUNTIME_ERROR);
                        result1.setMessage("DDL处理数据错误：[DDL message:" + resultMap.get("message") + "]");
                        return result1;
                }
            }
        }catch (Exception e) {
            AttachmentPushResult result1 = new AttachmentPushResult();
            result1.setStatusCode(AttachmentPushResult.RUNTIME_ERROR);
            result1.setMessage("DDL处理数据错误：[DDL message:" + e.getMessage() + "]");
            return result1;
        }
        AttachmentPushResult result1 = new AttachmentPushResult();
        result1.setStatusCode(AttachmentPushResult.RUNTIME_ERROR);
        result1.setMessage("DDL校验处理数据错误");
        return result1;
    }

    private boolean validateAttachment(AttachmentInfo attachment){
        if(isEmpty(attachment.getFileId())||isEmpty(attachment.getFileName())||attachment.getFileSize()<=0||attachment.getAttachmentStream()==null){
            return true;
        }
        return false;
    }
    private boolean isEmpty(String s){
        if(s==null||s.length()==0){
            return true;
        }
        return false;
    }
    /**
     * 附件上传至clb
     *
     * @param filename
     * @param length
     * @param in
     * @return clbId附件在clb中的id
     */
    private int createFile(AttachmentInfo attachment) {
        DocumentService dService = CLBServiceFactory.getDocumentService(conn);
        CreateInfo info = new CreateInfo();
        info.title = attachment.getFileName();
        StreamInfo stream = new StreamInfo();
        stream.setFilename(attachment.getFileName());
        stream.setLength(attachment.getFileSize());
        stream.setInputStream(attachment.getAttachmentStream());
        return dService.createDocument(info, stream).docid;
    }

    /**
     * 将文件docId加入email的默认团队
     *
     * @param fileName
     * @param email
     * @param teamId
     * @param docId
     * @param fileSize
     * @return
     */
    private AttachmentPushResult createFileResouceInDDL( String email, int teamId,int docId, AttachmentInfo attachment ) {
        AttachmentPushResult result = new AttachmentPushResult();
        setAttachResult(result, attachment);
        PostMethod method = new PostMethod(getDDLIp());
        method.getParams().setParameter(HttpMethodParams.HTTP_CONTENT_CHARSET,"utf-8");
        method.addParameter(CLBID, docId + "");
        method.addParameter(EMAIL, email);
        method.addParameter(TEAMID, teamId+"");
        method.addParameter(FILESIZE, attachment.getFileSize() + "");
        method.addParameter(FILEID,attachment.getFileId());
        try {
            method.addParameter(FILENAME, URLEncoder.encode(attachment.getFileName(),"UTF-8"));
            int status  = ddlClient.executeMethod(method);
            if (status >= 200 && status < 300) {
                String responseString =method.getResponseBodyAsString();
                AttachmentPushResult r = dealHttpResponse(responseString);
                setAttachResult(r, attachment);
                return r;
            } else {
                result.setStatusCode(AttachmentPushResult.NETWORK_ERROR);
                result.setMessage("与DDL连接发生" + status + "错误");
                return result;
            }
        } catch (HttpException e) {
            result.setStatusCode(AttachmentPushResult.NETWORK_ERROR);
            result.setMessage("与DDL连接发生" + e.getMessage() + "错误");
            return result;
        } catch (IOException e) {
            result.setStatusCode(AttachmentPushResult.IO_ERROR);
            result.setMessage("ddl的Io出现问题");
            return result;
        } catch (Exception e) {
            result.setStatusCode(AttachmentPushResult.IO_ERROR);
            result.setMessage("ddl的出现问题"+e.getStackTrace());
            return result;
        }finally{
            method.releaseConnection();
        }
    }

    private String getDDLIp() {
        return ddlCreatFile;
    }
    private String getDDLValidateIp() {
        return ddlFindFile;
    }

    /**
     * 处理ddl返回值
     *
     * @param response
     * @return
     */
    private AttachmentPushResult dealHttpResponse(String response) {

        AttachmentPushResult result = new AttachmentPushResult();
        try {
            Map<String, Object> resultMap = dealJsonResult(response);
            Object status = resultMap.get("statusCode");
            if (status != null) {
                try {
                    int code = Integer.parseInt(status.toString());
                    switch (code) {
                        case 0:
                            result.setStatusCode(AttachmentPushResult.NORMAL);
                            result.setMessage("操作成功");
                            result.setAttachmentURL(resultMap.get("attachmentURL").toString());
                            return result;
                        case 2:
                        case 3:
                            result.setStatusCode(AttachmentPushResult.ARGUE_ERROR);
                            result.setMessage("传给DDL参数错误：[ddl message:" + resultMap.get("message") + "]");
                            return result;
                        case 9:
                            result.setStatusCode(9);
                            result.setMessage( resultMap.get("message")+"");
                            return result;
                        default:
                            result.setStatusCode(AttachmentPushResult.RUNTIME_ERROR);
                            result.setMessage("DDL处理数据错误：[DDL message:" + resultMap.get("message") + "]");
                            return result;
                    }
                } catch (RuntimeException e) {
                }
            }
        } catch (ParseException e) {
        }

        result.setStatusCode(AttachmentPushResult.ARGUE_ERROR);
        result.setMessage("ddl返回值格式错误[responseString:" + response + "]");
        return result;
    }

    /**
     * 将json串转换成map，注意json串只支持简单键值对，复杂对象嵌套、数组不支持，如想支持请使用json解析包
     *
     * @param response
     * @return
     * @throws ParseException
     */
    private Map<String, Object> dealJsonResult(String response) throws ParseException {
        Map<String,Object> result = new HashMap<String,Object>();
        JSONObject obj =new JSONObject(response);
        Iterator<String> keys = obj.keys();
        String key = null;
        while(keys.hasNext()){
            key = keys.next();
            result.put(key,obj.get(key));
        }
        return result;
    }

    @Override
    public void close() {
        conn.releaseConnection();
    }

    @Override
    public List<AttachmentPushResult> sendAttachToDDL(String email,int teamId, List<AttachmentInfo> attachments) {
        List<AttachmentPushResult> results = new ArrayList<AttachmentPushResult>();
        if(attachments==null||attachments.isEmpty()){
            AttachmentPushResult result = new AttachmentPushResult();
            result.setStatusCode(AttachmentPushResult.ARGUE_ERROR);
            result.setMessage("输入参数出现异常[email=" + email + ",attachments=" + attachments+"]");
            results.add(result);
            return results;
        }
        for(AttachmentInfo info : attachments){
            AttachmentPushResult r = sendAttachToDDL(email, teamId,info);
            results.add(r);
        }
        return results;
    }



    public static void main(String[] args) throws IOException {
        //      EmailAttachmentSenderImpl email = new EmailAttachmentSenderImpl(PropertiesUtils.getProperty("clb.address"),
        //              PropertiesUtils.getProperty("clb.name"), PropertiesUtils.getProperty("clb.password"),
        //              PropertiesUtils.getProperty("ddl.address"));
        //      InputStream in = new FileInputStream("D:\\test\\IMG_6126.JPG");
        //      AttachmentInfo info = new AttachmentInfo("IMG_6126%.JPG",in.available(),"sssii980987677",in,true);
        //      System.out.println(email.sendAttachToDDL("zhonghui@cnic.cn","IMG_6126.JPG",  in.available(),
        //              new FileInputStream("D:\\test\\IMG_6126.JPG")));
        IOException e = new IOException();
        System.out.println(getStackTrace(e));
    }

    @Override
    public AttachmentPushResult sendAttachToDDL(String email, AttachmentInfo attachment) {
        return sendAttachToDDL(email, 0,attachment);
    }

    @Override
    public List<AttachmentPushResult> sendAttachToDDL(String email, List<AttachmentInfo> attachments) {
        return sendAttachToDDL(email, 0, attachments);
    }
}
