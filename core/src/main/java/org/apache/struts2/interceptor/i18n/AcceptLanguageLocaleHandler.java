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
package org.apache.struts2.interceptor.i18n;

import org.apache.struts2.ActionInvocation;

import java.util.Enumeration;
import java.util.Locale;
import java.util.Set;

/**
 * Resolves locale by first checking the request-only parameter and then falling back
 * to the browser's {@code Accept-Language} header.
 * <p>
 * When a {@code supportedLocale} set is configured, only Accept-Language values present
 * in that set are accepted. When the set is empty (the default), the first locale
 * advertised by the browser is returned as-is.
 *
 * @see RequestLocaleHandler
 * @see AbstractStoredLocaleHandler
 */
public abstract class AcceptLanguageLocaleHandler extends RequestLocaleHandler {

    private final Set<Locale> supportedLocale;

    protected AcceptLanguageLocaleHandler(ActionInvocation invocation, String requestOnlyParameterName, Set<Locale> supportedLocale) {
        super(invocation, requestOnlyParameterName);
        this.supportedLocale = supportedLocale;
    }

    @Override
    public Locale find() {
        Locale locale = findRequestOnlyParamLocale();
        if (locale != null) {
            return locale;
        }
        return findAcceptLanguageLocale();
    }

    @Override
    public Locale read(ActionInvocation invocation) {
        if (!supportedLocale.isEmpty()) {
            Locale locale = findAcceptLanguageLocale();
            if (locale != null) {
                return locale;
            }
        }
        return super.read(invocation);
    }

    @SuppressWarnings("rawtypes")
    protected Locale findAcceptLanguageLocale() {
        Enumeration locales = actionInvocation.getInvocationContext().getServletRequest().getLocales();
        while (locales.hasMoreElements()) {
            Locale acceptLocale = (Locale) locales.nextElement();
            if (supportedLocale.isEmpty() || supportedLocale.contains(acceptLocale)) {
                return acceptLocale;
            }
        }
        return null;
    }
}
