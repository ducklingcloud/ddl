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
package net.duckling.ddl.service.mail.compile;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;

public class MyClassLoader extends ClassLoader {
    private InputStream classFile = null;
    private String name = null;
  
    /**
     * @param name String  类全名
     * @param url URL  类路径
     * @throws FileNotFoundException
     * @throws IOException
     */
    public MyClassLoader(String name, String dirPath) throws
            FileNotFoundException, IOException {
        super(getSystemClassLoader());
        this.name = name + ".class";
 
        InputStream classIs = new FileInputStream(new File(dirPath+this.name));
        this.classFile = classIs;
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        byte buf[] = new byte[1024];
        //读取文件流
        for (int i = 0; (i = classIs.read(buf)) != -1; ) {
            baos.write(buf, 0, i);
        }
        classIs.close();
        baos.close();
 

        //创建新的类对象
        byte[] data = baos.toByteArray();
        defineClass(name, data, 0, data.length);
    }
 

    /**
     * 重载 getResourceAsStream() 是为了返回该类的文件流。
     * @return an InputStream of the class bytes, or null
     */
    public InputStream getResourceAsStream(String resourceName) {
        try {
            if (resourceName.equals(name)) {
                return this.classFile;
            }
        } catch (Exception e) {
            return null;
        }
        return null;
    }
}