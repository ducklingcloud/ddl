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
package net.duckling.ddl.service.mail.compile;
/**
 * 
 */


/**
 * 
 * 动态编译工具类会抛出的异常
 * @author lvly
 * @since 2012-11-7
 */
public class DyminicCompileException extends RuntimeException{
	/**
	 * 
	 */
	private static final long serialVersionUID = 1146432061518051L;

	public DyminicCompileException(String msg){
		super(msg);
	}
	public DyminicCompileException(String msg,Throwable exception){
		super(msg,exception);
	}
}
