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

import com.mockobjects.dynamic.C;
import com.mockobjects.dynamic.Mock;
import com.opensymphony.xwork2.ObjectFactory;
import com.opensymphony.xwork2.XWorkConstants;
import com.opensymphony.xwork2.config.Configuration;
import com.opensymphony.xwork2.config.ConfigurationManager;
import com.opensymphony.xwork2.config.entities.InterceptorMapping;
import com.opensymphony.xwork2.config.entities.InterceptorStackConfig;
import com.opensymphony.xwork2.config.entities.PackageConfig;
import com.opensymphony.xwork2.inject.Container;
import com.opensymphony.xwork2.interceptor.Interceptor;
import com.opensymphony.xwork2.LocalizedTextProvider;
import org.apache.struts2.StrutsConstants;
import org.apache.struts2.StrutsInternalTestCase;
import org.apache.struts2.dispatcher.multipart.MultiPartRequestWrapper;
import org.apache.struts2.util.ObjectFactoryDestroyable;
import org.springframework.mock.web.MockHttpServletRequest;
import org.springframework.mock.web.MockHttpServletResponse;
import org.springframework.mock.web.MockServletContext;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.util.Collections;
import java.util.HashMap;
import java.util.Locale;
import java.util.Map;

/**
 * Test case for Dispatcher.
 *
 */
public class DispatcherTest extends StrutsInternalTestCase {

    public void testDefaultResurceBundlePropertyLoaded() throws Exception {
        LocalizedTextProvider localizedTextProvider = container.getInstance(LocalizedTextProvider.class);

        // some i18n messages from xwork-messages.properties
        assertEquals(localizedTextProvider.findDefaultText("xwork.error.action.execution", Locale.US),
                "Error during Action invocation");

        // some i18n messages from struts-messages.properties
        assertEquals(localizedTextProvider.findDefaultText("struts.messages.error.uploading", Locale.US,
                        new Object[] { "some error messages" }),
                "Error uploading: some error messages");
    }

    public void testPrepareSetEncodingProperly() throws Exception {
        HttpServletRequest req = new MockHttpServletRequest();
        HttpServletResponse res = new MockHttpServletResponse();

        Dispatcher du = initDispatcher(new HashMap<String, String>() {{
            put(StrutsConstants.STRUTS_I18N_ENCODING, "utf-8");
        }});
        du.prepare(req, res);

        assertEquals(req.getCharacterEncoding(), "utf-8");
    }

    public void testEncodingForXMLHttpRequest() throws Exception {
        // given
        MockHttpServletRequest req = new MockHttpServletRequest();
        req.addHeader("X-Requested-With", "XMLHttpRequest");
        req.setCharacterEncoding("UTF-8");
        HttpServletResponse res = new MockHttpServletResponse();

        Dispatcher du = initDispatcher(new HashMap<String, String>() {{
            put(StrutsConstants.STRUTS_I18N_ENCODING, "latin-2");
        }});

        // when
        du.prepare(req, res);

        // then
        assertEquals(req.getCharacterEncoding(), "UTF-8");
    }

    public void testSetEncodingIfDiffer() throws Exception {
        // given
        Mock mock = new Mock(HttpServletRequest.class);
        mock.expectAndReturn("getCharacterEncoding", "utf-8");
        mock.expectAndReturn("getHeader", "X-Requested-With", "");
        mock.expectAndReturn("getCharacterEncoding", "utf-8");
        HttpServletRequest req = (HttpServletRequest) mock.proxy();
        HttpServletResponse res = new MockHttpServletResponse();

        Dispatcher du = initDispatcher(new HashMap<String, String>() {{
            put(StrutsConstants.STRUTS_I18N_ENCODING, "utf-8");
        }});


        // when
        du.prepare(req, res);

        // then

        assertEquals(req.getCharacterEncoding(), "utf-8");
        mock.verify();
    }

    public void testPrepareSetEncodingPropertyWithMultipartRequest() throws Exception {
        MockHttpServletRequest req = new MockHttpServletRequest();
        MockHttpServletResponse res = new MockHttpServletResponse();

        req.setContentType("multipart/form-data");
        Dispatcher du = initDispatcher(new HashMap<String, String>() {{
            put(StrutsConstants.STRUTS_I18N_ENCODING, "utf-8");
        }});
        du.prepare(req, res);

        assertEquals("utf-8", req.getCharacterEncoding());
    }
    
    public void testPrepareMultipartRequest() throws Exception {
        MockHttpServletRequest req = new MockHttpServletRequest();
        MockHttpServletResponse res = new MockHttpServletResponse();

        req.setMethod("post");
        req.setContentType("multipart/form-data; boundary=asdcvb345asd");
        Dispatcher du = initDispatcher(Collections.<String, String>emptyMap());
        du.prepare(req, res);
        HttpServletRequest wrapped = du.wrapRequest(req);

        assertTrue(wrapped instanceof MultiPartRequestWrapper);
    }

    public void testPrepareMultipartRequestAllAllowedCharacters() throws Exception {
        MockHttpServletRequest req = new MockHttpServletRequest();
        MockHttpServletResponse res = new MockHttpServletResponse();

        req.setMethod("post");
        req.setContentType("multipart/form-data; boundary=01=23a.bC:D((e)d'z?p+o_r,e-");
        Dispatcher du = initDispatcher(Collections.<String, String>emptyMap());
        du.prepare(req, res);
        HttpServletRequest wrapped = du.wrapRequest(req);

        assertTrue(wrapped instanceof MultiPartRequestWrapper);
    }

    public void testPrepareMultipartRequestIllegalCharacter() throws Exception {
        MockHttpServletRequest req = new MockHttpServletRequest();
        MockHttpServletResponse res = new MockHttpServletResponse();

        req.setMethod("post");
        req.setContentType("multipart/form-data; boundary=01=2;3a.bC:D((e)d'z?p+o_r,e-");
        Dispatcher du = initDispatcher(Collections.<String, String>emptyMap());
        du.prepare(req, res);
        HttpServletRequest wrapped = du.wrapRequest(req);

        assertFalse(wrapped instanceof MultiPartRequestWrapper);
    }

    public void testDispatcherListener() throws Exception {
    	
    	final DispatcherListenerState state = new DispatcherListenerState();
    	
    	Dispatcher.addDispatcherListener(new DispatcherListener() {
			public void dispatcherDestroyed(Dispatcher du) {
				state.isDestroyed = true;
			}
			public void dispatcherInitialized(Dispatcher du) {
				state.isInitialized = true;
			}
    	});
    	
    	
    	assertFalse(state.isDestroyed);
    	assertFalse(state.isInitialized);
    	
        Dispatcher du = initDispatcher(new HashMap<String, String>() );
    	
    	assertTrue(state.isInitialized);
    	
    	du.cleanup();

    	assertTrue(state.isDestroyed);
    }
    
    
    public void testConfigurationManager() {
    	Dispatcher du;
    	final InternalConfigurationManager configurationManager = new InternalConfigurationManager(Container.DEFAULT_NAME);
    	try {
    		du = new MockDispatcher(new MockServletContext(), new HashMap<String, String>(), configurationManager);
    		du.init();
            Dispatcher.setInstance(du);
            
            assertFalse(configurationManager.destroyConfiguration);
            
            du.cleanup();
            
            assertTrue(configurationManager.destroyConfiguration);
            
    	}
    	finally {
    		Dispatcher.setInstance(null);
    	}
    }
    
    public void testObjectFactoryDestroy() throws Exception {

        ConfigurationManager cm = new ConfigurationManager(Container.DEFAULT_NAME);
        Dispatcher du = new MockDispatcher(new MockServletContext(), new HashMap<String, String>(), cm);
        Mock mockConfiguration = new Mock(Configuration.class);
        cm.setConfiguration((Configuration)mockConfiguration.proxy());
        
        Mock mockContainer = new Mock(Container.class);
        String reloadConfigs = container.getInstance(String.class, XWorkConstants.RELOAD_XML_CONFIGURATION);
        mockContainer.expectAndReturn("getInstance", C.args(C.eq(String.class), C.eq(XWorkConstants.RELOAD_XML_CONFIGURATION)),
                reloadConfigs);
        final InnerDestroyableObjectFactory destroyedObjectFactory = new InnerDestroyableObjectFactory();
        destroyedObjectFactory.setContainer((Container) mockContainer.proxy());
        mockContainer.expectAndReturn("getInstance", C.args(C.eq(ObjectFactory.class)), destroyedObjectFactory);

        mockConfiguration.expectAndReturn("getContainer", mockContainer.proxy());
        mockConfiguration.expectAndReturn("getContainer", mockContainer.proxy());
        mockConfiguration.expect("destroy");
        mockConfiguration.matchAndReturn("getPackageConfigs", new HashMap<String, PackageConfig>());

        du.init();
        assertFalse(destroyedObjectFactory.destroyed);
        du.cleanup();
        assertTrue(destroyedObjectFactory.destroyed);
        mockConfiguration.verify();
        mockContainer.verify();
    }
    
    public void testInterceptorDestroy() throws Exception {           
        Mock mockInterceptor = new Mock(Interceptor.class);
        mockInterceptor.matchAndReturn("hashCode", 0);
        mockInterceptor.expect("destroy");
        
        InterceptorMapping interceptorMapping = new InterceptorMapping("test", (Interceptor) mockInterceptor.proxy());
        
        InterceptorStackConfig isc = new InterceptorStackConfig.Builder("test").addInterceptor(interceptorMapping).build();
        
        PackageConfig packageConfig = new PackageConfig.Builder("test").addInterceptorStackConfig(isc).build();
        
        Map<String, PackageConfig> packageConfigs = new HashMap<String, PackageConfig>();
        packageConfigs.put("test", packageConfig);

        Mock mockContainer = new Mock(Container.class);
        mockContainer.matchAndReturn("getInstance", C.args(C.eq(ObjectFactory.class)), new ObjectFactory());
        String reloadConfigs = container.getInstance(String.class, XWorkConstants.RELOAD_XML_CONFIGURATION);
        mockContainer.expectAndReturn("getInstance", C.args(C.eq(String.class), C.eq(XWorkConstants.RELOAD_XML_CONFIGURATION)),
                reloadConfigs);

        Mock mockConfiguration = new Mock(Configuration.class);
        mockConfiguration.matchAndReturn("getPackageConfigs", packageConfigs);
        mockConfiguration.matchAndReturn("getContainer", mockContainer.proxy());
        mockConfiguration.expect("destroy");
        
        ConfigurationManager configurationManager = new ConfigurationManager(Container.DEFAULT_NAME);
        configurationManager.setConfiguration((Configuration) mockConfiguration.proxy());
        
        Dispatcher dispatcher = new MockDispatcher(new MockServletContext(), new HashMap<String, String>(), configurationManager);
        dispatcher.init();
        dispatcher.cleanup();
        
        mockInterceptor.verify();
        mockContainer.verify();
        mockConfiguration.verify();
    }

    public void testMultipartSupportEnabledByDefault() throws Exception {
        HttpServletRequest req = new MockHttpServletRequest();
        HttpServletResponse res = new MockHttpServletResponse();

        Dispatcher du = initDispatcher(Collections.<String, String>emptyMap());
        du.prepare(req, res);

        assertTrue(du.isMultipartSupportEnabled(req));
    }

    public void testIsMultipartRequest() throws Exception {
        MockHttpServletRequest req = new MockHttpServletRequest();
        HttpServletResponse res = new MockHttpServletResponse();

        req.setMethod("POST");
        Dispatcher du = initDispatcher(Collections.<String, String>emptyMap());
        du.prepare(req, res);

        req.setContentType("multipart/form-data");
        assertTrue(du.isMultipartRequest(req));

        req.setContentType("multipart/form-data; boundary=---------------------------207103069210263");
        assertTrue(du.isMultipartRequest(req));

        req.setContentType("multipart/form-data; boundary=---------------------------207103069210263;charset=UTF-8");
        assertTrue(du.isMultipartRequest(req));

        req.setContentType("multipart/form-data; boundary=---------------------------207103069210263;charset=ISO-8859-1");
        assertTrue(du.isMultipartRequest(req));

        req.setContentType("multipart/form-data; boundary=---------------------------207103069210263;charset=Windows-1250");
        assertTrue(du.isMultipartRequest(req));

        req.setContentType("multipart/form-data; boundary=---------------------------207103069210263;charset=US-ASCII");
        assertTrue(du.isMultipartRequest(req));

        req.setContentType("multipart/form-data; boundary=---------------------------207103069210263;charset=UTF-16LE");
        assertTrue(du.isMultipartRequest(req));
    }

    class InternalConfigurationManager extends ConfigurationManager {
    	public boolean destroyConfiguration = false;

        public InternalConfigurationManager(String name) {
            super(name);
        }

        @Override
    	public synchronized void destroyConfiguration() {
    		super.destroyConfiguration();
    		destroyConfiguration = true;
    	}
    }
    
    
    class DispatcherListenerState {
    	public boolean isInitialized = false;
    	public boolean isDestroyed = false;
    }

    public static class InnerDestroyableObjectFactory extends ObjectFactory implements ObjectFactoryDestroyable {
        public boolean destroyed = false;

        public InnerDestroyableObjectFactory() {
        }

        public void destroy() {
            destroyed = true;
        }
    }

}
