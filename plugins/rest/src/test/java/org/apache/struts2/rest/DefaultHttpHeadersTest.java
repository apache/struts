/*
 * $Id: Restful2ActionMapper.java 540819 2007-05-23 02:48:36Z mrdon $
 *
 * Licensed to the Apache Software Foundation (ASF) under one
 * or more contributor license agreements.  See the NOTICE file
 * distributed with this work for additional information
 * regarding copyright ownership.  The ASF licenses this file
 * to you under the Apache License, Version 2.0 (the
 * "License"); you may not use this file except in compliance
 * with the License.  You may obtain a copy of the License at
 *
 *  http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing,
 * software distributed under the License is distributed on an
 * "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY
 * KIND, either express or implied.  See the License for the
 * specific language governing permissions and limitations
 * under the License.
 */
package org.apache.struts2.rest;

import junit.framework.TestCase;
import org.springframework.mock.web.MockHttpServletRequest;
import org.springframework.mock.web.MockHttpServletResponse;

import static javax.servlet.http.HttpServletResponse.*;
import java.util.Date;

public class DefaultHttpHeadersTest extends TestCase {
    private MockHttpServletResponse mockResponse;
    private MockHttpServletRequest mockRequest;

    @Override
    public void setUp() {
        mockResponse = new MockHttpServletResponse();
        mockRequest = new MockHttpServletRequest();
    }

    @Override
    public void tearDown() {
        mockRequest = null;
        mockRequest = null;
    }

    public void testApply() {
        Date now = new Date();
        DefaultHttpHeaders headers = new DefaultHttpHeaders()
                .lastModified(now)
                .withStatus(SC_OK)
                .setLocationId("44")
                .withETag("asdf");
        mockRequest.setRequestURI("/foo/bar.xhtml");

        headers.apply(mockRequest, mockResponse, new Object());

        assertEquals(SC_OK, mockResponse.getStatus());
        assertEquals("http://localhost:80/foo/bar/44.xhtml", mockResponse.getHeader("Location"));
        assertEquals("asdf", mockResponse.getHeader("ETag"));
        assertEquals(now.getTime(), mockResponse.getHeader("Last-Modified"));

    }

    public void testApplyNoLocationExtension() {
        DefaultHttpHeaders headers = new DefaultHttpHeaders()
                .setLocationId("44");
        mockRequest.setRequestURI("/foo/bar");

        headers.apply(mockRequest, mockResponse, new Object());
        assertEquals("http://localhost:80/foo/bar/44", mockResponse.getHeader("Location"));

    }

    public void testApplyFullLocation() {
        DefaultHttpHeaders headers = new DefaultHttpHeaders()
                .setLocation("http://localhost/bar/44");
        mockRequest.setRequestURI("/foo/bar");

        headers.apply(mockRequest, mockResponse, new Object());
        assertEquals("http://localhost/bar/44", mockResponse.getHeader("Location"));

    }

    public void testAutoETag() {
        DefaultHttpHeaders headers = new DefaultHttpHeaders();
        headers.apply(mockRequest, mockResponse, new Object() {
            @Override
            public int hashCode() {
                return 123;
            }
        });

        assertEquals("123", mockResponse.getHeader("ETag"));
    }

    public void testNoCache() {
        DefaultHttpHeaders headers = new DefaultHttpHeaders()
                .disableCaching();
        headers.apply(mockRequest, mockResponse, new Object());

        assertEquals("no-cache", mockResponse.getHeader("Cache-Control"));
    }

    public void testConditionalGetForJustETag() {
        DefaultHttpHeaders headers = new DefaultHttpHeaders()
                .withETag("asdf");
        mockRequest.addHeader("If-None-Match", "asdf");
        headers.apply(mockRequest, mockResponse, new Object());

        assertEquals(SC_NOT_MODIFIED, mockResponse.getStatus());
        assertEquals("asdf", mockResponse.getHeader("ETag"));
    }

    public void testConditionalGetForJustETagNotOK() {
        DefaultHttpHeaders headers = new DefaultHttpHeaders()
                .withETag("asdf")
                .withStatus(SC_BAD_REQUEST);
        mockRequest.addHeader("If-None-Match", "asdf");
        headers.apply(mockRequest, mockResponse, new Object());

        assertEquals(SC_BAD_REQUEST, mockResponse.getStatus());
        assertEquals("asdf", mockResponse.getHeader("ETag"));
    }

    public void testConditionalGetForJustLastModified() {
        Date now = new Date();
        DefaultHttpHeaders headers = new DefaultHttpHeaders()
                .lastModified(now);
        mockRequest.addHeader("If-Modified-Since", String.valueOf(now.getTime()));
        headers.apply(mockRequest, mockResponse, new Object());

        assertEquals(SC_NOT_MODIFIED, mockResponse.getStatus());
    }

    public void testConditionalGetForJustLastModifiedDifferent() {
        Date now = new Date();
        DefaultHttpHeaders headers = new DefaultHttpHeaders()
                .lastModified(now);
        mockRequest.addHeader("If-Modified-Since", String.valueOf(new Date(2323L).getTime()));
        headers.apply(mockRequest, mockResponse, new Object());

        assertEquals(SC_OK, mockResponse.getStatus());
    }

    public void testConditionalGetForLastModifiedAndETag() {
        Date now = new Date();
        DefaultHttpHeaders headers = new DefaultHttpHeaders()
                .lastModified(now)
                .withETag("asdf");
        mockRequest.addHeader("If-None-Match", "asdf");
        mockRequest.addHeader("If-Modified-Since", String.valueOf(now.getTime()));
        headers.apply(mockRequest, mockResponse, new Object());

        assertEquals(SC_NOT_MODIFIED, mockResponse.getStatus());
    }

    public void testConditionalGetForLastModifiedAndETagButNoCache() {
        Date now = new Date();
        DefaultHttpHeaders headers = new DefaultHttpHeaders()
                .lastModified(now)
                .withETag("asdf")
                .disableCaching();
        mockRequest.addHeader("If-None-Match", "asdf");
        mockRequest.addHeader("If-Modified-Since", String.valueOf(now.getTime()));
        headers.apply(mockRequest, mockResponse, new Object());

        assertEquals(SC_OK, mockResponse.getStatus());
    }

    public void testConditionalGetForLastModifiedAndETagWithBadETag() {
        Date now = new Date();
        DefaultHttpHeaders headers = new DefaultHttpHeaders()
                .lastModified(now)
                .withETag("fdsa");
        mockRequest.addHeader("If-None-Match", "asdfds");
        mockRequest.addHeader("If-Modified-Since", String.valueOf(now.getTime()));
        headers.apply(mockRequest, mockResponse, new Object());

        assertEquals(SC_OK, mockResponse.getStatus());
    }
}
