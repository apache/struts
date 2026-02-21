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
package com.opensymphony.xwork2.util;

import java.util.function.Supplier;

/**
 * A thread-safe lazy reference that computes its value on first access using
 * double-checked locking. The cached value can be invalidated via {@link #reset()},
 * causing the next {@link #get()} call to recompute the value.
 *
 * @param <T> the type of the lazily computed value
 * @since 6.9.0
 */
public class LazyRef<T> implements Supplier<T> {

    private final Supplier<T> factory;
    private volatile T value;

    /**
     * Creates a new LazyRef with the given factory supplier.
     *
     * @param factory the supplier used to compute the value; must not be null
     */
    public LazyRef(Supplier<T> factory) {
        this.factory = factory;
    }

    /**
     * Returns the cached value, computing it on first access or after a {@link #reset()}.
     *
     * @return the computed value
     */
    @Override
    public T get() {
        T result = value;
        if (result == null) {
            synchronized (this) {
                result = value;
                if (result == null) {
                    result = factory.get();
                    value = result;
                }
            }
        }
        return result;
    }

    /**
     * Invalidates the cached value so the next {@link #get()} call recomputes it.
     */
    public void reset() {
        value = null;
    }
}
