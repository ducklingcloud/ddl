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

package net.duckling.ddl.service.render;

import java.io.IOException;

import javax.servlet.ServletException;
import javax.servlet.jsp.PageContext;

import net.duckling.ddl.common.VWBContext;



/**
 * Introduction Here.
 * @date Feb 5, 2010
 * @author xiejj@cnic.cn
 */
public class JSPRendable implements Rendable{
    public JSPRendable(String jsp, int viewportid){
		this.jsp=jsp;
	}
	
	public void render(VWBContext context, PageContext pageContext) throws ServletException, IOException {
		pageContext.include(jsp);
	}
	public int getResourceId() {
		return viewportid;
	}
	public void setId(int id){
		this.viewportid=id;
	}
	private int viewportid;
	private String jsp;
}
