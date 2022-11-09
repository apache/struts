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
import org.apache.struts2.StrutsInternalTestCase;
import org.apache.struts2.dispatcher.SessionMap;
import org.apache.struts2.interceptor.csp.CspInterceptor;
import org.springframework.mock.web.MockHttpServletRequest;
import org.springframework.mock.web.MockHttpServletResponse;

import javax.servlet.http.HttpSession;

import static org.apache.struts2.interceptor.csp.CspSettings.BASE_URI;
import static org.apache.struts2.interceptor.csp.CspSettings.CSP_ENFORCE_HEADER;
import static org.apache.struts2.interceptor.csp.CspSettings.CSP_REPORT_HEADER;
import static org.apache.struts2.interceptor.csp.CspSettings.HTTP;
import static org.apache.struts2.interceptor.csp.CspSettings.HTTPS;
import static org.apache.struts2.interceptor.csp.CspSettings.NONE;
import static org.apache.struts2.interceptor.csp.CspSettings.OBJECT_SRC;
import static org.apache.struts2.interceptor.csp.CspSettings.REPORT_URI;
import static org.apache.struts2.interceptor.csp.CspSettings.SCRIPT_SRC;
import static org.apache.struts2.interceptor.csp.CspSettings.STRICT_DYNAMIC;
import static org.junit.Assert.assertNotEquals;

public class CspInterceptorTest extends StrutsInternalTestCase {

    private final CspInterceptor interceptor = new CspInterceptor();
    private final MockActionInvocation mai = new MockActionInvocation();
    private final MockHttpServletRequest request = new MockHttpServletRequest();
    private final MockHttpServletResponse response = new MockHttpServletResponse();

    private HttpSession session;

    public void test_whenRequestReceived_thenNonceIsSetInSession_andCspHeaderContainsIt() throws Exception {
        String reportUri = "/barfoo";
        String reporting = "false";
        interceptor.setReportUri(reportUri);
        interceptor.setEnforcingMode(reporting);

        interceptor.intercept(mai);

        assertNotNull("Nonce key does not exist", session.getAttribute("nonce"));
        assertFalse("Nonce value is empty", Strings.isEmpty((String) session.getAttribute("nonce")));
        checkHeader(reportUri, reporting);
    }

    public void test_whenNonceAlreadySetInSession_andRequestReceived_thenNewNonceIsSet() throws Exception {
        String reportUri = "https://www.google.com/";
        String enforcingMode = "true";
        interceptor.setReportUri(reportUri);
        interceptor.setEnforcingMode(enforcingMode);
        session.setAttribute("nonce", "foo");

        interceptor.intercept(mai);

        assertNotNull("Nonce key does not exist", session.getAttribute("nonce"));
        assertFalse("Nonce value is empty", Strings.isEmpty((String) session.getAttribute("nonce")));
        assertNotEquals("New nonce value couldn't be set", "foo", session.getAttribute("nonce"));
        checkHeader(reportUri, enforcingMode);
    }

    public void testEnforcingCspHeadersSet() throws Exception {
        String reportUri = "/csp-reports";
        String enforcingMode = "true";
        interceptor.setReportUri(reportUri);
        interceptor.setEnforcingMode(enforcingMode);
        session.setAttribute("nonce", "foo");

        interceptor.intercept(mai);

        assertNotNull("Nonce key does not exist", session.getAttribute("nonce"));
        assertFalse("Nonce value is empty", Strings.isEmpty((String) session.getAttribute("nonce")));
        assertNotEquals("New nonce value couldn't be set", "foo", session.getAttribute("nonce"));
        checkHeader(reportUri, enforcingMode);
    }

    public void testReportingCspHeadersSet() throws Exception {
        String reportUri = "/csp-reports";
        String enforcingMode = "false";
        interceptor.setReportUri(reportUri);
        interceptor.setEnforcingMode(enforcingMode);
        session.setAttribute("nonce", "foo");

        interceptor.intercept(mai);

        assertNotNull("Nonce value is empty", session.getAttribute("nonce"));
        assertNotEquals("New nonce value couldn't be set", "foo", session.getAttribute("nonce"));
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

    public void testCannotParseUri() {
        String enforcingMode = "false";
        interceptor.setEnforcingMode(enforcingMode);

        try {
            interceptor.setReportUri("ww w. google.@com");
            assert (false);
        } catch (IllegalArgumentException e) {
            assert (true);
        }
    }

    public void testCannotParseRelativeUri() {
        String enforcingMode = "false";
        interceptor.setEnforcingMode(enforcingMode);

        try {
            interceptor.setReportUri("some-uri");
            assert (false);
        } catch (IllegalArgumentException e) {
            assert (true);
        }
    }

    public void checkHeader(String reportUri, String enforcingMode) {
        String expectedCspHeader;
        if (Strings.isEmpty(reportUri)) {
            expectedCspHeader = String.format("%s '%s'; %s 'nonce-%s' '%s' %s %s; %s '%s'; ",
                OBJECT_SRC, NONE,
                SCRIPT_SRC, session.getAttribute("nonce"), STRICT_DYNAMIC, HTTP, HTTPS,
                BASE_URI, NONE
            );
        } else {
            expectedCspHeader = String.format("%s '%s'; %s 'nonce-%s' '%s' %s %s; %s '%s'; %s %s",
                OBJECT_SRC, NONE,
                SCRIPT_SRC, session.getAttribute("nonce"), STRICT_DYNAMIC, HTTP, HTTPS,
                BASE_URI, NONE,
                REPORT_URI, reportUri
            );
        }

        String header;
        if (enforcingMode.equals("true")) {
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
        ActionContext context = ActionContext.getContext()
            .withServletRequest(request)
            .withServletResponse(response)
            .withSession(new SessionMap<>(request))
            .bind();
        mai.setInvocationContext(context);
        session = request.getSession();
    }
}
