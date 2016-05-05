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
package net.duckling.ddl.web.bean;

import java.util.LinkedHashMap;

/**
 * 文件类型扩展名配置，辅助操作
 * @author Brett
 *
 */
public class FileTypeHelper {
	public static final String EXT_PDF = "pdf"; 
	private static LinkedHashMap<String, FileType> extMap = new LinkedHashMap<String, FileType>();
	
	static{
		extMap.put(FileType.IMAGE, new FileType(FileType.IMAGE,new String []{
				"jpg","jpeg","gif","png","bmp","tiff"
			}));
		extMap.put(FileType.PDF, new FileType(FileType.PDF,new String []{
				EXT_PDF
			}));
		extMap.put(FileType.TXT, new FileType(FileType.TXT,new String []{
				"txt"
			}));
		extMap.put(FileType.OFFICE, new FileType(FileType.OFFICE,new String []{
				EXT_PDF  //PDF
				,"doc","docx","docm","dot","dotm","dotx","odt"  //Word
				,"xls","xlsx","xlsm","ods"  //Excel
				,"ppt","pptx","odp","pot","potm","potx","pps","ppsm","ppsx","pptm"   //PowerPoint
				//,"one","onetoc2","onepkg"    //OneNote
			}));
	}
	
	public static FileType getFileType(String name){
		return extMap.get(name);
	}

	
	/**
	 * 根据文件名称获取文件视图名称
	 * @param fileName
	 * @return
	 */
	public static String getName(String fileName){
		String ext = getFileExt(fileName);
		String result = FileType.FILE;
		
		for(String key : extMap.keySet()){
			FileType view = extMap.get(key);
			if(view.isSupported(ext)){
				result = view.getName();
				break;
			}
		}
		return result;
	}
	
	/**
	 * 获取文件扩展名
	 * @param fileName
	 * @return
	 */
	public static String getFileExt(String fileName){
		if(fileName==null||fileName.length()==0){
			return null;
		}
		int index = fileName.lastIndexOf(".");
		if(index==-1||index==fileName.length()){
			return null;
		}
		return fileName.substring(index+1).toLowerCase();
	}
}
