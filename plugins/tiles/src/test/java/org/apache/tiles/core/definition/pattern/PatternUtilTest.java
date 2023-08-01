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
package org.apache.tiles.core.definition.pattern;

import org.apache.tiles.api.Attribute;
import org.apache.tiles.api.Definition;
import org.apache.tiles.api.Expression;
import org.apache.tiles.api.ListAttribute;
import org.junit.Test;

import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertTrue;

public class PatternUtilTest {

    /**
     * The size of the list in the main list attribute.
     */
    private static final int LIST_ATTRIBUTE_SIZE = 3;

    /**
     * Test method for
     * {@link PatternUtil#replacePlaceholders(Definition, String, Object[])}.
     */
    @Test
    public void testReplacePlaceholders() {
        Map<String, Attribute> attributes = new HashMap<>();
        attributes.put("attrib1", new Attribute("value{2}"));
        attributes.put("attrib2", new Attribute("value{2}{3}"));
        attributes.put("attrib3", new Attribute(null, Expression
                .createExpression("expr{1}", "EL"), null, null));
        Definition definition = new Definition("definitionName", new Attribute(
                "template{1}"), attributes);
        definition.setExtends("{2}ext");
        definition.setPreparer("{3}prep");
        Definition nudef = PatternUtil.replacePlaceholders(definition, "nudef",
                "value0", "value1", "value2", "value3");
        assertEquals("nudef", nudef.getName());
        assertEquals("value2ext", nudef.getExtends());
        assertEquals("value3prep", nudef.getPreparer());
        Attribute attribute = nudef.getTemplateAttribute();
        assertEquals("templatevalue1", attribute.getValue());
        attribute = nudef.getAttribute("attrib1");
        assertEquals("valuevalue2", attribute.getValue());
        attribute = nudef.getAttribute("attrib2");
        assertEquals("valuevalue2value3", attribute.getValue());
        attribute = nudef.getAttribute("attrib3");
        assertEquals("exprvalue1", attribute.getExpressionObject().getExpression());
    }

    /**
     * Test method for
     * {@link PatternUtil#replacePlaceholders(Definition, String, Object[])}.
     */
    @Test
    public void testReplacePlaceholdersNullTemplate() {
        Map<String, Attribute> attributes = new HashMap<>();
        attributes.put("attrib1", new Attribute("value{2}"));
        attributes.put("attrib2", new Attribute("value{2}{3}"));
        Definition definition = new Definition("definitionName", null, attributes);
        Definition nudef = PatternUtil.replacePlaceholders(definition, "nudef",
                "value0", "value1", "value2", "value3");
        assertEquals("nudef", nudef.getName());
        assertNull(nudef.getTemplateAttribute());
        Attribute attribute = nudef.getAttribute("attrib1");
        assertEquals("valuevalue2", attribute.getValue());
        attribute = nudef.getAttribute("attrib2");
        assertEquals("valuevalue2value3", attribute.getValue());
    }

    /**
     * Test method for
     * {@link PatternUtil#replacePlaceholders(Definition, String, Object[])}.
     */
    @Test
    public void testReplacePlaceholdersCascadedAttributes() {
        Definition definition = new Definition("definitionName", new Attribute(
                "template{1}"), null);
        definition.putAttribute("attrib1", new Attribute("value{2}"), true);
        definition.putAttribute("attrib2", new Attribute("value{2}{3}"), true);
        Definition nudef = PatternUtil.replacePlaceholders(definition, "nudef",
                "value0", "value1", "value2", "value3");
        assertEquals("nudef", nudef.getName());
        Attribute attribute = nudef.getTemplateAttribute();
        assertEquals("templatevalue1", attribute.getValue());
        attribute = nudef.getAttribute("attrib1");
        assertEquals("valuevalue2", attribute.getValue());
        attribute = nudef.getAttribute("attrib2");
        assertEquals("valuevalue2value3", attribute.getValue());
    }

    /**
     * Test method for
     * {@link PatternUtil#replacePlaceholders(Definition, String, Object[])}.
     */
    @Test
    public void testReplacePlaceholdersListAttribute() {
        Map<String, Attribute> attributes = new HashMap<>();
        ListAttribute listAttribute = new ListAttribute();
        ListAttribute internalListAttribute = new ListAttribute();
        listAttribute.setInherit(true);
        attributes.put("myList", listAttribute);
        listAttribute.add(new Attribute("value{2}"));
        listAttribute.add(new Attribute("value{2}{3}"));
        listAttribute.add(internalListAttribute);
        internalListAttribute.add(new Attribute("secondvalue{2}"));
        internalListAttribute.add(new Attribute("secondvalue{2}{3}"));
        Definition definition = new Definition("definitionName", new Attribute(
                "template{1}"), attributes);
        Definition nudef = PatternUtil.replacePlaceholders(definition, "nudef",
                "value0", "value1", "value2", "value3");
        assertEquals("nudef", nudef.getName());
        Attribute attribute = nudef.getTemplateAttribute();
        assertEquals("templatevalue1", attribute.getValue());
        ListAttribute nuListAttribute = (ListAttribute) nudef.getAttribute("myList");
        assertTrue(nuListAttribute.isInherit());
        List<Attribute> list = nuListAttribute.getValue();
        assertEquals(LIST_ATTRIBUTE_SIZE, list.size());
        attribute = list.get(0);
        assertEquals("valuevalue2", attribute.getValue());
        attribute = list.get(1);
        assertEquals("valuevalue2value3", attribute.getValue());
        ListAttribute evaluatedListAttribute = (ListAttribute) list.get(2);
        assertFalse(evaluatedListAttribute.isInherit());
        list = evaluatedListAttribute.getValue();
        assertEquals(2, list.size());
        attribute = list.get(0);
        assertEquals("secondvaluevalue2", attribute.getValue());
        attribute = list.get(1);
        assertEquals("secondvaluevalue2value3", attribute.getValue());
    }


    /**
     * Tests {@link PatternUtil#createExtractedMap(Map, Set)}.
     */
    @Test
    public void testCreateExtractedMap() {
        Map<Integer, String> map = new HashMap<>();
        map.put(0, "value0");
        map.put(1, "value1");
        map.put(2, "value2");
        Set<Integer> set = new HashSet<>();
        set.add(1);
        set.add(2);
        Map<Integer, String> extractedMap = PatternUtil.createExtractedMap(map, set);
        assertEquals(2, extractedMap.size());
        assertEquals("value1", extractedMap.get(1));
        assertEquals("value2", extractedMap.get(2));
    }

    /**
     * Test method for
     * {@link PatternUtil#replacePlaceholders(Definition, String, Object[])}.
     * See TILES-502
     */
    @Test
    public void testReplacePlaceholdersEL_0() {
        Map<String, Attribute> attributes = new HashMap<>();
        Attribute attribute = new Attribute("some-{1}-${requestScope.someVariable}.jsp");
        attribute.setExpressionObject(new Expression((String)attribute.getValue()));
        attributes.put("something", attribute);
        Definition definition = new Definition("definitionName", new Attribute("template"), attributes);
        Definition nudef = PatternUtil.replacePlaceholders(definition, "nudef", "value0", "value1", "value2", "value3");
        assertEquals("nudef", nudef.getName());

        assertEquals(
                "some-value1-${requestScope.someVariable}.jsp",
                nudef.getAttribute("something").getValue());

        assertEquals(
                "some-value1-${requestScope.someVariable}.jsp",
                nudef.getAttribute("something").getExpressionObject().getExpression());
    }

    /**
     * Test method for
     * {@link PatternUtil#replacePlaceholders(Definition, String, Object[])}.
     * See TILES-574
     */
    @Test
    public void testReplacePlaceholdersEL_1() {
        Map<String, Attribute> attributes = new HashMap<>();
        Attribute attribute = new Attribute("some-{1}-${requestScope.someVariable}-other-{2}.jsp");
        attribute.setExpressionObject(new Expression((String)attribute.getValue()));
        attributes.put("something", attribute);
        Definition definition = new Definition("definitionName", new Attribute("template"), attributes);
        Definition nudef = PatternUtil.replacePlaceholders(definition, "nudef", "value0", "value1", "value2", "value3");
        assertEquals("nudef", nudef.getName());

        assertEquals(
                "some-value1-${requestScope.someVariable}-other-value2.jsp",
                nudef.getAttribute("something").getValue());

        assertEquals(
                "some-value1-${requestScope.someVariable}-other-value2.jsp",
                nudef.getAttribute("something").getExpressionObject().getExpression());
    }

    /**
     * Test method for
     * {@link PatternUtil#replacePlaceholders(Definition, String, Object[])}.
     * See TILES-574
     */
    @Test
    public void testReplacePlaceholdersEL_2() {
        Map<String, Attribute> attributes = new HashMap<>();
        Attribute attribute = new Attribute("some-${requestScope.someVariable}-other-{1}-${requestScope.someOtherVariable}.jsp");
        attribute.setExpressionObject(new Expression((String)attribute.getValue()));
        attributes.put("something", attribute);
        Definition definition = new Definition("definitionName", new Attribute("template"), attributes);
        Definition nudef = PatternUtil.replacePlaceholders(definition, "nudef", "value0", "value1", "value2", "value3");
        assertEquals("nudef", nudef.getName());

        assertEquals(
                "some-${requestScope.someVariable}-other-value1-${requestScope.someOtherVariable}.jsp",
                nudef.getAttribute("something").getValue());

        assertEquals(
                "some-${requestScope.someVariable}-other-value1-${requestScope.someOtherVariable}.jsp",
                nudef.getAttribute("something").getExpressionObject().getExpression());
    }

    /**
     * Test method for
     * {@link PatternUtil#replacePlaceholders(Definition, String, Object[])}.
     */
    @Test
    public void testReplacePlaceholdersEL_conditional() {
        Map<String, Attribute> attributes = new HashMap<>();
        Attribute attribute = new Attribute("{1}/some-other-{2}-${requestScope.someBoolean ? 'a' : 'b'}.jsp");
        attribute.setExpressionObject(new Expression((String)attribute.getValue()));
        attributes.put("something", attribute);
        Definition definition = new Definition("definitionName", new Attribute("template"), attributes);
        Definition nudef = PatternUtil.replacePlaceholders(definition, "nudef", "value0", "value1", "value2", "value3");
        assertEquals("nudef", nudef.getName());

        assertEquals(
                "value1/some-other-value2-${requestScope.someBoolean ? 'a' : 'b'}.jsp",
                nudef.getAttribute("something").getValue());

        assertEquals(
                "value1/some-other-value2-${requestScope.someBoolean ? 'a' : 'b'}.jsp",
                nudef.getAttribute("something").getExpressionObject().getExpression());
    }

    /**
     * Test method for
     * {@link PatternUtil#replacePlaceholders(Definition, String, Object[])}.
     */
    @Test
    public void testReplacePlaceholdersEL_twice() {
        Map<String, Attribute> attributes = new HashMap<>();
        Attribute attribute = new Attribute("some-${requestScope.firstVariable}-${requestScope.secondVariable}.jsp");
        attribute.setExpressionObject(new Expression((String)attribute.getValue()));
        attributes.put("something", attribute);
        Definition definition = new Definition("definitionName", new Attribute("template"), attributes);
        Definition nudef = PatternUtil.replacePlaceholders(definition, "nudef", "value0", "value1", "value2", "value3");
        assertEquals("nudef", nudef.getName());

        assertEquals(
                "some-${requestScope.firstVariable}-${requestScope.secondVariable}.jsp",
                nudef.getAttribute("something").getValue());

        assertEquals(
                "some-${requestScope.firstVariable}-${requestScope.secondVariable}.jsp",
                nudef.getAttribute("something").getExpressionObject().getExpression());
    }

    /**
     * Test method for
     * {@link PatternUtil#replacePlaceholders(Definition, String, Object[])}.
     */
    @Test
    public void testReplacePlaceholdersEL_options() {
        Map<String, Attribute> attributes = new HashMap<>();
        Attribute attribute = new Attribute("{1}/{options[my_fallback}}/some-other-{2}-${requestScope.someVariable}.jsp");
        attribute.setExpressionObject(new Expression((String)attribute.getValue()));
        attributes.put("something", attribute);
        Definition definition = new Definition("definitionName", new Attribute("template"), attributes);
        Definition nudef = PatternUtil.replacePlaceholders(definition, "nudef", "value0", "value1", "value2", "value3");
        assertEquals("nudef", nudef.getName());

        assertEquals(
                "value1/{options[my_fallback}}/some-other-value2-${requestScope.someVariable}.jsp",
                nudef.getAttribute("something").getValue());

        assertEquals(
                "value1/{options[my_fallback}}/some-other-value2-${requestScope.someVariable}.jsp",
                nudef.getAttribute("something").getExpressionObject().getExpression());
    }
}
