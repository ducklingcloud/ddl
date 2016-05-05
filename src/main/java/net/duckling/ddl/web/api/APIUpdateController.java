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

package net.duckling.ddl.web.api;

import java.io.FileInputStream;
import java.io.FileNotFoundException;

import javax.servlet.ServletContext;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import net.duckling.ddl.common.VWBContainer;
import net.duckling.ddl.common.VWBContainerImpl;
import net.duckling.ddl.util.JsonUtil;

import org.apache.log4j.Logger;
import org.json.simple.JSONObject;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;



import com.thoughtworks.xstream.XStream;

/**
 * API调用的服务程序
 * @date 2011-8-22
 * @author xiejj@cnic.cn
 */
@Controller
@RequestMapping("/api/update")
public class APIUpdateController extends APIBaseController {
	
	@SuppressWarnings("unchecked")
	@RequestMapping
	public void service(HttpServletRequest request, HttpServletResponse response){
		VWBContainer container =VWBContainerImpl.findContainer();
		AppUpdate updateInfo = getUpdateInfo(request);
		JSONObject object = new JSONObject();
		if (updateInfo!=null){
			object.put("found", true);
			object.put("appName", updateInfo.getAppName());
			object.put("version", updateInfo.getVersion());
			object.put("changeNotes", updateInfo.getChangeNotes());
			object.put("updateTime", updateInfo.getUpdateTime());
			object.put("url",container.getBaseURL()+"/apks/"+updateInfo.getFileName());
		}else{
			object.put("found", false);
		}
		JsonUtil.writeJSONObject(response, object);
	}
	private synchronized AppUpdate getUpdateInfo(HttpServletRequest request){
		if (!loaded){
			try {
				updateInfo = loadUpdate(request.getSession().getServletContext());
			} catch (FileNotFoundException e) {
				LOGGER.error("apk update file(/apks/apps.xml) could not be found.");
			}
		}
		return updateInfo;
	}
	private static final Logger LOGGER = Logger.getLogger(APIUpdateController.class);
	private AppUpdate updateInfo;
	private boolean loaded=false;
	private AppUpdate loadUpdate(ServletContext context) throws FileNotFoundException{
		loaded = true;
		XStream xstream = new XStream();
		xstream.autodetectAnnotations(true);
		xstream.processAnnotations(AppUpdate.class);
		String configFileName = context.getRealPath("/apks/apps.xml");
		return (AppUpdate)xstream.fromXML(new FileInputStream(configFileName));
	}

}
