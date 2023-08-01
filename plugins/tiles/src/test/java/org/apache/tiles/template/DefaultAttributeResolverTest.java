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

package org.apache.tiles.template;

import org.apache.tiles.api.Attribute;
import org.apache.tiles.api.AttributeContext;
import org.apache.tiles.api.Expression;
import org.apache.tiles.api.TilesContainer;
import org.apache.tiles.request.Request;
import org.junit.Before;
import org.junit.Test;

import static org.easymock.EasyMock.createMock;
import static org.easymock.EasyMock.expect;
import static org.easymock.EasyMock.replay;
import static org.easymock.EasyMock.verify;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNull;

/**
 * Tests {@link DefaultAttributeResolver}.
 */
public class DefaultAttributeResolverTest {

    /**
     * The resolver to test.
     */
    private DefaultAttributeResolver resolver;

    /**
     * Sets up the test.
     */
    @Before
    public void setUp() {
        resolver = new DefaultAttributeResolver();
    }

    /**
     * Test method for {@link DefaultAttributeResolver
     * #computeAttribute(org.apache.tiles.TilesContainer, org.apache.tiles.Attribute,
     * java.lang.String, java.lang.String, boolean, java.lang.Object, java.lang.String,
     * java.lang.String, Request)}.
     */
    @Test
    public void testComputeAttributeInContext() {
        TilesContainer container = createMock(TilesContainer.class);
        AttributeContext attributeContext = createMock(AttributeContext.class);
        Request request = createMock(Request.class);
        Attribute attribute = new Attribute("myValue", Expression.createExpression("myExpression", null), "myRole", "myRenderer");

        expect(container.getAttributeContext(request)).andReturn(attributeContext);
        expect(attributeContext.getAttribute("myName")).andReturn(attribute);

        replay(container, attributeContext, request);
        assertEquals(attribute, resolver.computeAttribute(container, null, "myName", null, false, null, null, null, request));
        verify(container, attributeContext, request);
    }

    /**
     * Test method for {@link DefaultAttributeResolver
     * #computeAttribute(org.apache.tiles.TilesContainer, org.apache.tiles.Attribute,
     * java.lang.String, java.lang.String, boolean, java.lang.Object, java.lang.String,
     * java.lang.String, Request)}.
     */
    @Test
    public void testComputeAttributeInCall() {
        TilesContainer container = createMock(TilesContainer.class);
        Request request = createMock(Request.class);
        Attribute attribute = new Attribute("myValue", Expression.createExpression("myExpression", null), "myRole", "myRenderer");

        replay(container, request);
        assertEquals(attribute, resolver.computeAttribute(container, attribute, null, null, false, null, null, null, request));
        verify(container, request);
    }

    /**
     * Test method for {@link DefaultAttributeResolver
     * #computeAttribute(org.apache.tiles.TilesContainer, org.apache.tiles.Attribute,
     * java.lang.String, java.lang.String, boolean, java.lang.Object, java.lang.String,
     * java.lang.String, Request)}.
     */
    @Test
    public void testComputeAttributeDefault() {
        TilesContainer container = createMock(TilesContainer.class);
        Request request = createMock(Request.class);
        AttributeContext attributeContext = createMock(AttributeContext.class);

        expect(container.getAttributeContext(request)).andReturn(attributeContext);
        expect(attributeContext.getAttribute("myName")).andReturn(null);

        replay(container, attributeContext, request);
        Attribute attribute = resolver.computeAttribute(container, null, "myName", null, false, "defaultValue", "defaultRole", "defaultType", request);
        assertEquals("defaultValue", attribute.getValue());
        assertEquals("defaultRole", attribute.getRole());
        assertEquals("defaultType", attribute.getRenderer());
        verify(container, attributeContext, request);
    }

    /**
     * Test method for {@link DefaultAttributeResolver
     * #computeAttribute(org.apache.tiles.TilesContainer, org.apache.tiles.Attribute,
     * java.lang.String, java.lang.String, boolean, java.lang.Object, java.lang.String,
     * java.lang.String, Request)}.
     */
    @Test(expected = NoSuchAttributeException.class)
    public void testComputeAttributeException() {
        TilesContainer container = createMock(TilesContainer.class);
        AttributeContext attributeContext = createMock(AttributeContext.class);
        Request request = createMock(Request.class);

        expect(container.getAttributeContext(request)).andReturn(attributeContext);
        expect(attributeContext.getAttribute("myName")).andReturn(null);

        replay(container, attributeContext, request);
        resolver.computeAttribute(container, null, "myName", null, false, null, "defaultRole", "defaultType", request);
        verify(container, attributeContext, request);
    }

    /**
     * Test method for {@link DefaultAttributeResolver
     * #computeAttribute(org.apache.tiles.TilesContainer, org.apache.tiles.Attribute,
     * java.lang.String, java.lang.String, boolean, java.lang.Object, java.lang.String,
     * java.lang.String, Request)}.
     */
    @Test
    public void testComputeAttributeIgnore() {
        TilesContainer container = createMock(TilesContainer.class);
        AttributeContext attributeContext = createMock(AttributeContext.class);
        Request request = createMock(Request.class);

        expect(container.getAttributeContext(request)).andReturn(attributeContext);
        expect(attributeContext.getAttribute("myName")).andReturn(null);

        replay(container, attributeContext, request);
        assertNull(resolver.computeAttribute(container, null, "myName", null, true, null, "defaultRole", "defaultType", request));
        verify(container, attributeContext, request);
    }
}
