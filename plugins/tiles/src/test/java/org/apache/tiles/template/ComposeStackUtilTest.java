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

import org.apache.tiles.request.Request;
import org.junit.Test;

import java.util.ArrayDeque;
import java.util.Date;
import java.util.Deque;
import java.util.HashMap;
import java.util.Map;

import static org.easymock.EasyMock.createMock;
import static org.easymock.EasyMock.expect;
import static org.easymock.EasyMock.replay;
import static org.easymock.EasyMock.verify;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertSame;

/**
 * Tests {@link ComposeStackUtil}.
 */
public class ComposeStackUtilTest {

    /**
     * An integer value.
     */
    private static final int INT_VALUE = 3;

    /**
     * A long value.
     */
    private static final long LONG_VALUE = 2L;

    /**
     * Test method for {@link ComposeStackUtil
     * #findAncestorWithClass(java.util.Stack, java.lang.Class)}.
     */
    @Test
    public void testFindAncestorWithClass() {
        Deque<Object> composeStack = new ArrayDeque<>();
        Integer integerValue = 1;
        Long longValue = LONG_VALUE;
        String stringValue = "my value";
        Integer integerValue2 = INT_VALUE;
        composeStack.push(integerValue);
        composeStack.push(longValue);
        composeStack.push(stringValue);
        composeStack.push(integerValue2);
        assertEquals(integerValue2, ComposeStackUtil.findAncestorWithClass(composeStack, Integer.class));
        assertEquals(longValue, ComposeStackUtil.findAncestorWithClass(composeStack, Long.class));
        assertEquals(stringValue, ComposeStackUtil.findAncestorWithClass(composeStack, String.class));
        assertEquals(integerValue2, ComposeStackUtil.findAncestorWithClass(composeStack, Object.class));
        assertNull(ComposeStackUtil.findAncestorWithClass(composeStack, Date.class));
    }

    /**
     * Tests {@link ComposeStackUtil#getComposeStack(Request)}.
     */
    @Test
    public void testGetComposeStackNull() {
        Request request = createMock(Request.class);

        Map<String, Object> requestScope = new HashMap<>();
        expect(request.getContext("request")).andReturn(requestScope);

        replay(request);
        assertSame(ComposeStackUtil.getComposeStack(request),
            requestScope.get(ComposeStackUtil.COMPOSE_STACK_ATTRIBUTE_NAME));
        verify(request);
    }

    /**
     * Tests {@link ComposeStackUtil#getComposeStack(Request)}.
     */
    @Test
    public void testGetComposeStackNotNull() {
        Request request = createMock(Request.class);
        Deque<Object> composeStack = createMock(Deque.class);

        Map<String, Object> requestScope = new HashMap<>();
        requestScope.put(ComposeStackUtil.COMPOSE_STACK_ATTRIBUTE_NAME, composeStack);
        expect(request.getContext("request")).andReturn(requestScope);

        replay(request, composeStack);
        assertSame(composeStack, ComposeStackUtil.getComposeStack(request));
        verify(request, composeStack);
    }

    /**
     * Tests {@link ComposeStackUtil#getComposeStack(Request)}.
     */
    @Test
    public void testGetComposeStackNoNull() {
        Request request = createMock(Request.class);

        Map<String, Object> requestScope = new HashMap<>();
        expect(request.getContext("request")).andReturn(requestScope);

        replay(request);
        assertSame(ComposeStackUtil.getComposeStack(request),
            requestScope.get(ComposeStackUtil.COMPOSE_STACK_ATTRIBUTE_NAME));
        verify(request);
    }
}
