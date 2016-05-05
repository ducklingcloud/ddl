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
package net.duckling.ddl.service.task;

/**包含CN%UID格式的姓名
 * @author lvly
 * @since 2012-07-30
 * */
public class UserIdFormatter {
	
	private String userId;
	
    public String getUserId() {
		return userId;
	}
	public void setUserId(String userId) {
		this.userId = userId;
	}
	/**获得用户汉字姓名*/
	public String getUserNameStr(){
			return TakerWrapper.getUserName(userId);
		}
	/**获得用户的ID*/
    public String getUserIDStr(){
			return TakerWrapper.getUserID(userId);
		}
}
