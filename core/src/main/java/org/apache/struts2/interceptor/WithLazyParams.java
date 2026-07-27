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

import org.apache.struts2.ActionInvocation;
import org.apache.struts2.ActionContext;
import org.apache.struts2.inject.Inject;
import org.apache.struts2.ognl.OgnlUtil;
import org.apache.struts2.util.TextParseUtil;
import org.apache.struts2.util.TextParser;
import org.apache.struts2.util.ValueStack;

import java.util.Map;

/**
 * Interceptors marked with this interface support dynamic parameter evaluation at action invocation time.
 * <p>
 * This enables both:
 * <ul>
 *   <li>Static configuration in interceptor stacks (e.g., allowedTypes="image/png,image/jpeg")</li>
 *   <li>Dynamic expressions evaluated per-request (e.g., maximumSize="${maxUploadSize}")</li>
 * </ul>
 * <p>
 * The {@link Interceptor#init()} method is called after initial parameter setting, so interceptors
 * can rely on configured values during initialization. Expression parameters (containing ${...})
 * are re-evaluated at invocation time via {@link LazyParamInjector}. Interceptors that need
 * invocation-scoped lazy parameter state should additionally implement {@link InvocationScoped}.
 *
 * @since 2.5.9
 */
public interface WithLazyParams {

    /**
     * Optional extension for interceptors that need lazy params isolated per invocation instead of
     * being written back into the long-lived interceptor instance.
     */
    interface InvocationScoped<P> extends WithLazyParams, Interceptor {

        /**
         * Creates the per-invocation holder used for lazily resolved interceptor params.
         */
        P newLazyParams();

        /**
         * Intercepts using the per-invocation lazy params resolved for the current request.
         */
        String intercept(ActionInvocation invocation, P lazyParams) throws Exception;

        @Override
        default String intercept(ActionInvocation invocation) throws Exception {
            return intercept(invocation, newLazyParams());
        }
    }

    class LazyParamInjector {

        protected OgnlUtil ognlUtil;
        protected TextParser textParser;
        private final TextParseUtil.ParsedValueEvaluator valueEvaluator;

        public LazyParamInjector(final ValueStack valueStack) {
            // no asType !!!
            valueEvaluator = valueStack::findValue;
        }

        @Inject
        public void setTextParser(TextParser textParser) {
            this.textParser = textParser;
        }

        @Inject
        public void setOgnlUtil(OgnlUtil ognlUtil) {
            this.ognlUtil = ognlUtil;
        }

        public <P> P injectParams(InvocationScoped<P> interceptor, Map<String, String> params, ActionContext invocationContext) {
            P lazyParams = interceptor.newLazyParams();
            injectParams(lazyParams, params, invocationContext);
            return lazyParams;
        }

        public Interceptor injectParams(Interceptor interceptor, Map<String, String> params, ActionContext invocationContext) {
            injectParams((Object) interceptor, params, invocationContext);
            return interceptor;
        }

        private void injectParams(Object target, Map<String, String> params, ActionContext invocationContext) {
            for (Map.Entry<String, String> entry : params.entrySet()) {
                Object paramValue = textParser.evaluate(new char[]{'$'}, entry.getValue(), valueEvaluator, TextParser.DEFAULT_LOOP_COUNT);
                ognlUtil.setProperty(entry.getKey(), paramValue, target, invocationContext.getContextMap());
            }
        }
    }
}
