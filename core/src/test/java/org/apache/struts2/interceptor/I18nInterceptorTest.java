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
package org.apache.struts2.interceptor;

import org.apache.struts2.action.Action;
import org.apache.struts2.ActionContext;
import org.apache.struts2.ActionInvocation;
import org.apache.struts2.locale.DefaultLocaleProviderFactory;
import org.apache.struts2.mock.MockActionInvocation;
import org.apache.struts2.mock.MockActionProxy;
import junit.framework.TestCase;
import org.apache.struts2.ServletActionContext;
import org.apache.struts2.StrutsStatics;
import org.apache.struts2.dispatcher.HttpParameters;
import org.easymock.EasyMock;
import org.easymock.IArgumentMatcher;
import org.springframework.mock.web.MockHttpServletRequest;
import org.springframework.mock.web.MockHttpSession;

import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletResponse;

import java.io.Serializable;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Locale;
import java.util.Map;

public class I18nInterceptorTest extends TestCase {

    private I18nInterceptor interceptor;
    private ActionInvocation mai;
    private ActionContext ac;
    private Map<String, Object> session;
    private MockHttpServletRequest request;

    public void testEmptyParamAndSession() throws Exception {
        interceptor.intercept(mai);
    }

    public void testNoSessionNoLocale() {
        request.setSession(null);
        try {
            interceptor.intercept(mai);
            assertTrue(true);
        } catch (Exception ignore) {
            fail("Shouldn't throw any exception!");
        }

        assertFalse("should have been removed",
                mai.getInvocationContext().getParameters().get(I18nInterceptor.DEFAULT_PARAMETER).isDefined());
        assertNull("should not be created", request.getSession(false));
        assertNull("should not be stored here", session.get(I18nInterceptor.DEFAULT_SESSION_ATTRIBUTE));
    }

    public void testNoSessionButLocale() {
        prepare(I18nInterceptor.DEFAULT_PARAMETER, "da_DK"); //prevents shouldStore to being false
        request.setSession(null);
        try {
            interceptor.intercept(mai);
            assertTrue(true);
        } catch (Exception ignore) {
            fail("Shouldn't throw any exception!");
        }

        assertFalse("should have been removed",
                mai.getInvocationContext().getParameters().get(I18nInterceptor.DEFAULT_PARAMETER).isDefined());
        assertNotNull("should be created", request.getSession(false));
        assertNotNull("should be stored here", session.get(I18nInterceptor.DEFAULT_SESSION_ATTRIBUTE));
    }

    public void testDefaultLocale() throws Exception {
        prepare(I18nInterceptor.DEFAULT_PARAMETER, "_"); // bad locale that would get us default locale instead
        interceptor.intercept(mai);

        assertFalse(mai.getInvocationContext().getParameters().get(I18nInterceptor.DEFAULT_PARAMETER).isDefined()); // should have been removed

        assertNotNull(session.get(I18nInterceptor.DEFAULT_SESSION_ATTRIBUTE)); // should be stored here
        assertEquals(Locale.getDefault(), session.get(I18nInterceptor.DEFAULT_SESSION_ATTRIBUTE)); // should create a locale object
    }

    public void testDenmarkLocale() throws Exception {
        prepare(I18nInterceptor.DEFAULT_PARAMETER, "da_DK");
        interceptor.intercept(mai);

        assertFalse(mai.getInvocationContext().getParameters().get(I18nInterceptor.DEFAULT_PARAMETER).isDefined()); // should have been removed

        Locale denmark = new Locale("da", "DK");
        assertNotNull(session.get(I18nInterceptor.DEFAULT_SESSION_ATTRIBUTE)); // should be stored here
        assertEquals(denmark, session.get(I18nInterceptor.DEFAULT_SESSION_ATTRIBUTE)); // should create a locale object
    }

    public void testDenmarkLocaleRequestOnly() throws Exception {
        prepare(I18nInterceptor.DEFAULT_REQUEST_ONLY_PARAMETER, "da_DK");
        interceptor.intercept(mai);

        assertFalse(mai.getInvocationContext().getParameters().get(I18nInterceptor.DEFAULT_PARAMETER).isDefined()); // should have been removed

        Locale denmark = new Locale("da", "DK");
        assertNull(session.get(I18nInterceptor.DEFAULT_SESSION_ATTRIBUTE)); // should be stored here
        assertEquals(denmark, mai.getInvocationContext().getLocale()); // should create a locale object
    }

    public void testLanguageOnlyLocale() throws Exception {
        prepare(I18nInterceptor.DEFAULT_PARAMETER, "da");
        interceptor.intercept(mai);

        assertFalse(mai.getInvocationContext().getParameters().get(I18nInterceptor.DEFAULT_PARAMETER).isDefined()); // should have been removed

        Locale denmark = new Locale("da");
        assertNotNull(session.get(I18nInterceptor.DEFAULT_SESSION_ATTRIBUTE)); // should be stored here
        assertEquals(denmark, session.get(I18nInterceptor.DEFAULT_SESSION_ATTRIBUTE)); // should create a locale object
    }

    public void testEmptyLocale() throws Exception {
        prepare(I18nInterceptor.DEFAULT_PARAMETER, "");
        interceptor.intercept(mai);

        assertFalse(mai.getInvocationContext().getParameters().get(I18nInterceptor.DEFAULT_PARAMETER).isDefined()); // should have been removed

        assertNotNull(session.get(I18nInterceptor.DEFAULT_SESSION_ATTRIBUTE)); // should be stored here
        assertEquals(Locale.getDefault(), session.get(I18nInterceptor.DEFAULT_SESSION_ATTRIBUTE)); // should create a locale object
    }

    public void testNotExistingLocale() throws Exception {
        prepare(I18nInterceptor.DEFAULT_PARAMETER, "ble");
        interceptor.intercept(mai);

        assertFalse(mai.getInvocationContext().getParameters().get(I18nInterceptor.DEFAULT_PARAMETER).isDefined()); // should have been removed

        assertNotNull(session.get(I18nInterceptor.DEFAULT_SESSION_ATTRIBUTE)); // should be stored here
        assertEquals(Locale.getDefault(), session.get(I18nInterceptor.DEFAULT_SESSION_ATTRIBUTE)); // should create a locale object
    }

    public void testTrimableLocaleString1() throws Exception {
        prepare(I18nInterceptor.DEFAULT_PARAMETER, "de\n");

        interceptor.intercept(mai);

        assertFalse(mai.getInvocationContext().getParameters().get(I18nInterceptor.DEFAULT_PARAMETER).isDefined()); // should have been removed
        assertNotNull(session.get(I18nInterceptor.DEFAULT_SESSION_ATTRIBUTE)); // should be stored here
        assertEquals(Locale.GERMAN, session.get(I18nInterceptor.DEFAULT_SESSION_ATTRIBUTE)); // should create a locale object
    }

    public void testTrimableLocaleString2() throws Exception {
        prepare(I18nInterceptor.DEFAULT_PARAMETER, "de ");

        interceptor.intercept(mai);

        assertFalse(mai.getInvocationContext().getParameters().get(I18nInterceptor.DEFAULT_PARAMETER).isDefined()); // should have been removed
        assertNotNull(session.get(I18nInterceptor.DEFAULT_SESSION_ATTRIBUTE)); // should be stored here
        assertEquals(Locale.GERMAN, session.get(I18nInterceptor.DEFAULT_SESSION_ATTRIBUTE)); // should create a locale object
    }

    public void testWithVariant() throws Exception {
        prepare(I18nInterceptor.DEFAULT_PARAMETER, "ja_JP_JP");
        interceptor.intercept(mai);

        assertFalse(mai.getInvocationContext().getParameters().get(I18nInterceptor.DEFAULT_PARAMETER).isDefined()); // should have been removed

        Locale variant = new Locale("ja", "JP", "JP");
        Locale locale = (Locale) session.get(I18nInterceptor.DEFAULT_SESSION_ATTRIBUTE);
        assertNotNull(locale); // should be stored here
        assertEquals(variant, locale);
        assertEquals("JP", locale.getVariant());
    }

    public void testWithVariantRequestOnly() throws Exception {
        prepare(I18nInterceptor.DEFAULT_REQUEST_ONLY_PARAMETER, "ja_JP_JP");
        interceptor.intercept(mai);

        assertFalse(mai.getInvocationContext().getParameters().get(I18nInterceptor.DEFAULT_PARAMETER).isDefined()); // should have been removed
        assertNull(session.get(I18nInterceptor.DEFAULT_SESSION_ATTRIBUTE));

        Locale variant = new Locale("ja", "JP", "JP");
        Locale locale = mai.getInvocationContext().getLocale();
        assertNotNull(locale); // should be stored here
        assertEquals(variant, locale);
        assertEquals("JP", locale.getVariant());
    }

    public void testRealLocaleObjectInParams() throws Exception {
        prepare(I18nInterceptor.DEFAULT_PARAMETER, Locale.CANADA_FRENCH);
        interceptor.intercept(mai);

        assertFalse(mai.getInvocationContext().getParameters().get(I18nInterceptor.DEFAULT_PARAMETER).isDefined()); // should have been removed

        assertNotNull(session.get(I18nInterceptor.DEFAULT_SESSION_ATTRIBUTE)); // should be stored here
        assertEquals(Locale.CANADA_FRENCH, session.get(I18nInterceptor.DEFAULT_SESSION_ATTRIBUTE)); // should create a locale object
    }

    public void testRealLocalesInParams() throws Exception {
        Locale[] locales = new Locale[]{Locale.CANADA_FRENCH};
        assertTrue(locales.getClass().isArray());
        prepare(I18nInterceptor.DEFAULT_PARAMETER, locales);
        interceptor.intercept(mai);

        assertFalse(mai.getInvocationContext().getParameters().get(I18nInterceptor.DEFAULT_PARAMETER).isDefined()); // should have been removed

        assertNotNull(session.get(I18nInterceptor.DEFAULT_SESSION_ATTRIBUTE)); // should be stored here
        assertEquals(Locale.CANADA_FRENCH, session.get(I18nInterceptor.DEFAULT_SESSION_ATTRIBUTE));
    }

    public void testSetParameterAndAttributeNames() throws Exception {
        // given
        prepare("world", Locale.CHINA);

        interceptor.setAttributeName("hello");
        interceptor.setParameterName("world");

        // when
        interceptor.intercept(mai);

        // then
        assertFalse(mai.getInvocationContext().getParameters().contains("world")); // should have been removed

        assertNotNull(session.get("hello")); // should be stored here
        assertEquals(Locale.CHINA, session.get("hello"));
    }

    public void testActionContextLocaleIsPreservedWhenNotOverridden() throws Exception {
        final Locale locale1 = Locale.TRADITIONAL_CHINESE;
        mai.getInvocationContext().withLocale(locale1);
        interceptor.intercept(mai);

        Locale locale = (Locale) session.get(I18nInterceptor.DEFAULT_SESSION_ATTRIBUTE);
        assertNull(locale); // should not be stored here
        locale = mai.getInvocationContext().getLocale();
        assertEquals(locale1, locale);
    }

    public void testCookieCreation() throws Exception {
        prepare(I18nInterceptor.DEFAULT_COOKIE_PARAMETER, "da_DK");

        final Cookie cookie = new Cookie(I18nInterceptor.DEFAULT_COOKIE_ATTRIBUTE, "da_DK");

        HttpServletResponse response = EasyMock.createMock(HttpServletResponse.class);
        response.addCookie(CookieMatcher.eqCookie(cookie));
        EasyMock.replay(response);

        ac.put(StrutsStatics.HTTP_RESPONSE, response);
        interceptor.setLocaleStorage(I18nInterceptor.Storage.COOKIE.name());
        interceptor.intercept(mai);

        EasyMock.verify(response);

        assertNull(session.get(I18nInterceptor.DEFAULT_SESSION_ATTRIBUTE)); // should not be stored here
        assertNull(session.get(I18nInterceptor.DEFAULT_SESSION_ATTRIBUTE)); // should not create a locale object
    }

    public void testAcceptLanguageBasedLocale() throws Exception {
        // given
        request.setPreferredLocales(Arrays.asList(new Locale("da_DK"), new Locale("pl")));
        interceptor.setLocaleStorage(null);
        interceptor.setSupportedLocale("en,pl");

        // when
        interceptor.intercept(mai);

        // then
        assertNull(session.get(I18nInterceptor.DEFAULT_SESSION_ATTRIBUTE)); // should not be stored here
        assertNull(session.get(I18nInterceptor.DEFAULT_SESSION_ATTRIBUTE)); // should not create a locale object
        assertEquals(new Locale("pl"), mai.getInvocationContext().getLocale());
    }

    public void testSupportedLocaleWithRequestLocale() throws Exception {
        // given - supportedLocale configured + request_locale param with SESSION storage
        request.setPreferredLocales(Arrays.asList(new Locale("en")));
        interceptor.setSupportedLocale("en,fr");
        prepare(I18nInterceptor.DEFAULT_PARAMETER, "fr");

        // when
        interceptor.intercept(mai);

        // then - request_locale wins over Accept-Language
        assertEquals(new Locale("fr"), session.get(I18nInterceptor.DEFAULT_SESSION_ATTRIBUTE));
        assertEquals(new Locale("fr"), mai.getInvocationContext().getLocale());
    }

    public void testSupportedLocaleRejectsUnsupportedRequestLocale() throws Exception {
        // given - request_locale=es but supportedLocale="en,fr"
        request.setPreferredLocales(Arrays.asList(new Locale("en")));
        interceptor.setSupportedLocale("en,fr");
        prepare(I18nInterceptor.DEFAULT_PARAMETER, "es");

        // when
        interceptor.intercept(mai);

        // then - es rejected, falls back to Accept-Language match (en)
        assertNull(session.get(I18nInterceptor.DEFAULT_SESSION_ATTRIBUTE));
        assertEquals(new Locale("en"), mai.getInvocationContext().getLocale());
    }

    public void testSupportedLocaleRevalidatesSessionLocale() throws Exception {
        // given - session has stored locale "de" but supportedLocale changed to "en,fr"
        session.put(I18nInterceptor.DEFAULT_SESSION_ATTRIBUTE, new Locale("de"));
        request.setPreferredLocales(Arrays.asList(new Locale("fr")));
        interceptor.setSupportedLocale("en,fr");

        // when
        interceptor.intercept(mai);

        // then - stored "de" rejected, falls back to Accept-Language match (fr)
        assertEquals(new Locale("fr"), mai.getInvocationContext().getLocale());
    }

    public void testSupportedLocaleWithCookieStorage() throws Exception {
        // given - supportedLocale configured + request_cookie_locale param with COOKIE storage
        prepare(I18nInterceptor.DEFAULT_COOKIE_PARAMETER, "fr");
        request.setPreferredLocales(Arrays.asList(new Locale("en")));
        interceptor.setSupportedLocale("en,fr");

        final Cookie cookie = new Cookie(I18nInterceptor.DEFAULT_COOKIE_ATTRIBUTE, "fr");
        HttpServletResponse response = EasyMock.createMock(HttpServletResponse.class);
        response.addCookie(CookieMatcher.eqCookie(cookie));
        EasyMock.replay(response);

        ac.put(StrutsStatics.HTTP_RESPONSE, response);
        interceptor.setLocaleStorage(I18nInterceptor.Storage.COOKIE.name());

        // when
        interceptor.intercept(mai);

        // then - request_cookie_locale=fr wins
        EasyMock.verify(response);
        assertEquals(new Locale("fr"), mai.getInvocationContext().getLocale());
    }

    public void testSupportedLocaleRejectsUnsupportedRequestCookieLocale() throws Exception {
        // given - request_cookie_locale=es but supportedLocale="en,fr"
        prepare(I18nInterceptor.DEFAULT_COOKIE_PARAMETER, "es");
        request.setPreferredLocales(Arrays.asList(new Locale("en")));
        interceptor.setSupportedLocale("en,fr");

        HttpServletResponse response = EasyMock.createStrictMock(HttpServletResponse.class);
        EasyMock.replay(response);

        ac.put(StrutsStatics.HTTP_RESPONSE, response);
        interceptor.setLocaleStorage(I18nInterceptor.Storage.COOKIE.name());

        // when
        interceptor.intercept(mai);

        // then - unsupported request_cookie_locale ignored, falls back to Accept-Language match
        EasyMock.verify(response);
        assertEquals(new Locale("en"), mai.getInvocationContext().getLocale());
    }

    public void testSupportedLocaleRevalidatesStoredCookieLocale() throws Exception {
        // given - cookie has stored "de" but supportedLocale changed to "en,fr"
        request.setCookies(new Cookie(I18nInterceptor.DEFAULT_COOKIE_ATTRIBUTE, "de"));
        request.setPreferredLocales(Arrays.asList(new Locale("it")));
        interceptor.setSupportedLocale("en,fr");

        HttpServletResponse response = EasyMock.createStrictMock(HttpServletResponse.class);
        EasyMock.replay(response);

        ac.put(StrutsStatics.HTTP_RESPONSE, response);
        interceptor.setLocaleStorage(I18nInterceptor.Storage.COOKIE.name());

        // when
        interceptor.intercept(mai);

        // then - stored "de" rejected and fallback locale from invocation context is used
        EasyMock.verify(response);
        assertEquals(Locale.US, mai.getInvocationContext().getLocale());
    }

    public void testRequestOnlyLocalePrecedenceWithSupportedLocale() throws Exception {
        // given - request_only_locale should win over Accept-Language match
        prepare(I18nInterceptor.DEFAULT_REQUEST_ONLY_PARAMETER, "fr");
        request.setPreferredLocales(Arrays.asList(new Locale("en")));
        interceptor.setSupportedLocale("en,fr");

        // when
        interceptor.intercept(mai);

        // then - request_only_locale applied and not persisted
        assertNull(session.get(I18nInterceptor.DEFAULT_SESSION_ATTRIBUTE));
        assertEquals(new Locale("fr"), mai.getInvocationContext().getLocale());
    }

    public void testAcceptLanguageBasedLocaleWithFallbackToDefault() throws Exception {
        // given
        request.setPreferredLocales(Arrays.asList(new Locale("da_DK"), new Locale("es")));

        interceptor.setLocaleStorage(null);
        interceptor.setSupportedLocale("en,pl");

        // when
        interceptor.intercept(mai);

        // then
        assertNull(session.get(I18nInterceptor.DEFAULT_SESSION_ATTRIBUTE)); // should not be stored here
        assertNull(session.get(I18nInterceptor.DEFAULT_SESSION_ATTRIBUTE)); // should not create a locale object
        assertEquals(Locale.US, mai.getInvocationContext().getLocale());
    }

    private void prepare(String key, Serializable value) {
        Map<String, Serializable> params = new HashMap<>();
        params.put(key, value);

        mai.getInvocationContext().withParameters(HttpParameters.create(params).build());
    }

    public void setUp() throws Exception {
        interceptor = new I18nInterceptor();
        interceptor.setLocaleProviderFactory(new DefaultLocaleProviderFactory());
        interceptor.init();
        session = new HashMap<>();

        ac = ActionContext.of()
                .bind()
                .withSession(session)
                .withParameters(HttpParameters.create().build());

        request = new MockHttpServletRequest();
        request.setSession(new MockHttpSession());
        ServletActionContext.setRequest(request);

        Action action = () -> Action.SUCCESS;

        MockActionProxy proxy = new MockActionProxy();
        proxy.setAction(action);
        proxy.setNamespace("i18n");
        proxy.setActionName("anAction");

        mai = new MockActionInvocation();
        ((MockActionInvocation) mai).setAction(action);
        ((MockActionInvocation) mai).setInvocationContext(ac);
        ((MockActionInvocation) mai).setProxy(proxy);
    }

    public void tearDown() throws Exception {
        interceptor.destroy();
        interceptor = null;
        ac = null;
        session = null;
        mai = null;
    }

    static class CookieMatcher implements IArgumentMatcher {

        private final Cookie expected;

        CookieMatcher(Cookie cookie) {
            expected = cookie;
        }

        public boolean matches(Object argument) {
            Cookie cookie = ((Cookie) argument);
            return
                    (cookie.getName().equals(expected.getName()) &&
                            cookie.getValue().equals(expected.getValue()));
        }

        public static Cookie eqCookie(Cookie ck) {
            EasyMock.reportMatcher(new CookieMatcher(ck));
            return null;
        }

        public void appendTo(StringBuffer buffer) {
            buffer
                    .append("Received")
                    .append(expected.getName())
                    .append("/")
                    .append(expected.getValue());
        }
    }

}
