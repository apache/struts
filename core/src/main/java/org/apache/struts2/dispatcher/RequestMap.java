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

import jakarta.servlet.http.HttpServletRequest;
import java.io.Serializable;
import java.util.AbstractMap;
import java.util.Enumeration;
import java.util.HashSet;
import java.util.Set;

/**
 * A simple implementation of the {@link java.util.Map} interface to handle a collection of request attributes.
 */
public class RequestMap extends AbstractMap<String, Object> implements Serializable {

    private static final long serialVersionUID = -7675640869293787926L;

    private Set<Entry<String, Object>> entries;
    private HttpServletRequest request;

    /**
     * Saves the request to use as the backing for getting and setting values
     *
     * @param request the http servlet request.
     */
    public RequestMap(final HttpServletRequest request) {
        this.request = request;
    }


    /**
     * Removes all attributes from the request as well as clears entries in this map.
     */
    @Override
    public void clear() {
        entries = null;
        Enumeration<String> keys = request.getAttributeNames();

        while (keys.hasMoreElements()) {
            String key = keys.nextElement();
            request.removeAttribute(key);
        }
    }

    /**
     * Returns a Set of attributes from the http request.
     *
     * @return a Set of attributes from the http request.
     */
    @Override
    public Set<Entry<String, Object>> entrySet() {
        if (entries == null) {
            entries = new HashSet<>();

            Enumeration<String> enumeration = request.getAttributeNames();

            while (enumeration.hasMoreElements()) {
                final String key = enumeration.nextElement();
                final Object value = request.getAttribute(key);
                entries.add(new StringObjectEntry(key, value) {
                    @Override
                    public Object setValue(final Object obj) {
                        request.setAttribute(key, obj);

                        return value;
                    }
                });
            }
        }

        return entries;
    }

    /**
     * Returns the request attribute associated with the given key or <tt>null</tt> if it doesn't exist.
     *
     * @param key the name of the request attribute.
     * @return the request attribute or <tt>null</tt> if it doesn't exist.
     */
    public Object get(final String key) {
        return request.getAttribute(key);
    }

    /**
     * Saves an attribute in the request.
     *
     * @param key   the name of the request attribute.
     * @param value the value to set.
     * @return the object that was just set.
     */
    @Override
    public Object put(final String key, final Object value) {
        Object oldValue = get(key);
        entries = null;

        request.setAttribute(key, value);

        return oldValue;
    }

    /**
     * Removes the specified request attribute.
     *
     * @param key the name of the attribute to remove.
     * @return the value that was removed or <tt>null</tt> if the value was not found (and hence, not removed).
     */
    public Object remove(final String key) {
        entries = null;

        Object value = get(key);
        request.removeAttribute(key);

        return value;
    }
}
