/*
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
package org.apache.struts2.interceptor;


import static org.apache.struts2.interceptor.ResourceIsolationPolicy.DEST_EMBED;
import static org.apache.struts2.interceptor.ResourceIsolationPolicy.DEST_OBJECT;
import static org.apache.struts2.interceptor.ResourceIsolationPolicy.DEST_SCRIPT;
import static org.apache.struts2.interceptor.ResourceIsolationPolicy.MODE_NAVIGATE;
import static org.apache.struts2.interceptor.ResourceIsolationPolicy.SEC_FETCH_DEST_HEADER;
import static org.apache.struts2.interceptor.ResourceIsolationPolicy.SEC_FETCH_MODE_HEADER;
import static org.apache.struts2.interceptor.ResourceIsolationPolicy.SEC_FETCH_SITE_HEADER;
import static org.apache.struts2.interceptor.ResourceIsolationPolicy.SEC_FETCH_USER_HEADER;
import static org.apache.struts2.interceptor.ResourceIsolationPolicy.SITE_NONE;
import static org.apache.struts2.interceptor.ResourceIsolationPolicy.SITE_SAME_ORIGIN;
import static org.apache.struts2.interceptor.ResourceIsolationPolicy.SITE_SAME_SITE;
import static org.apache.struts2.interceptor.ResourceIsolationPolicy.VARY_HEADER;
import static org.junit.Assert.assertNotEquals;

import com.opensymphony.xwork2.ActionContext;
import com.opensymphony.xwork2.XWorkTestCase;
import com.opensymphony.xwork2.mock.MockActionInvocation;
import org.apache.struts2.ServletActionContext;
import org.springframework.mock.web.MockHttpServletRequest;
import org.springframework.mock.web.MockHttpServletResponse;

import java.util.Arrays;
import javax.servlet.http.HttpServletResponse;

public class FetchMetadataInterceptorTest extends XWorkTestCase {

    private final FetchMetadataInterceptor interceptor = new FetchMetadataInterceptor();
    private final MockActionInvocation mai = new MockActionInvocation();
    private final MockHttpServletRequest request = new MockHttpServletRequest();
    private final MockHttpServletResponse response = new MockHttpServletResponse();
    private static final String ACCEPT_ENCODING_VALUE = "Accept-Encoding";
    private static final String VARY_HEADER_VALUE = String.format("%s,%s,%s,%s", SEC_FETCH_DEST_HEADER, SEC_FETCH_MODE_HEADER, SEC_FETCH_SITE_HEADER, SEC_FETCH_USER_HEADER);
    private static final String SC_FORBIDDEN = String.valueOf(HttpServletResponse.SC_FORBIDDEN);

    @Override
    protected void setUp() throws Exception {
        super.setUp();
        container.inject(interceptor);
        interceptor.setExemptedPaths("/foo,/bar");
        ServletActionContext.setRequest(request);
        ServletActionContext.setResponse(response);
        ActionContext context = ServletActionContext.getActionContext();
        mai.setInvocationContext(context);
    }

    public void testNoSite() throws Exception {
        request.removeHeader(SEC_FETCH_SITE_HEADER);

        assertNotEquals("Expected interceptor to accept this request", SC_FORBIDDEN, interceptor.intercept(mai));
    }

    public void testValidSite() throws Exception {
        for (String header : Arrays.asList(SITE_SAME_ORIGIN, SITE_SAME_SITE, SITE_NONE)){
            request.addHeader(SEC_FETCH_SITE_HEADER, header);

            assertNotEquals("Expected interceptor to accept this request", SC_FORBIDDEN, interceptor.intercept(mai));
        }

    }

    public void testValidTopLevelNavigation() throws Exception {
        request.addHeader(SEC_FETCH_MODE_HEADER, MODE_NAVIGATE);
        request.addHeader(SEC_FETCH_DEST_HEADER, DEST_SCRIPT);
        request.setMethod("GET");

        assertNotEquals("Expected interceptor to accept this request", SC_FORBIDDEN, interceptor.intercept(mai));
    }

    public void testInvalidTopLevelNavigation() throws Exception {
        for (String header : Arrays.asList(DEST_OBJECT, DEST_EMBED)) {
            request.addHeader(SEC_FETCH_SITE_HEADER, "foo");
            request.addHeader(SEC_FETCH_MODE_HEADER, MODE_NAVIGATE);
            request.addHeader(SEC_FETCH_DEST_HEADER, header);
            request.setMethod("GET");

            assertEquals("Expected interceptor to NOT accept this request", SC_FORBIDDEN, interceptor.intercept(mai));
        }
    }

    public void testPathInExemptedPaths() throws Exception {
        request.addHeader(SEC_FETCH_SITE_HEADER, "foo");
        request.setContextPath("/foo");

        assertNotEquals("Expected interceptor to accept this request", SC_FORBIDDEN, interceptor.intercept(mai));
    }

    public void testPathNotInExemptedPaths() throws Exception {
        request.addHeader(SEC_FETCH_SITE_HEADER, "foo");
        request.setContextPath("/foobar");

        assertEquals("Expected interceptor to NOT accept this request", SC_FORBIDDEN, interceptor.intercept(mai));
    }

    public void testVaryHeaderAcceptedReq() throws Exception {
        request.addHeader(SEC_FETCH_SITE_HEADER, "foo");
        request.setContextPath("/foo");

        interceptor.intercept(mai);

        assertTrue("Expected vary header to be included", response.containsHeader(VARY_HEADER));
        assertEquals("Expected different vary header value", response.getHeader(VARY_HEADER), VARY_HEADER_VALUE);
    }

    public void testVaryHeaderRejectedReq() throws Exception {
        request.addHeader(SEC_FETCH_SITE_HEADER, "foo");

        interceptor.intercept(mai);

        assertTrue("Expected vary header to be included", response.containsHeader(VARY_HEADER));
        assertEquals("Expected different vary header value", response.getHeader(VARY_HEADER), VARY_HEADER_VALUE);
    }

    public void testVaryHeaderReplaced() throws Exception {
        request.addHeader(SEC_FETCH_SITE_HEADER, "foo");
        response.addHeader(VARY_HEADER, ACCEPT_ENCODING_VALUE);  // Simulate Vary header present due to processing before this interceptor.
        assertEquals("Initial vary response header addition failed ?", response.getHeader(VARY_HEADER), ACCEPT_ENCODING_VALUE);

        interceptor.intercept(mai);

        assertTrue("Expected vary header to be included", response.containsHeader(VARY_HEADER));
        assertFalse("Expected original vary header content to be replaced", response.getHeader(VARY_HEADER).contains(ACCEPT_ENCODING_VALUE));
        assertTrue("Expected added vary header content to be present", response.getHeader(VARY_HEADER).contains(VARY_HEADER_VALUE));
    }
}
