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

import org.apache.struts2.ActionContext;
import org.apache.struts2.ActionSupport;
import org.apache.struts2.XWorkTestCase;
import org.apache.struts2.mock.MockActionInvocation;
import com.thoughtworks.xstream.XStream;
import com.thoughtworks.xstream.io.xml.StaxDriver;
import com.thoughtworks.xstream.security.TypePermission;
import org.apache.struts2.rest.handler.xstream.XStreamAllowedClassNames;
import org.apache.struts2.rest.handler.xstream.XStreamAllowedClasses;
import org.apache.struts2.rest.handler.xstream.XStreamPermissionProvider;
import org.apache.struts2.rest.handler.xstream.XStreamProvider;

import java.io.Reader;
import java.io.StringReader;
import java.io.StringWriter;
import java.io.Writer;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.HashSet;
import java.util.Locale;
import java.util.Set;

import static org.assertj.core.api.Assertions.assertThat;

public class XStreamHandlerTest extends XWorkTestCase {

    private XStreamHandler handler;
    private MockActionInvocation ai;

    public void setUp() throws Exception {
        super.setUp();
        handler = new XStreamHandler();
        ai = new MockActionInvocation();
        ActionSupport action = new ActionSupport();
        ActionContext context = ActionContext.of().withLocale(Locale.US);
        ai.setInvocationContext(context);
        ai.setAction(action);
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
        assertThat(stream.toString())
            .contains("<org.apache.struts2.rest.handler.SimpleBean>")
            .contains("<name>Jan</name>")
            .contains("<age>12</age>")
            .contains("<parents class=\"java.util.Arrays$ArrayList\">")
            .contains("<string>Adam</string>")
            .contains("<string>Ewa</string>")
            .contains("</org.apache.struts2.rest.handler.SimpleBean>");
    }

    public void testXmlToObject() {
        // given
        String xml = "<?xml version='1.0' encoding='UTF-8'?><org.apache.struts2.rest.handler.SimpleBean><name>Jan</name><age>12</age><parents class=\"java.util.ArrayList\"><string>Adam</string><string>Ewa</string></parents></org.apache.struts2.rest.handler.SimpleBean>";

        SimpleBean obj = new SimpleBean();
        ai.setAction(new SimpleAction());

        // when
        Reader in = new StringReader(xml);
        handler.toObject(ai, in, obj);

        // then
        assertNotNull(obj);
        assertEquals("Jan", obj.getName());
        assertEquals(12L, obj.getAge().longValue());
        assertNotNull(obj.getParents());
        assertThat(obj.getParents())
            .hasSize(2)
            .containsExactly("Adam", "Ewa");
    }

    public void testXmlToObjectWithAliases() {
        // given
        String xml = "<?xml version='1.0' encoding='UTF-8'?><data><name>Jan</name><age>12</age><parents><string>Adam</string><string>Ewa</string></parents></data>";

        SimpleBean obj = new SimpleBean();
        ai.setAction(new SimpleAliasAction());

        // when
        Reader in = new StringReader(xml);
        handler.toObject(ai, in, obj);

        // then
        assertNotNull(obj);
        assertEquals("Jan", obj.getName());
        assertEquals(12L, obj.getAge().longValue());
        assertNotNull(obj.getParents());
        assertThat(obj.getParents())
            .hasSize(2)
            .containsExactly("Adam", "Ewa");
    }

    private static class SimpleAction implements XStreamAllowedClasses, XStreamAllowedClassNames, XStreamPermissionProvider {
        @Override
        public Set<Class<?>> allowedClasses() {
            Set<Class<?>> classes = new HashSet<>();
            classes.add(SimpleBean.class);
            return classes;
        }

        @Override
        public Set<String> allowedClassNames() {
            return Collections.emptySet();
        }

        @Override
        public Collection<TypePermission> getTypePermissions() {
            return Collections.emptyList();
        }
    }

    private static class SimpleAliasAction extends SimpleAction implements XStreamProvider {
        @Override
        public XStream createXStream() {
            XStream stream = new XStream(new StaxDriver());
            stream.alias("parents", ArrayList.class);
            stream.alias("data", SimpleBean.class);
            return stream;
        }
    }
}
