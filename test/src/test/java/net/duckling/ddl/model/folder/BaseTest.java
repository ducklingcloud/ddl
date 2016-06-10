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
package net.duckling.ddl.model.folder;

import java.beans.PropertyVetoException;

import net.duckling.ddl.service.resource.FolderPathService;
import net.duckling.ddl.service.resource.dao.FolderPathDAOImpl;
import net.duckling.ddl.service.resource.impl.FolderPathDAO;
import net.duckling.ddl.service.resource.impl.FolderPathServiceImpl;
import net.duckling.ddl.util.AbstractBaseDAO;

import org.junit.AfterClass;
import org.junit.BeforeClass;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate;

import com.mchange.v2.c3p0.ComboPooledDataSource;

public class BaseTest {

    protected static ComboPooledDataSource ds = new ComboPooledDataSource();
    protected static FolderPathDAO folderPathDao;
    protected static FolderPathService folderPathService;
    protected static JdbcTemplate template;
    @BeforeClass
    public static void prepare() throws PropertyVetoException {
        ds.setDriverClass("com.mysql.jdbc.Driver");
        ds.setJdbcUrl("jdbc:mysql://10.10.1.82:3306/zh?useUnicode=true&characterEncoding=utf8&noAccessToProcedureBodies=true");
        ds.setUser("root");
        ds.setPassword("root");
        ds.setInitialPoolSize(10);
        ds.setMinPoolSize(10);
        ds.setMaxPoolSize(10);
        ds.setAcquireIncrement(10);

        template = new JdbcTemplate(ds);
        NamedParameterJdbcTemplate ntemplate = new NamedParameterJdbcTemplate(ds);
        
        folderPathDao = new FolderPathDAOImpl();
        ((AbstractBaseDAO)folderPathDao).setJdbcTemplate(template);
        ((AbstractBaseDAO)folderPathDao).setNamedParameterJdbcTemplate(ntemplate);
        
        FolderPathServiceImpl folderPathServiceImpl = new FolderPathServiceImpl();
        folderPathServiceImpl.setFolderPathDAO(folderPathDao);
        folderPathService = folderPathServiceImpl;
    }
    public  void cleanTable(String table){
    	template.update("delete from "+table);
    }
    @AfterClass
    public static void tearDown(){
        ds.close();
    }

}
