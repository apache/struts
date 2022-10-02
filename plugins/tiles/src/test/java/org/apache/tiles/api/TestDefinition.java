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
import java.util.Map;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;

/**
 * Tests the Definition class.
 */
public class TestDefinition {

    /**
     * Tests {@link Definition#Definition(Definition)}.
     */
    @Test
    public void testDefinitionCopy() {
        Definition definition = new Definition();
        definition.setName("myDefinition");
        definition.setExtends("myExtends");
        Attribute attribute1 = new Attribute("value1");
        definition.putAttribute("name1", attribute1);
        Attribute attribute2 = new Attribute("value2");
        definition.putAttribute("name2", attribute2);
        Definition toCheck = new Definition(definition);
        assertEquals("myDefinition", toCheck.getName());
        assertEquals("myExtends", toCheck.getExtends());
        assertEquals(attribute1, toCheck.getAttribute("name1"));
        assertEquals(attribute2, toCheck.getAttribute("name2"));
    }

    /**
     * Tests {@link Definition#Definition(Definition)}.
     */
    @Test
    public void testDefinitionComplete() {
        Map<String, Attribute> attributeMap = new HashMap<>();
        Attribute attribute1 = new Attribute("value1");
        Attribute attribute2 = new Attribute("value2");
        attributeMap.put("name1", attribute1);
        attributeMap.put("name2", attribute2);
        Attribute templateAttribute = Attribute.createTemplateAttribute("/my/template.jsp");
        Definition definition = new Definition("myDefinition",
            templateAttribute, attributeMap);
        assertEquals("myDefinition", definition.getName());
        assertEquals(templateAttribute, definition.getTemplateAttribute());
        assertEquals(attribute1, definition.getAttribute("name1"));
        assertEquals(attribute2, definition.getAttribute("name2"));
    }

    /**
     * Verifies the put Attribute functionality. Attributes are added or replaced in the definition.
     */
    @Test
    public void testPutAttribute() {
        Definition def = new Definition();
        def.setName("test1");
        def.setTemplateAttribute(Attribute
            .createTemplateAttribute("/page1.jsp"));
        Attribute attr1 = new Attribute("test.definition.name", null, null, "definition");
        def.putAttribute("attr1", attr1);

        attr1 = def.getAttribute("attr1");
        assertNotNull("Null attribute.", attr1);
        assertEquals("Wrong attribute type", "definition", attr1.getRenderer());
    }

    /**
     * Tests the {@link Definition#inherit(BasicAttributeContext)} method.
     */
    @Test
    public void testInherit() {
        Definition toCopy = new Definition();
        toCopy.putAttribute("name1", new Attribute("value1"), true);
        toCopy.putAttribute("name2", new Attribute("value2"), true);
        toCopy.putAttribute("name3", new Attribute("value3"), false);
        toCopy.putAttribute("name4", new Attribute("value4"), false);
        Definition context = new Definition();
        toCopy.putAttribute("name1", new Attribute("newValue1"), true);
        toCopy.putAttribute("name3", new Attribute("newValue3"), false);
        context.inherit(toCopy);
        Attribute attribute = context.getCascadedAttribute("name1");
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

        toCopy = new Definition();
        toCopy.setPreparer("ExtendedPreparer");
        Attribute templateAttribute = new Attribute("extendedTemplate.jsp",
            Expression.createExpression("expression", "language"),
            "extendedRole", "template");
        toCopy.setTemplateAttribute(templateAttribute);
        context = new Definition();
        context.inherit(toCopy);
        assertEquals("Preparer not inherited", "ExtendedPreparer", context
            .getPreparer());
        assertNotNull("Roles not inherited", context.getTemplateAttribute()
            .getRoles());
        assertEquals("Roles not inherited", context.getTemplateAttribute()
            .getRoles().size(), 1);
        assertTrue("Roles not inherited", context.getTemplateAttribute()
            .getRoles().contains(
                "extendedRole"));
        assertEquals("Template not inherited", "extendedTemplate.jsp", context
            .getTemplateAttribute().getValue());
        assertEquals("Template expression not inherited", "expression", context
            .getTemplateAttribute().getExpressionObject().getExpression());
        assertEquals("Template expression language not inherited", "language",
            context.getTemplateAttribute().getExpressionObject()
                .getLanguage());
        context = new Definition();
        context.setPreparer("LocalPreparer");
        templateAttribute = new Attribute("localTemplate.jsp", Expression
            .createExpression("localExpression", "localLanguage"),
            "localRole", "template");
        context.setTemplateAttribute(templateAttribute);
        assertEquals("Preparer inherited", "LocalPreparer", context
            .getPreparer());
        assertNotNull("Roles not correct", context.getTemplateAttribute()
            .getRoles());
        assertEquals("Roles not correct", context.getTemplateAttribute()
            .getRoles().size(), 1);
        assertTrue("Roles inherited", context.getTemplateAttribute().getRoles()
            .contains("localRole"));
        assertEquals("Template inherited", "localTemplate.jsp", context
            .getTemplateAttribute().getValue());
        assertEquals("Template expression inherited", "localExpression",
            context.getTemplateAttribute().getExpressionObject()
                .getExpression());
        assertEquals("Template expression language not inherited",
            "localLanguage", context.getTemplateAttribute()
                .getExpressionObject().getLanguage());
    }

    /**
     * Tests {@link Definition#toString()}.
     */
    @Test
    public void testToString() {
        Definition definition = new Definition();
        definition.setName("myDefinitionName");
        assertEquals(
            "{name=myDefinitionName, template=<null>, role=<null>, preparerInstance=null, attributes=null}",
            definition.toString());
        definition.setTemplateAttribute(Attribute.createTemplateAttribute("myTemplate"));
        assertEquals(
            "{name=myDefinitionName, template=myTemplate, role=null, preparerInstance=null, attributes=null}",
            definition.toString());
        definition.putAttribute("myAttributeName", new Attribute("myAttributeValue"));
        assertEquals(
            "{name=myDefinitionName, template=myTemplate, role=null, preparerInstance=null, "
                + "attributes={myAttributeName=myAttributeValue}}",
            definition.toString());
    }

    /**
     * Tests {@link Definition#equals(Object)}.
     */
    @Test
    public void testEquals() {
        Definition definition = new Definition();
        definition.setName("myDefinition");
        definition.setExtends("myExtends");
        Attribute attribute1 = new Attribute("value1");
        definition.putAttribute("name1", attribute1);
        Attribute attribute2 = new Attribute("value2");
        definition.putAttribute("name2", attribute2);
        Definition toCheck = new Definition(definition);
        assertEquals(definition, toCheck);
        toCheck = new Definition(definition);
        toCheck.setName("anotherDefinition");
        assertNotEquals(definition, toCheck);
        toCheck = new Definition(definition);
        toCheck.setExtends("anotherExtends");
        assertNotEquals(definition, toCheck);
        toCheck = new Definition(definition);
        toCheck.putAttribute("name1", new Attribute("anotherAttribute"));
        assertNotEquals(definition, toCheck);
    }

    /**
     * Tests {@link Definition#hashCode()}.
     */
    @Test
    public void testHashCode() {
        Definition definition = new Definition();
        definition.setName("myDefinition");
        definition.setExtends("myExtends");
        Attribute attribute1 = new Attribute("value1");
        definition.putAttribute("name1", attribute1);
        Attribute attribute2 = new Attribute("value2");
        definition.putAttribute("name2", attribute2);
        BasicAttributeContext attributeContext = new BasicAttributeContext();
        attributeContext.putAttribute("name1", attribute1);
        attributeContext.putAttribute("name2", attribute2);
        assertEquals("myDefinition".hashCode() + "myExtends".hashCode()
            + attributeContext.hashCode(), definition.hashCode());
    }

    /**
     * Tests {@link Definition#isExtending()}.
     */
    @Test
    public void testIsExtending() {
        Definition definition = new Definition();
        definition.setName("myDefinition");
        assertFalse(definition.isExtending());
        definition.setExtends("myExtends");
        assertTrue(definition.isExtending());
    }
}
