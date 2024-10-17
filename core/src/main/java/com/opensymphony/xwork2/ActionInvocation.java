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

import com.opensymphony.xwork2.interceptor.PreResultListener;
import com.opensymphony.xwork2.util.ValueStack;

/**
 * {@inheritDoc}
 *
 * @deprecated since 6.7.0, use {@link org.apache.struts2.ActionInvocation} instead.
 */
@Deprecated
public interface ActionInvocation extends org.apache.struts2.ActionInvocation {

    @Override
    ActionContext getInvocationContext();

    @Override
    Result getResult() throws Exception;

    @Override
    default void addPreResultListener(org.apache.struts2.interceptor.PreResultListener listener) {
        addPreResultListener(PreResultListener.adapt(listener));
    }

    void addPreResultListener(PreResultListener listener);

    @Override
    default void setActionEventListener(org.apache.struts2.ActionEventListener listener) {
        setActionEventListener(ActionEventListener.adapt(listener));
    }

    void setActionEventListener(ActionEventListener listener);

    static ActionInvocation adapt(org.apache.struts2.ActionInvocation actualInvocation) {
        return actualInvocation != null ? new LegacyAdapter(actualInvocation) : null;
    }

    class LegacyAdapter implements ActionInvocation {

        private final org.apache.struts2.ActionInvocation adaptee;

        private LegacyAdapter(org.apache.struts2.ActionInvocation adaptee) {
            this.adaptee = adaptee;
        }

        @Override
        public Object getAction() {
            return adaptee.getAction();
        }

        @Override
        public boolean isExecuted() {
            return adaptee.isExecuted();
        }

        @Override
        public ActionContext getInvocationContext() {
            return ActionContext.adapt(adaptee.getInvocationContext());
        }

        @Override
        public ActionProxy getProxy() {
            return adaptee.getProxy();
        }

        @Override
        public Result getResult() throws Exception {
            return Result.adapt(adaptee.getResult());
        }

        @Override
        public String getResultCode() {
            return adaptee.getResultCode();
        }

        @Override
        public void setResultCode(String resultCode) {
            adaptee.setResultCode(resultCode);
        }

        @Override
        public ValueStack getStack() {
            return adaptee.getStack();
        }

        @Override
        public void addPreResultListener(PreResultListener listener) {
            adaptee.addPreResultListener(listener);
        }

        @Override
        public String invoke() throws Exception {
            return adaptee.invoke();
        }

        @Override
        public String invokeActionOnly() throws Exception {
            return adaptee.invokeActionOnly();
        }

        @Override
        public void setActionEventListener(ActionEventListener listener) {
            adaptee.setActionEventListener(listener);
        }

        @Override
        public void init(ActionProxy proxy) {
            adaptee.init(proxy);
        }
    }

}
