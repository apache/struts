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

import com.opensymphony.xwork2.ActionContext;
import org.apache.struts2.dispatcher.filter.StrutsExecuteFilter;
import org.apache.struts2.dispatcher.filter.StrutsPrepareFilter;
import org.junit.Before;
import org.junit.Test;
import org.springframework.mock.web.MockFilterChain;
import org.springframework.mock.web.MockFilterConfig;
import org.springframework.mock.web.MockHttpServletRequest;
import org.springframework.mock.web.MockHttpServletResponse;

import jakarta.servlet.Filter;
import jakarta.servlet.FilterChain;
import jakarta.servlet.FilterConfig;
import jakarta.servlet.ServletException;
import jakarta.servlet.ServletRequest;
import jakarta.servlet.ServletResponse;
import java.io.IOException;
import java.util.Arrays;
import java.util.LinkedList;
import java.util.Map;

import static java.util.Collections.emptyMap;
import static java.util.Collections.singletonMap;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertTrue;
import static org.junit.Assert.fail;

/**
 * Integration tests for the filter
 */
public class TwoFilterIntegrationTest {
    private StrutsExecuteFilter filterExecute;
    private StrutsPrepareFilter filterPrepare;
    private Filter failFilter;
    private Filter stringFilter;

    @Before
    public void setUp() {
        filterPrepare = new StrutsPrepareFilter();
        filterExecute = new StrutsExecuteFilter();
        failFilter = newFilter((req, res, chain) -> fail("Should never get here"));
        stringFilter = newFilter((req, res, chain) -> {
            res.getWriter().write("content");
            assertNotNull(ActionContext.getContext());
            assertNotNull(Dispatcher.getInstance());
        });
    }

    @Test
    public void test404() throws ServletException, IOException {
        MockHttpServletResponse response = run("/foo.action", filterPrepare, filterExecute, failFilter);
        assertEquals(404, response.getStatus());
    }

    @Test
    public void test200() throws ServletException, IOException {
        MockHttpServletResponse response = run("/hello.action", filterPrepare, filterExecute, failFilter);
        assertEquals(200, response.getStatus());
    }

    @Test
    public void testStaticFallthrough() throws ServletException, IOException {
        MockHttpServletResponse response = run("/foo.txt", filterPrepare, filterExecute, stringFilter);
        assertEquals(200, response.getStatus());
        assertEquals("content", response.getContentAsString());

    }

    @Test
    public void testStaticExecute() throws ServletException, IOException {
        MockHttpServletResponse response = run("/static/utils.js", filterPrepare, filterExecute, failFilter);
        assertEquals(200, response.getStatus());
        assertTrue(response.getContentAsString().contains("StrutsUtils"));
    }

    @Test
    public void testFilterInMiddle() throws ServletException, IOException {
        Filter middle = newFilter((req, res, chain) -> {
            assertNotNull(ActionContext.getContext());
            assertNotNull(Dispatcher.getInstance());
            assertNull(ActionContext.getContext().getActionInvocation());
            chain.doFilter(req, res);
            assertEquals("hello", ActionContext.getContext().getActionInvocation().getProxy().getActionName());
        });
        MockHttpServletResponse response = run("/hello.action", filterPrepare, middle, filterExecute, failFilter);
        assertEquals(200, response.getStatus());
    }

    /**
     * It is possible for a Struts excluded URL to be forwarded to a Struts URL. If this happens, the ActionContext
     * should not be cleared until the very first execution of the StrutsPrepareFilter, otherwise SiteMesh will malfunction.
     */
    @Test
    public void testActionContextNotClearedUntilEndWhenForwardedFromExcludedUrl() throws ServletException, IOException {
        Filter firstFilter = newFilter((req, res, chain) -> {
            chain.doFilter(req, res);
            // Assert ActionContext cleared at end of request lifecycle
            assertNull(ActionContext.getContext());
        });
        Filter dummySiteMesh = newFilter((req, res, chain) -> {
            // Assert ActionContext not created initially, as URL is Struts excluded
            assertNull(ActionContext.getContext());
            chain.doFilter(req, res);
            // Assert ActionContext not cleared by second StrutsPrepareFilter even though it created it
            assertNotNull(ActionContext.getContext());
        });
        Filter dummyForward = newFilter((req, res, chain) -> {
            MockHttpServletRequest castReq = (MockHttpServletRequest) req;
            String oldUri = castReq.getRequestURI();
            castReq.setRequestURI("/hello.action");
            chain.doFilter(castReq, res);
            castReq.setRequestURI(oldUri);
        });
        MockHttpServletResponse response = run(
                "/excluded/hello.action",
                singletonMap("struts.action.excludePattern", "^/excluded/hello.action"),
                firstFilter,
                filterPrepare,
                dummySiteMesh,
                filterExecute,
                dummyForward,
                filterPrepare);
        assertEquals(200, response.getStatus());
    }

    private MockHttpServletResponse run(String uri, final Filter... filters) throws ServletException, IOException {
        return run(uri, emptyMap(), filters);
    }

    private MockHttpServletResponse run(String uri, Map<String, String> filterInitParams, final Filter... filters) throws ServletException, IOException {
        final LinkedList<Filter> filterList = new LinkedList<>(Arrays.asList(filters));
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
                    } catch (IOException | ServletException e) {
                        throw new RuntimeException(e);
                    }
                }
            }
        };
        filterInitParams.forEach(filterConfig::addInitParameter);
        request.setRequestURI(uri);
        for (Filter filter : filters) {
            filter.init(filterConfig);
        }

        filterList.removeFirst().doFilter(request, response, filterChain);
        assertNull(ActionContext.getContext());
        assertNull(Dispatcher.getInstance());
        return response;
    }

    private Filter newFilter(DoFilterConsumer doFilterConsumer) {
        return new Filter() {
            public void init(FilterConfig filterConfig) {
            }

            public void doFilter(ServletRequest request, ServletResponse response, FilterChain chain) throws IOException, ServletException {
                doFilterConsumer.accept(request, response, chain);
            }

            public void destroy() {
            }
        };
    }

    @FunctionalInterface
    public interface DoFilterConsumer {
        void accept(ServletRequest req, ServletResponse res, FilterChain chain) throws IOException, ServletException;
    }
}
