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
package org.apache.struts2.interceptor.parameter;

import java.util.ArrayDeque;
import java.util.Deque;

/**
 * ThreadLocal holder for per-request parameter authorization state, used by deserializer-level
 * authorization (e.g. the REST plugin's Jackson module). All state — the {@link ParameterAuthorizer},
 * the target, the action, and the current property-path stack — is bound by
 * {@link org.apache.struts2.rest.ContentTypeInterceptor} (or other input-channel interceptors)
 * before invoking the deserializer, and unbound in a {@code finally} block afterwards.
 *
 * <p>Implementations that consult this context (e.g. {@code AuthorizingSettableBeanProperty}) call
 * {@link #isActive()} to decide whether to enforce authorization at all — when no context is bound
 * (default config, {@code requireAnnotations=false}), they short-circuit to the delegate behavior.</p>
 *
 * @since 7.2.0
 */
public final class ParameterAuthorizationContext {

    private static final ThreadLocal<State> STATE = new ThreadLocal<>();
    private static final ThreadLocal<Deque<String>> PATH_STACK = new ThreadLocal<>();

    private ParameterAuthorizationContext() {
        // utility
    }

    public static void bind(ParameterAuthorizer authorizer, Object target, Object action) {
        STATE.set(new State(authorizer, target, action));
    }

    public static void unbind() {
        STATE.remove();
        PATH_STACK.remove();
    }

    public static boolean isActive() {
        return STATE.get() != null;
    }

    /**
     * Authorizes a parameter at the given path against the bound authorizer. Returns {@code true}
     * when no context is bound — callers that don't want enforcement at all should not bind context
     * in the first place; this default keeps wrapping deserializers safe for non-authorized requests.
     */
    public static boolean isAuthorized(String parameterPath) {
        State state = STATE.get();
        if (state == null) {
            return true;
        }
        return state.authorizer.isAuthorized(parameterPath, state.target, state.action);
    }

    public static void pushPath(String fullPath) {
        pathStack().push(fullPath);
    }

    public static void popPath() {
        Deque<String> stack = PATH_STACK.get();
        if (stack != null && !stack.isEmpty()) {
            stack.pop();
        }
    }

    /**
     * @return the current top-of-stack path prefix, or empty string if none
     */
    public static String currentPathPrefix() {
        Deque<String> stack = PATH_STACK.get();
        if (stack == null || stack.isEmpty()) {
            return "";
        }
        return stack.peek();
    }

    /**
     * Builds the full path for a property at the current nesting level: {@code prefix.propertyName}
     * (or just {@code propertyName} when at the root).
     */
    public static String pathFor(String propertyName) {
        String prefix = currentPathPrefix();
        return prefix.isEmpty() ? propertyName : prefix + "." + propertyName;
    }

    private static Deque<String> pathStack() {
        Deque<String> stack = PATH_STACK.get();
        if (stack == null) {
            stack = new ArrayDeque<>();
            PATH_STACK.set(stack);
        }
        return stack;
    }

    private static final class State {
        final ParameterAuthorizer authorizer;
        final Object target;
        final Object action;

        State(ParameterAuthorizer authorizer, Object target, Object action) {
            this.authorizer = authorizer;
            this.target = target;
            this.action = action;
        }
    }
}
