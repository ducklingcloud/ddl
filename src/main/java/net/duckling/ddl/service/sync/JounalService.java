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
package net.duckling.ddl.service.sync;

import java.util.Date;
import java.util.List;

import net.duckling.ddl.service.resource.FolderPathService;
import net.duckling.falcon.api.idg.IIDGeneratorService;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class JounalService implements IJounalService {
    private static final String APP_NAME="ddl";
    private static final String GROUP_NAME="team_jid";

    @Override
    public long add(int tid, long fid, long fver,String device, String operation, boolean isDir, String path) {
        return add(tid, fid, fver, device, operation, isDir, path, null);
    }

    @Override
    public long add(int tid, long fid, long fver,String device, String operation, boolean isDir, String path, String toPath) {
        Jounal j = assmebleJounal(tid, fid, fver, device, operation, isDir, path, toPath);
        j.setJid(jounalDao.insert(j));
        return j.getJid();
    }

    @Override
    public boolean addBatch(List<Jounal> jounalList) {
        return jounalDao.insertBath(jounalList);
    }

    @Override
    public String getPathString(int rid){
        return folderPathService.getPathString(rid);
    }

    @Override
    public List<Jounal> list(int tid, long jid) {
        return jounalDao.list(tid, jid);
    }

    @Override
    public Jounal assmebleJounal(int tid, long fid, long fver,String device, String operation,boolean isDir, String path, String toPath){
        Jounal j = new Jounal();
        Date currentTime = new Date();
        j.setTid(tid);
        j.setJid(gerneratorService.getNextID(APP_NAME, GROUP_NAME, tid));
        j.setDevice(device);
        j.setOperation(operation);
        j.setFid(fid);
        j.setFver(fver);
        j.setOccurTime(currentTime);
        j.setDir(isDir);
        j.setPath(path);
        j.setToPath(toPath);
        return j;
    }

    @Override
    public int getLatestJid(int tid) {
        return jounalDao.getLatestJid(tid);
    }

    @Autowired
    IJounalDao jounalDao;
    @Autowired
    IIDGeneratorService gerneratorService;
    @Autowired
    FolderPathService folderPathService;
}
