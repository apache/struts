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

import org.apache.struts2.ActionInvocation;
import org.apache.struts2.UnknownHandlerManager;
import org.apache.struts2.config.entities.ActionConfig;

/**
 * Decides whether a rejection result name can be rendered for an invocation.
 *
 * @since 7.5.0
 */
public final class ResultResolution {

    private ResultResolution() {
    }

    /**
     * @return true when the action maps {@code resultName} (or a catch-all) or an unknown handler supplies a result
     * @since 7.5.0
     */
    public static boolean canResolve(ActionInvocation invocation, String resultName) {
        ActionConfig config = invocation.getProxy().getConfig();
        if (config.getResults().containsKey(resultName) || config.getResults().containsKey("*")) {
            return true;
        }
        UnknownHandlerManager unknownHandlerManager = invocation.getInvocationContext().getContainer().getInstance(UnknownHandlerManager.class);
        return unknownHandlerManager.hasUnknownHandlers()
            && unknownHandlerManager.handleUnknownResult(invocation.getInvocationContext(),
                invocation.getProxy().getActionName(), config, resultName) != null;
    }
}
