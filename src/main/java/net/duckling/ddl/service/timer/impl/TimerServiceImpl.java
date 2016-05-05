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
package net.duckling.ddl.service.timer.impl;

import java.util.Date;
import java.util.concurrent.locks.ReentrantLock;

import net.duckling.ddl.service.timer.JobTask;
import net.duckling.ddl.service.timer.TimerService;

import org.apache.log4j.Logger;
import org.quartz.Job;
import org.quartz.JobDetail;
import org.quartz.JobExecutionContext;
import org.quartz.JobExecutionException;
import org.quartz.Scheduler;
import org.quartz.SchedulerException;
import org.quartz.SchedulerFactory;
import org.quartz.Trigger;
import org.quartz.TriggerUtils;
import org.quartz.impl.StdSchedulerFactory;

public class TimerServiceImpl implements TimerService {
	public static class TimerJob implements Job{
		public void execute(JobExecutionContext context) throws JobExecutionException {
			JobTask task =(JobTask)context.getJobDetail().getJobDataMap().get("task");
			Date now = new Date();
			task.execute(now);
		}
	}
	private static final Logger LOG = Logger.getLogger(TimerService.class);
	private final ReentrantLock addJobLock = new ReentrantLock();
	
	private Scheduler schduler;
	
	private SchedulerFactory schedulerFactory;
	public TimerServiceImpl(String filename) {
		try {
			schedulerFactory = new StdSchedulerFactory(filename);
			schduler = schedulerFactory.getScheduler();
		} catch (SchedulerException e) {
			LOG.error("constructor error",e);
		}
	}
	private void addJob(String jobName, JobTask task, Trigger trigger) {
		JobDetail jobdetail = new JobDetail(jobName, Scheduler.DEFAULT_GROUP,
				TimerJob.class);
		jobdetail.getJobDataMap().put("task", task);
		addJobLock.lock();
		try {
			JobDetail job =schduler.getJobDetail(jobdetail.getName(), jobdetail.getGroup());
			if(job==null){
				schduler.scheduleJob(jobdetail, trigger);
				LOG.debug(new StringBuilder("启动调度任务:").append(jobName).append(
						",下次调度时间:").append(trigger.getNextFireTime()));
			}
		} catch (SchedulerException e) {
			LOG.error("add job error",e);
		}finally{
			addJobLock.unlock();
		}
	}
	public void addDailyTask(String jobName,int hour, int time, JobTask task){
		Trigger dailyTrigger =  TriggerUtils.makeDailyTrigger("daiyly4"+jobName, hour, time);
		addJob(jobName, task, dailyTrigger);
	}
	public void addHourlyTask(String jobName, JobTask task) {
		Trigger hourlyTrigger =  TriggerUtils.makeHourlyTrigger("hourly4"+jobName);
		addJob(jobName, task, hourlyTrigger);
	}
	public void addMinutelyTask(String jobName, JobTask task){
		Trigger minutelyTrigger = TriggerUtils.makeMinutelyTrigger("minutely4"+jobName);
		addJob(jobName, task, minutelyTrigger);
	}
	
	public void addTask(String jobName, JobTask task) {
		this.addHourlyTask(jobName, task);
		LOG.debug("new Task has been added:" + task.toString());
	}
	
	public void destroy(){
		try {
			schduler.shutdown();
		} catch (SchedulerException e) {
			LOG.error("",e);
		}
	}
	
	public void init(){
		try {
			schduler.start();
		} catch (SchedulerException e) {
			LOG.error("init error",e);
		}
	}
	
	public void removeTask(String jobName){
		try {
			schduler.deleteJob(jobName, Scheduler.DEFAULT_GROUP);
		} catch (SchedulerException e) {
			LOG.error("",e);
		}
		LOG.debug(jobName+" has been removed.");	
	}
}
