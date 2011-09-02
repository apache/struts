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

package org.apache.struts2.dispatcher;

import java.io.IOException;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.Map;

import javax.servlet.ServletContext;
import javax.servlet.ServletException;
import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.struts2.dispatcher.mapper.ActionMapping;
import org.springframework.mock.web.MockFilterConfig;
import org.springframework.mock.web.MockHttpServletRequest;
import org.springframework.mock.web.MockHttpServletResponse;
import org.springframework.mock.web.MockServletContext;

import com.mockobjects.servlet.MockFilterChain;

import junit.framework.TestCase;

/**
 * @version $Date$ $Id$
 */
public class ActionContextCleanUpTest extends TestCase {


    protected MockFilterConfig filterConfig;
    protected MockHttpServletRequest request;
    protected MockHttpServletResponse response;
    protected MockFilterChain filterChain;
    protected MockFilterChain filterChain2;
    protected MockServletContext servletContext;

    protected Counter counter;
    protected Map<String, Integer> _tmpStore;
    protected InnerDispatcher _dispatcher;
    protected InnerDispatcher _dispatcher2;
    protected ActionContextCleanUp cleanUp;
    protected ActionContextCleanUp cleanUp2;


    @Override
    protected void tearDown() throws Exception {
        filterConfig = null;
        request = null;
        response = null;
        filterChain = null;
        filterChain2 = null;
        servletContext = null;
        counter = null;
        _tmpStore = null;
        _dispatcher = null;
        _dispatcher2 = null;
        cleanUp = null;
        cleanUp2 = null;
    }

    @Override
    protected void setUp() throws Exception {
        Dispatcher.setInstance(null);

        counter = new Counter();
        _tmpStore = new LinkedHashMap<String, Integer>();

        filterConfig = new MockFilterConfig();
        request = new MockHttpServletRequest();
        response = new MockHttpServletResponse();
        servletContext = new MockServletContext();
        _dispatcher = new InnerDispatcher(servletContext) {
            @Override
            public String toString() {
                return "dispatcher";
            }
        };
        _dispatcher2 = new InnerDispatcher(servletContext){
            @Override
            public String toString() {
                return "dispatcher2";
            }
        };


        filterChain = new MockFilterChain() {
            @Override
            public void doFilter(ServletRequest request, ServletResponse response) throws IOException, ServletException {
                _tmpStore.put("counter"+(counter.count++), (Integer) request.getAttribute("__cleanup_recursion_counter"));
            }
        };

        cleanUp = new ActionContextCleanUp();
        cleanUp2 = new ActionContextCleanUp();
        filterChain2 = new MockFilterChain() {
            @Override
            public void doFilter(ServletRequest request, ServletResponse response) throws IOException, ServletException {
                _tmpStore.put("counter"+(counter.count++), (Integer) request.getAttribute("__cleanup_recursion_counter"));
                cleanUp2.doFilter(request, response, filterChain);
            }
        };
    }


    public void testSingle() throws Exception {
        assertNull(request.getAttribute("__cleanup_recursion_counter"));

        cleanUp.init(filterConfig);
        cleanUp.doFilter(request, response, filterChain);
        cleanUp.destroy();

        assertEquals(_tmpStore.size(), 1);
        assertEquals(_tmpStore.get("counter0"), new Integer(1));

        assertEquals(request.getAttribute("__cleanup_recursion_counter"), new Integer("0"));
    }

    public void testMultiple() throws Exception {
        assertNull(request.getAttribute("__cleanup_recursion_counter"));

        cleanUp.init(filterConfig);
        cleanUp2.init(filterConfig);
        cleanUp.doFilter(request, response, filterChain2);
        cleanUp2.destroy();
        cleanUp.destroy();

        assertEquals(_tmpStore.size(), 2);
        assertEquals(_tmpStore.get("counter0"), new Integer(1));
        assertEquals(_tmpStore.get("counter1"), new Integer(2));

        assertEquals(request.getAttribute("__cleanup_recursion_counter"), new Integer("0"));
    }


    class InnerDispatcher extends Dispatcher {
        public boolean prepare = false;
        public boolean wrapRequest = false;
        public boolean service = false;

        public InnerDispatcher(ServletContext servletContext) {
            super(servletContext, new HashMap<String,String>());
        }

        @Override
        public void prepare(HttpServletRequest request, HttpServletResponse response) {
            prepare = true;
        }

        @Override
        public HttpServletRequest wrapRequest(HttpServletRequest request, ServletContext servletContext) throws IOException {
            wrapRequest = true;
            return request;
        }

        @Override
        public void serviceAction(HttpServletRequest request, HttpServletResponse response, ServletContext context, ActionMapping mapping) throws ServletException {
            service = true;
        }
    }

    class Counter {
        public int count=0;
    }
}
