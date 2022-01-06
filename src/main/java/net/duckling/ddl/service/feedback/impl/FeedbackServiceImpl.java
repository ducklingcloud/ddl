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

package net.duckling.ddl.service.feedback.impl;

import java.util.List;

import net.duckling.ddl.service.feedback.Feedback;
import net.duckling.ddl.service.feedback.FeedbackService;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

/**
 * @date 2011-12-8
 * @author clive
 */
@Service
public class FeedbackServiceImpl implements FeedbackService {
    @Autowired
    private FeedbackDAO feedbackDao;

    @Override
    public void insert(Feedback f) {
        feedbackDao.inset(f);
    }

    @Override
    public List<Feedback> getFeedbackList(String type) {
        return feedbackDao.getFeedbackByType(type);
    }

    @Override
    public List<Feedback> getAll() {
        return feedbackDao.getAll();
    }
}
