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

package org.apache.struts2.s1;

import java.util.Arrays;
import java.util.Iterator;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.struts.Globals;
import org.apache.struts.action.Action;
import org.apache.struts.action.ActionForm;
import org.apache.struts.action.ActionForward;
import org.apache.struts.action.ActionMapping;
import org.apache.struts.action.ActionMessage;
import org.apache.struts.action.ActionMessages;
import org.apache.struts2.ServletActionContext;
import org.apache.struts2.StrutsException;
import org.apache.struts2.dispatcher.DefaultActionSupport;
import org.apache.struts2.dispatcher.Dispatcher;

import com.opensymphony.xwork2.ActionContext;
import com.opensymphony.xwork2.ObjectFactory;
import com.opensymphony.xwork2.config.Configuration;
import com.opensymphony.xwork2.config.entities.ActionConfig;
import com.opensymphony.xwork2.inject.Inject;
import com.opensymphony.xwork2.interceptor.ScopedModelDriven;

/**
 * Wraps legacy Struts 1.3 Actions.  Supports the following features:
 * <ul>
 *  <li>ActionForms</li>
 *  <li>ActionForwards that have the same name as a result</li>
 *  <li>ActionMessages stored in the request, converted to Struts 2 messages</li>
 *  <li>Action-level validation flag</li>
 * </ul>
 * Still to do:
 * <ul>
 *  <li>Custom ActionForward instances that don't have an associated result config</li>
 *  <li>setServlet() calls for the Action</li>
 *  <li>Most everything else...</li>
 * </ul>
 */
public class Struts1Action extends DefaultActionSupport implements ScopedModelDriven<ActionForm> {

    private ActionForm actionForm;
    private String className;
    private boolean validate;
    private String scopeKey;
    private ObjectFactory objectFactory;
    private Configuration configuration;
    
    @Inject
    public void setObjectFactory(ObjectFactory fac) {
        this.objectFactory = fac;
    }
    
    @Inject
    public void setConfiguration(Configuration config) {
        this.configuration = config;
    }
    
    public String execute() throws Exception {
        ActionContext ctx = ActionContext.getContext();
        ActionConfig actionConfig = ctx.getActionInvocation().getProxy().getConfig();
        Action action = null;
        try {
            action = (Action) objectFactory.buildBean(className, null);
        } catch (Exception e) {
            throw new StrutsException("Unable to create the legacy Struts Action", e, actionConfig);
        }
        
        // We should call setServlet() here, but let's stub that out later
        
        Struts1Factory strutsFactory = new Struts1Factory(Dispatcher.getInstance().getConfigurationManager().getConfiguration());
        ActionMapping mapping = strutsFactory.createActionMapping(actionConfig);
        HttpServletRequest request = ServletActionContext.getRequest();
        HttpServletResponse response = ServletActionContext.getResponse();
        ActionForward forward = action.execute(mapping, actionForm, request, response);
        
        ActionMessages messages = (ActionMessages) request.getAttribute(Globals.MESSAGE_KEY);
        if (messages != null) {
            for (Iterator i = messages.get(); i.hasNext(); ) {
                ActionMessage msg = (ActionMessage) i.next();
                if (msg.getValues() != null && msg.getValues().length > 0) {
                    addActionMessage(getText(msg.getKey(), Arrays.asList(msg.getValues())));
                } else {
                    addActionMessage(getText(msg.getKey()));
                }
            }
        }
        
        if (forward instanceof WrapperActionForward || actionConfig.getResults().containsKey(forward.getName())) {
            return forward.getName();
        } else {
            throw new StrutsException("Unable to handle action forwards that don't have an associated result", actionConfig);
        }
    }
    
    public void setModel(ActionForm model) {
        actionForm = model;
    }

    public ActionForm getModel() {
        return actionForm;
    }
    
    /**
     * @return the validate
     */
    public boolean isValidate() {
        return validate;
    }

    /**
     * @param validate the validate to set
     */
    public void setValidate(boolean validate) {
        this.validate = validate;
    }

    /**
     * @param className the className to set
     */
    public void setClassName(String className) {
        this.className = className;
    }

    public String getScopeKey() {
        return scopeKey;
    }

    public void setScopeKey(String key) {
        this.scopeKey = key;
    }
}
