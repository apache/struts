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

import com.opensymphony.xwork2.ActionInvocation;
import com.opensymphony.xwork2.XWorkTestCase;
import com.opensymphony.xwork2.mock.MockActionInvocation;

import java.io.Reader;
import java.io.StringReader;
import java.io.StringWriter;
import java.io.Writer;
import java.util.Arrays;

import static org.fest.assertions.Assertions.assertThat;

public class JacksonXmlHandlerTest extends XWorkTestCase {

    private String xml;
    private JacksonXmlHandler handler;
    private ActionInvocation ai;

    public void setUp() throws Exception {
        super.setUp();
        xml = "<SimpleBean>" +
                "<name>Jan</name>" +
                "<age>12</age>" +
                "<parents>" +
                "<parents>Adam</parents>" +
                "<parents>Ewa</parents>" +
                "</parents>" +
                "</SimpleBean>";
        handler = new JacksonXmlHandler();
        ai = new MockActionInvocation();
    }

    public void testObjectToXml() throws Exception {
        // given
        SimpleBean obj = new SimpleBean();
        obj.setName("Jan");
        obj.setAge(12L);
        obj.setParents(Arrays.asList("Adam", "Ewa"));

        // when
        Writer stream = new StringWriter();
        handler.fromObject(ai, obj, null, stream);

        // then
        stream.flush();
        assertEquals(xml, stream.toString());
    }

    public void testXmlToObject() throws Exception {
        // given
        SimpleBean obj = new SimpleBean();

        // when
        Reader in = new StringReader(xml);
        handler.toObject(ai, in, obj);

        // then
        assertNotNull(obj);
        assertEquals(obj.getName(), "Jan");
        assertEquals(obj.getAge().longValue(), 12L);
        assertNotNull(obj.getParents());
        assertThat(obj.getParents())
                .hasSize(2)
                .containsExactly("Adam", "Ewa");
    }

}