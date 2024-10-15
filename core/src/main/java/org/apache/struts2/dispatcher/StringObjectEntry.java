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
package org.apache.struts2.dispatcher;

import java.util.Map.Entry;

abstract class StringObjectEntry implements Entry<String, Object> {
    private final String key;
    private final Object value;

    StringObjectEntry(final String key, final Object value) {
        this.key = key;
        this.value = value;
    }

    @Override
    public String getKey() {
        return key;
    }

    @Override
    public Object getValue() {
        return value;
    }

    @Override
    public boolean equals(final Object obj) {
        if (!(obj instanceof Entry<?, ?> entry)) {
            return false;
        }

        return keyEquals(entry) && valueEquals(entry);
    }

    private boolean keyEquals(final Entry<?, ?> entry) {
        return (key == null) ? (entry.getKey() == null) : key.equals(entry.getKey());
    }

    private boolean valueEquals(Entry<?, ?> entry) {
        return (value == null) ? (entry.getValue() == null) : value.equals(entry.getValue());
    }

    @Override
    public int hashCode() {
        return ((key == null) ? 0 : key.hashCode()) ^ ((value == null) ? 0 : value.hashCode());
    }
}
