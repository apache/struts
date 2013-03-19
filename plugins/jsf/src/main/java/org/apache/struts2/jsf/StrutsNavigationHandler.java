/*
 * $Id$
 *
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

package org.apache.struts2.jsf;

import com.opensymphony.xwork2.ActionContext;
import com.opensymphony.xwork2.config.entities.ActionConfig;
import com.opensymphony.xwork2.config.entities.ResultConfig;

import javax.faces.application.NavigationHandler;
import javax.faces.context.FacesContext;
import java.util.Map;

/**
 * Overrides the JFS navigation by delegating the result to handling by the core
 * result code lookup and execution.  If a result cannot be found, the previous
 * NavigationHandler is called.
 */
public class StrutsNavigationHandler extends NavigationHandler {

    private NavigationHandler parent;

    /**
     * Creates the handler
     *
     * @param handler The old NavigationHandler to possibly delegate to
     */
    public StrutsNavigationHandler(NavigationHandler handler) {
        this.parent = handler;
    }

    /**
     * Stores any outcomes as the result code, failing over to the old
     * NavigationHandler
     *
     * @param facesContext The faces context
     * @param fromAction The action we are coming from
     * @param outcome The String return code
     */
    @Override
    public void handleNavigation(FacesContext facesContext, String fromAction, String outcome) {
        ActionContext ctx = ActionContext.getContext();
        if (outcome != null) {
            if (ctx == null || ctx.getActionInvocation() == null) {
                delegateToParentNavigation(facesContext, fromAction, outcome);
            } else {
                ActionConfig config = ctx.getActionInvocation().getProxy().getConfig();
                Map results = config.getResults();
    
                ResultConfig resultConfig = null;
    
                synchronized (config) {
                    try {
                        resultConfig = (ResultConfig) results.get(outcome);
                    } catch (NullPointerException e) {
                    }
                    if (resultConfig == null) {
                        // If no result is found for the given resultCode, try to get a wildcard '*' match.
                        resultConfig = (ResultConfig) results.get("*");
                    }
                }
                if (resultConfig != null) {
                    ctx.getActionInvocation().setResultCode(outcome);
                } else {
                    delegateToParentNavigation(facesContext, fromAction, outcome);
                }
            }
        }
    }

    private void delegateToParentNavigation(FacesContext facesContext, String fromAction, String outcome) {
        // Failing over to parent handler
        parent.handleNavigation(facesContext, fromAction, outcome);
    }

}
