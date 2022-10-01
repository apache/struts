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

import org.junit.Before;
import org.junit.Test;

import javax.portlet.MimeResponse;
import java.io.IOException;
import java.io.OutputStream;
import java.io.PrintWriter;

import static org.easymock.EasyMock.createMock;
import static org.easymock.EasyMock.expect;
import static org.easymock.EasyMock.replay;
import static org.easymock.EasyMock.verify;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

/**
 * Tests {@link MimeResponseDelegate}.
 */
public class MimeResponseDelegateTest {

    /**
     * The response.
     */
    private MimeResponse response;

    /**
     * The delegate to test.
     */
    private MimeResponseDelegate delegate;

    /**
     * Sets up the test.
     */
    @Before
    public void setUp() {
        response = createMock(MimeResponse.class);
        delegate = new MimeResponseDelegate(response);
    }

    /**
     * Test method for {@link MimeResponseDelegate#getOutputStream()}.
     *
     * @throws IOException If something goes wrong.
     */
    @Test
    public void testGetOutputStream() throws IOException {
        OutputStream os = createMock(OutputStream.class);

        expect(response.getPortletOutputStream()).andReturn(os);

        replay(response, os);
        assertEquals(os, delegate.getOutputStream());
        verify(response, os);
    }

    /**
     * Test method for {@link MimeResponseDelegate#getPrintWriter()}.
     *
     * @throws IOException If something goes wrong.
     */
    @Test
    public void testGetPrintWriter() throws IOException {
        PrintWriter os = createMock(PrintWriter.class);

        expect(response.getWriter()).andReturn(os);

        replay(response, os);
        assertEquals(os, delegate.getPrintWriter());
        verify(response, os);
    }

    /**
     * Test method for {@link MimeResponseDelegate#getWriter()}.
     *
     * @throws IOException If something goes wrong.
     */
    @Test
    public void testGetWriter() throws IOException {
        PrintWriter os = createMock(PrintWriter.class);

        expect(response.getWriter()).andReturn(os);

        replay(response, os);
        assertEquals(os, delegate.getWriter());
        verify(response, os);
    }

    /**
     * Test method for {@link MimeResponseDelegate#isResponseCommitted()}.
     */
    @Test
    public void testIsResponseCommitted() {
        expect(response.isCommitted()).andReturn(true);

        replay(response);
        assertTrue(delegate.isResponseCommitted());
        verify(response);
    }

    /**
     * Test method for {@link MimeResponseDelegate#setContentType(String)}.
     */
    @Test
    public void testSetContentType() {
        response.setContentType("text/html");

        replay(response);
        delegate.setContentType("text/html");
        verify(response);
    }

}
