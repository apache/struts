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
import com.opensymphony.xwork2.ActionContext;
import com.opensymphony.xwork2.LocalizedTextProvider;
import com.opensymphony.xwork2.ObjectFactory;
import com.opensymphony.xwork2.StubValueStack;
import com.opensymphony.xwork2.config.Configuration;
import com.opensymphony.xwork2.config.ConfigurationManager;
import com.opensymphony.xwork2.config.entities.InterceptorMapping;
import com.opensymphony.xwork2.config.entities.InterceptorStackConfig;
import com.opensymphony.xwork2.config.entities.PackageConfig;
import com.opensymphony.xwork2.inject.Container;
import com.opensymphony.xwork2.interceptor.Interceptor;
import com.opensymphony.xwork2.mock.MockActionInvocation;
import com.opensymphony.xwork2.mock.MockActionProxy;
import org.apache.struts2.ServletActionContext;
import org.apache.struts2.StrutsConstants;
import org.apache.struts2.StrutsInternalTestCase;
import org.apache.struts2.dispatcher.mapper.ActionMapping;
import org.apache.struts2.dispatcher.multipart.MultiPartRequestWrapper;
import org.apache.struts2.util.ObjectFactoryDestroyable;
import org.springframework.mock.web.MockHttpServletRequest;
import org.springframework.mock.web.MockHttpServletResponse;
import org.springframework.mock.web.MockHttpSession;
import org.springframework.mock.web.MockServletContext;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Locale;
import java.util.Map;
import java.util.Set;

/**
 * Test case for Dispatcher.
 */
public class DispatcherTest extends StrutsInternalTestCase {

    public void testDefaultResourceBundlePropertyLoaded() {
        LocalizedTextProvider localizedTextProvider = container.getInstance(LocalizedTextProvider.class);

        // some i18n messages from xwork-messages.properties
        assertEquals(localizedTextProvider.findDefaultText("xwork.error.action.execution", Locale.US),
            "Error during Action invocation");

        // some i18n messages from struts-messages.properties
        assertEquals(localizedTextProvider.findDefaultText("struts.messages.error.uploading", Locale.US,
                new Object[]{"some error messages"}),
            "Error uploading: some error messages");
    }

    public void testPrepareSetEncodingProperly() {
        HttpServletRequest req = new MockHttpServletRequest();
        HttpServletResponse res = new MockHttpServletResponse();

        Dispatcher du = initDispatcher(new HashMap<String, String>() {{
            put(StrutsConstants.STRUTS_I18N_ENCODING, "utf-8");
        }});
        du.prepare(req, res);

        assertEquals(req.getCharacterEncoding(), "utf-8");
        assertEquals(res.getCharacterEncoding(), "utf-8");
    }

    public void testEncodingForXMLHttpRequest() {
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
        assertEquals(res.getCharacterEncoding(), "UTF-8");
    }

    public void testSetEncodingIfDiffer() {
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
        assertEquals(res.getCharacterEncoding(), "utf-8");
        mock.verify();
    }

    public void testPrepareSetEncodingPropertyWithMultipartRequest() {
        MockHttpServletRequest req = new MockHttpServletRequest();
        MockHttpServletResponse res = new MockHttpServletResponse();

        req.setContentType("multipart/form-data");
        Dispatcher du = initDispatcher(new HashMap<String, String>() {{
            put(StrutsConstants.STRUTS_I18N_ENCODING, "utf-8");
        }});
        du.prepare(req, res);

        assertEquals("utf-8", req.getCharacterEncoding());
        assertEquals("utf-8", res.getCharacterEncoding());
    }

    public void testPrepareMultipartRequest() throws Exception {
        MockHttpServletRequest req = new MockHttpServletRequest();
        MockHttpServletResponse res = new MockHttpServletResponse();

        req.setMethod("post");
        req.setContentType("multipart/form-data; boundary=asdcvb345asd");
        Dispatcher du = initDispatcher(Collections.emptyMap());
        du.prepare(req, res);
        HttpServletRequest wrapped = du.wrapRequest(req);

        assertTrue(wrapped instanceof MultiPartRequestWrapper);
    }

    public void testPrepareMultipartRequestAllAllowedCharacters() throws Exception {
        MockHttpServletRequest req = new MockHttpServletRequest();
        MockHttpServletResponse res = new MockHttpServletResponse();

        req.setMethod("post");
        req.setContentType("multipart/form-data; boundary=01=23a.bC:D((e)d'z?p+o_r,e-");
        Dispatcher du = initDispatcher(Collections.emptyMap());
        du.prepare(req, res);
        HttpServletRequest wrapped = du.wrapRequest(req);

        assertTrue(wrapped instanceof MultiPartRequestWrapper);
    }

    public void testPrepareMultipartRequestIllegalCharacter() throws Exception {
        MockHttpServletRequest req = new MockHttpServletRequest();
        MockHttpServletResponse res = new MockHttpServletResponse();

        req.setMethod("post");
        req.setContentType("multipart/form-data; boundary=01=2;3a.bC:D((e)d'z?p+o_r,e-");
        Dispatcher du = initDispatcher(Collections.emptyMap());
        du.prepare(req, res);
        HttpServletRequest wrapped = du.wrapRequest(req);

        assertFalse(wrapped instanceof MultiPartRequestWrapper);
    }

    public void testDispatcherListener() {

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

        Dispatcher du = initDispatcher(new HashMap<>());

        assertTrue(state.isInitialized);

        du.cleanup();

        assertTrue(state.isDestroyed);
    }

    public void testConfigurationManager() {
        Dispatcher du;
        final InternalConfigurationManager configurationManager = new InternalConfigurationManager(Container.DEFAULT_NAME);
        try {
            du = new MockDispatcher(new MockServletContext(), new HashMap<>(), configurationManager);
            du.init();
            Dispatcher.setInstance(du);

            assertFalse(configurationManager.destroyConfiguration);

            du.cleanup();

            assertTrue(configurationManager.destroyConfiguration);

        } finally {
            Dispatcher.setInstance(null);
        }
    }

    public void testInitLoadsDefaultConfig() {
        Dispatcher du = new Dispatcher(new MockServletContext(), new HashMap<>());
        du.init();
        Configuration config = du.getConfigurationManager().getConfiguration();
        assertNotNull(config);
        Set<String> expected = new HashSet<>();
        expected.add("struts-default.xml");
        expected.add("struts-beans.xml");
        expected.add("struts-excluded-classes.xml");
        expected.add("struts-plugin.xml");
        expected.add("struts.xml");
        expected.add("struts-deferred.xml");
        assertEquals(expected, config.getLoadedFileNames());
        assertTrue(config.getPackageConfigs().size() > 0);
        PackageConfig packageConfig = config.getPackageConfig("struts-default");
        assertTrue(packageConfig.getInterceptorConfigs().size() > 0);
        assertTrue(packageConfig.getResultTypeConfigs().size() > 0);
    }

    public void testObjectFactoryDestroy() {

        ConfigurationManager cm = new ConfigurationManager(Container.DEFAULT_NAME);
        Dispatcher du = new MockDispatcher(new MockServletContext(), new HashMap<>(), cm);
        Mock mockConfiguration = new Mock(Configuration.class);
        cm.setConfiguration((Configuration) mockConfiguration.proxy());

        Mock mockContainer = new Mock(Container.class);
        final InnerDestroyableObjectFactory destroyedObjectFactory = new InnerDestroyableObjectFactory();
        destroyedObjectFactory.setContainer((Container) mockContainer.proxy());
        mockContainer.expectAndReturn("getInstance", C.args(C.eq(ObjectFactory.class)), destroyedObjectFactory);

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

    public void testInterceptorDestroy() {
        Mock mockInterceptor = new Mock(Interceptor.class);
        mockInterceptor.matchAndReturn("hashCode", 0);
        mockInterceptor.expect("destroy");

        InterceptorMapping interceptorMapping = new InterceptorMapping("test", (Interceptor) mockInterceptor.proxy());

        InterceptorStackConfig isc = new InterceptorStackConfig.Builder("test").addInterceptor(interceptorMapping).build();

        PackageConfig packageConfig = new PackageConfig.Builder("test").addInterceptorStackConfig(isc).build();

        Map<String, PackageConfig> packageConfigs = new HashMap<>();
        packageConfigs.put("test", packageConfig);

        Mock mockContainer = new Mock(Container.class);
        mockContainer.matchAndReturn("getInstance", C.args(C.eq(ObjectFactory.class)), new ObjectFactory());

        Mock mockConfiguration = new Mock(Configuration.class);
        mockConfiguration.matchAndReturn("getPackageConfigs", packageConfigs);
        mockConfiguration.matchAndReturn("getContainer", mockContainer.proxy());
        mockConfiguration.expect("destroy");

        ConfigurationManager configurationManager = new ConfigurationManager(Container.DEFAULT_NAME);
        configurationManager.setConfiguration((Configuration) mockConfiguration.proxy());

        Dispatcher dispatcher = new MockDispatcher(new MockServletContext(), new HashMap<>(), configurationManager);
        dispatcher.init();
        dispatcher.cleanup();

        mockInterceptor.verify();
        mockContainer.verify();
        mockConfiguration.verify();
    }

    public void testMultipartSupportEnabledByDefault() {
        HttpServletRequest req = new MockHttpServletRequest();
        HttpServletResponse res = new MockHttpServletResponse();

        Dispatcher du = initDispatcher(Collections.emptyMap());
        du.prepare(req, res);

        assertTrue(du.isMultipartSupportEnabled(req));
    }

    public void testIsMultipartRequest() {
        MockHttpServletRequest req = new MockHttpServletRequest();
        HttpServletResponse res = new MockHttpServletResponse();

        req.setMethod("POST");
        Dispatcher du = initDispatcher(Collections.emptyMap());
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

        req.setContentType("multipart/form-data;boundary=---------------------------207103069210263;charset=UTF-16LE");
        assertTrue(du.isMultipartRequest(req));

        req.setContentType("multipart/form-data;boundary=---------------------------207103069210263; charset=UTF-16LE");
        assertTrue(du.isMultipartRequest(req));

        req.setContentType("multipart/form-data;boundary=---------------------------207103069210263 ;charset=UTF-16LE");
        assertTrue(du.isMultipartRequest(req));

        req.setContentType("multipart/form-data;boundary=---------------------------207103069210263 ; charset=UTF-16LE");
        assertTrue(du.isMultipartRequest(req));

        req.setContentType("multipart/form-data ;boundary=---------------------------207103069210263;charset=UTF-16LE");
        assertTrue(du.isMultipartRequest(req));

        req.setContentType("multipart/form-data ; boundary=---------------------------207103069210263;charset=UTF-16LE");
        assertTrue(du.isMultipartRequest(req));

        req.setContentType("Multipart/Form-Data ; boundary=---------------------------207103069210263;charset=UTF-16LE");
        assertTrue(du.isMultipartRequest(req));
    }

    public void testServiceActionResumePreviousProxy() throws Exception {
        Dispatcher du = initDispatcher(Collections.emptyMap());

        MockActionInvocation mai = new MockActionInvocation();
        ActionContext.getContext().withActionInvocation(mai);

        MockActionProxy actionProxy = new MockActionProxy();
        actionProxy.setInvocation(mai);
        mai.setProxy(actionProxy);

        mai.setStack(new StubValueStack());

        HttpServletRequest req = new MockHttpServletRequest();
        req.setAttribute(ServletActionContext.STRUTS_VALUESTACK_KEY, mai.getStack());

        assertFalse(actionProxy.isExecutedCalled());

        du.setDevMode("false");
        du.setHandleException("false");
        du.serviceAction(req, null, new ActionMapping());

        assertTrue("should execute previous proxy", actionProxy.isExecutedCalled());
    }

    public void testServiceActionCreatesNewProxyIfDifferentMapping() throws Exception {
        Dispatcher du = initDispatcher(Collections.emptyMap());
        container.inject(du);

        MockActionInvocation mai = new MockActionInvocation();
        ActionContext.getContext().withActionInvocation(mai);

        MockActionProxy previousActionProxy = new MockActionProxy();
        previousActionProxy.setActionName("first-action");
        previousActionProxy.setNamespace("namespace1");
        previousActionProxy.setInvocation(mai);
        mai.setProxy(previousActionProxy);

        mai.setStack(new StubValueStack());

        HttpServletRequest request = new MockHttpServletRequest();
        request.setAttribute(ServletActionContext.STRUTS_VALUESTACK_KEY, mai.getStack());

        HttpServletResponse response = new MockHttpServletResponse();

        assertFalse(previousActionProxy.isExecutedCalled());

        ActionMapping newActionMapping = new ActionMapping();
        newActionMapping.setName("hello");
        du.serviceAction(request, response, newActionMapping);

        assertFalse(previousActionProxy.isExecutedCalled());
    }

    /**
     * Verify proper default (true) handleExceptionState for Dispatcher and that
     * it properly reflects a manually configured change to false.
     */
    public void testHandleException() {
        Dispatcher du = initDispatcher(new HashMap<>());
        assertTrue("Default Dispatcher handleException state not true ?", du.isHandleException());

        Dispatcher du2 = initDispatcher(new HashMap<String, String>() {{
            put(StrutsConstants.STRUTS_HANDLE_EXCEPTION, "false");
        }});
        assertFalse("Modified Dispatcher handleException state not false ?", du2.isHandleException());
    }

    /**
     * Verify proper default (false) devMode for Dispatcher and that
     * it properly reflects a manually configured change to true.
     */
    public void testDevMode() {
        Dispatcher du = initDispatcher(new HashMap<>());
        assertFalse("Default Dispatcher devMode state not false ?", du.isDevMode());

        Dispatcher du2 = initDispatcher(new HashMap<String, String>() {{
            put(StrutsConstants.STRUTS_DEVMODE, "true");
        }});
        assertTrue("Modified Dispatcher devMode state not true ?", du2.isDevMode());
    }

    public void testGetLocale_With_DefaultLocale_FromConfiguration() {
        // Given
        Mock mock = new Mock(HttpServletRequest.class);
        MockHttpSession mockHttpSession = new MockHttpSession();
        mock.expectAndReturn("getCharacterEncoding", "utf-8");       // From Dispatcher prepare().
        mock.expectAndReturn("getHeader", "X-Requested-With", "");   // From Dispatcher prepare().
        mock.expectAndReturn("getParameterMap", new HashMap<String, Object>());  // From Dispatcher prepare().
        mock.expectAndReturn("getSession", false, mockHttpSession);  // From Dispatcher prepare().
        mock.expectAndReturn("getSession", true, mockHttpSession);   // From createTestContextMap().
        HttpServletRequest request = (HttpServletRequest) mock.proxy();
        HttpServletResponse response = new MockHttpServletResponse();

        Dispatcher testDispatcher = initDispatcher(new HashMap<String, String>() {{
            put(StrutsConstants.STRUTS_I18N_ENCODING, "utf-8");
            // Not setting a Struts Locale here, so we should receive the default "de_DE" from the test configuration.
        }});

        // When
        testDispatcher.prepare(request, response);
        ActionContext context = ActionContext.of(createTestContextMap(testDispatcher, request, response));

        // Then
        assertEquals(Locale.GERMANY, context.getLocale());  // Expect the Dispatcher defaultLocale value "de_DE" from the test configuration.
        mock.verify();
    }

    public void testGetLocale_With_DefaultLocale_fr_CA() {
        // Given
        Mock mock = new Mock(HttpServletRequest.class);
        MockHttpSession mockHttpSession = new MockHttpSession();
        mock.expectAndReturn("getCharacterEncoding", "utf-8");       // From Dispatcher prepare().
        mock.expectAndReturn("getHeader", "X-Requested-With", "");   // From Dispatcher prepare().
        mock.expectAndReturn("getParameterMap", new HashMap<String, Object>());  // From Dispatcher prepare().
        mock.expectAndReturn("getSession", false, mockHttpSession);  // From Dispatcher prepare().
        mock.expectAndReturn("getSession", true, mockHttpSession);   // From createTestContextMap().
        HttpServletRequest request = (HttpServletRequest) mock.proxy();
        HttpServletResponse response = new MockHttpServletResponse();

        Dispatcher testDispatcher = initDispatcher(new HashMap<String, String>() {{
            put(StrutsConstants.STRUTS_I18N_ENCODING, "utf-8");
            put(StrutsConstants.STRUTS_LOCALE, Locale.CANADA_FRENCH.toString());  // Set the Dispatcher defaultLocale to fr_CA.
        }});

        // When
        testDispatcher.prepare(request, response);
        ActionContext context = ActionContext.of(createTestContextMap(testDispatcher, request, response));

        // Then
        assertEquals(Locale.CANADA_FRENCH, context.getLocale());  // Expect the Dispatcher defaultLocale value.
        mock.verify();
    }

    public void testGetLocale_With_BadDefaultLocale_RequestLocale_en_UK() {
        // Given
        Mock mock = new Mock(HttpServletRequest.class);
        MockHttpSession mockHttpSession = new MockHttpSession();
        mock.expectAndReturn("getCharacterEncoding", "utf-8");       // From Dispatcher prepare().
        mock.expectAndReturn("getHeader", "X-Requested-With", "");   // From Dispatcher prepare().
        mock.expectAndReturn("getLocale", Locale.UK);                // From Dispatcher prepare().
        mock.expectAndReturn("getParameterMap", new HashMap<String, Object>());  // From Dispatcher prepare().
        mock.expectAndReturn("getSession", false, mockHttpSession);  // From Dispatcher prepare().
        mock.expectAndReturn("getSession", true, mockHttpSession);   // From createTestContextMap().
        mock.expectAndReturn("getLocale", Locale.UK);     // From createTestContextMap().
        HttpServletRequest request = (HttpServletRequest) mock.proxy();
        HttpServletResponse response = new MockHttpServletResponse();

        Dispatcher testDispatcher = initDispatcher(new HashMap<String, String>() {{
            put(StrutsConstants.STRUTS_I18N_ENCODING, "utf-8");
            put(StrutsConstants.STRUTS_LOCALE, "This_is_not_a_valid_Locale_string");  // Set Dispatcher defaultLocale to an invalid value.
        }});

        // When
        testDispatcher.prepare(request, response);
        ActionContext context = ActionContext.of(createTestContextMap(testDispatcher, request, response));

        // Then
        assertEquals(Locale.UK, context.getLocale());  // Expect the request set value from Mock.
        mock.verify();
    }

    public void testGetLocale_With_BadDefaultLocale_And_RuntimeException() {
        // Given
        Mock mock = new Mock(HttpServletRequest.class);
        MockHttpSession mockHttpSession = new MockHttpSession();
        mock.expectAndReturn("getCharacterEncoding", "utf-8");       // From Dispatcher prepare().
        mock.expectAndReturn("getHeader", "X-Requested-With", "");   // From Dispatcher prepare().
        mock.expectAndReturn("getLocale", Locale.UK);                // From Dispatcher prepare().
        mock.expectAndReturn("getParameterMap", new HashMap<String, Object>());  // From Dispatcher prepare().
        mock.expectAndReturn("getSession", false, mockHttpSession);  // From Dispatcher prepare().
        mock.expectAndReturn("getSession", true, mockHttpSession);   // From createTestContextMap().
        mock.expectAndThrow("getLocale", new IllegalStateException("Test theoretical state preventing HTTP Request Locale access"));  // From createTestContextMap().
        HttpServletRequest request = (HttpServletRequest) mock.proxy();
        HttpServletResponse response = new MockHttpServletResponse();

        Dispatcher testDispatcher = initDispatcher(new HashMap<String, String>() {{
            put(StrutsConstants.STRUTS_I18N_ENCODING, "utf-8");
            put(StrutsConstants.STRUTS_LOCALE, "This_is_not_a_valid_Locale_string");  // Set the Dispatcher defaultLocale to an invalid value.
        }});

        // When
        testDispatcher.prepare(request, response);
        ActionContext context = ActionContext.of(createTestContextMap(testDispatcher, request, response));

        // Then
        assertEquals(Locale.getDefault(), context.getLocale());  // Expect the system default value, when BOTH Dispatcher default Locale AND request access fail.
        mock.verify();
    }

    public void testGetLocale_With_NullDefaultLocale() {
        // Given
        Mock mock = new Mock(HttpServletRequest.class);
        MockHttpSession mockHttpSession = new MockHttpSession();
        mock.expectAndReturn("getCharacterEncoding", "utf-8");       // From Dispatcher prepare().
        mock.expectAndReturn("getHeader", "X-Requested-With", "");   // From Dispatcher prepare().
        mock.expectAndReturn("getLocale", Locale.CANADA_FRENCH);     // From Dispatcher prepare().
        mock.expectAndReturn("getParameterMap", new HashMap<String, Object>());  // From Dispatcher prepare().
        mock.expectAndReturn("getSession", false, mockHttpSession);  // From Dispatcher prepare().
        mock.expectAndReturn("getSession", true, mockHttpSession);   // From createTestContextMap().
        mock.expectAndReturn("getLocale", Locale.CANADA_FRENCH);     // From createTestContextMap().
        HttpServletRequest request = (HttpServletRequest) mock.proxy();
        HttpServletResponse response = new MockHttpServletResponse();

        Dispatcher testDispatcher = initDispatcher(new HashMap<String, String>() {{
            put(StrutsConstants.STRUTS_I18N_ENCODING, "utf-8");
            // Attempting to set StrutsConstants.STRUTS_LOCALE to null here via parameters causes an NPE.
        }});

        testDispatcher.setDefaultLocale(null);  // Force a null Struts default locale, otherwise we receive the default "de_DE" from the test configuration.

        // When
        testDispatcher.prepare(request, response);
        ActionContext context = ActionContext.of(createTestContextMap(testDispatcher, request, response));

        // Then
        assertEquals(Locale.CANADA_FRENCH, context.getLocale());  // Expect the request set value from Mock.
        mock.verify();
    }

    public void testGetLocale_With_NullDefaultLocale_And_RuntimeException() {
        // Given
        Mock mock = new Mock(HttpServletRequest.class);
        MockHttpSession mockHttpSession = new MockHttpSession();
        mock.expectAndReturn("getCharacterEncoding", "utf-8");       // From Dispatcher prepare().
        mock.expectAndReturn("getHeader", "X-Requested-With", "");   // From Dispatcher prepare().
        mock.expectAndReturn("getLocale", Locale.CANADA_FRENCH);     // From Dispatcher prepare().
        mock.expectAndReturn("getParameterMap", new HashMap<String, Object>());  // From Dispatcher prepare().
        mock.expectAndReturn("getSession", false, mockHttpSession);  // From Dispatcher prepare().
        mock.expectAndReturn("getSession", true, mockHttpSession);   // From createTestContextMap().
        mock.expectAndThrow("getLocale", new IllegalStateException("Test some theoretical state preventing HTTP Request Locale access"));  // From createTestContextMap().
        HttpServletRequest request = (HttpServletRequest) mock.proxy();
        HttpServletResponse response = new MockHttpServletResponse();

        Dispatcher testDispatcher = initDispatcher(new HashMap<String, String>() {{
            put(StrutsConstants.STRUTS_I18N_ENCODING, "utf-8");
            // Attempting to set StrutsConstants.STRUTS_LOCALE to null via parameters causes an NPE.
        }});

        testDispatcher.setDefaultLocale(null);  // Force a null Struts default locale, otherwise we receive the default "de_DE" from the test configuration.

        // When
        testDispatcher.prepare(request, response);
        ActionContext context = ActionContext.of(createTestContextMap(testDispatcher, request, response));

        // Then
        assertEquals(Locale.getDefault(), context.getLocale());  // Expect the system default value when Mock request access fails.
        mock.verify();
    }

    /**
     * Create a test context Map from a Dispatcher instance.
     * <p>
     * The method directly calls getParameterMap() and getSession(true) on the HttpServletRequest.
     * <p>
     * The method indirectly calls getLocale(request) on the Dispatcher instance, allowing a test of that code path.
     * The derived Struts Dispatcher Locale can be retrieved from the Map afterwards.
     */
    protected static Map<String, Object> createTestContextMap(Dispatcher dispatcher,
                                                              HttpServletRequest request, HttpServletResponse response) {
        if (dispatcher == null) {
            throw new IllegalArgumentException("Cannot create a test ContextMap from a null Dispatcher");
        }
        if (request == null) {
            throw new IllegalArgumentException("Cannot create a test ContextMap from a null HttpServletRequest");
        }
        if (response == null) {
            throw new IllegalArgumentException("Cannot create a test ContextMap from a null HttpServletResponse");
        }

        return dispatcher.createContextMap(new RequestMap(request),
            HttpParameters.create(request.getParameterMap()).build(),
            new SessionMap(request),
            new ApplicationMap(request.getSession(true).getServletContext()),
            request,
            response);
    }

    static class InternalConfigurationManager extends ConfigurationManager {
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


    static class DispatcherListenerState {
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
