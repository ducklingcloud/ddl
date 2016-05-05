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

import net.duckling.ddl.service.user.UserExt;
import net.duckling.ddl.util.PinyinUtil;

/**
 * @date 2011-11-10
 * @author JohnX
 */
public final class ContactUtil {
    private ContactUtil() {
    }

    public static ContactExt convertToContactExt(Contact contact) {
        ContactExt instance = new ContactExt();
        instance.setAddress(contact.getAddress());
        instance.setDepartment(contact.getDepartment());
        instance.setId(contact.getId());
        instance.setMainEmail(contact.getMainEmail());
        instance.setMobile(contact.getMobile());
        instance.setMsn(contact.getMsn());
        instance.setName(contact.getName());
        instance.setOptionEmail(contact.getOptionEmail());
        instance.setOrgnization(contact.getOrgnization());
        instance.setPhoto(contact.getPhoto());
        instance.setPinyin(contact.getPinyin());
        instance.setQq(contact.getQq());
        instance.setSex(contact.getSex());
        instance.setTelephone(contact.getTelephone());
        instance.setUid(contact.getUid());
        instance.setWeibo(contact.getWeibo());
        instance.setSource(ContactExt.USER);
        instance.setTag(ContactConstants.PERSON_CONTACT);
        return instance;
    }

    public static ContactExt convertToContactExt(UserExt userExt) {
        ContactExt instance = new ContactExt();
        instance.setAddress(userExt.getAddress());
        instance.setDepartment(userExt.getDepartment());
        instance.setId(userExt.getId());
        instance.setMainEmail(userExt.getEmail());
        instance.setMobile(userExt.getMobile());
        instance.setMsn(userExt.getMsn());
        instance.setName(userExt.getName());
        instance.setOrgnization(userExt.getOrgnization());
        instance.setPhoto(userExt.getPhoto());
        instance.setPinyin(PinyinUtil.getPinyin(instance.getName()));
        instance.setQq(userExt.getQq());
        instance.setSex(userExt.getSex());
        instance.setTelephone(userExt.getTelephone());
        instance.setUid(userExt.getUid());
        instance.setWeibo(userExt.getWeibo());
        instance.setPinyin(userExt.getPinyin());
        instance.setSource(ContactExt.TEAM);
        instance.setTag(ContactConstants.TEAM_CONTACT);
        return instance;
    }

    public static ContactExt convertToContactExt(UserExt userExt, String owner) {
        ContactExt instance = new ContactExt();
        instance.setAddress(userExt.getAddress());
        instance.setDepartment(userExt.getDepartment());
        instance.setId(userExt.getId());
        instance.setMainEmail(userExt.getUid());
        instance.setMobile(userExt.getMobile());
        instance.setMsn(userExt.getMsn());
        instance.setName(userExt.getName());
        instance.setOrgnization(userExt.getOrgnization());
        instance.setPhoto(userExt.getPhoto());
        instance.setPinyin(PinyinUtil.getPinyin(instance.getName()));
        instance.setQq(userExt.getQq());
        instance.setSex(userExt.getSex());
        instance.setTelephone(userExt.getTelephone());
        instance.setUid(owner);
        instance.setWeibo(userExt.getWeibo());
        instance.setPinyin(userExt.getPinyin());
        instance.setSource(ContactExt.TEAM);
        instance.setTag(ContactConstants.TEAM_CONTACT);
        return instance;
    }

    public static Contact convertToContact(UserExt userExt, String owner) {
        Contact instance = new Contact();
        instance.setAddress(userExt.getAddress());
        instance.setDepartment(userExt.getDepartment());
        instance.setMainEmail(userExt.getEmail());
        instance.setMobile(userExt.getMobile());
        instance.setMsn(userExt.getMsn());
        instance.setName(userExt.getName());
        instance.setOrgnization(userExt.getOrgnization());
        instance.setPhoto(userExt.getPhoto());
        instance.setPinyin(PinyinUtil.getPinyin(instance.getName()));
        instance.setQq(userExt.getQq());
        instance.setSex(userExt.getSex());
        instance.setTelephone(userExt.getTelephone());
        instance.setUid(owner);
        instance.setWeibo(userExt.getWeibo());
        instance.setPinyin(userExt.getPinyin());
        return instance;
    }
}
