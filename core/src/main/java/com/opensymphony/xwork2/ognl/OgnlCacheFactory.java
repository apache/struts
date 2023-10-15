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

/**
 * Used by {@link com.opensymphony.xwork2.ognl.OgnlUtil} to create appropriate OGNL
 * caches based on configuration.
 *
 * @param <Key>   The type for the cache key entries
 * @param <Value> The type for the cache value entries
 */
public interface OgnlCacheFactory<Key, Value> {
    OgnlCache<Key, Value> buildOgnlCache();

    /**
     * Note that if {@code lruCache} is {@code false}, the cache type could still be LRU if the default cache type is
     * configured as such.
     * @deprecated since 6.4.0, use {@link #buildOgnlCache(int, int, float, CacheType)}
     */
    @Deprecated
    default OgnlCache<Key, Value> buildOgnlCache(int evictionLimit,
                                                 int initialCapacity,
                                                 float loadFactor,
                                                 boolean lruCache) {
        if (lruCache) {
            return buildOgnlCache(evictionLimit, initialCapacity, loadFactor, CacheType.SYNC_LINKED_LRU);
        } else {
            return buildOgnlCache(evictionLimit,
                    initialCapacity,
                    loadFactor,
                    lruCache ? CacheType.SYNC_LINKED_LRU : getDefaultCacheType());
        }
    }

    /**
     * @param evictionLimit   maximum capacity of the cache where applicable for cache type chosen
     * @param initialCapacity initial capacity of the cache where applicable for cache type chosen
     * @param loadFactor      load factor of the cache where applicable for cache type chosen
     * @param cacheType       type of cache to build
     * @return a new cache instance
     */
    OgnlCache<Key, Value> buildOgnlCache(int evictionLimit, int initialCapacity, float loadFactor, CacheType cacheType);

    int getCacheMaxSize();

    /**
     * @deprecated since 6.4.0
     */
    @Deprecated
    default boolean getUseLRUCache() {
        return CacheType.SYNC_LINKED_LRU.equals(getDefaultCacheType());
    }

    CacheType getDefaultCacheType();

    enum CacheType {
        CONCURRENT_BASIC,
        SYNC_LINKED_LRU,
        CAFFEINE_WTLFU
    }
}
