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

import org.apache.tiles.request.portlet.extractor.SessionScopeExtractor;
import org.junit.Before;
import org.junit.Test;

import javax.portlet.PortletRequest;
import javax.portlet.PortletSession;
import java.util.Enumeration;

import static org.easymock.EasyMock.*;
import static org.easymock.EasyMock.*;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNull;

/**
 * Tests {@link SessionScopeExtractor}.
 */
public class SessionScopeExtractorTest {

    /**
     * The request.
     */
    private PortletRequest request;

    /**
     * The session.
     */
    private PortletSession session;

    /**
     * The scope to test.
     */
    private SessionScopeExtractor extractor;

    /**
     * Sets up the test.
     */
    @Before
    public void setUp() {
        request = createMock(PortletRequest.class);
        session = createMock(PortletSession.class);
        extractor = new SessionScopeExtractor(request, PortletSession.PORTLET_SCOPE);
    }


    /**
     * Tests {@link SessionScopeExtractor#SessionScopeExtractor(PortletRequest, int)}.
     */
    @Test(expected = IllegalArgumentException.class)
    public void testIllegalScope() {
        replay(request, session);
        new SessionScopeExtractor(request, 0);
        verify(request, session);
    }

    /**
     * Test method for {@link SessionScopeExtractor#setValue(String, Object)}.
     */
    @Test
    public void testSetValue() {
        expect(request.getPortletSession()).andReturn(session);
        session.setAttribute("name", "value", PortletSession.PORTLET_SCOPE);

        replay(request, session);
        extractor.setValue("name", "value");
        verify(request, session);
    }

    /**
     * Test method for {@link SessionScopeExtractor#removeValue(String)}.
     */
    @Test
    public void testRemoveValue() {
        expect(request.getPortletSession(false)).andReturn(session);
        session.removeAttribute("name", PortletSession.PORTLET_SCOPE);

        replay(request, session);
        extractor.removeValue("name");
        verify(request, session);
    }

    /**
     * Test method for {@link SessionScopeExtractor#getKeys()}.
     */
    @SuppressWarnings("unchecked")
    @Test
    public void testGetKeys() {
        Enumeration<String> keys = createMock(Enumeration.class);

        expect(request.getPortletSession(false)).andReturn(session);
        expect(session.getAttributeNames(PortletSession.PORTLET_SCOPE)).andReturn(keys);

        replay(request, session, keys);
        assertEquals(keys, extractor.getKeys());
        verify(request, session, keys);
    }

    /**
     * Test method for {@link SessionScopeExtractor#getKeys()}.
     */
    @Test
    public void testGetKeysNoSession() {
        expect(request.getPortletSession(false)).andReturn(null);

        replay(request, session);
        assertNull(extractor.getKeys());
        verify(request, session);
    }

    /**
     * Test method for {@link SessionScopeExtractor#getValue(String)}.
     */
    @Test
    public void testGetValue() {
        expect(request.getPortletSession(false)).andReturn(session);
        expect(session.getAttribute("name", PortletSession.PORTLET_SCOPE)).andReturn("value");

        replay(request, session);
        assertEquals("value", extractor.getValue("name"));
        verify(request, session);
    }

    /**
     * Test method for {@link SessionScopeExtractor#getValue(String)}.
     */
    @Test
    public void testGetValueNoSession() {
        expect(request.getPortletSession(false)).andReturn(null);

        replay(request, session);
        assertNull(extractor.getValue("name"));
        verify(request, session);
    }

}
