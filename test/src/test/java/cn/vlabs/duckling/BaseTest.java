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

package cn.vlabs.duckling;

import java.io.IOException;
import java.lang.reflect.Method;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.Random;

import net.duckling.ddl.common.Site;
import net.duckling.ddl.common.VWBContainerImpl;
import net.duckling.ddl.common.VWBContext;
import net.duckling.ddl.common.VWBSite;
import net.duckling.ddl.constant.Attributes;
import net.duckling.ddl.constant.KeyConstants;
import net.duckling.ddl.service.team.Team;
import net.duckling.ddl.service.team.TeamService;

import org.apache.log4j.PropertyConfigurator;
import org.junit.BeforeClass;
import org.springframework.beans.factory.BeanFactory;
import org.springframework.core.io.ClassPathResource;
import org.springframework.core.io.Resource;


/**
 * @date Mar 2, 2011
 * @author xiejj@cnic.cn
 */
public class BaseTest {
	public static BeanFactory f;
    public static Random rand = new Random();
    private static SpringManager testBase;
	public static Site site;
	@BeforeClass
    public static void setUpContext() throws Exception {
        if (testBase == null) {
            testBase = new SpringManager();
            testBase.init();
            f = testBase.getFactory();
            intiSite(f);
            VWBContext.setCurrentTid(site.getId());
        }
    }
	
	
	static {
        Resource rootClassRes = new ClassPathResource("/");
        try {
            String logRootDir = rootClassRes.getFile().getPath();
            System.setProperty("catalina.base", logRootDir);
            System.out.println(logRootDir);
            PropertyConfigurator.configure(logRootDir + "/log4j.properties");
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
	
	static void intiSite(BeanFactory factory){
		VWBContainerImpl container = (VWBContainerImpl) factory
				.getBean(Attributes.CONTAINER_KEY);
		VWBContainerImpl.setBeanFactory(f);
		String name = "ddlUnitTestTeam";
		TeamService teamService = f.getBean(TeamService.class);
		Team team = teamService.getTeamByName(name);
		int tid = 0;
		if(team==null){
			Map<String, String> params = new HashMap<String,String>();
			params.put(KeyConstants.SITE_DISPLAY_NAME,name);
			params.put(KeyConstants.SITE_NAME_KEY,name);
			params.put(KeyConstants.SITE_CREATOR,"zhonghui@cnic.cn");
			params.put(KeyConstants.SITE_DESCRIPTION,"test");
			params.put(KeyConstants.TEAM_TYPE,"common");
			params.put(KeyConstants.TEAM_ACCESS_TYPE,Team.ACCESS_PUBLIC);
			params.put(KeyConstants.TEAM_DEFAULT_MEMBER_AUTH,Team.AUTH_EDIT);
			tid = teamService.creatTeam("", params,new Date(),false);
		}else{
			tid = team.getId();
		}
		site = container.loadSite(tid);
		initSiteArge(site);
	}
	
	private static void initSiteArge(Site site){
		Method[] methods =VWBSite.class.getMethods();
		for(Method method : methods){
			String methodName = method.getName();
			if(methodName.startsWith("get")&&(method.getGenericParameterTypes()==null||method.getGenericParameterTypes().length==0)){
				Class re = method.getReturnType();
				if(re.isInterface()){
					try {
						Object o = method.invoke(site);
					} catch (Exception e) {
						System.out.println("----"+method+"-----");
						e.printStackTrace();
					}
				}else{
					try {
					} catch (Exception e) {
						System.out.println("----"+method+"-----");
						e.printStackTrace();
					}
				}
			}
		}
	}
	
	private static boolean extend(Class[] s,Class c){
		if(s!=null){
			for(Class o:s){
				if(o.equals(c)){
					return true;
				}
			}
		}
		return false;
	}
	
}
