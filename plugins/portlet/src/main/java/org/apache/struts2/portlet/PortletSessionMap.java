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
package org.apache.struts2.portlet;

import javax.portlet.PortletRequest;
import javax.portlet.PortletSession;
import java.util.AbstractMap;
import java.util.Enumeration;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

/**
 * A simple implementation of the {@link java.util.Map} interface to handle a collection of portlet session
 * attributes. The {@link #entrySet()} method enumerates over all session attributes and creates a Set of entries.
 * Note, this will occur lazily - only when the entry set is asked for.
 *
 */
public class PortletSessionMap extends AbstractMap<String, Object> {

    private final PortletSession session;
    private Set<Entry<String, Object>> entries = null;

    /**
     * Creates a new session map given a portlet request.
     *
     * @param request the portlet request object.
     */
    public PortletSessionMap(PortletRequest request) {
        this.session = request.getPortletSession();
    }

    /**
     * @see java.util.Map#entrySet()
     */
    public Set<Entry<String, Object>> entrySet() {
        synchronized (session) {
            if (entries == null) {
                entries = new HashSet<Entry<String, Object>>();

                Enumeration enumeration = session.getAttributeNames();

                while (enumeration.hasMoreElements()) {
                    final String key = enumeration.nextElement().toString();
                    final Object value = session.getAttribute(key);
                    entries.add(new Entry<String, Object>() {
                        public boolean equals(Object obj) {
                            Map.Entry entry = (Map.Entry) obj;

                            return ((key == null) ? (entry.getKey() == null)
                                    : key.equals(entry.getKey()))
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
                            session.setAttribute(key, obj);

                            return value;
                        }
                    });
                }
            }
        }

        return entries;
    }

    /**
     * Returns the session attribute associated with the given key or
     * <tt>null</tt> if it doesn't exist.
     *
     * @param key the name of the session attribute.
     * @return the session attribute or <tt>null</tt> if it doesn't exist.
     */
    public Object get(Object key) {
        synchronized (session) {
            return session.getAttribute(key.toString());
        }
    }

    /**
     * Saves an attribute in the session.
     *
     * @param key the name of the session attribute.
     * @param value the value to set.
     * @return the object that was just set.
     */
    public Object put(String key, Object value) {
        synchronized (session) {
            entries = null;
            session.setAttribute(key, value);

            return get(key);
        }
    }

    /**
     * @see java.util.Map#clear()
     */
    public void clear() {
        synchronized (session) {
            entries = null;
            session.invalidate();
        }
    }

    /**
     * Removes the specified session attribute.
     *
     * @param key the name of the attribute to remove.
     * @return the value that was removed or <tt>null</tt> if the value was
     * not found (and hence, not removed).
     */
    public Object remove(Object key) {
        synchronized (session) {
            entries = null;

            Object value = get(key);
            session.removeAttribute(key.toString());

            return value;
        }
    }
}
