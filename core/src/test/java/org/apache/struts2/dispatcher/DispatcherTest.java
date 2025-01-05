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

import org.apache.struts2.ActionContext;
import org.apache.struts2.text.LocalizedTextProvider;
import org.apache.struts2.ObjectFactory;
import org.apache.struts2.StubValueStack;
import org.apache.struts2.config.ConfigurationException;
import org.apache.struts2.config.ConfigurationManager;
import org.apache.struts2.config.entities.InterceptorMapping;
import org.apache.struts2.config.entities.InterceptorStackConfig;
import org.apache.struts2.config.entities.PackageConfig;
import org.apache.struts2.inject.Container;
import org.apache.struts2.inject.ContainerBuilder;
import org.apache.struts2.interceptor.Interceptor;
import org.apache.struts2.mock.MockActionInvocation;
import org.apache.struts2.mock.MockActionProxy;
import org.apache.struts2.test.StubConfigurationProvider;
import org.apache.struts2.util.location.LocatableProperties;
import org.apache.struts2.ServletActionContext;
import org.apache.struts2.StrutsConstants;
import org.apache.struts2.StrutsJUnit4InternalTestCase;
import org.apache.struts2.dispatcher.mapper.ActionMapping;
import org.apache.struts2.dispatcher.multipart.MultiPartRequestWrapper;
import org.apache.struts2.util.ObjectFactoryDestroyable;
import org.junit.Test;
import org.springframework.mock.web.MockHttpServletRequest;
import org.springframework.mock.web.MockHttpServletResponse;
import org.springframework.mock.web.MockHttpSession;
import org.springframework.mock.web.MockServletContext;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

import java.nio.charset.StandardCharsets;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Locale;
import java.util.Map;
import java.util.Set;
import java.util.regex.Pattern;

import static java.nio.charset.StandardCharsets.UTF_8;
import static java.util.Collections.emptyMap;
import static java.util.Collections.singletonMap;
import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyBoolean;
import static org.mockito.Mockito.doReturn;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.spy;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

/**
 * Test case for Dispatcher.
 */
public class DispatcherTest extends StrutsJUnit4InternalTestCase {

    @Test
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

    @Test
    public void testPrepareSetEncodingProperly() {
        HttpServletRequest req = new MockHttpServletRequest();
        HttpServletResponse res = new MockHttpServletResponse();

        initDispatcher(singletonMap(StrutsConstants.STRUTS_I18N_ENCODING, UTF_8.name()));
        dispatcher.prepare(req, res);

        assertEquals(req.getCharacterEncoding(), UTF_8.name());
        assertEquals(res.getCharacterEncoding(), UTF_8.name());
    }

    @Test
    public void testEncodingForXMLHttpRequest() {
        // given
        MockHttpServletRequest req = new MockHttpServletRequest();
        req.addHeader("X-Requested-With", "XMLHttpRequest");
        req.setCharacterEncoding(UTF_8.name());
        HttpServletResponse res = new MockHttpServletResponse();

        initDispatcher(singletonMap(StrutsConstants.STRUTS_I18N_ENCODING, StandardCharsets.ISO_8859_1.name()));

        // when
        dispatcher.prepare(req, res);

        // then
        assertEquals(req.getCharacterEncoding(), UTF_8.name());
        assertEquals(res.getCharacterEncoding(), UTF_8.name());
    }

    @Test
    public void testSetEncodingIfDiffer() {
        // given
        HttpServletRequest req = mock(HttpServletRequest.class);
        when(req.getCharacterEncoding()).thenReturn(UTF_8.name());
        when(req.getHeader("X-Requested-With")).thenReturn("");
        HttpServletResponse res = new MockHttpServletResponse();

        initDispatcher(singletonMap(StrutsConstants.STRUTS_I18N_ENCODING, UTF_8.name()));

        // when
        dispatcher.prepare(req, res);

        // then
        assertEquals(UTF_8.name(), req.getCharacterEncoding());
        assertEquals(UTF_8.name(), res.getCharacterEncoding());
    }

    @Test
    public void testPrepareSetEncodingPropertyWithMultipartRequest() {
        MockHttpServletRequest req = new MockHttpServletRequest();
        MockHttpServletResponse res = new MockHttpServletResponse();

        req.setContentType("multipart/form-data");
        initDispatcher(singletonMap(StrutsConstants.STRUTS_I18N_ENCODING, UTF_8.name()));
        dispatcher.prepare(req, res);

        assertEquals(UTF_8.name(), req.getCharacterEncoding());
        assertEquals(UTF_8.name(), res.getCharacterEncoding());
    }

    @Test
    public void testPrepareMultipartRequest() throws Exception {
        MockHttpServletRequest req = new MockHttpServletRequest();
        MockHttpServletResponse res = new MockHttpServletResponse();

        req.setMethod("post");
        req.setContentType("multipart/form-data; boundary=asdcvb345asd");

        dispatcher.prepare(req, res);

        assertTrue(dispatcher.wrapRequest(req) instanceof MultiPartRequestWrapper);
    }

    @Test
    public void testPrepareMultipartRequestAllAllowedCharacters() throws Exception {
        MockHttpServletRequest req = new MockHttpServletRequest();
        MockHttpServletResponse res = new MockHttpServletResponse();

        req.setMethod("post");
        req.setContentType("multipart/form-data; boundary=01=23a.bC:D((e)d'z?p+o_r,e-");

        dispatcher.prepare(req, res);

        assertTrue(dispatcher.wrapRequest(req) instanceof MultiPartRequestWrapper);
    }

    @Test
    public void testPrepareMultipartRequestIllegalCharacter() throws Exception {
        MockHttpServletRequest req = new MockHttpServletRequest();
        MockHttpServletResponse res = new MockHttpServletResponse();

        req.setMethod("post");
        req.setContentType("multipart/form-data; boundary=01=2;3a.bC:D((e)d'z?p+o_r,e-");

        dispatcher.prepare(req, res);

        assertFalse(dispatcher.wrapRequest(req) instanceof MultiPartRequestWrapper);
    }

    @Test
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

        dispatcher.init();

        assertTrue(state.isInitialized);

        dispatcher.cleanup();

        assertTrue(state.isDestroyed);
    }

    @Test
    public void testConfigurationManager() {
        configurationManager = spy(new ConfigurationManager(Container.DEFAULT_NAME));
        dispatcher = spyDispatcherWithConfigurationManager(new Dispatcher(new MockServletContext(), emptyMap()), configurationManager);

        dispatcher.init();

        verify(configurationManager, never()).destroyConfiguration();

        dispatcher.cleanup();

        verify(configurationManager).destroyConfiguration();
    }

    @Test
    public void testInitLoadsDefaultConfig() {
        assertNotNull(configuration);
        Set<String> expected = new HashSet<>();
        expected.add("struts-default.xml");
        expected.add("struts-beans.xml");
        expected.add("struts-excluded-classes.xml");
        expected.add("struts-plugin.xml");
        expected.add("struts.xml");
        expected.add("struts-deferred.xml");
        assertEquals(expected, configuration.getLoadedFileNames());
        assertFalse(configuration.getPackageConfigs().isEmpty());
        PackageConfig packageConfig = configuration.getPackageConfig("struts-default");
        assertFalse(packageConfig.getInterceptorConfigs().isEmpty());
        assertFalse(packageConfig.getResultTypeConfigs().isEmpty());
    }

    @Test
    public void testObjectFactoryDestroy() {
        InnerDestroyableObjectFactory destroyedObjectFactory = new InnerDestroyableObjectFactory();
        dispatcher.setObjectFactory(destroyedObjectFactory);

        assertFalse(destroyedObjectFactory.destroyed);
        dispatcher.cleanup();
        assertTrue(destroyedObjectFactory.destroyed);
    }

    @Test
    public void testInterceptorDestroy() {
        Interceptor mockedInterceptor = mock(Interceptor.class);
        InterceptorMapping interceptorMapping = new InterceptorMapping("test", mockedInterceptor);
        InterceptorStackConfig isc = new InterceptorStackConfig.Builder("test").addInterceptor(interceptorMapping).build();
        PackageConfig packageConfig = new PackageConfig.Builder("test").addInterceptorStackConfig(isc).build();

        configurationManager = spy(new ConfigurationManager(Container.DEFAULT_NAME));
        dispatcher = spyDispatcherWithConfigurationManager(new Dispatcher(new MockServletContext(), emptyMap()), configurationManager);

        dispatcher.init();

        configuration = spy(configurationManager.getConfiguration());
        configurationManager.setConfiguration(configuration);
        when(configuration.getPackageConfigs()).thenReturn(singletonMap("test", packageConfig));

        dispatcher.cleanup();

        verify(mockedInterceptor).destroy();
        verify(configuration).destroy();
    }

    @Test
    public void testMultipartSupportEnabledByDefault() {
        HttpServletRequest req = new MockHttpServletRequest();
        HttpServletResponse res = new MockHttpServletResponse();

        dispatcher.prepare(req, res);

        assertTrue(dispatcher.isMultipartSupportEnabled(req));
    }

    @Test
    public void testIsMultipartRequest() {
        MockHttpServletRequest req = new MockHttpServletRequest();
        HttpServletResponse res = new MockHttpServletResponse();

        req.setMethod("POST");

        dispatcher.prepare(req, res);

        req.setContentType("multipart/form-data");
        assertTrue(dispatcher.isMultipartRequest(req));

        req.setContentType("multipart/form-data; boundary=---------------------------207103069210263");
        assertTrue(dispatcher.isMultipartRequest(req));

        req.setContentType("multipart/form-data; boundary=---------------------------207103069210263;charset=UTF-8");
        assertTrue(dispatcher.isMultipartRequest(req));

        req.setContentType("multipart/form-data; boundary=---------------------------207103069210263;charset=ISO-8859-1");
        assertTrue(dispatcher.isMultipartRequest(req));

        req.setContentType("multipart/form-data; boundary=---------------------------207103069210263;charset=Windows-1250");
        assertTrue(dispatcher.isMultipartRequest(req));

        req.setContentType("multipart/form-data; boundary=---------------------------207103069210263;charset=US-ASCII");
        assertTrue(dispatcher.isMultipartRequest(req));

        req.setContentType("multipart/form-data; boundary=---------------------------207103069210263;charset=UTF-16LE");
        assertTrue(dispatcher.isMultipartRequest(req));

        req.setContentType("multipart/form-data;boundary=---------------------------207103069210263;charset=UTF-16LE");
        assertTrue(dispatcher.isMultipartRequest(req));

        req.setContentType("multipart/form-data;boundary=---------------------------207103069210263; charset=UTF-16LE");
        assertTrue(dispatcher.isMultipartRequest(req));

        req.setContentType("multipart/form-data;boundary=---------------------------207103069210263 ;charset=UTF-16LE");
        assertTrue(dispatcher.isMultipartRequest(req));

        req.setContentType("multipart/form-data;boundary=---------------------------207103069210263 ; charset=UTF-16LE");
        assertTrue(dispatcher.isMultipartRequest(req));

        req.setContentType("multipart/form-data ;boundary=---------------------------207103069210263;charset=UTF-16LE");
        assertTrue(dispatcher.isMultipartRequest(req));

        req.setContentType("multipart/form-data ; boundary=---------------------------207103069210263;charset=UTF-16LE");
        assertTrue(dispatcher.isMultipartRequest(req));

        req.setContentType("Multipart/Form-Data ; boundary=---------------------------207103069210263;charset=UTF-16LE");
        assertTrue(dispatcher.isMultipartRequest(req));

        req.setContentType("multipart/form-data; boundary=\"----=_Part_38_1092302434.1734807780737\"");
        assertTrue(dispatcher.isMultipartRequest(req));
    }

    @Test
    public void testServiceActionResumePreviousProxy() throws Exception {
        MockActionInvocation mai = new MockActionInvocation();
        ActionContext.getContext().withActionInvocation(mai);

        MockActionProxy actionProxy = new MockActionProxy();
        actionProxy.setInvocation(mai);
        mai.setProxy(actionProxy);

        mai.setStack(new StubValueStack());

        HttpServletRequest req = new MockHttpServletRequest();
        req.setAttribute(ServletActionContext.STRUTS_VALUESTACK_KEY, mai.getStack());

        assertFalse(actionProxy.isExecutedCalled());

        dispatcher.setDevMode("false");
        dispatcher.setHandleException("false");
        dispatcher.serviceAction(req, null, new ActionMapping());

        assertTrue("should execute previous proxy", actionProxy.isExecutedCalled());
    }

    @Test
    public void testServiceActionCreatesNewProxyIfDifferentMapping() throws Exception {
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
        dispatcher.serviceAction(request, response, newActionMapping);

        assertFalse(previousActionProxy.isExecutedCalled());
    }

    /**
     * Verify proper default (true) handleExceptionState for Dispatcher and that
     * it properly reflects a manually configured change to false.
     */
    @Test
    public void testHandleException() {
        assertTrue("Default Dispatcher handleException state not true ?", dispatcher.isHandleException());

        initDispatcher(singletonMap(StrutsConstants.STRUTS_HANDLE_EXCEPTION, "false"));
        assertFalse("Modified Dispatcher handleException state not false ?", dispatcher.isHandleException());
    }

    /**
     * Verify proper default (false) devMode for Dispatcher and that
     * it properly reflects a manually configured change to true.
     */
    @Test
    public void testDevMode() {
        assertFalse("Default Dispatcher devMode state not false ?", dispatcher.isDevMode());

        initDispatcher(singletonMap(StrutsConstants.STRUTS_DEVMODE, "true"));
        assertTrue("Modified Dispatcher devMode state not true ?", dispatcher.isDevMode());
    }

    @Test
    public void testGetLocale_With_DefaultLocale_FromConfiguration() {
        // Given
        HttpServletRequest request = mock(HttpServletRequest.class);
        MockHttpSession mockHttpSession = new MockHttpSession();
        when(request.getCharacterEncoding()).thenReturn(UTF_8.name());
        when(request.getHeader("X-Requested-With")).thenReturn("");
        when(request.getParameterMap()).thenReturn(emptyMap());
        when(request.getSession(anyBoolean())).thenReturn(mockHttpSession);
        HttpServletResponse response = new MockHttpServletResponse();

        // Not setting a Struts Locale here, so we should receive the default "de_DE" from the test configuration.
        initDispatcher(singletonMap(StrutsConstants.STRUTS_I18N_ENCODING, UTF_8.name()));

        // When
        dispatcher.prepare(request, response);
        ActionContext context = ActionContext.of(createTestContextMap(dispatcher, request, response));

        // Then
        assertEquals(Locale.GERMANY, context.getLocale());  // Expect the Dispatcher defaultLocale value "de_DE" from the test configuration.
    }

    @Test
    public void testGetLocale_With_DefaultLocale_fr_CA() {
        // Given
        HttpServletRequest request = mock(HttpServletRequest.class);
        MockHttpSession mockHttpSession = new MockHttpSession();
        when(request.getCharacterEncoding()).thenReturn(UTF_8.name());
        when(request.getHeader("X-Requested-With")).thenReturn("");
        when(request.getParameterMap()).thenReturn(emptyMap());
        when(request.getSession(anyBoolean())).thenReturn(mockHttpSession);
        HttpServletResponse response = new MockHttpServletResponse();

        initDispatcher(new HashMap<String, String>() {{
            put(StrutsConstants.STRUTS_I18N_ENCODING, UTF_8.name());
            put(StrutsConstants.STRUTS_LOCALE, Locale.CANADA_FRENCH.toString());  // Set the Dispatcher defaultLocale to fr_CA.
        }});

        // When
        dispatcher.prepare(request, response);
        ActionContext context = ActionContext.of(createTestContextMap(dispatcher, request, response));

        // Then
        assertEquals(Locale.CANADA_FRENCH, context.getLocale());  // Expect the Dispatcher defaultLocale value.
    }

    @Test
    public void testGetLocale_With_BadDefaultLocale_RequestLocale_en_UK() {
        // Given
        HttpServletRequest request = mock(HttpServletRequest.class);
        MockHttpSession mockHttpSession = new MockHttpSession();
        when(request.getCharacterEncoding()).thenReturn(UTF_8.name());
        when(request.getHeader("X-Requested-With")).thenReturn("");
        when(request.getParameterMap()).thenReturn(emptyMap());
        when(request.getSession(anyBoolean())).thenReturn(mockHttpSession);
        when(request.getLocale()).thenReturn(Locale.UK);
        HttpServletResponse response = new MockHttpServletResponse();

        initDispatcher(new HashMap<String, String>() {{
            put(StrutsConstants.STRUTS_I18N_ENCODING, UTF_8.name());
            put(StrutsConstants.STRUTS_LOCALE, "This_is_not_a_valid_Locale_string");  // Set Dispatcher defaultLocale to an invalid value.
        }});

        // When
        dispatcher.prepare(request, response);
        ActionContext context = ActionContext.of(createTestContextMap(dispatcher, request, response));

        // Then
        assertEquals(Locale.UK, context.getLocale());  // Expect the request set value from Mock.
    }

    @Test
    public void testGetLocale_With_BadDefaultLocale_And_RuntimeException() {
        // Given
        HttpServletRequest request = mock(HttpServletRequest.class);
        MockHttpSession mockHttpSession = new MockHttpSession();
        when(request.getCharacterEncoding()).thenReturn(UTF_8.name());
        when(request.getHeader("X-Requested-With")).thenReturn("");
        when(request.getParameterMap()).thenReturn(emptyMap());
        when(request.getSession(anyBoolean())).thenReturn(mockHttpSession);
        when(request.getLocale()).thenReturn(Locale.UK);
        HttpServletResponse response = new MockHttpServletResponse();

        initDispatcher(new HashMap<String, String>() {{
            put(StrutsConstants.STRUTS_I18N_ENCODING, UTF_8.name());
            put(StrutsConstants.STRUTS_LOCALE, "This_is_not_a_valid_Locale_string");  // Set the Dispatcher defaultLocale to an invalid value.
        }});

        // When
        dispatcher.prepare(request, response);
        when(request.getLocale()).thenThrow(new IllegalStateException("Test theoretical state preventing HTTP Request Locale access"));
        ActionContext context = ActionContext.of(createTestContextMap(dispatcher, request, response));

        // Then
        assertEquals(Locale.getDefault(), context.getLocale());  // Expect the system default value, when BOTH Dispatcher default Locale AND request access fail.
    }

    @Test
    public void testGetLocale_With_NullDefaultLocale() {
        // Given
        HttpServletRequest request = mock(HttpServletRequest.class);
        MockHttpSession mockHttpSession = new MockHttpSession();
        when(request.getCharacterEncoding()).thenReturn(UTF_8.name());
        when(request.getHeader("X-Requested-With")).thenReturn("");
        when(request.getParameterMap()).thenReturn(emptyMap());
        when(request.getSession(anyBoolean())).thenReturn(mockHttpSession);
        when(request.getLocale()).thenReturn(Locale.CANADA_FRENCH);
        HttpServletResponse response = new MockHttpServletResponse();

        // Attempting to set StrutsConstants.STRUTS_LOCALE to null here via parameters causes an NPE.
        initDispatcher(singletonMap(StrutsConstants.STRUTS_I18N_ENCODING, UTF_8.name()));

        dispatcher.setDefaultLocale(null);  // Force a null Struts default locale, otherwise we receive the default "de_DE" from the test configuration.

        // When
        dispatcher.prepare(request, response);
        ActionContext context = ActionContext.of(createTestContextMap(dispatcher, request, response));

        // Then
        assertEquals(Locale.CANADA_FRENCH, context.getLocale());  // Expect the request set value from Mock.
    }

    @Test
    public void testGetLocale_With_NullDefaultLocale_And_RuntimeException() {
        // Given
        HttpServletRequest request = mock(HttpServletRequest.class);
        MockHttpSession mockHttpSession = new MockHttpSession();
        when(request.getCharacterEncoding()).thenReturn(UTF_8.name());
        when(request.getHeader("X-Requested-With")).thenReturn("");
        when(request.getParameterMap()).thenReturn(emptyMap());
        when(request.getSession(anyBoolean())).thenReturn(mockHttpSession);
        when(request.getLocale()).thenReturn(Locale.CANADA_FRENCH);
        HttpServletResponse response = new MockHttpServletResponse();

        // Attempting to set StrutsConstants.STRUTS_LOCALE to null via parameters causes an NPE.
        initDispatcher(singletonMap(StrutsConstants.STRUTS_I18N_ENCODING, UTF_8.name()));

        dispatcher.setDefaultLocale(null);  // Force a null Struts default locale, otherwise we receive the default "de_DE" from the test configuration.

        // When
        dispatcher.prepare(request, response);
        when(request.getLocale()).thenThrow(new IllegalStateException("Test theoretical state preventing HTTP Request Locale access"));
        ActionContext context = ActionContext.of(createTestContextMap(dispatcher, request, response));

        // Then
        assertEquals(Locale.getDefault(), context.getLocale());  // Expect the system default value when Mock request access fails.
    }

    @Test
    public void dispatcherReinjectedAfterReload() {
        HttpServletRequest request = mock(HttpServletRequest.class);
        HttpServletResponse response = mock(HttpServletResponse.class);

        dispatcher.prepare(request, response);

        assertEquals(Locale.GERMANY, dispatcher.getLocale(request));

        configurationManager.addContainerProvider(new StubConfigurationProvider() {
            @Override
            public void register(ContainerBuilder builder,
                                 LocatableProperties props) throws ConfigurationException {
                props.setProperty(StrutsConstants.STRUTS_LOCALE, "fr_CA");
            }
        });
        configurationManager.reload();
        dispatcher.cleanUpRequest(request);
        dispatcher.prepare(request, response);

        assertEquals(Locale.CANADA_FRENCH, dispatcher.getLocale(request));
    }

    @Test
    public void testExcludePatterns() {
        initDispatcher(singletonMap(StrutsConstants.STRUTS_ACTION_EXCLUDE_PATTERN, "/ns1/.*\\.json,/ns2/.*\\.json"));

        assertThat(dispatcher.getActionExcludedPatterns()).extracting(Pattern::toString).containsOnly(
                "/ns1/.*\\.json",
                "/ns2/.*\\.json"
        );
    }

    @Test
    public void testExcludePatternsUsingCustomSeparator() {
        Map<String, String> props = new HashMap<>();
        props.put(StrutsConstants.STRUTS_ACTION_EXCLUDE_PATTERN, "/ns1/[a-z]{1,10}.json///ns2/[a-z]{1,10}.json");
        props.put(StrutsConstants.STRUTS_ACTION_EXCLUDE_PATTERN_SEPARATOR, "//");

        initDispatcher(props);

        assertThat(dispatcher.getActionExcludedPatterns()).extracting(Pattern::toString).containsOnly(
                "/ns1/[a-z]{1,10}.json",
                "/ns2/[a-z]{1,10}.json"
        );
    }

    public static Dispatcher spyDispatcherWithConfigurationManager(Dispatcher dispatcher, ConfigurationManager configurationManager) {
        Dispatcher spiedDispatcher = spy(dispatcher);
        doReturn(configurationManager).when(spiedDispatcher).createConfigurationManager(any());
        return spiedDispatcher;
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
