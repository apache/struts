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

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.apache.struts2.ActionInvocation;
import org.apache.struts2.dispatcher.Parameter;

import java.util.Locale;

/**
 * Resolves locale from a request-only parameter (not persisted to session or cookie).
 * <p>
 * When a matching request parameter is present and the locale is
 * {@linkplain #isLocaleSupported(Locale) supported}, it is applied to the current
 * request only; it is never stored for subsequent requests.
 *
 * @see AcceptLanguageLocaleHandler
 * @see AbstractStoredLocaleHandler
 */
public abstract class RequestLocaleHandler extends AbstractLocaleHandler {

    private static final Logger LOG = LogManager.getLogger(RequestLocaleHandler.class);

    private final String requestOnlyParameterName;

    protected RequestLocaleHandler(ActionInvocation invocation, String requestOnlyParameterName) {
        super(invocation);
        this.requestOnlyParameterName = requestOnlyParameterName;
    }

    @Override
    public Locale find() {
        return findRequestOnlyParamLocale();
    }

    /**
     * Looks up the locale from the request-only parameter without any additional fallback.
     * Subclasses that add fallback logic (e.g. Accept-Language) can override {@link #find()}
     * while stored-locale handlers can call this method directly to skip the fallback.
     */
    protected Locale findRequestOnlyParamLocale() {
        LOG.debug("Searching locale in request under parameter {}", requestOnlyParameterName);

        Parameter requestedLocale = findLocaleParameter(actionInvocation, requestOnlyParameterName);
        if (requestedLocale.isDefined()) {
            Locale locale = getLocaleFromParam(requestedLocale.getValue());
            if (locale != null && isLocaleSupported(locale)) {
                return locale;
            }
            LOG.debug("Requested locale {} is not supported, ignoring", requestedLocale.getValue());
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
        Locale locale = invocation.getInvocationContext().getLocale();
        if (locale != null) {
            LOG.debug("Applied invocation context locale: {}", locale);
        }
        return locale;
    }
}
