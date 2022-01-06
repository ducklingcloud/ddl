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

import java.awt.image.BufferedImage;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;

import javax.imageio.ImageIO;

import net.duckling.ddl.service.file.DFileSaver;
import net.duckling.ddl.service.file.FileStorage;
import net.duckling.ddl.service.file.IPictureService;
import net.duckling.ddl.service.file.Picture;
import net.duckling.ddl.util.ImageUtils;
import net.duckling.ddl.util.ReflectUtils;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

/**
 * @author lvly
 * @since 2012-11-20
 */
@Service
public class PictureServiceImpl implements IPictureService {
    public static final String DEFAULT_FILE_NAME = "SimplePicture";
    @Autowired
    private PictureDAO pictureDAO;
    @Autowired
    private FileStorage storage;

    @Override
    public int addPictrue(int clbId, int clbVersion, String tmpFilePath) {
        if (ImageUtils.scare(tmpFilePath)) {
            File scaledFile = null;
            try {
                scaledFile = new File(tmpFilePath);
                InputStream ins = new FileInputStream(scaledFile);
                int simplePicClbId = storage.createFile(DEFAULT_FILE_NAME
                                                        + clbId + "-" + clbVersion, scaledFile.length(), ins);
                Picture pic = new Picture();
                pic.setClbId(simplePicClbId);
                pic.setFileClbId(clbId);
                pic.setFileClbVersion(clbVersion);
                BufferedImage img = ImageIO.read(new File(tmpFilePath));
                pic.setHeight(img.getHeight());
                pic.setWidth(img.getWidth());
                return pictureDAO.addPicture(pic);
            } catch (IOException io) {
                return -1;
            } finally {
                if (scaledFile != null) {
                    scaledFile.delete();
                }
            }
        }

        return -1;
    }

    @Override
    public int downLoadAndAddPicture(int clbId, int clbVersion) {
        DFileSaver fs = new PictureFileSaver();
        storage.getContent(clbId, clbVersion, fs);
        return addPictrue(clbId, clbVersion,
                          (String) ReflectUtils.getValue(fs, "fileName"));
    }

    @Override
    public Picture getPicture(int clbId, int clbVersion) {
        return pictureDAO.getPicture(clbId, clbVersion);
    };

    public void setPictureDAO(PictureDAO picDAO) {
        this.pictureDAO = picDAO;
    }

}
