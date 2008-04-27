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
 *  http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing,
 * software distributed under the License is distributed on an
 * "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY
 * KIND, either express or implied.  See the License for the
 * specific language governing permissions and limitations
 * under the License.
 */

package org.apache.struts2.jsf;

import java.io.IOException;

import javax.faces.FacesException;
import javax.faces.application.Application;
import javax.faces.application.ViewHandler;
import javax.faces.context.FacesContext;
import javax.faces.event.PhaseId;

/**
 * Performs the JSF render lifecycle phase.
 *
 */
public class FacesRender extends FacesSupport {

    /**
     * Executes the render phase, borrowed from MyFaces
     *
     * @param facesContext
     *            The faces context
     * @throws FacesException
     *             If anything goes wrong
     */
    public void render(FacesContext facesContext) throws FacesException {
        // if the response is complete we should not be invoking the phase
        // listeners
        if (isResponseComplete(facesContext, "render", true)) {
            return;
        }
        if (log.isTraceEnabled())
            log.trace("entering renderResponse");

        informPhaseListenersBefore(facesContext, PhaseId.RENDER_RESPONSE);
        try {
            // also possible that one of the listeners completed the response
            if (isResponseComplete(facesContext, "render", true)) {
                return;
            }
            Application application = facesContext.getApplication();
            ViewHandler viewHandler = application.getViewHandler();
            try {
                viewHandler
                        .renderView(facesContext, facesContext.getViewRoot());
            } catch (IOException e) {
                throw new FacesException(e.getMessage(), e);
            }
        } finally {
            informPhaseListenersAfter(facesContext, PhaseId.RENDER_RESPONSE);
        }
        if (log.isTraceEnabled()) {
            // Note: DebugUtils Logger must also be in trace level
            // DebugUtils.traceView("View after rendering");
        }

        if (log.isTraceEnabled())
            log.trace("exiting renderResponse");
    }
}
