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
import com.opensymphony.xwork2.config.ConfigurationException;
import com.opensymphony.xwork2.mock.MockActionInvocation;
import org.apache.logging.log4j.util.Strings;
import org.apache.struts2.StrutsInternalTestCase;
import org.apache.struts2.TestAction;
import org.apache.struts2.action.CspSettingsAware;
import org.apache.struts2.dispatcher.SessionMap;
import org.apache.struts2.interceptor.csp.CspInterceptor;
import org.apache.struts2.interceptor.csp.CspSettings;
import org.apache.struts2.interceptor.csp.DefaultCspSettings;
import org.springframework.mock.web.MockHttpServletRequest;
import org.springframework.mock.web.MockHttpServletResponse;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;

import static org.junit.Assert.assertNotEquals;

public class CspInterceptorTest extends StrutsInternalTestCase {

    private final CspInterceptor interceptor = new CspInterceptor();
    private final MockActionInvocation mai = new MockActionInvocation();
    private final MockHttpServletRequest request = new MockHttpServletRequest();
    private final MockHttpServletResponse response = new MockHttpServletResponse();

    private HttpSession session;

    public void test_whenRequestReceived_thenNonceIsSetInSession_andCspHeaderContainsIt() throws Exception {
        String reportUri = "/barfoo";
        boolean reporting = false;
        interceptor.setReportUri(reportUri);
        interceptor.setEnforcingMode(reporting);

        interceptor.intercept(mai);

        assertNotNull("Nonce key does not exist", session.getAttribute("nonce"));
        assertFalse("Nonce value is empty", Strings.isEmpty((String) session.getAttribute("nonce")));
        checkHeader(reportUri, reporting);
    }

    public void test_whenNonceAlreadySetInSession_andRequestReceived_thenNewNonceIsSet() throws Exception {
        String reportUri = "https://www.google.com/";
        boolean enforcingMode = true;
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
        String reportTo = "csp-group";
        boolean enforcingMode = true;
        interceptor.setReportUri(reportUri);
        interceptor.setReportTo(reportTo);
        interceptor.setEnforcingMode(enforcingMode);
        session.setAttribute("nonce", "foo");

        interceptor.intercept(mai);

        assertNotNull("Nonce key does not exist", session.getAttribute("nonce"));
        assertFalse("Nonce value is empty", Strings.isEmpty((String) session.getAttribute("nonce")));
        assertNotEquals("New nonce value couldn't be set", "foo", session.getAttribute("nonce"));
        checkHeader(reportUri, reportTo, enforcingMode);
    }

    public void testReportingCspHeadersSet() throws Exception {
        String reportUri = "/csp-reports";
        String reportTo = "csp-group";
        boolean enforcingMode = false;
        interceptor.setReportUri(reportUri);
        interceptor.setReportTo(reportTo);
        interceptor.setEnforcingMode(enforcingMode);
        session.setAttribute("nonce", "foo");

        interceptor.intercept(mai);

        assertNotNull("Nonce value is empty", session.getAttribute("nonce"));
        assertNotEquals("New nonce value couldn't be set", "foo", session.getAttribute("nonce"));
        checkHeader(reportUri, reportTo, enforcingMode);
    }

    public void test_uriSetOnlyWhenSetIsCalled() throws Exception {
        boolean enforcingMode = false;
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
        boolean enforcingMode = false;
        interceptor.setEnforcingMode(enforcingMode);

        try {
            interceptor.setReportUri("ww w. google.@com");
            assert (false);
        } catch (IllegalArgumentException e) {
            assert (true);
        }
    }

    public void testCannotParseRelativeUri() {
        boolean enforcingMode = false;
        interceptor.setEnforcingMode(enforcingMode);

        try {
            interceptor.setReportUri("some-uri");
            assert (false);
        } catch (IllegalArgumentException e) {
            assert (true);
        }
    }

    public void testCustomPreResultListener() throws Exception {
        boolean enforcingMode = false;
        mai.setAction(new CustomerCspAction("/report-uri"));
        interceptor.setEnforcingMode(enforcingMode);
        interceptor.intercept(mai);
        checkHeader("/report-uri", enforcingMode);
    }

    public void testPrependContext() throws Exception {
        boolean enforcingMode = true;
        mai.setAction(new TestAction());
        request.setContextPath("/app");

        interceptor.setEnforcingMode(enforcingMode);
        interceptor.setReportUri("/report-uri");

        interceptor.intercept(mai);

        checkHeader("/app/report-uri", enforcingMode);
    }

    public void testNoPrependContext() throws Exception {
        boolean enforcingMode = true;
        mai.setAction(new TestAction());
        request.setContextPath("/app");

        interceptor.setEnforcingMode(enforcingMode);
        interceptor.setReportUri("/report-uri");
        interceptor.setPrependServletContext(false);

        interceptor.intercept(mai);

        checkHeader("/report-uri", enforcingMode);
    }

    public void testNonExistingCspSettingsClassName() throws Exception {
        boolean enforcingMode = true;
        mai.setAction(new TestAction());
        request.setContextPath("/app");

        interceptor.setEnforcingMode(enforcingMode);
        interceptor.setReportUri("/report-uri");
        interceptor.setPrependServletContext(false);

        try {
            interceptor.setCspSettingsClassName("foo");
            interceptor.intercept(mai);
            fail("Expected exception");
        } catch (ConfigurationException e) {
            assertEquals("The class foo doesn't exist!", e.getMessage());
        }
    }

    public void testInvalidCspSettingsClassName() throws Exception {
        boolean enforcingMode = true;
        mai.setAction(new TestAction());
        request.setContextPath("/app");

        interceptor.setEnforcingMode(enforcingMode);
        interceptor.setReportUri("/report-uri");
        interceptor.setPrependServletContext(false);

        try {
            interceptor.setCspSettingsClassName(Integer.class.getName());
            interceptor.intercept(mai);
            fail("Expected exception");
        } catch (ConfigurationException e) {
            assertEquals("The class java.lang.Integer doesn't implement org.apache.struts2.interceptor.csp.CspSettings!", e.getMessage());
        }
    }

    public void testCustomCspSettingsClassName() throws Exception {
        boolean enforcingMode = true;
        mai.setAction(new TestAction());
        request.setContextPath("/app");

        interceptor.setEnforcingMode(enforcingMode);
        interceptor.setReportUri("/report-uri");
        interceptor.setPrependServletContext(false);
        interceptor.setCspSettingsClassName(CustomDefaultCspSettings.class.getName());

        interceptor.intercept(mai);

        String header = response.getHeader(CspSettings.CSP_ENFORCE_HEADER);

        // no other customization matters for this particular class
        assertEquals("foo", header);
    }

    public void checkHeader(String reportUri, boolean enforcingMode) {
        checkHeader(reportUri, null, enforcingMode);
    }

    public void checkHeader(String reportUri, String reportTo, boolean enforcingMode) {
        String expectedCspHeader;
        if (Strings.isEmpty(reportUri)) {
            expectedCspHeader = String.format("%s '%s'; %s 'nonce-%s' '%s' %s %s; %s '%s'; ",
                    CspSettings.OBJECT_SRC, CspSettings.NONE,
                    CspSettings.SCRIPT_SRC, session.getAttribute("nonce"), CspSettings.STRICT_DYNAMIC, CspSettings.HTTP, CspSettings.HTTPS,
                    CspSettings.BASE_URI, CspSettings.NONE
            );
        } else {
            if (Strings.isEmpty(reportTo)) {
                expectedCspHeader = String.format("%s '%s'; %s 'nonce-%s' '%s' %s %s; %s '%s'; %s %s; ",
                        CspSettings.OBJECT_SRC, CspSettings.NONE,
                        CspSettings.SCRIPT_SRC, session.getAttribute("nonce"), CspSettings.STRICT_DYNAMIC, CspSettings.HTTP, CspSettings.HTTPS,
                        CspSettings.BASE_URI, CspSettings.NONE,
                        CspSettings.REPORT_URI, reportUri
                );
            } else {
                expectedCspHeader = String.format("%s '%s'; %s 'nonce-%s' '%s' %s %s; %s '%s'; %s %s; %s %s; ",
                        CspSettings.OBJECT_SRC, CspSettings.NONE,
                        CspSettings.SCRIPT_SRC, session.getAttribute("nonce"), CspSettings.STRICT_DYNAMIC, CspSettings.HTTP, CspSettings.HTTPS,
                        CspSettings.BASE_URI, CspSettings.NONE,
                        CspSettings.REPORT_URI, reportUri,
                        CspSettings.REPORT_TO, reportTo
                );
            }
        }

        String header;
        if (enforcingMode) {
            header = response.getHeader(CspSettings.CSP_ENFORCE_HEADER);
        } else {
            header = response.getHeader(CspSettings.CSP_REPORT_HEADER);
        }

        assertFalse("No CSP header exists", Strings.isEmpty(header));
        assertEquals("Response headers do not contain nonce header", expectedCspHeader, header);
    }

    @Override
    protected void setUp() throws Exception {
        super.setUp();
        container.inject(interceptor);
        ActionContext context = ActionContext.getContext()
                .withContainer(container)
                .withServletRequest(request)
                .withServletResponse(response)
                .withSession(new SessionMap(request))
                .bind();
        mai.setInvocationContext(context);
        session = request.getSession();
    }

    private static class CustomerCspAction implements CspSettingsAware {

        private final String reportUri;

        private CustomerCspAction(String reportUri) {
            this.reportUri = reportUri;
        }

        @Override
        public CspSettings getCspSettings() {
            DefaultCspSettings settings = new DefaultCspSettings();
            settings.setReportUri(reportUri);
            return settings;
        }
    }

    /**
     * Custom DefaultCspSettings class that overrides the createPolicyFormat method
     * to return a fixed value.
     */
    public static class CustomDefaultCspSettings extends DefaultCspSettings {

        protected String createPolicyFormat(String nonceValue) {
            return "foo";
        }
    }
}
