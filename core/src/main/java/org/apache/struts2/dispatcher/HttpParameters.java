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

import org.apache.struts2.interceptor.ParameterAware;

import java.util.Collection;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;
import java.util.TreeMap;
import java.util.TreeSet;

@SuppressWarnings("unchecked")
public class HttpParameters implements Map<String, Parameter>, Cloneable {

    private Map<String, Parameter> parameters;

    private HttpParameters(Map<String, Parameter> parameters) {
        this.parameters = parameters;
    }

    public static Builder create(Map requestParameterMap) {
        return new Builder(requestParameterMap);
    }

    public static Builder create() {
        return new Builder(new HashMap<String, Object>());
    }

    public HttpParameters remove(Set<String> paramsToRemove) {
        for (String paramName : paramsToRemove) {
            parameters.remove(paramName);
        }
        return this;
    }

    public HttpParameters remove(final String paramToRemove) {
        return remove(new HashSet<String>() {{
            add(paramToRemove);
        }});
    }

    public boolean contains(String name) {
        return parameters.containsKey(name);
    }

    /**
     * Access to this method will be restricted with the next versiob
     * @deprecated since 2.5.6, do not use it
     * TODO: reduce access level to `private`
     */
    @Deprecated
    public Map<String, String[]> toMap() {
        Map<String, String[]> result = new HashMap<>(parameters.size());
        for (Map.Entry<String, Parameter> entry : parameters.entrySet()) {
            result.put(entry.getKey(), entry.getValue().getMultipleValues());
        }
        return result;
    }

    public HttpParameters appendAll(Map<String, Parameter> newParams) {
        parameters.putAll(newParams);
        return this;
    }

    public void applyParameters(ParameterAware parameterAware) {
        parameterAware.setParameters(toMap());
    }

    @Override
    public int size() {
        return parameters.size();
    }

    @Override
    public boolean isEmpty() {
        return parameters.isEmpty();
    }

    @Override
    public boolean containsKey(Object key) {
        return parameters.containsKey(key);
    }

    @Override
    public boolean containsValue(Object value) {
        return parameters.containsValue(value);
    }

    @Override
    public Parameter get(Object key) {
        if (parameters.containsKey(key)) {
            return parameters.get(key);
        } else {
            return new Parameter.Empty(String.valueOf(key));
        }
    }

    @Override
    public Parameter put(String key, Parameter value) {
        throw new IllegalAccessError("HttpParameters are immutable, you cannot put value directly!");
    }

    @Override
    public Parameter remove(Object key) {
        throw new IllegalAccessError("HttpParameters are immutable, you cannot remove object directly!");
    }

    @Override
    public void putAll(Map<? extends String, ? extends Parameter> m) {
        throw new IllegalAccessError("HttpParameters are immutable, you cannot put values directly!");
    }

    @Override
    public void clear() {
        throw new IllegalAccessError("HttpParameters are immutable, you cannot clear values directly!");
    }

    @Override
    public Set<String> keySet() {
        return Collections.unmodifiableSet(new TreeSet<>(parameters.keySet()));
    }

    @Override
    public Collection<Parameter> values() {
        return Collections.unmodifiableCollection(parameters.values());
    }

    @Override
    public Set<Entry<String, Parameter>> entrySet() {
        return Collections.unmodifiableSet(parameters.entrySet());
    }

    @Override
    public String toString() {
        return parameters.toString();
    }

    public static class Builder {
        private Map<String, Object> requestParameterMap;
        private HttpParameters parent;

        protected Builder(Map<String, ?> requestParameterMap) {
            this.requestParameterMap = new HashMap<>();
            this.requestParameterMap.putAll(requestParameterMap);
        }

        public Builder withParent(HttpParameters parentParams) {
            if (parentParams != null) {
                parent = parentParams;
            }
            return this;
        }

        public Builder withExtraParams(Map<String, ?> params) {
            if (params != null) {
                requestParameterMap.putAll(params);
            }
            return this;
        }

        public Builder withComparator(Comparator<String> orderedComparator) {
            requestParameterMap = new TreeMap<>(orderedComparator);
            return this;
        }

        public HttpParameters build() {
            Map<String, Parameter> parameters = (parent == null)
                    ? new HashMap<String, Parameter>()
                    : new HashMap<>(parent.parameters);

            for (Map.Entry<String, Object> entry : requestParameterMap.entrySet()) {
                String name = entry.getKey();
                Object value = entry.getValue();
                parameters.put(name, new Parameter.Request(name, value));
            }

            return new HttpParameters(parameters);
        }
    }
}
