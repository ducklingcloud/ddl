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
package net.duckling.ddl.service.copy.impl;

import java.sql.Timestamp;
import java.util.Date;

import net.duckling.ddl.constant.LynxConstants;
import net.duckling.ddl.service.copy.CopyException;
import net.duckling.ddl.service.copy.CopyLog;
import net.duckling.ddl.service.copy.CopyLogDisplay;
import net.duckling.ddl.service.copy.ICopyService;
import net.duckling.ddl.service.devent.EventDispatcher;
import net.duckling.ddl.service.file.FileVersion;
import net.duckling.ddl.service.file.FileVersionService;
import net.duckling.ddl.service.file.impl.FileVersionDAO;
import net.duckling.ddl.service.resource.IResourceService;
import net.duckling.ddl.service.resource.PageRender;
import net.duckling.ddl.service.resource.Resource;
import net.duckling.ddl.service.resource.ResourceOperateService;
import net.duckling.ddl.service.resource.ResourceUtils;
import net.duckling.ddl.service.team.TeamService;
import net.duckling.ddl.service.user.AoneUserService;
import net.duckling.ddl.util.DateUtil;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;


/**
 * Copy功能数据库实现类
 * 
 * @author lvly
 * @since 2012-11-13
 */
@Service
public class CopyService implements ICopyService {
	@Autowired
	private CopyDAO copyDAO;
	@Autowired
	private FileVersionDAO fileVersionDAO;
	@Autowired
	private IResourceService resourceService;
	@Autowired
	private ResourceOperateService resourceOperateService;
	@Autowired
	private AoneUserService userService;
	@Autowired
	private TeamService teamService;
	@Autowired 
	private EventDispatcher eventDispatcher;
	@Autowired
	private FileVersionService fileVersionService;


	public void setCopyDAO(CopyDAO copyDAO) {
		this.copyDAO = copyDAO;
	}

	public void setFileVersionDAO(FileVersionDAO fileVersionDAO) {
		this.fileVersionDAO = fileVersionDAO;
	}

	@Override
	public boolean isNeedCover(int fromRid, int toTid) {
		return copyDAO.isDoUpdate(fromRid, toTid);

	}
	@Override
	public CopyLogDisplay getCopyedDisplay(int toRid, int toVersion) {
		CopyLog copyLog=copyDAO.getCopyLogByTo(toRid,toVersion);
		if(copyLog!=null){
			CopyLogDisplay copyDisplay=new CopyLogDisplay();
			copyDisplay.setCopyDate(DateUtil.getTime(copyLog.getCopyTime()));
			copyDisplay.setFromTeamName(teamService.getTeamByID(copyLog.getFromTid()).getDisplayName());
			copyDisplay.setFromVersion(copyLog.getFromVersion()+"");
			copyDisplay.setUserName(userService.getUserNameByID(copyLog.getUid()));
			copyDisplay.setrTitle(resourceService.getResource(copyLog.getFromRid()).getTitle());
			return copyDisplay;
		}
		return null;
	}

	@Override
	public void doCopy(int fromRid, int version,int fromTid, int[] toTids, boolean[] cover, String uid) throws CopyException {
		int index = 0;
		for (int toTid : toTids) {
			// 执行update操作
			if (cover[index++]){
				doUpdate(fromRid,version, toTid, copyDAO.getToRid(fromRid, toTid), uid);
			}
			// 执行create操作
			else {
				doCreate(fromRid,version, toTid, uid);
			}
		}

	}

	/**
	 * 执行创建操作
	 * 
	 * @param fromRid
	 * @param toTid
	 * @return 新生成的rid
	 */
	private void doCreate(int fromRid,int version, int toTid, String uid) throws CopyException {
		Resource oldResource =resourceService.getResource(fromRid);
		boolean isSelf=oldResource.getTid()==toTid;
		CopyLog log = new CopyLog();
		log.setFromRid(fromRid);
		log.setFromTid(oldResource.getTid());
		log.setFromVersion(version);
		log.setToTid(toTid);
		log.setToVersion(1);
		log.setCopyTime(new Timestamp(System.currentTimeMillis()));
		log.setUid(uid);
		log.setType(CopyLog.TYPE_CREATE);
		if (oldResource.isPage()){
			PageRender oldPageRender = resourceOperateService.getPageRender(oldResource.getTid(),
					oldResource.getRid(),version);
			Resource r = ResourceUtils.createDDoc(toTid, 0, LynxConstants.DEFAULT_DDOC_TITLE, uid);
			resourceService.create(r);
			r.setCreator(uid);
			r.setLastEditorName(uid);
			r.setLastVersion(0);
			r.setTitle((isSelf?"副本_":"")+oldPageRender.getDetail().getTitle());
			resourceOperateService.createPageVersion(r, oldPageRender.getDetail().getContent());
			log.setToRid(resourceService.getResource(r.getRid(), r.getTid()).getRid());
		} else if (oldResource.isFile()) {
			FileVersion oldFileVersion = fileVersionService.getFileVersion(oldResource.getRid(), oldResource.getTid(),version);
			Resource newCopyFile = getNewCopyFile(oldFileVersion, uid, toTid);
			resourceService.update(newCopyFile);
			FileVersion fv = getNewCopyFileVersion(newCopyFile,oldFileVersion.getClbId(),oldFileVersion.getClbVersion());
			fv.setSize(oldFileVersion.getSize());
			fileVersionDAO.update(fv.getId(),fv);
			log.setToRid(newCopyFile.getRid());
			eventDispatcher.sendFileUploadEvent(newCopyFile.getTitle(),newCopyFile.getRid(), uid,toTid);
		} else {
			throw new CopyException(fromRid);
		}
		copyDAO.addCopyLog(log);
	}

	/**
	 * 复制的时候已被证明是有来源的,进行版本+1操作
	 * 
	 * @param fromRid
	 * @param toRid
	 * @param toTid
	 */
	private void doUpdate(int fromRid,int version, int toTid, int toRid, String uid) throws CopyException {
		Resource oldResource = resourceService.getResource(fromRid);
		Resource destResource = resourceService.getResource(toRid);
		CopyLog log = new CopyLog();
		log.setFromRid(fromRid);
		log.setFromTid(oldResource.getTid());
		log.setFromVersion(version);
		log.setToTid(toTid);
		log.setToRid(destResource.getRid());
		log.setCopyTime(new Timestamp(System.currentTimeMillis()));
		log.setUid(uid);
		log.setType(CopyLog.TYPE_CREATE);
		if (oldResource.isPage()) {
			PageRender oldPageRender = resourceOperateService.getPageRender(oldResource.getTid(), oldResource.getRid(),version);
			resourceOperateService.updatePageVersion(oldPageRender.getMeta(), oldPageRender.getDetail().getContent());
			log.setToVersion(oldPageRender.getMeta().getLastVersion());
		} else if (oldResource.isFile()) {
			FileVersion oldFileVersion = fileVersionService.getFileVersion(oldResource.getRid(), oldResource.getTid(),
					version);
			Resource toFile = resourceService.getResource(toRid);
			toFile.setLastEditor(uid);
			toFile.setTitle(oldFileVersion.getTitle());
			toFile.setLastEditTime(new Date());
			toFile.setLastVersion(toFile.getLastVersion() + 1);
			resourceService.update(toFile);
			FileVersion fv = getNewCopyFileVersion(toFile,oldFileVersion.getClbId(),oldFileVersion.getClbVersion());
			fv.setSize(oldFileVersion.getSize());
			fileVersionDAO.update(fv.getId(), fv);
			log.setToVersion(fv.getVersion());
			eventDispatcher.sendFileModifyEvent(toFile.getTitle(), toFile.getRid(), toFile.getLastEditor(), toFile.getLastVersion(), toFile.getTid());
		} else {
			throw new CopyException(oldResource.getRid());
		}
		copyDAO.addCopyLog(log);
	}

	/**
	 * 获得新文件，一旦获得就会在数据库加记录，不加记录，获取不到fid
	 * 
	 * @param oldFileVersion
	 *            复制源file
	 * @param tid
	 *            团队id
	 * @param uid
	 *            UserId 用户邮箱
	 * @param newFile
	 * */
	private Resource getNewCopyFile(FileVersion oldFileVersion, String uid, int toTid) {
		Resource file = new Resource();
		file.setCreateTime(new Date());
		file.setCreator(uid);
		file.setLastEditor(uid);
		file.setLastEditTime(file.getCreateTime());
		file.setLastVersion(1);
		file.setStatus(LynxConstants.STATUS_AVAILABLE);
		file.setTid(toTid);
		file.setTitle((oldFileVersion.getTid()==toTid?"副本_":"")+oldFileVersion.getTitle());
		return file;
	}

	/**
	 * 返回一个新的FileVersion，对rid和size无赋值
	 * 
	 * @param newCopyFile
	 *            复制成功的一个file对象
	 * @return 初始化好的FileVersion
	 * */
	private FileVersion getNewCopyFileVersion(Resource newCopyFile,int clbId,int clbVersion) {
		FileVersion fv = new FileVersion();
		fv.setRid(newCopyFile.getRid());
		fv.setEditor(newCopyFile.getLastEditor());
		fv.setEditTime(newCopyFile.getCreateTime());
		fv.setTid(newCopyFile.getTid());
		fv.setTitle(newCopyFile.getTitle());
		fv.setVersion(newCopyFile.getLastVersion());
		fv.setClbId(clbId);
		fv.setClbVersion(clbVersion);
		fv.setId(fileVersionDAO.create(fv));
		return fv;

	}
}
