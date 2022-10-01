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
package org.apache.tiles.request.velocity.extractor;

import java.util.Enumeration;

import org.apache.tiles.request.attribute.AttributeExtractor;
import org.apache.velocity.context.Context;

/**
 * Extracts attributes from Velocity context..
 */
public class VelocityScopeExtractor implements AttributeExtractor {

    /**
     * The Velocity context.
     */
    private Context context;

    /**
     * Constructor.
     *
     * @param context The Velocity context.
     */
    public VelocityScopeExtractor(Context context) {
        this.context = context;
    }

    @Override
    public void removeValue(String name) {
        context.remove(name);
    }

    @Override
    public Enumeration<String> getKeys() {
        return new KeyEnumeration(context.getKeys());
    }

    @Override
    public Object getValue(String key) {
        return context.get(key);
    }

    @Override
    public void setValue(String key, Object value) {
        context.put(key, value);
    }

    /**
     * Enumerates an array.
     */
    private static class KeyEnumeration implements Enumeration<String> {

        /**
         * The current index.
         */
        private int index = 0;

        /**
         * The array to enumerate.
         */
        private Object[] keys;

        /**
         * Constructor.
         *
         * @param keys The array to enumerate.
         */
        public KeyEnumeration(Object[] keys) {
            this.keys = keys;
        }

        @Override
        public boolean hasMoreElements() {
            return index < keys.length;
        }

        @Override
        public String nextElement() {
            return (String) keys[index++];
        }
    }
}
