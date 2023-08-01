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
package org.apache.tiles.request.render;

import org.apache.tiles.request.Request;
import org.easymock.EasyMock;
import org.junit.Before;
import org.junit.Test;

import java.io.IOException;
import java.io.StringWriter;

import static org.easymock.EasyMock.createMock;
import static org.easymock.EasyMock.expect;
import static org.easymock.EasyMock.replay;
import static org.easymock.EasyMock.verify;

/**
 * Tests {@link ChainedDelegateRenderer}.
 */
public class ChainedDelegateRendererTest {

    /**
     * The renderer.
     */
    private ChainedDelegateRenderer renderer;

    /**
     * A mock string attribute renderer.
     */
    private Renderer stringRenderer;

    /**
     * A mock template attribute renderer.
     */
    private Renderer templateRenderer;

    /**
     * A mock definition attribute renderer.
     */
    private Renderer definitionRenderer;

    /**
     * Sets up the test.
     */
    @Before
    public void setUp() {
        stringRenderer = createMock(Renderer.class);
        templateRenderer = createMock(Renderer.class);
        definitionRenderer = createMock(Renderer.class);
        renderer = new ChainedDelegateRenderer();
        renderer.addAttributeRenderer(definitionRenderer);
        renderer.addAttributeRenderer(templateRenderer);
        renderer.addAttributeRenderer(stringRenderer);
    }

    /**
     * Tests
     * {@link ChainedDelegateRenderer#render(String, Request)}
     * writing a definition.
     *
     * @throws IOException If something goes wrong during rendition.
     */
    @Test
    public void testWriteDefinition() throws IOException {
        Request requestContext = EasyMock.createMock(Request.class);

        expect(definitionRenderer.isRenderable("my.definition", requestContext)).andReturn(Boolean.TRUE);
        definitionRenderer.render("my.definition", requestContext);

        replay(requestContext, stringRenderer, templateRenderer, definitionRenderer);
        renderer.render("my.definition", requestContext);
        verify(requestContext, stringRenderer, templateRenderer, definitionRenderer);
    }

    /**
     * Tests
     * {@link ChainedDelegateRenderer#render(String, Request)}
     * writing a definition.
     *
     * @throws IOException If something goes wrong during rendition.
     */
    @Test(expected = NullPointerException.class)
    public void testWriteNull() throws IOException {
        StringWriter writer = new StringWriter();
        Request requestContext = EasyMock.createMock(Request.class);

        replay(requestContext, stringRenderer, templateRenderer, definitionRenderer);
        try {
            renderer.render(null, requestContext);
        } finally {
            writer.close();
            verify(requestContext, stringRenderer, templateRenderer, definitionRenderer);
        }
    }

    /**
     * Tests
     * {@link ChainedDelegateRenderer#render(String, Request)}
     * writing a definition.
     *
     * @throws IOException If something goes wrong during rendition.
     */
    @Test(expected = CannotRenderException.class)
    public void testWriteNotRenderable() throws IOException {
        StringWriter writer = new StringWriter();
        Request requestContext = EasyMock.createMock(Request.class);

        expect(definitionRenderer.isRenderable("Result", requestContext)).andReturn(Boolean.FALSE);
        expect(templateRenderer.isRenderable("Result", requestContext)).andReturn(Boolean.FALSE);
        expect(stringRenderer.isRenderable("Result", requestContext)).andReturn(Boolean.FALSE);

        replay(requestContext, stringRenderer, templateRenderer, definitionRenderer);
        try {
            renderer.render("Result", requestContext);
        } finally {
            writer.close();
            verify(requestContext, stringRenderer, templateRenderer, definitionRenderer);
        }
    }

    /**
     * Tests
     * {@link ChainedDelegateRenderer#render(String, Request)}
     * writing a string.
     *
     * @throws IOException If something goes wrong during rendition.
     */
    @Test
    public void testWriteString() throws IOException {
        Request requestContext = EasyMock.createMock(Request.class);
        expect(definitionRenderer.isRenderable("Result", requestContext)).andReturn(Boolean.FALSE);
        expect(templateRenderer.isRenderable("Result", requestContext)).andReturn(Boolean.FALSE);
        expect(stringRenderer.isRenderable("Result", requestContext)).andReturn(Boolean.TRUE);
        stringRenderer.render("Result", requestContext);

        replay(requestContext, stringRenderer, templateRenderer, definitionRenderer);
        renderer.render("Result", requestContext);
        verify(requestContext, stringRenderer, templateRenderer, definitionRenderer);
    }

    /**
     * Tests
     * {@link ChainedDelegateRenderer#render(String, Request)}
     * writing a template.
     *
     * @throws IOException If something goes wrong during rendition.
     */
    @Test
    public void testWriteTemplate() throws IOException {
        StringWriter writer = new StringWriter();
        Request requestContext = EasyMock.createMock(Request.class);
        templateRenderer.render("/myTemplate.jsp", requestContext);
        expect(definitionRenderer.isRenderable("/myTemplate.jsp", requestContext)).andReturn(Boolean.FALSE);
        expect(templateRenderer.isRenderable("/myTemplate.jsp", requestContext)).andReturn(Boolean.TRUE);

        replay(requestContext, stringRenderer, templateRenderer, definitionRenderer);
        renderer.render("/myTemplate.jsp", requestContext);
        writer.close();
        verify(requestContext, stringRenderer, templateRenderer, definitionRenderer);
    }
}
