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

import java.util.Collections;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.function.Function;

/**
 * <p>A basic OGNL LRU cache implementation.</p>
 *
 * <p>The implementation utilizes a {@link Collections#synchronizedMap(java.util.Map)}
 * backed by a {@link LinkedHashMap}.  May be replaced by a more efficient implementation in the future.</p>
 *
 * <p>Setting too low an eviction limit will produce more overhead than value.</p>
 * <p>Setting too high an eviction limit may also produce more overhead than value.</p>
 * <p>An appropriate eviction limit will need to be determined on an individual application basis.</p>
 *
 * @param <K> The type for the cache key entries
 * @param <V> The type for the cache value entries
 */
public class OgnlLRUCache<K, V> implements OgnlCache<K, V> {

    private final Map<K, V> ognlLRUCache;
    private final AtomicInteger cacheEvictionLimit;

    public OgnlLRUCache(int evictionLimit, int initialCapacity, float loadFactor) {
        cacheEvictionLimit = new AtomicInteger(evictionLimit);
        // Access-order mode selected (order mode true in LinkedHashMap constructor).
        ognlLRUCache = Collections.synchronizedMap(new LinkedHashMap<>(initialCapacity, loadFactor, true) {
            @Override
            protected boolean removeEldestEntry(Map.Entry<K, V> eldest) {
                return size() > cacheEvictionLimit.get();
            }
        });
    }

    @Override
    public V get(K key) {
        return ognlLRUCache.get(key);
    }

    @Override
    public V computeIfAbsent(K key, Function<? super K, ? extends V> mappingFunction) {
        return ognlLRUCache.computeIfAbsent(key, mappingFunction);
    }

    @Override
    public void put(K key, V value) {
        ognlLRUCache.put(key, value);
    }

    @Override
    public void putIfAbsent(K key, V value) {
        ognlLRUCache.putIfAbsent(key, value);
    }

    @Override
    public int size() {
        return ognlLRUCache.size();
    }

    @Override
    public void clear() {
        ognlLRUCache.clear();
    }

    @Override
    public int getEvictionLimit() {
        return this.cacheEvictionLimit.get();
    }

    @Override
    public void setEvictionLimit(int cacheEvictionLimit) {
        if (cacheEvictionLimit < size()) {
            clear();
        }
        this.cacheEvictionLimit.set(cacheEvictionLimit);
    }
}
