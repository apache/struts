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
import org.apache.struts2.interceptor.csp.CspInterceptor;
import org.springframework.mock.web.MockHttpServletRequest;
import org.springframework.mock.web.MockHttpServletResponse;

import java.util.HashMap;
import java.util.Map;

import static org.apache.struts2.interceptor.csp.CspSettings.*;

public class CspInterceptorTest extends StrutsInternalTestCase {

    private final CspInterceptor interceptor = new CspInterceptor();
    private final MockActionInvocation mai = new MockActionInvocation();
    private final MockHttpServletRequest request = new MockHttpServletRequest();
    private final MockHttpServletResponse response = new MockHttpServletResponse();
    private final Map<String, Object> session = new HashMap<>();

    public void testNonceNotExists() throws Exception {
        String reportUri = "barfoo";
        String reporting = "false";
        interceptor.setReportUri(reportUri);
        interceptor.setReporting(reporting);

        interceptor.intercept(mai);

        assertTrue("Nonce key does not exist", session.containsKey("nonce"));
        assertFalse("Nonce value is empty", Strings.isEmpty((String) session.get("nonce")));
        checkHeader(reportUri, reporting);
    }

    public void testNonceExists() throws Exception {
        String reportUri = "barfoo";
        String reporting = "true";
        interceptor.setReportUri(reportUri);
        interceptor.setReporting(reporting);
        session.put("nonce", "foo");

        interceptor.intercept(mai);

        assertTrue("Nonce key does not exist", session.containsKey("nonce"));
        assertFalse("Nonce value is empty", Strings.isEmpty((String) session.get("nonce")));
        checkHeader(reportUri, reporting);
    }

    public void checkHeader(String reportUri, String reporting){
        String expectedCspHeader = String.format("%s '%s'; %s 'nonce-%s' '%s' %s %s; %s '%s'; %s '%s';",
                OBJECT_SRC, NONE,
                SCRIPT_SRC, session.get("nonce"), STRICT_DYNAMIC, HTTP, HTTPS,
                BASE_URI, NONE,
                REPORT_URI, reportUri
        );

        String header = "";
        if (reporting.equals("true")){
            header = response.getHeader(CSP_REPORT_HEADER);
        } else {
            header = response.getHeader(CSP_ENFORCE_HEADER);
        }
        assertFalse("No CSP header exists", Strings.isEmpty(header));
        assertEquals("Repsonse headers does not contain nonce header", expectedCspHeader, header);
    }

    @Override
    protected void setUp() throws Exception {
        super.setUp();
        container.inject(interceptor);
        ServletActionContext.setRequest(request);
        ServletActionContext.setResponse(response);
        ActionContext context = ServletActionContext.getActionContext().bind();
        context.withSession(session);
        mai.setInvocationContext(context);
    }
}
