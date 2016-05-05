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

package net.duckling.ddl.web.api;

import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import net.duckling.ddl.common.Site;
import net.duckling.ddl.service.devent.AoneNoticeParam;
import net.duckling.ddl.service.devent.INoticeService;
import net.duckling.ddl.service.devent.Notice;
import net.duckling.ddl.service.devent.NoticeRule;
import net.duckling.ddl.service.mail.notice.CompositeNotice;
import net.duckling.ddl.service.mail.notice.DailyCompositeNotice;
import net.duckling.ddl.service.mail.notice.DailyNotice;
import net.duckling.ddl.service.team.TeamPreferenceService;
import net.duckling.ddl.util.JsonUtil;
import net.duckling.ddl.web.interceptor.access.RequirePermission;

import org.apache.commons.lang.time.DateUtils;
import org.apache.log4j.Logger;
import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;


/**
 * 查询系统更新
 * 
 * @date 2011-8-22
 * @author xiejj@cnic.cn
 */
@Controller
@RequestMapping("/api/teamUpdates")
@RequirePermission(target="team", operation="view")
public class APITeamUpdatesController extends APIBaseNoticeController {
    
	private static final int DEFAULT_MOBILE_DURATION = -7;
	private static final int DEFAULT_FILTER_COUNT = 10;
	private static final int DEFAULT_MIN_COUNT = 10;
	private static final int DEFAULT_MAX_COUNT = 50;
	private static final String RECORDS = "records";
	private static final String DATE = "date";
	private static final String OFFSET = "offset";
	private static final Logger log = Logger.getLogger(APITeamUpdatesController.class);
	@Autowired
	private TeamPreferenceService teamPreferenceService;
	@Autowired
    private INoticeService noticeService;
	
	@SuppressWarnings("unchecked")
	@RequestMapping
	public void service(HttpServletRequest request, HttpServletResponse response) {
		Site site = findSite(request);
		String uid = findUser(request);
		int tid = site.getId();
		String date = request.getParameter(DATE);
		String offsetStr = request.getParameter(OFFSET);
		int offset = 0; // 上次返回时的偏移量offset
		try {
			offset = Integer.parseInt(offsetStr);
		} catch(NumberFormatException e) {
			log.error("",e);
		}
		
		AoneNoticeParam param = getMobileTeamNoticeQueryParam(tid, date, offset);
		
		// 获取七天的数据，为空，循环获取
		List<Notice> teamNoticeList = noticeService.readNotification(param,uid);
		for(int i=0; i<10; i++) {
			// 最多做十次循环，防止每次查询都没数据时后台出现死循环的现象
			if(teamNoticeList == null || teamNoticeList.size() < DEFAULT_MIN_COUNT + offset) {
				param.setBeginDate(DateUtils.addDays(param.getBeginDate(), DEFAULT_MOBILE_DURATION));
				param.setEndDate(DateUtils.addDays(param.getEndDate(), DEFAULT_MOBILE_DURATION));
				List<Notice> tempTeamNoticeList = noticeService.readNotification(param,uid);
				if(tempTeamNoticeList != null && tempTeamNoticeList.size() > 0) {
					teamNoticeList.addAll(tempTeamNoticeList);
				}
			}
		}
		
		DailyNotice[] dailyGroup = getDailyNoticeArray(teamNoticeList);
		List<DailyCompositeNotice> results = getDailyCompositeList(dailyGroup);
		
		//处理结果集
		Map<String, Object> resultMap = filterByCount(results, offset, date);
		results = (List<DailyCompositeNotice>)resultMap.get(RECORDS);
		String tempOffset = String.valueOf(resultMap.get(OFFSET));
		String tempdate = String.valueOf(resultMap.get(DATE));
		if(tempdate != null && !"".equals(tempdate)) {
			date = tempdate;
		}
		teamPreferenceService.updateNoticeAccessTime(uid, tid, NoticeRule.TEAM_NOTICE);
		JSONArray jsonArray = JsonUtil.getJSONArrayFromList(results);
		JSONObject jsonObj = new JSONObject();
		jsonObj.put(RECORDS, jsonArray);
		jsonObj.put(DATE, date);
		jsonObj.put(OFFSET, tempOffset);
		String api = request.getParameter("api");
		jsonObj.put("api", api);
		JsonUtil.writeJSONObject(response, jsonObj);
	}
	
	/**
	 * 按记录数过滤，每次最多返回DEFAULT_MAX_COUNT条，最少返回DEFAULT_MIN_COUNT条
	 * @param dailyCompositeList
	 * @param offset 上次数据偏移量
	 * @param startDate 上次数据取得天数
	 * @return
	 */
	private static Map<String, Object> filterByCount(List<DailyCompositeNotice> dailyCompositeList, int offset, String startDate) {
		Map<String, Object> resultMap = new HashMap<String, Object>();
		List<DailyCompositeNotice> result = new ArrayList<DailyCompositeNotice>();
		if(dailyCompositeList == null || dailyCompositeList.size() <= 0) return resultMap;
		int count = 0;
		String date = null;
		
		for(DailyCompositeNotice dailyComposite : dailyCompositeList) {
			date = dailyComposite.getDate();
			CompositeNotice[] compositeList = dailyComposite.getCompositeArray();
			if(compositeList == null) continue;
			
			int lenth = compositeList.length;
			int tempCount = 0;
			if(date.equalsIgnoreCase(startDate) && lenth > offset) {// 是上次取数据的时间，去掉上次已经取的数据
				tempCount = 0;
				int max = lenth-offset;
				CompositeNotice[] tempNotice = new CompositeNotice[max>DEFAULT_MAX_COUNT?DEFAULT_MAX_COUNT:max];
				
				for(CompositeNotice compositeNotice : compositeList) {
					if(tempCount >= offset && tempCount < offset + DEFAULT_MAX_COUNT) {
						tempNotice[tempCount-offset] = compositeNotice;
						tempCount ++;
					} else if(tempCount < offset) {
						tempCount ++;
					} else {
						break;
					}
				}
				
				dailyComposite.setCompositeArray(tempNotice);
				count = count + tempCount - offset;
			} else { // 非上次数据，按偏移量增加
				int max = DEFAULT_MAX_COUNT - count;
				tempCount = 0;
				CompositeNotice[] tempNotice = new CompositeNotice[max>lenth?lenth:max];
				
				for(CompositeNotice compositeNotice : compositeList) {
					if(tempCount < max) {
						tempNotice[tempCount] = compositeNotice;
						tempCount ++;
					} else {
						break;
					}
				}
				
				dailyComposite.setCompositeArray(tempNotice);
				count += tempCount;
			}
			
			resultMap.put(DATE, date);
			if(tempCount >= lenth) {
				resultMap.put(OFFSET, 0);
			} else {
				resultMap.put(OFFSET, tempCount);
			}
			result.add(dailyComposite);
			
			if(count >= DEFAULT_FILTER_COUNT) {
				break;
			}
		}
		resultMap.put(RECORDS, result);
		return resultMap;
	}
	
	@SuppressWarnings("deprecation")
	private static AoneNoticeParam getMobileTeamNoticeQueryParam(int tid, String date, int offset) {
		AoneNoticeParam p = new AoneNoticeParam(tid,NoticeRule.TEAM_NOTICE, tid+"");
		DateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd");
		Date begin = null;
		try {
			if(date != null) {
				begin = dateFormat.parse(date);
				if(offset > 0) {
					// 修改时间偏正值，时钟和分钟
					Date now = new Date();
					int hour = now.getHours();
					int minute = now.getMinutes();
					long time = begin.getTime() + hour*60*60*1000 + minute*60*1000;
					begin = new Date(time);
				}
			} else {
				begin = new Date();
			}
		} catch (ParseException e) {
			begin = new Date();
		}
		p.setBeginDate(DateUtils.addDays(begin, DEFAULT_MOBILE_DURATION));
		p.setEndDate(begin);
		return p;
	}
	
}
