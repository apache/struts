/*
 * Copyright (c) 2002-2003 by OpenSymphony
 * All rights reserved.
 */
package com.opensymphony.webwork.views.jsp;

import com.opensymphony.webwork.components.ActionComponent;
import com.opensymphony.webwork.components.Component;
import com.opensymphony.xwork.util.OgnlValueStack;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

/**
 * @see ActionComponent
 */
public class ActionTag extends ComponentTagSupport {
    protected String name;
    protected String namespace;
    protected boolean executeResult;
    protected boolean ignoreContextParams;

    public Component getBean(OgnlValueStack stack, HttpServletRequest req, HttpServletResponse res) {
        return new ActionComponent(stack, req, res);
    }

    protected void populateParams() {
        super.populateParams();

        ActionComponent action = (ActionComponent) component;
        action.setName(name);
        action.setNamespace(namespace);
        action.setExecuteResult(executeResult);
        action.setIgnoreContextParams(ignoreContextParams);
        action.start(pageContext.getOut());
    }

    protected void addParameter(String name, Object value) {
        ActionComponent ac = (ActionComponent) component;
        ac.addParameter(name, value);
    }

    public void setName(String name) {
        this.name = name;
    }

    public void setNamespace(String namespace) {
        this.namespace = namespace;
    }

    public void setExecuteResult(boolean executeResult) {
        this.executeResult = executeResult;
    }

    public void setIgnoreContextParams(boolean ignoreContextParams) {
        this.ignoreContextParams = ignoreContextParams;
    }
}
