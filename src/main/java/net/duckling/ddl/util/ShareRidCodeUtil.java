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

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class ShareRidCodeUtil {

	public static final char[] code = { 'a', 'b', 'c', 'd', 'e', 'f', 'g', 'h', 'i', 'j', 'k', 'l', 'm', 'n', 'o', 'p',
			'q', 'r', 's', 't', 'u', 'v', 'w', 'x', 'y', 'z', 'A', 'B', 'C', 'D', 'E', 'F', 'G', 'H', 'I', 'J', 'K',
			'L', 'M', 'N', 'O', 'P', 'Q', 'R', 'S', 'T', 'U', 'V', 'W', 'X', 'Y', 'Z', '1', '2', '3', '4', '5', '6',
			'7', '8', '9', '0' };

	public static String encode(int n) {
		int s = code.length;
		List<Character> list = new ArrayList<Character>();
		int tmp = 0;
		do {
			tmp = n % s;
			list.add(code[tmp]);
			n = n / s;
		} while (n > 0);
		Collections.reverse(list);
		StringBuilder sb = new StringBuilder();
		for (Character c : list) {
			sb.append(c.toString());
		}
		return sb.toString();
	}

	public static int decode(String c) {
		char[] cs = c.toCharArray();
		int result = 0;
		int s = code.length;
		for (char cc : cs) {
			int i = 0;
			for (; i < s; i++) {
				if (code[i] == cc) {
					break;
				}
			}
			result = result * s + i;
		}
		return result;
	}
}
