/*
 * $Id$
 *
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

package org.apache.struts2.dojo.views.jsp.ui;

import java.util.Collections;
import java.util.Enumeration;
import java.util.HashMap;
import java.util.Locale;
import java.util.Map;
import java.util.Vector;

import javax.servlet.RequestDispatcher;
import javax.servlet.http.HttpSession;

import junit.framework.AssertionFailedError;

import com.mockobjects.servlet.MockHttpServletRequest;


/**
 * StrutsMockHttpServletRequest
 *
 */
public class StrutsMockHttpServletRequest extends MockHttpServletRequest {

    Locale locale = Locale.US;
    private Map attributes = new HashMap();
    private Map parameterMap = new HashMap();
    private String context = "";
    private String pathInfo = "";
    private String queryString;
    private String requestURI;
    private String scheme;
    private String serverName;
    private int serverPort;
    private String encoding;
    private String requestDispatherString;


    public void setAttribute(String s, Object o) {
        attributes.put(s, o);
    }

    public Object getAttribute(String s) {
        return attributes.get(s);
    }

    public Enumeration getAttributeNames() {
        Vector v = new Vector();
        v.addAll(attributes.keySet());

        return v.elements();
    }

    public String getContextPath() {
        return this.context;
    }

    public void setLocale(Locale locale) {
        this.locale = locale;
    }

    public Locale getLocale() {
        return locale;
    }

    public void setCharacterEncoding(String s) {
        this.encoding = s;
    }

    public String getCharacterEncoding() {
        return encoding;
    }

    public void setParameterMap(Map parameterMap) {
        this.parameterMap = parameterMap;
    }

    public Map getParameterMap() {
        return parameterMap;
    }

    public String getParameter(String string) {
        return (String) parameterMap.get(string);
    }

    public Enumeration getParameterNames() {
        return Collections.enumeration(parameterMap.keySet());
    }

    public String[] getParameterValues(String string) {
        return (String[]) parameterMap.get(string);
    }

    public String getPathInfo() {
        return pathInfo;
    }

    public void setQueryString(String queryString) {
        this.queryString = queryString;
    }

    public String getQueryString() {
        return queryString;
    }

    public RequestDispatcher getRequestDispatcher(String string) {
        this.requestDispatherString = string;
        return super.getRequestDispatcher(string);
    }

    /**
     * Get's the source string that was used in the last getRequestDispatcher method call.
     */
    public String getRequestDispatherString() {
        return requestDispatherString;
    }

    public void setRequestURI(String requestURI) {
        this.requestURI = requestURI;
    }

    public String getRequestURI() {
        return requestURI;
    }

    public void setScheme(String scheme) {
        this.scheme = scheme;
    }

    public String getScheme() {
        return scheme;
    }

    public void setServerName(String serverName) {
        this.serverName = serverName;
    }

    public String getServerName() {
        return serverName;
    }

    public void setServerPort(int serverPort) {
        this.serverPort = serverPort;
    }

    public int getServerPort() {
        return serverPort;
    }

    public HttpSession getSession() {
        HttpSession session = null;

        try {
            session = super.getSession();
        } catch (AssertionFailedError e) {
            //ignore
        }

        if (session == null) {
            session = new StrutsMockHttpSession();
            setSession(session);
        }

        return session;
    }

    public void setupGetContext(String context) {
        this.context = context;
    }

    public void setupGetPathInfo(String pathInfo) {
        this.pathInfo = pathInfo;
    }

    public int getRemotePort() {
        return 0;
    }

    public String getLocalName() {
        return null;
    }

    public String getLocalAddr() {
        return null;
    }

    public int getLocalPort() {
        return 0;
    }
}
