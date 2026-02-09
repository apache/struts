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
package com.opensymphony.xwork2.ognl;

import com.opensymphony.xwork2.inject.Inject;
import org.apache.commons.lang3.EnumUtils;
import org.apache.struts2.StrutsConstants;

/**
 * Struts Proxy Cache factory implementation for ProxyUtil caches.
 * <p>
 * This factory is used to create caches for proxy detection in ProxyUtil.
 * The cache type and size can be configured via Struts constants.
 *
 * @param <Key>   The type for the cache key entries
 * @param <Value> The type for the cache value entries
 * @since 6.8.0
 */
public class StrutsProxyCacheFactory<Key, Value> extends DefaultOgnlCacheFactory<Key, Value>
        implements ProxyCacheFactory<Key, Value> {

    @Inject
    public StrutsProxyCacheFactory(
            @Inject(value = StrutsConstants.STRUTS_PROXY_CACHE_MAXSIZE) String cacheMaxSize,
            @Inject(value = StrutsConstants.STRUTS_PROXY_CACHE_TYPE) String defaultCacheType) {
        super(Integer.parseInt(cacheMaxSize), EnumUtils.getEnumIgnoreCase(CacheType.class, defaultCacheType));
    }
}
