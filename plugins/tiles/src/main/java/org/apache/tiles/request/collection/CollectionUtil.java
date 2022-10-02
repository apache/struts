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

import java.util.Enumeration;

/**
 * Utilities for requests.
 */
public final class CollectionUtil {

    /**
     * Constructor.
     */
    private CollectionUtil() {

    }

    /**
     * Returns the string representation of the key.
     *
     * @param key The key.
     * @return The string representation of the key.
     * @throws IllegalArgumentException If the key is <code>null</code>.
     */
    public static String key(Object key) {
        if (key == null) {
            throw new IllegalArgumentException();
        } else if (key instanceof String) {
            return ((String) key);
        } else {
            return (key.toString());
        }
    }

    /**
     * Returns the number of elements in an enumeration, by iterating it.
     *
     * @param keys The enumeration.
     * @return The number of elements.
     */
    public static int enumerationSize(Enumeration<?> keys) {
        int n = 0;
        while (keys.hasMoreElements()) {
            keys.nextElement();
            n++;
        }
        return n;
    }
}
