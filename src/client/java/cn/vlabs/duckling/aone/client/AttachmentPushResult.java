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
package cn.vlabs.duckling.aone.client;

/**
 *
 * 附件添加结果
 *
 * @author zhonghui
 *
 */
public class AttachmentPushResult {
    public static int NORMAL = 1;
    public static int NETWORK_ERROR = 2;
    public static int IO_ERROR = 3;
    public static int RUNTIME_ERROR = 4;
    public static int ARGUE_ERROR = 5;
    public static int FILE_EXIST=7;

    private int statusCode;
    private String message;
    /**
     * 附件在DDL中的URL
     */
    private String attachmentURL;

    private String fileName;

    private String mid;


    public String getMid() {
        return mid;
    }

    public void setMid(String mid) {
        this.mid = mid;
    }

    public int getStatusCode() {
        return statusCode;
    }

    public void setStatusCode(int statusCode) {
        this.statusCode = statusCode;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public String getAttachmentURL() {
        return attachmentURL;
    }

    public void setAttachmentURL(String attachmentURL) {
        this.attachmentURL = attachmentURL;
    }

    public String getFileName() {
        return fileName;
    }

    public void setFileName(String fileName) {
        this.fileName = fileName;
    }

    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder();
        sb.append("[fileName=").append(fileName).append(",statusCode=").append(statusCode).append(",").append("message=").append(message)
                .append(",").append("attachmentURL=").append(attachmentURL);
        return sb.toString();
    }
}
