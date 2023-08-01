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
package org.apache.tiles.core.renderer;

import org.apache.tiles.api.TilesContainer;
import org.apache.tiles.request.Request;
import org.apache.tiles.request.render.CannotRenderException;
import org.junit.Before;
import org.junit.Test;

import java.io.IOException;

import static org.easymock.EasyMock.createMock;
import static org.easymock.EasyMock.expect;
import static org.easymock.EasyMock.replay;
import static org.easymock.EasyMock.verify;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

/**
 * Tests {@link DefinitionRenderer}.
 */
public class DefinitionRendererTest {

    /**
     * The renderer.
     */
    private DefinitionRenderer renderer;

    /**
     * The container.
     */
    private TilesContainer container;

    /**
     * {@inheritDoc}
     */
    @Before
    public void setUp() {
        container = createMock(TilesContainer.class);
        renderer = new DefinitionRenderer(container);
    }

    /**
     * Tests
     * {@link DefinitionRenderer#render(String, Request)}.
     *
     * @throws IOException If something goes wrong during rendition.
     */
    @Test
    public void testWrite() throws IOException {
        Request requestContext = createMock(Request.class);
        container.render("my.definition", requestContext);
        replay(requestContext, container);
        renderer.render("my.definition", requestContext);
        verify(requestContext, container);
    }

    /**
     * Tests
     * {@link DefinitionRenderer#render(String, Request)}.
     *
     * @throws IOException If something goes wrong during rendition.
     */
    @Test(expected = CannotRenderException.class)
    public void testRenderException() throws IOException {
        Request requestContext = createMock(Request.class);
        replay(requestContext, container);
        try {
            renderer.render(null, requestContext);
        } finally {
            verify(requestContext, container);
        }
    }

    /**
     * Tests
     * {@link DefinitionRenderer#isRenderable(String, Request)}
     * .
     */
    @Test
    public void testIsRenderable() {
        Request requestContext = createMock(Request.class);
        expect(container.isValidDefinition("my.definition", requestContext)).andReturn(Boolean.TRUE);
        replay(requestContext, container);
        assertTrue(renderer.isRenderable("my.definition", requestContext));
        assertFalse(renderer.isRenderable(null, requestContext));
        verify(requestContext, container);
    }
}
