/*
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

import com.opensymphony.xwork2.ActionInvocation;
import org.apache.struts2.StrutsInternalTestCase;

import com.opensymphony.xwork2.ActionContext;
import com.opensymphony.xwork2.mock.MockActionInvocation;
import org.apache.struts2.dispatcher.HttpParameters;

/**
 * Unit test for ChecboxInterceptor. 
 */
public class CheckboxInterceptorTest extends StrutsInternalTestCase {

    private CheckboxInterceptor interceptor;
    private MockActionInvocation ai;
    private Map<String, Object> param;
    
    protected void setUp() throws Exception {
    	super.setUp();
    	param = new HashMap<>();
    	
    	interceptor = new CheckboxInterceptor();
    	ai = new MockActionInvocation();
    	ai.setInvocationContext(ActionContext.getContext());
    }

	private void prepare(ActionInvocation ai) {
		ai.getInvocationContext().setParameters(HttpParameters.create(param).build());
	}

	public void testNoParam() throws Exception {
		prepare(ai);

		interceptor.init();
		interceptor.intercept(ai);
		interceptor.destroy();

		assertEquals(0, param.size());
	}

	public void testPassthroughOne() throws Exception {
		param.put("user", "batman");

		prepare(ai);

		interceptor.init();
		interceptor.intercept(ai);
		interceptor.destroy();
		
		assertEquals(1, ai.getInvocationContext().getParameters().keySet().size());
	}

	public void testPassthroughTwo() throws Exception {
		param.put("user", "batman");
		param.put("email", "batman@comic.org");

		prepare(ai);

		interceptor.init();
		interceptor.intercept(ai);
		interceptor.destroy();
		
		assertEquals(2, ai.getInvocationContext().getParameters().keySet().size());
	}

	public void testOneCheckboxTrue() throws Exception {
		param.put("user", "batman");
		param.put("email", "batman@comic.org");
		param.put("superpower", "true");
		param.put("__checkbox_superpower", "true");
		assertTrue(param.containsKey("__checkbox_superpower"));

		prepare(ai);

		interceptor.init();
		interceptor.intercept(ai);
		interceptor.destroy();

		HttpParameters parameters = ai.getInvocationContext().getParameters();
		assertFalse(parameters.contains("__checkbox_superpower"));
		assertEquals(3, parameters.keySet().size()); // should be 3 as __checkbox_ should be removed
		assertEquals("true", parameters.get("superpower").getValue());
	}

	public void testOneCheckboxNoValue() throws Exception {
		param.put("user", "batman");
		param.put("email", "batman@comic.org");
		param.put("__checkbox_superpower", "false");
		assertTrue(param.containsKey("__checkbox_superpower"));

		prepare(ai);

		interceptor.init();
		interceptor.intercept(ai);
		interceptor.destroy();

		HttpParameters parameters = ai.getInvocationContext().getParameters();
		assertFalse(parameters.contains("__checkbox_superpower"));
		assertEquals(3, parameters.keySet().size()); // should be 3 as __checkbox_ should be removed
		assertEquals("false", parameters.get("superpower").getValue());
	}

	public void testOneCheckboxNoValueDifferentDefault() throws Exception {
		param.put("user", "batman");
		param.put("email", "batman@comic.org");
		param.put("__checkbox_superpower", "false");
		assertTrue(param.containsKey("__checkbox_superpower"));

		prepare(ai);

		interceptor.setUncheckedValue("off");
		interceptor.init();
		interceptor.intercept(ai);
		interceptor.destroy();

		HttpParameters parameters = ai.getInvocationContext().getParameters();
		assertFalse(parameters.contains("__checkbox_superpower"));
		assertEquals(3, parameters.keySet().size()); // should be 3 as __checkbox_ should be removed
		assertEquals("off", parameters.get("superpower").getValue());
	}

    public void testTwoCheckboxNoValue() throws Exception {
		param.put("user", "batman");
		param.put("email", "batman@comic.org");
		param.put("__checkbox_superpower", new String[]{"true", "true"});

		prepare(ai);

		interceptor.init();
		interceptor.intercept(ai);
		interceptor.destroy();

		HttpParameters parameters = ai.getInvocationContext().getParameters();
		assertFalse(parameters.contains("__checkbox_superpower"));
		assertEquals(2, parameters.keySet().size()); // should be 2 as __checkbox_ should be removed
		assertFalse(parameters.get("superpower").isDefined());
    }

    public void testTwoCheckboxMixed() throws Exception {
		param.put("user", "batman");
		param.put("email", "batman@comic.org");
		param.put("__checkbox_superpower", "true");
		param.put("superpower", "yes");
		param.put("__checkbox_cool", "no");
		assertTrue(param.containsKey("__checkbox_superpower"));
		assertTrue(param.containsKey("__checkbox_cool"));

		prepare(ai);

		interceptor.init();
		interceptor.intercept(ai);
		interceptor.destroy();

		HttpParameters parameters = ai.getInvocationContext().getParameters();
		assertFalse(parameters.contains("__checkbox_superpower"));
		assertFalse(parameters.contains("__checkbox_cool"));
		assertEquals(4, parameters.keySet().size()); // should be 4 as __checkbox_ should be removed
		assertEquals("yes", parameters.get("superpower").getValue());
		assertEquals("false", parameters.get("cool").getValue()); // will use false as default and not 'no'
	}

	public void testTwoCheckboxMixedWithDifferentDefault() throws Exception {
		param.put("user", "batman");
		param.put("email", "batman@comic.org");
		param.put("__checkbox_superpower", "true");
		param.put("superpower", "yes");
		param.put("__checkbox_cool", "no");
		assertTrue(param.containsKey("__checkbox_superpower"));
		assertTrue(param.containsKey("__checkbox_cool"));

		prepare(ai);

		interceptor.setUncheckedValue("no");
		interceptor.init();
		interceptor.intercept(ai);
		interceptor.destroy();

		HttpParameters parameters = ai.getInvocationContext().getParameters();
		assertFalse(parameters.contains("__checkbox_superpower"));
		assertFalse(parameters.contains("__checkbox_cool"));
		assertEquals(4, parameters.keySet().size()); // should be 4 as __checkbox_ should be removed
		assertEquals("yes", parameters.get("superpower").getValue());
		assertEquals("no", parameters.get("cool").getValue());
	}
	
}
