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

import java.util.Calendar;
import java.util.Date;

import net.duckling.ddl.service.invitation.Invitation;
import net.duckling.ddl.service.user.Activation;

import org.apache.commons.codec.binary.Base64;


/**
 * @date 2011-6-21
 * @author Clive Lee
 */
public final class EncodeUtil {
	
	private EncodeUtil(){}
	
	private static final char[][] ACTIVATION_MATRIX = new char[][] {
			{ 'P', '@', 's', 'w', '0', 'R', '*', 'L', 'k', '.' },
			{ 'q', 'A', '%', '&', '2', 'Z', 'x', '6', ';', 'M' },
			{ '!', '$', 'c', 'V', 'U', ')', 'T', 'J', 'b', '~' } };

	private static final char[][] LOGIN_PASSWORD_MATRIX = new char[][] {
			{ 'A', '@', 's', 'b', '0', 'C', '*', 'd', 'E', '.' },
			{ 'f', 'G', '%', '&', '2', 'h', 'i', '6', 'j', '(' },
			{ '!', 'k', 'L', 'M', 'N', ')', 'T', 'o', 'p', '~' } };

	// private static final SimpleDateFormat SDF = new
	private static final int PASSWORD_SIZE = 7;
	private static final int MATRIX_ROW_SIZE = 3;

	private static int getRowValue(int d) {
		return d % MATRIX_ROW_SIZE;
	}

	private static int getRandomNumber() {
		double a = Math.random() * 10;
		a = Math.ceil(a);
		int decimal = new Double(a).intValue();
		return decimal % 10;
	}

	public static String generateRandomLoginPassword() {
		return getEncodeCipherText(generateEncodePlainText(),
				LOGIN_PASSWORD_MATRIX);
	}

	public static String generateEncode() {
		return getEncodeCipherText(generateEncodePlainText(), ACTIVATION_MATRIX);
	}

	public static String[] getDecodeArray(String downloadURL) {
		byte[] byteArray = Base64.decodeBase64(downloadURL.getBytes());
		StringBuilder sb = new StringBuilder();
		for (int i = 0; i < byteArray.length; i++) {
			sb.append((char) byteArray[i]);
		}
		String decodeURL = sb.toString();
		String[] tempArray = decodeURL.split("#");
		return tempArray;
	}

	private static String generateEncodePlainText() {
		// 生成7位数字的明文
		StringBuilder plainNumberBuilder = new StringBuilder();
		for (int i = 0; i < PASSWORD_SIZE; i++) {
			plainNumberBuilder.append(getRandomNumber());
		}
		return plainNumberBuilder.toString();
	}

	private static String getEncodeCipherText(String plainNumber,
			char[][] matrix) {
		// 获得7位数字所翻译出的密文
		int r = getRowValue(Integer.parseInt(plainNumber.charAt(0) + ""));
		StringBuilder cipherTextBuilder = new StringBuilder();
		for (int i = 1; i < PASSWORD_SIZE; i++) {
			int column = Integer.parseInt(plainNumber.charAt(i) + "");
			cipherTextBuilder.append(matrix[(r++) % MATRIX_ROW_SIZE][column]);
		}
		return cipherTextBuilder.toString();
	}

	public static String getDisplayURL(Invitation instance) {
		String all = instance.getEncode() + instance.getId();
		return new String(Base64.encodeBase64(all.getBytes())).trim();
	}

	public static String getDisplayURL(Activation instance) {
		String all = instance.getEncode() + instance.getId();
		return new String(Base64.encodeBase64(all.getBytes())).trim();
	}

	public static String[] decodeKeyAndID(String displayURL) {
		String temp = displayURL.replace(' ', '+');
		byte[] bytes = Base64.decodeBase64(temp.getBytes());
		StringBuilder keyBuilder = new StringBuilder();
		StringBuilder idBuilder = new StringBuilder();
		for (int i = 0; i < bytes.length; i++) {
			if (i < (PASSWORD_SIZE - 1)) {
				keyBuilder.append((char) bytes[i]);
			} else {
				idBuilder.append((char) bytes[i]);
			}
		}
		return new String[] { keyBuilder.toString(), idBuilder.toString() };
	}

	public static String getNextThreeDay() {
		Calendar c = Calendar.getInstance();
		c.setTime(new Date());
		c.add(Calendar.DATE, 3);
		return AoneTimeUtils.formatToDateTime(c.getTime());
	}
}
