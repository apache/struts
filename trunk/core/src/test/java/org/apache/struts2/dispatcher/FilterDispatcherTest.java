/*
 * $Id$
 *
 * Copyright 2006 The Apache Software Foundation.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.apache.struts2.dispatcher;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

import javax.servlet.ServletContext;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.struts2.StrutsConstants;
import org.apache.struts2.StrutsTestCase;
import org.apache.struts2.config.Settings;
import org.apache.struts2.dispatcher.mapper.ActionMapper;
import org.apache.struts2.dispatcher.mapper.ActionMapping;
import org.apache.struts2.util.ObjectFactoryDestroyable;
import org.apache.struts2.util.ObjectFactoryInitializable;
import org.apache.struts2.util.ObjectFactoryLifecycle;
import org.springframework.mock.web.MockFilterConfig;
import org.springframework.mock.web.MockHttpServletRequest;
import org.springframework.mock.web.MockHttpServletResponse;
import org.springframework.mock.web.MockServletContext;

import com.mockobjects.servlet.MockFilterChain;
import com.opensymphony.xwork2.ObjectFactory;
import com.opensymphony.xwork2.config.ConfigurationManager;
import com.opensymphony.xwork2.config.impl.DefaultConfiguration;

/**
 * FilterDispatcher TestCase.
 *
 */
public class FilterDispatcherTest extends StrutsTestCase {


    public void testParsePackages() throws Exception {
        FilterDispatcher filterDispatcher = new FilterDispatcher();
        String[] result1 = filterDispatcher.parse("foo.bar.package1 foo.bar.package2 foo.bar.package3");
        String[] result2 = filterDispatcher.parse("foo.bar.package1\tfoo.bar.package2\tfoo.bar.package3");
        String[] result3 = filterDispatcher.parse("foo.bar.package1,foo.bar.package2,foo.bar.package3");
        String[] result4 = filterDispatcher.parse("foo.bar.package1    foo.bar.package2  \t foo.bar.package3   , foo.bar.package4");

        assertEquals(result1[0], "foo/bar/package1/");
        assertEquals(result1[1], "foo/bar/package2/");
        assertEquals(result1[2], "foo/bar/package3/");

        assertEquals(result2[0], "foo/bar/package1/");
        assertEquals(result2[1], "foo/bar/package2/");
        assertEquals(result2[2], "foo/bar/package3/");

        assertEquals(result3[0], "foo/bar/package1/");
        assertEquals(result3[1], "foo/bar/package2/");
        assertEquals(result3[2], "foo/bar/package3/");

        assertEquals(result4[0], "foo/bar/package1/");
        assertEquals(result4[1], "foo/bar/package2/");
        assertEquals(result4[2], "foo/bar/package3/");
        assertEquals(result4[3], "foo/bar/package4/");
    }

    public void testObjectFactoryDestroy() throws Exception {

        FilterDispatcher filterDispatcher = new FilterDispatcher();
        filterDispatcher.init(new MockFilterConfig((ServletContext) null));
        InnerDestroyableObjectFactory destroyedObjectFactory = new InnerDestroyableObjectFactory();
        ObjectFactory.setObjectFactory(destroyedObjectFactory);

        assertFalse(destroyedObjectFactory.destroyed);
        filterDispatcher.destroy();
        assertTrue(destroyedObjectFactory.destroyed);
    }


    public void testObjectFactoryInitializable() throws Exception {

        Map configMap = new HashMap();
        configMap.put(StrutsConstants.STRUTS_OBJECTFACTORY, "org.apache.struts2.dispatcher.FilterDispatcherTest$InnerInitializableObjectFactory");
        configMap.put(StrutsConstants.STRUTS_CONFIGURATION_XML_RELOAD, "false");
        Settings.setInstance(new InnerConfiguration(configMap));

        MockServletContext servletContext = new MockServletContext();
        MockFilterConfig filterConfig = new MockFilterConfig(servletContext);


        FilterDispatcher filterDispatcher = new FilterDispatcher();
        filterDispatcher.init(filterConfig);

        assertTrue(ObjectFactory.getObjectFactory() instanceof InnerInitializableObjectFactory);
        assertTrue(((InnerInitializableObjectFactory) ObjectFactory.getObjectFactory()).initializable);
    }

    public void testObjectFactoryLifecycle() throws Exception {

        Map configMap = new HashMap();
        configMap.put(StrutsConstants.STRUTS_OBJECTFACTORY, "org.apache.struts2.dispatcher.FilterDispatcherTest$InnerInitailizableDestroyableObjectFactory");
        configMap.put(StrutsConstants.STRUTS_CONFIGURATION_XML_RELOAD, "false");
        Settings.setInstance(new InnerConfiguration(configMap));

        MockServletContext servletContext = new MockServletContext();
        MockFilterConfig filterConfig = new MockFilterConfig(servletContext);


        FilterDispatcher filterDispatcher = new FilterDispatcher();
        filterDispatcher.init(filterConfig);

        assertTrue(ObjectFactory.getObjectFactory() instanceof InnerInitailizableDestroyableObjectFactory);
        assertTrue(((InnerInitailizableDestroyableObjectFactory) ObjectFactory.getObjectFactory()).initializable);

        assertFalse(((InnerInitailizableDestroyableObjectFactory) ObjectFactory.getObjectFactory()).destroyable);
        filterDispatcher.destroy();
        assertTrue(((InnerInitailizableDestroyableObjectFactory) ObjectFactory.getObjectFactory()).destroyable);
    }
    
    public void testIfActionMapperIsNullDontServiceAction() throws Exception {
    	try {
    		MockServletContext servletContext = new MockServletContext();
    		MockFilterConfig filterConfig = new MockFilterConfig(servletContext);
    		MockHttpServletRequest req = new MockHttpServletRequest(servletContext);
    		MockHttpServletResponse res = new MockHttpServletResponse();
    		MockFilterChain chain = new MockFilterChain();
    		final NoOpDispatcher dispatcher = new NoOpDispatcher(servletContext);
    		Dispatcher.setInstance(null);

    		ConfigurationManager confManager = new ConfigurationManager();
    		confManager.setConfiguration(new DefaultConfiguration());
    		dispatcher.setConfigurationManager(confManager);
    		
    		
    		ObjectFactory.setObjectFactory(new InnerObjectFactory());
    		
    		Map settings = new HashMap();
    		settings.put(StrutsConstants.STRUTS_MAPPER_CLASS, "org.apache.struts2.dispatcher.FilterDispatcherTest$NullActionMapper");
    		Settings.setInstance(new InnerConfiguration(settings));
    	
    		FilterDispatcher filter = new FilterDispatcher() {
    			protected Dispatcher createDispatcher() {
    				return dispatcher;
    			}
    		};
    		filter.init(filterConfig);
    		filter.doFilter(req, res, chain);
    	
    		assertFalse(dispatcher.serviceRequest);
    	}
    	finally {
    		Settings.reset();
    	}
    }
    
    public void testCharacterEncodingSetBeforeRequestWrappingAndActionService() throws Exception { 
    	try {
    		MockServletContext servletContext = new MockServletContext();
    		MockFilterConfig filterConfig = new MockFilterConfig(servletContext);
    		MockHttpServletRequest req = new MockHttpServletRequest(servletContext);
    		MockHttpServletResponse res = new MockHttpServletResponse();
    		MockFilterChain chain = new MockFilterChain();
    		final InnerDispatcher dispatcher = new InnerDispatcher(servletContext);
    		Dispatcher.setInstance(null);

    		ConfigurationManager confManager = new ConfigurationManager();
    		confManager.setConfiguration(new DefaultConfiguration());
    		dispatcher.setConfigurationManager(confManager);
    		
    		
    		ObjectFactory.setObjectFactory(new InnerObjectFactory());
    		
    		Map settings = new HashMap();
    		settings.put(StrutsConstants.STRUTS_I18N_ENCODING, "UTF-16_DUMMY");
    		settings.put(StrutsConstants.STRUTS_MAPPER_CLASS, "org.apache.struts2.dispatcher.FilterDispatcherTest$InnerActionMapper");
    		Settings.setInstance(new InnerConfiguration(settings));
    	
    		FilterDispatcher filter = new FilterDispatcher() {
    			protected Dispatcher createDispatcher() {
    				return dispatcher;
    			}
    		};
    		filter.init(filterConfig);
    		filter.doFilter(req, res, chain);
    	
    		assertTrue(dispatcher.wrappedRequest);
    		assertTrue(dispatcher.serviceRequest);
    	}
    	finally {
    		Settings.reset();
    	}
    }


    // === inner class ========
    public static class InnerObjectFactory extends ObjectFactory {
    	
    }
    
    public static class NoOpDispatcher extends Dispatcher {
    	protected boolean wrappedRequest = false;
    	protected boolean serviceRequest = false;

		public NoOpDispatcher(ServletContext servletContext) {
			super(servletContext);
		}

		public HttpServletRequest wrapRequest(HttpServletRequest request, ServletContext servletContext) throws IOException {
			wrappedRequest = true;
			return request;
		}
		
		public void serviceAction(HttpServletRequest request, HttpServletResponse response, ServletContext context, ActionMapping mapping) throws ServletException {
			serviceRequest = true;
		}
    }
    
    public static class InnerDispatcher extends Dispatcher {
    	
    	protected boolean wrappedRequest = false;
    	protected boolean serviceRequest = false;

		public InnerDispatcher(ServletContext servletContext) {
			super(servletContext);
		}

		public HttpServletRequest wrapRequest(HttpServletRequest request, ServletContext servletContext) throws IOException {
			wrappedRequest = true;
			// if we set the chracter encoding AFTER we do wrap request, we will get
			// a failing test
			assertNotNull(request.getCharacterEncoding());
			assertEquals(request.getCharacterEncoding(), "UTF-16_DUMMY");
			
			return request;
		}
		
		public void serviceAction(HttpServletRequest request, HttpServletResponse response, ServletContext context, ActionMapping mapping) throws ServletException {
			serviceRequest = true;
			// if we set the chracter encoding AFTER we do wrap request, we will get
			// a failing test
			assertNotNull(request.getCharacterEncoding());
			assertEquals(request.getCharacterEncoding(), "UTF-16_DUMMY");
		}
    }
    
    public static class InnerActionMapper implements ActionMapper {

		public ActionMapping getMapping(HttpServletRequest request, ConfigurationManager config) {
			return new ActionMapping();
		}

		public String getUriFromActionMapping(ActionMapping mapping) {
			return null;
		}
    }
    
    public static class NullActionMapper implements ActionMapper {
    	public ActionMapping getMapping(HttpServletRequest request, ConfigurationManager config) {
			return null;
		}

		public String getUriFromActionMapping(ActionMapping mapping) {
			return null;
		}
    }
    
    
    public static class InnerConfiguration extends Settings {
        Map<String,String> m;

        public InnerConfiguration(Map configMap) {
            m = configMap;
        }

        public boolean isSetImpl(String name) {
            if (!m.containsKey(name))
                return super.isSetImpl(name);
            else
                return true;
        }

        public String getImpl(String aName) throws IllegalArgumentException {
            if (!m.containsKey(aName))
                return super.getImpl(aName);
            else
                return m.get(aName);
        }
    }

    public static class InnerDestroyableObjectFactory extends ObjectFactory implements ObjectFactoryDestroyable {
        public boolean destroyed = false;

        public void destroy() {
            destroyed = true;
        }
    }

    public static class InnerInitializableObjectFactory extends ObjectFactory implements ObjectFactoryInitializable {
        public boolean initializable = false;

        public void init(ServletContext servletContext) {
            initializable = true;
        }
    }

    public static class InnerInitailizableDestroyableObjectFactory extends ObjectFactory implements ObjectFactoryLifecycle {
        public boolean initializable = false;
        public boolean destroyable = false;

        public void init(ServletContext servletContext) {
            initializable = true;
        }

        public void destroy() {
            destroyable = true;
        }
    }


}
