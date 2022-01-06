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

package net.duckling.ddl.service.export;


import java.util.List;
import java.util.Map;

import javax.servlet.http.HttpServletResponse;

import net.duckling.ddl.common.VWBContext;
import net.duckling.ddl.service.resource.Tag;


/**
 * @date 2012-06-13
 * @author yangxiaopeng@cnic.cn
 */
public interface ExportService {
    /**
     * 从资料页下载如果个资源的内容
     * @param context 请求的上下文
     * @param tname 团队名
     * @param rids[] 需要下载的资源ID
     * @param resp HttpServletResponse对象
     * @param format 下载保存的格式：zip或epub
     */
    void download(VWBContext context, String tname, int[] rids, HttpServletResponse resp, String format);
    /**
     * 管理员导出若干个标签的所有资源
     * @param context 请求的上下文
     * @param tname 团队名
     * @param tagMap 包含标签集和标签的HashMap
     * @param resp HttpServletResponse对象
     * @param format 下载保存的格式：zip或epub
     */
    void download(VWBContext context, String tname, Map<String, List<Tag>> tagMap, HttpServletResponse resp, String format);
}
