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

import java.util.Locale;

public enum PdfStatus {
	ERROR, SOURCE_NOT_FOUND, CONVERTING, SUCCESS, FAIL, NEED_CONVERT,ENCRYPTED_SOURCE_FILE,CORRUPT_SOURCE_FILE,CONVERT_SUCCESS_AND_HAS_MORE;

	@Override
	public String toString() {
		return this.name().toLowerCase(Locale.US);
	}
	
	public static boolean isError(String status){
		return ERROR.toString().equals(status);
	}
	public static boolean isSourceNotFound(String status){
		return SOURCE_NOT_FOUND.toString().equals(status);
	}
	public static boolean isConverting(String status){
		return CONVERTING.toString().equals(status);
	}
	public static boolean isSuccess(String status){
		return SUCCESS.toString().equals(status);
	}
	public static boolean isFail(String status){
		return FAIL.toString().equals(status);
	}
	public static boolean isNeedConvert(String status){
		return NEED_CONVERT.toString().equals(status);
	}
}
