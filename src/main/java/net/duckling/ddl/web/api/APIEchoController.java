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

package net.duckling.ddl.web.api;

import java.io.IOException;
import java.io.PrintWriter;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;

/**
 * API调用的Controller（查询集合页面）
 * @date 2011-8-22
 * @author xiejj@cnic.cn
 */
@Controller
@RequestMapping("/api/echo")
public class APIEchoController extends APIBaseController {
    private static final int ECHO_MAX_LENGTH = 100;
    @RequestMapping
    public void service(HttpServletRequest request, HttpServletResponse response) throws IOException{
        String message = request.getParameter("msg");
        if (message==null){
            message="";
        }
        if (message.length()>ECHO_MAX_LENGTH){
            message = message.substring(0, ECHO_MAX_LENGTH);
        }
        PrintWriter w = response.getWriter();
        w.write(message);
        w.flush();
    }
}
