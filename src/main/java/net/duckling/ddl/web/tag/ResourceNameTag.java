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
package net.duckling.ddl.web.tag;

import java.io.IOException;
import java.util.Map;

import javax.servlet.jsp.JspException;
import javax.servlet.jsp.JspWriter;
import javax.servlet.jsp.tagext.BodyContent;
import javax.servlet.jsp.tagext.BodyTag;

import net.duckling.ddl.constant.LynxConstants;
import net.duckling.ddl.service.devent.DEntity;
import net.duckling.ddl.service.resource.Resource;
import net.duckling.ddl.web.bean.SimpleResourceKey;

import org.apache.commons.lang.StringUtils;
import org.apache.log4j.Logger;

/**
 * 用于显示资源名称
 * 如果资源被删除了 以中横杠来表示
 * @author zhonghui
 *
 */
public class ResourceNameTag extends VWBBaseTag implements BodyTag{
	/**
	 * 
	 */
	private static final long serialVersionUID = 5297677238861246543L;
	private static final Logger LOG = Logger.getLogger(ResourceNameTag.class);
	private Map<SimpleResourceKey,Resource> resourceMap;
	private String aClass;
	private DEntity target;
	private SimpleResourceKey key;
	private boolean notHref = false;
	private String onclick;
	
	public void setOnclick(String onclick) {
		this.onclick = onclick;
	}

	public void setNotHref(boolean notHref) {
		this.notHref = notHref;
	}

	public void setClass(String clazz){
		aClass = clazz;
	}
	
	public void setResourceMap(Map<SimpleResourceKey,Resource> resource) {
		this.resourceMap = resource;
	}

	public void setTarget(DEntity target) {
		this.target = target;
		key = new SimpleResourceKey();
		key.setRid(Integer.parseInt(target.getId()));
		key.setItemType(target.getType());
	}

	@Override
	public int doVWBStart() throws Exception {
		return EVAL_BODY_BUFFERED;
	}
	
	@Override
	public int doEndTag() throws JspException {
		JspWriter j = pageContext.getOut();
		try {
			j.print(getTagConext());
		} catch (IOException e) {
			LOG.error("",e);
		}
		return super.doEndTag();
	}
	
	private String getTagConext(){
		StringBuilder sb = new StringBuilder();
		boolean delete = isDelete();
		if(!delete&&!notHref){
			sb.append("<a href='").append(target.getUrl()).append("' ").append(getAClass());
			sb.append(getOnclickString()).append(">");
		}
		sb.append(getAContext());
		
		if(!delete&&!notHref){
			sb.append("</a>");
		}
		if(delete){
			sb.append("<span class='notice-delete'>已删除</span>");
		}
		return sb.toString();
	}
	
	private String getAContext() {
		StringBuilder sb = new StringBuilder();
		boolean a = isDelete();
		if(a){
			sb.append("<s ");
			if(onclick!=null&&onclick.length()>0){
				sb.append(" onclick='").append(onclick).append("'");
			}
			sb.append(">");
		}
		sb.append(getResourceName());
		if(a){
			sb.append("</s>");
		}
		return sb.toString();
	}

	private String getOnclickString(){
		if(onclick!=null&&onclick.length()>0){
			StringBuilder sb = new StringBuilder();
			sb.append(" onclick='").append(onclick).append("'");
			return sb.toString();
		}else{
			return "";
		}
	}
	private boolean isDelete() {
		if(resourceMap==null){
			return false;
		}else{
			Resource r = resourceMap.get(key);
			if(r!=null){
				if(LynxConstants.STATUS_DELETE.equals(r.getStatus())){
					return true;
				}
			}
		}
		return false;
	}

	private Object getAClass() {
		if(StringUtils.isNotEmpty(aClass)){
			return "class='"+aClass+"'";
		}
		return "";
	}

	private String getResourceName(){
		if(StringUtils.isNotEmpty(target.getName())){
			if(LynxConstants.TYPE_PAGE.equals(target.getType())){
				return tranferHtml(target.getName()+".ddoc");
			}else{
				return tranferHtml(target.getName());
			}
		}
		return "";
		
	}
	private String tranferHtml(String s){
		StringBuilder sb = new StringBuilder();
		char[] cs = s.toCharArray();
		for(char c : cs){
			switch (c) {
			case '<':
				sb.append("&lt;");
				break;
			case '>':
				sb.append("&gt;");
				break;
			default:
				sb.append(c);
				break;
			}
		}
		return sb.toString();
	}
	@Override
	public void setBodyContent(BodyContent b) {
		
	}

	@Override
	public void doInitBody() throws JspException {
		
	}
}
