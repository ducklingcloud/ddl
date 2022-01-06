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
/**
 *
 */
package net.duckling.ddl.service.copy.impl;

import net.duckling.ddl.service.copy.CopyLog;

/**
 *
 * copy行为的记录，持久层
 * @author lvly
 * @since 2012-11-13
 */
public interface CopyDAO {
    /**
     * 判断一个文件是否是复制过来的
     * @param rid 待判定的文件rid
     * @return boolean 是否是复制过来的文件
     * */
    boolean isCopyed(int rid);
    /**
     * 判断需要做更新操作还是创建操作
     * <p>判断依据只有两个，一个是，他发过吗？，一个是他收到过吗？
     * @param
     * @return true为更新，false为创建
     * */
    boolean isDoUpdate(int fromRid,int toTeamId);

    /**
     *创建一个copyLog
     *@param copy entity
     *@param return
     * */
    int addCopyLog(CopyLog copy);

    /**
     * @param fromRid
     * @param toTid
     * @return
     */
    int getToRid(int fromRid, int toTid);

    /**
     * 用于获取复制来源
     * @param toRid
     * @param toVersion
     * @return
     */
    CopyLog getCopyLogByTo(int toRid, int toVersion);

}
