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

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import net.duckling.ddl.common.DucklingProperties;
import net.duckling.ddl.common.VWBContext;
import net.duckling.ddl.common.VWBSession;
import net.duckling.ddl.constant.ELayout;
import net.duckling.ddl.service.pan.PanAclUtil;
import net.duckling.ddl.service.pan.PanSpaceApplication;
import net.duckling.ddl.service.pan.PanSpaceApplicationService;
import net.duckling.ddl.service.resource.TeamSpaceSize;
import net.duckling.ddl.service.url.UrlPatterns;
import net.duckling.ddl.service.user.AoneUserService;
import net.duckling.ddl.util.FileSizeUtils;
import net.duckling.ddl.util.JsonUtil;
import net.duckling.ddl.web.controller.BaseController;
import net.duckling.ddl.web.interceptor.access.RequirePermission;
import net.duckling.meepo.api.IPanService;
import net.duckling.meepo.api.PanAcl;

import org.apache.log4j.Logger;
import org.json.JSONArray;
import org.json.JSONObject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.servlet.ModelAndView;

import cn.cnic.cerc.dlog.client.WebLog;

import com.meepotech.sdk.MeePoException;
import com.meepotech.sdk.MeePoUsage;

@Controller
@RequestMapping("/pan/applicationSpace")
@RequirePermission(authenticated = true)
public class PanApplicationSpaceController extends BaseController{
	private static final Logger LOG = Logger.getLogger(PanApplicationSpaceController.class);
	@Autowired
	private AoneUserService aoneUserService;
	@Autowired
    private IPanService service;
	@Autowired
	private PanSpaceApplicationService panSpaceApplicationService;
	@Autowired
	private DucklingProperties config;
	
	@WebLog(method = "PanApplicationSpace", params = "uid")
	@RequestMapping
	public ModelAndView display(HttpServletRequest request){
		MeePoUsage use = null;;
		try {
			use = service.usage(PanAclUtil.getInstance(request));
		} catch (MeePoException e) {
			LOG.error("", e);
		}
		TeamSpaceSize size = transfer(use);
		boolean canApp = false;
		if(canApplicat(use.quota,use.used)){
			canApp = true;
		}
		String uid = VWBSession.getCurrentUid(request);
		VWBContext context = VWBContext.createContext(request, UrlPatterns.T_TEAM_HOME);
		ModelAndView mv = layout(ELayout.LYNX_MAIN, context, "/jsp/pan/pan_application_space.jsp");
		List<PanSpaceApplication> records = panSpaceApplicationService.queryByUid(uid);
		List<Map<String,String>> res = new ArrayList<Map<String,String>>();
		long add = 0;
		for(PanSpaceApplication re : records){
			Map<String,String> map = new HashMap<String,String>();
			map.put("type", re.getTypeDisplay());
			long s = re.getNewSize() - re.getOriginalSize();
			map.put("size", FileSizeUtils.getFileSize(s));
			SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
			map.put("time", sdf.format(re.getApproveTime()));
			add+=s;
			res.add(map);
		}
		mv.addObject("userName", aoneUserService.getSimpleUserByUid(uid).getName());
		mv.addObject("canApply", canApp);
		mv.addObject("size", size);
		mv.addObject("records", res);
		mv.addObject("totalSize",FileSizeUtils.getFileSize(add));
		mv.addObject("teamType", "pan");
		return mv;
	}
	
	private TeamSpaceSize transfer(MeePoUsage use) {
		TeamSpaceSize result = new TeamSpaceSize();
		result.setTotal(use.quota);
		result.setUsed(use.used);
		return result;
	}

	public boolean canApplicat(long total,long used) {
		return (total-used)<=FileSizeUtils.ONE_GB*5;
	}
	@WebLog(method = "PanManualApply", params = "uid")
	@RequestMapping(params="func=manualApply")
	public void application(HttpServletRequest req,HttpServletResponse resp){
		JSONObject obj = new JSONObject();
		PanAcl acl = PanAclUtil.getInstance(req);
		MeePoUsage use = null;;
		try {
			use = service.usage(PanAclUtil.getInstance(req));
		} catch (MeePoException e) {
			e.printStackTrace();
		}
		
		if(!canApplicat(use.quota,use.used)){
			obj.put("status", false);
			obj.put("message", "您的空间还未到扩容条件，请核对后再申请！");
			JsonUtil.writeJSONObject(resp, obj);
			return;
		}
		//10G
		long s = FileSizeUtils.ONE_GB*10L;
		long newSize = s+use.quota;
		MeePoUsage newUsage = null;
		String rootToken;
		try {
			rootToken = "product".equals(config.getProperty("ddl.profile.env")) ? service.getRootToken("root", "cas__", "iphone") :
				service.getRootToken("root", "meiyoumima", "iphone");
			rootToken = service.getRootToken("root", "cas__", "iphone");
			newUsage = service.updateQuota(acl, rootToken, newSize);
			if(newUsage.quota==newSize){
				panSpaceApplicationService.add(newSize, use.quota, acl.getUid(), PanSpaceApplication.TYPE_MANUAL);
			}else{
				obj.put("status", false);
				obj.put("message", "扩容失败");
				JsonUtil.writeJSONObject(resp, obj);
				return;
			}
		} catch (MeePoException e) {
			LOG.error("", e);
			obj.put("status", false);
			obj.put("message", "扩容处理失败");
			JsonUtil.writeJSONObject(resp, obj);
			return;
		}
		obj.put("status", true);
		List<PanSpaceApplication> records = panSpaceApplicationService.queryByUid(acl.getUid());
		JSONArray arr = new JSONArray();
		long add = 0;
		for(PanSpaceApplication re : records){
			JSONObject map = new JSONObject();
			map.put("type", re.getTypeDisplay());
			long ss = re.getNewSize() - re.getOriginalSize();
			map.put("size", FileSizeUtils.getFileSize(ss));
			SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
			map.put("time", sdf.format(re.getApproveTime()));
			add+=ss;
			arr.put(map);
		}
		boolean canApp = false;
		if(canApplicat(newUsage.quota,newUsage.used)){
			canApp = true;
		}
		putNewSize(obj, newUsage);
		obj.put("totalSize", FileSizeUtils.getFileSize(newSize));
		obj.put("canApp", canApp);
		obj.put("records", arr);
		obj.put("totalAddSize", add);
		JsonUtil.writeJSONObject(resp, obj);
	}
	
	private void putNewSize(JSONObject obj,MeePoUsage newUsage){
		TeamSpaceSize nowSize = transfer(newUsage);
		JSONObject size = new JSONObject();
		size.put("percent", nowSize.getPercentDisplay());
		size.put("total", nowSize.getTotalDisplay());
		size.put("used", nowSize.getUsedDisplay());
		obj.put("totalShow", size);
	}
//	@RequestMapping(params="func=resize")
//	public void resize(HttpServletRequest req,HttpServletResponse resp) throws IOException{
//		PanAcl acl = PanAcl.getInstance(req);
//		String rootToken;
//		try {
//			rootToken = service.getRootToken("root", "cas__", "iphone");
//			service.updateQuota(acl, rootToken,  FileSizeUtils.ONE_GB*6L);
//		} catch (MeePoException e) {
//			// TODO Auto-generated catch block
//			e.printStackTrace();
//		}
//		resp.sendRedirect("applicationSpace");
//	}
}
