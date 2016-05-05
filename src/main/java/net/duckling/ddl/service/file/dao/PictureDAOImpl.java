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
/**
 * 
 */
package net.duckling.ddl.service.file.dao;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Timestamp;
import java.util.List;

import net.duckling.ddl.service.file.Picture;
import net.duckling.ddl.service.file.impl.PictureDAO;
import net.duckling.ddl.util.AbstractBaseDAO;
import net.duckling.ddl.util.CommonUtils;

import org.springframework.jdbc.core.PreparedStatementCreator;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.jdbc.support.GeneratedKeyHolder;
import org.springframework.stereotype.Repository;


/**
 * @author lvly
 * @since 2012-11-20
 */
@Repository
public class PictureDAOImpl extends AbstractBaseDAO implements PictureDAO{
	public static final String SQL_CREATE="insert into a1_picture(file_clb_version,file_clb_id,width,height,clb_id,create_time) values(?,?,?,?,?,?)";
	public static final String SQL_SELECT="select * from a1_picture where  file_clb_id=? and file_clb_version=? ";
	@Override
	public int addPicture(final Picture pic) {
			GeneratedKeyHolder keyHolder = new GeneratedKeyHolder();
			getJdbcTemplate().update((new PreparedStatementCreator() {
				public PreparedStatement createPreparedStatement(Connection conn)
						throws SQLException {
					PreparedStatement pst = null;
					pst = conn.prepareStatement(SQL_CREATE,
							PreparedStatement.RETURN_GENERATED_KEYS);
					int i=0;
					pst.setInt(++i, pic.getFileClbVersion());
					pst.setInt(++i, pic.getFileClbId());
					pst.setInt(++i, pic.getWidth());
					pst.setInt(++i, pic.getHeight());
					pst.setInt(++i, pic.getClbId());
					pst.setTimestamp(++i, new Timestamp(System.currentTimeMillis()));
					return pst;
				}
			}), keyHolder);
			return keyHolder.getKey().intValue();
	}
	@Override
	public Picture getPicture(int clbId, int clbVersion) {
		List<Picture> pics=getJdbcTemplate().query(SQL_SELECT, new Object[]{clbId,clbVersion},rowMapper);
		return CommonUtils.first(pics);
	}
	private RowMapper<Picture> rowMapper = new RowMapper<Picture>() {
		public Picture mapRow(ResultSet rs, int index) throws SQLException {
			Picture pic = new Picture();
			pic.setId(rs.getInt("id"));
			pic.setCreateTime(rs.getTimestamp("create_time"));
			pic.setFileClbId(rs.getInt("file_clb_id"));
			pic.setClbId(rs.getInt("clb_id"));
			pic.setFileClbVersion(rs.getInt("file_clb_version"));
			pic.setHeight(rs.getInt("height"));
			pic.setWidth(rs.getInt("width"));
			return pic;
		}
	};
}
