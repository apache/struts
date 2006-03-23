/*
 * Copyright (c) 2002-2006 by OpenSymphony
 * All rights reserved.
 */
package com.opensymphony.webwork.views.jsp.ui;

import java.io.ByteArrayOutputStream;
import java.io.InputStream;

import com.mockobjects.servlet.MockHttpServletResponse;
import com.opensymphony.webwork.ServletActionContext;
import com.opensymphony.webwork.views.jsp.AbstractUITagTest;
import com.opensymphony.xwork.mock.MockActionInvocation;
import com.opensymphony.xwork.util.OgnlValueStack;

import junit.framework.TestCase;

/**
 * Abstract test case for all RichTextEditor based test case. (contains common methods)
 * 
 * @author tm_jee
 * @version $Date: 2006/03/04 15:07:50 $ $Id: AbstractRichtexteditorTest.java,v 1.4 2006/03/04 15:07:50 rainerh Exp $
 */
public abstract class AbstractRichtexteditorTest extends TestCase {
	
	protected MockActionInvocation invocation;
	protected MockHttpServletResponse response;
	
	protected void setUp() throws Exception {
		super.setUp();
		//ActionProxy actionProxy = ActionProxyFactory.getFactory().createActionProxy("namespace", "actionName", new HashMap());
		//ActionProxy actionProxy = new MockActionProxy();
		//invocation = ActionProxyFactory.getFactory().createActionInvocation(actionProxy);
		invocation = new MockActionInvocation();
		invocation.setStack(new OgnlValueStack());
		response = new MockHttpServletResponse();
		ServletActionContext.setResponse(response);
	}
	
	protected void tearDown() throws Exception {
		super.tearDown();
		invocation = null;
		response = null;
	}
	
	
	protected void verify(InputStream is) throws Exception {
		String result1 = response.getOutputStreamContents();
		
		ByteArrayOutputStream baos = new ByteArrayOutputStream();
		byte[] tmpBytes = new byte[1024];
		while(is.read(tmpBytes) != -1) {
			baos.write(tmpBytes);
		}
		String result2 = new String(baos.toByteArray());
		baos.close();
		
		//System.out.println("*** result1 (p)="+result1);
		//System.out.println("*** result2 (p)="+result2);
		
		assertEquals(AbstractUITagTest.normalize(result2.trim(), false), AbstractUITagTest.normalize(result1.trim(), false));
	}
}
