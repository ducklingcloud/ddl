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
package net.duckling.ddl.web.bean;

import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;

import net.duckling.ddl.common.VWBContainer;
import net.duckling.ddl.common.VWBContainerImpl;
import net.duckling.ddl.common.VWBContext;


public class UMTServerURLUtil {

    public static String getUMTLoginServerURL(VWBContext context){
        VWBContainer container = context.getContainer();
        String umtLoginPath = container.getProperty("duckling.umt.login");
        String ddlPath =VWBContainerImpl.findContainer().getURL("switchTeam", null, null, true);
        try {
            umtLoginPath = umtLoginPath+"?WebServerURL="+URLEncoder.encode(ddlPath, "utf-8")
                    +"&appname="
                    + URLEncoder.encode(container
                                        .getProperty("duckling.dct.localName"), "UTF-8")
                    + "&theme="
                    + container.getProperty("duckling.umt.theme");
            return umtLoginPath;
        } catch (UnsupportedEncodingException e) {
            return umtLoginPath;
        }

    }
}
