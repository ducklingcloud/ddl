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
package net.duckling.ddl.service.param;

import java.util.List;
import java.util.Map;



/**系统参数业务层
 * @author lvly
 * @since 2012-07-20
 * */
public interface IParamService {
    /**根据type获取所有param*/
    List<Param> getParamByType(String type);
    /**增加系统参数
     * @param param 要增加的参数
     * */
    void addParam(Param param);

    /**更新系统参数
     * @param param 要更新的参数
     * */
    void updateParam(Param param);

    /**获得type下的所有键值对
     * @param type 参数类型
     * @param itemId 实体Id，或为uid，或为tid，或为null
     * @return map <key,value>
     * */
    Map<String,String> getMap(String type,String itemId);

    /**获得type下的所有键值对
     * @param type 参数类型
     * @param itemId 实体Id，或为uid，或为tid，或为null
     * @return map <key,value>
     * */
    List<Param> getList(String type,String itemId);

    /**获得type下的某个key下的值
     * @param type 参数类型
     * @param itemId 实体Id，或为uid，或为tid，或为null
     * @return String value
     * */
    String getValue(String type,String key,String itemId);

    /**通过type和key 定位一条param
     * @param type 参数类型
     * @param key 参数键
     * @param itemId 实体Id，或为uid，或为tid，或为null
     * @return 参数实体对象
     * */
    Param get(String type,String key,String itemId);

    /**
     * 通过type，value把所有取出来，排重
     * @param type 类型
     * @param value itemId
     * */
    List<Param> getByTypeAndValue(String type,String value);
}
