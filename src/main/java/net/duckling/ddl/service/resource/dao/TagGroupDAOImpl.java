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
package net.duckling.ddl.service.resource.dao;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.List;

import net.duckling.ddl.service.resource.TagGroup;
import net.duckling.ddl.service.resource.impl.TagGroupDAO;
import net.duckling.ddl.util.AbstractBaseDAO;
import net.duckling.ddl.util.CommonUtil;

import org.apache.log4j.Logger;
import org.springframework.jdbc.core.BatchPreparedStatementSetter;
import org.springframework.jdbc.core.PreparedStatementCreator;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.jdbc.support.GeneratedKeyHolder;
import org.springframework.stereotype.Repository;


@Repository
public class TagGroupDAOImpl extends AbstractBaseDAO implements TagGroupDAO {

    private static final Logger LOG = Logger.getLogger(TagGroupDAOImpl.class);

    private static final String SQL_CREATE = "insert into a1_tag_group(tid,title,creator,sequence) values(?,?,?,?)";
    private static final String SQL_DELETE = "delete from a1_tag_group where id=?";
    private static final String SQL_UPDATE = "update a1_tag_group set tid=?, title=?," +
            "creator=?, sequence=? where id=?";
    private static final String SQL_QUERY_ALLTAGGROUP_TEAM = "select * from a1_tag_group where tid=? order by sequence ASC";
    private static final String SQL_QUERY_TAGGROUP_BYID = "select * from a1_tag_group where id=?";

    private RowMapper<TagGroup> tagGroupRowMapper = new RowMapper<TagGroup>(){

            @Override
            public TagGroup mapRow(ResultSet rs, int index) throws SQLException {
                TagGroup tagGroup = new TagGroup();
                tagGroup.setId(rs.getInt("id"));
                tagGroup.setTid(rs.getInt("tid"));
                tagGroup.setTitle(rs.getString("title"));
                tagGroup.setCreator(rs.getString("creator"));
                tagGroup.setSequence(rs.getInt("sequence"));
                return tagGroup;
            }

        };

    @Override
    public int create(final TagGroup tagGroup) {
        GeneratedKeyHolder keyHolder = new GeneratedKeyHolder();
        this.getJdbcTemplate().update(new PreparedStatementCreator(){

                @Override
                public PreparedStatement createPreparedStatement(Connection conn)
                        throws SQLException {
                    PreparedStatement ps = null;
                    ps = conn.prepareStatement(SQL_CREATE, PreparedStatement.RETURN_GENERATED_KEYS);
                    int i = 0;
                    ps.setInt(++i, tagGroup.getTid());
                    ps.setString(++i, tagGroup.getTitle());
                    ps.setString(++i, tagGroup.getCreator());
                    ps.setInt(++i, tagGroup.getSequence());
                    return ps;
                }

            }, keyHolder);
        Number key = keyHolder.getKey();
        int id = (key==null)?-1:key.intValue();
        tagGroup.setId(id);
        return id;
    }

    @Override
    public int delete(int id) {
        return this.getJdbcTemplate().update(SQL_DELETE, new Object[]{id});
    }

    @Override
    public int update(int id, TagGroup tagGroup) {
        return this.getJdbcTemplate().update(SQL_UPDATE, new Object[]{
                tagGroup.getTid(), tagGroup.getTitle(), tagGroup.getCreator(),
                tagGroup.getSequence(), id
            });
    }

    @Override
    public TagGroup getTagGroupById(int id) {
        List<TagGroup> list = this.getJdbcTemplate().query(SQL_QUERY_TAGGROUP_BYID,
                                                           new Object[]{id}, tagGroupRowMapper);
        if(null==list || list.size()<=0)
        {
            return null;
        }
        else if(list.size()>1){
            LOG.error("there exist more than one object while quering for TagGroup " +
                      "by id = "+id);
        }
        return list.get(0);
    }

    @Override
    public List<TagGroup> getAllTagGroupByTid(int tid) {
        return this.getJdbcTemplate().query(SQL_QUERY_ALLTAGGROUP_TEAM,
                                            new Object[]{tid}, tagGroupRowMapper);
    }

    @Override
    public int getTagGroupTitleCount(int tid, String title) {
        String sql = "select count(*) from a1_tag_group where tid=? and title=?";
        return this.getJdbcTemplate().queryForObject(sql, new Object[]{tid, title}, Integer.class);
    }
    @Override
    public TagGroup getTagGroupLikeTitle(int tid,String title){
        String sql = "select * from a1_tag_group where tid=? and title like ?";
        return CommonUtil.first(this.getJdbcTemplate().query(sql, new Object[]{tid, "%"+title+"%"},tagGroupRowMapper));
    }


    private static final String UPDATE_SEQUENCE = "update a1_tag_group set sequence=? where id=?";
    @Override
    public void updateSequence(final Integer[] tgids) {
        this.getJdbcTemplate().batchUpdate(UPDATE_SEQUENCE, new BatchPreparedStatementSetter(){
                @Override
                public int getBatchSize() {
                    return tgids.length;
                }
                @Override
                public void setValues(PreparedStatement ps, int index)
                        throws SQLException {
                    int i = 0;
                    ps.setInt(++i, index);
                    ps.setInt(++i, tgids[index]);
                }

            });
    }

}
