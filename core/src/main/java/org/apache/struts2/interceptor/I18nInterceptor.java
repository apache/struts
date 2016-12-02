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

import com.opensymphony.xwork2.ActionInvocation;
import com.opensymphony.xwork2.LocaleProvider;
import com.opensymphony.xwork2.inject.Inject;
import com.opensymphony.xwork2.interceptor.AbstractInterceptor;
import com.opensymphony.xwork2.util.LocalizedTextUtil;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.apache.logging.log4j.message.ParameterizedMessage;
import org.apache.struts2.ServletActionContext;
import org.apache.struts2.dispatcher.HttpParameters;
import org.apache.struts2.dispatcher.Parameter;

import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletResponse;
import java.util.Locale;
import java.util.Map;

/**
 * <!-- START SNIPPET: description -->
 * <p>
 * An interceptor that handles setting the locale specified in a session as the locale for the current action request.
 * In addition, this interceptor will look for a specific HTTP request parameter and set the locale to whatever value is
 * provided, it also looks for specific cookie to read locale from. This means that this interceptor can be used to allow
 * for your application to dynamically change the locale for the user's session or, alternatively, only for the current
 * request (since XWork 2.1.3).
 * This is very useful for applications that require multi-lingual support and want the user to
 * be able to set his or her language preference at any point. The locale parameter is removed during the execution of
 * this interceptor, ensuring that properties aren't set on an action (such as request_locale) that have no typical
 * corresponding setter in your action.
 * </p>
 *
 * <p>
 * For example, using the default parameter name, a request to <b>foo.action?request_locale=en_US</b>, then the
 * locale for US English is saved in the user's session and will be used for all future requests.
 * If there is no locale set (for example with the first visit), the interceptor uses the browser locale.
 * </p>
 * <!-- END SNIPPET: description -->
 *
 * <!-- START SNIPPET: parameters -->
  *
  * <ul>
  *
  * <li>parameterName (optional) - the name of the HTTP request parameter that dictates the locale to switch to and save
  * in the session. By default this is <b>request_locale</b></li>
  *
  * <li>requestCookieParameterName (optional) - the name of the HTTP request parameter that dictates the locale to switch to
  * and save in a cookien. By default this is <b>request_cookie_locale</b></li>
  *
  * <li>requestOnlyParameterName (optional) - the name of the HTTP request parameter that dictates the locale to switch to
  * for the current request only, without saving it in the session. By default this is <b>request_only_locale</b></li>
  *
  * <li>attributeName (optional) - the name of the session key to store the selected locale. By default this is
  * <b>WW_TRANS_I18N_LOCALE</b></li>
  *
  * <li>storage (optional) - the name of storage location, it can be <b>none</b>, <b>session</b> or <b>cookie</b>.
  * By default this is <b>session</b></li>
  *
  * </ul>
  *
  * <!-- END SNIPPET: parameters -->
  *
  * <!-- START SNIPPET: example -->
  * &lt;interceptor name="i18nCookie" class="org.apache.struts2.interceptor.I18nInterceptor"/&gt;
  *
  * &lt;action name="someAction" class="com.examples.SomeAction"&gt;
  *     &lt;interceptor-ref name="i18nCookie"/&gt;
  *     &lt;interceptor-ref name="basicStack"/&gt;
  *     &lt;result name="success"&gt;good_result.ftl&lt;/result&gt;
  * &lt;/action&gt;
  * <!-- END SNIPPET: example -->
  */
public class I18nInterceptor extends AbstractInterceptor {

    private static final Logger LOG = LogManager.getLogger(I18nInterceptor.class);

    public static final String DEFAULT_SESSION_ATTRIBUTE = "WW_TRANS_I18N_LOCALE";
    public static final String DEFAULT_PARAMETER = "request_locale";
    public static final String DEFAULT_REQUEST_ONLY_PARAMETER = "request_only_locale";
    public static final String DEFAULT_COOKIE_ATTRIBUTE = DEFAULT_SESSION_ATTRIBUTE;
    public static final String DEFAULT_COOKIE_PARAMETER = "request_cookie_locale";

    protected String parameterName = DEFAULT_PARAMETER;
    protected String requestOnlyParameterName = DEFAULT_REQUEST_ONLY_PARAMETER;
    protected String attributeName = DEFAULT_SESSION_ATTRIBUTE;
    protected String requestCookieParameterName = DEFAULT_COOKIE_PARAMETER;
    protected Storage storage = Storage.SESSION;

    protected LocaleProvider localeProvider;

    // Request-Only = None
    protected enum Storage { COOKIE, SESSION, NONE }

    public void setParameterName(String parameterName) {
        this.parameterName = parameterName;
    }

    public void setAttributeName(String attributeName) {
        this.attributeName = attributeName;
    }

    public void setRequestOnlyParameterName(String requestOnlyParameterName) {
        this.requestOnlyParameterName = requestOnlyParameterName;
    }

    public void setRequestCookieParameterName(String requestCookieParameterName) {
        this.requestCookieParameterName = requestCookieParameterName;
    }

    public void setLocaleStorage(String storageName) {
        if (storageName == null || "".equals(storageName)) {
            this.storage = Storage.NONE;
        } else {
            try {
                this.storage = Storage.valueOf(storageName.toUpperCase());
            } catch (IllegalArgumentException e) {
                LOG.warn(new ParameterizedMessage("Wrong storage name [{{}] was defined, falling back to {}", storageName, Storage.SESSION), e);
                this.storage = Storage.SESSION;
            }
        }
    }

    @Inject
    public void setLocaleProvider(LocaleProvider localeProvider) {
        this.localeProvider = localeProvider;
    }

    @Override
    public String intercept(ActionInvocation invocation) throws Exception {
        LOG.debug("Intercept '{}/{}'",
            invocation.getProxy().getNamespace(), invocation.getProxy().getActionName());

        RequestOnlyLocaleFinder localeFinder = getLocaleFinder(invocation);
        Locale locale = getLocaleFromParam(localeFinder.find());
        locale = storeLocale(invocation, locale);
        useLocale(invocation, locale);

        if (LOG.isDebugEnabled()) {
            LOG.debug("Before action invocation Locale={}", invocation.getStack().findValue("locale"));
        }

        final String result = invocation.invoke();

        if (LOG.isDebugEnabled()) {
            LOG.debug("After action invocation Locale={}", invocation.getStack().findValue("locale"));
        }

        return result;
    }

    protected RequestOnlyLocaleFinder getLocaleFinder(ActionInvocation invocation) {
        RequestOnlyLocaleFinder localeFinder;
        if (this.storage == Storage.COOKIE) {
            localeFinder = new CookieLocaleFinder(invocation);
        } else if (this.storage == Storage.SESSION) {
            localeFinder = new SessionLocaleFinder(invocation);
        } else {
            localeFinder = new RequestOnlyLocaleFinder(invocation);
        }

        LOG.debug("Using LocaleFinder implementation {}", localeFinder.getClass().getName());
        return localeFinder;
    }

    /**
     * Store the locale to the chosen storage, like f. e. the session
     *
     * @param invocation the action invocation
     * @param locale the locale to store
     *
     * @return the locale
     */
    protected Locale storeLocale(ActionInvocation invocation, Locale locale) {
        if (locale == null) {
            storage = Storage.NONE;
            locale = readStoredLocale(invocation);
        }

        if (storage == Storage.COOKIE) {
            storeLocaleInCookie(invocation, locale);
        }

        if (Storage.SESSION == storage) {
            storeLocaleInSession(invocation, locale);
        }

        return locale;
    }

    protected void storeLocaleInCookie(ActionInvocation ignore, Locale locale) {
        HttpServletResponse response = ServletActionContext.getResponse();

        Cookie cookie = new Cookie(DEFAULT_COOKIE_ATTRIBUTE, locale.toString());
        cookie.setMaxAge(1209600); // two weeks
        response.addCookie(cookie);
    }

    protected void storeLocaleInSession(ActionInvocation invocation, Locale locale) {
        //save it in session
        Map<String, Object> session = invocation.getInvocationContext().getSession();

        if (session != null) {
            synchronized (session) {
                session.put(attributeName, locale);
            }
        }
    }

    /**
     * Reads the locale from the session, and if not found from the
     * current invocation (=browser)
     *
     * @param invocation the current invocation
     * @return the read locale
     */
    protected Locale readStoredLocale(ActionInvocation invocation) {
        Locale locale;
        if (storage == Storage.COOKIE) {
            locale = readStoredLocalFromSession(invocation);
            if (locale != null) {
                LOG.debug("Found stored Locale {} in session, using it!", locale);
                return locale;
            }
        }

        if (storage == Storage.SESSION) {
            locale = readStoredLocaleFromCookie(invocation);
            if (locale != null) {
                LOG.debug("Found stored Locale {} in cookies, using it!", locale);
                return locale;
            }
        }

        LOG.debug("Neither locale was in session nor in cookies, searching current Invocation context");
        return readStoredLocalFromCurrentInvocation(invocation);
    }

    /**
     * Creates a Locale object from the request param, which might
     * be already a Local or a String
     *
     * @param requestedLocale the parameter from the request
     * @return the Locale
     */
    protected Locale getLocaleFromParam(Object requestedLocale) {
        Locale locale = null;
        if (requestedLocale != null) {
            if (requestedLocale instanceof Locale) {
                locale = (Locale) requestedLocale;
            } else {
                String localeStr = requestedLocale.toString();
                if (localeProvider.isValidLocaleString(localeStr)) {
                    locale = LocalizedTextUtil.localeFromString(requestedLocale.toString(), null);
                }
            }
            if (locale != null) {
                LOG.debug("Applied request locale: {}", locale);
            }
        }

        if (locale != null && !localeProvider.isValidLocale(locale)) {
            Locale defaultLocale = localeProvider.getLocale();
            LOG.debug("Provided locale {} isn't valid, fallback to default locale", locale, defaultLocale);
            locale = defaultLocale;
        }

        return locale;
    }

    protected Locale readStoredLocalFromSession(ActionInvocation invocation) {
        // check session for saved locale
        Map<String, Object> session = invocation.getInvocationContext().getSession();

        if (session != null) {
            synchronized (session) {
                Object sessionLocale = session.get(attributeName);
                if (sessionLocale != null && sessionLocale instanceof Locale) {
                    Locale locale = (Locale) sessionLocale;
                    LOG.debug("Applied session locale: {}", locale);
                    return locale;
                }
            }
        }
        return null;
    }

    protected Locale readStoredLocaleFromCookie(ActionInvocation ignore) {
        Cookie[] cookies = ServletActionContext.getRequest().getCookies();
        if (cookies != null) {
            for (Cookie cookie : cookies) {
                if (DEFAULT_COOKIE_ATTRIBUTE.equals(cookie.getName())) {
                    return getLocaleFromParam(cookie.getValue());
                }
            }
        }
        return null;
    }

    protected Locale readStoredLocalFromCurrentInvocation(ActionInvocation invocation) {
        // no overriding locale definition found, stay with current invocation (=browser) locale
        Locale locale = invocation.getInvocationContext().getLocale();
        if (locale != null) {
            LOG.debug("Applied invocation context locale: {}", locale);
        }
        return locale;
    }

    protected Parameter findLocaleParameter(HttpParameters params, String parameterName) {
        Parameter requestedLocale = params.get(parameterName);
        params.remove(parameterName);
        if (requestedLocale.isDefined()) {
            LOG.debug("Requested locale: {}", requestedLocale.getValue());
        }
        return requestedLocale;
    }

    /**
     * Save the given locale to the ActionInvocation.
     *
     * @param invocation The ActionInvocation.
     * @param locale     The locale to save.
     */
    protected void useLocale(ActionInvocation invocation, Locale locale) {
        invocation.getInvocationContext().setLocale(locale);
    }

    protected class RequestOnlyLocaleFinder {

        protected ActionInvocation actionInvocation = null;

        protected RequestOnlyLocaleFinder(ActionInvocation invocation) {
            actionInvocation = invocation;
        }

        public String find() {
            HttpParameters params = actionInvocation.getInvocationContext().getParameters();

            Parameter requestedLocale = findLocaleParameter(params, requestOnlyParameterName);
            if (requestedLocale.isDefined()) {
                storage = Storage.NONE;
                return requestedLocale.getValue();
            }

            return null;
        }
    }

    protected class SessionLocaleFinder extends RequestOnlyLocaleFinder {

        protected SessionLocaleFinder(ActionInvocation invocation) {
            super(invocation);
        }

        public String find() {
            String requestOnlyLocale = super.find();

            if (requestOnlyLocale != null) {
                return requestOnlyLocale;
            }

            HttpParameters params = actionInvocation.getInvocationContext().getParameters();

            Parameter requestedLocale = findLocaleParameter(params, parameterName);
            if (requestedLocale.isDefined()) {
                return requestedLocale.getValue();
            }

            return requestedLocale.getValue();
        }

    }

    protected class CookieLocaleFinder extends RequestOnlyLocaleFinder {
        protected CookieLocaleFinder(ActionInvocation invocation) {
            super(invocation);
        }

        @Override
        public String find() {
            String requestOnlySessionLocale = super.find();

            if (requestOnlySessionLocale != null) {
                return requestOnlySessionLocale;
            }

            HttpParameters params = actionInvocation.getInvocationContext().getParameters();

            Parameter requestedLocale = findLocaleParameter(params, requestCookieParameterName);
            if (requestedLocale.isDefined()) {
                storage = Storage.COOKIE;
                return requestedLocale.getValue();
            }

            return null;
        }
    }

}
