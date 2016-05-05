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
package net.duckling.ddl.service.file.impl;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import net.duckling.ddl.service.file.DFileRef;
import net.duckling.ddl.service.file.FileVersion;
import net.duckling.ddl.service.file.FileVersionService;

@Service
public class FileVersionServiceImpl implements FileVersionService {
	@Autowired
	private FileVersionDAO fileVersionDAO;
	@Autowired
	private FileReferenceDAO fileReferenceDAO;
	@Override
	public int create(FileVersion fileVersion) {
		return fileVersionDAO.create(fileVersion);
	}

	@Override
	public int delete(int id) {
		return fileVersionDAO.delete(id);
	}

	@Override
	public int delete(int rid, int tid, int version) {
		return fileVersionDAO.delete(rid, tid, version);
	}

	@Override
	public int deleteAllFileVersion(int rid, int tid) {
		return fileVersionDAO.deleteAllFileVersion(rid, tid);
	}

	@Override
	public int update(int id, FileVersion fileVersion) {
		return fileVersionDAO.update(id, fileVersion);
	}

	@Override
	public FileVersion getFileVersionById(int id) {
		return fileVersionDAO.getFileVersionById(id);
	}

	@Override
	public FileVersion getFileVersion(int rid, int tid, int version) {
		return fileVersionDAO.getFileVersion(rid, tid, version);
	}

	@Override
	public FileVersion getFirstFileVersion(int rid, int tid) {
		return fileVersionDAO.getFirstFileVersion(rid, tid);
	}
	@Override
	public FileVersion getLatestFileVersion(int rid, int tid) {
		return fileVersionDAO.getLatestFileVersion(rid, tid);
	}

	@Override
	public List<FileVersion> getFileVersions(int rid, int tid) {
		return fileVersionDAO.getFileVersions(rid, tid);
	}
	
	@Override
	public List<FileVersion> getFileVersions(int rid, int tid,int offset,int pageSize) {
		return fileVersionDAO.getFileVersions(rid, tid,offset,pageSize);
	}

	@Override
	public List<FileVersion> getFileSizeByRids(List<Long> ids) {
		return fileVersionDAO.getFileSizeByRids(ids);
	}

	@Override
	public List<FileVersion> getDFilesOfPage(int rid, int tid) {
		return fileVersionDAO.getDFilesOfPage(rid, tid);
	}

	

	@Override
	public List<FileVersion> getLatestFileVersions(int[] rids, int tid) {
		return fileVersionDAO.getLatestFileVersions(rids, tid);
	}

	@Override
	public int recoverFileVersion(int rid, int tid) {
		return fileVersionDAO.recoverFileVersion(rid, tid);
	}

	@Override
	public FileVersion getFileVersionByDocId(int clbId, String clbVersion) {
		return fileVersionDAO.getFileVersionByDocId(clbId, clbVersion);
	}

	@Override
	public void deleteRefer(int rid, int tid) {
		fileReferenceDAO.deletePageRefer(rid, tid);
		fileReferenceDAO.deleteFileRefer(rid, tid);
	}

	@Override
	public void deleteFileAndPageRefer(int fid, int pid, int tid) {
		fileReferenceDAO.deleteFileAndPageRefer(fid, pid, tid);
	}

	@Override
	public List<FileVersion> getFilesOfPage(int pid, int tid) {
		return fileVersionDAO.getDFilesOfPage(pid, tid);
	}

	@Override
	public void referTo(Integer rid, int tid, int[] rids) {
		//保存页面关联的文件
		List<DFileRef> pageReferedList = fileReferenceDAO.getPageReferences(rid, tid);
		fileReferenceDAO.referTo(distinctDFileRef(pageReferedList, rid, tid, rids, "fileRid"));
		
		//保存文件引用此页面
		List<DFileRef> fileReferedList = fileReferenceDAO.getFileReferences(rid, tid);
		fileReferenceDAO.referTo(distinctDFileRef(fileReferedList, rid, tid, rids, "pageRid"));
	}
	
	@Override
	public List<DFileRef> getPageReferences(Integer pageRid, int tid) {
		return fileReferenceDAO.getPageReferences(pageRid, tid);
	}
	
	/**
	 * 去除重复
	 * @param referedList
	 * @return
	 */
	private DFileRef[] distinctDFileRef(List<DFileRef> referedList,Integer rid, int tid, int[] rids, String idType){
		Map<Integer, Integer> refMap = new HashMap<Integer, Integer>();
    	for(DFileRef item : referedList){
    		if("fileRid".equals(idType)){
    			refMap.put(item.getFileRid(), item.getFileRid());
    		}else if("pageRid".equals(idType)){
    			refMap.put(item.getPageRid(), item.getPageRid());
    		}
    	}
		List<DFileRef> refList = new ArrayList<DFileRef>();
		for (int i = 0; i < rids.length; i++) {
			//是否已存在
			if(refMap.containsKey(rids[i])){
				continue;
			}
			DFileRef ref = new DFileRef();
			if("fileRid".equals(idType)){
				ref.setFileRid(rids[i]);
				ref.setPageRid(rid);
			}else if("pageRid".equals(idType)){
				ref.setFileRid(rid);
				ref.setPageRid(rids[i]);
			}
			ref.setTid(tid);
			refList.add(ref);
		}
		return (DFileRef[])refList.toArray(new DFileRef[0]);
	}

}
