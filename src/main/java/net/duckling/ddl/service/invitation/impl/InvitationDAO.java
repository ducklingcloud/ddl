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

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import net.duckling.ddl.service.invitation.Invitation;
import net.duckling.ddl.util.AbstractBaseDAO;
import net.duckling.ddl.util.AoneTimeUtils;
import net.duckling.ddl.util.StatusUtil;

import org.springframework.jdbc.core.PreparedStatementCreator;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.jdbc.support.GeneratedKeyHolder;
import org.springframework.stereotype.Repository;


/**
 * @date 2011-6-17
 * @author Clive Lee
 */
@Repository
public class InvitationDAO extends AbstractBaseDAO {
    private static RowMapper<Invitation> rowMapper = new RowMapper<Invitation>() {
            public Invitation mapRow(ResultSet rs, int rowNum) throws SQLException {
                Invitation instance = new Invitation();
                instance.setId(rs.getInt("id"));
                instance.setInviter(rs.getString("inviter"));
                instance.setInvitee(rs.getString("invitee"));
                instance.setStatus(rs.getString("status"));
                instance.setEncode(rs.getString("encode"));
                instance.setInviterName(rs.getString("inviter_name")); //冗余字段
                instance.setTeamId(rs.getInt("team"));
                instance.setTeamDisplayName(rs.getString("team_display_name")); //冗余字段
                instance.setTeamName(rs.getString("team_name"));  //冗余字段
                instance.setAcceptTime(transferDate(rs.getTimestamp("accept_time")));
                instance.setInviteTime(transferDate(rs.getTimestamp("invite_time")));
                instance.setMessage(rs.getString("message")); //新增字段
                return instance;
            }
        };
    private static String transferDate(Date d){
        if(d==null){
            return null;
        }
        SimpleDateFormat s = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        return s.format(d);
    }


    private static final String INSERT_AND_UPDATE = "insert into vwb_invitation (encode, inviter, invitee, team, invite_time, status, message) values(?,?,?,?,?,?,?)";
    public int insertInvitation(final Invitation instance) {
        GeneratedKeyHolder keyHolder = new GeneratedKeyHolder();
        getJdbcTemplate().update(new PreparedStatementCreator() {
                public PreparedStatement createPreparedStatement(Connection conn)
                        throws SQLException {
                    PreparedStatement pst = conn.prepareStatement(INSERT_AND_UPDATE, PreparedStatement.RETURN_GENERATED_KEYS);
                    int i = 0;
                    pst.setString(++i, instance.getEncode());
                    pst.setString(++i, instance.getInviter());
                    pst.setString(++i, instance.getInvitee());
                    pst.setInt(++i, instance.getTeamId());
                    pst.setString(++i, instance.getInviteTime());
                    pst.setString(++i, instance.getStatus());
                    pst.setString(++i, instance.getMessage());
                    return pst;
                }
            }, keyHolder);
        return (keyHolder.getKey()!=null)?keyHolder.getKey().intValue():-1;
    }

    public void updateInvitationStatus(Invitation instance) {
        String sql = "update vwb_invitation set status=?,accept_time=?,invite_time=?,message=? where id=? and encode=?";
        getJdbcTemplate().update(sql, new Object[] {instance.getStatus(),instance.getAcceptTime(),instance.getInviteTime(),
                instance.getMessage(),instance.getId(),instance.getEncode()});
    }

    public static final String GET_EXIST_VALID_INVITE = "select v.*,t.name as team_name,t.display_name as team_display_name,x.name as inviter_name from vwb_invitation v,vwb_user_ext x,vwb_team t where v.team=? and v.invitee=? and status='WAITING' and v.team=t.id and x.uid=v.inviter";
    public Invitation getExistValidInvitation(String user,int tid) {
        List<Invitation> results = this.getJdbcTemplate().query(GET_EXIST_VALID_INVITE,new Object[] {tid,user}, rowMapper);
        if(results!=null&&results.size()!=0){
            return results.get(0);
        }
        return null;
    }

    public boolean checkInvitation(Invitation instance) {
        String sql = "select count(*) from vwb_invitation v where v.id=? and encode=? and DATE_ADD(v.invite_time,INTERVAL 30 DAY)>? and v.status='WAITING'";
        int count = getJdbcTemplate().queryForObject(sql,new Object[] {instance.getId(),instance.getEncode(),AoneTimeUtils.formatToDateTime(new Date())},Integer.class);
        return count==1;
    }

    private static final String QUERY_BY_ID_AND_ENCODE = "select v.*,t.name as team_name,t.display_name as team_display_name,x.name as inviter_name from vwb_invitation v,vwb_user_ext x,vwb_team t where v.id=? and v.encode=? and v.team=t.id and x.uid=v.inviter";
    public Invitation getInvitationByURL(String id,String encode) {
        List<Invitation> results = new ArrayList<Invitation>();
        results = getJdbcTemplate().query(QUERY_BY_ID_AND_ENCODE,new Object[] {id,encode}, rowMapper);
        if(results!=null && results.size()==1){
            return results.get(0);
        }
        return new Invitation();
    }

    private static final String QUERY_BY_TID = "select v.*,t.name as team_name,t.display_name as team_display_name,x.name as inviter_name from vwb_invitation v,vwb_user_ext x,vwb_team t where v.team=? and v.team=t.id and x.uid=v.inviter";
    public List<Invitation> getInvitationByTeam(int tid) {
        return getJdbcTemplate().query(QUERY_BY_TID,new Object[] {tid}, rowMapper);
    }

    private static final String QUERY_BY_UID = "select v.*,t.name as team_name,t.display_name as team_display_name,x.name as inviter_name from vwb_invitation v,vwb_user_ext x,vwb_team t where v.invitee=? and v.status='WAITING' and v.team=t.id and x.uid=v.inviter";
    public List<Invitation> getInvitationByUser(String user) {
        return getJdbcTemplate().query(QUERY_BY_UID,new Object[] {user}, rowMapper);
    }

    public boolean updateWaiteToAccept(int tid, String uid, String waiting) {
        String sql = "update vwb_invitation set status=? where team=? and invitee = ? and status = ?";
        return getJdbcTemplate().update(sql, new Object[]{StatusUtil.ACCEPT,tid,uid,waiting})>0;
    }

    public int getInvitationCount(String user) {
        String sql = "select count(v.id) from vwb_invitation v where v.invitee=? and v.status='WAITING' ";
        return getJdbcTemplate().queryForObject(sql,new Object[] {user}, Integer.class);
    }

}
