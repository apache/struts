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

import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.atomic.AtomicInteger;

/**
 * Default OGNL cache implementation.
 * 
 * Setting a very high eviction limit simulates an unlimited cache.
 * Setting too low an eviction limit will make the cache ineffective.
 * 
 * @param <Key> The type for the cache key entries
 * @param <Value> The type for the cache value entries
 */
public class OgnlDefaultCache<Key, Value> implements OgnlCache<Key, Value> {

    private final ConcurrentHashMap<Key, Value> ognlCache;
    private final AtomicInteger cacheEvictionLimit = new AtomicInteger(25000);

    public OgnlDefaultCache(int evictionLimit, int initialCapacity, float loadFactor) {
        this.cacheEvictionLimit.set(evictionLimit);
        ognlCache = new ConcurrentHashMap<>(initialCapacity, loadFactor);
    }

    @Override
    public Value get(Key key) {
        return ognlCache.get(key);
    }

    @Override
    public void put(Key key, Value value) {
        ognlCache.put(key, value);
        this.clearIfEvictionLimitExceeded();
    }

    @Override
    public void putIfAbsent(Key key, Value value) {
        ognlCache.putIfAbsent(key, value);
        this.clearIfEvictionLimitExceeded();
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
        return this.cacheEvictionLimit.get();
    }

    @Override
    public void setEvictionLimit(int cacheEvictionLimit) {
        this.cacheEvictionLimit.set(cacheEvictionLimit);
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
