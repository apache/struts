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
package org.apache.struts2.components;

import org.apache.struts2.util.ValueStack;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

import java.util.Map;

/**
 * Default implementation of UrlProvider
 */
public class ComponentUrlProvider implements UrlProvider {
    protected HttpServletRequest httpServletRequest;
    protected HttpServletResponse httpServletResponse;

    protected String includeParams;
    protected String scheme;
    protected String value;
    protected String action;
    protected String namespace;
    protected String method;
    protected boolean encode = true;
    protected boolean includeContext = true;
    protected boolean escapeAmp = true;
    protected String portletMode;
    protected String windowState;
    protected String portletUrlType;
    protected String anchor;
    protected boolean forceAddSchemeHostAndPort;
    protected String urlIncludeParams;
    protected ExtraParameterProvider extraParameterProvider;
    protected UrlRenderer urlRenderer;

    protected Component component;
    private final Map parameters;

    /**
     *
     * @param component The component used to delegate some calls to
     * @param parameters parameters passed from &lt;param...&gt;
     */
    public ComponentUrlProvider(Component component, Map parameters) {
        this.component = component;
        this.parameters = parameters;
    }

    @Override
    public String determineActionURL(String action, String namespace, String method, HttpServletRequest req, HttpServletResponse res, Map parameters, String scheme, boolean includeContext, boolean encodeResult, boolean forceAddSchemeHostAndPort, boolean escapeAmp) {
        return component.determineActionURL(action, namespace, method, req, res, parameters, scheme, includeContext, encodeResult, forceAddSchemeHostAndPort, escapeAmp);
    }

    @Override
    public String determineNamespace(String namespace, ValueStack stack, HttpServletRequest req) {
        return component.determineNamespace(namespace, stack, req);
    }

    @Override
    public String findString(String expr) {
        return component.findString(expr);
    }

    @Override
    public Map getParameters() {
        return parameters;
    }

    @Override
    public HttpServletRequest getHttpServletRequest() {
        return httpServletRequest;
    }

    @Override
    public void setHttpServletRequest(HttpServletRequest httpServletRequest) {
        this.httpServletRequest = httpServletRequest;
    }

    @Override
    public HttpServletResponse getHttpServletResponse() {
        return httpServletResponse;
    }

    @Override
    public void setHttpServletResponse(HttpServletResponse httpServletResponse) {
        this.httpServletResponse = httpServletResponse;
    }

    @Override
    public String getIncludeParams() {
        return includeParams;
    }

    @Override
    public void setIncludeParams(String includeParams) {
        this.includeParams = includeParams;
    }

    @Override
    public String getScheme() {
        return scheme;
    }

    @Override
    public void setScheme(String scheme) {
        this.scheme = scheme;
    }

    @Override
    public boolean isPutInContext() {
        return component instanceof ContextBean;
    }

    @Override
    public String getVar() {
        return isPutInContext() ? ((ContextBean)component).getVar() :  null;
    }

    @Override
    public String getValue() {
        return value;
    }

    @Override
    public void setValue(String value) {
        this.value = value;
    }

    @Override
    public String getAction() {
        return action;
    }

    @Override
    public void setAction(String action) {
        this.action = action;
    }

    @Override
    public String getNamespace() {
        return namespace;
    }

    @Override
    public void setNamespace(String namespace) {
        this.namespace = namespace;
    }

    @Override
    public String getMethod() {
        return method;
    }

    @Override
    public void setMethod(String method) {
        this.method = method;
    }

    @Override
    public boolean isEncode() {
        return encode;
    }

    @Override
    public void setEncode(boolean encode) {
        this.encode = encode;
    }

    @Override
    public boolean isIncludeContext() {
        return includeContext;
    }

    @Override
    public void setIncludeContext(boolean includeContext) {
        this.includeContext = includeContext;
    }

    @Override
    public boolean isEscapeAmp() {
        return escapeAmp;
    }

    @Override
    public void setEscapeAmp(boolean escapeAmp) {
        this.escapeAmp = escapeAmp;
    }

    @Override
    public String getPortletMode() {
        return portletMode;
    }

    @Override
    public void setPortletMode(String portletMode) {
        this.portletMode = portletMode;
    }

    @Override
    public String getWindowState() {
        return windowState;
    }

    @Override
    public void setWindowState(String windowState) {
        this.windowState = windowState;
    }

    @Override
    public String getPortletUrlType() {
        return portletUrlType;
    }

    @Override
    public ValueStack getStack() {
        return component.getStack();
    }

    @Override
    public void setPortletUrlType(String portletUrlType) {
        this.portletUrlType = portletUrlType;
    }

    @Override
    public String getAnchor() {
        return anchor;
    }

    @Override
    public void setAnchor(String anchor) {
        this.anchor = anchor;
    }

    @Override
    public boolean isForceAddSchemeHostAndPort() {
        return forceAddSchemeHostAndPort;
    }

    @Override
    public void setForceAddSchemeHostAndPort(boolean forceAddSchemeHostAndPort) {
        this.forceAddSchemeHostAndPort = forceAddSchemeHostAndPort;
    }

    @Override
    public void putInContext(String result) {
        if (isPutInContext()) {
            ((ContextBean)component).putInContext(result);
        }
    }

    @Override
    public String getUrlIncludeParams() {
        return urlIncludeParams;
    }

    @Override
    public void setUrlIncludeParams(String urlIncludeParams) {
        this.urlIncludeParams = urlIncludeParams;
    }

    @Override
    public ExtraParameterProvider getExtraParameterProvider() {
        return extraParameterProvider;
    }

    @Override
    public void setExtraParameterProvider(ExtraParameterProvider extraParameterProvider) {
        this.extraParameterProvider = extraParameterProvider;
    }

    public UrlRenderer getUrlRenderer() {
        return urlRenderer;
    }

    @Override
    public void setUrlRenderer(UrlRenderer urlRenderer) {
        this.urlRenderer = urlRenderer;
    }
}
