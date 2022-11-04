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

import org.junit.Before;
import org.junit.Test;

import static org.junit.Assert.assertEquals;

public class StrutsUrlEncoderTest {

    private StrutsUrlEncoder encoder;

    @Test
    public void testEncodeValidIso88591Start() {
        String result = encoder.encode("%xxxx", "ISO-8859-1");
        assertEquals("%25xxxx", result);
    }

    @Test
    public void testEncodeValidIso88591Middle() {
        String result = encoder.encode("xx%xx", "ISO-8859-1");
        assertEquals("xx%25xx", result);
    }

    @Test
    public void testEncodeValidIso88591End() {
        String result = encoder.encode("xxxx%", "ISO-8859-1");
        assertEquals("xxxx%25", result);
    }

    @Test
    public void testEncodeValidUtf8Start() {
        String result = encoder.encode("\u00eaxxxx", "UTF-8");
        assertEquals("%C3%AAxxxx", result);
    }

    @Test
    public void testEncodeValidUtf8Middle() {
        String result = encoder.encode("xx\u00eaxx", "UTF-8");
        assertEquals("xx%C3%AAxx", result);
    }

    @Test
    public void testEncodeValidUtf8End() {
        String result = encoder.encode("xxxx\u00ea", "UTF-8");
        assertEquals("xxxx%C3%AA", result);
    }

    @Test
    public void testEncodePlusCharAsSpace() {
        String result = encoder.encode("a b", "UTF-8");
        assertEquals("a+b", result);
    }

    @Test
    public void testEncodeException() {
        String result = encoder.encode("a b", "UNKNOWN-8");
        assertEquals("a b", result);
    }

    @Test
    public void testSettingEncoding() {
        encoder.setEncoding("ISO-8859-1");
        String result = encoder.encode("%xxxx");
        assertEquals("%25xxxx", result);
    }

    @Test
    public void testEncoding() {
        String result = encoder.encode("\u65b0\u805e");

        assertEquals("%E6%96%B0%E8%81%9E", result);
    }

    @Before
    public void setUp() throws Exception {
        this.encoder = new StrutsUrlEncoder();
    }

}
