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

/**
 * <p>Default OGNL Cache factory implementation.</p>
 *
 * <p>Currently used for Expression cache and BeanInfo cache creation.</p>
 *
 * @param <Key>   The type for the cache key entries
 * @param <Value> The type for the cache value entries
 */
public class DefaultOgnlCacheFactory<Key, Value> implements OgnlCacheFactory<Key, Value> {

    private static final int DEFAULT_INIT_CAPACITY = 16;
    private static final float DEFAULT_LOAD_FACTOR = 0.75f;

    private final CacheType defaultCacheType;
    private final int cacheMaxSize;
    private final int initialCapacity;

    public DefaultOgnlCacheFactory(int cacheMaxSize, CacheType defaultCacheType) {
        this(cacheMaxSize, defaultCacheType, DEFAULT_INIT_CAPACITY);
    }

    public DefaultOgnlCacheFactory(int cacheMaxSize, CacheType defaultCacheType, int initialCapacity) {
        this.cacheMaxSize = cacheMaxSize;
        this.defaultCacheType = defaultCacheType;
        this.initialCapacity = initialCapacity;
    }

    @Override
    public OgnlCache<Key, Value> buildOgnlCache() {
        return buildOgnlCache(getCacheMaxSize(), initialCapacity, DEFAULT_LOAD_FACTOR, defaultCacheType);
    }

    @Override
    public OgnlCache<Key, Value> buildOgnlCache(int evictionLimit,
                                                int initialCapacity,
                                                float loadFactor,
                                                CacheType cacheType) {
        return switch (cacheType) {
            case BASIC -> new OgnlDefaultCache<>(evictionLimit, initialCapacity, loadFactor);
            case LRU -> new OgnlLRUCache<>(evictionLimit, initialCapacity, loadFactor);
            case WTLFU -> new OgnlCaffeineCache<>(evictionLimit, initialCapacity);
        };
    }

    @Override
    public int getCacheMaxSize() {
        return cacheMaxSize;
    }

    @Override
    public CacheType getDefaultCacheType() {
        return defaultCacheType;
    }
}
