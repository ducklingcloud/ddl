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
import net.duckling.ddl.service.team.Team;
import net.duckling.ddl.service.team.TeamService;
import net.duckling.ddl.service.url.URLGenerator;
import net.duckling.ddl.service.url.UrlPatterns;
import net.duckling.ddl.web.interceptor.access.RequirePermission;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.servlet.ModelAndView;
import org.springframework.web.servlet.view.RedirectView;


@Controller
@RequestMapping("/{teamCode}/search")
public class LynxBannerSearchController extends BaseController {
    @Autowired
    private TeamService teamService;
    @Autowired
    private URLGenerator urlGenerator;

    @RequirePermission(target="team", operation="view")
    @RequestMapping
    public ModelAndView display(HttpServletRequest request, @PathVariable("teamCode") String tname){
        String redirectURL = urlGenerator.getURL(UrlPatterns.GLOBAL_SEARCH, null,"func=searchResult");
        String keyword = request.getParameter("keyword");
        Team team = teamService.getTeamByName(tname);
        ModelAndView mv = new ModelAndView(new RedirectView(redirectURL));
        mv.addObject("keyword", keyword);
        mv.addObject("teamName", team.getName());
        return mv;
    }

}
