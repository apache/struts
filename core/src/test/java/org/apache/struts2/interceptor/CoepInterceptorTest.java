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

import java.util.HashMap;
import java.util.Map;

public class CoepInterceptorTest extends StrutsInternalTestCase {

    private final CoepInterceptor interceptor = new CoepInterceptor();
    private final MockActionInvocation mai = new MockActionInvocation();
    private final MockHttpServletRequest request = new MockHttpServletRequest();
    private final MockHttpServletResponse response = new MockHttpServletResponse();

    private final String COEP_ENFORCING_HEADER = "Cross-Origin-Embedder-Policy";
    private final String COEP_REPORT_HEADER = "Cross-Origin-Embedder-Policy-Report-Only";
    private final String HEADER_CONTENT = "require-corp";


    public void testDisabled() throws Exception {
        interceptor.setDisabled("true");

        interceptor.intercept(mai);

        String header = response.getHeader(COEP_ENFORCING_HEADER);
        assertTrue("COEP is not disabled", Strings.isEmpty(header));
    }

    public void testEnforcingHeader() throws Exception {
        interceptor.setEnforcingMode("true");

        interceptor.intercept(mai);

        String header = response.getHeader(COEP_ENFORCING_HEADER);
        assertFalse("COEP enforcing header does not exist", Strings.isEmpty(header));
        assertEquals("COEP header value is incorrect", HEADER_CONTENT, header);
    }

    public void testExemptedPath() throws Exception{
        request.setContextPath("/foo");
        interceptor.setEnforcingMode("true");

        interceptor.intercept(mai);

        String header = response.getHeader(COEP_ENFORCING_HEADER);
        assertTrue("COEP applied to exempted path", Strings.isEmpty(header));
    }

    public void testReportingHeader() throws Exception {
        interceptor.setEnforcingMode("false");

        interceptor.intercept(mai);

        String header = response.getHeader(COEP_REPORT_HEADER);
        assertFalse("COEP reporting header does not exist", Strings.isEmpty(header));
        assertEquals("COEP header value is incorrect", HEADER_CONTENT, header);
    }

    @Override
    protected void setUp() throws Exception {
        super.setUp();
        container.inject(interceptor);
        interceptor.setExemptedPaths("/foo");
        ServletActionContext.setRequest(request);
        ServletActionContext.setResponse(response);
        ActionContext context = ServletActionContext.getActionContext();
        Map<String, Object> session = new HashMap<>();
        context.withSession(session);
        mai.setInvocationContext(context);
    }

}
