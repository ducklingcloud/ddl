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
package net.duckling.ddl.service.team.impl;


/**
 * vmt中team用户管理
 * @author zhonghui
 *
 */
public interface VmtUserManager {
    /**
     * 向vmt中添加用户
     * @param tid
     * @param uid
     * @return
     */
    boolean addUserToVmt(int tid,String uid);
    /**
     * 移除vmt中用户
     * @param tid
     * @param uid
     * @return
     */
    boolean removeUserToVmt(int tid,String uid);
    /**
     * 向VMT中移除管理员
     * @param tid
     * @param uid
     * @param isRomoveUser 是否移除用户
     * @return
     */
    boolean removeAdminToVmt(int tid,String uid,boolean isRomoveUser);
    /**
     * 向VMT中添加管理员
     * @param tid
     * @param uid
     * @param isAddUser 用户是否存在
     * @param flag
     * @return
     */
    boolean addAdminToVmt(int tid,String uid,boolean isExistUser);
}
