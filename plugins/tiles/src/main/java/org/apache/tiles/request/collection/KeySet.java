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
import java.util.Iterator;
import java.util.List;
import java.util.NoSuchElementException;
import java.util.Set;

import static org.apache.tiles.request.collection.CollectionUtil.enumerationSize;
import static org.apache.tiles.request.collection.CollectionUtil.key;

/**
 * Exposes keys of a {@link HasKeys} object as a set.
 */
public class KeySet implements Set<String> {

    /**
     * The request to read.
     */
    private final HasKeys<?> request;

    /**
     * Constructor.
     *
     * @param request The request to read.
     */
    public KeySet(HasKeys<?> request) {
        this.request = request;
    }

    @Override
    public boolean add(String e) {
        throw new UnsupportedOperationException();
    }

    @Override
    public boolean addAll(Collection<? extends String> c) {
        throw new UnsupportedOperationException();
    }

    @Override
    public void clear() {
        throw new UnsupportedOperationException();
    }

    @Override
    public boolean contains(Object o) {
        return request.getValue(key(o)) != null;
    }

    @SuppressWarnings("unchecked")
    @Override
    public boolean containsAll(Collection<?> c) {
        Collection<String> realCollection = (Collection<String>) c;
        for (String key : realCollection) {
            if (request.getValue(key(key)) == null) {
                return false;
            }
        }
        return true;
    }

    @Override
    public boolean isEmpty() {
        return !request.getKeys().hasMoreElements();
    }

    @Override
    public Iterator<String> iterator() {
        return new KeySetIterator();
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
        return enumerationSize(request.getKeys());
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
     * Turns this set into a list.
     *
     * @return The corresponding list.
     */
    private List<String> toList() {
        List<String> entries = new ArrayList<String>();
        Enumeration<String> names = request.getKeys();
        while (names.hasMoreElements()) {
            entries.add(names.nextElement());
        }
        return entries;
    }

    /**
     * Iterates elements of {@link KeySet}.
     */
    private class KeySetIterator implements Iterator<String> {

        /**
         * The key names enumeration.
         */
        private final Enumeration<String> namesEnumeration = request.getKeys();

        @Override
        public boolean hasNext() {
            return namesEnumeration.hasMoreElements();
        }

        @Override
        public String next() {
            if (namesEnumeration.hasMoreElements()) {
                return namesEnumeration.nextElement();
            }
            throw new NoSuchElementException();
        }

        @Override
        public void remove() {
            throw new UnsupportedOperationException();
        }
    }
}
