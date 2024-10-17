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
package com.opensymphony.xwork2;

import com.opensymphony.xwork2.conversion.impl.ConversionData;
import com.opensymphony.xwork2.inject.Container;
import com.opensymphony.xwork2.util.ValueStack;
import org.apache.struts2.dispatcher.HttpParameters;
import org.apache.struts2.dispatcher.mapper.ActionMapping;

import javax.servlet.ServletContext;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.jsp.PageContext;
import java.util.Locale;
import java.util.Map;

/**
 * {@inheritDoc}
 *
 * @deprecated since 6.7.0, use {@link org.apache.struts2.ActionContext} instead.
 */
@Deprecated
public class ActionContext extends org.apache.struts2.ActionContext {

    private ActionContext(org.apache.struts2.ActionContext actualContext) {
        super(actualContext.getContextMap());
    }

    public static ActionContext adapt(org.apache.struts2.ActionContext actualContext) {
        return actualContext != null ? new ActionContext(actualContext) : null;
    }

    public static ActionContext of(Map<String, Object> context) {
        return adapt(org.apache.struts2.ActionContext.of(context));
    }

    public static ActionContext of() {
        return adapt(org.apache.struts2.ActionContext.of());
    }

    public static ActionContext bind(ActionContext actionContext) {
        return adapt(org.apache.struts2.ActionContext.bind(actionContext));
    }

    public static boolean containsValueStack(Map<String, Object> context) {
        return org.apache.struts2.ActionContext.containsValueStack(context);
    }

    public static void clear() {
        org.apache.struts2.ActionContext.clear();
    }

    public static ActionContext getContext() {
        return adapt(org.apache.struts2.ActionContext.getContext());
    }

    @Override
    public ActionContext bind() {
        super.bind();
        return this;
    }

    public ActionContext withActionInvocation(ActionInvocation actionInvocation) {
        return withActionInvocation((org.apache.struts2.ActionInvocation) actionInvocation);
    }

    @Override
    public ActionContext withActionInvocation(org.apache.struts2.ActionInvocation actionInvocation) {
        super.withActionInvocation(actionInvocation);
        return this;
    }

    @Override
    public ActionInvocation getActionInvocation() {
        return ActionInvocation.adapt(super.getActionInvocation());
    }

    @Override
    public ActionContext withApplication(Map<String, Object> application) {
        super.withApplication(application);
        return this;
    }

    @Override
    public Map<String, Object> getApplication() {
        return super.getApplication();
    }

    @Override
    public Map<String, Object> getContextMap() {
        return super.getContextMap();
    }

    @Override
    public ActionContext withConversionErrors(Map<String, ConversionData> conversionErrors) {
        super.withConversionErrors(conversionErrors);
        return this;
    }

    @Override
    public Map<String, ConversionData> getConversionErrors() {
        return super.getConversionErrors();
    }

    @Override
    public ActionContext withLocale(Locale locale) {
        super.withLocale(locale);
        return this;
    }

    @Override
    public Locale getLocale() {
        return super.getLocale();
    }

    @Override
    public ActionContext withActionName(String actionName) {
        super.withActionName(actionName);
        return this;
    }

    @Override
    public String getActionName() {
        return super.getActionName();
    }

    @Override
    public ActionContext withParameters(HttpParameters parameters) {
        super.withParameters(parameters);
        return this;
    }

    @Override
    public HttpParameters getParameters() {
        return super.getParameters();
    }

    @Override
    public ActionContext withSession(Map<String, Object> session) {
        super.withSession(session);
        return this;
    }

    @Override
    public Map<String, Object> getSession() {
        return super.getSession();
    }

    @Override
    public ActionContext withValueStack(ValueStack valueStack) {
        super.withValueStack(valueStack);
        return this;
    }

    @Override
    public ValueStack getValueStack() {
        return super.getValueStack();
    }

    @Override
    public ActionContext withContainer(Container container) {
        super.withContainer(container);
        return this;
    }

    @Override
    public Container getContainer() {
        return super.getContainer();
    }

    @Override
    public <T> T getInstance(Class<T> type) {
        return super.getInstance(type);
    }

    @Override
    public Object get(String key) {
        return super.get(key);
    }

    @Override
    public void put(String key, Object value) {
        super.put(key, value);
    }

    @Override
    public ServletContext getServletContext() {
        return super.getServletContext();
    }

    @Override
    public ActionContext withServletContext(ServletContext servletContext) {
        super.withServletContext(servletContext);
        return this;
    }

    @Override
    public HttpServletRequest getServletRequest() {
        return super.getServletRequest();
    }

    @Override
    public ActionContext withServletRequest(HttpServletRequest request) {
        super.withServletRequest(request);
        return this;
    }

    @Override
    public HttpServletResponse getServletResponse() {
        return super.getServletResponse();
    }

    @Override
    public ActionContext withServletResponse(HttpServletResponse response) {
        super.withServletResponse(response);
        return this;
    }

    @Override
    public PageContext getPageContext() {
        return super.getPageContext();
    }

    @Override
    public ActionContext withPageContext(PageContext pageContext) {
        super.withPageContext(pageContext);
        return this;
    }

    @Override
    public ActionMapping getActionMapping() {
        return super.getActionMapping();
    }

    @Override
    public ActionContext withActionMapping(ActionMapping actionMapping) {
        super.withActionMapping(actionMapping);
        return this;
    }

    @Override
    public ActionContext withExtraContext(Map<String, Object> extraContext) {
        super.withExtraContext(extraContext);
        return this;
    }

    @Override
    public ActionContext with(String key, Object value) {
        super.with(key, value);
        return this;
    }
}
