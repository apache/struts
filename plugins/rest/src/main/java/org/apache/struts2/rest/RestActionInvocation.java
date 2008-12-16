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

package org.apache.struts2.rest;

import com.opensymphony.xwork2.ActionInvocation;
import com.opensymphony.xwork2.DefaultActionInvocation;
import com.opensymphony.xwork2.Result;
import com.opensymphony.xwork2.UnknownHandlerManager;
import com.opensymphony.xwork2.config.entities.ActionConfig;
import com.opensymphony.xwork2.inject.Inject;
import com.opensymphony.xwork2.util.logging.Logger;
import com.opensymphony.xwork2.util.logging.LoggerFactory;
import com.opensymphony.xwork2.util.profiling.UtilTimerStack;

import java.io.IOException;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.Map;


/**
 * Extends the usual {@link ActionInvocation} to add support for processing the object returned
 * from the action execution.  This allows us to support methods that return {@link HttpHeaders}
 * as well as apply content type-specific operations to the result.
 */
public class RestActionInvocation extends DefaultActionInvocation {
    
    private static final long serialVersionUID = 3485701178946428716L;

    private static final Logger LOG = LoggerFactory.getLogger(RestActionInvocation.class);
    
    private ContentTypeHandlerManager handlerSelector;

    protected RestActionInvocation(Map extraContext, boolean pushAction) {
        super(extraContext, pushAction);
    }

    @Inject
    public void setMimeTypeHandlerSelector(ContentTypeHandlerManager sel) {
        this.handlerSelector = sel;
    }
    
    protected String invokeAction(Object action, ActionConfig actionConfig) throws Exception {
        String methodName = proxy.getMethod();

        if (LOG.isDebugEnabled()) {
            LOG.debug("Executing action method = " + actionConfig.getMethodName());
        }

        String timerKey = "invokeAction: "+proxy.getActionName();
        try {
            UtilTimerStack.push(timerKey);
            
            boolean methodCalled = false;
            Object methodResult = null;
            Method method = null;
            try {
                method = getAction().getClass().getMethod(methodName, new Class[0]);
            } catch (NoSuchMethodException e) {
                // hmm -- OK, try doXxx instead
                try {
                    String altMethodName = "do" + methodName.substring(0, 1).toUpperCase() + methodName.substring(1);
                    method = getAction().getClass().getMethod(altMethodName, new Class[0]);
                } catch (NoSuchMethodException e1) {
                    // well, give the unknown handler a shot
                    if (unknownHandlerManager.hasUnknownHandlers()) {
                        try {
                            methodResult = unknownHandlerManager.handleUnknownMethod(action, methodName);
                            methodCalled = true;
                        } catch (NoSuchMethodException e2) {
                            // throw the original one
                            throw e;
                        }
                    } else {
                        throw e;
                    }
                }
            }
            
            if (!methodCalled) {
                methodResult = method.invoke(action, new Object[0]);
            }
            
            return processResult(actionConfig, methodResult);
        } catch (NoSuchMethodException e) {
            throw new IllegalArgumentException("The " + methodName + "() is not defined in action " + getAction().getClass() + "");
        } catch (InvocationTargetException e) {
            // We try to return the source exception.
            Throwable t = e.getTargetException();

            if (actionEventListener != null) {
                String result = actionEventListener.handleException(t, getStack());
                if (result != null) {
                    return result;
                }
            }
            if (t instanceof Exception) {
                throw(Exception) t;
            } else {
                throw e;
            }
        } finally {
            UtilTimerStack.pop(timerKey);
        }
    }

    protected String processResult(ActionConfig actionConfig, Object methodResult) throws IOException {
        if (methodResult instanceof Result) {
            this.explicitResult = (Result) methodResult;
            // Wire the result automatically
            container.inject(explicitResult);
            return null;
        } else if (methodResult != null) {
            resultCode = handlerSelector.handleResult(actionConfig, methodResult, action);
        }
        return resultCode;
    }

}
