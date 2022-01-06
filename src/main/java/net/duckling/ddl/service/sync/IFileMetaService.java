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

import java.util.List;

import net.duckling.ddl.service.resource.Resource;

public interface IFileMetaService {

    boolean isConflict(int tid, long fid, long fver);

    /**
     * 文件夹返回其直接子文件列表,文件则返回自己
     * @param tid
     * @param r
     * @return
     */
    List<FileMeta> list(int tid, Resource r) ;

    FileMeta get(Resource r, Long fver);
    FileMeta get(Resource r);
    FileMeta get(int tid, Long fid, Long fver);
    FileMeta get(int tid, Long fid);
    List<FileMeta> getDescendants(int tid, int parentId);
}
