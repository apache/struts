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

package org.apache.struts2.interceptor.debugging;

import java.beans.IntrospectionException;
import java.io.Writer;
import java.util.Collection;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;

import com.opensymphony.xwork2.util.reflection.ReflectionException;
import com.opensymphony.xwork2.util.reflection.ReflectionProvider;

/**
 * Writes an object as a table, where each field can be expanded if it is an Object/Collection/Array
 *
 */
class ObjectToHTMLWriter {
    private PrettyPrintWriter prettyWriter;

    ObjectToHTMLWriter(Writer writer) {
        this.prettyWriter = new PrettyPrintWriter(writer);
        this.prettyWriter.setEscape(false);
    }

    @SuppressWarnings("unchecked")
    public void write(ReflectionProvider reflectionProvider, Object root, String expr) throws IntrospectionException,
        ReflectionException {
        prettyWriter.startNode("table");
        prettyWriter.addAttribute("class", "debugTable");

        if (root instanceof Map) {
            for (Iterator iterator = ((Map) root).entrySet().iterator(); iterator
                .hasNext();) {
                Map.Entry property = (Map.Entry) iterator.next();
                String key = property.getKey().toString();
                Object value = property.getValue();
                writeProperty(key, value, expr);
            }
        } else if (root instanceof List) {
            List list = (List) root;
            for (int i = 0; i < list.size(); i++) {
                Object element = list.get(i);
                writeProperty(String.valueOf(i), element, expr);
            }
        } else if (root instanceof Set) {
            Set set = (Set) root;
            for (Iterator iterator = set.iterator(); iterator.hasNext();) {
                writeProperty("", iterator.next(), expr);
            }
        } else if (root.getClass().isArray()) {
            Object[] objects = (Object[]) root;
            for (int i = 0; i < objects.length; i++) {
                writeProperty(String.valueOf(i), objects[i], expr);
            }
        } else {
            //print properties
            Map<String, Object> properties = reflectionProvider.getBeanMap(root);
            for (Map.Entry<String, Object> property : properties.entrySet()) {
                String name = property.getKey();
                Object value = property.getValue();

                if ("class".equals(name))
                    continue;

                writeProperty(name, value, expr);
            }
        }

        prettyWriter.endNode();
    }

    private void writeProperty(String name, Object value, String expr) {
        prettyWriter.startNode("tr");

        //name cell
        prettyWriter.startNode("td");
        prettyWriter.addAttribute("class", "nameColumn");
        prettyWriter.setValue(name);
        prettyWriter.endNode();

        //value cell
        prettyWriter.startNode("td");
        if (value != null) {
            //if is is an empty collection or array, don't write a link
            if (value != null &&
                (isEmptyCollection(value) || isEmptyMap(value) || (value.getClass()
                    .isArray() && ((Object[]) value).length == 0))) {
                prettyWriter.addAttribute("class", "emptyCollection");
                prettyWriter.setValue("empty");
            } else {
                prettyWriter.addAttribute("class", "valueColumn");
                writeValue(name, value, expr);
            }
        } else {
            prettyWriter.addAttribute("class", "nullValue");
            prettyWriter.setValue("null");
        }
        prettyWriter.endNode();

        //type cell
        prettyWriter.startNode("td");
        if (value != null) {
            prettyWriter.addAttribute("class", "typeColumn");
            Class clazz = value.getClass();
            prettyWriter.setValue(clazz.getName());
        } else {
            prettyWriter.addAttribute("class", "nullValue");
            prettyWriter.setValue("unknown");
        }
        prettyWriter.endNode();

        //close tr
        prettyWriter.endNode();
    }

    /**
     * Some maps, like AttributeMap will throw an exception when isEmpty() is called
     */
    private boolean isEmptyMap(Object value) {
        try {
            return value instanceof Map && ((Map) value).isEmpty();
        } catch (Exception e) {
            return true;
        }
    }

    /**
     * Some collections might throw an exception when isEmpty() is called
     */
    private boolean isEmptyCollection(Object value) {
        try {
            return value instanceof Collection && ((Collection) value).isEmpty();
        } catch (Exception e) {
            return true;
        }
    }

    private void writeValue(String name, Object value, String expr) {
        Class clazz = value.getClass();
        if (clazz.isPrimitive() || Number.class.isAssignableFrom(clazz) ||
            clazz.equals(String.class) || Boolean.class.equals(clazz)) {
            prettyWriter.setValue(String.valueOf(value));
        } else {
            prettyWriter.startNode("a");
            String path = expr.replaceAll("#", "%23") + "[\"" +
                name.replaceAll("#", "%23") + "\"]";
            prettyWriter.addAttribute("onclick", "expand(this, '" + path + "')");
            prettyWriter.addAttribute("href", "javascript://nop/");
            prettyWriter.setValue("Expand");
            prettyWriter.endNode();
        }
    }
}
