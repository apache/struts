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

import org.apache.tiles.request.portlet.extractor.InitParameterExtractor;
import org.junit.Before;
import org.junit.Test;

import javax.portlet.PortletContext;
import java.util.Enumeration;

import static org.easymock.EasyMock.*;
import static org.easymock.EasyMock.*;
import static org.junit.Assert.assertEquals;

/**
 * Tests {@link InitParameterExtractor}.
 */
public class InitParameterExtractorTest {

    /**
     * The portlet context.
     */
    private PortletContext context;

    /**
     * The extractor to test.
     */
    private InitParameterExtractor extractor;

    /**
     * Sets up the test.
     */
    @Before
    public void setUp() {
        context = createMock(PortletContext.class);
        extractor = new InitParameterExtractor(context);
    }

    /**
     * Test method for {@link InitParameterExtractor#getKeys()}.
     */
    @SuppressWarnings("unchecked")
    @Test
    public void testGetKeys() {
        Enumeration<String> keys = createMock(Enumeration.class);

        expect(context.getInitParameterNames()).andReturn(keys);

        replay(context, keys);
        assertEquals(keys, extractor.getKeys());
        verify(context, keys);
    }

    /**
     * Test method for {@link InitParameterExtractor#getValue(String)}.
     */
    @Test
    public void testGetValue() {
        expect(context.getInitParameter("name")).andReturn("value");

        replay(context);
        assertEquals("value", extractor.getValue("name"));
        verify(context);
    }

}
