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
import java.util.Objects;

/**
 * ThreadLocal holder for per-request parameter authorization state, used by deserializer-level
 * authorization (e.g. the REST plugin's {@code ContentTypeInterceptor}). All state — the
 * {@link ParameterAuthorizer}, the target, the action, and the current property-path stack — is
 * bound by input-channel interceptors before invoking the deserializer, and unbound in a
 * {@code finally} block afterwards.
 *
 * <p>Implementations that consult this context (e.g. {@code AuthorizingSettableBeanProperty}) call
 * {@link #isActive()} to decide whether to enforce authorization at all — when no context is bound
 * (default config, {@code requireAnnotations=false}), they short-circuit to the delegate behavior.</p>
 *
 * @since 7.2.0
 */
public final class ParameterAuthorizationContext {

    private static final ThreadLocal<State> STATE = new ThreadLocal<>();
    private static final ThreadLocal<Deque<String>> PATH_STACK = ThreadLocal.withInitial(ArrayDeque::new);

    private ParameterAuthorizationContext() {
        // utility
    }

    /**
     * Binds an authorizer, target, and action to the current thread. {@code target} is the object
     * being populated — typically the action itself, or the model object for {@code ModelDriven}
     * actions (the same contract as {@link ParameterAuthorizer#isAuthorized}). {@code action} is
     * always the action instance. A subsequent call without an intervening {@link #unbind()} replaces
     * the prior state without resetting the path stack.
     *
     * @param authorizer the authorizer to use for this request; must not be {@code null}
     * @param target     the object being populated (action or model)
     * @param action     the action instance
     */
    public static void bind(ParameterAuthorizer authorizer, Object target, Object action) {
        Objects.requireNonNull(authorizer, "authorizer");
        STATE.set(new State(authorizer, target, action));
    }

    /**
     * Removes the bound authorizer state and clears the path stack for the current thread.
     * Safe to call even when no context has been bound.
     */
    public static void unbind() {
        STATE.remove();
        PATH_STACK.remove();
    }

    /**
     * Returns {@code true} if an authorizer has been bound on the current thread via {@link #bind}.
     */
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

    /**
     * Pushes the full cumulative path prefix onto the stack. Subsequent {@link #pathFor(String)}
     * calls will append {@code name} to this prefix. Callers building a collection-element prefix
     * (e.g. {@code items[0]}) must pass the full string including the suffix.
     *
     * @param cumulativePath the full path prefix to push (e.g. {@code "address"} or {@code "items[0]"})
     */
    public static void pushPath(String cumulativePath) {
        PATH_STACK.get().push(cumulativePath);
    }

    /**
     * Pops the top path prefix from the stack. Has no effect if the stack is empty.
     */
    public static void popPath() {
        Deque<String> stack = PATH_STACK.get();
        if (!stack.isEmpty()) {
            stack.pop();
        }
    }

    /**
     * @return the current top-of-stack path prefix, or empty string if none
     */
    public static String currentPathPrefix() {
        Deque<String> stack = PATH_STACK.get();
        if (stack.isEmpty()) {
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
