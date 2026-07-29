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

import java.io.Reader;
import java.io.StringReader;
import java.util.Map;
import java.util.concurrent.atomic.AtomicLong;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertThrows;
import static org.junit.Assert.assertTrue;

/**
 * Verifies that {@link JSONUtil#deserializeInput(Reader, int)} applies the configured input length
 * limit while reading, bounding how much input is consumed before the limit takes effect, and that
 * input within the limit still parses.
 */
public class JSONUtilInputLimitTest {

    /**
     * Emits {@code total} characters with no line terminator anywhere, and records how many
     * characters the caller actually consumed.
     */
    private static final class UnterminatedReader extends Reader {
        private final long total;
        private final AtomicLong consumed;
        private long produced = 0;

        UnterminatedReader(long total, AtomicLong consumed) {
            this.total = total;
            this.consumed = consumed;
        }

        @Override
        public int read(char[] cbuf, int off, int len) {
            if (produced >= total) {
                return -1;
            }
            int count = (int) Math.min(len, total - produced);
            for (int i = 0; i < count; i++) {
                cbuf[off + i] = 'a';
            }
            produced += count;
            consumed.addAndGet(count);
            return count;
        }

        @Override
        public void close() {
            // characters are generated on demand, so there is nothing to release
        }
    }

    @Test
    public void inputWithoutLineTerminatorIsLimitedWhileReading() {
        int maxLength = 1024;
        long inputSize = 64L * 1024 * 1024;
        AtomicLong consumed = new AtomicLong();

        JSONUtil util = new JSONUtil();
        Reader input = new UnterminatedReader(inputSize, consumed);

        assertThrows(JSONException.class, () -> util.deserializeInput(input, maxLength));

        long read = consumed.get();
        // Reading proceeds in chunks, so a single chunk of overshoot beyond the limit is expected.
        assertTrue("Consumed " + read + " characters for a limit of " + maxLength,
                read < maxLength + 65_536L);
    }

    @Test
    public void inputWithinLimitIsParsed() throws JSONException {
        JSONUtil util = new JSONUtil();
        util.setReader(new StrutsJSONReader());

        Object result = util.deserializeInput(new StringReader("{\"a\":1, \"b\":\"hello\"}"), 1024);

        assertTrue("Expected a parsed JSON object", result instanceof Map);
        assertEquals(1L, ((Map<?, ?>) result).get("a"));
        assertEquals("hello", ((Map<?, ?>) result).get("b"));
    }

    @Test
    public void inputSpanningMultipleLinesIsParsed() throws JSONException {
        JSONUtil util = new JSONUtil();
        util.setReader(new StrutsJSONReader());

        // Line terminators between tokens are insignificant whitespace to the reader.
        Object result = util.deserializeInput(new StringReader("{\n\"a\":1,\n\"b\":2\n}"), 1024);

        assertTrue("Expected a parsed JSON object", result instanceof Map);
        assertEquals(1L, ((Map<?, ?>) result).get("a"));
        assertEquals(2L, ((Map<?, ?>) result).get("b"));
    }
}
