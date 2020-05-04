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
package org.apache.struts2;

import com.opensymphony.xwork2.ActionContext;
import com.opensymphony.xwork2.util.ValueStack;
import org.apache.struts2.dispatcher.mapper.ActionMapping;

import javax.servlet.ServletContext;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.jsp.PageContext;

/**
 * Web-specific context information for actions. This class subclasses <tt>ActionContext</tt> which
 * provides access to things like the action name, value stack, etc. This class adds access to
 * web objects like servlet parameters, request attributes and things like the HTTP session.
 */
public class ServletActionContext implements StrutsStatics {

    public static final String STRUTS_VALUESTACK_KEY = "struts.valueStack";

    @SuppressWarnings("unused")
    private ServletActionContext() {
    }

    /**
     * Gets the current action context
     *
     * @param req The request
     * @return The current action context
     */
    public static ActionContext getActionContext(HttpServletRequest req) {
        ValueStack vs = getValueStack(req);
        if (vs != null) {
            return ActionContext.of(vs.getContext()).bind();
        } else {
            return null;
        }
    }

    /**
     * Do not use this method, use {@link #getActionContext()}
     * @return action context
     * @deprecated Use {@link #getActionContext()} instead
     */
    @Deprecated
    public static ActionContext getContext() {
        return ActionContext.getContext();
    }

    public static ActionContext getActionContext() {
        return ActionContext.getContext();
    }
    /**
     * Gets the current value stack for this request
     *
     * @param req The request
     * @return The value stack
     */
    public static ValueStack getValueStack(HttpServletRequest req) {
        return (ValueStack) req.getAttribute(STRUTS_VALUESTACK_KEY);
    }

    /**
     * Gets the action mapping for this context
     *
     * @return The action mapping
     */
    public static ActionMapping getActionMapping() {
        return ActionContext.getContext().getActionMapping();
    }

    /**
     * Returns the HTTP page context.
     *
     * @return the HTTP page context.
     */
    public static PageContext getPageContext() {
        return ActionContext.getContext().getPageContext();
    }

    /**
     * Sets the HTTP servlet request object.
     *
     * @param request the HTTP servlet request object.
     */
    public static void setRequest(HttpServletRequest request) {
        ActionContext.getContext().withServletRequest(request);
    }

    /**
     * Gets the HTTP servlet request object.
     *
     * @return the HTTP servlet request object.
     */
    public static HttpServletRequest getRequest() {
        return ActionContext.getContext().getServletRequest();
    }

    /**
     * Sets the HTTP servlet response object.
     *
     * @param response the HTTP servlet response object.
     */
    public static void setResponse(HttpServletResponse response) {
        ActionContext.getContext().withServletResponse(response);
    }

    /**
     * Gets the HTTP servlet response object.
     *
     * @return the HTTP servlet response object.
     */
    public static HttpServletResponse getResponse() {
        return ActionContext.getContext().getServletResponse();
    }

    /**
     * Gets the servlet context.
     *
     * @return the servlet context.
     */
    public static ServletContext getServletContext() {
        return ActionContext.getContext().getServletContext();
    }

    /**
     * Sets the current servlet context object
     *
     * @param servletContext The servlet context to use
     */
    public static void setServletContext(ServletContext servletContext) {
        ActionContext.getContext().withServletContext(servletContext);
    }
}
