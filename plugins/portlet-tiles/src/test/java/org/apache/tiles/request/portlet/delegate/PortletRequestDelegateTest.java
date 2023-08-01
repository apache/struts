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
package org.apache.tiles.request.portlet.delegate;

import org.apache.tiles.request.collection.ReadOnlyEnumerationMap;
import org.junit.Before;
import org.junit.Test;

import javax.portlet.PortletRequest;
import java.util.Map;

import static org.easymock.EasyMock.createMock;
import static org.easymock.EasyMock.expect;
import static org.easymock.EasyMock.replay;
import static org.easymock.EasyMock.verify;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

/**
 * Tests {@link PortletRequestDelegate}.
 */
public class PortletRequestDelegateTest {

    /**
     * The request.
     */
    private PortletRequest request;

    /**
     * The delegate to test.
     */
    private PortletRequestDelegate delegate;

    /**
     * Sets up the test.
     */
    @Before
    public void setUp() {
        request = createMock(PortletRequest.class);
        delegate = new PortletRequestDelegate(request);
    }

    /**
     * Test method for {@link PortletRequestDelegate#getParam()}.
     */
    @Test
    public void testGetParam() {
        replay(request);
        assertTrue(delegate.getParam() instanceof ReadOnlyEnumerationMap);
        verify(request);
    }

    /**
     * Test method for {@link PortletRequestDelegate#getParamValues()}.
     */
    @Test
    public void testGetParamValues() {
        Map<String, String[]> params = createMock(Map.class);

        expect(request.getParameterMap()).andReturn(params);

        replay(request, params);
        assertEquals(params, delegate.getParamValues());
        verify(request, params);
    }

}
