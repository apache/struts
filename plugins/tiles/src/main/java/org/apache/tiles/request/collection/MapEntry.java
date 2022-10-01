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

import java.util.Map;

/**
 * <p>Map.Entry implementation that can be constructed to either be read-only
 * or not.</p>
 *
 * @param <K> The key type.
 * @param <V> The value type.
 */

public class MapEntry<K, V> implements Map.Entry<K, V> {

    /**
     * <p>The entry key.</p>
     */
    private final K key;

    /**
     * <p>The entry value.</p>
     */
    private V value;

    /**
     * <p>Whether the entry can be modified.</p>
     */
    private final boolean modifiable;

    /**
     * <p>Creates a map entry that can either allow modifications or not.</p>
     *
     * @param key        The entry key
     * @param value      The entry value
     * @param modifiable Whether the entry should allow modification or not
     */
    public MapEntry(K key, V value, boolean modifiable) {
        this.key = key;
        this.value = value;
        this.modifiable = modifiable;
    }

    /**
     * <p>Gets the entry key.</p>
     *
     * @return The entry key
     */
    public K getKey() {
        return key;
    }

    /**
     * <p>Gets the entry value.</p>
     *
     * @return The entry key
     */
    public V getValue() {
        return value;
    }

    /**
     * <p>Sets the entry value if the entry can be modified.</p>
     *
     * @param val The new value
     * @return The old entry value
     * @throws UnsupportedOperationException If the entry cannot be modified
     */
    public V setValue(V val) {
        if (modifiable) {
            V oldVal = this.value;
            this.value = val;
            return oldVal;
        }
        throw new UnsupportedOperationException("The map entry is not modifiable");
    }

    /**
     * <p>Determines if this entry is equal to the passed object.</p>
     *
     * @param o The object to test
     * @return True if equal, else false
     */
    @Override
    @SuppressWarnings("unchecked")
    public boolean equals(Object o) {
        if (o instanceof Map.Entry) {
            Map.Entry<K, V> entry = (Map.Entry<K, V>) o;
            return (this.getKey() == null ? entry.getKey() == null : this
                .getKey().equals(entry.getKey()))
                && (this.getValue() == null ? entry.getValue() == null
                : this.getValue().equals(entry.getValue()));
        }
        return false;
    }

    /**
     * <p>Returns the hashcode for this entry.</p>
     *
     * @return The and'ed hashcode of the key and value
     */
    @Override
    public int hashCode() {
        return (this.getKey() == null ? 0 : this.getKey().hashCode())
            ^ (this.getValue() == null ? 0 : this.getValue().hashCode());
    }
}
