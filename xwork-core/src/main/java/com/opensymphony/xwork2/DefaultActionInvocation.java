/*
 * Copyright 2002-2006,2009 The Apache Software Foundation.
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
package com.opensymphony.xwork2;

import com.opensymphony.xwork2.config.ConfigurationException;
import com.opensymphony.xwork2.config.entities.ActionConfig;
import com.opensymphony.xwork2.config.entities.InterceptorMapping;
import com.opensymphony.xwork2.config.entities.ResultConfig;
import com.opensymphony.xwork2.inject.Container;
import com.opensymphony.xwork2.inject.Inject;
import com.opensymphony.xwork2.interceptor.PreResultListener;
import com.opensymphony.xwork2.ognl.OgnlUtil;
import com.opensymphony.xwork2.util.ValueStack;
import com.opensymphony.xwork2.util.ValueStackFactory;
import com.opensymphony.xwork2.util.logging.Logger;
import com.opensymphony.xwork2.util.logging.LoggerFactory;
import com.opensymphony.xwork2.util.profiling.UtilTimerStack;
import ognl.MethodFailedException;
import ognl.NoSuchPropertyException;
import ognl.OgnlException;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Map;


/**
 * The Default ActionInvocation implementation
 *
 * @author Rainer Hermanns
 * @author tmjee
 * @version $Date$ $Id$
 * @see com.opensymphony.xwork2.DefaultActionProxy
 */
public class DefaultActionInvocation implements ActionInvocation {

    private static final Logger LOG = LoggerFactory.getLogger(DefaultActionInvocation.class);

    protected Object action;
    protected ActionProxy proxy;
    protected List<PreResultListener> preResultListeners;
    protected Map<String, Object> extraContext;
    protected ActionContext invocationContext;
    protected Iterator<InterceptorMapping> interceptors;
    protected ValueStack stack;
    protected Result result;
    protected Result explicitResult;
    protected String resultCode;
    protected boolean executed = false;
    protected boolean pushAction = true;
    protected ObjectFactory objectFactory;
    protected ActionEventListener actionEventListener;
    protected ValueStackFactory valueStackFactory;
    protected Container container;
    protected UnknownHandlerManager unknownHandlerManager;
    protected OgnlUtil ognlUtil;

    public DefaultActionInvocation(final Map<String, Object> extraContext, final boolean pushAction) {
        this.extraContext = extraContext;
        this.pushAction = pushAction;
    }

    @Inject
    public void setUnknownHandlerManager(UnknownHandlerManager unknownHandlerManager) {
        this.unknownHandlerManager = unknownHandlerManager;
    }

    @Inject
    public void setValueStackFactory(ValueStackFactory fac) {
        this.valueStackFactory = fac;
    }

    @Inject
    public void setObjectFactory(ObjectFactory fac) {
        this.objectFactory = fac;
    }

    @Inject
    public void setContainer(Container cont) {
        this.container = cont;
    }

    @Inject(required=false)
    public void setActionEventListener(ActionEventListener listener) {
        this.actionEventListener = listener;
    }

    @Inject
    public void setOgnlUtil(OgnlUtil ognlUtil) {
        this.ognlUtil = ognlUtil;
    }

    public Object getAction() {
        return action;
    }

    public boolean isExecuted() {
        return executed;
    }

    public ActionContext getInvocationContext() {
        return invocationContext;
    }

    public ActionProxy getProxy() {
        return proxy;
    }

    /**
     * If the DefaultActionInvocation has been executed before and the Result is an instance of ActionChainResult, this method
     * will walk down the chain of ActionChainResults until it finds a non-chain result, which will be returned. If the
     * DefaultActionInvocation's result has not been executed before, the Result instance will be created and populated with
     * the result params.
     *
     * @return a Result instance
     * @throws Exception
     */
    public Result getResult() throws Exception {
        Result returnResult = result;

        // If we've chained to other Actions, we need to find the last result
        while (returnResult instanceof ActionChainResult) {
            ActionProxy aProxy = ((ActionChainResult) returnResult).getProxy();

            if (aProxy != null) {
                Result proxyResult = aProxy.getInvocation().getResult();

                if ((proxyResult != null) && (aProxy.getExecuteResult())) {
                    returnResult = proxyResult;
                } else {
                    break;
                }
            } else {
                break;
            }
        }

        return returnResult;
    }

    public String getResultCode() {
        return resultCode;
    }

    public void setResultCode(String resultCode) {
        if (isExecuted())
            throw new IllegalStateException("Result has already been executed.");

        this.resultCode = resultCode;
    }


    public ValueStack getStack() {
        return stack;
    }

    /**
     * Register a com.opensymphony.xwork2.interceptor.PreResultListener to be notified after the Action is executed and before the
     * Result is executed. The ActionInvocation implementation must guarantee that listeners will be called in the order
     * in which they are registered. Listener registration and execution does not need to be thread-safe.
     *
     * @param listener to register
     */
    public void addPreResultListener(PreResultListener listener) {
        if (preResultListeners == null) {
            preResultListeners = new ArrayList<PreResultListener>(1);
        }

        preResultListeners.add(listener);
    }

    public Result createResult() throws Exception {
        LOG.trace("Creating result related to resultCode [#0]", resultCode);

        if (explicitResult != null) {
            Result ret = explicitResult;
            explicitResult = null;

            return ret;
        }
        ActionConfig config = proxy.getConfig();
        Map<String, ResultConfig> results = config.getResults();

        ResultConfig resultConfig = null;

        try {
            resultConfig = results.get(resultCode);
        } catch (NullPointerException e) {
            if (LOG.isDebugEnabled()) {
                LOG.debug("Got NPE trying to read result configuration for resultCode [#0]", resultCode);
            }
        }
        
        if (resultConfig == null) {
            // If no result is found for the given resultCode, try to get a wildcard '*' match.
            resultConfig = results.get("*");
        }

        if (resultConfig != null) {
            try {
                return objectFactory.buildResult(resultConfig, invocationContext.getContextMap());
            } catch (Exception e) {
                if (LOG.isErrorEnabled()) {
                    LOG.error("There was an exception while instantiating the result of type #0", e, resultConfig.getClassName());
                }
                throw new XWorkException(e, resultConfig);
            }
        } else if (resultCode != null && !Action.NONE.equals(resultCode) && unknownHandlerManager.hasUnknownHandlers()) {
            return unknownHandlerManager.handleUnknownResult(invocationContext, proxy.getActionName(), proxy.getConfig(), resultCode);
        }
        return null;
    }

    /**
     * @throws ConfigurationException If no result can be found with the returned code
     */
    public String invoke() throws Exception {
        String profileKey = "invoke: ";
        try {
            UtilTimerStack.push(profileKey);

            if (executed) {
                throw new IllegalStateException("Action has already executed");
            }

            if (interceptors.hasNext()) {
                final InterceptorMapping interceptor = interceptors.next();
                String interceptorMsg = "interceptor: " + interceptor.getName();
                UtilTimerStack.push(interceptorMsg);
                try {
                                resultCode = interceptor.getInterceptor().intercept(DefaultActionInvocation.this);
                            }
                finally {
                    UtilTimerStack.pop(interceptorMsg);
                }
            } else {
                resultCode = invokeActionOnly();
            }

            // this is needed because the result will be executed, then control will return to the Interceptor, which will
            // return above and flow through again
            if (!executed) {
                if (preResultListeners != null) {
                    LOG.trace("Executing PreResultListeners for result [#0]", result);

                    for (Object preResultListener : preResultListeners) {
                        PreResultListener listener = (PreResultListener) preResultListener;

                        String _profileKey = "preResultListener: ";
                        try {
                            UtilTimerStack.push(_profileKey);
                            listener.beforeResult(this, resultCode);
                        }
                        finally {
                            UtilTimerStack.pop(_profileKey);
                        }
                    }
                }

                // now execute the result, if we're supposed to
                if (proxy.getExecuteResult()) {
                    executeResult();
                }

                executed = true;
            }

            return resultCode;
        }
        finally {
            UtilTimerStack.pop(profileKey);
        }
    }

    public String invokeActionOnly() throws Exception {
        return invokeAction(getAction(), proxy.getConfig());
    }

    protected void createAction(Map<String, Object> contextMap) {
        // load action
        String timerKey = "actionCreate: " + proxy.getActionName();
        try {
            UtilTimerStack.push(timerKey);
            action = objectFactory.buildAction(proxy.getActionName(), proxy.getNamespace(), proxy.getConfig(), contextMap);
        } catch (InstantiationException e) {
            throw new XWorkException("Unable to intantiate Action!", e, proxy.getConfig());
        } catch (IllegalAccessException e) {
            throw new XWorkException("Illegal access to constructor, is it public?", e, proxy.getConfig());
        } catch (Exception e) {
            String gripe;

            if (proxy == null) {
                gripe = "Whoa!  No ActionProxy instance found in current ActionInvocation.  This is bad ... very bad";
            } else if (proxy.getConfig() == null) {
                gripe = "Sheesh.  Where'd that ActionProxy get to?  I can't find it in the current ActionInvocation!?";
            } else if (proxy.getConfig().getClassName() == null) {
                gripe = "No Action defined for '" + proxy.getActionName() + "' in namespace '" + proxy.getNamespace() + "'";
            } else {
                gripe = "Unable to instantiate Action, " + proxy.getConfig().getClassName() + ",  defined for '" + proxy.getActionName() + "' in namespace '" + proxy.getNamespace() + "'";
            }

            gripe += (((" -- " + e.getMessage()) != null) ? e.getMessage() : " [no message in exception]");
            throw new XWorkException(gripe, e, proxy.getConfig());
        } finally {
            UtilTimerStack.pop(timerKey);
        }

        if (actionEventListener != null) {
            action = actionEventListener.prepare(action, stack);
        }
    }

    protected Map<String, Object> createContextMap() {
        Map<String, Object> contextMap;

        if ((extraContext != null) && (extraContext.containsKey(ActionContext.VALUE_STACK))) {
            // In case the ValueStack was passed in
            stack = (ValueStack) extraContext.get(ActionContext.VALUE_STACK);

            if (stack == null) {
                throw new IllegalStateException("There was a null Stack set into the extra params.");
            }

            contextMap = stack.getContext();
        } else {
            // create the value stack
            // this also adds the ValueStack to its context
            stack = valueStackFactory.createValueStack();

            // create the action context
            contextMap = stack.getContext();
        }

        // put extraContext in
        if (extraContext != null) {
            contextMap.putAll(extraContext);
        }

        //put this DefaultActionInvocation into the context map
        contextMap.put(ActionContext.ACTION_INVOCATION, this);
        contextMap.put(ActionContext.CONTAINER, container);

        return contextMap;
    }

    /**
     * Uses getResult to get the final Result and executes it
     *
     * @throws ConfigurationException If not result can be found with the returned code
     */
    private void executeResult() throws Exception {
        result = createResult();

        String timerKey = "executeResult: " + getResultCode();
        try {
            UtilTimerStack.push(timerKey);
            if (result != null) {
                result.execute(this);
            } else if (resultCode != null && !Action.NONE.equals(resultCode)) {
                throw new ConfigurationException("No result defined for action " + getAction().getClass().getName()
                        + " and result " + getResultCode(), proxy.getConfig());
            } else {
                if (LOG.isDebugEnabled()) {
                    LOG.debug("No result returned for action " + getAction().getClass().getName() + " at " + proxy.getConfig().getLocation());
                }
            }
        } finally {
            UtilTimerStack.pop(timerKey);
        }
    }

    public void init(ActionProxy proxy) {
        this.proxy = proxy;
        Map<String, Object> contextMap = createContextMap();

        // Setting this so that other classes, like object factories, can use the ActionProxy and other
        // contextual information to operate
        ActionContext actionContext = ActionContext.getContext();

        if (actionContext != null) {
            actionContext.setActionInvocation(this);
        }

        createAction(contextMap);

        if (pushAction) {
            stack.push(action);
            contextMap.put("action", action);
        }

        invocationContext = new ActionContext(contextMap);
        invocationContext.setName(proxy.getActionName());

        createInterceptors(proxy);
    }

    protected void createInterceptors(ActionProxy proxy) {
        // get a new List so we don't get problems with the iterator if someone changes the list
        List<InterceptorMapping> interceptorList = new ArrayList<InterceptorMapping>(proxy.getConfig().getInterceptors());
        interceptors = interceptorList.iterator();
    }

    protected String invokeAction(Object action, ActionConfig actionConfig) throws Exception {
        String methodName = proxy.getMethod();

        if (LOG.isDebugEnabled()) {
            LOG.debug("Executing action method = #0", methodName);
        }

        String timerKey = "invokeAction: " + proxy.getActionName();
        try {
            UtilTimerStack.push(timerKey);

            Object methodResult;
            try {
                methodResult = ognlUtil.callMethod(methodName + "()", getStack().getContext(), action);
            } catch (MethodFailedException e) {
                // if reason is missing method, try find version with "do" prefix
                if (e.getReason() instanceof NoSuchMethodException) {
                    try {
                        String altMethodName = "do" + methodName.substring(0, 1).toUpperCase() + methodName.substring(1) + "()";
                        methodResult = ognlUtil.callMethod(altMethodName, getStack().getContext(), action);
                    } catch (MethodFailedException e1) {
                        // if still method doesn't exist, try checking UnknownHandlers
                        if (e1.getReason() instanceof NoSuchMethodException) {
                            if (unknownHandlerManager.hasUnknownHandlers()) {
                                try {
                                    methodResult = unknownHandlerManager.handleUnknownMethod(action, methodName);
                                } catch (NoSuchMethodException e2) {
                                    // throw the original one
                                    throw e;
                                }
                            } else {
                                // throw the original one
                                throw e;
                            }
                            // throw the original exception as UnknownHandlers weren't able to handle invocation as well
                            if (methodResult == null) {
                                throw e;
                            }
                        } else {
                            // exception isn't related to missing action method, throw it
                            throw e1;
                        }
                    }
                } else {
                    // exception isn't related to missing action method, throw it
                    throw e;
                }
            }
            return saveResult(actionConfig, methodResult);
        } catch (NoSuchPropertyException e) {
            throw new IllegalArgumentException("The " + methodName + "() is not defined in action " + getAction().getClass() + "");
        } catch (MethodFailedException e) {
            // We try to return the source exception.
            Throwable t = e.getCause();

            if (actionEventListener != null) {
                String result = actionEventListener.handleException(t, getStack());
                if (result != null) {
                    return result;
                }
            }
            if (t instanceof Exception) {
                throw (Exception) t;
            } else {
                throw e;
            }
        } finally {
            UtilTimerStack.pop(timerKey);
        }
    }

    /**
     * Save the result to be used later.
     * @param actionConfig current ActionConfig
     * @param methodResult the result of the action.
     * @return the result code to process.
     */
    protected String saveResult(ActionConfig actionConfig, Object methodResult) {
        if (methodResult instanceof Result) {
            this.explicitResult = (Result) methodResult;

            // Wire the result automatically
            container.inject(explicitResult);
            return null;
        } else {
            return (String) methodResult;
        }
    }

    /**
     * Version ready to be serialize
     *
     * @return instance without reference to {@link Container}
     */
    public ActionInvocation serialize() {
        DefaultActionInvocation that = this;
        that.container = null;
        return that;
    }

    /**
     * Restoring Container
     *
     * @param actionContext current {@link ActionContext}
     * @return instance which can be used to invoke action
     */
    public ActionInvocation deserialize(ActionContext actionContext) {
        DefaultActionInvocation that = this;
        that.container = actionContext.getContainer();
        return that;
    }

}
