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
package net.duckling.ddl.service.file.impl;

import net.duckling.ddl.service.file.Picture;

/**
 * @author lvly
 * @since 2012-11-20
 */
public interface PictureDAO {

	/**
	 * 创建缩略图信息
	 * @param pic
	 */
	int addPicture(Picture pic);

	/**
	 * 获得一个图片信息
	 * @param clbId 原图片的clbId;
	 * @param clbVersion 原图片的版本
	 * @return 简略图的clbId，简略图只有1版本
	 * */
	Picture getPicture(int clbId, int clbVersion);

}
