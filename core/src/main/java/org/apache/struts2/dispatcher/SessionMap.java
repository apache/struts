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
import jakarta.servlet.http.HttpSession;
import java.io.Serializable;
import java.util.AbstractMap;
import java.util.Collections;
import java.util.Enumeration;
import java.util.HashSet;
import java.util.Set;

/**
 * A simple implementation of the {@link java.util.Map} interface to handle a collection of HTTP session
 * attributes. The {@link #entrySet()} method enumerates over all session attributes and creates a Set of entries.
 * Note, this will occur lazily - only when the entry set is asked for.
 */
public class SessionMap extends AbstractMap<String, Object> implements Serializable {

    private static final long serialVersionUID = 4678843241638046854L;

    protected HttpSession session;
    protected Set<Entry<String, Object>> entries;
    protected HttpServletRequest request;


    /**
     * Creates a new session map given a http servlet request. Note, the enumeration of request
     * attributes will occur when the map entries are asked for.
     *
     * @param request the http servlet request object.
     */
    public SessionMap(final HttpServletRequest request) {
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
    @Override
    public void clear() {
        if (session == null) {
            return;
        }

        synchronized (session.getId().intern()) {
            entries = null;
            final Enumeration<String> attributeNamesEnum = session.getAttributeNames();
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
    @Override
    public Set<Entry<String, Object>> entrySet() {
        if (session == null) {
            return Collections.emptySet();
        }

        synchronized (session.getId().intern()) {
            if (entries == null) {
                entries = new HashSet<>();

                final Enumeration<String> enumeration = session.getAttributeNames();

                while (enumeration.hasMoreElements()) {
                    final String key = enumeration.nextElement();
                    final Object value = session.getAttribute(key);
                    entries.add(new StringObjectEntry(key, value) {
                        @Override
                        public Object setValue(final Object obj) {
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
     * Returns the session attribute associated with the given key or <tt>null</tt> if it doesn't exist.
     * 
     * <b>Note:</b> Must use the same signature as {@link java.util.AbstractMap#get(java.lang.Object)} to ensure the
     *   expected specialized behaviour is performed here (and not the generic ancestor behaviour).
     *
     * @param key the name of the session attribute.
     * @return the session attribute or <tt>null</tt> if it doesn't exist.
     */
    @Override
    public Object get(final Object key) {
        if (session == null) {
            return null;
        }

        synchronized (session.getId().intern()) {
            return session.getAttribute(key != null ? key.toString() : null);
        }
    }

    /**
     * Saves an attribute in the session.
     *
     * @param key   the name of the session attribute.
     * @param value the value to set.
     * @return the object that was just set.
     */
    @Override
    public Object put(final String key, final Object value) {
        synchronized (this) {
            if (session == null) {
                session = request.getSession(true);
            }
        }
        synchronized (session.getId().intern()) {
            final Object oldValue = get(key);
            entries = null;
            session.setAttribute(key, value);
            return oldValue;
        }
    }

    /**
     * Removes the specified session attribute.
     *
     * <b>Note:</b> Must use the same signature as {@link java.util.AbstractMap#remove(java.lang.Object)} to ensure the
     *   expected specialized behaviour is performed here (and not the generic ancestor behaviour).
     * 
     * @param key the name of the attribute to remove.
     * @return the value that was removed or <tt>null</tt> if the value was not found (and hence, not removed).
     */
    @Override
    public Object remove(final Object key) {
        if (session == null) {
            return null;
        }

        synchronized (session.getId().intern()) {
            entries = null;

            final String keyAsString = (key != null ? key.toString() : null);
            final Object value = get(keyAsString);
            session.removeAttribute(keyAsString);

            return value;
        }
    }


    /**
     * Checks if the specified session attribute with the given key exists.
     *
     * <b>Note:</b> Must use the same signature as {@link java.util.AbstractMap#containsKey(java.lang.Object)} to ensure the
     *   expected specialized behaviour is performed here (and not the generic ancestor behaviour).
     * 
     * @param key the name of the session attribute.
     * @return <tt>true</tt> if the session attribute exits or <tt>false</tt> if it doesn't exist.
     */
    @Override
    public boolean containsKey(final Object key) {
        if (session == null) {
            return false;
        }

        synchronized (session.getId().intern()) {
            final String keyAsString = (key != null ? key.toString() : null);
            return (session.getAttribute(keyAsString) != null);
        }
    }
}
