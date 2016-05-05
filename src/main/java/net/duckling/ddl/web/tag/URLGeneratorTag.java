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

import javax.servlet.jsp.JspException;
import javax.servlet.jsp.JspWriter;
import javax.servlet.jsp.tagext.BodyContent;
import javax.servlet.jsp.tagext.BodyTag;

import net.duckling.ddl.common.DDLFacade;
import net.duckling.ddl.common.VWBContext;
import net.duckling.ddl.service.url.URLGenerator;

import org.springframework.beans.factory.annotation.Autowired;

public class URLGeneratorTag extends VWBBaseTag implements BodyTag {
	/**
	 * 
	 */
	private static final long serialVersionUID = -7607579157789951939L;
	private String pattern;
	private String nValue;
	private String params;
	private boolean absolute;
	URLGenerator generator;
	
	public String getPattern() {
		return pattern;
	}

	public void setPattern(String pattern) {
		this.pattern = pattern;
	}

	public String getnValue() {
		return nValue;
	}

	public void setnValue(String nValue) {
		this.nValue = nValue;
	}

	public String getParams() {
		return params;
	}

	public void setParams(String params) {
		this.params = params;
	}

	public boolean isAbsolute() {
		return absolute;
	}

	public void setAbsolute(boolean absolute) {
		this.absolute = absolute;
	}

	@Override
	public void setBodyContent(BodyContent b) {
		
	}
	
	private String getUrl(){
		initGenerator();
		int tid = VWBContext.getCurrentTid();
		if(tid<=0){
			if(absolute){
				return generator.getAbsoluteURL(pattern, nValue, params);
			}else{
				return generator.getURL(pattern, nValue, params);
			}
		}else{
			if(absolute){
				return generator.getAbsoluteURL(tid,pattern, nValue, params);
			}else{
				return generator.getURL(tid, pattern, nValue, params);
			}
		}
	}
	private void initGenerator() {
		if(generator==null){
			synchronized (this) {
				if(generator==null){
					generator = DDLFacade.getBean(URLGenerator.class);
				}
			}
		}
	}

	@Override
	public int doEndTag() throws JspException {
		JspWriter out = pageContext.getOut();
		try {
			out.append(getUrl());
		} catch (IOException e) {
			e.printStackTrace();
		}
		return EVAL_PAGE;
	}
	
	@Override
	public void doInitBody() throws JspException {
		
	}

	@Override
	public int doVWBStart() throws Exception {
		return 0;
	}

}
