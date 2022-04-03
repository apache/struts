/*
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
package org.apache.struts2.result;

import com.opensymphony.xwork2.ActionContext;
import com.opensymphony.xwork2.Result;
import com.opensymphony.xwork2.mock.MockActionInvocation;
import org.apache.struts2.StrutsException;
import org.apache.struts2.StrutsInternalTestCase;
import org.apache.struts2.result.plain.ResponseBuilder;
import org.springframework.mock.web.MockHttpServletResponse;

public class PlainResultTest extends StrutsInternalTestCase {

    private MockHttpServletResponse response;
    private MockActionInvocation invocation;

    public void testWritePlainText() throws Exception {
        PlainResult result = (PlainResult) response ->
            response.write("test").withContentTypeTextPlain();

        result.execute(invocation);

        assertEquals("test", response.getContentAsString());
        assertEquals("text/plain; charset=UTF-8", response.getContentType());
    }

    public void testWritePlainHtml() throws Exception {
        PlainResult result = (PlainResult) response ->
            response.write("<b>test</b>").withContentTypeTextHtml();

        result.execute(invocation);

        assertEquals("<b>test</b>", response.getContentAsString());
        assertEquals("text/html; charset=UTF-8", response.getContentType());
    }

    public void testWriteJson() throws Exception {
        PlainResult result = (PlainResult) response ->
            response.write("{ 'value': 'test' }").withContentTypeJson();

        result.execute(invocation);

        assertEquals("{ 'value': 'test' }", response.getContentAsString());
        assertEquals("application/json", response.getContentType());
    }

    public void testWriteContentTypeCsvWithCookie() throws Exception {
        PlainResult result = (PlainResult) response ->
            response.writeLine("name;value")
                .withContentType("text/csv")
                .withCookie("X-Test", "test")
                .writeLine("line;1")
                .write("line;2");

        result.execute(invocation);

        assertEquals("name;value\nline;1\nline;2", response.getContentAsString());
        assertEquals("text/csv", response.getContentType());
    }

    public void testHeaders() throws Exception {
        PlainResult result = (PlainResult) response ->
            response.withHeader("X-String", "test")
                .withHeader("X-Date", 0L)
                .withHeader("X-Number", 100)
                .write("");

        result.execute(invocation);

        assertEquals("", response.getContentAsString());
        assertEquals("text/plain; charset=UTF-8", response.getContentType());
        assertEquals("test", response.getHeader("X-String"));
        assertEquals("Thu, 01 Jan 1970 00:00:00 GMT", response.getHeader("X-Date"));
        assertEquals("100", response.getHeader("X-NUmber"));
    }

    public void testExceptionOnCommitted() throws Exception {
        response.setCommitted(true);

        PlainResult result = (PlainResult) response ->
            response.write("");

        try {
            result.execute(invocation);
            fail("Exception was expected!");
        } catch (StrutsException e) {
            assertEquals("Http response already committed, cannot modify it!", e.getMessage());
        }
    }

    public void testNoExceptionOnCommitted() throws Exception {
        response.setCommitted(true);

        PlainResult result = new PlainResult() {
            @Override
            public void write(ResponseBuilder response) {
                response.write("");
            }

            @Override
            public boolean ignoreCommitted() {
                return true;
            }
        };

        try {
            result.execute(invocation);
            assertTrue(true);
        } catch (StrutsException e) {
            fail(e.getMessage());
        }
    }

    public void testPassingNullInvocation() throws Exception{
        Result result = (PlainResult) response -> response.write("ignore");
        try {
            result.execute(null);
            fail("Exception should be thrown!");
        } catch (IllegalArgumentException e) {
            assertEquals("Invocation cannot be null!", e.getMessage());
        }
    }

    public void setUp() throws Exception {
        super.setUp();
        invocation = new MockActionInvocation();
        response = new MockHttpServletResponse();
        invocation.setInvocationContext(ActionContext.getContext());

        ActionContext.getContext().withServletResponse(response).withActionInvocation(invocation);
    }
}