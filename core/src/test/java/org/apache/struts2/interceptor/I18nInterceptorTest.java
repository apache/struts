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
package org.apache.struts2.interceptor;

import com.opensymphony.xwork2.Action;
import com.opensymphony.xwork2.ActionContext;
import com.opensymphony.xwork2.ActionInvocation;
import com.opensymphony.xwork2.mock.MockActionInvocation;
import org.apache.struts2.StrutsStatics;
import org.easymock.EasyMock;
import org.easymock.IArgumentMatcher;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletResponse;
import java.util.HashMap;
import java.util.Locale;
import java.util.Map;

import static org.jmock.expectation.AssertMo.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertTrue;

public class I18nInterceptorTest {
    private I18nInterceptor interceptor;
    private ActionInvocation mai;
    private ActionContext ac;
    private Map<String, Object> params;
    private Map session;

    @Before
    public void setUp() throws Exception {
        interceptor = new I18nInterceptor();
        interceptor.init();
        params = new HashMap<String, Object>();
        session = new HashMap();

        Map<String, Object> ctx = new HashMap<String, Object>();
        ctx.put(ActionContext.PARAMETERS, params);
        ctx.put(ActionContext.SESSION, session);
        ac = new ActionContext(ctx);

        Action action = new Action() {
            public String execute() throws Exception {
                return SUCCESS;
            }
        };
        mai = new MockActionInvocation();
        ((MockActionInvocation) mai).setAction(action);
        ((MockActionInvocation) mai).setInvocationContext(ac);
    }

    @After
    public void tearDown() throws Exception {
        interceptor.destroy();
        interceptor = null;
        ac = null;
        params = null;
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

    @Test
    public void testCookieCreation() throws Exception {

        params.put(I18nInterceptor.DEFAULT_COOKIE_PARAMETER, "da_DK");

        final Cookie cookie = new Cookie(I18nInterceptor.DEFAULT_COOKIE_ATTRIBUTE, "da_DK");

        HttpServletResponse response = EasyMock.createMock(HttpServletResponse.class);
        response.addCookie(CookieMatcher.eqCookie(cookie));
        EasyMock.replay(response);

        ac.put(StrutsStatics.HTTP_RESPONSE, response);
        interceptor.intercept(mai);

        EasyMock.verify(response);

        Locale denmark = new Locale("da", "DK");
        assertNotNull(session.get(I18nInterceptor.DEFAULT_SESSION_ATTRIBUTE)); // should be stored here
        assertEquals(denmark, session.get(I18nInterceptor.DEFAULT_SESSION_ATTRIBUTE)); // should create a locale object
    }

    @Test
    public void testNoSession() throws Exception {
        ac.setSession(null);
        interceptor.intercept(mai);
    }

    @Test
    public void testDefaultLocale() throws Exception {
        params.put(I18nInterceptor.DEFAULT_PARAMETER, "_"); // bad locale that would get us default locale instead
        interceptor.intercept(mai);

        assertNull(params.get(I18nInterceptor.DEFAULT_PARAMETER)); // should have been removed

        assertNotNull(session.get(I18nInterceptor.DEFAULT_SESSION_ATTRIBUTE)); // should be stored here
        assertEquals(Locale.getDefault(), session.get(I18nInterceptor.DEFAULT_SESSION_ATTRIBUTE)); // should create a locale object
    }

    @Test
    public void testDenmarkLocale() throws Exception {
        params.put(I18nInterceptor.DEFAULT_PARAMETER, "da_DK");
        interceptor.intercept(mai);

        assertNull(params.get(I18nInterceptor.DEFAULT_PARAMETER)); // should have been removed

        Locale denmark = new Locale("da", "DK");
        assertNotNull(session.get(I18nInterceptor.DEFAULT_SESSION_ATTRIBUTE)); // should be stored here
        assertEquals(denmark, session.get(I18nInterceptor.DEFAULT_SESSION_ATTRIBUTE)); // should create a locale object
    }

    @Test
    public void testDenmarkLocaleRequestOnly() throws Exception {
        params.put(I18nInterceptor.DEFAULT_REQUESTONLY_PARAMETER, "da_DK");
        interceptor.intercept(mai);

        assertNull(params.get(I18nInterceptor.DEFAULT_PARAMETER)); // should have been removed

        Locale denmark = new Locale("da", "DK");
        assertNull(session.get(I18nInterceptor.DEFAULT_SESSION_ATTRIBUTE)); // should be stored here
        assertEquals(denmark, mai.getInvocationContext().getLocale()); // should create a locale object
    }

    @Test
    public void testCountryOnlyLocale() throws Exception {
        params.put(I18nInterceptor.DEFAULT_PARAMETER, "NL");
        interceptor.intercept(mai);

        assertNull(params.get(I18nInterceptor.DEFAULT_PARAMETER)); // should have been removed

        Locale denmark = new Locale("NL");
        assertNotNull(session.get(I18nInterceptor.DEFAULT_SESSION_ATTRIBUTE)); // should be stored here
        assertEquals(denmark, session.get(I18nInterceptor.DEFAULT_SESSION_ATTRIBUTE)); // should create a locale object
    }

    @Test
    public void testLanguageOnlyLocale() throws Exception {
        params.put(I18nInterceptor.DEFAULT_PARAMETER, "da_");
        interceptor.intercept(mai);

        assertNull(params.get(I18nInterceptor.DEFAULT_PARAMETER)); // should have been removed

        Locale denmark = new Locale("da");
        assertNotNull(session.get(I18nInterceptor.DEFAULT_SESSION_ATTRIBUTE)); // should be stored here
        assertEquals(denmark, session.get(I18nInterceptor.DEFAULT_SESSION_ATTRIBUTE)); // should create a locale object
    }

    @Test
    public void testWithVariant() throws Exception {
        params.put(I18nInterceptor.DEFAULT_PARAMETER, "ja_JP_JP");
        interceptor.intercept(mai);

        assertNull(params.get(I18nInterceptor.DEFAULT_PARAMETER)); // should have been removed

        Locale variant = new Locale("ja", "JP", "JP");
        Locale locale = (Locale) session.get(I18nInterceptor.DEFAULT_SESSION_ATTRIBUTE);
        assertNotNull(locale); // should be stored here
        assertEquals(variant, locale);
        assertEquals("JP", locale.getVariant());
    }

    @Test
    public void testWithVariantRequestOnly() throws Exception {
        params.put(I18nInterceptor.DEFAULT_REQUESTONLY_PARAMETER, "ja_JP_JP");
        interceptor.intercept(mai);

        assertNull(params.get(I18nInterceptor.DEFAULT_PARAMETER)); // should have been removed
        assertNull(session.get(I18nInterceptor.DEFAULT_SESSION_ATTRIBUTE));

        Locale variant = new Locale("ja", "JP", "JP");
        Locale locale = mai.getInvocationContext().getLocale();
        assertNotNull(locale); // should be stored here
        assertEquals(variant, locale);
        assertEquals("JP", locale.getVariant());
    }

    @Test
    public void testRealLocaleObjectInParams() throws Exception {
        params.put(I18nInterceptor.DEFAULT_PARAMETER, Locale.CANADA_FRENCH);
        interceptor.intercept(mai);

        assertNull(params.get(I18nInterceptor.DEFAULT_PARAMETER)); // should have been removed

        assertNotNull(session.get(I18nInterceptor.DEFAULT_SESSION_ATTRIBUTE)); // should be stored here
        assertEquals(Locale.CANADA_FRENCH, session.get(I18nInterceptor.DEFAULT_SESSION_ATTRIBUTE)); // should create a locale object
    }

    @Test
    public void testRealLocalesInParams() throws Exception {
        Locale[] locales = new Locale[]{Locale.CANADA_FRENCH};
        assertTrue(locales.getClass().isArray());
        params.put(I18nInterceptor.DEFAULT_PARAMETER, locales);
        interceptor.intercept(mai);

        assertNull(params.get(I18nInterceptor.DEFAULT_PARAMETER)); // should have been removed

        assertNotNull(session.get(I18nInterceptor.DEFAULT_SESSION_ATTRIBUTE)); // should be stored here
        assertEquals(Locale.CANADA_FRENCH, session.get(I18nInterceptor.DEFAULT_SESSION_ATTRIBUTE));
    }

    @Test
    public void testSetParameterAndAttributeNames() throws Exception {
        interceptor.setAttributeName("hello");
        interceptor.setParameterName("world");

        params.put("world", Locale.CHINA);
        interceptor.intercept(mai);

        assertNull(params.get("world")); // should have been removed

        assertNotNull(session.get("hello")); // should be stored here
        assertEquals(Locale.CHINA, session.get("hello"));
    }
}
