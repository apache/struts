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

package org.apache.struts2.views.jsp;

import java.io.File;
import java.io.StringWriter;
import java.util.HashMap;
import java.util.Map;

import javax.servlet.http.HttpServletResponse;
import javax.servlet.jsp.JspWriter;

import org.apache.struts2.ServletActionContext;
import org.apache.struts2.StrutsInternalTestCase;
import org.apache.struts2.TestAction;
import org.apache.struts2.dispatcher.ApplicationMap;
import org.apache.struts2.dispatcher.Dispatcher;
import org.apache.struts2.dispatcher.RequestMap;
import org.apache.struts2.dispatcher.SessionMap;

import com.mockobjects.dynamic.Mock;
import com.opensymphony.xwork2.Action;
import com.opensymphony.xwork2.ActionContext;
import com.opensymphony.xwork2.inject.Container;
import com.opensymphony.xwork2.util.ValueStack;


/**
 * Base class to extend for unit testing UI Tags.
 *
 */
public abstract class AbstractTagTest extends StrutsInternalTestCase {
    protected Action action;
    protected Map context;
    protected Map session;
    protected ValueStack stack;

    /**
     * contains the buffer that our unit test will write to.  we can later verify this buffer for correctness.
     */
    protected StringWriter writer;
    protected StrutsMockHttpServletRequest request;
    protected StrutsMockPageContext pageContext;
    protected HttpServletResponse response;
    
    protected Mock mockContainer;

    /**
     * Constructs the action that we're going to test against.  For most UI tests, this default action should be enough.
     * However, simply override getAction to return a custom Action if you need something more sophisticated.
     *
     * @return the Action to be added to the ValueStack as part of the unit test
     */
    public Action getAction() {
        return new TestAction();
    }

    protected void setUp() throws Exception {
        super.setUp();
        /**
         * create our standard mock objects
         */
        createMocks();
    }

    protected void createMocks() throws Exception {
        action = this.getAction();
        stack = ActionContext.getContext().getValueStack();
        context = stack.getContext();
        stack.push(action);

        request = new StrutsMockHttpServletRequest();
        request.setAttribute(ServletActionContext.STRUTS_VALUESTACK_KEY, stack);
        response = new StrutsMockHttpServletResponse();
        request.setSession(new StrutsMockHttpSession());
        request.setupGetServletPath("/");

        writer = new StringWriter();

        JspWriter jspWriter = new StrutsMockJspWriter(writer);

        servletContext.setRealPath(new File("nosuchfile.properties").getAbsolutePath());
        servletContext.setServletInfo("Resin");

        pageContext = new StrutsMockPageContext();
        pageContext.setRequest(request);
        pageContext.setResponse(response);
        pageContext.setJspWriter(jspWriter);
        pageContext.setServletContext(servletContext);

        mockContainer = new Mock(Container.class);
        Dispatcher du = new Dispatcher(pageContext.getServletContext(), new HashMap());
        du.init();
        Dispatcher.setInstance(du);
        du.setConfigurationManager(configurationManager);
        session = new SessionMap(request);
        Map<String, Object> extraContext = du.createContextMap(new RequestMap(request),
                request.getParameterMap(),
                session,
                new ApplicationMap(pageContext.getServletContext()),
                request,
                response);
        // let's not set the locale -- there is a test that checks if Dispatcher actually picks this up...
        // ... but generally we want to just use no locale (let it stay system default)
        extraContext.remove(ActionContext.LOCALE);
        stack.getContext().putAll(extraContext);

        context.put(ServletActionContext.HTTP_REQUEST, request);
        context.put(ServletActionContext.HTTP_RESPONSE, response);
        context.put(ServletActionContext.SERVLET_CONTEXT, servletContext);

        ActionContext.setContext(new ActionContext(context));
    }

    protected void tearDown() throws Exception {
        super.tearDown();
        pageContext.verify();
        request.verify();
        action = null;
        context = null;
        session = null;
        stack = null;
        writer = null;
        request = null;
        pageContext = null;
        response = null;
        servletContext = null;
        mockContainer = null;
    }
}
