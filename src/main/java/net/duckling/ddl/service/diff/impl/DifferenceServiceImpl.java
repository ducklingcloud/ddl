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

package net.duckling.ddl.service.diff.impl;

import net.duckling.ddl.common.VWBContext;
import net.duckling.ddl.service.diff.DifferenceService;
import net.duckling.ddl.service.render.RenderingService;
import net.duckling.ddl.service.resource.PageVersion;
import net.duckling.ddl.service.resource.PageVersionService;
import net.duckling.ddl.service.resource.Resource;

import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

/**
 * Load, initialize and delegate to the DiffProvider that will actually do the
 * work.
 * 
 * @author Yong Ke
 */
@Service
public class DifferenceServiceImpl implements DifferenceService {
    private static final Logger LOGGER = Logger.getLogger(DifferenceServiceImpl.class);

    @Autowired
    private DiffProvider provider;
    @Autowired
    private RenderingService renderingService;
    @Autowired
    private PageVersionService pageVersionService;

    public String makeDiff(VWBContext context, Resource resource, int verFirst, int verSecond) {
        String diff = null;
        try {
            PageVersion pageFirstVersion =pageVersionService.getPageVersion(resource.getRid(), verFirst);
            PageVersion pageSecondVersion =pageVersionService.getPageVersion(resource.getRid(), verSecond);
            if (pageFirstVersion == null) {
                diff = "Failed to create diff for page content can't be found on version " + verFirst;
                LOGGER.warn(diff);
            }
            if (pageSecondVersion == null) {
                diff = "Failed to create diff for page content can't be found on version " + verSecond;
                LOGGER.warn(diff);
            }

            if (pageFirstVersion != null && pageSecondVersion != null) {
                String dmlDiff = provider.makeDiffHtml(pageFirstVersion.getContent(),
                        pageSecondVersion.getContent());
                diff = renderingService.getHTML(context, dmlDiff);
            }

            if (diff == null) {
                diff = "";

            }
        } catch (Exception e) {
            diff = "Failed to create a diff, check the logs.";
            LOGGER.warn(diff, e);
        }
        return diff;
    }

    public String getDiffResult(VWBContext context, String oldHtml, String newHtml) {
        try {
            return provider.makeDiffHtml(oldHtml, newHtml);
        } catch (Exception e) {
            LOGGER.error("oldHtml=" + oldHtml + ";newHtml" + newHtml + "处理错误", e);
        }
        return null;
    }
}
