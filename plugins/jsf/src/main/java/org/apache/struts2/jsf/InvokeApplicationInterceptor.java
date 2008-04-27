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

import javax.faces.FacesException;
import javax.faces.context.FacesContext;
import javax.faces.event.PhaseId;

/**
 * Invokes the application
 */
public class InvokeApplicationInterceptor extends FacesInterceptor {

    private static final long serialVersionUID = -7388153356410171208L;

    /**
     * Invoke Application (JSF.2.2.5)
     *
     * @param viewId
     *            The view id
     * @param facesContext
     *            The faces context
     * @return true, if response is complete
     */
    protected boolean executePhase(String viewId, FacesContext facesContext)
            throws FacesException {
        boolean skipFurtherProcessing = false;
        if (log.isTraceEnabled())
            log.trace("entering invokeApplication");

        informPhaseListenersBefore(facesContext, PhaseId.INVOKE_APPLICATION);

        try {
            if (isResponseComplete(facesContext, "invokeApplication", true)) {
                // have to return right away
                return true;
            }
            if (shouldRenderResponse(facesContext, "invokeApplication", true)) {
                skipFurtherProcessing = true;
            }

            facesContext.getViewRoot().processApplication(facesContext);
        } finally {
            informPhaseListenersAfter(facesContext, PhaseId.INVOKE_APPLICATION);
        }

        if (isResponseComplete(facesContext, "invokeApplication", false)
                || shouldRenderResponse(facesContext, "invokeApplication",
                        false)) {
            // since this phase is completed we don't need to return right away
            // even if the response is completed
            skipFurtherProcessing = true;
        }

        if (!skipFurtherProcessing && log.isTraceEnabled())
            log.trace("exiting invokeApplication ");

        return skipFurtherProcessing;
    }
}
