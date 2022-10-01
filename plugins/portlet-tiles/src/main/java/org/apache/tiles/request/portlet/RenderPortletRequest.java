/*
 * $Id$
 *
 * Licensed to the Apache Software Foundation (ASF) under one
 * or more contributor license agreements.  See the NOTICE file
 * distributed with this work for additional information
 * regarding copyright ownership.  The ASF licenses this file
 * to you under the Apache License, Version 2.0 (the
 * "License"); you may not use this file except in compliance
 * with the License.  You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing,
 * software distributed under the License is distributed on an
 * "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY
 * KIND, either express or implied.  See the License for the
 * specific language governing permissions and limitations
 * under the License.
 */
package org.apache.tiles.request.portlet;

import org.apache.tiles.request.ApplicationContext;
import org.apache.tiles.request.portlet.delegate.MimeResponseDelegate;
import org.apache.tiles.request.portlet.delegate.PortletRequestDelegate;

import javax.portlet.PortletContext;
import javax.portlet.RenderRequest;
import javax.portlet.RenderResponse;

/**
 * Portlet request for a {@link RenderRequest}.
 */
public class RenderPortletRequest extends PortletRequest {

    /**
     * Constructor.
     *
     * @param applicationContext The application context.
     * @param context            The portlet context.
     * @param request            The portlet request.
     * @param response           The portlet response.
     */
    public RenderPortletRequest(ApplicationContext applicationContext, PortletContext context, RenderRequest request, RenderResponse response) {
        super(applicationContext, context, request, response, new PortletRequestDelegate(request), new MimeResponseDelegate(response));
    }
}
