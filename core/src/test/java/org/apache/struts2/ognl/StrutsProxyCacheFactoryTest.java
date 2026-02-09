/*
 * Licensed to the Apache Software Foundation (ASF) under one
 * or more contributor license agreements.  See the NOTICE file
 * distributed with this work for additional information
 * regarding copyright ownership.  The ASF licenses this file
 * to you under the Apache License, Version 2.0 (the
 * "License"); you may not use this file except in compliance
 * with the License.  You may obtain a copy of the License at
 *
 *  http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing,
 * software distributed under the License is distributed on an
 * "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY
 * KIND, either express or implied.  See the License for the
 * specific language governing permissions and limitations
 * under the License.
 */
package org.apache.struts2.ognl;

import org.junit.Test;

import static org.assertj.core.api.Assertions.assertThat;

/**
 * Tests for {@link StrutsProxyCacheFactory}.
 */
public class StrutsProxyCacheFactoryTest {

    @Test
    public void testCreateBasicCache() {
        StrutsProxyCacheFactory<String, Boolean> factory = new StrutsProxyCacheFactory<>("1000", "basic");

        OgnlCache<String, Boolean> cache = factory.buildOgnlCache();

        assertThat(cache).isNotNull();
        assertThat(cache).isInstanceOf(OgnlDefaultCache.class);
        assertThat(cache.getEvictionLimit()).isEqualTo(1000);
    }

    @Test
    public void testCreateLruCache() {
        StrutsProxyCacheFactory<String, Boolean> factory = new StrutsProxyCacheFactory<>("500", "lru");

        OgnlCache<String, Boolean> cache = factory.buildOgnlCache();

        assertThat(cache).isNotNull();
        assertThat(cache).isInstanceOf(OgnlLRUCache.class);
        assertThat(cache.getEvictionLimit()).isEqualTo(500);
    }

    @Test
    public void testCreateWtlfuCache() {
        StrutsProxyCacheFactory<String, Boolean> factory = new StrutsProxyCacheFactory<>("2000", "wtlfu");

        OgnlCache<String, Boolean> cache = factory.buildOgnlCache();

        assertThat(cache).isNotNull();
        assertThat(cache).isInstanceOf(OgnlCaffeineCache.class);
        assertThat(cache.getEvictionLimit()).isEqualTo(2000);
    }

    @Test
    public void testCacheTypeIgnoresCase() {
        StrutsProxyCacheFactory<String, Boolean> factory = new StrutsProxyCacheFactory<>("1000", "BASIC");

        OgnlCache<String, Boolean> cache = factory.buildOgnlCache();

        assertThat(cache).isInstanceOf(OgnlDefaultCache.class);
    }

    @Test
    public void testGetCacheMaxSize() {
        StrutsProxyCacheFactory<String, Boolean> factory = new StrutsProxyCacheFactory<>("5000", "basic");

        assertThat(factory.getCacheMaxSize()).isEqualTo(5000);
    }

    @Test
    public void testGetDefaultCacheType() {
        StrutsProxyCacheFactory<String, Boolean> factory = new StrutsProxyCacheFactory<>("1000", "lru");

        assertThat(factory.getDefaultCacheType()).isEqualTo(OgnlCacheFactory.CacheType.LRU);
    }
}
