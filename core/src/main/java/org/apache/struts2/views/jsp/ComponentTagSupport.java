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

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.jsp.JspException;

import com.opensymphony.xwork2.ActionContext;
import org.apache.struts2.components.Component;

import com.opensymphony.xwork2.inject.Container;
import com.opensymphony.xwork2.util.ValueStack;

/**
 */
public abstract class ComponentTagSupport extends StrutsBodyTagSupport {
    protected Component component;

    public abstract Component getBean(ValueStack stack, HttpServletRequest req, HttpServletResponse res);

    @Override
    public int doEndTag() throws JspException {
        component.end(pageContext.getOut(), getBody());
        component = null;  // Always clear component reference (since clearTagStateForTagPoolingServers() is conditional).
        clearTagStateForTagPoolingServers();
        return EVAL_PAGE;
    }

    @Override
    public int doStartTag() throws JspException {
        ValueStack stack = getStack();
        component = getBean(stack, (HttpServletRequest) pageContext.getRequest(), (HttpServletResponse) pageContext.getResponse());
        Container container = stack.getActionContext().getContainer();
        container.inject(component);
        
        populateParams();
        boolean evalBody = component.start(pageContext.getOut());

        if (evalBody) {
            return component.usesBody() ? EVAL_BODY_BUFFERED : EVAL_BODY_INCLUDE;
        } else {
            return SKIP_BODY;
        }
    }

    /**
     * Define method to populate component state based on the Tag parameters.
     * 
     * Descendants should override this method for custom behaviour, but should <em>always</em> call the ancestor method when doing so.
     */
    protected void populateParams() {
        populatePerformClearTagStateForTagPoolingServersParam();
    }

    /**
     * Specialized method to populate the performClearTagStateForTagPoolingServers state of the Component to match the value set in the Tag.
     * 
     * Generally only unit tests would call this method directly, to avoid calling the whole populateParams() chain again after doStartTag()
     * has been called.  Doing that can break tag / component state behaviour, but unit tests still need a way to set the
     * performClearTagStateForTagPoolingServers state for the component (which only comes into being after doStartTag() is called).
     */
    protected void populatePerformClearTagStateForTagPoolingServersParam() {
        if (component != null) {
            component.setPerformClearTagStateForTagPoolingServers(super.getPerformClearTagStateForTagPoolingServers());
        }
    }

    public Component getComponent() {
        return component;
    }

    @Override
    protected void clearTagStateForTagPoolingServers() {
       if (getPerformClearTagStateForTagPoolingServers() == false) {
            return;  // If flag is false (default setting), do not perform any state clearing.
        }
        super.clearTagStateForTagPoolingServers();
        component = null;  // Duplicate clear, kept for consistency.
    }

}
