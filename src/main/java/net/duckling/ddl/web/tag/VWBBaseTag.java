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

import javax.servlet.http.HttpServletRequest;
import javax.servlet.jsp.JspException;
import javax.servlet.jsp.PageContext;
import javax.servlet.jsp.tagext.TagSupport;
import javax.servlet.jsp.tagext.TryCatchFinally;

import net.duckling.ddl.common.DDLFacade;
import net.duckling.ddl.common.VWBContext;

import org.apache.log4j.Logger;

/**
 * Introduction Here.
 * 
 * @date Feb 24, 2010
 * @author xiejj@cnic.cn
 */
public abstract class VWBBaseTag extends TagSupport implements TryCatchFinally {
	public static final String ATTR_CONTEXT = "duckling.vwbcontext";

	public void setPageContext(PageContext context) {
		super.setPageContext(context);
		initTag();
	}

	public abstract int doVWBStart() throws Exception;

	public void doCatch(Throwable arg0) throws Throwable {

	}

	public void doFinally() {
		vwbcontext = null;
	}

	public int doEndTag() throws JspException {
		return EVAL_PAGE;
	}

	public int doStartTag() throws JspException {
		try {
			vwbcontext = VWBContext.getContext((HttpServletRequest) pageContext
					.getRequest());
			if (vwbcontext == null) {
				throw new JspException(
						"VWBContext may not be NULL - serious internal problem!");
			}
			return doVWBStart();
		} catch (Exception e) {
			LOG.error("Tag failed", e);
			throw new JspException("Tag failed, check logs: " + e.getMessage());
		} catch (Throwable e) {
			throw new JspException("Tag failed, check logs: " + e.getMessage());
		}
	}

	protected void initTag() {
		vwbcontext = null;
	}

	protected <T> T getBean(Class<T> clazz) {
		return DDLFacade.getBean(clazz);
	}

	protected static final Logger LOG = Logger.getLogger(VWBBaseTag.class);
	protected VWBContext vwbcontext;
	private static final long serialVersionUID = 1L;
}
