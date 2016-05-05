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

import java.util.HashSet;
import java.util.Set;

public final class PlainTextHelper {
	private PlainTextHelper(){}
	private static final Set<String> SUPPORT_FILE_TYPE = new HashSet<String>();
	private static final String XML = "xml";
	static{
		// xml
		SUPPORT_FILE_TYPE.add(XML);
		SUPPORT_FILE_TYPE.add("xslt");
		SUPPORT_FILE_TYPE.add("tld");
		SUPPORT_FILE_TYPE.add("jsp");
		SUPPORT_FILE_TYPE.add("txt");
		//html
		SUPPORT_FILE_TYPE.add("html");
		SUPPORT_FILE_TYPE.add("htm");
		
		//java
		SUPPORT_FILE_TYPE.add("java");
		//javascript
		SUPPORT_FILE_TYPE.add("js");
		//css
		SUPPORT_FILE_TYPE.add("css");
		//c,c++
		SUPPORT_FILE_TYPE.add("c");
		SUPPORT_FILE_TYPE.add("cpp");
		SUPPORT_FILE_TYPE.add("h");
		// vb
		SUPPORT_FILE_TYPE.add("vb");
		SUPPORT_FILE_TYPE.add("jsl");
		// c#
		SUPPORT_FILE_TYPE.add("cs");
		//shell bash
		SUPPORT_FILE_TYPE.add("sh");
		SUPPORT_FILE_TYPE.add("bash");
		SUPPORT_FILE_TYPE.add("shell");
		//sql
		SUPPORT_FILE_TYPE.add("sql");
		//python
		SUPPORT_FILE_TYPE.add("py");
		//ruby
		SUPPORT_FILE_TYPE.add("rb");
		//groovy
		SUPPORT_FILE_TYPE.add("groovy");
		//php
		SUPPORT_FILE_TYPE.add("php");
	}
	
	public static boolean isSupported(String fileTypeStr){
		String fileType = fileTypeStr;
		if(null == fileType || "".equals(fileType)){
			return false;
		}
		fileType = fileType.toLowerCase();
		return SUPPORT_FILE_TYPE.contains(fileType);
	}
	
	public static String convert2BrushClassFileType(String fileTypeStr){
		String fileType = fileTypeStr;
		if(null == fileType || "".equals(fileType)){
			return null;
		}
		fileType = fileType.toLowerCase();
		if("c".equals(fileType) || "cpp".equals(fileType) || "h".equals(fileType)){
			return "cpp";
		}else if(XML.equals(fileType) || "txt".equals(fileType)
				|| "xslt".equals(fileType)|| "jsp".equals(fileType)
				|| "tld".equals(fileType)){
			return XML;
		}else if("sh".equals(fileType) || "bash".equals(fileType)
				|| "shell".equals(fileType)){
			return "shell";
		}else if("vb".equals(fileType) || "jsl".equals(fileType)){
			return "vb";
		}else if("cs".equals(fileType)){
			return "csharp";
		}else if("java".equals(fileType) || "js".equals(fileType) || "css".equals(fileType)
				|| "cs".equals(fileType) || "sql".equals(fileType) || "py".equals(fileType)
				|| "rb".equals(fileType) || "groovy".equals(fileType) || "php".equals(fileType)){
			return fileType;
		}else if(SUPPORT_FILE_TYPE.contains(fileType)){
			return XML;
		}else{
			return fileType;
		}
	}
}
