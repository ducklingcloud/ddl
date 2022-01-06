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
package net.duckling.ddl.service.file.dao;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.List;

import net.duckling.ddl.service.file.DFileRef;
import net.duckling.ddl.service.file.impl.FileReferenceDAO;
import net.duckling.ddl.util.AbstractBaseDAO;

import org.springframework.jdbc.core.BatchPreparedStatementSetter;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.stereotype.Repository;
@Repository
public class FileReferenceDAOImpl extends AbstractBaseDAO implements FileReferenceDAO {
    private RowMapper<DFileRef> refMapper = new RowMapper<DFileRef>() {
            @Override
            public DFileRef mapRow(ResultSet rs, int rowNum) throws SQLException {
                DFileRef r = new DFileRef();
                r.setId(rs.getInt("id"));
                r.setTid(rs.getInt("tid"));
                r.setPageRid(rs.getInt("page_rid"));
                r.setFileRid(rs.getInt("file_rid"));
                r.setItemType(rs.getString("item_type"));
                r.setFileType(rs.getString("file_type"));
                return r;
            }
        };

    private static final String SELECT_PAGE_REF = "SELECT vdf.*,ar.item_type,ar.file_type FROM vwb_dfile_ref vdf inner join a1_resource ar" +
            " on ar.rid=vdf.file_rid WHERE vdf.page_rid=? and vdf.tid=?";

    @Override
    public List<DFileRef> getPageReferences(int pageRid, int tid) {
        return getJdbcTemplate().query(SELECT_PAGE_REF, new Object[] { pageRid,tid }, refMapper);
    }

    private static final String SELECT_FILE_REF = "SELECT vdf.*,ar.item_type,ar.file_type FROM vwb_dfile_ref vdf inner join a1_resource ar" +
            " on ar.rid=vdf.file_rid WHERE vdf.file_rid=? and vdf.tid=?";
    @Override
    public List<DFileRef> getFileReferences(int fileRid,int tid) {
        return getJdbcTemplate().query(SELECT_FILE_REF, new Object[] { fileRid,tid }, refMapper);
    }


    private static final String INSERT_DFILE_REF = "INSERT INTO vwb_dfile_ref(page_rid,file_rid,tid) VALUES(?,?,?)";
    @Override
    public void referTo(final DFileRef[] refArray) {
        getJdbcTemplate().batchUpdate(INSERT_DFILE_REF,
                                      new BatchPreparedStatementSetter() {
                                          public int getBatchSize() {
                                              return refArray.length;
                                          }

                                          public void setValues(PreparedStatement pst, int index)
                                                  throws SQLException {
                                              int i = 0;
                                              pst.setInt(++i, refArray[index].getPageRid());
                                              pst.setInt(++i, refArray[index].getFileRid());
                                              pst.setInt(++i, refArray[index].getTid());
                                          }
                                      });
    }


    @Override
    public void deletePageRefer(int pageRid,int tid) {
        String sql = "delete from vwb_dfile_ref where tid=? and page_rid=?";
        getJdbcTemplate().update(sql, new Object[]{tid,pageRid});
    }
    @Override
    public void deleteFileRefer(int fileRid, int tid) {
        String sql = "delete from vwb_dfile_ref where tid=? and file_rid=? ";
        getJdbcTemplate().update(sql,new Object[]{tid,fileRid});
    }
    @Override
    public void deleteFileAndPageRefer(int fileRid,int pageRid,int tid){
        String sql = "DELETE FROM vwb_dfile_ref WHERE file_rid=? and page_rid=? and tid=?";
        getJdbcTemplate().update(sql,new Object[]{fileRid,pageRid,tid});
    }



}
