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

import java.util.Map;


/**
 * The {@link ActionProxyFactory} is used to create {@link ActionProxy}s to be executed.
 * <p/>
 * It is the entry point to XWork that is used by a dispatcher to create an {@link ActionProxy} to execute
 * for a particular namespace and action name.
 *
 * @author Jason Carreira
 * @see DefaultActionProxyFactory
 */
public interface ActionProxyFactory {

    /**
     * Creates an {@link ActionProxy} for the given namespace and action name by looking up the configuration.The ActionProxy
     * should be fully initialized when it is returned, including having an {@link ActionInvocation} instance associated.
     * <p/>
     * <b>Note:</b> This is the most used create method.
     *
     * @param namespace    the namespace of the action, can be <tt>null</tt>
     * @param actionName   the name of the action
     * @param extraContext a Map of extra parameters to be provided to the ActionProxy, can be <tt>null</tt>
     * @return ActionProxy  the created action proxy
     * @deprecated Since 2.1.1, use {@link #createActionProxy(String,String,String,Map) instead}
     */
    @Deprecated public ActionProxy createActionProxy(String namespace, String actionName, Map<String, Object> extraContext);

    /**
     * Creates an {@link ActionProxy} for the given namespace and action name by looking up the configuration.The ActionProxy
     * should be fully initialized when it is returned, including having an {@link ActionInvocation} instance associated.
     * <p/>
     * <b>Note:</b> This is the most used create method.
     *
     * @param namespace    the namespace of the action, can be <tt>null</tt>
     * @param actionName   the name of the action
     * @param methodName   the name of the method to execute
     * @param extraContext a Map of extra parameters to be provided to the ActionProxy, can be <tt>null</tt>
     * @return ActionProxy  the created action proxy
     * @since 2.1.1
     */
    public ActionProxy createActionProxy(String namespace, String actionName, String methodName, Map<String, Object> extraContext);

    /**
     * Creates an {@link ActionProxy} for the given namespace and action name by looking up the configuration.The ActionProxy
     * should be fully initialized when it is returned, including having an {@link ActionInvocation} instance associated.
     *
     * @param namespace    the namespace of the action, can be <tt>null</tt>
     * @param actionName   the name of the action
     * @param extraContext a Map of extra parameters to be provided to the ActionProxy, can be <tt>null</tt>
     * @param executeResult flag which tells whether the result should be executed after the action
     * @param cleanupContext flag which tells whether the original context should be preserved during execution of the proxy.
     * @return ActionProxy  the created action proxy
     * @deprecated Since 2.1.1, use {@link #createActionProxy(String,String,String,Map,boolean,boolean)} instead
     */
    @Deprecated public ActionProxy createActionProxy(String namespace, String actionName, Map<String, Object> extraContext, boolean executeResult, boolean cleanupContext);

    /**
     * Creates an {@link ActionProxy} for the given namespace and action name by looking up the configuration.The ActionProxy
     * should be fully initialized when it is returned, including having an {@link ActionInvocation} instance associated.
     *
     * @param namespace    the namespace of the action, can be <tt>null</tt>
     * @param actionName   the name of the action
     * @param methodName   the name of the method to execute
     * @param extraContext a Map of extra parameters to be provided to the ActionProxy, can be <tt>null</tt>
     * @param executeResult flag which tells whether the result should be executed after the action
     * @param cleanupContext flag which tells whether the original context should be preserved during execution of the proxy.
     * @return ActionProxy  the created action proxy
     * @since 2.1.1
     */
    public ActionProxy createActionProxy(String namespace, String actionName, String methodName, Map<String, Object> extraContext, boolean executeResult, boolean cleanupContext);


     /**
     * Creates an {@link ActionProxy} for the given namespace and action name by looking up the configuration.The ActionProxy
     * should be fully initialized when it is returned, including passed {@link ActionInvocation} instance.
     *
     * @param actionInvocation the action invocation instance to associate with
     * @param namespace    the namespace of the action, can be <tt>null</tt>
     * @param actionName   the name of the action
     * @param methodName   the name of the method to execute
     * @param executeResult flag which tells whether the result should be executed after the action
     * @param cleanupContext flag which tells whether the original context should be preserved during execution of the proxy.
     * @return ActionProxy  the created action proxy
     * @since 2.1.1
     */
    public ActionProxy createActionProxy(ActionInvocation actionInvocation, String namespace, String actionName, String methodName,
                                         boolean executeResult, boolean cleanupContext);
    
}
