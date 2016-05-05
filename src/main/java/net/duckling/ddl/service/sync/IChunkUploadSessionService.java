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
package net.duckling.ddl.service.sync;

public interface IChunkUploadSessionService {
    /**
     * 创建一个块上传会话，默认状态为appending。
     * @param clbId
     * @return 唯一标识一个块文件上传的会话的sessionId字符串
     */
    String create(Long clbId);
    
    /**
     * 创建一个状态为status的块上传会话。
     * @param clbId
     * @param status
     * @return 唯一标识一个块文件上传的会话的sessionId字符串
     */
    String create(Long clbId, String status);

    /**
     * @param sessionId
     * @return 对应的块上传会话。如果未找到，返回null。
     */
    ChunkUploadSession get(String sessionId);
}
