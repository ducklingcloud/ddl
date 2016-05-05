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
package net.duckling.ddl.web.controller.pan;

import java.io.UnsupportedEncodingException;
import java.net.URLDecoder;
import java.net.URLEncoder;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import net.duckling.ddl.common.VWBSession;
import net.duckling.ddl.constant.LynxConstants;
import net.duckling.ddl.service.pan.PanAclUtil;
import net.duckling.ddl.service.url.URLGenerator;
import net.duckling.ddl.service.url.UrlPatterns;
import net.duckling.ddl.service.user.AoneUserService;
import net.duckling.ddl.service.user.SimpleUser;
import net.duckling.ddl.util.JsonUtil;
import net.duckling.ddl.web.interceptor.access.RequirePermission;
import net.duckling.meepo.api.IPanService;
import net.duckling.meepo.api.PanAcl;

import org.apache.commons.lang.StringUtils;
import org.apache.log4j.Logger;
import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

import cn.cnic.cerc.dlog.client.WebLog;

import com.meepotech.sdk.MeePoException;
import com.meepotech.sdk.MeePoException.AlreadyExists;
import com.meepotech.sdk.MeePoMeta;

@Controller
@RequirePermission(authenticated = true)
@RequestMapping("/pan/fileMove")
public class PanFileMoveController {
	private static final Logger LOG = Logger.getLogger(PanFileMoveController.class);
	private static final int SUCCESS = 0;
	private static final int WARNING = 1;
	private static final int ERROR = 2;
	
	@Autowired
    private IPanService service;
	@Autowired
	private URLGenerator urlGenerator;
	@Autowired
    private AoneUserService aoneUserService;
	@WebLog(method = "PanMove", params = "originalRid,targetRid")
	@RequestMapping(params="func=move")
	public void moveFileTo(HttpServletRequest request, HttpServletResponse response, 
			@RequestParam("originalRid")String originalRid,
			@RequestParam("targetRid")String targetRid) {
		originalRid = decode(originalRid);
		targetRid = decode(targetRid);
		if (isMovingToParent(originalRid, targetRid)) {
			writeResponse(response, WARNING, "您要移动的文件已经存在于目标路径");
			return ;
		}
		if (originalRid.equals(targetRid)) { // 1.不能移动到自身
			writeResponse(response, ERROR, "不能将文件夹移动到自身");
			return ;
		}
		if(isMovingToDescendant(originalRid,targetRid)){
			writeResponse(response, WARNING, "不能将文件夹移动到其子目录中");
			return ;
		}
		try {
			MeePoMeta  org = service.ls(PanAclUtil.getInstance(request), originalRid, false);
			boolean result = service.mv(PanAclUtil.getInstance(request), originalRid, targetRid);
			if(result){
				
				String url = urlGenerator.getAbsoluteURL(UrlPatterns.PAN_VIEW,encode(targetRid) , null);
				int index = targetRid.lastIndexOf("/")+1;
				String targetPathString = targetRid.substring(index);
				writeResponse(response, SUCCESS, "“" + org.name + "”成功移至文件夹 <a href=\"" + url + "\">" + getMoveTargName(targetPathString) + "</a>");
			}else{
				writeResponse(response, ERROR, "移动失败");
			}
		} catch (MeePoException e) {
			String message = "移动失败";
			if(e instanceof AlreadyExists){
				message = "文档已存在于目标路径中，请核对都再试";
			}
			writeResponse(response, ERROR, message);
			LOG.error("", e);
		}
	}
	
	private String getMoveTargName(String name){
		if(StringUtils.isEmpty(name)){
			return "所有文件";
		}else{
			return name;
		}
	}
	
	
	private boolean isMovingToDescendant(String originalRid, String targetRid) {
		return targetRid.startsWith(originalRid+"/");
	}


	private boolean isMovingToParent(String originalRid, String targetRid) {
		int index = originalRid.lastIndexOf("/");
		String orPar = originalRid.substring(0,index);
		return targetRid.equals(orPar);
	}

	@WebLog(method = "PanMoves", params = "originalRids,targetRid")
	@RequestMapping(params="func=moveSelected")
	public void moveSelected(HttpServletRequest request, HttpServletResponse response,
			@RequestParam("originalRids")String originalRidsString,
			@RequestParam("targetRid")String targetRid) {
		targetRid = decode(targetRid);
		String[] originalRidsStrings = originalRidsString.split(",");
		if (originalRidsStrings == null||originalRidsStrings.length==0) {
			writeResponse(response, ERROR, "没有要移动的文件");
			return;
		}
		if(originalRidsStrings.length==1){
			//只有一个移动文件时
			moveFileTo(request, response, originalRidsStrings[0], targetRid);
			return;
		}
		List<String> originalRids = new ArrayList<String>();
		for(String s : originalRidsStrings){
			originalRids.add(decode(s));
		}
		// 要移动的可能不仅仅是同级文档，所以需要循环判断
		List<String> ridsToMove = new ArrayList<String>();
		for (int originalRidIndex = 0; originalRidIndex < originalRids.size(); originalRidIndex++) {
			if (!isMovingToParent(originalRids.get(originalRidIndex), targetRid)) {
				ridsToMove.add(originalRids.get(originalRidIndex));
			}
		}
		if (ridsToMove.size() != originalRids.size()) {
			originalRids = ridsToMove;
		}
		if (originalRids.size() == 0) {
			writeResponse(response, WARNING, "文档已存在于目标路径中");
			return ;
		}
		for (String originalRid : originalRids) {
			if (originalRid.equals(targetRid)) { // 1.不能移动到自身
				writeResponse(response, ERROR, "不能将文件夹移动到自身");
				return ;
			}
			
			// 2.不能移动到自身的子文件夹中
			for(String originalRidd : originalRids) {
				if (isMovingToDescendant(originalRidd, targetRid)) {
					writeResponse(response, ERROR, "不能将文件夹移动到其子目录中");
					return ;
				}
			}
		}
		PanAcl panAcl = PanAclUtil.getInstance(request);
		int count = 0;
		for(String rid : originalRids){
			try {
				boolean result = service.mv(panAcl, rid, targetRid);
				if(!result){
					count++;
				}
			} catch (MeePoException e) {
				count++;
				LOG.error("", e);
			}
		}
		String url = urlGenerator.getAbsoluteURL(UrlPatterns.PAN_VIEW, targetRid, null);
		int index = targetRid.lastIndexOf("/")+1;
		String targetPathString = targetRid.substring(index);
		if(count==0){
			writeResponse(response, SUCCESS, "已成功移至文件夹 <a href=\"" + url + "\">" + getMoveTargName(targetPathString) + "</a>");
		}else if(count == originalRids.size()){
			writeResponse(response, ERROR, "全部移动失败，请核对都再试");
		}else{
			writeResponse(response, WARNING, "已部分成功移至文件夹 <a href=\"" + url + "\">" + getMoveTargName(targetPathString)+ "</a>");
		}
	}
	
	private String decode(String s) {
        try {
            return URLDecoder.decode(s, "UTF-8");
        } catch (UnsupportedEncodingException e) {
            return "";
        }
    }
	
	private String encode(String s){
		try {
			return URLEncoder.encode(s, "UTF-8");
		} catch (UnsupportedEncodingException e) {
			return s;
		}
	}
	
	private static void writeResponse(HttpServletResponse response, int state, String message) {
		JSONObject msg = new JSONObject();
		msg.put("state", state);
		msg.put("msg", message);
		JsonUtil.writeJSONObject(response, msg);
	}
	@WebLog(method = "moveList", params = "rid,originalRid")
	@RequestMapping(params="func=list")
	public void list(HttpServletRequest request, HttpServletResponse response, 
			@RequestParam("rid") String rid,
			@RequestParam("originalRid") String originalRid) {
		originalRid = decode(originalRid);
		if("/".equals(rid)){
			// 根文件夹需要特殊处理
			JSONArray rootArray = new JSONArray();
			JSONObject rootJsonObject = new JSONObject();
			rootJsonObject.put("data", "全部文件");
			JSONObject attr = new JSONObject();
			attr.put("rid", "node_/");
			rootJsonObject.put("attr", attr);
			// 开始写root的子文件（夹）
			JSONArray childrenJson = new JSONArray();
			try {
				PanAcl panAcl = PanAclUtil.getInstance(request);
				MeePoMeta root = service.ls(panAcl, rid, true);
				List<PanResourceBean> childrenList = getChildren(root, aoneUserService.getSimpleUserByUid(VWBSession.getCurrentUid(request)));
				if(childrenList.isEmpty()){
					attr.put("rel", "default");
				}else{
					String ridToDeal = null;
					if (StringUtils.isNotEmpty(originalRid)) {
						List<PanResourceBean> parentResources = getResourcePath(originalRid);
						if(parentResources.size() > 1) {
							ridToDeal = parentResources.get(0).getRid();
							PanResourceBean lastResource = parentResources.get(parentResources.size() - 2);
							JSONArray lastJsonArray = getChildrenJSONArray(lastResource.getRid(),panAcl);
							for (int index = parentResources.size() - 2; index >= 0 ; index--) {
								PanResourceBean resource = parentResources.get(index);
								JSONObject tmpObject = resourceToJSONObject(resource.getRid(), true);
								tmpObject.put("children", lastJsonArray);
								if (index == 0) {
									childrenJson.add(tmpObject);
								} else {
									lastJsonArray = getChildrenJSONArray(parentResources.get(index - 1).getRid(), resource.getRid(),panAcl);
									lastJsonArray.add(tmpObject);
								}
							}
						}
					}
					for (PanResourceBean child : childrenList) {
						if (child.getRid().equals(ridToDeal)) {
							// 跳过该目录
							continue;
						}
						childrenJson.add(resourceToJSONObject(child.getRid(), false));
					}
					
					rootJsonObject.put("children", childrenJson);
					rootJsonObject.put("state", "open");
					attr.put("rel", "folder");
					
				}
			} catch (MeePoException e) {
				LOG.error("",e);
			}
			rootArray.add(rootJsonObject);
			JsonUtil.writeJSONObject(response, rootArray);
		}else{
			rid = decode(rid);
			JSONArray childrenJson = getChildrenJSONArray(rid,PanAclUtil.getInstance(request));
			JsonUtil.writeJSONObject(response, childrenJson);
		}
	}
	
	private List<PanResourceBean> getResourcePath(String originalRid) {
		List<PanResourceBean> result = new ArrayList<PanResourceBean>();
		int index = originalRid.lastIndexOf("/");
		if(index>0){
			String[] sp = originalRid.split("/");
			StringBuilder sb = new StringBuilder();
			for(int i=0;i<sp.length;i++){
				if(i==0){
					sb.append("/");
					continue;
				}
				sb.append(sp[i]);
				PanResourceBean bean = new PanResourceBean();
				bean.setRid(encode(sb.toString()));
				bean.setTitle(sp[i]);
				result.add(bean);
				sb.append("/");
			}
		}
		return result;
	}
	
	private JSONArray getChildrenJSONArray(String rid,PanAcl panAcl) {
		JSONArray childrenJson = new JSONArray();
		MeePoMeta root;
		try {
			root = service.ls(panAcl, decode(rid), true);
			List<PanResourceBean> beans = getChildren(root, aoneUserService.getSimpleUserByUid(panAcl.getUid()));
			for (PanResourceBean child : beans) {
				childrenJson.add(resourceToJSONObject(child.getRid(), false));
			}
		} catch (MeePoException e) {
			LOG.error("", e);
		}
		return childrenJson;
	}

	private JSONArray getChildrenJSONArray(String rid, String ignoreRid,PanAcl panAcl) {
		JSONArray childrenJson = new JSONArray();
		MeePoMeta root;
		try {
			root = service.ls(panAcl, decode(rid), true);
			List<PanResourceBean> beans = getChildren(root, aoneUserService.getSimpleUserByUid(panAcl.getUid()));
			for (PanResourceBean child : beans) {
				if(child.getRid().equals(ignoreRid)){
					continue;
				}
				childrenJson.add(resourceToJSONObject(child.getRid(), false));
			}
		} catch (MeePoException e) {
			LOG.error("", e);
		}
		return childrenJson;
	}

	private JSONObject resourceToJSONObject(String rid, boolean open) {
		rid = decode(rid);
		JSONObject result = new JSONObject();
		int index = rid.lastIndexOf("/");
		String title = rid.substring(index+1);
		result.put("data", title);
		JSONObject attr = new JSONObject();
		attr.put("rid", "node_" + encode(rid));
		attr.put("rel", "folder");
		result.put("attr", attr);
		if (open) {
			result.put("state", "open");
		} else {
			result.put("state", "closed");
		}
		return result;
	}
	
	
	private List<PanResourceBean> getChildren(MeePoMeta root ,SimpleUser user){
		List<PanResourceBean> result = new ArrayList<PanResourceBean>();
		if(root==null||root.contents==null){
			return result;
		}
		MeePoMeta[] ms = root.contents;
		for(MeePoMeta m : ms){
			result.add(MeePoMetaToPanBeanUtil.transfer(m, user));
		}
		filterFolder(result);
		PanResourceBeanSort.sort(result, PanResourceBeanSort.TYPE_TIME_DESC);
		return result;
	}
	
	private void filterFolder(List<PanResourceBean> beans){
		Iterator<PanResourceBean> it = beans.iterator();
		while(it.hasNext()){
			PanResourceBean bean = it.next();
			if(!LynxConstants.TYPE_FOLDER.equals(bean.getItemType())){
				it.remove();
			}
		}
	}
}
