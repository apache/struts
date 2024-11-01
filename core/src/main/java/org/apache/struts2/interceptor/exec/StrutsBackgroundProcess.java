/*
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
package org.apache.struts2.interceptor.exec;

import org.apache.struts2.ActionContext;
import org.apache.struts2.ActionInvocation;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.io.Serial;
import java.io.Serializable;

/**
 * Background thread to be executed by the ExecuteAndWaitInterceptor.
 */
public class StrutsBackgroundProcess implements BackgroundProcess, Serializable {

    @Serial
    private static final long serialVersionUID = 3884464776311686443L;

    private static final Logger LOG = LogManager.getLogger(StrutsBackgroundProcess.class);

    private final String threadName;
    private final int threadPriority;

    private transient Thread processThread;
    //WW-4900 transient since 2.5.15
    protected transient ActionInvocation invocation;
    protected transient Exception exception;

    protected String result;
    protected boolean done;

    /**
     * Constructs a background process
     *
     * @param invocation     The action invocation
     * @param threadName     The name of background thread
     * @param threadPriority The priority of background thread
     */
    public StrutsBackgroundProcess(ActionInvocation invocation, String threadName, int threadPriority) {
        this.invocation = invocation;
        this.threadName = threadName;
        this.threadPriority = threadPriority;
    }

    @Override
    public BackgroundProcess prepare() {
        try {
            processThread = new Thread(() -> {
                try {
                    beforeInvocation();
                    result = invocation.invokeActionOnly();
                } catch (Exception e) {
                    LOG.warn("Exception during invokeActionOnly() execution", e);
                    exception = e;
                } finally {
                    try {
                        afterInvocation();
                    } catch (Exception ex) {
                        if (exception == null) {
                            exception = ex;
                        }
                        LOG.warn("Exception during afterInvocation() execution", ex);
                    }
                    done = true;
                }
            });
            processThread.setName(threadName);
            processThread.setPriority(threadPriority);
        } catch (Exception e) {
            done = true;
            exception = e;
        }
        return this;
    }

    @Override
    public void run() {
        if (processThread == null) {
            done = true;
            exception = new IllegalStateException("Background thread " + threadName + " has not been prepared!");
            return;
        }
        processThread.start();
    }

    /**
     * Called before the background thread determines the result code
     * from the ActionInvocation.
     *
     * @throws Exception any exception thrown will be thrown, in turn, by the ExecuteAndWaitInterceptor
     */
    protected void beforeInvocation() throws Exception {
        ActionContext.bind(invocation.getInvocationContext());
    }

    /**
     * Called after the background thread determines the result code
     * from the ActionInvocation, but before the background thread is
     * marked as done.
     *
     * @throws Exception any exception thrown will be thrown, in turn, by the ExecuteAndWaitInterceptor
     */
    protected void afterInvocation() throws Exception {
        ActionContext.clear();
    }

    /**
     * Retrieves the action.
     *
     * @return the action.
     */
    @Override
    public Object getAction() {
        return invocation.getAction();
    }

    /**
     * Retrieves the action invocation.
     *
     * @return the action invocation
     */
    @Override
    public ActionInvocation getInvocation() {
        return invocation;
    }

    /**
     * Gets the result of the background process.
     *
     * @return the result; <tt>null</tt> if not done.
     */
    @Override
    public String getResult() {
        return result;
    }

    /**
     * Gets the exception if any was thrown during the execution of the background process.
     *
     * @return the exception or <tt>null</tt> if no exception was thrown.
     */
    @Override
    public Exception getException() {
        return exception;
    }

    /**
     * Returns the status of the background process.
     *
     * @return <tt>true</tt> if finished, <tt>false</tt> otherwise
     */
    @Override
    public boolean isDone() {
        return done;
    }

    @Override
    public String toString() {
        return "StrutsBackgroundProcess { name = " + processThread.getName() + " }";
    }
}
