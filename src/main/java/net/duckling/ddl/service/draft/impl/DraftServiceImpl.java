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

package net.duckling.ddl.service.draft.impl;

import java.util.Date;

import net.duckling.ddl.service.draft.Draft;
import net.duckling.ddl.service.draft.IDraftService;
import net.duckling.ddl.service.resource.PageRender;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;


/**
 * @date 2011-7-15
 * @author Clive Lee
 */
@Service
public class DraftServiceImpl implements IDraftService {
	
    @Autowired
	private DraftDAO draftDao;
    
	@Override
	public void updateAutoSaveDraft(int tid,PageRender page,String uid) {
		updateDraftByType(tid, page,uid, Draft.AUTO_TYPE);
	}
	
	@Override
	public void updateManualSaveDraft(int tid,PageRender page,String uid) {
		updateDraftByType(tid,page,uid, Draft.MANUAL_TYPE);
	}
	
	private void updateDraftByType(int tid, PageRender page,String uid, String currentType) {
		Draft draft  = null;
		if(draftDao.isExistDraft(tid,page.getMeta().getRid(),uid,currentType)) {
			draft = draftDao.getInstance(tid, page.getMeta().getRid(),uid, currentType);
			draft.setContent(page.getDetail().getContent());
			draft.setModifyTime(new Date());
			draft.setTitle(page.getMeta().getTitle());
			draftDao.update(draft);
		}else {
			draft = new Draft(tid,page.getMeta().getRid(),uid,page.getMeta().getTitle(),page.getDetail().getContent(),currentType);
			draftDao.insert(draft);
		}
	}
	
	@Override
	public Draft getAutoSaveDraft(int tid,int pid,String uid) {
		return draftDao.getInstance(tid, pid, uid, Draft.AUTO_TYPE);
	}
	
	@Override
	public Draft getManualSaveDraft(int tid,int pid,String uid) {
		return draftDao.getInstance(tid, pid, uid, Draft.MANUAL_TYPE);
	}

	@Override
	public void clearAutoSaveDraft(int tid, int pid, String uid) {
		draftDao.delete(tid,pid,uid,Draft.AUTO_TYPE);
	}

	@Override
	public void clearManualSaveDraft(int tid, int pid, String uid) {
		draftDao.delete(tid,pid,uid,Draft.MANUAL_TYPE);
	}

	@Override
	public Draft getLastestDraft(int tid, Integer pid, String currentUid) {
		return draftDao.getLatest(tid, pid, currentUid);
	}

	
}
