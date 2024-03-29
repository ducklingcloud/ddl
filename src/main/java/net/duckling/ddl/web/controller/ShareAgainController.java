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

package net.duckling.ddl.web.controller;

import javax.servlet.http.HttpServletRequest;

import net.duckling.ddl.common.VWBContext;
import net.duckling.ddl.service.url.UrlPatterns;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.servlet.ModelAndView;


/**
 * @date 2011-8-18
 * @author Administrator
 */
@Controller
@RequestMapping("/system/shareAgain")
public class ShareAgainController extends BaseController {

    @RequestMapping
    public ModelAndView prepareShareAgain(HttpServletRequest request) {
        VWBContext context = getVWBContext(request);
        ModelAndView mv = layout(".aone.portal",context, "/jsp/aone/team/share/shareFile2.jsp");
        mv.addObject("shareAgain", true);
        mv.addObject("email", request.getParameter("email"));
        return mv;
    }

    private VWBContext getVWBContext(HttpServletRequest request) {
        return VWBContext.createContext(request,UrlPatterns.SHARE_FILE_AGAIN);
    }
}
