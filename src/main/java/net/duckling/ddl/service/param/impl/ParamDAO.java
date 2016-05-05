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
package net.duckling.ddl.service.param.impl;

import java.util.List;

import net.duckling.ddl.service.param.Param;


/**系统参数的数据层
 * @author lvly
 * @since 2012-07-20
 * */
public interface ParamDAO {
	/**增加系统参数
	 * @param param 需要新增的参数
	 * */
	void create(Param param);
	
	/**更新系统参数
	 * @param param 需要更新的参数
	 * */
	void update(Param param);
	
	
	/**获取所有type下的键值对
	 * @param type 参数类型
	 * @param itemId 实体Id，或为uid，或为tid，或为null
	 * @return List<param>
	 **/
	 List<Param> getList(String type,String itemId);
	
	/**通过id获得一个param
	 * @param id 主键
	 * 
	 * @return  param实体
	 * */
	Param get(int id);
	
	/**通过type，和key 定位获得一个param
	 * @param type 参数类型
	 * @parm key 参数键
	 * @param itemId 实体Id，或为uid，或为tid，或为null
	 * @return  param实体
	 * */
	Param get(String type, String key,String itemId);

	/**
	 * 通过type，value把所有取出来，排重
	 * @param type 类型
	 * @param value itemId
	 * */
	List<Param> getByTypeAndValue(String type, String value);

	/**根据type获取所有param*/
	List<Param> getParamByType(String type);
}
