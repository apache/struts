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
package org.apache.struts2.views.freemarker;

import java.io.PrintWriter;
import java.io.StringWriter;

import junit.framework.TestCase;

import org.apache.struts2.ServletActionContext;
import org.apache.struts2.StrutsStatics;
import org.apache.struts2.StrutsTestCase;
import org.apache.struts2.views.jsp.StrutsMockHttpServletResponse;
import org.apache.struts2.views.jsp.StrutsMockServletContext;
import org.springframework.mock.web.MockHttpServletRequest;

import com.opensymphony.xwork2.ActionContext;
import com.opensymphony.xwork2.mock.MockActionInvocation;
import com.opensymphony.xwork2.util.ValueStack;
import com.opensymphony.xwork2.util.ValueStackFactory;

/**
 * Test case for FreeMarkerResult.
 *
 */
public class FreeMarkerResultTest extends StrutsTestCase {

    ValueStack stack;
    MockActionInvocation invocation;
    ActionContext context;
    StrutsMockHttpServletResponse response;
    PrintWriter writer;
    StringWriter stringWriter;
    StrutsMockServletContext servletContext;
    private FreemarkerManager mgr;
    private MockHttpServletRequest request;

    public void testWriteIfCompleted() throws Exception {
        FreemarkerResult result = new FreemarkerResult();
        result.setLocation("someFreeMarkerFile.ftl");
        result.setFreemarkerManager(mgr);
        result.setWriteIfCompleted(true);

        try {
            result.execute(invocation);
            assertTrue(false);
        } catch (Exception e) {
            assertEquals(0, stringWriter.getBuffer().length());
        }
    }

    public void testWithoutWriteIfCompleted() throws Exception {
        FreemarkerResult result = new FreemarkerResult();
        result.setLocation("someFreeMarkerFile.ftl");
        result.setFreemarkerManager(mgr);

        try {
            result.execute(invocation);
            assertTrue(false);
        } catch (Exception e) {
            assertTrue(stringWriter.getBuffer().length() > 0);
        }
    }

    protected void setUp() throws Exception {
        super.setUp();
        mgr = new FreemarkerManager();
        mgr.setEncoding("UTF-8");
        stringWriter = new StringWriter();
        writer = new PrintWriter(stringWriter);
        response = new StrutsMockHttpServletResponse();
        response.setWriter(writer);
        request = new MockHttpServletRequest();
        servletContext = new StrutsMockServletContext();
        stack = ActionContext.getContext().getValueStack();
        context = new ActionContext(stack.getContext());
        context.put(StrutsStatics.HTTP_RESPONSE, response);
        context.put(StrutsStatics.HTTP_REQUEST, request);
        context.put(StrutsStatics.SERVLET_CONTEXT, servletContext);
        ServletActionContext.setServletContext(servletContext);
        ServletActionContext.setRequest(request);
        ServletActionContext.setResponse(response);
        servletContext.setAttribute(FreemarkerManager.CONFIG_SERVLET_CONTEXT_KEY, null);
        invocation = new MockActionInvocation();
        invocation.setStack(stack);
        invocation.setInvocationContext(context);
        servletContext.setRealPath(FreeMarkerResultTest.class.getResource(
            "someFreeMarkerFile.ftl").getFile());
    }

    protected void tearDown() throws Exception {
        stack = null;
        invocation = null;
        context = null;
        response = null;
        writer = null;
        stringWriter = null;
        servletContext = null;

        super.tearDown();
    }
}
