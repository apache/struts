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

    public int doEndTag() throws JspException {
        component.end(pageContext.getOut(), getBody());
        component = null;
        return EVAL_PAGE;
    }

    public int doStartTag() throws JspException {
        ValueStack stack = getStack();
        component = getBean(stack, (HttpServletRequest) pageContext.getRequest(), (HttpServletResponse) pageContext.getResponse());
        Container container = (Container) stack.getContext().get(ActionContext.CONTAINER);
        container.inject(component);
        
        populateParams();
        boolean evalBody = component.start(pageContext.getOut());

        if (evalBody) {
            return component.usesBody() ? EVAL_BODY_BUFFERED : EVAL_BODY_INCLUDE;
        } else {
            return SKIP_BODY;
        }
    }

    protected void populateParams() {
    }

    public Component getComponent() {
        return component;
    }
}
