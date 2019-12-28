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
package org.apache.struts2;

import com.opensymphony.xwork2.XWorkTestCase;
import com.opensymphony.xwork2.util.location.Location;

public class StrutsExceptionTest extends XWorkTestCase {

    public void testUnknown() {
        StrutsException e = new StrutsException("testXXX", this);
        assertEquals(Location.UNKNOWN, e.getLocation());
    }

    public void testThrowable() {
        StrutsException e = new StrutsException("testThrowable", new IllegalArgumentException("Arg is null"));
        assertEquals("org/apache/struts2/StrutsExceptionTest.java", e.getLocation().getURI());
        String s = e.getLocation().toString();
        assertTrue(s.contains("Method: testThrowable"));
    }

    public void testCauseAndTarget() {
        StrutsException e = new StrutsException(new IllegalArgumentException("Arg is null"), this);
        assertEquals("org/apache/struts2/StrutsExceptionTest.java", e.getLocation().getURI());
        String s = e.getLocation().toString();
        assertTrue(s.contains("Method: testCauseAndTarget"));
    }

    public void testDefaultConstructor() {
        StrutsException e = new StrutsException();

        assertNull(e.getCause());
        assertNull(e.getMessage());
        assertNull(e.getLocation());

        assertNull(e.toString()); // mo message so it returns null
    }

    public void testMessageOnly() {
        StrutsException e = new StrutsException("Hello World");

        assertNull(e.getCause());
        assertEquals("Hello World", e.getMessage());
        assertEquals(Location.UNKNOWN, e.getLocation());
    }

    public void testCauseOnly() {
        StrutsException e = new StrutsException(new IllegalArgumentException("Arg is null"));

        assertNotNull(e.getCause());
        assertNotNull(e.getLocation());
        assertEquals("org/apache/struts2/StrutsExceptionTest.java", e.getLocation().getURI());
        String s = e.getLocation().toString();
        assertTrue(s.contains("Method: testCauseOnly"));
        assertTrue(e.toString().contains("Arg is null"));
    }

    public void testCauseOnlyNoMessage() {
        StrutsException e = new StrutsException(new IllegalArgumentException());

        assertNotNull(e.getCause());
        assertNotNull(e.getLocation());
        assertEquals("org/apache/struts2/StrutsExceptionTest.java", e.getLocation().getURI());
        String s = e.getLocation().toString();
        assertTrue(s.contains("Method: testCauseOnly"));
        assertTrue(e.toString().contains("Method: testCauseOnly"));
    }

}
