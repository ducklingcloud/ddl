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
package cn.vlabs.duckling.util;

import junit.framework.Assert;

import net.duckling.ddl.util.SiteUtil;

import org.junit.Before;
import org.junit.Test;
import org.springframework.mock.web.MockHttpServletRequest;

public class SiteUtilTest {

    MockHttpServletRequest request;

    @Before
    public void setup() {
        request = new MockHttpServletRequest();

    }

    @Test
    public void testGetTeamCodeFromRequest() {
        request.setContextPath("/ddl");

        request.setRequestURI("/ddl/system/reclogging");
        String teamcode = SiteUtil.parseTeamCode(request);
        Assert.assertEquals(null, teamcode);

        request.setRequestURI("/ddl/cerc/reclogging");
        teamcode = SiteUtil.parseTeamCode(request);
        Assert.assertEquals("cerc", teamcode);

        request.setContextPath("/");

        request.setRequestURI("/system/reclogging");
        teamcode = SiteUtil.parseTeamCode(request);
        Assert.assertEquals(null, teamcode);

        request.setRequestURI("/what/reclogging");
        teamcode = SiteUtil.parseTeamCode(request);
        Assert.assertEquals("what", teamcode);

        request.setRequestURI("/jsp/reclogging");
        teamcode = SiteUtil.parseTeamCode(request);
        Assert.assertEquals(null, teamcode);
    }

}
