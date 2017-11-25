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

import com.opensymphony.xwork2.XWorkTestCase;
import org.apache.struts2.ServletActionContext;
import org.springframework.mock.web.MockAsyncContext;
import org.springframework.mock.web.MockHttpServletRequest;

import java.util.concurrent.Callable;
import java.util.concurrent.Semaphore;
import java.util.concurrent.TimeUnit;

public class DefaultAsyncManagerTest extends XWorkTestCase {
    public void testInvokeAsyncAction() throws Exception {
        final MockHttpServletRequest request = new MockHttpServletRequest();
        request.setAsyncSupported(true);

        ServletActionContext.setRequest(request);

        final Semaphore lock = new Semaphore(1);
        lock.acquire();

        AsyncAction asyncAction = new AsyncAction(new Callable() {
            @Override
            public Object call() throws Exception {
                final MockAsyncContext mockAsyncContext = (MockAsyncContext) request.getAsyncContext();
                mockAsyncContext.addDispatchHandler(new Runnable() {
                    @Override
                    public void run() {
                        mockAsyncContext.complete();
                        lock.release();
                    }
                });

                return "success";
            }
        });

        DefaultAsyncManager asyncManager = new DefaultAsyncManager();
        asyncManager.invokeAsyncAction(asyncAction);
        asyncManager.invokeAsyncAction(asyncAction);    // duplicate invoke should not raise any problem

        if (lock.tryAcquire(1500L, TimeUnit.MILLISECONDS)) {
            try {
                assertTrue("an async result is expected", asyncManager.hasAsyncActionResult());
                assertEquals("success", asyncManager.getAsyncActionResult());
            } finally {
                lock.release();
            }
        } else {
            lock.release();
            fail("async result did not received on timeout!");
        }
    }

    public void testInvokeAsyncActionException() throws Exception {
        final MockHttpServletRequest request = new MockHttpServletRequest();
        request.setAsyncSupported(true);

        ServletActionContext.setRequest(request);

        final Semaphore lock = new Semaphore(1);
        lock.acquire();

        final Exception expected = new Exception();
        AsyncAction asyncAction = new AsyncAction(new Callable() {
            @Override
            public Object call() throws Exception {
                final MockAsyncContext mockAsyncContext = (MockAsyncContext) request.getAsyncContext();
                mockAsyncContext.addDispatchHandler(new Runnable() {
                    @Override
                    public void run() {
                        mockAsyncContext.complete();
                        lock.release();
                    }
                });

                throw expected;
            }
        });

        DefaultAsyncManager asyncManager = new DefaultAsyncManager();
        asyncManager.invokeAsyncAction(asyncAction);

        if (lock.tryAcquire(1500L, TimeUnit.MILLISECONDS)) {
            try {
                assertTrue("an async result is expected", asyncManager.hasAsyncActionResult());
                assertEquals(expected, asyncManager.getAsyncActionResult());
            } finally {
                lock.release();
            }
        } else {
            fail("async result did not received on timeout!");
        }
    }
}
