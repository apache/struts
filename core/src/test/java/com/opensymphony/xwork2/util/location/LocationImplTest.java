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

import com.opensymphony.xwork2.util.ClassLoaderUtil;
import junit.framework.TestCase;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.URL;
import java.util.List;

public class LocationImplTest extends TestCase {
    
    public LocationImplTest(String name) {
        super(name);
    }
    
    static final String str = "path/to/file.xml:1:40";

    public void testEquals() throws Exception {
        Location loc1 = LocationUtils.parse(str);
        Location loc2 = new LocationImpl(null, "path/to/file.xml", 1, 40);
        
        assertEquals("locations", loc1, loc2);
        assertEquals("hashcode", loc1.hashCode(), loc2.hashCode());
        assertEquals("string representation", loc1.toString(), loc2.toString());
    }
    
    /**
     * Test that Location.UNKNOWN is kept identical on deserialization
     */
    public void testSerializeUnknown() throws Exception {
        ByteArrayOutputStream bos = new ByteArrayOutputStream();
        ObjectOutputStream oos = new ObjectOutputStream(bos);
        
        oos.writeObject(Location.UNKNOWN);
        oos.close();
        bos.close();
        
        ByteArrayInputStream bis = new ByteArrayInputStream(bos.toByteArray());
        ObjectInputStream ois = new ObjectInputStream(bis);
        
        Object obj = ois.readObject();
        
        assertSame("unknown location", Location.UNKNOWN, obj);
    }
    
    public void testGetSnippet() throws Exception {
        URL url = ClassLoaderUtil.getResource("com/opensymphony/xwork2/somefile.txt", getClass());
        Location loc = new LocationImpl("foo", url.toString(), 3, 2);
        
        List snippet = loc.getSnippet(1);
        assertNotNull(snippet);
        assertTrue("Wrong length: "+snippet.size(), 3 == snippet.size());
        
        assertTrue("is".equals(snippet.get(0)));
        assertTrue("a".equals(snippet.get(1)));
        assertTrue("file".equals(snippet.get(2)));
    }
    
    public void testGetSnippetNoPadding() throws Exception {
        URL url = ClassLoaderUtil.getResource("com/opensymphony/xwork2/somefile.txt", getClass());
        Location loc = new LocationImpl("foo", url.toString(), 3, 2);
        
        List snippet = loc.getSnippet(0);
        assertNotNull(snippet);
        assertTrue("Wrong length: "+snippet.size(), 1 == snippet.size());
        
        assertTrue("a".equals(snippet.get(0)));
    }
}
