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
                // Two-phase deserialization: deserialize into a fresh instance, then copy only authorized properties
                Object freshInstance;
                try {
                    freshInstance = target.getClass().getDeclaredConstructor().newInstance();
                } catch (ReflectiveOperationException e) {
                    throw new IllegalStateException(
                            "Cannot create fresh instance of " + target.getClass().getName()
                            + " for parameter authorization. REST body deserialization requires a public no-arg constructor"
                            + " when struts.parameters.requireAnnotations is enabled.", e);
                }

                handler.toObject(invocation, reader, freshInstance);
                copyAuthorizedProperties(freshInstance, target, invocation.getAction());
            } else {
                // Direct deserialization (backward compat when requireAnnotations is not enabled)
                handler.toObject(invocation, reader, target);
            }
        }
        return invocation.invoke();
    }

    private void copyAuthorizedProperties(Object source, Object target, Object action) throws Exception {
        BeanInfo beanInfo = Introspector.getBeanInfo(source.getClass(), Object.class);
        for (PropertyDescriptor pd : beanInfo.getPropertyDescriptors()) {
            Method readMethod = pd.getReadMethod();
            Method writeMethod = pd.getWriteMethod();
            if (readMethod == null || writeMethod == null) {
                continue;
            }

            if (!parameterAuthorizer.isAuthorized(pd.getName(), target, action)) {
                LOG.warn("REST body parameter [{}] rejected by @StrutsParameter authorization on [{}]",
                        pd.getName(), target.getClass().getName());
                continue;
            }

            Object value = readMethod.invoke(source);
            if (value == null) {
                continue; // Skip null values to avoid overwriting pre-initialized fields on target
            }
            writeMethod.invoke(target, value);
        }
    }

}
