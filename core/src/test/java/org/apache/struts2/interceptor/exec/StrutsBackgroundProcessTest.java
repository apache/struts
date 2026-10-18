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

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.security.SecureRandom;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Random;
import java.util.concurrent.Callable;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Semaphore;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.TimeoutException;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.concurrent.atomic.AtomicReference;

import ognl.OgnlContext;
import org.apache.struts2.StrutsInternalTestCase;
import org.apache.struts2.util.ValueStack;
import org.apache.struts2.util.ValueStackFactory;
import org.springframework.mock.web.MockHttpServletRequest;
import org.springframework.mock.web.MockHttpSession;

import org.apache.struts2.ActionContext;
import org.apache.struts2.ActionInvocation;
import org.apache.struts2.mock.MockActionInvocation;

import static org.awaitility.Awaitility.await;

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

        Thread.sleep(800);

        for (BackgroundProcess bp : bps) {
            assertTrue("Process is still active: " + bp, bp.isDone());
        }
        assertEquals(100, mutableState.get());
    }

    public void testErrorableProcesses1() {
        MockActionInvocationWithActionInvoker invocation = new MockActionInvocationWithActionInvoker(() -> {
            throw new IllegalStateException("boom");
        });

        BackgroundProcess bp = new ErrorableBackgroundProcess(invocation, null).prepare();
        executor.execute(bp);

        await().atLeast(100, TimeUnit.MILLISECONDS).until(bp::isDone);

        assertTrue("afterInvocation not called in case of exception", ((ErrorableBackgroundProcess) bp).isDoneAfter());
    }

    public void testErrorableProcesses2() {
        MockActionInvocationWithActionInvoker invocation = new MockActionInvocationWithActionInvoker(() -> "done");

        IllegalStateException expected = new IllegalStateException("after!");
        BackgroundProcess bp = new ErrorableBackgroundProcess(invocation, expected).prepare();
        executor.execute(bp);

        await().atLeast(100, TimeUnit.MILLISECONDS).until(bp::isDone);

        assertEquals(expected, bp.getException());
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

    public void testSandboxContextIsCopiedAtHandoverAndStaysAnOgnlContext() {
        ValueStack stack = container.getInstance(ValueStackFactory.class).createValueStack();
        ActionContext requestContext = ActionContext.of(stack.getContext()).withSession(new HashMap<>());
        AtomicReference<Map<String, Object>> seen = new AtomicReference<>();
        MockActionInvocationWithActionInvoker invocation = new MockActionInvocationWithActionInvoker(() -> {
            seen.set(ActionContext.getContext().getContextMap());
            return "done";
        });
        invocation.setInvocationContext(requestContext);
        SandboxedSessionMap sandbox = new SandboxedSessionMap(new MockHttpSession(), "copy");

        BackgroundProcess bp = new StrutsBackgroundProcess(invocation, "copy", Thread.NORM_PRIORITY)
                .withSessionSandbox(sandbox)
                .prepare();
        requestContext.put("afterHandover", "x");
        executor.execute(bp);
        await().atMost(5, TimeUnit.SECONDS).until(bp::isDone);

        assertNull(bp.getException());
        Map<String, Object> background = seen.get();
        assertTrue(background instanceof OgnlContext);
        assertNotSame(requestContext.getContextMap(), background);
        assertFalse(background.containsKey("afterHandover"));
        assertSame(sandbox, ActionContext.of(background).getSession());
        assertSame(stack, ActionContext.of(background).getValueStack());
        assertNotSame(sandbox, requestContext.getSession());
    }

    public void testSandboxHonoursTheContextBoundByBeforeInvocation() {
        AtomicReference<Map<String, Object>> seen = new AtomicReference<>();
        MockActionInvocationWithActionInvoker invocation = new MockActionInvocationWithActionInvoker(() -> {
            seen.set(ActionContext.getContext().getContextMap());
            return "done";
        });
        invocation.setInvocationContext(ActionContext.of());
        SandboxedSessionMap sandbox = new SandboxedSessionMap(new MockHttpSession(), "custom");

        BackgroundProcess bp = new StrutsBackgroundProcess(invocation, "custom", Thread.NORM_PRIORITY) {
            @Override
            protected void beforeInvocation() {
                ActionContext.bind(ActionContext.of().with("custom", "yes"));
            }
        }.withSessionSandbox(sandbox).prepare();
        executor.execute(bp);
        await().atMost(5, TimeUnit.SECONDS).until(bp::isDone);

        assertNull(bp.getException());
        assertEquals("yes", seen.get().get("custom"));
        assertSame(sandbox, ActionContext.of(seen.get()).getSession());
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
        @SuppressWarnings("unused")
        private final MockHttpServletRequest notSerializableField;

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
}

class ErrorableBackgroundProcess extends StrutsBackgroundProcess {

    private final Exception afterException;
    private boolean doneAfter;

    public ErrorableBackgroundProcess(ActionInvocation invocation, Exception afterException) {
        super(invocation, "errorabale process", Thread.NORM_PRIORITY);
        this.afterException = afterException;
    }

    @Override
    protected void afterInvocation() throws Exception {
        if (afterException != null) {
            throw afterException;
        } else {
            super.afterInvocation();
            doneAfter = true;
        }
    }

    public boolean isDoneAfter() {
        return doneAfter;
    }
}
