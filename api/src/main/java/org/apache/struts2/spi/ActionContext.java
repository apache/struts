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
package org.apache.struts2.spi;

import java.lang.reflect.Method;

/**
 * Context of an action execution.
 *
 * @author crazybob@google.com (Bob Lee)
 */
public interface ActionContext {

    /**
     * Gets action instance.
     */
    Object getAction();

    /**
     * Gets action method.
     */
    Method getMethod();

    /**
     * Gets action name.
     */
    String getActionName();

    /**
     * Gets the path for the action's namespace.
     */
    String getNamespacePath();

    /**
     * Gets the {@link Result} instance for the action.
     *
     * @return {@link Result} instance or {@code null} if we don't have a result yet.
     */
    Result getResult();

    /**
     * Adds a result interceptor for the action. Enables executing code before and after a result, executing an
     * alternate result, etc.
     */
    void addResultInterceptor(Result interceptor);

    /**
     * Gets context of action which chained to us.
     *
     * @return context of previous action or {@code null} if this is the first action in the chain
     */
    ActionContext getPrevious();

    /**
     * Gets context of action which this action chained to.
     *
     * @return context of next action or {@code null} if we haven't chained to another action yet or this is the last
     *  action in the chain.
     */
    ActionContext getNext();
}
