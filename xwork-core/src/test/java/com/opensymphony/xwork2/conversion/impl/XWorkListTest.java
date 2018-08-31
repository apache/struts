/*
 * Copyright 2002-2003,2009 The Apache Software Foundation.
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
package com.opensymphony.xwork2.conversion.impl;

import com.opensymphony.xwork2.XWorkTestCase;

import java.util.ArrayList;


/**
 * Test cases for {@link XWorkList}.
 *
 * @author Mark Woon
 */
public class XWorkListTest extends XWorkTestCase {

    public void testAddAllIndex() {
        XWorkList xworkList = new XWorkList(String.class);
        xworkList.add(new String[]{"a"});
        xworkList.add("b");

        ArrayList addList = new ArrayList();
        addList.add(new String[]{"1"});
        addList.add(new String[]{"2"});
        addList.add(new String[]{"3"});

        // trim
        xworkList.addAll(3, addList);
        assertEquals(6, xworkList.size());
        assertEquals("a", xworkList.get(0));
        assertEquals("b", xworkList.get(1));
        assertEquals("", xworkList.get(2));
        assertEquals("1", xworkList.get(3));
        assertEquals("2", xworkList.get(4));
        assertEquals("3", xworkList.get(5));

        // take 2, no trim
        xworkList = new XWorkList(String.class);
        xworkList.add(new String[]{"a"});
        xworkList.add("b");

        addList = new ArrayList();
        addList.add(new String[]{"1"});
        addList.add(new String[]{"2"});
        addList.add(new String[]{"3"});

        xworkList.addAll(2, addList);
        assertEquals(5, xworkList.size());
        assertEquals("a", xworkList.get(0));
        assertEquals("b", xworkList.get(1));
        assertEquals("1", xworkList.get(2));
        assertEquals("2", xworkList.get(3));
        assertEquals("3", xworkList.get(4));

        // take 3, insert
        xworkList = new XWorkList(String.class);
        xworkList.add(new String[]{"a"});
        xworkList.add("b");

        addList = new ArrayList();
        addList.add(new String[]{"1"});
        addList.add(new String[]{"2"});
        addList.add(new String[]{"3"});

        xworkList.addAll(1, addList);
        assertEquals(5, xworkList.size());
        assertEquals("a", xworkList.get(0));
        assertEquals("1", xworkList.get(1));
        assertEquals("2", xworkList.get(2));
        assertEquals("3", xworkList.get(3));
        assertEquals("b", xworkList.get(4));
    }
}
