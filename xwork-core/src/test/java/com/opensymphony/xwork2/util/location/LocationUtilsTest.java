/*
 * Copyright 2005 The Apache Software Foundation.
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
package com.opensymphony.xwork2.util.location;

import junit.framework.TestCase;

public class LocationUtilsTest extends TestCase {
    
    public LocationUtilsTest(String name) {
        super(name);
    }
    
    static final String str = "path/to/file.xml:1:40";

    public void testParse() throws Exception {
        String str = "<map:generate> - path/to/file.xml:1:40";
        Location loc = LocationUtils.parse(str);
        
        assertEquals("<map:generate>", loc.getDescription());
        assertEquals("URI", "path/to/file.xml", loc.getURI());
        assertEquals("line", 1, loc.getLineNumber());
        assertEquals("column", 40, loc.getColumnNumber());
        assertEquals("string representation", str, loc.toString());
    }
    
    public void testGetLocation_location() throws Exception {
    		Location loc = new LocationImpl("desc", "sysId", 10, 4);
    		assertTrue("Location should be the same", 
				loc == LocationUtils.getLocation(loc, null));
    }
    
    public void testGetLocation_exception() throws Exception {
    		Exception e = new Exception();
    		Location loc = LocationUtils.getLocation(e, null);
    		
    		assertTrue("Wrong sysId: "+loc.getURI(),
    				"com/opensymphony/xwork2/util/location/LocationUtilsTest.java"
    				.equals(loc.getURI()));
    }
}
