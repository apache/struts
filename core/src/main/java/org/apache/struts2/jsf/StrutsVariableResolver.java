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
import javax.faces.el.EvaluationException;
import javax.faces.el.VariableResolver;

import com.opensymphony.xwork.ActionContext;

/**
 * Adds the current Action instance to the variable lookups. All other requests
 * delegate to underlying resolver.
 */
public class StrutsVariableResolver extends VariableResolver {

	/** The original <code>VariableResolver</code> passed to our constructor. */
	private VariableResolver original = null;

	/** The variable name of our Struts action */
	private static final String STRUTS_VARIABLE_NAME = "action";

	/**
	 * Constructor
	 * 
	 * @param original
	 *            Original resolver to delegate to.
	 */
	public StrutsVariableResolver(VariableResolver original) {

		this.original = original;

	}

	/**
	 * <p>
	 * Resolve variable names known to this resolver; otherwise, delegate to the
	 * original resolver passed to our constructor.
	 * </p>
	 * 
	 * @param name
	 *            Variable name to be resolved
	 */
	public Object resolveVariable(FacesContext context, String name)
			throws EvaluationException {

		if (STRUTS_VARIABLE_NAME.equals(name)) {
			return ActionContext.getContext().getActionInvocation().getAction();
		} else {
			return original.resolveVariable(context, name);
		}

	}

}
