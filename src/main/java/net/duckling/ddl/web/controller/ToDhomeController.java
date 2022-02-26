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
/**
 *
 */
package net.duckling.ddl.web.controller;

import javax.servlet.http.HttpServletRequest;

import net.duckling.common.DucklingProperties;
import net.duckling.ddl.common.VWBContext;
import net.duckling.ddl.constant.KeyConstants;
import net.duckling.ddl.service.url.UrlPatterns;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.servlet.ModelAndView;
import org.springframework.web.servlet.view.RedirectView;

/**
 * 去往dhome的入口
 *
 * @author lvly
 * @since 2012-11-22
 */
@Controller
@RequestMapping("/system/toDhome")
public class ToDhomeController {
    @Autowired
    private DucklingProperties systemProperty;
    @RequestMapping
    public ModelAndView toDhome(HttpServletRequest request) {
        VWBContext context = VWBContext.createContext(request, UrlPatterns.TO_DHOME);
        String email = context.getCurrentUID();
        String dhomeUrl = systemProperty.getProperty(KeyConstants.TO_DHOME_URL);
        return new ModelAndView(new RedirectView(dhomeUrl + "?email=" + email));
    }

}
