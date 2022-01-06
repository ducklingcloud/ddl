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
package net.duckling.ddl.service.tobedelete;

import java.util.List;

import net.duckling.ddl.service.file.DFileRef;


public interface FileDAO {
    int create(File file);

    /**删除文件，现改为更改标记位
     * @author lvly
     * @since 2012-07-20
     * @param fId FileID
     * @param tID teamId
     * @return 1;
     * */
    int delete(int fid, int tid);
    /**
     * 批量删除文件，即更新标记位
     * @param tid 团队ID
     * @param fids 文件ID集合
     * @return
     */
    int batchDelete(int tid, List<Integer> fids);
    int update(int fid, int tid, File file);
    int update(File file);
    File getFile(int fid, int tid);

    List<File> getFilesOfTeam(int tid, int offset, int size);
    List<DFileRef> getReferenceOfDFile(int fid,int tid);
    void deleteDFileReference(int fid, int tid);
    void deleteFileAndPageReference(int fid,int pid,int tid);
    void removePageRefers(int pid,int tid);

    List<File> getFileByStartName(int tid, String name);

    /**
     * 通过mid获取用户可用附件文件列表
     * @param mid
     * @param tid
     * @param uid
     * @return
     */
    List<File> getFileByEmailMidAndUid(String mid,int tid,String uid);
    /**
     * 获取用户团队内的所有文件
     * @param uid
     * @param tid
     * @return
     */
    List<File> getFileByTid(int[] tids,int offset,int rows);

    int getTeamFileCount(int[] tids);

    List<File> getFileByTidAndTitle(String title, int tid);
}
