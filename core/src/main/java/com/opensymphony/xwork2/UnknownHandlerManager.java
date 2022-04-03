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
package com.opensymphony.xwork2;

import com.opensymphony.xwork2.config.entities.ActionConfig;

import java.util.List;

/**
 * An unknown handler manager contains a list of UnknownHandler and iterates on them by order
 *
 * @see com.opensymphony.xwork2.DefaultUnknownHandlerManager
 */
public interface UnknownHandlerManager {

    Result handleUnknownResult(ActionContext actionContext, String actionName, ActionConfig actionConfig, String resultCode);

    /**
     * Tries to handle passed methodName if cannot find method should re
     *
     * @param action Action's instance
     * @param methodName method name to handle
     * @return Result representing result of given action method
     * @throws NoSuchMethodException if method can be handled by defined UnknownHandlers
     */
    Object handleUnknownMethod(Object action, String methodName) throws NoSuchMethodException;

    ActionConfig handleUnknownAction(String namespace, String actionName);

    boolean hasUnknownHandlers();

    List<UnknownHandler> getUnknownHandlers();

}
