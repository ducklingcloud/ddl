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
package net.duckling.ddl.web.bean;

import java.util.ArrayList;
import java.util.List;

public class PageNum{
	int currPageNum=0;
	int pageSize=30;
	int totalNum=0;
	//用于控制页面显示的条数
	int showNumSize=5;
	public PageNum(int currPageNum,int totalNum,int pageSize){
		this.currPageNum=currPageNum;
		this.pageSize=pageSize;
		this.totalNum=totalNum;
	}
	
	public int getTotalPageNum(){
		if(totalNum<=0){
			return 0;
		}
		
		return totalNum/pageSize+1;
	}
	
	public boolean hasNext(){
		return currPageNum<getTotalPageNum();
	}
	
	public boolean hasPre(){
		return currPageNum>1;
	}
	
	public List<Integer> getPageNumList(){
		List<Integer> result=new ArrayList<Integer>();
		int temp=(showNumSize-1)/2;
		//前面数字个数
		int lowNumSize=currPageNum-1;
		//后面数字个数
		int highNumSize=getTotalPageNum()-currPageNum;
		
		
		int needPre=lowNumSize-temp;
		int needNext=highNumSize-temp;
		//记录前后最终页数
		int highTemp=temp;
		int lowTemp=temp;
		
		//只前面不够
		if(needPre<0&&needNext>=0){
			lowTemp=lowNumSize;
			highTemp=temp-needPre;
		}
		
		//只后面不够
		if(needNext<0&&needPre>=0){
			highTemp=highNumSize;
			lowTemp=temp-needNext;
		}
		
		
		/*if(lowNumSize<temp||highNumSize<temp){
			
			
			
			if(lowNumSize<temp){
				lowTemp=lowNumSize;
				highTemp=temp+highNumSize-lowNumSize;
			}
		}*/
		
		while(lowTemp>0){
			if(currPageNum-lowTemp<=0){
				lowTemp=currPageNum-1;
			}
			int tempNum=currPageNum-lowTemp;
			if(tempNum<1 || tempNum==currPageNum){
				break;
			}
			result.add(tempNum);
			lowTemp--;
		}
		
		result.add(currPageNum);
		
		int i=0;
		while(i<highTemp){
			++i;
			int tempNum=currPageNum+i;
			if(tempNum>this.getTotalPageNum()){
				break;
			}
			result.add(tempNum);
			//highTemp--;
		}
		
		
		return result;
	}
	
	public int getOffset(){
		return (currPageNum-1)*pageSize;
	}

	public int getCurrPageNum() {
		return currPageNum;
	}

	public void setCurrPageNum(int currPageNum) {
		this.currPageNum = currPageNum;
	}

	public int getPageSize() {
		return pageSize;
	}

	public void setPageSize(int pageSize) {
		this.pageSize = pageSize;
	}

	public int getTotalNum() {
		return totalNum;
	}

	public void setTotalNum(int totalNum) {
		this.totalNum = totalNum;
	}

	public int getShowNumSize() {
		return showNumSize;
	}

	public void setShowNumSize(int showNumSize) {
		this.showNumSize = showNumSize;
	}
}
