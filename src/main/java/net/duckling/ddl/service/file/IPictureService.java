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
package net.duckling.ddl.service.file;



/**
 * Picture服务层的实现类
 * @author lvly
 * @since 2012-11-20
 */
public interface IPictureService {
	/**
	 * 创建一个图片记录，压缩完图片会自动把tmpFilePath路径的图片删掉
	 * @param pic 源图片信息clbId
	 * @param pic 源图片版本
	 * @param pic 需要压缩的图片路径
	 * 
	 * */
	int addPictrue(int clbId,int clbVersion,String tmpFilePath);
	
	/**
	 * 下载并创建图片简略图信息，一般是后台判断是否没有简略图，然后调用
	 * @param clbId
     * @param pic 源图片信息clbId
	 * @param pic 源图片版本
	 * */
	int downLoadAndAddPicture(int clbId,int clbVersion);
	/**
	 * 获得一个简略图片信息
	 * @param rid ResourceId;
	 * @return 简略图的clbId，简略图只有1版本
	 * */
	Picture getPicture(int clbId, int clbVersion);
	
}
