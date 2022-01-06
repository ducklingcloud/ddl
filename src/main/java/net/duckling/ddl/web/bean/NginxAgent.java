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

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import net.duckling.ddl.common.VWBContainer;
import net.duckling.ddl.common.VWBContainerImpl;
import net.duckling.ddl.constant.KeyConstants;
import net.duckling.ddl.util.Browser;

import org.apache.commons.lang.StringUtils;

public class NginxAgent {
    private static final String X_ACCEL_REDIRECT = "X-Accel-Redirect";
    private static final String TOMCAT_FILE_ACCESS_MODE = "tomcat";

    /**
     * 是否配置的为nginx访问clb代理下载
     * @return
     */
    public static boolean isNginxMode() {
        VWBContainer c = VWBContainerImpl.findContainer();
        String accessMode = c.getProperty(KeyConstants.CONTAINER_CLB_ACCESS_MODE);
        return !(StringUtils.isEmpty(accessMode) || TOMCAT_FILE_ACCESS_MODE.equals(accessMode));
    }

    /**
     * 通过设置X-Accel-Redirect头，跳转nignx
     * @param req
     * @param resp
     * @param fileName
     * @param fileSize
     * @param url
     */
    public static void setRedirectUrl(HttpServletRequest req, HttpServletResponse resp, String fileName, long fileSize, String url) {
        if(fileSize>0){
            resp.addHeader(X_ACCEL_REDIRECT, url + "?agent=" + Browser.recognizeBrowser(req.getHeader("USER-AGENT")).toString().toLowerCase());
        }

        // x_accel_redirect to nginx-clbs (gridfs) that
        // doesn't include Content-Disposition.
        // Set it here and the headers will be combined.
        resp.setHeader("Content-Disposition", Browser.encodeFileName(req.getHeader("USER-AGENT"), fileName));

    }
}
