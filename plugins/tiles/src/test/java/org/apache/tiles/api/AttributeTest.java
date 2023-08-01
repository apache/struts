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

import org.apache.tiles.request.Request;
import org.junit.Test;

import java.util.HashSet;
import java.util.LinkedHashSet;
import java.util.Objects;
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
 * Tests {@link Attribute}.
 */
public class AttributeTest {

    /**
     * Tests {@link Attribute#createTemplateAttribute(String)}.
     */
    @Test
    public void testCreateTemplateAttribute1() {
        Attribute attribute = Attribute.createTemplateAttribute("/my/template.jsp");
        assertEquals("/my/template.jsp", attribute.getValue());
        assertEquals("template", attribute.getRenderer());
    }

    /**
     * Tests {@link Attribute#Attribute()}.
     */
    @Test
    public void testAttribute() {
        Attribute attribute = new Attribute();
        assertNull(attribute.getValue());
    }

    /**
     * Tests {@link Attribute#Attribute(Object)}.
     */
    @Test
    public void testAttributeObject() {
        Attribute attribute = new Attribute("my.value");
        assertEquals("my.value", attribute.getValue());
        assertNull(attribute.getRenderer());
    }

    /**
     * Tests {@link Attribute#Attribute(Object, String)}.
     */
    @Test
    public void testAttributeObjectString() {
        Attribute attribute = new Attribute("my.value", "role1,role2");
        assertEquals("my.value", attribute.getValue());
        assertNull(attribute.getRenderer());
        Set<String> roles = new HashSet<>();
        roles.add("role1");
        roles.add("role2");
        assertEquals(roles, attribute.getRoles());
    }

    /**
     * Tests {@link Attribute#Attribute(Object, Expression, String, String)}.
     */
    @Test
    public void testAttributeComplete() {
        Expression expression = new Expression("my.expression", "MYLANG");
        Attribute attribute = new Attribute("my.value", expression, "role1,role2", "myrenderer");
        assertEquals("my.value", attribute.getValue());
        assertEquals("myrenderer", attribute.getRenderer());
        Set<String> roles = new HashSet<>();
        roles.add("role1");
        roles.add("role2");
        assertEquals(roles, attribute.getRoles());
        assertEquals("my.expression", attribute.getExpressionObject().getExpression());
        assertEquals("MYLANG", attribute.getExpressionObject().getLanguage());
    }

    /**
     * Tests {@link Attribute#Attribute(Attribute)}.
     */
    @Test
    public void testAttributeCopy() {
        Expression expression = new Expression("my.expression", "MYLANG");
        Attribute attribute = new Attribute("my.value", expression, "role1,role2", "myrenderer");
        attribute = new Attribute(attribute);
        assertEquals("my.value", attribute.getValue());
        assertEquals("myrenderer", attribute.getRenderer());
        Set<String> roles = new HashSet<>();
        roles.add("role1");
        roles.add("role2");
        assertEquals(roles, attribute.getRoles());
        assertEquals("my.expression", attribute.getExpressionObject().getExpression());
        assertEquals("MYLANG", attribute.getExpressionObject().getLanguage());

        attribute = new Attribute("my.value", null, "role1,role2", "myrenderer");
        attribute = new Attribute(attribute);
        assertEquals("my.value", attribute.getValue());
        assertEquals("myrenderer", attribute.getRenderer());
        roles = new HashSet<>();
        roles.add("role1");
        roles.add("role2");
        assertEquals(roles, attribute.getRoles());
        assertNull(attribute.getExpressionObject());
    }

    /**
     * Tests {@link Attribute#equals(Object)}.
     */
    @Test
    public void testEquals() {
        Expression expression = new Expression("my.expression", "MYLANG");
        Attribute attribute = new Attribute("my.value", expression, "role1,role2", "myrenderer");
        Attribute attribute2 = new Attribute(attribute);
        assertEquals(attribute, attribute2);
        attribute2.setRenderer("anotherRenderer");
        assertNotEquals(attribute, attribute2);
        attribute2 = new Attribute(attribute);
        attribute2.setRole("otherrole");
        assertNotEquals(attribute, attribute2);
        attribute2 = new Attribute(attribute);
        attribute2.setExpressionObject(new Expression("another.expression", "MYLANG"));
        assertNotEquals(attribute, attribute2);
        attribute2 = new Attribute(attribute);
        attribute2.setValue("anothervalue");
        assertNotEquals(attribute, attribute2);
    }

    /**
     * Tests {@link Attribute#getRole()} and {@link Attribute#setRole(String)}.
     */
    @Test
    public void testGetRole() {
        Attribute attribute = new Attribute("my.value");
        assertNull(attribute.getRole());
        Set<String> roles = new LinkedHashSet<>();
        attribute.setRoles(roles);
        assertNull(attribute.getRole());
        roles.add("role1");
        roles.add("role2");
        assertEquals("role1,role2", attribute.getRole());
    }

    /**
     * Tests {@link Attribute#hashCode()}.
     */
    @Test
    public void testHashCode() {
        Expression expression = new Expression("my.expression", "MYLANG");
        Attribute attribute = new Attribute("my.value", expression, "role1,role2", "myrenderer");
        Set<String> roles = new HashSet<>();
        roles.add("role1");
        roles.add("role2");
        assertEquals(
            Objects.hashCode("my.value")
                + Objects.hashCode(expression) + Objects.hashCode(roles)
                + Objects.hashCode("myrenderer"), attribute.hashCode()
        );
    }

    /**
     * Tests {@link Attribute#toString()}.
     */
    @Test
    public void testToString() {
        Expression expression = new Expression("my.expression", "MYLANG");
        Attribute attribute = new Attribute("my.value", expression, "role1,role2", "myrenderer");
        assertEquals("my.value", attribute.toString());
        attribute.setValue(null);
        assertNotNull(attribute.toString());
    }

    @Test
    public void testInherit() {
        Attribute attribute = new Attribute(null, null, null, null);
        Attribute parentAttribute = new Attribute("value", Expression
            .createExpression("expression", "language"), "role", "renderer");
        attribute.inherit(parentAttribute);
        assertEquals("value", attribute.getValue());
        assertEquals("expression", attribute.getExpressionObject().getExpression());
        assertEquals("language", attribute.getExpressionObject().getLanguage());
        assertEquals("role", attribute.getRole());
        assertEquals("renderer", attribute.getRenderer());
        Expression expression = new Expression(null, "MYLANG");
        attribute = new Attribute(null, expression, null, null);
        attribute.setRoles(new HashSet<>());
        attribute.inherit(parentAttribute);
        assertEquals("value", attribute.getValue());
        assertEquals("expression", attribute.getExpressionObject().getExpression());
        assertEquals("language", attribute.getExpressionObject().getLanguage());
        assertEquals("role", attribute.getRole());
        assertEquals("renderer", attribute.getRenderer());
    }

    /**
     * Tests {@link Attribute#clone()}.
     */
    @Test
    public void testClone() {
        Expression expression = new Expression("my.expression", "MYLANG");
        Attribute attribute = new Attribute("my.value", expression, "role1,role2", "myrenderer");
        attribute = attribute.copy();
        assertEquals("my.value", attribute.getValue());
        assertEquals("myrenderer", attribute.getRenderer());
        Set<String> roles = new HashSet<>();
        roles.add("role1");
        roles.add("role2");
        assertEquals(roles, attribute.getRoles());
        assertEquals("my.expression", attribute.getExpressionObject().getExpression());
        assertEquals("MYLANG", attribute.getExpressionObject().getLanguage());
    }

    /**
     * Tests {@link Attribute#createTemplateAttribute(String, String, String, String)}.
     */
    @Test
    public void testCreateTemplateAttribute() {
        Attribute attribute = Attribute.createTemplateAttribute("myTemplate", "MYLANG:myExpression", "myType", "myRole");
        assertEquals("myTemplate", attribute.getValue());
        assertEquals("MYLANG", attribute.getExpressionObject().getLanguage());
        assertEquals("myExpression", attribute.getExpressionObject().getExpression());
        assertEquals("myType", attribute.getRenderer());
        Set<String> roles = attribute.getRoles();
        assertEquals(1, roles.size());
        assertTrue(roles.contains("myRole"));
    }

    @Test
    public void testIsPermitted() {
        Attribute attribute = new Attribute("myvalue");
        Request requestContext = createMock(Request.class);
        expect(requestContext.isUserInRole("first")).andReturn(Boolean.TRUE)
            .anyTimes();
        expect(requestContext.isUserInRole("second")).andReturn(Boolean.FALSE)
            .anyTimes();
        replay(requestContext);
        assertTrue(attribute.isPermitted(requestContext));
        Set<String> roles = new HashSet<>();
        roles.add("first");
        attribute.setRoles(roles);
        assertTrue("The role is not permitted", attribute.isPermitted(
            requestContext));
        roles.clear();
        roles.add("second");
        assertFalse("The role is not permitted", attribute.isPermitted(
            requestContext));
        verify(requestContext);
    }
}
