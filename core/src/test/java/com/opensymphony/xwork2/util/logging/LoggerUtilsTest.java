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
package com.opensymphony.xwork2.util.logging;


import junit.framework.TestCase;

public class LoggerUtilsTest extends TestCase {

    public void testFormatMessage() {
        assertEquals("foo", LoggerUtils.format("foo"));
        assertEquals("foo #", LoggerUtils.format("foo #"));
        assertEquals("#foo", LoggerUtils.format("#foo"));
        assertEquals("foo #1", LoggerUtils.format("foo #1"));
        assertEquals("foo bob", LoggerUtils.format("foo #0", "bob"));
        assertEquals("foo bob joe", LoggerUtils.format("foo #0 #1", "bob", "joe"));
        assertEquals("foo bob joe #8", LoggerUtils.format("foo #0 #1 #8", "bob", "joe"));
        assertEquals("foo (bob/ally)", LoggerUtils.format("foo (#0/#1)", "bob", "ally"));
        assertEquals("foo (bobally)", LoggerUtils.format("foo (#0#1)", "bob", "ally"));

        assertEquals(null, LoggerUtils.format(null));
        assertEquals("", LoggerUtils.format(""));
        
    }

}
