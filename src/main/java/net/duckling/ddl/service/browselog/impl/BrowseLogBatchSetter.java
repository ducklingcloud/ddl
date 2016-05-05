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
package net.duckling.ddl.service.browselog.impl;

import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.sql.Timestamp;
import java.util.Collection;
import java.util.Iterator;

import net.duckling.ddl.service.browselog.BrowseLog;

import org.springframework.jdbc.core.BatchPreparedStatementSetter;

public class BrowseLogBatchSetter implements BatchPreparedStatementSetter {
	private Iterator<BrowseLog> iter;
	private int size;

	public BrowseLogBatchSetter(Collection<BrowseLog> list) {
		iter = list.iterator();
		size = list.size();
	}

	@Override
	public void setValues(PreparedStatement ps, int index) throws SQLException {
		int i = 0;
		BrowseLog browseLog = iter.next();
		ps.setInt(++i, browseLog.getTid());
		ps.setInt(++i, browseLog.getRid());
		ps.setString(++i, browseLog.getItemType());
		ps.setString(++i, browseLog.getUserId());
		ps.setString(++i, browseLog.getDisplayName());
		ps.setString(++i, browseLog.getTrackingId());
		ps.setTimestamp(++i, new Timestamp(browseLog.getBrowseTime().getTime()));
	}

	@Override
	public int getBatchSize() {
		return size;
	}
}
