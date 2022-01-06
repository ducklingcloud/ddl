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
package net.duckling.ddl.service.space;

import java.util.List;

import net.duckling.meepo.api.PanAcl;

public interface SpaceGainedService {
    List<SpaceGained> getList(String uid, Integer objId, String remark, Integer spaceType) ;
    List<SpaceGained> getList(String uid, Integer objId, String remark);
    int add(SpaceGained spaceGained);

    /**
     * 记录团队空间增加
     * @param uid
     * @param objId
     * @param size
     * @param remark
     */
    void addSpaceOfTeam(String uid, Integer objId, Long size, String remark);

    /**
     * 记录pan空间增加
     * @param uid
     * @param objId
     * @param size
     * @param remark
     * @param acl
     */
    void addSpaceOfPan(String uid, Integer objId, Long size, String remark, PanAcl acl);
}
