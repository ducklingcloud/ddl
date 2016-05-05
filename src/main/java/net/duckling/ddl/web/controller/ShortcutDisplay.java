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
package net.duckling.ddl.web.controller;

import net.duckling.ddl.service.resource.DShortcut;
import net.duckling.ddl.service.resource.Resource;

public class ShortcutDisplay {
	private int sid;
	private int tid;
	private int tgid;
	private int squence;
	private int rid;
	private String creator;
	private String color;
	private String resourceTitle; 
	private String resourceType;
	private String resourceURL;
	private String resourceFileType;
	private boolean choice;
	public ShortcutDisplay(){
	}
	public ShortcutDisplay(DShortcut s){
		setShortcut(s);
	}
	public ShortcutDisplay(Resource r){
		setResource(r);
	}
	public void setShortcut(DShortcut s){
		sid = s.getId();
		tid = s.getTid();
		tgid =s.getTgid();
		rid = s.getRid();
		squence = s.getSequence();
		creator =s.getCreator();
		color = s.getColor();
	}
	
	public void setResource(Resource r){
		rid = r.getRid();
		resourceTitle =r.getTitle();
		resourceType = r.getItemType();
		resourceFileType=r.getFileType();
	}
	
	
	public String getColor() {
		return color;
	}

	public void setColor(String color) {
		this.color = color;
	}

	public String getResourceType() {
		return resourceType;
	}
	public void setResourceType(String resouceType) {
		this.resourceType = resouceType;
	}
	public int getSid() {
		return sid;
	}
	
	public String getResourceURL() {
		return resourceURL;
	}
	public void setResourceURL(String resouceURL) {
		this.resourceURL = resouceURL;
	}
	public void setSid(int sid) {
		this.sid = sid;
	}
	public int getTid() {
		return tid;
	}
	public void setTid(int tid) {
		this.tid = tid;
	}
	public int getTgid() {
		return tgid;
	}
	public void setTgid(int tgid) {
		this.tgid = tgid;
	}
	public int getSquence() {
		return squence;
	}
	public void setSquence(int squence) {
		this.squence = squence;
	}
	public int getRid() {
		return rid;
	}
	public void setRid(int rid) {
		this.rid = rid;
	}
	public String getCreator() {
		return creator;
	}
	public void setCreator(String creator) {
		this.creator = creator;
	}
	public String getResourceTitle() {
		return resourceTitle;
	}
	public void setResourceTitle(String resourceTitle) {
		this.resourceTitle = resourceTitle;
	}
	public boolean isChoice() {
		return choice;
	}
	public void setChoice(boolean choice) {
		this.choice = choice;
	}
	public String getResourceFileType() {
		return resourceFileType;
	}
	public void setResourceFileType(String resourceFileType) {
		this.resourceFileType = resourceFileType;
	}
	
	
}
