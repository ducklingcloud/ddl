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
package net.duckling.ddl.service.itemtypemap.dao;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.List;

import net.duckling.ddl.service.itemtypemap.ItemTypemapping;
import net.duckling.ddl.service.itemtypemap.impl.ItemTypeMappingDAO;
import net.duckling.ddl.util.AbstractBaseDAO;

import org.springframework.jdbc.core.RowMapper;
import org.springframework.stereotype.Repository;
@Repository
public class ItemTypeMappingDAOImpl extends AbstractBaseDAO implements ItemTypeMappingDAO {
	
	private RowMapper<ItemTypemapping> itemMapper = new RowMapper<ItemTypemapping>(){
		@Override
		public ItemTypemapping mapRow(ResultSet rs, int index) throws SQLException {
			ItemTypemapping item = new ItemTypemapping();
			item.setItemId(rs.getInt("item_id"));
			item.setItemType(rs.getString("item_type"));
			item.setRid(rs.getInt("rid"));
			item.setTid(rs.getInt("tid"));
			return item;
		}
		
	};
	@Override
	public ItemTypemapping getItemTypeMapping(int tid,int itemId, String itemType) {
		String sql = "select * from ddl_item_mapping where tid=? and item_id=? and item_type=?";
		List<ItemTypemapping> r = getJdbcTemplate().query(sql, new Object[]{tid,itemId,itemType}, itemMapper);
		if(r!=null&&r.size()>0){
			return r.get(0);
		}
		return null;
	}
}
