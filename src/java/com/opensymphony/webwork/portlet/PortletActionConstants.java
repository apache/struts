/*
 * Copyright (c) 2002-2006 by OpenSymphony
 * All rights reserved.
 */
package com.opensymphony.webwork.portlet;

/**
 * Interface defining some constants used in the WebWork2 portlet implementation
 * 
 * @author <a href="mailto:nils-helge.garli@bekk.no">Nils-Helge Garli</a>
 *  
 */
public interface PortletActionConstants {
	/**
	 * Default action name to use when no default action has been configured in the portlet
	 * init parameters.
	 */
	String DEFAULT_ACTION_NAME = "default";
	
	/**
	 * Action name parameter name
	 */
	String ACTION_PARAM = "webwork.portlet.action"; 
	
	/**
	 * Key for parameter holding the last executed portlet mode.
	 */
	String MODE_PARAM = "webwork.portlet.mode";
	
	/**
     * Key used for looking up and storing the portlet phase
     */
    String PHASE = "webwork.portlet.phase";

    /**
     * Constant used for the render phase (
     * {@link javax.portlet.Portlet#render(javax.portlet.RenderRequest, javax.portlet.RenderResponse)})
     */
    Integer RENDER_PHASE = new Integer(1);

    /**
     * Constant used for the event phase (
     * {@link javax.portlet.Portlet#processAction(javax.portlet.ActionRequest, javax.portlet.ActionResponse)})
     */
    Integer EVENT_PHASE = new Integer(2);

    /**
     * Key used for looking up and storing the
     * {@link javax.portlet.PortletRequest}
     */
    String REQUEST = "webwork.portlet.request";

    /**
     * Key used for looking up and storing the
     * {@link javax.portlet.PortletResponse}
     */
    String RESPONSE = "webwork.portlet.response";
    
    /**
     * Key used for looking up and storing the action that was invoked in the event phase.
     */
    String EVENT_ACTION = "webwork.portlet.eventAction";

    /**
     * Key used for looking up and storing the
     * {@link javax.portlet.PortletConfig}
     */
    String PORTLET_CONFIG = "webwork.portlet.config";

    /**
     * Name of the action used as error handler
     */
    String ERROR_ACTION = "errorHandler";

    /**
     * Key for the portlet namespace stored in the 
     * {@link com.opensymphony.webwork.portlet.context.PortletActionContext}.
     */
    String PORTLET_NAMESPACE = "webwork.portlet.portletNamespace";
    
    /**
     * Key for the mode-to-namespace map stored in the 
     * {@link com.opensymphony.webwork.portlet.context.PortletActionContext}.
     */
    String MODE_NAMESPACE_MAP = "webwork.portlet.modeNamespaceMap";
    
    /**
     * Key for the default action name for the portlet, stored in the 
     * {@link com.opensymphony.webwork.portlet.context.PortletActionContext}.
     */
    String DEFAULT_ACTION_FOR_MODE = "webwork.portlet.defaultActionForMode";
}
