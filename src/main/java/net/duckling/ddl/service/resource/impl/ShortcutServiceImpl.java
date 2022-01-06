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
package net.duckling.ddl.service.resource.impl;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.locks.ReentrantLock;

import net.duckling.ddl.service.resource.DShortcut;
import net.duckling.ddl.service.resource.ShortcutService;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class ShortcutServiceImpl implements ShortcutService {

    private ReentrantLock lock = new ReentrantLock();
    @Autowired
    private ShortcutDAO shortcutDao;

    @Override
    public boolean addShortcut(DShortcut instance) {
        lock.lock();
        try {
            int squence = shortcutDao.getShortcutCount(instance.getTid(),
                                                       instance.getTgid()) + 1;
            instance.setSequence(squence);
            return shortcutDao.insert(instance);
        } finally {
            lock.unlock();
        }
    }

    @Override
    public boolean deleteShortCut(int sid) {
        return shortcutDao.delete(sid);
    }

    @Override
    public List<DShortcut> getCollectionShortcut(int tid, int tgId) {
        return shortcutDao.queryShortcut(tid, tgId);
    }

    @Override
    public List<DShortcut> getCollectionShortcut(int tid, int[] tgid) {
        if (tgid == null || tgid.length == 0) {
            return getCollectionShortcut(tid, 0);
        }
        List<Integer> ids = new ArrayList<Integer>();
        for (int i : tgid) {
            ids.add(i);
        }
        return shortcutDao.queryShortcut(tid, ids);
    }

    @Override
    public DShortcut getDSortcutById(int id) {
        return shortcutDao.getDSortcutById(id);
    }

    @Override
    public boolean removeById(int id) {
        return shortcutDao.delete(id);
    }

    @Override
    public boolean updateShortcuts(DShortcut ins) {
        return shortcutDao.update(ins);
    }

    @Override
    public boolean updateShortcuts(List<DShortcut> ins) {
        return shortcutDao.update(ins);
    }

    /**
     * 按照给定的shortid的顺序进行排序
     *
     * @param ids
     * @return
     */
    @Override
    public boolean updateShortSequece(List<Integer> ids) {
        if (ids == null || ids.isEmpty()) {
            return false;
        }
        return shortcutDao.updateSequece(ids);
    }

}
