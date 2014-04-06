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

import javax.servlet.FilterConfig;
import javax.servlet.ServletContext;
import javax.servlet.ServletException;
import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.struts2.StrutsInternalTestCase;
import org.apache.struts2.dispatcher.mapper.ActionMapper;
import org.apache.struts2.dispatcher.mapper.ActionMapping;
import org.springframework.mock.web.MockFilterConfig;
import org.springframework.mock.web.MockHttpServletRequest;
import org.springframework.mock.web.MockHttpServletResponse;
import org.springframework.mock.web.MockServletContext;

import com.mockobjects.servlet.MockFilterChain;
import com.opensymphony.xwork2.ObjectFactory;
import com.opensymphony.xwork2.config.ConfigurationManager;
import com.opensymphony.xwork2.inject.Container;


/**
 *
 * @version $Date$ $Id$
 */
public class FilterTest extends StrutsInternalTestCase {

    protected MockFilterConfig filterConfig;
    protected MockHttpServletRequest request;
    protected MockHttpServletResponse response;
    protected MockFilterChain filterChain;
    protected MockFilterChain filterChain2;
    protected MockServletContext servletContext;

    protected InnerDispatcher _dispatcher1;
    protected InnerDispatcher _dispatcher2;
    protected ActionContextCleanUp cleanUp;
    protected FilterDispatcher filterDispatcher;

    protected int cleanUpFilterCreateDispatcherCount = 0; // number of times clean up filter create a dispatcher
    protected int filterDispatcherCreateDispatcherCount = 0; // number of times FilterDispatcher create a dispatcher


    @Override
    protected void tearDown() throws Exception {
        super.tearDown();
        filterConfig = null;
        request = null;
        response = null;
        filterChain = null;
        filterChain2 = null;
        servletContext = null;
        _dispatcher1 = null;
        _dispatcher2 = null;
        cleanUp = null;
        filterDispatcher = null;
    }

    @Override
    protected void setUp() throws Exception {
        super.setUp();
        Dispatcher.setInstance(null);

        filterConfig = new MockFilterConfig();
        request = new MockHttpServletRequest();
        response = new MockHttpServletResponse();
        servletContext = new MockServletContext();

        _dispatcher1 = new InnerDispatcher(servletContext){
            @Override
            public String toString() {
                return "dispatcher1";
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
            public void doFilter(ServletRequest req, ServletResponse res) throws IOException, ServletException {
                filterDispatcher.doFilter(req, res, filterChain2);
            }
        };
        filterChain2 = new MockFilterChain() {
            @Override
            public void doFilter(ServletRequest req, ServletResponse res) throws IOException, ServletException {
            }
        };


        cleanUp = new ActionContextCleanUp();

        filterDispatcher = new FilterDispatcher() {
            @Override
            protected Dispatcher createDispatcher(FilterConfig filterConfig) {
                filterDispatcherCreateDispatcherCount++;
                return _dispatcher2;
            }
            
            @Override
            public String toString() {
                return "filterDispatcher";
            }
        };
    }

    public void testUsingFilterDispatcherOnly() throws Exception {
        assertEquals(cleanUpFilterCreateDispatcherCount, 0);
        assertEquals(filterDispatcherCreateDispatcherCount, 0);
        assertFalse(_dispatcher1.init);
        assertFalse(_dispatcher1.prepare);
        assertFalse(_dispatcher1.wrapRequest);
        assertFalse(_dispatcher1.service);
        assertFalse(_dispatcher2.init);
        assertFalse(_dispatcher2.prepare);
        assertFalse(_dispatcher2.wrapRequest);
        assertFalse(_dispatcher2.service);

        filterDispatcher.init(filterConfig);
        filterDispatcher.setActionMapper(new FilterTest.InnerMapper());
        filterDispatcher.doFilter(request, response, filterChain2);
        filterDispatcher.destroy();

        // we are using FilterDispatcher only, so cleanUp filter's Dispatcher should not be created.
        assertEquals(cleanUpFilterCreateDispatcherCount, 0);
        assertEquals(filterDispatcherCreateDispatcherCount, 1);
        assertFalse(_dispatcher1.init);
        assertFalse(_dispatcher1.prepare);
        assertFalse(_dispatcher1.wrapRequest);
        assertFalse(_dispatcher1.service);
        assertTrue(_dispatcher2.init);
        assertTrue(_dispatcher2.prepare);
        assertTrue(_dispatcher2.wrapRequest);
        assertTrue(_dispatcher2.service);
        assertTrue(Dispatcher.getInstance() == null);
    }


    public void testUsingFilterDispatcherOnly_Multiple() throws Exception {
        
        filterDispatcher.setActionMapper(new FilterTest.InnerMapper());

        assertEquals(cleanUpFilterCreateDispatcherCount, 0);
        assertEquals(filterDispatcherCreateDispatcherCount, 0);
        assertFalse(_dispatcher1.prepare);
        assertFalse(_dispatcher1.wrapRequest);
        assertFalse(_dispatcher1.service);
        assertFalse(_dispatcher1.cleanUp);
        assertFalse(_dispatcher2.prepare);
        assertFalse(_dispatcher2.wrapRequest);
        assertFalse(_dispatcher2.service);
        assertFalse(_dispatcher2.cleanUp);

        filterDispatcher.init(filterConfig);
        filterDispatcher.setActionMapper(new FilterTest.InnerMapper());
        filterDispatcher.doFilter(request, response, filterChain2);
        filterDispatcher.doFilter(request, response, filterChain2);
        filterDispatcher.destroy();

        assertEquals(cleanUpFilterCreateDispatcherCount, 0);
        // We should create dispatcher once, although filter.doFilter(...) is called  many times.
        assertEquals(filterDispatcherCreateDispatcherCount, 1);
        assertFalse(_dispatcher1.prepare);
        assertFalse(_dispatcher1.wrapRequest);
        assertFalse(_dispatcher1.service);
        assertFalse(_dispatcher1.cleanUp);
        assertTrue(_dispatcher2.prepare);
        assertTrue(_dispatcher2.wrapRequest);
        assertTrue(_dispatcher2.service);
        assertTrue(_dispatcher2.cleanUp);
        assertTrue(Dispatcher.getInstance() == null);
        
    }

    

    /*public void testUsingCleanUpAndFilterDispatcher() throws Exception {
        ObjectFactory oldObjecFactory = ObjectFactory.getObjectFactory();
        try {
            ObjectFactory.setObjectFactory(new InnerObjectFactory());
            filterDispatcher.setActionMapper(new FilterTest.InnerMapper());

            assertEquals(cleanUpFilterCreateDispatcherCount, 0);
            assertEquals(filterDispatcherCreateDispatcherCount, 0);
            assertFalse(_dispatcher1.prepare);
            assertFalse(_dispatcher1.wrapRequest);
            assertFalse(_dispatcher1.service);
            assertFalse(_dispatcher2.prepare);
            assertFalse(_dispatcher2.wrapRequest);
            assertFalse(_dispatcher2.service);

            cleanUp.init(filterConfig);
            filterDispatcher.init(filterConfig);
            cleanUp.doFilter(request, response, filterChain);
            filterDispatcher.destroy();
            cleanUp.destroy();

            assertEquals(1, cleanUpFilterCreateDispatcherCount);
            assertEquals(1, filterDispatcherCreateDispatcherCount);
            assertTrue(_dispatcher1.prepare);
            assertTrue(_dispatcher1.wrapRequest);
            assertTrue(_dispatcher1.service);
            assertFalse(_dispatcher2.prepare);
            assertFalse(_dispatcher2.wrapRequest);
            assertFalse(_dispatcher2.service);
            assertTrue(Dispatcher.getInstance() == null);
        }
        finally {
            ObjectFactory.setObjectFactory(oldObjecFactory);
        }
    }


    public void testUsingCleanUpAndFilterDispatcher_Multiple() throws Exception {
        ObjectFactory oldObjecFactory = ObjectFactory.getObjectFactory();
        try {
            ObjectFactory.setObjectFactory(new InnerObjectFactory());
            filterDispatcher.setActionMapper(new FilterTest.InnerMapper());

            assertEquals(cleanUpFilterCreateDispatcherCount, 0);
            assertEquals(filterDispatcherCreateDispatcherCount, 0);
            assertFalse(_dispatcher1.prepare);
            assertFalse(_dispatcher1.wrapRequest);
            assertFalse(_dispatcher1.service);
            assertFalse(_dispatcher2.prepare);
            assertFalse(_dispatcher2.wrapRequest);
            assertFalse(_dispatcher2.service);

            cleanUp.init(filterConfig);
            filterDispatcher.init(filterConfig);
            cleanUp.doFilter(request, response, filterChain);
            cleanUp.doFilter(request, response, filterChain);
            filterDispatcher.destroy();
            cleanUp.destroy();

            assertEquals(cleanUpFilterCreateDispatcherCount, 1);
            assertEquals(filterDispatcherCreateDispatcherCount, 1);
            assertTrue(_dispatcher1.prepare);
            assertTrue(_dispatcher1.wrapRequest);
            assertTrue(_dispatcher1.service);
            assertFalse(_dispatcher2.prepare);
            assertFalse(_dispatcher2.wrapRequest);
            assertFalse(_dispatcher2.service);
            assertTrue(Dispatcher.getInstance() == null);
        }
        finally {
            ObjectFactory.setObjectFactory(oldObjecFactory);
        }
    }
    */


    class InnerDispatcher extends Dispatcher {
    	public boolean init = false;
        public boolean prepare = false;
        public boolean wrapRequest = false;
        public boolean service = false;
        public boolean cleanUp = false;

        public InnerDispatcher(ServletContext servletContext) {
            super(servletContext, new HashMap<String, String>());
        }
        
        @Override
        public void init() {
        	init= true;
        }
        
        @Override 
        public Container getContainer() {
            return container;
        }

        @Override
        public void prepare(HttpServletRequest request, HttpServletResponse response) {
            prepare = true;
        }

        @Override
        public HttpServletRequest wrapRequest(HttpServletRequest request) throws IOException {
            wrapRequest = true;
            return request;
        }

        @Override
        public void serviceAction(HttpServletRequest request, HttpServletResponse response, ActionMapping mapping) throws ServletException {
            service = true;
        }

        @Override
        public void cleanup() {
        	cleanUp = true;
        }
    }

    class NullInnerMapper implements ActionMapper {
        public ActionMapping getMapping(HttpServletRequest request, ConfigurationManager configManager) {
            return null;
        }

        public ActionMapping getMappingFromActionName(String actionName) {
            return null;  //To change body of implemented methods use File | Settings | File Templates.
        }

        public String getUriFromActionMapping(ActionMapping mapping) {
            return null;
        }
    }

    public static class InnerMapper implements ActionMapper {

        public InnerMapper() {}

        public ActionMapping getMapping(HttpServletRequest request, ConfigurationManager configManager) {
            return new ActionMapping();
        }

        public ActionMapping getMappingFromActionName(String actionName) {
            return null;  //To change body of implemented methods use File | Settings | File Templates.
        }

        public String getUriFromActionMapping(ActionMapping mapping) {
            return "";
        }
    }

    class InnerObjectFactory extends ObjectFactory {
        public InnerObjectFactory() {
            super();
        }
    }
}

