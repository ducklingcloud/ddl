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
package net.duckling.ddl.service.task;
/**进度情况用于独立任务
 * @author lvly
 * @since 2012-6-25
 * */
public class UserProcess extends UserIdFormatter{
	private int undoCount;
	private int doingCount;
	private int finishCount;
	public UserProcess(){}
	/**构造方法*/
	public UserProcess(int uC,int dC,int fC,String userId){
		this.undoCount=uC;
		this.doingCount=dC;
		this.finishCount=fC;
		this.setUserId(userId);
	}
	@Override
	public String toString(){
		int allCount=undoCount+doingCount+finishCount;
		return"[['未接受("+undoCount+"/"+allCount+")',"+undoCount+"],"+
				"['执行中("+doingCount+"/"+allCount+")',"+doingCount+"],"+
				"['已完成("+finishCount+"/"+allCount+")',"+finishCount+"]]";
	}
	/**获得进度*/
	public double getUserProcess(){
		
		return (finishCount*100.0+0.0)/(finishCount+doingCount+0.0);
	}
	/**获得共享任务用户进度*/
	public double getShareProcess(){
		return (finishCount*100+0.0)/(undoCount+doingCount+finishCount+0.0);
	}
	/**用分数形式显示*/
	public String getUserProcessStr(){
		return (finishCount)+"/"+(finishCount+doingCount);
	}
	/**共享任务进度用分数形式*/
	public String getShareProcessStr(){
		return finishCount+"/"+(undoCount+doingCount+finishCount);
	}
	public int getUndoCount() {
		return undoCount;
	}
	public void setUndoCount(int undoCount) {
		this.undoCount = undoCount;
	}
	public int getDoingCount() {
		return doingCount;
	}
	public void setDoingCount(int doingCount) {
		this.doingCount = doingCount;
	}
	public int getFinishCount() {
		return finishCount;
	}
	public void setFinishCount(int finishCount) {
		this.finishCount = finishCount;
	}

	
}
