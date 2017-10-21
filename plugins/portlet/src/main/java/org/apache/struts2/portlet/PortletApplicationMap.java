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

import javax.portlet.PortletContext;
import java.io.Serializable;
import java.util.AbstractMap;
import java.util.Enumeration;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

/**
 * Portlet specific {@link java.util.Map} implementation representing the
 * {@link javax.portlet.PortletContext} of a Portlet.
 *
 */
public class PortletApplicationMap extends AbstractMap<String, Object> implements Serializable {

    private static final long serialVersionUID = 2296107511063504414L;

    private PortletContext context;

    private Set<Entry<String, Object>> entries;

    /**
     * Creates a new map object given the {@link PortletContext}.
     *
     * @param ctx The portlet context.
     */
    public PortletApplicationMap(PortletContext ctx) {
        this.context = ctx;
    }

    /**
     * Removes all entries from the Map and removes all attributes from the
     * portlet context.
     */
    public void clear() {
        entries = null;

        Enumeration e = context.getAttributeNames();

        while (e.hasMoreElements()) {
            context.removeAttribute(e.nextElement().toString());
        }
    }

    /**
     * Creates a Set of all portlet context attributes as well as context init
     * parameters.
     *
     * @return a Set of all portlet context attributes as well as context init
     *         parameters.
     */
    public Set<Entry<String, Object>> entrySet() {
        if (entries == null) {
            entries = new HashSet<Entry<String, Object>>();

            // Add portlet context attributes
            Enumeration enumeration = context.getAttributeNames();

            while (enumeration.hasMoreElements()) {
                final String key = enumeration.nextElement().toString();
                final Object value = context.getAttribute(key);
                entries.add(new Entry<String, Object>() {
                    public boolean equals(Object obj) {
                        Map.Entry entry = (Map.Entry) obj;

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
                        context.setAttribute(key, obj);

                        return value;
                    }
                });
            }

            // Add portlet context init params
            enumeration = context.getInitParameterNames();

            while (enumeration.hasMoreElements()) {
                final String key = enumeration.nextElement().toString();
                final Object value = context.getInitParameter(key);
                entries.add(new Entry<String, Object>() {
                    public boolean equals(Object obj) {
                        Map.Entry entry = (Map.Entry) obj;

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
                        context.setAttribute(key, obj);

                        return value;
                    }
                });
            }
        }

        return entries;
    }

    /**
     * Returns the portlet context attribute or init parameter based on the
     * given key. If the entry is not found, <tt>null</tt> is returned.
     *
     * @param key
     *            the entry key.
     * @return the portlet context attribute or init parameter or <tt>null</tt>
     *         if the entry is not found.
     */
    public Object get(String key) {
        // Try context attributes first, then init params
        // This gives the proper shadowing effects
        Object value = context.getAttribute(key);

        return (value == null) ? context.getInitParameter(key) : value;
    }

    /**
     * Sets a portlet context attribute given a attribute name and value.
     *
     * @param key
     *            the name of the attribute.
     * @param value
     *            the value to set.
     * @return the attribute that was just set.
     */
    public Object put(String key, Object value) {
        entries = null;
        context.setAttribute(key, value);

        return get(key);
    }

    /**
     * Removes the specified portlet context attribute.
     *
     * @param key
     *            the attribute to remove.
     * @return the entry that was just removed.
     */
    public Object remove(String key) {
        entries = null;

        Object value = get(key);
        context.removeAttribute(key);

        return value;
    }
}
