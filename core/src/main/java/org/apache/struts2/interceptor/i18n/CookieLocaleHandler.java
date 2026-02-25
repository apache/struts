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
import org.apache.struts2.ServletActionContext;

import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletResponse;

import java.util.Locale;
import java.util.Set;

public abstract class CookieLocaleHandler extends AbstractStoredLocaleHandler {

    private static final Logger LOG = LogManager.getLogger(CookieLocaleHandler.class);

    private final String attributeName;

    protected CookieLocaleHandler(ActionInvocation invocation,
                                  String requestOnlyParameterName,
                                  Set<Locale> supportedLocale,
                                  String requestCookieParameterName,
                                  String attributeName) {
        super(invocation, requestOnlyParameterName, supportedLocale, requestCookieParameterName);
        this.attributeName = attributeName;
    }

    @Override
    public Locale find() {
        Locale locale = findExplicitLocale(LOG, "Requested cookie locale {} is not supported, ignoring");
        if (locale != null) {
            return locale;
        }
        return findRequestOnlyLocale(LOG, null);
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

        return normalizeStoredLocale(
            LOG,
            locale,
            "Stored cookie locale {} is not in supportedLocale, ignoring",
            "No Locale defined in cookie, fetching from current request and it won't be stored!",
            "Found stored Locale {} in cookie, using it!",
            invocation
        );
    }
}
