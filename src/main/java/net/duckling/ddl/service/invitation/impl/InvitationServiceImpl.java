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

package net.duckling.ddl.service.invitation.impl;

import java.util.Date;
import java.util.List;

import net.duckling.ddl.service.invitation.Invitation;
import net.duckling.ddl.service.invitation.InvitationService;
import net.duckling.ddl.util.AoneTimeUtils;
import net.duckling.ddl.util.StatusUtil;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;


/**
 * @date 2012-2-17
 * @author clive
 */
@Service
public class InvitationServiceImpl implements InvitationService {
	@Autowired
	private InvitationDAO invitationDao;
	
	@Override
    public int getInvitationCount(String user) {
        return invitationDao.getInvitationCount(user);
    }

	public Invitation getInvitationInstance(String encode, String id) {
		return invitationDao.getInvitationByURL(id, encode);
	}
	public List<Invitation> getInvitationListByTeam(int tid) {
		return invitationDao.getInvitationByTeam(tid);
	}
	
	public List<Invitation> getInvitationListByUser(String user) {
		return invitationDao.getInvitationByUser(user);
	}

	public int saveInvitation(Invitation instance) {
		Invitation oldInvite = invitationDao.getExistValidInvitation(
				instance.getInvitee(), instance.getTeamId());
		if (oldInvite != null) {
			oldInvite.setInviteTime(AoneTimeUtils.formatToDateTime(new Date()));
			oldInvite.setMessage(instance.getMessage());
			instance.setEncode(oldInvite.getEncode()); // 使用老的密码
			invitationDao.updateInvitationStatus(oldInvite);
			return oldInvite.getId();
		}
		return invitationDao.insertInvitation(instance);
	}
	
	public void saveInvites(List<Invitation> array) {
		if (array != null && array.size() != 0) {
			for (Invitation i : array) {
				int id = invitationDao.insertInvitation(i);
				i.setId(id);
			}
		}
	}
	
	public boolean updateInviteStatus(String encode, String id, String status) {
		Invitation instance = invitationDao.getInvitationByURL(id, encode);
		if (invitationDao.checkInvitation(instance)) {
			instance.setStatus(status);
			instance.setAcceptTime(AoneTimeUtils.formatToDateTime(new Date()));
			invitationDao.updateInvitationStatus(instance);
			return true;
		} else {
			instance.setStatus(StatusUtil.INVALID);
			invitationDao.updateInvitationStatus(instance);
			return false;
		}
	}

    public boolean updateWaiteToAccept(int tid,String uid){
		return invitationDao.updateWaiteToAccept(tid,uid,StatusUtil.WAITING);
	}
    
    @Override
    public Invitation getExistValidInvitation(String user,int tid){
    	return invitationDao.getExistValidInvitation(user, tid);
    }

}
