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

import com.opensymphony.xwork2.inject.Inject;
import org.apache.struts2.StrutsConstants;

/**
 * Default OGNL Expression Cache factory implementation.
 *
 * Currently used for Expression cache creation.
 *
 * @param <Key> The type for the cache key entries
 * @param <Value> The type for the cache value entries
 */
public class DefaultOgnlExpressionCacheFactory<Key, Value> extends DefaultOgnlCacheFactory<Key, Value>
    implements ExpressionCacheFactory<Key, Value> {

    @Override
    @Inject(value = StrutsConstants.STRUTS_OGNL_EXPRESSION_CACHE_MAXSIZE, required = false)
    protected void setCacheMaxSize(String maxSize) {
        super.setCacheMaxSize(maxSize);
    }

    @Override
    @Inject(value = StrutsConstants.STRUTS_OGNL_EXPRESSION_CACHE_LRU_MODE, required = false)
    protected void setUseLRUCache(String useLRUMode) {
        super.setUseLRUCache(useLRUMode);
    }

}
