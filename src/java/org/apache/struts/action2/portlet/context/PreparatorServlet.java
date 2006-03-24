/*
 * Copyright (c) 2002-2006 by OpenSymphony
 * All rights reserved.
 */
package com.opensymphony.webwork.portlet.context;

import java.io.IOException;

import javax.servlet.ServletContext;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import com.opensymphony.webwork.WebWorkStatics;
import com.opensymphony.xwork.ActionContext;

/**
 * Since a portlet is not dispatched the same way as a servlet, the 
 * {@link com.opensymphony.webwork.ServletActionContext} is not immediately available, as it 
 * depends on objects from the servlet API. However, the WW2 view implementations require access
 * to the objects in the {@link com.opensymphony.webwork.ServletActionContext}, and this servlet
 * makes sure that these are available when the portlet actions are executing the render results.
 * 
 * @author Nils-Helge Garli
 */
public class PreparatorServlet extends HttpServlet implements WebWorkStatics {

    private final static Log LOG = LogFactory.getLog(PreparatorServlet.class);

    /**
     * Prepares the {@link com.opensymphony.webwork.ServletActionContext} with the
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
