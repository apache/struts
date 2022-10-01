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
package org.apache.tiles.request.portlet.extractor;

import org.apache.tiles.request.portlet.extractor.HeaderExtractor;
import org.junit.Before;
import org.junit.Test;

import javax.portlet.PortletRequest;
import javax.portlet.PortletResponse;
import java.util.Enumeration;

import static org.easymock.EasyMock.*;
import static org.easymock.EasyMock.*;
import static org.junit.Assert.assertEquals;

/**
 * Tests {@link HeaderExtractor}.
 */
public class HeaderExtractorTest {

    /**
     * The request.
     */
    private PortletRequest request;

    /**
     * The response.
     */
    private PortletResponse response;

    /**
     * The extractor to test.
     */
    private HeaderExtractor extractor;

    /**
     * Sets up the test.
     */
    @Before
    public void setUp() {
        request = createMock(PortletRequest.class);
        response = createMock(PortletResponse.class);
        extractor = new HeaderExtractor(request, response);
    }

    /**
     * Test method for {@link HeaderExtractor#getKeys()}.
     */
    @SuppressWarnings("unchecked")
    @Test
    public void testGetKeys() {
        Enumeration<String> keys = createMock(Enumeration.class);

        expect(request.getPropertyNames()).andReturn(keys);

        replay(request, response, keys);
        assertEquals(keys, extractor.getKeys());
        verify(request, response, keys);
    }

    /**
     * Test method for {@link HeaderExtractor#getValue(String)}.
     */
    @Test
    public void testGetValue() {
        expect(request.getProperty("name")).andReturn("value");

        replay(request, response);
        assertEquals("value", extractor.getValue("name"));
        verify(request, response);
    }

    /**
     * Test method for {@link HeaderExtractor#getValues(String)}.
     */
    @SuppressWarnings("unchecked")
    @Test
    public void testGetValues() {
        Enumeration<String> keys = createMock(Enumeration.class);

        expect(request.getProperties("name")).andReturn(keys);

        replay(request, response, keys);
        assertEquals(keys, extractor.getValues("name"));
        verify(request, response, keys);
    }

    /**
     * Test method for {@link HeaderExtractor#setValue(String, String)}.
     */
    @Test
    public void testSetValue() {
        response.setProperty("name", "value");

        replay(request, response);
        extractor.setValue("name", "value");
        verify(request, response);
    }

}
