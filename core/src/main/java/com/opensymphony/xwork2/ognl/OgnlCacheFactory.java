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
 * @param <Key> The type for the cache key entries
 * @param <Value> The type for the cache value entries
 */
interface OgnlCacheFactory<Key, Value> {
    OgnlCache<Key, Value> buildOgnlCache();
    OgnlCache<Key, Value> buildOgnlCache(int evictionLimit, int initialCapacity, float loadFactor, boolean lruCache);
    int getCacheMaxSize();
    boolean getUseLRUCache();
}
