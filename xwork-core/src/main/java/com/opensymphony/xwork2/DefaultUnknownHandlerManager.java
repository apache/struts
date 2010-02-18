/*
 * Copyright 2002-2006,2009 The Apache Software Foundation.
 * 
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 * 
 *      http://www.apache.org/licenses/LICENSE-2.0
 * 
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package com.opensymphony.xwork2;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;

import com.opensymphony.xwork2.ActionContext;
import com.opensymphony.xwork2.Result;
import com.opensymphony.xwork2.UnknownHandler;
import com.opensymphony.xwork2.UnknownHandlerManager;
import com.opensymphony.xwork2.config.Configuration;
import com.opensymphony.xwork2.config.entities.ActionConfig;
import com.opensymphony.xwork2.config.entities.UnknownHandlerConfig;
import com.opensymphony.xwork2.inject.Container;
import com.opensymphony.xwork2.inject.Inject;

/**
 * Default implementation of UnknownHandlerManager
 *
 * @see com.opensymphony.xwork2.UnknownHandlerManager
 */
public class DefaultUnknownHandlerManager implements UnknownHandlerManager {
    protected ArrayList<UnknownHandler> unknownHandlers;
    private Configuration configuration;
    private Container container;

    @Inject
    public void setConfiguration(Configuration configuration) {
        this.configuration = configuration;
        build();
    }

    @Inject
    public void setContainer(Container container) {
        this.container = container;
        build();
    }

    /**
     * Builds a list of UnknowHandlers in the order specified by the configured "unknown-handler-stack".
     * If "unknown-handler-stack" was not configured, all UnknowHandlers will be returned, in no specific order
     */
    protected void build() {
        if (configuration != null && container != null) {
            List<UnknownHandlerConfig> unkownHandlerStack = configuration.getUnknownHandlerStack();
            unknownHandlers = new ArrayList<UnknownHandler>();

            if (unkownHandlerStack != null && !unkownHandlerStack.isEmpty()) {
                //get UnknownHandlers in the specified order
                for (UnknownHandlerConfig unknownHandlerConfig : unkownHandlerStack) {
                    UnknownHandler uh = container.getInstance(UnknownHandler.class, unknownHandlerConfig.getName());
                    unknownHandlers.add(uh);
                }
            } else {
                //add all available UnknownHandlers
                Set<String> unknowHandlerNames = container.getInstanceNames(UnknownHandler.class);
                if (unknowHandlerNames != null) {
                    for (String unknowHandlerName : unknowHandlerNames) {
                        UnknownHandler uh = container.getInstance(UnknownHandler.class, unknowHandlerName);
                        unknownHandlers.add(uh);
                    }
                }
            }
        }
    }

    /**
     * Iterate over UnknownHandlers and return the result of the first one that can handle it
     */
    public Result handleUnknownResult(ActionContext actionContext, String actionName, ActionConfig actionConfig, String resultCode) {
        for (UnknownHandler unknownHandler : unknownHandlers) {
            Result result = unknownHandler.handleUnknownResult(actionContext, actionName, actionConfig, resultCode);
            if (result != null)
                return result;
        }

        return null;
    }

    /**
     * Iterate over UnknownHandlers and return the result of the first one that can handle it
     *
     * @throws NoSuchMethodException
     */
    public Object handleUnknownMethod(Object action, String methodName) throws NoSuchMethodException {
        for (UnknownHandler unknownHandler : unknownHandlers) {
            Object result = unknownHandler.handleUnknownActionMethod(action, methodName);
            if (result != null)
                return result;
        }

        return null;
    }

    /**
     * Iterate over UnknownHandlers and return the result of the first one that can handle it
     *
     * @throws NoSuchMethodException
     */
    public ActionConfig handleUnknownAction(String namespace, String actionName) {
        for (UnknownHandler unknownHandler : unknownHandlers) {
            ActionConfig result = unknownHandler.handleUnknownAction(namespace, actionName);
            if (result != null)
                return result;
        }

        return null;
    }

    public boolean hasUnknownHandlers() {
        return unknownHandlers != null && !unknownHandlers.isEmpty();
    }

    public List<UnknownHandler> getUnknownHandlers() {
        return unknownHandlers;
    }
}
