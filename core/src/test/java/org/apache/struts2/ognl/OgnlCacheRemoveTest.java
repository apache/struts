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

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNull;

public class OgnlCacheRemoveTest {

    private void assertRemoveContract(OgnlCache<String, String> cache) {
        cache.put("k", "v");
        assertEquals("v", cache.get("k"));
        assertEquals("remove returns previous value", "v", cache.remove("k"));
        assertNull("entry gone after remove", cache.get("k"));
        assertNull("remove of absent key returns null", cache.remove("absent"));
    }

    @Test
    public void caffeineCacheRemove() {
        assertRemoveContract(new OgnlCaffeineCache<>(10, 16));
    }

    @Test
    public void defaultCacheRemove() {
        assertRemoveContract(new OgnlDefaultCache<>(10, 16, 0.75f));
    }

    @Test
    public void lruCacheRemove() {
        assertRemoveContract(new OgnlLRUCache<>(10, 16, 0.75f));
    }
}
