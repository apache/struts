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

public abstract class RequestLocaleHandler extends AbstractLocaleHandler {

    private static final Logger LOG = LogManager.getLogger(RequestLocaleHandler.class);

    private final String requestOnlyParameterName;

    protected RequestLocaleHandler(ActionInvocation invocation, String requestOnlyParameterName) {
        super(invocation);
        this.requestOnlyParameterName = requestOnlyParameterName;
    }

    @Override
    public Locale find() {
        LOG.debug("Searching locale in request under parameter {}", requestOnlyParameterName);

        Parameter requestedLocale = findLocaleParameter(actionInvocation, requestOnlyParameterName);
        if (requestedLocale.isDefined()) {
            return getLocaleFromParam(requestedLocale.getValue());
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
