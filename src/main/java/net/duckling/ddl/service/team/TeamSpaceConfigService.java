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

import java.util.List;

public interface TeamSpaceConfigService {
    /**
     * 获取team空间大小配置，如果没有配置则返回默认配置。
     * @param tid
     * @return 不为空
     */
    TeamSpaceConfig getTeamSpaceConfig(int tid);
    boolean update(TeamSpaceConfig config);
    List<TeamSpaceConfig> getAllTeamSpaceConfig();
    boolean insert(final TeamSpaceConfig c);
    void delete(int id);
}
