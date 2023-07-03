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
package org.apache.struts2.views.jsp;

import com.mockobjects.ExpectationList;
import com.mockobjects.ExpectationSet;
import com.mockobjects.ExpectationValue;
import com.mockobjects.MapEntry;
import com.mockobjects.MockObject;
import com.mockobjects.ReturnObjectBag;
import com.mockobjects.ReturnObjectList;
import com.mockobjects.ReturnValue;

import jakarta.servlet.*;
import jakarta.servlet.http.*;

import java.io.BufferedReader;
import java.io.IOException;
import java.security.Principal;
import java.util.Collection;
import java.util.Enumeration;
import java.util.Locale;
import java.util.Map;

public class MockHttpServletRequest  extends MockObject implements HttpServletRequest {
    private final ReturnObjectBag myParameters = new ReturnObjectBag("parameters");
    private final ReturnObjectBag myHeaders = new ReturnObjectBag("headers");
    private final ReturnValue myHttpSession = new ReturnValue("session");
    private final ReturnValue myContentTypeToReturn = new ReturnValue("content type");
    private final ReturnValue myContextPath = new ReturnValue("context path");
    private final ReturnValue myPathInfo = new ReturnValue("path info");
    private final ReturnValue myRemoteAddress = new ReturnValue("remote address");
    private final ReturnValue myRequestURI = new ReturnValue("request uri");
    private final ReturnValue method = new ReturnValue("method");
    private final ReturnValue protocol = new ReturnValue("protocol");
    private final ReturnValue inputStream = new ReturnValue("input stream");
    private final ReturnValue myUserPrincipal = new ReturnValue("user principal");
    private final ReturnValue myAttributesNames = new ReturnValue("attribute names");
    private final ReturnValue queryString = new ReturnValue("query string");
    private final ReturnValue scheme = new ReturnValue("scheme");
    private final ReturnValue serverName = new ReturnValue("server name");
    private final ReturnValue reader = new ReturnValue("reader");
    private final ExpectationSet mySetAttributes = new ExpectationSet("HttpServletRequest.setAttribute");
    private final ExpectationSet myRemoveAttributes = new ExpectationSet("HttpServletRequest.removeAttribute");
    private final ReturnObjectList myAttributesToReturn = new ReturnObjectList("attributes");
    private final ExpectationValue myContentType = new ExpectationValue("content type");
    private final ExpectationList myGetAttributeNames = new ExpectationList("get attribute names");
    private final ReturnValue servletPath = new ReturnValue("servlet path");
    private final ReturnValue parameterMap = new ReturnValue("parameter map");
    private final ReturnValue myParameterNames = new ReturnValue("parameter names");
    private final ReturnValue requestDispatcher = new ReturnValue("request dispatcher");
    private final ExpectationValue requestDispatcherURI = new ExpectationValue("request dispatcher uri");
    private final ExpectationValue createSession = new ExpectationValue("create session");

    public MockHttpServletRequest() {
    }

    public void setupGetAttribute(Object anAttributeToReturn) {
        this.myAttributesToReturn.addObjectToReturn(anAttributeToReturn);
    }

    public void addExpectedGetAttributeName(String anAttributeName) {
        this.myGetAttributeNames.addExpected(anAttributeName);
    }

    public Object getAttribute(String anAttributeName) {
        this.myGetAttributeNames.addActual(anAttributeName);
        return this.myAttributesToReturn.nextReturnObject();
    }

    public void setupGetAttrubuteNames(Enumeration attributeNames) {
        this.myAttributesNames.setValue(attributeNames);
    }

    public Enumeration getAttributeNames() {
        return (Enumeration)this.myAttributesNames.getValue();
    }

    public String getAuthType() {
        this.notImplemented();
        return null;
    }

    public String getCharacterEncoding() {
        this.notImplemented();
        return null;
    }

    public int getContentLength() {
        this.notImplemented();
        return 0;
    }

    @Override
    public long getContentLengthLong() {
        return 0;
    }

    public String getContentType() {
        return (String)this.myContentTypeToReturn.getValue();
    }

    public void setupGetContentType(String aContentType) {
        this.myContentTypeToReturn.setValue(aContentType);
    }

    public void setExpectedContentType(String aContentType) {
        this.myContentType.setExpected(aContentType);
    }

    public void setContentType(String contentType) {
        this.myContentType.setActual(contentType);
    }

    public String getContextPath() {
        return (String)this.myContextPath.getValue();
    }

    public void setupGetContextPath(String contextPath) {
        this.myContextPath.setValue(contextPath);
    }

    public Cookie[] getCookies() {
        this.notImplemented();
        return null;
    }

    public long getDateHeader(String arg1) {
        this.notImplemented();
        return 0L;
    }

    public String getHeader(String key) {
        return (String)this.myHeaders.getNextReturnObject(key);
    }

    public Enumeration getHeaderNames() {
        this.notImplemented();
        return null;
    }

    public Enumeration getHeaders(String arg1) {
        this.notImplemented();
        return null;
    }

    public void setupGetInputStream(ServletInputStream inputStream) {
        this.inputStream.setValue(inputStream);
    }

    public ServletInputStream getInputStream() throws IOException {
        return (ServletInputStream)this.inputStream.getValue();
    }

    public int getIntHeader(String arg1) {
        this.notImplemented();
        return 0;
    }

    public Locale getLocale() {
        this.notImplemented();
        return null;
    }

    public Enumeration getLocales() {
        this.notImplemented();
        return null;
    }

    public void setupGetMethod(String aMethod) {
        this.method.setValue(aMethod);
    }

    public String getMethod() {
        return (String)this.method.getValue();
    }

    public String getParameter(String paramName) {
        String[] values = this.getParameterValues(paramName);
        return values == null ? null : values[0];
    }

    public void setupGetParameterNames(Enumeration names) {
        this.myParameterNames.setValue(names);
    }

    public Enumeration getParameterNames() {
        return (Enumeration)this.myParameterNames.getValue();
    }

    public String[] getParameterValues(String key) {
        return (String[])this.myParameters.getNextReturnObject(key);
    }

    public String getPathInfo() {
        return (String)this.myPathInfo.getValue();
    }

    public String getPathTranslated() {
        this.notImplemented();
        return null;
    }

    public String getProtocol() {
        return (String)this.protocol.getValue();
    }

    public void setupGetProtocol(String protocol) {
        this.protocol.setValue(protocol);
    }

    public String getQueryString() {
        return (String)this.queryString.getValue();
    }

    public BufferedReader getReader() {
        return (BufferedReader)this.reader.getValue();
    }

    public void setupGetReader(BufferedReader reader) throws IOException {
        this.reader.setValue(reader);
    }

    public String getRealPath(String arg1) {
        this.notImplemented();
        return null;
    }

    public void setupGetRemoteAddr(String remoteAddress) {
        this.myRemoteAddress.setValue(remoteAddress);
    }

    public String getRemoteAddr() {
        return (String)this.myRemoteAddress.getValue();
    }

    public String getRemoteHost() {
        this.notImplemented();
        return null;
    }

    public String getRemoteUser() {
        this.notImplemented();
        return null;
    }

    public void setupGetRequestDispatcher(RequestDispatcher requestDispatcher) {
        this.requestDispatcher.setValue(requestDispatcher);
    }

    public RequestDispatcher getRequestDispatcher(String uri) {
        this.requestDispatcherURI.setActual(uri);
        return (RequestDispatcher)this.requestDispatcher.getValue();
    }

    @Override
    public int getRemotePort() {
        return 0;
    }

    @Override
    public String getLocalName() {
        return null;
    }

    @Override
    public String getLocalAddr() {
        return null;
    }

    @Override
    public int getLocalPort() {
        return 0;
    }

    @Override
    public ServletContext getServletContext() {
        return null;
    }

    @Override
    public AsyncContext startAsync() throws IllegalStateException {
        return null;
    }

    @Override
    public AsyncContext startAsync(ServletRequest servletRequest, ServletResponse servletResponse) throws IllegalStateException {
        return null;
    }

    @Override
    public boolean isAsyncStarted() {
        return false;
    }

    @Override
    public boolean isAsyncSupported() {
        return false;
    }

    @Override
    public AsyncContext getAsyncContext() {
        return null;
    }

    @Override
    public DispatcherType getDispatcherType() {
        return null;
    }

    @Override
    public String getRequestId() {
        return null;
    }

    @Override
    public String getProtocolRequestId() {
        return null;
    }

    @Override
    public ServletConnection getServletConnection() {
        return null;
    }

    public void setExpectedRequestDispatcherURI(String uri) {
        this.requestDispatcherURI.setExpected(uri);
    }

    public String getRequestedSessionId() {
        this.notImplemented();
        return null;
    }

    public void setupGetRequestURI(String aRequestURI) {
        this.myRequestURI.setValue(aRequestURI);
    }

    public String getRequestURI() {
        return (String)this.myRequestURI.getValue();
    }

    public String getScheme() {
        return (String)this.scheme.getValue();
    }

    public String getServerName() {
        return (String)this.serverName.getValue();
    }

    public int getServerPort() {
        this.notImplemented();
        return 0;
    }

    public void setupGetServletPath(String path) {
        this.servletPath.setValue(path);
    }

    public String getServletPath() {
        return (String)this.servletPath.getValue();
    }

    public HttpSession getSession() {
        return (HttpSession)this.myHttpSession.getValue();
    }

    @Override
    public String changeSessionId() {
        return null;
    }

    public void setSession(HttpSession httpSession) {
        this.myHttpSession.setValue(httpSession);
    }

    public void setExpectedCreateSession(boolean createSession) {
        this.createSession.setExpected(createSession);
    }

    public HttpSession getSession(boolean createSession) {
        this.createSession.setActual(createSession);
        return this.getSession();
    }

    public void setupGetUserPrincipal(Principal userPrincipal) {
        this.myUserPrincipal.setValue(userPrincipal);
    }

    public Principal getUserPrincipal() {
        return (Principal)this.myUserPrincipal.getValue();
    }

    public boolean isRequestedSessionIdFromCookie() {
        this.notImplemented();
        return false;
    }

    public boolean isRequestedSessionIdFromUrl() {
        this.notImplemented();
        return false;
    }

    public boolean isRequestedSessionIdFromURL() {
        this.notImplemented();
        return false;
    }

    @Override
    public boolean authenticate(HttpServletResponse httpServletResponse) throws IOException, ServletException {
        return false;
    }

    @Override
    public void login(String s, String s1) throws ServletException {

    }

    @Override
    public void logout() throws ServletException {

    }

    @Override
    public Collection<Part> getParts() throws IOException, ServletException {
        return null;
    }

    @Override
    public Part getPart(String s) throws IOException, ServletException {
        return null;
    }

    @Override
    public <T extends HttpUpgradeHandler> T upgrade(Class<T> aClass) throws IOException, ServletException {
        return null;
    }

    public boolean isRequestedSessionIdValid() {
        this.notImplemented();
        return false;
    }

    public boolean isSecure() {
        this.notImplemented();
        return false;
    }

    public boolean isUserInRole(String arg1) {
        this.notImplemented();
        return false;
    }

    public void setupRemoveAttribute(String anAttributeToRemove) {
        this.myRemoveAttributes.addExpected(anAttributeToRemove);
    }

    public void removeAttribute(String anAttributeToRemove) {
        this.myRemoveAttributes.addActual(anAttributeToRemove);
    }

    public void addExpectedSetAttribute(String attributeName, Object attributeValue) {
        this.mySetAttributes.addExpected(new MapEntry(attributeName, attributeValue));
    }

    public void setAttribute(String attributeName, Object attributeValue) {
        this.mySetAttributes.addActual(new MapEntry(attributeName, attributeValue));
    }

    public void setupAddParameter(String paramName, String[] values) {
        this.myParameters.putObjectToReturn(paramName, values);
    }

    public void setupAddParameter(String paramName, String value) {
        this.setupAddParameter(paramName, new String[]{value});
    }

    public void setupAddHeader(String headerName, String value) {
        this.myHeaders.putObjectToReturn(headerName, value);
    }

    public void setupPathInfo(String pathInfo) {
        this.myPathInfo.setValue(pathInfo);
    }

    public void setupQueryString(String aQueryString) {
        this.queryString.setValue(aQueryString);
    }

    public void setupScheme(String aScheme) {
        this.scheme.setValue(aScheme);
    }

    public void setupServerName(String aServerName) {
        this.serverName.setValue(aServerName);
    }

    public StringBuffer getRequestURL() {
        this.notImplemented();
        return null;
    }

    public void setCharacterEncoding(String s) {
        this.notImplemented();
    }

    public void setupGetParameterMap(Map map) {
        this.parameterMap.setValue(map);
    }

    public Map getParameterMap() {
        return (Map)this.parameterMap.getValue();
    }
}
