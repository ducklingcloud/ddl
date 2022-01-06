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

package net.duckling.ddl.service.contact.impl;

import java.util.HashSet;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Set;

import net.duckling.ddl.service.contact.Contact;
import net.duckling.ddl.service.contact.ContactExt;
import net.duckling.ddl.service.contact.ContactUtil;
import net.duckling.ddl.service.contact.ContactsService;
import net.duckling.ddl.service.team.Team;
import net.duckling.ddl.service.team.TeamMemberService;
import net.duckling.ddl.service.team.TeamService;
import net.duckling.ddl.service.user.AoneUserService;
import net.duckling.ddl.service.user.UserExt;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

/**
 * @date 2011-11-18
 * @author JohnX
 */
@Service
public class ContactsServiceImpl implements ContactsService {
    @Autowired
    private AoneUserService aoneUserService;
    @Autowired
    private ContactsDAO contactsDao;
    @Autowired
    private TeamMemberService teamMemberService;
    @Autowired
    private TeamService teamService;

    private void addTeamContactsToList(Set<ContactExt> contactExts,
                                       List<Team> teams, List<UserExt> users, String uid) {
        Set<UserExt> userTeamContacts = new HashSet<UserExt>();
        for (Team team : teams) {
            // filter the demo team
            if (team.getId() == 1) {
                continue;
            }
            List<UserExt> tUsers = teamMemberService.getTeamContacts(team
                                                                     .getId());
            for (UserExt ue : tUsers) {
                userTeamContacts.add(ue);
            }
        }
        for (UserExt ue : users) {
            if (userTeamContacts.contains(ue)) {
                contactExts.add(ContactUtil.convertToContactExt(ue, uid));
            }
        }
    }

    // 返回-1表示数据库中已有此条记录
    public int add2PersonContacts(int userExtId, String uid) {
        UserExt userExt = aoneUserService.getUserExtByAutoID(userExtId);
        Contact contact = ContactUtil.convertToContact(userExt, uid);
        return contactsDao.addContactItem(contact);
    }

    // 删除一条记录
    public void deleteContactById(int id) {
        contactsDao.deleteByID(id);
    }

    // 删除某用户的个人通讯录
    public void deleteUserContacts(String uid) {
        contactsDao.deleteByUid(uid);
    }

    public Contact getUserContactById(int id) {
        List<Contact> list = contactsDao.getUserContactById(id);
        if (list != null) {
            return list.get(0);
        }
        return null;
    }

    public List<Contact> getUserContactsByName(String uid, String name) {
        return this.contactsDao.getUserContactsByName(uid, name);
    }

    public List<Contact> getUserContactsByPinyin(String uid, String pinyin) {
        return this.contactsDao.getUserContactsByPinyin(uid, pinyin);
    }

    public List<Contact> getUserContactsByUid(String uid) {
        return this.contactsDao.getUserContactsByOwner(uid);
    }

    // 添加一条记录，返回-1表示数据库中已有此条记录
    public int insertOneContact(Contact contact) {
        return contactsDao.addContactItem(contact);
    }

    public Set<ContactExt> searchContactsByMail(String uid, String mail) {
        List<Contact> contacts = contactsDao.getUserContactsByMail(uid, mail);
        Set<ContactExt> contactExts = new LinkedHashSet<ContactExt>();
        for (Contact _contact : contacts) {
            contactExts.add(ContactUtil.convertToContactExt(_contact));
        }
        List<Team> teams = teamService.getAllUserTeams(uid);
        List<UserExt> users = aoneUserService.searchUserByMail(mail);
        addTeamContactsToList(contactExts, teams, users, uid);
        return contactExts;
    }

    public Set<ContactExt> searchContactsByName(String uid, String name) {
        List<UserExt> contacts = aoneUserService.searchUserByUserTeamAndName(
            uid, name);
        Set<ContactExt> contactExts = new HashSet<ContactExt>();
        for (UserExt contact : contacts) {
            contactExts.add(ContactUtil.convertToContactExt(contact, uid));
        }
        return contactExts;
    }

    public Set<ContactExt> searchContactsByPinyin(String uid, String searchParam) {
        List<UserExt> contacts = aoneUserService.searchUserByUserPinyinAndName(
            uid, searchParam);
        Set<ContactExt> contactExts = new HashSet<ContactExt>();
        for (UserExt contact : contacts) {
            contactExts.add(ContactUtil.convertToContactExt(contact, uid));
        }
        return contactExts;
    }

    public Set<ContactExt> searchContactsByPinyinAndMail(String uid,
                                                         String searchParam) {
        Set<ContactExt> contactExts = new HashSet<ContactExt>();
        Set<ContactExt> pinyinCE = searchContactsByPinyin(uid, searchParam);
        Set<ContactExt> mailCE = searchContactsByMail(uid, searchParam);
        for (ContactExt ce : pinyinCE) {
            contactExts.add(ce);
        }
        for (ContactExt ce : mailCE) {
            contactExts.add(ce);
        }
        return contactExts;
    }

    public Set<ContactExt> searchContactsInCurrentTeam(String name, int tid) {
        Set<ContactExt> contacts = new HashSet<ContactExt>();
        List<UserExt> users = teamMemberService.getTeamContacts(tid);
        for (UserExt user : users) {
            if (user.getName().contains(name)
                || user.getPinyin().contains(name)
                || user.getUid().substring(0, user.getUid().indexOf('@'))
                .contains(name)) {
                ContactExt contactExt = ContactUtil.convertToContactExt(user);
                contacts.add(contactExt);
            }
        }
        return contacts;
    }

    public void updateContactById(Contact contact) {
        contactsDao.updateContactById(contact);
    }

    // 更新一条记录
    public void updateContactByName(Contact contact) {
        contactsDao.updateContactByName(contact);
    }
}
