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

import com.mockobjects.servlet.MockFilterChain;
import com.opensymphony.xwork2.ObjectFactory;
import com.opensymphony.xwork2.config.ConfigurationManager;
import com.opensymphony.xwork2.config.impl.DefaultConfiguration;
import org.apache.struts2.StrutsInternalTestCase;
import org.apache.struts2.dispatcher.mapper.ActionMapper;
import org.apache.struts2.dispatcher.mapper.ActionMapping;
import org.apache.struts2.util.ObjectFactoryDestroyable;
import org.springframework.mock.web.MockFilterConfig;
import org.springframework.mock.web.MockHttpServletRequest;
import org.springframework.mock.web.MockHttpServletResponse;
import org.springframework.mock.web.MockServletContext;

import javax.servlet.FilterConfig;
import javax.servlet.ServletContext;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.Collections;
import java.util.HashMap;

/**
 * FilterDispatcher TestCase.
 *
 * @version $Date$ $Id$
 */
public class FilterDispatcherTest extends StrutsInternalTestCase {

    public void testIfActionMapperIsNullDontServiceAction() throws Exception {
        MockServletContext servletContext = new MockServletContext();
        MockFilterConfig filterConfig = new MockFilterConfig(servletContext);
        MockHttpServletRequest req = new MockHttpServletRequest(servletContext);
        MockHttpServletResponse res = new MockHttpServletResponse();
        MockFilterChain chain = new MockFilterChain();
        final NoOpDispatcher _dispatcher = new NoOpDispatcher(servletContext);
        ConfigurationManager confManager = new ConfigurationManager();
        confManager.setConfiguration(new DefaultConfiguration());
        _dispatcher.setConfigurationManager(confManager);
        Dispatcher.setInstance(_dispatcher);

        


        FilterDispatcher filter = new FilterDispatcher() {
            protected Dispatcher createDispatcher() {
                return _dispatcher;
            }
        };
        filter.init(filterConfig);
        filter.setActionMapper(null);
        filter.doFilter(req, res, chain);

        assertFalse(_dispatcher.serviceRequest);
    }
    
    public void testCharacterEncodingSetBeforeRequestWrappingAndActionService() throws Exception {
        MockServletContext servletContext = new MockServletContext();
        MockFilterConfig filterConfig = new MockFilterConfig(servletContext);
        MockHttpServletRequest req = new MockHttpServletRequest(servletContext);
        MockHttpServletResponse res = new MockHttpServletResponse();
        MockFilterChain chain = new MockFilterChain();
        final InnerDispatcher _dispatcher = new InnerDispatcher(servletContext);
        Dispatcher.setInstance(null);

        _dispatcher.setDefaultEncoding("UTF-16_DUMMY");

        FilterDispatcher filter = new FilterDispatcher() {
            protected Dispatcher createDispatcher(FilterConfig filterConfig) {
                return _dispatcher;
            }
        };
        filter.init(filterConfig);
        // set ActionMapper after init() as all dependencies will be injected in init()
        filter.setActionMapper(new InnerActionMapper());
        _dispatcher.setDefaultEncoding("UTF-16_DUMMY");
        filter.doFilter(req, res, chain);

        assertTrue(_dispatcher.wrappedRequest);
        assertTrue(_dispatcher.serviceRequest);
    }

    // === inner class ========
    public static class InnerObjectFactory extends ObjectFactory {

    }

    public static class NoOpDispatcher extends Dispatcher {
        protected boolean wrappedRequest = false;
        protected boolean serviceRequest = false;

        public NoOpDispatcher(ServletContext servletContext) {
            super(servletContext, new HashMap());
        }

        @Override
        public HttpServletRequest wrapRequest(HttpServletRequest request) throws IOException {
            wrappedRequest = true;
            return request;
        }

        public void serviceAction(HttpServletRequest request, HttpServletResponse response, ActionMapping mapping) throws ServletException {
            serviceRequest = true;
        }

        @Override
        public void sendError(HttpServletRequest request, HttpServletResponse response, int code, Exception e) {
            // NO-OP
        }
    }

    public static class InnerDispatcher extends Dispatcher {

        protected boolean wrappedRequest = false;
        protected boolean serviceRequest = false;

        public InnerDispatcher(ServletContext servletContext) {
            super(servletContext, new HashMap());
        }

        @Override
        public HttpServletRequest wrapRequest(HttpServletRequest request) throws IOException {
            wrappedRequest = true;
            // if we set the chracter encoding AFTER we do wrap request, we will get
            // a failing test
            assertNotNull(request.getCharacterEncoding());
            assertEquals("UTF-16_DUMMY", request.getCharacterEncoding());

            return request;
        }

        public void serviceAction(HttpServletRequest request, HttpServletResponse response, ActionMapping mapping) throws ServletException {
            serviceRequest = true;
            // if we set the chracter encoding AFTER we do wrap request, we will get
            // a failing test
            assertNotNull(request.getCharacterEncoding());
            assertEquals("UTF-16_DUMMY", request.getCharacterEncoding());
        }
    }

    public static class InnerActionMapper implements ActionMapper {

        public ActionMapping getMapping(HttpServletRequest request, ConfigurationManager config) {
            return new ActionMapping("action", "/", null, Collections.<String, Object>emptyMap());
        }

        public ActionMapping getMappingFromActionName(String actionName) {
            return null;  //To change body of implemented methods use File | Settings | File Templates.
        }

        public String getUriFromActionMapping(ActionMapping mapping) {
            return null;
        }
    }

    public static class NullActionMapper implements ActionMapper {
        public ActionMapping getMapping(HttpServletRequest request, ConfigurationManager config) {
            return null;
        }

        public ActionMapping getMappingFromActionName(String actionName) {
            return null;  //To change body of implemented methods use File | Settings | File Templates.
        }

        public String getUriFromActionMapping(ActionMapping mapping) {
            return null;
        }
    }


    public static class InnerDestroyableObjectFactory extends ObjectFactory implements ObjectFactoryDestroyable {
        public boolean destroyed = false;

        public void destroy() {
            destroyed = true;
        }
    }

}
