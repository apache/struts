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

import com.fasterxml.jackson.annotation.JsonIdentityInfo;
import com.fasterxml.jackson.annotation.ObjectIdGenerators;
import com.fasterxml.jackson.dataformat.xml.annotation.JacksonXmlElementWrapper;
import com.fasterxml.jackson.dataformat.xml.annotation.JacksonXmlProperty;
import com.fasterxml.jackson.dataformat.xml.annotation.JacksonXmlText;
import org.apache.struts2.ActionInvocation;
import org.apache.struts2.XWorkTestCase;
import org.apache.struts2.interceptor.parameter.ParameterAuthorizationContext;
import org.apache.struts2.interceptor.parameter.ParameterAuthorizer;
import org.apache.struts2.mock.MockActionInvocation;

import java.io.Reader;
import java.io.StringReader;
import java.io.StringWriter;
import java.io.Writer;
import java.util.Arrays;
import java.util.List;
import java.util.Set;

import static org.assertj.core.api.Assertions.assertThat;

public class JacksonXmlHandlerTest extends XWorkTestCase {

    private String xml, prefix, suffix, name, age, parents;
    private JacksonXmlHandler handler;
    private ActionInvocation ai;

    public void setUp() throws Exception {
        super.setUp();
        name = "<name>Jan</name>";
        age = "<age>12</age>";
        parents = "<parents>" +
                    "<parents>Adam</parents>" +
                    "<parents>Ewa</parents>" +
                    "</parents>";
        prefix = "<SimpleBean>";
        suffix = "</SimpleBean>";
        xml = prefix + name + age + parents + suffix;

        handler = new JacksonXmlHandler();
        ai = new MockActionInvocation();
    }

    @Override
    public void tearDown() throws Exception {
        ParameterAuthorizationContext.unbind();
        super.tearDown();
    }

    private void bind(ParameterAuthorizer authorizer, Object target) {
        ParameterAuthorizationContext.bind(authorizer, target, target);
    }

    private <T> T read(String body, T target) throws Exception {
        handler.toObject(ai, new StringReader(body), target);
        return target;
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
        String actual = stream.toString();
        assertTrue(actual.length() == xml.length() &&
            actual.startsWith(prefix) &&
            actual.contains(name) &&
            actual.contains(age) &&
            actual.contains(parents) &&
            actual.endsWith(suffix));
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

    public void testUnwrappedListWithoutContext() throws Exception {
        // Jackson XML's deserializer modifier must see Jackson's own bean deserializer, not the
        // authorization wrapper, to install its unwrapped-list handling.
        UnwrappedListBean bean = read("<bean><items>a</items><items>b</items><name>n</name></bean>",
                new UnwrappedListBean());
        assertEquals(List.of("a", "b"), bean.items);
        assertEquals("n", bean.name);
    }

    public void testUnwrappedListAuthorized() throws Exception {
        Set<String> granted = Set.of("items", "items[0]", "name");
        bind((path, t, a) -> granted.contains(path), new UnwrappedListBean());
        UnwrappedListBean bean = read("<bean><items>a</items><items>b</items><name>n</name></bean>",
                new UnwrappedListBean());
        assertEquals(List.of("a", "b"), bean.items);
        assertEquals("n", bean.name);
    }

    public void testUnwrappedListRejected() throws Exception {
        Set<String> granted = Set.of("name");
        bind((path, t, a) -> granted.contains(path), new UnwrappedListBean());
        UnwrappedListBean bean = read("<bean><items>a</items><items>b</items><name>n</name></bean>",
                new UnwrappedListBean());
        assertNull(bean.items);
        assertEquals("n", bean.name);
    }

    public void testXmlTextStillReads() throws Exception {
        TextBean bean = read("<bean><attr>x</attr>hello</bean>", new TextBean());
        assertEquals("hello", bean.text);
        assertEquals("x", bean.attr);
    }

    public void testSoleXmlTextWithAttributeReadsAndIsAuthorized() throws Exception {
        // A sole text property next to an attribute goes through Jackson XML's text deserializer,
        // which the reorder now installs; it must still write through the authorizing property.
        TextAttributeBean bean = read("<bean attr=\"x\">hello</bean>", new TextAttributeBean());
        assertEquals("hello", bean.text);
        assertEquals("x", bean.attr);

        Set<String> granted = Set.of("attr");
        bind((path, t, a) -> granted.contains(path), new TextAttributeBean());
        TextAttributeBean rejected = read("<bean attr=\"x\">hello</bean>", new TextAttributeBean());
        assertNull(rejected.text);
        assertEquals("x", rejected.attr);
    }

    public void testBeanTypedObjectIdDeclaredOnTheReferencingPropertyAuthorizedUnderTheIdPath() throws Exception {
        // The per-property reader is rebuilt in createContextual through the XML wrapper Jackson
        // keeps around a bean with an unwrapped list, inside the redaction wrapper.
        Set<String> granted = Set.of("child", "child.id", "child.k", "child.name", "child.tags", "child.tags[0]");
        bind((path, t, a) -> granted.contains(path), new KeyIdentifiedHolder());
        KeyIdentifiedHolder holder = read(
                "<holder><child><id><k>x</k></id><name>alice</name><tags>t</tags></child></holder>",
                new KeyIdentifiedHolder());
        assertEquals("alice", holder.child.name);
        assertEquals(List.of("t"), holder.child.tags);
        assertNull("id member authorized by the referring bean's grant for [child.k] ?", holder.child.id.k);
    }

    public static class UnwrappedListBean {
        @JacksonXmlElementWrapper(useWrapping = false)
        public List<String> items;
        public String name;
    }

    public static class TextBean {
        @JacksonXmlText
        public String text;
        public String attr;
    }

    public static class TextAttributeBean {
        @JacksonXmlText
        public String text;
        @JacksonXmlProperty(isAttribute = true)
        public String attr;
    }

    public static class Key {
        public String k;
    }

    public static class PlainKeyed {
        public Key id;
        public String k;
        public String name;
        @JacksonXmlElementWrapper(useWrapping = false)
        public List<String> tags;
    }

    public static class KeyIdentifiedHolder {
        @JsonIdentityInfo(generator = ObjectIdGenerators.PropertyGenerator.class, property = "id")
        public PlainKeyed child;
    }
}
