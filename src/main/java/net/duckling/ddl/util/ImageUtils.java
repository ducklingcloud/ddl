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
package net.duckling.ddl.util;

import java.awt.Graphics;
import java.awt.Image;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;

import javax.imageio.ImageIO;


import org.apache.commons.io.IOUtils;
import org.apache.log4j.Logger;
import org.springframework.util.ClassUtils;


/**
 * @author lvly
 * @since 2012-11-20
 */
public class ImageUtils {
	public static final Logger LOG=Logger.getLogger(ImageUtils.class);
	public static final String PATH;
	public static final String TEMP = "temp_file_";
	public static final String TYPE_JPG = "jpg";
	public static final String TYPE_PNG = "png";
	public static final String TYPE_BMP = "bmp";
	public static final String TYPE_GIF = "gif";
	public static final String POINT = ".";
	public static final int DEFAULT_WIDTH=300;
	static{
		PATH=ClassUtils.getDefaultClassLoader().getResource("/").getPath()+File.separator + "resources" + File.separator + "temp" + File.separator;
		File file=new File(PATH);
		if(!file.exists()){
			file.mkdirs();
		}
	}

	public static boolean isPicture(String fileName) {
		if (isTitleContainPoint(fileName)) {
			return false;
		} else {
			String type = getFileType(fileName).toLowerCase();
			return (type.equals(TYPE_JPG) || type.equals(TYPE_GIF) || type.equals(TYPE_BMP) || type.equals(TYPE_PNG));
		}
	}

	/**
	 * @param fileName
	 * @return
	 */
	private static String getFileType(String fileName) {
		return fileName.substring(fileName.indexOf(POINT) + 1);
	}

	private static boolean isTitleContainPoint(String fileName) {
		return CommonUtils.isNull(fileName) || !fileName.contains(POINT);
	}
	
	/**
	 * 把流存成一个文件
	 * @param  in 输入流
	 * @return 文件的绝对路径
	 * */
	public static String saveAsFile(InputStream in) {
		String fileName = PATH+TEMP + System.nanoTime();
		try {
			File directory=new File(PATH);
			if(!directory.exists()){
				directory.mkdirs();
			}
			File file=new File(fileName);
			FileOutputStream fos = new FileOutputStream(file);
			IOUtils.copy(in, fos);
			in.close();
			fos.close();
		} catch (IOException e) {
			throw new RuntimeException("save tmp file error:"+fileName,e);
		}
		return fileName;
	}

	/**
	 * 压缩图片，返回压缩完成以后的图片地址
	 * @param tmpFilePath 源图片路径
	 * @return
	 */
	public static boolean scare(String tmpFilePath) {
			try {
				BufferedImage src = ImageIO.read(new File(tmpFilePath)); // 读入文件
				int width = src.getWidth();
				int height = src.getHeight();
				if(width>DEFAULT_WIDTH){
					height=(DEFAULT_WIDTH*height)/width;
					width=DEFAULT_WIDTH;
				}
				Image image = src.getScaledInstance(width, height, Image.SCALE_DEFAULT);
				BufferedImage tag = new BufferedImage(width, height, BufferedImage.TYPE_INT_RGB);
				Graphics g = tag.getGraphics();
				g.drawImage(image, 0, 0, null); // 绘制缩小后的图
				g.dispose();
				File resultFile = new File(tmpFilePath);
				ImageIO.write(tag, "JPEG", resultFile);// 输出到文件流
				return true;
			} catch (IOException e) {
				LOG.error(e);
			}
			return false;
	}
}
