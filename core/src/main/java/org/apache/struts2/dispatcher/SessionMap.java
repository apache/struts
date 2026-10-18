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

import java.io.Serial;
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

    @Serial
    private static final long serialVersionUID = 4678843241638046854L;

    protected volatile HttpSession session;
    /**
     * @deprecated no longer read or written; {@link #entrySet()} builds a fresh set on every call.
     */
    @Deprecated(since = "7.5.0", forRemoval = true)
    protected Set<Entry<String, Object>> entries;
    protected HttpServletRequest request;


    protected SessionMap() {
    }

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
        HttpSession current = session;
        if (current == null) {
            return;
        }

        try {
            current.invalidate();
        } catch (IllegalStateException e) {
            // already invalidated elsewhere
        }
        unpin(current);
    }

    /**
     * Removes all attributes from the session.
     */
    @Override
    public void clear() {
        HttpSession current = session;
        if (current == null) {
            return;
        }

        try {
            final Enumeration<String> attributeNamesEnum = current.getAttributeNames();
            while (attributeNamesEnum.hasMoreElements()) {
                current.removeAttribute(attributeNamesEnum.nextElement());
            }
        } catch (IllegalStateException e) {
            unpin(current);
        }
    }

    /**
     * Returns a Set of attributes from the http session, built fresh on every call.
     *
     * @return a Set of attributes from the http session.
     */
    @Override
    public Set<Entry<String, Object>> entrySet() {
        HttpSession current = session;
        if (current == null) {
            return Collections.emptySet();
        }

        final Set<Entry<String, Object>> result = new HashSet<>();
        try {
            final Enumeration<String> enumeration = current.getAttributeNames();

            while (enumeration.hasMoreElements()) {
                final String key = enumeration.nextElement();
                final Object value = current.getAttribute(key);
                result.add(new StringObjectEntry(key, value) {
                    @Override
                    public Object setValue(final Object obj) {
                        current.setAttribute(key, obj);

                        return value;
                    }
                });
            }
        } catch (IllegalStateException e) {
            unpin(current);
            return Collections.emptySet();
        }

        return result;
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
        HttpSession current = session;
        if (current == null) {
            return null;
        }

        try {
            return current.getAttribute(key != null ? key.toString() : null);
        } catch (IllegalStateException e) {
            unpin(current);
            return null;
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
        HttpSession current = session;
        if (current == null) {
            current = pin();
        }

        final Object oldValue = get(key);
        try {
            current.setAttribute(key, value);
        } catch (IllegalStateException e) {
            unpin(current);
            pin().setAttribute(key, value);
            return null;
        }
        return oldValue;
    }

    private HttpSession pin() {
        synchronized (this) {
            HttpSession current = session;
            if (current == null) {
                current = request.getSession(true);
                session = current;
            }
            return current;
        }
    }

    private void unpin(final HttpSession stale) {
        synchronized (this) {
            if (session == stale) {
                session = null;
            }
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
        HttpSession current = session;
        if (current == null) {
            return null;
        }

        final String keyAsString = (key != null ? key.toString() : null);
        final Object value = get(keyAsString);
        try {
            current.removeAttribute(keyAsString);
        } catch (IllegalStateException e) {
            unpin(current);
            return null;
        }

        return value;
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
        HttpSession current = session;
        if (current == null) {
            return false;
        }

        final String keyAsString = (key != null ? key.toString() : null);
        try {
            return current.getAttribute(keyAsString) != null;
        } catch (IllegalStateException e) {
            unpin(current);
            return false;
        }
    }
}
