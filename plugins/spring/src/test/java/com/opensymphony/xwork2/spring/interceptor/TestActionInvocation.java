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
package com.opensymphony.xwork2.spring.interceptor;

import com.opensymphony.xwork2.*;
import com.opensymphony.xwork2.interceptor.PreResultListener;
import com.opensymphony.xwork2.util.ValueStack;

import java.lang.reflect.Method;

/**
 * @author Simon Stewart
 */
public class TestActionInvocation implements ActionInvocation {
    private Object action;
    private boolean executed;

    public TestActionInvocation(Object wrappedAction) {
        this.action = wrappedAction;
    }

    public Object getAction() {
        return action;
    }

    public boolean isExecuted() {
        return executed;
    }

    public ActionContext getInvocationContext() {
        return null;
    }

    public ActionProxy getProxy() {
        return null;
    }

    public Result getResult() throws Exception {
        return null;
    }

    public String getResultCode() {
        return null;
    }

    public void setResultCode(String resultCode) {

    }

    public ValueStack getStack() {
        return null;
    }

    public void addPreResultListener(PreResultListener listener) {
    }

    public String invoke() throws Exception {
        return invokeActionOnly();
    }

    public String invokeActionOnly() throws Exception {
        executed = true;
        Method method = action.getClass().getMethod("execute", new Class[0]);
        return (String) method.invoke(action, new Object[0]);
    }

    public void setActionEventListener(ActionEventListener listener) {
    }

    public void init(ActionProxy proxy)  {
    }

}
