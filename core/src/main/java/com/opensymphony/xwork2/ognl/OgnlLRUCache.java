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

import java.util.Collections;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.concurrent.atomic.AtomicInteger;

/**
 * A basic OGNL LRU cache implementation.
 * 
 * The implementation utilizes a {@link Collections#synchronizedMap(java.util.Map)}
 * backed by a {@link LinkedHashMap}.  May be replaced by a more efficient implementation in the future.
 * 
 * Setting too low an eviction limit will produce more overhead than value.
 * Setting too high an eviction limit may also produce more overhead than value.
 * An appropriate eviction limit will need to be determined on an individual application basis.
 * 
 * @param <Key> The type for the cache key entries
 * @param <Value> The type for the cache value entries
 */
public class OgnlLRUCache<Key, Value> implements OgnlCache<Key, Value> {

    private final Map<Key, Value> ognlLRUCache;
    private final AtomicInteger cacheEvictionLimit = new AtomicInteger(2500);

    public OgnlLRUCache(int evictionLimit, int initialCapacity, float loadFactor) {
        this.cacheEvictionLimit.set(evictionLimit);
        // Access-order mode selected (order mode true in LinkedHashMap constructor).
        ognlLRUCache = Collections.synchronizedMap (new LinkedHashMap<Key, Value>(initialCapacity, loadFactor, true) {
            @Override
            protected boolean removeEldestEntry(Map.Entry<Key,Value> eldest) {
                return (this.size() > cacheEvictionLimit.get());
            }
        });
    }

    @Override
    public Value get(Key key) {
        return ognlLRUCache.get(key);
    }

    @Override
    public void put(Key key, Value value) {
        ognlLRUCache.put(key, value);
    }

    @Override
    public void putIfAbsent(Key key, Value value) {
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
        this.cacheEvictionLimit.set(cacheEvictionLimit);
    }

}
