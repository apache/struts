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
package org.apache.struts2.rest;

import org.apache.struts2.ActionInvocation;
import org.apache.struts2.ModelDriven;
import org.apache.struts2.StrutsConstants;
import org.apache.struts2.inject.Inject;
import org.apache.struts2.interceptor.AbstractInterceptor;
import org.apache.struts2.interceptor.parameter.ParameterAuthorizer;
import org.apache.struts2.ServletActionContext;
import org.apache.struts2.rest.handler.ContentTypeHandler;
import org.apache.commons.lang3.BooleanUtils;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import jakarta.servlet.http.HttpServletRequest;
import java.beans.BeanInfo;
import java.beans.Introspector;
import java.beans.PropertyDescriptor;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.lang.reflect.Array;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.Collection;
import java.util.LinkedHashMap;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.SortedMap;
import java.util.SortedSet;
import java.util.TreeMap;
import java.util.TreeSet;

/**
 * Uses the content handler to apply the request body to the action.
 * <p>
 * When {@code struts.parameters.requireAnnotations} is enabled, only properties annotated with
 * {@link org.apache.struts2.interceptor.parameter.StrutsParameter} will be populated from the request body,
 * consistent with the parameter authorization enforced by
 * {@link org.apache.struts2.interceptor.parameter.ParametersInterceptor} for form/query parameters.
 */
public class ContentTypeInterceptor extends AbstractInterceptor {

    private static final Logger LOG = LogManager.getLogger(ContentTypeInterceptor.class);

    private ContentTypeHandlerManager selector;
    private ParameterAuthorizer parameterAuthorizer;
    private boolean requireAnnotations = false;

    @Inject
    public void setContentTypeHandlerSelector(ContentTypeHandlerManager selector) {
        this.selector = selector;
    }

    @Inject
    public void setParameterAuthorizer(ParameterAuthorizer parameterAuthorizer) {
        this.parameterAuthorizer = parameterAuthorizer;
    }

    @Inject(value = StrutsConstants.STRUTS_PARAMETERS_REQUIRE_ANNOTATIONS, required = false)
    public void setRequireAnnotations(String requireAnnotations) {
        this.requireAnnotations = BooleanUtils.toBoolean(requireAnnotations);
    }

    public String intercept(ActionInvocation invocation) throws Exception {
        HttpServletRequest request = ServletActionContext.getRequest();
        ContentTypeHandler handler = selector.getHandlerForRequest(request);

        Object target = invocation.getAction();
        if (target instanceof ModelDriven) {
            target = ((ModelDriven<?>)target).getModel();
        }

        if (request.getContentLength() > 0) {
            final String encoding = request.getCharacterEncoding();
            InputStream is = request.getInputStream();
            InputStreamReader reader = encoding == null ? new InputStreamReader(is) : new InputStreamReader(is, encoding);

            if (requireAnnotations) {
                // Two-phase deserialization: deserialize into a fresh instance, then copy only authorized properties.
                // Requires a public no-arg constructor on the target class.
                // If absent, body processing is rejected entirely — a best-effort scrub cannot guarantee
                // that every nested unauthorized property is nulled out, so the safer choice is to skip.
                Object freshInstance = createFreshInstance(target.getClass());
                if (freshInstance != null) {
                    handler.toObject(invocation, reader, freshInstance);
                    copyAuthorizedProperties(freshInstance, target, invocation.getAction(), target, "");
                } else {
                    LOG.warn("REST body rejected: requireAnnotations=true but [{}] has no no-arg constructor; "
                            + "body deserialization skipped to preserve @StrutsParameter authorization integrity",
                            target.getClass().getName());
                }
            } else {
                // Direct deserialization (backward compat when requireAnnotations is not enabled)
                handler.toObject(invocation, reader, target);
            }
        }
        return invocation.invoke();
    }

    private Object createFreshInstance(Class<?> clazz) {
        try {
            return clazz.getDeclaredConstructor().newInstance();
        } catch (ReflectiveOperationException e) {
            LOG.debug("Cannot create fresh instance of [{}] via no-arg constructor: {}", clazz.getName(), e.getMessage());
            return null;
        }
    }

    /**
     * Recursively copies only authorized properties from {@code source} to {@code target},
     * enforcing {@code @StrutsParameter} depth semantics for nested object graphs.
     *
     * <p>{@code authTarget} is always the root action/model passed unchanged through all levels.
     * {@code isAuthorized} uses the full dot/bracket path against the root class, so the root
     * target must be used — not the nested object being visited at the current recursion depth.
     */
    private void copyAuthorizedProperties(
            Object source, Object target, Object action, Object authTarget, String prefix) throws Exception {
        BeanInfo beanInfo = Introspector.getBeanInfo(source.getClass(), Object.class);
        for (PropertyDescriptor pd : beanInfo.getPropertyDescriptors()) {
            Method readMethod = pd.getReadMethod();
            Method writeMethod = pd.getWriteMethod();
            if (readMethod == null || writeMethod == null) {
                continue;
            }

            String fullPath = prefix.isEmpty() ? pd.getName() : prefix + "." + pd.getName();

            // Always check against authTarget (root action/model), never the nested object being traversed
            if (!parameterAuthorizer.isAuthorized(fullPath, authTarget, action)) {
                LOG.warn("REST body parameter [{}] rejected by @StrutsParameter authorization on [{}]",
                        fullPath, authTarget.getClass().getName());
                continue;
            }

            Object sourceValue = readMethod.invoke(source);
            if (sourceValue == null) {
                // Intentionally skip null values: in two-phase deserialization, properties NOT present in the
                // request body will be null in the fresh instance. Copying null would clear pre-initialized
                // fields on the target. This is the safer default — an explicit JSON null and a missing field
                // are indistinguishable after deserialization into a fresh POJO.
                continue;
            }

            if (isNestedBeanType(sourceValue.getClass())) {
                // Complex bean: recurse to authorize nested fields, passing authTarget unchanged
                Object targetValue = readMethod.invoke(target);
                if (targetValue == null) {
                    Object newTarget = createFreshInstance(sourceValue.getClass());
                    if (newTarget != null) {
                        writeMethod.invoke(target, newTarget);
                        targetValue = newTarget;
                    } else {
                        // No no-arg constructor for the nested bean: skip rather than bulk-copy the
                        // unfiltered source value, which would bypass per-path authorization for every
                        // property underneath this node.
                        LOG.warn("REST nested bean [{}] skipped — no no-arg constructor for [{}],"
                                + " cannot authorize its nested properties",
                                fullPath, sourceValue.getClass().getName());
                        continue;
                    }
                }
                copyAuthorizedProperties(sourceValue, targetValue, action, authTarget, fullPath);
            } else if (sourceValue instanceof Collection) {
                writeMethod.invoke(target,
                        deepCopyAuthorizedCollection((Collection<?>) sourceValue, fullPath, authTarget, action));
            } else if (sourceValue instanceof Map) {
                writeMethod.invoke(target,
                        deepCopyAuthorizedMap((Map<?, ?>) sourceValue, fullPath, authTarget, action));
            } else if (sourceValue.getClass().isArray()) {
                writeMethod.invoke(target,
                        deepCopyAuthorizedArray(sourceValue, fullPath, authTarget, action));
            } else {
                writeMethod.invoke(target, sourceValue);
            }
        }
    }

    /**
     * Authorizes each complex element of a collection using indexed-path semantics ({@code path[0].field}),
     * matching {@code ParametersInterceptor} depth counting. Scalar elements are copied directly.
     * Elements whose class has no no-arg constructor are skipped to avoid copying an unfiltered object graph.
     */
    @SuppressWarnings({"unchecked", "rawtypes"})
    private Collection deepCopyAuthorizedCollection(
            Collection<?> source, String collectionPath, Object authTarget, Object action) throws Exception {
        // Preserve the collection type so that writeMethod.invoke does not fail when the setter
        // parameter is typed as Set, SortedSet, etc. Fall back to ArrayList for unrecognised types.
        Collection result;
        if (source instanceof SortedSet) {
            result = new TreeSet(((SortedSet) source).comparator());
        } else if (source instanceof Set) {
            result = new LinkedHashSet();
        } else {
            result = new ArrayList();
        }
        for (Object element : source) {
            if (element != null && isNestedBeanType(element.getClass())) {
                String elementPath = collectionPath + "[0]";
                if (!parameterAuthorizer.isAuthorized(elementPath, authTarget, action)) {
                    LOG.warn("REST collection element [{}] rejected by @StrutsParameter authorization", elementPath);
                    continue;
                }
                Object newElement = createFreshInstance(element.getClass());
                if (newElement != null) {
                    copyAuthorizedProperties(element, newElement, action, authTarget, elementPath);
                    result.add(newElement);
                } else {
                    // No no-arg constructor: skip element rather than copy an unfiltered object graph
                    LOG.warn("REST collection element [{}] skipped — no no-arg constructor for [{}]",
                            elementPath, element.getClass().getName());
                }
            } else {
                result.add(element);
            }
        }
        return result;
    }

    /**
     * Authorizes each complex map value using indexed-path semantics ({@code path[0]}),
     * consistent with OGNL bracket notation depth counting. Scalar values are copied directly.
     */
    @SuppressWarnings({"unchecked", "rawtypes"})
    private Map deepCopyAuthorizedMap(
            Map<?, ?> source, String mapPath, Object authTarget, Object action) throws Exception {
        // Preserve the map type so that writeMethod.invoke does not fail when the setter
        // parameter is typed as SortedMap, TreeMap, etc.
        Map result;
        if (source instanceof SortedMap) {
            result = new TreeMap(((SortedMap) source).comparator());
        } else {
            result = new LinkedHashMap();
        }
        for (Map.Entry<?, ?> entry : source.entrySet()) {
            Object value = entry.getValue();
            if (value != null && isNestedBeanType(value.getClass())) {
                String valuePath = mapPath + "[0]";
                if (!parameterAuthorizer.isAuthorized(valuePath, authTarget, action)) {
                    LOG.warn("REST map value [{}] rejected by @StrutsParameter authorization", valuePath);
                    continue;
                }
                Object newValue = createFreshInstance(value.getClass());
                if (newValue != null) {
                    copyAuthorizedProperties(value, newValue, action, authTarget, valuePath);
                    result.put(entry.getKey(), newValue);
                } else {
                    LOG.warn("REST map value [{}] skipped — no no-arg constructor for [{}]",
                            valuePath, value.getClass().getName());
                }
            } else {
                result.put(entry.getKey(), value);
            }
        }
        return result;
    }

    /**
     * Authorizes each complex element of an array ({@code Pojo[]}) using indexed-path semantics,
     * matching {@code ParametersInterceptor} depth counting. Scalar elements are copied directly.
     */
    private Object deepCopyAuthorizedArray(
            Object sourceArray, String arrayPath, Object authTarget, Object action) throws Exception {
        int length = Array.getLength(sourceArray);
        Class<?> componentType = sourceArray.getClass().getComponentType();
        Object result = Array.newInstance(componentType, length);
        for (int i = 0; i < length; i++) {
            Object element = Array.get(sourceArray, i);
            if (element != null && isNestedBeanType(element.getClass())) {
                String elementPath = arrayPath + "[0]";
                if (!parameterAuthorizer.isAuthorized(elementPath, authTarget, action)) {
                    LOG.warn("REST array element [{}] rejected by @StrutsParameter authorization", elementPath);
                    continue;
                }
                Object newElement = createFreshInstance(element.getClass());
                if (newElement != null) {
                    copyAuthorizedProperties(element, newElement, action, authTarget, elementPath);
                    Array.set(result, i, newElement);
                } else {
                    LOG.warn("REST array element [{}] skipped — no no-arg constructor for [{}]",
                            elementPath, element.getClass().getName());
                }
            } else {
                Array.set(result, i, element);
            }
        }
        return result;
    }

    /**
     * Determines whether a class represents a nested bean that should be recursively authorized,
     * as opposed to simple/leaf types (primitives, strings, collections, maps, arrays, enums) that
     * are handled directly.
     */
    private boolean isNestedBeanType(Class<?> clazz) {
        if (clazz.isPrimitive() || clazz.isEnum() || clazz.isArray()) {
            return false;
        }
        // Exclude standard library value/leaf types that have no meaningful bean properties to recurse into.
        // java.lang.*, java.math.* — primitives, String, Number subclasses, etc.
        // java.util.* leaf types — UUID, Currency, Locale, Date, etc. (NOT Collection/Map which are handled separately)
        if (clazz.getName().startsWith("java.lang.") || clazz.getName().startsWith("java.math.")) {
            return false;
        }
        if (clazz.getName().startsWith("java.util.") && !Collection.class.isAssignableFrom(clazz)
                && !Map.class.isAssignableFrom(clazz)) {
            return false;
        }
        if (java.time.temporal.Temporal.class.isAssignableFrom(clazz)) {
            return false;
        }
        if (clazz.getName().startsWith("java.time.")) {
            return false;
        }
        if (clazz.getName().startsWith("java.net.") || clazz.getName().startsWith("java.io.")
                || clazz.getName().startsWith("java.nio.")) {
            return false;
        }
        if (Collection.class.isAssignableFrom(clazz) || Map.class.isAssignableFrom(clazz)) {
            return false;
        }
        return true;
    }

}
