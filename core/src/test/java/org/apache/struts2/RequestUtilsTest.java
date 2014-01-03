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

package org.apache.struts2;

/**
 * <code>RequestUtilsTest</code>
 *
 */
import junit.framework.TestCase;
import org.easymock.MockControl;

import javax.servlet.http.HttpServletRequest;
import java.util.Date;

public class RequestUtilsTest extends TestCase {

    private MockControl control;
    private HttpServletRequest requestMock;

    public void testGetServletPathWithServletPathSet() throws Exception {
        control.expectAndReturn(requestMock.getServletPath(), "/mycontext/");
        control.expectAndReturn(requestMock.getRequestURI(), "/mycontext/");
        control.replay();
        assertEquals("/mycontext/", RequestUtils.getServletPath(requestMock));
        control.verify();
    }

    public void testGetServletPathWithRequestURIAndEmptyContextPath() throws Exception {
        control.expectAndReturn(requestMock.getServletPath(), null);
        control.expectAndReturn(requestMock.getRequestURI(), "/mycontext/test.jsp");
        control.expectAndReturn(requestMock.getContextPath(), "");
        control.expectAndReturn(requestMock.getPathInfo(), "test.jsp");
        control.expectAndReturn(requestMock.getPathInfo(), "test.jsp");
        control.replay();
        assertEquals("/mycontext/", RequestUtils.getServletPath(requestMock));
        control.verify();
    }

    public void testGetServletPathWithRequestURIAndContextPathSet() throws Exception {
        control.expectAndReturn(requestMock.getServletPath(), null);
        control.expectAndReturn(requestMock.getRequestURI(), "/servlet/mycontext/test.jsp");
        control.expectAndReturn(requestMock.getContextPath(), "/servlet");
        control.expectAndReturn(requestMock.getContextPath(), "/servlet");
        control.expectAndReturn(requestMock.getPathInfo(), "test.jsp");
        control.expectAndReturn(requestMock.getPathInfo(), "test.jsp");
        control.replay();
        assertEquals("/mycontext/", RequestUtils.getServletPath(requestMock));
        control.verify();
    }

    public void testGetServletPathWithRequestURIAndContextPathSetButNoPatchInfo() throws Exception {
        control.expectAndReturn(requestMock.getServletPath(), null);
        control.expectAndReturn(requestMock.getRequestURI(), "/servlet/mycontext/");
        control.expectAndReturn(requestMock.getContextPath(), "/servlet");
        control.expectAndReturn(requestMock.getContextPath(), "/servlet");
        control.expectAndReturn(requestMock.getPathInfo(), null);
        control.replay();
        assertEquals("/mycontext/", RequestUtils.getServletPath(requestMock));
        control.verify();
    }
    
    public void testGetServletPathWithSemicolon() throws Exception {
        control.expectAndReturn(requestMock.getRequestURI(), "/friend/mycontext/jim;bob");
        control.expectAndReturn(requestMock.getServletPath(), "/mycontext/jim");
        control.replay();
        assertEquals("/mycontext/jim;bob", RequestUtils.getServletPath(requestMock));
        control.verify();
    }

    public void testParseRFC1123() {
        Date date = RequestUtils.parseIfModifiedSince("Thu, 23 Jul 2013 19:42:23 GMT");
        assertNotNull(date);
    }

    public void testParseRFC1036() {
        Date date = RequestUtils.parseIfModifiedSince("Thursday, 23-Jul-13 19:42:23 GMT");
        assertNotNull(date);
    }

    public void testParseASC() {
        Date date = RequestUtils.parseIfModifiedSince("Thu Jul 23 19:42:23 2013");
        assertNotNull(date);
    }


    protected void setUp() {
        control = MockControl.createControl(HttpServletRequest.class);
        requestMock = (HttpServletRequest) control.getMock();
    }

}
