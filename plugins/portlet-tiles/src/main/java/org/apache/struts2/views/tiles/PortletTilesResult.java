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
package org.apache.struts2.views.tiles;

import com.opensymphony.xwork2.ActionInvocation;
import org.apache.struts2.portlet.PortletConstants;
import org.apache.struts2.portlet.context.PortletActionContext;
import org.apache.struts2.result.ServletDispatcherResult;
import org.apache.tiles.TilesContainer;
import org.apache.tiles.TilesException;
import org.apache.tiles.access.TilesAccess;
import org.apache.tiles.request.ApplicationContext;
import org.apache.tiles.request.Request;
import org.apache.tiles.request.portlet.RenderPortletRequest;

import javax.portlet.ActionResponse;
import javax.portlet.PortletContext;
import javax.portlet.RenderRequest;
import javax.portlet.RenderResponse;
import java.util.Map;

/**
 * Dedicated Tile result to be used in Portlet environment
 *
 * WW-2749
 */
public class PortletTilesResult extends ServletDispatcherResult {

    public static final String TILES_ACTION_NAME = "tilesDirect";

    public PortletTilesResult() {
        super();
    }

    public PortletTilesResult(String location) {
        super(location);
    }

    public void doExecute(String location, ActionInvocation invocation) throws Exception {
        if (PortletActionContext.getPhase().isAction() || PortletActionContext.getPhase().isEvent()) {
            executeActionResult(location, invocation);
        } else {
            executeRenderResult(location);
        }
    }

    protected void executeRenderResult(String location) throws TilesException {
        setLocation(location);

        PortletContext portletContext = PortletActionContext.getPortletContext();
        RenderRequest request = PortletActionContext.getRenderRequest();
        RenderResponse response = PortletActionContext.getRenderResponse();

        TilesContainer container = getCurrentContainer(request, portletContext);
        ApplicationContext applicationContext = container.getApplicationContext();
        Request currentRequest = new RenderPortletRequest(applicationContext, portletContext, request, response);

        container.render(location, currentRequest);
    }

    protected void executeActionResult(String location, ActionInvocation invocation) {
        ActionResponse res = PortletActionContext.getActionResponse();

        res.setRenderParameter(PortletConstants.ACTION_PARAM, TILES_ACTION_NAME);

        Map<String, Object> sessionMap = invocation.getInvocationContext().getSession();
        sessionMap.put(PortletConstants.RENDER_DIRECT_LOCATION, location);

        res.setRenderParameter(PortletConstants.MODE_PARAM, PortletActionContext.getRequest().getPortletMode().toString());
    }

    protected TilesContainer getCurrentContainer(javax.portlet.PortletRequest request, PortletContext context) {

        TilesContainer container = (TilesContainer) request.getAttribute(TilesAccess.CURRENT_CONTAINER_ATTRIBUTE_NAME);

        if (container == null) {
            container = getContainer(context);
            request.setAttribute(TilesAccess.CURRENT_CONTAINER_ATTRIBUTE_NAME, container);
        }

        return container;
    }

    protected TilesContainer getContainer(PortletContext context) {
        return getContainer(context, TilesAccess.CONTAINER_ATTRIBUTE);
    }

    protected TilesContainer getContainer(PortletContext context, String key) {
        if (key == null) {
            key = TilesAccess.CONTAINER_ATTRIBUTE;
        }
        return (TilesContainer) context.getAttribute(key);
    }

}
