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
package com.opensymphony.xwork2.mock;

import com.opensymphony.xwork2.ActionInvocation;
import com.opensymphony.xwork2.SimpleAction;
import com.opensymphony.xwork2.interceptor.AbstractInterceptor;
import com.opensymphony.xwork2.interceptor.Interceptor;
import com.opensymphony.xwork2.interceptor.WithLazyParams;
import org.junit.Assert;

public class MockLazyInterceptor extends AbstractInterceptor implements WithLazyParams {

    private String foo = "";

    public void setFoo(String foo) {
        this.foo = foo;
    }

    public String intercept(ActionInvocation invocation) throws Exception {
        if (invocation.getAction() instanceof SimpleAction) {
            ((SimpleAction) invocation.getAction()).setName(foo);
        }
        return invocation.invoke();
    }
}
