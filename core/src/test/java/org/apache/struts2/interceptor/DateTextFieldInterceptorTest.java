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

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

import org.apache.struts2.StrutsInternalTestCase;

import com.opensymphony.xwork2.ActionContext;
import com.opensymphony.xwork2.mock.MockActionInvocation;
import org.apache.struts2.dispatcher.HttpParameters;

/**
 * Unit test for DateTextFieldInterceptor. 
 */
public class DateTextFieldInterceptorTest extends StrutsInternalTestCase {

    private DateTextFieldInterceptor interceptor;
    private MockActionInvocation ai;
    private Map<String, Object> param;
    
    protected void setUp() throws Exception {
    	super.setUp();
    	param = new HashMap<>();
    	
    	interceptor = new DateTextFieldInterceptor();
    	ai = new MockActionInvocation();
    	ai.setInvocationContext(ActionContext.getContext());
    }
	
	public void testNoParam() throws Exception {
		ActionContext.getContext().setParameters(HttpParameters.create(param).build());

		interceptor.init();
		interceptor.intercept(ai);
		interceptor.destroy();

		assertEquals(0, ai.getInvocationContext().getParameters().keySet().size());
	}

	public void testOneDateTextField() throws Exception {
		param.put("__year_name", new String[]{"2000"});
		param.put("__month_name", new String[]{"06"});
		param.put("__day_name", new String[]{"15"});

		ActionContext.getContext().setParameters(HttpParameters.create(param).build());

		interceptor.init();
		interceptor.intercept(ai);
		interceptor.destroy();

		HttpParameters parameters = ai.getInvocationContext().getParameters();
		assertFalse(parameters.contains("__year_name"));
		assertFalse(parameters.contains("__month_name"));
		assertFalse(parameters.contains("__day_name"));
		assertTrue(parameters.contains("name"));
		assertEquals(1, parameters.keySet().size());
		Date date = new SimpleDateFormat("yyyy-MM-dd").parse("2000-06-15"); 
		assertEquals(date, parameters.get("name").getObject());
	}

}
