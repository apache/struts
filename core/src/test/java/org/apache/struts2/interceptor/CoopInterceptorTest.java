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

import com.opensymphony.xwork2.ActionContext;
import com.opensymphony.xwork2.mock.MockActionInvocation;
import org.apache.logging.log4j.util.Strings;
import org.apache.struts2.ServletActionContext;
import org.apache.struts2.StrutsInternalTestCase;
import org.springframework.mock.web.MockHttpServletRequest;
import org.springframework.mock.web.MockHttpServletResponse;

public class CoopInterceptorTest extends StrutsInternalTestCase {

    private final CoopInterceptor interceptor = new CoopInterceptor();
    private final MockActionInvocation mai = new MockActionInvocation();
    private final MockHttpServletRequest request = new MockHttpServletRequest();
    private final MockHttpServletResponse response = new MockHttpServletResponse();

    String SAME_ORIGIN = "same-origin";
    String SAME_SITE = "same-site";
    String UNSAFE_NONE = "unsafe-none";
    String COOP_HEADER = "Cross-Origin-Opener-Policy";

    public void testHeaderIsSetNonExemptedPath() throws Exception {
        request.setContextPath("/some");
        interceptor.intercept(mai);

        String header = response.getHeader(COOP_HEADER);
        assertFalse("Coop header does not existing non-exempted path", Strings.isEmpty(header));
        assertEquals("Coop header is not same-origin", SAME_ORIGIN, header);
    }

    public void testHeaderIsNotSetExemptedPath() throws Exception {
        request.setContextPath("/foo");
        interceptor.intercept(mai);

        String header = response.getHeader(COOP_HEADER);
        assertTrue("Coop header exists in exempted path", Strings.isEmpty(header));
    }

    public void testChangeDefaultMode() throws Exception {
        interceptor.setMode("unsafe-none");
        request.setContextPath("/some");
        interceptor.intercept(mai);

        String header = response.getHeader(COOP_HEADER);
        assertFalse("Coop header does not existin non-exempted path", Strings.isEmpty(header));
        assertEquals("Coop header is not same-origin", UNSAFE_NONE, header);
    }

    public void testErrorNotRecognizedMode() throws Exception {
        request.setContextPath("/some");

        try{
            interceptor.setMode("foobar");
            fail("Exception should be thrown for unrecognized mode");
        } catch (IllegalArgumentException e){
            assert(true);
        }
    }

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

}
