/*
 * Licensed to the Apache Software Foundation (ASF) under one or more
 * contributor license agreements.  See the NOTICE file distributed with
 * this work for additional information regarding copyright ownership.
 * The ASF licenses this file to you under the Apache License, Version 2.0
 * (the "License"); you may not use this file except in compliance with
 * the License.  You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.apache.struts2.rest.handler.jackson;

import org.apache.struts2.interceptor.parameter.ParameterAuthorizationContext;

import java.util.ArrayDeque;
import java.util.Deque;

/**
 * Tracks the bounded subtree authorized by an explicitly opted-in Jackson any-setter.
 */
final class DynamicKeyAuthorizationContext {

    private static final ThreadLocal<Deque<Scope>> SCOPES = new ThreadLocal<>();

    private DynamicKeyAuthorizationContext() {
        // utility
    }

    static boolean isAuthorized(String path) {
        Deque<Scope> scopes = SCOPES.get();
        if (scopes == null || scopes.isEmpty()) {
            return ParameterAuthorizationContext.isAuthorized(path);
        }
        return remainingDepth(scopes.peek(), path) >= 0;
    }

    static int limitForNestedScope(String path, int requestedDepth) {
        if (requestedDepth < 0) {
            return -1;
        }
        Deque<Scope> scopes = SCOPES.get();
        if (scopes == null || scopes.isEmpty()) {
            return requestedDepth;
        }
        int remainingDepth = remainingDepth(scopes.peek(), path);
        return remainingDepth < 0 ? -1 : Math.min(requestedDepth, remainingDepth);
    }

    static void push(String basePath, int maxDepth) {
        Deque<Scope> scopes = SCOPES.get();
        if (scopes == null) {
            scopes = new ArrayDeque<>();
            SCOPES.set(scopes);
        }
        scopes.push(new Scope(basePath, maxDepth));
    }

    static void pop() {
        Deque<Scope> scopes = SCOPES.get();
        if (scopes != null && !scopes.isEmpty()) {
            scopes.pop();
        }
        if (scopes == null || scopes.isEmpty()) {
            SCOPES.remove();
        }
    }

    static boolean isActive() {
        Deque<Scope> scopes = SCOPES.get();
        return scopes != null && !scopes.isEmpty();
    }

    private static int remainingDepth(Scope scope, String path) {
        if (path == null || scope.basePath == null || !path.startsWith(scope.basePath)) {
            return -1;
        }
        if (path.length() == scope.basePath.length()) {
            return scope.maxDepth;
        }

        char boundary = path.charAt(scope.basePath.length());
        if (boundary != '.' && boundary != '[' && boundary != '(') {
            return -1;
        }

        int usedDepth = 0;
        for (int i = scope.basePath.length(); i < path.length(); i++) {
            char current = path.charAt(i);
            if (current == '.' || current == '[' || current == '(') {
                usedDepth++;
            }
        }
        return scope.maxDepth - usedDepth;
    }

    private static final class Scope {
        private final String basePath;
        private final int maxDepth;

        private Scope(String basePath, int maxDepth) {
            this.basePath = basePath;
            this.maxDepth = maxDepth;
        }
    }
}
