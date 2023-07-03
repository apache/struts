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

import com.opensymphony.xwork2.ActionContext;
import com.opensymphony.xwork2.ActionInvocation;
import com.opensymphony.xwork2.mock.MockActionInvocation;
import org.apache.struts2.StrutsInternalTestCase;
import org.springframework.mock.web.MockHttpServletRequest;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.security.SecureRandom;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;
import java.util.concurrent.Callable;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Semaphore;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.TimeoutException;
import java.util.concurrent.atomic.AtomicInteger;

/**
 * Test case for BackgroundProcessTest.
 */
public class StrutsBackgroundProcessTest extends StrutsInternalTestCase {

    private final ExecutorService executor = Executors.newSingleThreadExecutor();

    public void testSerializeDeserialize() throws Exception {
        final NotSerializableException expectedException = new NotSerializableException(new MockHttpServletRequest());
        final Semaphore lock = new Semaphore(1);
        lock.acquire();
        MockActionInvocationWithActionInvoker invocation = new MockActionInvocationWithActionInvoker(() -> {
            lock.release();
            throw expectedException;
        });
        invocation.setInvocationContext(ActionContext.getContext());

        StrutsBackgroundProcess bp = (StrutsBackgroundProcess) new StrutsBackgroundProcess(
            invocation,
            "BackgroundProcessTest.testSerializeDeserialize",
            Thread.MIN_PRIORITY
        ).prepare();
        executor.execute(bp);

        if (!lock.tryAcquire(1500L, TimeUnit.MILLISECONDS)) {
            lock.release();
            fail("background thread did not release lock on timeout");
        }
        lock.release();

        bp.result = "BackgroundProcessTest.testSerializeDeserialize";
        bp.done = true;
        Thread.sleep(1000);//give a chance to background thread to set exception
        assertEquals(expectedException, bp.exception);

        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        ObjectOutputStream oos = new ObjectOutputStream(baos);
        oos.writeObject(bp);
        oos.close();
        byte[] b = baos.toByteArray();
        baos.close();

        ByteArrayInputStream bais = new ByteArrayInputStream(b);
        ObjectInputStream ois = new ObjectInputStream(bais);
        StrutsBackgroundProcess deserializedBp = (StrutsBackgroundProcess) ois.readObject();
        ois.close();
        bais.close();

        assertNull("invocation should not be serialized", deserializedBp.invocation);
        assertNull("exception should not be serialized", deserializedBp.exception);
        assertEquals(bp.result, deserializedBp.result);
        assertEquals(bp.done, deserializedBp.done);
    }

    public void testMultipleProcesses() throws InterruptedException {
        Random random = new SecureRandom();
        AtomicInteger mutableState = new AtomicInteger(0);
        MockActionInvocationWithActionInvoker invocation = new MockActionInvocationWithActionInvoker(() -> {
            Thread.sleep(Math.max(50, random.nextInt(150)));
            mutableState.getAndIncrement();
            return "done";
        });

        List<BackgroundProcess> bps = new ArrayList<>();
        for (int i = 0; i < 100; i++) {
            String name = String.format("Order: %s", i);
            BackgroundProcess bp = new LockBackgroundProcess(invocation, name).prepare();
            bps.add(bp);
            executor.execute(bp);
        }

        Thread.sleep(400);

        for (BackgroundProcess bp : bps) {
            assertTrue("Process is still active: " + bp, bp.isDone());
        }
        assertEquals(100, mutableState.get());
    }

    public void testUnpreparedProcess() throws ExecutionException, InterruptedException, TimeoutException {
        // given
        MockActionInvocationWithActionInvoker invocation = new MockActionInvocationWithActionInvoker(() -> "done");
        BackgroundProcess bp = new StrutsBackgroundProcess(invocation, "Unprepared", Thread.NORM_PRIORITY);

        // when
        executor.submit(bp).get(1000, TimeUnit.MILLISECONDS);

        // then
        assertTrue(bp.isDone());
        assertEquals("Background thread Unprepared has not been prepared!", bp.getException().getMessage());
    }

    private static class MockActionInvocationWithActionInvoker extends MockActionInvocation {
        private final Callable<String> actionInvoker;

        MockActionInvocationWithActionInvoker(Callable<String> actionInvoker) {
            this.actionInvoker = actionInvoker;
        }

        @Override
        public String invokeActionOnly() throws Exception {
            return actionInvoker.call();
        }
    }

    private static class NotSerializableException extends Exception {
        private MockHttpServletRequest notSerializableField;

        NotSerializableException(MockHttpServletRequest notSerializableField) {
            this.notSerializableField = notSerializableField;
        }
    }

}

class LockBackgroundProcess extends StrutsBackgroundProcess {

    private final Object lock = LockBackgroundProcess.class;

    public LockBackgroundProcess(ActionInvocation invocation, String name) {
        super(invocation, name, Thread.NORM_PRIORITY);
    }

    @Override
    public void run() {
        synchronized (lock) {
            super.run();
        }
    }

    @Override
    protected void afterInvocation() throws Exception {
        super.afterInvocation();
        lock.notify();
    }
}
