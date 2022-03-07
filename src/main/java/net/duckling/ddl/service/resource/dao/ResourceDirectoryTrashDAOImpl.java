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

import java.io.IOException;
import java.io.ObjectInputStream;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Timestamp;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import net.duckling.ddl.service.resource.ResourceDirectoryTrash;
import net.duckling.ddl.service.resource.ResourceDirectoryTree;
import net.duckling.ddl.util.AbstractBaseDAO;

import org.springframework.jdbc.core.PreparedStatementSetter;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.stereotype.Repository;

@Repository
public class ResourceDirectoryTrashDAOImpl extends AbstractBaseDAO implements ResourceDirectoryTrashDAO {

    private static final String CREATE_SQL = "insert into ddl_resource_dir_trash (rid,tid,uid,delete_date,directory) values(?,?,?,?,?)";
    private static final String QUERY_SQL = "select * from ddl_resource_dir_trash where rid=?";
    private static final String DELETE_SQL = "delete from ddl_resource_dir_trash where rid=?";
    private RowMapper<ResourceDirectoryTrash> rowMapper = new RowMapper<ResourceDirectoryTrash>(){

            @Override
            public ResourceDirectoryTrash mapRow(ResultSet rs, int rowNum) throws SQLException {
                ResourceDirectoryTrash trash = new ResourceDirectoryTrash();
                trash.setId(rs.getInt("id"));
                trash.setRid(rs.getInt("rid"));
                trash.setUid(rs.getString("uid"));
                trash.setTid(rs.getInt("tid"));
                trash.setDeleteDate(rs.getDate("delete_date"));
                ObjectInputStream in;
                Object o = null;
                try {
                    in = new ObjectInputStream(rs.getBinaryStream("directory"));
                    o = in.readObject();
                } catch (IOException e) {
                    e.printStackTrace();
                } catch (ClassNotFoundException e) {
                    e.printStackTrace();
                }
                if(o!=null){
                    Map<String,ResourceDirectoryTree> dir = (Map<String,ResourceDirectoryTree>)o;
                    trash.setAncestors(dir.get("ancestors"));
                    trash.setDescendants(dir.get("descendants"));
                }
                return trash;
            }

        };

    @Override
    public void create(final ResourceDirectoryTrash trash) {
        final Map<String,ResourceDirectoryTree> directory = new HashMap<String,ResourceDirectoryTree>();
        directory.put("ancestors", trash.getAncestors());
        directory.put("descendants", trash.getDescendants());
        getJdbcTemplate().update(CREATE_SQL,new PreparedStatementSetter(){
                @Override
                public void setValues(PreparedStatement ps) throws SQLException {
                    int i = 0;
                    ps.setInt(++i, trash.getRid());
                    ps.setInt(++i, trash.getTid());
                    ps.setString(++i, trash.getUid());
                    ps.setTimestamp(++i, new Timestamp(trash.getDeleteDate().getTime()));
                    ps.setObject(++i, directory);
                }
            });
    }

    @Override
    public ResourceDirectoryTrash getResoourceTrash(int rid) {
        List<ResourceDirectoryTrash> rs = getJdbcTemplate().query(QUERY_SQL,new Object[]{rid} ,rowMapper);
        if(rs!=null&&!rs.isEmpty()){
            return rs.get(0);
        }
        return null;
    }

    @Override
    public void deleteResourceTrash(int rid) {
        getJdbcTemplate().update(DELETE_SQL, rid);
    }

}
