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
package org.apache.struts2;

/**
 * <code>RequestUtilsTest</code>
 *
 */
import junit.framework.TestCase;
import static org.easymock.EasyMock.*;

import javax.servlet.http.HttpServletRequest;
import java.util.Date;

public class RequestUtilsTest extends TestCase {

    private HttpServletRequest requestMock;

    public void testGetServletPathWithServletPathSet() throws Exception {
        expect(requestMock.getServletPath()).andStubReturn("/mycontext/");
        expect(requestMock.getRequestURI()).andStubReturn("/mycontext/");
        replay(requestMock);
        assertEquals("/mycontext/", RequestUtils.getServletPath(requestMock));
        verify(requestMock);
    }

    public void testGetServletPathWithRequestURIAndEmptyContextPath() throws Exception {
        expect(requestMock.getServletPath()).andStubReturn(null);
        expect(requestMock.getRequestURI()).andStubReturn("/mycontext/test.jsp");
        expect(requestMock.getContextPath()).andStubReturn("");
        expect(requestMock.getPathInfo()).andStubReturn("test.jsp");
        expect(requestMock.getPathInfo()).andStubReturn("test.jsp");
        replay(requestMock);
        assertEquals("/mycontext/", RequestUtils.getServletPath(requestMock));
        verify(requestMock);
    }

    public void testGetServletPathWithRequestURIAndContextPathSet() throws Exception {
        expect(requestMock.getServletPath()).andStubReturn(null);
        expect(requestMock.getRequestURI()).andStubReturn("/servlet/mycontext/test.jsp");
        expect(requestMock.getContextPath()).andStubReturn("/servlet");
        expect(requestMock.getContextPath()).andStubReturn("/servlet");
        expect(requestMock.getPathInfo()).andStubReturn("test.jsp");
        expect(requestMock.getPathInfo()).andStubReturn("test.jsp");
        replay(requestMock);
        assertEquals("/mycontext/", RequestUtils.getServletPath(requestMock));
        verify(requestMock);
    }

    public void testGetServletPathWithRequestURIAndContextPathSetButNoPatchInfo() throws Exception {
        expect(requestMock.getServletPath()).andStubReturn(null);
        expect(requestMock.getRequestURI()).andStubReturn("/servlet/mycontext/");
        expect(requestMock.getContextPath()).andStubReturn("/servlet");
        expect(requestMock.getContextPath()).andStubReturn("/servlet");
        expect(requestMock.getPathInfo()).andStubReturn(null);
        replay(requestMock);
        assertEquals("/mycontext/", RequestUtils.getServletPath(requestMock));
        verify(requestMock);
    }
    
    public void testGetServletPathWithSemicolon() throws Exception {
        expect(requestMock.getRequestURI()).andStubReturn("/friend/mycontext/jim;bob");
        expect(requestMock.getServletPath()).andStubReturn("/mycontext/jim");
        replay(requestMock);
        assertEquals("/mycontext/jim;bob", RequestUtils.getServletPath(requestMock));
        verify(requestMock);
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
        requestMock = (HttpServletRequest) createMock(HttpServletRequest.class);
    }

}
