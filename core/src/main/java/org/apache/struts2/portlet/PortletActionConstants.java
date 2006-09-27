/*
 * $Id$
 *
 * Copyright 2006 The Apache Software Foundation.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.apache.struts2.portlet;

/**
 * Interface defining some constants used in the Struts portlet implementation
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
	String ACTION_PARAM = "struts.portlet.action"; 
	
	/**
	 * Key for parameter holding the last executed portlet mode.
	 */
	String MODE_PARAM = "struts.portlet.mode";
	
	/**
     * Key used for looking up and storing the portlet phase
     */
    String PHASE = "struts.portlet.phase";

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
    String REQUEST = "struts.portlet.request";

    /**
     * Key used for looking up and storing the
     * {@link javax.portlet.PortletResponse}
     */
    String RESPONSE = "struts.portlet.response";
    
    /**
     * Key used for looking up and storing the action that was invoked in the event phase.
     */
    String EVENT_ACTION = "struts.portlet.eventAction";

    /**
     * Key used for looking up and storing the
     * {@link javax.portlet.PortletConfig}
     */
    String PORTLET_CONFIG = "struts.portlet.config";

    /**
     * Name of the action used as error handler
     */
    String ERROR_ACTION = "errorHandler";

    /**
     * Key for the portlet namespace stored in the 
     * {@link org.apache.struts2.portlet.context.PortletActionContext}.
     */
    String PORTLET_NAMESPACE = "struts.portlet.portletNamespace";
    
    /**
     * Key for the mode-to-namespace map stored in the 
     * {@link org.apache.struts2.portlet.context.PortletActionContext}.
     */
    String MODE_NAMESPACE_MAP = "struts.portlet.modeNamespaceMap";
    
    /**
     * Key for the default action name for the portlet, stored in the 
     * {@link org.apache.struts2.portlet.context.PortletActionContext}.
     */
    String DEFAULT_ACTION_FOR_MODE = "struts.portlet.defaultActionForMode";
}
