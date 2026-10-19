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

import org.apache.struts2.ActionInvocation;
import org.apache.struts2.UnknownHandlerManager;
import org.apache.struts2.action.Action;
import org.apache.struts2.config.entities.ActionConfig;
import org.apache.struts2.interceptor.AbstractInterceptor;
import org.apache.struts2.util.AnnotationUtils;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.lang.annotation.Annotation;
import java.lang.reflect.AnnotatedElement;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.EnumSet;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

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
 * <p>
 * The allowed methods can also be declared on the action in XML with the {@code http-method} attribute.
 * When both the attribute and annotations are present, the request method must satisfy both.
 * <p>
 * If no result can be resolved under the bad request result name, the response is
 * {@code 405 Method Not Allowed} with an {@code Allow} header listing the accepted methods.
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
            HttpMethod requestedHttpMethod = HttpMethod.parseOrNull(request.getMethod());
            if (requestedHttpMethod != null) {
                ((HttpMethodAware) (action)).setMethod(requestedHttpMethod);
            }
        }

        if (invocation.getProxy().isMethodSpecified()) {
            Method method = action.getClass().getMethod(invocation.getProxy().getMethod());
            if (AnnotationUtils.isAnnotatedBy(method, HTTP_METHOD_ANNOTATIONS)) {
                LOG.debug("Action's method: {} annotated with: {}, checking if request: {} meets allowed methods!",
                    invocation.getProxy().getMethod(), AllowedHttpMethod.class.getSimpleName(), request.getMethod());
                return doIntercept(invocation, method);
            }
        }
        if (AnnotationUtils.isAnnotatedBy(action.getClass(), HTTP_METHOD_ANNOTATIONS)) {
            LOG.debug("Action: {} annotated with: {}, checking if request: {} meets allowed methods!",
                action, AllowedHttpMethod.class.getSimpleName(), request.getMethod());
            return doIntercept(invocation, action.getClass());
        }

        Set<HttpMethod> configuredMethods = invocation.getProxy().getConfig().getHttpMethods();
        if (!configuredMethods.isEmpty()) {
            LOG.debug("Action: {} declares http-method: {}, checking if request: {} meets allowed methods!",
                action, configuredMethods, request.getMethod());
            return checkAllowedMethods(invocation, configuredMethods);
        }

        return invocation.invoke();
    }

    protected String doIntercept(ActionInvocation invocation, AnnotatedElement element) throws Exception {
        Set<HttpMethod> allowedMethods = EnumSet.noneOf(HttpMethod.class);
        allowedMethods.addAll(readAllowedMethods(element));

        Set<HttpMethod> configuredMethods = invocation.getProxy().getConfig().getHttpMethods();
        if (!configuredMethods.isEmpty()) {
            allowedMethods.retainAll(configuredMethods);
        }

        return checkAllowedMethods(invocation, allowedMethods);
    }

    private String checkAllowedMethods(ActionInvocation invocation, Set<HttpMethod> allowedMethods) throws Exception {
        HttpServletRequest request = invocation.getInvocationContext().getServletRequest();
        String requestedMethod = request.getMethod();

        if (isAllowed(requestedMethod, allowedMethods)) {
            LOG.trace("Request method: {} matches allowed methods: {}, continuing invocation!", requestedMethod, allowedMethods);
            return invocation.invoke();
        } else {
            LOG.trace("Request method: {} doesn't match allowed methods: {}, rejecting request!", requestedMethod, allowedMethods);
            return rejectRequest(invocation, allowedMethods);
        }
    }

    private boolean isAllowed(String requestedMethod, Set<HttpMethod> allowedMethods) {
        return allowedMethods.stream().anyMatch(method -> method.name().equalsIgnoreCase(requestedMethod));
    }

    /**
     * @return the bad request result name when the action maps that result, otherwise {@link Action#NONE}
     * after sending {@code 405 Method Not Allowed} with an {@code Allow} header
     * @since 7.5.0
     */
    protected String rejectRequest(ActionInvocation invocation, Collection<HttpMethod> allowedMethods) throws IOException {
        String resultName = getBadRequestResultName(invocation);
        if (canResolveResult(invocation, resultName)) {
            return resultName;
        }

        LOG.debug("No result mapped for: {}, sending: {} with allowed methods: {}",
            resultName, HttpServletResponse.SC_METHOD_NOT_ALLOWED, allowedMethods);
        HttpServletResponse response = invocation.getInvocationContext().getServletResponse();
        response.setHeader("Allow", allowedMethods.stream().map(HttpMethod::name).collect(Collectors.joining(", ")));
        response.sendError(HttpServletResponse.SC_METHOD_NOT_ALLOWED);
        return Action.NONE;
    }

    private boolean canResolveResult(ActionInvocation invocation, String resultName) {
        ActionConfig config = invocation.getProxy().getConfig();
        if (config.getResults().containsKey(resultName) || config.getResults().containsKey("*")) {
            return true;
        }
        UnknownHandlerManager unknownHandlerManager = invocation.getInvocationContext().getContainer().getInstance(UnknownHandlerManager.class);
        return unknownHandlerManager.hasUnknownHandlers()
            && unknownHandlerManager.handleUnknownResult(invocation.getInvocationContext(),
                invocation.getProxy().getActionName(), config, resultName) != null;
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
