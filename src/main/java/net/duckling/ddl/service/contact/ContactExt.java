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

package net.duckling.ddl.service.contact;



/**
 * @date 2011-11-10
 * @author JohnX
 */
public class ContactExt extends Contact implements Comparable<ContactExt> {
	public static final String  USER = "个人通讯录";
	public static final String  TEAM = "团队通讯录";
	
	private int tag;
	private String source;
	
	public String getSource() {
		return source;
	}
	public void setSource(String source) {
		this.source = source;
	}
	/**
	 * @return the tag
	 */
	public int getTag() {
		return tag;
	}
	/**
	 * @param tag the tag to set
	 */
	public void setTag(int tag) {
		this.tag = tag;
	}
	/* (non-Javadoc)
	 * @see java.lang.Comparable#compareTo(java.lang.Object)
	 */
	public int compareTo(ContactExt o) {
		if(null == this.getPinyin() || null == o.getPinyin()) {
			return this.getMainEmail().compareTo(o.getMainEmail());
		}
		int result = this.getPinyin().compareTo(o.getPinyin());
		if( result == 0) {
			return this.getMainEmail().compareTo(o.getMainEmail());
		}
		return result;
	}
	/* (non-Javadoc)
	 * @see java.lang.Object#hashCode()
	 */
	@Override
	public int hashCode() {
		return (this.getUid()+this.getMainEmail()).hashCode();
	}
	/* (non-Javadoc)
	 * @see java.lang.Object#equals(java.lang.Object)
	 */
	@Override
	public boolean equals(Object obj) {
		if(obj instanceof ContactExt) {
			ContactExt ce = (ContactExt)obj;
			return (this.getUid()+this.getMainEmail()).equals(ce.getUid()+ce.getMainEmail());
		}
		return false;
	}
	
}
