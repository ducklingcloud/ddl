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

package net.duckling.ddl;

import java.io.IOException;

import org.apache.log4j.Logger;
import org.apache.log4j.PropertyConfigurator;
import org.springframework.beans.factory.BeanFactory;
import org.springframework.context.support.FileSystemXmlApplicationContext;
import org.springframework.core.io.ClassPathResource;
import org.springframework.core.io.Resource;

public class SpringManager {
    private Logger log;
    private static FileSystemXmlApplicationContext factory = null;

    private SpringManager() {
        initLog4j();
        initWebRoot();
        initApplicationContext();
    }

    public static BeanFactory getFactory() {
        if (factory == null) {
            new SpringManager();
        }
        return factory;
    }

    public static void destroy() {
        if (factory != null) {
            factory.close();
            factory = null;
        }
    }

    private void initApplicationContext() {
        String contextXml = "classpath:test-context.xml";
        String mvcXml = "classpath:test-controllers.xml";
        try {
            factory = new FileSystemXmlApplicationContext(
                contextXml, mvcXml);
        } catch (Throwable e) {
            log.error("Startup failed:" + e);
            e.printStackTrace();
        }
    }

    private void initWebRoot() {
        String webRootKey = "ddl.root";
        try {
            Resource rootClassRes = new ClassPathResource("/");
            String webroot = rootClassRes.getFile().getPath();
            System.setProperty(webRootKey, webroot);
            log.info("set web root path["+ webRootKey +"]:"+ webroot);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private void initLog4j() {
        String log4jConfig = "src/test/resources/log4j.properties";
        PropertyConfigurator.configure(log4jConfig);
        log = Logger.getLogger(SpringManager.class);
    }

}
