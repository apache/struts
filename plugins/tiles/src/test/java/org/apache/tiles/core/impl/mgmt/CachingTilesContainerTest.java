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
package org.apache.tiles.core.impl.mgmt;

import org.apache.tiles.api.Definition;
import org.apache.tiles.api.TilesContainer;
import org.apache.tiles.core.definition.NoSuchDefinitionException;
import org.apache.tiles.request.Request;
import org.junit.Before;
import org.junit.Test;

import java.util.HashMap;
import java.util.Map;

import static org.easymock.EasyMock.createMock;
import static org.easymock.EasyMock.eq;
import static org.easymock.EasyMock.expect;
import static org.easymock.EasyMock.isA;
import static org.easymock.EasyMock.replay;
import static org.easymock.EasyMock.verify;
import static org.junit.Assert.assertSame;
import static org.junit.Assert.assertTrue;

/**
 * Tests {@link CachingTilesContainer}.
 *
 * @version $Rev$ $Date$
 */
public class CachingTilesContainerTest {

    /**
     * The default name of the attribute in which storing custom definitions.
     */
    private static final String DEFAULT_DEFINITIONS_ATTRIBUTE_NAME =
        "org.apache.tiles.impl.mgmt.DefinitionManager.DEFINITIONS";

    /**
     * The wrapped Tiles container.
     */
    private TilesContainer wrapped;

    /**
     * The Tiles container to test.
     */
    private CachingTilesContainer container;

    /**
     * Sets up the test.
     */
    @Before
    public void setUp() {
        wrapped = createMock(TilesContainer.class);
        container = new CachingTilesContainer(wrapped);
    }

    @Test
    public void testCachingTilesContainer() {
        Request request = createMock(Request.class);
        Map<String, Definition> definitions = createMock(Map.class);
        Map<String, Object> scope = createMock(Map.class);
        Definition definition = createMock(Definition.class);
        TilesContainer wrapped = createMock(TilesContainer.class);

        expect(request.getContext("request")).andReturn(scope);
        expect(scope.get(DEFAULT_DEFINITIONS_ATTRIBUTE_NAME)).andReturn(definitions);
        expect(definitions.get("definition")).andReturn(definition);

        replay(wrapped, request, definitions, scope, definition);
        CachingTilesContainer container = new CachingTilesContainer(wrapped);
        assertSame(definition, container.getDefinition("definition", request));
        verify(wrapped, request, definitions, scope, definition);
    }

    @Test
    public void testGetDefinition() {
        Request request = createMock(Request.class);
        Map<String, Definition> definitions = createMock(Map.class);
        Map<String, Object> scope = createMock(Map.class);
        Definition definition = createMock(Definition.class);

        expect(request.getContext("request")).andReturn(scope);
        expect(scope.get(DEFAULT_DEFINITIONS_ATTRIBUTE_NAME)).andReturn(
            definitions);
        expect(definitions.get("definition")).andReturn(definition);

        replay(wrapped, request, definitions, scope, definition);
        assertSame(definition, container.getDefinition("definition", request));
        verify(wrapped, request, definitions, scope, definition);
    }

    @Test
    public void testGetDefinitionContainer() {
        Request request = createMock(Request.class);
        Map<String, Object> scope = createMock(Map.class);
        Definition definition = createMock(Definition.class);

        expect(request.getContext("request")).andReturn(scope);
        expect(scope.get(DEFAULT_DEFINITIONS_ATTRIBUTE_NAME)).andReturn(
            null);
        expect(wrapped.getDefinition("definition", request)).andReturn(
            definition);

        replay(wrapped, request, scope, definition);
        assertSame(definition, container.getDefinition("definition", request));
        verify(wrapped, request, scope, definition);
    }

    @Test
    public void testIsValidDefinition() {
        Request request = createMock(Request.class);
        Map<String, Definition> definitions = createMock(Map.class);
        Map<String, Object> scope = createMock(Map.class);
        Definition definition = createMock(Definition.class);

        expect(request.getContext("request")).andReturn(scope);
        expect(scope.get(DEFAULT_DEFINITIONS_ATTRIBUTE_NAME)).andReturn(definitions);
        expect(definitions.get("definition")).andReturn(definition);

        replay(wrapped, request, definitions, scope, definition);
        assertTrue(container.isValidDefinition("definition", request));
        verify(wrapped, request, definitions, scope, definition);
    }

    @Test
    public void testIsValidDefinitionContainer() {
        Request request = createMock(Request.class);
        Map<String, Definition> definitions = createMock(Map.class);
        Map<String, Object> scope = createMock(Map.class);

        expect(request.getContext("request")).andReturn(scope);
        expect(scope.get(DEFAULT_DEFINITIONS_ATTRIBUTE_NAME)).andReturn(definitions);
        expect(definitions.get("definition")).andReturn(null);
        expect(wrapped.isValidDefinition("definition", request)).andReturn(true);

        replay(wrapped, request, definitions, scope);
        assertTrue(container.isValidDefinition("definition", request));
        verify(wrapped, request, definitions, scope);
    }

    @Test
    public void testRegister() {
        Request request = createMock(Request.class);
        Map<String, Definition> definitions = createMock(Map.class);
        Map<String, Object> scope = createMock(Map.class);
        Definition definition = createMock(Definition.class);

        expect(request.getContext("request")).andReturn(scope);
        expect(scope.get(DEFAULT_DEFINITIONS_ATTRIBUTE_NAME)).andReturn(definitions);
        expect(definition.getName()).andReturn(null);
        expect(definitions.containsKey("$anonymousMutableDefinition1")).andReturn(false);
        definition.setName("$anonymousMutableDefinition1");
        expect(definition.isExtending()).andReturn(true);
        // trick to test resolve definition separately.
        expect(definition.isExtending()).andReturn(false);
        expect(definition.getName()).andReturn("$anonymousMutableDefinition1");
        expect(definitions.put("$anonymousMutableDefinition1", definition)).andReturn(null);

        replay(wrapped, request, definitions, scope, definition);
        container.register(definition, request);
        verify(wrapped, request, definitions, scope, definition);
    }

    @Test
    public void testRegisterInheritance() {
        Request request = createMock(Request.class);
        Map<String, Definition> definitions = createMock(Map.class);
        Map<String, Object> scope = createMock(Map.class);
        Definition definition = createMock(Definition.class);
        Definition parent = createMock(Definition.class);
        Definition grandparent = createMock(Definition.class);

        expect(request.getContext("request")).andReturn(scope).anyTimes();
        expect(scope.get(DEFAULT_DEFINITIONS_ATTRIBUTE_NAME)).andReturn(
            definitions).anyTimes();
        expect(definition.getName()).andReturn(null);
        expect(definitions.containsKey("$anonymousMutableDefinition1"))
            .andReturn(false);
        definition.setName("$anonymousMutableDefinition1");
        expect(definition.isExtending()).andReturn(true);
        // trick to test resolve definition separately.
        expect(definition.isExtending()).andReturn(true);
        expect(definition.getExtends()).andReturn("parent");
        expect(definitions.get("parent")).andReturn(parent);
        expect(parent.isExtending()).andReturn(true);
        expect(parent.getExtends()).andReturn("grandparent");
        expect(definition.getName()).andReturn("$anonymousMutableDefinition1");
        expect(definitions.get("grandparent")).andReturn(null);
        expect(wrapped.getDefinition("grandparent", request)).andReturn(
            grandparent);
        parent.inherit(grandparent);
        definition.inherit(parent);
        expect(definitions.put("$anonymousMutableDefinition1", definition))
            .andReturn(null);

        replay(wrapped, request, definitions, scope, definition, parent,
            grandparent);
        container.register(definition, request);
        verify(wrapped, request, definitions, scope, definition, parent,
            grandparent);
    }

    @Test(expected = NoSuchDefinitionException.class)
    public void testRegisterInheritanceFail() {
        Request request = createMock(Request.class);
        Map<String, Definition> definitions = createMock(Map.class);
        Map<String, Object> scope = createMock(Map.class);
        Definition definition = createMock(Definition.class);

        expect(request.getContext("request")).andReturn(scope).anyTimes();
        expect(scope.get(DEFAULT_DEFINITIONS_ATTRIBUTE_NAME)).andReturn(
            definitions).anyTimes();
        expect(definition.getName()).andReturn(null);
        expect(definitions.containsKey("$anonymousMutableDefinition1"))
            .andReturn(false);
        definition.setName("$anonymousMutableDefinition1");
        expect(definition.isExtending()).andReturn(true);
        // trick to test resolve definition separately.
        expect(definition.isExtending()).andReturn(true);
        expect(definition.getExtends()).andReturn("parent");
        expect(definitions.get("parent")).andReturn(null);
        expect(wrapped.getDefinition("parent", request)).andReturn(null);
        expect(definition.getName()).andReturn("$anonymousMutableDefinition1");

        replay(wrapped, request, definitions, scope, definition);
        try {
            container.register(definition, request);
        } finally {
            verify(wrapped, request, definitions, scope, definition);
        }
    }

    @Test
    public void testRegisterCreateDefinitions() {
        Request request = createMock(Request.class);
        Map<String, Object> scope = createMock(Map.class);
        Definition definition = createMock(Definition.class);

        expect(request.getContext("request")).andReturn(scope).anyTimes();
        expect(scope.get(DEFAULT_DEFINITIONS_ATTRIBUTE_NAME)).andReturn(
            null);
        expect(scope.put(eq(DEFAULT_DEFINITIONS_ATTRIBUTE_NAME), isA(HashMap.class))).andReturn(null);
        expect(definition.getName()).andReturn(null);
        definition.setName("$anonymousMutableDefinition1");
        expect(definition.isExtending()).andReturn(true);
        // trick to test resolve definition separately.
        expect(definition.isExtending()).andReturn(false);
        expect(definition.getName()).andReturn("$anonymousMutableDefinition1");

        replay(wrapped, request, scope, definition);
        container.register(definition, request);
        verify(wrapped, request, scope, definition);
    }

    @Test
    public void testRender() {
        Request request = createMock(Request.class);
        Map<String, Definition> definitions = createMock(Map.class);
        Map<String, Object> scope = createMock(Map.class);
        Definition definition = createMock(Definition.class);

        expect(request.getContext("request")).andReturn(scope);
        expect(scope.get(DEFAULT_DEFINITIONS_ATTRIBUTE_NAME)).andReturn(definitions);
        expect(definitions.get("definition")).andReturn(definition);
        container.render(definition, request);

        replay(wrapped, request, definitions, scope, definition);
        container.render("definition", request);
        verify(wrapped, request, definitions, scope, definition);
    }

    @Test(expected = NoSuchDefinitionException.class)
    public void testRenderFail() {
        Request request = createMock(Request.class);
        Map<String, Definition> definitions = createMock(Map.class);
        Map<String, Object> scope = createMock(Map.class);

        expect(request.getContext("request")).andReturn(scope);
        expect(scope.get(DEFAULT_DEFINITIONS_ATTRIBUTE_NAME)).andReturn(definitions);
        expect(definitions.get("definition")).andReturn(null);
        expect(wrapped.getDefinition("definition", request)).andReturn(null);

        replay(wrapped, request, definitions, scope);
        try {
            container.render("definition", request);
        } finally {
            verify(wrapped, request, definitions, scope);
        }
    }
}
