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
import net.duckling.common.util.CommonUtils;

import javax.servlet.jsp.JspException;

import net.duckling.ddl.constant.KeyConstants;
import net.duckling.ddl.constant.LynxConstants;
import net.duckling.ddl.util.HTMLConvertUtil;


/**
 * 应用名称
 * @date Feb 24, 2010
 * @author xiejj@cnic.cn
 */
public class ApplicationNameTag extends VWBBaseTag {
    private static final long serialVersionUID = 1L;

    @Override
    public int doVWBStart() throws JspException {
        String sitename;
        if (vwbcontext.getSite()!=null){
            sitename=vwbcontext.getSite().getSiteName();
        }else{
            String teamTitle = (String)vwbcontext.getHttpRequest().getAttribute(LynxConstants.TEAM_TITLE);
            if(CommonUtils.isNullOrEmptyOrBlankOrLiteralNull(teamTitle)){
                sitename = vwbcontext.getContainer().getProperty(KeyConstants.SITE_NAME_KEY);
            }else{
                sitename = teamTitle;
            }
        }
        try {
            pageContext.getOut().write(HTMLConvertUtil.replaceLtGt(sitename));
        } catch (IOException e) {
            throw new JspException(e);
        }
        return SKIP_BODY;
    }
}
