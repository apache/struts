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
package org.apache.struts2.async;

import com.opensymphony.xwork2.AsyncManager;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.apache.struts2.ServletActionContext;

import javax.servlet.AsyncContext;
import javax.servlet.AsyncEvent;
import javax.servlet.AsyncListener;
import javax.servlet.http.HttpServletRequest;
import java.io.IOException;
import java.util.concurrent.Callable;
import java.util.concurrent.Executor;
import java.util.concurrent.atomic.AtomicInteger;

/**
 * Implements {@link AsyncManager} to add support for invoke async actions via Servlet 3's API.
 *
 * @since 2.6
 */
public class DefaultAsyncManager implements AsyncManager, AsyncListener {
    private static final Logger LOG = LogManager.getLogger(DefaultAsyncManager.class);
    private static final AtomicInteger threadCount = new AtomicInteger(0);

    private AsyncContext asyncContext;
    private boolean asyncActionStarted;
    private Boolean asyncCompleted;
    private Object asyncActionResult;

    @Override
    public void invokeAsyncAction(final Callable asyncAction) {
        if (asyncActionStarted) {
            return;
        }

        Long timeout = null;
        Executor executor = null;
        if (asyncAction instanceof AsyncAction) {
            AsyncAction customAsyncAction = (AsyncAction) asyncAction;
            timeout = customAsyncAction.getTimeout();
            executor = customAsyncAction.getExecutor();
        }

        HttpServletRequest req = ServletActionContext.getRequest();
        asyncActionResult = null;
        asyncCompleted = false;

        if (asyncContext == null || !req.isAsyncStarted()) {
            asyncContext = req.startAsync(req, ServletActionContext.getResponse());
            asyncContext.addListener(this);
            if (timeout != null) {
                asyncContext.setTimeout(timeout);
            }
        }
        asyncActionStarted = true;
        LOG.debug("Async processing started for " + asyncContext);

        final Runnable task = new Runnable() {
            @Override
            public void run() {
                try {
                    setAsyncActionResultAndDispatch(asyncAction.call());
                } catch (Throwable e) {
                    setAsyncActionResultAndDispatch(e);
                }
            }
        };
        if (executor != null) {
            executor.execute(task);
        } else {
            final Thread thread = new Thread(new Runnable() {
                @Override
                public void run() {
                    try {
                        task.run();
                    } finally {
                        threadCount.decrementAndGet();
                    }
                }
            }, this.getClass().getSimpleName() + "-" + threadCount.incrementAndGet());
            thread.start();
        }
    }

    private void setAsyncActionResultAndDispatch(Object asyncActionResult) {
        this.asyncActionResult = asyncActionResult;

        String log = "Async result [" + asyncActionResult + "] of " + asyncContext;
        if (asyncCompleted) {
            LOG.debug(log + " - could not complete result executing due to timeout or network error");
        } else {
            LOG.debug(log + " - dispatching request to execute result in container");
            asyncContext.dispatch();
        }
    }

    @Override
    public boolean hasAsyncActionResult() {
        return asyncActionResult != null;
    }

    @Override
    public Object getAsyncActionResult() {
        return asyncActionResult;
    }

    @Override
    public void onComplete(AsyncEvent asyncEvent) throws IOException {
        asyncContext = null;
        asyncCompleted = true;
    }

    @Override
    public void onTimeout(AsyncEvent asyncEvent) throws IOException {
        LOG.debug("Processing timeout for " + asyncEvent.getAsyncContext());
        setAsyncActionResultAndDispatch(AsyncAction.TIMEOUT);
    }

    @Override
    public void onError(AsyncEvent asyncEvent) throws IOException {
        Throwable e = asyncEvent.getThrowable();
        LOG.error("Processing error for " + asyncEvent.getAsyncContext(), e);
        setAsyncActionResultAndDispatch(e);
    }

    @Override
    public void onStartAsync(AsyncEvent asyncEvent) throws IOException {

    }
}
