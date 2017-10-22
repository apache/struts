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
package org.apache.struts2.tiles;

import java.util.List;
import java.util.Set;

import org.apache.struts2.tiles.annotation.TilesDefinition;
import org.apache.tiles.Attribute;
import org.apache.tiles.Definition;
import org.apache.tiles.Expression;
import org.junit.Test;

import org.junit.Assert;

public class StrutsTilesAnnotationProcessorTest {

    @Test
    public void findAnnotationSingleAction() {
        StrutsTilesAnnotationProcessor annotationProcessor = new StrutsTilesAnnotationProcessor();
        TilesDefinition tilesDefinition = annotationProcessor.findAnnotation(new TilesTestActionSingleAnnotation(), null);
        Assert.assertNotNull(tilesDefinition);
        Assert.assertEquals("definition-name", tilesDefinition.name());
    }

    @Test
    public void findAnnotationMultipleActionNameNull() {
        StrutsTilesAnnotationProcessor annotationProcessor = new StrutsTilesAnnotationProcessor();
        TilesDefinition tilesDefinition = annotationProcessor.findAnnotation(new TilesTestActionMultipleAnnotations(), null);
        Assert.assertNotNull(tilesDefinition);
        Assert.assertEquals("def1", tilesDefinition.name());
    }

    @Test
    public void findAnnotationMultipleActionNameGiven() {
        StrutsTilesAnnotationProcessor annotationProcessor = new StrutsTilesAnnotationProcessor();
        TilesDefinition tilesDefinition = annotationProcessor.findAnnotation(new TilesTestActionMultipleAnnotations(), "def2");
        Assert.assertNotNull(tilesDefinition);
        Assert.assertEquals("def2", tilesDefinition.name());
    }

    @Test
    public void findAnnotationMultipleActionNotFound() {
        StrutsTilesAnnotationProcessor annotationProcessor = new StrutsTilesAnnotationProcessor();
        TilesDefinition tilesDefinition = annotationProcessor.findAnnotation(new TilesTestActionMultipleAnnotations(), "def3");
        Assert.assertNull(tilesDefinition);
    }

    @Test
    public void buildDefiniton() {
        StrutsTilesAnnotationProcessor annotationProcessor = new StrutsTilesAnnotationProcessor();
        TilesDefinition tilesDefinition = annotationProcessor.findAnnotation(new TilesTestActionSingleAnnotation(), null);

        Definition definition = annotationProcessor.buildTilesDefinition("tileName", tilesDefinition);

        Assert.assertNotNull(definition);
        Assert.assertEquals("tileName", definition.getName());
        Assert.assertEquals("preparer", definition.getPreparer());
        Assert.assertEquals("base-definition", definition.getExtends());
        Attribute templateAttribute = definition.getTemplateAttribute();
        Assert.assertEquals("template", templateAttribute.getValue());
        Assert.assertEquals("type", templateAttribute.getRenderer());
        Assert.assertEquals("role", templateAttribute.getRole());
        Expression definitionExpressionObject = templateAttribute.getExpressionObject();
        Assert.assertEquals("templ*", definitionExpressionObject.getExpression());
        Assert.assertNull(definitionExpressionObject.getLanguage());

        Attribute putAttribute = definition.getAttribute("put-attr");
        Assert.assertNotNull(putAttribute);
        Assert.assertEquals("attr-val", putAttribute.getValue());
        Assert.assertEquals("attr-type", putAttribute.getRenderer());
        Assert.assertEquals("attr-role", putAttribute.getRole());
        Expression putAttrExpressionObject = putAttribute.getExpressionObject();
        Assert.assertEquals("expr", putAttrExpressionObject.getExpression());
        Assert.assertEquals("lang", putAttrExpressionObject.getLanguage());

        Attribute listAttribute = definition.getAttribute("list-name");
        Assert.assertEquals("list-role", listAttribute.getRole());
        List<Attribute> listValue = getListValue(listAttribute);
        Assert.assertEquals(2, listValue.size());

        Attribute addAttribute = listValue.get(0);
        Assert.assertEquals("list-attr-role", addAttribute.getRole());
        Assert.assertEquals("list-attr-val", addAttribute.getValue());
        Assert.assertEquals("list-attr-type", addAttribute.getRenderer());
        Expression addAttrExpressionObject = addAttribute.getExpressionObject();
        Assert.assertEquals("list-attr-expr", addAttrExpressionObject.getExpression());

        Attribute addListAttribute = listValue.get(1);
        Assert.assertEquals("list-list-attr-role", addListAttribute.getRole());
        List<Attribute> addListValue = getListValue(addListAttribute);
        Assert.assertEquals(1, addListValue.size());
        Assert.assertEquals("list-list-add-attr", addListValue.get(0).getValue());

        Set<String> cascadedAttributeNames = definition.getCascadedAttributeNames();
        Assert.assertEquals(2, cascadedAttributeNames.size());
        Assert.assertTrue(cascadedAttributeNames.contains("put-attr"));
        Assert.assertTrue(cascadedAttributeNames.contains("list-name"));
    }

    @Test
    public void buildDefinitonAllEmpty() {
        StrutsTilesAnnotationProcessor annotationProcessor = new StrutsTilesAnnotationProcessor();
        TilesDefinition tilesDefinition = annotationProcessor.findAnnotation(new TilesTestActionSingleAnnotationAllEmpty(), null);

        Definition definition = annotationProcessor.buildTilesDefinition(null, tilesDefinition);

        Assert.assertNotNull(definition);
        Assert.assertNull(definition.getName());
        Assert.assertNull(definition.getPreparer());
        Assert.assertNull(definition.getExtends());
        Attribute templateAttribute = definition.getTemplateAttribute();
        Assert.assertNull(templateAttribute.getValue());
        Assert.assertNull(templateAttribute.getRole());
        Assert.assertNull(templateAttribute.getExpressionObject());

        Attribute putAttribute = definition.getAttribute("put-attr");
        Assert.assertNotNull(putAttribute);
        Assert.assertNull(putAttribute.getValue());
        Assert.assertNull(putAttribute.getRenderer());
        Assert.assertNull(putAttribute.getRole());
        Assert.assertNull(putAttribute.getExpressionObject());

        Attribute listAttribute = definition.getAttribute("list-name");
        Assert.assertNull(listAttribute.getRole());
        List<Attribute> listValue = getListValue(listAttribute);
        Assert.assertEquals(2, listValue.size());

        Attribute addAttribute = listValue.get(0);
        Assert.assertNull(addAttribute.getRole());
        Assert.assertNull(addAttribute.getValue());
        Assert.assertNull(addAttribute.getRenderer());
        Assert.assertNull(addAttribute.getExpressionObject());

        Attribute addListAttribute = listValue.get(1);
        Assert.assertNull(addListAttribute.getRole());
        List<Attribute> addListValue = getListValue(addListAttribute);
        Assert.assertEquals(1, addListValue.size());
        Assert.assertNull(addListValue.get(0).getValue());

        Set<String> cascadedAttributeNames = definition.getCascadedAttributeNames();
        Assert.assertNull(cascadedAttributeNames);
    }

    @SuppressWarnings("unchecked")
    protected List<Attribute> getListValue(Attribute listAttribute) {
        return (List<Attribute>) listAttribute.getValue();
    }
}
