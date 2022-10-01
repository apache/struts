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
package org.apache.tiles.request.collection;

import org.apache.tiles.request.attribute.HasAddableKeys;

import java.util.Collection;
import java.util.Map;
import java.util.Set;

/**
 * Exposes an {@link HasAddableKeys} object as a put/get (no remove) map.
 */
public class AddableParameterMap extends ReadOnlyEnumerationMap<String> {

    /**
     * The request.
     */
    private final HasAddableKeys<String> request;

    /**
     * Constructor.
     *
     * @param request The request object to use.
     */
    public AddableParameterMap(HasAddableKeys<String> request) {
        super(request);
        this.request = request;
    }

    /**
     * {@inheritDoc}
     */
    public Set<Entry<String, String>> entrySet() {
        return new AddableParameterEntrySet();
    }

    /**
     * {@inheritDoc}
     */
    public String put(String key, String value) {
        String oldValue = request.getValue(key);
        request.setValue(key, value);
        return oldValue;
    }


    /**
     * {@inheritDoc}
     */
    public void putAll(Map<? extends String, ? extends String> map) {
        for (Entry<? extends String, ? extends String> entry : map
            .entrySet()) {
            request.setValue(entry.getKey(), entry.getValue());
        }
    }


    /**
     * Entry set implementation for {@link AddableParameterMap}.
     */
    private class AddableParameterEntrySet extends ReadOnlyEnumerationMap<String>.ReadOnlyEnumerationMapEntrySet {

        @Override
        public boolean add(Entry<String, String> e) {
            request.setValue(e.getKey(), e.getValue());
            return true;
        }

        @Override
        public boolean addAll(
            Collection<? extends Entry<String, String>> c) {
            for (Entry<String, String> entry : c) {
                request.setValue(entry.getKey(), entry.getValue());
            }
            return true;
        }
    }
}
