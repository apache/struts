/*
 * $Id: DefaultActionSupport.java 651946 2008-04-27 13:41:38Z apetrelli $
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
package org.apache.struts2.dispatcher.ng;

import com.opensymphony.xwork2.ActionContext;
import junit.framework.TestCase;
import org.apache.struts2.dispatcher.Dispatcher;
import org.apache.struts2.dispatcher.ng.filter.StrutsPrepareAndExecuteFilter;
import org.springframework.mock.web.MockFilterChain;
import org.springframework.mock.web.MockFilterConfig;
import org.springframework.mock.web.MockHttpServletRequest;
import org.springframework.mock.web.MockHttpServletResponse;

import javax.servlet.ServletException;
import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;
import javax.servlet.FilterConfig;
import java.io.IOException;
import java.util.regex.Pattern;
import java.util.ArrayList;

/**
 * Integration tests for the filter
 */
public class StrutsPrepareAndExecuteFilterIntegrationTest extends TestCase {

    public void test404() throws ServletException, IOException {
        MockHttpServletRequest request = new MockHttpServletRequest();
        MockHttpServletResponse response = new MockHttpServletResponse();
        MockFilterConfig filterConfig = new MockFilterConfig();
        MockFilterChain filterChain = new MockFilterChain() {
            @Override
            public void doFilter(ServletRequest req, ServletResponse res) {
                fail("Shouldn't get here");
            }
        };

        request.setRequestURI("/foo.action");
        StrutsPrepareAndExecuteFilter filter = new StrutsPrepareAndExecuteFilter();
        filter.init(filterConfig);
        filter.doFilter(request, response, filterChain);
        assertEquals(404, response.getStatus());
        assertNull(ActionContext.getContext());
        assertNull(Dispatcher.getInstance());
    }

    public void test200() throws ServletException, IOException {
        MockHttpServletRequest request = new MockHttpServletRequest();
        MockHttpServletResponse response = new MockHttpServletResponse();
        MockFilterConfig filterConfig = new MockFilterConfig();
        MockFilterChain filterChain = new MockFilterChain() {
            @Override
            public void doFilter(ServletRequest req, ServletResponse res) {
                fail("Shouldn't get here");
            }
        };

        request.setRequestURI("/hello.action");
        StrutsPrepareAndExecuteFilter filter = new StrutsPrepareAndExecuteFilter();
        filter.init(filterConfig);
        filter.doFilter(request, response, filterChain);
        assertEquals(200, response.getStatus());
        assertNull(ActionContext.getContext());
        assertNull(Dispatcher.getInstance());
    }

    public void testActionMappingLookup() throws ServletException, IOException {
        MockHttpServletRequest request = new MockHttpServletRequest();
        MockHttpServletResponse response = new MockHttpServletResponse();
        MockFilterConfig filterConfig = new MockFilterConfig();
        MockFilterChain filterChain = new MockFilterChain() {
            @Override
            public void doFilter(ServletRequest req, ServletResponse res) {
                fail("Shouldn't get here");
            }
        };

        request.setRequestURI("/hello.action");
        StrutsPrepareAndExecuteFilter filter = new StrutsPrepareAndExecuteFilter();
        filter.init(filterConfig);
        filter.doFilter(request, response, filterChain);
        assertEquals(200, response.getStatus());
        assertNull(ActionContext.getContext());
        assertNull(Dispatcher.getInstance());

        //simulate a FORWARD
        MockFilterChain filterChain2 = new MockFilterChain() {
            @Override
            public void doFilter(ServletRequest req, ServletResponse res) {
                req.setAttribute("__invoked", true);
            }
        };
        request.setRequestURI("hello.jsp");
        filter.doFilter(request, response, filterChain2);
        assertEquals(200, response.getStatus());
        assertNull(ActionContext.getContext());
        assertNull(Dispatcher.getInstance());
        assertTrue((Boolean) request.getAttribute("__invoked"));
    }

    public void testUriPatternExclusion() throws ServletException, IOException {
        MockHttpServletRequest request = new MockHttpServletRequest();
        MockHttpServletResponse response = new MockHttpServletResponse();
        MockFilterConfig filterConfig = new MockFilterConfig();
        MockFilterChain filterChain = new MockFilterChain() {
            @Override
            public void doFilter(ServletRequest req, ServletResponse res) {
                req.setAttribute("i_was", "invoked");
            }
        };

        request.setRequestURI("/hello.action");
        StrutsPrepareAndExecuteFilter filter = new StrutsPrepareAndExecuteFilter() {
            @Override
            public void init( FilterConfig filterConfig ) throws ServletException {
                super.init(filterConfig);
                excludedPatterns = new ArrayList<Pattern>();
                excludedPatterns.add(Pattern.compile(".*hello.*"));
            }
        };
        filter.init(filterConfig);
        filter.doFilter(request, response, filterChain);
        assertEquals(200, response.getStatus());
        assertEquals("invoked", request.getAttribute("i_was"));
    }

    public void testStaticFallthrough() throws ServletException, IOException {
        MockHttpServletRequest request = new MockHttpServletRequest();
        MockHttpServletResponse response = new MockHttpServletResponse();
        MockFilterConfig filterConfig = new MockFilterConfig();
        MockFilterChain filterChain = new MockFilterChain() {
            @Override
            public void doFilter(ServletRequest req, ServletResponse res) {
                assertNotNull(ActionContext.getContext());
                assertNotNull(Dispatcher.getInstance());
                try {
                    res.getWriter().write("found");
                } catch (IOException e) {
                    fail(e.getMessage());
                }
            }
        };

        request.setRequestURI("/foo.txt");
        StrutsPrepareAndExecuteFilter filter = new StrutsPrepareAndExecuteFilter();
        filter.init(filterConfig);
        filter.doFilter(request, response, filterChain);
        assertEquals(200, response.getStatus());
        assertEquals("found", response.getContentAsString());
        assertNull(ActionContext.getContext());
        assertNull(Dispatcher.getInstance());
    }

    public void testStaticExecute() throws ServletException, IOException {
        MockHttpServletRequest request = new MockHttpServletRequest();
        MockHttpServletResponse response = new MockHttpServletResponse();
        MockFilterConfig filterConfig = new MockFilterConfig();
        MockFilterChain filterChain = new MockFilterChain() {
            @Override
            public void doFilter(ServletRequest req, ServletResponse res) {
                fail("Should never get here");
            }
        };

        request.setRequestURI("/struts/utils.js");
        StrutsPrepareAndExecuteFilter filter = new StrutsPrepareAndExecuteFilter();
        filter.init(filterConfig);
        filter.doFilter(request, response, filterChain);
        assertEquals(200, response.getStatus());
        assertTrue(response.getContentAsString().contains("StrutsUtils"));
        assertNull(ActionContext.getContext());
        assertNull(Dispatcher.getInstance());
    }
}
