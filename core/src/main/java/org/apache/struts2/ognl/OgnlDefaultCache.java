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

import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.function.Function;

/**
 * <p>Basic OGNL cache implementation.</p>
 *
 * <p>This implementation is backed by a {@link ConcurrentHashMap} that is cleared whenever the eviction limit is
 * surpassed.</p>
 *
 * <p>Setting a very high eviction limit simulates an unlimited cache.</p>
 * <p>Setting too low an eviction limit will make the cache ineffective.</p>
 *
 * @param <K> The type for the cache key entries
 * @param <V> The type for the cache value entries
 */
public class OgnlDefaultCache<K, V> implements OgnlCache<K, V> {

    private final ConcurrentHashMap<K, V> ognlCache;
    private final AtomicInteger cacheEvictionLimit;

    public OgnlDefaultCache(int evictionLimit, int initialCapacity, float loadFactor) {
        cacheEvictionLimit = new AtomicInteger(evictionLimit);
        ognlCache = new ConcurrentHashMap<>(initialCapacity, loadFactor);
    }

    @Override
    public V get(K key) {
        return ognlCache.get(key);
    }

    @Override
    public V computeIfAbsent(K key, Function<? super K, ? extends V> mappingFunction) {
        return ognlCache.computeIfAbsent(key, mappingFunction);
    }

    @Override
    public void put(K key, V value) {
        ognlCache.put(key, value);
        clearIfEvictionLimitExceeded();
    }

    @Override
    public void putIfAbsent(K key, V value) {
        ognlCache.putIfAbsent(key, value);
        clearIfEvictionLimitExceeded();
    }

    @Override
    public int size() {
        return ognlCache.size();
    }

    @Override
    public void clear() {
        ognlCache.clear();
    }

    @Override
    public int getEvictionLimit() {
        return cacheEvictionLimit.get();
    }

    @Override
    public void setEvictionLimit(int newCacheEvictionLimit) {
        cacheEvictionLimit.set(newCacheEvictionLimit);
    }

    /**
     * Clear the cache if the eviction limit has been exceeded.
     */
    private void clearIfEvictionLimitExceeded() {
        if (ognlCache.size() > cacheEvictionLimit.get()) {
            ognlCache.clear();
        }
    }
}
