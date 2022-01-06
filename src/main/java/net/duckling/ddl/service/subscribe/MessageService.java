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
package net.duckling.ddl.service.subscribe;

import java.util.List;

public interface MessageService {

    void updateFeedTypeMessages(String user, int tid);

    void updateRecommendTypeMessages(String user, int tid);

    int[] countMessages(int tid, String user);

    void updateMessageStatus(int tid, int pageId, String userId,
                             String messageType);

    void saveRecommend(int tid, MessageBody body, int pageId, String[] userIds);

    void saveRecommendCommentMessage(int tid, MessageBody body,
                                     Publisher publisher, String userId);

    void saveFeedCommentMessage(int tid, MessageBody body, int pageId,
                                int commentId);

    void removePageMessages(int tid, int pageId);

    void savePageMessage(int tid, MessageBody message, int pageId);

    void saveAttachmentMessage(int tid, MessageBody message, String userName);

    List<Message> getMessage(int tid, String userId);
}
