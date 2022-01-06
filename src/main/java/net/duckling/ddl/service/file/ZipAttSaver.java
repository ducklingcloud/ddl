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
package net.duckling.ddl.service.file;

import java.io.IOException;
import java.io.InputStream;
import java.util.zip.ZipEntry;
import java.util.zip.ZipOutputStream;

public class ZipAttSaver implements DFileSaver {
    private String path;
    private ZipOutputStream zipOut;

    public ZipAttSaver(String path,ZipOutputStream zipOut){
        if(path.startsWith("/")){
            path = path.substring(1);
        }
        this.path = path;
        this.zipOut = zipOut;
    }

    @Override
    public void save(String filename, InputStream in) {
        ZipEntry en = new ZipEntry(path);
        try {
            zipOut.putNextEntry(en);
            byte[] tmp = new byte[1024];
            int t = 0;
            int count=0;
            while((t=in.read(tmp))!=-1){
                zipOut.write(tmp, 0, t);
                count++;
                if(count>100){
                    zipOut.flush();
                    count = 0;
                }
            }
            zipOut.flush();
            in.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    @Override
    public void setLength(long length) {

    }

}
