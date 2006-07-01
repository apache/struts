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

import javax.faces.application.NavigationHandler;
import javax.faces.context.FacesContext;

import com.opensymphony.xwork.ActionContext;

/**
 * Overrides the JFS navigation by delegating the result to handling by the core
 * result code lookup and execution.
 */
public class StrutsNavigationHandler extends NavigationHandler {

	/**
	 * Stores any outcomes as the result code
	 * 
	 * @param facesContext The faces context
	 * @param fromAction The action we are coming from
	 * @param outcome The String return code
	 */
	@Override
	public void handleNavigation(FacesContext facesContext, String fromAction, String outcome) {
		ActionContext ctx = ActionContext.getContext();
		if (outcome != null) {
			ctx.getActionInvocation().setResultCode(outcome);
		}
	}

}
