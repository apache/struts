/*
 * Copyright (c) 2002-2003 by OpenSymphony
 * All rights reserved.
 */
package org.apache.struts.action2.portlet.util;

import java.util.ArrayList;
import java.util.List;

import javax.servlet.RequestDispatcher;
import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;

import org.springframework.mock.web.MockHttpServletRequest;
import org.springframework.mock.web.MockHttpServletResponse;
import org.springframework.mock.web.MockRequestDispatcher;

import org.apache.struts.action2.TestAction;
import org.apache.struts.action2.util.ListEntry;
import org.apache.struts.action2.util.StrutsUtil;
import com.opensymphony.xwork.util.OgnlValueStack;

import junit.framework.TestCase;

/**
 * Test case for StrutsUtil.
 * 
 * @author tm_jee
 * @version $Date$ $Id$
 */
public class StrutsUtilTest extends TestCase {
	
	protected OgnlValueStack stack = null;
	protected InternalMockHttpServletRequest request = null;
	protected MockHttpServletResponse response = null;
	protected StrutsUtil strutsUtil = null;
	
	public void testBeanMethod() throws Exception {
		Object o = strutsUtil.bean("org.apache.struts.action2.TestAction");
		assertNotNull(o);
		assertTrue(o instanceof TestAction);
	}
	
	public void testIsTrueMethod() throws Exception {
		stack.push(new Object() {
			public String getMyString() {
				return "myString";
			}
			public boolean getMyBoolean(boolean bool) {
				return bool;
			}
		});
		assertTrue(strutsUtil.isTrue("myString == 'myString'"));
		assertFalse(strutsUtil.isTrue("myString == 'myOtherString'"));
		assertTrue(strutsUtil.isTrue("getMyBoolean(true)"));
		assertFalse(strutsUtil.isTrue("getMyBoolean(false)"));
	}
	
	public void testFindStringMethod() throws Exception {
		stack.push(new Object() {
			public String getMyString() {
				return "myString";
			}
			public boolean getMyBoolean(boolean bool) {
				return bool;
			}
		});
		
		assertEquals(strutsUtil.findString("myString"), "myString");
		assertNull(strutsUtil.findString("myOtherString"));
		assertEquals(strutsUtil.findString("getMyBoolean(true)"), "true");
	}
	
	public void testIncludeMethod() throws Exception {
		strutsUtil.include("/some/includedJspFile.jsp");
		
		// with include, this must have been created and should not be null
		assertNotNull(request.getDispatcher()); 
		// this must be true, indicating we actaully ask container to do an include
		assertTrue(request.getDispatcher().included);
	}
	
	
	public void testTextToHtmlMethod() throws Exception {
		assertEquals(
				strutsUtil.textToHtml("<html><head><title>some title</title><body>some content</body></html>"), 
				"&lt;html&gt;&lt;head&gt;&lt;title&gt;some title&lt;/title&gt;&lt;body&gt;some content&lt;/body&gt;&lt;/html&gt;");
	}
	
	public void testUrlEncodeMethod() throws Exception {
		assertEquals(
				strutsUtil.urlEncode("http://www.opensymphony.com/action2/index.jsp?param1=value1"), 
				"http%3A%2F%2Fwww.opensymphony.com%2Faction2%2Findex.jsp%3Fparam1%3Dvalue1");
	}
	
	public void testBuildUrlMethod() throws Exception {
		request.setContextPath("/myContextPath");
		assertEquals(strutsUtil.buildUrl("/someUrl?param1=value1"), "/myContextPath/someUrl?param1=value1");
	}
	
	
	public void testFindValueMethod() throws Exception {
		stack.push(new Object() {
			public String getMyString() {
				return "myString";
			}
			public boolean getMyBoolean(boolean bool) {
				return bool;
			}
		});
		Object obj1 = strutsUtil.findValue("myString", "java.lang.String");
		Object obj2 = strutsUtil.findValue("getMyBoolean(true)", "java.lang.Boolean");
		
		assertNotNull(obj1);
		assertNotNull(obj2);
		assertTrue(obj1 instanceof String);
		assertTrue(obj2 instanceof Boolean);
		assertEquals(obj1, "myString");
		assertEquals(obj2, Boolean.TRUE);
	}
	
	

	public void testGetTextMethod() throws Exception {
		// this should be in xwork-messages.properties (included by default 
		// by LocalizedTextUtil
		assertNotNull(strutsUtil.getText("xwork.error.action.execution"));
		assertEquals(strutsUtil.getText("xwork.error.action.execution"), "Error during Action invocation");
	}
	
	
	public void testGetContextMethod() throws Exception {
		request.setContextPath("/myContext");
		assertEquals(strutsUtil.getContext(), "/myContext");
	}
	
	
	public void testMakeSelectListMethod() throws Exception {
		String[] selectedList = new String[] { "Car", "Airplane", "Bus" };
		List list = new ArrayList();
		list.add("Lorry");
		list.add("Car");
		list.add("Helicopter");
		
		stack.getContext().put("mySelectedList", selectedList);
		stack.getContext().put("myList", list);
		
		List listMade = strutsUtil.makeSelectList("#mySelectedList", "#myList", null, null);
		
		assertEquals(listMade.size(), 3);
		assertEquals(((ListEntry)listMade.get(0)).getKey(), "Lorry");
		assertEquals(((ListEntry)listMade.get(0)).getValue(), "Lorry");
		assertEquals(((ListEntry)listMade.get(0)).getIsSelected(), false);
		assertEquals(((ListEntry)listMade.get(1)).getKey(), "Car");
		assertEquals(((ListEntry)listMade.get(1)).getValue(), "Car");
		assertEquals(((ListEntry)listMade.get(1)).getIsSelected(), true);
		assertEquals(((ListEntry)listMade.get(2)).getKey(), "Helicopter");
		assertEquals(((ListEntry)listMade.get(2)).getValue(), "Helicopter");
		assertEquals(((ListEntry)listMade.get(2)).getIsSelected(), false);
	}
	
	
	public void testHtmlEncode() throws Exception {
		assertEquals(
				strutsUtil.htmlEncode("<html><head><title>some title</title><body>some content</body></html>"), 
				"&lt;html&gt;&lt;head&gt;&lt;title&gt;some title&lt;/title&gt;&lt;body&gt;some content&lt;/body&gt;&lt;/html&gt;");
	}
	

	public void testToInt() throws Exception {
		assertEquals(strutsUtil.toInt(11l), 11);
	}
	
	
	public void testToLong() throws Exception {
		assertEquals(strutsUtil.toLong(11), 11l);
	}
	
	
	public void testToString() throws Exception {
		assertEquals(strutsUtil.toString(1), "1");
		assertEquals(strutsUtil.toString(11l), "11");
	}
	
	
	// === Junit Hook
	
	protected void setUp() throws Exception {
		super.setUp();
		stack = new OgnlValueStack();
		request = new InternalMockHttpServletRequest();
		response = new MockHttpServletResponse();
		strutsUtil = new StrutsUtil(stack, request, response);
	}
	
	protected void tearDown() throws Exception {
		stack = null;
		request = null;
		response = null;
		strutsUtil = null;
		super.tearDown();
	}
	
	
	
	// === internal class to assist in testing
	
	class InternalMockHttpServletRequest extends MockHttpServletRequest {
		InternalMockRequestDispatcher dispatcher = null;
		public RequestDispatcher getRequestDispatcher(String path) {
			dispatcher = new InternalMockRequestDispatcher(path);
			return dispatcher;
		}
		
		public InternalMockRequestDispatcher getDispatcher() { 
			return dispatcher;
		}
	}
	
	class InternalMockRequestDispatcher extends MockRequestDispatcher {
		private String url;
		boolean included = false;
		public InternalMockRequestDispatcher(String url) {
			super(url);
			this.url = url;
		}
		public void include(ServletRequest servletRequest, ServletResponse servletResponse) {
			if (servletResponse instanceof MockHttpServletResponse) {
				((MockHttpServletResponse) servletResponse).setIncludedUrl(this.url);
			}
			included = true;
		}
	}
	
}
