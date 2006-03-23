/*
 * Copyright (c) 2002-2006 by OpenSymphony
 * All rights reserved.
 */
package com.opensymphony.webwork.portlet.example;

import java.util.Map;

import com.opensymphony.xwork.ActionContext;
import com.opensymphony.xwork.ActionSupport;

public class ExampleAction extends ActionSupport {
    
    private String name = "PortletWork Example";
    
    public String getName() {
        return name;
    }
    
    public Map getRenderParameters() {
        return ActionContext.getContext().getParameters();
    }
}
