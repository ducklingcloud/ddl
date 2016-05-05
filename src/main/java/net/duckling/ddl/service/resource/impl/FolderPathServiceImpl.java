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
package net.duckling.ddl.service.resource.impl;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import net.duckling.ddl.constant.LynxConstants;
import net.duckling.ddl.service.resource.FolderPath;
import net.duckling.ddl.service.resource.FolderPathService;
import net.duckling.ddl.service.resource.PathName;
import net.duckling.ddl.service.resource.Resource;
import net.duckling.ddl.util.PaginationBean;

import org.apache.commons.collections.CollectionUtils;
import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;
/**
 * 目录关系维持service
 * 
 * @author zhonghui
 *
 */
@Service
public class FolderPathServiceImpl implements FolderPathService {
	private static final Logger LOG = Logger.getLogger(FolderPathServiceImpl.class);
	private static String getPattenName(String fileName,String itemType,String add) {
		if(!LynxConstants.TYPE_FILE.equals(itemType)){
			return transfFileName(fileName)+add;
		}
		int index;
		if (!fileName.toLowerCase().endsWith(".dsf")){
			index = fileName.lastIndexOf('.');
		}else{
			index = fileName.lastIndexOf('.',fileName.length()-5);
		}
		if(index<=0){
			return transfFileName(fileName)+add;
		}else{
			return transfFileName(fileName.substring(0, index))+add+fileName.substring(index);
		}
	}
	private static String transfFileName(String s){
		StringBuilder sb = new StringBuilder();
		String ss = "(){}[]*$^+|?.\\";
		for(int i =0;i<s.length();i++){
			char a = s.charAt(i);
			if(ss.contains(a+"")){
				sb.append("\\"+a);
			}else{
				sb.append(a);
			}
		}
		return sb.toString();
	}

	@Autowired
	private FolderPathDAO folderPathDAO;
	@Autowired
	private ResourcePathDAO resourcePathDAO;
	
	/**
	 * 获取所有rid的子孙节点与rid的祖先节点的关系 
	 * @param rid
	 * @param ancestors
	 * @param descendants
	 * @return
	 */
	private List<FolderPath> generateRelations(int rid, List<FolderPath> ancestors, List<FolderPath> descendants) {
	        List<FolderPath> relations = new LinkedList<FolderPath>();
	        if (!CollectionUtils.isEmpty(ancestors) && !CollectionUtils.isEmpty(descendants)) {
	            for (FolderPath a : ancestors) {
	                if (rid != a.getAncestorRid()) { //当前子树根节点和子树元素的关系保持不变
	                    for (FolderPath d : descendants) {
	                    	FolderPath relation = new FolderPath();
	                        relation.setAncestorRid(a.getAncestorRid());
	                        relation.setTid(d.getTid());
	                        relation.setRid(d.getRid());
	                        relation.setLength(a.getLength() + d.getLength() + 1); //长度需要+1
	                        relations.add(relation);
	                    }
	                }
	            }
	        }
	        return relations;
	    }

	private String getQueryName(String fileName,String itemType,String add) {
		if(!LynxConstants.TYPE_FILE.equals(itemType)){
			return fileName+add;
		}
		if (fileName==null){
			fileName="";
		}
		//Security file
		int index =0;
		if (!fileName.toLowerCase().endsWith(".dsf")){
			index = fileName.lastIndexOf(".");
			
		}else{
			index = fileName.lastIndexOf('.', fileName.length()-5);
		}
		if(index<=0){
			return fileName+add;
		}else{
			return fileName.substring(0, index)+add+fileName.substring(index);
		}
	}

	private String getResourceName(List<Resource> rs ,String fileName,String itemType){
		if(rs==null||rs.isEmpty()){
			return fileName;
		}
		int max = 0;
		String reg = getPattenName(fileName,itemType, "\\((\\d+)\\)");
		try{
			Pattern pattern = Pattern.compile(reg,Pattern.CASE_INSENSITIVE);
			for(Resource file : rs){
				Matcher ma = pattern.matcher(file.getTitle());
				if(ma.matches()){
					String id = ma.group(1);
					try{
						int tmp = Integer.parseInt(id);
						if(tmp>max){
							max=tmp;
						}
					}catch(RuntimeException e){
						
					}
				}
			}
			
		}catch(RuntimeException e){
			LOG.error("",e);
			return fileName;
		}
		max++;
		return getQueryName(fileName,itemType,"("+max+")" );
	}

	public void setFolderPathDAO(FolderPathDAO pathDao){
		this.folderPathDAO = pathDao;
	}
	@Override
	public boolean create(int parentRid,int rid,int tid) {
		if(parentRid==0){
			List<FolderPath> p = folderPathDAO.getAncestor(tid,parentRid);
			if(p==null||p.isEmpty()){
				folderPathDAO.create(new FolderPath(0,tid,0,0));
			}
		}
		List<FolderPath> parent = folderPathDAO.getPath(tid,parentRid);
		if(parent!=null&&parent.size()>0){
			for(FolderPath p:parent){
				p.setRid(rid);
				p.setLength(p.getLength()+1);
			}
		}else{
			parent = new ArrayList<FolderPath>();
		}
		FolderPath p = new FolderPath(rid,tid,rid,0);
		parent.add(p);
		return folderPathDAO.insertBatch(parent);
	}

	@Override
	public boolean delete(int tid,int rid) {
		//删除子节点
		List<FolderPath> children = folderPathDAO.getDescendants(tid,rid);
		List<Integer> i = new ArrayList<Integer>();
		for(FolderPath p : children){
			i.add(p.getRid());
		}
		folderPathDAO.deleteByRids(i);
		//删除自己
		folderPathDAO.deleteByRid(rid);
		return true;
	}
	@Override
	public boolean delete(int tid,List<Integer> rids){
		return folderPathDAO.delete(tid,rids);
	}
	 @Override
	public List<FolderPath> getChildrenPath(int tid,int rid) {
		return folderPathDAO.getChildren(tid,rid);
	}
	
	@Override
	public PaginationBean<Resource> getChildren(int tid, int rid, String fileType, String orderStr, int begin,int size, String keyWord) {
		return resourcePathDAO.getChildren(tid, rid, fileType, orderStr,begin, size, keyWord);
	}

	@Override
	public List<Resource> getChildrenFolder(int tid, int rid) {
		
		return resourcePathDAO.getChildrenFolder(tid, rid);
	}
	@Override
	public List<Resource> getChildren(int tid, int rid) {
		
		return resourcePathDAO.getChildren(tid, rid);
	}
	@Override
	public List<FolderPath> getDescendantsPath(int tid,int rid) {
		return folderPathDAO.getDescendants(tid,rid);
	}

	@Override
	public List<Resource> getDescendants(int tid, int rid) {
		return resourcePathDAO.getDescendants(tid,rid);
	}

	@Override
	public String getResourceName(int tid, int parentRid,String itemType, String name) {
		return getResourceName(tid, parentRid, itemType, name, null);
	}
	@Override
	public FolderPath getParent(int rid) {
		return folderPathDAO.getParent(rid);
	}
	@Override
	public Resource getParentResource(int rid) {
		return resourcePathDAO.getParent(rid);
	}
	@Override
	public List<FolderPath> getPath(int tid,int rid) {
		return folderPathDAO.getPath(tid,rid);
	}
	@Override
	public FolderPath get(int tid,int rid, int ancestorRid) {
		return folderPathDAO.get(tid,rid,ancestorRid);
	}
	
	@Override
	public List<Resource> getResourcePath(int rid) {
		if(rid==0){
			return Collections.emptyList();
		}
		return resourcePathDAO.getPath(rid);
	}
	
	@Override
	public String getPathString(int rid){
		if(rid==0){
			return PathName.DELIMITER;
		}
		List<Resource> resList = this.getResourcePath(rid);
		StringBuilder sb = new StringBuilder();
		for(Resource item : resList){
			sb.append(PathName.DELIMITER + item.getTitle());
		}
		return sb.toString();
	}

	@Override
	public boolean move(int tid,int orginalRid, int targetRid) {
		//获取自己所有祖先
		List<FolderPath> ancestors = folderPathDAO.getAncestor(tid,orginalRid);
		//获取所有子孙
		List<FolderPath> children = folderPathDAO.getDescendants(tid,orginalRid);
		
		List<FolderPath> relations = generateRelations(orginalRid, ancestors, children);
		//删除所有自己和子孙节点中与祖先节点有关系的目录信息
		folderPathDAO.delete(relations);
		//获取目标所有祖先
		List<FolderPath> newAncestors = folderPathDAO.getAncestor(tid,targetRid);
		relations = generateRelations(orginalRid, newAncestors, children);
		return folderPathDAO.insertBatch(relations);
	}
	@Override
	public void move(int tid, List<Resource> srcResources, int targetRid) {
		List<Integer> orginalRids = new ArrayList<Integer>();
		for(Resource r : srcResources){
			orginalRids.add(r.getRid());
		}
		move(orginalRids, tid, targetRid);
	}
	public void move(List<Integer> orginalRids,int tid,int targetRid){
		List<FolderPath> ancestors = folderPathDAO.getAncestor(tid, orginalRids);
		Map<Integer,List<FolderPath>> ancestorsMap = new HashMap<Integer,List<FolderPath>>();
		for(FolderPath p : ancestors){
			List<FolderPath> lp = ancestorsMap.get(p.getRid());
			if(lp == null){
				lp = new ArrayList<FolderPath>();
				ancestorsMap.put(p.getRid(), lp);
			}
			lp.add(p);
		}
		List<FolderPath> children = folderPathDAO.getDescendants(tid,orginalRids);
		Map<Integer,List<FolderPath>> chilMap = new HashMap<Integer,List<FolderPath>>();
		for(FolderPath p : children){
			List<FolderPath> lp = chilMap.get(p.getAncestorRid());
			if(lp == null){
				lp = new ArrayList<FolderPath>();
				chilMap.put(p.getAncestorRid(), lp);
			}
			lp.add(p);
		}
		List<FolderPath> relations = new ArrayList<FolderPath>();
		for(Entry<Integer, List<FolderPath>> entry : ancestorsMap.entrySet()){
			List<FolderPath> child = chilMap.get(entry.getKey());
			List<FolderPath> re = generateRelations(entry.getKey(), entry.getValue(), child);
			relations.addAll(re);
		}
		folderPathDAO.delete(relations);
		List<FolderPath> newAncestors = folderPathDAO.getAncestor(tid,targetRid);
		List<FolderPath> childrenPath = new ArrayList<FolderPath>();
		for(Entry<Integer,List<FolderPath>> entry : chilMap.entrySet()){
			List<FolderPath> c = generateRelations(entry.getKey(), newAncestors, entry.getValue());
			childrenPath.addAll(c);
		}
		folderPathDAO.insertBatch(childrenPath);
	}

	@Override
	public List<FolderPath> query(int rid, int length) {
		return folderPathDAO.query(rid, length);
	}
	@Override
	public List<Resource> getResourceByName(int tid, int parentRid, String itemType, String name) {
		return resourcePathDAO.getResourceByName(tid, parentRid, itemType, name);
	}
	
	@Override
	public List<Resource> getResourceByName(int tid, int parentRid, String name) {
		return resourcePathDAO.getResourceByName(tid, parentRid, name);
	}
	
	@Override
	public PaginationBean<Resource> searchResource(int tid, int parentRid, String keyWord, String order, int begin,
			int size) {
		return resourcePathDAO.searchResource(tid, parentRid, keyWord,order, begin,size);
	}
	@Override
	public void updateResourceName(int tid, int destRid, List<Resource> rs) {
		List<Resource> destChildren = resourcePathDAO.getChildren(tid, destRid);
		if(destChildren==null||destChildren.isEmpty()){
			return;
		}
		Map<String,Set<String>> destName= new HashMap<String,Set<String>>();
		for(Resource r : destChildren){
			Set<String> s = destName.get(r.getItemType());
			if(s==null){
				s = new HashSet<String>();
				destName.put(r.getItemType(), s);
			}
			s.add(r.getTitle());
		}
		for(Resource r : rs){
			Set<String> s = destName.get(r.getItemType());
			if(s==null){
				s = new HashSet<String>();
				destName.put(r.getItemType(), s);
				s.add(r.getTitle());
				continue;
			}
			if(s.contains(r.getTitle())){
				String newTitle = getTitle(r.getTitle(),r.getItemType());
				while(s.contains(newTitle)){
					newTitle = getTitle(newTitle,r.getItemType());
				}
				r.setTitle(newTitle);
				s.add(newTitle);
			}else{
				s.add(r.getTitle());
			}
		}
		
		
	}
	private static final Pattern pattern =Pattern.compile( ".*\\((\\d+)\\)$");
	private String getTitle(String fileName,String itemType) {
		String end = "";
		if(LynxConstants.TYPE_FILE.equals(itemType)){
			int index = fileName.lastIndexOf(".");
			if(index>0){
				end = fileName.substring(index);
				fileName = fileName.substring(0,index);
			}
		}
		Matcher ma = pattern.matcher(fileName);
		if(ma.matches()){
			String i = ma.group(1);
			try{
				int id = Integer.parseInt(i);
				int index = fileName.lastIndexOf("("+id+")");
				id++;
				return fileName.substring(0, index)+"("+id+")"+end;
			}catch (Exception e) {
				return fileName+"(1)"+end;
			}
		}
		return fileName+"(1)"+end;
	}
	@Override
	public String getResourceName(int tid, int parentRid, String itemType, String name, int[] filterRid) {
		if(StringUtils.isEmpty(name)&&LynxConstants.TYPE_FOLDER.equals(itemType)){
			name = "新建文件夹";
		}else if(StringUtils.isEmpty(name)&&LynxConstants.TYPE_PAGE.equals(itemType)){
			name = LynxConstants.DEFAULT_DDOC_TITLE;
		}
		//判断是否有重名
		List<Resource> rs = LynxConstants.TYPE_PAGE.equals(itemType) ? getResourceByName(tid, parentRid, itemType, name) 
				: getResourceByName(tid, parentRid, name);
		filter(rs, filterRid);
		if(rs==null||rs.isEmpty()){
			return name;
		}
		
		String queryName = getQueryName(name,itemType,"%");
		List<Resource> f =  resourcePathDAO.getFolderByStartName(tid,parentRid,queryName,
										LynxConstants.TYPE_PAGE.equals(itemType) ? itemType : null);
		
		filter(f, filterRid);
		boolean re = false;
		if(f!=null){
			for(Resource r :f){
				if(name.equalsIgnoreCase(r.getTitle())){
					re = true;
					break;
				}
			}
			if(re){
				name = getResourceName(f, name,itemType);
			}
		}
		return name;
	}
	
	@Override
	public Resource getResourceByPath(int tid, PathName pn){
		//根目录
		if(PathName.DELIMITER.equals(pn.getPath())){
			Resource r = new Resource();
			r.setTitle("");
			r.setItemType(LynxConstants.TYPE_FOLDER);
			return r; 
		}
		//一级目录
		if(pn.getLength()==1){
			List<Resource> r = resourcePathDAO.getResourceByTitle(tid, 0, pn.getName());
			return r.size()>0?r.get(0) : null;
		}
		String parentFolder = pn.getNames().get(pn.getLength()-2);

		List<Resource> resList = resourcePathDAO.getPathsByTitle(tid, parentFolder);
		List<List<Resource>> paths = new ArrayList<List<Resource>>();
		//路径分组
		List<Resource> p = null;
		for(Resource item : resList){
			if(item.getBid()==0){
				p = new ArrayList<Resource>();
				paths.add(p);
			}
			p.add(item);
		}
		List<Resource> result = null;
		//从路径组中选择匹配项
		for(List<Resource> item : paths){
			if(pathMatch(item, pn.getNames())){
				result = item;
				break;
			}
		}
		if(result == null){ 
			return null;
		}
		
		Resource parentRes = result.get(result.size()-1);
		List<Resource> r = resourcePathDAO.getResourceByTitle(tid, parentRes.getRid(), pn.getName());
		return r.size()>0?r.get(0) : null;
	}
	@Override
	public Resource getResourceByPath(int tid, String path){
		return getResourceByPath(tid, new PathName(path));
	}
	
	@Override
	public List<Resource> setResourceListPath(List<Resource> list, String parentPath){
		for(Resource item : list){
			item.setPath(PathName.appendDelimiter(parentPath) + item.getTitle());
		}
		return list;
	}
	
	@Override
	public List<Resource> setResourceListPath(List<Resource> list){
		for(Resource item : list){
			item.setPath(this.getPathString(item.getRid()));
		}
		return list;
	}
	
	/**
	 * 路径匹配
	 * @param list
	 * @param pathList
	 * @return
	 */
	private boolean pathMatch(List<Resource> list, List<String> pathList){
		int folderCount = pathList.size()-1;
		if(folderCount == list.size()){
			for(int i=0;i< pathList.size()-1; i++){
				if(! pathList.get(i).equals(list.get(i).getTitle())){
					break;
				}
				if(i==folderCount-1){
					return true;
				}
			}
		}
		return false;
	}

	private void filter(List<Resource> rs,int[] filterRid){
		if(filterRid==null||rs==null||rs.size()==0){
			return;
		}
		Iterator<Resource> it = rs.iterator();
		while(it.hasNext()){
			Resource r = it.next();
			for(int i : filterRid){
				if(i==r.getRid()){
					it.remove();
					continue;
				}
			}
		}
	}
}
