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

package net.duckling.ddl.service.variable;

import net.duckling.ddl.common.VWBContext;

/**
 * 变量管理服务
 * @date May 6, 2010
 * @author xiejj@cnic.cn
 */
public interface VariableService {
    /**
     * 查询当前访问的一些变量服务
     * @param context 当前访问Context
     * @param varName 变量的名称
     * @return 返回变量的值
     * @throws NoSuchVariableException 如果这个变量不支持，抛出该异常
     */
    Object getValue(VWBContext context, String varName) throws NoSuchVariableException;
}
