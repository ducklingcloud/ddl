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

package net.duckling.ddl.service.authority.impl;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.List;

import net.duckling.ddl.service.authority.TeamAcl;
import net.duckling.ddl.service.authority.UserTeamAclBean;
import net.duckling.ddl.util.AbstractBaseDAO;

import org.apache.log4j.Logger;
import org.springframework.jdbc.core.BatchPreparedStatementSetter;
import org.springframework.jdbc.core.RowMapper;


/**
 * @date 2011-5-26
 * @author Clive Lee
 */
public class TeamAclDAO extends AbstractBaseDAO {

    private static final Logger LOG = Logger.getLogger(TeamAclDAO.class);

    public static final String INSERT_SQL = "insert into vwb_team_acl(tid, uid, auth) values(?,?,?)";
    public static final String QUERY_BY_UID = "select a.tid,e.uid,e.name,a.auth from vwb_team_acl a ,vwb_user_ext e where a.uid=? and a.uid=e.uid";
    public static final String SINGLE_MEMBER_ACL = "select a.tid,e.uid,e.name,a.auth from vwb_team_acl a,vwb_user_ext e where a.tid=? and a.uid=? and a.uid = e.uid";
    public static final String TEAM_ACL_QUERY = "select a.tid,e.uid,e.name,a.auth from vwb_team_acl a,vwb_user_ext e where a.tid=? and a.uid = e.uid";
    public static final String REMOVE_MEMBER_ACL = "delete from vwb_team_acl where tid=? and uid=?";
    public static final String UPDATE_ACL = "update vwb_team_acl a set a.auth=? where a.tid=? and a.uid=?";
    public static final String QUERY_BY_TID_AUTH = "select a.tid,e.uid,e.name,a.auth  from vwb_team_acl a ,vwb_user_ext e where tid=? and auth=? and a.uid = e.uid";
    public static final String QUERY_BY_UID_AUTH = "select * from vwb_team_acl where uid=? and auth=?";
    private RowMapper<TeamAcl> rowMapper = new RowMapper<TeamAcl>() {
            public TeamAcl mapRow(ResultSet rs, int index) throws SQLException {
                TeamAcl instance = new TeamAcl();
                instance.setTid(rs.getString("tid"));
                instance.setUid(rs.getString("uid"));
                instance.setUserName(rs.getString("name"));
                instance.setAuth(rs.getString("auth"));
                return instance;
            }
        };

    private RowMapper<UserTeamAclBean> userRowMapper = new RowMapper<UserTeamAclBean>() {
            public UserTeamAclBean mapRow(ResultSet rs, int index) throws SQLException {
                UserTeamAclBean instance = new UserTeamAclBean();
                instance.setId(rs.getInt("id"));
                instance.setTid(rs.getInt("tid"));
                instance.setUid(rs.getString("uid"));
                instance.setAuth(rs.getString("auth"));
                return instance;
            }
        };
    public UserTeamAclBean getUserTeamAcl(int tid,String uid){
        String sql = "select * from vwb_team_acl where tid=? and uid=?";
        List<UserTeamAclBean> result = getJdbcTemplate().query(sql, new Object[]{tid,uid}, userRowMapper);
        if(result!=null&&!result.isEmpty()){
            return result.get(0);
        }else{
            return null;
        }
    }
    public List<UserTeamAclBean> getTeamAclByUidAndAuth(String uid,String auth){
        return getJdbcTemplate().query(QUERY_BY_UID_AUTH, new Object[]{uid,auth}, userRowMapper);
    }

    public List<TeamAcl> getTeamMembersAuthority(int tid) {
        return getJdbcTemplate().query(TEAM_ACL_QUERY,
                                       new Object[] { tid }, rowMapper);
    }

    public String getSingleMemberAuthority(int tid, String uid) {
        List<TeamAcl> ca = getJdbcTemplate().query(SINGLE_MEMBER_ACL,
                                                   new Object[] { tid, uid }, rowMapper);

        if(null == ca || ca.size()<=0){
            return "forbid";
        }
        else if(ca.size()>1){
            LOG.error("there exists more than one object while query for TeamAcl by tid="+tid+" and uid="+uid);
        }
        return ca.get(0).getAuth();
    }

    public boolean addBatchTeamAcl(final String[] uids, final int tid, final String acl) {
        getJdbcTemplate().batchUpdate(INSERT_SQL, new BatchPreparedStatementSetter() {
                public int getBatchSize() {
                    return uids.length;
                }

                public void setValues(PreparedStatement pst, int index) throws SQLException {
                    int i = 0;
                    pst.setInt(++i, tid);
                    pst.setString(++i, uids[index]);
                    pst.setString(++i, acl);
                }
            });
        return false;
    }

    public boolean addBatchTeamAcl(final String[] uids, final int tid, final String[] acls) {
        getJdbcTemplate().batchUpdate(INSERT_SQL, new BatchPreparedStatementSetter() {
                public int getBatchSize() {
                    return uids.length;
                }

                public void setValues(PreparedStatement pst, int index) throws SQLException {
                    int i = 0;
                    pst.setInt(++i, tid);
                    pst.setString(++i, uids[index]);
                    pst.setString(++i, acls[index]);
                }
            });
        return false;
    }

    public void removeMemberAcl(int tid, String uid) {
        getJdbcTemplate().update(REMOVE_MEMBER_ACL, new Object[] { tid, uid });
    }

    /**
     * 批量删除用户权限
     * @param tid
     * @param uids
     */
    public void removeMemberAcls(final int tid, final String[] uids) {
        if(null == uids || uids.length<=0){
            return;
        }
        this.getJdbcTemplate().batchUpdate(REMOVE_MEMBER_ACL, new BatchPreparedStatementSetter(){

                @Override
                public int getBatchSize() {
                    return uids.length;
                }

                @Override
                public void setValues(PreparedStatement ps, int index)
                        throws SQLException {
                    ps.setInt(1, tid);
                    ps.setString(2, uids[index]);
                }

            });
    }

    public void updateMembersAuthority(final int tid,final String[] uids,final String[] auths) {
        getJdbcTemplate().batchUpdate(UPDATE_ACL, new BatchPreparedStatementSetter(){
                public int getBatchSize() {
                    return uids.length;
                }
                public void setValues(PreparedStatement pst, int index) throws SQLException {
                    int i=0;
                    pst.setString(++i, auths[index]);
                    pst.setInt(++i, tid);
                    pst.setString(++i,uids[index]);
                }
            });
    }

    public List<TeamAcl> getTeamAclByTidAndAuth(int tid,String auth){
        return getJdbcTemplate().query(QUERY_BY_TID_AUTH, new Object[]{tid,auth}, rowMapper);
    }

    public List<TeamAcl> getUserAllTeamAcl(String uid) {
        return getJdbcTemplate().query(QUERY_BY_UID, new Object[]{uid}, rowMapper);
    }

    public void updateMemberAuthority(int tid, String uid, String auth) {
        getJdbcTemplate().update(UPDATE_ACL, new Object[]{auth,tid,uid});
    }
}
