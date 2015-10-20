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

package org.apache.struts2.interceptor;

import java.io.Serializable;

import com.opensymphony.xwork2.ActionContext;
import com.opensymphony.xwork2.ActionInvocation;

/**
 * Background thread to be executed by the ExecuteAndWaitInterceptor.
 *
 */
public class BackgroundProcess implements Serializable {

    private static final long serialVersionUID = 3884464776311686443L;

    protected Object action;
    protected ActionInvocation invocation;
    protected String result;
    protected Exception exception;
    protected boolean done;

    /**
     * Constructs a background process
     *
     * @param threadName The thread name
     * @param invocation The action invocation
     * @param threadPriority The thread priority
     */
    public BackgroundProcess(String threadName, final ActionInvocation invocation, int threadPriority) {
        this.invocation = invocation;
        this.action = invocation.getAction();
        try {
            final Thread t = new Thread(new Runnable() {
                public void run() {
                    try {
                        beforeInvocation();
                        result = invocation.invokeActionOnly();
                        afterInvocation();
                    } catch (Exception e) {
                        exception = e;
                    }

                    done = true;
                }
            });
            t.setName(threadName);
            t.setPriority(threadPriority);
            t.start();
        } catch (Exception e) {
            exception = e;
        }
    }

    /**
     * Called before the background thread determines the result code
     * from the ActionInvocation.
     *
     * @throws Exception any exception thrown will be thrown, in turn, by the ExecuteAndWaitInterceptor
     */
    protected void beforeInvocation() throws Exception {
        ActionContext.setContext(invocation.getInvocationContext());
    }

    /**
     * Called after the background thread determines the result code
     * from the ActionInvocation, but before the background thread is
     * marked as done.
     *
     * @throws Exception any exception thrown will be thrown, in turn, by the ExecuteAndWaitInterceptor
     */
    protected void afterInvocation() throws Exception {
        ActionContext.setContext(null);
    }

    /**
     * Retrieves the action.
     *
     * @return  the action.
     */
    public Object getAction() {
        return action;
    }

    /**
     * Retrieves the action invocation.
     *
     * @return the action invocation
     */
    public ActionInvocation getInvocation() {
        return invocation;
    }

    /**
     * Gets the result of the background process.
     *
     * @return  the result; <tt>null</tt> if not done.
     */
    public String getResult() {
        return result;
    }

    /**
     * Gets the exception if any was thrown during the execution of the background process.
     *
     * @return the exception or <tt>null</tt> if no exception was thrown.
     */
    public Exception getException() {
        return exception;
    }

    /**
     * Returns the status of the background process.
     *
     * @return <tt>true</tt> if finished, <tt>false</tt> otherwise
     */
    public boolean isDone() {
        return done;
    }
}
