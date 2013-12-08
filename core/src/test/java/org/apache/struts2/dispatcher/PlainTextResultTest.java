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

package org.apache.struts2.dispatcher;

import com.opensymphony.xwork2.ActionContext;
import com.opensymphony.xwork2.mock.MockActionInvocation;
import com.opensymphony.xwork2.util.ClassLoaderUtil;
import com.opensymphony.xwork2.util.ValueStack;
import org.apache.struts2.StrutsStatics;
import org.apache.struts2.StrutsInternalTestCase;
import org.apache.struts2.views.jsp.AbstractUITagTest;
import org.apache.struts2.views.jsp.StrutsMockHttpServletResponse;
import org.apache.struts2.views.jsp.StrutsMockServletContext;

import java.io.InputStream;
import java.io.PrintWriter;
import java.io.StringWriter;

/**
 * Test case for PlainTextResult.
 *
 */
public class PlainTextResultTest extends StrutsInternalTestCase {

    ValueStack stack;
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
                "org/apache/struts2/dispatcher/someJspFile.jsp",
                PlainTextResultTest.class);


        try {
            servletContext.setResourceAsStream(jspResourceInputStream);
            result.execute(invocation);

            String r = AbstractUITagTest.normalize(stringWriter.getBuffer().toString(), true);
            String e = AbstractUITagTest.normalize(
                    readAsString("org/apache/struts2/dispatcher/someJspFile.jsp"), true);
            assertEquals(r, e);
        }
        finally {
            jspResourceInputStream.close();
        }
    }

    public void testPlainTextWithoutSlash() throws Exception {
        PlainTextResult result = new PlainTextResult();
        result.setLocation("someJspFile.jsp");

        response.setExpectedContentType("text/plain");
        response.setExpectedHeader("Content-Disposition", "inline");
        InputStream jspResourceInputStream =
            ClassLoaderUtil.getResourceAsStream("org/apache/struts2/dispatcher/someJspFile.jsp", PlainTextResultTest.class);


        try {
            servletContext.setResourceAsStream(jspResourceInputStream);
            result.execute(invocation);

            String r = AbstractUITagTest.normalize(stringWriter.getBuffer().toString(), true);
            String e = AbstractUITagTest.normalize(readAsString("org/apache/struts2/dispatcher/someJspFile.jsp"), true);
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
                "org/apache/struts2/dispatcher/someJspFile.jsp",
                PlainTextResultTest.class);


        try {
            servletContext.setResourceAsStream(jspResourceInputStream);
            result.execute(invocation);

            String r = AbstractUITagTest.normalize(stringWriter.getBuffer().toString(), true);
            String e = AbstractUITagTest.normalize(
                    readAsString("org/apache/struts2/dispatcher/someJspFile.jsp"), true);
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
            StringBuilder stringBuilder = new StringBuilder();
            while((sizeRead = is.read(buffer)) != -1) {
                stringBuilder.append(new String(buffer, 0, sizeRead));
            }
            return stringBuilder.toString();
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
        stack = ActionContext.getContext().getValueStack();
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
