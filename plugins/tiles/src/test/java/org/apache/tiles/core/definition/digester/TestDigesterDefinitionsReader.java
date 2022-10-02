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

package org.apache.tiles.core.definition.digester;

import org.apache.tiles.api.Attribute;
import org.apache.tiles.api.Definition;
import org.apache.tiles.api.ListAttribute;
import org.apache.tiles.core.definition.DefinitionsFactoryException;
import org.junit.Before;
import org.junit.Test;

import java.io.IOException;
import java.io.InputStream;
import java.net.URL;
import java.util.List;
import java.util.Map;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertTrue;
import static org.junit.Assert.fail;

/**
 * Tests the <code>org.apache.tiles.definition.digester.DigesterDefinitionsReader</code> class.
 */
public class TestDigesterDefinitionsReader {

    private DigesterDefinitionsReader reader;

    @Before
    public void setUp() {
        reader = new DigesterDefinitionsReader();
    }

    /**
     * Tests the read method under normal conditions.
     *
     * @throws IOException If something goes wrong.
     */
    @Test
    public void testRead() throws IOException {
        URL configFile = this.getClass().getClassLoader().getResource("org/apache/tiles/core/config/tiles-defs.xml");
        assertNotNull("Config file not found", configFile);

        InputStream source = configFile.openStream();
        Map<String, Definition> definitions = reader.read(source);

        assertNotNull("Definitions not returned.", definitions);
        assertNotNull("Couldn't find doc.mainLayout tile.", definitions.get("doc.mainLayout"));
        assertNotNull("Couldn't Find title attribute.", definitions.get("doc.mainLayout").getAttribute("title").getValue());
        assertEquals("Incorrect Find title attribute.", "Tiles Library Documentation", definitions.get("doc.mainLayout").getAttribute("title").getValue());

        Definition def = definitions.get("doc.role.test");
        assertNotNull("Couldn't find doc.role.test tile.", def);
        Attribute attribute = def.getAttribute("title");
        assertNotNull("Couldn't Find title attribute.", attribute.getValue());
        assertEquals("Role 'myrole' expected", attribute.getRole(), "myrole");

        def = definitions.get("doc.listattribute.test");
        assertNotNull("Couldn't find doc.listattribute.test tile.", def);
        attribute = def.getAttribute("items");
        assertNotNull("Couldn't Find items attribute.", attribute);
        assertTrue("The class of the attribute is not right", attribute instanceof ListAttribute);
        assertTrue("The class of value of the attribute is not right", attribute.getValue() instanceof List);
    }


    /**
     * Tests the read method under normal conditions for the new features in 2.1
     * version of the DTD.
     *
     * @throws IOException If something goes wrong.
     */
    @Test
    public void testRead21Version() throws IOException {
        URL configFile = this.getClass().getClassLoader().getResource("org/apache/tiles/core/config/tiles-defs-2.1.xml");
        assertNotNull("Config file not found", configFile);

        InputStream source = configFile.openStream();
        Map<String, Definition> definitions = reader.read(source);

        assertNotNull("Definitions not returned.", definitions);
        Definition def = definitions.get("doc.cascaded.test");

        assertNotNull("Couldn't find doc.role.test tile.", def);
        Attribute attribute = def.getLocalAttribute("title");
        assertNotNull("Couldn't Find title local attribute.", attribute);
        attribute = def.getCascadedAttribute("title2");
        assertNotNull("Couldn't Find title2 cascaded attribute.", attribute);
        attribute = def.getLocalAttribute("items1");
        assertNotNull("Couldn't Find items1 local attribute.", attribute);
        attribute = def.getCascadedAttribute("items2");
        assertNotNull("Couldn't Find items2 cascaded attribute.", attribute);

        def = definitions.get("test.nesting.definitions");
        assertNotNull("Couldn't find test.nesting.definitions tile.", def);
        assertEquals("/layout.jsp", def.getTemplateAttribute().getValue());
        assertEquals("template", def.getTemplateAttribute().getRenderer());
        attribute = def.getAttribute("body");
        assertNotNull("Couldn't Find body attribute.", attribute);
        assertEquals("Attribute not of 'definition' type", "definition", attribute.getRenderer());
        assertNotNull("Attribute value null", attribute.getValue());
        String defName = attribute.getValue().toString();
        def = definitions.get(defName);
        assertNotNull("Couldn't find " + defName + " tile.", def);

        def = definitions.get("test.nesting.list.definitions");
        assertNotNull("Couldn't find test.nesting.list.definitions tile.", def);
        attribute = def.getAttribute("list");
        assertNotNull("Couldn't Find list attribute.", attribute);
        assertTrue("Attribute not of valid type", attribute instanceof ListAttribute);
        ListAttribute listAttribute = (ListAttribute) attribute;
        List<Attribute> list = listAttribute.getValue();
        assertEquals("The list is not of correct size", 1, list.size());
        attribute = list.get(0);
        assertNotNull("Couldn't Find element attribute.", attribute);
        assertEquals("Attribute not of 'definition' type", "definition", attribute.getRenderer());
        assertNotNull("Attribute value null", attribute.getValue());
        defName = attribute.getValue().toString();
        def = definitions.get(defName);
        assertNotNull("Couldn't find " + defName + " tile.", def);

        defName = "test.inherit.list.base";
        def = definitions.get(defName);
        assertNotNull("Couldn't find " + defName + " tile.", def);
        defName = "test.inherit.list";
        def = definitions.get(defName);
        assertNotNull("Couldn't find " + defName + " tile.", def);
        listAttribute = (ListAttribute) def.getAttribute("list");
        assertTrue("This definition does not inherit its list attribute", listAttribute.isInherit());
        defName = "test.noinherit.list";
        def = definitions.get(defName);
        listAttribute = (ListAttribute) def.getAttribute("list");
        assertFalse("This definition inherits its list attribute", listAttribute.isInherit());

        defName = "test.new.attributes";
        def = definitions.get(defName);
        assertNotNull("Couldn't find " + defName + " tile.", def);
        Attribute templateAttribute = def.getTemplateAttribute();
        assertEquals(templateAttribute.getExpressionObject().getExpression(), "${my.expression}");
        assertEquals("mytype", templateAttribute.getRenderer());
        attribute = def.getAttribute("body");
        assertNotNull("Couldn't Find body attribute.", attribute);
        assertEquals("${my.attribute.expression}", attribute.getExpressionObject().getExpression());
    }

    /**
     * Tests read with bad input source.
     */
    @Test
    public void testBadSource() {
        try {
            reader.read("Bad Input");
            fail("Should've thrown an exception.");
        } catch (DefinitionsFactoryException e) {
            assertTrue(true);
        } catch (Exception e) {
            fail("Exception reading configuration." + e);
        }
    }

    /**
     * Tests read with bad XML source.
     */
    @Test
    public void testBadXml() {
        try {
            URL configFile = this.getClass().getClassLoader().getResource(
                "org/apache/tiles/config/malformed-defs.xml");
            assertNotNull("Config file not found", configFile);

            InputStream source = configFile.openStream();
            reader.read(source);
            fail("Should've thrown an exception.");
        } catch (DefinitionsFactoryException e) {
            assertTrue(true);
        } catch (Exception e) {
            fail("Exception reading configuration." + e);
        }
    }

    /**
     * Tests the validating input parameter.
     * <p>
     * This test case enables Digester's validating property then passes in a
     * configuration file with invalid XML.
     */
    @Test
    public void testValidatingParameter() {
        // Testing with default (validation ON).
        try {
            URL configFile = this.getClass().getClassLoader().getResource(
                "org/apache/tiles/config/invalid-defs.xml");
            assertNotNull("Config file not found", configFile);

            InputStream source = configFile.openStream();
            reader.setValidating(true);
            reader.read(source);
            fail("Should've thrown an exception.");
        } catch (DefinitionsFactoryException e) {
            assertTrue(true);
        } catch (Exception e) {
            fail("Exception reading configuration." + e);
        }

        // Testing with validation OFF.
        try {
            setUp();
            URL configFile = this.getClass().getClassLoader().getResource(
                "org/apache/tiles/config/invalid-defs.xml");
            assertNotNull("Config file not found", configFile);

            InputStream source = configFile.openStream();
            reader.read(source);
        } catch (DefinitionsFactoryException e) {
            fail("Should not have thrown an exception." + e);
        } catch (Exception e) {
            fail("Exception reading configuration." + e);
        }
    }

    /**
     * Regression test for bug TILES-352.
     *
     * @throws IOException If something goes wrong.
     */
    @Test
    public void testRegressionTiles352() throws IOException {
        URL configFile = this.getClass().getClassLoader().getResource(
            "org/apache/tiles/config/defs_regression_TILES-352.xml");
        assertNotNull("Config file not found", configFile);

        InputStream source = configFile.openStream();
        Map<String, Definition> name2defs = reader.read(source);
        source.close();
        Definition root = name2defs.get("root");
        Attribute attribute = root.getAttribute("body");
        Definition child = name2defs.get(attribute.getValue());
        ListAttribute listAttribute = (ListAttribute) child.getAttribute("list");
        List<Attribute> list = listAttribute.getValue();
        assertEquals((list.get(0)).getValue(), "This is a value");
    }

    /**
     * Tests {@link DigesterDefinitionsReader#read(Object)}.
     */
    @Test
    public void testReadNoSource() {
        assertNull(reader.read(null));
    }

    /**
     * Tests {@link DigesterDefinitionsReader#addDefinition(Definition)}.
     */
    @Test(expected = DigesterDefinitionsReaderException.class)
    public void testAddDefinitionNoName() {
        Definition def = new Definition();
        reader.addDefinition(def);
    }
}
