/*
 * $Id: TokenInterceptor.java 394468 2006-04-16 12:16:03Z tmjee $
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
package org.apache.struts2.jsf;

import javax.faces.context.FacesContext;
import javax.faces.event.PhaseEvent;
import javax.faces.event.PhaseId;
import javax.faces.event.PhaseListener;
import javax.faces.lifecycle.Lifecycle;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import com.opensymphony.xwork.ActionContext;

/**
 * Provides common lifecycle phase methods needed by interceptors and results.
 */
public class FacesSupport {

	private static final String LIFECYCLE_KEY = "lifecycle";

	/** Log instance for each class */
	protected Log log;

	/** Initializes log instance for the instance object */
	protected FacesSupport() {
		log = LogFactory.getLog(getClass());
	}

	/**
	 * Gets the shared lifecycle for this request
	 * 
	 * @return The lifecycle
	 */
	private Lifecycle getLifecycle() {
		return (Lifecycle) ActionContext.getContext().get(LIFECYCLE_KEY);
	}

	/**
	 * Sets the lifecycle for this request
	 * 
	 * @param lifecycle
	 *            The lifecycle
	 */
	protected void setLifecycle(Lifecycle lifecycle) {
		ActionContext.getContext().put(LIFECYCLE_KEY, lifecycle);
	}

	/**
	 * Informs phase listeners before a phase is executed
	 * 
	 * @param facesContext
	 *            The current faces context
	 * @param phaseId
	 *            The phase id about to be executed
	 */
	protected void informPhaseListenersBefore(FacesContext facesContext,
			PhaseId phaseId) {
		Lifecycle lifecycle = getLifecycle();
		PhaseListener[] phaseListeners = lifecycle.getPhaseListeners();
		for (int i = 0; i < phaseListeners.length; i++) {
			PhaseListener phaseListener = phaseListeners[i];
			int listenerPhaseId = phaseListener.getPhaseId().getOrdinal();
			if (listenerPhaseId == PhaseId.ANY_PHASE.getOrdinal()
					|| listenerPhaseId == phaseId.getOrdinal()) {
				phaseListener.beforePhase(new PhaseEvent(FacesContext
						.getCurrentInstance(), phaseId, lifecycle));
			}
		}

	}

	/**
	 * Informs phase listeners after a phase is executed
	 * 
	 * @param facesContext
	 *            The current faces context
	 * @param phaseId
	 *            The phase id that was executed
	 */
	protected void informPhaseListenersAfter(FacesContext facesContext,
			PhaseId phaseId) {
		Lifecycle lifecycle = getLifecycle();
		PhaseListener[] phaseListeners = lifecycle.getPhaseListeners();
		for (int i = 0; i < phaseListeners.length; i++) {
			PhaseListener phaseListener = phaseListeners[i];
			int listenerPhaseId = phaseListener.getPhaseId().getOrdinal();
			if (listenerPhaseId == PhaseId.ANY_PHASE.getOrdinal()
					|| listenerPhaseId == phaseId.getOrdinal()) {
				phaseListener.afterPhase(new PhaseEvent(FacesContext
						.getCurrentInstance(), phaseId, lifecycle));
			}
		}

	}

	/**
	 * Checks to see if the response has been completed. Mainly used for better
	 * debugging messages.
	 * 
	 * @param facesContext
	 *            The current faces context
	 * @param phase
	 *            The phase id in execution
	 * @param before
	 *            Whether the phase has been executed or not
	 * @return True if the response is complete
	 */
	protected boolean isResponseComplete(FacesContext facesContext,
			String phase, boolean before) {
		boolean flag = false;
		if (facesContext.getResponseComplete()) {
			if (log.isDebugEnabled())
				log
						.debug("exiting from lifecycle.execute in "
								+ phase
								+ " because getResponseComplete is true from one of the "
								+ (before ? "before" : "after") + " listeners");
			flag = true;
		}
		return flag;
	}

	/**
	 * Checks to see the render phase should be executed next. Mainly used for
	 * better debugging messages.
	 * 
	 * @param facesContext
	 *            The current faces context
	 * @param phase
	 *            The phase id in execution
	 * @param before
	 *            Whether the phase has been executed or not
	 * @return True if the response is complete
	 */
	protected boolean shouldRenderResponse(FacesContext facesContext,
			String phase, boolean before) {
		boolean flag = false;
		if (facesContext.getRenderResponse()) {
			if (log.isDebugEnabled())
				log.debug("exiting from lifecycle.execute in " + phase
						+ " because getRenderResponse is true from one of the "
						+ (before ? "before" : "after") + " listeners");
			flag = true;
		}
		return flag;
	}
}
