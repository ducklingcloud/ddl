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

import java.util.regex.Matcher;

/**
 * email工具类
 * @author zhonghui
 *
 */
public final class EmailUtil {
	// http://www.ex-parrot.com/~pdw/Mail-RFC822-Address.html regex in java
	private static String ATOM = "[^\\x00-\\x1F^\\(^\\)^\\<^\\>^\\@^\\,^\\;^\\:^\\\\^\\\"^\\.^\\[^\\]^\\s]";
	private static String DOMAIN = "(" + ATOM + "+(\\." + ATOM + "+)*";
	private static String IP_DOMAIN = "\\[[0-9]{1,3}\\.[0-9]{1,3}\\.[0-9]{1,3}\\.[0-9]{1,3}\\]";

	private static java.util.regex.Pattern pattern = java.util.regex.Pattern.compile("^" + ATOM + "+(\\." + ATOM
			+ "+)*@" + DOMAIN + "|" + IP_DOMAIN + ")$", java.util.regex.Pattern.CASE_INSENSITIVE);
	/**
	 * 校验email是否合乎规范,采用的校验规则是PFC822，实现来源hibernate validation
	 * @param email
	 * @return
	 */
	public static boolean isValidEmail(String email) {
		if (email == null || email.length() == 0) {
			return false;
		}
		Matcher m = pattern.matcher(email);
		return m.matches();
	}
	
	/**
     * 获取遮挡的Email
     * @param uid
     * @return
     */
    public static String coverEmail(String email){
		if(email.indexOf('@')==-1){
			return email;
		}
    	String result = "";
    	int pos = email.indexOf('@');
    	String pre = email.substring(0, pos);
    	String last = email.substring(pos);
    	result+=pre.substring(0,2)+"***";
    	if(pre.length()>=5){
    		result+=pre.substring(pre.length()-2);
    	}
    	result+=last;
    	return result;
    }
	
}
