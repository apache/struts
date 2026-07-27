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

/**
 * Opt-in support for the {@code disabled} interceptor parameter.
 * <p>
 * Interceptors implementing {@link WithLazyParams} must have their params holder extend this
 * class to support {@code <param name="disabled">...</param>}; there is deliberately no
 * fallback to the interceptor instance, which would reintroduce shared mutable state.
 *
 * @since 7.3.0
 */
public class DisableParams implements InterceptorParams {

    private boolean disabled;

    public DisableParams() {
    }

    protected DisableParams(DisableParams other) {
        this.disabled = other.disabled;
    }

    /**
     * @param disable if {@code true}, execution of the interceptor is skipped for this invocation
     */
    public void setDisabled(String disable) {
        this.disabled = Boolean.parseBoolean(disable);
    }

    public boolean isDisabled() {
        return disabled;
    }
}
