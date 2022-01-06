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
package net.duckling.ddl.service.invitation;

import java.util.List;

import net.duckling.ddl.service.authenticate.UserPrincipal;

/**
 * 邀请管理服务
 * @author xiejj@cstnet.cn
 *
 */
public interface ClientInviteService {
    /**
     * 邀请安装加密客户端
     * @param invitor   邀请者
     * @param invitee   被邀请者
     */
    void invite(UserPrincipal invitor, String invitee);
    /**
     * 被邀请者已经安装了客户端
     * @param invitee   被邀请者
     */
    void accept(String invitee);
    /**
     * 获取用户收到的消息
     * @param username   用户名
     * @return  未读消息
     */
    List<String> readMessage(String username);
}
