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

import java.util.HashMap;
import java.util.Locale;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.struts2.StrutsConstants;
import org.apache.struts2.StrutsTestCase;
import org.apache.struts2.dispatcher.FilterDispatcherTest.InnerDestroyableObjectFactory;
import org.apache.struts2.xwork2.ObjectFactory;
import org.apache.struts2.xwork2.config.Configuration;
import org.apache.struts2.xwork2.config.ConfigurationManager;
import org.apache.struts2.xwork2.config.entities.InterceptorMapping;
import org.apache.struts2.xwork2.config.entities.InterceptorStackConfig;
import org.apache.struts2.xwork2.config.entities.PackageConfig;
import org.apache.struts2.xwork2.inject.Container;
import org.apache.struts2.xwork2.interceptor.Interceptor;
import org.apache.struts2.xwork2.util.LocalizedTextUtil;
import org.springframework.mock.web.MockHttpServletRequest;
import org.springframework.mock.web.MockHttpServletResponse;
import org.springframework.mock.web.MockServletContext;

import com.mockobjects.dynamic.C;
import com.mockobjects.dynamic.Mock;

/**
 * Test case for Dispatcher.
 *
 */
public class DispatcherTest extends StrutsTestCase {

    public void testDefaultResurceBundlePropertyLoaded() throws Exception {
        Locale.setDefault(Locale.US); // force to US locale as we also have _de and _da properties

        // some i18n messages from xwork-messages.properties
        assertEquals(
                LocalizedTextUtil.findDefaultText("xwork.error.action.execution", Locale.US),
                "Error during Action invocation");

        // some i18n messages from struts-messages.properties
        assertEquals(
                LocalizedTextUtil.findDefaultText("struts.messages.error.uploading", Locale.US,
                        new Object[] { "some error messages" }),
                "Error uploading: some error messages");
    }

    public void testPrepareSetEncodingProperly() throws Exception {
        HttpServletRequest req = new MockHttpServletRequest();
        HttpServletResponse res = new MockHttpServletResponse();

        Dispatcher du = initDispatcher(new HashMap() {{
            put(StrutsConstants.STRUTS_I18N_ENCODING, "utf-8");
        }});
        du.prepare(req, res);

        assertEquals(req.getCharacterEncoding(), "utf-8");
    }

    public void testPrepareSetEncodingPropertyWithMultipartRequest() throws Exception {
        MockHttpServletRequest req = new MockHttpServletRequest();
        MockHttpServletResponse res = new MockHttpServletResponse();

        req.setContentType("multipart/form-data");
        Dispatcher du = initDispatcher(new HashMap() {{
            put(StrutsConstants.STRUTS_I18N_ENCODING, "utf-8");
        }});
        du.prepare(req, res);

        assertEquals("utf-8", req.getCharacterEncoding());
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
    	Dispatcher du = null;
    	InternalConfigurationManager configurationManager = new InternalConfigurationManager();
    	try {
    		du = new Dispatcher(new MockServletContext(), new HashMap<String, String>());
    		du.setConfigurationManager(configurationManager);
    		
    		du.init();
    		
            Dispatcher.setInstance(du);
            
            assertFalse(configurationManager.destroyConfiguration);
            
            du.cleanup();
            
            assertTrue(configurationManager.destroyConfiguration);
            
    	}
    	finally {
    		du.setInstance(null);
    	}
    }
    
    public void testObjectFactoryDestroy() throws Exception {

        final InnerDestroyableObjectFactory destroyedObjectFactory = new InnerDestroyableObjectFactory();
        Dispatcher du = new Dispatcher(new MockServletContext(), new HashMap<String, String>());
        ConfigurationManager cm = new ConfigurationManager();
        Mock mockConfiguration = new Mock(Configuration.class);
        cm.setConfiguration((Configuration)mockConfiguration.proxy());
        
        Mock mockContainer = new Mock(Container.class);
        mockConfiguration.expectAndReturn("getContainer", mockContainer.proxy());
        mockContainer.expectAndReturn("getInstance", C.args(C.eq(ObjectFactory.class)), destroyedObjectFactory);
        mockConfiguration.expect("destroy");
        mockConfiguration.matchAndReturn("getPackageConfigs", new HashMap<String, PackageConfig>());
        
        du.setConfigurationManager(cm);
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
        
        Mock mockConfiguration = new Mock(Configuration.class);
        mockConfiguration.matchAndReturn("getPackageConfigs", packageConfigs);
        mockConfiguration.matchAndReturn("getContainer", mockContainer.proxy());
        mockConfiguration.expect("destroy");
        
        ConfigurationManager configurationManager = new ConfigurationManager();
        configurationManager.setConfiguration((Configuration) mockConfiguration.proxy());
        
        Dispatcher dispatcher = new Dispatcher(new MockServletContext(), new HashMap<String, String>());
        dispatcher.setConfigurationManager(configurationManager);
        dispatcher.cleanup();
        
        mockInterceptor.verify();
        mockContainer.verify();
        mockConfiguration.verify();
    }
    
    class InternalConfigurationManager extends ConfigurationManager {
    	public boolean destroyConfiguration = false;
    	
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
}
