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

public abstract class AcceptLanguageLocaleHandler extends RequestLocaleHandler {

    private final Set<Locale> supportedLocale;

    protected AcceptLanguageLocaleHandler(ActionInvocation invocation, String requestOnlyParameterName, Set<Locale> supportedLocale) {
        super(invocation, requestOnlyParameterName);
        this.supportedLocale = supportedLocale;
    }

    @Override
    @SuppressWarnings("rawtypes")
    public Locale find() {
        Locale locale = super.find();
        if (locale != null) {
            return locale;
        }

        if (!supportedLocale.isEmpty()) {
            Enumeration locales = actionInvocation.getInvocationContext().getServletRequest().getLocales();
            while (locales.hasMoreElements()) {
                Locale acceptLocale = (Locale) locales.nextElement();
                if (supportedLocale.contains(acceptLocale)) {
                    return acceptLocale;
                }
            }
        }
        return null;
    }
}
