/*
 * Copyright 2022 Apache Software Foundation.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.apache.struts2.ognl;

import java.util.function.Function;

import static java.util.Objects.requireNonNull;

/**
 * A basic cache interface for use with OGNL processing (such as Expression, BeanInfo). Implementation must be
 * thread-safe. All OGNL caches will have an eviction limit, but setting an extremely high value can simulate an
 * "effectively unlimited" cache.
 *
 * @param <K> The type for the cache key entries
 * @param <V> The type for the cache value entries
 */
public interface OgnlCache<K, V> {

    V get(K key);

    /**
     * @since 7.1
     */
    default V computeIfAbsent(K key,
                              Function<? super K, ? extends V> mappingFunction) {
        requireNonNull(mappingFunction);
        if (get(key) == null) {
            putIfAbsent(key, mappingFunction.apply(key));
        }
        return get(key);
    }

    void put(K key, V value);

    void putIfAbsent(K key, V value);

    int size();

    void clear();

    int getEvictionLimit();

    void setEvictionLimit(int cacheEvictionLimit);
}
