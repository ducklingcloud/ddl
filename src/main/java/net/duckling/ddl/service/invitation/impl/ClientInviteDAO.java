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

import java.util.List;

import net.duckling.ddl.util.AbstractBaseDAO;

import org.springframework.stereotype.Repository;

@Repository
public class ClientInviteDAO extends AbstractBaseDAO {

	public void save(String invitor, String invitee) {
		String sql = "insert into vwb_client_invite(invitor, invitee, invitetime) values(?,?,now())";
		getJdbcTemplate().update(sql, invitor, invitee);
	}

	public List<String> getInvitors(String invitee) {
		String sql = "select invitor from vwb_client_invite where invitee=? and isnull(accepttime)";
		return getJdbcTemplate().queryForList(sql, new Object[] { invitee },
				String.class);
	}

	public void markAccept(String invitee) {
		String sql = "update vwb_client_invite set accepttime=now() where invitee=? and isnull(accepttime)";
		getJdbcTemplate().update(sql, invitee);
	}
}
