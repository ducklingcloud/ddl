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

package net.duckling.ddl.web.controller.file;

import java.io.IOException;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import net.duckling.ddl.web.interceptor.access.RequirePermission;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;


/**
 * 附件下载链接
 *
 * @author xiejj@cnic.cn
 */
@Controller
@RequirePermission(target = "team", operation = "view")
public class AttachController extends BaseAttachController{

    @RequestMapping("/{teamCode}/attach/{hash}")
    public void attach(HttpServletRequest req, HttpServletResponse res,
                       @PathVariable String hash) throws IOException {
        AttachmentURL attachUrl = new AttachmentURL(false, hash);
        if (attachUrl.isValid()) {
            getContent(req, res, attachUrl.getDocID(), attachUrl.getVersion(),null, false);
        }
    }

    @RequestMapping("/{teamCode}/cachable/{hash}")
    public void cachable(HttpServletRequest req, HttpServletResponse res,
                         @PathVariable String hash) throws IOException {
        AttachmentURL attachUrl = new AttachmentURL(true, hash);
        if (attachUrl.isValid()) {
            getContent(req, res, attachUrl.getDocID(), attachUrl.getVersion(),null, true);
        }
    }

}
