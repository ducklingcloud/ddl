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
package net.duckling.ddl.service.param.impl;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import net.duckling.ddl.service.param.IParamService;
import net.duckling.ddl.service.param.Param;
import net.duckling.ddl.util.CommonUtil;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;


/**系统参数业务层实现
 * @author lvly
 * @since 2012-07-20
 * */
@Service
public class ParamServiceImpl implements IParamService{

    @Autowired
    private ParamDAO paramDAO;

    public ParamDAO getParamDAO() {
        return paramDAO;
    }

    public void setParamDAO(ParamDAO paramDAO) {
        this.paramDAO = paramDAO;
    }

    @Override
    public List<Param> getParamByType(String type) {
        return paramDAO.getParamByType(type);
    }
    @Override
    public void addParam(Param param) {
        paramDAO.create(param);
    }
    @Override
    public List<Param> getByTypeAndValue(String type,String value) {
        return paramDAO.getByTypeAndValue(type,value);
    }
    @Override
    public void updateParam(Param param) {
        paramDAO.update(param);

    }
    @Override
    public List<Param> getList(String type,String itemId) {
        return paramDAO.getList(type,itemId);
    }
    @Override
    public Map<String, String> getMap(String type,String itemId) {
        List<Param> list=paramDAO.getList(type,itemId);
        Map<String,String> map=new HashMap<String,String>();
        if(CommonUtil.isNullArray(list)){
            return map;
        }else{
            for(Param param:list){
                map.put(param.getKey(), param.getValue());
            }
        }
        return map;
    }
    @Override
    public String getValue(String type, String key,String itemId) {
        return get(type,key,itemId).getValue();
    }
    @Override
    public Param get(String type, String key,String itemId) {
        return paramDAO.get(type,key,itemId);
    }

}
