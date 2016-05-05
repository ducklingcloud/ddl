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

package net.duckling.ddl.service.file;

import java.io.InputStream;

import net.duckling.ddl.web.bean.ClbUrlTypeBean;
import cn.vlabs.clb.api.document.ChunkResponse;
import cn.vlabs.clb.api.document.DocMetaInfo;
import cn.vlabs.clb.api.document.MetaInfo;

/**
 * 文件存储提供者，目前只有CLB一个实现
 * @date 2011-8-10
 * @author xiejj@cnic.cn
 */
public interface FileStorage {
	
	/**
	 * 分块上传准备
	 * @param filename 文件名
	 * @param md5  为文件创建唯一标示
	 * @param size 文件大小
	 * @return
	 */
	ChunkResponse prepareChunkUpload(String filename, String md5, long size);
	
	/**
	 * 文件块执行上传
	 * @param docid clb文档id
	 * @param chunkedIndex 文件块序列索引
	 * @param buf 文件块字节数组
	 * @param numOfBytes 文件块实际大小
	 * @return
	 */
	ChunkResponse executeChunkUpload(int docid, int chunkedIndex, byte[] buf, int numOfBytes);
	
	/**
	 * 完成分块上传
	 * @param docid clb文档id
	 * @return
	 */
	ChunkResponse finishChunkUpload(int docid);
	
	/**
	 * 创建文件
	 * @param filename	文件名
	 * @param length	文件的长度
	 * @param in		文件内容
	 * @return			新创建的文件的ID号，新创建的文件版本号为1。
	 */
	int createFile(String filename, long length, InputStream in);
	/**
	 * 更新文件
	 * @param docid		被更新的文件的ID号，由createFile返回
	 * @param filename	文件名
	 * @param length	文件长度
	 * @param in		文件内容
	 * @return			新的版本号。
	 */
	int updateFile(int docid, String filename, long length,	InputStream in);
	/**
	 * 下载文件内容
	 * @param docid		文件的ID号，由createFile返回。
	 * @param version	要下载的版本号，如果版本号为-1，则下载最新版本。
	 * @param fs		文件保存对象。
	 */
	void getContent(int docid, int version, DFileSaver fs);
	
	/**
	 * 获取文件的元信息
	 * @param docid		文件的ID号
	 * @return			文件的元信息（文档ID，文档的最新版本，更新时间等）
	 */
	@Deprecated
	MetaInfo getMeta(int docid);
	
	/**
	 * 获取文件指定版本的元信息
	 * @param docid		文件的ID号
	 * @param version	文件版本号
	 * @return			文件的元信息（文档ID，文档的版本，更新时间等）
	 */
	@Deprecated
	MetaInfo getMeta(int docid, String version);
	
	/**
     * 获取文件的元信息
     * @param docid     文件的ID号
     * @return          文件的元信息（文档ID，文档的最新版本，更新时间等）
     */
    DocMetaInfo getDocMeta(int docid);
    
	/**
     * 获取文件指定版本的元信息
     * @param docid     文件的ID号
     * @param version   文件版本号
     * @return          文件的元信息（文档ID，文档的版本，更新时间等）
     */
    DocMetaInfo getDocMeta(int docid, String version);
    
	/**
	 * 下载编号为docid,版本号为version的文档所对应的PDF文档
	 * @param docid 文件ID号
	 * @param version 版本号
	 * @param fs 文件保存对象
	 */
	void getPdfContent(int docid, int version, DFileSaver fs);
	/**
	 * 查询编号为docid,版本号为version的文档所对应的PDF文档状态
	 * @param docid 文件ID号
	 * @param version 版本号
	 * @return status 文档的PDF状态
	 */
	String queryPdfStatus(int docid, String version);
	/**
	 * 发送将编号为docid,版本号为version的文档转换成PDF文档的请求事件
	 * @param docid 文件ID号
	 * @param version 版本号
	 */
	void sendPdfTransformEvent(int docid, String version);
	/**
	 * 获取clbid指定nginx的转发url
	 * @param clbId
	 * @return
	 */
	String getDirectURL(int clbId,String version,boolean isPDF);
	/**
	 * 按类型获取图片的重定向url
	 * @param clbId
	 * @param version
	 * @param type
	 * @return
	 */
	ClbUrlTypeBean getImageDirevtURL(int clbId,String version,String type);
	/**
	 * 
	 * @param clbId
	 * @param version
	 */
	void resizeImage(int clbId,String version);
	
	/**
	 * 根据类型下载图片
	 * @param docid
	 * @param version
	 * @param type
	 * @param fs
	 */
	void getImageContent(int docid, int version,String type, DFileSaver fs);
	
	/**
	 * 根据类型获取图片状态，如果图片不合规格，返回not_ready并重新压缩
	 * @param clbId
	 * @param version
	 * @param type 值为 not_ready, ready, failed;
	 * @return
	 */
	String getImageStatus(int clbId, String version, String type);
}
