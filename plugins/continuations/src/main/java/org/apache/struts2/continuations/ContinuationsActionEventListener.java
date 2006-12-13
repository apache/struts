/*
 * $Id: Dispatcher.java 484733 2006-12-08 20:16:16Z mrdon $
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
package org.apache.struts2.continuations;

import java.util.Map;

import com.opensymphony.xwork2.ActionContext;
import com.opensymphony.xwork2.ActionEventListener;
import com.opensymphony.xwork2.util.ValueStack;
import com.uwyn.rife.continuations.ContinuableObject;
import com.uwyn.rife.continuations.ContinuationConfig;
import com.uwyn.rife.continuations.ContinuationContext;
import com.uwyn.rife.continuations.ContinuationManager;
import com.uwyn.rife.continuations.exceptions.PauseException;

/**
 * Hooks Rife continuations into key events in the Action instance lifecycle
 */
public class ContinuationsActionEventListener implements ActionEventListener {
    ContinuationManager cm;
    
    public ContinuationsActionEventListener() {
        if (ContinuationConfig.getInstance() != null) {
            cm = new ContinuationManager();
        }
    }
    
    /**
     * Sets the continuation context and loads the proper continuation action
     */
    public Object prepare(Object action, ValueStack stack) {
        Map params = ActionContext.getContext().getParameters();
        String contId = (String) params.get(StrutsContinuationConfig.CONTINUE_PARAM);
        if (contId != null) {
            // remove the continue key from the params - we don't want to bother setting
            // on the value stack since we know it won't work. Besides, this breaks devMode!
            params.remove(StrutsContinuationConfig.CONTINUE_PARAM);
        }
        
        
        if (action instanceof ContinuableObject) {
            ContinuationContext ctx = ContinuationContext.createInstance((ContinuableObject) action);
            if (action instanceof NonCloningContinuableObject) {
                ctx.setShouldClone(false);
            }
        }

        try {
            if (contId != null) {
                ContinuationContext context = cm.getContext(contId);
                if (context != null) {
                    ContinuationContext.setContext(context);
                    // use the original action instead
                    Object original = context.getContinuable();
                    action = original;
                }
            }
        } catch (CloneNotSupportedException e) {
            e.printStackTrace();
        }
        return action;
    }
    
    /**
     * Handles the normal continuation exception
     */
    public String handleException(Throwable t, ValueStack stack) {
        if (t instanceof PauseException) {
            // continuations in effect!
            PauseException pe = ((PauseException) t);
            ContinuationContext context = pe.getContext();
            String result = (String) pe.getParameters();
            stack.getContext().put(StrutsContinuationConfig.CONTINUE_KEY, context.getId());
            cm.addContext(context);

            return result;
        }
        return null;
    }
}
