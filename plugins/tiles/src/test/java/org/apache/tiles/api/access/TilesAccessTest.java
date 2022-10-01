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
package org.apache.tiles.api.access;

import org.apache.tiles.api.NoSuchContainerException;
import org.apache.tiles.api.TilesContainer;
import org.apache.tiles.request.ApplicationContext;
import org.apache.tiles.request.Request;
import org.junit.Test;

import java.util.HashMap;
import java.util.Map;

import static org.easymock.EasyMock.createMock;
import static org.easymock.EasyMock.expect;
import static org.easymock.EasyMock.replay;
import static org.easymock.EasyMock.verify;
import static org.junit.Assert.assertEquals;

/**
 * Tests {@link TilesAccess}.
 */
public class TilesAccessTest {

    @Test
    public void testSetContainer() {
        ApplicationContext context = createMock(ApplicationContext.class);
        TilesContainer container = createMock(TilesContainer.class);
        Map<String, Object> attribs = new HashMap<>();
        expect(context.getApplicationScope()).andReturn(attribs);
        replay(context, container);
        TilesAccess.setContainer(context, container, null);
        assertEquals(attribs.size(), 1);
        assertEquals(attribs.get(TilesAccess.CONTAINER_ATTRIBUTE), container);
        verify(context, container);
    }

    @Test
    public void testSetContainerWithKey() {
        ApplicationContext context = createMock(ApplicationContext.class);
        TilesContainer container = createMock(TilesContainer.class);
        Map<String, Object> attribs = new HashMap<>();
        expect(context.getApplicationScope()).andReturn(attribs).anyTimes();
        replay(context, container);
        TilesAccess.setContainer(context, container, "myKey");
        assertEquals(1, attribs.size());
        assertEquals(container, attribs.get("myKey"));

        TilesAccess.setContainer(context, null, "myKey");
        assertEquals(0, attribs.size());

        TilesAccess.setContainer(context, container, null);
        assertEquals(1, attribs.size());
        assertEquals(container, attribs.get(TilesAccess.CONTAINER_ATTRIBUTE));
        verify(context, container);
    }

    @Test
    public void testGetContainer() {
        ApplicationContext context = createMock(ApplicationContext.class);
        TilesContainer container = createMock(TilesContainer.class);
        Map<String, Object> attribs = new HashMap<>();
        expect(context.getApplicationScope()).andReturn(attribs).anyTimes();

        replay(context, container);
        attribs.put(TilesAccess.CONTAINER_ATTRIBUTE, container);
        assertEquals(container, TilesAccess.getContainer(context));
        verify(context, container);
    }

    @Test
    public void testGetContainerWithKey() {
        ApplicationContext context = createMock(ApplicationContext.class);
        TilesContainer container = createMock(TilesContainer.class);
        Map<String, Object> attribs = new HashMap<>();
        expect(context.getApplicationScope()).andReturn(attribs).anyTimes();

        replay(context, container);
        attribs.put(TilesAccess.CONTAINER_ATTRIBUTE, container);
        attribs.put("myKey", container);
        assertEquals(container, TilesAccess.getContainer(context, null));
        assertEquals(container, TilesAccess.getContainer(context, "myKey"));
        verify(context, container);
    }

    @Test
    public void testSetCurrentContainer() {
        Request request = createMock(Request.class);
        ApplicationContext context = createMock(ApplicationContext.class);
        TilesContainer container = createMock(TilesContainer.class);
        Map<String, Object> attribs = new HashMap<>();
        attribs.put("myKey", container);
        Map<String, Object> requestScope = new HashMap<>();

        expect(context.getApplicationScope()).andReturn(attribs).anyTimes();
        expect(request.getContext("request")).andReturn(requestScope);
        expect(request.getApplicationContext()).andReturn(context);
        replay(request, context, container);
        TilesAccess.setCurrentContainer(request, "myKey");
        assertEquals(container, requestScope.get(TilesAccess.CURRENT_CONTAINER_ATTRIBUTE_NAME));
        verify(request, context, container);
    }

    @Test(expected = NoSuchContainerException.class)
    public void testSetCurrentContainerException() {
        Request request = createMock(Request.class);
        ApplicationContext context = createMock(ApplicationContext.class);
        Map<String, Object> attribs = new HashMap<>();

        expect(request.getApplicationContext()).andReturn(context);
        expect(context.getApplicationScope()).andReturn(attribs).anyTimes();
        replay(request, context);
        try {
            TilesAccess.setCurrentContainer(request, "myKey");
        } finally {
            verify(request, context);
        }
    }

    @Test
    public void testSetCurrentContainerWithContainer() {
        Request request = createMock(Request.class);
        ApplicationContext context = createMock(ApplicationContext.class);
        TilesContainer container = createMock(TilesContainer.class);
        Map<String, Object> attribs = new HashMap<>();
        attribs.put("myKey", container);
        Map<String, Object> requestScope = new HashMap<>();

        expect(context.getApplicationScope()).andReturn(attribs).anyTimes();
        expect(request.getContext("request")).andReturn(requestScope);

        replay(request, context, container);
        TilesAccess.setCurrentContainer(request, container);
        assertEquals(container, requestScope.get(TilesAccess.CURRENT_CONTAINER_ATTRIBUTE_NAME));
        verify(request, context, container);
    }

    @Test(expected = NullPointerException.class)
    public void testSetCurrentContainerWithContainerException() {
        Request request = createMock(Request.class);
        ApplicationContext context = createMock(ApplicationContext.class);
        Map<String, Object> attribs = new HashMap<>();

        expect(context.getApplicationScope()).andReturn(attribs).anyTimes();

        replay(request, context);
        try {
            TilesAccess.setCurrentContainer(request, (TilesContainer) null);
        } finally {
            verify(request, context);
        }
    }

    @Test
    public void testGetCurrentContainer() {
        Request request = createMock(Request.class);
        ApplicationContext context = createMock(ApplicationContext.class);
        TilesContainer container = createMock(TilesContainer.class);
        Map<String, Object> attribs = new HashMap<>();
        attribs.put("myKey", container);
        Map<String, Object> requestScope = new HashMap<>();
        requestScope.put(TilesAccess.CURRENT_CONTAINER_ATTRIBUTE_NAME, container);

        expect(request.getApplicationContext()).andReturn(context);
        expect(context.getApplicationScope()).andReturn(attribs).anyTimes();
        expect(request.getContext("request")).andReturn(requestScope);

        replay(request, context, container);
        assertEquals(container, TilesAccess.getCurrentContainer(request));
        verify(request, context, container);
    }

    @Test
    public void testGetCurrentContainerDefault() {
        Request request = createMock(Request.class);
        ApplicationContext context = createMock(ApplicationContext.class);
        TilesContainer container = createMock(TilesContainer.class);
        Map<String, Object> attribs = new HashMap<>();
        attribs.put(TilesAccess.CONTAINER_ATTRIBUTE, container);
        Map<String, Object> requestScope = new HashMap<>();

        expect(request.getApplicationContext()).andReturn(context);
        expect(context.getApplicationScope()).andReturn(attribs).anyTimes();
        expect(request.getContext("request")).andReturn(requestScope);

        replay(request, context, container);
        assertEquals(container, TilesAccess.getCurrentContainer(request));
        verify(request, context, container);
    }
}
