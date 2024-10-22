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
 * @deprecated since 6.7.0, use {@link org.apache.struts2.interceptor.Interceptor} instead.
 */
@Deprecated
public interface Interceptor extends org.apache.struts2.interceptor.Interceptor {

    @Override
    default String intercept(org.apache.struts2.ActionInvocation invocation) throws Exception {
        return intercept(ActionInvocation.adapt(invocation));
    }

    String intercept(ActionInvocation invocation) throws Exception;

    static Interceptor adapt(org.apache.struts2.interceptor.Interceptor actualInterceptor) {
        if (actualInterceptor instanceof org.apache.struts2.interceptor.ConditionalInterceptor) {
            return ConditionalInterceptor.adapt((org.apache.struts2.interceptor.ConditionalInterceptor) actualInterceptor);
        }
        if (actualInterceptor instanceof Interceptor) {
            return (Interceptor) actualInterceptor;
        }
        return actualInterceptor != null ? new LegacyAdapter(actualInterceptor) : null;
    }

    class LegacyAdapter implements Interceptor {

        private final org.apache.struts2.interceptor.Interceptor adaptee;

        protected LegacyAdapter(org.apache.struts2.interceptor.Interceptor adaptee) {
            this.adaptee = adaptee;
        }

        public org.apache.struts2.interceptor.Interceptor getAdaptee() {
            return adaptee;
        }

        @Override
        public String intercept(ActionInvocation invocation) throws Exception {
            return adaptee.intercept(invocation);
        }

        @Override
        public void destroy() {
            adaptee.destroy();
        }

        @Override
        public void init() {
            adaptee.init();
        }
    }
}
