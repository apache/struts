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

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.apache.struts2.ActionContext;
import org.apache.struts2.ActionInvocation;
import org.apache.struts2.inject.Inject;
import org.apache.struts2.ognl.OgnlUtil;
import org.apache.struts2.util.TextParseUtil;
import org.apache.struts2.util.TextParser;
import org.apache.struts2.util.ValueStack;
import org.apache.struts2.util.reflection.ReflectionException;
import org.apache.struts2.util.reflection.ReflectionProvider;

import java.util.Map;

/**
 * Interceptors marked with this interface support dynamic parameter evaluation at action invocation time.
 * Parameters are set during interceptor creation (factory time), then re-evaluated during each action
 * invocation to resolve expressions like ${someValue}.
 * <p>
 * This enables both:
 * <ul>
 *   <li>Static configuration in interceptor stacks (e.g., allowedTypes="image/png,image/jpeg")</li>
 *   <li>Dynamic expressions evaluated per-request (e.g., maximumSize="${maxUploadSize}")</li>
 * </ul>
 * <p>
 * The {@link Interceptor#init()} method is called after initial parameter setting, so interceptors
 * can rely on configured values during initialization. Expression parameters (containing ${...})
 * are re-evaluated at invocation time via {@link LazyParamInjector}.
 *
 * @since 2.5.9
 */
public interface WithLazyParams<P extends InterceptorParams> {

    /**
     * @return a fresh holder for one invocation, seeded from the configured values
     * @since 7.3.0
     */
    P newLazyParams();

    /**
     * Invoked in place of {@link Interceptor#intercept(ActionInvocation)} when lazy params apply.
     *
     * @param lazyParams params resolved for this invocation only
     * @since 7.3.0
     */
    String intercept(ActionInvocation invocation, P lazyParams) throws Exception;

    class LazyParamInjector {

        private static final Logger LOG = LogManager.getLogger(LazyParamInjector.class);

        protected OgnlUtil ognlUtil;
        protected TextParser textParser;
        protected ReflectionProvider reflectionProvider;

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
        public void setReflectionProvider(ReflectionProvider reflectionProvider) {
            this.reflectionProvider = reflectionProvider;
        }

        @Inject
        public void setOgnlUtil(OgnlUtil ognlUtil) {
            this.ognlUtil = ognlUtil;
        }

        /**
         * Resolves configured params into a per-invocation holder, leaving the interceptor untouched.
         * <p>
         * A {@code ${...}} expression that resolves to null or an empty value is not written: the
         * holder keeps its seeded configuration value and is notified via
         * {@link InterceptorParams#unresolved(String)}. This also catches an expression that
         * legitimately evaluates to an empty string, which is indistinguishable from a failed
         * resolution (see {@link #isUnresolved}); for a fail-closed policy such as an allowlist,
         * treating both as unusable is the safe reading, so a broken expression cannot silently
         * relax a validation policy.
         *
         * @since 7.3.0
         */
        public <P extends InterceptorParams> P resolveInto(P target, Map<String, String> params, ActionContext invocationContext) {
            for (Map.Entry<String, String> entry : params.entrySet()) {
                String paramName = entry.getKey();
                String rawValue = entry.getValue();
                Object paramValue = textParser.evaluate(new char[]{'$'}, rawValue, valueEvaluator, TextParser.DEFAULT_LOOP_COUNT);

                if (isUnresolved(rawValue, paramValue)) {
                    LOG.warn("Param [{}] of [{}] could not be resolved from expression [{}]; keeping the configured value",
                            paramName, target.getClass().getName(), rawValue);
                    target.unresolved(paramName);
                    continue;
                }
                try {
                    // throwPropertyExceptions=true so a param with no matching property on the holder is
                    // reported rather than silently ignored; OgnlUtil only warns in devMode otherwise
                    ognlUtil.setProperty(paramName, paramValue, target, invocationContext.getContextMap(), true);
                } catch (ReflectionException e) {
                    LOG.warn("Param [{}] cannot be applied to [{}]; check the interceptor configuration",
                            paramName, target.getClass().getName(), e);
                }
            }
            return target;
        }

        /**
         * A {@code ${...}} param is treated as unresolved when its evaluated value is null or empty.
         * <p>
         * {@link org.apache.struts2.util.OgnlTextParser} yields the same empty string both when an
         * expression fails to resolve and when it resolves to a legitimately empty value — there is
         * no way to tell the two apart from the parser's output alone. This method does not attempt
         * to; a param that legitimately evaluates to an empty string is therefore also reported as
         * unresolved. That is a deliberate fail-closed choice: for a security-sensitive param (e.g.
         * an allowlist), silently accepting an unintended empty value is worse than refusing it.
         */
        private boolean isUnresolved(String rawValue, Object paramValue) {
            return rawValue != null
                    && rawValue.contains("${")
                    && (paramValue == null || paramValue.toString().isEmpty());
        }
    }
}
