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
package net.duckling.ddl.service.mail.impl;

import net.duckling.ddl.service.resource.Resource;
import net.duckling.ddl.service.team.Team;

public class RenderTeam {
    private Team team;
    private String accessUrl;

    public Team getTeam() {
        return team;
    }

    public void setTeam(Team team) {
        this.team = team;
    }

    public String getAccessUrl() {
        return accessUrl;
    }

    public void setAccessUrl(String teamName, Resource rs) {
        accessUrl = "/" + teamName;
        if (rs.isFolder()) {
            accessUrl += "/f/" + rs.getRid();
        } else if (rs.isFile()) {
            accessUrl += "/f/" + rs.getRid();
        } else if (rs.isPage()) {
            accessUrl += "/f/" + rs.getRid();
        }
    }

}
