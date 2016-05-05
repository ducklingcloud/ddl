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
package net.duckling.ddl.service.sync;

import cn.vlabs.clb.api.CLBException;
import cn.vlabs.clb.api.document.DocMetaInfo;
import net.duckling.ddl.constant.LynxConstants;
import net.duckling.ddl.service.file.FileStorage;
import net.duckling.ddl.service.file.FileVersion;
import net.duckling.ddl.service.file.FileVersionService;
import net.duckling.ddl.service.resource.*;
import net.duckling.ddl.service.sync.dao.ResourceDao;
import net.duckling.ddl.service.sync.domain.resource.DFile;
import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;


@Service
public class FileMetaService implements IFileMetaService{
	private static final Logger LOG = Logger.getLogger(FileMetaService.class);
	@Override
	public boolean isConflict(int tid, long fid, long fver) {
		return false;
	}

	@Override
	public List<FileMeta> list(int tid, Resource r) {
		if(r==null || r.isPage()){
			return null;
		}
		//目录
		if(r.isFolder()){
			return getListByParentId(tid,r.getRid());
		}
		//文件
		List<FileMeta> list = new ArrayList<FileMeta>();
		list.add(get(r));
		return list;
	}

	@Override
	public FileMeta get(Resource r, Long fver) {
		String parentPath = getPath(r.getBid());
		if (r.isFolder()) {
			return assemble(r, parentPath);
		} else if (r.isFile()) {
			FileVersion fv = fileVersionService.getFileVersion(r.getRid(), r.getTid(), fver.intValue());
			return assemble(r, fv, parentPath);
		} else if (r.isPage()) {
			return assembleDdoc(r, parentPath);
		}
		return null;
	}

	@Override
	public FileMeta get(Resource r) {
		if (r.isPage()) {
			return assembleDdoc(r, getPath(r.getBid()));
		}
		return assemble(r, getPath(r.getBid()));
	}
	
	@Override
	public FileMeta get(int tid, Long fid, Long fver) {
		Resource r = resourceService.getResource(fid.intValue(), tid);
		return get(r, fver);
	}
	
	@Override
	public FileMeta get(int tid, Long fid) {
		Resource r = resourceService.getResource(fid.intValue(), tid);
		return get(r);
	}
	
	@Override
	public List<FileMeta> getDescendants(int tid, int parentId){
		List<FileMeta> result = new ArrayList<FileMeta>();
		
		//checksum缓存map
		Map<Integer,String> checksumMap = getChecksumMap(tid,parentId);
		
		//路径缓存map
		Map<Integer,String> pathMap = new HashMap<Integer,String>();
		pathMap.put(parentId, getPath(parentId));
		
		List<net.duckling.ddl.service.sync.domain.resource.Resource> list = 
				resourceDao.getDescendants(tid, parentId, null);
		for(net.duckling.ddl.service.sync.domain.resource.Resource r : list){
			FileMeta meta = new FileMeta();
			meta.setTid(r.getTid());
			meta.setFid(r.getRid());
			
			//先从缓存获取父路径
			String parentPath = pathMap.get(r.getBid());
			if(parentPath==null){
				parentPath = getPath(r.getBid());
				pathMap.put(r.getBid(), parentPath);
			}
			if (r.getItemType().equals(LynxConstants.TYPE_PAGE)) {
				meta.setPath(PathName.appendDelimiter(parentPath) + r.getTitle() + ".ddoc");
				meta.setName(r.getTitle() + ".ddoc");
			} else {
				meta.setPath(PathName.appendDelimiter(parentPath) + r.getTitle());
				meta.setName(r.getTitle());
			}

			meta.setDir(r.isFolder());
			meta.setPfid(r.getBid());
			meta.setMimetype(r.getFileType());
			meta.setChecksum(checksumMap.get(r.getRid()));
			meta.setUploadDevice(""); 
			meta.setFver(r.getVersion());
			meta.setSize(String.valueOf(r.getSize()));

			meta.setUploadTime(r.getLastEditTime());
			meta.setMtime(r.getLastEditTime()==null?0:r.getLastEditTime().getTime());
			meta.setUploadUser(r.getLastEditor());
			result.add(meta);
		}
		return result;
	}
	
	private List<FileMeta> getListByParentId(int tid, int parentId){
		List<FileMeta> list = new ArrayList<FileMeta>();
		List<Resource> resList = folderPathService.getChildren(tid, parentId);
		String parentPath = getPath(parentId);
		for(Resource item : resList){
			if (item.isPage()) {
				list.add(assembleDdoc(item, parentPath));
			} else {
				list.add(assemble(item, parentPath));
			}
		}
		return list;
	}
	
	/**
	 * 获取资源完整路径,如/hello/abc.doc
	 * @param rid
	 * @return
	 */
	private String getPath(int rid){
		//根目录
		if(rid == 0){
			return PathName.DELIMITER;
		}
		List<Resource> list = folderPathService.getResourcePath(rid);
		StringBuilder sb= new StringBuilder();
		for(Resource item : list){
			sb.append(PathName.DELIMITER + item.getTitle());
		}
		return sb.toString();
	}
	
	private FileMeta assemble(Resource r, FileVersion fv, String parentPath){
		if(r.isFolder()){
			fv = new FileVersion();
			fv.setVersion(r.getLastVersion());
			fv.setSize(r.getSize());
			fv.setTitle(r.getTitle());
			fv.setEditTime(r.getLastEditTime());
			fv.setEditor(r.getLastEditor());
		}
		
		//如果文件的checksum为null，则更新
		if(r.isFile() && fv.getChecksum()==null){
			updateChecksum(fv);
		}
		
		FileMeta meta = new FileMeta();
		meta.setTid(r.getTid());
		meta.setFid(r.getRid());
		meta.setPath(PathName.appendDelimiter(parentPath) + fv.getTitle());
		meta.setDir(r.isFolder());
		meta.setPfid(r.getBid());
		meta.setMimetype(r.getFileType());
		meta.setChecksum(fv.getChecksum());
		meta.setUploadDevice(fv.getDevice()); 
		meta.setFver(fv.getVersion());
		meta.setSize(String.valueOf(fv.getSize()));
		meta.setName(fv.getTitle());
		meta.setUploadTime(fv.getEditTime());
		meta.setMtime(fv.getEditTime()==null?0:fv.getEditTime().getTime());
		meta.setUploadUser(fv.getEditor());
		return meta;
	}

	private FileMeta assembleDdoc(Resource r, String parentPath) {
		FileMeta meta = new FileMeta();
		meta.setTid(r.getTid());
		meta.setFid(r.getRid());
		meta.setPath(PathName.appendDelimiter(parentPath) + r.getTitle() + ".ddoc");
		meta.setDir(r.isFolder());
		meta.setPfid(r.getBid());
		meta.setMimetype(r.getFileType());
		meta.setChecksum("");
		meta.setUploadDevice("");
		meta.setFver(r.getLastVersion());
		meta.setSize(String.valueOf(r.getSize()));
		meta.setName(r.getTitle() + ".ddoc");
		meta.setUploadTime(r.getCreateTime());
		meta.setMtime(r.getLastEditTime() == null ? 0 : r.getLastEditTime().getTime());
		meta.setUploadUser(r.getCreator());
		return meta;
	}

	private FileMeta assemble(Resource r, String parentPath){
		FileVersion fv = fileVersionService.getFileVersion(r.getRid(), r.getTid(), r.getLastVersion());
		return assemble(r, fv, parentPath);
	}
	
	/**
	 * 从clb查询checksum更新到数据库中
	 * @param fv
	 */
	private void updateChecksum(FileVersion fv){
		DocMetaInfo meta = null;
		try{
		    meta = storage.getDocMeta(fv.getClbId(), String.valueOf(fv.getClbVersion()));
		}catch(CLBException e){
			LOG.error("clb file not found. rid="+ fv.getRid()+",editor="+fv.getEditor()+
					",clbId="+fv.getClbId() + ",clbVersion=" +fv.getClbVersion() + " message:"+e.getMessage());
			return;
		}
		if(meta!=null){
			fv.setChecksum(meta.md5);
			fileVersionService.update(fv.getId(), fv);
		}
	}

	private Map<Integer,String> getChecksumMap(int tid, int folderRid){
		Map<Integer,String> map = new HashMap<Integer,String>();
		List<DFile> list = resourceDao.getDescendantsChecksum(tid, folderRid);
		for(DFile item : list){
			map.put(item.getRid(), item.getChecksum());
		}
		return map;
	}
	
	@Autowired
	FileVersionService fileVersionService;
	@Autowired
	PageVersionService pageVersionService;
	@Autowired
	IResourceService resourceService;
	@Autowired
	FolderPathService folderPathService;
	@Autowired
	private FileStorage storage;
	@Autowired
	ResourceDao resourceDao;
}
