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
package org.apache.struts2.rest.handler;

import com.opensymphony.xwork2.mock.MockActionInvocation;
import junit.framework.TestCase;

import java.io.IOException;
import java.io.StringReader;
import java.io.StringWriter;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class JacksonJsonHandlerTest extends TestCase {

    public void testFromObject() throws IOException {
        Contact contact = new Contact("bob", true, 44);

        StringWriter writer = new StringWriter();
        JacksonJsonHandler handler = new JacksonJsonHandler();
        handler.fromObject(new MockActionInvocation(), contact, "success", writer);
        String data = writer.toString();
        assertTrue(data.startsWith("{"));
        assertTrue(data.contains("\"age\":44"));
        assertTrue(data.contains("\"important\":true"));
        assertTrue(data.contains("\"name\":\"bob\""));
    }

    public void testFromObjectArray() throws IOException {
        Contact contact = new Contact("bob", true, 44);

        StringWriter writer = new StringWriter();
        JacksonJsonHandler handler = new JacksonJsonHandler();
        handler.fromObject(new MockActionInvocation(), Arrays.asList(contact), "success", writer);

        String data = writer.toString();
        assertTrue(data.startsWith("[{"));
        assertTrue(data.contains("\"age\":44"));
        assertTrue(data.contains("\"important\":true"));
        assertTrue(data.contains("\"name\":\"bob\""));
    }

    public void testToObject() throws IOException {
        Contact contact = new Contact("bob", true, 44);

        Contact target = new Contact();
        StringReader reader = new StringReader("{\"age\":44,\"important\":true,\"name\":\"bob\"}");
        JacksonJsonHandler handler = new JacksonJsonHandler();
        handler.toObject(new MockActionInvocation(), reader, target);
        assertEquals(contact, target);
    }

    public void testToObjectList() throws IOException {

        List<Contact> source = new ArrayList<Contact>();
        source.add(new Contact("bob", true, 44));
        source.add(new Contact("john", false, 33));

        List<Contact> target = new ArrayList<Contact>();
        StringReader reader = new StringReader("[{\"age\":44,\"important\":true,\"name\":\"bob\"},{\"age\":33,\"important\":false,\"name\":\"john\"}]");
        JacksonJsonHandler handler = new JacksonJsonHandler();
        handler.toObject(new MockActionInvocation(), reader, target);
        assertEquals(source.size(), target.size());
    }

    public void testContentType() throws IOException {
        JacksonJsonHandler handler = new JacksonJsonHandler();
        assertEquals(handler.getContentType(), "application/json;charset=ISO-8859-1");
    }

    public void testDefaultEncoding() throws IOException {
        JacksonJsonHandler handler = new JacksonJsonHandler();
        handler.setDefaultEncoding("UTF-8");
        assertEquals(handler.getContentType(), "application/json;charset=UTF-8");
    }
}
