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
package net.duckling.ddl.service.itemtypemap.impl;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import net.duckling.ddl.service.itemtypemap.ItemTypeMappingService;
import net.duckling.ddl.service.itemtypemap.ItemTypemapping;

@Service
public class ItemTypeMappingServiceImpl implements ItemTypeMappingService {
    @Autowired
    private ItemTypeMappingDAO  itemTypeMappingDAO;

    @Override
    public ItemTypemapping getItemTypeMapping(int tid,int itemId, String itemType) {
        return itemTypeMappingDAO.getItemTypeMapping(tid,itemId, itemType);
    }

}
