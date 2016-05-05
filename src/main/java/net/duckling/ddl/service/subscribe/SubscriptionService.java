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
package net.duckling.ddl.service.subscribe;

import java.util.List;


public interface SubscriptionService {

	 void removePersonSubscription(int tid, String uid,
			int uxid);

	 void removePageSubscribe(int tid, int rid);

	 boolean isFeedPerson(String user, int id);

	 void removeSubscription(int feedId);

	 void removeSubscription(String currUser, Publisher[] publishers);

	 void addBatchFeedRecords(String currUser, Publisher[] publishers);

	 int addSingleFeedRecord(String feeder, Publisher publisher);

	 int addMyCreatePageFeedRecord(String user, int rid);

	 List<Subscription> getPersonSubscribers(int tid, String author);

	 List<Subscription> getSubscriptionByUserId(int tid, String userId,
			String type);

	 List<Subscription> getPageSubscribers(int tid, int rid);

	 List<String> getTeamMemberFeedList(String user, int tid);

}
