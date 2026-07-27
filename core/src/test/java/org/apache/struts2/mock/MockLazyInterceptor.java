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
import org.apache.struts2.interceptor.WithLazyParams;

public class MockLazyInterceptor extends AbstractInterceptor implements WithLazyParams<MockLazyInterceptor.LazyParams> {

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
    public String intercept(ActionInvocation invocation) throws Exception {
        return WithLazyParams.super.intercept(invocation);
    }

    @Override
    public LazyParams newLazyParams() {
        return new LazyParams(foo, bar);
    }

    @Override
    public String intercept(ActionInvocation invocation, LazyParams lazyParams) throws Exception {
        if (invocation.getAction() instanceof SimpleAction) {
            ((SimpleAction) invocation.getAction()).setName(lazyParams.getFoo());
            // Only set blah if bar is configured (not empty)
            if (lazyParams.getBar() != null && !lazyParams.getBar().isEmpty()) {
                ((SimpleAction) invocation.getAction()).setBlah(lazyParams.getBar());
            }
        }
        return invocation.invoke();
    }

    public static final class LazyParams {
        private String foo = "";
        private String bar = "";

        private LazyParams(String foo, String bar) {
            this.foo = foo;
            this.bar = bar;
        }

        public String getFoo() {
            return foo;
        }

        public void setFoo(String foo) {
            this.foo = foo;
        }

        public String getBar() {
            return bar;
        }

        public void setBar(String bar) {
            this.bar = bar;
        }
    }
}
