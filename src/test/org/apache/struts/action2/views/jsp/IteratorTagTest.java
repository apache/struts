/*
 * Copyright (c) 2002-2003 by OpenSymphony
 * All rights reserved.
 */
package org.apache.struts.action2.views.jsp;

import com.mockobjects.servlet.MockBodyContent;
import com.mockobjects.servlet.MockJspWriter;

import javax.servlet.jsp.JspException;
import javax.servlet.jsp.tagext.TagSupport;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;


/**
 * Test Case for Iterator Tag 
 * 
 * @author $Author: tmjee $
 * @version $Date: 2006/01/25 16:58:03 $ $Id: IteratorTagTest.java,v 1.23 2006/01/25 16:58:03 tmjee Exp $
 */
public class IteratorTagTest extends AbstractUITagTest {

    IteratorTag tag;

    
    public void testIteratingWithIdSpecified() throws Exception {
    	List list = new ArrayList();
    	list.add("one");
    	list.add("two");
    	list.add("three");
    	list.add("four");
    	list.add("five");
    	
    	Foo foo = new Foo();
    	foo.setList(list);
    	
    	stack.push(foo);
    	
    	tag.setValue("list");
    	tag.setId("myId");
    	
    	// one
    	int result = tag.doStartTag();
    	assertEquals(result, TagSupport.EVAL_BODY_INCLUDE);
    	assertEquals(stack.peek(), "one");
    	assertEquals(stack.getContext().get("myId"), "one");
    	

    	tag.doInitBody();
    	
    	// two
    	result = tag.doAfterBody();
    	assertEquals(result, TagSupport.EVAL_BODY_AGAIN);
    	assertEquals(stack.peek(), "two");
    	assertEquals(stack.getContext().get("myId"), "two");
    	
    	
    	// three
    	result = tag.doAfterBody();
    	assertEquals(result, TagSupport.EVAL_BODY_AGAIN);
    	assertEquals(stack.peek(), "three");
    	assertEquals(stack.getContext().get("myId"), "three");
    	
    	
    	// four
    	result = tag.doAfterBody();
    	assertEquals(result, TagSupport.EVAL_BODY_AGAIN);
    	assertEquals(stack.peek(), "four");
    	assertEquals(stack.getContext().get("myId"), "four");
    	
    	
    	// five
    	result = tag.doAfterBody();
    	assertEquals(result, TagSupport.EVAL_BODY_AGAIN);
    	assertEquals(stack.peek(), "five");
    	assertEquals(stack.getContext().get("myId"), "five");
    	
    	
    	result = tag.doAfterBody();
    	assertEquals(result, TagSupport.SKIP_BODY);
    	
    	result = tag.doEndTag();
    	assertEquals(result, TagSupport.EVAL_PAGE);
    }
    

    public void testArrayIterator() {
        Foo foo = new Foo();
        foo.setArray(new String[]{"test1", "test2", "test3"});

        stack.push(foo);

        tag.setValue("array");

        iterateThreeStrings();
    }

    public void testCollectionIterator() {
        Foo foo = new Foo();
        ArrayList list = new ArrayList();
        list.add("test1");
        list.add("test2");
        list.add("test3");
        foo.setList(list);

        stack.push(foo);

        tag.setValue("list");

        iterateThreeStrings();
    }

    public void testIteratorWithDefaultValue() {
        stack.push(new String[]{"test1", "test2", "test3"});
        iterateThreeStrings();
    }

    public void testMapIterator() {
        Foo foo = new Foo();
        HashMap map = new HashMap();
        map.put("test1", "123");
        map.put("test2", "456");
        map.put("test3", "789");
        foo.setMap(map);

        stack.push(foo);

        tag.setValue("map");

        int result = 0;

        try {
            result = tag.doStartTag();
        } catch (JspException e) {
            e.printStackTrace();
            fail();
        }

        assertEquals(TagSupport.EVAL_BODY_INCLUDE, result);
        assertEquals(4, stack.size());
        assertTrue(stack.getRoot().peek() instanceof Map.Entry);

        try {
            result = tag.doAfterBody();
        } catch (JspException e) {
            e.printStackTrace();
            fail();
        }

        assertEquals(TagSupport.EVAL_BODY_AGAIN, result);
        assertEquals(4, stack.size());
        assertTrue(stack.getRoot().peek() instanceof Map.Entry);

        try {
            result = tag.doAfterBody();
        } catch (JspException e) {
            e.printStackTrace();
            fail();
        }

        assertEquals(TagSupport.EVAL_BODY_AGAIN, result);
        assertEquals(4, stack.size());
        assertTrue(stack.getRoot().peek() instanceof Map.Entry);

        try {
            result = tag.doAfterBody();
        } catch (JspException e) {
            e.printStackTrace();
            fail();
        }

        assertEquals(TagSupport.SKIP_BODY, result);
        assertEquals(3, stack.size());
    }

    public void testStatus() {
        Foo foo = new Foo();
        foo.setArray(new String[]{"test1", "test2", "test3"});

        stack.push(foo);

        tag.setValue("array");
        tag.setStatus("fooStatus");

        int result = 0;

        try {
            result = tag.doStartTag();
        } catch (JspException e) {
            e.printStackTrace();
            fail();
        }

        assertEquals(result, TagSupport.EVAL_BODY_INCLUDE);
        assertEquals("test1", stack.getRoot().peek());
        assertEquals(4, stack.size());

        IteratorStatus status = (IteratorStatus) context.get("fooStatus");
        assertNotNull(status);
        assertFalse(status.isLast());
        assertTrue(status.isFirst());
        assertEquals(0, status.getIndex());
        assertEquals(1, status.getCount());
        assertTrue(status.isOdd());
        assertFalse(status.isEven());

        try {
            result = tag.doAfterBody();
        } catch (JspException e) {
            e.printStackTrace();
            fail();
        }

        assertEquals(result, TagSupport.EVAL_BODY_AGAIN);
        assertEquals("test2", stack.getRoot().peek());
        assertEquals(4, stack.size());

        status = (IteratorStatus) context.get("fooStatus");
        assertNotNull(status);
        assertFalse(status.isLast());
        assertFalse(status.isFirst());
        assertEquals(1, status.getIndex());
        assertEquals(2, status.getCount());
        assertFalse(status.isOdd());
        assertTrue(status.isEven());

        try {
            result = tag.doAfterBody();
        } catch (JspException e) {
            e.printStackTrace();
            fail();
        }

        assertEquals(result, TagSupport.EVAL_BODY_AGAIN);
        assertEquals("test3", stack.getRoot().peek());
        assertEquals(4, stack.size());

        status = (IteratorStatus) context.get("fooStatus");
        assertNotNull(status);
        assertTrue(status.isLast());
        assertFalse(status.isFirst());
        assertEquals(2, status.getIndex());
        assertEquals(3, status.getCount());
        assertTrue(status.isOdd());
        assertFalse(status.isEven());
    }

    public void testEmptyArray() {
        Foo foo = new Foo();
        foo.setArray(new String[]{});

        stack.push(foo);

        tag.setValue("array");

        validateSkipBody();
    }

    public void testNullArray() {
        Foo foo = new Foo();
        foo.setArray(null);

        stack.push(foo);

        tag.setValue("array");

        validateSkipBody();
    }

    public void testEmptyCollection() {
        Foo foo = new Foo();
        foo.setList(new ArrayList());

        stack.push(foo);

        tag.setValue("list");

        validateSkipBody();
    }

    public void testNullCollection() {
        Foo foo = new Foo();
        foo.setList(null);

        stack.push(foo);

        tag.setValue("list");

        validateSkipBody();
    }

    protected void setUp() throws Exception {
        super.setUp();

        // create the needed objects
        tag = new IteratorTag();

        MockBodyContent mockBodyContent = new TestMockBodyContent();
        mockBodyContent.setupGetEnclosingWriter(new MockJspWriter());
        tag.setBodyContent(mockBodyContent);

        // associate the tag with the mock page request
        tag.setPageContext(pageContext);
    }

    private void iterateThreeStrings() {
        int result = 0;

        try {
            result = tag.doStartTag();
        } catch (JspException e) {
            e.printStackTrace();
            fail();
        }

        assertEquals(result, TagSupport.EVAL_BODY_INCLUDE);
        assertEquals("test1", stack.getRoot().peek());
        assertEquals(4, stack.size());

        try {
            result = tag.doAfterBody();
        } catch (JspException e) {
            e.printStackTrace();
            fail();
        }

        assertEquals(result, TagSupport.EVAL_BODY_AGAIN);
        assertEquals("test2", stack.getRoot().peek());
        assertEquals(4, stack.size());

        try {
            result = tag.doAfterBody();
        } catch (JspException e) {
            e.printStackTrace();
            fail();
        }

        assertEquals(result, TagSupport.EVAL_BODY_AGAIN);
        assertEquals("test3", stack.getRoot().peek());
        assertEquals(4, stack.size());

        try {
            result = tag.doAfterBody();
        } catch (JspException e) {
            e.printStackTrace();
            fail();
        }

        assertEquals(result, TagSupport.SKIP_BODY);
        assertEquals(3, stack.size());
    }

    private void validateSkipBody() {
        int result = 0;

        try {
            result = tag.doStartTag();
        } catch (JspException e) {
            e.printStackTrace();
            fail();
        }

        assertEquals(result, TagSupport.SKIP_BODY);
        try {
            result = tag.doEndTag();
        } catch (JspException e) {
            e.printStackTrace();
            fail();
        }
    }

    class Foo {
        private Collection list;
        private Map map;
        private String[] array;

        public void setArray(String[] array) {
            this.array = array;
        }

        public String[] getArray() {
            return array;
        }

        public void setList(Collection list) {
            this.list = list;
        }

        public Collection getList() {
            return list;
        }

        public void setMap(Map map) {
            this.map = map;
        }

        public Map getMap() {
            return map;
        }
    }

    class TestMockBodyContent extends MockBodyContent {
        public String getString() {
            return ".-.";
        }
    }
}
