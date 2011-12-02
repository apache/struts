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

import javax.faces.context.FacesContext;
import javax.faces.el.EvaluationException;
import javax.faces.el.VariableResolver;

import com.opensymphony.xwork2.ognl.OgnlValueStack;
import com.opensymphony.xwork2.ActionContext;

/**
 * Will return a reference to the current action if the action name matches the
 * requested variable name. Otherwise it will attempt to resolve the name from
 * the value stack. Otherwise it will delegate to the original jsf resolver.
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
     * Will return a reference to the current action if the action name matches
     * the requested variable name. Otherwise it will attempt to resolve the
     * name from the value stack. Otherwise it will delegate to the original jsf
     * resolver.
     * </p>
     *
     * @param name
     *            Variable name to be resolved
     */
    public Object resolveVariable(FacesContext context, String name)
            throws EvaluationException {

        if (STRUTS_VARIABLE_NAME.equals(name)) {
            return ActionContext.getContext().getActionInvocation().getAction();
        }

        Object obj = ActionContext.getContext().getValueStack().findValue(name);
        if (obj != null) {
            return obj;
        } else {
            return original.resolveVariable(context, name);
        }

    }

}
