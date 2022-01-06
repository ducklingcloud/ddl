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
package net.duckling.ddl.service.file.impl;

import java.io.InputStream;

import net.duckling.ddl.service.file.DFileSaver;
import net.duckling.ddl.util.ImageUtils;


/**
 * 下载图片时候用，用来压缩图片
 * @author lvly
 * @since 2012-11-20
 */
public class PictureFileSaver implements DFileSaver{
    private String fileName;

    public String getFileName() {
        return fileName;
    }

    public void setFileName(String fileName) {
        this.fileName = fileName;
    }

    @Override
    public void save(String filename, InputStream in) {
        this.fileName=ImageUtils.saveAsFile(in);
    }

    @Override
    public void setLength(long length) {

    }

}
