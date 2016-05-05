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

import java.util.List;
import java.util.Set;



/**
 * @date 2011-11-9
 * @author JohnX
 */
public interface ContactsService {
	
	 List<Contact> getUserContactsByUid(String uid);
	
	 List<Contact> getUserContactsByPinyin(String uid, String pinyin) ;

	 List<Contact> getUserContactsByName(String uid, String name) ;
	
	 Set<ContactExt> searchContactsByName(String uid, String name);
	 Set<ContactExt> searchContactsByPinyin(String uid, String pinyin);
	 Set<ContactExt> searchContactsByMail(String uid, String mail);
	 Set<ContactExt> searchContactsByPinyinAndMail(String uid, String mail);
	
	 Set<ContactExt> searchContactsInCurrentTeam(String name, int tid);
	
	 int add2PersonContacts(int userExtId, String uid);
	
	 Contact getUserContactById(int id);
	
	//添加一条记录
	 int insertOneContact(Contact contact);
	
	//更新一条记录
	 void updateContactByName(Contact contact);
	 void updateContactById(Contact contact);
	
	//删除一条记录
	 void deleteContactById(int id);
	
	//删除某用户的个人通讯录
	 void deleteUserContacts(String uid);
}
