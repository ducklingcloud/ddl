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
package net.duckling.ddl.service.pan;

import java.util.List;

import net.duckling.ddl.exception.MessageException;
import net.duckling.meepo.api.PanAcl;

import com.meepotech.sdk.MeePoException;

public interface PanSpaceApplicationService {
    int add(long newSize, long originalSize, String uid, String applicationType);
    /**
     * 分配空间到盘
     * @param uid
     * @param size
     * @param acl
     * @throws MeePoException
     * @throws MessageException
     */
    void updateSpaceAllocate(String uid, long size, PanAcl acl) throws MeePoException, MessageException;
    List<PanSpaceApplication> queryByUid(String uid);
    void delete(int id);
}
