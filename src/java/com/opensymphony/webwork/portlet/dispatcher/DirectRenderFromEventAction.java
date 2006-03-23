/*
 * Copyright (c) 2002-2006 by OpenSymphony
 * All rights reserved.
 */
package com.opensymphony.webwork.portlet.dispatcher;

import com.opensymphony.xwork.ActionSupport;

/**
 * When a portlet is targetted for an <code>event</code>, the portlet will receive two 
 * portlet requests, one for the <code>event</code> phase, and then followed by a <code>render</code>
 * operation. When in the <code>event</code> phase, the action that is executed can't render
 * any output. This means that if an action in the XWork configuration is executed in the event 
 * phase, and the action is set up with a result that should render something, the result can't 
 * immediately be executed. The portlet needs to "wait" to the render phase to do the 
 * rendering. 
 * 
 * When the {@link com.opensymphony.webwork.portlet.result.PortletResult} detects such a 
 * scenario, instead of executing the actual view, it prepares a couple of render parameters
 * specifying this action and the location of the view, which then will be executed in the 
 * following render request.
 * 
 * @author Nils-Helge Garli
 */
public class DirectRenderFromEventAction extends ActionSupport {
    private String location = null;

    /**
     * Get the location of the view.
     * 
     * @return Returns the location.
     */
    public String getLocation() {
        return location;
    }

    /**
     * Set the location of the view.
     * 
     * @param location The location to set.
     */
    public void setLocation(String location) {
        this.location = location;
    }
}
