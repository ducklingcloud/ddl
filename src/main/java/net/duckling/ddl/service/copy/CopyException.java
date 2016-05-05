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
 * 复制动作特有的异常类
 * @author lvly
 * @since 2012-11-13
 */
public class CopyException extends Exception{
	private static final long serialVersionUID = 4412511765596907179L;
	private static String getMsg(int rid){
		return "this version in ddl,can't copy bundle,the rid="+rid+",the type of resource is neither page or file";
	}
	public CopyException(int rid){
		super(getMsg(rid));
	}
	public CopyException(int rid,Exception e){
		super(getMsg(rid),e);
	}
}
