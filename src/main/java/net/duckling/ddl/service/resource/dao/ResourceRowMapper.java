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

import com.google.gson.reflect.TypeToken;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.ResultSetMetaData;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;
import net.duckling.ddl.common.DBs;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import net.duckling.ddl.service.resource.Resource;
import net.duckling.ddl.util.JsonUtil;
import net.duckling.ddl.util.SQLObjectMapper;

import org.springframework.jdbc.core.RowMapper;

public class ResourceRowMapper implements RowMapper<Resource> {
    private final Logger log =
            LoggerFactory.getLogger(ResourceRowMapper.class);
    private String prefix;

    public ResourceRowMapper(String s) {
        this.prefix = DBs.getDbms().equals("mysql") ?
                s : "";
    }

    private Map<Integer,String> parseSerialize(String src){
        if(src==null || src.length()==0){
            return null;
        }
        return JsonUtil.readValue(
            src, new TypeToken<Map<Integer, String>>(){}.getType());
    }

    @SuppressWarnings("unchecked")
    @Override
    public Resource mapRow(ResultSet rs, int index) throws SQLException {
        Resource r = new Resource();

        // ResultSetMetaData md = rs.getMetaData();
        // for (int i=1; i <= md.getColumnCount(); i++) {
        //     log.error("{} name:{} label:{}", i, md.getColumnName(i),
        //               md.getColumnLabel(i));
        // }
        
        r.setRid(rs.getInt(prefix+"rid"));
        r.setTid(rs.getInt(prefix+"tid"));
        r.setItemType(rs.getString(prefix+"item_type"));
        r.setTitle(rs.getString(prefix+"title"));
        r.setCreateTime(rs.getTimestamp(prefix+"create_time"));
        r.setCreator(rs.getString(prefix+"creator"));
        r.setLastEditor(rs.getString(prefix+"last_editor"));
        r.setLastEditorName(rs.getString(prefix+"last_editor_name"));
        r.setLastEditTime(rs.getTimestamp(prefix+"last_edit_time"));
        r.setLastVersion(rs.getInt(prefix+"last_version"));
        r.setTagMap(parseSerialize(rs.getString(prefix+"tags")));
        r.setFileType(rs.getString(prefix+"file_type"));
        r.setStatus(rs.getString(prefix+"status"));
        r.setMarkedUserSet((Set<String>)SQLObjectMapper.writeObject(rs,prefix+"marked_users"));
        r.setBid(rs.getInt(prefix+"bid"));
        r.setOrderType(rs.getInt(prefix+"order_type"));
        r.setSize(rs.getLong(prefix+"size"));
        r.setShared(rs.getBoolean(prefix + "shared"));
        return r;
    }

}
