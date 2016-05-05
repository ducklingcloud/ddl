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

import org.springframework.util.ClassUtils;

/**
 * @author lvly
 * @since 2012-11-9
 */
public class ClassFileInfo {
	private String directoryPath;
	private String path;
	private long lastModifyTime;
	private String fileName;
	private String classPath;

	public ClassFileInfo(String directoryPath, String path, long lastModifyTime, String fileName) {
		this.directoryPath = directoryPath;
		this.path = path;
		this.lastModifyTime = lastModifyTime;
		this.fileName = fileName;
		this.classPath = ClassUtils.getDefaultClassLoader().getResource("/").getPath();
	}

	public void setLastModifyTime(long modifyTime) {
		this.lastModifyTime = modifyTime;
	}

	public String getClassPath() {
		return classPath;
	}

	public String getDirectoryPath() {
		return directoryPath;
	}

	public void setDirectoryPath(String directoryPath) {
		this.directoryPath = directoryPath;
	}

	public String getPath() {
		return this.path;
	}

	public String getFileName() {
		return this.fileName;
	}

	public boolean isNeedReload(Long lastModify) {
		return !lastModify.equals(lastModifyTime);
	}

}
