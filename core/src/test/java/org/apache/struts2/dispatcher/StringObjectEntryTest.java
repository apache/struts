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
package org.apache.struts2.dispatcher;

import static org.junit.Assert.assertNotEquals;

import junit.framework.TestCase;

public class StringObjectEntryTest extends TestCase {
    public void testGetKey() {
        StringObjectEntry entry = new StringObjectEntryTestImpl("theKey", "theValue");
        assertEquals("theKey", entry.getKey());
    }
    
    public void testGetValue() {
        StringObjectEntry entry = new StringObjectEntryTestImpl("theKey", "theValue");
        assertEquals("theValue", entry.getValue());
    }
    
    public void testEquals() {
        StringObjectEntry entry = new StringObjectEntryTestImpl("theKey", "theValue");
        
        assertEquals(entry, new StringObjectEntryTestImpl("theKey", "theValue"));
        assertNotEquals(entry, new StringObjectEntryTestImpl("theKey", "differentValue"));
        assertNotEquals(entry, new StringObjectEntryTestImpl("differentKey", "theValue"));
        assertNotEquals(entry, new StringObjectEntryTestImpl("differentKey", "differentValue"));
    }
    
    public void testHashCode() {
        StringObjectEntry entry = new StringObjectEntryTestImpl("theKey", "theValue");
        assertEquals(-1962296402, entry.hashCode());
    }
    
    static class StringObjectEntryTestImpl extends StringObjectEntry {
        StringObjectEntryTestImpl(final String key, final Object value) {
            super(key, value);
        }

        @Override
        public Object setValue(final Object value) {
            return value;
        }
        
    }
}
