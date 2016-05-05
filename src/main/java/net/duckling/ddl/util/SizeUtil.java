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

import java.text.DecimalFormat;

/**
 * @date 2011-10-12
 * @author clive
 */
public final class SizeUtil {
	private SizeUtil(){}
	
	public final static double KB_SCALE = 1024;
	public final static double MB_SCALE = 1024*1024;
	public final static double GB_SCALE = 1024*1024*1024;
	
	private static DecimalFormat df =new DecimalFormat("#.00");
	
	public static String getFormatSize(long size){
		double d = size;
		if(size<KB_SCALE)
		{
			return size+" B";
		}
		if(size<MB_SCALE)
		{
			return df.format(d/KB_SCALE)+" KB";
		}
		if(size<GB_SCALE)
		{
			return df.format(d/MB_SCALE)+" MB";
		}
		return df.format(d/GB_SCALE)+" GB";
	}
}
