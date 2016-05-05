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

import java.util.Date;

import javax.servlet.http.HttpServletRequest;

import net.duckling.ddl.common.VWBContext;
import net.duckling.ddl.service.feedback.Feedback;
import net.duckling.ddl.service.feedback.impl.FeedbackServiceImpl;
import net.duckling.ddl.service.url.URLGenerator;
import net.duckling.ddl.service.url.UrlPatterns;
import net.duckling.ddl.web.controller.BaseController;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.servlet.ModelAndView;
import org.springframework.web.servlet.view.RedirectView;


/**
 * @date 2011-12-8
 * @author clive
 */

@Controller
@RequestMapping("/system/feedback")
public class FeedbackController extends BaseController {
    
    @Autowired
    private FeedbackServiceImpl feedbackService;
    @Autowired
    private URLGenerator urlGenerator;

	@RequestMapping
	public ModelAndView prepare(HttpServletRequest request) {
		VWBContext context = VWBContext.createContext(request,UrlPatterns.FEEDBACK);
		ModelAndView mv = layout(".aone.portal", context, "/jsp/aone/feedback/feedback.jsp");
		return mv;
	}

	@RequestMapping(params="func=submit")
	public ModelAndView submitFeedback(HttpServletRequest request) {
		String email = request.getParameter("email");
		String message = request.getParameter("message");
		Feedback f = new Feedback();
		f.setEmail(email);
		f.setMessage(message);
		f.setStatus(Feedback.UNHANDLE);
		f.setSendTime(new Date());
		feedbackService.insert(f);
		String url = urlGenerator.getURL(UrlPatterns.FEEDBACK, null,"func=success");
		return new ModelAndView(new RedirectView(url));
	}

	@RequestMapping(params="func=success")
	public ModelAndView success(HttpServletRequest request) {
		return layout(".aone.portal",VWBContext.createContext(request, UrlPatterns.FEEDBACK),"/jsp/aone/feedback/success.jsp");
	}
}
