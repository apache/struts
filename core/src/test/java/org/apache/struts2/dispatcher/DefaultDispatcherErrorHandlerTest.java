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
package org.apache.struts2.dispatcher;

import java.io.IOException;
import java.util.Collections;
import org.apache.struts2.StrutsInternalTestCase;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import org.apache.struts2.views.freemarker.FreemarkerManager;
import static org.easymock.EasyMock.anyInt;
import static org.easymock.EasyMock.anyString;
import static org.easymock.EasyMock.createMock;
import static org.easymock.EasyMock.expect;
import static org.easymock.EasyMock.expectLastCall;
import static org.easymock.EasyMock.replay;

public class DefaultDispatcherErrorHandlerTest extends StrutsInternalTestCase {
    private HttpServletRequest requestMock;
    private HttpServletResponse responseMock;

    /**
     * Test to exercise the code path and prove handleError() will output 
     * the desired log warning when an IOException is thrown with devMode false.
     */
    public void testHandleErrorIOException() {
        DefaultDispatcherErrorHandler defaultDispatcherErrorHandler = new DefaultDispatcherErrorHandler();
        defaultDispatcherErrorHandler.setDevMode("false");
        defaultDispatcherErrorHandler.setFreemarkerManager(dispatcher.getContainer().getInstance(FreemarkerManager.class));
        defaultDispatcherErrorHandler.init(dispatcher.servletContext);
        Exception fakeException = new Exception("Fake Exception, devMode false");
        try {
            requestMock.setAttribute("javax.servlet.error.exception", fakeException);
            expectLastCall();
            requestMock.setAttribute("javax.servlet.jsp.jspException", fakeException);
            expectLastCall();
            responseMock.sendError(HttpServletResponse.SC_INTERNAL_SERVER_ERROR, fakeException.getMessage());
            expectLastCall().andStubThrow(new IOException("Fake IO Exception (SC_INTERNAL_SERVER_ERROR, devMode false)"));
            replay(responseMock);
        } catch (IOException ioe) {
            fail("Mock sendError call setup failed.  Ex: " + ioe);
        }
        defaultDispatcherErrorHandler.handleError(requestMock, responseMock, HttpServletResponse.SC_INTERNAL_SERVER_ERROR, fakeException);
    }

    /**
     * Test to exercise the code path and prove handleError() will output 
     * the desired log warning when an IOException is thrown with devMode true.
     */
    public void testHandleErrorIOExceptionDevMode() {
        DefaultDispatcherErrorHandler defaultDispatcherErrorHandler = new DefaultDispatcherErrorHandler();
        defaultDispatcherErrorHandler.setDevMode("true");
        defaultDispatcherErrorHandler.setFreemarkerManager(dispatcher.getContainer().getInstance(FreemarkerManager.class));
        defaultDispatcherErrorHandler.init(dispatcher.servletContext);
        Exception fakeException = new Exception("Fake Exception, devMode true");
        try {
            responseMock.setContentType("text/html");
            expectLastCall().andStubThrow(new IllegalStateException("Fake IllegalState Exception (report write)"));  // Fake error during report write
            responseMock.sendError(anyInt(), anyString());
            expectLastCall().andStubThrow(new IOException("Fake IO Exception (SC_INTERNAL_SERVER_ERROR, devMode true)"));
            replay(responseMock);
        } catch (IOException ioe) {
            fail("Mock sendError call setup failed.  Ex: " + ioe);
        }
        defaultDispatcherErrorHandler.handleError(requestMock, responseMock, HttpServletResponse.SC_INTERNAL_SERVER_ERROR, fakeException);
    }

    /**
     * Test to exercise the code path and prove handleError() will output 
     * the desired log warning when an IllegalStateException is thrown with devMode false.
     */
    public void testHandleErrorIllegalStateException() {
        DefaultDispatcherErrorHandler defaultDispatcherErrorHandler = new DefaultDispatcherErrorHandler();
        defaultDispatcherErrorHandler.setDevMode("false");
        defaultDispatcherErrorHandler.setFreemarkerManager(dispatcher.getContainer().getInstance(FreemarkerManager.class));
        defaultDispatcherErrorHandler.init(dispatcher.servletContext);
        Exception fakeException = new Exception("Fake Exception, devMode false");
        try {
            requestMock.setAttribute("javax.servlet.error.exception", fakeException);
            expectLastCall();
            requestMock.setAttribute("javax.servlet.jsp.jspException", fakeException);
            expectLastCall();
            expect(responseMock.isCommitted()).andStubReturn(Boolean.TRUE);
            responseMock.sendError(HttpServletResponse.SC_INTERNAL_SERVER_ERROR, fakeException.getMessage());
            expectLastCall().andStubThrow(new IllegalStateException("Fake IllegalState Exception (SC_INTERNAL_SERVER_ERROR, devMode false)"));
            replay(responseMock);
        } catch (IOException ioe) {
            fail("Mock sendError call setup failed.  Ex: " + ioe);
        }
        defaultDispatcherErrorHandler.handleError(requestMock, responseMock, HttpServletResponse.SC_INTERNAL_SERVER_ERROR, fakeException);
    }

    /**
     * Test to exercise the code path and prove handleError() will output 
     * the desired log warning when an IllegalStateException is thrown with devMode true.
     */
    public void testHandleErrorIllegalStateExceptionDevMode() {
        DefaultDispatcherErrorHandler defaultDispatcherErrorHandler = new DefaultDispatcherErrorHandler();
        defaultDispatcherErrorHandler.setDevMode("true");
        defaultDispatcherErrorHandler.setFreemarkerManager(dispatcher.getContainer().getInstance(FreemarkerManager.class));
        defaultDispatcherErrorHandler.init(dispatcher.servletContext);
        Exception fakeException = new Exception("Fake Exception, devMode true");
        try {
            expect(responseMock.isCommitted()).andStubReturn(Boolean.TRUE);
            responseMock.setContentType("text/html");
            expectLastCall().andStubThrow(new IllegalStateException("Fake IllegalState Exception (report write)"));  // Fake error during report write
            responseMock.sendError(anyInt(), anyString());
            expectLastCall().andStubThrow(new IllegalStateException("Fake IllegalState Exception (SC_INTERNAL_SERVER_ERROR, devMode true)"));
            replay(responseMock);
        } catch (IOException ioe) {
            fail("Mock sendError call setup failed.  Ex: " + ioe);
        }
        defaultDispatcherErrorHandler.handleError(requestMock, responseMock, HttpServletResponse.SC_INTERNAL_SERVER_ERROR, fakeException);
    }

    protected void setUp() {
        requestMock = (HttpServletRequest) createMock(HttpServletRequest.class);
        responseMock = (HttpServletResponse) createMock(HttpServletResponse.class);
        dispatcher = initDispatcher(Collections.<String, String>emptyMap());
    }
}
