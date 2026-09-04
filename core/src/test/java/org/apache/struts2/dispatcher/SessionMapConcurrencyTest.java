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
package org.apache.struts2.dispatcher;

import org.junit.Test;
import org.springframework.mock.web.MockHttpServletRequest;
import org.springframework.mock.web.MockHttpSession;

import java.util.concurrent.CountDownLatch;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.concurrent.atomic.AtomicReference;

import static org.assertj.core.api.Assertions.assertThat;

/**
 * Concurrent tests for SessionMap to verify thread-safety (WW-3576).
 * <p>
 * These tests verify that the double-check locking pattern with volatile
 * correctly prevents race conditions between null checks and synchronized blocks.
 */
public class SessionMapConcurrencyTest {

    /**
     * Tests that concurrent invalidate() calls do not cause NullPointerException.
     * This was the primary issue reported in WW-3576.
     */
    @Test
    public void concurrentInvalidateDoesNotThrowNPE() throws InterruptedException {
        int threadCount = 10;
        int iterations = 100;
        AtomicReference<Throwable> error = new AtomicReference<>();

        for (int i = 0; i < iterations; i++) {
            MockHttpSession session = new MockHttpSession();
            MockHttpServletRequest req = new MockHttpServletRequest();
            req.setSession(session);
            SessionMap sessionMap = new SessionMap(req);

            CountDownLatch startLatch = new CountDownLatch(1);
            CountDownLatch doneLatch = new CountDownLatch(threadCount);

            for (int t = 0; t < threadCount; t++) {
                new Thread(() -> {
                    try {
                        startLatch.await();
                        sessionMap.invalidate();
                    } catch (Throwable e) {
                        error.compareAndSet(null, e);
                    } finally {
                        doneLatch.countDown();
                    }
                }).start();
            }

            startLatch.countDown();
            assertThat(doneLatch.await(5, TimeUnit.SECONDS))
                    .as("All threads should complete within timeout")
                    .isTrue();

            if (error.get() != null) {
                throw new AssertionError("Concurrent invalidate() threw exception", error.get());
            }
        }
    }

    /**
     * Tests that concurrent get() and invalidate() do not cause NullPointerException.
     */
    @Test
    public void concurrentGetAndInvalidateDoesNotThrowNPE() throws InterruptedException {
        int iterations = 100;
        AtomicReference<Throwable> error = new AtomicReference<>();

        for (int i = 0; i < iterations; i++) {
            MockHttpSession session = new MockHttpSession();
            session.setAttribute("key", "value");
            MockHttpServletRequest req = new MockHttpServletRequest();
            req.setSession(session);
            SessionMap sessionMap = new SessionMap(req);

            CountDownLatch startLatch = new CountDownLatch(1);
            CountDownLatch doneLatch = new CountDownLatch(2);

            // Thread 1: repeatedly calls get()
            new Thread(() -> {
                try {
                    startLatch.await();
                    for (int j = 0; j < 100; j++) {
                        sessionMap.get("key");
                    }
                } catch (Throwable e) {
                    error.compareAndSet(null, e);
                } finally {
                    doneLatch.countDown();
                }
            }).start();

            // Thread 2: calls invalidate()
            new Thread(() -> {
                try {
                    startLatch.await();
                    sessionMap.invalidate();
                } catch (Throwable e) {
                    error.compareAndSet(null, e);
                } finally {
                    doneLatch.countDown();
                }
            }).start();

            startLatch.countDown();
            assertThat(doneLatch.await(5, TimeUnit.SECONDS))
                    .as("All threads should complete within timeout")
                    .isTrue();

            if (error.get() != null) {
                throw new AssertionError("Concurrent get/invalidate threw exception", error.get());
            }
        }
    }

    /**
     * Tests that concurrent put() and invalidate() do not cause NullPointerException.
     * Note: IllegalStateException is acceptable as the session may be invalidated.
     */
    @Test
    public void concurrentPutAndInvalidateDoesNotThrowNPE() throws InterruptedException {
        int iterations = 100;
        AtomicReference<Throwable> error = new AtomicReference<>();

        for (int i = 0; i < iterations; i++) {
            MockHttpSession session = new MockHttpSession();
            MockHttpServletRequest req = new MockHttpServletRequest();
            req.setSession(session);
            SessionMap sessionMap = new SessionMap(req);

            CountDownLatch startLatch = new CountDownLatch(1);
            CountDownLatch doneLatch = new CountDownLatch(2);

            // Thread 1: repeatedly calls put()
            new Thread(() -> {
                try {
                    startLatch.await();
                    for (int j = 0; j < 100; j++) {
                        sessionMap.put("key" + j, "value" + j);
                    }
                } catch (IllegalStateException e) {
                    // Expected: session was invalidated concurrently
                } catch (Throwable e) {
                    error.compareAndSet(null, e);
                } finally {
                    doneLatch.countDown();
                }
            }).start();

            // Thread 2: calls invalidate()
            new Thread(() -> {
                try {
                    startLatch.await();
                    Thread.sleep(1); // slight delay to let put() start
                    sessionMap.invalidate();
                } catch (Throwable e) {
                    error.compareAndSet(null, e);
                } finally {
                    doneLatch.countDown();
                }
            }).start();

            startLatch.countDown();
            assertThat(doneLatch.await(5, TimeUnit.SECONDS))
                    .as("All threads should complete within timeout")
                    .isTrue();

            if (error.get() != null) {
                throw new AssertionError("Concurrent put/invalidate threw exception", error.get());
            }
        }
    }

    /**
     * Tests that concurrent clear() and invalidate() do not cause NullPointerException.
     */
    @Test
    public void concurrentClearAndInvalidateDoesNotThrowNPE() throws InterruptedException {
        int iterations = 100;
        AtomicReference<Throwable> error = new AtomicReference<>();

        for (int i = 0; i < iterations; i++) {
            MockHttpSession session = new MockHttpSession();
            session.setAttribute("key1", "value1");
            session.setAttribute("key2", "value2");
            MockHttpServletRequest req = new MockHttpServletRequest();
            req.setSession(session);
            SessionMap sessionMap = new SessionMap(req);

            CountDownLatch startLatch = new CountDownLatch(1);
            CountDownLatch doneLatch = new CountDownLatch(2);

            // Thread 1: calls clear()
            new Thread(() -> {
                try {
                    startLatch.await();
                    sessionMap.clear();
                } catch (Throwable e) {
                    error.compareAndSet(null, e);
                } finally {
                    doneLatch.countDown();
                }
            }).start();

            // Thread 2: calls invalidate()
            new Thread(() -> {
                try {
                    startLatch.await();
                    sessionMap.invalidate();
                } catch (Throwable e) {
                    error.compareAndSet(null, e);
                } finally {
                    doneLatch.countDown();
                }
            }).start();

            startLatch.countDown();
            assertThat(doneLatch.await(5, TimeUnit.SECONDS))
                    .as("All threads should complete within timeout")
                    .isTrue();

            if (error.get() != null) {
                throw new AssertionError("Concurrent clear/invalidate threw exception", error.get());
            }
        }
    }

    /**
     * Tests that concurrent remove() and invalidate() do not cause NullPointerException.
     */
    @Test
    public void concurrentRemoveAndInvalidateDoesNotThrowNPE() throws InterruptedException {
        int iterations = 100;
        AtomicReference<Throwable> error = new AtomicReference<>();

        for (int i = 0; i < iterations; i++) {
            MockHttpSession session = new MockHttpSession();
            session.setAttribute("key", "value");
            MockHttpServletRequest req = new MockHttpServletRequest();
            req.setSession(session);
            SessionMap sessionMap = new SessionMap(req);

            CountDownLatch startLatch = new CountDownLatch(1);
            CountDownLatch doneLatch = new CountDownLatch(2);

            // Thread 1: calls remove()
            new Thread(() -> {
                try {
                    startLatch.await();
                    sessionMap.remove("key");
                } catch (Throwable e) {
                    error.compareAndSet(null, e);
                } finally {
                    doneLatch.countDown();
                }
            }).start();

            // Thread 2: calls invalidate()
            new Thread(() -> {
                try {
                    startLatch.await();
                    sessionMap.invalidate();
                } catch (Throwable e) {
                    error.compareAndSet(null, e);
                } finally {
                    doneLatch.countDown();
                }
            }).start();

            startLatch.countDown();
            assertThat(doneLatch.await(5, TimeUnit.SECONDS))
                    .as("All threads should complete within timeout")
                    .isTrue();

            if (error.get() != null) {
                throw new AssertionError("Concurrent remove/invalidate threw exception", error.get());
            }
        }
    }

    /**
     * Tests that concurrent containsKey() and invalidate() do not cause NullPointerException.
     */
    @Test
    public void concurrentContainsKeyAndInvalidateDoesNotThrowNPE() throws InterruptedException {
        int iterations = 100;
        AtomicReference<Throwable> error = new AtomicReference<>();

        for (int i = 0; i < iterations; i++) {
            MockHttpSession session = new MockHttpSession();
            session.setAttribute("key", "value");
            MockHttpServletRequest req = new MockHttpServletRequest();
            req.setSession(session);
            SessionMap sessionMap = new SessionMap(req);

            CountDownLatch startLatch = new CountDownLatch(1);
            CountDownLatch doneLatch = new CountDownLatch(2);

            // Thread 1: repeatedly calls containsKey()
            new Thread(() -> {
                try {
                    startLatch.await();
                    for (int j = 0; j < 100; j++) {
                        sessionMap.containsKey("key");
                    }
                } catch (Throwable e) {
                    error.compareAndSet(null, e);
                } finally {
                    doneLatch.countDown();
                }
            }).start();

            // Thread 2: calls invalidate()
            new Thread(() -> {
                try {
                    startLatch.await();
                    sessionMap.invalidate();
                } catch (Throwable e) {
                    error.compareAndSet(null, e);
                } finally {
                    doneLatch.countDown();
                }
            }).start();

            startLatch.countDown();
            assertThat(doneLatch.await(5, TimeUnit.SECONDS))
                    .as("All threads should complete within timeout")
                    .isTrue();

            if (error.get() != null) {
                throw new AssertionError("Concurrent containsKey/invalidate threw exception", error.get());
            }
        }
    }

    /**
     * Tests that concurrent entrySet() and invalidate() do not cause NullPointerException.
     */
    @Test
    public void concurrentEntrySetAndInvalidateDoesNotThrowNPE() throws InterruptedException {
        int iterations = 100;
        AtomicReference<Throwable> error = new AtomicReference<>();

        for (int i = 0; i < iterations; i++) {
            MockHttpSession session = new MockHttpSession();
            session.setAttribute("key", "value");
            MockHttpServletRequest req = new MockHttpServletRequest();
            req.setSession(session);
            SessionMap sessionMap = new SessionMap(req);

            CountDownLatch startLatch = new CountDownLatch(1);
            CountDownLatch doneLatch = new CountDownLatch(2);

            // Thread 1: repeatedly calls entrySet()
            new Thread(() -> {
                try {
                    startLatch.await();
                    for (int j = 0; j < 100; j++) {
                        sessionMap.entrySet();
                    }
                } catch (Throwable e) {
                    error.compareAndSet(null, e);
                } finally {
                    doneLatch.countDown();
                }
            }).start();

            // Thread 2: calls invalidate()
            new Thread(() -> {
                try {
                    startLatch.await();
                    sessionMap.invalidate();
                } catch (Throwable e) {
                    error.compareAndSet(null, e);
                } finally {
                    doneLatch.countDown();
                }
            }).start();

            startLatch.countDown();
            assertThat(doneLatch.await(5, TimeUnit.SECONDS))
                    .as("All threads should complete within timeout")
                    .isTrue();

            if (error.get() != null) {
                throw new AssertionError("Concurrent entrySet/invalidate threw exception", error.get());
            }
        }
    }

    /**
     * Stress test with multiple operations happening concurrently.
     * Note: IllegalStateException is acceptable as the session may be invalidated by clear().
     */
    @Test
    public void stressTestMixedOperations() throws InterruptedException {
        int threadCount = 20;
        int operationsPerThread = 50;
        AtomicReference<Throwable> error = new AtomicReference<>();
        AtomicInteger completedOperations = new AtomicInteger(0);

        MockHttpSession session = new MockHttpSession();
        MockHttpServletRequest req = new MockHttpServletRequest();
        req.setSession(session);
        SessionMap sessionMap = new SessionMap(req);

        ExecutorService executor = Executors.newFixedThreadPool(threadCount);
        CountDownLatch startLatch = new CountDownLatch(1);
        CountDownLatch doneLatch = new CountDownLatch(threadCount);

        for (int t = 0; t < threadCount; t++) {
            final int threadId = t;
            executor.submit(() -> {
                try {
                    startLatch.await();
                    for (int j = 0; j < operationsPerThread; j++) {
                        int operation = (threadId + j) % 6;
                        String key = "key" + threadId + "_" + j;
                        try {
                            switch (operation) {
                                case 0 -> sessionMap.put(key, "value");
                                case 1 -> sessionMap.get(key);
                                case 2 -> sessionMap.remove(key);
                                case 3 -> sessionMap.containsKey(key);
                                case 4 -> sessionMap.entrySet();
                                case 5 -> sessionMap.clear();
                            }
                        } catch (IllegalStateException e) {
                            // Expected: session may be invalidated
                        }
                        completedOperations.incrementAndGet();
                    }
                } catch (Throwable e) {
                    error.compareAndSet(null, e);
                } finally {
                    doneLatch.countDown();
                }
            });
        }

        startLatch.countDown();
        boolean finished = doneLatch.await(30, TimeUnit.SECONDS);
        executor.shutdownNow();

        assertThat(finished).as("All threads should complete within timeout").isTrue();
        if (error.get() != null) {
            throw new AssertionError("Stress test threw exception after " + completedOperations.get() + " operations", error.get());
        }
    }
}
