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
 * {@inheritDoc}
 *
 * @deprecated since 6.7.0, use {@link org.apache.struts2.interceptor.PreResultListener} instead.
 */
@Deprecated
public interface PreResultListener extends org.apache.struts2.interceptor.PreResultListener {

    @Override
    default void beforeResult(org.apache.struts2.ActionInvocation invocation, String resultCode) {
        beforeResult(ActionInvocation.adapt(invocation), resultCode);
    }

    void beforeResult(ActionInvocation invocation, String resultCode);

    static PreResultListener adapt(org.apache.struts2.interceptor.PreResultListener actualListener) {
        if (actualListener instanceof PreResultListener) {
            return (PreResultListener) actualListener;
        }
        return actualListener != null ? new LegacyAdapter(actualListener) : null;
    }

    class LegacyAdapter implements PreResultListener {

        private final org.apache.struts2.interceptor.PreResultListener adaptee;

        private LegacyAdapter(org.apache.struts2.interceptor.PreResultListener adaptee) {
            this.adaptee = adaptee;
        }

        @Override
        public void beforeResult(ActionInvocation invocation, String resultCode) {
            adaptee.beforeResult(invocation, resultCode);
        }
    }
}
