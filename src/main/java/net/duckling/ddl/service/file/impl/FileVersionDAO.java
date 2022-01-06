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
package net.duckling.ddl.service.file.impl;

import java.util.List;

import net.duckling.ddl.service.file.FileVersion;


public interface FileVersionDAO {
    int create(FileVersion fileVersion);
    int delete(int id);
    int delete(int rid, int tid, int version);

    /***删除文件版本信息,现改为更新标记为
     * @author lvly
     * @since 2012-07-20
     * @param rid FileId
     * @param tid TeamId
     * @return int 1
     * */
    int deleteAllFileVersion(int rid, int tid);
    int update(int id, FileVersion fileVersion);
    FileVersion getFileVersionById(int id);
    FileVersion getFileVersion(int rid, int tid, int version);
    FileVersion getLatestFileVersion(int rid, int tid);
    FileVersion getFirstFileVersion(int rid, int tid);
    List<FileVersion> getFileVersions(int rid,int tid);
    List<FileVersion> getFileVersions(int rid,int tid,int offset,int pageSize);
    List<FileVersion> getFileSizeByRids(List<Long> ids);
    List<FileVersion> getDFilesOfPage(int pageRid,int tid);
    List<FileVersion> getLatestFileVersions(int[] rids, int tid);
    /**
     * 恢复删除的版本信息
     * @param fid
     * @param tid
     * @return
     */
    int recoverFileVersion(int rid,int tid);
    FileVersion getFileVersionByDocId(int clbId, String clbVersion);
}
