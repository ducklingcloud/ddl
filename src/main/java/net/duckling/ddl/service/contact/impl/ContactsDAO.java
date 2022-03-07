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

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.List;

import net.duckling.ddl.service.contact.Contact;
import net.duckling.ddl.util.AbstractBaseDAO;

import org.springframework.jdbc.core.BatchPreparedStatementSetter;
import org.springframework.jdbc.core.PreparedStatementCreator;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.jdbc.support.GeneratedKeyHolder;
import org.springframework.stereotype.Repository;


/**
 * @date 2011-11-9
 * @author JohnX
 */
@Repository
public class ContactsDAO extends AbstractBaseDAO {
    private static final String QUERY = "select * from vwb_person_contacts where uid=?";
    private static final String QUERY_BY_ID="select * from vwb_person_contacts where id=?";
    private static final String QUERY_BY_NAME =
            "SELECT * FROM vwb_person_contacts WHERE uid=? AND name LIKE ?";
    private static final String INSERT = "insert into vwb_person_contacts(uid,main_email,option_email,name,orgnization,department,sex,telephone," +
            "mobile,qq,msn,address,photo,weibo,pinyin) values(?,?,?,?,?,?,?,?,?,?,?,?,?,?,?)";
    private static final String DELETE_BY_NAME = "delete from vwb_person_contacts where and name=?";
    private static final String DELETE_BY_ID = "delete from vwb_person_contacts where id=?";
    private static final String DELETE_BY_UID = "delete from vwb_person_contacts where uid=?";
    private static final String UPDATE_BY_ID = "update vwb_person_contacts set uid=?,main_email=?,option_email=?,name=?,orgnization=?,department=?,sex=?,telephone=?,mobile=?,qq=?,msn=?,address=?,photo=?,weibo=?,pinyin=? where id=?";
    private static final String UPDATE_BY_NAME = "update vwb_person_contacts set main_email=?,option_email=?,orgnization=?,department=?,sex=?,telephone=?,mobile=?,qq=?,msn=?,address=?,photo=?,weibo=?,pinyin=? where uid=? and name=?";
    private static final String QUERY_BY_PINYIN =
            "SELECT * FROM vwb_person_contacts WHERE uid=? AND pinyin LIKE ?";
    private static final String QUERY_BY_MAIL =
            "SELECT * FROM vwb_person_contacts WHERE uid=? AND main_email LIKE ?";
    private static final String QUERY_BY_UNM = "select * from vwb_person_contacts where uid=? and name=? and main_email=?";

    public List<Contact> getUserContactsByOwner(String uid) {
        return getJdbcTemplate().query(QUERY, new Object[]{uid}, contactRowMapper);
    }

    public List<Contact> getUserContactsByName(String uid, String name) {
        return getJdbcTemplate().query(QUERY_BY_NAME,
                                       new Object[]{uid, getLike(name)},
                                       contactRowMapper);
    }

    public List<Contact> getUserContactsByPinyin(String uid, String pinyin) {
        return getJdbcTemplate().query(QUERY_BY_PINYIN,
                                       new Object[]{uid, getLike(pinyin)},
                                       contactRowMapper);
    }
    
    public List<Contact> getUserContactsByMail(String uid, String mail) {
        return getJdbcTemplate().query(QUERY_BY_MAIL,
                                       new Object[]{uid, getLike(mail)},
                                       contactRowMapper);
    }

    public List<Contact> getContacts(String uid, String name, String mail) {
        return getJdbcTemplate().query(QUERY_BY_UNM, new Object[]{uid, name, mail}, contactRowMapper);
    }
    
    public List<Contact> getUserContactById(int id) {
        return getJdbcTemplate().query(QUERY_BY_ID, new Object[]{id}, contactRowMapper);
    }
    
    public void addContactItems(final Contact[] contacts) {
        getJdbcTemplate().batchUpdate(INSERT, new BatchPreparedStatementSetter() {
                public int getBatchSize() {
                    return contacts.length;
                }

                public void setValues(PreparedStatement pst, int index) throws SQLException {
                    int i = 0;
                    pst.setString(++i, contacts[index].getUid());
                    pst.setString(++i, contacts[index].getMainEmail());
                    pst.setString(++i, contacts[index].getOptionEmail());
                    pst.setString(++i, contacts[index].getName());
                    pst.setString(++i, contacts[index].getOrgnization());
                    pst.setString(++i, contacts[index].getDepartment());
                    pst.setString(++i, contacts[index].getSex());
                    pst.setString(++i, contacts[index].getTelephone());
                    pst.setString(++i, contacts[index].getMobile());
                    pst.setString(++i, contacts[index].getQq());
                    pst.setString(++i, contacts[index].getMsn());
                    pst.setString(++i, contacts[index].getAddress());
                    pst.setString(++i, contacts[index].getPhoto());
                    pst.setString(++i, contacts[index].getWeibo());
                    pst.setString(++i, contacts[index].getPinyin());
                }
            });
    }
    
    private boolean exists(Contact contact) {
        List<Contact> contacts = this.getContacts(contact.getUid(), contact.getName(), contact.getMainEmail());
        if(contacts.size()>0) {
            return true; //表示个人通讯录中已有此人记录
        }
        return false;
    }
    
    public int addContactItem(final Contact instance) {
        if(exists(instance)) {
            return -1;
        }
        GeneratedKeyHolder keyHolder = new GeneratedKeyHolder();
        getJdbcTemplate().update(new PreparedStatementCreator() {
                public PreparedStatement createPreparedStatement(Connection conn) throws SQLException {
                    PreparedStatement pst = conn.prepareStatement(INSERT,
                                                                  PreparedStatement.RETURN_GENERATED_KEYS);
                    int i = 0;
                    pst.setString(++i, instance.getUid());
                    pst.setString(++i, instance.getMainEmail());
                    pst.setString(++i, instance.getOptionEmail());
                    pst.setString(++i, instance.getName());
                    pst.setString(++i, instance.getOrgnization());
                    pst.setString(++i, instance.getDepartment());
                    pst.setString(++i, instance.getSex());
                    pst.setString(++i, instance.getTelephone());
                    pst.setString(++i, instance.getMobile());
                    pst.setString(++i, instance.getQq());
                    pst.setString(++i, instance.getMsn());
                    pst.setString(++i, instance.getAddress());
                    pst.setString(++i, instance.getPhoto());
                    pst.setString(++i, instance.getWeibo());
                    pst.setString(++i, instance.getPinyin());
                    return pst;
                }
            }, keyHolder);
        Number key = keyHolder.getKey();
        return (key != null) ? key.intValue() : -1;
    }

    public void deleteByName(int uid, String name) {
        getJdbcTemplate().update(DELETE_BY_NAME, new Object[] {uid, name});
    }

    public void deleteByID(int id) {
        getJdbcTemplate().update(DELETE_BY_ID, new Object[] {id});
    }

    public void deleteByUid(String uid) {
        getJdbcTemplate().update(DELETE_BY_UID, new Object[] {uid});
    }

    public void updateContactById(Contact c) {
        getJdbcTemplate().update(UPDATE_BY_ID, new Object[] {
                c.getUid(),c.getMainEmail(),c.getOptionEmail(),c.getName(),c.getOrgnization(),c.getDepartment(),c.getSex(),c.getTelephone(),c.getMobile(),
                c.getQq(),c.getMsn(),c.getAddress(),c.getPhoto(),c.getWeibo(),c.getPinyin(),c.getId()
            });
    }
    
    public void updateContactByName(Contact c) {
        getJdbcTemplate().update(UPDATE_BY_NAME, new Object[] {
                c.getMainEmail(),c.getOptionEmail(),c.getOrgnization(),c.getDepartment(),c.getSex(),c.getTelephone(),c.getMobile(),
                c.getQq(),c.getMsn(),c.getAddress(),c.getPhoto(),c.getWeibo(),c.getPinyin(),c.getUid(),c.getName()
            });
    }

    public void batchUpdateById(final Contact[] contacts) {
        getJdbcTemplate().batchUpdate(UPDATE_BY_ID, new BatchPreparedStatementSetter() {
                public int getBatchSize() {
                    return contacts.length;
                }

                public void setValues(PreparedStatement pst, int index) throws SQLException {
                    int i = 0;
                    pst.setString(++i, contacts[index].getUid());
                    pst.setString(++i, contacts[index].getMainEmail());
                    pst.setString(++i, contacts[index].getOptionEmail());
                    pst.setString(++i, contacts[index].getName());
                    pst.setString(++i, contacts[index].getOrgnization());
                    pst.setString(++i, contacts[index].getDepartment());
                    pst.setString(++i, contacts[index].getSex());
                    pst.setString(++i, contacts[index].getTelephone());
                    pst.setString(++i, contacts[index].getMobile());
                    pst.setString(++i, contacts[index].getQq());
                    pst.setString(++i, contacts[index].getMsn());
                    pst.setString(++i, contacts[index].getAddress());
                    pst.setString(++i, contacts[index].getPhoto());
                    pst.setString(++i, contacts[index].getWeibo());
                    pst.setString(++i, contacts[index].getPinyin());
                    pst.setInt(++i, contacts[index].getId());
                }
            });
    }

    private RowMapper<Contact> contactRowMapper = new RowMapper<Contact>() {
            public Contact mapRow(ResultSet rs, int index) throws SQLException {
                Contact instance = new Contact();
                instance.setId(rs.getInt("id"));
                instance.setUid(rs.getString("uid"));
                instance.setMainEmail(rs.getString("main_email"));
                instance.setOptionEmail(rs.getString("option_email"));
                instance.setName(rs.getString("name"));
                instance.setMobile(rs.getString("mobile"));
                instance.setAddress(rs.getString("address"));
                instance.setMsn(rs.getString("msn"));
                instance.setQq(rs.getString("qq"));
                instance.setSex(rs.getString("sex"));
                instance.setOrgnization(rs.getString("orgnization"));
                instance.setTelephone(rs.getString("telephone"));
                // instance.setBirthday(rs.getDate("birthday"));
                // 做一个文字处理的辅助类，比如TextUtil，处理日期的格式化
                instance.setDepartment(rs.getString("department"));
                instance.setWeibo(rs.getString("weibo"));
                instance.setPinyin(rs.getString("pinyin"));
                instance.setPhoto(rs.getString("photo"));
                return instance;
            }
        };

    private String getLike(String str) {
        return "%"+ str +"%";
    }
}
