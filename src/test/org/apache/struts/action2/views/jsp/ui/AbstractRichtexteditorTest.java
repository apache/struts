/*
 * $Id$
 *
 * Copyright 2006 The Apache Software Foundation.
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
package org.apache.struts.action2.views.jsp.ui;

import java.io.ByteArrayOutputStream;
import java.io.InputStream;

import com.mockobjects.servlet.MockHttpServletResponse;
import org.apache.struts.action2.ServletActionContext;
import org.apache.struts.action2.views.jsp.AbstractUITagTest;
import com.opensymphony.xwork.mock.MockActionInvocation;
import com.opensymphony.xwork.util.OgnlValueStack;

import junit.framework.TestCase;

/**
 * Abstract test case for all RichTextEditor based test case. (contains common methods)
 * 
 * @author tm_jee
 * @version $Date$ $Id$
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
