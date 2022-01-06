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
package net.duckling.ddl.service.resource.impl;

import java.util.List;

import net.duckling.ddl.service.resource.Resource;
import net.duckling.ddl.util.PaginationBean;
/**
 * resource目录关系查询dao
 * @author zhonghui
 *
 */
public interface ResourcePathDAO {

    /**
     * 查询rid的子元素
     * @param tid
     * @param rid
     * @param fileType
     * @param order
     * @param begin
     * @param size
     * @param keyWord
     * @return
     */
    PaginationBean<Resource> getChildren(int tid,int rid,String fileType,String order,int begin,int size, String keyWord);
    Resource getParent(int rid);
    /**
     * 获取rid所在在的路径，路径上包括自己。list按从上至下顺序排列
     * @param rid
     * @return
     */
    List<Resource> getPath(int rid);

    List<Resource> getFolderByStartName(int tid, int parentRid, String queryName, String itemType);
    List<Resource> getChildrenFolder(int tid, int rid);
    List<Resource> getDescendants(int tid, int rid);
    List<Resource> getChildren(int tid, int rid);
    List<Resource> getResourceByName(int tid, int parentRid, String itemType, String name);
    List<Resource> getResourceByName(int tid, int parentRid, String name);

    /**
     * 获取团队某目录下标题为title的所有resource，包括文件、文件夹和DDOC。
     * @param tid 团队id
     * @param parentRid 父目录rid
     * @param title 文件、文件夹或者DDOC的标题（DDOC不含.ddoc后缀）
     * @return 满足条件的资源列表
     */
    List<Resource> getResourceByTitle(int tid, int parentRid, String title);

    PaginationBean<Resource> searchResource(int tid, int parentRid, String keyWord, String order, int begin, int size);

    /**
     * 根据文件夹名，获取所有的路径，为了通过路径找到其rid
     * @param folderTitle
     * @return
     */
    List<Resource> getPathsByTitle(int tid, String folderTitle);
}
