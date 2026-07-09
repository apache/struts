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
     * (cursor, token buffer, depth counter) leaking into another's.
     */
    @Test
    public void testConcurrentReuseDoesNotBypassMaxDepth() throws Exception {
        int maxDepth = 5;
        var reader = new StrutsJSONReader();
        reader.setMaxDepth(maxDepth);

        String overDepthPayload = nestedObject(maxDepth + 1);
        String shallowPayload = "{\"a\":1}";
        int iterations = 20_000;
        AtomicInteger overDepthAccepted = new AtomicInteger();

        runConcurrently(iterations,
                () -> {
                    try {
                        reader.read(overDepthPayload);
                        overDepthAccepted.incrementAndGet();
                    } catch (JSONException expected) {
                        // must always be rejected: depth is one more than maxDepth
                    }
                },
                () -> {
                    try {
                        reader.read(shallowPayload);
                    } catch (JSONException ignored) {
                        // unrelated to the assertion below
                    }
                });

        assertEquals("an over-depth payload must never be accepted, even when the reader instance " +
                "is shared with concurrent unrelated parses", 0, overDepthAccepted.get());
    }

    /**
     * Companion to {@link #testConcurrentReuseDoesNotBypassMaxDepth()}: confirms that concurrent
     * parses on a shared reader instance never cross-contaminate each other's data, i.e. one
     * request's parsed result never contains fragments of a different, concurrently-parsed request.
     */
    @Test
    public void testConcurrentReuseDoesNotLeakDataAcrossParses() throws Exception {
        String secretMarker = "VICTIM_SECRET_TOKEN_9f8e7d6c5b4a3210";
        var reader = new StrutsJSONReader();

        String victimPayload = "{\"account\":\"victim\",\"secret\":\"" + secretMarker + "\"}";
        String attackerPayload = "{\"account\":\"attacker\",\"comment\":\"nothing interesting here\"}";
        int iterations = 50_000;
        AtomicInteger leaksObserved = new AtomicInteger();

        runConcurrently(iterations,
                () -> {
                    try {
                        Object result = reader.read(attackerPayload);
                        if (containsFragmentOf(result, secretMarker)) {
                            leaksObserved.incrementAndGet();
                        }
                    } catch (JSONException ignored) {
                        // a failed parse under contention isn't itself a data leak
                    }
                },
                () -> {
                    try {
                        reader.read(victimPayload);
                    } catch (JSONException ignored) {
                        // the victim side failing to parse isn't what's under test here
                    }
                });

        assertEquals("a concurrently-parsed request's data must never appear inside this request's " +
                "own parsed result", 0, leaksObserved.get());
    }

    private static void runConcurrently(int iterationsPerThread, Runnable first, Runnable second) throws InterruptedException {
        CountDownLatch start = new CountDownLatch(1);
        ExecutorService pool = Executors.newFixedThreadPool(2);
        pool.submit(() -> runLoop(start, iterationsPerThread, first));
        pool.submit(() -> runLoop(start, iterationsPerThread, second));
        start.countDown();
        pool.shutdown();
        assertTrue(pool.awaitTermination(120, TimeUnit.SECONDS));
    }

    private static void runLoop(CountDownLatch start, int iterations, Runnable body) {
        try {
            start.await();
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
            return;
        }
        for (int i = 0; i < iterations; i++) {
            body.run();
        }
    }

    private static String nestedObject(int depth) {
        StringBuilder sb = new StringBuilder();
        sb.append("{\"a\":".repeat(depth)).append("1").append("}".repeat(depth));
        return sb.toString();
    }

    @SuppressWarnings("unchecked")
    private static boolean containsFragmentOf(Object parsed, String needle) {
        if (parsed instanceof Map) {
            for (Object value : ((Map<Object, Object>) parsed).values()) {
                if (containsFragmentOf(value, needle)) {
                    return true;
                }
            }
            return false;
        }
        if (!(parsed instanceof String haystack)) {
            return false;
        }
        int fragmentLength = 12;
        for (int i = 0; i + fragmentLength <= needle.length(); i++) {
            if (haystack.contains(needle.substring(i, i + fragmentLength))) {
                return true;
            }
        }
        return false;
    }
}
