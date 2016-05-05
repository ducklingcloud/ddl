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

package net.duckling.ddl.service.user;

import java.io.Serializable;


/**
 * @date 2011-3-8
 * @author Clive Lee
 */
public class SimpleUser  implements Serializable{

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	private int id;
	private String uid;
	private String name;
	private String email;
	private String pinyin;
	private String picture;
	
	/**
	 * @return the uid
	 */
	public String getUid() {
	    return uid;
	}

	/**
	 * @param uid the uid to set
	 */
	public void setUid(String uid) {
	    this.uid = uid;
	}

	/**
	 * @return the name
	 */
	public String getName() {
	    return name;
	}
             
	/**
	 * @param name the name to set
	 */
	public void setName(String name) {
	    this.name = name;
	}

	/**
	 * @return the pinyin
	 */
	public String getPinyin() {
		return pinyin;
	}

	/**
	 * @param pinyin the pinyin to set
	 */
	public void setPinyin(String pinyin) {
		this.pinyin = pinyin;
	}

	public String getPicture() {
		return picture;
	}

	public void setPicture(String picture) {
		this.picture = picture;
	}
	
	
	public String getEmail() {
		return email;
	}

	public void setEmail(String email) {
		this.email = email;
	}

	public int getId() {
		return id;
	}

	public void setId(int id) {
		this.id = id;
	}

	public static SimpleUser transfer(UserExt user){
		SimpleUser simple = new SimpleUser();
		simple.setEmail(user.getEmail());
		simple.setId(user.getId());
		simple.setName(user.getName());
		simple.setUid(user.getUid());
		simple.setPinyin(user.getPinyin());
		return simple;
	}
}
