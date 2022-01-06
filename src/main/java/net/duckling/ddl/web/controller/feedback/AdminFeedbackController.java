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

package net.duckling.ddl.web.controller.feedback;

import java.util.List;

import javax.servlet.http.HttpServletRequest;

import net.duckling.ddl.common.VWBContext;
import net.duckling.ddl.service.feedback.Feedback;
import net.duckling.ddl.service.feedback.impl.FeedbackServiceImpl;
import net.duckling.ddl.service.url.UrlPatterns;
import net.duckling.ddl.web.controller.BaseController;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.servlet.ModelAndView;


/**
 * @date 2011-12-8
 * @author clive
 */

@Controller
@RequestMapping("/aone/feedback")
public class AdminFeedbackController extends BaseController {

    @Autowired
    private FeedbackServiceImpl feedbackService;

    @RequestMapping
    public ModelAndView display(HttpServletRequest request){
        VWBContext context = VWBContext.createContext(request,UrlPatterns.ADMIN);
        List<Feedback> feedbackList = feedbackService.getAll();
        ModelAndView mv = layout(".aone.portal",context, "/jsp/aone/feedback/adminFeedback.jsp");
        mv.addObject("feedbacks", feedbackList);
        return mv;
    }

}
