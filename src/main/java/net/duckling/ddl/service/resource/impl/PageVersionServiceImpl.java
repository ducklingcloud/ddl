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

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import net.duckling.ddl.service.resource.PageVersion;
import net.duckling.ddl.service.resource.PageVersionService;

@Service
public class PageVersionServiceImpl implements PageVersionService {
    @Autowired
    private PageVersionDAO pageVersionDAO;
    @Override
    public int create(PageVersion pageVersion) {
        return pageVersionDAO.create(pageVersion);
    }

    @Override
    public int delete(int id) {
        return pageVersionDAO.delete(id);
    }

    @Override
    public int recoverPageVersion(int pid, int tid) {
        return pageVersionDAO.recoverPafeVersion(pid, tid);
    }

    @Override
    public int deleteAllPageVersion(int pid, int tid) {
        return pageVersionDAO.deleteAllPageVersion(pid, tid);
    }

    @Override
    public int update(int id, PageVersion pageVersion) {
        return pageVersionDAO.update(id, pageVersion);
    }

    @Override
    public PageVersion getPageVersionById(int id) {
        return pageVersionDAO.getPageVersionById(id);
    }

    @Override
    public PageVersion getPageVersion(int rid, int version) {
        return pageVersionDAO.getPageVersion(rid, version);
    }

    @Override
    public PageVersion getLatestPageVersion(int rid) {
        return pageVersionDAO.getLatestPageVersion(rid);
    }

    @Override
    public List<PageVersion> getAllPageVersionByTIDRID(int tid, int rid) {
        return pageVersionDAO.getAllPageVersionByTIDRID(tid, rid);
    }

    @Override
    public List<PageVersion> getVersions(int rid, int tid, int offset, int size) {
        return pageVersionDAO.getVersions(rid, tid, offset, size);
    }

    @Override
    public List<PageVersion> getUserRecentPages() {
        return pageVersionDAO.getUserRecentPages();
    }

}
