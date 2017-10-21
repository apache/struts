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
package org.apache.struts2.portlet.dispatcher;

import com.opensymphony.xwork2.ActionContext;
import org.apache.logging.log4j.Logger;
import org.apache.logging.log4j.LogManager;
import org.apache.struts2.portlet.PortletPhase;
import org.apache.struts2.portlet.servlet.PortletServletResponse;
import org.apache.struts2.portlet.servlet.PortletServletResponseJSR286;

import javax.portlet.EventRequest;
import javax.portlet.EventResponse;
import javax.portlet.PortletException;
import javax.portlet.PortletRequest;
import javax.portlet.PortletResponse;
import javax.portlet.ResourceRequest;
import javax.portlet.ResourceResponse;
import java.io.IOException;

public class Jsr286Dispatcher extends Jsr168Dispatcher {

    private final static Logger LOG = LogManager.getLogger(Jsr286Dispatcher.class);


    @Override
    public void processEvent(EventRequest request, EventResponse response) throws PortletException, IOException {
        if (LOG.isDebugEnabled()) {
            LOG.debug("Entering processEvent");
        }
        resetActionContext();
        try {
            // We'll use the event name as the "action"
            serviceAction(request, response,
                    getRequestMap(request), getParameterMap(request),
                    getSessionMap(request), getApplicationMap(),
                    portletNamespace, PortletPhase.EVENT_PHASE);
            if (LOG.isDebugEnabled()) LOG.debug("Leaving processEvent");
        } finally {
            ActionContext.setContext(null);
        }
    }

    @Override
    public void serveResource(ResourceRequest request, ResourceResponse response)
            throws PortletException, IOException {
        if (LOG.isDebugEnabled()) {
            LOG.debug("Entering serveResource");
        }
        resetActionContext();
        try {
            serviceAction(request, response,
                    getRequestMap(request), getParameterMap(request),
                    getSessionMap(request), getApplicationMap(),
                    portletNamespace, PortletPhase.SERVE_RESOURCE_PHASE);
        } finally {
            ActionContext.setContext(null);
        }
    }

    @Override
    protected String getDefaultActionPath(PortletRequest portletRequest) {
        if (portletRequest instanceof EventRequest) {
            return ((EventRequest) portletRequest).getEvent().getName();
        }
        return super.getDefaultActionPath(portletRequest);
    }

    @Override
    protected PortletServletResponse createPortletServletResponse(PortletResponse response) {
        return new PortletServletResponseJSR286(response);
    }

}
