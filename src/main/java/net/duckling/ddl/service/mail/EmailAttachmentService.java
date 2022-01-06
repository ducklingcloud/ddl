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
package net.duckling.ddl.service.mail;

import java.util.List;

import net.duckling.ddl.service.resource.Resource;

public interface EmailAttachmentService {
    /**
     * 创建映射关系
     * @param attachment
     * @return
     */
    int createEmailAttach(EmailAttachment attachment);
    /**
     * 查找与用户相关的文件
     * @param uid
     * @param tid
     * @return
     */
    List<EmailAttachment> getEmailAttachByUidAndTid(String uid,int tid);
    /**
     * 通过附件的唯一标识符进行查询
     * @param mid
     * @return
     */
    List<EmailAttachment> getEmailAttachByMid(String mid);
    /**
     * 更新映射关系
     * @param attachment
     * @return
     */
    boolean updateEmailAttach(EmailAttachment attachment);
    /**
     * 通过mid获取用户可用附件文件列表
     * @param mid
     * @param tid
     * @param uid
     * @return
     */
    List<Resource> getFileByEmailMidAndUid(String mid,int tid,String uid);
    /**
     * 删除映射关系
     * @param id
     * @return
     */
    boolean deleteEmailAttach(int id);
    List<Resource> getFileByTidAndTitle(String title,int tid);
    /**
     * 获取团队中的所有文件
     * @param uid
     * @param tid
     * @return
     */
    List<Resource> getFileByTid(int[] tids,int offset,int rows);
    int getTeamFileCount(int[] tids);
}
