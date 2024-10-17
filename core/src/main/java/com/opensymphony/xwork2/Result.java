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

/**
 * {@inheritDoc}
 *
 * @deprecated since 6.7.0, use {@link org.apache.struts2.Result} instead.
 */
@Deprecated
public interface Result extends org.apache.struts2.Result {

    @Override
    default void execute(org.apache.struts2.ActionInvocation invocation) throws Exception {
        execute(ActionInvocation.adapt(invocation));
    }

    void execute(ActionInvocation invocation) throws Exception;

    static Result adapt(org.apache.struts2.Result actualResult) {
        if (actualResult instanceof Result) {
            return (Result) actualResult;
        }
        return actualResult != null ? new LegacyAdapter(actualResult) : null;
    }

    class LegacyAdapter implements Result {

        private final org.apache.struts2.Result adaptee;

        private LegacyAdapter(org.apache.struts2.Result adaptee) {
            this.adaptee = adaptee;
        }

        @Override
        public void execute(ActionInvocation invocation) throws Exception {
            adaptee.execute(ActionInvocation.adapt(invocation));
        }
    }
}
