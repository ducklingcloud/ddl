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

import net.duckling.ddl.service.team.Team;
import net.duckling.ddl.service.team.TeamService;

/**
 * @date 2011-11-25
 * @author clive
 */
public class SpaceTypeTag extends VWBBaseTag {
    @Override
    public int doVWBStart() throws Exception {
        int tid = vwbcontext.getSite().getId();
        String uid = vwbcontext.getCurrentUID();
        Team instance = getBean(TeamService.class).getTeamByID(tid);
        if (instance!=null){
            if(instance.getId()==1){
                pageContext.setAttribute("spaceType", "public");
            }else if(instance.isPersonalTeam() && instance.getCreator().equals(uid)){
                pageContext.setAttribute("spaceType", "personal");
            }else{
                pageContext.setAttribute("spaceType", "common");
            }
        }
        else{
            pageContext.setAttribute("spaceType", "common");
        }
        return EVAL_PAGE;
    }

}
