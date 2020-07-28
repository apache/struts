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

    public void test_whenRequestReceived_thenNonceIsSetInSession_andCspHeaderContainsIt() throws Exception {
        String reportUri = "/barfoo";
        String reporting = "false";
        interceptor.setReportUri(reportUri);
        interceptor.setEnforcingMode(reporting);

        interceptor.intercept(mai);

        assertTrue("Nonce key does not exist", session.containsKey("nonce"));
        assertFalse("Nonce value is empty", Strings.isEmpty((String) session.get("nonce")));
        checkHeader(reportUri, reporting);
    }

    public void test_whenNonceAlreadySetInSession_andRequestReceived_thenNewNonceIsSet() throws Exception {
        String reportUri = "https://www.google.com/";
        String enforcingMode = "true";
        interceptor.setReportUri(reportUri);
        interceptor.setEnforcingMode(enforcingMode);
        session.put("nonce", "foo");

        interceptor.intercept(mai);

        assertTrue("Nonce key does not exist", session.containsKey("nonce"));
        assertFalse("Nonce value is empty", Strings.isEmpty((String) session.get("nonce")));
        assertFalse("New nonce value couldn't be set", session.get("nonce").equals("foo"));
        checkHeader(reportUri, enforcingMode);
    }

    public void testEnforcingCspHeadersSet() throws Exception {
        String reportUri = "/csp-reports";
        String enforcingMode = "true";
        interceptor.setReportUri(reportUri);
        interceptor.setEnforcingMode(enforcingMode);
        session.put("nonce", "foo");

        interceptor.intercept(mai);

        assertTrue("Nonce key does not exist", session.containsKey("nonce"));
        assertFalse("Nonce value is empty", Strings.isEmpty((String) session.get("nonce")));
        assertFalse("New nonce value couldn't be set", session.get("nonce").equals("foo"));
        checkHeader(reportUri, enforcingMode);
    }

    public void testReportingCspHeadersSet() throws Exception {
        String reportUri = "/csp-reports";
        String enforcingMode = "false";
        interceptor.setReportUri(reportUri);
        interceptor.setEnforcingMode(enforcingMode);
        session.put("nonce", "foo");

        interceptor.intercept(mai);

        assertTrue("Nonce key does not exist", session.containsKey("nonce"));
        assertFalse("Nonce value is empty", Strings.isEmpty((String) session.get("nonce")));
        assertFalse("New nonce value couldn't be set", session.get("nonce").equals("foo"));
        checkHeader(reportUri, enforcingMode);
    }

    public void test_uriSetOnlyWhenSetIsCalled() throws Exception {
        String enforcingMode = "false";
        interceptor.setEnforcingMode(enforcingMode);

        interceptor.intercept(mai);
        checkHeader(null, enforcingMode);

//      set report uri
        String reportUri = "/some-uri";
        interceptor.setReportUri(reportUri);
        interceptor.intercept(mai);
        checkHeader(reportUri, enforcingMode);
    }

    public void testCannotParseUri() throws Exception {
        String enforcingMode = "false";
        interceptor.setEnforcingMode(enforcingMode);

        try{
            interceptor.setReportUri("ww w. google.@com");
            assert(false);
        } catch (IllegalArgumentException e){
            assert(true);
        }
    }

    public void testCannotParseRelativeUri() throws Exception {
        String enforcingMode = "false";
        interceptor.setEnforcingMode(enforcingMode);

        try{
            interceptor.setReportUri("some-uri");
            assert(false);
        } catch (IllegalArgumentException e){
            assert(true);
        }
    }

    public void checkHeader(String reportUri, String enforcingMode){
        String expectedCspHeader = "";
        if (Strings.isEmpty(reportUri)) {
            expectedCspHeader = String.format("%s '%s'; %s 'nonce-%s' '%s' %s %s; %s '%s'; ",
                    OBJECT_SRC, NONE,
                    SCRIPT_SRC, session.get("nonce"), STRICT_DYNAMIC, HTTP, HTTPS,
                    BASE_URI, NONE
            );
        } else {
            expectedCspHeader = String.format("%s '%s'; %s 'nonce-%s' '%s' %s %s; %s '%s'; %s %s",
                    OBJECT_SRC, NONE,
                    SCRIPT_SRC, session.get("nonce"), STRICT_DYNAMIC, HTTP, HTTPS,
                    BASE_URI, NONE,
                    REPORT_URI, reportUri
            );
        }

        String header = "";
        if (enforcingMode.equals("true")){
            header = response.getHeader(CSP_ENFORCE_HEADER);
        } else {
            header = response.getHeader(CSP_REPORT_HEADER);
        }

        assertFalse("No CSP header exists", Strings.isEmpty(header));
        assertEquals("Response headers do not contain nonce header", expectedCspHeader, header);
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
