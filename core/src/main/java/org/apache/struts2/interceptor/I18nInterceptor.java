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

import com.opensymphony.xwork2.ActionInvocation;
import com.opensymphony.xwork2.LocaleProvider;
import com.opensymphony.xwork2.LocaleProviderFactory;
import com.opensymphony.xwork2.inject.Inject;
import com.opensymphony.xwork2.interceptor.AbstractInterceptor;
import org.apache.commons.lang3.LocaleUtils;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.apache.logging.log4j.message.ParameterizedMessage;
import org.apache.struts2.ServletActionContext;
import org.apache.struts2.dispatcher.HttpParameters;
import org.apache.struts2.dispatcher.Parameter;

import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

import java.util.Locale;
import java.util.Map;

/**
 * An interceptor that handles setting the locale specified in a session as the locale for the current action request.
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

    protected LocaleProviderFactory localeProviderFactory;

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
                LOG.warn(new ParameterizedMessage("Wrong storage name [{}] was defined, falling back to {}", storageName, Storage.SESSION), e);
                this.storage = Storage.SESSION;
            }
        }
    }

    @Inject
    public void setLocaleProviderFactory(LocaleProviderFactory localeProviderFactory) {
        this.localeProviderFactory = localeProviderFactory;
    }

    @Override
    public String intercept(ActionInvocation invocation) throws Exception {
        LOG.debug("Intercept '{}/{}'", invocation.getProxy().getNamespace(), invocation.getProxy().getActionName());

        LocaleHandler localeHandler = getLocaleHandler(invocation);
        Locale locale = localeHandler.find();

        if (locale == null) {
            locale = localeHandler.read(invocation);
        }

        if (localeHandler.shouldStore()) {
            locale = localeHandler.store(invocation, locale);
        }

        useLocale(invocation, locale);

        if (LOG.isDebugEnabled()) {
            LOG.debug("Before action invocation Locale={}", invocation.getStack().findValue("locale"));
        }

        try {
            return invocation.invoke();
        } finally {
            if (LOG.isDebugEnabled()) {
                LOG.debug("After action invocation Locale={}", invocation.getStack().findValue("locale"));
            }
        }
    }

    /**
     * Override this method to use your own implementation of {@link LocaleHandler}
     *
     * @param invocation current action invocation context
     * @return instance of {@link LocaleHandler}
     */
    protected LocaleHandler getLocaleHandler(ActionInvocation invocation) {
        LocaleHandler localeHandler;

        if (this.storage == Storage.COOKIE) {
            localeHandler = new CookieLocaleHandler(invocation);
        } else if (this.storage == Storage.SESSION) {
            localeHandler = new SessionLocaleHandler(invocation);
        } else {
            localeHandler = new RequestOnlyLocaleHandler(invocation);
        }

        LOG.debug("Using LocaleFinder implementation {}", localeHandler.getClass().getName());
        return localeHandler;
    }

    /**
     * Creates a Locale object from the request param, which might
     * be already a Local or a String
     *
     * @param requestedLocale the parameter from the request
     * @return the Locale
     */
    protected Locale getLocaleFromParam(Object requestedLocale) {
        LocaleProvider localeProvider = localeProviderFactory.createLocaleProvider();

        Locale locale = null;
        if (requestedLocale != null) {
            if (requestedLocale instanceof Locale) {
                locale = (Locale) requestedLocale;
            } else {
                String localeStr = requestedLocale.toString();
                if (localeProvider.isValidLocaleString(localeStr)) {
                    locale = LocaleUtils.toLocale(localeStr);
                } else {
                    locale = localeProvider.getLocale();
                }
            }
            if (locale != null) {
                LOG.debug("Found locale: {}", locale);
            }
        }

        if (locale != null && !localeProvider.isValidLocale(locale)) {
            Locale defaultLocale = localeProvider.getLocale();
            LOG.debug("Provided locale {} isn't valid, fallback to default locale", locale, defaultLocale);
            locale = defaultLocale;
        }

        return locale;
    }

    protected Parameter findLocaleParameter(ActionInvocation invocation, String parameterName) {
        HttpParameters params = invocation.getInvocationContext().getParameters();
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

    /**
     * Uses to handle reading/storing Locale from/in different locations
     */
    protected interface LocaleHandler {
        Locale find();
        Locale read(ActionInvocation invocation);
        Locale store(ActionInvocation invocation, Locale locale);
        boolean shouldStore();
    }

    protected class RequestOnlyLocaleHandler implements LocaleHandler {

        protected ActionInvocation actionInvocation = null;
        protected boolean shouldStore = true;

        protected RequestOnlyLocaleHandler(ActionInvocation invocation) {
            actionInvocation = invocation;
        }

        public Locale find() {
            LOG.debug("Searching locale in request under parameter {}", requestOnlyParameterName);

            Parameter requestedLocale = findLocaleParameter(actionInvocation, requestOnlyParameterName);
            if (requestedLocale.isDefined()) {
                return getLocaleFromParam(requestedLocale.getValue());
            }

            return null;
        }

        @Override
        public Locale store(ActionInvocation invocation, Locale locale) {
            return locale;
        }

        @Override
        public Locale read(ActionInvocation invocation) {
            LOG.debug("Searching current Invocation context");
            // no overriding locale definition found, stay with current invocation (=browser) locale
            Locale locale = invocation.getInvocationContext().getLocale();
            if (locale != null) {
                LOG.debug("Applied invocation context locale: {}", locale);
            }
            return locale;
        }

        @Override
        public boolean shouldStore() {
            return shouldStore;
        }
    }

    protected class SessionLocaleHandler extends RequestOnlyLocaleHandler {

        protected SessionLocaleHandler(ActionInvocation invocation) {
            super(invocation);
        }

        public Locale find() {
            Locale requestOnlyLocale = super.find();

            if (requestOnlyLocale != null) {
                LOG.debug("Found locale under request only param, it won't be stored in session!");
                shouldStore = false;
                return requestOnlyLocale;
            }

            LOG.debug("Searching locale in request under parameter {}", parameterName);
            Parameter requestedLocale = findLocaleParameter(actionInvocation, parameterName);
            if (requestedLocale.isDefined()) {
                return getLocaleFromParam(requestedLocale.getValue());
            }

            return null;
        }

        @Override
        public Locale store(ActionInvocation invocation, Locale locale) {
            HttpSession session = ServletActionContext.getRequest().getSession(false);

            if (session != null) {
                String sessionId = session.getId();
                synchronized (sessionId.intern()) {
                    invocation.getInvocationContext().getSession().put(attributeName, locale);
                }
            } else {
                LOG.debug("session creation avoided as it doesn't exist already");
            }

            return locale;
        }

        @Override
        public Locale read(ActionInvocation invocation) {
            Locale locale = null;

            LOG.debug("Checks session for saved locale");
            HttpSession session = ServletActionContext.getRequest().getSession(false);

            if (session != null) {
                String sessionId = session.getId();
                synchronized (sessionId.intern()) {
                    Object sessionLocale = invocation.getInvocationContext().getSession().get(attributeName);
                    if (sessionLocale != null && sessionLocale instanceof Locale) {
                        locale = (Locale) sessionLocale;
                        LOG.debug("Applied session locale: {}", locale);
                    }
                }
            }

            if (locale == null) {
                LOG.debug("No Locale defined in session, fetching from current request and it won't be stored in session!");
                shouldStore = false;
                locale = super.read(invocation);
            } else {
                LOG.debug("Found stored Locale {} in session, using it!", locale);
            }

            return locale;
        }
    }

    protected class CookieLocaleHandler extends RequestOnlyLocaleHandler {
        protected CookieLocaleHandler(ActionInvocation invocation) {
            super(invocation);
        }

        @Override
        public Locale find() {
            Locale requestOnlySessionLocale = super.find();

            if (requestOnlySessionLocale != null) {
                shouldStore = false;
                return requestOnlySessionLocale;
            }

            LOG.debug("Searching locale in request under parameter {}", requestCookieParameterName);
            Parameter requestedLocale = findLocaleParameter(actionInvocation, requestCookieParameterName);
            if (requestedLocale.isDefined()) {
                return getLocaleFromParam(requestedLocale.getValue());
            }

            return null;
        }

        @Override
        public Locale store(ActionInvocation invocation, Locale locale) {
            HttpServletResponse response = ServletActionContext.getResponse();

            Cookie cookie = new Cookie(attributeName, locale.toString());
            cookie.setMaxAge(1209600); // two weeks
            response.addCookie(cookie);

            return locale;
        }

        @Override
        public Locale read(ActionInvocation invocation) {
            Locale locale = null;

            Cookie[] cookies = ServletActionContext.getRequest().getCookies();
            if (cookies != null) {
                for (Cookie cookie : cookies) {
                    if (attributeName.equals(cookie.getName())) {
                        locale = getLocaleFromParam(cookie.getValue());
                    }
                }
            }

            if (locale == null) {
                LOG.debug("No Locale defined in cookie, fetching from current request and it won't be stored!");
                shouldStore = false;
                locale = super.read(invocation);
            } else {
                LOG.debug("Found stored Locale {} in cookie, using it!", locale);
            }
            return locale;
        }
    }

}
