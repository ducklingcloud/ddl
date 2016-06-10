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

package cn.vlabs.duckling.aone.infrastructure.repository;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;

import java.util.Date;

import net.duckling.ddl.service.comment.Comment;
import net.duckling.ddl.service.comment.impl.CommentDAO;
import net.duckling.ddl.service.user.SimpleUser;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import cn.vlabs.duckling.BaseTest;

/**
 * @date Mar 17, 2011
 * @author xiejj@cnic.cn
 */
public class CommentDAOTest extends BaseTest {
    private CommentDAO cd;

    @Before
    public void setUp() throws Exception {
        cd = f.getBean(CommentDAO.class);
    }

    @After
    public void tearDown() throws Exception {
        cd.clean(0);
        cd = null;
    }

    @Test
    public void testCreateComment() {
        Comment comment = newComment(-1);
        int commentId = cd.createComment(comment);

        Comment loadedComment = cd.getComment(0,commentId);
        assertNotNull(loadedComment);
        assertEquals(comment.getContent(), loadedComment.getContent());

        Comment replayComment = newComment(commentId);
        cd.createComment(replayComment);
    }

    @SuppressWarnings("unused")
    private Comment newComment(int replyTo) {
        Comment comment = new Comment();
        comment.setContent("赞!");
        comment.setCreateTime(new Date());
        comment.setRid(1);
        SimpleUser user = new SimpleUser();
        user.setEmail("zz@cnic.cn");
        comment.setSender(user);
        comment.setReceiver(user);
        return comment;
    }

    @Test
    public void testGetPageComments() {
    }

    @Test
    public void testRemoveComment() {
        Comment a = newComment(-1);
        int commentId = cd.createComment(a);
        cd.removeComment(0,commentId);
        assertNull(cd.getComment(0,commentId));
    }

}
