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
package com.opensymphony.xwork2.ognl.accessor;

import com.opensymphony.xwork2.ActionContext;
import com.opensymphony.xwork2.XWorkTestCase;
import com.opensymphony.xwork2.util.ListHolder;
import com.opensymphony.xwork2.util.ValueStack;
import ognl.ListPropertyAccessor;
import ognl.PropertyAccessor;

import java.util.ArrayList;
import java.util.List;

/**
 * @author Jason Carreira <jcarreira@eplus.com>
 */
public class XWorkListPropertyAccessorTest extends XWorkTestCase {

    public void testContains() {
        ValueStack vs = ActionContext.getContext().getValueStack();
        ListHolder listHolder = new ListHolder();
        vs.push(listHolder);

        vs.setValue("longs", new String[] {"1", "2", "3"});

        assertNotNull(listHolder.getLongs());
        assertEquals(3, listHolder.getLongs().size());
        assertEquals(new Long(1), (Long) listHolder.getLongs().get(0));
        assertEquals(new Long(2), (Long) listHolder.getLongs().get(1));
        assertEquals(new Long(3), (Long) listHolder.getLongs().get(2));

        assertTrue(((Boolean) vs.findValue("longs.contains(1)")).booleanValue());
    }

    public void testCanAccessListSizeProperty() {
        ValueStack vs = ActionContext.getContext().getValueStack();
        List myList = new ArrayList();
        myList.add("a");
        myList.add("b");

        ListHolder listHolder = new ListHolder();
        listHolder.setStrings(myList);

        vs.push(listHolder);

        assertEquals(new Integer(myList.size()), vs.findValue("strings.size()"));
        assertEquals(new Integer(myList.size()), vs.findValue("strings.size"));
    }

    public void testAutoGrowthCollectionLimit() {
        PropertyAccessor accessor = container.getInstance(PropertyAccessor.class, ArrayList.class.getName());
        ((XWorkListPropertyAccessor) accessor).setAutoGrowCollectionLimit("2");

        List<String> myList = new ArrayList<>();
        ListHolder listHolder = new ListHolder();
        listHolder.setStrings(myList);

        ValueStack vs = ActionContext.getContext().getValueStack();
        vs.push(listHolder);

        vs.setValue("strings[0]", "a");
        vs.setValue("strings[1]", "b");
        vs.setValue("strings[2]", "c");
        vs.setValue("strings[3]", "d");

        assertEquals(3, vs.findValue("strings.size()"));
    }

    public void testDeprecatedAutoGrowCollectionLimit() {
        PropertyAccessor accessor = container.getInstance(PropertyAccessor.class, ArrayList.class.getName());
        ((XWorkListPropertyAccessor) accessor).setDeprecatedAutoGrowCollectionLimit("2");

        List<String> myList = new ArrayList<>();
        ListHolder listHolder = new ListHolder();
        listHolder.setStrings(myList);

        ValueStack vs = ActionContext.getContext().getValueStack();
        vs.push(listHolder);

        vs.setValue("strings[0]", "a");
        vs.setValue("strings[1]", "b");
        vs.setValue("strings[2]", "c");
        vs.setValue("strings[3]", "d");

        assertEquals(3, vs.findValue("strings.size()"));
    }

}
