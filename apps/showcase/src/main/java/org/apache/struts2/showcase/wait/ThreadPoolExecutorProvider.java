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
package org.apache.struts2.showcase.wait;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.apache.struts2.interceptor.exec.ExecutorProvider;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.LinkedBlockingDeque;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;

public class ThreadPoolExecutorProvider implements ExecutorProvider {

    private static final Logger LOG = LogManager.getLogger(ThreadPoolExecutorProvider.class);

    private final ExecutorService executor;

    public ThreadPoolExecutorProvider() {
        this.executor = new ThreadPoolExecutor(1, 2, 0L, TimeUnit.MILLISECONDS, new LinkedBlockingDeque<>());
    }

    @Override
    public void execute(Runnable task) {
        LOG.info("Executing task: {}", task);
        executor.execute(task);
    }

    @Override
    public boolean isShutdown() {
        return executor.isShutdown();
    }

    @Override
    public void shutdown() {
        LOG.info("Shutting down executor");
        executor.shutdown();
    }
}
