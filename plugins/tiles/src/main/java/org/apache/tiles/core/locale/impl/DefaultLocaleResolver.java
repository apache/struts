/*
 * Licensed to the Apache Software Foundation (ASF) under one
 * or more contributor license agreements.  See the NOTICE file
 * distributed with this work for additional information
 * regarding copyright ownership.  The ASF licenses this file
 * to you under the Apache License, Version 2.0 (the
 * "License"); you may not use this file except in compliance
 * with the License.  You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing,
 * software distributed under the License is distributed on an
 * "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY
 * KIND, either express or implied.  See the License for the
 * specific language governing permissions and limitations
 * under the License.
 */
package org.apache.tiles.core.locale.impl;

import org.apache.tiles.core.locale.LocaleResolver;
import org.apache.tiles.request.Request;

import java.util.Locale;
import java.util.Map;

/**
 * Default implementation of <code>LocaleResolver</code><br>
 * It tries to take the locale from the session-scoped attribute
 * {@link DefaultLocaleResolver#LOCALE_KEY}. If it is not found, it returns the
 * locale included in the request.
 */
public class DefaultLocaleResolver implements LocaleResolver {

    /**
     * The attribute name that is used to store the current locale.
     */
    public static final String LOCALE_KEY = "org.apache.tiles.LOCALE";

    /**
     * {@inheritDoc}
     */
    public Locale resolveLocale(Request request) {
        Locale retValue = null;
        Map<String, Object> session = request.getContext("session");
        if (session != null) {
            retValue = (Locale) session.get(DefaultLocaleResolver.LOCALE_KEY);
        }
        if (retValue == null) {
            retValue = request.getRequestLocale();
        }

        return retValue;
    }
}
