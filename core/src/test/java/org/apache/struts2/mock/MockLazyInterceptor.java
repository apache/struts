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
package org.apache.struts2.mock;

import org.apache.struts2.ActionInvocation;
import org.apache.struts2.SimpleAction;
import org.apache.struts2.interceptor.AbstractInterceptor;
import org.apache.struts2.interceptor.InterceptorParams;
import org.apache.struts2.interceptor.WithLazyParams;

public class MockLazyInterceptor extends AbstractInterceptor implements WithLazyParams<MockLazyInterceptor.MockLazyParams> {

    /**
     * Per-invocation holder, seeded from the configured values.
     */
    public static class MockLazyParams implements InterceptorParams {

        private String foo = "";
        private String bar = "";

        public void setFoo(String foo) {
            this.foo = foo;
        }

        public String getFoo() {
            return foo;
        }

        public void setBar(String bar) {
            this.bar = bar;
        }

        public String getBar() {
            return bar;
        }
    }

    private String foo = "";
    private String bar = "";

    public void setFoo(String foo) {
        this.foo = foo;
    }

    public String getFoo() {
        return foo;
    }

    public void setBar(String bar) {
        this.bar = bar;
    }

    public String getBar() {
        return bar;
    }

    @Override
    public MockLazyParams newLazyParams() {
        MockLazyParams params = new MockLazyParams();
        params.setFoo(foo);
        params.setBar(bar);
        return params;
    }

    @Override
    public String intercept(ActionInvocation invocation) throws Exception {
        return intercept(invocation, newLazyParams());
    }

    @Override
    public String intercept(ActionInvocation invocation, MockLazyParams lazyParams) throws Exception {
        if (invocation.getAction() instanceof SimpleAction) {
            ((SimpleAction) invocation.getAction()).setName(lazyParams.getFoo());
            // Only set blah if bar is configured (not empty)
            if (lazyParams.getBar() != null && !lazyParams.getBar().isEmpty()) {
                ((SimpleAction) invocation.getAction()).setBlah(lazyParams.getBar());
            }
        }
        return invocation.invoke();
    }
}
