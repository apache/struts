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
import org.apache.struts2.dispatcher.ng.filter.StrutsExecuteFilter;
import org.apache.struts2.dispatcher.ng.filter.StrutsPrepareFilter;
import org.springframework.mock.web.*;

import javax.servlet.*;
import java.io.IOException;
import java.util.LinkedList;
import java.util.Arrays;

/**
 * Integration tests for the filter
 */
public class TwoFilterIntegrationTest extends TestCase {
    StrutsExecuteFilter filterExecute;
    StrutsPrepareFilter filterPrepare;
    Filter failFilter;
    private Filter stringFilter;

    public void setUp() {
        filterPrepare = new StrutsPrepareFilter();
        filterExecute = new StrutsExecuteFilter();
        failFilter = new Filter() {
            public void init(FilterConfig filterConfig) throws ServletException {}
            public void doFilter(ServletRequest request, ServletResponse response, FilterChain chain) throws IOException, ServletException {
                fail("Should never get here");
            }
            public void destroy() {}
        };
        stringFilter = new Filter() {
            public void init(FilterConfig filterConfig) throws ServletException {}
            public void doFilter(ServletRequest request, ServletResponse response, FilterChain chain) throws IOException, ServletException {
                response.getWriter().write("content");
                assertNotNull(ActionContext.getContext());
                assertNotNull(Dispatcher.getInstance());
            }
            public void destroy() {}
        };
    }

    public void test404() throws ServletException, IOException {
        MockHttpServletResponse response = run("/foo.action", filterPrepare, filterExecute, failFilter);
        assertEquals(404, response.getStatus());
    }

    public void test200() throws ServletException, IOException {
        MockHttpServletResponse response = run("/hello.action", filterPrepare, filterExecute, failFilter);
        assertEquals(200, response.getStatus());
    }

    public void testStaticFallthrough() throws ServletException, IOException {
        MockHttpServletResponse response = run("/foo.txt", filterPrepare, filterExecute, stringFilter);
        assertEquals(200, response.getStatus());
        assertEquals("content", response.getContentAsString());

    }

    public void testStaticExecute() throws ServletException, IOException {
        MockHttpServletResponse response = run("/struts/utils.js", filterPrepare, filterExecute, failFilter);
        assertEquals(200, response.getStatus());
        assertTrue(response.getContentAsString().contains("StrutsUtils"));
    }

    public void testFilterInMiddle() throws ServletException, IOException {
        Filter middle = new Filter() {
            public void init(FilterConfig filterConfig) throws ServletException {}
            public void doFilter(ServletRequest request, ServletResponse response, FilterChain chain) throws IOException, ServletException {
                assertNotNull(ActionContext.getContext());
                assertNotNull(Dispatcher.getInstance());
                assertNull(ActionContext.getContext().getActionInvocation());
                chain.doFilter(request, response);
                assertEquals("hello", ActionContext.getContext().getActionInvocation().getProxy().getActionName());
            }
            public void destroy() {}
        };
        MockHttpServletResponse response = run("/hello.action", filterPrepare, middle, filterExecute, failFilter);
        assertEquals(200, response.getStatus());
    }

    private MockHttpServletResponse run(String uri, final Filter... filters) throws ServletException, IOException {
        return run(uri, null, filters);
    }
    private MockHttpServletResponse run(String uri, ActionContext existingContext, final Filter... filters) throws ServletException, IOException {
        final LinkedList<Filter> filterList = new LinkedList<Filter>(Arrays.asList(filters));
        MockHttpServletRequest request = new MockHttpServletRequest();
        MockHttpServletResponse response = new MockHttpServletResponse();
        MockFilterConfig filterConfig = new MockFilterConfig();
        MockFilterChain filterChain = new MockFilterChain() {
            @Override
            public void doFilter(ServletRequest req, ServletResponse res) {
                Filter next = (filterList.size() > 0 ? filterList.removeFirst() : null);
                if (next != null) {
                    try {
                        next.doFilter(req, res, this);
                    } catch (IOException e) {
                        throw new RuntimeException(e);
                    } catch (ServletException e) {
                        throw new RuntimeException(e);
                    }
                }
            }
        };

        if (existingContext != null) {
            request.setAttribute(PrepareOperations.CLEANUP_RECURSION_COUNTER, 1);
        }
        request.setRequestURI(uri);
        for (Filter filter : filters) {
            filter.init(filterConfig);
        }

        ActionContext.setContext(existingContext);
        filterList.removeFirst().doFilter(request, response, filterChain);
        if (existingContext == null) {
            assertNull(ActionContext.getContext());
            assertNull(Dispatcher.getInstance());
        } else {
            assertEquals(Integer.valueOf(1), request.getAttribute(PrepareOperations.CLEANUP_RECURSION_COUNTER));
        }
        return response;
    }


}