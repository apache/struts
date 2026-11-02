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
package org.apache.struts2.interceptor;

import jakarta.servlet.http.HttpServletResponse;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.apache.struts2.ActionInvocation;
import org.apache.struts2.ActionProxy;
import org.apache.struts2.action.Action;
import org.apache.struts2.dispatcher.mapper.ActionExtensions;
import org.apache.struts2.dispatcher.mapper.ActionMapping;

import java.io.IOException;
import java.util.List;
import java.util.Objects;

/**
 * Rejects requests whose URL extension is not among the extensions declared on the action or its package
 * with the {@code extension} attribute. Actions without a declaration are not restricted.
 * <p>
 * A rejected request resolves to {@link #setBadExtensionResultName(String) badExtensionResultName}
 * when the action maps that result, otherwise the response is {@code 404 Not Found}.
 * <p>
 * Only the request's own action is checked; actions reached by chaining are not.
 *
 * @since 7.5.0
 */
public class ActionExtensionInterceptor extends AbstractInterceptor {

    private static final Logger LOG = LogManager.getLogger(ActionExtensionInterceptor.class);

    private String badExtensionResultName = "bad-extension";

    @Override
    public String intercept(ActionInvocation invocation) throws Exception {
        ActionProxy proxy = invocation.getProxy();
        List<String> allowed = proxy.getConfig().getExtensions();
        if (allowed.isEmpty()) {
            return invocation.invoke();
        }

        ActionMapping mapping = invocation.getInvocationContext().getActionMapping();
        if (mapping == null
                || !Objects.equals(mapping.getName(), proxy.getActionName())
                || !Objects.equals(mapping.getNamespace(), proxy.getNamespace())) {
            return invocation.invoke();
        }

        if (ActionExtensions.allows(allowed, mapping.getExtension())) {
            return invocation.invoke();
        }

        LOG.debug("Extension [{}] is not among the declared extensions {}, rejecting request!", mapping.getExtension(), allowed);
        return rejectRequest(invocation);
    }

    /**
     * @return the bad extension result name when the action maps that result, otherwise {@link Action#NONE}
     * after sending {@code 404 Not Found}
     * @since 7.5.0
     */
    protected String rejectRequest(ActionInvocation invocation) throws IOException {
        if (ResultResolution.canResolve(invocation, badExtensionResultName)) {
            return badExtensionResultName;
        }

        LOG.debug("No result mapped for: {}, sending: {}", badExtensionResultName, HttpServletResponse.SC_NOT_FOUND);
        invocation.getInvocationContext().getServletResponse().sendError(HttpServletResponse.SC_NOT_FOUND);
        return Action.NONE;
    }

    /**
     * @since 7.5.0
     */
    public void setBadExtensionResultName(String badExtensionResultName) {
        this.badExtensionResultName = badExtensionResultName;
    }
}
