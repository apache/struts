/*
 * Copyright (c) 2002-2003 by OpenSymphony
 * All rights reserved.
 */
package com.opensymphony.webwork.views.jsp;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import com.opensymphony.webwork.views.jsp.iterator.AppendIteratorTag;
import com.opensymphony.xwork.Action;
import com.opensymphony.xwork.ActionSupport;

/**
 * Test case for AppendIteratorTag.
 * @author tmjee
 * @version $Date: 2005/11/26 04:46:46 $ $Id: AppendIteratorTagTest.java,v 1.1 2005/11/26 04:46:46 tmjee Exp $
 */
public class AppendIteratorTagTest extends AbstractTagTest {

	
	public void testAppendingIteratorUsingArrayAsSource() throws Exception {
		AppendIteratorTag tag = new AppendIteratorTag();
		tag.setPageContext(pageContext);
		tag.setId("myAppendedIterator");
		
		ParamTag iterator1ParamTag = new ParamTag();
		iterator1ParamTag.setPageContext(pageContext);
		iterator1ParamTag.setValue("%{myArr1}");
		
		ParamTag iterator2ParamTag = new ParamTag();
		iterator2ParamTag.setPageContext(pageContext);
		iterator2ParamTag.setValue("%{myArr2}");
		
		ParamTag iterator3ParamTag = new ParamTag();
		iterator3ParamTag.setPageContext(pageContext);
		iterator3ParamTag.setValue("%{myArr3}");
		
		
		tag.doStartTag();
		iterator1ParamTag.doStartTag();
		iterator1ParamTag.doEndTag();
		iterator2ParamTag.doStartTag();
		iterator2ParamTag.doEndTag();
		iterator3ParamTag.doStartTag();
		iterator3ParamTag.doEndTag();
		tag.doEndTag();
		
		Iterator appendedIterator = (Iterator) stack.findValue("#myAppendedIterator");
		
		assertNotNull(appendedIterator);
		assertTrue(appendedIterator.hasNext());
		assertEquals(appendedIterator.next(), "1");
		assertTrue(appendedIterator.hasNext());
		assertEquals(appendedIterator.next(), "2");
		assertTrue(appendedIterator.hasNext());
		assertEquals(appendedIterator.next(), "3");
		assertTrue(appendedIterator.hasNext());
		assertEquals(appendedIterator.next(), "a");
		assertTrue(appendedIterator.hasNext());
		assertEquals(appendedIterator.next(), "b");
		assertTrue(appendedIterator.hasNext());
		assertEquals(appendedIterator.next(), "c");
		assertTrue(appendedIterator.hasNext());
		assertEquals(appendedIterator.next(), "A");
		assertTrue(appendedIterator.hasNext());
		assertEquals(appendedIterator.next(), "B");
		assertTrue(appendedIterator.hasNext());
		assertEquals(appendedIterator.next(), "C");
		assertFalse(appendedIterator.hasNext());
	}
	
	public void testAppendingIteratorsUsingListAsSource() throws Exception {
		AppendIteratorTag tag = new AppendIteratorTag();
		tag.setPageContext(pageContext);
		tag.setId("myAppendedIterator");
		
		ParamTag iterator1ParamTag = new ParamTag();
		iterator1ParamTag.setPageContext(pageContext);
		iterator1ParamTag.setValue("%{myList1}");
		
		ParamTag iterator2ParamTag = new ParamTag();
		iterator2ParamTag.setPageContext(pageContext);
		iterator2ParamTag.setValue("%{myList2}");
		
		ParamTag iterator3ParamTag = new ParamTag();
		iterator3ParamTag.setPageContext(pageContext);
		iterator3ParamTag.setValue("%{myList3}");
		
		
		tag.doStartTag();
		iterator1ParamTag.doStartTag();
		iterator1ParamTag.doEndTag();
		iterator2ParamTag.doStartTag();
		iterator2ParamTag.doEndTag();
		iterator3ParamTag.doStartTag();
		iterator3ParamTag.doEndTag();
		tag.doEndTag();
		
		Iterator appendedIterator = (Iterator) stack.findValue("#myAppendedIterator");
		
		assertNotNull(appendedIterator);
		assertTrue(appendedIterator.hasNext());
		assertEquals(appendedIterator.next(), "1");
		assertTrue(appendedIterator.hasNext());
		assertEquals(appendedIterator.next(), "2");
		assertTrue(appendedIterator.hasNext());
		assertEquals(appendedIterator.next(), "3");
		assertTrue(appendedIterator.hasNext());
		assertEquals(appendedIterator.next(), "a");
		assertTrue(appendedIterator.hasNext());
		assertEquals(appendedIterator.next(), "b");
		assertTrue(appendedIterator.hasNext());
		assertEquals(appendedIterator.next(), "c");
		assertTrue(appendedIterator.hasNext());
		assertEquals(appendedIterator.next(), "A");
		assertTrue(appendedIterator.hasNext());
		assertEquals(appendedIterator.next(), "B");
		assertTrue(appendedIterator.hasNext());
		assertEquals(appendedIterator.next(), "C");
		assertFalse(appendedIterator.hasNext());
	}
	

	
	public Action getAction() {
		return new ActionSupport() {
			public List getMyList1() {
				List l = new ArrayList();
				l.add("1");
				l.add("2");
				l.add("3");
				return l;
			}
			
			public List getMyList2() {
				List l = new ArrayList();
				l.add("a");
				l.add("b");
				l.add("c");
				return l;
			}
			
			public List getMyList3() {
				List l = new ArrayList(0);
				l.add("A");
				l.add("B");
				l.add("C");
				return l;
			}
			
			public String[] getMyArr1() {
				return new String[] { "1", "2", "3" };
			}
			
			public String[] getMyArr2() {
				return new String[] { "a", "b", "c" };
			}
			
			public String[] getMyArr3() {
				return new String[] { "A", "B", "C" };
			}
		};
	}
}
