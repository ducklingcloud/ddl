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


package net.duckling.ddl.web.filter;

import javax.servlet.ServletContext;
import javax.servlet.ServletContextEvent;
import javax.servlet.ServletContextListener;

import net.duckling.ddl.common.VWBContainerImpl;

import org.springframework.web.context.WebApplicationContext;
import org.springframework.web.context.support.WebApplicationContextUtils;


/*
 * VWB容器的启动Listener
 * @date Feb 1, 2010
 * @author Xiejj
 */

public class VWBStartupListener implements ServletContextListener {
    public void contextDestroyed(ServletContextEvent event) {
        WebApplicationContext ctx = getWebApplicationContext(event);
        if (ctx != null) {
            VWBContainerImpl.setBeanFactory(null);
        }
    }

    public void contextInitialized(ServletContextEvent event) {
        WebApplicationContext factory = getWebApplicationContext(event);
        VWBContainerImpl.setBeanFactory(factory);
    }

    private WebApplicationContext getWebApplicationContext(ServletContextEvent event) {
        ServletContext servletContext = event.getServletContext();
        return WebApplicationContextUtils.getWebApplicationContext(servletContext);
    }
}
