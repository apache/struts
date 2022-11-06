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
package org.apache.struts2.url;

import org.apache.struts2.StrutsConstants;
import org.junit.Before;
import org.junit.Test;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertTrue;

public class StrutsUrlDecoderTest {

    private StrutsUrlDecoder decoder;

    @Test
    public void testDecodeStringInvalid() {
        // %n rather than %nn should throw an IAE according to the Javadoc
        Exception exception = null;
        try {
            decoder.decode("%5xxxxx", "ISO-8859-1", false);
        } catch (Exception e) {
            exception = e;
        }
        assertTrue(exception instanceof IllegalArgumentException);

        // Edge case trying to trigger ArrayIndexOutOfBoundsException
        exception = null;
        try {
            decoder.decode("%5", "ISO-8859-1", false);
        } catch (Exception e) {
            exception = e;
        }
        assertTrue(exception instanceof IllegalArgumentException);
    }

    @Test
    public void testDecodeStringValidIso88591Start() {
        String result = decoder.decode("%41xxxx", "ISO-8859-1", false);
        assertEquals("Axxxx", result);
    }

    @Test
    public void testDecodeStringValidIso88591Middle() {
        String result = decoder.decode("xx%41xx", "ISO-8859-1", false);
        assertEquals("xxAxx", result);
    }

    @Test
    public void testDecodeStringValidIso88591End() {
        String result = decoder.decode("xxxx%41", "ISO-8859-1", false);
        assertEquals("xxxxA", result);
    }

    @Test
    public void testDecodeStringValidUtf8Start() {
        String result = decoder.decode("%c3%aaxxxx", "UTF-8", false);
        assertEquals("\u00eaxxxx", result);
    }

    @Test
    public void testDecodeStringValidUtf8Middle() {
        String result = decoder.decode("xx%c3%aaxx", "UTF-8", false);
        assertEquals("xx\u00eaxx", result);
    }

    @Test
    public void testDecodeStringValidUtf8End() {
        String result = decoder.decode("xxxx%c3%aa", "UTF-8", false);
        assertEquals("xxxx\u00ea", result);
    }

    @Test
    public void testDecodePlusCharAsSpace() {
        String result = decoder.decode("a+b", "UTF-8", true);
        assertEquals("a b", result);
    }

    @Test
    public void testDecodeNull() {
        String result = decoder.decode(null);
        assertNull(result);
    }

    @Test
    public void testSettingEncoding() {
        decoder.setEncoding("ISO-8859-1");
        String result = decoder.decode("xxxx%41");
        assertEquals("xxxxA", result);
    }

    @Test
    public void testDecoding() {
        String result = decoder.decode("%E6%96%B0%E8%81%9E");

        assertEquals("\u65b0\u805e", result);
    }

    @Before
    public void setUp() throws Exception {
        this.decoder = new StrutsUrlDecoder();
    }
}
