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

import java.util.concurrent.atomic.AtomicBoolean;
import java.util.concurrent.atomic.AtomicInteger;
import org.apache.commons.lang3.BooleanUtils;

/**
 * Default OGNL Cache factory implementation.
 *
 * Currently used for Expression cache and BeanInfo cache creation.
 *
 * @param <Key> The type for the cache key entries
 * @param <Value> The type for the cache value entries
 */
public class DefaultOgnlCacheFactory<Key, Value> implements OgnlCacheFactory<Key, Value> {

    private final AtomicBoolean useLRUCache = new AtomicBoolean(false);
    private final AtomicInteger cacheMaxSize = new AtomicInteger(25000);

    @Override
    public OgnlCache<Key, Value> buildOgnlCache() {
        return buildOgnlCache(getCacheMaxSize(), 16, 0.75f, getUseLRUCache());
    }

    @Override
    public OgnlCache<Key, Value> buildOgnlCache(int evictionLimit, int initialCapacity, float loadFactor, boolean lruCache) {
        if (lruCache) {
            return new OgnlLRUCache<>(evictionLimit, initialCapacity, loadFactor);
        } else {
            return new OgnlDefaultCache<>(evictionLimit, initialCapacity, loadFactor);
        }
    }

    @Override
    public int getCacheMaxSize() {
        return cacheMaxSize.get();
    }

    protected void setCacheMaxSize(String maxSize) {
        cacheMaxSize.set(Integer.parseInt(maxSize));
    }

    @Override
    public boolean getUseLRUCache() {
        return useLRUCache.get();
    }

    protected void setUseLRUCache(String useLRUMode) {
        useLRUCache.set(BooleanUtils.toBoolean(useLRUMode));
    }
}
