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

import org.apache.logging.log4j.Logger;
import org.apache.struts2.ActionInvocation;
import org.apache.struts2.dispatcher.Parameter;

import java.util.Locale;
import java.util.Set;

public abstract class AbstractStoredLocaleHandler extends AcceptLanguageLocaleHandler {

    private final String explicitParameterName;

    protected AbstractStoredLocaleHandler(ActionInvocation invocation,
                                          String requestOnlyParameterName,
                                          Set<Locale> supportedLocale,
                                          String explicitParameterName) {
        super(invocation, requestOnlyParameterName, supportedLocale);
        this.explicitParameterName = explicitParameterName;
    }

    protected Locale findExplicitLocale(Logger logger, String unsupportedLogPattern) {
        logger.debug("Searching locale in request under parameter {}", explicitParameterName);
        Parameter requestedLocale = findLocaleParameter(actionInvocation, explicitParameterName);
        if (requestedLocale.isDefined()) {
            Locale locale = getLocaleFromParam(requestedLocale.getValue());
            if (locale != null && isLocaleSupported(locale)) {
                return locale;
            }
            logger.debug(unsupportedLogPattern, requestedLocale.getValue());
        }
        return null;
    }

    protected Locale findRequestOnlyLocale(Logger logger, String requestOnlyFoundLogPattern) {
        Locale requestOnlyLocale = super.find();
        if (requestOnlyLocale != null) {
            if (requestOnlyFoundLogPattern != null) {
                logger.debug(requestOnlyFoundLogPattern);
            }
            shouldStore = false;
            return requestOnlyLocale;
        }
        return null;
    }

    protected Locale normalizeStoredLocale(Logger logger,
                                           Locale locale,
                                           String unsupportedStoredLogPattern,
                                           String missingStoredLogPattern,
                                           String foundStoredLogPattern,
                                           ActionInvocation invocation) {
        if (locale != null && !isLocaleSupported(locale)) {
            logger.debug(unsupportedStoredLogPattern, locale);
            locale = null;
        }

        if (locale == null) {
            logger.debug(missingStoredLogPattern);
            shouldStore = false;
            return super.read(invocation);
        } else {
            logger.debug(foundStoredLogPattern, locale);
            return locale;
        }
    }
}
