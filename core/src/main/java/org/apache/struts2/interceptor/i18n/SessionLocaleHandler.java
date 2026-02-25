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

import jakarta.servlet.http.HttpSession;

import java.util.Locale;
import java.util.Map;
import java.util.Set;

public abstract class SessionLocaleHandler extends AbstractStoredLocaleHandler {

    private static final Logger LOG = LogManager.getLogger(SessionLocaleHandler.class);

    private final String attributeName;

    protected SessionLocaleHandler(ActionInvocation invocation,
                                   String requestOnlyParameterName,
                                   Set<Locale> supportedLocale,
                                   String parameterName,
                                   String attributeName) {
        super(invocation, requestOnlyParameterName, supportedLocale, parameterName);
        this.attributeName = attributeName;
    }

    @Override
    public Locale find() {
        Locale locale = findExplicitLocale(LOG, "Requested locale {} is not supported, ignoring");
        if (locale != null) {
            return locale;
        }
        return findRequestOnlyLocale(LOG, "Found locale under request only param, it won't be stored in session!");
    }

    @Override
    public Locale store(ActionInvocation invocation, Locale locale) {
        Map<String, Object> session = invocation.getInvocationContext().getSession();

        if (session != null) {
            String sessionId = ServletActionContext.getRequest().getSession().getId();
            synchronized (sessionId.intern()) {
                session.put(attributeName, locale);
            }
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
                if (sessionLocale instanceof Locale) {
                    locale = (Locale) sessionLocale;
                    LOG.debug("Applied session locale: {}", locale);
                }
            }
        }

        return normalizeStoredLocale(
            LOG,
            locale,
            "Stored session locale {} is not in supportedLocale, ignoring",
            "No Locale defined in session, fetching from current request and it won't be stored in session!",
            "Found stored Locale {} in session, using it!",
            invocation
        );
    }
}
