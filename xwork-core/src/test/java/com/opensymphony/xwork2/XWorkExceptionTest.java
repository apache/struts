/*
 * Copyright 2002-2007,2009 The Apache Software Foundation.
 * 
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 * 
 *      http://www.apache.org/licenses/LICENSE-2.0
 * 
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package com.opensymphony.xwork2;

import com.opensymphony.xwork2.util.location.Location;

public class XWorkExceptionTest extends XWorkTestCase {

    public void testUnknown() throws Exception {
        XWorkException e = new XWorkException("testXXX", this);
        assertEquals(Location.UNKNOWN, e.getLocation());
    }

    public void testThrowable() {
        XWorkException e = new XWorkException("testThrowable", new IllegalArgumentException("Arg is null"));
        assertEquals("com/opensymphony/xwork2/XWorkExceptionTest.java", e.getLocation().getURI());
        String s = e.getLocation().toString();
        assertTrue(s.contains("Method: testThrowable"));
    }

    public void testCauseAndTarget() {
        XWorkException e = new XWorkException(new IllegalArgumentException("Arg is null"), this);
        assertEquals("com/opensymphony/xwork2/XWorkExceptionTest.java", e.getLocation().getURI());
        String s = e.getLocation().toString();
        assertTrue(s.contains("Method: testCauseAndTarget"));
    }

    public void testDefaultConstructor() {
        XWorkException e = new XWorkException();

        assertNull(e.getCause());
        assertNull(e.getThrowable());
        assertNull(e.getMessage());
        assertNull(e.getLocation());

        assertNull(e.toString()); // mo message so it returns null
    }

    public void testMessageOnly() {
        XWorkException e = new XWorkException("Hello World");

        assertNull(e.getCause());
        assertEquals("Hello World", e.getMessage());
        assertEquals(Location.UNKNOWN, e.getLocation());
    }

    public void testCauseOnly() {
        XWorkException e = new XWorkException(new IllegalArgumentException("Arg is null"));

        assertNotNull(e.getCause());
        assertNotNull(e.getLocation());
        assertEquals("com/opensymphony/xwork2/XWorkExceptionTest.java", e.getLocation().getURI());
        String s = e.getLocation().toString();
        assertTrue(s.contains("Method: testCauseOnly"));
        assertTrue(e.toString().contains("Arg is null"));
    }

    public void testCauseOnlyNoMessage() {
        XWorkException e = new XWorkException(new IllegalArgumentException());

        assertNotNull(e.getCause());
        assertNotNull(e.getLocation());
        assertEquals("com/opensymphony/xwork2/XWorkExceptionTest.java", e.getLocation().getURI());
        String s = e.getLocation().toString();
        assertTrue(s.contains("Method: testCauseOnly"));
        assertTrue(e.toString().contains("Method: testCauseOnly"));
    }

}
