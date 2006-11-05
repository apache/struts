/*
 * $Id: FilterDispatcherTest.java 449367 2006-09-24 06:49:04Z mrdon $
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

import javax.servlet.ServletContext;
import javax.servlet.ServletException;
import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import junit.framework.TestCase;

import org.apache.struts2.StrutsConstants;
import org.apache.struts2.config.Settings;
import org.apache.struts2.dispatcher.mapper.ActionMapper;
import org.apache.struts2.dispatcher.mapper.ActionMapping;
import org.springframework.mock.web.MockFilterConfig;
import org.springframework.mock.web.MockHttpServletRequest;
import org.springframework.mock.web.MockHttpServletResponse;
import org.springframework.mock.web.MockServletContext;

import com.mockobjects.servlet.MockFilterChain;
import com.opensymphony.xwork2.ObjectFactory;
import com.opensymphony.xwork2.config.ConfigurationManager;


/**
 * 
 * @version $Date$ $Id$
 */
public class FilterTest extends TestCase { 
	
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
		
		
		cleanUp = new ActionContextCleanUp() {
			@Override
			protected Dispatcher createDispatcher() {
				cleanUpFilterCreateDispatcherCount++;
				return _dispatcher1;
			}
			
			@Override
			public String toString() {
				return "cleanUp";
			}
		};
		
		filterDispatcher = new FilterDispatcher() {
			@Override
			protected Dispatcher createDispatcher() {
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
		ObjectFactory oldObjecFactory = ObjectFactory.getObjectFactory();
		try {
			ObjectFactory.setObjectFactory(new InnerObjectFactory());
			Settings.set(StrutsConstants.STRUTS_MAPPER_CLASS, "org.apache.struts2.dispatcher.FilterTest$InnerMapper");
		
			assertEquals(cleanUpFilterCreateDispatcherCount, 0);
			assertEquals(filterDispatcherCreateDispatcherCount, 0);
			assertFalse(_dispatcher1.prepare);
			assertFalse(_dispatcher1.wrapRequest);
			assertFalse(_dispatcher1.service);
			assertFalse(_dispatcher2.prepare);
			assertFalse(_dispatcher2.wrapRequest);
			assertFalse(_dispatcher2.service);
		
			filterDispatcher.init(filterConfig);
			filterDispatcher.doFilter(request, response, filterChain2);
			filterDispatcher.destroy();
		
			// we are using FilterDispatcher only, so cleanUp filter's Dispatcher should not be created.
			assertEquals(cleanUpFilterCreateDispatcherCount, 0);
			assertEquals(filterDispatcherCreateDispatcherCount, 1);
			assertFalse(_dispatcher1.prepare);
			assertFalse(_dispatcher1.wrapRequest);
			assertFalse(_dispatcher1.service);
			assertTrue(_dispatcher2.prepare);
			assertTrue(_dispatcher2.wrapRequest);
			assertTrue(_dispatcher2.service);
			assertTrue(Dispatcher.getInstance() == null);
		}
		finally {
			ObjectFactory.setObjectFactory(oldObjecFactory);
		}
	}
	
	public void testUsingFilterDispatcherOnly_Multiple() throws Exception {
		ObjectFactory oldObjecFactory = ObjectFactory.getObjectFactory();
		try {
			ObjectFactory.setObjectFactory(new InnerObjectFactory());
			Settings.set(StrutsConstants.STRUTS_MAPPER_CLASS, "org.apache.struts2.dispatcher.FilterTest$InnerMapper");
		
			assertEquals(cleanUpFilterCreateDispatcherCount, 0);
			assertEquals(filterDispatcherCreateDispatcherCount, 0);
			assertFalse(_dispatcher1.prepare);
			assertFalse(_dispatcher1.wrapRequest);
			assertFalse(_dispatcher1.service);
			assertFalse(_dispatcher2.prepare);
			assertFalse(_dispatcher2.wrapRequest);
			assertFalse(_dispatcher2.service);
		
			filterDispatcher.init(filterConfig);
			filterDispatcher.doFilter(request, response, filterChain2);
			filterDispatcher.doFilter(request, response, filterChain2);
			filterDispatcher.destroy();
		
			assertEquals(cleanUpFilterCreateDispatcherCount, 0);
			// We should create dispatcher once, although filter.doFilter(...) is called  many times.
			assertEquals(filterDispatcherCreateDispatcherCount, 1);
			assertFalse(_dispatcher1.prepare);
			assertFalse(_dispatcher1.wrapRequest);
			assertFalse(_dispatcher1.service);
			assertTrue(_dispatcher2.prepare);
			assertTrue(_dispatcher2.wrapRequest);
			assertTrue(_dispatcher2.service);
			assertTrue(Dispatcher.getInstance() == null);
		}
		finally {
			ObjectFactory.setObjectFactory(oldObjecFactory);
		}
	}
	
	public void testUsingCleanUpAndFilterDispatcher() throws Exception {
		ObjectFactory oldObjecFactory = ObjectFactory.getObjectFactory();
		try {
			ObjectFactory.setObjectFactory(new InnerObjectFactory());
			Settings.set(StrutsConstants.STRUTS_MAPPER_CLASS, "org.apache.struts2.dispatcher.FilterTest$InnerMapper");
		
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
	
	
	public void testUsingCleanUpAndFilterDispatcher_Multiple() throws Exception {
		ObjectFactory oldObjecFactory = ObjectFactory.getObjectFactory();
		try {
			ObjectFactory.setObjectFactory(new InnerObjectFactory());
			Settings.set(StrutsConstants.STRUTS_MAPPER_CLASS, "org.apache.struts2.dispatcher.FilterTest$InnerMapper");
		
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
	
	
	class InnerDispatcher extends Dispatcher {
		public boolean prepare = false;
		public boolean wrapRequest = false;
		public boolean service = false;
		
		public InnerDispatcher(ServletContext servletContext) {
			super(servletContext);
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
	
	class NullInnerMapper implements ActionMapper {
		public ActionMapping getMapping(HttpServletRequest request, ConfigurationManager configManager) {
			return null;
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

