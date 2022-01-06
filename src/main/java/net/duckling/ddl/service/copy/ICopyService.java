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
package net.duckling.ddl.service.copy;



/**
 * @author lvly
 * @since 2012-11-13
 */
public interface ICopyService {
    /**
     * 执行复制
     * @param fromRid   复制源rid
     * @param version 复制源文件版本
     * @param fromTid   复制源团队
     * @param toTid 复制目标团队
     * @param cover 用户的勾选情况，创建还是覆盖
     * @param uid 复制人
     * @throws CopyException
     */
    public void doCopy(int fromRid,int version,int fromTid, int[] toTid,boolean[] cover,String uid)throws CopyException;

    /**
     * 判定是否有可能出发覆盖操作
     * @param fromRid 复制源rid
     * @param toTid 目标团队
     * */
    public boolean isNeedCover(int fromRid, int toTid);

    /**
     * 看这个文件的某个版本是不是复制过来的
     * @param rid 待判定rid
     * @param version 待判定版本号
     * @return CopyLogDisplay 便于显示的copylog，给页面用的
     * */
    public CopyLogDisplay getCopyedDisplay(int rid,int version);
}
