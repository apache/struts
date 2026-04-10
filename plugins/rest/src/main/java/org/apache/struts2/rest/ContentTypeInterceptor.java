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
import java.lang.reflect.Method;
import java.util.Collection;
import java.util.Map;

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
                // This requires a public no-arg constructor on the target class. If absent, fall back to
                // single-phase deserialization with post-deserialization scrubbing of unauthorized properties.
                Object freshInstance = createFreshInstance(target.getClass());
                if (freshInstance != null) {
                    handler.toObject(invocation, reader, freshInstance);
                    copyAuthorizedProperties(freshInstance, target, invocation.getAction(), "");
                } else {
                    LOG.warn("No no-arg constructor for [{}], using single-phase deserialization with post-scrub", target.getClass().getName());
                    handler.toObject(invocation, reader, target);
                    scrubUnauthorizedProperties(target, invocation.getAction());
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
     * Recursively copies only authorized properties from source to target, enforcing {@code @StrutsParameter}
     * depth semantics for nested object graphs.
     */
    private void copyAuthorizedProperties(Object source, Object target, Object action, String prefix) throws Exception {
        BeanInfo beanInfo = Introspector.getBeanInfo(source.getClass(), Object.class);
        for (PropertyDescriptor pd : beanInfo.getPropertyDescriptors()) {
            Method readMethod = pd.getReadMethod();
            Method writeMethod = pd.getWriteMethod();
            if (readMethod == null || writeMethod == null) {
                continue;
            }

            String fullPath = prefix.isEmpty() ? pd.getName() : prefix + "." + pd.getName();

            if (!parameterAuthorizer.isAuthorized(fullPath, target, action)) {
                LOG.warn("REST body parameter [{}] rejected by @StrutsParameter authorization on [{}]",
                        fullPath, target.getClass().getName());
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

            // For complex bean types (not primitives, strings, collections, etc.), recurse to enforce
            // nested authorization. Collections/Maps/arrays are copied as-is since their contents were
            // already deserialized and the depth check on the parent property covers them.
            if (isNestedBeanType(sourceValue.getClass())) {
                Object targetValue = readMethod.invoke(target);
                if (targetValue == null) {
                    Object newTarget = createFreshInstance(sourceValue.getClass());
                    if (newTarget != null) {
                        writeMethod.invoke(target, newTarget);
                        targetValue = newTarget;
                    } else {
                        // Cannot recurse without a fresh target — copy whole value
                        writeMethod.invoke(target, sourceValue);
                        continue;
                    }
                }
                copyAuthorizedProperties(sourceValue, targetValue, action, fullPath);
            } else {
                writeMethod.invoke(target, sourceValue);
            }
        }
    }

    /**
     * Fallback for actions without a no-arg constructor: scrub unauthorized properties after direct deserialization.
     */
    private void scrubUnauthorizedProperties(Object target, Object action) throws Exception {
        BeanInfo beanInfo = Introspector.getBeanInfo(target.getClass(), Object.class);
        for (PropertyDescriptor pd : beanInfo.getPropertyDescriptors()) {
            Method readMethod = pd.getReadMethod();
            Method writeMethod = pd.getWriteMethod();
            if (readMethod == null || writeMethod == null) {
                continue;
            }

            if (!parameterAuthorizer.isAuthorized(pd.getName(), target, action)) {
                Object value = readMethod.invoke(target);
                if (value != null) {
                    try {
                        writeMethod.invoke(target, (Object) null);
                    } catch (IllegalArgumentException e) {
                        // Primitive type — cannot null, set to default
                        LOG.debug("Cannot null primitive property [{}], skipping scrub", pd.getName());
                    }
                }
            }
        }
    }

    /**
     * Determines whether a class represents a nested bean that should be recursively authorized,
     * as opposed to simple/leaf types that are copied directly.
     */
    private boolean isNestedBeanType(Class<?> clazz) {
        if (clazz.isPrimitive() || clazz.isEnum() || clazz.isArray()) {
            return false;
        }
        if (clazz.getName().startsWith("java.lang.") || clazz.getName().startsWith("java.math.")) {
            return false;
        }
        if (java.util.Date.class.isAssignableFrom(clazz)) {
            return false;
        }
        if (java.time.temporal.Temporal.class.isAssignableFrom(clazz)) {
            return false;
        }
        if (Collection.class.isAssignableFrom(clazz) || Map.class.isAssignableFrom(clazz)) {
            return false;
        }
        return true;
    }

}
