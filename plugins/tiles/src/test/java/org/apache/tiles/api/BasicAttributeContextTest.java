/*
 * Licensed to the Apache Software Foundation (ASF) under one
 * or more contributor license agreements.  See the NOTICE file
 * distributed with this work for additional information
 * regarding copyright ownership.  The ASF licenses this file
 * to you under the Apache License, Version 2.0 (the
 * "License"); you may not use this file except in compliance
 * with the License.  You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing,
 * software distributed under the License is distributed on an
 * "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY
 * KIND, either express or implied.  See the License for the
 * specific language governing permissions and limitations
 * under the License.
 */
package org.apache.tiles.api;

import org.junit.Test;

import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import static org.easymock.EasyMock.createMock;
import static org.easymock.EasyMock.expect;
import static org.easymock.EasyMock.replay;
import static org.easymock.EasyMock.verify;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertTrue;

/**
 * Tests <code>BasicAttributeContext</code>.
 *
 * @version $Rev$ $Date$
 */
public class BasicAttributeContextTest {

    /**
     * Tests {@link BasicAttributeContext#BasicAttributeContext()}.
     */
    @Test
    public void testBasicAttributeContext() {
        AttributeContext context = new BasicAttributeContext();
        assertNull("There are some spurious attributes", context
            .getLocalAttributeNames());
        assertNull("There are some spurious attributes", context
            .getCascadedAttributeNames());
    }

    /**
     * Tests {@link BasicAttributeContext#BasicAttributeContext(Map)}.
     */
    @Test
    public void testBasicAttributeContextMapOfStringAttribute() {
        Map<String, Attribute> name2attrib = new HashMap<>();
        Attribute attribute = new Attribute("Value 1");
        name2attrib.put("name1", attribute);
        attribute = new Attribute("Value 2");
        name2attrib.put("name2", attribute);
        AttributeContext context = new BasicAttributeContext(name2attrib);
        attribute = context.getAttribute("name1");
        assertNotNull("Attribute name1 not found", attribute);
        assertEquals("Attribute name1 has not been set correctly", "Value 1",
            attribute.getValue());
        attribute = context.getAttribute("name2");
        assertNotNull("Attribute name2 not found", attribute);
        assertEquals("Attribute name2 has not been set correctly", "Value 2",
            attribute.getValue());
    }

    /**
     * Tests
     * {@link BasicAttributeContext#BasicAttributeContext(AttributeContext)}.
     */
    @Test
    public void testBasicAttributeContextAttributeContext() {
        Set<String> localAttributes = new LinkedHashSet<>();
        Set<String> cascadedAttributes = new LinkedHashSet<>();
        localAttributes.add("local1");
        localAttributes.add("local2");
        cascadedAttributes.add("cascaded1");
        cascadedAttributes.add("cascaded2");
        AttributeContext toCopy = createMock(AttributeContext.class);
        expect(toCopy.getLocalAttributeNames()).andReturn(localAttributes);
        expect(toCopy.getLocalAttribute("local1")).andReturn(
            new Attribute("value1")).anyTimes();
        expect(toCopy.getLocalAttribute("local2")).andReturn(
            new Attribute("value2")).anyTimes();
        expect(toCopy.getCascadedAttributeNames())
            .andReturn(cascadedAttributes);
        expect(toCopy.getCascadedAttribute("cascaded1")).andReturn(
            new Attribute("value3")).anyTimes();
        expect(toCopy.getCascadedAttribute("cascaded2")).andReturn(
            new Attribute("value4")).anyTimes();
        Attribute templateAttribute = new Attribute("/template.jsp", Expression
            .createExpression("expression", null), "role1,role2",
            "template");
        expect(toCopy.getTemplateAttribute()).andReturn(templateAttribute);
        Set<String> roles = new HashSet<>();
        roles.add("role1");
        roles.add("role2");
        expect(toCopy.getPreparer()).andReturn("my.preparer.Preparer");
        replay(toCopy);
        BasicAttributeContext context = new BasicAttributeContext(toCopy);
        assertEquals("The template has not been set correctly",
            "/template.jsp", context.getTemplateAttribute().getValue());
        assertEquals("The template expression has not been set correctly",
            "expression", context.getTemplateAttribute()
                .getExpressionObject().getExpression());
        assertEquals("The roles are not the same", roles, context
            .getTemplateAttribute().getRoles());
        assertEquals("The preparer has not been set correctly",
            "my.preparer.Preparer", context.getPreparer());
        Attribute attribute = context.getLocalAttribute("local1");
        assertNotNull("Attribute local1 not found", attribute);
        assertEquals("Attribute local1 has not been set correctly", "value1",
            attribute.getValue());
        attribute = context.getLocalAttribute("local2");
        assertNotNull("Attribute local2 not found", attribute);
        assertEquals("Attribute local2 has not been set correctly", "value2",
            attribute.getValue());
        attribute = context.getCascadedAttribute("cascaded1");
        assertNotNull("Attribute cascaded1 not found", attribute);
        assertEquals("Attribute cascaded1 has not been set correctly",
            "value3", attribute.getValue());
        attribute = context.getCascadedAttribute("cascaded2");
        assertNotNull("Attribute cascaded2 not found", attribute);
        assertEquals("Attribute cascaded2 has not been set correctly",
            "value4", attribute.getValue());
    }

    /**
     * Tests
     * {@link BasicAttributeContext#BasicAttributeContext(BasicAttributeContext)}
     * .
     */
    @Test
    public void testBasicAttributeContextBasicAttributeContext() {
        BasicAttributeContext toCopy = new BasicAttributeContext();
        toCopy.putAttribute("name1", new Attribute("value1"), false);
        toCopy.putAttribute("name2", new Attribute("value2"), true);
        Attribute templateAttribute = Attribute
            .createTemplateAttribute("/template.jsp");
        Set<String> roles = new HashSet<>();
        roles.add("role1");
        roles.add("role2");
        templateAttribute.setRoles(roles);
        toCopy.setTemplateAttribute(templateAttribute);
        toCopy.setPreparer("my.preparer.Preparer");
        AttributeContext context = new BasicAttributeContext(toCopy);
        assertEquals("The template has not been set correctly",
            "/template.jsp", context.getTemplateAttribute().getValue());
        assertEquals("The roles are not the same", roles, context
            .getTemplateAttribute().getRoles());
        assertEquals("The preparer has not been set correctly",
            "my.preparer.Preparer", context.getPreparer());
        Attribute attribute = context.getLocalAttribute("name1");
        assertNotNull("Attribute name1 not found", attribute);
        assertEquals("Attribute name1 has not been set correctly", "value1",
            attribute.getValue());
        attribute = context.getCascadedAttribute("name2");
        assertNotNull("Attribute name2 not found", attribute);
        assertEquals("Attribute name2 has not been set correctly", "value2",
            attribute.getValue());
    }

    /**
     * Tests
     * {@link BasicAttributeContext#inheritCascadedAttributes(AttributeContext)}
     * .
     */
    @Test
    public void testInheritCascadedAttributes() {
        AttributeContext toCopy = new BasicAttributeContext();
        toCopy.putAttribute("name1", new Attribute("value1"), false);
        toCopy.putAttribute("name2", new Attribute("value2"), true);
        AttributeContext context = new BasicAttributeContext();
        context.inheritCascadedAttributes(toCopy);
        Attribute attribute = context.getLocalAttribute("name1");
        assertNull("Attribute name1 found", attribute);
        attribute = context.getCascadedAttribute("name2");
        assertNotNull("Attribute name2 not found", attribute);
        assertEquals("Attribute name2 has not been set correctly", "value2",
            attribute.getValue());
    }

    /**
     * Tests {@link BasicAttributeContext#inherit(BasicAttributeContext)}
     * testing inheritance between {@link ListAttribute} instances.
     */
    @Test
    public void testInheritListAttribute() {
        AttributeContext toCopy = new BasicAttributeContext();
        ListAttribute parentListAttribute = new ListAttribute();
        Attribute first = new Attribute("first");
        Attribute second = new Attribute("second");
        parentListAttribute.add(first);
        toCopy.putAttribute("list", parentListAttribute);
        AttributeContext context = new BasicAttributeContext();
        ListAttribute listAttribute = new ListAttribute();
        listAttribute.setInherit(true);
        listAttribute.add(second);
        context.putAttribute("list", listAttribute);
        context.inherit(toCopy);
        ListAttribute result = (ListAttribute) context.getAttribute("list");
        assertNotNull("The attribute must exist", result);
        List<Attribute> value = result.getValue();
        assertNotNull("The list must exist", value);
        assertEquals("The size is not correct", 2, value.size());
        assertEquals("The first element is not correct", first, value.get(0));
        assertEquals("The second element is not correct", second, value
            .get(1));

        context = new BasicAttributeContext();
        listAttribute = new ListAttribute();
        listAttribute.add(second);
        context.putAttribute("list", listAttribute);
        context.inherit(toCopy);
        result = (ListAttribute) context.getAttribute("list");
        assertNotNull("The attribute must exist", result);
        value = result.getValue();
        assertNotNull("The list must exist", value);
        assertEquals("The size is not correct", 1, value.size());
        assertEquals("The second element is not correct", second, value
            .get(0));
    }

    /**
     * Tests
     * {@link BasicAttributeContext#inheritCascadedAttributes(AttributeContext)}
     * .
     */
    @Test
    public void testInherit() {
        AttributeContext toCopy = new BasicAttributeContext();
        Attribute parentTemplateAttribute = new Attribute();
        parentTemplateAttribute.setValue("/parent/template.jsp");
        toCopy.setTemplateAttribute(parentTemplateAttribute);
        toCopy.putAttribute("name1", new Attribute("value1"), true);
        toCopy.putAttribute("name2", new Attribute("value2"), true);
        toCopy.putAttribute("name3", new Attribute("value3"), false);
        toCopy.putAttribute("name4", new Attribute("value4"), false);
        AttributeContext context = new BasicAttributeContext();
        Attribute templateAttribute = new Attribute();
        templateAttribute.setRole("role1,role2");
        context.setTemplateAttribute(templateAttribute);
        context.putAttribute("name1", new Attribute("newValue1"), true);
        context.putAttribute("name3", new Attribute("newValue3"), false);
        context.inherit(toCopy);
        Attribute attribute = context.getTemplateAttribute();
        assertEquals("/parent/template.jsp", attribute.getValue());
        assertTrue(attribute.getRoles().contains("role1"));
        assertTrue(attribute.getRoles().contains("role2"));
        attribute = context.getCascadedAttribute("name1");
        assertNotNull("Attribute name1 not found", attribute);
        assertEquals("Attribute name1 has not been set correctly", "newValue1",
            attribute.getValue());
        attribute = context.getCascadedAttribute("name2");
        assertNotNull("Attribute name2 not found", attribute);
        assertEquals("Attribute name2 has not been set correctly", "value2",
            attribute.getValue());
        attribute = context.getLocalAttribute("name3");
        assertNotNull("Attribute name3 not found", attribute);
        assertEquals("Attribute name3 has not been set correctly", "newValue3",
            attribute.getValue());
        attribute = context.getLocalAttribute("name4");
        assertNotNull("Attribute name4 not found", attribute);
        assertEquals("Attribute name4 has not been set correctly", "value4",
            attribute.getValue());

        toCopy = new BasicAttributeContext();
        toCopy.putAttribute("name1", new Attribute("value1"), true);
        toCopy.putAttribute("name2", new Attribute("value2"), true);
        toCopy.putAttribute("name3", new Attribute("value3"), false);
        toCopy.putAttribute("name4", new Attribute("value4"), false);
        context = new BasicAttributeContext();
        context.inherit(toCopy);
        attribute = context.getCascadedAttribute("name1");
        assertNotNull("Attribute name1 not found", attribute);
        assertEquals("Attribute name1 has not been set correctly", "value1",
            attribute.getValue());
        attribute = context.getCascadedAttribute("name2");
        assertNotNull("Attribute name2 not found", attribute);
        assertEquals("Attribute name2 has not been set correctly", "value2",
            attribute.getValue());
        attribute = context.getLocalAttribute("name3");
        assertNotNull("Attribute name3 not found", attribute);
        assertEquals("Attribute name3 has not been set correctly", "value3",
            attribute.getValue());
        attribute = context.getLocalAttribute("name4");
        assertNotNull("Attribute name4 not found", attribute);
        assertEquals("Attribute name4 has not been set correctly", "value4",
            attribute.getValue());
    }

    /**
     * Tests
     * {@link BasicAttributeContext#inherit(AttributeContext)}
     * .
     */
    @Test
    public void testInheritAttributeContext() {
        AttributeContext toCopy = createMock(AttributeContext.class);
        Attribute templateAttribute = Attribute.createTemplateAttribute("/my/template.jsp");
        expect(toCopy.getTemplateAttribute()).andReturn(templateAttribute);
        expect(toCopy.getPreparer()).andReturn("my.preparer");
        Set<String> cascadedNames = new HashSet<>();
        cascadedNames.add("name1");
        cascadedNames.add("name2");
        expect(toCopy.getCascadedAttributeNames()).andReturn(cascadedNames);
        expect(toCopy.getCascadedAttribute("name1")).andReturn(new Attribute("value1"));
        expect(toCopy.getCascadedAttribute("name2")).andReturn(new Attribute("value2"));
        Set<String> names = new HashSet<>();
        names.add("name3");
        names.add("name4");
        expect(toCopy.getLocalAttributeNames()).andReturn(names);
        expect(toCopy.getLocalAttribute("name3")).andReturn(new Attribute("value3"));
        expect(toCopy.getLocalAttribute("name4")).andReturn(new Attribute("value4"));

        replay(toCopy);
        AttributeContext context = new BasicAttributeContext();
        context.putAttribute("name1", new Attribute("newValue1"), true);
        context.putAttribute("name3", new Attribute("newValue3"), false);
        context.inherit(toCopy);
        Attribute attribute = context.getCascadedAttribute("name1");
        assertEquals("/my/template.jsp", context.getTemplateAttribute().getValue());
        assertEquals("my.preparer", context.getPreparer());
        assertNotNull("Attribute name1 not found", attribute);
        assertEquals("Attribute name1 has not been set correctly", "newValue1",
            attribute.getValue());
        attribute = context.getCascadedAttribute("name2");
        assertNotNull("Attribute name2 not found", attribute);
        assertEquals("Attribute name2 has not been set correctly", "value2",
            attribute.getValue());
        attribute = context.getLocalAttribute("name3");
        assertNotNull("Attribute name3 not found", attribute);
        assertEquals("Attribute name3 has not been set correctly", "newValue3",
            attribute.getValue());
        attribute = context.getLocalAttribute("name4");
        assertNotNull("Attribute name4 not found", attribute);
        assertEquals("Attribute name4 has not been set correctly", "value4",
            attribute.getValue());
        verify(toCopy);
    }

    /**
     * Tests {@link BasicAttributeContext#inherit(AttributeContext)}
     * testing inheritance between {@link ListAttribute} instances.
     */
    @Test
    public void testInheritAttributeContextListAttribute() {
        AttributeContext toCopy = createMock(AttributeContext.class);
        Attribute templateAttribute = Attribute.createTemplateAttribute("/my/template.jsp");
        expect(toCopy.getTemplateAttribute()).andReturn(templateAttribute).times(2);
        expect(toCopy.getPreparer()).andReturn("my.preparer").times(2);
        ListAttribute parentListAttribute = new ListAttribute();
        Attribute first = new Attribute("first");
        Attribute second = new Attribute("second");
        Attribute third = new Attribute("third");
        Attribute fourth = new Attribute("fourth");
        parentListAttribute.add(first);
        ListAttribute parentListAttribute2 = new ListAttribute();
        parentListAttribute2.add(third);
        Set<String> names = new HashSet<>();
        names.add("list");
        Set<String> cascadedNames = new HashSet<>();
        cascadedNames.add("list2");
        expect(toCopy.getCascadedAttributeNames()).andReturn(cascadedNames).times(2);
        expect(toCopy.getCascadedAttribute("list2")).andReturn(parentListAttribute2).times(2);
        expect(toCopy.getLocalAttributeNames()).andReturn(names).times(2);
        expect(toCopy.getLocalAttribute("list")).andReturn(parentListAttribute).times(2);

        replay(toCopy);
        AttributeContext context = new BasicAttributeContext();
        ListAttribute listAttribute = new ListAttribute();
        listAttribute.setInherit(true);
        listAttribute.add(second);
        context.putAttribute("list", listAttribute, false);
        ListAttribute listAttribute2 = new ListAttribute();
        listAttribute2.setInherit(true);
        listAttribute2.add(fourth);
        context.putAttribute("list2", listAttribute2, true);
        context.inherit(toCopy);
        ListAttribute result = (ListAttribute) context.getAttribute("list");
        assertNotNull("The attribute must exist", result);
        List<Attribute> value = result.getValue();
        assertNotNull("The list must exist", value);
        assertEquals("The size is not correct", 2, value.size());
        assertEquals("The first element is not correct", first, value.get(0));
        assertEquals("The second element is not correct", second, value
            .get(1));
        result = (ListAttribute) context.getAttribute("list2");
        assertNotNull("The attribute must exist", result);
        value = result.getValue();
        assertNotNull("The list must exist", value);
        assertEquals("The size is not correct", 2, value.size());
        assertEquals("The first element is not correct", third, value.get(0));
        assertEquals("The second element is not correct", fourth, value
            .get(1));

        context = new BasicAttributeContext();
        listAttribute = new ListAttribute();
        listAttribute.add(second);
        context.putAttribute("list", listAttribute);
        context.inherit(toCopy);
        result = (ListAttribute) context.getAttribute("list");
        assertNotNull("The attribute must exist", result);
        value = result.getValue();
        assertNotNull("The list must exist", value);
        assertEquals("The size is not correct", 1, value.size());
        assertEquals("The second element is not correct", second, value
            .get(0));
        verify(toCopy);
    }

    /**
     * Tests {@link BasicAttributeContext#addAll(Map)}.
     */
    @Test
    public void testAddAll() {
        AttributeContext context = new BasicAttributeContext();
        Map<String, Attribute> name2attrib = new HashMap<>();
        Attribute attribute = new Attribute("Value 1");
        name2attrib.put("name1", attribute);
        attribute = new Attribute("Value 2");
        name2attrib.put("name2", attribute);
        context.addAll(name2attrib);
        attribute = context.getAttribute("name1");
        assertNotNull("Attribute name1 not found", attribute);
        assertEquals("Attribute name1 has not been set correctly", "Value 1",
            attribute.getValue());
        attribute = context.getAttribute("name2");
        assertNotNull("Attribute name2 not found", attribute);
        assertEquals("Attribute name2 has not been set correctly", "Value 2",
            attribute.getValue());

        context.addAll(null);
        attribute = context.getAttribute("name1");
        assertNotNull("Attribute name1 not found", attribute);
        assertEquals("Attribute name1 has not been set correctly", "Value 1",
            attribute.getValue());
        attribute = context.getAttribute("name2");
        assertNotNull("Attribute name2 not found", attribute);
        assertEquals("Attribute name2 has not been set correctly", "Value 2",
            attribute.getValue());

        name2attrib = new HashMap<>();
        name2attrib.put("name3", new Attribute("Value 3"));
        context.addAll(name2attrib);
        attribute = context.getAttribute("name1");
        assertNotNull("Attribute name1 not found", attribute);
        assertEquals("Attribute name1 has not been set correctly", "Value 1",
            attribute.getValue());
        attribute = context.getAttribute("name2");
        assertNotNull("Attribute name2 not found", attribute);
        assertEquals("Attribute name2 has not been set correctly", "Value 2",
            attribute.getValue());
        attribute = context.getAttribute("name3");
        assertNotNull("Attribute name3 not found", attribute);
        assertEquals("Attribute name3 has not been set correctly", "Value 3",
            attribute.getValue());
    }

    /**
     * Tests {@link BasicAttributeContext#getAttribute(String)}.
     */
    @Test
    public void testGetAttribute() {
        AttributeContext context = new BasicAttributeContext();
        context.putAttribute("name1", new Attribute("value1"), false);
        context.putAttribute("name2", new Attribute("value2"), true);
        context.putAttribute("name3", new Attribute("value3a"), true);
        context.putAttribute("name3", new Attribute("value3"), false);
        Attribute attribute = context.getAttribute("name1");
        assertNotNull("Attribute name1 not found", attribute);
        assertEquals("Attribute name1 has not been set correctly", "value1",
            attribute.getValue());
        attribute = context.getAttribute("name2");
        assertNotNull("Attribute name2 not found", attribute);
        assertEquals("Attribute name2 has not been set correctly", "value2",
            attribute.getValue());
        attribute = context.getAttribute("name3");
        assertNotNull("Attribute name3 not found", attribute);
        assertEquals("Attribute name3 has not been set correctly", "value3",
            attribute.getValue());
    }

    /**
     * Tests {@link BasicAttributeContext#getLocalAttribute(String)}.
     */
    @Test
    public void testGetLocalAttribute() {
        AttributeContext context = new BasicAttributeContext();
        context.putAttribute("name1", new Attribute("value1"), false);
        context.putAttribute("name2", new Attribute("value2"), true);
        context.putAttribute("name3", new Attribute("value3a"), true);
        context.putAttribute("name3", new Attribute("value3"), false);
        Attribute attribute = context.getLocalAttribute("name1");
        assertNotNull("Attribute name1 not found", attribute);
        assertEquals("Attribute name1 has not been set correctly", "value1",
            attribute.getValue());
        attribute = context.getLocalAttribute("name2");
        assertNull("Attribute name2 found", attribute);
        attribute = context.getLocalAttribute("name3");
        assertNotNull("Attribute name3 not found", attribute);
        assertEquals("Attribute name3 has not been set correctly", "value3",
            attribute.getValue());
    }

    /**
     * Tests {@link BasicAttributeContext#getCascadedAttribute(String)}.
     */
    @Test
    public void testGetCascadedAttribute() {
        AttributeContext context = new BasicAttributeContext();
        context.putAttribute("name1", new Attribute("value1"), false);
        context.putAttribute("name2", new Attribute("value2"), true);
        context.putAttribute("name3", new Attribute("value3a"), true);
        context.putAttribute("name3", new Attribute("value3"), false);
        Attribute attribute = context.getCascadedAttribute("name1");
        assertNull("Attribute name1 found", attribute);
        attribute = context.getCascadedAttribute("name2");
        assertNotNull("Attribute name2 not found", attribute);
        assertEquals("Attribute name2 has not been set correctly", "value2",
            attribute.getValue());
        attribute = context.getCascadedAttribute("name3");
        assertNotNull("Attribute name3 not found", attribute);
        assertEquals("Attribute name3 has not been set correctly", "value3a",
            attribute.getValue());
    }

    /**
     * Tests {@link BasicAttributeContext#getLocalAttributeNames()}.
     */
    @Test
    public void testGetLocalAttributeNames() {
        AttributeContext context = new BasicAttributeContext();
        context.putAttribute("name1", new Attribute("value1"), false);
        context.putAttribute("name2", new Attribute("value2"), true);
        context.putAttribute("name3", new Attribute("value3a"), true);
        context.putAttribute("name3", new Attribute("value3"), false);
        Set<String> names = context.getLocalAttributeNames();
        assertTrue("Attribute name1 is not present", names.contains("name1"));
        assertFalse("Attribute name2 is present", names.contains("name2"));
        assertTrue("Attribute name3 is not present", names.contains("name3"));
    }

    /**
     * Tests {@link BasicAttributeContext#getCascadedAttributeNames()}.
     */
    @Test
    public void testGetCascadedAttributeNames() {
        AttributeContext context = new BasicAttributeContext();
        context.putAttribute("name1", new Attribute("value1"), false);
        context.putAttribute("name2", new Attribute("value2"), true);
        context.putAttribute("name3", new Attribute("value3a"), true);
        context.putAttribute("name3", new Attribute("value3"), false);
        Set<String> names = context.getCascadedAttributeNames();
        assertFalse("Attribute name1 is present", names.contains("name1"));
        assertTrue("Attribute name2 is not present", names.contains("name2"));
        assertTrue("Attribute name3 is not present", names.contains("name3"));
    }

    /**
     * Tests {@link BasicAttributeContext#putAttribute(String, Attribute)}.
     */
    @Test
    public void testPutAttributeStringAttribute() {
        AttributeContext context = new BasicAttributeContext();
        context.putAttribute("name1", new Attribute("value1"));
        Attribute attribute = context.getLocalAttribute("name1");
        assertNotNull("Attribute name1 not found", attribute);
        assertEquals("Attribute name1 has not been set correctly", "value1",
            attribute.getValue());
        attribute = context.getCascadedAttribute("name1");
        assertNull("Attribute name1 found", attribute);
    }

    /**
     * Tests
     * {@link BasicAttributeContext#putAttribute(String, Attribute, boolean)}.
     */
    @Test
    public void testPutAttributeStringAttributeBoolean() {
        AttributeContext context = new BasicAttributeContext();
        context.putAttribute("name1", new Attribute("value1"), false);
        context.putAttribute("name2", new Attribute("value2"), true);
        Attribute attribute = context.getLocalAttribute("name1");
        assertNotNull("Attribute name1 not found", attribute);
        assertEquals("Attribute name1 has not been set correctly", "value1",
            attribute.getValue());
        attribute = context.getCascadedAttribute("name1");
        assertNull("Attribute name1 found", attribute);
        attribute = context.getCascadedAttribute("name2");
        assertNotNull("Attribute name2 not found", attribute);
        assertEquals("Attribute name2 has not been set correctly", "value2",
            attribute.getValue());
        attribute = context.getLocalAttribute("name2");
        assertNull("Attribute name2 found", attribute);
    }

    /**
     * Tests {@link BasicAttributeContext#clear()}.
     */
    @Test
    public void testClear() {
        AttributeContext context = new BasicAttributeContext();
        context.putAttribute("name1", new Attribute("value1"), false);
        context.putAttribute("name2", new Attribute("value2"), true);
        context.clear();
        Set<String> names = context.getLocalAttributeNames();
        assertTrue("There are local attributes", names == null
            || names.isEmpty());
        names = context.getCascadedAttributeNames();
        assertTrue("There are cascaded attributes", names == null
            || names.isEmpty());
    }

    /**
     * Tests {@link BasicAttributeContext#equals(Object)}.
     */
    @Test
    public void testEquals() {
        BasicAttributeContext attributeContext = new BasicAttributeContext();
        attributeContext.setPreparer("my.preparer");
        attributeContext.setTemplateAttribute(Attribute.createTemplateAttribute("/my/template.jsp"));
        attributeContext.putAttribute("attribute1", new Attribute("value1"), true);
        attributeContext.putAttribute("attribute2", new Attribute("value2"), true);
        attributeContext.putAttribute("attribute3", new Attribute("value3"), false);
        BasicAttributeContext toCompare = new BasicAttributeContext(attributeContext);
        assertEquals(toCompare, attributeContext);
        toCompare = new BasicAttributeContext(attributeContext);
        toCompare.putAttribute("attribute4", new Attribute("value4"), true);
        assertNotEquals(toCompare, attributeContext);
        toCompare = new BasicAttributeContext(attributeContext);
        toCompare.putAttribute("attribute4", new Attribute("value4"), false);
        assertNotEquals(toCompare, attributeContext);
        toCompare = new BasicAttributeContext(attributeContext);
        toCompare.setPreparer("another.preparer");
        assertNotEquals(toCompare, attributeContext);
        toCompare = new BasicAttributeContext(attributeContext);
        toCompare.setTemplateAttribute(Attribute.createTemplateAttribute("/another/template.jsp"));
        assertNotEquals(toCompare, attributeContext);
    }

    /**
     * Tests {@link BasicAttributeContext#hashCode()}.
     */
    @Test
    public void testHashCode() {
        BasicAttributeContext attributeContext = new BasicAttributeContext();
        attributeContext.setPreparer("my.preparer");
        Attribute templateAttribute = Attribute.createTemplateAttribute("/my/template.jsp");
        attributeContext.setTemplateAttribute(templateAttribute);
        Attribute attribute1 = new Attribute("value1");
        Attribute attribute2 = new Attribute("value2");
        Attribute attribute3 = new Attribute("value3");
        attributeContext.putAttribute("attribute1", attribute1, true);
        attributeContext.putAttribute("attribute2", attribute2, true);
        attributeContext.putAttribute("attribute3", attribute3, false);
        Map<String, Attribute> cascadedAttributes = new HashMap<>();
        cascadedAttributes.put("attribute1", attribute1);
        cascadedAttributes.put("attribute2", attribute2);
        Map<String, Attribute> attributes = new HashMap<>();
        attributes.put("attribute3", attribute3);
        assertEquals(templateAttribute.hashCode() + "my.preparer".hashCode()
                + attributes.hashCode() + cascadedAttributes.hashCode(),
            attributeContext.hashCode());
    }

}
