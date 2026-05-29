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
}
