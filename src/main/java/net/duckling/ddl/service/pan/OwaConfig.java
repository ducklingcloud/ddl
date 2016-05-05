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
package net.duckling.ddl.service.pan;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.Properties;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.apache.log4j.Logger;
import org.springframework.core.io.FileSystemResource;
import org.springframework.core.io.Resource;
import org.springframework.core.io.support.PropertiesLoaderUtils;

public class OwaConfig {
    
    private Properties props;
    private static OwaConfig config;
    private Logger log;
    private Pattern pattern = Pattern.compile("([^\\}]*)\\$\\{([^}]*)\\}(.*)");

    /**
     * 获取配置文件读取类的实例
     * 
     * @return 配置读取对象的实例
     */
    public static OwaConfig getInstance() {
        if (config == null) {
            config = new OwaConfig();
        }
        return config;
    }

    private OwaConfig() {
        log = Logger.getLogger(OwaConfig.class);
        props = new Properties();
        String appRootPath = System.getProperty("ddl.root");
        String clbConfPropPath = appRootPath + "WEB-INF" + File.separator + "conf" + File.separator
                + "owaconfig.properties";
        Resource res = new FileSystemResource(clbConfPropPath);
        try {
            props = PropertiesLoaderUtils.loadProperties(res);
        } catch (FileNotFoundException e) {
            log.error("配置文件未找到。", e);
        } catch (IOException e) {
            log.error(e);
        }
    }
    
    private String getStringProp(String key, String defaultval) {
        String value = props.getProperty(key);
        if (value != null) {
            return replace(value);
        } else
            return defaultval;
    }
    
    private String replace(String input) {
        input = input.trim();
        int dollerPos = input.indexOf('$');
        if (dollerPos != -1) {
            Matcher matcher = pattern.matcher(input);
            if (matcher.matches()) {
                String left = matcher.group(1);
                String value = getStringProp(matcher.group(2), null);
                if (value == null)
                    value = System.getProperty(matcher.group(2));
                if (value == null)
                    value = matcher.group(2);
                String right = replace(matcher.group(3));
                return left + value + right;
            }
        }
        return input;
    }
    
    public String getServiceUrl(String status, String ext){
        String key = status+"."+ext;
        String value = this.getStringProp(key, null);
        return value;
    }

}
