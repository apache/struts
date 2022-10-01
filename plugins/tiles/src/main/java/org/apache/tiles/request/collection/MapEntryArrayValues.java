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
import java.util.Objects;

/**
 * Multi-valued map entry.
 *
 * @param <K> The key type.
 * @param <V> The value type.
 */
public class MapEntryArrayValues<K, V> extends MapEntry<K, V[]> {

    /**
     * Constructor.
     *
     * @param key The key of the entry.
     * @param value The array of values.
     * @param modifiable If <code>true</code> the entry is modifiable.
     */
    public MapEntryArrayValues(K key, V[] value, boolean modifiable) {
        super(key, value, modifiable);
    }

    /**
     * <p>Returns the hashcode for this entry.</p>
     *
     * @return The and'ed hashcode of the key and value
     */
    @Override
    public int hashCode() {
        int valueHash = 0;
        V[] value = getValue();
        if (value != null) {
            for (V v : value) {
                valueHash += v.hashCode();
            }
        }

        return (this.getKey() == null ? 0 : this.getKey().hashCode()) ^ valueHash;
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
            Map.Entry<K, V[]> entry = (Map.Entry<K, V[]>) o;
            if (this.getKey() == null ? entry.getKey() == null : this
                    .getKey().equals(entry.getKey())) {
                V[] values = getValue();
                V[] otherValues = entry.getValue();
                if (values != null) {
                    if (otherValues != null) {
                        if (values.length == otherValues.length) {
                            boolean same = true;
                            for (int i = 0; i < values.length && same; i++) {
                                same = Objects.equals(values[i], otherValues[i]);
                            }
                            return same;
                        }
                    } else {
                        return false;
                    }
                } else {
                    return otherValues == null;
                }
            }
        }
        return false;
    }
}
