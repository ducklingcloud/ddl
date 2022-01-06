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

package net.duckling.ddl.service.comment.impl;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Timestamp;
import java.util.List;

import net.duckling.ddl.constant.LynxConstants;
import net.duckling.ddl.service.comment.Comment;
import net.duckling.ddl.service.user.SimpleUser;
import net.duckling.ddl.util.AbstractBaseDAO;

import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.jdbc.core.PreparedStatementCreator;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.jdbc.support.GeneratedKeyHolder;
import org.springframework.stereotype.Repository;


/**
 * @date Mar 17, 2011
 * @author xiejj@cnic.cn
 */
@Repository
public class CommentDAO extends AbstractBaseDAO {

    private static class CommentRowMapper implements RowMapper<Comment> {
        public Comment mapRow(ResultSet rs, int index) throws SQLException {
            Comment comment = new Comment();
            comment.setId(rs.getInt("c_id"));
            SimpleUser sender = new SimpleUser();
            sender.setId(rs.getInt("s_id"));
            sender.setUid(rs.getString("s_uid"));
            sender.setEmail(rs.getString("s_email"));
            sender.setName(rs.getString("s_name"));
            comment.setSender(sender);
            SimpleUser receiver = new SimpleUser();
            receiver.setId(rs.getInt("r_id"));
            receiver.setUid(rs.getString("r_uid"));
            receiver.setEmail(rs.getString("r_email"));
            receiver.setName(rs.getString("r_name"));
            comment.setReceiver(receiver);
            comment.setRid(rs.getInt("rid"));
            comment.setItemType(rs.getString("item_type"));
            comment.setCreateTime(rs.getTimestamp("create_time"));
            comment.setContent(rs.getString("content"));
            comment.setTid(rs.getInt("c_tid"));
            return comment;
        }
    }

    private static final String CLEAN_COMMENT = "delete from vwb_comment where tid=?";
    //modify by lvly@ 2012-07-20
    private static final String DELETE_PAGE_COMMENT="update  vwb_comment set status='"+LynxConstants.STATUS_DELETE+"' where tid=? and rid=? and item_type=?";
    private static final String DELETE_COMMENT = "delete from vwb_comment where tid=? and id=?";

    private static final String INSERT_COMMENT = "insert into vwb_comment(tid,item_type, create_time, sender, receiver, content,rid) values(?,?,?,?,?,?,?)";

    private static final String QUERY_PAGE_COMMENTS =
            "SELECT c.rid as rid,c.id as c_id,c.content,c.create_time,c.item_type,c.tid as c_tid," +
            "s.id as s_id,s.uid as s_uid,s.email as s_email,s.name as s_name,s.id as s_uxid," +
            "r.id as r_id,r.uid as r_uid,r.email as r_email,r.name as r_name,r.id as r_uxid "+
            "FROM vwb_comment c left join vwb_user_ext s on c.sender = s.uid " +
            "left join vwb_user_ext r on c.receiver=r.uid " +
            "WHERE c.tid=? and c.rid=? and c.item_type=?";

    private static final String QUEYR_COMMENT =
            "SELECT c.rid as rid,c.id as c_id,c.content,c.create_time,c.item_type,c.tid as c_tid," +
            "s.id as s_id,s.uid as s_uid,s.email as s_email,s.name as s_name,s.id as s_uxid," +
            "r.id as r_id,r.uid as r_uid,r.email as r_email,r.name as r_name,r.id as r_uxid "+
            "FROM vwb_comment c left join vwb_user_ext s on c.sender = s.uid " +
            "left join vwb_user_ext r on c.receiver=r.uid " +
            "WHERE c.tid=? and c.id=?";

    private static final String QUERY_LATEST_COMMENTS =
            "SELECT c.rid as rid, c.id as c_id,c.content,c.create_time,c.item_type,c.tid as c_tid," +
            "s.id as s_id,s.uid as s_uid,s.email as s_email,s.name as s_name,s.id as s_uxid," +
            "r.id as r_id,r.uid as r_uid,r.email as r_email,r.name as r_name,r.id as r_uxid "+
            "FROM vwb_comment c left join vwb_user_ext s on c.sender = s.uid " +
            "left join vwb_user_ext r on c.receiver=r.uid " +
            "WHERE c.tid=? and c.rid=? and c.item_type=? ORDER BY create_time desc limit ?";

    private static final String COUNT_COMMENT = "SELECT COUNT(id) FROM vwb_comment where tid=? and rid=? and item_type=? ";

    public void clean(int tid) {
        getJdbcTemplate().update(CLEAN_COMMENT,new Object[]{tid});
    }

    public int createComment(final Comment comment) {
        GeneratedKeyHolder keyHolder = new GeneratedKeyHolder();
        getJdbcTemplate().update(new PreparedStatementCreator() {
                public PreparedStatement createPreparedStatement(Connection conn)
                        throws SQLException {
                    PreparedStatement pst = conn.prepareStatement(
                        INSERT_COMMENT,
                        PreparedStatement.RETURN_GENERATED_KEYS);
                    int i = 0;
                    pst.setInt(++i, comment.getTid());
                    pst.setString(++i, comment.getItemType());
                    pst.setTimestamp(++i, new Timestamp(comment.getCreateTime().getTime()));
                    pst.setString(++i, comment.getSender().getUid());
                    pst.setString(++i, comment.getReceiver().getUid());
                    pst.setString(++i, comment.getContent());
                    pst.setInt(++i, comment.getRid());
                    return pst;
                }
            }, keyHolder);
        return keyHolder.getKey().intValue();
    }

    public Comment getComment(int tid,int commentId) {
        try {
            return  getJdbcTemplate().queryForObject(QUEYR_COMMENT, new Object[] { tid,commentId }, new CommentRowMapper());
        } catch (EmptyResultDataAccessException e) {
            return null;
        }
    }

    public List<Comment> getPageComments(int tid,int rid,String itemType) {
        try {
            return getJdbcTemplate().query(QUERY_PAGE_COMMENTS, new Object[] {tid, rid,itemType }, new CommentRowMapper());
        } catch (EmptyResultDataAccessException e) {
            return null;
        }
    }

    public void removeComment(int tid,int commentId) {
        getJdbcTemplate().update(DELETE_COMMENT, new Object[] { tid,commentId });
    }
    /**删除评论，改为更新状态位
     * @author lvly
     * @since 2012-07-20
     * @param rid pageID
     * @param itemType LynxConstants.TYPE_XXX
     * */
    public void removePageComment(int tid,int rid,String itemType){
        getJdbcTemplate().update(DELETE_PAGE_COMMENT, new Object[]{tid,rid,itemType});
    }
    public int getPageCommentCount(int tid,int rid,String itemType) {
        return getJdbcTemplate().queryForObject(COUNT_COMMENT,new Object[] {tid, rid,itemType }, Integer.class);
    }

    public List<Comment> getPageBriefComments(int tid,int rid,String itemType, int pageSize) {
        return getJdbcTemplate().query(QUERY_LATEST_COMMENTS, new Object[] {tid, rid,itemType, pageSize }, new CommentRowMapper());
    }
}
