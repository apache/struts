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

import org.apache.struts2.ActionInvocation;
import org.apache.struts2.locale.LocaleProvider;
import org.apache.struts2.locale.LocaleProviderFactory;
import org.apache.struts2.inject.Inject;
import org.apache.struts2.util.TextParseUtil;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.apache.logging.log4j.message.ParameterizedMessage;
import org.apache.struts2.dispatcher.HttpParameters;
import org.apache.struts2.dispatcher.Parameter;

import java.util.Collections;
import java.util.Locale;
import java.util.Set;
import java.util.stream.Collectors;

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

    private Set<Locale> supportedLocale = Collections.emptySet();

    protected enum Storage { COOKIE, SESSION, REQUEST, ACCEPT_LANGUAGE }

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
        if (storageName == null || storageName.isEmpty()) {
            this.storage = Storage.ACCEPT_LANGUAGE;
        } else {
            try {
                this.storage = Storage.valueOf(storageName.toUpperCase());
            } catch (IllegalArgumentException e) {
                LOG.warn(new ParameterizedMessage("Wrong storage name [{}] was defined, falling back to {}", storageName, Storage.SESSION), e);
                this.storage = Storage.SESSION;
            }
        }
    }

    /**
     * Sets supported Locales by the application
     *
     * @param supportedLocale a comma separated list of supported Locale
     */
    public void setSupportedLocale(String supportedLocale) {
        this.supportedLocale = TextParseUtil
            .commaDelimitedStringToSet(supportedLocale)
            .stream()
            .map(Locale::new)
            .collect(Collectors.toSet());
    }

    protected boolean isLocaleSupported(Locale locale) {
        return supportedLocale.isEmpty() || supportedLocale.contains(locale);
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
        } else if (this.storage == Storage.REQUEST) {
            localeHandler = new RequestLocaleHandler(invocation);
        } else {
            localeHandler = new AcceptLanguageLocaleHandler(invocation);
        }

        LOG.debug("Using LocaleFinder implementation {}", localeHandler.getClass().getName());
        return localeHandler;
    }

    /**
     * Creates a Locale object from the request param
     *
     * @param requestedLocale the parameter from the request
     * @return instance of {@link Locale} or null
     */
    protected Locale getLocaleFromParam(String requestedLocale) {
        LocaleProvider localeProvider = localeProviderFactory.createLocaleProvider();

        Locale locale = null;
        if (requestedLocale != null) {
            locale = localeProvider.toLocale(requestedLocale);
            if (locale == null) {
                locale = localeProvider.getLocale();
            }

            if (locale != null) {
                LOG.debug("Found locale: {}", locale);
            }
        }

        if (locale != null && !localeProvider.isValidLocale(locale)) {
            Locale defaultLocale = localeProvider.getLocale();
            LOG.debug("Provided locale {} isn't valid, fallback to default locale {}", locale, defaultLocale);
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
        invocation.getInvocationContext().withLocale(locale);
    }

    /**
     * Uses to handle reading/storing Locale from/in different locations
     */
    @Deprecated(forRemoval = true, since = "7.2.0")
    protected interface LocaleHandler extends org.apache.struts2.interceptor.i18n.LocaleHandler {
    }

    /**
     * @deprecated Since 7.2.0, use {@link org.apache.struts2.interceptor.i18n.RequestLocaleHandler}.
     * Scheduled for removal in the next release cycle.
     */
    @Deprecated(forRemoval = true, since = "7.2.0")
    protected class RequestLocaleHandler implements LocaleHandler {

        private final org.apache.struts2.interceptor.i18n.RequestLocaleHandler delegate;

        protected RequestLocaleHandler(ActionInvocation invocation) {
            delegate = new org.apache.struts2.interceptor.i18n.RequestLocaleHandler(invocation, requestOnlyParameterName) {
                @Override
                protected Locale getLocaleFromParam(String requestedLocale) {
                    return I18nInterceptor.this.getLocaleFromParam(requestedLocale);
                }

                @Override
                protected Parameter findLocaleParameter(ActionInvocation invocation, String parameterName) {
                    return I18nInterceptor.this.findLocaleParameter(invocation, parameterName);
                }

                @Override
                protected boolean isLocaleSupported(Locale locale) {
                    return I18nInterceptor.this.isLocaleSupported(locale);
                }
            };
        }

        @Override
        public Locale find() {
            return delegate.find();
        }

        @Override
        public Locale read(ActionInvocation invocation) {
            return delegate.read(invocation);
        }

        @Override
        public Locale store(ActionInvocation invocation, Locale locale) {
            return delegate.store(invocation, locale);
        }

        @Override
        public boolean shouldStore() {
            return delegate.shouldStore();
        }
    }

    /**
     * @deprecated Since 7.2.0, use {@link org.apache.struts2.interceptor.i18n.AcceptLanguageLocaleHandler}.
     * Scheduled for removal in the next release cycle.
     */
    @Deprecated(forRemoval = true, since = "7.2.0")
    protected class AcceptLanguageLocaleHandler implements LocaleHandler {

        private final org.apache.struts2.interceptor.i18n.AcceptLanguageLocaleHandler delegate;

        protected AcceptLanguageLocaleHandler(ActionInvocation invocation) {
            delegate = new org.apache.struts2.interceptor.i18n.AcceptLanguageLocaleHandler(
                invocation, requestOnlyParameterName, supportedLocale
            ) {
                @Override
                protected Locale getLocaleFromParam(String requestedLocale) {
                    return I18nInterceptor.this.getLocaleFromParam(requestedLocale);
                }

                @Override
                protected Parameter findLocaleParameter(ActionInvocation invocation, String parameterName) {
                    return I18nInterceptor.this.findLocaleParameter(invocation, parameterName);
                }

                @Override
                protected boolean isLocaleSupported(Locale locale) {
                    return I18nInterceptor.this.isLocaleSupported(locale);
                }
            };
        }

        @Override
        public Locale find() {
            return delegate.find();
        }

        @Override
        public Locale read(ActionInvocation invocation) {
            return delegate.read(invocation);
        }

        @Override
        public Locale store(ActionInvocation invocation, Locale locale) {
            return delegate.store(invocation, locale);
        }

        @Override
        public boolean shouldStore() {
            return delegate.shouldStore();
        }
    }

    /**
     * @deprecated Since 7.2.0, use {@link org.apache.struts2.interceptor.i18n.SessionLocaleHandler}.
     * Scheduled for removal in the next release cycle.
     */
    @Deprecated(forRemoval = true, since = "7.2.0")
    protected class SessionLocaleHandler implements LocaleHandler {

        private final org.apache.struts2.interceptor.i18n.SessionLocaleHandler delegate;

        protected SessionLocaleHandler(ActionInvocation invocation) {
            delegate = new org.apache.struts2.interceptor.i18n.SessionLocaleHandler(
                invocation, requestOnlyParameterName, supportedLocale, parameterName, attributeName
            ) {
                @Override
                protected Locale getLocaleFromParam(String requestedLocale) {
                    return I18nInterceptor.this.getLocaleFromParam(requestedLocale);
                }

                @Override
                protected Parameter findLocaleParameter(ActionInvocation invocation, String parameterName) {
                    return I18nInterceptor.this.findLocaleParameter(invocation, parameterName);
                }

                @Override
                protected boolean isLocaleSupported(Locale locale) {
                    return I18nInterceptor.this.isLocaleSupported(locale);
                }
            };
        }

        @Override
        public Locale find() {
            return delegate.find();
        }

        @Override
        public Locale read(ActionInvocation invocation) {
            return delegate.read(invocation);
        }

        @Override
        public Locale store(ActionInvocation invocation, Locale locale) {
            return delegate.store(invocation, locale);
        }

        @Override
        public boolean shouldStore() {
            return delegate.shouldStore();
        }
    }

    /**
     * @deprecated Since 7.2.0, use {@link org.apache.struts2.interceptor.i18n.CookieLocaleHandler}.
     * Scheduled for removal in the next release cycle.
     */
    @Deprecated(forRemoval = true, since = "7.2.0")
    protected class CookieLocaleHandler implements LocaleHandler {

        private final org.apache.struts2.interceptor.i18n.CookieLocaleHandler delegate;

        protected CookieLocaleHandler(ActionInvocation invocation) {
            delegate = new org.apache.struts2.interceptor.i18n.CookieLocaleHandler(
                invocation, requestOnlyParameterName, supportedLocale, requestCookieParameterName, attributeName
            ) {
                @Override
                protected Locale getLocaleFromParam(String requestedLocale) {
                    return I18nInterceptor.this.getLocaleFromParam(requestedLocale);
                }

                @Override
                protected Parameter findLocaleParameter(ActionInvocation invocation, String parameterName) {
                    return I18nInterceptor.this.findLocaleParameter(invocation, parameterName);
                }

                @Override
                protected boolean isLocaleSupported(Locale locale) {
                    return I18nInterceptor.this.isLocaleSupported(locale);
                }
            };
        }

        @Override
        public Locale find() {
            return delegate.find();
        }

        @Override
        public Locale read(ActionInvocation invocation) {
            return delegate.read(invocation);
        }

        @Override
        public Locale store(ActionInvocation invocation, Locale locale) {
            return delegate.store(invocation, locale);
        }

        @Override
        public boolean shouldStore() {
            return delegate.shouldStore();
        }
    }

}
