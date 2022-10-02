/*
 * Licensed to the Apache Software Foundation (ASF) under one
 * or more contributor license agreements.  See the NOTICE file
 * distributed with this work for additional information
 * regarding copyright ownership.  The ASF licenses this file
 * to you under the Apache License, Version 2.0 (the
 * "License"); you may not use this file except in compliance
 * with the License.  You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing,
 * software distributed under the License is distributed on an
 * "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY
 * KIND, either express or implied.  See the License for the
 * specific language governing permissions and limitations
 * under the License.
 */

package org.apache.tiles.template;

import org.apache.tiles.request.Request;

import java.util.Deque;
import java.util.LinkedList;
import java.util.Map;

/**
 * Utilities to work with compose stacks.
 *
 * @since 3.0.0
 */
public final class ComposeStackUtil {

    /**
     * The name of the attribute that holds to compose stack.
     */
    public static final String COMPOSE_STACK_ATTRIBUTE_NAME = "org.apache.tiles.template.COMPOSE_STACK";

    /**
     * Private constructor to avoid instantiation.
     */
    private ComposeStackUtil() {

    }

    /**
     * Finds the first ancestor in the stack, that is assignable to the given class.
     *
     * @param composeStack To compose stack to evaluate.
     * @param clazz The class to check.
     * @return The first ancestor that is assignable to the class, or null if not found.
     * @since 3.0.0
     */
    public static Object findAncestorWithClass(Deque<Object> composeStack, Class<?> clazz) {
        for (Object obj : composeStack) {
            if (clazz.isAssignableFrom(obj.getClass())) {
                return obj;
            }
        }

        return null;
    }

    /**
     * Returns the current compose stack, or creates a new one if not present.
     *
     * @param request The request.
     * @return The compose stack.
     * @since 3.0.0
     */
    @SuppressWarnings("unchecked")
    public static Deque<Object> getComposeStack(Request request) {
        Map<String, Object> requestScope = request.getContext("request");
        Deque<Object> composeStack = (Deque<Object>) requestScope
                .get(COMPOSE_STACK_ATTRIBUTE_NAME);
        if (composeStack == null) {
            composeStack = new LinkedList<>();
            requestScope.put(ComposeStackUtil.COMPOSE_STACK_ATTRIBUTE_NAME, composeStack);
        }
        return composeStack;
    }
}
