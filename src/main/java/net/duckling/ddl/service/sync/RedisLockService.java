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
package net.duckling.ddl.service.sync;

import javax.annotation.PostConstruct;
import javax.annotation.PreDestroy;

import org.redisson.Config;
import org.redisson.Redisson;
import org.redisson.RedissonClient;
import org.redisson.core.RLock;
import org.redisson.core.RReadWriteLock;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

@Service
public class RedisLockService implements IRedisLockService {

	private RedissonClient redisson;

	private static final String APP_PREFIX = "DDL";

	@Value("${duckling.redis.server}")
	private String host;
	@Value("${duckling.redis.port}")
	private String port;

	@PostConstruct
	public void init() {
		Config config = new Config();
		config.useSingleServer().setAddress(host + ":" + port);
		config.setThreads(10);
		redisson = Redisson.create(config);
	}

	@PreDestroy
	public void destroy() {
		redisson.shutdown();
	}

	public RLock getRemoteLock(int tid, int fid) {
		return redisson.getLock(String.format("lock:%s:%d:%d", APP_PREFIX, tid, fid));
	}

	public RReadWriteLock getReadWriteLock(int tid, String path) {
		return redisson.getReadWriteLock(String.format("rwpathlock:%s:%d:%s", APP_PREFIX, tid, path));
	}
}
