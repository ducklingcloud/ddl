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
import java.util.Calendar;
import java.util.Date;
import java.util.List;

import javax.annotation.PostConstruct;

import net.duckling.ddl.common.DDLFacade;
import net.duckling.ddl.service.draft.Draft;
import net.duckling.ddl.service.draft.IDraftService;
import net.duckling.ddl.service.jobmaster.JobmasterService;
import net.duckling.ddl.service.resource.PageLock;
import net.duckling.ddl.service.resource.ResourceOperateService;
import net.duckling.ddl.service.timer.JobTask;
import net.duckling.ddl.service.timer.TimerService;

import org.apache.commons.lang.time.DateFormatUtils;
import org.apache.commons.lang.time.DateUtils;
import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;


/**
 * @date Jul 13, 2011
 * @author xiejj@cnic.cn
 */
@Service
public class PageLockProvider {
	@Value("${duckling.pagelock.timeout}")
	private int timeOutInterval; //以分钟为单位|
	@Autowired
	private PageLockDAO pageLockDAO;
	@Autowired
	private TimerService timerService;
	@Autowired
	private JobmasterService jobmaster;
	
	private static final Logger LOG = Logger.getLogger(PageLockProvider.class);
			
	public void setTimeOutInterval(int timeOutInterval) {
		this.timeOutInterval = timeOutInterval;
	}
   
    
    public synchronized void unlockPage(int tid,int pid,String uid){
    	 PageLock lock = getCurrentLock(tid, pid);
    	 if(lock!=null&&lock.getUid().equals(uid)&&lock.getTid()==tid){
    		 clearPageLock(lock);
    	 }
    }
    
	public synchronized PageLock getCurrentLock(int tid, int pid){
		return getPageLock(tid,pid);
	}
	
	public synchronized  void updateLockTime(int tid, int pageid) {
		
    	PageLock  lock = getPageLock(tid,pageid);
    	if (lock!=null){
    		lock.setLastAccess(new Date());
    		pageLockDAO.updatePageLock(lock);
    	}
	}
	
	public synchronized List<PageLock> getTimeoutLocks(){
		List<PageLock> tempList = new ArrayList<PageLock>();
		Date now = new Date();
		List<PageLock> ps = pageLockDAO.getAllPageLock();
		if(ps!=null){
			for(PageLock lock:ps) {
				if(isTimeOut(lock.getLastAccess(),now)) {
					tempList.add(lock);
				}
			}
		}
		return tempList;
	}
	
	public synchronized  void clearTimeoutLock(List<PageLock> timeoutLocks) { 
		for(PageLock lock:timeoutLocks) {
			clearPageLock(lock);
		}
	}
	
	private void clearPageLock(PageLock p){
		pageLockDAO.cleanPageLock(p);
	}
	public synchronized boolean isTimeOut(Date lastAccess,Date now) {
		Date timeOut=DateUtils.addMinutes(lastAccess, this.timeOutInterval);
		return timeOut.before(now);
	}
	
	public synchronized long getLeftTimeOfPageLock(int tid, int rid) {
		PageLock lock = getPageLock(tid, rid);
		Date timeOut=DateUtils.addMinutes(lock.getLastAccess(), this.timeOutInterval);
		Calendar calendardate1 = Calendar.getInstance();
		calendardate1.setTime(new Date());
		Calendar calendardate2 = Calendar.getInstance();
		calendardate2.setTime(timeOut);
		return (calendardate2.getTimeInMillis() - calendardate1.getTimeInMillis())/1000;
	}
	
	public synchronized PageLock lockPage(int tid,int rid,String uid,int version){
		PageLock lock=null;
		lock= getPageLock(tid,rid);
		if(lock==null){
			lock=new PageLock();
			lock.setRid(rid);
			lock.setUid(uid);
			lock.setTid(tid);
			lock.setMaxVersion(version);
			lock.setLastAccess(new Date());
			addPageLock(lock);
		}
		return lock;
	}
	private void addPageLock(PageLock lock){
		pageLockDAO.addPageLock(lock);
	}
	private PageLock getPageLock(int tid,int rid){
		return pageLockDAO.getPageLock(tid, rid);
	}
	@PostConstruct
	public void doInit(){
		timerService.addMinutelyTask("pageUnlockJobWorker", new PageUnlockJobWorker());
	}
	
	private class PageUnlockJobWorker implements JobTask  {

		public void execute(Date scheduledDate) {
			if (jobmaster.take(buildJobName(scheduledDate))){
				cleanTimeoutLock();
			}
		}
		
		private void cleanTimeoutLock(){
			List<PageLock> timeoutLocks = getTimeoutLocks();
			for(PageLock lock:timeoutLocks) {
				Draft draft = DDLFacade.getBean(IDraftService.class).getManualSaveDraft(lock.getTid(), lock.getRid(), lock.getUid());
				if(draft!=null){
					try{
						DDLFacade.getBean(ResourceOperateService.class).publishManualSaveDraft(lock.getMaxVersion(),draft);
					}catch(Exception e){
						//输出发布草稿时出错信息
						LOG.warn("[rid:" + draft.getRid()+",uid:"+draft.getUid()+",title:"+draft.getTitle()+"] " + e.getMessage(),e);
					}
				}
			}
			clearTimeoutLock(timeoutLocks);
		}
		
		private String buildJobName(Date currentDay) {
			return "pageLockProvider"+DateFormatUtils.format(currentDay, "yyyy-MM-dd HH:mm");
		}

	}
	
}
