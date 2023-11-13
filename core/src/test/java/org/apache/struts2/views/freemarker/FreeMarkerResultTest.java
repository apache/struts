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
package org.apache.struts2.views.freemarker;

import com.opensymphony.xwork2.ActionContext;
import com.opensymphony.xwork2.mock.MockActionInvocation;
import com.opensymphony.xwork2.mock.MockActionProxy;
import com.opensymphony.xwork2.util.ValueStack;
import com.opensymphony.xwork2.util.fs.DefaultFileManagerFactory;
import org.apache.struts2.StrutsInternalTestCase;
import org.apache.struts2.views.jsp.StrutsMockHttpServletResponse;
import org.apache.struts2.views.jsp.StrutsMockServletContext;
import org.springframework.mock.web.MockHttpServletRequest;

import java.io.File;
import java.io.PrintWriter;
import java.io.StringWriter;

/**
 * Test case for FreeMarkerResult.
 */
public class FreeMarkerResultTest extends StrutsInternalTestCase {

    ValueStack stack;
    MockActionInvocation invocation;
    ActionContext context;
    StrutsMockHttpServletResponse response;
    PrintWriter writer;
    StringWriter stringWriter;
    StrutsMockServletContext servletContext;
    private FreemarkerManager mgr;
    private MockHttpServletRequest request;

    public void testWriteIfCompleted() {
        FreemarkerResult result = new FreemarkerResult();
        result.setLocation("someFreeMarkerFile.ftl");
        result.setFreemarkerManager(mgr);
        result.setWriteIfCompleted(true);

        try {
            result.execute(invocation);
            fail();
        } catch (Exception e) {
            assertEquals(0, stringWriter.getBuffer().length());
        }
    }

    public void testWithoutWriteIfCompleted() {
        FreemarkerResult result = new FreemarkerResult();
        result.setLocation("someFreeMarkerFile.ftl");
        result.setFreemarkerManager(mgr);

        try {
            result.execute(invocation);
            fail();
        } catch (Exception e) {
            assertTrue(stringWriter.getBuffer().length() > 0);
        }
    }

    public void testContentTypeIsNotOverwritten() throws Exception {
        servletContext.setRealPath(new File(FreeMarkerResultTest.class.getResource(
            "nested.ftl").toURI()).toURL().getFile());

        FreemarkerResult result = new FreemarkerResult();
        result.setLocation("nested.ftl");
        result.setFreemarkerManager(mgr);

        response.setContentType("contenttype");
        result.execute(invocation);
        assertEquals("contenttype", response.getContentType());
    }

    public void testDefaultContentType() throws Exception {
        servletContext.setRealPath(new File(FreeMarkerResultTest.class.getResource(
            "nested.ftl").toURI()).toURL().getFile());

        FreemarkerResult result = new FreemarkerResult();
        result.setLocation("nested.ftl");
        result.setFreemarkerManager(mgr);

        assertNull(response.getContentType());
        result.execute(invocation);
        assertEquals("text/html; charset=UTF-8", response.getContentType());
    }

    public void testContentTypeFromTemplate() throws Exception {
        servletContext.setRealPath(new File(FreeMarkerResultTest.class.getResource(
            "something.ftl").toURI()).toURL().getFile());

        FreemarkerResult result = new FreemarkerResult();
        result.setLocation("something.ftl");
        result.setFreemarkerManager(mgr);

        assertNull(response.getContentType());
        result.execute(invocation);
        assertEquals("text/xml", response.getContentType());
    }

    protected void setUp() throws Exception {
        super.setUp();
        mgr = new FreemarkerManager();
        mgr.setEncoding("UTF-8");

        DefaultFileManagerFactory factory = new DefaultFileManagerFactory();
        container.inject(factory);
        mgr.setFileManagerFactory(factory);

        FreemarkerThemeTemplateLoader themeLoader = new FreemarkerThemeTemplateLoader();
        container.inject(themeLoader);
        mgr.setThemeTemplateLoader(themeLoader);

        stringWriter = new StringWriter();
        writer = new PrintWriter(stringWriter);
        response = new StrutsMockHttpServletResponse();
        response.setWriter(writer);
        request = new MockHttpServletRequest();
        servletContext = new StrutsMockServletContext();
        stack = ActionContext.getContext().getValueStack();

        context = ActionContext.of(stack.getContext())
            .withServletResponse(response)
            .withServletRequest(request)
            .withServletContext(servletContext)
            .bind();

        servletContext.setAttribute(FreemarkerManager.CONFIG_SERVLET_CONTEXT_KEY, null);

        invocation = new MockActionInvocation();
        invocation.setStack(stack);
        invocation.setInvocationContext(context);
        invocation.setProxy(new MockActionProxy());
        servletContext.setRealPath(new File(FreeMarkerResultTest.class.getResource(
            "someFreeMarkerFile.ftl").toURI()).toURL().getFile());
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
