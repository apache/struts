/*
 * $Id$
 *
 * Copyright 2000-2004 The Apache Software Foundation.
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

package org.apache.struts2.s1;

import javax.servlet.http.HttpServletRequest;

import org.apache.struts.action.ActionForm;
import org.apache.struts.action.ActionMapping;
import org.apache.struts2.ServletActionContext;
import org.apache.struts2.dispatcher.Dispatcher;

import com.opensymphony.xwork2.ActionInvocation;
import com.opensymphony.xwork2.interceptor.AbstractInterceptor;
import com.opensymphony.xwork2.interceptor.ScopedModelDriven;

/**
 *  Calls the reset() method on the ActionForm, if it exists.
 */
public class ActionFormResetInterceptor extends AbstractInterceptor {

    @Override
    public String intercept(ActionInvocation invocation) throws Exception {
        Object action = invocation.getAction();

        if (action instanceof ScopedModelDriven) {
            ScopedModelDriven modelDriven = (ScopedModelDriven) action;
            Object model = modelDriven.getModel();
            if (model != null && model instanceof ActionForm) {
                Struts1Factory factory = new Struts1Factory(Dispatcher.getInstance().getConfigurationManager().getConfiguration());
                ActionMapping mapping = factory.createActionMapping(invocation.getProxy().getConfig());
                HttpServletRequest req = ServletActionContext.getRequest();
                ((ActionForm)model).reset(mapping, req);
            }
        }
        return invocation.invoke();
    }
}
