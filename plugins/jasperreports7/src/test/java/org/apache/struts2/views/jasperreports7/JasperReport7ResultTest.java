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
package org.apache.struts2.views.jasperreports7;

import jakarta.servlet.ServletException;
import net.sf.jasperreports.engine.JasperCompileManager;
import org.apache.struts2.ActionContext;
import org.apache.struts2.junit.StrutsTestCase;
import org.apache.struts2.mock.MockActionInvocation;
import org.apache.struts2.security.NotExcludedAcceptedPatternsChecker;
import org.apache.struts2.util.ClassLoaderUtil;
import org.apache.struts2.util.ValueStack;

import java.net.URL;
import java.sql.Connection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.regex.Pattern;
import java.util.stream.Stream;

import static org.assertj.core.api.Assertions.assertThat;
import static org.easymock.EasyMock.createMock;
import static org.easymock.EasyMock.expectLastCall;
import static org.easymock.EasyMock.replay;
import static org.easymock.EasyMock.verify;

public class JasperReport7ResultTest extends StrutsTestCase {

    private MockActionInvocation invocation;
    private ValueStack stack;
    private JasperReport7Result result;

    public void testConnClose() throws Exception {
        // given
        Connection connection = createMock(Connection.class);
        final Boolean[] closed = {Boolean.FALSE};
        connection.close();
        expectLastCall().andAnswer(() -> {
            closed[0] = true;
            return null;
        });
        replay(connection);

        stack.push(connection);
        result.setConnection("top");
        assertFalse(closed[0]);

        // when
        result.execute(this.invocation);

        // then
        verify(connection);
        assertTrue(closed[0]);
    }

    public void testDataSourceNotAccepted() throws Exception {
        // given
        stack.push(new Object() {
            public String getDatasourceName() {
                return "getDatasource()";
            }

            public List<Map<String, String>> getDatasource() {
                return JR_MAP_ARRAY_DATA_SOURCE;
            }
        });
        result.setDataSource("${datasourceName}");

        try {
            result.execute(this.invocation);
        } catch (ServletException e) {
            assertEquals("Unaccepted dataSource expression [getDatasource()]", e.getMessage());
        }

        // verify that above test has really effect
        result.setNotExcludedAcceptedPatterns(NO_EXCLUSION_ACCEPT_ALL_PATTERNS_CHECKER);

        // when
        result.execute(this.invocation);

        // then
        assertThat(response.getContentType()).isEqualTo("text/xml");
        assertThat(response.getContentAsString()).contains("Hello Foo Bar!");
    }

    public void testDataSourceAccepted() throws Exception {
        // given
        stack.push(new Object() {
            public String getDatasourceName() {
                return "datasource";
            }

            public List<Map<String, String>> getDatasource() {
                return JR_MAP_ARRAY_DATA_SOURCE;
            }
        });
        result.setDataSource("${datasourceName}");

        // when
        result.execute(this.invocation);

        // then
        assertThat(response.getContentType()).isEqualTo("text/xml");
        assertTrue(response.getContentAsString().contains("Hello Foo Bar!"));
    }

    public void testDataSourceExpressionAccepted() throws Exception {
        // given
        result.setDataSource("{#{'firstName':'Qux', 'lastName':'Quux'}}");

        // when
        result.execute(this.invocation);

        // then
        assertThat(response.getContentType()).isEqualTo("text/xml");
        assertThat(response.getContentAsString()).contains("Hello Qux Quux!");
    }

    public void testReportParametersNotAccepted() throws Exception {
        // given
        result.setDataSource("{#{'firstName':'ignore', 'lastName':'ignore'}}");

        stack.push(new Object() {
            public String getReportParametersName() {
                return "getReportParameters()";
            }

            public Map<String, String> getReportParameters() {
                return new HashMap<>() {{
                    put("title", "Baz");
                }};
            }
        });

        result.setReportParameters("${reportParametersName}");

        // when
        result.execute(this.invocation);
        assertTrue(response.getContentAsString().contains("null Report"));

        // verify that above test has really effect
        response.setCommitted(false);
        response.reset();
        result.setNotExcludedAcceptedPatterns(NO_EXCLUSION_ACCEPT_ALL_PATTERNS_CHECKER);

        // when
        result.execute(this.invocation);

        // then
        assertThat(response.getContentType()).isEqualTo("text/xml");
        assertTrue(response.getContentAsString().contains("Baz Report"));
    }

    public void testReportParametersAccepted() throws Exception {
        // given
        result.setDataSource("{#{'firstName':'ignore', 'lastName':'ignore'}}");

        stack.push(new Object() {
            public String getReportParametersName() {
                return "reportParameters";
            }

            public Map<String, String> getReportParameters() {
                return new HashMap<>() {{
                    put("title", "Baz");
                }};
            }
        });

        result.setReportParameters("${reportParametersName}");

        // when
        result.execute(this.invocation);

        // then
        assertThat(response.getContentType()).isEqualTo("text/xml");
        assertThat(response.getContentAsString()).contains("Baz Report");
    }

    public void testExportToXml() throws Exception {
        // given
        result.setDataSource("{#{'firstName':'ignore', 'lastName':'ignore'}}");
        result.setReportParameters("#{'title':'Qux'}");
        result.setFormat(JasperReport7Constants.FORMAT_XML);

        // when
        result.execute(this.invocation);

        // then
        assertThat(response.getContentType()).isEqualTo("text/xml");
        assertThat(response.getContentAsString()).contains("Qux Report");
    }

    public void testExportToCsv() throws Exception {
        // given
        result.setDataSource("{#{'firstName':'ignore', 'lastName':'ignore'}}");
        result.setReportParameters("#{'title':'Qux'}");
        result.setFormat(JasperReport7Constants.FORMAT_CSV);

        // when
        result.execute(this.invocation);

        // then
        assertThat(response.getContentType()).isEqualTo("text/csv");
        assertThat(response.getContentAsString()).contains("Qux Report");
    }

    public void testExportToRtf() throws Exception {
        // given
        result.setDataSource("{#{'firstName':'ignore', 'lastName':'ignore'}}");
        result.setReportParameters("#{'title':'Qux'}");
        result.setFormat(JasperReport7Constants.FORMAT_RTF);

        // when
        result.execute(this.invocation);

        // then
        assertThat(response.getContentType()).isEqualTo("application/rtf");
        assertThat(response.getContentAsString()).contains("Qux Report");
    }

    public void testExportToPdf() throws Exception {
        // given
        result.setDataSource("{#{'firstName':'ignore', 'lastName':'ignore'}}");
        result.setReportParameters("#{'title':'Qux'}");
        result.setFormat(JasperReport7Constants.FORMAT_PDF);

        // when
        result.execute(this.invocation);

        // then
        assertThat(response.getContentType()).isEqualTo("application/pdf");
        assertThat(response.getContentAsByteArray()).hasSizeGreaterThan(0);
    }

    public void testExportToHtml() throws Exception {
        // given
        result.setDataSource("{#{'firstName':'ignore', 'lastName':'ignore'}}");
        result.setReportParameters("#{'title':'Qux'}");
        result.setFormat(JasperReport7Constants.FORMAT_HTML);

        // when
        result.execute(this.invocation);

        // then
        assertThat(response.getContentType()).isEqualTo("text/html");
        assertThat(response.getContentAsString()).contains("Qux Report");
    }

    public void testExportToXlsx() throws Exception {
        // given
        result.setDataSource("{#{'firstName':'ignore', 'lastName':'ignore'}}");
        result.setReportParameters("#{'title':'Qux'}");
        result.setFormat(JasperReport7Constants.FORMAT_XLSX);

        // when
        result.execute(this.invocation);

        // then
        assertThat(response.getContentType()).isEqualTo("application/vnd.openxmlformats-officedocument.spreadsheetml.sheet");
        assertThat(response.getContentAsByteArray()).hasSizeGreaterThan(0);
    }

    @Override
    protected void setUp() throws Exception {
        super.setUp();
        this.request.setRequestURI("http://someuri");
        ActionContext context = ActionContext.getContext()
                .withServletResponse(this.response)
                .withServletRequest(this.request)
                .withServletContext(this.servletContext);
        this.stack = context.getValueStack();

        this.invocation = new MockActionInvocation();
        this.invocation.setInvocationContext(context);
        this.invocation.setStack(this.stack);

        result = new JasperReport7Result();
        container.inject(result);
        URL url = ClassLoaderUtil.getResource("org/apache/struts2/views/jasperreports7/simple.jrxml", this.getClass());
        JasperCompileManager.compileReportToFile(url.getFile(), url.getFile() + ".jasper");
        result.setLocation("org/apache/struts2/views/jasperreports7/simple.jrxml.jasper");
        result.setFormat(JasperReport7Constants.FORMAT_XML);
    }

    private static final List<Map<String, String>> JR_MAP_ARRAY_DATA_SOURCE = Stream.<Map<String, String>>of(
            new HashMap<>() {{
                put("firstName", "Foo");
                put("lastName", "Bar");
            }}
    ).toList();

    private static final NotExcludedAcceptedPatternsChecker NO_EXCLUSION_ACCEPT_ALL_PATTERNS_CHECKER
            = new NotExcludedAcceptedPatternsChecker() {
        @Override
        public IsAllowed isAllowed(String value) {
            return IsAllowed.yes("*");
        }

        @Override
        public IsAccepted isAccepted(String value) {
            return null;
        }

        @Override
        public void setAcceptedPatterns(String commaDelimitedPatterns) {

        }

        @Override
        public void setAcceptedPatterns(String[] patterns) {

        }

        @Override
        public void setAcceptedPatterns(Set<String> patterns) {

        }

        @Override
        public Set<Pattern> getAcceptedPatterns() {
            return null;
        }

        @Override
        public IsExcluded isExcluded(String value) {
            return null;
        }

        @Override
        public void setExcludedPatterns(String commaDelimitedPatterns) {

        }

        @Override
        public void setExcludedPatterns(String[] patterns) {

        }

        @Override
        public void setExcludedPatterns(Set<String> patterns) {

        }

        @Override
        public Set<Pattern> getExcludedPatterns() {
            return null;
        }
    };
}
