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
package org.apache.struts2.factory;

import org.apache.struts2.ActionContext;
import org.apache.struts2.ActionInvocation;
import org.apache.struts2.XWorkTestCase;
import org.apache.struts2.config.entities.InterceptorConfig;
import org.apache.struts2.interceptor.AbstractInterceptor;
import org.apache.struts2.interceptor.WithLazyParams;
import org.apache.struts2.util.ValueStack;
import org.apache.struts2.util.ValueStackFactory;

import java.util.Collections;

public class DefaultInterceptorFactoryTest extends XWorkTestCase {

    public void testBuildInterceptorDefersTypedLazyParamsUntilInvocationTime() throws Exception {
        DefaultInterceptorFactory factory = new DefaultInterceptorFactory();
        container.inject(factory);

        InterceptorConfig config = new InterceptorConfig.Builder("typedLazy", TypedLazyInterceptor.class.getName())
                .addParam("maximumSize", "${maxFileSize}")
                .addParam("mode", "strict")
                .build();

        TypedLazyInterceptor interceptor =
                (TypedLazyInterceptor) factory.buildInterceptor(config, Collections.emptyMap());

        assertNull("Dynamic Long param should not be written into the singleton interceptor at factory time",
                interceptor.getMaximumSize());
        assertEquals("strict", interceptor.getMode());

        ValueStack valueStack = container.getInstance(ValueStackFactory.class).createValueStack();
        valueStack.push(new LazyParamAction(42L));

        ActionContext context = ActionContext.of(valueStack.getContext())
                .withContainer(container)
                .withValueStack(valueStack)
                .bind();

        try {
            WithLazyParams.LazyParamInjector lazyParamInjector = new WithLazyParams.LazyParamInjector(valueStack);
            container.inject(lazyParamInjector);

            TypedLazyInterceptor.LazyParams lazyParams =
                    lazyParamInjector.injectParams(interceptor, config.getParams(), context);

            assertEquals(Long.valueOf(42L), lazyParams.getMaximumSize());
            assertEquals("strict", lazyParams.getMode());
        } finally {
            ActionContext.clear();
        }
    }

    public static final class TypedLazyInterceptor extends AbstractInterceptor
            implements WithLazyParams<TypedLazyInterceptor.LazyParams> {
        private Long maximumSize;
        private String mode;

        public Long getMaximumSize() {
            return maximumSize;
        }

        public void setMaximumSize(Long maximumSize) {
            this.maximumSize = maximumSize;
        }

        public String getMode() {
            return mode;
        }

        public void setMode(String mode) {
            this.mode = mode;
        }

        @Override
        public LazyParams newLazyParams() {
            return new LazyParams(maximumSize, mode);
        }

        @Override
        public String intercept(ActionInvocation invocation) throws Exception {
            return WithLazyParams.super.intercept(invocation);
        }

        @Override
        public String intercept(ActionInvocation invocation, LazyParams lazyParams) throws Exception {
            return invocation.invoke();
        }

        public static final class LazyParams {
            private Long maximumSize;
            private String mode;

            private LazyParams(Long maximumSize, String mode) {
                this.maximumSize = maximumSize;
                this.mode = mode;
            }

            public Long getMaximumSize() {
                return maximumSize;
            }

            public void setMaximumSize(Long maximumSize) {
                this.maximumSize = maximumSize;
            }

            public String getMode() {
                return mode;
            }

            public void setMode(String mode) {
                this.mode = mode;
            }
        }
    }

    public static final class LazyParamAction {
        private final Long maxFileSize;

        private LazyParamAction(Long maxFileSize) {
            this.maxFileSize = maxFileSize;
        }

        public Long getMaxFileSize() {
            return maxFileSize;
        }
    }
}
