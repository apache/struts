/*
 * Licensed to the Apache Software Foundation (ASF) under one
 * or more contributor license agreements.  See the NOTICE file
 * distributed with this work for additional information
 * regarding copyright ownership.  The ASF licenses this file
 * to you under the Apache License, Version 2.0 (the
 * "License"); you may not use this file except in compliance
 * with the License.  You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing,
 * software distributed under the License is distributed on an
 * "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY
 * KIND, either express or implied.  See the License for the
 * specific language governing permissions and limitations
 * under the License.
 */
package org.apache.tiles.request.velocity;

import static org.apache.tiles.request.collection.CollectionUtil.key;

import java.util.HashSet;
import java.util.Set;

import org.apache.tiles.request.collection.ScopeMap;
import org.apache.tiles.request.velocity.extractor.VelocityScopeExtractor;
import org.apache.velocity.context.Context;

/**
 * <p>
 * Private implementation of <code>Map</code> for servlet request attributes.
 * </p>
 *
 */

final class VelocityScopeMap extends ScopeMap {

    /**
     * The request object to use.
     */
    private Context request = null;

    /**
     * Constructor.
     *
     * @param request The request object to use.
     */
    public VelocityScopeMap(Context request) {
        super(new VelocityScopeExtractor(request));
        this.request = request;
    }

    @Override
    public Object remove(Object key) {
        return request.remove(key(key));
    }

    @Override
    public Object put(String key, Object value) {
        return request.put(key, value);
    }

    /** {@inheritDoc} */
    public boolean containsKey(Object key) {
        return request.containsKey(key(key));
    }

    /** {@inheritDoc} */
    public boolean isEmpty() {
        return size() < 1;
    }

    /** {@inheritDoc} */
    public Set<String> keySet() {
        Set<String> set = new HashSet<String>();
        for (Object key : request.getKeys()) {
            set.add((String) key);
        }
        return (set);
    }

    /** {@inheritDoc} */
    public int size() {
        return request.getKeys().length;
    }
}
