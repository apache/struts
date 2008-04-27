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

package org.apache.struts2.views.util;
/**
 * <code>ResourceUtilTest</code>
 *
 */
import javax.servlet.http.HttpServletRequest;

import junit.framework.TestCase;

import org.easymock.MockControl;

public class ResourceUtilTest extends TestCase {

    private MockControl control;
    private HttpServletRequest requestMock;

    public void testGetResourceBase() throws Exception {
        control.expectAndReturn(requestMock.getServletPath(), "/mycontext/");
        control.expectAndReturn(requestMock.getRequestURI(), "/mycontext/");
        control.replay();
        assertEquals("/mycontext", ResourceUtil.getResourceBase(requestMock));
        control.verify();

        control.reset();

        control.expectAndReturn(requestMock.getServletPath(), "/mycontext/test.jsp");
        control.expectAndReturn(requestMock.getRequestURI(), "/mycontext/test.jsp");
        control.replay();
        
        assertEquals("/mycontext", ResourceUtil.getResourceBase(requestMock));
        control.verify();

    }


    protected void setUp() {
        control = MockControl.createControl(HttpServletRequest.class);
        requestMock = (HttpServletRequest) control.getMock();
    }
}
