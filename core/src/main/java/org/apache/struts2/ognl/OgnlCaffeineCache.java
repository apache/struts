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

import com.github.benmanes.caffeine.cache.Cache;
import com.github.benmanes.caffeine.cache.Caffeine;

import java.util.function.Function;

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
 * @param <K> The type for the cache key entries
 * @param <V> The type for the cache value entries
 */
public class OgnlCaffeineCache<K, V> implements OgnlCache<K, V> {

    private final Cache<K, V> cache;

    public OgnlCaffeineCache(int evictionLimit, int initialCapacity) {
        this.cache = Caffeine.newBuilder().initialCapacity(initialCapacity).maximumSize(evictionLimit).build();
    }

    @Override
    public V get(K key) {
        return cache.getIfPresent(key);
    }

    @Override
    public V computeIfAbsent(K key, Function<? super K, ? extends V> mappingFunction) {
        return cache.asMap().computeIfAbsent(key, mappingFunction);
    }

    @Override
    public void put(K key, V value) {
        cache.put(key, value);
    }

    @Override
    public void putIfAbsent(K key, V value) {
        cache.asMap().putIfAbsent(key, value);
    }

    @Override
    public int size() {
        return cache.asMap().size();
    }

    @Override
    public void clear() {
        cache.invalidateAll();
    }

    @Override
    public int getEvictionLimit() {
        return Math.toIntExact(cache.policy().eviction().orElseThrow(IllegalStateException::new).getMaximum());
    }

    @Override
    public void setEvictionLimit(int cacheEvictionLimit) {
        cache.policy().eviction().orElseThrow(IllegalStateException::new).setMaximum(cacheEvictionLimit);
    }
}
