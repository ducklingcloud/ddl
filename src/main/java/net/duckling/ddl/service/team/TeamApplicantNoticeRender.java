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
package net.duckling.ddl.service.team;

import java.io.Serializable;



/**
 * 用户接收到的团队申请通知信息
 * @author Yangxp
 *
 */
public class TeamApplicantNoticeRender implements Serializable{

    /**
     *
     */
    private static final long serialVersionUID = 1L;

    private TeamApplicant teamApplicant;
    private String teamName;

    public TeamApplicant getTeamApplicant() {
        return teamApplicant;
    }
    public void setTeamApplicant(TeamApplicant teamApplicant) {
        this.teamApplicant = teamApplicant;
    }
    public String getTeamName() {
        return teamName;
    }
    public void setTeamName(String teamName) {
        this.teamName = teamName;
    }

}
