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
package org.apache.struts2.json;

import org.apache.struts2.ActionContext;
import org.apache.struts2.config.entities.ActionConfig;
import org.apache.struts2.mock.MockActionInvocation;
import org.apache.struts2.mock.MockActionProxy;
import org.apache.struts2.util.ValueStack;
import org.apache.struts2.StrutsStatics;
import org.apache.struts2.dispatcher.mapper.DefaultActionMapper;
import org.apache.struts2.junit.StrutsTestCase;
import org.apache.struts2.url.QueryStringBuilder;
import org.apache.struts2.url.StrutsQueryStringBuilder;
import org.apache.struts2.url.StrutsUrlEncoder;
import org.springframework.mock.web.MockHttpServletRequest;
import org.springframework.mock.web.MockHttpServletResponse;
import org.springframework.mock.web.MockServletContext;

public class JSONActionRedirectResultTest extends StrutsTestCase {

    private DefaultActionMapper actionMapper;
    private QueryStringBuilder queryStringBuilder;

    MockActionInvocation invocation;
    MockHttpServletResponse response;
    MockServletContext servletContext;
    ActionContext context;
    ValueStack stack;
    MockHttpServletRequest request;

    public void testNormalRedirect() throws Exception {
        JSONActionRedirectResult result = new JSONActionRedirectResult();
        result.setActionName("targetAction");
        result.setActionMapper(actionMapper);
        result.setQueryStringBuilder(queryStringBuilder);

        Object action = new Object();
        stack.push(action);

        this.invocation.setAction(action);
        result.execute(this.invocation);

        String content = response.getContentAsString();
        assertEquals("", content);
        String location = response.getHeader("Location");
        assertEquals("/targetAction.action", location);
        assertEquals(302, response.getStatus());
    }

    public void testJsonRedirect() throws Exception {
        JSONActionRedirectResult result = new JSONActionRedirectResult();
        result.setActionName("targetAction");
        result.setActionMapper(actionMapper);
        result.setQueryStringBuilder(queryStringBuilder);

        request.setParameter("struts.enableJSONValidation", "true");
        request.setParameter("struts.validateOnly", "false");

        Object action = new Object();
        stack.push(action);

        this.invocation.setAction(action);
        result.execute(this.invocation);

        String content = response.getContentAsString();
        assertEquals("{\"location\": \"/targetAction.action\"}", content);
        assertEquals(200, response.getStatus());
    }

    public void testValidateOnlyFalse() throws Exception {
        JSONActionRedirectResult result = new JSONActionRedirectResult();
        result.setActionName("targetAction");
        result.setActionMapper(actionMapper);
        result.setQueryStringBuilder(queryStringBuilder);

        request.setParameter("struts.enableJSONValidation", "true");
        request.setParameter("struts.validateOnly", "true");

        Object action = new Object();
        stack.push(action);

        this.invocation.setAction(action);
        result.execute(this.invocation);

        String content = response.getContentAsString();
        assertEquals("", content);
        String location = response.getHeader("Location");
        assertEquals("/targetAction.action", location);
        assertEquals(302, response.getStatus());
    }

    @Override
    protected void setUp() throws Exception {
        super.setUp();
        this.response = new MockHttpServletResponse();
        this.request = new MockHttpServletRequest();
        this.context = ActionContext.getContext();
        this.context.put(StrutsStatics.HTTP_RESPONSE, this.response);
        this.context.put(StrutsStatics.HTTP_REQUEST, this.request);
        this.stack = context.getValueStack();
        this.servletContext = new MockServletContext();
        this.context.put(StrutsStatics.SERVLET_CONTEXT, this.servletContext);
        this.invocation = new MockActionInvocation();
        this.invocation.setInvocationContext(this.context);
        this.invocation.setStack(this.stack);
        MockActionProxy mockActionProxy = new MockActionProxy();
        mockActionProxy.setConfig(new ActionConfig.Builder(null, null, null).build());
        this.invocation.setProxy(mockActionProxy);

        this.actionMapper = new DefaultActionMapper();
        this.queryStringBuilder = new StrutsQueryStringBuilder(new StrutsUrlEncoder());
    }
}
