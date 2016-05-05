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

package net.duckling.ddl.service.resource;

import java.util.Date;
import java.util.List;

import org.apache.commons.lang.time.DateFormatUtils;
import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;

import net.duckling.ddl.common.DDLFacade;
import net.duckling.ddl.service.draft.Draft;
import net.duckling.ddl.service.draft.IDraftService;
import net.duckling.ddl.service.jobmaster.JobmasterService;
import net.duckling.ddl.service.resource.impl.PageLockProvider;
import net.duckling.ddl.service.timer.JobTask;

/**
 * @date 2011-7-19
 * @author Clive Lee
 */
public class PageUnlockJobWorker implements JobTask  {
	private static final Logger LOG = Logger.getLogger(PageUnlockJobWorker.class);
	@Autowired
	private PageLockProvider provider;
	@Autowired
	private JobmasterService jobmaster;

	public void execute(Date scheduledDate) {
		if (jobmaster.take(buildJobName(scheduledDate))){
			cleanTimeoutLock();
		}
	}
	
	private void cleanTimeoutLock(){
		List<PageLock> timeoutLocks = provider.getTimeoutLocks();
		for(PageLock lock:timeoutLocks) {
			Draft draft = DDLFacade.getBean(IDraftService.class).getManualSaveDraft(lock.getTid(), lock.getRid(), lock.getUid());
			if(draft!=null){
				try{
					DDLFacade.getBean(ResourceOperateService.class).publishManualSaveDraft(lock.getMaxVersion(),draft);
				}catch(Exception e){
					LOG.error("清除页面锁错误", e);
				}
			}
		}
		provider.clearTimeoutLock(timeoutLocks);
	}
	
	private String buildJobName(Date currentDay) {
		return "pageUnlockJob"+DateFormatUtils.format(currentDay, "yyyy-MM-dd HH:mm");
	}

}
