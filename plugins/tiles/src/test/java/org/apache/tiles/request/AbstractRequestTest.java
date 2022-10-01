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

import java.util.HashMap;
import java.util.Map;

import static org.easymock.EasyMock.createMockBuilder;
import static org.easymock.EasyMock.expect;
import static org.easymock.EasyMock.replay;
import static org.easymock.EasyMock.verify;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

/**
 * Tests {@link AbstractRequest}.
 */
public class AbstractRequestTest {

    /**
     * Test method for {@link AbstractRequest#setForceInclude(boolean)}.
     */
    @Test
    public void testSetForceInclude() {
        AbstractRequest request = createMockBuilder(AbstractRequest.class).createMock();
        Map<String, Object> scope = new HashMap<>();

        expect(request.getContext(Request.REQUEST_SCOPE)).andReturn(scope).anyTimes();

        replay(request);
        assertFalse(request.isForceInclude());
        request.setForceInclude(true);
        assertTrue(request.isForceInclude());
        verify(request);
    }
}
