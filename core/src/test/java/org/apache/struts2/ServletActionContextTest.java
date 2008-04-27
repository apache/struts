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

import java.util.HashMap;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import junit.framework.TestCase;

import com.mockobjects.servlet.MockHttpServletRequest;
import com.mockobjects.servlet.MockHttpServletResponse;
import com.mockobjects.servlet.MockServletContext;
import com.opensymphony.xwork2.ActionContext;


/**
 * Unit test for ServletActionContext. Based loosly on Jason's ActionContextTest.
 * My first attempt at unit testing. Please hack away as needed.
 *
 */
public class ServletActionContextTest extends TestCase implements StrutsStatics {

    ActionContext actionContext;
    ServletActionContext servletActionContext;
    private HttpServletRequest request;
    private HttpServletResponse response;
    private MockServletContext servletContext;


    public void setUp() {
        Map extraContext = new HashMap();

        request = new MockHttpServletRequest();
        response = new MockHttpServletResponse();
        servletContext = new MockServletContext();

        extraContext.put(HTTP_REQUEST, request);
        extraContext.put(HTTP_RESPONSE, response);
        extraContext.put(SERVLET_CONTEXT, servletContext);

        actionContext = new ActionContext(extraContext);
        ServletActionContext.setContext(actionContext);
    }

    public void testContextParams() {
        assertEquals(ServletActionContext.getRequest(), request);
        assertEquals(ServletActionContext.getResponse(), response);
        assertEquals(ServletActionContext.getServletContext(), servletContext);
    }

    public void testGetContext() {
        ActionContext threadContext = ServletActionContext.getContext();
        assertEquals(actionContext, threadContext);
    }
}
