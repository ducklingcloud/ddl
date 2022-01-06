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
package net.duckling.ddl.service.sync.dao.jdbc;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import net.duckling.ddl.constant.LynxConstants;
import net.duckling.ddl.service.sync.dao.ResourceDao;
import net.duckling.ddl.service.sync.domain.resource.DFile;
import net.duckling.ddl.service.sync.domain.resource.Resource;
import net.duckling.ddl.util.AbstractBaseDAO;
import net.duckling.ddl.util.JsonUtil;

import org.springframework.jdbc.core.RowMapper;
import org.springframework.stereotype.Repository;

import com.fasterxml.jackson.core.type.TypeReference;

@Repository
public class ResourceDaoImpl extends AbstractBaseDAO implements ResourceDao {

    @Override
    public List<Resource> getDescendants(int tid, int folderRid, String itemType) {
        String select = "select r.* from a1_resource r,ddl_folder_path p where p.tid =? and p.tid=r.tid and (r.status='"
                + LynxConstants.STATUS_AVAILABLE + "' ) and" + " r.rid=p.rid and p.ancestor_rid=?";
        List<Object> params = new ArrayList<Object>();
        params.add(tid);
        params.add(folderRid);

        if (itemType != null) {
            select += " and (r.item_type = ? or r.item_type ='" + Resource.ItemType.Folder + "')";
            params.add(itemType);
        }
        select += " order by p.length desc";

        return getJdbcTemplate().query(select, params.toArray(), rowMapper);
    }

    @Override
    public List<DFile> getDescendantsChecksum(int tid, int folderRid) {
        String select = "select r.rid,v.checksum from a1_resource r,ddl_folder_path p,a1_file_version v where p.tid =? and p.tid=r.tid and (r.status='"
                + LynxConstants.STATUS_AVAILABLE
                + "' ) and"
                + " r.rid=p.rid and p.ancestor_rid=? and r.item_type='"
                + Resource.ItemType.DFile
                + "' and v.rid = r.rid and"
                + " v.version = (select max(v2.version) from a1_file_version v2 where v2.rid=r.rid)";
        List<Object> params = new ArrayList<Object>();
        params.add(tid);
        params.add(folderRid);
        return getJdbcTemplate().query(select, params.toArray(), rowMapperDFile);
    }

    private RowMapper<DFile> rowMapperDFile = new RowMapper<DFile>() {
            @Override
            public DFile mapRow(ResultSet rs, int index) throws SQLException {
                DFile f = new DFile();
                f.setRid(rs.getInt("r.rid"));
                f.setChecksum(rs.getString("v.checksum"));
                return f;
            }
        };

    private RowMapper<Resource> rowMapper = new RowMapper<Resource>() {
            @Override
            public Resource mapRow(ResultSet rs, int index) throws SQLException {
                String prefix = "r.";
                Resource r = new Resource();
                r.setRid(rs.getInt(prefix + "rid"));
                r.setTid(rs.getInt(prefix + "tid"));
                r.setItemType(rs.getString(prefix + "item_type"));
                r.setTitle(rs.getString(prefix + "title"));
                r.setCreateTime(rs.getTimestamp(prefix + "create_time"));
                r.setCreator(rs.getString(prefix + "creator"));
                r.setLastEditor(rs.getString(prefix + "last_editor"));
                r.setLastEditorName(rs.getString(prefix + "last_editor_name"));
                r.setLastEditTime(rs.getTimestamp(prefix + "last_edit_time"));
                r.setVersion(rs.getInt(prefix + "last_version"));
                r.setTags(JsonUtil.readValue(rs.getString(prefix + "tags"),
                                             new TypeReference<HashMap<Integer, String>>(){}));
                r.setFileType(rs.getString(prefix + "file_type"));
                r.setStatus(rs.getString(prefix + "status"));
                r.setBid(rs.getInt(prefix + "bid"));
                r.setSize(rs.getLong(prefix + "size"));
                return r;
            }
        };

}
