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

import org.apache.tiles.request.ApplicationContext;
import org.apache.tiles.request.Request;
import org.junit.Before;
import org.junit.Test;

import java.io.IOException;

import static org.easymock.EasyMock.createMock;
import static org.easymock.EasyMock.expect;
import static org.easymock.EasyMock.replay;
import static org.easymock.EasyMock.verify;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertSame;
import static org.junit.Assert.assertTrue;

/**
 * Tests {@link TilesContainerWrapper}.
 */
public class TilesContainerWrapperTest {

    /**
     * The container.
     */
    private TilesContainer container;

    /**
     * The wrapper to test.
     */
    private TilesContainerWrapper wrapper;

    /**
     * Sets up the test.
     */
    @Before
    public void setUp() {
        container = createMock(TilesContainer.class);
        wrapper = new TilesContainerWrapper(container);
    }

    /**
     * Tests {@link TilesContainerWrapper#TilesContainerWrapper(TilesContainer)}.
     */
    @Test(expected = NullPointerException.class)
    public void testTilesContainerWrapperNPE() {
        new TilesContainerWrapper(null);
    }

    @Test
    public void testEndContext() {
        Request request = createMock(Request.class);

        container.endContext(request);

        replay(container, request);
        wrapper.endContext(request);
        verify(container, request);
    }

    /**
     * Test method for {@link TilesContainerWrapper#evaluate(Attribute, Request)}.
     */
    @Test
    public void testEvaluate() {
        Request request = createMock(Request.class);
        Attribute attribute = createMock(Attribute.class);

        expect(container.evaluate(attribute, request)).andReturn(1);

        replay(container, request, attribute);
        assertEquals(new Integer(1), wrapper.evaluate(attribute, request));
        verify(container, request, attribute);
    }

    @Test
    public void testGetApplicationContext() {
        ApplicationContext applicationContext = createMock(ApplicationContext.class);

        expect(container.getApplicationContext()).andReturn(applicationContext);

        replay(container, applicationContext);
        assertSame(applicationContext, wrapper.getApplicationContext());
        verify(container, applicationContext);
    }

    /**
     * Test method for {@link TilesContainerWrapper#getAttributeContext(Request)}.
     */
    @Test
    public void testGetAttributeContext() {
        Request request = createMock(Request.class);
        AttributeContext attribute = createMock(AttributeContext.class);

        expect(container.getAttributeContext(request)).andReturn(attribute);

        replay(container, request, attribute);
        assertSame(attribute, wrapper.getAttributeContext(request));
        verify(container, request, attribute);
    }

    /**
     * Test method for {@link TilesContainerWrapper#getDefinition(String, Request)}.
     */
    @Test
    public void testGetDefinition() {
        Request request = createMock(Request.class);
        Definition definition = createMock(Definition.class);

        expect(container.getDefinition("definition", request)).andReturn(definition);

        replay(container, request, definition);
        assertSame(definition, wrapper.getDefinition("definition", request));
        verify(container, request, definition);
    }

    /**
     * Test method for {@link TilesContainerWrapper#isValidDefinition(String, Request)}.
     */
    @Test
    public void testIsValidDefinition() {
        Request request = createMock(Request.class);

        expect(container.isValidDefinition("definition", request)).andReturn(true);

        replay(container, request);
        assertTrue(wrapper.isValidDefinition("definition", request));
        verify(container, request);
    }

    /**
     * Test method for {@link TilesContainerWrapper#prepare(String, Request)}.
     */
    @Test
    public void testPrepare() {
        Request request = createMock(Request.class);

        container.prepare("preparer", request);

        replay(container, request);
        wrapper.prepare("preparer", request);
        verify(container, request);
    }

    /**
     * Test method for {@link TilesContainerWrapper#render(String, Request)}.
     */
    @Test
    public void testRenderStringRequest() {
        Request request = createMock(Request.class);

        container.render("definition", request);

        replay(container, request);
        wrapper.render("definition", request);
        verify(container, request);
    }

    /**
     * Test method for {@link TilesContainerWrapper#render(Definition, Request)}.
     */
    @Test
    public void testRenderDefinitionRequest() {
        Request request = createMock(Request.class);
        Definition definition = createMock(Definition.class);

        container.render(definition, request);

        replay(container, request, definition);
        wrapper.render(definition, request);
        verify(container, request, definition);
    }

    /**
     * Test method for {@link TilesContainerWrapper#render(Attribute, Request)}.
     *
     * @throws IOException If something goes wrong.
     */
    @Test
    public void testRenderAttributeRequest() throws IOException {
        Request request = createMock(Request.class);
        Attribute attribute = createMock(Attribute.class);

        container.render(attribute, request);

        replay(container, request, attribute);
        wrapper.render(attribute, request);
        verify(container, request, attribute);
    }

    @Test
    public void testRenderContext() {
        Request request = createMock(Request.class);

        container.renderContext(request);

        replay(container, request);
        wrapper.renderContext(request);
        verify(container, request);
    }

    @Test
    public void testStartContext() {
        Request request = createMock(Request.class);
        AttributeContext attribute = createMock(AttributeContext.class);

        expect(container.startContext(request)).andReturn(attribute);

        replay(container, request, attribute);
        assertSame(attribute, wrapper.startContext(request));
        verify(container, request, attribute);
    }

}
