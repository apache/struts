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
package org.apache.struts2.action;

import com.opensymphony.xwork2.XWorkTestCase;
import org.apache.struts2.StrutsConstants;
import org.apache.struts2.interceptor.csp.CspSettings;
import org.springframework.mock.web.MockHttpServletRequest;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.Reader;
import java.util.Properties;
import java.util.concurrent.atomic.AtomicLong;

/**
 * Verifies that {@link CspReportAction} applies an upper bound to the report body it accepts, and
 * that the bound is configurable.
 */
public class CspReportActionReportSizeTest extends XWorkTestCase {

    /**
     * The reader supplied by the container buffers ahead, so consumption is bounded by the limit
     * plus one buffer rather than by the limit exactly. That overshoot is fixed, not proportional
     * to the size of the body.
     */
    private static final long READ_AHEAD_ALLOWANCE = 8192L;

    /**
     * Produces {@code total} characters without buffering them, and records how many the caller
     * actually consumed.
     */
    private static final class CountingReader extends Reader {
        private final long total;
        private final AtomicLong consumed;
        private long produced = 0;

        CountingReader(long total, AtomicLong consumed) {
            this.total = total;
            this.consumed = consumed;
        }

        @Override
        public int read(char[] cbuf, int off, int len) {
            if (produced >= total) {
                return -1;
            }
            int count = (int) Math.min(len, total - produced);
            for (int i = 0; i < count; i++) {
                cbuf[off + i] = 'a';
            }
            produced += count;
            consumed.addAndGet(count);
            return count;
        }

        @Override
        public void close() {
            // characters are generated on demand, so there is nothing to release
        }
    }

    private static final class CapturingCspReportAction extends CspReportAction {
        String captured;
        int reports;

        @Override
        void processReport(String jsonCspReport) {
            captured = jsonCspReport;
            reports++;
        }
    }

    /**
     * A request that both declares and delivers {@code size} characters, matching what a client can
     * actually send: the declared length and the delivered body agree.
     */
    private MockHttpServletRequest requestOfSize(final long size, final AtomicLong consumed) {
        MockHttpServletRequest request = new MockHttpServletRequest("POST", "/csp-reports") {
            @Override
            public int getContentLength() {
                return (int) Math.min(size, Integer.MAX_VALUE);
            }

            @Override
            public BufferedReader getReader() {
                return new BufferedReader(new CountingReader(size, consumed));
            }
        };
        request.setContentType(CspSettings.CSP_REPORT_TYPE);
        return request;
    }

    public void testReportAboveLimitIsNotProcessed() {
        AtomicLong consumed = new AtomicLong();
        MockHttpServletRequest request = requestOfSize(64L * 1024 * 1024, consumed);

        CapturingCspReportAction action = new CapturingCspReportAction();
        action.withServletRequest(request);

        assertEquals("A report above the limit should not be processed", 0, action.reports);
        assertTrue("Consumed " + consumed.get() + " characters for a limit of "
                        + CspReportAction.DEFAULT_MAX_REPORT_SIZE,
                consumed.get() <= CspReportAction.DEFAULT_MAX_REPORT_SIZE + READ_AHEAD_ALLOWANCE);
    }

    public void testReportWithinLimitIsProcessed() {
        String sampleReport = "{\"csp-report\":{\"document-uri\":\"https://example.test/\"}}";
        MockHttpServletRequest request = new MockHttpServletRequest("POST", "/csp-reports");
        request.setContent(sampleReport.getBytes());
        request.setContentType(CspSettings.CSP_REPORT_TYPE);

        CapturingCspReportAction action = new CapturingCspReportAction();
        action.withServletRequest(request);

        assertEquals("A report within the limit should be processed", 1, action.reports);
        assertEquals("The report should be passed through unchanged", sampleReport, action.captured);
    }

    public void testConfiguredLimitIsApplied() {
        AtomicLong consumed = new AtomicLong();
        MockHttpServletRequest request = requestOfSize(4096, consumed);

        CapturingCspReportAction action = new CapturingCspReportAction();
        action.setMaxReportSize("1024");
        action.withServletRequest(request);

        assertEquals("A report above the configured limit should not be processed", 0, action.reports);
        assertTrue("Consumed " + consumed.get() + " characters for a configured limit of 1024",
                consumed.get() <= 1024L + READ_AHEAD_ALLOWANCE);
    }

    /**
     * The key named by {@link StrutsConstants#STRUTS_CSP_REPORT_MAX_SIZE} must exist in
     * default.properties under exactly that name. If the two drift apart the value is silently never
     * injected, leaving the limit hard-coded and the documented setting inert.
     */
    public void testLimitKeyIsDefinedInDefaultProperties() throws IOException {
        Properties defaults = new Properties();
        try (InputStream in = getClass().getClassLoader()
                .getResourceAsStream("org/apache/struts2/default.properties")) {
            assertNotNull("default.properties should be on the classpath", in);
            defaults.load(in);
        }

        assertEquals(StrutsConstants.STRUTS_CSP_REPORT_MAX_SIZE + " should be defined in default.properties",
                String.valueOf(CspReportAction.DEFAULT_MAX_REPORT_SIZE),
                defaults.getProperty(StrutsConstants.STRUTS_CSP_REPORT_MAX_SIZE));
    }

    public void testUnusableConfiguredValuesAreIgnored() {
        String[] unusable = {"", "  ", "not-a-number", "0", "-1", "2147483647"};

        for (String value : unusable) {
            AtomicLong consumed = new AtomicLong();
            MockHttpServletRequest request = requestOfSize(64L * 1024 * 1024, consumed);

            CapturingCspReportAction action = new CapturingCspReportAction();
            action.setMaxReportSize(value);
            action.withServletRequest(request);

            assertEquals("A report above the default limit should not be processed for value '"
                    + value + "'", 0, action.reports);
            assertTrue("Consumed " + consumed.get() + " characters for value '" + value + "'",
                    consumed.get() <= CspReportAction.DEFAULT_MAX_REPORT_SIZE + READ_AHEAD_ALLOWANCE);
        }
    }
}
