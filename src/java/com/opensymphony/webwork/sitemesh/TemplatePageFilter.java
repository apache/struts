/*
 * Copyright (c) 2002-2006 by OpenSymphony
 * All rights reserved.
 */

package com.opensymphony.webwork.sitemesh;

import com.opensymphony.module.sitemesh.Decorator;
import com.opensymphony.module.sitemesh.Page;
import com.opensymphony.module.sitemesh.filter.PageFilter;
import com.opensymphony.webwork.ServletActionContext;
import com.opensymphony.webwork.dispatcher.DispatcherUtils;
import com.opensymphony.webwork.WebWorkConstants;
import com.opensymphony.webwork.config.Configuration;

import com.opensymphony.xwork.*;
import com.opensymphony.xwork.interceptor.PreResultListener;
import com.opensymphony.xwork.util.OgnlValueStack;

import javax.servlet.FilterConfig;
import javax.servlet.ServletContext;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

/**
 *  An abstract template page filter that sets up the proper contexts for
 *  template processing.
 * 
 * @author patrick
 */
public abstract class TemplatePageFilter extends PageFilter {
    
    private FilterConfig filterConfig;

    public void init(FilterConfig filterConfig) {
        super.init(filterConfig);
        this.filterConfig = filterConfig;
    }

    /**
     *  Applies the decorator, using the relevent contexts
     * 
     * @param page The page
     * @param decorator The decorator
     * @param req The servlet request
     * @param res The servlet response
     * @param servletContext The servlet context
     * @param ctx The action context for this request, populated with the server state
     */
    protected abstract void applyDecorator(Page page, Decorator decorator,
                                  HttpServletRequest req, HttpServletResponse res,
                                  ServletContext servletContext, ActionContext ctx)
            throws ServletException, IOException;
    
    /**
     *  Applies the decorator, creating the relevent contexts and delegating to
     *  the extended applyDecorator().
     * 
     * @param page The page
     * @param decorator The decorator
     * @param req The servlet request
     * @param res The servlet response
     */
    protected void applyDecorator(Page page, Decorator decorator,
                                  HttpServletRequest req, HttpServletResponse res)
            throws ServletException, IOException {
            
        ServletContext servletContext = filterConfig.getServletContext();
        ActionContext ctx = ServletActionContext.getActionContext(req);
        if (ctx == null) {
            // ok, one isn't associated with the request, so let's get a ThreadLocal one (which will create one if needed)
            OgnlValueStack vs = new OgnlValueStack();
            vs.getContext().putAll(DispatcherUtils.getInstance().createContextMap(req, res, null, servletContext));
            ctx = new ActionContext(vs.getContext());
            if (ctx.getActionInvocation() == null) {
                // put in a dummy ActionSupport so basic functionality still works
                ActionSupport action = new ActionSupport();
                vs.push(action);
                ctx.setActionInvocation(new DummyActionInvocation(action));
            }
        }

        // delegate to the actual page decorator
        applyDecorator(page, decorator, req, res, servletContext, ctx);
    }

    
    /**
     *  Gets the L18N encoding of the system.  The default is UTF-8.
     */
    protected String getEncoding() {
        String encoding = (String) Configuration.get(WebWorkConstants.WEBWORK_I18N_ENCODING);
        if (encoding == null) {
            encoding = System.getProperty("file.encoding");
        }
        if (encoding == null) {
            encoding = "UTF-8";
        }
        return encoding;
    }

    static class DummyActionInvocation implements ActionInvocation {
        ActionSupport action;

        public DummyActionInvocation(ActionSupport action) {
            this.action = action;
        }

        public Object getAction() {
            return action;
        }

        public boolean isExecuted() {
            return false;
        }

        public ActionContext getInvocationContext() {
            return null;
        }

        public ActionProxy getProxy() {
            return null;
        }

        public Result getResult() throws Exception {
            return null;
        }

        public String getResultCode() {
            return null;
        }

        public void setResultCode(String resultCode) {
        }

        public OgnlValueStack getStack() {
            return null;
        }

        public void addPreResultListener(PreResultListener listener) {
        }

        public String invoke() throws Exception {
            return null;
        }

        public String invokeActionOnly() throws Exception {
            return null;
        }
    }
}
