/*
 * Licensed to the Apache Software Foundation (ASF) under one
 * or more contributor license agreements.  See the NOTICE file
 * distributed with this work for additional information
 * regarding copyright ownership.  The ASF licenses this file
 * to you under the Apache License, Version 2.0 (the
 * "License"); you may not use this file except in compliance
 * with the License.  You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing,
 * software distributed under the License is distributed on an
 * "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY
 * KIND, either express or implied.  See the License for the
 * specific language governing permissions and limitations
 * under the License.
 */
package org.apache.tiles.request.portlet;

import org.apache.tiles.request.ApplicationContext;
import org.apache.tiles.request.collection.HeaderValuesMap;
import org.apache.tiles.request.collection.ReadOnlyEnumerationMap;
import org.apache.tiles.request.collection.ScopeMap;
import org.apache.tiles.request.portlet.delegate.RequestDelegate;
import org.apache.tiles.request.portlet.delegate.ResponseDelegate;
import org.apache.tiles.request.portlet.extractor.HeaderExtractor;
import org.junit.Before;
import org.junit.Test;

import javax.portlet.PortletContext;
import javax.portlet.PortletException;
import javax.portlet.PortletRequestDispatcher;
import javax.portlet.PortletResponse;
import javax.servlet.ServletOutputStream;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.Locale;
import java.util.Map;

import static org.easymock.EasyMock.createMock;
import static org.easymock.EasyMock.expect;
import static org.easymock.EasyMock.expectLastCall;
import static org.easymock.EasyMock.replay;
import static org.easymock.EasyMock.verify;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

/**
 * Tests {@link PortletRequest}.
 */
public class PortletRequestTest {

    /**
     * The application context.
     */
    private ApplicationContext applicationContext;

    /**
     * The portlet context.
     */
    private PortletContext portletContext;

    /**
     * The request.
     */
    private javax.portlet.PortletRequest request;

    /**
     * The response.
     */
    private PortletResponse response;

    /**
     * The request to test.
     */
    private PortletRequest req;

    /**
     * The request delegate.
     */
    private RequestDelegate requestDelegate;

    /**
     * The response delegate.
     */
    private ResponseDelegate responseDelegate;

    /**
     * Sets up the test.
     */
    @Before
    public void setUp() {
        applicationContext = createMock(ApplicationContext.class);
        portletContext = createMock(PortletContext.class);
        request = createMock(javax.portlet.PortletRequest.class);
        response = createMock(PortletResponse.class);
        requestDelegate = createMock(RequestDelegate.class);
        responseDelegate = createMock(ResponseDelegate.class);
        req = new PortletRequest(applicationContext, portletContext, request,
            response, requestDelegate, responseDelegate);
    }

    /**
     * Test method for {@link PortletRequest#doForward(String)}.
     *
     * @throws IOException      If something goes wrong.
     * @throws PortletException If something goes wrong.
     */
    @Test
    public void testDoForward() throws PortletException, IOException {
        PortletRequestDispatcher rd = createMock(PortletRequestDispatcher.class);

        expect(responseDelegate.isResponseCommitted()).andReturn(false);
        expect(portletContext.getRequestDispatcher("/my/path")).andReturn(rd);
        rd.forward(request, response);

        replay(applicationContext, portletContext, request, response, rd);
        req.doForward("/my/path");
        verify(applicationContext, portletContext, request, response, rd);
    }

    /**
     * Test method for {@link PortletRequest#doForward(String)}.
     *
     * @throws IOException If something goes wrong.
     */
    @Test(expected = IOException.class)
    public void testDoForwardNoDispatcher() throws IOException {
        expect(responseDelegate.isResponseCommitted()).andReturn(false);
        expect(portletContext.getRequestDispatcher("/my/path")).andReturn(null);

        replay(applicationContext, request, response, portletContext, requestDelegate, responseDelegate);
        try {
            req.doForward("/my/path");
        } finally {
            verify(applicationContext, request, response, portletContext, requestDelegate, responseDelegate);
        }
    }

    /**
     * Test method for {@link PortletRequest#doForward(String)}.
     *
     * @throws IOException      If something goes wrong.
     * @throws PortletException If something goes wrong.
     */
    @Test(expected = IOException.class)
    public void testDoForwardPortletException() throws PortletException, IOException {
        PortletRequestDispatcher rd = createMock(PortletRequestDispatcher.class);

        expect(responseDelegate.isResponseCommitted()).andReturn(false);
        expect(portletContext.getRequestDispatcher("/my/path")).andReturn(rd);
        rd.forward(request, response);
        expectLastCall().andThrow(new PortletException());

        replay(applicationContext, request, response, rd, portletContext, requestDelegate, responseDelegate);
        try {
            req.doForward("/my/path");
        } finally {
            verify(applicationContext, request, response, rd, portletContext, requestDelegate, responseDelegate);
        }
    }

    /**
     * Test method for {@link PortletRequest#doForward(String)}.
     *
     * @throws IOException      If something goes wrong.
     * @throws PortletException If something goes wrong.
     */
    @Test
    public void testDoForwardInclude() throws PortletException, IOException {
        PortletRequestDispatcher rd = createMock(PortletRequestDispatcher.class);

        expect(responseDelegate.isResponseCommitted()).andReturn(true);
        expect(portletContext.getRequestDispatcher("/my/path")).andReturn(rd);
        rd.include(request, response);

        replay(applicationContext, request, response, rd, portletContext, requestDelegate, responseDelegate);
        req.doForward("/my/path");
        verify(applicationContext, request, response, rd, portletContext, requestDelegate, responseDelegate);
    }

    /**
     * Test method for {@link PortletRequest#doInclude(String)}.
     *
     * @throws IOException      If something goes wrong.
     * @throws PortletException If something goes wrong.
     */
    @Test
    public void testDoInclude() throws IOException, PortletException {
        PortletRequestDispatcher rd = createMock(PortletRequestDispatcher.class);

        expect(portletContext.getRequestDispatcher("/my/path")).andReturn(rd);
        rd.include(request, response);

        replay(applicationContext, request, response, rd, portletContext, requestDelegate, responseDelegate);
        req.doInclude("/my/path");
        verify(applicationContext, request, response, rd, portletContext, requestDelegate, responseDelegate);
    }

    /**
     * Test method for {@link PortletRequest#doInclude(String)}.
     *
     * @throws IOException If something goes wrong.
     */
    @Test(expected = IOException.class)
    public void testDoIncludeNoDispatcher() throws IOException {
        expect(portletContext.getRequestDispatcher("/my/path")).andReturn(null);

        replay(applicationContext, request, response, portletContext, requestDelegate, responseDelegate);
        try {
            req.doInclude("/my/path");
        } finally {
            verify(applicationContext, request, response, portletContext, requestDelegate, responseDelegate);
        }
    }

    /**
     * Test method for {@link PortletRequest#doInclude(String)}.
     *
     * @throws IOException      If something goes wrong.
     * @throws PortletException If something goes wrong.
     */
    @Test(expected = IOException.class)
    public void testDoIncludePortletException() throws IOException, PortletException {
        PortletRequestDispatcher rd = createMock(PortletRequestDispatcher.class);

        expect(portletContext.getRequestDispatcher("/my/path")).andReturn(rd);
        rd.include(request, response);
        expectLastCall().andThrow(new PortletException());

        replay(applicationContext, request, response, rd, portletContext, requestDelegate, responseDelegate);
        try {
            req.doInclude("/my/path");
        } finally {
            verify(applicationContext, request, response, rd, portletContext, requestDelegate, responseDelegate);
        }
    }

    /**
     * Test method for {@link PortletRequest#getHeader()}.
     */
    @Test
    public void testGetHeader() {
        assertTrue(req.getHeader() instanceof ReadOnlyEnumerationMap);
    }

    /**
     * Test method for {@link PortletRequest#getResponseHeaders()}.
     */
    @Test
    public void testGetResponseHeaders() {
        assertTrue(req.getResponseHeaders() instanceof HeaderExtractor);
    }

    /**
     * Test method for {@link PortletRequest#getHeaderValues()}.
     */
    @Test
    public void testGetHeaderValues() {
        assertTrue(req.getHeaderValues() instanceof HeaderValuesMap);
    }

    /**
     * Test method for {@link PortletRequest#getParam()}.
     */
    @Test
    public void testGetParam() {
        Map<String, String> map = createMock(Map.class);

        expect(requestDelegate.getParam()).andReturn(map);

        replay(applicationContext, request, response, portletContext, requestDelegate, responseDelegate);
        assertEquals(map, req.getParam());
        verify(applicationContext, request, response, portletContext, requestDelegate, responseDelegate);
    }

    /**
     * Test method for {@link PortletRequest#getParamValues()}.
     */
    @Test
    public void testGetParamValues() {
        Map<String, String[]> paramMap = createMock(Map.class);

        expect(requestDelegate.getParamValues()).andReturn(paramMap);

        replay(applicationContext, request, response, paramMap, portletContext, requestDelegate, responseDelegate);
        assertEquals(paramMap, req.getParamValues());
        verify(applicationContext, request, response, paramMap, portletContext, requestDelegate, responseDelegate);
    }

    /**
     * Test method for {@link PortletRequest#getRequestScope()}.
     */
    @Test
    public void testGetRequestScope() {
        assertTrue(req.getRequestScope() instanceof ScopeMap);
    }

    /**
     * Test method for {@link PortletRequest#getSessionScope()}.
     */
    @Test
    public void testGetSessionScope() {
        assertTrue(req.getSessionScope() instanceof ScopeMap);
    }

    /**
     * Test method for {@link PortletRequest#getPortletSessionScope()}.
     */
    @Test
    public void testGetPortletSessionScope() {
        assertTrue(req.getPortletSessionScope() instanceof ScopeMap);
    }

    /**
     * Test method for {@link PortletRequest#getOutputStream()}.
     *
     * @throws IOException If something goes wrong.
     */
    @Test
    public void testGetOutputStream() throws IOException {
        ServletOutputStream os = createMock(ServletOutputStream.class);

        expect(responseDelegate.getOutputStream()).andReturn(os);

        replay(applicationContext, request, response, os, portletContext, requestDelegate, responseDelegate);
        assertEquals(req.getOutputStream(), os);
        verify(applicationContext, request, response, os, portletContext, requestDelegate, responseDelegate);
    }

    /**
     * Test method for {@link PortletRequest#getWriter()}.
     *
     * @throws IOException If something goes wrong.
     */
    @Test
    public void testGetWriter() throws IOException {
        PrintWriter os = createMock(PrintWriter.class);

        expect(responseDelegate.getWriter()).andReturn(os);

        replay(applicationContext, request, response, os, portletContext, requestDelegate, responseDelegate);
        assertEquals(req.getWriter(), os);
        verify(applicationContext, request, response, os, portletContext, requestDelegate, responseDelegate);
    }

    /**
     * Test method for {@link PortletRequest#getPrintWriter()}.
     *
     * @throws IOException If something goes wrong.
     */
    @Test
    public void testGetPrintWriter() throws IOException {
        PrintWriter os = createMock(PrintWriter.class);

        expect(responseDelegate.getPrintWriter()).andReturn(os);

        replay(applicationContext, request, response, os, portletContext, requestDelegate, responseDelegate);
        assertEquals(req.getPrintWriter(), os);
        verify(applicationContext, request, response, os, portletContext, requestDelegate, responseDelegate);
    }

    /**
     * Test method for {@link PortletRequest#isResponseCommitted()}.
     */
    @Test
    public void testIsResponseCommitted() {
        expect(responseDelegate.isResponseCommitted()).andReturn(true);

        replay(applicationContext, request, response, portletContext, requestDelegate, responseDelegate);
        assertTrue(req.isResponseCommitted());
        verify(applicationContext, request, response, portletContext, requestDelegate, responseDelegate);
    }

    /**
     * Test method for {@link PortletRequest#setContentType(String)}.
     */
    @Test
    public void testSetContentType() {
        responseDelegate.setContentType("text/html");

        replay(applicationContext, request, response, portletContext, requestDelegate, responseDelegate);
        req.setContentType("text/html");
        verify(applicationContext, request, response, portletContext, requestDelegate, responseDelegate);
    }

    /**
     * Test method for {@link PortletRequest#getRequestLocale()}.
     */
    @Test
    public void testGetRequestLocale() {
        Locale locale = Locale.ITALY;

        expect(request.getLocale()).andReturn(locale);

        replay(applicationContext, request, response, portletContext, requestDelegate, responseDelegate);
        assertEquals(locale, req.getRequestLocale());
        verify(applicationContext, request, response, portletContext, requestDelegate, responseDelegate);
    }

    /**
     * Test method for {@link PortletRequest#getRequest()}.
     */
    @Test
    public void testGetRequest() {
        replay(applicationContext, request, response, portletContext, requestDelegate, responseDelegate);
        assertEquals(request, req.getRequest());
        verify(applicationContext, request, response, portletContext, requestDelegate, responseDelegate);
    }

    /**
     * Test method for {@link PortletRequest#isUserInRole(String)}.
     */
    @Test
    public void testIsUserInRole() {
        expect(request.isUserInRole("myrole")).andReturn(true);

        replay(applicationContext, request, response, portletContext, requestDelegate, responseDelegate);
        assertTrue(req.isUserInRole("myrole"));
        verify(applicationContext, request, response, portletContext, requestDelegate, responseDelegate);
    }

}
