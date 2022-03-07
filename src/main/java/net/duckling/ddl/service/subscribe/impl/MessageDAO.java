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

package net.duckling.ddl.service.subscribe.impl;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Timestamp;
import java.util.Date;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import net.duckling.ddl.service.subscribe.Message;
import net.duckling.ddl.service.subscribe.MessageBody;
import net.duckling.ddl.service.subscribe.Publisher;
import net.duckling.ddl.service.subscribe.Subscription;
import net.duckling.ddl.util.AbstractBaseDAO;

import org.springframework.jdbc.core.BatchPreparedStatementSetter;
import org.springframework.jdbc.core.PreparedStatementCreator;
import org.springframework.jdbc.core.ResultSetExtractor;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.jdbc.support.GeneratedKeyHolder;
import org.springframework.stereotype.Repository;


/**
 * 消息队列操作类
 *
 * @date 2011-3-1
 * @author Clive Lee
 */
@Repository
public class MessageDAO extends AbstractBaseDAO {
    private static class MessageMapper implements RowMapper<Message> {
        public Message mapRow(ResultSet rs, int index) throws SQLException {
            MessageBody body = new MessageBody();
            body.setId(rs.getInt("message_id"));
            body.setType(rs.getString("type"));
            body.setTitle(rs.getString("title"));
            body.setDigest(rs.getString("digest"));
            body.setRid(rs.getInt("rid"));
            body.setTime(rs.getTimestamp("time"));
            body.setFrom(rs.getString("from"));
            body.setRemark(rs.getString("remark"));

            Publisher publisher = new Publisher();
            publisher.setId(rs.getInt("publisher"));
            publisher.setType(rs.getString("publisher_type"));

            Message message = new Message();
            message.setId(rs.getInt("id"));
            message.setBody(body);
            message.setStatus(rs.getInt("status"));
            message.setUserId(rs.getString("user_id"));
            message.setPublisher(publisher);
            return message;
        }
    }
    private static final String ADD_MESSAGE_MAPPING = "insert into vwb_message(message_id, user_id, publisher, publisher_type, tid) values(?,?,?,?, ?)";
    private static final String COUNT_TYPE_NEW_MESSAGES = "select count(*) as message_count,t.publisher_type from (" +
            "select count(*),publisher_type from vwb_message v,vwb_message_body b where b.tid=? and v.message_id=b.id and v.status=0 and b.time>? and user_id=? group by rid,publisher_type ) t " +
            "group by t.publisher_type";
    private static final String DELETE_MESSAGE_BODY="delete from vwb_message_body where id=?";

    private static final String DELETE_USER_MESSAGE="delete from vwb_message where message_id=?";
    private static final String DELETE_PAGE_MESSAGE="delete from vwb_message where message_id in (select id from vwb_message_body where tid=? and rid=?)";

    private static final String DELETE_PAGE_MESSAGE_BODY="delete from vwb_message_body where tid=? and rid=?";

    private static final String INSERT_MESSAGE = "insert into vwb_message_body(type, title, digest, rid, time, \"from\", remark, tid) values(?,?,?,?,?,?,?, ?)";

    private static final String QUERY_MESSAGE_IN_DURATION = "select m.*, b.* from vwb_message m" +
            " inner join vwb_message_body b on b.tid=? and m.message_id=b.id" +
            " where m.user_id=? and b.time>=? and b.time<? order by b.time desc";

    private static final String UPDATE_PERSON_STATUS="update vwb_message v1 set status=1 where publisher=? and publisher_type=? and user_id=? and v1.tid=?";

    private static final String UPDATE_STATUS = "update vwb_message v1 set status=1  where " +
            "v1.message_id in (select v2.id from vwb_message_body v2 where v2.rid=? and v2.tid=?) " +
            "and v1.status=0 and v1.publisher_type=? and v1.user_id=?";

    private static final String UPDATE_TYPE_MESSAGES_STATUS = "update vwb_message set status=1 where user_id=? and publisher_type=? and tid=?";

    private int addToMessageTable(final int tid, final MessageBody message) {
        GeneratedKeyHolder keyHolder = new GeneratedKeyHolder();
        getJdbcTemplate().update(new PreparedStatementCreator() {
                public PreparedStatement createPreparedStatement(Connection conn)
                        throws SQLException {
                    PreparedStatement pst = conn.prepareStatement(INSERT_MESSAGE, PreparedStatement.RETURN_GENERATED_KEYS);
                    int i = 0;
                    pst.setString(++i, message.getType());
                    pst.setString(++i, message.getTitle());
                    pst.setString(++i, message.getDigest());
                    pst.setInt(++i, message.getRid());
                    pst.setTimestamp(++i, new Timestamp(message.getTime().getTime()));
                    pst.setString(++i, message.getFrom());
                    pst.setString(++i, message.getRemark());
                    pst.setInt(++i, tid);
                    return pst;
                }
            }, keyHolder);

        Number key = keyHolder.getKey();
        if (key != null) {
            return key.intValue();
        } else {
            return -1;
        }
    }


    public Map<String, Integer> countNewMessages(int tid,String user,Date start) {
        return getJdbcTemplate().query(
            COUNT_TYPE_NEW_MESSAGES,
            new Object[] { tid, new Timestamp(start.getTime()), user },
            new ResultSetExtractor<Map<String, Integer>>() {
                public Map<String, Integer> extractData(ResultSet rs) throws SQLException {
                    Map<String, Integer> map = new HashMap<String, Integer>();
                    while (rs.next()) {
                        String key = rs.getString("publisher_type");
                        int value = rs.getInt("message_count");
                        map.put(key, value);
                    }
                    return map;
                };
            });
    }

    /**
     * @param messageBody
     * @param subMap
     */
    public int createMessage(final int tid, final MessageBody message,final List<Subscription> subscriptions) {
        final int messageId = addToMessageTable(tid,message);
        if (messageId != -1) {
            getJdbcTemplate().batchUpdate(ADD_MESSAGE_MAPPING,
                                          new BatchPreparedStatementSetter() {
                                              private Iterator<Subscription> iter=subscriptions.iterator();
                                              public int getBatchSize() {
                                                  return subscriptions.size();
                                              }

                                              public void setValues(PreparedStatement pst, int index)
                                                      throws SQLException {
                                                  Subscription sub = iter.next();
                                                  int i = 0;
                                                  pst.setInt(++i, messageId);
                                                  pst.setString(++i, sub.getUserId());
                                                  pst.setInt(++i, sub.getPublisher().getId());
                                                  pst.setString(++i, sub.getPublisher().getType());
                                                  pst.setInt(++i, tid);
                                              }

                                          });
        }
        return messageId;
    }

    /**
     * 创建消息
     *
     * @param message
     *            消息内容
     * @param publisher
     *            发布者
     * @param userids
     *            用户名列表
     * @return 新创建的消息的ID
     */
    public int createMessage(final int tid, MessageBody message, final Publisher publisher,
                             final String[] userids) {
        final int messageId = addToMessageTable(tid,message);
        if (messageId != -1) {
            getJdbcTemplate().batchUpdate(ADD_MESSAGE_MAPPING,
                                          new BatchPreparedStatementSetter() {
                                              public int getBatchSize() {
                                                  return userids.length;
                                              }

                                              public void setValues(PreparedStatement pst, int index)
                                                      throws SQLException {
                                                  int i = 0;
                                                  pst.setInt(++i, messageId);
                                                  pst.setString(++i, userids[index]);
                                                  pst.setInt(++i, publisher.getId());
                                                  pst.setString(++i, publisher.getType());
                                                  pst.setInt(++i, tid);
                                              }

                                          });
        }
        return messageId;
    }
    /**
     * 查询用户在某一时间段内的消息
     * @param userid 用户名
     * @param start 时间段的开始时间
     * @param end 时间段的结束时间
     * @return 所有这段时间内用户接收到的消息<br>
     *        时间段的定义 [start, end)<br>
     *        如果没有合适的消息，返回的是null
     */
    public List<Message> getMessage(int tid, String userid, Date start, Date end) {
        return getJdbcTemplate().query(
            QUERY_MESSAGE_IN_DURATION,
            new Object[] {tid, userid, start, end }, new MessageMapper());
    }
    public void removeMessage(int messageId) {
        getJdbcTemplate().update(DELETE_USER_MESSAGE,new Object[]{messageId});
        getJdbcTemplate().update(DELETE_MESSAGE_BODY,new Object[]{messageId});
    }
    public void removePagemessages(int tid, int pageId) {
        getJdbcTemplate().update(DELETE_PAGE_MESSAGE, new Object[]{tid, pageId});
        getJdbcTemplate().update(DELETE_PAGE_MESSAGE_BODY, new Object[]{tid, pageId});
    }

    /**
     * 更新用户阅读消息的状态
     * @param userId 用户ID（Email格式）
     * @param messageId 消息的ID
     * @param status 新的状态
     */
    public void updateStatus(int tid, String userId, int pageId,String type) {
        if (Publisher.PERSON_TYPE.equals(type)){
            getJdbcTemplate().update(UPDATE_PERSON_STATUS,
                                     new Object[] { pageId,type,userId, tid});
        }else{
            getJdbcTemplate().update(UPDATE_STATUS,
                                     new Object[] { pageId,tid, type,userId });
        }
    }

    public void updateTypeMessageStatus(String user,String type,int tid) {
        this.getJdbcTemplate().update(UPDATE_TYPE_MESSAGES_STATUS,new Object[] {user,type,tid});
    }
}
