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

package org.apache.tiles.ognl;

import ognl.PropertyAccessor;
import org.apache.tiles.request.ApplicationContext;
import org.apache.tiles.request.Request;
import org.junit.Test;

import static org.easymock.EasyMock.createMock;
import static org.easymock.EasyMock.replay;
import static org.easymock.EasyMock.verify;
import static org.junit.Assert.assertEquals;

/**
 * Tests {@link TilesContextPropertyAccessorDelegateFactory}.
 */
public class TilesContextPropertyAccessorDelegateFactoryTest {

    /**
     * Test method for
     * {@link TilesContextPropertyAccessorDelegateFactory#getPropertyAccessor(String, Request)}
     * .
     */
    @Test
    public void testGetPropertyAccessorRequest() {
        PropertyAccessor objectPropertyAccessor = createMock(PropertyAccessor.class);
        PropertyAccessor applicationContextPropertyAccessor = createMock(PropertyAccessor.class);
        PropertyAccessor requestScopePropertyAccessor = createMock(PropertyAccessor.class);
        PropertyAccessor sessionScopePropertyAccessor = createMock(PropertyAccessor.class);
        PropertyAccessor applicationScopePropertyAccessor = createMock(PropertyAccessor.class);
        Request request = createMock(Request.class);

        replay(objectPropertyAccessor, applicationContextPropertyAccessor, requestScopePropertyAccessor,
            sessionScopePropertyAccessor, applicationScopePropertyAccessor, request);
        PropertyAccessorDelegateFactory<Request> factory = new TilesContextPropertyAccessorDelegateFactory(
            objectPropertyAccessor, applicationContextPropertyAccessor,
            requestScopePropertyAccessor, sessionScopePropertyAccessor);
        assertEquals(objectPropertyAccessor, factory.getPropertyAccessor("writer", request));

        verify(objectPropertyAccessor, applicationContextPropertyAccessor, requestScopePropertyAccessor,
            sessionScopePropertyAccessor, applicationScopePropertyAccessor, request);
    }

    /**
     * Test method for
     * {@link TilesContextPropertyAccessorDelegateFactory#getPropertyAccessor(String, Request)}
     * .
     */
    @Test
    public void testGetPropertyAccessorApplication() {
        PropertyAccessor objectPropertyAccessor = createMock(PropertyAccessor.class);
        PropertyAccessor applicationContextPropertyAccessor = createMock(PropertyAccessor.class);
        PropertyAccessor requestScopePropertyAccessor = createMock(PropertyAccessor.class);
        PropertyAccessor sessionScopePropertyAccessor = createMock(PropertyAccessor.class);
        PropertyAccessor applicationScopePropertyAccessor = createMock(PropertyAccessor.class);
        Request request = createMock(Request.class);

        replay(objectPropertyAccessor, applicationContextPropertyAccessor, requestScopePropertyAccessor,
            sessionScopePropertyAccessor, applicationScopePropertyAccessor, request);
        PropertyAccessorDelegateFactory<Request> factory = new TilesContextPropertyAccessorDelegateFactory(
            objectPropertyAccessor, applicationContextPropertyAccessor,
            requestScopePropertyAccessor, sessionScopePropertyAccessor);
        assertEquals(applicationContextPropertyAccessor, factory.getPropertyAccessor("initParams", request));

        verify(objectPropertyAccessor, applicationContextPropertyAccessor, requestScopePropertyAccessor,
            sessionScopePropertyAccessor, applicationScopePropertyAccessor, request);
    }

    /**
     * Test method for
     * {@link TilesContextPropertyAccessorDelegateFactory#getPropertyAccessor(String, Request)}
     * .
     */
    @Test
    public void testGetPropertyAccessorRequestScope() {
        PropertyAccessor objectPropertyAccessor = createMock(PropertyAccessor.class);
        PropertyAccessor applicationContextPropertyAccessor = createMock(PropertyAccessor.class);
        PropertyAccessor requestScopePropertyAccessor = createMock(PropertyAccessor.class);
        PropertyAccessor sessionScopePropertyAccessor = createMock(PropertyAccessor.class);
        PropertyAccessor applicationScopePropertyAccessor = createMock(PropertyAccessor.class);
        Request request = createMock(Request.class);

        replay(objectPropertyAccessor, applicationContextPropertyAccessor, requestScopePropertyAccessor,
            sessionScopePropertyAccessor, applicationScopePropertyAccessor, request);
        PropertyAccessorDelegateFactory<Request> factory = new TilesContextPropertyAccessorDelegateFactory(
            objectPropertyAccessor, applicationContextPropertyAccessor,
            requestScopePropertyAccessor, sessionScopePropertyAccessor);
        assertEquals(requestScopePropertyAccessor, factory.getPropertyAccessor("attribute", request));

        verify(objectPropertyAccessor, applicationContextPropertyAccessor, requestScopePropertyAccessor,
            sessionScopePropertyAccessor, applicationScopePropertyAccessor, request);
    }

    /**
     * Test method for
     * {@link TilesContextPropertyAccessorDelegateFactory#getPropertyAccessor(String, Request)}
     * .
     */
    @Test
    public void testGetPropertyAccessorSessionScope() {
        PropertyAccessor objectPropertyAccessor = createMock(PropertyAccessor.class);
        PropertyAccessor applicationContextPropertyAccessor = createMock(PropertyAccessor.class);
        PropertyAccessor requestScopePropertyAccessor = createMock(PropertyAccessor.class);
        PropertyAccessor sessionScopePropertyAccessor = createMock(PropertyAccessor.class);
        PropertyAccessor applicationScopePropertyAccessor = createMock(PropertyAccessor.class);
        Request request = createMock(Request.class);

        replay(objectPropertyAccessor, applicationContextPropertyAccessor, requestScopePropertyAccessor,
            sessionScopePropertyAccessor, applicationScopePropertyAccessor, request);
        PropertyAccessorDelegateFactory<Request> factory = new TilesContextPropertyAccessorDelegateFactory(
            objectPropertyAccessor, applicationContextPropertyAccessor,
            requestScopePropertyAccessor, sessionScopePropertyAccessor);
        assertEquals(requestScopePropertyAccessor, factory.getPropertyAccessor("attribute", request));

        verify(objectPropertyAccessor, applicationContextPropertyAccessor, requestScopePropertyAccessor,
            sessionScopePropertyAccessor, applicationScopePropertyAccessor, request);
    }

    /**
     * Test method for
     * {@link TilesContextPropertyAccessorDelegateFactory#getPropertyAccessor(String, Request)}
     * .
     */
    @Test
    public void testGetPropertyAccessorApplicationScope() {
        PropertyAccessor objectPropertyAccessor = createMock(PropertyAccessor.class);
        PropertyAccessor applicationContextPropertyAccessor = createMock(PropertyAccessor.class);
        PropertyAccessor requestScopePropertyAccessor = createMock(PropertyAccessor.class);
        PropertyAccessor sessionScopePropertyAccessor = createMock(PropertyAccessor.class);
        PropertyAccessor applicationScopePropertyAccessor = createMock(PropertyAccessor.class);
        Request request = createMock(Request.class);
        ApplicationContext applicationContext = createMock(ApplicationContext.class);

        replay(objectPropertyAccessor, applicationContextPropertyAccessor, requestScopePropertyAccessor,
            sessionScopePropertyAccessor, applicationScopePropertyAccessor, request, applicationContext);
        PropertyAccessorDelegateFactory<Request> factory = new TilesContextPropertyAccessorDelegateFactory(
            objectPropertyAccessor, applicationContextPropertyAccessor,
            requestScopePropertyAccessor, sessionScopePropertyAccessor);
        assertEquals(requestScopePropertyAccessor, factory.getPropertyAccessor("attribute", request));

        verify(objectPropertyAccessor, applicationContextPropertyAccessor, requestScopePropertyAccessor,
            sessionScopePropertyAccessor, applicationScopePropertyAccessor, request, applicationContext);
    }

    /**
     * Test method for
     * {@link TilesContextPropertyAccessorDelegateFactory#getPropertyAccessor(String, Request)}
     * .
     */
    @Test
    public void testGetPropertyAccessorRequestScopeDefault() {
        PropertyAccessor objectPropertyAccessor = createMock(PropertyAccessor.class);
        PropertyAccessor applicationContextPropertyAccessor = createMock(PropertyAccessor.class);
        PropertyAccessor requestScopePropertyAccessor = createMock(PropertyAccessor.class);
        PropertyAccessor sessionScopePropertyAccessor = createMock(PropertyAccessor.class);
        PropertyAccessor applicationScopePropertyAccessor = createMock(PropertyAccessor.class);
        Request request = createMock(Request.class);
        ApplicationContext applicationContext = createMock(ApplicationContext.class);

        replay(objectPropertyAccessor, applicationContextPropertyAccessor, requestScopePropertyAccessor,
            sessionScopePropertyAccessor, applicationScopePropertyAccessor, request, applicationContext);
        PropertyAccessorDelegateFactory<Request> factory = new TilesContextPropertyAccessorDelegateFactory(
            objectPropertyAccessor, applicationContextPropertyAccessor,
            requestScopePropertyAccessor, sessionScopePropertyAccessor);
        assertEquals(requestScopePropertyAccessor, factory.getPropertyAccessor("attribute", request));

        verify(objectPropertyAccessor, applicationContextPropertyAccessor, requestScopePropertyAccessor,
            sessionScopePropertyAccessor, applicationScopePropertyAccessor, request, applicationContext);
    }

}
