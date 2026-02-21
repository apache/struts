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

import org.apache.commons.lang3.EnumUtils;
import org.apache.struts2.StrutsConstants;
import org.apache.struts2.inject.Inject;

/**
 * Struts proxy cache factory implementation.
 * Used for creating caches for proxy detection operations.
 *
 * @param &lt;Key&gt;   The type for the cache key entries
 * @param &lt;Value&gt; The type for the cache value entries
 * @since 7.2.0
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
