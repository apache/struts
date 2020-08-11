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


import static org.apache.struts2.interceptor.ResourceIsolationPolicy.SEC_FETCH_DEST_HEADER;
import static org.apache.struts2.interceptor.ResourceIsolationPolicy.SEC_FETCH_MODE_HEADER;
import static org.apache.struts2.interceptor.ResourceIsolationPolicy.SEC_FETCH_SITE_HEADER;
import static org.apache.struts2.interceptor.ResourceIsolationPolicy.VARY_HEADER;
import static org.junit.Assert.assertNotEquals;

import com.opensymphony.xwork2.ActionContext;
import com.opensymphony.xwork2.XWorkTestCase;
import com.opensymphony.xwork2.mock.MockActionInvocation;
import org.apache.struts2.ServletActionContext;
import org.springframework.mock.web.MockHttpServletRequest;
import org.springframework.mock.web.MockHttpServletResponse;

import java.util.Arrays;

public class FetchMetadataInterceptorTest extends XWorkTestCase {

    private final FetchMetadataInterceptor interceptor = new FetchMetadataInterceptor();
    private final MockActionInvocation mai = new MockActionInvocation();
    private final MockHttpServletRequest request = new MockHttpServletRequest();
    private final MockHttpServletResponse response = new MockHttpServletResponse();
    private static final String VARY_HEADER_VALUE = String.format(
        "%s,%s,%s",
        SEC_FETCH_DEST_HEADER,
        SEC_FETCH_SITE_HEADER,
        SEC_FETCH_MODE_HEADER
    );

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
        request.removeHeader("sec-fetch-site");

        assertNotEquals("Expected interceptor to accept this request", "403",
            interceptor.intercept(mai));
    }

    public void testValidSite() throws Exception {
        for (String header : Arrays.asList("same-origin", "same-site", "none")){
            request.addHeader("sec-fetch-site", header);

            assertNotEquals("Expected interceptor to accept this request", "403",
                interceptor.intercept(mai));
        }

    }

    public void testValidTopLevelNavigation() throws Exception {
        request.addHeader("sec-fetch-mode", "navigate");
        request.addHeader("sec-fetch-dest", "script");
        request.setMethod("GET");

        assertNotEquals("Expected interceptor to accept this request", "403",
            interceptor.intercept(mai));
    }

    public void testInvalidTopLevelNavigation() throws Exception {
        for (String header : Arrays.asList("object", "embed")) {
            request.addHeader("sec-fetch-site", "foo");
            request.addHeader("sec-fetch-mode", "navigate");
            request.addHeader("sec-fetch-dest", header);
            request.setMethod("GET");

            assertEquals("Expected interceptor to NOT accept this request", "403", interceptor.intercept(mai));
        }
    }

    public void testPathInExemptedPaths() throws Exception {
        request.addHeader("sec-fetch-site", "foo");
        request.setContextPath("/foo");

        assertNotEquals("Expected interceptor to accept this request", "403",
            interceptor.intercept(mai));
    }

    public void testPathNotInExemptedPaths() throws Exception {
        request.addHeader("sec-fetch-site", "foo");
        request.setContextPath("/foobar");

        assertEquals("Expected interceptor to NOT accept this request", "403", interceptor.intercept(mai));
    }

    public void testVaryHeaderAcceptedReq() throws Exception {
        request.addHeader("sec-fetch-site", "foo");
        request.setContextPath("/foo");

        interceptor.intercept(mai);

        assertTrue("Expected vary header to be included", response.containsHeader(VARY_HEADER));
        assertEquals("Expected different vary header value", response.getHeader(VARY_HEADER), VARY_HEADER_VALUE);
    }

    public void testVaryHeaderRejectedReq() throws Exception {
        request.addHeader("sec-fetch-site", "foo");

        interceptor.intercept(mai);

        assertTrue("Expected vary header to be included", response.containsHeader(VARY_HEADER));
        assertEquals("Expected different vary header value", response.getHeader(VARY_HEADER), VARY_HEADER_VALUE);
    }
}
