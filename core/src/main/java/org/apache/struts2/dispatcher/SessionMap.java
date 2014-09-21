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

import org.apache.struts2.components.Submit;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;
import java.io.Serializable;
import java.util.AbstractMap;
import java.util.Collections;
import java.util.Enumeration;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;


/**
 * A simple implementation of the {@link java.util.Map} interface to handle a collection of HTTP session
 * attributes. The {@link #entrySet()} method enumerates over all session attributes and creates a Set of entries.
 * Note, this will occur lazily - only when the entry set is asked for.
 */
public class SessionMap<K, V> extends AbstractMap<K, V> implements Serializable {

    private static final long serialVersionUID = 4678843241638046854L;

    protected HttpSession session;
    protected Set<Map.Entry<K, V>> entries;
    protected HttpServletRequest request;


    /**
     * Creates a new session map given a http servlet request. Note, ths enumeration of request
     * attributes will occur when the map entries are asked for.
     *
     * @param request the http servlet request object.
     */
    public SessionMap(HttpServletRequest request) {
        // note, holding on to this request and relying on lazy session initalization will not work
        // if you are running your action invocation in a background task, such as using the
        // "execAndWait" interceptor
        this.request = request;
        this.session = request.getSession(false);
    }

    /**
     * Invalidate the http session.
     */
    public void invalidate() {
        if (session == null) {
            return;
        }

        synchronized (session.getId().intern()) {
            session.invalidate();
            session = null;
            entries = null;
        }
    }

    /**
     * Removes all attributes from the session as well as clears entries in this
     * map.
     */
    @SuppressWarnings("unchecked")
    public void clear() {
        if (session == null) {
            return;
        }

        synchronized (session.getId().intern()) {
            entries = null;
            Enumeration<String> attributeNamesEnum = session.getAttributeNames();
            while (attributeNamesEnum.hasMoreElements()) {
                session.removeAttribute(attributeNamesEnum.nextElement());
            }
        }

    }

    /**
     * Returns a Set of attributes from the http session.
     *
     * @return a Set of attributes from the http session.
     */
    @SuppressWarnings("unchecked")
    public Set<java.util.Map.Entry<K, V>> entrySet() {
        if (session == null) {
            return Collections.emptySet();
        }

        synchronized (session.getId().intern()) {
            if (entries == null) {
                entries = new HashSet<Map.Entry<K, V>>();

                Enumeration<? extends Object> enumeration = session.getAttributeNames();

                while (enumeration.hasMoreElements()) {
                    final String key = enumeration.nextElement().toString();
                    final Object value = session.getAttribute(key);
                    entries.add(new Map.Entry<K, V>() {
                        public boolean equals(Object obj) {
                            if (!(obj instanceof Map.Entry)) {
                                return false;
                            }
                            Map.Entry<K, V> entry = (Map.Entry<K, V>) obj;

                            return ((key == null) ? (entry.getKey() == null) : key.equals(entry.getKey())) && ((value == null) ? (entry.getValue() == null) : value.equals(entry.getValue()));
                        }

                        public int hashCode() {
                            return ((key == null) ? 0 : key.hashCode()) ^ ((value == null) ? 0 : value.hashCode());
                        }

                        public K getKey() {
                            return (K) key;
                        }

                        public V getValue() {
                            return (V) value;
                        }

                        public V setValue(Object obj) {
                            session.setAttribute(key, obj);

                            return (V) value;
                        }
                    });
                }
            }
        }

        return entries;
    }

    /**
     * Returns the session attribute associated with the given key or <tt>null</tt> if it doesn't exist.
     *
     * @param key the name of the session attribute.
     * @return the session attribute or <tt>null</tt> if it doesn't exist.
     */
    @SuppressWarnings("unchecked")
    public V get(Object key) {
        if (session == null) {
            return null;
        }

        synchronized (session.getId().intern()) {
            return (V) session.getAttribute(key.toString());
        }
    }

    /**
     * Saves an attribute in the session.
     *
     * @param key   the name of the session attribute.
     * @param value the value to set.
     * @return the object that was just set.
     */
    public V put(K key, V value) {
        synchronized (this) {
            if (session == null) {
                session = request.getSession(true);
            }
        }
        synchronized (session.getId().intern()) {
            V oldValue = get(key);
            entries = null;
            session.setAttribute(key.toString(), value);
            return oldValue;
        }
    }

    /**
     * Removes the specified session attribute.
     *
     * @param key the name of the attribute to remove.
     * @return the value that was removed or <tt>null</tt> if the value was not found (and hence, not removed).
     */
    public V remove(Object key) {
        if (session == null) {
            return null;
        }

        synchronized (session.getId().intern()) {
            entries = null;

            V value = get(key);
            session.removeAttribute(key.toString());

            return value;
        }
    }


    /**
     * Checks if the specified session attribute with the given key exists.
     *
     * @param key the name of the session attribute.
     * @return <tt>true</tt> if the session attribute exits or <tt>false</tt> if it doesn't exist.
     */
    public boolean containsKey(Object key) {
        if (session == null) {
            return false;
        }

        synchronized (session.getId().intern()) {
            return (session.getAttribute(key.toString()) != null);
        }
    }
}
