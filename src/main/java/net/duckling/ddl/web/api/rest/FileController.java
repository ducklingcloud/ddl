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
package net.duckling.ddl.web.api.rest;

import javax.servlet.http.HttpServletResponse;

import net.duckling.ddl.service.resource.IResourceService;
import net.duckling.ddl.service.resource.ResourceOperateService;
import net.duckling.ddl.web.interceptor.access.RequirePermission;

import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;

@Controller
@RequirePermission(target="team",operation="edit")
@RequestMapping("/api/v1/resource/files")
public class FileController extends AbstractController {
    private static final Logger LOG = Logger.getLogger(FileController.class);


    /**
     * TODO 下载文件
     * @param path
     * @param response
     */
    @RequestMapping("/{path}")
    public void download(@PathVariable String path, HttpServletResponse response) {

    }

    @Autowired
    private IResourceService resourceService;
    @Autowired
    private ResourceOperateService resourceOperateService;
}
