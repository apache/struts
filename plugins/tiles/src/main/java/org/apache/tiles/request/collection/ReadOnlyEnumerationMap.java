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

import org.apache.tiles.request.attribute.HasKeys;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Enumeration;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.NoSuchElementException;
import java.util.Set;

import static org.apache.tiles.request.collection.CollectionUtil.enumerationSize;
import static org.apache.tiles.request.collection.CollectionUtil.key;

/**
 * Wraps an {@link HasKeys} object into a read-only map.
 *
 * @param <V> The type of the values.
 */
public class ReadOnlyEnumerationMap<V> implements Map<String, V> {

    /**
     * The request.
     */
    protected HasKeys<V> request;

    /**
     * Constructor.
     *
     * @param request The request object to use.
     */
    public ReadOnlyEnumerationMap(HasKeys<V> request) {
        this.request = request;
    }

    /**
     * {@inheritDoc}
     */
    public void clear() {
        throw new UnsupportedOperationException();
    }

    /**
     * {@inheritDoc}
     */
    public boolean containsKey(Object key) {
        return (request.getValue(key(key)) != null);
    }

    /**
     * {@inheritDoc}
     */
    @SuppressWarnings("unchecked")
    public boolean containsValue(Object value) {
        V realValue = (V) value;
        for (Enumeration<String> keysIt = request.getKeys(); keysIt.hasMoreElements(); ) {
            if (realValue.equals(request.getValue(keysIt.nextElement()))) {
                return true;
            }
        }
        return false;
    }

    /**
     * {@inheritDoc}
     */
    public Set<Entry<String, V>> entrySet() {
        return new ReadOnlyEnumerationMapEntrySet();
    }

    /**
     * {@inheritDoc}
     */
    public V get(Object key) {
        return (request.getValue(key(key)));
    }

    /**
     * {@inheritDoc}
     */
    public boolean isEmpty() {
        return !request.getKeys().hasMoreElements();
    }

    /**
     * {@inheritDoc}
     */
    public Set<String> keySet() {
        return new KeySet(request);
    }

    /**
     * {@inheritDoc}
     */
    public V put(String key, V value) {
        throw new UnsupportedOperationException();
    }

    /**
     * {@inheritDoc}
     */
    public void putAll(Map<? extends String, ? extends V> map) {
        throw new UnsupportedOperationException();
    }

    /**
     * {@inheritDoc}
     */
    public V remove(Object key) {
        throw new UnsupportedOperationException();
    }

    /**
     * {@inheritDoc}
     */
    public int size() {
        return enumerationSize(request.getKeys());
    }

    /**
     * {@inheritDoc}
     */
    public Collection<V> values() {
        return new ReadOnlyEnumerationMapValuesCollection();
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public boolean equals(Object obj) {
        if (obj == null) {
            return false;
        }
        if (!(obj instanceof ReadOnlyEnumerationMap)) {
            return false;
        }
        @SuppressWarnings("unchecked")
        HasKeys<V> otherRequest = ((ReadOnlyEnumerationMap<V>) obj).request;
        boolean retValue = true;
        Set<String> otherKeys;
        otherKeys = new HashSet<>();
        for (Enumeration<String> attribs = otherRequest.getKeys(); attribs.hasMoreElements(); ) {
            otherKeys.add(attribs.nextElement());
        }
        for (Enumeration<String> attribs = request.getKeys(); attribs.hasMoreElements() && retValue; ) {
            String parameterName = attribs.nextElement();
            retValue = request.getValue(parameterName).equals(otherRequest.getValue(parameterName));
            otherKeys.remove(parameterName);
        }

        return retValue && otherKeys.isEmpty();
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public int hashCode() {
        int retValue = 0;
        for (Enumeration<String> attribs = request.getKeys(); attribs
            .hasMoreElements(); ) {
            String parameterName = attribs.nextElement();
            V value = request.getValue(parameterName);
            retValue += parameterName.hashCode() ^ (value == null ? 0 : value.hashCode());
        }
        return retValue;
    }

    /**
     * Entry set implementation for {@link ReadOnlyEnumerationMap}.
     */
    class ReadOnlyEnumerationMapEntrySet implements Set<Entry<String, V>> {

        @Override
        public boolean add(Entry<String, V> e) {
            throw new UnsupportedOperationException();
        }

        @Override
        public boolean addAll(
            Collection<? extends Entry<String, V>> c) {
            throw new UnsupportedOperationException();
        }

        @Override
        public void clear() {
            throw new UnsupportedOperationException();
        }

        @SuppressWarnings("unchecked")
        @Override
        public boolean contains(Object o) {
            return containsEntry((Entry<String, V>) o);
        }

        @SuppressWarnings("unchecked")
        @Override
        public boolean containsAll(Collection<?> c) {
            Collection<Entry<String, V>> realCollection =
                (Collection<Entry<String, V>>) c;
            for (Entry<String, V> entry : realCollection) {
                if (!containsEntry(entry)) {
                    return false;
                }
            }
            return true;
        }

        @Override
        public boolean isEmpty() {
            return ReadOnlyEnumerationMap.this.isEmpty();
        }

        @Override
        public Iterator<Entry<String, V>> iterator() {
            return new ReadOnlyEnumerationMapEntrySetIterator();
        }

        @Override
        public boolean remove(Object o) {
            throw new UnsupportedOperationException();
        }

        @Override
        public boolean removeAll(Collection<?> c) {
            throw new UnsupportedOperationException();
        }

        @Override
        public boolean retainAll(Collection<?> c) {
            throw new UnsupportedOperationException();
        }

        @Override
        public int size() {
            return ReadOnlyEnumerationMap.this.size();
        }

        @Override
        public Object[] toArray() {
            return toList().toArray();
        }

        @Override
        public <T> T[] toArray(T[] a) {
            return toList().toArray(a);
        }

        /**
         * Checks whether the entry is present.
         *
         * @param entry The entry to check.
         * @return <code>true</code> if the entry is present.
         */
        protected boolean containsEntry(Entry<String, V> entry) {
            V storedValue = request.getValue(key(entry.getKey()));
            return storedValue != null && storedValue.equals(entry.getValue());
        }

        /**
         * Turns this set into a list.
         *
         * @return The list.
         */
        private List<Entry<String, V>> toList() {
            List<Entry<String, V>> entries = new ArrayList<>();
            Enumeration<String> names = request.getKeys();
            while (names.hasMoreElements()) {
                entries.add(extractNextEntry(names));
            }
            return entries;
        }

        /**
         * Returns the next entry, given the enumeration.
         *
         * @param names The enumeration to get the next key from.
         * @return The next entry.
         */
        private MapEntry<String, V> extractNextEntry(
            Enumeration<String> names) {
            String name = names.nextElement();
            return new MapEntry<>(name, request.getValue(name), false);
        }

        /**
         * Iterates entries of {@link ReadOnlyEnumerationMap}.
         */
        private class ReadOnlyEnumerationMapEntrySetIterator implements Iterator<Entry<String, V>> {

            /**
             * Enumerates keys.
             */
            private final Enumeration<String> namesEnumeration = request.getKeys();

            @Override
            public boolean hasNext() {
                return namesEnumeration.hasMoreElements();
            }

            @Override
            public Entry<String, V> next() {
                if (namesEnumeration.hasMoreElements()) {
                    return extractNextEntry(namesEnumeration);
                }
                throw new NoSuchElementException();
            }

            @Override
            public void remove() {
                throw new UnsupportedOperationException();
            }

        }
    }

    /**
     * Values collection for {@link ReadOnlyEnumerationMap}.
     */
    private class ReadOnlyEnumerationMapValuesCollection implements Collection<V> {

        @Override
        public boolean add(V e) {
            throw new UnsupportedOperationException();
        }

        @Override
        public boolean addAll(Collection<? extends V> c) {
            throw new UnsupportedOperationException();
        }

        @Override
        public void clear() {
            throw new UnsupportedOperationException();
        }

        @Override
        public boolean contains(Object o) {
            return containsValue(o);
        }

        @SuppressWarnings("unchecked")
        @Override
        public boolean containsAll(Collection<?> c) {
            Collection<String> realCollection = (Collection<String>) c;
            List<String> valueList = new ArrayList<>(realCollection);
            for (Enumeration<String> keysEnum = request.getKeys(); keysEnum.hasMoreElements(); ) {
                valueList.remove(request.getValue(keysEnum.nextElement()));
                if (valueList.isEmpty()) {
                    return true;
                }
            }
            return false;
        }

        @Override
        public boolean isEmpty() {
            return ReadOnlyEnumerationMap.this.isEmpty();
        }

        @Override
        public Iterator<V> iterator() {
            return new ReadOnlyEnumerationMapValuesCollectionIterator();
        }

        @Override
        public boolean remove(Object o) {
            throw new UnsupportedOperationException();
        }

        @Override
        public boolean removeAll(Collection<?> c) {
            throw new UnsupportedOperationException();
        }

        @Override
        public boolean retainAll(Collection<?> c) {
            throw new UnsupportedOperationException();
        }

        @Override
        public int size() {
            return ReadOnlyEnumerationMap.this.size();
        }

        @Override
        public Object[] toArray() {
            return toList().toArray();
        }

        @Override
        public <T> T[] toArray(T[] a) {
            return toList().toArray(a);
        }

        /**
         * Turns this collection into a list.
         *
         * @return The list.
         */
        private List<V> toList() {
            List<V> entries = new ArrayList<>();
            Enumeration<String> names = request.getKeys();
            while (names.hasMoreElements()) {
                entries.add(request.getValue(names.nextElement()));
            }
            return entries;
        }

        /**
         * Iterates values of {@link ReadOnlyEnumerationMap}.
         */
        private class ReadOnlyEnumerationMapValuesCollectionIterator implements Iterator<V> {

            /**
             * Enumerates attribute keys.
             */
            private final Enumeration<String> namesEnumeration = request.getKeys();

            @Override
            public boolean hasNext() {
                return namesEnumeration.hasMoreElements();
            }

            @Override
            public V next() {
                if (namesEnumeration.hasMoreElements()) {
                    return request.getValue(namesEnumeration.nextElement());
                }
                throw new NoSuchElementException();
            }

            @Override
            public void remove() {
                throw new UnsupportedOperationException();
            }
        }
    }
}
