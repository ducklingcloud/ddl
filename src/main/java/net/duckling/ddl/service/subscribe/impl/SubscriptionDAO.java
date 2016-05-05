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
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import net.duckling.ddl.service.subscribe.NotifyPolicy;
import net.duckling.ddl.service.subscribe.Publisher;
import net.duckling.ddl.service.subscribe.Subscription;
import net.duckling.ddl.util.AbstractBaseDAO;

import org.springframework.jdbc.core.BatchPreparedStatementSetter;
import org.springframework.jdbc.core.PreparedStatementCreator;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.jdbc.support.GeneratedKeyHolder;
import org.springframework.stereotype.Repository;


/**
 * @date 2011-3-1
 * @author Clive Lee
 */
@Repository
public class SubscriptionDAO extends AbstractBaseDAO {
    
    private static final String DELETE_SQL = "delete from vwb_subscription where tid=? and user_id=? and publisher=? and publisher_type=? ";
    private static final String INSERT_SQL = "insert into vwb_subscription (user_id,publisher,publisher_type,policy, tid) values(?,?,?,?,?)";
    private static final String FIND_SUBSCRIBER_SQL = "select * from vwb_subscription where tid=? and publisher=? and publisher_type=? ";
    private static final String FIND_PAGE_SUBSCRIBER="select * from vwb_subscription where tid=? and publisher_type=? and publisher=?  order by publisher";
  
    
    public int delete(Subscription persistentInstance) {
        String sql="delete from vwb_subscription where id=:id";
        int id=persistentInstance.getId();
        Map<String, Integer> paramMap=new HashMap<String, Integer>();
        paramMap.put("id", id);
        return getNamedParameterJdbcTemplate().update(sql, paramMap);
    }
    
    
    public int save(final Subscription sub) {
		GeneratedKeyHolder keyHolder = new GeneratedKeyHolder();
		getJdbcTemplate().update(new PreparedStatementCreator() {
			public PreparedStatement createPreparedStatement(Connection conn)
					throws SQLException {
				PreparedStatement pst = conn.prepareStatement((INSERT_SQL), PreparedStatement.RETURN_GENERATED_KEYS);
				int i = 0;
				pst.setString(++i, sub.getUserId());
				pst.setInt(++i, sub.getPublisher().getId());
				pst.setString(++i, sub.getPublisher().getType());
				pst.setString(++i, sub.getNotifyPolicy().getPolicy());
				pst.setInt(++i, sub.getTid());
				return pst;
			}
		}, keyHolder);
		return(keyHolder.getKey()!=null)?keyHolder.getKey().intValue():-1;
	}
    

    public void batchSave(final Subscription[] subArray) {
        final int size = subArray.length;
        getJdbcTemplate().batchUpdate((INSERT_SQL),
                new BatchPreparedStatementSetter() {
                    public int getBatchSize() {
                        return size;
                    }
                    public void setValues(PreparedStatement pst, int index)
                            throws SQLException {
                        int i = 0;
                        pst.setString(++i, subArray[index].getUserId());
                        pst.setInt(++i, subArray[index].getPublisher().getId());
                        pst.setString(++i, subArray[index].getPublisher().getType());
                        pst.setString(++i, subArray[index].getNotifyPolicy().getPolicy());
                        pst.setInt(++i, subArray[index].getTid());
                    }
                });
    }
    
    public void delete(final Subscription[] subArray) {
        final int size = subArray.length;
        getJdbcTemplate().batchUpdate((DELETE_SQL),
                new BatchPreparedStatementSetter() {
                    public int getBatchSize() {
                        return size;
                    }
                    public void setValues(PreparedStatement pst, int index)
                            throws SQLException {
                        int i = 0;
                        pst.setInt(++i, subArray[index].getTid());
                        pst.setString(++i, subArray[index].getUserId());
                        pst.setInt(++i, subArray[index].getPublisher().getId());
                        pst.setString(++i, subArray[index].getPublisher().getType());
                      /*  pst.setInt(++i, subArray[index].getTid());*/
                    }
                });
    }
    
	public List<Subscription> getSubscriptionByUserId(int tid,String userId, String type) {
		if ("page".equals(type)) {
			return getJdbcTemplate().query(
					FIND_USER_PAGE_SUBSCRIPTION,new Object[] {tid,userId}, displayRowMapper);
		} else if ("person".equals(type)) {
			return  getJdbcTemplate().query(
					FIND_USER_PERSON_SUBSCRIPTION,new Object[] {tid,userId}, displayRowMapper);
		} else
		{
			return null;
		}
	}
    
    private static final String FIND_USER_PERSON_SUBSCRIPTION = 
        "select s.id,s.user_id,s.publisher,s.publisher_type,p.name as publisher_name,p.id as publisher_url " +
        " from vwb_subscription s,vwb_user_ext p " +
    	" where s.tid=? and s.user_id=? and s.publisher=p.id and s.publisher_type='person'";
    
    private static final String FIND_USER_PAGE_SUBSCRIPTION = 
    "select s.id,s.user_id,s.publisher,s.publisher_type,p.title as publisher_name,p.rid as publisher_url " +
    " from vwb_subscription s,a1_resource p " +
	" where s.tid=? and p.tid=s.tid and s.user_id=? and s.publisher=p.rid and s.publisher_type='page' and p.item_type='DPage'";
    
    private RowMapper<Subscription> displayRowMapper = new RowMapper<Subscription>() {
        public Subscription mapRow(ResultSet rs, int arg1) throws SQLException {
            Subscription instance = new Subscription();
            instance.setId(rs.getInt("id"));
            instance.setUserId(rs.getString("user_id"));
            Publisher publisher = new Publisher();
            publisher.setId(rs.getInt("publisher"));
            publisher.setType(rs.getString("publisher_type"));
            publisher.setName(rs.getString("publisher_name"));
            publisher.setRootPage(rs.getInt("publisher_url"));
            instance.setPublisher(publisher);
            return instance;
        }
    };

        
    private RowMapper<Subscription> rowMapper=new RowMapper<Subscription>(){
        public Subscription mapRow(ResultSet rs, int arg1) throws SQLException {
            Subscription instance = new Subscription();
            instance.setId(rs.getInt("id"));
            instance.setUserId(rs.getString("user_id"));
            Publisher publisher = new Publisher();
            publisher.setId(rs.getInt("publisher"));
            publisher.setType(rs.getString("publisher_type"));
            instance.setPublisher(publisher);
            NotifyPolicy policy = new NotifyPolicy();
            policy.setPolicy(rs.getString("policy"));
            instance.setNotifyPolicy(policy);
            return instance;
        }
    };
    
	public List<Subscription> findSubscriptions(int tid, Publisher publisher){
        return getJdbcTemplate().query( FIND_SUBSCRIBER_SQL, new Object[] {tid, publisher.getId(), publisher.getType() },
                rowMapper);
    }
	
    public Set<String> findSubscribers(int tid, int rid, String pageType) {
        Set<String> results = new HashSet<String>();
//        String newSQL = getSql();
        List<String> tempList = getJdbcTemplate().queryForList(
                FIND_SUBSCRIBER_SQL, new Object[] { tid, rid, pageType },
                String.class);
        if(tempList!=null)
            results.addAll(tempList);
        return results;
    }

    private static final String FIND_TEMA_MEMBER_FEED = "select u.uid from vwb_subscription s,vwb_user_ext u where s.user_id=? and s.tid=? and s.publisher_type='person' and s.publisher=u.id";
	public List<String> getTeamMemberFeed(String user, int tid) {
		return this.getJdbcTemplate().queryForList((FIND_TEMA_MEMBER_FEED),new Object[] {user,tid},String.class);
	}

	public boolean isFeedPerson(String user, int id) {
		String sql = "select count(id) from vwb_subscription s where s.user_id=? and s.tid=$tid and s.publisher=? and s.publisher_type='person'";
		int flag = getJdbcTemplate().queryForObject((sql),new Object[] {user,id}, Integer.class);
		return flag>0;
	}

	public List<Subscription> findPageSubscirption(int tid, int rid) {
    	return getJdbcTemplate().query(FIND_PAGE_SUBSCRIBER, new Object[]{tid, Publisher.PAGE_TYPE,rid}, rowMapper);
	}


	public void removePageSubscribe(int tid, int rid) {
		String sql = "delete from vwb_subscription where tid=? and (publisher_type='page' or publisher_type='my_create_page') and publisher=?";
		getJdbcTemplate().update(sql, new Object[]{tid,rid});
	}

	public void removeSubscriptionAboutPerson(int tid, String uid, int uxid) {
		String sql = "delete from vwb_subscription where tid=? and publisher=? and publisher_type='person'";
		getJdbcTemplate().update(sql,new Object[]{tid, uxid});
		String sql2 = "delete from vwb_subscription where tid=? and user_id=?";
		getJdbcTemplate().update(sql2,new Object[]{tid, uid});
	}

}
