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
import java.util.Set;

public abstract class AbstractStoredLocaleHandler extends AcceptLanguageLocaleHandler {

    private static final Logger LOG = LogManager.getLogger(AbstractStoredLocaleHandler.class);

    private final String explicitParameterName;

    protected AbstractStoredLocaleHandler(ActionInvocation invocation,
                                          String requestOnlyParameterName,
                                          Set<Locale> supportedLocale,
                                          String explicitParameterName) {
        super(invocation, requestOnlyParameterName, supportedLocale);
        this.explicitParameterName = explicitParameterName;
    }

    protected Locale findExplicitLocale() {
        LOG.debug("Searching locale in request under parameter {}", explicitParameterName);
        Parameter requestedLocale = findLocaleParameter(actionInvocation, explicitParameterName);
        if (requestedLocale.isDefined()) {
            Locale locale = getLocaleFromParam(requestedLocale.getValue());
            if (locale != null && isLocaleSupported(locale)) {
                return locale;
            }
            LOG.debug("Requested locale {} is not supported, ignoring", requestedLocale.getValue());
        }
        return null;
    }

    protected Locale findRequestOnlyLocale() {
        Locale requestOnlyLocale = findRequestOnlyParamLocale();
        if (requestOnlyLocale != null) {
            LOG.debug("Found locale under request only param, it won't be stored!");
            disableStore();
            return requestOnlyLocale;
        }
        return null;
    }

    protected Locale normalizeStoredLocale(Locale locale, ActionInvocation invocation) {
        if (locale != null && !isLocaleSupported(locale)) {
            LOG.debug("Stored locale {} is not in supportedLocale, ignoring", locale);
            locale = null;
        }

        if (locale == null) {
            LOG.debug("No Locale defined in storage, fetching from current request and it won't be stored!");
            disableStore();
            return super.read(invocation);
        } else {
            LOG.debug("Found stored Locale {}, using it!", locale);
            return locale;
        }
    }
}
