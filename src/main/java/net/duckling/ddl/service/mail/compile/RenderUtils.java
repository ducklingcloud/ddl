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
import java.net.URL;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Stack;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import net.duckling.ddl.util.CommonUtils;

import org.springframework.util.ClassUtils;


/**
 * 渲染模板工具类v.1.1 --加入缓存机制，模板只会读一次 --加入for嵌套机制，用法跟jstl表达式一致 --代码优化，性能提高
 * 
 * @author lvly
 * @since 2012-11-7
 */
public class RenderUtils {
	public static final String ALL_NOTICE_TEMP;
	public static final String ALL_NOTICE_WEEK_TEMP;
	public static final String BASE_END = "@#";
	/**
	 * 常用变量
	 * */
	public static final String BASE_START = "#@";
	public static final String BRAKET_END="\\]";
	public static final String BRAKET_STAET="\\[";
	/**
	 * 读好的模板，放到这里，当缓存用
	 * */
	public static final Map<String, List<String>> CACHE_MAP = new HashMap<String, List<String>>();
	public static final String CLASS_END = ".class";
	public static final String CONVERT_D_QUOTES = "\\\"";
	public static final String D_QUOTES = "\"";
	public static final String ELSE_IF_START = "<elseif test=\\\"";
	public static final String EMPTY = "";
	public static final String END_ELSE = "</else>";
	public static final String END_ELSE_IF = "</elseif>";
	public static final String END_FOR = "</for>";
	public static final String END_IF = "</if>";
	public static final String IF_END = "\\\">";
	public static final String IF_START = "<if test=\\\"";
	public static final String ITEMS_END = "@#\\\"";
	public static final String ITEMS_START = "items=\\\"#@";
	public static final String MENTION_TEMP;
	/**
	 * 模板所在详细路径
	 * */
	public static final String PATH;
	
	public static final String POINT = ".";
	public static final String QUICK_SHARE_FROM;
	public static final String QUICK_SHARE_TO;
	/**
	 * 需要匹配的正则表达式
	 * */
	public static final Pattern REG_BASE = Pattern.compile("#@.+?@#");
	public static final Pattern REG_ELSE = Pattern.compile("^<else>$");
	public static final Pattern REG_ELSE_IF = Pattern.compile("^<elseif test=\\\\\".+?\\\\\">$");
	public static final Pattern REG_FOR = Pattern.compile("^<for items=\\\\\"#@.+?@#\\\\\" var=\\\\\".+?\\\\\">$");
	public static final Pattern REG_IF = Pattern.compile("^<if test=\\\\\".+?\\\\\">$");
	public static final Pattern REG_ITEMS = Pattern.compile("items=\\\\\".+?\\\\\"");
	public static final Pattern REG_SET = Pattern.compile("^<set var=\\\\\".+?\\\\\" value=\\\\\".*\\\\\"/>$");
	public static final Pattern REG_VALUE = Pattern.compile("value=\\\\\".*\\\\\"");
	public static final Pattern REG_VAR = Pattern.compile("var=\\\\\".+?\\\\\"");
	/**
	 * 刚开始记录，最后更新时间
	 * */
	public static final Map<String, ClassFileInfo> RELOAD_CACHE_MAP = new HashMap<String, ClassFileInfo>();
	/**
	 * 分享邮件提醒
	 * */
	public static final String SHARE_NOTICE_TEMP;
	public static final String TMP_END = ".html";
	public static final String VALUE_START = "value=\\\"";
	public static final String VAR_END = "\\\"";
	public static final String VAR_START = "var=\\\"";

	static {
		String path = "";
		URL url = ClassUtils.getDefaultClassLoader().getResource("/");
		if (url != null) {
			path = url.getPath() + "emailTmp/";
		}
		PATH = path;
		// 模板路径赋值
		SHARE_NOTICE_TEMP = init("ShareNoticeTemp");
		ALL_NOTICE_TEMP = init("AllNoticeTemp");
		ALL_NOTICE_WEEK_TEMP = init("AllNoticeWeekTemp");
		QUICK_SHARE_FROM=init("QuickShareFromTemp");
		QUICK_SHARE_TO=init("QuickShareToTemp");
		MENTION_TEMP = init("MentionTemp");
	}

    private static void doElse(StringBuffer result, Map<String, Object> map, String line) {
		result.append("else{");
	}
	/**
	 * 验证test成功与否
	 * 
	 * @param map
	 *            params
	 * @param line
	 *            当前行
	 * */
	private static void doElseIf(StringBuffer result, Map<String, Object> map, String line) {
		result.append("else if(" + returnTestEx(result, map, line) + "){");
	}

	/**
	 * 因为有可能多层嵌套for循环的关系，这应该是一个递归关系
	 * 
	 * @param result
	 *            最终结果
	 * @param map
	 *            替换变量字典，因域不通Map也不禁相同
	 * @param source
	 *            原来模板
	 * @param index
	 *            当前游标位置
	 * */
	private static void doFor(StringBuffer result, Map<String, Object> map, String line) {
		Matcher varMatcher = REG_VAR.matcher(line);
		Matcher itemMatcher = REG_ITEMS.matcher(line);
		if (itemMatcher.find()) {
			String itemKey = itemMatcher.group().replace(ITEMS_START, EMPTY).replace(ITEMS_END, EMPTY);
			if (varMatcher.find()){
				String varKey = varMatcher.group().replace(VAR_START, EMPTY).replace(VAR_END, EMPTY);
				result.append("if(map.get(\"" + itemKey + "\")!=null){\n");
				result.append("for(Iterator it"+varKey+"=toCollection(map.get(\"" + itemKey + "\")).iterator();it"+varKey+".hasNext();){\n");
				result.append("map.put(\"" + varKey + "\",it" + varKey + ".next());\n");
			} else {
				throw new RenderException("the <for> tag can't find attribute named var [" + line + "]");
			}
		} else {
			throw new RenderException("the <for> tag can't find attribute named items [" + line + "]");
		}
	}

	/**
	 * 验证test成功与否
	 * 
	 * @param map
	 *            params
	 * @param line
	 *            当前行
	 * */
	private static void doIf(StringBuffer result, Map<String, Object> map, String line) {
		result.append("if(" + returnTestEx(result, map, line) + "){");
	}

	/**
	 * 替换普通变量，视为字符串替换
	 * 
	 * @param result
	 *            最终结果
	 * @param map
	 *            替换变量字典，因域不通Map也不禁相同
	 * @param line
	 *            待替换的行
	 * @return
	 * */
	private static void doReplace(StringBuffer result, Map<String, Object> map, String line) {
		doReplaceAndReturn(result, map, line, EMPTY, true);
	}

	/**
	 * 替换普通变量，视为字符串替换
	 * 
	 * @param result
	 *            最终结果
	 * @param map
	 *            替换变量字典，因域不通Map也不禁相同
	 * @param line
	 *            待替换的行
	 * @param subfix
	 *            带到替换以后的前后面
	 * 
	 * @return 同上，只是返回不做别的操作
	 * */
    private static String doReplaceAndReturn(StringBuffer result, Map<String, Object> map, String line, String subfix,
			boolean killNull) {
		if (map == null) {
			return "";
		}
		Matcher matcher = REG_BASE.matcher(line);
		String tempLine = line;
		;
		result.append("result.append(killNull(\"" + line + "\").toString()");
		while (matcher.find()) {
			String key = matcher.group();
			String mapKey = key.replace(BASE_START, EMPTY).replace(BASE_END, EMPTY);
			result.append(".replaceAll(\"" + key.replace("[", "\\\\[").replace("]", "\\\\]") + "\",killNull(map.get(\"" + mapKey + "\")))");
		}
		result.append(");\n");
		return tempLine;
	}

	/**
	 * 
	 * **/
    private static void doSet(StringBuffer result, Map<String, Object> map, String line) {
		Matcher varMatcher = REG_VAR.matcher(line);
		Matcher valueMatcher = REG_VALUE.matcher(line);
		if (varMatcher.find()) {
			String varKey = varMatcher.group().replace(VAR_START, EMPTY).replace(VAR_END, EMPTY);
			if (valueMatcher.find()) {
				String valueEx = valueMatcher.group().replace(VALUE_START, EMPTY);
				valueEx = valueEx.substring(0, valueEx.length() - 2);
				Matcher matcher = REG_BASE.matcher(line);
				while (matcher.find()) {
					String key = matcher.group();
					String mapKey = key.replace(BASE_START, EMPTY).replace(BASE_END, EMPTY);
					valueEx = valueEx.replace(key, "map.get(\"" + mapKey + "\")");
				}
				result.append("map.put(\"" + varKey + "\"," + valueEx.replace(CONVERT_D_QUOTES, D_QUOTES) + ");\n");
			} else {
				throw new RenderException("the <set> tag can't find attribute named value [" + line + "]");
			}
		} else {
			throw new RenderException("the <set> tag can't find attribute named var [" + line + "]");
		}
	}

	/**
	 * 一行代码的执行情况，以后如果加上更加强大的特性，就需要再次改
	 * 
	 * @param result
	 *            渲染后的目标集
	 * @param params
	 *            替换的字典
	 * @param source
	 *            模板
	 * @param i
	 *            当前代码行数
	 * @param forIndex
	 *            for循环的闭合情况
	 * @return int 当前代码行数
	 */
	private static void executeLine(StringBuffer result, Map<String, Object> params, String line) {
		// 判断是否是</for>
		if (END_FOR.equals(line)) {
			result.append("}\n}\n");
		}
		// 先判断是否是</if>
		else if (END_IF.equals(line) || END_ELSE.equals(line) || END_ELSE_IF.equals(line)) {
			result.append("\n}");
		}
		// 先判断是否是if标签
		else if (isMatch(REG_IF, line)) {
			doIf(result, params, line);
		} else if (isMatch(REG_ELSE_IF, line)) {
			doElseIf(result, params, line);
		} else if (isMatch(REG_ELSE, line)) {
			doElse(result, params, line);
		}
		// 判断是否是<for>标签
		else if (isMatch(REG_FOR, line)) {
			doFor(result, params, line);
		}
		// 判断是否是<set>标签
		else if (isMatch(REG_SET, line)) {
			doSet(result, params, line);
		}
		// 再判断是否是${xx}
		else {
			doReplace(result, params, line);
		}
	}

	private static final String init(String key) {
		String tmpFile = PATH + key + TMP_END;
		// 加入到缓存
		CACHE_MAP.put(tmpFile, readSource(tmpFile));
		// 记录最后更新时间
		RELOAD_CACHE_MAP.put(tmpFile, new ClassFileInfo(PATH, PATH + key + CLASS_END,-1,key));
		return tmpFile;
	}

	/**
	 * 匹配正则
	 * @param pattern 正则表达式，
	 * @param line 待匹配字符串
	 * @return boolean 是否匹配
	 * */
	private static boolean isMatch(Pattern pattern, String line) {
		return pattern.matcher(line).find();
	}

	/**
	 * 分析标签的闭合关系
	 * 
	 * @param source
	 *            文件
	 * */
	private static boolean parseTag(List<String> source) {
		Stack<Integer> forStack = new Stack<Integer>();
		Stack<Integer> ifStack = new Stack<Integer>();
		Map<Integer, Integer> result = new HashMap<Integer, Integer>();
		
		for (int i = 0; i < source.size(); i++) {
			try {
				String line = source.get(i);
				// for
				if (isMatch(REG_FOR, line)) {
					forStack.push(i);
				}
				if (END_FOR.equals(line)) {
					result.put(forStack.pop(), i);
				}
				// if
				if (isMatch(REG_IF, line)) {
					ifStack.push(i);
				}
				if (END_IF.equals(line)) {
					result.put(ifStack.pop(), i);
				}
			} catch (RuntimeException e) {
				throw new RenderException(i+"\t<for> tag or <if> tag invalid in line near[" + source.get(i-1)+ ","+source.get(i)+","+source.get(i+1)+"]");
			}
		}
		return true;
	}

	/**
	 * 初始化，加载到内存里面,外部吾用
	 * @param key
	 *            应当是成员变量TMP_XX,
	 * */
	private static List<String> readSource(String key) {
		BufferedReader sr = null;
		try {
			sr = new BufferedReader(new InputStreamReader(new FileInputStream(new File(key)), "utf8"));
			String str = null;
			List<String> sourceListStr = new LinkedList<String>();
			while ((str = sr.readLine()) != null) {
				sourceListStr.add(str.trim().replace(D_QUOTES, CONVERT_D_QUOTES));
			}
			return sourceListStr;
		} catch (IOException e) {
			throw new RenderException("the file[" + key + "] is not access,not exists or can't access");
		} finally {
			if (sr != null) {
				try {
					sr.close();
				} catch (IOException e) {
					throw new RenderException("wo ka ao ,the stream can't close,why?");
				}
			}
		}
	}

    private static String returnTestEx(StringBuffer result, Map<String, Object> map, String line) {
		String testEx = line.replace(IF_START, EMPTY).replace(IF_END, EMPTY).replace(ELSE_IF_START, EMPTY);
		testEx=testEx.replace(CONVERT_D_QUOTES, D_QUOTES);
		// 因为是表达式，不能过滤掉null值
		Matcher matcher = REG_BASE.matcher(line);
		while (matcher.find()) {
			String key = matcher.group();
			String mapKey = key.replace(BASE_START, EMPTY).replace(BASE_END, EMPTY);
			testEx = testEx.replace(key, "killNull(map.get(\"" + mapKey + "\")).toString()");
		}
		return testEx;
	}
	/**
	 * 本工具类主打的方法渲染
	 * 
	 * @param map
	 *            <String,Object> 需要替换的变量param
	 * @param tempFile
	 *            应当是成员变量TMP_XX中的一个,或者直接给出绝对地址
	 * @return string 渲染好的结果
	 * */
	public static String render(Map<String, Object> map, String tempFile) {
		List<String> strList = CACHE_MAP.get(tempFile);
		ClassFileInfo info = RELOAD_CACHE_MAP.get(tempFile);
		if (info.isNeedReload(new File(tempFile).lastModified())){
			info.setLastModifyTime(new File(tempFile).lastModified());
			strList = readSource(tempFile);
			if (CommonUtils.isNull(strList)) {
				throw new RenderException("can't find tmp file named[" + tempFile + "]");
			}
			if (!parseTag(strList)) {
				throw new RenderException("the tag is not double Matched[" + tempFile + "]");
			}
			;
			StringBuffer result = new StringBuffer();
			// 遍历模板的每一行，替换变量
			for (int i = 0; i < strList.size(); i++) {
				executeLine(result, map, strList.get(i));
			}
			DyminicCompile compile = new DyminicCompile(info, map);
			compile.reload(result.toString());
			return compile.executeMethod();
		} else {
			DyminicCompile compile = new DyminicCompile(info, map);
			return compile.executeMethod();
		}
	}

}
