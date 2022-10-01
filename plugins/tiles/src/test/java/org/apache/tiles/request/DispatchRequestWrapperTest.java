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

import org.junit.Test;

import java.io.IOException;

import static org.easymock.EasyMock.createMock;
import static org.easymock.EasyMock.replay;
import static org.easymock.EasyMock.verify;

/**
 * Tests {@link DispatchRequestWrapper}.
 */
public class DispatchRequestWrapperTest {

    protected DispatchRequest createMockRequest() {
        return createMock(DispatchRequest.class);
    }

    protected DispatchRequestWrapper createRequestWrapper(Request wrappedRequest) {
        return new DispatchRequestWrapper((DispatchRequest) wrappedRequest);
    }

    /**
     * Test method for {@link DispatchRequestWrapper#dispatch(String)}.
     *
     * @throws IOException If something goes wrong.
     */
    @Test
    public void testDispatch() throws IOException {
        DispatchRequest wrappedRequest = createMockRequest();

        wrappedRequest.dispatch("/my/path.html");

        replay(wrappedRequest);
        DispatchRequestWrapper request = createRequestWrapper(wrappedRequest);
        request.dispatch("/my/path.html");
        verify(wrappedRequest);
    }

    /**
     * Test method for {@link DispatchRequestWrapper#include(String)}.
     *
     * @throws IOException If something goes wrong.
     */
    @Test
    public void testInclude() throws IOException {
        DispatchRequest wrappedRequest = createMockRequest();

        wrappedRequest.include("/my/path.html");

        replay(wrappedRequest);
        DispatchRequestWrapper request = createRequestWrapper(wrappedRequest);
        request.include("/my/path.html");
        verify(wrappedRequest);
    }

    /**
     * Test method for {@link DispatchRequestWrapper#setContentType(String)}.
     */
    @Test
    public void testSetContentType() {
        DispatchRequest wrappedRequest = createMockRequest();

        wrappedRequest.setContentType("text/html");

        replay(wrappedRequest);
        DispatchRequestWrapper request = createRequestWrapper(wrappedRequest);
        request.setContentType("text/html");
        verify(wrappedRequest);
    }
}
