/*
 * $Id$
 *
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
package org.apache.struts2.convention;

import junit.framework.TestCase;

/**
 * <p>
 * This class tests the string tools.
 * </p>
 */
public class StringToolsTest extends TestCase {
    public void testEmpty() {
        assertTrue(StringTools.isTrimmedEmpty(null));
        assertTrue(StringTools.isTrimmedEmpty(""));
        assertTrue(StringTools.isTrimmedEmpty("   "));
        assertFalse(StringTools.isTrimmedEmpty("f"));
        assertFalse(StringTools.isTrimmedEmpty("  f  "));
    }

    public void testLastToken() {
        assertEquals("bar", StringTools.lastToken("/foo/bar", "/"));
        assertEquals("baz", StringTools.lastToken("/foo/bar/baz", "/"));
        assertEquals("baz", StringTools.lastToken("baz", "/"));
    }

    public void testUpToLastToken() {
        assertEquals("/foo", StringTools.upToLastToken("/foo/bar", "/"));
        assertEquals("/foo/bar", StringTools.upToLastToken("/foo/bar/baz", "/"));
        assertEquals("", StringTools.upToLastToken("/foo", "/"));
        assertEquals("", StringTools.upToLastToken("foo", "/"));
    }
}