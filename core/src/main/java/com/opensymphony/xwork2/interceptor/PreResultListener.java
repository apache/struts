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
package com.opensymphony.xwork2.interceptor;

import com.opensymphony.xwork2.ActionInvocation;

/**
 * PreResultListeners may be registered with an {@link ActionInvocation} to get a callback after the
 * {@link com.opensymphony.xwork2.Action} has been executed but before the {@link com.opensymphony.xwork2.Result}
 * is executed.
 *
 * @author Jason Carreira
 */
public interface PreResultListener {

    /**
     * This callback method will be called after the {@link com.opensymphony.xwork2.Action} execution and
     * before the {@link com.opensymphony.xwork2.Result} execution.
     *
     * @param invocation  the action invocation
     * @param resultCode  the result code returned by the action (eg. <code>success</code>).
     */
    void beforeResult(ActionInvocation invocation, String resultCode);

}
