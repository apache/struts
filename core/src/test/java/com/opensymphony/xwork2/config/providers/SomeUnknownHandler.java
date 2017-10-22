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
package com.opensymphony.xwork2.config.providers;

import com.opensymphony.xwork2.ActionContext;
import com.opensymphony.xwork2.Result;
import com.opensymphony.xwork2.UnknownHandler;
import com.opensymphony.xwork2.XWorkException;
import com.opensymphony.xwork2.config.entities.ActionConfig;

public class SomeUnknownHandler implements UnknownHandler{
    private ActionConfig actionConfig;
    private String actionMethodResult;

    public ActionConfig handleUnknownAction(String namespace, String actionName) throws XWorkException {
        return actionConfig;
    }

    public Object handleUnknownActionMethod(Object action, String methodName) {
        return actionMethodResult;
    }

    public Result handleUnknownResult(ActionContext actionContext, String actionName, ActionConfig actionConfig,
            String resultCode) throws XWorkException {
        return null;
    }

    public void setActionConfig(ActionConfig actionConfig) {
        this.actionConfig = actionConfig;
    }

    public void setActionMethodResult(String actionMethodResult) {
        this.actionMethodResult = actionMethodResult;
    }
}
