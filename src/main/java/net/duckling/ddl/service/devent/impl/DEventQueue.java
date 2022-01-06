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

import java.util.LinkedList;
import java.util.List;

import net.duckling.ddl.service.devent.DEvent;

import org.springframework.util.Assert;

/**
 * @date Feb 28, 2011
 * @author xiejj@cnic.cn
 */
public class DEventQueue {
    private LinkedList<DEventListener> listeners;
    private String eventType;
    public DEventQueue(String eventType) {
        this.listeners = new LinkedList<DEventListener>();
        this.eventType = eventType;
    }

    public DEventQueue(String eventType, List<DEventListener> listeners) {
        Assert.notNull(listeners, "hanler list can't be null");
        this.listeners = new LinkedList<DEventListener>();
        this.listeners.addAll(listeners);
        this.eventType = eventType;
    }
    public void addListener(DEventListener listener){
        this.listeners.add(listener);
    }

    public String getEventType(){
        return eventType;
    }
    public void handle(DEvent event){
        for (DEventListener handler:listeners){
            handler.handle(event);
        }
    }

    public void clear(){
        this.listeners.clear();
    }
}
