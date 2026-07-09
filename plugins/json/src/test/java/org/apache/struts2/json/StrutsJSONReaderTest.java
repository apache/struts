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
package org.apache.struts2.json;

import org.junit.Test;

import java.util.List;
import java.util.Map;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicInteger;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertThrows;
import static org.junit.Assert.assertTrue;

public class StrutsJSONReaderTest {

    @Test
    public void testArrayExceedingMaxElementsThrows() {
        var reader = new StrutsJSONReader();
        reader.setMaxElements(3);
        String json = "[1, 2, 3, 4]";
        var ex = assertThrows(JSONException.class, () -> reader.read(json));
        assertTrue(ex.getMessage().contains("maximum allowed elements (3)"));
        assertTrue(ex.getMessage().contains(JSONConstants.JSON_MAX_ELEMENTS));
    }

    @Test
    public void testArrayAtExactMaxElementsAllowed() throws Exception {
        var reader = new StrutsJSONReader();
        reader.setMaxElements(3);
        // Exactly 3 elements: check is >= before add, so 3 elements fit (size 0,1,2 when checked)
        var result = reader.read("[1, 2, 3]");
        assertNotNull(result);
        assertEquals(3, ((List<?>) result).size());
    }

    @Test
    public void testObjectExceedingMaxElementsThrows() {
        var reader = new StrutsJSONReader();
        reader.setMaxElements(2);
        String json = "{\"a\":1, \"b\":2, \"c\":3}";
        var ex = assertThrows(JSONException.class, () -> reader.read(json));
        assertTrue(ex.getMessage().contains("maximum allowed elements (2)"));
    }

    @Test
    public void testNestingExceedingMaxDepthThrows() {
        var reader = new StrutsJSONReader();
        reader.setMaxDepth(2);
        String json = "{\"a\":{\"b\":{\"c\":1}}}";
        var ex = assertThrows(JSONException.class, () -> reader.read(json));
        assertTrue(ex.getMessage().contains("maximum allowed depth (2)"));
        assertTrue(ex.getMessage().contains(JSONConstants.JSON_MAX_DEPTH));
    }

    @Test
    public void testArrayNestingExceedingMaxDepthThrows() {
        var reader = new StrutsJSONReader();
        reader.setMaxDepth(2);
        String json = "[[[1]]]";
        var ex = assertThrows(JSONException.class, () -> reader.read(json));
        assertTrue(ex.getMessage().contains("maximum allowed depth (2)"));
    }

    @Test
    public void testStringAtExactMaxStringLengthAllowed() throws Exception {
        var reader = new StrutsJSONReader();
        reader.setMaxStringLength(5);
        // String "abcde" has length exactly 5, should be allowed (check is >)
        Object result = reader.read("\"abcde\"");
        assertEquals("abcde", result);
    }

    @Test
    public void testStringExceedingMaxStringLengthThrows() {
        var reader = new StrutsJSONReader();
        reader.setMaxStringLength(5);
        String json = "\"abcdefghij\"";
        var ex = assertThrows(JSONException.class, () -> reader.read(json));
        assertTrue(ex.getMessage().contains("maximum allowed length (5)"));
        assertTrue(ex.getMessage().contains(JSONConstants.JSON_MAX_STRING_LENGTH));
    }

    @Test
    public void testObjectKeyExceedingMaxKeyLengthThrowsOnFirstKey() {
        var reader = new StrutsJSONReader();
        reader.setMaxKeyLength(3);
        String json = "{\"longkey\":1}";
        var ex = assertThrows(JSONException.class, () -> reader.read(json));
        assertTrue(ex.getMessage().contains("maximum allowed length (3)"));
        assertTrue(ex.getMessage().contains(JSONConstants.JSON_MAX_KEY_LENGTH));
    }

    @Test
    public void testObjectKeyExceedingMaxKeyLengthThrowsOnSubsequentKey() {
        var reader = new StrutsJSONReader();
        reader.setMaxKeyLength(3);
        // First key "a" is within limit, second key "longkey" exceeds it
        String json = "{\"a\":1, \"longkey\":2}";
        var ex = assertThrows(JSONException.class, () -> reader.read(json));
        assertTrue(ex.getMessage().contains("maximum allowed length (3)"));
        assertTrue(ex.getMessage().contains(JSONConstants.JSON_MAX_KEY_LENGTH));
    }

    @Test
    public void testObjectKeyAtExactMaxKeyLengthAllowed() throws Exception {
        var reader = new StrutsJSONReader();
        reader.setMaxKeyLength(3);
        // Key "abc" has length exactly 3, should be allowed (check is >)
        var result = reader.read("{\"abc\":1}");
        assertTrue(result instanceof Map);
        assertEquals(1L, ((Map<?, ?>) result).get("abc"));
    }

    @Test
    public void testDefaultLimitsAllowTypicalPayload() throws Exception {
        var reader = new StrutsJSONReader();
        var json = "{\"name\":\"test\", \"values\":[1, 2, 3], \"nested\":{\"key\":\"value\"}}";
        assertTrue(reader.read(json) instanceof Map);
    }

    @Test
    public void testDepthCounterResetsAfterParsing() throws Exception {
        var reader = new StrutsJSONReader();
        reader.setMaxDepth(3);

        // First parse: uses depth up to 2
        assertTrue(reader.read("{\"a\":{\"b\":1}}") instanceof Map);

        // Second parse: should work fine if depth was reset
        assertTrue(reader.read("{\"a\":{\"b\":1}}") instanceof Map);
    }

    @Test
    public void testMixedNestingDepth() {
        var reader = new StrutsJSONReader();
        reader.setMaxDepth(2);
        // array inside object inside array = depth 3, should fail at depth 2
        String json = "[{\"a\":[1]}]";
        var ex = assertThrows(JSONException.class, () -> reader.read(json));
        assertTrue(ex.getMessage().contains("maximum allowed depth"));
    }

    /**
     * A single StrutsJSONReader instance is injected once into JSONUtil/JSONInterceptor and reused
     * across every concurrent request handled by that interceptor, so read() must be safe to call
     * concurrently from multiple threads on the same instance without one call's parse state
     * (cursor, token buffer, depth counter) leaking into another's -- both as a maxDepth bypass and
     * as one request's data appearing inside a different, concurrently-parsed request's result.
     *
     * <p>Uses a higher thread count than the minimum needed to demonstrate the bug: with only two
     * threads on a machine with many cores, the OS scheduler has no need to preempt either thread
     * mid-call, so the race window is rarely hit and the test can pass even against the unpatched
     * code. Sixteen threads contending for the same instance reproduces both symptoms reliably.</p>
     */
    @Test
    public void testConcurrentReuseDoesNotBypassMaxDepthOrLeakDataAcrossParses() throws Exception {
        int maxDepth = 5;
        var reader = new StrutsJSONReader();
        reader.setMaxDepth(maxDepth);

        String overDepthPayload = nestedObject(maxDepth + 1);
        int threadCount = 16;
        int iterations = 10_000;
        AtomicInteger overDepthAccepted = new AtomicInteger();
        AtomicInteger crossThreadLeaks = new AtomicInteger();
        CountDownLatch start = new CountDownLatch(1);
        ExecutorService pool = Executors.newFixedThreadPool(threadCount);

        for (int t = 0; t < threadCount; t++) {
            String marker = "MARKER_" + t + "_9f8e7d6c5b4a3210";
            String ownPayload = "{\"account\":\"user" + t + "\",\"secret\":\"" + marker + "\"}";
            pool.submit(() -> {
                await(start);
                for (int i = 0; i < iterations; i++) {
                    try {
                        Object result = reader.read(ownPayload);
                        if (!(result instanceof Map) || !marker.equals(((Map<?, ?>) result).get("secret"))) {
                            // this thread's own parse doesn't even contain its own data: the shared
                            // cursor/buffer was corrupted or overwritten by another thread
                            crossThreadLeaks.incrementAndGet();
                        }
                    } catch (JSONException ignored) {
                        // a failed parse under contention is not itself the thing measured here
                    }
                    try {
                        reader.read(overDepthPayload);
                        overDepthAccepted.incrementAndGet();
                    } catch (JSONException expected) {
                        // must always be rejected: depth is one more than maxDepth
                    }
                }
            });
        }

        start.countDown();
        pool.shutdown();
        assertTrue(pool.awaitTermination(120, TimeUnit.SECONDS));

        assertEquals("an over-depth payload must never be accepted, even when the reader instance " +
                "is shared with concurrent unrelated parses", 0, overDepthAccepted.get());
        assertEquals("a concurrently-parsed request's data must never appear in place of this " +
                "thread's own parsed result", 0, crossThreadLeaks.get());
    }

    private static void await(CountDownLatch latch) {
        try {
            latch.await();
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
        }
    }

    private static String nestedObject(int depth) {
        StringBuilder sb = new StringBuilder();
        sb.append("{\"a\":".repeat(depth)).append("1").append("}".repeat(depth));
        return sb.toString();
    }
}
