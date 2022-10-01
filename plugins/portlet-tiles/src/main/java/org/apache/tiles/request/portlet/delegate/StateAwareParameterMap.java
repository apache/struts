/*
 * $Id$
 *
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
package org.apache.tiles.request.portlet.delegate;

import java.util.Collection;
import java.util.Map;
import java.util.Set;

/**
 * Parameter map to be used when the response is a {@link javax.portlet.StateAwareResponse}.
 */
public class StateAwareParameterMap implements Map<String, String[]> {

    /**
     * The request parameter map.
     */
    private final Map<String, String[]> requestMap;

    /**
     * The response parameter map.
     */
    private final Map<String, String[]> responseMap;

    /**
     * Constructor.
     *
     * @param requestMap The request parameter map.
     * @param responseMap The response parameter map.
     */
    public StateAwareParameterMap(Map<String, String[]> requestMap, Map<String, String[]> responseMap) {
        this.requestMap = requestMap;
        this.responseMap = responseMap;
    }

    @Override
    public void clear() {
        responseMap.clear();
    }

    @Override
    public boolean containsKey(Object key) {
        return requestMap.containsKey(key);
    }

    @Override
    public boolean containsValue(Object value) {
        return requestMap.containsValue(value);
    }

    @Override
    public Set<Entry<String, String[]>> entrySet() {
        return requestMap.entrySet();
    }

    @Override
    public String[] get(Object key) {
        return requestMap.get(key);
    }

    @Override
    public boolean isEmpty() {
        return requestMap.isEmpty();
    }

    @Override
    public Set<String> keySet() {
        return requestMap.keySet();
    }

    @Override
    public String[] put(String key, String[] value) {
        return responseMap.put(key, value);
    }

    @Override
    public void putAll(Map<? extends String, ? extends String[]> m) {
        responseMap.putAll(m);
    }

    @Override
    public String[] remove(Object key) {
        return responseMap.remove(key);
    }

    @Override
    public int size() {
        return requestMap.size();
    }

    @Override
    public Collection<String[]> values() {
        return requestMap.values();
    }
}
