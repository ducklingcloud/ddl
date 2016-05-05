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

public final class FileSizeUtils {
	public static final long ONE_KB = 1024l;
	public static final long ONE_MB = 1048576l;    //ONE_KB * ONE_KB;
	public static final long ONE_GB = 1073741824l; //ONE_KB * ONE_KB * ONE_KB;
	
	public static final String UNIT_MB = "MB";
	public static final String UNIT_GB = "GB";
	public static final String UNIT_KB = "KB";

	public static String getFileSize(long size) {
		String p = "";
		if(size<0){
			p="-";
			size = -size;
		}
		if (size / ONE_GB > 0) {
			return p+divce(size, ONE_GB, 2) + " " + UNIT_GB;
		} else if (size / ONE_MB > 0) {
			return p+divce(size, ONE_MB, 1) + " " + UNIT_MB;
		} else {
			if(size==0){
				return "0 B";
			}
			long n = size / ONE_KB;
			if (size % ONE_KB != 0) {
				n++;
			}
			if (n == 0) {
				n++;
			}
			return p+n + " " + UNIT_KB;
		}
	}
	
	public static Long getByteSize(String size) {
		if(size.endsWith(UNIT_GB)){
			return Long.parseLong(size.substring(0, size.lastIndexOf(UNIT_GB))) * ONE_GB; 
		}else if(size.endsWith(UNIT_MB)){
			return Long.parseLong(size.substring(0, size.lastIndexOf(UNIT_MB))) * ONE_MB; 
		}else if(size.endsWith(UNIT_KB)){
			return Long.parseLong(size.substring(0, size.lastIndexOf(UNIT_KB))) * ONE_KB; 
		}
		return 0l;
	}

	private static String divce(long size, long divider, int i) {
		if (i > 0) {
			if (size % divider != 0) {
				double re = ((double) size) / divider;
				return String.format("%."+i+"f", re);
			} else {
				return String.valueOf(size / divider);
			}
		} else {
			return String.valueOf(size / divider);
		}
	}
}
