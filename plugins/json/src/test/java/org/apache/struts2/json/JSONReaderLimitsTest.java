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

import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;
import static org.junit.Assert.fail;

/**
 * Tests for JSON reader configurable limits (WW-5618).
 */
public class JSONReaderLimitsTest {

    // --- maxDepth ---

    @Test
    public void testMaxDepthExceededByNestedObjects() {
        JSONReader reader = new JSONReader();
        reader.setMaxDepth(2);
        // 3 levels of nesting exceeds limit of 2
        try {
            reader.read("{\"a\":{\"b\":{\"c\":1}}}");
            fail("Expected JSONException for exceeding max depth");
        } catch (JSONException e) {
            assertTrue(e.getMessage().contains("2"));
        }
    }

    @Test
    public void testMaxDepthExceededByNestedArrays() {
        JSONReader reader = new JSONReader();
        reader.setMaxDepth(2);
        try {
            reader.read("[[[1]]]");
            fail("Expected JSONException for exceeding max depth");
        } catch (JSONException e) {
            assertTrue(e.getMessage().contains("2"));
        }
    }

    @Test
    public void testMaxDepthAtBoundaryIsAllowed() throws JSONException {
        JSONReader reader = new JSONReader();
        // 3 levels of nesting, limit is exactly 3 — should pass
        reader.setMaxDepth(3);
        Object result = reader.read("{\"a\":{\"b\":{\"c\":1}}}");
        assertNotNull(result);
    }

    @Test
    public void testMaxDepthOneBelowBoundaryIsRejected() {
        JSONReader reader = new JSONReader();
        // 3 levels of nesting, limit is 2 — should fail
        reader.setMaxDepth(2);
        try {
            reader.read("{\"a\":{\"b\":{\"c\":1}}}");
            fail("Expected JSONException");
        } catch (JSONException e) {
            assertTrue(e.getMessage().contains("depth"));
        }
    }

    // --- maxElements (per-container) ---

    @Test
    public void testMaxElementsExceededInObject() {
        JSONReader reader = new JSONReader();
        reader.setMaxElements(2);
        // 3 keys in one object exceeds per-container limit of 2
        try {
            reader.read("{\"a\":1,\"b\":2,\"c\":3}");
            fail("Expected JSONException for exceeding max elements");
        } catch (JSONException e) {
            assertTrue(e.getMessage().contains("2"));
        }
    }

    @Test
    public void testMaxElementsExceededInArray() {
        JSONReader reader = new JSONReader();
        reader.setMaxElements(2);
        try {
            reader.read("[1,2,3]");
            fail("Expected JSONException for exceeding max elements");
        } catch (JSONException e) {
            assertTrue(e.getMessage().contains("2"));
        }
    }

    @Test
    public void testMaxElementsAtBoundaryIsAllowed() throws JSONException {
        JSONReader reader = new JSONReader();
        // exactly 3 elements, limit is 3 — should pass
        reader.setMaxElements(3);
        Object result = reader.read("{\"a\":1,\"b\":2,\"c\":3}");
        assertNotNull(result);
    }

    @Test
    public void testMaxElementsIsPerContainerNotGlobal() throws JSONException {
        JSONReader reader = new JSONReader();
        // limit is 2; two containers each with 2 elements — should pass
        // because the limit applies per-container, not globally
        reader.setMaxElements(2);
        Object result = reader.read("{\"a\":[1,2],\"b\":[3,4]}");
        assertNotNull(result);
    }

    // --- maxStringLength (applies to both values and keys) ---

    @Test
    public void testMaxStringLengthExceededByValue() {
        JSONReader reader = new JSONReader();
        reader.setMaxStringLength(5);
        // "abcdef" is 6 chars, exceeds limit of 5
        try {
            reader.read("{\"k\":\"abcdef\"}");
            fail("Expected JSONException for exceeding max string length");
        } catch (JSONException e) {
            assertTrue(e.getMessage().contains("5"));
        }
    }

    @Test
    public void testMaxStringLengthExceededByKey() {
        JSONReader reader = new JSONReader();
        reader.setMaxStringLength(3);
        // key "longkey" is 7 chars, parsed through string() before validateKeyLength
        try {
            reader.read("{\"longkey\":1}");
            fail("Expected JSONException for exceeding max string length on key");
        } catch (JSONException e) {
            assertTrue(e.getMessage().contains("string length") || e.getMessage().contains("key length"));
        }
    }

    @Test
    public void testMaxStringLengthAtBoundaryIsAllowed() throws JSONException {
        JSONReader reader = new JSONReader();
        // "abcdef" is 6 chars, limit is 6 — should pass
        reader.setMaxStringLength(6);
        Object result = reader.read("{\"k\":\"abcdef\"}");
        assertNotNull(result);
    }

    // --- maxKeyLength ---

    @Test
    public void testMaxKeyLengthExceeded() {
        JSONReader reader = new JSONReader();
        reader.setMaxKeyLength(3);
        // "longkey" is 7 chars, exceeds limit of 3
        try {
            reader.read("{\"longkey\":1}");
            fail("Expected JSONException for exceeding max key length");
        } catch (JSONException e) {
            assertTrue(e.getMessage().contains("key length") || e.getMessage().contains("string length"));
        }
    }

    @Test
    public void testMaxKeyLengthAtBoundaryIsAllowed() throws JSONException {
        JSONReader reader = new JSONReader();
        // "key" is 3 chars, limit is 3 — should pass
        reader.setMaxKeyLength(3);
        Object result = reader.read("{\"key\":1}");
        assertNotNull(result);
    }

    // --- smoke test ---

    @Test
    public void testDefaultLimitsAcceptTypicalPayload() throws JSONException {
        JSONReader reader = new JSONReader();
        Object result = reader.read("{\"name\":\"test\",\"values\":[1,2,3],\"nested\":{\"a\":true}}");
        assertNotNull(result);
    }
}
