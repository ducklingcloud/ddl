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

package cn.vlabs.duckling.vwb.ui.command;


import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;

import java.util.Map;

import net.duckling.ddl.service.url.UrlPatternResolver;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;

/**
 * @date Jun 16, 2011
 * @author IBM
 */
public class UrlPatternResolverTest {

	@Before
	public void setUp() throws Exception {
	}

	/**
	 * @throws java.lang.Exception
	 */
	@After
	public void tearDown() throws Exception {
	}
	
	@Test
	public void testAttach(){
		UrlPatternResolver resolver = new UrlPatternResolver();
		Map<String, String> params = resolver.resolve("/attach/123213");
		assertNotNull(params);
		assertEquals("123213", params.get("key"));
		assertEquals("attach", params.get("type"));
	}
	@Test
	public void testCachable(){
		UrlPatternResolver resolver = new UrlPatternResolver();
		Map<String, String> params = resolver.resolve("/cachable/#AQEEE");
		assertNotNull(params);
		assertEquals("#AQEEE", params.get("key"));
		assertEquals("cachable", params.get("type"));
	}
	@Test
	public void testFile(){
		UrlPatternResolver resolver = new UrlPatternResolver();
		Map<String, String> params = resolver.resolve("/file/123213");
		assertNotNull(params);
		assertEquals("123213", params.get("key"));
		assertEquals("file", params.get("type"));
	}

}
