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
package org.apache.struts2.util;

import org.junit.Test;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNull;

public class SafeUriUtilTest {

    @Test
    public void nullValueReturnsNull() {
        assertNull(SafeUriUtil.rejectUnsafeScheme(null));
    }

    @Test
    public void executableSchemesAreRejected() {
        assertEquals("#", SafeUriUtil.rejectUnsafeScheme("javascript:alert(1)"));
        assertEquals("#", SafeUriUtil.rejectUnsafeScheme("vbscript:msgbox(1)"));
        assertEquals("#", SafeUriUtil.rejectUnsafeScheme("data:text/html,<script>alert(1)</script>"));
    }

    @Test
    public void rejectionIsCaseInsensitive() {
        assertEquals("#", SafeUriUtil.rejectUnsafeScheme("JaVaScRiPt:alert(1)"));
        assertEquals("#", SafeUriUtil.rejectUnsafeScheme("JAVASCRIPT:alert(1)"));
    }

    @Test
    public void embeddedTabOrNewlineInTheSchemeDoesNotDefeatTheCheck() {
        // A real browser's URL parser removes every ASCII tab/newline before reading the scheme
        // (WHATWG URL spec, section 4.4), so these still resolve to the javascript: scheme when activated.
        assertEquals("#", SafeUriUtil.rejectUnsafeScheme("java\tscript:alert(1)"));
        assertEquals("#", SafeUriUtil.rejectUnsafeScheme("java\nscript:alert(1)"));
        assertEquals("#", SafeUriUtil.rejectUnsafeScheme("java\rscript:alert(1)"));
        assertEquals("#", SafeUriUtil.rejectUnsafeScheme("j\ta\nv\ra\tscript:alert(1)"));
    }

    @Test
    public void leadingControlCharactersOrSpaceDoNotDefeatTheCheck() {
        assertEquals("#", SafeUriUtil.rejectUnsafeScheme(" javascript:alert(1)"));
        assertEquals("#", SafeUriUtil.rejectUnsafeScheme("\tjavascript:alert(1)"));
        assertEquals("#", SafeUriUtil.rejectUnsafeScheme("\u0000javascript:alert(1)"));
        assertEquals("#", SafeUriUtil.rejectUnsafeScheme("\u0001\u0002javascript:alert(1)"));
    }

    @Test
    public void allowedSchemesPassThroughUnchanged() {
        assertEquals("http://example.com/path", SafeUriUtil.rejectUnsafeScheme("http://example.com/path"));
        assertEquals("https://example.com/path", SafeUriUtil.rejectUnsafeScheme("https://example.com/path"));
        assertEquals("mailto:user@example.com", SafeUriUtil.rejectUnsafeScheme("mailto:user@example.com"));
        assertEquals("tel:+15551234567", SafeUriUtil.rejectUnsafeScheme("tel:+15551234567"));
        assertEquals("HTTPS://example.com", SafeUriUtil.rejectUnsafeScheme("HTTPS://example.com"));
    }

    @Test
    public void schemelessValuesPassThroughUnchanged() {
        assertEquals("/relative/path", SafeUriUtil.rejectUnsafeScheme("/relative/path"));
        assertEquals("relative/path", SafeUriUtil.rejectUnsafeScheme("relative/path"));
        assertEquals("action.action?id=1", SafeUriUtil.rejectUnsafeScheme("action.action?id=1"));
        assertEquals("#section", SafeUriUtil.rejectUnsafeScheme("#section"));
        assertEquals("", SafeUriUtil.rejectUnsafeScheme(""));
        // A colon with no earlier '/' still reads as a scheme per RFC 3986's own grammar, so a browser
        // would try to navigate to this as a (here, unregistered) custom-scheme URI rather than a path;
        // treating it the same as any other unrecognized scheme is the conservative, correct call.
        assertEquals("#", SafeUriUtil.rejectUnsafeScheme("looks-like-a-word:but-is-a-scheme-per-rfc-3986"));
    }
}
