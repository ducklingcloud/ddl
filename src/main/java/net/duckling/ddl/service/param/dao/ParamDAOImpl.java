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
package net.duckling.ddl.service.param.dao;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.List;

import net.duckling.ddl.service.param.Param;
import net.duckling.ddl.service.param.impl.ParamDAO;
import net.duckling.ddl.util.AbstractBaseDAO;
import net.duckling.ddl.util.CommonUtil;

import org.apache.commons.lang.StringUtils;
import org.springframework.jdbc.core.PreparedStatementSetter;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.stereotype.Repository;


/**系统参数的JDBC实现*/

@Repository
public class ParamDAOImpl extends AbstractBaseDAO implements ParamDAO{
    public static final String INSERT_PARAM="insert into a1_param(`item_id`,`key`,`value`,`type`) values(?,?,?,?)";
    public static final String UPDATE_PARAM="update a1_param set `item_id`=?,`key`=?,`value`=?,`type`=? where `id`=?";
    public static final String SELECT_PARAM="select * from a1_param where 1=1";

    public static final String BY_TYPE=" and `type`=?";
    public static final String BY_KEY=" and `key`=?";
    public static final String BY_VALUE=" and `value`=?";
    public static final String BY_ITEM_ID=" and `item_id`=?";
    public static final String BY_ID=" and `id`=?";

    //rowMapper BEGIN
    private RowMapper<Param> paramMapper = new RowMapper<Param>() {
            public Param mapRow(ResultSet rs, int index) throws SQLException {
                Param param = new Param();
                param.setId(rs.getInt("id"));
                param.setItemId(rs.getString("item_id"));
                param.setKey(rs.getString("key"));
                param.setType(rs.getString("type"));
                param.setValue(rs.getString("value"));
                return param;
            }
        };
    //END

    @Override
    public void create(final Param param) {
        getJdbcTemplate().update(INSERT_PARAM, new PreparedStatementSetter() {
                @Override
                public void setValues(PreparedStatement ps) throws SQLException {
                    int i=0;
                    ps.setString(++i, param.getItemId());
                    ps.setString(++i, param.getKey());
                    ps.setString(++i, param.getValue());
                    ps.setString(++i, param.getType());

                }
            });
    }
    @Override
    public void update( final Param param) {
        getJdbcTemplate().update(UPDATE_PARAM, new PreparedStatementSetter() {
                @Override
                public void setValues(PreparedStatement ps) throws SQLException {
                    int i=0;
                    ps.setString(++i, param.getItemId());
                    ps.setString(++i, param.getKey());
                    ps.setString(++i, param.getValue());
                    ps.setString(++i, param.getType());
                    ps.setInt(++i,param.getId());

                }
            });

    }
    @Override
    public Param get(final int id) {
        List<Param> list=getJdbcTemplate().query(SELECT_PARAM+BY_ID, new Object[]{id}, paramMapper);
        return CommonUtil.isNullArray(list)?null:CommonUtil.first(list);
    }
    @Override
    public Param get(String type, String key,String itemId) {
        String sql="";
        Object[] params=null;
        if(StringUtils.isEmpty(itemId)){
            sql=SELECT_PARAM+BY_TYPE+BY_KEY+BY_ITEM_ID.replace("?", "'"+Param.GLOBAL+"'");
            params= new Object[]{type,key};
        }else{
            sql=SELECT_PARAM+BY_TYPE+BY_KEY+BY_ITEM_ID;
            params= new Object[]{type,key,itemId};
        }
        List<Param> list=getJdbcTemplate().query(sql, params,paramMapper);
        return CommonUtil.isNullArray(list)?null:CommonUtil.first(list);
    }

    @Override
    public List<Param> getList(String type,String itemId) {
        String sql="";
        Object[] params=null;
        if(StringUtils.isEmpty(itemId)){
            sql=SELECT_PARAM+BY_TYPE+BY_ITEM_ID.replace("?", "'"+Param.GLOBAL+"'");
            params= new Object[]{type};
        }else{
            sql=SELECT_PARAM+BY_TYPE+BY_ITEM_ID;
            params= new Object[]{type,itemId};
        }

        return getJdbcTemplate().query(sql,params, paramMapper);
    }
    @Override
    public List<Param> getByTypeAndValue(String type, String value) {
        return getJdbcTemplate().query(SELECT_PARAM+BY_TYPE+BY_VALUE,new Object[]{type,value}, paramMapper);
    }
    @Override
    public List<Param> getParamByType(String type) {
        return getJdbcTemplate().query(SELECT_PARAM+BY_TYPE, new Object[]{type},paramMapper);
    }

}
