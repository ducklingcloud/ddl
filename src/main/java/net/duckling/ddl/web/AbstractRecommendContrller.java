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
package net.duckling.ddl.web;

import java.util.Collections;
import java.util.Comparator;
import java.util.List;

import javax.servlet.http.HttpServletResponse;

import net.duckling.ddl.common.VWBContext;
import net.duckling.ddl.service.team.Team;
import net.duckling.ddl.service.team.TeamMemberService;
import net.duckling.ddl.service.team.TeamService;
import net.duckling.ddl.service.user.SimpleUser;
import net.duckling.ddl.util.JsonUtil;

import org.apache.commons.lang.StringUtils;
import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import org.springframework.beans.factory.annotation.Autowired;

public abstract class AbstractRecommendContrller {
    @Autowired
    protected TeamMemberService teamMemberService;
    @Autowired
    private TeamService teamService;

    protected static final Comparator<SimpleUser> comparator = new Comparator<SimpleUser>() {
            public int compare(SimpleUser m1, SimpleUser m2) {
                return m1.getUid().compareTo(m2.getUid());
            }
        };


    @SuppressWarnings("unchecked")
    protected void prepareRecommend(HttpServletResponse response) {
        Team team =teamService.getTeamByID(VWBContext.getCurrentTid());
        List<SimpleUser> candidates = teamMemberService.getTeamMembersOrderByName(team.getId());
        Collections.sort(candidates, comparator);
        JsonArray array = new JsonArray();
        for (SimpleUser current : candidates) {
            JsonObject temp = new JsonObject();
            temp.addProperty("id", current.getUid());
            if (StringUtils.isNotEmpty(current.getName())) {
                temp.addProperty("name", current.getName());
            } else {
                temp.addProperty("name", current.getUid());
            }
            temp.addProperty("userExtId", current.getId());
            array.add(temp);
        }
        JsonUtil.write(response, array);
    }

}
