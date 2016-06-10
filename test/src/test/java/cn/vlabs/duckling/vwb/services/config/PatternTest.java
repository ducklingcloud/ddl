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

package cn.vlabs.duckling.vwb.services.config;


import static org.junit.Assert.*;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;

public class PatternTest {
	Pattern pattern = Pattern.compile("([^\\}]*)\\$\\{([^}]*)\\}(.*)");
	@Before
	public void setUp() throws Exception {
	}

	@After
	public void tearDown() throws Exception {
	}
	
	@Test
	public void testOne(){
		Matcher matcher = pattern.matcher("fdaf${abc}adfe");
		assertTrue(matcher.matches());
		assertEquals(3, matcher.groupCount());
		assertEquals("abc", matcher.group(2));
	}
	
	@Test
	public void testTwo(){
		Matcher matcher = pattern.matcher("fdaf${abc}a${df}e");
		assertTrue(matcher.matches());
		assertEquals(3, matcher.groupCount());
		assertEquals("abc", matcher.group(2));
	}
	
	@Test
	public void testEmptyHead(){
		Matcher matcher = pattern.matcher("${abc}a${df}e");
		assertTrue(matcher.matches());
		assertEquals(3, matcher.groupCount());
		assertEquals("abc", matcher.group(2));
		assertEquals("", matcher.group(1));
	}
}
