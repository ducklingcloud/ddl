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

package net.duckling.ddl.web.controller;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.Set;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import net.duckling.ddl.common.VWBContext;
import net.duckling.ddl.service.contact.Contact;
import net.duckling.ddl.service.contact.ContactExt;
import net.duckling.ddl.service.contact.ContactUtil;
import net.duckling.ddl.service.contact.ContactsService;
import net.duckling.ddl.service.team.Team;
import net.duckling.ddl.service.team.TeamMemberService;
import net.duckling.ddl.service.team.TeamService;
import net.duckling.ddl.service.url.UrlPatterns;
import net.duckling.ddl.service.user.AoneUserService;
import net.duckling.ddl.service.user.UserExt;
import net.duckling.ddl.util.JsonUtil;
import net.duckling.ddl.util.PinyinUtil;
import net.duckling.ddl.web.bean.ContactsExportUtil;
import net.duckling.ddl.web.interceptor.access.OnDeny;
import net.duckling.ddl.web.interceptor.access.RequirePermission;

import org.apache.log4j.Logger;
import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;

/**
 * @date 2011-11-9
 * @author JohnX
 */
@Controller
@RequestMapping("/dashboard/contacts")
@RequirePermission(authenticated = true)
public class ContactsController {
	private static final Logger LOGGER = Logger.getLogger(ContactsController.class);
	private static final int SESSION_TIME_OUT = 450;
	@Autowired
	private ContactsService contactsService;
	@Autowired
	private TeamMemberService teamMemberService;
	@Autowired
	private TeamService teamService;
	@Autowired
	private AoneUserService aoneUserService;

	private Contact getContactFromRequest(HttpServletRequest request,
			VWBContext context) {
		Contact contact = new Contact();
		contact.setAddress(request.getParameter("address"));
		contact.setDepartment(request.getParameter("department"));
		String id = request.getParameter("id");
		if (null != id) {
			contact.setId(Integer.parseInt(request.getParameter("id")));
		}
		contact.setMainEmail(request.getParameter("mainEmail"));
		contact.setMobile(request.getParameter("mobile"));
		contact.setMsn(request.getParameter("msn"));
		contact.setName(request.getParameter("name").trim());
		contact.setOptionEmail(request.getParameter("optionEmail"));
		contact.setOrgnization(request.getParameter("orgnization"));
		contact.setQq(request.getParameter("qq"));
		contact.setSex(request.getParameter("sex"));
		contact.setTelephone(request.getParameter("telephone"));
		contact.setUid(context.getCurrentUID());
		contact.setWeibo(request.getParameter("weibo"));
		return contact;
	}

	private VWBContext getVWBContext(HttpServletRequest request) {
		return VWBContext.createContext(request, UrlPatterns.T_TEAM_HOME);
	}

	private boolean isChineseCharacter(String chars) {
		if (null == chars || chars.length() == 0) {
			return false;
		}
		char ch = chars.charAt(0);
		return (ch >= PinyinUtil.CH_START && ch <= PinyinUtil.CH_END);
	}

	private boolean isConflict(List<ContactExt> list, ContactExt ce) {
		if (list.size() <= 0) {
			return false;
		}
		Iterator<ContactExt> itr = list.listIterator();
		while (itr.hasNext()) {
			ContactExt temp = itr.next();
			if (temp.getMainEmail().equals(ce.getMainEmail())
					&& temp.getName().equals(ce.getName())) {
				return true;
			}
		}
		return false;
	}

	private boolean isExistMember(String[] array, int id) {
		if (array.length <= 0) {
			return false;
		}
		for (String element : array) {
			if (id == Integer.parseInt(element)) {
				return true;
			}
		}
		return false;
	}

	private void sendToClient(Contact contact, HttpServletResponse response) {
		JSONObject object = JsonUtil.getJSONObject(contact);
		object.put("type", "person");
		JsonUtil.writeJSONObject(response, object);
	}

	private void sendToClient(UserExt user, HttpServletResponse response) {
		JSONObject object = JsonUtil.getJSONObject(user);
		object.put("type", "team");
		JsonUtil.writeJSONObject(response, object);
	}

	@RequestMapping(params = "func=addToPersonContacts")
	public void add2PersonContacts(HttpServletRequest request,
			HttpServletResponse response) {
		VWBContext context = getVWBContext(request);
		int id = Integer.parseInt(request.getParameter("itemId"));
		String uid = context.getCurrentUID();
		int addResult = contactsService.add2PersonContacts(id, uid);
		if (addResult < 0) {
			JSONObject object = new JSONObject();
			object.put("result", "failed");
			object.put("detail", "个人通讯录中已有此人");
			JsonUtil.writeJSONObject(response, object);
			return;
		}
		JSONObject object = new JSONObject();
		object.put("result", "success");
		JsonUtil.writeJSONObject(response, object);
	}

	@RequestMapping(params = "func=addItem")
	public void addItem(HttpServletRequest request, HttpServletResponse response) {
		VWBContext context = getVWBContext(request);
		Contact contact = getContactFromRequest(request, context);
		contact.setPinyin(PinyinUtil.getPinyin(contact.getName()));
		int insertResult = contactsService.insertOneContact(contact);
		if (insertResult < 0) {
			JSONObject object = new JSONObject();
			object.put("result", "failed");
			object.put("detail", "个人通讯录中已有此人");
			JsonUtil.writeJSONObject(response, object);
			return;
		}
		JSONObject object = new JSONObject();
		object.put("result", "success");
		JsonUtil.writeJSONObject(response, object);
	}

	@RequestMapping(params = "func=deleteItem")
	public void deleteItem(HttpServletRequest request) {
		contactsService.deleteContactById(Integer.parseInt(request
				.getParameter("itemId")));
		JSONObject object = new JSONObject();
		object.put("status", "success");
		JsonUtil.getJSONObject(object);
	}

	@RequestMapping(params = "func=exportPersonalContacts")
	public void exportPresonalContacts(HttpServletRequest request,
			HttpServletResponse response) {
		String contactList = request.getParameter("contactList");
		String str = contactList.replace("\"", "");
		String personalIdStr = str.substring(str.indexOf("pids") + 6,
				str.indexOf(']'));
		String teamIdStr = str.substring(str.indexOf("tids") + 6,
				str.lastIndexOf(']'));
		VWBContext context = getVWBContext(request);
		String user = context.getCurrentUID();
		List<ContactExt> contacts = new LinkedList<ContactExt>();

		// 获取个人通讯录信息
		if (personalIdStr != null && !"".equals(personalIdStr)) {
			List<Contact> personalContacts = new ArrayList<Contact>();
			String[] personalIds = personalIdStr.split(",");
			for (String id : personalIds) {
				Contact personalContact = contactsService
						.getUserContactById(Integer.parseInt(id));
				personalContacts.add(personalContact);
			}
			Iterator<Contact> personItr = personalContacts.iterator();
			while (personItr.hasNext()) {
				ContactExt ce = ContactUtil.convertToContactExt(personItr
						.next());
				contacts.add(ce);
			}
		}

		// 获取团队通讯录信息
		if (teamIdStr != null && !"".equals(teamIdStr)) {
			String[] teamIds = teamIdStr.split(",");
			Set<UserExt> teamMembers = new HashSet<UserExt>();
			List<Team> teams = teamService.getAllUserTeams(user);
			Iterator<Team> teamItr = teams.iterator();
			while (teamItr.hasNext()) {
				Team team = teamItr.next();
				List<UserExt> teamMember = teamMemberService
						.getTeamContacts(team.getId());
				for (UserExt ue : teamMember) {
					if (isExistMember(teamIds, ue.getId())) {
						teamMembers.add(ue);
					}
				}
			}

			Iterator<UserExt> teamMemItr = teamMembers.iterator();
			while (teamMemItr.hasNext()) {
				ContactExt ce = ContactUtil.convertToContactExt(teamMemItr
						.next());
				if (!isConflict(contacts, ce)) {
					contacts.add(ce);
				}
			}
		}

		String fileStr = ContactsExportUtil.convert2CSVString(contacts);
		String fileName = "个人通讯录.csv";
		ContactsExportUtil.download(request, response, fileStr, fileName);
	}

	@RequestMapping(params = "func=getItemData")
	public void getItemData(HttpServletRequest request,
			HttpServletResponse response) {
		// two params, id and tag | "teamName"
		String type = request.getParameter("itemType");
		String id = request.getParameter("itemId");
		if (type.equals("teamItem")) {
			UserExt user = aoneUserService.getUserExtByAutoID(Integer
					.parseInt(id));
			// convert to JSONObject, and send to the client
			sendToClient(user, response);
		} else if (type.equals("personItem")) {
			Contact contact = contactsService.getUserContactById(Integer
					.parseInt(id));
			sendToClient(contact, response);
		} else {
			Contact contact = contactsService.getUserContactById(Integer
					.parseInt(id));
			int tag = Integer.parseInt(type);
			switch (tag) {
			case 0:
			case 1:
			case 2:
			case 3:
				sendToClient(contact, response);// 冲突的情况，目前先按照个人通讯录来显示
				break;
			default:
				LOGGER.error("tag is not valid");
			}
		}
	}

	@RequestMapping(params = "func=getUser")
	public void getUserByName(HttpServletRequest request,
			HttpServletResponse response) {
		String name = request.getParameter("name");
		if (null == name || "".equals(name.trim())) {
			return;
		}
		VWBContext context = getVWBContext(request);
		String user = context.getCurrentUID();
		List<Contact> contacts = contactsService.getUserContactsByName(user,
				name);
		JSONArray array = new JSONArray();
		for (Contact _contact : contacts) {
			JSONObject object = JsonUtil.getJSONObject(_contact);
			array.add(object);
		}
		JsonUtil.writeJSONObject(response, array);
	}

	@OnDeny("*")
	public boolean onDeny(String methodName, HttpServletRequest request,
			HttpServletResponse response) throws IOException {
		response.sendError(SESSION_TIME_OUT);
		return true;
	}

	@RequestMapping(params = "func=saveItem")
	public void saveItemChange(HttpServletRequest request,
			HttpServletResponse response) {
		VWBContext context = getVWBContext(request);
		Contact contact = getContactFromRequest(request, context);
		contact.setPinyin(PinyinUtil.getPinyin(contact.getName()));
		contactsService.updateContactById(contact);
		JSONObject object = new JSONObject();
		object.put("result", "success");
		JsonUtil.writeJSONObject(response, object);
	}

	@RequestMapping(params = "func=searchUser")
	public void searchUser(HttpServletRequest request,
			HttpServletResponse response) {
		String searchParam = request.getParameter("searchParam");
		JSONArray array = new JSONArray();
		if (null == searchParam || "".equals(searchParam.trim())) {
			JsonUtil.writeJSONObject(response, array);
			return;
		}
		VWBContext context = getVWBContext(request);
		String user = context.getCurrentUID();
		Set<ContactExt> contacts = null;
		// 判断是否是汉字，是的话，就按名字搜
		if (isChineseCharacter(searchParam)) {
			contacts = contactsService.searchContactsByName(user, searchParam);
		} else {
			contacts = contactsService.searchContactsByMail(user,
					searchParam);
		}
		if (contacts != null) {
			for (ContactExt _contact : contacts) {
				JSONObject object = new JSONObject();
				// 目前只返回名字和邮箱字段，如果需要的话，在此处添加相应字段即可
				object.put("name", _contact.getName());
				object.put("id", _contact.getMainEmail());
				array.add(object);
			}
		}
		JsonUtil.writeJSONObject(response, array);
	}

	@RequestMapping(params = "func=searchUserInCurTeam")
	public void searchUserInCurrentTeam(HttpServletRequest request,
			HttpServletResponse response) {
		String searchParam = request.getParameter("searchParam");
		JSONArray array = new JSONArray();
		if (null == searchParam || "".equals(searchParam.trim())) {
			JsonUtil.writeJSONObject(response, array);
			return;
		}
		VWBContext context = getVWBContext(request);
		int tid = VWBContext.getCurrentTid();
		Set<ContactExt> contacts = null;
		contacts = contactsService
				.searchContactsInCurrentTeam(searchParam, tid);
		if (contacts != null) {
			for (ContactExt _contact : contacts) {
				JSONObject object = new JSONObject();
				// 目前只返回名字和邮箱字段，如果需要的话，在此处添加相应字段即可
				object.put("name", _contact.getName());
				object.put("id", _contact.getMainEmail());
				array.add(object);
			}
		}
		JsonUtil.writeJSONObject(response, array);
	}
}
