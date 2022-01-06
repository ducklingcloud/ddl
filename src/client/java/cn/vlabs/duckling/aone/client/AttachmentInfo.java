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

import java.io.InputStream;

/**
 * 附加信息
 * @author zhonghui
 *
 */
public class AttachmentInfo {
    private String fileName;
    private long fileSize;
    private InputStream attachmentStream;
    private String fileId;
    private boolean coverFlag;
    public AttachmentInfo(String fileName,long fileSize,String fileId,InputStream attachmentStream,boolean coverFlag){
        this.fileName = fileName;
        this.fileSize = fileSize;
        this.attachmentStream = attachmentStream;
        this.fileId = fileId;
        this.coverFlag = coverFlag;
    }

    public String getFileName() {
        return fileName;
    }
    public long getFileSize() {
        return fileSize;
    }
    public InputStream getAttachmentStream() {
        return attachmentStream;
    }
    public String getFileId(){
        return fileId;
    }
    public boolean getCoverFlag(){
        return coverFlag;
    }

    @Override
    public String toString() {
        return "[fileName=" + fileName + ",fileSize=" + fileSize
                + ",fileId="+fileId+",attachmentStream=" + attachmentStream + "]";
    }
}
