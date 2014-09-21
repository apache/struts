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

package org.apache.struts2.views.jsp.ui;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import org.apache.struts2.TestAction;
import org.apache.struts2.views.jsp.AbstractUITagTest;


/**
 */
public class SelectTest extends AbstractUITagTest {


    public void testHeaderCanBePreselected() throws Exception {
        SelectTag tag = new SelectTag();
        tag.setPageContext(pageContext);
        tag.setLabel("myLabel");
        tag.setList("#{1:'Cat',2:'Dog'}");
        tag.setName("myPet");
        tag.setHeaderKey("-1");
        tag.setHeaderValue("--- Please Select ---");
        tag.setValue("%{'-1'}");

        tag.doStartTag();
        tag.doEndTag();

        verify(SelectTag.class.getResource("Select-8.txt"));
    }

    /**
     * Tests WW-1601: Select tag template does not work properly for Object like Byte.
     */
    public void testByte() throws Exception {
        ByteObject hello = new ByteObject(new Byte((byte)1), "hello");
        ByteObject foo = new ByteObject(new Byte((byte)2), "foo");

        TestAction testAction = (TestAction) action;

        Collection collection = new ArrayList(2);
        // expect strings to be returned, we're still dealing with HTTP here!
        collection.add("1");
        collection.add("2");
        testAction.setCollection(collection);

        List list2 = new ArrayList();
        list2.add(hello);
        list2.add(foo);
        testAction.setList2(list2);

        SelectTag tag = new SelectTag();
        tag.setPageContext(pageContext);
        tag.setLabel("mylabel");
        tag.setName("collection");
        tag.setList("list2");
        tag.setListKey("byte");
        tag.setListValue("name");
        tag.setMultiple("true");
        tag.setTitle("mytitle");
        tag.setOnmousedown("alert('onmousedown');");
        tag.setOnmousemove("alert('onmousemove');");
        tag.setOnmouseout("alert('onmouseout');");
        tag.setOnmouseover("alert('onmouseover');");
        tag.setOnmouseup("alert('onmouseup');");

        tag.doStartTag();
        tag.doEndTag();

        verify(SelectTag.class.getResource("Select-10.txt"));
    }


    /**
     * Tests WW-455: Select tag template does not work properly for Object like BigDecimal.
     */
    public void testBigDecimal() throws Exception {
        BigDecimalObject hello = new BigDecimalObject("hello", new BigDecimal(1));
        BigDecimalObject foo = new BigDecimalObject("foo", new BigDecimal(2));

        TestAction testAction = (TestAction) action;

        Collection collection = new ArrayList(2);
        // expect strings to be returned, we're still dealing with HTTP here!
        collection.add("hello");
        collection.add("foo");
        testAction.setCollection(collection);

        List list2 = new ArrayList();
        list2.add(hello);
        list2.add(foo);
        list2.add(new BigDecimalObject("<cat>", new BigDecimal(1.500)));
        testAction.setList2(list2);

        SelectTag tag = new SelectTag();
        tag.setPageContext(pageContext);
        tag.setLabel("mylabel");
        tag.setName("collection");
        tag.setList("list2");
        tag.setListKey("name");
        tag.setListValue("bigDecimal");
        tag.setMultiple("true");
        tag.setTitle("mytitle");
        tag.setOnmousedown("alert('onmousedown');");
        tag.setOnmousemove("alert('onmousemove');");
        tag.setOnmouseout("alert('onmouseout');");
        tag.setOnmouseover("alert('onmouseover');");
        tag.setOnmouseup("alert('onmouseup');");

        tag.doStartTag();
        tag.doEndTag();

        verify(SelectTag.class.getResource("Select-3.txt"));
    }

    public class BigDecimalObject {
        private String name;
        private BigDecimal bigDecimal;

        public BigDecimalObject(String name, BigDecimal bigDecimal) {
            this.name = name;
            this.bigDecimal = bigDecimal;
        }

        public String getName() {
            return name;
        }

        public BigDecimal getBigDecimal() {
            return bigDecimal;
        }
    }

    public class ByteObject {
        private String name;
        private Byte byteValue;

        public ByteObject(Byte byteValue, String name) {
            this.name = name;
            this.byteValue = byteValue;
        }

        public String getName() {
            return name;
        }

        public Byte getByte() {
            return byteValue;
        }
    }

    public class LongObject {
        private Long id;
        private String value;


        public LongObject(Long id, String value) {
            this.id = id;
            this.value = value;
        }

        public Long getId() {
            return id;
        }

        public void setId(Long id) {
            this.id = id;
        }

        public String getValue() {
            return value;
        }

        public void setValue(String value) {
            this.value = value;
        }
    }

    public void testNullList() throws Exception {
        TestAction testAction = (TestAction) action;
        testAction.setList2(null);

        SelectTag tag = new SelectTag();
        tag.setName("collection");
        tag.setList("list2");
        tag.setLabel("tmjee_name");

        tag.setPageContext(pageContext);
        try {
            tag.doStartTag();
            tag.doEndTag();
            fail("exception should have been thrown value of select tag is null");
        }
        catch(Exception e) {
            assertTrue(true);
        }
    }


    public void testEmptyList() throws Exception {
        TestAction testAction = (TestAction) action;
        testAction.setList2(new ArrayList());

        SelectTag tag = new SelectTag();
        tag.setName("collection");
        tag.setList("list2");
        tag.setLabel("tmjee_name");

        tag.setPageContext(pageContext);
        tag.doStartTag();
        tag.doEndTag();

        verify(SelectTag.class.getResource("Select-4.txt"));
    }

    public void testMultiple() throws Exception {
        TestAction testAction = (TestAction) action;
        Collection collection = new ArrayList(2);
        collection.add("hello");
        collection.add("foo");
        testAction.setCollection(collection);
        testAction.setList(new String[][]{
                {"hello", "world"},
                {"foo", "bar"},
                {"cat", "dog"}
        });

        SelectTag tag = new SelectTag();
        tag.setPageContext(pageContext);
        tag.setLabel("mylabel");
        tag.setName("collection");
        tag.setList("list");
        tag.setListKey("top[0]");
        tag.setListValue("top[1]");
        tag.setMultiple("true");
        tag.setOnmousedown("alert('onmousedown');");
        tag.setOnmousemove("alert('onmousemove');");
        tag.setOnmouseout("alert('onmouseout');");
        tag.setOnmouseover("alert('onmouseover');");
        tag.setOnmouseup("alert('onmouseup');");

        tag.doStartTag();
        tag.doEndTag();

        verify(SelectTag.class.getResource("Select-2.txt"));
    }

    /**
     * WW-1747 - should be a valid test case for the described issue
     * @throws Exception
     */
    public void testMultipleWithLists() throws Exception {
        TestAction testAction = (TestAction) action;
        Collection collection = new ArrayList(2);

        collection.add(1l);
        collection.add(300000000l);
        testAction.setCollection(collection);

        List selectList = new ArrayList();
        selectList.add(new LongObject(1l, "foo"));
        selectList.add(new LongObject(2l, "bar"));
        selectList.add(new LongObject(300000000l, "foobar"));
        testAction.setList2(selectList);

        SelectTag tag = new SelectTag();
        tag.setPageContext(pageContext);
        tag.setLabel("mylabel");
        tag.setName("collection");
        tag.setList("list2");
        tag.setListKey("id");
        tag.setListValue("value");
        tag.setMultiple("true");
        tag.setOnmousedown("alert('onmousedown');");
        tag.setOnmousemove("alert('onmousemove');");
        tag.setOnmouseout("alert('onmouseout');");
        tag.setOnmouseover("alert('onmouseover');");
        tag.setOnmouseup("alert('onmouseup');");

        tag.doStartTag();
        tag.doEndTag();

        verify(SelectTag.class.getResource("Select-12.txt"));
    }

    public void testSimple() throws Exception {
        TestAction testAction = (TestAction) action;
        testAction.setFoo("hello");
        testAction.setList(new String[][]{
                {"hello", "world"},
                {"foo", "bar"}
        });

        SelectTag tag = new SelectTag();
        tag.setPageContext(pageContext);
        tag.setEmptyOption("true");
        tag.setLabel("mylabel");
        tag.setName("foo");
        tag.setList("list");
        tag.setListKey("top[0]");
        tag.setListValue("top[1]");

        // header stuff
        tag.setHeaderKey("headerKey");
        tag.setHeaderValue("headerValue");

        // empty option
        tag.setEmptyOption("true");

        tag.doStartTag();
        tag.doEndTag();

        verify(SelectTag.class.getResource("Select-1.txt"));
    }
    
    public void testSimpleWithNulls() throws Exception {
        TestAction testAction = (TestAction) action;
        testAction.setFoo("hello");
        testAction.setList(new String[][]{
                {"hello", null},
                {null, "bar"}
        });

        SelectTag tag = new SelectTag();
        tag.setPageContext(pageContext);
        tag.setEmptyOption("true");
        tag.setLabel("mylabel");
        tag.setName("foo");
        tag.setList("list");
        tag.setListKey("top[0]");
        tag.setListValue("top[1]");

        // header stuff
        tag.setHeaderKey("headerKey");
        tag.setHeaderValue("headerValue");

        // empty option
        tag.setEmptyOption("true");

        tag.doStartTag();
        tag.doEndTag();

        verify(SelectTag.class.getResource("Select-9.txt"));
    }

    public void testExtended() throws Exception {
        TestAction testAction = (TestAction) action;
        testAction.setFoo("hello");
        testAction.setList(new String[][]{
                {"hello", "world"},
                {"foo", "bar"}
        });

        SelectTag tag = new SelectTag();
        tag.setPageContext(pageContext);
        tag.setEmptyOption("true");
        tag.setLabel("mylabel");
        tag.setName("foo");
        tag.setList("list");
        tag.setListKey("top[0]");
        tag.setListValue("%{top[0] + ' - ' + top[1]}");

        // header stuff
        tag.setHeaderKey("headerKey");
        tag.setHeaderValue("%{foo + ': headerValue'}");

        // empty option
        tag.setEmptyOption("true");

        tag.doStartTag();
        tag.doEndTag();

        verify(SelectTag.class.getResource("Select-7.txt"));
     }

    public void testGenericSimple() throws Exception {
        SelectTag tag = new SelectTag();
        prepareTagGeneric(tag);
        verifyGenericProperties(tag, "simple", new String[]{"value"});
    }

    public void testGenericXhtml() throws Exception {
        SelectTag tag = new SelectTag();
        prepareTagGeneric(tag);
        verifyGenericProperties(tag, "xhtml", new String[]{"value"});
    }

    public void testMultipleOn() throws Exception {
        SelectTag tag = new SelectTag();
        tag.setPageContext(pageContext);
        tag.setLabel("media1");
        tag.setId("myId");
        tag.setEmptyOption("true");
        tag.setName("myName");
        tag.setMultiple("true");
        tag.setList("{'aaa','bbb'}");

        tag.doStartTag();
        tag.doEndTag();

        verify(SelectTag.class.getResource("Select-5.txt"));
    }

    public void testMultipleOff() throws Exception {
        SelectTag tag = new SelectTag();
        tag.setPageContext(pageContext);
        tag.setLabel("media2");
        tag.setId("myId");
        tag.setEmptyOption("true");
        tag.setName("myName");
        tag.setMultiple("false");
        tag.setList("{'aaa','bbb'}");

        tag.doStartTag();
        tag.doEndTag();

        verify(SelectTag.class.getResource("Select-6.txt"));
    }


    public void testSimpleInteger() throws Exception {
        TestAction testAction = (TestAction) action;

        IdName hello = new IdName(new Integer(1), "hello");
        IdName world = new IdName(new Integer(2), "world");
        List list2 = new ArrayList();
        list2.add(hello);
        list2.add(world);
        testAction.setList2(list2);

        testAction.setFooInt(new Integer(1));

        SelectTag tag = new SelectTag();
        tag.setPageContext(pageContext);
        tag.setEmptyOption("true");
        tag.setLabel("mylabel");
        tag.setName("fooInt");
        tag.setList("list2");
        tag.setListKey("id");
        tag.setListValue("name");

        // header stuff
        tag.setHeaderKey("headerKey");
        tag.setHeaderValue("headerValue");

        // empty option
        tag.setEmptyOption("true");

        tag.doStartTag();
        tag.doEndTag();

        verify(SelectTag.class.getResource("Select-11.txt"));
    }

    public void testSimpleIntegerWithValueWorkaround() throws Exception {
        TestAction testAction = (TestAction) action;

        IdName hello = new IdName(new Integer(1), "hello");
        IdName world = new IdName(new Integer(2), "world");
        List list2 = new ArrayList();
        list2.add(hello);
        list2.add(world);
        testAction.setList2(list2);

        testAction.setFooInt(new Integer(1));

        SelectTag tag = new SelectTag();
        tag.setPageContext(pageContext);
        tag.setEmptyOption("true");
        tag.setLabel("mylabel");
        tag.setName("fooInt");
        tag.setList("list2");
        tag.setListKey("id");
        tag.setListValue("name");
        tag.setValue("fooInt");

        // header stuff
        tag.setHeaderKey("headerKey");
        tag.setHeaderValue("headerValue");

        // empty option
        tag.setEmptyOption("true");

        tag.doStartTag();
        tag.doEndTag();

        verify(SelectTag.class.getResource("Select-11.txt"));
    }
    
    public void testEnumList() throws Exception {

        SelectTag tag = new SelectTag();
        tag.setPageContext(pageContext);
        tag.setLabel("mylabel");
        tag.setName("status");
        tag.setList("statusList");
        tag.setListKey("name");
        tag.setListValue("displayName");

        tag.doStartTag();
        tag.doEndTag();

        verify(SelectTag.class.getResource("Select-13.txt"));
    }

    public class IdName {
        private String name;
        private Integer id;

        public IdName(Integer id, String name) {
            this.name = name;
            this.id = id;
        }

        public String getName() {
            return name;
        }

        public Integer getId() {
            return id;
        }
    }
    
    private void prepareTagGeneric(SelectTag tag) {
        TestAction testAction = (TestAction) action;
        ArrayList collection = new ArrayList();
        collection.add("foo");
        collection.add("bar");
        collection.add("baz");

        testAction.setCollection(collection);

        tag.setList("collection");
    }

}
