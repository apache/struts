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

import com.mockobjects.dynamic.Mock;
import com.opensymphony.xwork2.Action;
import com.opensymphony.xwork2.ActionContext;
import com.opensymphony.xwork2.ActionInvocation;
import com.opensymphony.xwork2.ActionProxy;
import com.opensymphony.xwork2.inject.Container;
import com.opensymphony.xwork2.mock.MockActionInvocation;
import com.opensymphony.xwork2.mock.MockActionProxy;
import com.opensymphony.xwork2.util.ValueStack;
import org.apache.commons.lang3.builder.EqualsBuilder;
import org.apache.struts2.ServletActionContext;
import org.apache.struts2.StrutsInternalTestCase;
import org.apache.struts2.TestAction;
import org.apache.struts2.dispatcher.ApplicationMap;
import org.apache.struts2.dispatcher.Dispatcher;
import org.apache.struts2.dispatcher.HttpParameters;
import org.apache.struts2.dispatcher.MockDispatcher;
import org.apache.struts2.dispatcher.RequestMap;
import org.apache.struts2.dispatcher.SessionMap;

import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.jsp.JspWriter;
import java.io.File;
import java.io.StringWriter;
import java.util.HashMap;
import java.util.Map;

/**
 * Base class to extend for unit testing UI Tags.
 */
public abstract class AbstractTagTest extends StrutsInternalTestCase {
    protected Action action;
    protected ActionProxy actionProxy;
    protected Map<String, Object> context;
    protected Map<String, Object> session;
    protected ValueStack stack;

    /**
     * contains the buffer that our unit test will write to.  we can later verify this buffer for correctness.
     */
    protected StringWriter writer;
    protected StrutsMockHttpServletRequest request;
    protected StrutsMockPageContext pageContext;
    protected HttpServletResponse response;
    protected ActionInvocation actionInvocation;

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

    @Override
    protected void setUp() throws Exception {
        super.setUp();
        createMocks();
    }

    protected void createMocks() {
        action = this.getAction();
        actionProxy = new MockActionProxy();
        container.inject(action);

        stack = ActionContext.getContext().getValueStack();
        context = stack.getContext();
        stack.push(action);

        request = new StrutsMockHttpServletRequest();
        request.setAttribute(ServletActionContext.STRUTS_VALUESTACK_KEY, stack);
        response = new StrutsMockHttpServletResponse();
        request.setSession(new StrutsMockHttpSession());
        request.setServletPath("/");

        writer = new StringWriter();

        JspWriter jspWriter = new StrutsMockJspWriter(writer);

        servletContext.setRealPath(new File("nosuchfile.properties").getAbsolutePath());
        servletContext.setServletInfo("Resin");

        pageContext = new StrutsMockPageContext(servletContext, request, response);
        pageContext.setJspWriter(jspWriter);
        
        mockContainer = new Mock(Container.class);
        MockDispatcher du = new MockDispatcher(pageContext.getServletContext(), new HashMap<>(), configurationManager);
        du.init();
        Dispatcher.setInstance(du);
        session = new SessionMap(request);
        Map<String, Object> extraContext = du.createContextMap(new RequestMap(request),
            HttpParameters.create(request.getParameterMap()).build(),
            session,
            new ApplicationMap(pageContext.getServletContext()),
            request,
            response);
        // let's not set the locale -- there is a test that checks if Dispatcher actually picks this up...
        // ... but generally we want to just use no locale (let it stay system default)
        extraContext = ActionContext.of(extraContext).withLocale(null).getContextMap();
        stack.getContext().putAll(extraContext);

        actionInvocation = new MockActionInvocation();
        ((MockActionInvocation) actionInvocation).setAction(action);

        ((MockActionProxy) actionProxy).setAction(action);
        ((MockActionProxy) actionProxy).setInvocation(actionInvocation);
        ((MockActionInvocation) actionInvocation).setProxy(actionProxy);

        request.setRequestURI("/");
        withRequestPath(request.getRequestURI());

        ActionContext ac = ActionContext.of(context)
            .withServletRequest(request)
            .withServletResponse(response)
            .withServletContext(servletContext)
            .withActionInvocation(actionInvocation)
            .bind();

        ((MockActionInvocation) actionInvocation).setInvocationContext(ac);
    }

    protected void withRequestPath(String path) {
        request.setRequestURI(path);
        String namespace = path.substring(0, path.lastIndexOf('/'));
        String actionName = path.substring(path.lastIndexOf('/') + 1);
        if (namespace.equals("") && !actionName.contains(".action")) {
            actionName = path;
        }
        ((MockActionProxy) actionProxy).setNamespace(namespace);
        ((MockActionProxy) actionProxy).setActionName(actionName);
    }

    @Override
    protected void tearDown() throws Exception {
        super.tearDown();
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

    /**
     * Compare if two component tags are considered equal according to their fields as accessed
     * via reflection.
     * <p>
     * Utilizes {@link EqualsBuilder#reflectionEquals(java.lang.Object, java.lang.Object, boolean)} to perform
     * the check, and compares transient fields as well.  This may fail when run while a security manager is
     * active, due to a need to user reflection.
     * <p>
     * This method may be useful for checking if the state of a tag is what is expected after a given set of operations,
     * or after clearing state such as for calls involving {@link StrutsBodyTagSupport#clearTagStateForTagPoolingServers()}
     * has taken place following {@link StrutsBodyTagSupport#doEndTag()} processing.  When making comparisons, keep in mind the
     * pageContext and parent Tag state are not cleared by clearTagStateForTagPoolingServers().
     *
     * @param tag1 the first {@link StrutsBodyTagSupport} to compare against the other.
     * @param tag2 the second {@link StrutsBodyTagSupport} to compare against the other.
     * @return true if the Tags are equal based on field comparisons by reflection, false otherwise.
     */
    protected boolean strutsBodyTagsAreReflectionEqual(StrutsBodyTagSupport tag1, StrutsBodyTagSupport tag2) {
        return objectsAreReflectionEqual(tag1, tag2);
    }

    /**
     * Helper method to simplify setting the performClearTagStateForTagPoolingServers state for a
     * {@link ComponentTagSupport} tag's {@link import org.apache.struts2.components.Component} to match expectations
     * for the test.
     * <p>
     * The component reference is not available to the tag until after the doStartTag() method is called.
     * We need to ensure the component's {@link import org.apache.struts2.components.Component#performClearTagStateForTagPoolingServers}
     * state matches what we set for the Tag when a non-default (true) value is used, so this method accesses
     * the component instance, sets the value specified and forces the tag's parameters to be repopulated again.
     *
     * @param tag                                      The ComponentTagSupport tag upon whose component we will set the performClearTagStateForTagPoolingServers state.
     * @param performClearTagStateForTagPoolingServers true to clear tag state, false otherwise
     */
    protected void setComponentTagClearTagState(ComponentTagSupport tag, boolean performClearTagStateForTagPoolingServers) {
        tag.component.setPerformClearTagStateForTagPoolingServers(performClearTagStateForTagPoolingServers);
        //tag.populateParams();  // Not safe to call after doStartTag() ... breaks some tests.
        tag.populatePerformClearTagStateForTagPoolingServersParam();  // Only populate the performClearTagStateForTagPoolingServers parameter for the Tag.
    }

}
