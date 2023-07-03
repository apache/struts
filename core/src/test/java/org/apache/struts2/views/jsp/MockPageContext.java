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

import jakarta.servlet.*;
import jakarta.servlet.http.HttpSession;
import jakarta.servlet.jsp.JspWriter;
import jakarta.servlet.jsp.PageContext;
import jakarta.servlet.jsp.el.ExpressionEvaluator;
import jakarta.servlet.jsp.el.VariableResolver;

import java.io.IOException;
import java.util.Enumeration;

public class MockPageContext extends PageContext {
    private JspWriter jspWriter;
    private ServletRequest request;
    private HttpSession httpSession;
    private ServletContext servletContext;

    public MockPageContext() {
    }

    public void release() {
    }

    public JspWriter getOut() {
        return this.jspWriter;
    }

    /**
     * @deprecated
     */
    @Override
    public ExpressionEvaluator getExpressionEvaluator() {
        return null;
    }

    /**
     * @deprecated
     */
    @Override
    public VariableResolver getVariableResolver() {
        return null;
    }

    /**
     * @return
     */
    @Override
    public jakarta.el.ELContext getELContext() {
        return null;
    }

    public void setJspWriter(JspWriter jspWriter) {
        this.jspWriter = jspWriter;
    }

    public void handlePageException(Exception e) {
    }

    public ServletContext getServletContext() {
        return this.servletContext;
    }

    public void setServletContext(ServletContext servletContext) {
        this.servletContext = servletContext;
    }

    public int getAttributesScope(String s) {
        return -1;
    }

    public void include(String s) {
    }

    public void include(String s, boolean b) throws ServletException, IOException {

    }

    public void removeAttribute(String s, int i) {
    }

    public Enumeration getAttributeNamesInScope(int i) {
        return null;
    }

    public void forward(String s) {
    }

    public Object getPage() {
        return null;
    }

    public void handlePageException(Throwable t) {
    }

    public void setRequest(ServletRequest servletRequest) {
        this.request = servletRequest;
    }

    public ServletRequest getRequest() {
        return this.request;
    }

    public ServletResponse getResponse() {
        return null;
    }

    public void removeAttribute(String s) {
    }

    public Object getAttribute(String s, int i) {
        return null;
    }

    public ServletConfig getServletConfig() {
        return null;
    }

    public void initialize(Servlet servlet, ServletRequest servletRequest, ServletResponse servletResponse, String s, boolean b, int i, boolean b2) {
    }

    public Object findAttribute(String s) {
        return null;
    }

    public HttpSession getSession() {
        return this.httpSession;
    }

    public void setSession(HttpSession httpSession) {
        this.httpSession = httpSession;
    }

    public void setAttribute(String s, Object o) {
    }

    public void setAttribute(String s, Object o, int i) {
    }

    public Object getAttribute(String s) {
        return null;
    }

    public Exception getException() {
        return null;
    }

    public void verify() {
    }
}
