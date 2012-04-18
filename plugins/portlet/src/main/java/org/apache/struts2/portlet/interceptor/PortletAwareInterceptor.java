/*
 * $Id: PortletAwareInterceptor.java 590812 2007-10-31 20:32:54Z apetrelli $
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
package org.apache.struts2.portlet.interceptor;

import com.opensymphony.xwork2.ActionContext;
import com.opensymphony.xwork2.ActionInvocation;
import com.opensymphony.xwork2.interceptor.AbstractInterceptor;
import com.opensymphony.xwork2.util.logging.Logger;
import com.opensymphony.xwork2.util.logging.LoggerFactory;
import org.apache.struts2.StrutsStatics;
import org.apache.struts2.interceptor.PrincipalAware;
import org.apache.struts2.portlet.PortletConstants;

import javax.portlet.PortletContext;
import javax.portlet.PortletRequest;
import javax.portlet.PortletResponse;

public class PortletAwareInterceptor extends AbstractInterceptor implements StrutsStatics {

	private static final long serialVersionUID = 2476509721059587700L;
	
	private static final Logger LOG = LoggerFactory.getLogger(PortletAwareInterceptor.class);

	/**
     * Sets action properties based on the interfaces an action implements. Things like application properties,
     * parameters, session attributes, etc are set based on the implementing interface.
     *
     * @param invocation an encapsulation of the action execution state.
     * @throws Exception if an error occurs when setting action properties.
     */
    public String intercept(ActionInvocation invocation) throws Exception {
        final Object action = invocation.getAction();
        final ActionContext context = invocation.getInvocationContext();

        if (action instanceof PortletRequestAware) {
            PortletRequest request = (PortletRequest) context.get(PortletConstants.REQUEST);
            ((PortletRequestAware) action).setPortletRequest(request);
        }

        if (action instanceof PortletResponseAware) {
            PortletResponse response = (PortletResponse) context.get(PortletConstants.RESPONSE);
            ((PortletResponseAware) action).setPortletResponse(response);
        }
        if (action instanceof PrincipalAware) {
            PortletRequest request = (PortletRequest) context.get(PortletConstants.REQUEST);
            ((PrincipalAware) action).setPrincipalProxy(new PortletPrincipalProxy(request));
        }
        if (action instanceof PortletContextAware) {
            PortletContext portletContext = (PortletContext) context.get(StrutsStatics.STRUTS_PORTLET_CONTEXT);
            ((PortletContextAware) action).setPortletContext(portletContext);
        }
        if (action instanceof PortletPreferencesAware) {
        	PortletRequest request = (PortletRequest) context.get(PortletConstants.REQUEST);
            
            // Check if running in a servlet environment
            if (request == null) {
                LOG.warn("This portlet preferences implementation should only be used during development");
                ((PortletPreferencesAware)action).setPortletPreferences(new ServletPortletPreferences(ActionContext.getContext().getSession()));
            } else {
            	((PortletPreferencesAware)action).setPortletPreferences(request.getPreferences());
            }
        }
        return invocation.invoke();
    }
}
