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

import java.io.Serializable;
import java.util.AbstractMap;
import java.util.Enumeration;
import java.util.HashSet;
import java.util.Set;

import jakarta.servlet.ServletContext;

/**
 * A simple implementation of the {@link java.util.Map} interface to handle a collection of attributes and
 * init parameters in a {@link jakarta.servlet.ServletContext} object. The {@link #entrySet()} method
 * enumerates over all servlet context attributes and init parameters and returns a collection of both.
 * Note, this will occur lazily - only when the entry set is asked for.
 */
public class ApplicationMap extends AbstractMap<String, Object> implements Serializable {

    private static final long serialVersionUID = 9136809763083228202L;

    private ServletContext context;
    private Set<Entry<String, Object>> entries;


    /**
     * Creates a new map object given the servlet context.
     *
     * @param ctx the servlet context
     */
    public ApplicationMap(final ServletContext ctx) {
        this.context = ctx;
    }


    /**
     * Removes all entries from the Map and removes all attributes from the servlet context.
     */
    @Override
    public void clear() {
        entries = null;

        Enumeration<String> e = context.getAttributeNames();

        while (e.hasMoreElements()) {
            context.removeAttribute(e.nextElement());
        }
    }

    /**
     * Creates a Set of all servlet context attributes as well as context init parameters.
     *
     * @return a Set of all servlet context attributes as well as context init parameters.
     */
    @Override
    public Set<Entry<String, Object>> entrySet() {
        if (entries == null) {
            entries = new HashSet<>();

            // Add servlet context attributes
            Enumeration<String> enumeration = context.getAttributeNames();

            while (enumeration.hasMoreElements()) {
                final String key = enumeration.nextElement();
                final Object value = context.getAttribute(key);
                entries.add(new StringObjectEntry(key, value) {
                    @Override
                    public Object setValue(final Object obj) {
                        context.setAttribute(key, obj);

                        return value;
                    }
                });
            }

            // Add servlet context init params
            enumeration = context.getInitParameterNames();

            while (enumeration.hasMoreElements()) {
                final String key = enumeration.nextElement();
                final Object value = context.getInitParameter(key);
                entries.add(new StringObjectEntry(key, value) {
                    @Override
                    public Object setValue(final Object obj) {
                        context.setAttribute(key, obj);

                        return value;
                    }
                });
            }
        }

        return entries;
    }

    /**
     * Returns the servlet context attribute or init parameter based on the given key. If the
     * entry is not found, <tt>null</tt> is returned.
     *
     * @param key the entry key.
     * @return the servlet context attribute or init parameter or <tt>null</tt> if the entry is not found.
     */
    public Object get(final String key) {
        // Try context attributes first, then init params
        // This gives the proper shadowing effects
        Object value = context.getAttribute(key);

        return (value == null) ? context.getInitParameter(key) : value;
    }

    /**
     * Sets a servlet context attribute given a attribute name and value.
     *
     * @param key   the name of the attribute.
     * @param value the value to set.
     * @return the attribute that was just set.
     */
    @Override
    public Object put(final String key, final Object value) {
        Object oldValue = get(key);
        entries = null;

        context.setAttribute(key, value);

        return oldValue;
    }

    /**
     * Removes the specified servlet context attribute.
     *
     * @param key the attribute to remove.
     * @return the entry that was just removed.
     */
    public Object remove(final String key) {
        entries = null;

        Object value = get(key);
        context.removeAttribute(key);

        return value;
    }
}
