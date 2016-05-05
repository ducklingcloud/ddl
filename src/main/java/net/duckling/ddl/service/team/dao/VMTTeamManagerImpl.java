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
package net.duckling.ddl.service.team.dao;

import java.util.List;

import net.duckling.ddl.service.team.Team;
import net.duckling.ddl.service.team.impl.VMTTeamManager;
import net.duckling.vmt.api.domain.VmtGroup;
import net.duckling.vmt.api.domain.VmtUser;
import net.duckling.vmt.api.impl.GroupService;
import net.duckling.vmt.api.impl.UserService;

import org.apache.commons.lang.StringUtils;
import org.apache.log4j.Logger;

import cn.vlabs.rest.ServiceException;


public class VMTTeamManagerImpl implements VMTTeamManager { 
	private static final Logger LOG = Logger.getLogger(VMTTeamManagerImpl.class);
	private GroupService groupService;
	private UserService vmtUserService;
	
	public void setGroupService(GroupService groupService) {
		this.groupService = groupService;
	}

	public void setVmtUserService(UserService vmtUserService) {
		this.vmtUserService = vmtUserService;
	}

	@Override
	public String addTeam(Team team) {
		VmtGroup vmtGroup = new VmtGroup();
		try {
			String[] us = vmtUserService.getUmtIdByCstnetId(new String[]{team.getCreator()});
			vmtGroup.setCreator(us[0]);
		} catch (ServiceException e1) {
			LOG.error("获取用户UMTID错误"+team, e1);
			return null;
		}
		vmtGroup.setName(team.getDisplayName());
		vmtGroup.setSymbol(team.getName());
		vmtGroup.setFrom(VmtGroup.FROM_DDL);
		vmtGroup.setDescription(team.getDescription());
		try {
			String dn = groupService.create(vmtGroup);
			team.setVmtdn(dn);
			return dn;
		} catch (ServiceException e) {
			LOG.error("添加team错误"+team, e);
			return null;
		}
	}

	@Override
	public boolean updateTeam(Team team) {
		try {
			groupService.rename(team.getVmtdn(), team.getDisplayName());
			VmtGroup group = new VmtGroup();
			group.setDescription(team.getDescription());
			group.setSymbol(team.getName());
			groupService.update(group);
			return true;
		} catch (ServiceException e) {
			LOG.error("", e);
		}
		return false;
	}

	@Override
	public boolean addUserToTeam(String vmtdn, String uid) {
		try {
			boolean [] b = vmtUserService.addUnkownUserToDN(vmtdn, new String[]{uid}, true);
			return b[0];
		} catch (ServiceException e) {
			LOG.error("", e);
			return false;
		}
	}

	@Override
	public boolean removeUserToTeam(String vmtdn, String uid) {
		try {
			if(!validateVmtDn(vmtdn, uid)){
				return false;
			}
			List<VmtUser> users = vmtUserService.searchUserByCstnetId(vmtdn, new String[]{uid});
			if(users!=null&&!users.isEmpty()){
				VmtUser user = users.get(0);
				vmtUserService.removeUser(new String[]{user.getDn()});
				return true;
			}
		} catch (ServiceException e) {
			LOG.error("", e);
		}
		return false;
	}

	@Override
	public boolean addAdminToTeam(String vmtdn, String uid) {
		try {
			if(!validateVmtDn(vmtdn, uid)){
				return false;
			}
			groupService.addAdminByCstnetId(vmtdn, uid);
			return true;
		} catch (ServiceException e) {
			if(e.getCode()==7){
				LOG.info("用户"+uid+"vmt("+vmtdn+")中不存在");
				addUserToTeam(vmtdn, uid);
				try {
					groupService.addAdminByCstnetId(vmtdn, uid);
				} catch (ServiceException e1) {
					LOG.error("向"+vmtdn+"添加用户"+uid+"错误", e);
				}
				return true;
			}else{
				LOG.error("向"+vmtdn+"添加用户"+uid+"错误code = "+e.getCode(), e);
			}
		}
		return false;
	}

	@Override
	public boolean removeAdminToTeam(String vmtdn, String uid) {
		try {
			if(!validateVmtDn(vmtdn, uid)){
				return false;
			}
			groupService.removeAdminByCstnetId(vmtdn, uid);
			return true;
		} catch (ServiceException e) {
			LOG.error("向"+vmtdn+"移除用户"+uid+"错误", e);
			throw new RuntimeException(e);
		}
	}

	@Override
	public void deleteTeam(String vmtdn) {
		try {
			if(validateVmtDn(vmtdn, null)){
				groupService.delete(vmtdn);
			}
		} catch (ServiceException e) {
			LOG.error("删除"+vmtdn+"错误", e);
		}
	}

	@Override
	public String getTeamVmtdn(String teamCode) {
		try {
			VmtGroup group = groupService.getGroupBySymbol(teamCode);
			if(group!=null){
				return group.getDn();
			}
		} catch (ServiceException e) {
			LOG.error("", e);
			throw new RuntimeException(e);
		}
		return null;
	}

	@Override
	public VmtUser getUidByUmtId(String umtId) {
		try {
			return vmtUserService.getUserByUmtId(umtId);
		} catch (ServiceException e) {
			LOG.error("", e);
			throw new RuntimeException(e);
		}
	}

	@Override
	public String[] getUidByUmtId(String[] umtId) {
		try {
			List<VmtUser> users = vmtUserService.getUsersByUmtIds(umtId);
			String [] result = new String[users.size()];
			for(int i=0;i<users.size();i++){
				result[i]=users.get(i).getCstnetId();
			}
			return result;
		} catch (ServiceException e) {
			LOG.error("query uid by umt id from vmt error!", e);
			throw new RuntimeException(e);
		}
	}

	@Override
	public boolean teamCodeExistInVmt(String code) {
		try {
			return groupService.hasSymbolUsed(code);
		} catch (ServiceException e) {
			LOG.error("query teamCode in vmt error",e);
			return true;
		}
	}
	private boolean validateVmtDn(String vmtdn,String uid){
		if(StringUtils.isEmpty(vmtdn)){
			StackTraceElement[] s = Thread.currentThread().getStackTrace();
			StringBuilder sb = new StringBuilder();
			if(s!=null){
				for(StackTraceElement st : s){
					sb.append( st.getClassName() );
		            sb.append(".");
		            sb.append(st.getMethodName()+"(), line "+st.getLineNumber());
		            sb.append("/r/n");
				}
			}
			LOG.error("UID="+uid+";vmtdn=null/r/n"+sb.toString());
			return false;
		}
		return true;
	}

}
