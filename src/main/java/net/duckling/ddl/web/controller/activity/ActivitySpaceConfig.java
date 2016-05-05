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
package net.duckling.ddl.web.controller.activity;

public class ActivitySpaceConfig {
	public final static String ACTIVITY_TITLE = "送空间活动";
	public final static String REMARK_WEB_LOGIN = ACTIVITY_TITLE + "：Web登录";
	public final static String REMARK_SHARE_FILE = ACTIVITY_TITLE + "：分享文件";
	public final static String REMARK_CREATE_TEAM = ACTIVITY_TITLE + "：创建团队";
	public final static String REMARK_COMPUTER_LOGIN = ACTIVITY_TITLE + "：电脑客户端登录";
	public final static String REMARK_MOBILE_LOGIN = ACTIVITY_TITLE + "：手机客户端登录";
	//1024*1024*1024=1073741824=1G
	public final static long SIZE_WEB_LOGIN = 10737418240l; //10G
	public final static long SIZE_SHARE_FILE = 21474836480l; //20G
	public final static long SIZE_COMPUTER_LOGIN = 32212254720l; //30G
	public final static long SIZE_MOBILE_LOGIN = 32212254720l;
	public final static long SIZE_CREATE_TEAM = 10737418240l;
	
	public final static String ACTIVITY_TITLE_LOTTERY = "抽奖活动";
	public final static String REMARK_LOTTERY_PAN = ACTIVITY_TITLE_LOTTERY + "：个人空间同步版100M";
	public final static String REMARK_LOTTERY_TEAM = ACTIVITY_TITLE_LOTTERY + "：团队空间100M";
	public final static long SIZE_LOTTERY_PAN = 104857600l; //100M
	public final static long SIZE_LOTTERY_TEAM = 104857600l; //100M
}
