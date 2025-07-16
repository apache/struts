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
package org.apache.struts2.result;

import org.apache.struts2.ActionContext;
import org.apache.struts2.StrutsInternalTestCase;
import org.apache.struts2.StrutsStatics;
import org.apache.struts2.mock.MockActionInvocation;
import org.apache.struts2.util.ValueStack;
import org.apache.struts2.util.ValueStackFactory;
import org.springframework.mock.web.MockHttpServletRequest;
import org.springframework.mock.web.MockHttpServletResponse;

public class ServletDispatcherResultTest extends StrutsInternalTestCase implements StrutsStatics {

    private MockHttpServletRequest request;
    private MockHttpServletResponse response;
    private MockActionInvocation invocation;
    private ValueStack stack;

    public void testForward() throws Exception {
        ServletDispatcherResult view = new ServletDispatcherResult();
        view.setLocation("foo.jsp");

        request.setRequestURI("/app/namespace/my.action");
        request.setContextPath("/app");
        request.setServletPath("/namespace/my.action");
        request.setPathInfo(null);
        request.setQueryString("a=1&b=2");

        request.setAttribute("struts.actiontag.invocation", null);
        request.setAttribute("jakarta.servlet.include.servlet_path", null);

        response.setCommitted(Boolean.FALSE);

        view.execute(invocation);

        assertEquals("foo.jsp", response.getForwardedUrl());
    }

    public void testInclude() throws Exception {
        ServletDispatcherResult view = new ServletDispatcherResult();
        view.setLocation("foo.jsp");

        request.setRequestURI("/app/namespace/my.action");
        request.setContextPath("/app");
        request.setServletPath("/namespace/my.action");
        request.setPathInfo(null);
        request.setQueryString("a=1&b=2");

        request.setAttribute("struts.actiontag.invocation", null);
        response.setCommitted(Boolean.TRUE);

        view.execute(invocation);

        assertEquals("foo.jsp", response.getIncludedUrl());
    }

    public void testWithParameter() throws Exception {
        ServletDispatcherResult view = container.inject(ServletDispatcherResult.class);
        view.setLocation("foo.jsp?bar=1");

        view.execute(invocation);

        assertTrue(invocation.getInvocationContext().getParameters().contains("bar"));
        assertEquals("1", invocation.getInvocationContext().getParameters().get("bar").getValue());

        // See https://issues.apache.org/jira/browse/WW-5486
        assertEquals("1", stack.findString("#parameters.bar"));
    }

    @Override
    public void setUp() throws Exception {
        super.setUp();
        invocation = new MockActionInvocation();
        request = new MockHttpServletRequest();
        response = new MockHttpServletResponse();
        stack = container.getInstance(ValueStackFactory.class).createValueStack();
        invocation.setStack(stack);

        stack.getActionContext()
                .withServletRequest(request)
                .withServletResponse(response)
                .withActionInvocation(invocation)
                .withValueStack(stack)
                .bind();

        invocation.setInvocationContext(ActionContext.getContext());
    }

}
