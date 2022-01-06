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
package net.duckling.ddl.service.devent;

import java.util.List;

import net.duckling.ddl.service.resource.Resource;
import net.duckling.ddl.service.task.Task;
import net.duckling.ddl.service.task.TaskTaker;

public interface EventDispatcher {
    void sendFileDeleteEvent(int tid, Resource file, String actor);

    void sendFileModifyEvent(String title, int fid, String author, int version,
                             int tid);

    void sendFileRecommendEvent(int tid, int fid, String title, String author,
                                int version, String message, String recipients, String emailSendType);

    void sendFileRecoverEvent(int tid, Resource file, String actor);

    void sendFileUploadEvent(String title, int fid, String author, int tid);

    void sendPageCreateEvent(int tid, Resource resource);
    void sendFolderCreateEvent(int tid, Resource resource);

    /**
     * 页面删除事件
     *
     * @param site
     * @param page
     */
    void sendPageDeleteEvent(int tid, Resource resource, String actor);

    void sendPageModifyEvent(int tid, Resource resource);

    void sendPageRecommendEvent(int tid, int pid, String title, String author,
                                int version, String message, String recipients, String emailSendType);

    /**
     * 页面恢复事件
     *
     * @param site
     * @param page
     * @param actor
     */
    void sendPageRecoverEvent(int tid, Resource resource, String actor);

    void sendPageRenameEvent(int tid, Resource meta, String oldName, int version,String uid);
    void sendFolderRenameEvent(int tid, Resource meta, String oldName,String uid);
    void sendResourceRenameEvent(int tid, Resource meta, String oldName,String uid);
    void sendResourceCopyEvent(int tid,Resource meta,int destRid);
    void sendResourceMoveEvent(int tid,Resource meta,int destRid,String actor);
    void sendResourceCommentEvent(int tid, int rid, String itemType,
                                  String title, String creator, int version, String message);

    /**
     * 发送评论提及事件
     */
    void sendResourceMentionEvent(int tid, int rid, String itemType,
                                  String title, int version, String comment, String creator,
                                  String recipients);

    void sendResourceReplyEvent(int tid, int rid, String itemType,
                                String title, String creator, int version, String message,
                                String recipients);

    /* add by lvly@2012-06-13 */
    /**
     * 在任务发布的时候发送通知
     *
     * @param site
     *            获取到的Site实例
     * @param taker
     *            接受者实例
     * @param action
     *            执行动作
     * @param noticeType
     *            任务类型
     * @deprecated
     * */
    void sendTaskActionEvent(int tid, List<TaskTaker> takers, Task task,
                             String action, String actor, String noticeType);

    void sendFolderRecommendEvent(int tid, int rid, String title, String currUser, int lastVersion, String remark,
                                  String combineRecipients, String sendType);

    void sendFolderDeleteEvent(int tid, Resource r, String uid);

    void sendFolderRecoverEvent(int tid, Resource r, String currentUID);
}
