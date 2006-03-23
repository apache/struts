/*
 * Copyright (c) 2002-2003 by OpenSymphony
 * All rights reserved.
 */
package com.opensymphony.webwork.portlet.util;

import java.util.ArrayList;
import java.util.List;

import javax.servlet.RequestDispatcher;
import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;

import org.springframework.mock.web.MockHttpServletRequest;
import org.springframework.mock.web.MockHttpServletResponse;
import org.springframework.mock.web.MockRequestDispatcher;

import com.opensymphony.webwork.TestAction;
import com.opensymphony.webwork.util.ListEntry;
import com.opensymphony.webwork.util.WebWorkUtil;
import com.opensymphony.xwork.util.OgnlValueStack;

import junit.framework.TestCase;

/**
 * Test case for WebWorkUtil.
 * 
 * @author tm_jee
 * @version $Date: 2006/03/14 16:48:21 $ $Id: WebWorkUtilTest.java,v 1.1 2006/03/14 16:48:21 tmjee Exp $
 */
public class WebWorkUtilTest extends TestCase {
	
	protected OgnlValueStack stack = null;
	protected InternalMockHttpServletRequest request = null;
	protected MockHttpServletResponse response = null;
	protected WebWorkUtil webWorkUtil = null;
	
	public void testBeanMethod() throws Exception {
		Object o = webWorkUtil.bean("com.opensymphony.webwork.TestAction");
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
		assertTrue(webWorkUtil.isTrue("myString == 'myString'"));
		assertFalse(webWorkUtil.isTrue("myString == 'myOtherString'"));
		assertTrue(webWorkUtil.isTrue("getMyBoolean(true)"));
		assertFalse(webWorkUtil.isTrue("getMyBoolean(false)"));
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
		
		assertEquals(webWorkUtil.findString("myString"), "myString");
		assertNull(webWorkUtil.findString("myOtherString"));
		assertEquals(webWorkUtil.findString("getMyBoolean(true)"), "true");
	}
	
	public void testIncludeMethod() throws Exception {
		webWorkUtil.include("/some/includedJspFile.jsp");
		
		// with include, this must have been created and should not be null
		assertNotNull(request.getDispatcher()); 
		// this must be true, indicating we actaully ask container to do an include
		assertTrue(request.getDispatcher().included);
	}
	
	
	public void testTextToHtmlMethod() throws Exception {
		assertEquals(
				webWorkUtil.textToHtml("<html><head><title>some title</title><body>some content</body></html>"), 
				"&lt;html&gt;&lt;head&gt;&lt;title&gt;some title&lt;/title&gt;&lt;body&gt;some content&lt;/body&gt;&lt;/html&gt;");
	}
	
	public void testUrlEncodeMethod() throws Exception {
		assertEquals(
				webWorkUtil.urlEncode("http://www.opensymphony.com/webwork/index.jsp?param1=value1"), 
				"http%3A%2F%2Fwww.opensymphony.com%2Fwebwork%2Findex.jsp%3Fparam1%3Dvalue1");
	}
	
	public void testBuildUrlMethod() throws Exception {
		request.setContextPath("/myContextPath");
		assertEquals(webWorkUtil.buildUrl("/someUrl?param1=value1"), "/myContextPath/someUrl?param1=value1");
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
		Object obj1 = webWorkUtil.findValue("myString", "java.lang.String");
		Object obj2 = webWorkUtil.findValue("getMyBoolean(true)", "java.lang.Boolean");
		
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
		assertNotNull(webWorkUtil.getText("xwork.error.action.execution"));
		assertEquals(webWorkUtil.getText("xwork.error.action.execution"), "Error during Action invocation");
	}
	
	
	public void testGetContextMethod() throws Exception {
		request.setContextPath("/myContext");
		assertEquals(webWorkUtil.getContext(), "/myContext");
	}
	
	
	public void testMakeSelectListMethod() throws Exception {
		String[] selectedList = new String[] { "Car", "Airplane", "Bus" };
		List list = new ArrayList();
		list.add("Lorry");
		list.add("Car");
		list.add("Helicopter");
		
		stack.getContext().put("mySelectedList", selectedList);
		stack.getContext().put("myList", list);
		
		List listMade = webWorkUtil.makeSelectList("#mySelectedList", "#myList", null, null);
		
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
				webWorkUtil.htmlEncode("<html><head><title>some title</title><body>some content</body></html>"), 
				"&lt;html&gt;&lt;head&gt;&lt;title&gt;some title&lt;/title&gt;&lt;body&gt;some content&lt;/body&gt;&lt;/html&gt;");
	}
	

	public void testToInt() throws Exception {
		assertEquals(webWorkUtil.toInt(11l), 11);
	}
	
	
	public void testToLong() throws Exception {
		assertEquals(webWorkUtil.toLong(11), 11l);
	}
	
	
	public void testToString() throws Exception {
		assertEquals(webWorkUtil.toString(1), "1");
		assertEquals(webWorkUtil.toString(11l), "11");
	}
	
	
	// === Junit Hook
	
	protected void setUp() throws Exception {
		super.setUp();
		stack = new OgnlValueStack();
		request = new InternalMockHttpServletRequest();
		response = new MockHttpServletResponse();
		webWorkUtil = new WebWorkUtil(stack, request, response);
	}
	
	protected void tearDown() throws Exception {
		stack = null;
		request = null;
		response = null;
		webWorkUtil = null;
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
