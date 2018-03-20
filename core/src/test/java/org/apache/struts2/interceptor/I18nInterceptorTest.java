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

import com.opensymphony.xwork2.Action;
import com.opensymphony.xwork2.ActionContext;
import com.opensymphony.xwork2.ActionInvocation;
import com.opensymphony.xwork2.DefaultLocaleProviderFactory;
import com.opensymphony.xwork2.mock.MockActionInvocation;
import com.opensymphony.xwork2.mock.MockActionProxy;
import junit.framework.TestCase;
import org.apache.struts2.ServletActionContext;
import org.apache.struts2.StrutsStatics;
import org.apache.struts2.dispatcher.HttpParameters;
import org.easymock.EasyMock;
import org.easymock.IArgumentMatcher;
import org.springframework.mock.web.MockHttpServletRequest;
import org.springframework.mock.web.MockHttpSession;

import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletResponse;
import java.io.Serializable;
import java.util.HashMap;
import java.util.Locale;
import java.util.Map;

public class I18nInterceptorTest extends TestCase {

    private I18nInterceptor interceptor;
    private ActionInvocation mai;
    private ActionContext ac;
    private Map session;
    private MockHttpServletRequest request;

    public void testEmptyParamAndSession() throws Exception {
        interceptor.intercept(mai);
    }

    public void testNoSessionNoLocale() throws Exception {
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

    public void testNoSessionButLocale() throws Exception {
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
        assertNull("should not be created", request.getSession(false));
        assertNull("should not be stored here", session.get(I18nInterceptor.DEFAULT_SESSION_ATTRIBUTE));
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
        Locale[] locales = new Locale[] { Locale.CANADA_FRENCH };
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
        mai.getInvocationContext().setLocale(locale1);
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

    private void prepare(String key, Serializable value) {
        Map<String, Serializable> params = new HashMap<>();
        params.put(key, value);

        mai.getInvocationContext().setParameters(HttpParameters.create(params).build());
    }

    public void setUp() throws Exception {
        interceptor = new I18nInterceptor();
        interceptor.setLocaleProviderFactory(new DefaultLocaleProviderFactory());
        interceptor.init();
        session = new HashMap();

        Map<String, Object> ctx = new HashMap<String, Object>();
        ctx.put(ActionContext.PARAMETERS, HttpParameters.create().build());
        ctx.put(ActionContext.SESSION, session);

        ac = new ActionContext(ctx);

        ServletActionContext.setContext(ac);
        request = new MockHttpServletRequest();
        request.setSession(new MockHttpSession());
        ServletActionContext.setRequest(request);

        Action action = new Action() {
            public String execute() throws Exception {
                return SUCCESS;
            }
        };

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
        private Cookie expected;

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
