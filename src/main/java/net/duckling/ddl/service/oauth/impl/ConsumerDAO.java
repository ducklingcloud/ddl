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

package net.duckling.ddl.service.oauth.impl;

import net.duckling.ddl.service.oauth.OAuthConsumerExt;


/**
 * 负责操作OAuthConsumer表
 * @date 2011-8-29
 * @author xiejj@cnic.cn
 */
public interface ConsumerDAO {
    /**
     * 根据ConsumerKey查询OAuth的consumer.
     * @param consumerKey 分配给用户的consumerKey。这个是有字母和数字组成的字符串。
     * @return consumerKey对用的Consumer对象。
     */
    OAuthConsumerExt getConsumer(String consumerKey);
    /**
     * 创建Consumer
     * @param consumer
     */
    void createConsumer(OAuthConsumerExt consumer);
    /**
     * 更新Consumer
     * @param consumer
     */
    void updateConsumer(OAuthConsumerExt consumer);
    /**
     * 删除所有登记的Consumer（测试用）
     */
    void clear();
}
