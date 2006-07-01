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
package org.apache.struts2.portlet.context;

import java.io.IOException;

import javax.servlet.ServletContext;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import org.apache.struts2.StrutsStatics;
import com.opensymphony.xwork.ActionContext;

/**
 * Since a portlet is not dispatched the same way as a servlet, the 
 * {@link org.apache.struts2.ServletActionContext} is not immediately available, as it 
 * depends on objects from the servlet API. However, the WW2 view implementations require access
 * to the objects in the {@link org.apache.struts2.ServletActionContext}, and this servlet
 * makes sure that these are available when the portlet actions are executing the render results.
 * 
 */
public class PreparatorServlet extends HttpServlet implements StrutsStatics {

	private static final long serialVersionUID = 1853399729352984089L;
	
	private final static Log LOG = LogFactory.getLog(PreparatorServlet.class);

    /**
     * Prepares the {@link org.apache.struts2.ServletActionContext} with the
     * {@link ServletContext}, {@link HttpServletRequest} and {@link HttpServletResponse}.
     */
    public void service(HttpServletRequest servletRequest,
            HttpServletResponse servletResponse) throws ServletException,
            IOException {
        LOG.debug("Preparing servlet objects for dispatch");
        ServletContext ctx = getServletContext();
        ActionContext.getContext().put(SERVLET_CONTEXT, ctx);
        ActionContext.getContext().put(HTTP_REQUEST, servletRequest);
        ActionContext.getContext().put(HTTP_RESPONSE, servletResponse);
        LOG.debug("Preparation complete");
    }

}
