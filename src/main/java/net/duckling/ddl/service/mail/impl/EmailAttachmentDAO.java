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
package net.duckling.ddl.service.mail.impl;

import java.util.List;

import net.duckling.ddl.service.mail.EmailAttachment;
import net.duckling.ddl.service.resource.Resource;

/**
 * 邮件附件与文档文件映射关系管理dao
 * @author zhonghui
 *
 */
public interface EmailAttachmentDAO {
    /**
     * 创建映射关系
     * @param attachment
     * @return
     */
    int create(EmailAttachment attachment);
    /**
     * 更新映射关系
     * @param attachment
     * @return
     */
    boolean update(EmailAttachment attachment);
    /**
     * 删除映射关系
     * @param id
     * @return
     */
    boolean delete(int id);
    /**
     * 查找与用户相关的文件
     * @param uid
     * @param tid
     * @return
     */
    List<EmailAttachment> findByUserAndTid(String uid,int tid);
    /**
     * 通过附件的唯一标识符进行查询
     * @param mid
     * @return
     */
    List<EmailAttachment> findByMid(String mid);

    List<Resource> getFileByEmailMidAndUid(String mid, int tid, String uid);

    List<Resource> getFileByTid(int[] tids, int offset, int rows);

    int getTeamFileCount(int[] tids);

    List<Resource> getFileByTidAndTitle(String title, int tid);
}
