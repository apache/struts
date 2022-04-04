/*
 * $Id: PortletActionContext.java 564279 2007-08-09 17:00:49Z nilsga $
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
package org.apache.struts2.portlet.context;

import com.opensymphony.xwork2.ActionContext;
import org.apache.struts2.StrutsStatics;
import org.apache.struts2.dispatcher.mapper.ActionMapping;
import org.apache.struts2.portlet.PortletConstants;
import org.apache.struts2.portlet.PortletPhase;

import javax.portlet.ActionRequest;
import javax.portlet.ActionResponse;
import javax.portlet.PortletConfig;
import javax.portlet.PortletContext;
import javax.portlet.PortletMode;
import javax.portlet.PortletRequest;
import javax.portlet.PortletResponse;
import javax.portlet.RenderRequest;
import javax.portlet.RenderResponse;
import javax.portlet.ResourceResponse;
import java.util.Map;

import static org.apache.struts2.portlet.PortletConstants.DEFAULT_ACTION_FOR_MODE;
import static org.apache.struts2.portlet.PortletConstants.MODE_NAMESPACE_MAP;
import static org.apache.struts2.portlet.PortletConstants.PHASE;
import static org.apache.struts2.portlet.PortletConstants.PORTLET_CONFIG;
import static org.apache.struts2.portlet.PortletConstants.PORTLET_NAMESPACE;
import static org.apache.struts2.portlet.PortletConstants.REQUEST;
import static org.apache.struts2.portlet.PortletConstants.RESPONSE;


/**
 * PortletActionContext. ActionContext thread local for the portlet environment.
 *
 * @version $Revision: 564279 $ $Date: 2007-08-09 19:00:49 +0200 (Thu, 09 Aug 2007) $
 */
public class PortletActionContext {

    /**
     * Get the PortletConfig of the portlet that is executing.
     *
     * @return The PortletConfig of the executing portlet.
     */
    public static PortletConfig getPortletConfig() {
        return (PortletConfig) getContext().get(PORTLET_CONFIG);
    }

    /**
     * Get the RenderRequest. Can only be invoked in the render phase.
     *
     * @return The current RenderRequest.
     * @throws IllegalStateException If the method is invoked in the wrong phase.
     */
    public static RenderRequest getRenderRequest() {
        if (!getPhase().isRender()) {
            throw new IllegalStateException("RenderRequest cannot be obtained in event phase");
        }
        return (RenderRequest) getContext().get(REQUEST);
    }

    /**
     * Get the RenderResponse. Can only be invoked in the render phase.
     *
     * @return The current RenderResponse.
     * @throws IllegalStateException If the method is invoked in the wrong phase.
     */
    public static RenderResponse getRenderResponse() {
        if (!getPhase().isRender()) {
            throw new IllegalStateException("RenderResponse cannot be obtained in event phase");
        }
        return (RenderResponse) getContext().get(RESPONSE);
    }

    /**
     * Get the ActionRequest. Can only be invoked in the event phase.
     *
     * @return The current ActionRequest.
     * @throws IllegalStateException If the method is invoked in the wrong phase.
     */
    public static ActionRequest getActionRequest() {
        if (!getPhase().isAction()) {
            throw new IllegalStateException("ActionRequest cannot be obtained in render phase");
        }
        return (ActionRequest) getContext().get(REQUEST);
    }

    /**
     * Get the ActionRequest. Can only be invoked in the event phase.
     *
     * @return The current ActionRequest.
     * @throws IllegalStateException If the method is invoked in the wrong phase.
     */
    public static ActionResponse getActionResponse() {
        if (!getPhase().isAction()) {
            throw new IllegalStateException("ActionResponse cannot be obtained in render phase");
        }
        return (ActionResponse) getContext().get(RESPONSE);
    }
    
    /**
     * Get the ResourceResponse. Can only be invoked in the resource phase.
     *
     * @return The current ResourceResponse.
     * @throws IllegalStateException If the method is invoked in the wrong phase.
     */
    public static ResourceResponse getResourceResponse() {
       if (!getPhase().isResource()) {
           throw new IllegalStateException("ResourceResponse cannot be obtained in event phase");
       }
       return (ResourceResponse) getContext().get(RESPONSE);
    }

    /**
     * Get the action namespace of the portlet. Used to organize actions for multiple portlets in
     * the same portlet application.
     *
     * @return The portlet namespace as defined in <code>portlet.xml</code> and <code>struts.xml</code>
     */
    public static String getPortletNamespace() {
        return (String) getContext().get(PORTLET_NAMESPACE);
    }

    /**
     * Get the current PortletRequest.
     *
     * @return The current PortletRequest.
     */
    public static PortletRequest getRequest() {
        return (PortletRequest) getContext().get(REQUEST);
    }

    /**
     * Get the current PortletResponse
     *
     * @return The current PortletResponse.
     */
    public static PortletResponse getResponse() {
        return (PortletResponse) getContext().get(RESPONSE);
    }

    /**
     * Get the phase that the portlet is executing in.
     *
     * @return {@link PortletPhase#RENDER_PHASE} in render phase, and
     *         {@link PortletPhase#ACTION_PHASE} in the event phase.
     */
    public static PortletPhase getPhase() {
        return (PortletPhase) getContext().get(PHASE);
    }

    /**
     * @return The current ActionContext.
     */
    private static ActionContext getContext() {
        return ActionContext.getContext();
    }

    /**
     * Check to see if the current request is a portlet request.
     *
     * @return <code>true</code> if the current request is a portlet request.
     */
    public static boolean isPortletRequest() {
        return getRequest() != null;
    }

    /**
     * Get the default action mapping for the current mode.
     *
     * @return The default action mapping for the current portlet mode.
     */
    public static ActionMapping getDefaultActionForMode() {
        return (ActionMapping) getContext().get(DEFAULT_ACTION_FOR_MODE);
    }

    /**
     * Get the namespace to mode mappings.
     *
     * @return The map of the namespaces for each mode.
     */
    @SuppressWarnings("unchecked")
    public static Map<PortletMode,String> getModeNamespaceMap() {
        return (Map<PortletMode,String>) getContext().get(MODE_NAMESPACE_MAP);
    }
    
    /**
     * Get the mode to default action mappings.
     * 
     * @return The map of default action mapping for each mode
     */
    @SuppressWarnings("unchecked")
    public static Map<PortletMode,ActionMapping> getModeActionMap() {
        return (Map<PortletMode,ActionMapping>) getContext().get(PortletConstants.DEFAULT_ACTION_MAP);
    }

    /**
     * Get the portlet context.
     *
     * @return The portlet context.
     */
    public static PortletContext getPortletContext() {
        return (PortletContext) getContext().get(StrutsStatics.STRUTS_PORTLET_CONTEXT);
    }

    /**
     * Whether JSR286 features are supported.
     *
     * @return <code>true</code> if {@link javax.portlet.PortletContext#getMajorVersion()} returns a value greater than 1
     */
    public static boolean isJSR268Supported() {
        PortletContext ctx = getPortletContext();
        if (ctx == null) {
            return false; // fallback to old behaviour, check WW-3763
        }
        return ctx.getMajorVersion() > 1;
    }

}
