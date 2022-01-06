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

import java.util.List;

/**
 * ddl附件发送器
 * @author zhonghui
 *
 */
public interface IEmailAttachmentSender {
    /**
     * 将文件发送到指定的ddl和clb中
     * @param fileName
     * @param email
     * @param fileSize
     * @param attachmentStream
     * @return
     */
    AttachmentPushResult sendAttachToDDL( String email,AttachmentInfo attachment);

    AttachmentPushResult sendAttachToDDL( String email,int teamId,AttachmentInfo attachment);
    /**
     * 将文件发送到指定的ddl和clb中,并按给定顺序进行返回结果
     * @param fileName
     * @param email
     * @param fileSize
     * @param attachmentStream
     * @return
     */
    List<AttachmentPushResult> sendAttachToDDL( String email,List<AttachmentInfo> attachments);
    List<AttachmentPushResult> sendAttachToDDL( String email,int teamId,List<AttachmentInfo> attachments);

    /**
     * 关闭当前资源
     */
    void close();
}
