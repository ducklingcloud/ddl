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

package net.duckling.ddl.service.user;

import java.io.Serializable;
import java.util.Date;

/**
 * @date 2011-5-26
 * @author Clive Lee
 */
public class UserExt implements Serializable {

    public static final String CONF_STATUS_AVA = "available";
    public static final String CONF_STATUS_FOR = "forbidden";

    private static final long serialVersionUID = 1L;
    private int id;
    private String uid;
    private String name;
    private String confirmStatus;
    private String orgnization;
    private String department;
    private String sex;
    private String telephone;
    private String mobile;
    private String weibo;
    private String photo;
    private String address;
    private String qq;
    private String email;
    private String msn;
    private Date birthday;
    private String pinyin;
    private int operation;
    private Date regist_time;
    private int frequent;
    private int requestnum;
    private int version;
    private Date modifytime;
    private long unallocatedSpace;


    /**
     * @return the pinyin
     */
    public String getPinyin() {
        return pinyin;
    }
    /**
     * @param pinyin the pinyin to set
     */
    public void setPinyin(String pinyin) {
        this.pinyin = pinyin;
    }
    public int getId() {
        return id;
    }
    public void setId(int id) {
        this.id = id;
    }

    public String getConfirmStatus() {
        return confirmStatus;
    }
    public void setConfirmStatus(String confirmStatus) {
        this.confirmStatus = confirmStatus;
    }
    /**
     * @return the uid
     */
    public String getUid() {
        return uid;
    }

    /**
     * @param uid the uid to set
     */
    public void setUid(String uid) {
        this.uid = uid;
    }

    /**
     * @return the birthday
     */
    public Date getBirthday() {
        return birthday;
    }

    /**
     * @param birthday the birthday to set
     */
    public void setBirthday(Date birthday) {
        this.birthday = birthday;
    }

    public String getQq() {
        return qq;
    }

    /**
     * @param qq the qq to set
     */
    public void setQq(String qq) {
        this.qq = qq;
    }



    public String getEmail() {
        return email;
    }
    public void setEmail(String email) {
        this.email = email;
    }
    /**
     * @return the msn
     */
    public String getMsn() {
        return msn;
    }

    /**
     * @param msn the msn to set
     */
    public void setMsn(String msn) {
        this.msn = msn;
    }

    /**
     * @return the address
     */
    public String getAddress() {
        return address;
    }

    /**
     * @param address the address to set
     */
    public void setAddress(String address) {
        this.address = address;
    }

    /**
     * @return the photo
     */
    public String getPhoto() {
        return photo;
    }

    /**
     * @param photo the photo to set
     */
    public void setPhoto(String photo) {
        this.photo = photo;
    }

    /**
     * @return the sex
     */
    public String getSex() {
        return sex;
    }

    /**
     * @param sex the sex to set
     */
    public void setSex(String sex) {
        this.sex = sex;
    }


    /**
     * @return the name
     */
    public String getName() {
        return name;
    }
    /**
     * @param name the name to set
     */
    public void setName(String name) {
        this.name = name;
    }

    /**
     * @return the orgnization
     */
    public String getOrgnization() {
        return orgnization;
    }
    /**
     * @param orgnization the orgnization to set
     */
    public void setOrgnization(String orgnization) {
        this.orgnization = orgnization;
    }
    /**
     * @return the department
     */
    public String getDepartment() {
        return department;
    }
    /**
     * @param department the department to set
     */
    public void setDepartment(String department) {
        this.department = department;
    }
    /**
     * @return the telephone
     */
    public String getTelephone() {
        return telephone;
    }
    /**
     * @param telephone the telephone to set
     */
    public void setTelephone(String telephone) {
        this.telephone = telephone;
    }
    /**
     * @return the mobile
     */
    public String getMobile() {
        return mobile;
    }
    /**
     * @param mobile the mobile to set
     */
    public void setMobile(String mobile) {
        this.mobile = mobile;
    }
    /**
     * @return the weibo
     */
    public String getWeibo() {
        return weibo;
    }
    /**
     * @param weibo the weibo to set
     */
    public void setWeibo(String weibo) {
        this.weibo = weibo;
    }

    public Date getRegist_time() {
        return regist_time;
    }
    public void setRegist_time(Date regist_time) {
        this.regist_time = regist_time;
    }
    public int getFrequent() {
        return frequent;
    }
    public void setFrequent(int frequent) {
        this.frequent = frequent;
    }
    public int getRequestnum() {
        return requestnum;
    }
    public void setRequestnum(int requestnum) {
        this.requestnum = requestnum;
    }
    public int getVersion() {
        return version;
    }
    public void setVersion(int version) {
        this.version = version;
    }
    public Date getModifytime() {
        return modifytime;
    }
    public void setModifytime(Date modifytime) {
        this.modifytime = modifytime;
    }

    public int getOperation() {
        return operation;
    }
    public void setOperation(int operation) {
        this.operation = operation;
    }
    public long getUnallocatedSpace() {
        return unallocatedSpace;
    }
    public void setUnallocatedSpace(long unallocatedSpace) {
        this.unallocatedSpace = unallocatedSpace;
    }
    @Override
    public boolean equals(Object obj) {
        if(obj instanceof UserExt) {
            UserExt ue = (UserExt)obj;
            return this.uid.equals(ue.getUid());
        }
        return false;
    }
    @Override
    public int hashCode() {
        return this.uid.hashCode();
    }

    public boolean isConfStatusAvailable(){
        return CONF_STATUS_AVA.equals(confirmStatus);
    }

}
