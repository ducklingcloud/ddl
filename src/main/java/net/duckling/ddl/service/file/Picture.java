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
package net.duckling.ddl.service.file;

import java.util.Date;

/**
 * 特殊File,图片的缩略图信息
 * 用于显示瀑布流的图片实体，只存储最新版本的信息
 * 依赖于File实体类，Picture的前提必须是一个File
 * @author lvly
 * @since 2012-11-20
 */
public class Picture {
    private int id;
    /**源图fileId
     * */
    private int fileClbVersion;
    /**
     * 源图的clbId
     * */
    private int fileClbId;
    /**源图宽，其实这里记一个源图的比例就可以了，以后可能有用*/
    private int width;
    /**源图高*/
    private int height;
    /**
     * 创建时间
     * */
    private Date createTime;

    /**
     * 缩略图clbId
     * */
    private int clbId;

    public int getFileClbId() {
        return fileClbId;
    }

    public void setFileClbId(int fileClbId) {
        this.fileClbId = fileClbId;
    }

    public Date getCreateTime() {
        return createTime;
    }

    public int getFileClbVersion() {
        return fileClbVersion;
    }

    public void setFileClbVersion(int fileVersion) {
        this.fileClbVersion = fileVersion;
    }

    public void setCreateTime(Date createTime) {
        this.createTime = createTime;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public int getWidth() {
        return width;
    }

    public void setWidth(int width) {
        this.width = width;
    }

    public int getHeight() {
        return height;
    }

    public void setHeight(int height) {
        this.height = height;
    }

    public int getClbId() {
        return clbId;
    }

    public void setClbId(int clbId) {
        this.clbId = clbId;
    }
}
