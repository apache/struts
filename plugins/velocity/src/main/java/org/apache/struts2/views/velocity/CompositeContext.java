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
package org.apache.struts2.views.velocity;

import org.apache.velocity.VelocityContext;
import org.apache.velocity.context.Context;

import java.util.Arrays;
import java.util.stream.StreamSupport;

import static java.util.Objects.requireNonNull;

/**
 * Simple immutable composite Velocity {@link Context} implementation that delegates to a collection of other contexts.
 * The order of the contexts is significant as it checks them in iteration order. This context should be wrapped in a
 * {@link org.apache.velocity.VelocityContext} or other chained context before use, using the provided static factory
 * methods or otherwise.
 *
 * @since 6.4.0
 */
public class CompositeContext implements Context {
    private final Iterable<? extends Context> contexts;

    public static VelocityContext composite(Iterable<? extends Context> contexts) {
        return new ChainedVelocityContext(new CompositeContext(contexts));
    }

    public static VelocityContext composite(Context ...contexts) {
        return new ChainedVelocityContext(new CompositeContext(contexts));
    }

    public CompositeContext(Context ...contexts) {
        this(Arrays.asList(contexts));
    }

    public CompositeContext(Iterable<? extends Context> contexts) {
        this.contexts = requireNonNull(contexts);
    }

    @Override
    public Object get(String key) {
        for (Context context : contexts) {
            Object value = context.get(key);
            if (value != null) {
                return value;
            }
        }
        return null;
    }

    @Override
    public boolean containsKey(String key) {
        return StreamSupport.stream(contexts.spliterator(), false).anyMatch(context -> context.containsKey(key));
    }

    /**
     * Union of all keys for all contexts.
     */
    @Override
    public String[] getKeys() {
        return StreamSupport.stream(contexts.spliterator(), false)
                .map(Context::getKeys).flatMap(Arrays::stream).distinct().toArray(String[]::new);
    }

    @Override
    public Object remove(String key) {
        throw new UnsupportedOperationException();
    }

    @Override
    public Object put(String key, Object value) {
        throw new UnsupportedOperationException();
    }
}
