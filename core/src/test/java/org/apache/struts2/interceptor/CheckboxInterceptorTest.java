/*
 * $Id$
 * 
 * Licensed to the Apache Software Foundation (ASF) under one
 * or more contributor license agreements.  See the NOTICE file
 * distributed with this work for additional information
 * regarding copyright ownership.  The ASF licenses this file
 * to you under the Apache License, Version 2.0 (the
 * "License"); you may not use this file except in compliance
 * with the License.  You may obtain a copy of the License at
 *
 *  http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing,
 * software distributed under the License is distributed on an
 * "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY
 * KIND, either express or implied.  See the License for the
 * specific language governing permissions and limitations
 * under the License.
 */
package org.apache.struts2.interceptor;

import java.util.HashMap;
import java.util.Map;

import org.apache.struts2.StrutsTestCase;

import com.opensymphony.xwork2.ActionContext;
import com.opensymphony.xwork2.mock.MockActionInvocation;

/**
 * Unit test for ChecboxInterceptor. 
 */
public class CheckboxInterceptorTest extends StrutsTestCase {

    private CheckboxInterceptor interceptor;
    private MockActionInvocation ai;
    private Map<String, String> param;
    
    protected void setUp() throws Exception {
    	super.setUp();
    	param = new HashMap<String, String>();
    	
    	interceptor = new CheckboxInterceptor();
    	ai = new MockActionInvocation();
    	ai.setInvocationContext(ActionContext.getContext());
    	ActionContext.getContext().setParameters(param);
    }
	
	public void testNoParam() throws Exception {
		interceptor.init();
		interceptor.intercept(ai);
		interceptor.destroy();

		assertEquals(0, param.size());
	}

	public void testPassthroughOne() throws Exception {
		param.put("user", "batman");
		interceptor.init();
		interceptor.intercept(ai);
		interceptor.destroy();
		
		assertEquals(1, param.size());
	}

	public void testPassthroughTwo() throws Exception {
		param.put("user", "batman");
		param.put("email", "batman@comic.org");
		interceptor.init();
		interceptor.intercept(ai);
		interceptor.destroy();
		
		assertEquals(2, param.size());
	}

	public void testOneCheckboxTrue() throws Exception {
		param.put("user", "batman");
		param.put("email", "batman@comic.org");
		param.put("superpower", "true");
		param.put("__checkbox_superpower", "true");
		assertTrue(param.containsKey("__checkbox_superpower"));

		interceptor.init();
		interceptor.intercept(ai);
		interceptor.destroy();
		
		assertFalse(param.containsKey("__checkbox_superpower"));
		assertEquals(3, param.size()); // should be 3 as __checkbox_ should be removed
		assertEquals("true", param.get("superpower"));
	}

	public void testOneCheckboxNoValue() throws Exception {
		param.put("user", "batman");
		param.put("email", "batman@comic.org");
		param.put("__checkbox_superpower", "false");
		assertTrue(param.containsKey("__checkbox_superpower"));

		interceptor.init();
		interceptor.intercept(ai);
		interceptor.destroy();
		
		assertFalse(param.containsKey("__checkbox_superpower"));
		assertEquals(3, param.size()); // should be 3 as __checkbox_ should be removed
		assertEquals("false", param.get("superpower"));
	}

	public void testOneCheckboxNoValueDifferentDefault() throws Exception {
		param.put("user", "batman");
		param.put("email", "batman@comic.org");
		param.put("__checkbox_superpower", "false");
		assertTrue(param.containsKey("__checkbox_superpower"));

		interceptor.setUncheckedValue("off");
		interceptor.init();
		interceptor.intercept(ai);
		interceptor.destroy();
		
		assertFalse(param.containsKey("__checkbox_superpower"));
		assertEquals(3, param.size()); // should be 3 as __checkbox_ should be removed
		assertEquals("off", param.get("superpower"));
	}

	public void testTwoCheckboxMixed() throws Exception {
		param.put("user", "batman");
		param.put("email", "batman@comic.org");
		param.put("__checkbox_superpower", "true");
		param.put("superpower", "yes");
		param.put("__checkbox_cool", "no");
		assertTrue(param.containsKey("__checkbox_superpower"));
		assertTrue(param.containsKey("__checkbox_cool"));

		interceptor.init();
		interceptor.intercept(ai);
		interceptor.destroy();
		
		assertFalse(param.containsKey("__checkbox_superpower"));
		assertFalse(param.containsKey("__checkbox_cool"));
		assertEquals(4, param.size()); // should be 4 as __checkbox_ should be removed
		assertEquals("yes", param.get("superpower"));
		assertEquals("false", param.get("cool")); // will use false as default and not 'no'
	}

	public void testTwoCheckboxMixedWithDifferentDefault() throws Exception {
		param.put("user", "batman");
		param.put("email", "batman@comic.org");
		param.put("__checkbox_superpower", "true");
		param.put("superpower", "yes");
		param.put("__checkbox_cool", "no");
		assertTrue(param.containsKey("__checkbox_superpower"));
		assertTrue(param.containsKey("__checkbox_cool"));

		interceptor.setUncheckedValue("no");
		interceptor.init();
		interceptor.intercept(ai);
		interceptor.destroy();
		
		assertFalse(param.containsKey("__checkbox_superpower"));
		assertFalse(param.containsKey("__checkbox_cool"));
		assertEquals(4, param.size()); // should be 4 as __checkbox_ should be removed
		assertEquals("yes", param.get("superpower"));
		assertEquals("no", param.get("cool"));
	}
	
}
