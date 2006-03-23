/*
 * Copyright (c) 2002-2003 by OpenSymphony
 * All rights reserved.
 */
package com.opensymphony.webwork.views.jsp;

import com.opensymphony.webwork.ServletActionContext;
import com.opensymphony.webwork.TestAction;
import com.opensymphony.webwork.WebWorkTestCase;
import com.opensymphony.webwork.config.Configuration;
import com.opensymphony.webwork.dispatcher.ApplicationMap;
import com.opensymphony.webwork.dispatcher.DispatcherUtils;
import com.opensymphony.webwork.dispatcher.RequestMap;
import com.opensymphony.webwork.dispatcher.SessionMap;
import com.opensymphony.xwork.Action;
import com.opensymphony.xwork.ActionContext;
import com.opensymphony.xwork.util.OgnlValueStack;

import javax.servlet.http.HttpServletResponse;
import javax.servlet.jsp.JspWriter;
import java.io.File;
import java.io.StringWriter;
import java.util.Map;


/**
 * Base class to extend for unit testing UI Tags.
 *
 * @author jcarreira
 */
public abstract class AbstractTagTest extends WebWorkTestCase {
    protected Action action;
    protected Map context;
    protected Map session;
    protected OgnlValueStack stack;

    /**
     * contains the buffer that our unit test will write to.  we can later verify this buffer for correctness.
     */
    protected StringWriter writer;
    protected WebWorkMockHttpServletRequest request;
    protected WebWorkMockPageContext pageContext;
    protected HttpServletResponse response;
    protected WebWorkMockServletContext servletContext;

    /**
     * Constructs the action that we're going to test against.  For most UI tests, this default action should be enough.
     * However, simply override getAction to return a custom Action if you need something more sophisticated.
     *
     * @return the Action to be added to the OgnlValueStack as part of the unit test
     */
    public Action getAction() {
        return new TestAction();
    }

    protected void setUp() throws Exception {
        super.setUp();

        /**
         * create our standard mock objects
         */
        action = this.getAction();
        stack = new OgnlValueStack();
        context = stack.getContext();
        stack.push(action);

        request = new WebWorkMockHttpServletRequest();
        request.setAttribute(ServletActionContext.WEBWORK_VALUESTACK_KEY, stack);
        response = new WebWorkMockHttpServletResponse();
        request.setSession(new WebWorkMockHttpSession());
        request.setupGetServletPath("/");

        writer = new StringWriter();

        JspWriter jspWriter = new WebWorkMockJspWriter(writer);

        servletContext = new WebWorkMockServletContext();
        servletContext.setRealPath(new File("nosuchfile.properties").getAbsolutePath());
        servletContext.setServletInfo("Resin");

        pageContext = new WebWorkMockPageContext();
        pageContext.setRequest(request);
        pageContext.setResponse(response);
        pageContext.setJspWriter(jspWriter);
        pageContext.setServletContext(servletContext);

        DispatcherUtils.initialize(pageContext.getServletContext());
        DispatcherUtils du = DispatcherUtils.getInstance();
        session = new SessionMap(request);
        Map extraContext = du.createContextMap(new RequestMap(request),
                request.getParameterMap(),
                session,
                new ApplicationMap(pageContext.getServletContext()),
                request,
                response,
                pageContext.getServletContext());
        // let's not set the locale -- there is a test that checks if DispatcherUtils actually picks this up...
        // ... but generally we want to just use no locale (let it stay system default)
        extraContext.remove(ActionContext.LOCALE);
        stack.getContext().putAll(extraContext);

        context.put(ServletActionContext.HTTP_REQUEST, request);
        context.put(ServletActionContext.HTTP_RESPONSE, response);
        context.put(ServletActionContext.SERVLET_CONTEXT, servletContext);

        ActionContext.setContext(new ActionContext(context));

        Configuration.setConfiguration(null);
    }

    protected void tearDown() throws Exception {
        pageContext.verify();
        request.verify();
    }
}
