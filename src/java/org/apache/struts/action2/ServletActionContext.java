/*
 * $Id$
 *
 * Copyright 2006 The Apache Software Foundation.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.apache.struts.action2;

import com.opensymphony.xwork.ActionContext;
import com.opensymphony.xwork.util.OgnlValueStack;

import javax.servlet.ServletContext;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.jsp.PageContext;
import java.util.Map;


/**
 * Web-specific context information for actions. This class subclasses <tt>ActionContext</tt> which
 * provides access to things like the action name, value stack, etc. This class adds access to
 * web objects like servlet parameters, request attributes and things like the HTTP session.
 *
 * @author <a href="mailto:nightfal@etherlands.net">Erik Beeson</a>
 * @author Bill Lynch (docs)
 */
public class ServletActionContext extends ActionContext implements StrutsStatics {
	
	private static final long serialVersionUID = -666854718275106687L;
	
	public static final String STRUTS_VALUESTACK_KEY = "struts.valueStack";

    private ServletActionContext(Map context) {
        super(context);
    }


    public static ActionContext getActionContext(HttpServletRequest req) {
        OgnlValueStack vs = getValueStack(req);
        if (vs != null) {
            return new ActionContext(vs.getContext());
        } else {
            return null;
        }
    }

    public static OgnlValueStack getValueStack(HttpServletRequest req) {
        return (OgnlValueStack) req.getAttribute(STRUTS_VALUESTACK_KEY);
    }

    /**
     * Returns the HTTP page context.
     *
     * @return the HTTP page context.
     */
    public static PageContext getPageContext() {
        return (PageContext) ActionContext.getContext().get(PAGE_CONTEXT);
    }

    /**
     * Sets the HTTP servlet request object.
     *
     * @param request the HTTP servlet request object.
     */
    public static void setRequest(HttpServletRequest request) {
        ActionContext.getContext().put(HTTP_REQUEST, request);
    }

    /**
     * Gets the HTTP servlet request object.
     *
     * @return the HTTP servlet request object.
     */
    public static HttpServletRequest getRequest() {
        return (HttpServletRequest) ActionContext.getContext().get(HTTP_REQUEST);
    }

    /**
     * Sets the HTTP servlet response object.
     *
     * @param response the HTTP servlet response object.
     */
    public static void setResponse(HttpServletResponse response) {
        ActionContext.getContext().put(HTTP_RESPONSE, response);
    }

    /**
     * Gets the HTTP servlet response object.
     *
     * @return the HTTP servlet response object.
     */
    public static HttpServletResponse getResponse() {
        return (HttpServletResponse) ActionContext.getContext().get(HTTP_RESPONSE);
    }

    /**
     * Gets the servlet context.
     *
     * @return the servlet context.
     */
    public static ServletContext getServletContext() {
        return (ServletContext) ActionContext.getContext().get(SERVLET_CONTEXT);
    }

    public static void setServletContext(ServletContext servletContext) {
        ActionContext.getContext().put(SERVLET_CONTEXT, servletContext);
    }
}
