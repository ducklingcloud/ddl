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
package net.duckling.ddl.service.resource;

import java.io.InputStream;
import java.util.List;

import cn.vlabs.clb.api.document.ChunkResponse;
import cn.vlabs.clb.api.document.MetaInfo;
import net.duckling.ddl.exception.NoEnoughSpaceException;
import net.duckling.ddl.exception.ResourceExistedException;
import net.duckling.ddl.service.draft.Draft;
import net.duckling.ddl.service.file.AttSaver;
import net.duckling.ddl.service.file.DFileSaver;
import net.duckling.ddl.service.file.FileVersion;
import net.duckling.ddl.web.bean.ClbUrlTypeBean;

public interface ResourceOperateService {
	
	/**
	 * 分块上传准备
	 * @param uid 用户id
	 * @param tid 团队id
	 * @param parentRid 目录rid
	 * @param fileName 文件名
	 * @param md5 为文件创建唯一标示
	 * @param size 文件大小
	 * @return Object[0] = ChunkResponse Object[1]=rid
	 * @throws NoEnoughSpaceException
	 */
	Object[] prepareChunkUpload(String uid, int tid, int parentRid,String fileName, String md5, long size)  throws NoEnoughSpaceException;
	
	/**
	 * @param rid
	 * @param tid
	 * @param chunkedIndex 文件块序列索引
	 * @param buf 文件块字节数组
	 * @param numOfBytes 文件块实际大小
	 * @return
	 */
	ChunkResponse executeChunkUpload(int rid, int tid, int chunkedIndex, byte[] buf, int numOfBytes);
	
	/**
	 * 完成分块上传
	 * @param rid
	 * @param tid
	 * @return
	 */
	ChunkResponse finishChunkUpload(int rid, int tid);
	
	/**
	 * 创建resource，并添加目录关系
	 * @param r
	 */
	void addResource(Resource r);

	int createFolder(Resource r);
	/**
	 * 创建文件夹，并添加目录关系
	 * @param r
	 * @param device 设备来源
	 * @return rid
	 */
	int createFolder(Resource r, String device);
	
	/**
	 * 获取page的PageRender，包括其resource和pageVersion
	 * @param tid
	 * @param rid
	 * @param version
	 * @return
	 */
	public PageRender getPageRender(int tid,int rid, int version);
	public PageRender getPageRender(int tid,int rid);
	
	/**
	 * 更新pageVersion，当content变化后更新version值
	 * @param meta
	 * @param content
	 */
	void updatePageVersion(Resource meta,String content);

	void deleteResource(int tid,int rid,String uid);
	/**
	 *  删除resource，并删除其path,子文件,文件星标和标签，切星标和标签永久删除不能恢复
	 * @param tid
	 * @param rid
	 * @param device
	 */
	void deleteResource(int tid,int rid,String uid, String device);
	/**
	 *  删除resource，并删除其中的子文件和文件星标和标签，切星标和标签永久删除不能恢复
	 * @param tid
	 * @param rid
	 */
	void deleteResource(int tid,List<Integer> rids,String uid);
	/**
	 * 复制resource
	 * @param tid
	 * @param destRid
	 * @param srcRid
	 * @return 复制后的新resource
	 */
	Resource copyResource(int destTid,int destRid,int srcTid, int srcRid,String uid);
	/**
	 * 复制resource
	 * @param tid
	 * @param destRid 复制目的目录rid
	 * @param srcRid 要复制的rid
	 * @return 复制后的新resource
	 */
	List<Resource> copyResource(int destTid, int destRid,int srcTid, List<Integer> srcRids, String uid);
	/**
	 * 移动resource
	 * @param tid
	 * @param destRid 目的目录的rid
	 * @param srcRid 要移动的rid
	 */
	void moveResource(int tid,int destRid, int srcRid,String uid); 
	/**
	 * 移动resource
	 * @param tid
	 * @param destRid 移动目的目录rid
	 * @param srcRid 要移动的rids
	 */
	void moveResource(int tid,int destRid, List<Integer> srcRids,String uid); 
	
	PageVersion createPageVersion(Resource resource, String content); 
	/**
	 * 将一个已存在的page置为最新版本，版本号+1
	 * @param tid
	 * @param rid
	 * @param recoverVersion
	 */
	void recoverPageVersion(int tid, int rid,int recoverVersion);
	/**
	 * 发布手动保存的草稿信息
	 * @param version
	 * @param d
	 */
	void publishManualSaveDraft(int version, Draft d);
	/**
	 * 通过rid获取page
	 * @param tid
	 * @param rids
	 * @return
	 */
	List<Resource> getDDoc(int tid, List<Integer> rids);
	/**
	 * 
	 * 恢复已删除的页面
	 * @param pid
	 * @param tid
	 * @return
	 */
	public int recoverDDoc(int tid,int rid,int parentRid,String uid)throws NoEnoughSpaceException;
	
	/**
	 * 恢复删除的文件，包括版本信息
	 * @param fid
	 * @param tid
	 * @return
	 */
	int recoverFile(int rid,int tid,int parentRid,String uid) throws NoEnoughSpaceException;
	
	/**
	 * 恢复文件夹，将文件夹恢复至parentRid下
	 * @param tid
	 * @param rid
	 * @return
	 * @throws NoEnoughSpaceException
	 */
	int recoverFolder(int tid,int rid,int parentRid,String uid)throws NoEnoughSpaceException;
	/**
	 * 恢复某个版本成最新版本
	 * @param tid
	 * @param rid
	 * @param version
	 */
	void recoverFileVersion(int tid,int rid,int version,String uid);
	/**
	 * 获取文件保存在clb的meta
	 * @param docid
	 * @return
	 */
	MetaInfo getMetaInfo(int docid);
	/**
	 *  获取文件保存在clb的meta
	 * @param docid
	 * @param version
	 * @return
	 */
	MetaInfo getMetaInfo(int docid, String version);
	/**
	 * 获取文件pdf转换状态
	 * @param clbId
	 * @param version
	 * @return
	 */
	String queryPdfStatus(int clbId, String version);
	/**
	 * 获取clb上文件的pdf信息
	 * @param docid
	 * @param version
	 * @param fs
	 */
	void getPdfContent(int docid, String version, DFileSaver fs);
	/**
	 * 发送pdf转换请求
	 * @param clbId
	 * @param version
	 */
	void sendPdfTransformEvent(int clbId, String version);
	void getContent(int docid, String version, DFileSaver fs);
	void getContent(int docid, AttSaver fs);
	/**
	 * 根据类型获取图片状态，如果图片不合规格，返回not_ready并重新压缩
	 * @param clbId
	 * @param version
	 * @param type 值为 not_ready, ready, failed;
	 * @return
	 */
	String getImageStatus(int clbId, String version, String type);
	
	FileVersion updateCLBFile(int rid, int tid, String uid, String filename, long size, InputStream in, String device) throws NoEnoughSpaceException;
	/**
	 * 根据clbId更新一个文件资源
	 * @param rid
	 * @param tid
	 * @param uid
	 * @param fileName
	 * @param clbId
	 * @param device
	 * @return
	 * @throws NoEnoughSpaceException
	 */
	FileVersion updateFileFromClb(int rid, int tid, String uid, String fileName, int clbId, String device) throws NoEnoughSpaceException;
	/**
	 * 根据clbId创建一个文件资源
	 * @param uid
	 * @param tid
	 * @param parentRid
	 * @param fileName
	 * @param clbId
	 * @param sendEvent
	 * @param device
	 * @return
	 * @throws NoEnoughSpaceException
	 */
	FileVersion createFileFromClb(String uid, Integer tid, Integer parentRid,String fileName, int clbId,boolean sendEvent, String device)
				throws NoEnoughSpaceException, ResourceExistedException;
	
	FileVersion upload(String uid, Integer tid, Integer parentRid, String filename, long size, InputStream in) throws NoEnoughSpaceException;
	FileVersion upload(String uid, Integer tid, Integer parentRid,String fileName,long size, InputStream in,boolean addDefaultTag) throws NoEnoughSpaceException;
	FileVersion upload(String uid, Integer tid, Integer parentRid,String fileName, long size, InputStream in,boolean addDefaultTag,boolean sendEvent) throws NoEnoughSpaceException;
	
	FileVersion upload(String uid, Integer tid, Integer parentRid, String fileName, long size, InputStream in,
			boolean addDefaultTag, boolean sendEvent, boolean fileNameSerial, String folderName) throws NoEnoughSpaceException;
	
	/**
	 * @param uid
	 * @param tid
	 * @param parentRid 目录ID，0是根目录
	 * @param fileName
	 * @param size
	 * @param in
	 * @param addDefaultTag
	 * @param sendEvent
	 * @param fileNameSerial 文件已存在是否序列自增
	 * @param folderName 指定上传到的目录名称
	 * @param device
	 * @return
	 * @throws NoEnoughSpaceException
	 */
	FileVersion upload(String uid, Integer tid, Integer parentRid, String fileName, long size, InputStream in,
			boolean addDefaultTag, boolean sendEvent, boolean fileNameSerial, String folderName, String device) throws NoEnoughSpaceException;
	
	String getDirectURL(int clbId, String version, boolean isPDF);
	ClbUrlTypeBean getImageDirevtURL(int clbId, String version, String type);
	void getImageContent(int docid, int version, String type, DFileSaver fs);
	FileVersion referExistFileByClbId(int tid, int parentRid, String uid, int clbId, int clbVersion, String filename,
			long size);
	/**
	 * 删除权限检查
	 * @param tid
	 * @param rid
	 * @param currentUid
	 * @return
	 */
	boolean deleteAuthValidate(int tid,int rid,String currentUid);

	boolean renameResource(int tid, int rid, String uid, String fileName);
	/**
	 * 重命名
	 * @param tid
	 * @param rid
	 * @param uid
	 * @param fileName
	 * @param device
	 * @return
	 */
	boolean renameResource(int tid, int rid, String uid, String fileName, String device);
	
	boolean canUseFileName(int tid,int parentRid,int rid,String itemType,String fileName);
	boolean canUseFileName(int tid,int parentRid,String itemType,String fileName);
	
/**
 * 添加默认姓名标签
 * @param rid
 */
	void addDefaultTag(int rid);

	/**
	 * 重名返回自增序列的文件名
	 * @param tid
	 * @param parentRid
	 * @param fileName
	 * @return
	 */
	String getSerialFileName(Integer tid, Integer parentRid,String fileName);
	
	/**创建一个空白ddoc文档
	 * @param parentRid
	 * @param title
	 * @param tid
	 * @param uid
	 * @return rid
	 */
	int createNewPage(int parentRid, String title, int tid, String uid);
}
