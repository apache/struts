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
package org.apache.tiles.request;

import org.junit.Before;
import org.junit.Test;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

import static org.easymock.EasyMock.createMock;
import static org.easymock.EasyMock.createMockBuilder;
import static org.easymock.EasyMock.expect;
import static org.easymock.EasyMock.replay;
import static org.easymock.EasyMock.verify;
import static org.junit.Assert.assertTrue;

/**
 * Tests {@link AbstractViewRequest}.
 */
public class AbstractViewRequestTest {

    /**
     * The request to test.
     */
    private AbstractViewRequest request;

    /**
     * The internal request.
     */
    private DispatchRequest wrappedRequest;

    /**
     * The application context.
     */
    private ApplicationContext applicationContext;

    /**
     * Sets up the test.
     */
    @Before
    public void setUp() {
        wrappedRequest = createMock(DispatchRequest.class);
        request = createMockBuilder(AbstractViewRequest.class).withConstructor(wrappedRequest).createMock();
        applicationContext = createMock(ApplicationContext.class);
        Map<String, Object> applicationScope = new HashMap<>();

        expect(wrappedRequest.getApplicationContext()).andReturn(applicationContext).anyTimes();
        expect(applicationContext.getApplicationScope()).andReturn(applicationScope).anyTimes();
    }

    /**
     * Test method for {@link AbstractViewRequest#dispatch(String)}.
     *
     * @throws IOException If something goes wrong.
     */
    @Test
    public void testDispatch() throws IOException {
        Map<String, Object> requestScope = new HashMap<>();

        expect(request.getContext(Request.REQUEST_SCOPE)).andReturn(requestScope);
        wrappedRequest.include("/my/path.html");

        replay(wrappedRequest, request, applicationContext);
        request.dispatch("/my/path.html");
        assertTrue((Boolean) requestScope.get(AbstractRequest.FORCE_INCLUDE_ATTRIBUTE_NAME));
        verify(wrappedRequest, request, applicationContext);
    }

    /**
     * Test method for {@link AbstractViewRequest#include(String)}.
     *
     * @throws IOException If something goes wrong.
     */
    @Test
    public void testInclude() throws IOException {
        Map<String, Object> requestScope = new HashMap<>();

        expect(request.getContext(Request.REQUEST_SCOPE)).andReturn(requestScope);
        wrappedRequest.include("/my/path.html");

        replay(wrappedRequest, request, applicationContext);
        request.include("/my/path.html");
        assertTrue((Boolean) requestScope.get(AbstractRequest.FORCE_INCLUDE_ATTRIBUTE_NAME));
        verify(wrappedRequest, request, applicationContext);
    }

    /**
     * Test method for {@link AbstractViewRequest#doInclude(String)}.
     *
     * @throws IOException If something goes wrong.
     */
    @Test
    public void testDoInclude() throws IOException {
        wrappedRequest.include("/my/path.html");

        replay(wrappedRequest, request, applicationContext);
        request.doInclude("/my/path.html");
        verify(wrappedRequest, request, applicationContext);
    }

}
