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

import com.opensymphony.xwork2.config.entities.ActionConfig;


/**
 * ActionProxy is an extra layer between XWork and the action so that different proxies are possible.
 * <p/>
 * An example of this would be a remote proxy, where the layer between XWork and the action might be RMI or SOAP.
 *
 * @author Jason Carreira
 */
public interface ActionProxy {

    /**
     * Gets the Action instance for this Proxy.
     *
     * @return the Action instance
     */
    Object getAction();

    /**
     * Gets the alias name this ActionProxy is mapped to.
     *
     * @return the alias name
     */
    String getActionName();

    /**
     * Gets the ActionConfig this ActionProxy is built from.
     *
     * @return the ActionConfig
     */
    ActionConfig getConfig();

    /**
     * Sets whether this ActionProxy should also execute the Result after executing the Action.
     *
     * @param executeResult <tt>true</tt> to also execute the Result.
     */
    void setExecuteResult(boolean executeResult);

    /**
     * Gets the status of whether the ActionProxy is set to execute the Result after the Action is executed.
     *
     * @return the status
     */
    boolean getExecuteResult();

    /**
     * Gets the ActionInvocation associated with this ActionProxy.
     *
     * @return the ActionInvocation
     */
    ActionInvocation getInvocation();

    /**
     * Gets the namespace the ActionConfig for this ActionProxy is mapped to.
     *
     * @return the namespace
     */
    String getNamespace();

    /**
     * Execute this ActionProxy. This will set the ActionContext from the ActionInvocation into the ActionContext
     * ThreadLocal before invoking the ActionInvocation, then set the old ActionContext back into the ThreadLocal.
     *
     * @return the result code returned from executing the ActionInvocation
     * @throws Exception can be thrown.
     * @see ActionInvocation
     */
    String execute() throws Exception;

    /**
     * Gets the method name to execute, or <tt>null</tt> if no method has been specified (meaning <code>execute</code> will be invoked).
     *
     * @return the method to execute
     */
    String getMethod();

    /**
     * Gets status of the method value's initialization.
     *
     * @return true if the method returned by getMethod() is not a default initializer value.
     */
    boolean isMethodSpecified();
    
}
