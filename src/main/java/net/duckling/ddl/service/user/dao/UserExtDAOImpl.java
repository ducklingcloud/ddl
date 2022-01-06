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

package net.duckling.ddl.service.user.dao;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Timestamp;
import java.util.Collection;
import java.util.Date;
import java.util.List;
import java.util.Set;

import net.duckling.ddl.service.user.SimpleUser;
import net.duckling.ddl.service.user.UserExt;
import net.duckling.ddl.service.user.impl.UserExtDAO;
import net.duckling.ddl.util.AbstractBaseDAO;
import net.duckling.ddl.util.PinyinUtil;
import net.duckling.ddl.util.StringUtil;

import org.springframework.dao.DataAccessException;
import org.springframework.jdbc.core.PreparedStatementCreator;
import org.springframework.jdbc.core.ResultSetExtractor;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.jdbc.support.GeneratedKeyHolder;

/**
 * @date 2011-5-27
 * @author Clive Lee
 */
public class UserExtDAOImpl extends AbstractBaseDAO implements UserExtDAO {

    public static final String INSERT = "insert into vwb_user_ext (uid,name,orgnization,department,sex,telephone,mobile,email,qq,msn,address,photo,weibo,pinyin,regist_time)"
            + "values(?,?,?,?,?,?,?,?,?,?,?,?,?,?,?)";
    public static final String QUICK_INSERT_ONE = "insert into vwb_user_ext (uid,name,email,pinyin,regist_time) values (?,?,?,?,?)";
    public static final String CHECK_EXIST = "select count(id) from vwb_user_ext where uid=?";
    public static final String UPDATE = "update";

    public static final String SEARCH_BY_NAME = "select * from vwb_user_ext where name rlike ?";
    public static final String SEARCH_BY_PINYIN = "select * from vwb_user_ext where pinyin rlike ?";
    public static final String SEARCH_BY_MAIL = "select * from vwb_user_ext where uid rlike ?";
    private static final String SEARCH_BY_USER_TEAM_AND_NAME = "select distinct e.* from vwb_user_ext e,vwb_team_acl a where e.uid=a.uid and a.tid in(select tid from vwb_team_acl where uid=? )and e.uid<>? and name rlike ?";
    private static final String SEARCH_BY_USER_PINYIN_AND_NAME = "select distinct e.* from vwb_user_ext e,vwb_team_acl a where e.uid=a.uid and a.tid in(select tid from vwb_team_acl where uid=? )and e.uid<>? and (pinyin rlike ? or e.uid rlike ?)";

    @Override
    public List<UserExt> searchUserByName(String name) {
        return getJdbcTemplate().query(SEARCH_BY_NAME, new Object[] { name }, rowMapper);
    }

    private RowMapper<UserExt> rowMapper = new RowMapper<UserExt>() {
            public UserExt mapRow(ResultSet rs, int index) throws SQLException {
                UserExt ext = new UserExt();
                ext.setId(rs.getInt("id"));
                ext.setUid(rs.getString("uid"));
                ext.setConfirmStatus(rs.getString("confirm_status"));
                ext.setName(rs.getString("name"));
                ext.setSex(rs.getString("sex"));
                ext.setTelephone(rs.getString("telephone"));
                ext.setQq(rs.getString("qq"));
                ext.setAddress(rs.getString("address"));
                ext.setMsn(rs.getString("msn"));
                ext.setWeibo(rs.getString("weibo"));
                ext.setEmail(rs.getString("email"));
                ext.setOrgnization(rs.getString("orgnization"));
                ext.setDepartment(rs.getString("department"));
                ext.setMobile(rs.getString("mobile"));
                ext.setPinyin(rs.getString("pinyin"));
                ext.setUnallocatedSpace(rs.getLong("unallocated_space"));
                return ext;
            }
        };

    private RowMapper<SimpleUser> simpleMapper = new RowMapper<SimpleUser>() {
            public SimpleUser mapRow(ResultSet rs, int rowNum) throws SQLException {
                SimpleUser ext = new SimpleUser();
                ext.setId(rs.getInt("id"));
                ext.setUid(rs.getString("uid"));
                ext.setName(rs.getString("name"));
                ext.setEmail(rs.getString("email"));
                return ext;
            }
        };

    @Override
    public List<UserExt> searchUserByUserTeamAndName(String uid, String name) {
        return getJdbcTemplate().query(SEARCH_BY_USER_TEAM_AND_NAME, new Object[] { uid, uid, name }, rowMapper);
    }

    @Override
    public List<UserExt> searchUserByUserPinyinAndName(String uid, String name) {
        return getJdbcTemplate()
                .query(SEARCH_BY_USER_PINYIN_AND_NAME, new Object[] { uid, uid, name, name }, rowMapper);
    }

    @Override
    public List<UserExt> searchUserByPinyin(String pinyin) {
        return getJdbcTemplate().query(SEARCH_BY_PINYIN, new Object[] { pinyin }, rowMapper);
    }

    @Override
    public List<UserExt> searchUserByMail(String mail) {
        List<UserExt> users = getJdbcTemplate().query(SEARCH_BY_MAIL, new Object[] { mail }, rowMapper);
        return users;
    }

    @Override
    public List<UserExt> searchByUnallocatedSpace() {
        String sql = "select * from vwb_user_ext where unallocated_space > 0";
        return getJdbcTemplate().query(sql, rowMapper);
    }

    @Override
    public int createUserExt(final UserExt instance) {
        GeneratedKeyHolder keyHolder = new GeneratedKeyHolder();
        getJdbcTemplate().update(new PreparedStatementCreator() {
                public PreparedStatement createPreparedStatement(Connection conn) throws SQLException {
                    PreparedStatement pst = conn.prepareStatement(INSERT, PreparedStatement.RETURN_GENERATED_KEYS);
                    int i = 0;
                    pst.setString(++i, instance.getUid());
                    pst.setString(++i, instance.getName());
                    pst.setString(++i, instance.getOrgnization());
                    pst.setString(++i, instance.getDepartment());
                    pst.setString(++i, instance.getSex());
                    pst.setString(++i, instance.getTelephone());
                    pst.setString(++i, instance.getMobile());
                    pst.setString(++i, instance.getEmail());
                    pst.setString(++i, instance.getQq());
                    pst.setString(++i, instance.getMsn());
                    pst.setString(++i, instance.getAddress());
                    pst.setString(++i, instance.getPhoto());
                    pst.setString(++i, instance.getWeibo());
                    pst.setString(++i, instance.getPinyin());
                    pst.setTimestamp(++i, new Timestamp(new Date().getTime()));
                    return pst;
                }
            }, keyHolder);
        Number key = keyHolder.getKey();
        return (key != null) ? key.intValue() : -1;
    }

    @Override
    public int createUserExt(final String uid, final String name) {
        GeneratedKeyHolder keyHolder = new GeneratedKeyHolder();
        final String pinyin = (null == name || "".equals(name)) ? uid : PinyinUtil.getPinyin(name);
        getJdbcTemplate().update(new PreparedStatementCreator() {
                public PreparedStatement createPreparedStatement(Connection conn) throws SQLException {
                    PreparedStatement pst = conn.prepareStatement(QUICK_INSERT_ONE, PreparedStatement.RETURN_GENERATED_KEYS);
                    int i = 0;
                    pst.setString(++i, uid);
                    pst.setString(++i, name);
                    pst.setString(++i, uid);
                    pst.setString(++i, pinyin);
                    pst.setTimestamp(++i, new Timestamp(new Date().getTime()));
                    return pst;
                }
            }, keyHolder);
        Number key = keyHolder.getKey();
        return (key != null) ? key.intValue() : -1;
    }

    @Override
    public void updateUserExt(UserExt u) {
        String sql = "update vwb_user_ext set name=?,orgnization=?,department=?,sex=?,telephone=?,mobile=?,email=?,qq=?,msn=?,address=?,photo=?,weibo=? ,pinyin=?,confirm_status=?,unallocated_space=? where uid=?";
        getJdbcTemplate().update(
            sql,
            new Object[] { u.getName(), u.getOrgnization(), u.getDepartment(), u.getSex(), u.getTelephone(),
                u.getMobile(), u.getEmail(), u.getQq(), u.getMsn(), u.getAddress(), u.getPhoto(), u.getWeibo(),
                u.getPinyin(), u.getConfirmStatus(), u.getUnallocatedSpace(), u.getUid() });
    }

    private static final String CHECK_EXIST_REGISTER = "select count(id) from vwb_user_ext where uid=? ";

    @Override
    public boolean isExistRegister(String email) {
        return getJdbcTemplate().queryForObject(CHECK_EXIST_REGISTER, new Object[] { email }, Integer.class) > 0;
    }

    private static final String QUERY_FOR_USER_EXT = "select * from vwb_user_ext where uid=?";

    @Override
    public UserExt getUserExtInfo(String uid) {
        return getJdbcTemplate().query(QUERY_FOR_USER_EXT, new Object[] { uid }, new ResultSetExtractor<UserExt>() {
                public UserExt extractData(ResultSet rs) throws SQLException, DataAccessException {
                    if (rs.next()) {
                        return rowMapper.mapRow(rs, 0);
                    }
                    return null;
                }
            });
    }

    private static final String QUERY_BY_UXID = "select * from vwb_user_ext where id=?";

    @Override
    public UserExt getUserExtByAutoID(int uxid) {
        return getJdbcTemplate().query(QUERY_BY_UXID, new Object[] { uxid }, new ResultSetExtractor<UserExt>() {
                public UserExt extractData(ResultSet rs) throws SQLException, DataAccessException {
                    if (rs.next()) {
                        return rowMapper.mapRow(rs, 0);
                    }
                    return null;
                }
            });
    }

    @Override
    public List<UserExt> getUserExtList(List<String> userList) {
        String sql = "select * from vwb_user_ext where  uid in (";
        StringBuilder sb = new StringBuilder();
        for (int i = 0; i < userList.size(); i++) {
            sb.append("'");
            sb.append(userList.get(i));
            sb.append("'");
            if (i != (userList.size() - 1)) {
                sb.append(",");
            }
        }
        sql = sql + sb.toString() + ")";
        return getJdbcTemplate().query(sql, new Object[] {}, rowMapper);
    }

    @Override
    public List<SimpleUser> getAllSimpleUser() {
        String sql = "select * from vwb_user_ext";
        return getJdbcTemplate().query(sql, new Object[] {}, simpleMapper);
    }

    @Override
    public SimpleUser getSimpleUser(String uid) {
        String sql = "select * from vwb_user_ext where uid=?";
        List<SimpleUser> userList = getJdbcTemplate().query(sql, new Object[] { uid }, simpleMapper);
        return (userList != null && userList.size() != 0) ? userList.get(0) : null;
    }

    @Override
    public int getTotalUserNumber() {
        String sql = "select count(*) from vwb_user_ext";
        return this.getJdbcTemplate().queryForObject(sql, Integer.class);
    }

    @Override
    public List<UserExt> getUserExtByIds(Collection<Integer> ids) {
        String sql = "select * from vwb_user_ext where id in" + StringUtil.getSQLInFromInt(ids);
        return this.getJdbcTemplate().query(sql, rowMapper);
    }

    @Override
    public List<UserExt> getUserExtByUids(Set<String> userId) {
        String sql = "select * from vwb_user_ext where uid in" + StringUtil.getSQLInFromStr(userId.size());
        Object[] s = userId.toArray();
        return getJdbcTemplate().query(sql, s, rowMapper);
    }

}
