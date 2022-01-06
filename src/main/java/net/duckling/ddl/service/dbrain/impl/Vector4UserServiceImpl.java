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
package net.duckling.ddl.service.dbrain.impl;


import net.duckling.ddl.service.dbrain.Vector4User;
import net.duckling.ddl.service.dbrain.Vector4UserService;
import net.duckling.ddl.service.dbrain.dao.impl.Vector4UserDaoImpl;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class Vector4UserServiceImpl implements Vector4UserService{
    @Autowired
    private Vector4UserDaoImpl vector4UserDao ;

    public Vector4UserDaoImpl getVector4UserDao() {
        return vector4UserDao;
    }

    public void setVector4UserDao(Vector4UserDaoImpl vector4UserDao) {
        this.vector4UserDao = vector4UserDao;
    }

    @Override
    public int insertUserVec(Vector4User uservec)
    {
        return vector4UserDao.insertUserVec(uservec);
    }

    @Override
    public float [] getUserVecByPassport(String passport)
    {
        return vector4UserDao.getUserVecByPassport(passport);
    }

    @Override
    public Vector4User getUserVecByID(int id)
    {
        return vector4UserDao.getUserVecByID(id);
    }

}
