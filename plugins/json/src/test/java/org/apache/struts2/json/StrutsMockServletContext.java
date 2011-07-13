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

package org.apache.struts2.json;

import javax.servlet.RequestDispatcher;
import javax.servlet.Servlet;
import javax.servlet.ServletContext;
import javax.servlet.ServletException;
import java.io.InputStream;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.Collections;
import java.util.Enumeration;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;


/**
 * StrutsMockServletContext
 *
 */
public class StrutsMockServletContext implements ServletContext {

    String realPath;
    String servletInfo;
    String contextPath;
    Map initParams = new HashMap();
    Map attributes = new HashMap();
    InputStream resourceAsStream;

    public void setInitParameter(String name, String value) {
        initParams.put(name, value);
    }

    public void setRealPath(String value) {
        realPath = value;
    }

    public String getRealPath(String string) {
        return realPath;
    }

    public ServletContext getContext(String s) {
        return null;
    }

    public int getMajorVersion() {
        return 0;
    }

    public int getMinorVersion() {
        return 0;
    }

    public String getMimeType(String s) {
        return null;
    }

    public Set getResourcePaths(String s) {
        return null;
    }

    public URL getResource(String s) throws MalformedURLException {
        return null;
    }

    public InputStream getResourceAsStream(String s) {
        if (resourceAsStream != null) {
            return resourceAsStream;
        }
        return null;
    }

    public void setResourceAsStream(InputStream is) {
        this.resourceAsStream = is;
    }

    public RequestDispatcher getRequestDispatcher(String s) {
        return null;
    }

    public RequestDispatcher getNamedDispatcher(String s) {
        return null;
    }

    public Servlet getServlet(String s) throws ServletException {
        return null;
    }

    public Enumeration getServlets() {
        return null;
    }

    public Enumeration getServletNames() {
        return null;
    }

    public void log(String s) {
    }

    public void log(Exception e, String s) {
    }

    public void log(String s, Throwable throwable) {
    }

    public String getServerInfo() {
        return servletInfo;
    }

    public String getInitParameter(String s) {
        return (String) initParams.get(s);
    }

    public Enumeration getInitParameterNames() {
        return Collections.enumeration(initParams.keySet());
    }

    public Object getAttribute(String s) {
        return attributes.get(s);
    }

    public Enumeration getAttributeNames() {
        return Collections.enumeration(attributes.keySet());
    }

    public void setAttribute(String s, Object o) {
        attributes.put(s, o);
    }

    public void removeAttribute(String s) {
        attributes.remove(s);
    }

    public String getServletContextName() {
        return null;
    }

    public void setServletInfo(String servletInfo) {
        this.servletInfo = servletInfo;
    }

    public String getContextPath() {
        return contextPath;
    }

    public void setContextPath(String contextPath) {
        this.contextPath = contextPath;
    }
}