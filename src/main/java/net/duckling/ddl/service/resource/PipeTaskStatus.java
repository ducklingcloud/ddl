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

import java.util.List;

public class PipeTaskStatus {
	
	private final static String STATUS_FINISHED = "finished";
	private final static String STATUS_PROCESSING = "processing";
	
	private int total;
	private int success;
	private int failed;
	private int processing;
	private int waiting;
	private String taskId;
	private String status;
	
	private List<SubTask> subTaskList;
	
	public void setStatus(String status) {
		if("success".equals(status) || "failed".equals(status)){
			this.status = STATUS_FINISHED;
			return;
		}
		this.status = STATUS_PROCESSING;
	}
	public String getStatus() {
		return status;
	}
	public String getTaskId() {
		return taskId;
	}
	public void setTaskId(String taskId) {
		this.taskId = taskId;
	}
	public int getTotal() {
		return total;
	}
	public void setTotal(int total) {
		this.total = total;
	}
	public int getSuccess() {
		return success;
	}
	public void setSuccess(int success) {
		this.success = success;
	}
	public int getFailed() {
		return failed;
	}
	public void setFailed(int failed) {
		this.failed = failed;
	}
	public int getProcessing() {
		return processing;
	}
	public void setProcessing(int processing) {
		this.processing = processing;
	}
	public int getWaiting() {
		return waiting;
	}
	public void setWaiting(int waiting) {
		this.waiting = waiting;
	}

	public List<SubTask> getSubTaskList() {
		return subTaskList;
	}
	public void setSubTaskList(List<SubTask> subTaskList) {
		this.subTaskList = subTaskList;
	}
	
	@Override
	public String toString() {
		StringBuilder sb = new StringBuilder();
		sb.append("total=").append(total);
		sb.append(",success=").append(success);
		sb.append(",processing=").append(processing);
		sb.append(",failed=").append(failed);
		return sb.toString();
	}
	
}
