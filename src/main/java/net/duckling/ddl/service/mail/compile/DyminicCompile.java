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

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.io.StringWriter;
import java.lang.reflect.Method;
import java.net.MalformedURLException;
import java.util.Arrays;
import java.util.Map;

import javax.tools.JavaCompiler;
import javax.tools.JavaCompiler.CompilationTask;
import javax.tools.JavaFileObject;
import javax.tools.ToolProvider;


import org.apache.log4j.Logger;


/**
 * 动态加载编译类
 * @author lvly
 * @since 2012-11-8
 */
public class DyminicCompile<T> {
	private static final Logger LOG=Logger.getLogger(DyminicCompile.class);
	
	public static final String VAR_CLASS_NAME="#@ClassName@#";
	public static final String VAR_JAVA_CODE="#@JavaCode@#";
	public static final String METHOD_NAME = "execute";
	private ClassFileInfo info;
	private Map<String,T> map;
	public DyminicCompile(ClassFileInfo info,Map<String,T> map){
		this.map=map;
		this.info=info;
	}
	

	/** 重新编译 ，最好不要多次加載運行，影響效率,一般只运行一次
	 * @param javaString， 需要
	 * */
	public void reload(String javaString) {
		// 开始编译
		JavaCompiler javaCompiler = ToolProvider.getSystemJavaCompiler();
		JavaFileObject fileObject = new JavaStringObject(info.getFileName(), readSource(javaString).toString());
		CompilationTask task = javaCompiler.getTask(null, null, null, Arrays.asList("-d", info.getClassPath()), null,
				Arrays.asList(fileObject));
		boolean success = task.call();
		if (!success) {
			throw new DyminicCompileException("compile dyminic java file error");
		} else {
			try {
				ClassLoader classLoader = new MyClassLoader(info.getFileName(), info.getClassPath());
				classLoader.loadClass(info.getFileName());
			} catch (MalformedURLException e) {
				throw new DyminicCompileException("compile dyminic java file error", e);
			} catch (ReflectiveOperationException e) {
				throw new DyminicCompileException("compile dyminic java file error", e);
			} catch (IOException e) {
				throw new DyminicCompileException("compile dyminic java file error", e);
			}
		}
	}
	/**执行已加载的对象
	*约定为 加载到根目录，方法名为METHOD_NAME
	*类名为info.fileName;
	*@return 返回已渲染好的字符串对象
	**/
	public String executeMethod() {
		try {
			Class<?> class1 = Class.forName(info.getFileName());
			Method method = class1.getDeclaredMethod(METHOD_NAME, new Class[] { Map.class });
			return (String) method.invoke(class1.newInstance(), new Object[] { map });
		} catch (ReflectiveOperationException e) {
			throw new DyminicCompileException("runtime EXCEPTION", e);
		}
	}
	private StringWriter readSource(String javaString) {
		StringWriter writer = new StringWriter();// 内存字符串输出流
		PrintWriter out = new PrintWriter(writer);
		BufferedReader sr = null;
		String key = info.getClassPath()+"emailTmp/Java.tmp";
		try {
			sr = new BufferedReader(new InputStreamReader(new FileInputStream(new File(key)), "utf8"));
			String str = null;
			while ((str = sr.readLine()) != null) {
				if(str.contains(VAR_JAVA_CODE)){
					str=str.replace(VAR_JAVA_CODE, javaString);
				}
				if(str.contains(VAR_CLASS_NAME)){
					str=str.replace(VAR_CLASS_NAME, info.getFileName());
				}
				out.println(str.trim());
			}
			out.flush();
			out.close();
			LOG.debug("java.tmp reloaded!:\n"+writer.toString());
			return writer;
		} catch (IOException e) {
			throw new RenderException("the file[" + key + "] is not access,not exists or can't access");
		} finally {
			if (sr != null) {
				try {
					sr.close();
				} catch (IOException e) {
					throw new RenderException("wo kao ,the stream can't close,why?");
				}
			}
		}
	}
}