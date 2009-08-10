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

package org.apache.struts2.util;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Iterator;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Set;

import junit.framework.TestCase;

/**
 *
 * @version $Date$ $Id$
 */
public class ContainUtilTest extends TestCase {

    public void testNull() throws Exception {
        assertFalse(ContainUtil.contains(null, null));
        assertFalse(ContainUtil.contains(new Object(), null));
        assertFalse(ContainUtil.contains(null, new Object()));
    }

    public void testNullInAray()throws Exception {
        assertTrue(ContainUtil.contains(new String[] {"a", null, "b"}, "b"));
    }

    public void testSimpleList() throws Exception {
        List<String> l = new ArrayList<String>();
        l.add("one");
        l.add("two");

        assertFalse(ContainUtil.contains(l, "three"));
        assertTrue(ContainUtil.contains(l, "one"));
        assertTrue(ContainUtil.contains(l, "two"));
    }

    public void testSimpleSet() throws Exception {
        Set<String> s = new LinkedHashSet<String>();
        s.add("one");
        s.add("two");

        assertFalse(ContainUtil.contains(s, "thre"));
        assertTrue(ContainUtil.contains(s, "one"));
        assertTrue(ContainUtil.contains(s, "two"));
    }

    public void testComplexList() throws Exception {
        List<MyObject> l = new ArrayList<MyObject>();
        l.add(new MyObject("tm_jee", 20));
        l.add(new MyObject("jenny", 22));

        assertFalse(ContainUtil.contains(l, new MyObject("paul", 50)));
        assertFalse(ContainUtil.contains(l, new MyObject("tm_jee", 44)));
        assertTrue(ContainUtil.contains(l, new MyObject("tm_jee", 20)));
        assertTrue(ContainUtil.contains(l, new MyObject("jenny", 22)));
    }

    public void testComplexMap() throws Exception {
        Set<MyObject> s = new LinkedHashSet<MyObject>();
        s.add(new MyObject("tm_jee", 20));
        s.add(new MyObject("jenny", 22));

        assertFalse(ContainUtil.contains(s, new MyObject("paul", 50)));
        assertFalse(ContainUtil.contains(s, new MyObject("tm_jee", 44)));
        assertTrue(ContainUtil.contains(s, new MyObject("tm_jee", 20)));
        assertTrue(ContainUtil.contains(s, new MyObject("jenny", 22)));
    }

    public void testObject() throws Exception {
        assertFalse(ContainUtil.contains("aaa", "bbb"));
        assertFalse(ContainUtil.contains(new MyObject("tm_jee", 22), new MyObject("tmjee", 22)));
        assertTrue(ContainUtil.contains("apple", "apple"));
        assertTrue(ContainUtil.contains(new MyObject("tm_jee", 22), new MyObject("tm_jee", 22)));
    }

    public void testIterableObject() throws Exception {
    	MyIterableObject i = new MyIterableObject("one", "two");

        assertFalse(ContainUtil.contains(i, "thre"));
        assertTrue(ContainUtil.contains(i, "one"));
        assertTrue(ContainUtil.contains(i, "two"));
    }
    
    public static class MyIterableObject implements Iterable<String> {
    	private List<String> values;
    	
    	public MyIterableObject(String... strings) {
    		values = Arrays.asList(strings);
    	}
    	
		public Iterator<String> iterator() {
			return values.iterator();
		}
    }

    public static class MyObject {
        private String name;
        private Integer age;

        public MyObject(String name, Integer age) {
            this.name = name;
            this.age = age;
        }

        @Override
        public int hashCode() {
            return this.name.hashCode();
        }

        @Override
        public boolean equals(Object obj) {
            if (obj == null) { return false; }
            if (! (obj instanceof MyObject)) { return false; }
            MyObject tmp = (MyObject) obj;
            if (
                    tmp.name.equals(this.name) &&
                    tmp.age.equals(this.age)
                ) {
                return true;
            }
            return false;

        }
    }
}
