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

/**
 * Provides default implementations of optional lifecycle methods
 */
public abstract class AbstractInterceptor implements ConditionalInterceptor {

    private boolean disabled;

    /**
     * Does nothing
     */
    @Override
    public void init() {
    }

    /**
     * Does nothing
     */
    @Override
    public void destroy() {
    }

    /**
     * Override to handle interception
     */
    @Override
    public abstract String intercept(ActionInvocation invocation) throws Exception;

    /**
     * Allows to skip executing a given interceptor, just define {@code <param name="disabled">true</param>}
     * or use other way to override interceptor's parameters, see
     * <a href="https://struts.apache.org/core-developers/interceptors#interceptor-parameter-overriding">docs</a>.
     * @param disable if set to true, execution of a given interceptor will be skipped.
     */
    public void setDisabled(String disable) {
        this.disabled = Boolean.parseBoolean(disable);
    }

    @Override
    public boolean shouldIntercept(ActionInvocation invocation) {
        return !this.disabled;
    }
}
