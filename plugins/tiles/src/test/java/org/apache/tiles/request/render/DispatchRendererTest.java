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

import org.apache.tiles.request.DispatchRequest;
import org.apache.tiles.request.Request;
import org.junit.Before;
import org.junit.Test;

import java.io.IOException;

import static org.easymock.EasyMock.createMock;
import static org.easymock.EasyMock.replay;
import static org.easymock.EasyMock.verify;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

/**
 * Tests {@link DispatchRenderer}.
 */
public class DispatchRendererTest {

    /**
     * The renderer.
     */
    private DispatchRenderer renderer;

    @Before
    public void setUp() {
        renderer = new DispatchRenderer();
    }

    @Test
    public void testWrite() throws IOException {
        DispatchRequest requestContext = createMock(DispatchRequest.class);
        requestContext.dispatch("/myTemplate.jsp");
        replay(requestContext);
        renderer.render("/myTemplate.jsp", requestContext);
        verify(requestContext);
    }

    @Test(expected = CannotRenderException.class)
    public void testWriteNull() throws IOException {
        DispatchRequest requestContext = createMock(DispatchRequest.class);
        replay(requestContext);
        renderer.render(null, requestContext);
        verify(requestContext);
    }

    @Test
    public void testIsRenderable() {
        Request requestContext = createMock(DispatchRequest.class);
        replay(requestContext);
        assertTrue(renderer.isRenderable("/myTemplate.jsp", requestContext));
        assertFalse(renderer.isRenderable(null, requestContext));
        verify(requestContext);
    }
}
