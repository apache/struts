/*
 * $Id: DefaultActionSupport.java 651946 2008-04-27 13:41:38Z apetrelli $
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
package org.apache.struts2.dispatcher.filter;

import org.apache.struts2.dispatcher.Dispatcher;
import org.apache.struts2.StrutsException;
import com.opensymphony.xwork2.ActionContext;

/**
 * Contains cleanup operations in all stages for filters
 */
public class CleanupOperations {

    private Dispatcher dispatcher;

    public CleanupOperations(Dispatcher dispatcher) {
        this.dispatcher = dispatcher;
    }

    /**
     * Cleans up the dispatcher instance
     */
    public void cleanupDispatcher() {
        if (dispatcher == null) {
            throw new StrutsException("something is seriously wrong, Dispatcher is not initialized (null) ");
        } else {
            try {
                dispatcher.cleanup();
            } finally {
                ActionContext.setContext(null);
            }
        }
    }

    /**
     * Cleans up a request of thread locals
     */
    public void cleanupRequest() {
        // always dontClean up the thread request, even if an action hasn't been executed
        ActionContext.setContext(null);
        Dispatcher.setInstance(null);
    }

    /**
     * Cleans up after the initialization process
     */
    public void cleanupInit() {
        ActionContext.setContext(null);
    }

}
