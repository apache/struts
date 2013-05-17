/*
 * Copyright 2002-2007,2009 The Apache Software Foundation.
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

import com.opensymphony.xwork2.interceptor.PreResultListener;
import com.opensymphony.xwork2.util.ValueStack;

import java.io.Serializable;


/**
 * An {@link ActionInvocation} represents the execution state of an {@link Action}. It holds the Interceptors and the Action instance.
 * By repeated re-entrant execution of the <code>invoke()</code> method, initially by the {@link ActionProxy}, then by the Interceptors, the
 * Interceptors are all executed, and then the {@link Action} and the {@link Result}.
 *
 * @author Jason Carreira
 * @see com.opensymphony.xwork2.ActionProxy
 */
public interface ActionInvocation extends Serializable {

    /**
     * Get the Action associated with this ActionInvocation.
     *
     * @return the Action
     */
    Object getAction();

    /**
     * Gets whether this ActionInvocation has executed before.
     * This will be set after the Action and the Result have executed.
     *
     * @return <tt>true</tt> if this ActionInvocation has executed before.
     */
    boolean isExecuted();

    /**
     * Gets the ActionContext associated with this ActionInvocation. The ActionProxy is
     * responsible for setting this ActionContext onto the ThreadLocal before invoking
     * the ActionInvocation and resetting the old ActionContext afterwards.
     *
     * @return the ActionContext.
     */
    ActionContext getInvocationContext();

    /**
     * Get the ActionProxy holding this ActionInvocation.
     *
     * @return the ActionProxy.
     */
    ActionProxy getProxy();

    /**
     * If the ActionInvocation has been executed before and the Result is an instance of {@link ActionChainResult}, this method
     * will walk down the chain of <code>ActionChainResult</code>s until it finds a non-chain result, which will be returned. If the
     * ActionInvocation's result has not been executed before, the Result instance will be created and populated with
     * the result params.
     *
     * @return the result.
     * @throws Exception can be thrown.
     */
    Result getResult() throws Exception;

    /**
     * Gets the result code returned from this ActionInvocation.
     *
     * @return the result code
     */
    String getResultCode();

    /**
     * Sets the result code, possibly overriding the one returned by the
     * action.
     * <p/>
     * The "intended" purpose of this method is to allow PreResultListeners to
     * override the result code returned by the Action.
     * <p/>
     * If this method is used before the Action executes, the Action's returned
     * result code will override what was set. However the Action could (if
     * specifically coded to do so) inspect the ActionInvocation to see that
     * someone "upstream" (e.g. an Interceptor) had suggested a value as the
     * result, and it could therefore return the same value itself.
     * <p/>
     * If this method is called between the Action execution and the Result
     * execution, then the value set here will override the result code the
     * action had returned.  Creating an Interceptor that implements
     * {@link PreResultListener} will give you this oportunity.
     * <p/>
     * If this method is called after the Result has been executed, it will
     * have the effect of raising an IllegalStateException.
     *
     * @param resultCode  the result code.
     * @throws IllegalStateException if called after the Result has been executed.
     * @see #isExecuted()
     */
    void setResultCode(String resultCode);

    /**
     * Gets the ValueStack associated with this ActionInvocation.
     *
     * @return the ValueStack
     */
    ValueStack getStack();

    /**
     * Register a {@link PreResultListener} to be notified after the Action is executed and
     * before the Result is executed.
     * <p/>
     * The ActionInvocation implementation must guarantee that listeners will be called in
     * the order in which they are registered.
     * <p/>
     * Listener registration and execution does not need to be thread-safe.
     *
     * @param listener the listener to add.
     */
    void addPreResultListener(PreResultListener listener);

    /**
     * Invokes the next step in processing this ActionInvocation.
     * <p/>
     * If there are more Interceptors, this will call the next one. If Interceptors choose not to short-circuit
     * ActionInvocation processing and return their own return code, they will call invoke() to allow the next Interceptor
     * to execute. If there are no more Interceptors to be applied, the Action is executed.
     * If the {@link ActionProxy#getExecuteResult()} method returns <tt>true</tt>, the Result is also executed.
     *
     * @throws Exception can be thrown.
     * @return the return code.
     */
    String invoke() throws Exception;

    /**
     * Invokes only the Action (not Interceptors or Results).
     * <p/>
     * This is useful in rare situations where advanced usage with the interceptor/action/result workflow is
     * being manipulated for certain functionality.
     *
     * @return the return code.
     * @throws Exception can be thrown.
     */
    String invokeActionOnly() throws Exception;

    /**
     * Sets the action event listener to respond to key action events.
     *
     * @param listener the listener.
     */
    void setActionEventListener(ActionEventListener listener);

    void init(ActionProxy proxy) ;

    /**
     * Prepares instance of ActionInvocation to be serializable,
     * which simple means removing all unserializable fields, eg. Container
     *
     * @return ActionInvocation which can be serialize (eg. into HttpSession)
     */
    ActionInvocation serialize();

    /**
     * Performs opposite process to restore back ActionInvocation after deserialisation
     *
     * @param actionContext current {@link ActionContext}
     * @return fully operational ActionInvocation
     */
    ActionInvocation deserialize(ActionContext actionContext);

}
