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

import net.duckling.ddl.common.VWBContext;
import net.duckling.ddl.service.render.DPageRendable;

/**
 * Introduction Here.
 *
 * @date Mar 2, 2010
 * @author xiejj@cnic.cn
 */
public class InsertPageTag extends VWBBaseTag {
    private static final long serialVersionUID = 1L;
    private int m_page;
    private int m_version = -1;

    protected void initTag() {
        super.initTag();
        m_version = -1;
    }

    @SuppressWarnings("unused")
    public void setVersion(int version) {
        m_version = -1;
    }

    public void setPage(int page) {
        m_page = page;
    }

    @Override
    public int doVWBStart() throws Exception {
        DPageRendable rendable = new DPageRendable(VWBContext.getCurrentTid(), m_page, m_version);
        rendable.render(vwbcontext, pageContext);
        return SKIP_BODY;
    }
}
