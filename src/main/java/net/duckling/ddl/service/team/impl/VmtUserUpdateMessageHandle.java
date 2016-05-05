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
package net.duckling.ddl.service.team.impl;

import java.util.HashSet;
import java.util.Set;

import net.duckling.ddl.service.authority.AuthorityService;
import net.duckling.ddl.service.authority.UserTeamAclBean;
import net.duckling.ddl.service.team.Team;

public class VmtUserUpdateMessageHandle {
	private Set<String> addAdmin= new HashSet<String>();
	private Set<String> removeAdmin= new HashSet<String>();
	private Set<String> addUser = new HashSet<String>();
	private AuthorityService authorityService;
	public void setAuthorityService(AuthorityService authorityService){
		this.authorityService = authorityService;
	}
	private VmtUserManager vmtUserManager;
	public void setVmtUserManager(VmtUserManager vmtUserManager) {
		this.vmtUserManager = vmtUserManager;
	}

	public void addUpdateUser(int tid, String uid, String auth) {
		UserTeamAclBean b = authorityService.getUserTeamAcl(tid, uid);
		if (b == null) {
			if (Team.AUTH_ADMIN.equals(auth)) {
				addAdmin.add(uid);
			} else {
				addUser.add(uid);
			}
			return;
		}
		
		if (auth == null) {
			removeAdmin.add(uid);
			return;
		}
		//权限是否有变动
		if(!auth.equals(b.getAuth())){
			if(Team.AUTH_ADMIN.equals(auth)){
				addAdmin.add(uid);
			}else{
				removeAdmin.add(uid);
			}
		}
	}

	public void dealVmtUserMessage(int tid) {
		if (isNotEmpty(addUser)) {
			for (String s : addUser) {
				vmtUserManager.addUserToVmt(tid, s);
			}
		}
		if (isNotEmpty(addAdmin)) {
			for (String s : addAdmin) {
				vmtUserManager.addAdminToVmt(tid, s, false);
			}
		}
		if (isNotEmpty(removeAdmin)) {
			for (String s : removeAdmin) {
				vmtUserManager.removeAdminToVmt(tid, s, false);
			}
		}
	}
	private boolean isNotEmpty(Set<String> ss){
		return ss!=null&&!ss.isEmpty();
	}
}