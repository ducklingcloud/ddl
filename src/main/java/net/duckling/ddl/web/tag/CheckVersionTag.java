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

package net.duckling.ddl.web.tag;

import java.io.IOException;

import net.duckling.ddl.common.DDLFacade;
import net.duckling.ddl.service.resource.IResourceService;
import net.duckling.ddl.service.resource.Resource;


/**
 * Introduction Here.
 * @date Mar 2, 2010
 * @author xiejj@cnic.cn
 */
public class CheckVersionTag extends VWBBaseTag {
    private static final long serialVersionUID = 0L;

    public static final int LATEST = 0;

    public static final int NOTLATEST = 1;

    public static final int FIRST = 2;

    public static final int NOTFIRST = 3;

    private int m_mode;

    private int rid;

    public void setRid(int rid) {
        this.rid = rid;
    }

    public void initTag() {
        super.initTag();
        m_mode = 0;
    }

    public void setMode(String arg) {
        if ("latest".equals(arg)) {
            m_mode = LATEST;
        } else if ("notfirst".equals(arg)) {
            m_mode = NOTFIRST;
        } else if ("first".equals(arg)) {
            m_mode = FIRST;
        } else {
            m_mode = NOTLATEST;
        }
    }

    public final int doVWBStart() throws IOException {
        int version = 0;
        Resource page = DDLFacade.getBean(IResourceService.class).getResource(rid);
        rid = page.getRid();
        version = page.getLastVersion();
        boolean include = false;

        LOG.debug("Doing version check: this=" + version + ", latest=" + page.getLastVersion());

        switch (m_mode) {
            case LATEST:
                include = (version < 0) || (page.getLastVersion() == version);
                break;

            case NOTLATEST:
                include = (version > 0) && (page.getLastVersion() != version);
                break;

            case FIRST:
                include = (version == 1)
                        || (version < 0 && page.getLastVersion() == 1);
                break;

            case NOTFIRST:
                include = version > 1;
                break;
            default:break;
        }

        if (include) {
            return EVAL_BODY_INCLUDE;
        }

        return SKIP_BODY;
    }
}
