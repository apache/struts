/*
 * Copyright (c) 2002-2003 by OpenSymphony
 * All rights reserved.
 */
package org.apache.struts.webwork.dispatcher;

import java.io.InputStream;
import java.io.PrintWriter;
import java.io.StringWriter;

import com.opensymphony.util.ClassLoaderUtil;
import org.apache.struts.webwork.StrutsStatics;
import org.apache.struts.webwork.views.jsp.AbstractUITagTest;
import org.apache.struts.webwork.views.jsp.StrutsMockHttpServletResponse;
import org.apache.struts.webwork.views.jsp.StrutsMockServletContext;
import com.opensymphony.xwork.ActionContext;
import com.opensymphony.xwork.mock.MockActionInvocation;
import com.opensymphony.xwork.util.OgnlValueStack;

import junit.framework.TestCase;

/**
 * Test case for PlainTextResult.
 * 
 * @author tm_jee
 * @version $Date: 2006/03/11 15:38:08 $ $Id: PlainTextResultTest.java,v 1.2 2006/03/11 15:38:08 tmjee Exp $
 */
public class PlainTextResultTest extends TestCase {
	
	OgnlValueStack stack;
	MockActionInvocation invocation;
	ActionContext context;
	StrutsMockHttpServletResponse response;
	PrintWriter writer;
	StringWriter stringWriter;
	StrutsMockServletContext servletContext;
	

	public void testPlainText() throws Exception {
		PlainTextResult result = new PlainTextResult();
		result.setLocation("/someJspFile.jsp");
		
		response.setExpectedContentType("text/plain");
		response.setExpectedHeader("Content-Disposition", "inline");
		InputStream jspResourceInputStream = 
			ClassLoaderUtil.getResourceAsStream(
				"org/apache/struts/action2/dispatcher/someJspFile.jsp", 
				PlainTextResultTest.class);
		
		
		try {
			servletContext.setResourceAsStream(jspResourceInputStream);
			result.execute(invocation);
			
			String r = AbstractUITagTest.normalize(stringWriter.getBuffer().toString(), true);
			String e = AbstractUITagTest.normalize(
					readAsString("org/apache/struts/action2/dispatcher/someJspFile.jsp"), true);
			assertEquals(r, e);
		}
		finally {
			jspResourceInputStream.close();
		}
	}
	
	public void testPlainTextWithEncoding() throws Exception {
		PlainTextResult result = new PlainTextResult();
		result.setLocation("/someJspFile.jsp");
		result.setCharSet("UTF-8");
		
		response.setExpectedContentType("text/plain; charset=UTF-8");
		response.setExpectedHeader("Content-Disposition", "inline");
		InputStream jspResourceInputStream = 
			ClassLoaderUtil.getResourceAsStream(
				"org/apache/struts/webwork/dispatcher/someJspFile.jsp", 
				PlainTextResultTest.class);
		
		
		try {
			servletContext.setResourceAsStream(jspResourceInputStream);
			result.execute(invocation);
			
			String r = AbstractUITagTest.normalize(stringWriter.getBuffer().toString(), true);
			String e = AbstractUITagTest.normalize(
					readAsString("org/apache/struts/webwork/dispatcher/someJspFile.jsp"), true);
			assertEquals(r, e);
		}
		finally {
			jspResourceInputStream.close();
		}
	}
	
	protected String readAsString(String resource) throws Exception {
		InputStream is = null;
		try {
			is = ClassLoaderUtil.getResourceAsStream(resource, PlainTextResultTest.class);
			int sizeRead = 0;
			byte[] buffer = new byte[1024];
			StringBuffer stringBuffer = new StringBuffer();
			while((sizeRead = is.read(buffer)) != -1) {
				stringBuffer.append(new String(buffer, 0, sizeRead));
			}
			return stringBuffer.toString();
		}
		finally {
			if (is != null) 
				is.close();
		}
	
	}
	
	
	protected void setUp() throws Exception {
		super.setUp();
		
		stringWriter = new StringWriter();
		writer = new PrintWriter(stringWriter);
		response = new StrutsMockHttpServletResponse();
		response.setWriter(writer);
		servletContext = new StrutsMockServletContext();
		stack = new OgnlValueStack();
		context = new ActionContext(stack.getContext());
		context.put(StrutsStatics.HTTP_RESPONSE, response);
		context.put(StrutsStatics.SERVLET_CONTEXT, servletContext);
		invocation = new MockActionInvocation();
		invocation.setStack(stack);
		invocation.setInvocationContext(context);
	}
	
	
	protected void tearDown() throws Exception {
		stack = null;
		invocation = null;
		context = null;
		response = null;
		writer = null;
		stringWriter = null;
		servletContext = null;
		
		super.tearDown();
	}
}
