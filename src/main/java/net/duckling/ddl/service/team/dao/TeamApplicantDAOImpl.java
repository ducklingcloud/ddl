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
package net.duckling.ddl.service.team.dao;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Timestamp;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import net.duckling.ddl.service.team.TeamApplicant;
import net.duckling.ddl.service.team.TeamApplicantNoticeRender;
import net.duckling.ddl.service.team.TeamApplicantRender;
import net.duckling.ddl.service.team.impl.TeamApplicantDAO;
import net.duckling.ddl.util.AbstractBaseDAO;

import org.apache.log4j.Logger;
import org.springframework.jdbc.core.BatchPreparedStatementSetter;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.jdbc.core.namedparam.MapSqlParameterSource;
import org.springframework.jdbc.support.GeneratedKeyHolder;
import org.springframework.jdbc.support.KeyHolder;
import org.springframework.stereotype.Repository;

/**
 * 团队申请记录的DAO实现类
 * @author Yangxp
 * @since 2012-11-13
 */
@Repository
public class TeamApplicantDAOImpl extends AbstractBaseDAO implements TeamApplicantDAO {

    private static final Logger LOG = Logger.getLogger(TeamApplicantDAOImpl.class);
    private TeamApplicantRowMapper rowMapper = new TeamApplicantRowMapper();
    private TeamApplicantRenderRowMapper renderRowMapper = new TeamApplicantRenderRowMapper();
    private TeamApplicantNoticeRenderRowMapper noticeRowMapper = new TeamApplicantNoticeRenderRowMapper();
    private static final String UPDATE = "update vwb_team_applicant set status=:status,reason=:reason, apply_time=:applyTime, i_know=:iKnow where uid=:uid and tid=:tid";

    @Override
    public int create(TeamApplicant ta) {
        String sql = "insert into vwb_team_applicant(uid,tid,status,reason,apply_time,i_know) values(:uid,:tid,:status,:reason,:applyTime,:iKnow)";
        MapSqlParameterSource sps = new MapSqlParameterSource();
        sps.addValues(generateParamMap(ta));
        KeyHolder keyHolder = new GeneratedKeyHolder();
        this.getNamedParameterJdbcTemplate().update(sql, sps, keyHolder);
        return keyHolder.getKey().intValue();
    }

    @Override
    public void updateByUIDTID(TeamApplicant ta) {
        this.getNamedParameterJdbcTemplate().update(UPDATE, generateParamMap(ta));
    }

    @Override
    public void batchUpdateByUIDTID(List<TeamApplicant> taList) {
        if(null != taList && !taList.isEmpty()){
            @SuppressWarnings("unchecked")
                Map<String, Object>[] paramMaps = new HashMap[taList.size()];
            int i=0;
            for(TeamApplicant ta : taList){
                paramMaps[i++] = generateParamMap(ta);
            }
            this.getNamedParameterJdbcTemplate().batchUpdate(UPDATE, paramMaps);
        }
    }

    @Override
    public List<TeamApplicant> getUserApplicant(String uid) {
        String sql = "select * from vwb_team_applicant where uid=?";
        return this.getJdbcTemplate().query(sql, new Object[]{uid}, rowMapper);
    }

    @Override
    public List<TeamApplicantRender> getAcceptApplicantOfTeam(int tid) {
        String sql = "select a.*,b.name,b.orgnization from vwb_team_applicant a left join vwb_user_ext b on a.uid=b.uid " +
                "where tid=? and status='"+TeamApplicant.STATUS_ACCEPT+"'";
        return this.getJdbcTemplate().query(sql, new Object[]{tid}, renderRowMapper);
    }

    @Override
    public List<TeamApplicantRender> getWaitingApplicantOfTeam(int tid) {
        String sql = "select a.*,b.name,b.orgnization from vwb_team_applicant a left join vwb_user_ext b on a.uid=b.uid " +
                "where tid=? and status='"+TeamApplicant.STATUS_WAITING+"'";
        return this.getJdbcTemplate().query(sql, new Object[]{tid}, renderRowMapper);
    }

    @Override
    public List<TeamApplicantRender> getRejectApplicantOfTeam(int tid) {
        String sql = "select a.*,b.name,b.orgnization from vwb_team_applicant a left join vwb_user_ext b on a.uid=b.uid " +
                "where tid=? and status='"+TeamApplicant.STATUS_REJECT+"'";
        return this.getJdbcTemplate().query(sql, new Object[]{tid}, renderRowMapper);
    }

    @Override
    public void delete(int tid, String uid) {
        String sql = "delete from vwb_team_applicant where tid=? and uid=?";
        this.getJdbcTemplate().update(sql, new Object[]{tid, uid});
    }

    @Override
    public void batchDelete(final int tid, final String[] uids) {
        if(null == uids || uids.length<=0){
            return ;
        }
        String sql = "delete from vwb_team_applicant where tid=? and uid=?";
        this.getJdbcTemplate().batchUpdate(sql, new BatchPreparedStatementSetter(){

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

    @Override
    public TeamApplicant get(int tid, String uid) {
        String sql = "select * from vwb_team_applicant where tid=? and uid=?";
        List<TeamApplicant> list = this.getJdbcTemplate().query(sql, new Object[]{tid, uid}, rowMapper);
        if(null == list || list.isEmpty()){
            return null;
        }else if(list.size()>1){
            LOG.info("More than one object exist while query for teamApplicant by tid="+tid+" and uid="+uid);
        }
        return list.get(0);
    }

    @Override
    public List<TeamApplicantNoticeRender> getTeamApplicantNoticeInotKnow(String uid) {
        String sql = "select a.*,b.display_name from vwb_team_applicant a left join vwb_team b on a.tid=b.id " +
                "where uid=? and i_know='"+TeamApplicant.I_DIDNT_KNOW+"'";
        return this.getJdbcTemplate().query(sql, new Object[]{uid}, noticeRowMapper);
    }

    @Override
    public void iknowAllTeamApplicantNotice(String uid) {
        String sql = "update vwb_team_applicant set i_know = ? where uid=?";
        this.getJdbcTemplate().update(sql, new Object[]{TeamApplicant.I_KNOW, uid});
    }

    private Map<String, Object> generateParamMap(TeamApplicant ta){
        Map<String, Object> params = new HashMap<String, Object>();
        params.put("uid", ta.getUid());
        params.put("tid", ta.getTid());
        params.put("status", ta.getStatus());
        params.put("reason", ta.getReason());
        params.put("applyTime", new Timestamp((new Date()).getTime()));
        params.put("iKnow", ta.getiKnow());
        return params;
    }

    private class TeamApplicantRowMapper implements RowMapper<TeamApplicant>{

        @Override
        public TeamApplicant mapRow(ResultSet rs, int index)
                throws SQLException {
            TeamApplicant ta = new TeamApplicant();
            ta.setId(rs.getInt("id"));
            ta.setUid(rs.getString("uid"));
            ta.setTid(rs.getInt("tid"));
            ta.setStatus(rs.getString("status"));
            ta.setReason(rs.getString("reason"));
            ta.setApplyTime(new Date(rs.getTimestamp("apply_time").getTime()));
            ta.setiKnow(rs.getString("i_know"));
            return ta;
        }

    }

    private class TeamApplicantRenderRowMapper implements RowMapper<TeamApplicantRender>{

        @Override
        public TeamApplicantRender mapRow(ResultSet rs, int index)
                throws SQLException {
            TeamApplicantRender tar = new TeamApplicantRender();
            TeamApplicant ta = new TeamApplicant();
            ta.setId(rs.getInt("id"));
            ta.setUid(rs.getString("uid"));
            ta.setTid(rs.getInt("tid"));
            ta.setStatus(rs.getString("status"));
            ta.setReason(rs.getString("reason"));
            ta.setApplyTime(new Date(rs.getTimestamp("apply_time").getTime()));
            ta.setiKnow(rs.getString("i_know"));
            tar.setTeamApplicant(ta);
            tar.setDepartment(rs.getString("orgnization"));
            tar.setUserName(rs.getString("name"));
            return tar;
        }

    }

    private class TeamApplicantNoticeRenderRowMapper implements RowMapper<TeamApplicantNoticeRender>{

        @Override
        public TeamApplicantNoticeRender mapRow(ResultSet rs, int index)
                throws SQLException {
            TeamApplicantNoticeRender tanr = new TeamApplicantNoticeRender();
            TeamApplicant ta = new TeamApplicant();
            ta.setId(rs.getInt("id"));
            ta.setUid(rs.getString("uid"));
            ta.setTid(rs.getInt("tid"));
            ta.setStatus(rs.getString("status"));
            ta.setReason(rs.getString("reason"));
            ta.setApplyTime(new Date(rs.getTimestamp("apply_time").getTime()));
            ta.setiKnow(rs.getString("i_know"));
            tanr.setTeamApplicant(ta);
            tanr.setTeamName(rs.getString("display_name"));
            return tanr;
        }

    }
}
