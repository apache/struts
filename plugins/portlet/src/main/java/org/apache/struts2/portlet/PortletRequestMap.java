/*
 * $Id: PortletRequestMap.java 582626 2007-10-07 13:26:12Z mrdon $
 *
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
package org.apache.struts2.portlet;

import javax.portlet.PortletRequest;
import java.util.AbstractMap;
import java.util.Enumeration;
import java.util.HashSet;
import java.util.Set;

/**
 * A simple implementation of the {@link java.util.Map} interface to handle a collection of request attributes.
 *
 */
public class PortletRequestMap extends AbstractMap<String, Object> {

    private Set<Entry<String, Object>> entries = null;
    private PortletRequest request = null;

    /**
     * Saves the request to use as the backing for getting and setting values
     *
     * @param request the portlet request.
     */
    public PortletRequestMap(PortletRequest request) {
        this.request = request;
    }

    /**
     * Removes all attributes from the request as well as clears entries in this
     * map.
     */
    public void clear() {
        entries = null;
        Enumeration keys = request.getAttributeNames();

        while (keys.hasMoreElements()) {
            String key = (String) keys.nextElement();
            request.removeAttribute(key);
        }
    }

    /**
     * Returns a Set of attributes from the portlet request.
     *
     * @return a Set of attributes from the portlet request.
     */
    public Set<Entry<String, Object>> entrySet() {
        if (entries == null) {
            entries = new HashSet<Entry<String, Object>>();

            Enumeration<String> enumeration = request.getAttributeNames();

            while (enumeration.hasMoreElements()) {
                final String key = enumeration.nextElement();
                final Object value = request.getAttribute(key);
                entries.add(new Entry<String, Object>() {
                    public boolean equals(Object obj) {
                        Entry entry = (Entry) obj;

                        return ((key == null) ? (entry.getKey() == null) : key
                                .equals(entry.getKey()))
                                && ((value == null) ? (entry.getValue() == null)
                                        : value.equals(entry.getValue()));
                    }

                    public int hashCode() {
                        return ((key == null) ? 0 : key.hashCode())
                                ^ ((value == null) ? 0 : value.hashCode());
                    }

                    public String getKey() {
                        return key;
                    }

                    public Object getValue() {
                        return value;
                    }

                    public Object setValue(Object obj) {
                        request.setAttribute(key, obj);

                        return value;
                    }
                });
            }
        }

        return entries;
    }

    /**
     * Returns the request attribute associated with the given key or
     * <tt>null</tt> if it doesn't exist.
     *
     * @param key the name of the request attribute.
     * @return the request attribute or <tt>null</tt> if it doesn't exist.
     */
    public Object get(Object key) {
        return request.getAttribute(key.toString());
    }

    /**
     * Saves an attribute in the request.
     *
     * @param key the name of the request attribute.
     * @param value the value to set.
     * @return the object that was just set.
     */
    public Object put(String key, Object value) {
        entries = null;
        request.setAttribute(key, value);

        return get(key);
    }

    /**
     * Removes the specified request attribute.
     *
     * @param key the name of the attribute to remove.
     * @return the value that was removed or <tt>null</tt> if the value was
     * not found (and hence, not removed).
     */
    public Object remove(Object key) {
        entries = null;

        Object value = get(key);
        request.removeAttribute(key.toString());

        return value;
    }

}
