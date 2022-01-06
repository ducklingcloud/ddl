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

import java.util.ArrayList;
import java.util.List;

import net.duckling.ddl.common.DDLFacade;
import net.duckling.ddl.service.subscribe.Subscription;
import net.duckling.ddl.service.subscribe.impl.SubscriptionServiceImpl;


/**
 * @date Mar 9, 2011
 * @author liji@cnic.cn
 */
public class IsSubscribedTag extends VWBBaseTag {
    private static final long serialVersionUID = 1L;

    private String flagName;
    private String itemsName;


    /**
     * @param flagName the flagName to set
     */
    public void setFlagName(String flagName)
    {
        this.flagName = flagName;
    }

    /**
     * @param itemsName the itemsName to set
     */
    public void setItemsName(String itemsName)
    {
        this.itemsName = itemsName;
    }

    protected void initTag(){
        super.initTag();
        flagName=null;
        itemsName=null;
    }

    @Override
    public int doVWBStart() throws Exception {
        int pageId = vwbcontext.getResource().getRid();
        int tid = vwbcontext.getSite().getId();
        List<Subscription> existSub = DDLFacade.getBean(SubscriptionServiceImpl.class).getPageSubscribers(tid,pageId);
        List<Subscription> newExistSub = new ArrayList<Subscription>();
        String uid = vwbcontext.getCurrentUID();
        for (Subscription temp : existSub) {
            if(uid.equals(temp.getUserId())){
                newExistSub.add(temp);
            }
        }
        pageContext.getRequest().setAttribute(flagName, newExistSub.size()!=0 );
        pageContext.getRequest().setAttribute(itemsName, newExistSub );
        return EVAL_PAGE;
    }
}
