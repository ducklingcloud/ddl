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
package net.duckling.ddl.util;

import net.duckling.common.DucklingProperties;
import net.duckling.common.db.DbmsCompat;
import net.duckling.common.db.DbmsCompat.DbmsDialect;
import net.duckling.ddl.common.DBs;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate;

public abstract class AbstractBaseDAO {
    public static final String LIMIT_1 =
            DbmsCompat.getClause(DbmsDialect.LIMIT_1, DBs.getDbms());
    public static final String TOP_N =
            DbmsCompat.getClause(DbmsDialect.TOP_TEN, DBs.getDbms());
    public static final String LIMIT_OFFSET =
            DbmsCompat.getClause(DbmsDialect.LIMIT_OFFSET, DBs.getDbms());

    @Autowired
    private DucklingProperties config;
    @Autowired
    private JdbcTemplate jdbcTemplate;
    @Autowired
    private NamedParameterJdbcTemplate namedParameterJdbcTemplate;

    /**
     * @return the jdbcTemplate
     */
    protected JdbcTemplate getJdbcTemplate() {
        return jdbcTemplate;
    }

    /**
     * @param jdbcTemplate the jdbcTemplate to set
     */
    @Autowired
    @Qualifier("jdbcTemplate")
    public void setJdbcTemplate(JdbcTemplate jdbcTemplate) {
        this.jdbcTemplate = jdbcTemplate;
    }

    /**
     * @return the namedParameterJdbcTemplate
     */
    protected NamedParameterJdbcTemplate getNamedParameterJdbcTemplate() {
        return namedParameterJdbcTemplate;
    }

    /**
     * @param namedParameterJdbcTemplate the namedParameterJdbcTemplate to set
     */
    @Autowired
    @Qualifier("namedParameterJdbcTemplate")
    public void setNamedParameterJdbcTemplate(
        NamedParameterJdbcTemplate namedParameterJdbcTemplate) {
        this.namedParameterJdbcTemplate = namedParameterJdbcTemplate;
    }

}
