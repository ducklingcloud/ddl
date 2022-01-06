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

package net.duckling.ddl.service.variable.impl;

import net.duckling.ddl.common.VWBContext;
import net.duckling.ddl.service.url.URLGenerator;
import net.duckling.ddl.service.url.UrlPatterns;
import net.duckling.ddl.service.variable.NoSuchVariableException;
import net.duckling.ddl.service.variable.VariableService;

import org.apache.commons.lang.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

/**
 * Introduction Here.
 *
 * @date Feb 25, 2010
 * @author xiejj@cnic.cn
 */
@Service
public class VariableServiceImpl implements VariableService {

    @Autowired
    private URLGenerator urlGenerator;

    public Object getValue(VWBContext context, String varName, String defValue) {
        if (StringUtils.isEmpty(varName)) {
            throw new IllegalArgumentException("");
        }
        if (varName.equals("pagename")) {
            if (context.getResource() != null) {
                return Integer.toString(context.getResource().getRid());
            } else {
                return "";
            }
        }
        if (varName.equals("pagetitle")) {
            return context.getResource().getTitle();
        }
        if (varName.equals("applicationName")) {
            return context.getSite().getSiteName();
        }
        if (varName.equals("encoding")) {
            return context.getContainer().getContentEncoding();
        }
        if (varName.equals("frontPage")){
            String frontpage;
            if (context.getSite()!=null){
                frontpage = context.getSite().getFrontPage();
            }else{
                frontpage = urlGenerator.getAbsoluteURL(UrlPatterns.SWITCH_TEAM, null, null);
            }
            return frontpage;
        }
        String value = context.getContainer().getProperty(varName);
        if (value != null) {
            return value;
        } else {
            return defValue;
        }
    }

    public Object getValue(VWBContext context, String varName) throws NoSuchVariableException {
        Object value = getValue(context, varName, null);
        if (value == null) {
            throw new NoSuchVariableException(varName);
        }
        return value;
    }
}
