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

import net.duckling.ddl.constant.KeyConstants;

public class CLBCanUseTag extends VWBBaseTag {

    /**
     *
     */
    private static final long serialVersionUID = 1L;
    private boolean hasMessage = false;

    public boolean getHasMessage() {
        return hasMessage;
    }
    public void setHasMessage(boolean hasMessage) {
        this.hasMessage = hasMessage;
    }

    @Override
    public int doVWBStart() throws Exception {
        String isMaintainStr = vwbcontext.getContainer().getProperty(KeyConstants.CONTAINER_CLB_MAINTAIN);
        boolean isMaintain = Boolean.valueOf(isMaintainStr);
        pageContext.setAttribute("clbCanUse", !isMaintain);
        if(isMaintain){
            if(hasMessage){
                pageContext.getOut().print("<p>文档服务正在维护，暂无法使用文件上传功能！</p>");
            }
            return SKIP_BODY;
        }
        return EVAL_BODY_INCLUDE;
    }

}
