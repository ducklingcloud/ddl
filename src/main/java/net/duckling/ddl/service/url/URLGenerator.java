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
package net.duckling.ddl.service.url;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

import net.duckling.ddl.service.team.Team;
import net.duckling.ddl.service.team.TeamService;


public class URLGenerator {

    private TeamService teamService;
    
    private String baseUrl;
    
    public void setTeamService(TeamService teamService) {
        this.teamService = teamService;
    }

    public void setBaseUrl(String baseUrl) {
        this.baseUrl = baseUrl;
    }
    public String getBaseUrl(){
    	if(baseUrl.endsWith(":80")){
    		return baseUrl.replace(":80", "");
    	}
    	return baseUrl.replace(":80/", "/");
    }
    
    private DefaultURLConstructor systemURLMaker;
    
    private Map<Integer,DefaultURLConstructor> localURLMap = new ConcurrentHashMap<Integer,DefaultURLConstructor>();
    
    /**
     * @description 构造不需要TeamCode的相对URL,如%u/system/createTeam 
     * @example 已知 UrlPatterns.CONFIG_TEAM对应的模式值为"%u/system/configTeam?teamCode=%n" 且 contextPath = ddl
     *          那么 getURL(UrlPatterns.CONFIG_TEAM,"cerc","func=application") 
     *          返回的最终结果为 ddl/system/configTeam?teamCode=cerc&func=application
     * @param pattern URL的模式名,具体见UrlPatterns
     * @param nValue 如果选定模式中含有%n,则替换为nValue
     * @param params URL末尾的参数,该参数会用'&'或'?'与前面的值进行连接
     * @return 返回一个合成好的相对URL
     */
    public String getURL(String pattern,String nValue,String params){
        DefaultURLConstructor urlMaker = findUrlMaker();
        return urlMaker.makeURL(pattern, nValue, params, false);
    }
    
    /**
     * @description 构造某个团队下的U相对URL,会自动将该团队的TeamCode加入到URL中
     * @example 已知 UrlPatterns.T_PAGE对应的模式值为"%u/%t/page/%n" 且 tid=1的团队对应TeamCode为"cstnet" 且 contextPath = ddl
     *          那么 getURL(1, UrlPatterns.T_PAGE,"1001","func=hello") 
     *          返回的最终结果为 ddl/cstnet/page/1001?func=hello
     * @param tid 当前团队ID
     * @param pattern URL的模式名,都以T_为前缀,具体定义见UrlPatterns
     * @param nValue 如果选定模式中含有%n,则替换为nValue
     * @param params URL末尾的参数,该参数会用'&'或'?'与前面的值进行连接
     * @return 返回一个合成好的相对URL
     */
    public String getURL(int tid, String pattern, String nValue, String params) {
        DefaultURLConstructor urlMaker = findUrlMaker(tid);
        return urlMaker.makeURL(pattern, nValue, params, false);
    }
    
    /**
     * @description 构造一个包含域名的完整URL
     * @example 已知 UrlPatterns.CONFIG_TEAM对应的模式值为"%u/system/configTeam?teamCode=%n" 且 contextPath = ddl 且 baseDomain = 'http://www.escience.cn'
     *          那么 getAbsoluteURL(UrlPatterns.CONFIG_TEAM,"cerc","func=application") 
     *          返回的最终结果为 http://www.escience.cn/ddl/system/configTeam?teamCode=cerc&func=application
     * @param pattern URL的模式名,具体定义见UrlPatterns
     * @param nValue 如果选定模式中含有%n,则替换为nValue
     * @param params URL末尾的参数,该参数会用'&'或'?'与前面的值进行连接
     * @return 返回一个包含域名的完整URL
     */
    public String getAbsoluteURL(String pattern, String nValue, String params) {
        DefaultURLConstructor urlMaker = findUrlMaker();
        return urlMaker.makeURL(pattern, nValue, params, true);
    }
    
    /**
     * @description 构造某个团队下的U相对URL,该方法会用指定tid的团队的TeamCode替换模式中的%t
     * @example 已知 UrlPatterns.T_PAGE对应的模式值为"%u/%t/page/%n" 且 tid=1的团队对应TeamCode为"cstnet" 且 contextPath = ddl 且 baseDomain = 'http://www.escience.cn'
     *          那么 getAbsoluteURL(1, UrlPatterns.T_PAGE,"1001","func=hello") 
     *          返回的最终结果为 http://www.escience.cn/ddl/cstnet/page/1001?func=hello
     * @param tid 当前团队ID
     * @param pattern URL的模式名,都以T_为前缀,具体定义见UrlPatterns
     * @param nValue 如果选定模式中含有%n,则替换为nValue
     * @param params URL末尾的参数,该参数会用'&'或'?'与前面的值进行连接
     * @return 返回一个包含域名的完整URL
     */
    public String getAbsoluteURL(int tid, String pattern, String nValue, String params) {
        DefaultURLConstructor urlMaker = findUrlMaker(tid);
        return urlMaker.makeURL(pattern, nValue, params, true);
    }
    
    private DefaultURLConstructor findUrlMaker(){
        if(systemURLMaker==null){
            systemURLMaker = new DefaultURLConstructor(baseUrl,null);
        }
        return systemURLMaker;
    }

    private DefaultURLConstructor findUrlMaker(int tid) {
        Team team = null;
        DefaultURLConstructor urlMaker = localURLMap.get(tid);
        if (urlMaker == null) {
            team = teamService.getTeamByID(tid);
            if (team != null) {
                urlMaker = new DefaultURLConstructor(baseUrl, team.getName());
                localURLMap.put(team.getId(), urlMaker);
            }
        }
        return urlMaker;
    }
    
    
}
