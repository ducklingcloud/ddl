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
package net.duckling.ddl.service.user.impl;

import java.util.Collection;
import java.util.List;
import java.util.Set;

import net.duckling.ddl.service.user.SimpleUser;
import net.duckling.ddl.service.user.UserExt;

public interface UserExtDAO {

	public abstract List<UserExt> searchUserByName(String name);

	public abstract List<UserExt> searchUserByUserTeamAndName(String uid,
			String name);

	public abstract List<UserExt> searchUserByUserPinyinAndName(String uid,
			String name);

	public abstract List<UserExt> searchUserByPinyin(String pinyin);

	public abstract List<UserExt> searchUserByMail(String mail);
	
	public abstract List<UserExt> searchByUnallocatedSpace();

	public abstract int createUserExt(final UserExt instance);

	public abstract int createUserExt(final String uid, final String name);

	public abstract void updateUserExt(UserExt u);

	public abstract boolean isExistRegister(String email);

	public abstract UserExt getUserExtInfo(String uid);

	public abstract UserExt getUserExtByAutoID(int uxid);

	public abstract List<UserExt> getUserExtList(List<String> userList);

	public abstract List<SimpleUser> getAllSimpleUser();

	public abstract SimpleUser getSimpleUser(String uid);

	public abstract int getTotalUserNumber();

	public abstract List<UserExt> getUserExtByIds(Collection<Integer> ids);

	public abstract List<UserExt> getUserExtByUids(Set<String> userId);

}