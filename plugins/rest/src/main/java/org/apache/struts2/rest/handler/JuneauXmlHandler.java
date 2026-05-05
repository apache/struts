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
package org.apache.struts2.rest.handler;

import org.apache.struts2.ActionInvocation;
import org.apache.commons.beanutils.BeanUtils;
import org.apache.juneau.parser.ParseException;
import org.apache.juneau.serializer.SerializeException;
import org.apache.juneau.xml.XmlDocSerializer;
import org.apache.juneau.xml.XmlParser;
import org.apache.juneau.xml.XmlSerializer;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.apache.struts2.interceptor.parameter.ParameterAuthorizationContext;

import java.beans.BeanInfo;
import java.beans.IntrospectionException;
import java.beans.Introspector;
import java.beans.PropertyDescriptor;
import java.io.IOException;
import java.io.Reader;
import java.io.Writer;
import java.lang.reflect.Array;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.Collection;
import java.util.Map;

/**
 * Handles XML content using Apache Juneau
 * <a href="http://juneau.apache.org/#marshall.html">http://juneau.apache.org/#marshall.html</a>
 *
 * <p>Implements {@link AuthorizationAwareContentTypeHandler}: when
 * {@link ParameterAuthorizationContext#isActive()} is {@code true}, performs a post-parse walk
 * over the parsed result and copies only authorized properties to the target. Without an active
 * context, behavior is unchanged (Juneau parses, then {@code BeanUtils.copyProperties} populates
 * the target).</p>
 *
 * <p>Note: Juneau's parser builds the entire result tree before our authorization walk runs, so
 * setter side effects on transient nested objects may fire even for unauthorized properties —
 * those transient objects are then discarded. This is functionally equivalent to the legacy
 * two-phase copy in {@code ContentTypeInterceptor}, with the same security model. Only the
 * Jackson-based handlers ({@link JacksonJsonHandler}, {@link JacksonXmlHandler}) achieve the
 * stronger guarantee where unauthorized subtrees are never instantiated at all.</p>
 */
public class JuneauXmlHandler implements AuthorizationAwareContentTypeHandler {

    private static final Logger LOG = LogManager.getLogger(JuneauXmlHandler.class);

    private static final String DEFAULT_CONTENT_TYPE = "application/xml";

    private final XmlParser parser = XmlParser.DEFAULT;
    private final XmlSerializer serializer = XmlDocSerializer.DEFAULT;

    @Override
    public void toObject(ActionInvocation invocation, Reader in, Object target) throws IOException {
        LOG.debug("Converting input into an object of: {}", target.getClass().getName());
        try {
            Object result = parser.parse(in, target.getClass());
            if (ParameterAuthorizationContext.isActive()) {
                copyAuthorizedProperties(target, result, "");
            } else {
                BeanUtils.copyProperties(target, result);
            }
        } catch (ParseException | IllegalAccessException | InvocationTargetException e) {
            throw new IOException(e);
        }
    }

    /**
     * Recursively copies properties from {@code source} into {@code target}, consulting
     * {@link ParameterAuthorizationContext} at each property. Unauthorized properties are skipped
     * (target retains its existing value). Authorized scalar properties are copied directly;
     * authorized nested beans are recursed into so their nested fields are individually authorized;
     * authorized collections / maps / arrays use indexed-path semantics ({@code path[0].field}).
     */
    private void copyAuthorizedProperties(Object target, Object source, String prefix) throws IOException {
        if (source == null) {
            return;
        }
        BeanInfo beanInfo;
        try {
            beanInfo = Introspector.getBeanInfo(source.getClass(), Object.class);
        } catch (IntrospectionException e) {
            throw new IOException("Unable to introspect " + source.getClass(), e);
        }
        for (PropertyDescriptor pd : beanInfo.getPropertyDescriptors()) {
            copyAuthorizedProperty(target, source, prefix, pd);
        }
    }

    private void copyAuthorizedProperty(Object target, Object source, String prefix, PropertyDescriptor pd)
            throws IOException {
        Method readMethod = pd.getReadMethod();
        Method writeMethod = pd.getWriteMethod();
        if (readMethod == null || writeMethod == null) {
            return;
        }
        String path = prefix.isEmpty() ? pd.getName() : prefix + "." + pd.getName();
        if (!ParameterAuthorizationContext.isAuthorized(path)) {
            LOG.warn("REST body parameter [{}] rejected by @StrutsParameter authorization on [{}]",
                    path, target.getClass().getName());
            return;
        }
        Object value;
        try {
            value = readMethod.invoke(source);
        } catch (ReflectiveOperationException e) {
            throw new IOException("Failed reading " + path, e);
        }
        if (value == null) {
            return;
        }
        try {
            writeAuthorizedValue(target, readMethod, writeMethod, value, path);
        } catch (ReflectiveOperationException e) {
            throw new IOException("Failed writing " + path, e);
        }
    }

    private void writeAuthorizedValue(Object target, Method readMethod, Method writeMethod, Object value, String path)
            throws ReflectiveOperationException, IOException {
        if (value instanceof Collection<?> collection) {
            writeMethod.invoke(target, copyAuthorizedCollection(collection, path));
        } else if (value instanceof Map<?, ?> map) {
            writeMethod.invoke(target, copyAuthorizedMap(map, path));
        } else if (value.getClass().isArray()) {
            writeMethod.invoke(target, copyAuthorizedArray(value, path));
        } else if (isLeaf(value.getClass())) {
            writeMethod.invoke(target, value);
        } else {
            writeAuthorizedNestedBean(target, readMethod, writeMethod, value, path);
        }
    }

    private void writeAuthorizedNestedBean(Object target, Method readMethod, Method writeMethod,
                                           Object value, String path)
            throws ReflectiveOperationException, IOException {
        Object nestedTarget = readMethod.invoke(target);
        if (nestedTarget == null) {
            nestedTarget = newInstance(value.getClass());
            if (nestedTarget == null) {
                // Cannot authorize without a fresh target instance; skip rather than
                // bulk-copy the unfiltered value.
                LOG.warn("REST nested bean [{}] skipped — no no-arg constructor for [{}]",
                        path, value.getClass().getName());
                return;
            }
            writeMethod.invoke(target, nestedTarget);
        }
        copyAuthorizedProperties(nestedTarget, value, path);
    }

    private Collection<Object> copyAuthorizedCollection(Collection<?> source, String prefix) throws IOException {
        Collection<Object> result = newCollection(source);
        String elementPath = prefix + "[0]";
        for (Object element : source) {
            result.add(copyAuthorizedElement(element, elementPath));
        }
        return result;
    }

    private Map<Object, Object> copyAuthorizedMap(Map<?, ?> source, String prefix) throws IOException {
        Map<Object, Object> result = newMap(source);
        String elementPath = prefix + "[0]";
        for (Map.Entry<?, ?> entry : source.entrySet()) {
            result.put(entry.getKey(), copyAuthorizedElement(entry.getValue(), elementPath));
        }
        return result;
    }

    private Object copyAuthorizedArray(Object sourceArray, String prefix) throws IOException {
        int length = Array.getLength(sourceArray);
        Object result = Array.newInstance(sourceArray.getClass().getComponentType(), length);
        String elementPath = prefix + "[0]";
        for (int i = 0; i < length; i++) {
            Object element = Array.get(sourceArray, i);
            Object copied = copyAuthorizedElement(element, elementPath);
            if (copied != null || !sourceArray.getClass().getComponentType().isPrimitive()) {
                Array.set(result, i, copied);
            }
        }
        return result;
    }

    private Object copyAuthorizedElement(Object element, String elementPath) throws IOException {
        if (element == null || isLeaf(element.getClass())) {
            return element;
        }
        Object freshElement = newInstance(element.getClass());
        if (freshElement == null) {
            LOG.warn("REST element [{}] skipped — no no-arg constructor for [{}]",
                    elementPath, element.getClass().getName());
            return null;
        }
        copyAuthorizedProperties(freshElement, element, elementPath);
        return freshElement;
    }

    /**
     * Treats common JDK value types as leaves (no introspection needed). Mirrors the
     * conservative classification used elsewhere in the REST plugin's two-phase copy.
     */
    private static boolean isLeaf(Class<?> c) {
        if (c.isPrimitive() || c.isEnum()) return true;
        String n = c.getName();
        return n.startsWith("java.lang.")
                || n.startsWith("java.math.")
                || n.startsWith("java.time.")
                || n.startsWith("java.net.")
                || n.startsWith("java.io.")
                || n.startsWith("java.nio.")
                || (n.startsWith("java.util.") && !Collection.class.isAssignableFrom(c) && !Map.class.isAssignableFrom(c));
    }

    private static Object newInstance(Class<?> c) {
        try {
            return c.getDeclaredConstructor().newInstance();
        } catch (ReflectiveOperationException e) {
            return null;
        }
    }

    @SuppressWarnings("unchecked")
    private static Collection<Object> newCollection(Collection<?> source) {
        try {
            return (Collection<Object>) source.getClass().getDeclaredConstructor().newInstance();
        } catch (ReflectiveOperationException e) {
            return new java.util.ArrayList<>();
        }
    }

    @SuppressWarnings("unchecked")
    private static Map<Object, Object> newMap(Map<?, ?> source) {
        try {
            return (Map<Object, Object>) source.getClass().getDeclaredConstructor().newInstance();
        } catch (ReflectiveOperationException e) {
            return new java.util.LinkedHashMap<>();
        }
    }

    @Override
    public String fromObject(ActionInvocation invocation, Object obj, String resultCode, Writer stream) throws IOException {
        LOG.debug("Converting an object of {} into string", obj.getClass().getName());
        try {
            serializer
                    .copy()
                    .locale(invocation.getInvocationContext().getLocale())
                    .build()
                    .serialize(obj, stream);
            return null;
        } catch (SerializeException e) {
            throw new IOException(e);
        }
    }

    @Override
    public String getContentType() {
        return DEFAULT_CONTENT_TYPE;
    }

    @Override
    public String getExtension() {
        return "xml";
    }

}
