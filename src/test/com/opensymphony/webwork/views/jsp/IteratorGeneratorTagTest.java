/*
 * Copyright (c) 2002-2003 by OpenSymphony
 * All rights reserved.
 */
package com.opensymphony.webwork.views.jsp;

import java.util.Iterator;

import com.opensymphony.webwork.util.IteratorGenerator.Converter;
import com.opensymphony.webwork.views.jsp.iterator.IteratorGeneratorTag;
import com.opensymphony.xwork.Action;
import com.opensymphony.xwork.ActionSupport;

/**
 * Test case for IteratorGeneratorTag.
 * 
 * @author tm_jee ( tm_jee(at)yahoo.co.uk )
 * @version $Date: 2005/11/21 16:49:32 $ $Id: IteratorGeneratorTagTest.java,v 1.1 2005/11/21 16:49:32 tmjee Exp $
 */
public class IteratorGeneratorTagTest extends AbstractTagTest {

	public void testGeneratorBasic() throws Exception {
		IteratorGeneratorTag tag = new IteratorGeneratorTag();
		
		tag.setPageContext(pageContext);
		tag.setVal("%{'aaa,bbb,ccc,ddd,eee'}");
		tag.doStartTag();
		Object topOfStack = stack.findValue("top");
		
		
		assertTrue(topOfStack instanceof Iterator);
		// 1
		assertTrue(((Iterator)topOfStack).hasNext());
		assertEquals(((Iterator)topOfStack).next(), "aaa");
		// 2
		assertTrue(((Iterator)topOfStack).hasNext());
		assertEquals(((Iterator)topOfStack).next(), "bbb");
		// 3
		assertTrue(((Iterator)topOfStack).hasNext());
		assertEquals(((Iterator)topOfStack).next(), "ccc");
		// 4
		assertTrue(((Iterator)topOfStack).hasNext());
		assertEquals(((Iterator)topOfStack).next(), "ddd");
		// 5
		assertTrue(((Iterator)topOfStack).hasNext());
		assertEquals(((Iterator)topOfStack).next(),"eee");
		
		assertFalse(((Iterator)topOfStack).hasNext());
		
		tag.doEndTag();
		Object afterTopOfStack = stack.findValue("top");
		
		
		assertNotSame(afterTopOfStack, topOfStack);
	}
	
	public void testGeneratorWithSeparator() throws Exception {
		IteratorGeneratorTag tag = new IteratorGeneratorTag();
		
		tag.setPageContext(pageContext);
		tag.setVal("%{'aaa|bbb|ccc|ddd|eee'}");
		tag.setSeparator("|");
		tag.doStartTag();
		Object topOfStack = stack.findValue("top");
		tag.doEndTag();
		Object afterTopOfStack = stack.findValue("top");
		
		assertTrue(topOfStack instanceof Iterator);
		// 1
		assertTrue(((Iterator)topOfStack).hasNext());
		assertEquals(((Iterator)topOfStack).next(), "aaa");
		// 2 
		assertTrue(((Iterator)topOfStack).hasNext());
		assertEquals(((Iterator)topOfStack).next(), "bbb");
		// 3
		assertTrue(((Iterator)topOfStack).hasNext());
		assertEquals(((Iterator)topOfStack).next(), "ccc");
		// 4
		assertTrue(((Iterator)topOfStack).hasNext());
		assertEquals(((Iterator)topOfStack).next(), "ddd");
		// 5
		assertTrue(((Iterator)topOfStack).hasNext());
		assertEquals(((Iterator)topOfStack).next(), "eee");
		
		assertFalse(((Iterator)topOfStack).hasNext());
		assertNotSame(afterTopOfStack, topOfStack);
	}
	
	public void testGeneratorWithConverter() throws Exception {
		IteratorGeneratorTag tag = new IteratorGeneratorTag();
		
		tag.setPageContext(pageContext);
		tag.setVal("%{'aaa, bbb, ccc, ddd, eee'}");
		tag.setConverter("myConverter");
		tag.doStartTag();
		Object topOfStack = stack.findValue("top");
		tag.doEndTag();
		Object afterTopOfStack = stack.findValue("top");
		
		assertTrue(topOfStack instanceof Iterator);
		// 1.
		assertTrue(((Iterator)topOfStack).hasNext());
		assertEquals(((Iterator)topOfStack).next(), "myConverter-aaa");
		// 2
		assertTrue(((Iterator)topOfStack).hasNext());
		assertEquals(((Iterator)topOfStack).next(), "myConverter-bbb");
		// 3
		assertTrue(((Iterator)topOfStack).hasNext());
		assertEquals(((Iterator)topOfStack).next(), "myConverter-ccc");
		// 4.
		assertTrue(((Iterator)topOfStack).hasNext());
		assertEquals(((Iterator)topOfStack).next(), "myConverter-ddd");
		// 5.
		assertTrue(((Iterator)topOfStack).hasNext());
		assertEquals(((Iterator)topOfStack).next(), "myConverter-eee");
		
		assertFalse(((Iterator)topOfStack).hasNext());
		assertNotSame(afterTopOfStack, topOfStack);
	}
	
	public void testGeneratorWithId() throws Exception {
		IteratorGeneratorTag tag = new IteratorGeneratorTag();
		tag.setPageContext(pageContext);
		tag.setVal("%{'aaa,bbb,ccc,ddd,eee'}");
		tag.setId("myPageContextAttId");
		tag.doStartTag();
		tag.doEndTag();
		
		Object pageContextIterator = pageContext.getAttribute("myPageContextAttId"); 
		
		assertTrue(pageContextIterator instanceof Iterator);
		// 1
		assertTrue(((Iterator)pageContextIterator).hasNext());
		assertEquals(((Iterator)pageContextIterator).next(), "aaa");
		// 2.
		assertTrue(((Iterator)pageContextIterator).hasNext());
		assertEquals(((Iterator)pageContextIterator).next(), "bbb");
		// 3.
		assertTrue(((Iterator)pageContextIterator).hasNext());
		assertEquals(((Iterator)pageContextIterator).next(), "ccc");
		// 4
		assertTrue(((Iterator)pageContextIterator).hasNext());
		assertEquals(((Iterator)pageContextIterator).next(), "ddd");
		// 5
		assertTrue(((Iterator)pageContextIterator).hasNext());
		assertEquals(((Iterator)pageContextIterator).next(), "eee");
		
		assertFalse(((Iterator)pageContextIterator).hasNext());
	}
	
	public void testGeneratorWithCount() throws Exception {
		IteratorGeneratorTag tag = new IteratorGeneratorTag();
		
		tag.setPageContext(pageContext);
		tag.setVal("%{'aaa,bbb,ccc,ddd,eee'}");
		tag.setCount("myCount");
		tag.doStartTag();
		Object topOfStack = stack.findValue("top");
		tag.doEndTag();
		Object afterTopOfStack = stack.findValue("top");
		
		
		assertTrue(topOfStack instanceof Iterator);
		// 1
		assertTrue(((Iterator)topOfStack).hasNext());
		assertEquals(((Iterator)topOfStack).next(), "aaa");
		// 2
		assertTrue(((Iterator)topOfStack).hasNext());
		assertEquals(((Iterator)topOfStack).next(), "bbb");
		// 3.
		assertTrue(((Iterator)topOfStack).hasNext());
		assertEquals(((Iterator)topOfStack).next(), "ccc");
		
		assertFalse(((Iterator)topOfStack).hasNext());
		assertNotSame(topOfStack, afterTopOfStack);
	}
	
	
	public Action getAction() {
		return new ActionSupport() {
			public Converter getMyConverter() {
				return new Converter() {
					public Object convert(String value) throws Exception {
						return "myConverter-"+value;
					}
				};
			}
			
			public int getMyCount() {
				return 3;
			}
		};
	}
}

