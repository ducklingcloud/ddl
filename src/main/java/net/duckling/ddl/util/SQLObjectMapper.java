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

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.sql.ResultSet;
import java.sql.SQLException;

import org.apache.log4j.Logger;

public final class SQLObjectMapper {
    private SQLObjectMapper(){}
    private static final Logger LOG = Logger.getLogger(SQLObjectMapper.class);

    public static byte[] getBytes(Object obj) {
        try (ByteArrayOutputStream bos = new ByteArrayOutputStream();
             ObjectOutputStream out = new ObjectOutputStream(bos)) {
            out.writeObject(obj);
            out.flush();
            return bos.toByteArray();
        } catch (IOException e) {
            LOG.error("Failed to serialize an object to byte[]\n"+
                      e.toString());
            return null;
        }
    }
  
    public static Object writeObject( ResultSet rs,String key){
        ObjectInputStream ois = null;
        try {
            InputStream is = rs.getBinaryStream(key);
            if(is!=null&&is.available()!=0){
                ois = new ObjectInputStream(is);
                return ois.readObject();
            }
        }catch(SQLException e){
            LOG.error("SQL Object Mapping Error",e);
        }catch(IOException e){
            LOG.error("SQL Object Mapping Error",e);
        }catch(ClassNotFoundException e){
            LOG.error("SQL Object Mapping Error",e);
        }finally{
            try {
                if(ois!=null){
                    ois.close();
                }
            } catch (IOException e) {
                LOG.error("IO Exception When try to close input stream",e);
            }
        }
        return null;
    }
}
