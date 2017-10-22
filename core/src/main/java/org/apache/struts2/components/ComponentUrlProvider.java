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

import com.opensymphony.xwork2.util.ValueStack;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
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
    private Map parameters;

    /**
     *
     * @param component The component used to delegate some calls to
     * @param parameters parameters passed from &lt;param...&gt;
     */
    public ComponentUrlProvider(Component component, Map parameters) {
        this.component = component;
        this.parameters = parameters;
    }

    public String determineActionURL(String action, String namespace, String method, HttpServletRequest req, HttpServletResponse res, Map parameters, String scheme, boolean includeContext, boolean encodeResult, boolean forceAddSchemeHostAndPort, boolean escapeAmp) {
        return component.determineActionURL(action, namespace, method, req, res, parameters, scheme, includeContext, encodeResult, forceAddSchemeHostAndPort, escapeAmp);
    }

    public String determineNamespace(String namespace, ValueStack stack, HttpServletRequest req) {
        return component.determineNamespace(namespace, stack, req);
    }

    public String findString(String expr) {
        return component.findString(expr);
    }

    public Map getParameters() {
        return parameters;
    }

    public HttpServletRequest getHttpServletRequest() {
        return httpServletRequest;
    }

    public void setHttpServletRequest(HttpServletRequest httpServletRequest) {
        this.httpServletRequest = httpServletRequest;
    }

    public HttpServletResponse getHttpServletResponse() {
        return httpServletResponse;
    }

    public void setHttpServletResponse(HttpServletResponse httpServletResponse) {
        this.httpServletResponse = httpServletResponse;
    }

    public String getIncludeParams() {
        return includeParams;
    }

    public void setIncludeParams(String includeParams) {
        this.includeParams = includeParams;
    }

    public String getScheme() {
        return scheme;
    }

    public void setScheme(String scheme) {
        this.scheme = scheme;
    }

    public boolean isPutInContext() {
        return component instanceof ContextBean;
    }

    public String getVar() {
        return isPutInContext() ? ((ContextBean)component).getVar() :  null;
    }

    public String getValue() {
        return value;
    }

    public void setValue(String value) {
        this.value = value;
    }

    public String getAction() {
        return action;
    }

    public void setAction(String action) {
        this.action = action;
    }

    public String getNamespace() {
        return namespace;
    }

    public void setNamespace(String namespace) {
        this.namespace = namespace;
    }

    public String getMethod() {
        return method;
    }

    public void setMethod(String method) {
        this.method = method;
    }

    public boolean isEncode() {
        return encode;
    }

    public void setEncode(boolean encode) {
        this.encode = encode;
    }

    public boolean isIncludeContext() {
        return includeContext;
    }

    public void setIncludeContext(boolean includeContext) {
        this.includeContext = includeContext;
    }

    public boolean isEscapeAmp() {
        return escapeAmp;
    }

    public void setEscapeAmp(boolean escapeAmp) {
        this.escapeAmp = escapeAmp;
    }

    public String getPortletMode() {
        return portletMode;
    }

    public void setPortletMode(String portletMode) {
        this.portletMode = portletMode;
    }

    public String getWindowState() {
        return windowState;
    }

    public void setWindowState(String windowState) {
        this.windowState = windowState;
    }

    public String getPortletUrlType() {
        return portletUrlType;
    }

    public ValueStack getStack() {
        return component.getStack();
    }

    public void setPortletUrlType(String portletUrlType) {
        this.portletUrlType = portletUrlType;
    }

    public String getAnchor() {
        return anchor;
    }

    public void setAnchor(String anchor) {
        this.anchor = anchor;
    }

    public boolean isForceAddSchemeHostAndPort() {
        return forceAddSchemeHostAndPort;
    }

    public void setForceAddSchemeHostAndPort(boolean forceAddSchemeHostAndPort) {
        this.forceAddSchemeHostAndPort = forceAddSchemeHostAndPort;
    }

    public void putInContext(String result) {
        if (isPutInContext()) {
            ((ContextBean)component).putInContext(result);
        }
    }

    public String getUrlIncludeParams() {
        return urlIncludeParams;
    }

    public void setUrlIncludeParams(String urlIncludeParams) {
        this.urlIncludeParams = urlIncludeParams;
    }

    public ExtraParameterProvider getExtraParameterProvider() {
        return extraParameterProvider;
    }

    public void setExtraParameterProvider(ExtraParameterProvider extraParameterProvider) {
        this.extraParameterProvider = extraParameterProvider;
    }

    public UrlRenderer getUrlRenderer() {
        return urlRenderer;
    }

    public void setUrlRenderer(UrlRenderer urlRenderer) {
        this.urlRenderer = urlRenderer;
    }
}
