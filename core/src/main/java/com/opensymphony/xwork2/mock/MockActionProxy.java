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
package com.opensymphony.xwork2.mock;

import com.opensymphony.xwork2.ActionInvocation;
import com.opensymphony.xwork2.ActionProxy;
import com.opensymphony.xwork2.config.Configuration;
import com.opensymphony.xwork2.config.entities.ActionConfig;
import org.apache.commons.lang3.StringUtils;

/**
 * Mock for an {@link ActionProxy}.
 * 
 * @author Patrick Lightbody (plightbo at gmail dot com)
 */
public class MockActionProxy implements ActionProxy {
    
    Object action;
    String actionName;
    ActionConfig config;
    boolean executeResult;
    ActionInvocation invocation;
    String namespace;
    String method;
    boolean executedCalled;
    String returnedResult;
    Configuration configuration;
    boolean methodSpecified;

    public void prepare() throws Exception {}
    
    public String execute() throws Exception {
        executedCalled = true;

        return returnedResult;
    }

    public void setReturnedResult(String returnedResult) {
        this.returnedResult = returnedResult;
    }

    public boolean isExecutedCalled() {
        return executedCalled;
    }

    public Object getAction() {
        return action;
    }

    public void setAction(Object action) {
        this.action = action;
    }

    public String getActionName() {
        return actionName;
    }

    public void setActionName(String actionName) {
        this.actionName = actionName;
    }

    public ActionConfig getConfig() {
        return config;
    }

    public void setConfig(ActionConfig config) {
        this.config = config;
    }

    public boolean getExecuteResult() {
        return executeResult;
    }

    public void setExecuteResult(boolean executeResult) {
        this.executeResult = executeResult;
    }

    public ActionInvocation getInvocation() {
        return invocation;
    }

    public void setInvocation(ActionInvocation invocation) {
        this.invocation = invocation;
    }

    public String getNamespace() {
        return namespace;
    }

    public void setNamespace(String namespace) {
        this.namespace = namespace;
    }

    public String getMethod() {
        return method;
    }

    public void setMethod(String method) {
        this.method = method;
        methodSpecified = StringUtils.isNotEmpty(method);
    }

    public boolean isMethodSpecified()
    {
        return methodSpecified;
    }

    public void setMethodSpecified(boolean methodSpecified)
    {
        this.methodSpecified = methodSpecified;
    }

}
