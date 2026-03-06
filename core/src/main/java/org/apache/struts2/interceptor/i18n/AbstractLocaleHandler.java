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
import org.apache.struts2.dispatcher.Parameter;

import java.util.Locale;

public abstract class AbstractLocaleHandler implements LocaleHandler {

    protected final ActionInvocation actionInvocation;
    private boolean shouldStore = true;

    protected AbstractLocaleHandler(ActionInvocation invocation) {
        this.actionInvocation = invocation;
    }

    @Override
    public boolean shouldStore() {
        return shouldStore;
    }

    protected void disableStore() {
        this.shouldStore = false;
    }

    protected abstract Locale getLocaleFromParam(String requestedLocale);

    protected abstract Parameter findLocaleParameter(ActionInvocation invocation, String parameterName);

    protected abstract boolean isLocaleSupported(Locale locale);
}
