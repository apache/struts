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

import static org.apache.tiles.request.collection.CollectionUtil.*;

import java.util.Collection;
import java.util.Enumeration;
import java.util.LinkedHashSet;
import java.util.Map;
import java.util.Set;

import org.apache.tiles.request.attribute.AttributeExtractor;

/**
 * Exposes a scope context as a <String, Object> map.
 */
public class ScopeMap extends ReadOnlyEnumerationMap<Object> {

    /**
     * The context to read.
     */
    private final AttributeExtractor context;

    /**
     * Constructor.
     *
     * @param context The servlet context to use.
     */
    public ScopeMap(AttributeExtractor context) {
        super(context);
        this.context = context;
    }

    /** {@inheritDoc} */
    public void clear() {
        Enumeration<String> keys = context.getKeys();
        while (keys.hasMoreElements()) {
            context.removeValue(keys.nextElement());
        }
    }

    /** {@inheritDoc} */
    public Set<Map.Entry<String, Object>> entrySet() {
        return new ScopeEntrySet();
    }

    /** {@inheritDoc} */
    public Set<String> keySet() {
        return new RemovableKeySet(context);
    }

    /** {@inheritDoc} */
    public Object put(String key, Object value) {
        String strKey = key(key);
        Object previous = context.getValue(strKey);
        context.setValue(strKey, value);
        return previous;
    }

    /** {@inheritDoc} */
    public void putAll(Map<? extends String, ?> map) {
        for (String key : map.keySet()) {
            context.setValue(key, map.get(key));
        }
    }

    /** {@inheritDoc} */
    public Object remove(Object key) {
        String strKey = key(key);
        Object previous = context.getValue(strKey);
        context.removeValue(strKey);
        return (previous);
    }

    /**
     * Entry set implementation for {@link ScopeMap}.
     */
    private class ScopeEntrySet extends ReadOnlyEnumerationMap<Object>.ReadOnlyEnumerationMapEntrySet {

        @Override
        public boolean add(Map.Entry<String, Object> e) {
            String key = e.getKey();
            Object value = e.getValue();
            Object oldValue = get(key);
            if (oldValue == null || !oldValue.equals(value)) {
                context.setValue(key, value);
                return true;
            }
            return false;
        }

        @Override
        public boolean addAll(
                Collection<? extends Map.Entry<String, Object>> c) {
            boolean retValue = false;
            for (Map.Entry<String, Object> entry : c) {
                retValue |= add(entry);
            }
            return retValue;
        }

        @Override
        public void clear() {
            ScopeMap.this.clear();
        }

        @SuppressWarnings("unchecked")
        @Override
        public boolean remove(Object o) {
            Map.Entry<String, Object> entry = (Map.Entry<String, Object>) o;
            String key = entry.getKey();
            Object currentValue = context.getValue(key);
            if (currentValue != null && currentValue.equals(entry.getValue())) {
                context.removeValue(key);
                return true;
            }
            return false;
        }

        @SuppressWarnings("unchecked")
        @Override
        public boolean removeAll(Collection<?> c) {
            Collection<Map.Entry<String, Object>> realCollection = (Collection<Map.Entry<String, Object>>) c;
            boolean retValue = false;
            for (Map.Entry<String, Object> entry : realCollection) {
                retValue |= remove(entry);
            }
            return retValue;
        }

        @SuppressWarnings("unchecked")
        @Override
        public boolean retainAll(Collection<?> c) {
            Collection<Map.Entry<String, Object>> realCollection = (Collection<Map.Entry<String, Object>>) c;
            boolean retValue = false;
            Set<String> keysToRemove = new LinkedHashSet<>();
            for (Enumeration<String> keys = context.getKeys(); keys.hasMoreElements();) {
                String key = keys.nextElement();
                Object value = context.getValue(key);
                Map.Entry<String, Object> entry = new MapEntry<>(key, value, false);
                if (!realCollection.contains(entry)) {
                    retValue = true;
                    keysToRemove.add(key);
                }
            }
            for (String key : keysToRemove) {
                context.removeValue(key);
            }
            return retValue;
        }
    }
}
