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
package com.opensymphony.xwork2.ognl;

import com.github.benmanes.caffeine.cache.Cache;
import com.github.benmanes.caffeine.cache.Caffeine;

/**
 * <p>This OGNL Cache implementation is backed by {@link Caffeine} which uses the Window TinyLfu algorithm.</p>
 *
 * <p>An appropriate eviction limit should be chosen for your specific application based on factors and requirements
 * such as:</p>
 * <ul>
 *     <li>Quantity and complexity of actions</li>
 *     <li>Volume of requests</li>
 *     <li>Rate limits and attack potential/patterns</li>
 *     <li>Memory constraints</li>
 * </ul>
 *
 * @param <Key>   The type for the cache key entries
 * @param <Value> The type for the cache value entries
 */
public class OgnlCaffeineCache<Key, Value> implements OgnlCache<Key, Value> {

    private final Cache<Key, Value> cache;
    private final int evictionLimit;

    public OgnlCaffeineCache(int evictionLimit, int initialCapacity, float loadFactor) {
        this.evictionLimit = evictionLimit;
        this.cache = Caffeine.newBuilder().initialCapacity(initialCapacity).maximumSize(evictionLimit).build();
    }

    @Override
    public Value get(Key key) {
        return cache.getIfPresent(key);
    }

    @Override
    public void put(Key key, Value value) {
        cache.put(key, value);
    }

    @Override
    public void putIfAbsent(Key key, Value value) {
        if (cache.getIfPresent(key) == null) {
            cache.put(key, value);
        }
    }

    @Override
    public int size() {
        return Math.toIntExact(cache.estimatedSize());
    }

    @Override
    public void clear() {
        cache.invalidateAll();
    }

    @Override
    public int getEvictionLimit() {
        return evictionLimit;
    }

    @Override
    public void setEvictionLimit(int cacheEvictionLimit) {
        throw new UnsupportedOperationException("Cannot change eviction limit on a Caffeine cache after initialisation");
    }
}
