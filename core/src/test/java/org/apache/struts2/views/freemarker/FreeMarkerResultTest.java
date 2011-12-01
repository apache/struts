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

import java.io.File;
import java.io.PrintWriter;
import java.io.StringWriter;

import org.apache.struts2.ServletActionContext;
import org.apache.struts2.StrutsStatics;
import org.apache.struts2.StrutsTestCase;
import org.apache.struts2.dispatcher.Dispatcher;
import org.apache.struts2.dispatcher.mapper.ActionMapper;
import org.apache.struts2.dispatcher.mapper.ActionMapping;
import org.apache.struts2.views.jsp.StrutsMockHttpServletResponse;
import org.apache.struts2.views.jsp.StrutsMockServletContext;
import org.springframework.mock.web.MockHttpServletRequest;
import org.easymock.EasyMock;

import org.apache.struts2.xwork2.ActionContext;
import org.apache.struts2.xwork2.mock.MockActionInvocation;
import org.apache.struts2.xwork2.util.ValueStack;

import javax.servlet.ServletContext;

import freemarker.template.TemplateExceptionHandler;
import freemarker.template.Configuration;

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

    public void testActionThatThrowsExceptionTag() throws Exception {
        //get fm config to use it in mock servlet context
        FreemarkerManager freemarkerManager = container.getInstance(FreemarkerManager.class);
        Configuration freemarkerConfig = freemarkerManager.getConfiguration(ServletActionContext.getServletContext());
        freemarkerConfig.setTemplateExceptionHandler(TemplateExceptionHandler.RETHROW_HANDLER);

        ServletContext servletContext = EasyMock.createNiceMock(ServletContext.class);
        File file = new File(FreeMarkerResultTest.class.getResource("callActionFreeMarker2.ftl").toURI());
        EasyMock.expect(servletContext.getRealPath("/tutorial/org/apache/struts2/views/freemarker/callActionFreeMarker.ftl")).andReturn(file.getAbsolutePath());
        file = new File(FreeMarkerResultTest.class.getResource("nested.ftl").toURI());
        EasyMock.expect(servletContext.getRealPath("/tutorial/org/apache/struts2/views/freemarker/nested.ftl")).andReturn(file.getAbsolutePath());
        EasyMock.expect(servletContext.getAttribute(FreemarkerManager.CONFIG_SERVLET_CONTEXT_KEY)).andReturn(freemarkerConfig).anyTimes();
        EasyMock.replay(servletContext);

        freemarkerConfig.setServletContextForTemplateLoading(servletContext, null);
        ServletActionContext.setServletContext(servletContext);


        request.setRequestURI("/tutorial/test2.action");
        Dispatcher dispatcher = Dispatcher.getInstance();
        ActionMapping mapping = dispatcher.getContainer().getInstance(ActionMapper.class).getMapping(
                    request, dispatcher.getConfigurationManager());
        dispatcher.serviceAction(request, response, servletContext, mapping);
        assertEquals("beforenestedafter", stringWriter.toString());
    }

     public void testActionThatSucceedsTag() throws Exception {
        //get fm config to use it in mock servlet context
        FreemarkerManager freemarkerManager = container.getInstance(FreemarkerManager.class);
        Configuration freemarkerConfig = freemarkerManager.getConfiguration(ServletActionContext.getServletContext());
        freemarkerConfig.setTemplateExceptionHandler(TemplateExceptionHandler.RETHROW_HANDLER);

        ServletContext servletContext = EasyMock.createNiceMock(ServletContext.class);
        File file = new File(FreeMarkerResultTest.class.getResource("callActionFreeMarker2.ftl").toURI());
        EasyMock.expect(servletContext.getRealPath("/tutorial/org/apache/struts2/views/freemarker/callActionFreeMarker2.ftl")).andReturn(file.getAbsolutePath());
        file = new File(FreeMarkerResultTest.class.getResource("nested.ftl").toURI());
        EasyMock.expect(servletContext.getRealPath("/tutorial/org/apache/struts2/views/freemarker/nested.ftl")).andReturn(file.getAbsolutePath());
        EasyMock.expect(servletContext.getAttribute(FreemarkerManager.CONFIG_SERVLET_CONTEXT_KEY)).andReturn(freemarkerConfig).anyTimes();
        EasyMock.replay(servletContext);

        freemarkerConfig.setServletContextForTemplateLoading(servletContext, null); 
        ServletActionContext.setServletContext(servletContext); 


        request.setRequestURI("/tutorial/test5.action");
        Dispatcher dispatcher = Dispatcher.getInstance();
        ActionMapping mapping = dispatcher.getContainer().getInstance(ActionMapper.class).getMapping(
                    request, dispatcher.getConfigurationManager());
        dispatcher.serviceAction(request, response, servletContext, mapping);
        assertEquals("beforenestedafter", stringWriter.toString());
    }

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
