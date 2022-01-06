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

package net.duckling.ddl.service.url;

/**
 * 用于构建URL
 * @date Mar 2, 2010
 * @author xiejj@cnic.cn
 */
public interface URLConstructor {
    /**
     * 构造URL
     * @param action 操作类别
     * @param resourceId 操作的资源的ID
     * @param params 参数列表
     * @param absolute 是否使用绝对地址。
     * @param isNewURL 使用新版本url或者旧版本
     */
    String makeURL(String action, String page, String params, boolean absolute);
    /**
     * 构造URL
     * @param action 操作类别
     * @param resourceId 操作的资源的ID
     * @param params 参数列表
     */
    String makeURL(String action, String page, String params);
}
