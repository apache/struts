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

import java.util.concurrent.Callable;
import java.util.concurrent.Executor;

/**
 * A {@link Callable} with a timeout value and an {@link Executor}.
 *
 * @since 2.6
 */
public class AsyncAction implements Callable {

    /**
     * The action invocation was successful but did not return the result before timeout.
     */
    public static final String TIMEOUT = "timeout";

    private Callable callable;
    private Long timeout;
    private Executor executor;

    public AsyncAction(Callable callable) {
        this.callable = callable;
    }

    public AsyncAction(long timeout, Callable callable) {
        this(callable);
        this.timeout = timeout;
    }

    public AsyncAction(Executor executor, Callable callable) {
        this(callable);
        this.executor = executor;
    }

    public AsyncAction(long timeout, Executor executor, Callable callable) {
        this(timeout, callable);
        this.executor = executor;
    }

    public Long getTimeout() {
        return timeout;
    }

    public Executor getExecutor() {
        return executor;
    }

    @Override
    public Object call() throws Exception {
        return callable.call();
    }
}
