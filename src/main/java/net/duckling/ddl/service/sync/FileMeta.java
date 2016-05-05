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
package net.duckling.ddl.service.sync;

import java.util.Date;
import java.util.List;

public class FileMeta {
	
	private int tid;
    /**
     * 文件id号,全称为file\_id
     */
    private long fid;
    /**
     * 文件版本号,全称file\_version
     */
    private long fver;
    /**
     * 全路径，包含文件名（如/hello/abc.doc）
     */
    private String path;
    /**
     * 文件名
     */
    private String name;
    
    private String size;
    /**
     * 客户器端最后修改时间
     */
    private long mtime;
    private boolean isDir;
    /**
     * 父文件夹ID
     */
    private long pfid;
    /**
     * 文件内容校验和
     */
    private String checksum;
    /**
     * 通行证ID
     */
    private String uploadUser;
    private Date uploadTime;
    private String uploadDevice;
    private String mimetype;
    /**
     * 如果是文件夹的话,仅包含一层子文件信息
     */
    private List<FileMeta> children;

    public int getTid() {
		return tid;
	}

	public void setTid(int tid) {
		this.tid = tid;
	}

	public long getFid() {
        return fid;
    }

    public void setFid(long fid) {
        this.fid = fid;
    }

    public long getFver() {
        return fver;
    }

    public void setFver(long fver) {
        this.fver = fver;
    }

    public String getPath() {
        return path;
    }

    public void setPath(String path) {
        this.path = path;
    }
    
    public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public String getSize() {
        return size;
    }

    public void setSize(String size) {
        this.size = size;
    }

    public long getMtime() {
        return mtime;
    }

    public void setMtime(long mtime) {
        this.mtime = mtime;
    }

    public boolean isDir() {
		return isDir;
	}

	public void setDir(boolean isDir) {
		this.isDir = isDir;
	}

	public long getPfid() {
        return pfid;
    }

    public void setPfid(long pfid) {
        this.pfid = pfid;
    }

    public Date getUploadTime() {
        return uploadTime;
    }

    public void setUploadTime(Date uploadTime) {
        this.uploadTime = uploadTime;
    }

    public String getUploadDevice() {
        return uploadDevice;
    }

    public void setUploadDevice(String uploadDevice) {
        this.uploadDevice = uploadDevice;
    }

    public String getChecksum() {
        return checksum;
    }

    public void setChecksum(String checksum) {
        this.checksum = checksum;
    }

	public String getUploadUser() {
		return uploadUser;
	}

	public void setUploadUser(String uploadUser) {
		this.uploadUser = uploadUser;
	}

	public String getMimetype() {
		return mimetype;
	}

	public void setMimetype(String mimetype) {
		this.mimetype = mimetype;
	}

	public List<FileMeta> getChildren() {
		return children;
	}

	public void setChildren(List<FileMeta> children) {
		this.children = children;
	}

}
