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
package org.apache.struts2.interceptor.httpmethod;

import com.opensymphony.xwork2.ActionInvocation;
import com.opensymphony.xwork2.interceptor.AbstractInterceptor;
import com.opensymphony.xwork2.util.AnnotationUtils;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import jakarta.servlet.http.HttpServletRequest;
import java.lang.annotation.Annotation;
import java.lang.reflect.AnnotatedElement;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

/**
 * Interceptor is used to control with what http methods action can be called,
 * if request with not allowed method was performed, {@link #badRequestResultName}
 * will be returned or if action implements {@link HttpMethodAware}
 * and {@link HttpMethodAware#getBadRequestResultName()} returns non-null result name,
 * thus value will be used instead.
 * <p/>
 * To limit allowed http methods, annotate action class with {@link AllowedHttpMethod} and specify,
 * which methods are allowed. You can also use shorter versions {@link HttpGet}, {@link HttpPost},
 * {@link HttpPut}, {@link HttpDelete} and {@link HttpGetOrPost}
 * <p>
 * You can combine any of these annotations to achieve required allowed methods' filtering.
 *
 * @see HttpMethodAware
 * @see HttpMethod
 * @see AllowedHttpMethod
 * @see HttpGet
 * @see HttpPost
 * @see HttpPut
 * @see HttpDelete
 * @see HttpGetOrPost
 * @since 6.2.0
 */
public class HttpMethodInterceptor extends AbstractInterceptor {

    @SuppressWarnings({"unchecked"})
    private static final Class<? extends Annotation>[] HTTP_METHOD_ANNOTATIONS = new Class[]{
        AllowedHttpMethod.class,
        HttpGet.class,
        HttpPost.class,
        HttpGetOrPost.class,
        HttpPut.class,
        HttpDelete.class
    };

    private static final Logger LOG = LogManager.getLogger(HttpMethodInterceptor.class);

    private String badRequestResultName = "bad-request";

    @Override
    public String intercept(ActionInvocation invocation) throws Exception {
        Object action = invocation.getAction();
        HttpServletRequest request = invocation.getInvocationContext().getServletRequest();

        if (action instanceof HttpMethodAware) {
            LOG.debug("Action: {} implements: {}, setting request method: {}",
                action, HttpMethodAware.class.getSimpleName(), request.getMethod());
            ((HttpMethodAware) (action)).setMethod(HttpMethod.parse(request.getMethod()));
        }

        if (invocation.getProxy().isMethodSpecified()) {
            Method method = action.getClass().getMethod(invocation.getProxy().getMethod());
            if (AnnotationUtils.isAnnotatedBy(method, HTTP_METHOD_ANNOTATIONS)) {
                LOG.debug("Action's method: {} annotated with: {}, checking if request: {} meets allowed methods!",
                    invocation.getProxy().getMethod(), AllowedHttpMethod.class.getSimpleName(), request.getMethod());
                return doIntercept(invocation, method);
            }
        } else if (AnnotationUtils.isAnnotatedBy(action.getClass(), HTTP_METHOD_ANNOTATIONS)) {
            LOG.debug("Action: {} annotated with: {}, checking if request: {} meets allowed methods!",
                action, AllowedHttpMethod.class.getSimpleName(), request.getMethod());
            return doIntercept(invocation, action.getClass());
        }

        return invocation.invoke();
    }

    protected String doIntercept(ActionInvocation invocation, AnnotatedElement element) throws Exception {
        List<HttpMethod> allowedMethods = readAllowedMethods(element);
        HttpServletRequest request = invocation.getInvocationContext().getServletRequest();
        HttpMethod requestedMethod = HttpMethod.parse(request.getMethod());

        if (allowedMethods.contains(requestedMethod)) {
            LOG.trace("Request method: {} matches allowed methods: {}, continuing invocation!", requestedMethod, allowedMethods);
            return invocation.invoke();
        } else {
            LOG.trace("Request method: {} doesn't match allowed methods: {}, continuing invocation!", requestedMethod, allowedMethods);
            return getBadRequestResultName(invocation);
        }
    }

    protected List<HttpMethod> readAllowedMethods(AnnotatedElement element) {
        List<HttpMethod> allowedMethods = new ArrayList<>();
        if (AnnotationUtils.isAnnotatedBy(element, AllowedHttpMethod.class)) {
            allowedMethods.addAll(Arrays.asList(element.getAnnotation(AllowedHttpMethod.class).value()));
        }
        if (AnnotationUtils.isAnnotatedBy(element, HttpGet.class)) {
            allowedMethods.addAll(Arrays.asList(element.getAnnotation(HttpGet.class).value()));
        }
        if (AnnotationUtils.isAnnotatedBy(element, HttpPost.class)) {
            allowedMethods.addAll(Arrays.asList(element.getAnnotation(HttpPost.class).value()));
        }
        if (AnnotationUtils.isAnnotatedBy(element, HttpPut.class)) {
            allowedMethods.addAll(Arrays.asList(element.getAnnotation(HttpPut.class).value()));
        }
        if (AnnotationUtils.isAnnotatedBy(element, HttpDelete.class)) {
            allowedMethods.addAll(Arrays.asList(element.getAnnotation(HttpDelete.class).value()));
        }
        if (AnnotationUtils.isAnnotatedBy(element, HttpGetOrPost.class)) {
            allowedMethods.addAll(Arrays.asList(element.getAnnotation(HttpGetOrPost.class).value()));
        }
        return Collections.unmodifiableList(allowedMethods);
    }

    protected String getBadRequestResultName(ActionInvocation invocation) {
        Object action = invocation.getAction();
        String resultName = badRequestResultName;
        if (action instanceof HttpMethodAware) {
            String actionResultName = ((HttpMethodAware) action).getBadRequestResultName();
            if (actionResultName != null) {
                resultName = actionResultName;
            }
        }
        LOG.trace("Bad request result name is: {}", resultName);
        return resultName;
    }

    public void setBadRequestResultName(String badRequestResultName) {
        this.badRequestResultName = badRequestResultName;
    }

}
