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

package net.duckling.ddl.service.devent.impl;

import java.util.Collections;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.LinkedBlockingQueue;

import javax.annotation.PostConstruct;
import javax.annotation.PreDestroy;

import net.duckling.ddl.common.VWBContext;
import net.duckling.ddl.service.devent.DEvent;

import org.apache.log4j.Logger;
import org.springframework.stereotype.Service;
import org.springframework.util.Assert;


/**
 * @date Feb 28, 2011
 * @author xiejj@cnic.cn
 */
@Service
public class DEventRunner {
	private class EventWorker extends Thread {
		private boolean stopped = false;
		
		public EventWorker(){
			super("MessageThread");
		}

		public void run() {
			while (!stopped) {
				try {
					DEvent event = eventQueue.take();
					VWBContext.setCurrentTid(event.getTid());
					DEventQueue handlerQueue = handlerMap.get(event.getEventType());
					if (handlerQueue != null) {
						handlerQueue.handle(event);
					} else {
						log.error("Event handler for " + event.getEventType()
								+ " not found.");
						log.error(event);
					}
				} catch (InterruptedException e) {
					log.info("Thread for event service is interrupted.");
				} catch (Exception e) {
					log.error("Error occured: ", e);
				}
			}
		}

		public void stopLoop() {
			this.stopped = true;
		}
	}

	private final  static Logger log = Logger.getLogger(DEventRunner.class);

	private BlockingQueue<DEvent> eventQueue;

	private Map<String, DEventQueue> handlerMap;

	private EventWorker worker;

	public DEventRunner() {
		eventQueue = new LinkedBlockingQueue<DEvent>();
		handlerMap = Collections.synchronizedMap(new HashMap<String, DEventQueue>());
		worker = new EventWorker();
	}
	/**
	 * 注册事件监听处理程序
	 * @param queueName	事件队列
	 * @param listener	监听处理程序
	 */
	public void registListener(String queueName, DEventListener listener){
		//由于这个函数只在系统初始化时被调用，因此这里不再加synchronized同步语义。
		Assert.notNull(queueName,"queue's name can't be null");
		Assert.notNull(listener, "listener can't be null");
		DEventQueue queue = handlerMap.get(queueName);
		if (queue==null){
			queue = new DEventQueue(queueName);
			handlerMap.put(queueName, queue);
		}
		queue.addListener(listener);
	}
	public void raise(DEvent event) {
		try {
			eventQueue.put(event);
		} catch (InterruptedException e) {

		}
	}
	
	@PostConstruct
	public void start() {
		worker.start();
	}

	@PreDestroy
	public void stop(){
		worker.stopLoop();
		worker.interrupt();
		handlerMap.clear();
		eventQueue.clear();
	}
}
