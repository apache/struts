/*
 * Copyright (c) 2002-2006 by OpenSymphony
 * All rights reserved.
 */
package com.opensymphony.webwork.portlet.example;

import java.util.Collection;
import java.util.Map;

import javax.portlet.RenderRequest;

import com.opensymphony.webwork.portlet.context.PortletActionContext;
import com.opensymphony.xwork.ActionSupport;

/**
 * @author Nils-Helge Garli
 */
public class FormResultAction extends ActionSupport {

    private String result = null;
    
    public String getResult() {
        return result;
    }
    public void setResult(String result) {
        this.result = result;
    }
    
    public Collection getRenderParams() {
        RenderRequest req = PortletActionContext.getRenderRequest();
        Map params = req.getParameterMap();
        return params.entrySet();
    }
}
