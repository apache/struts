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
package org.apache.struts2.views.jasperreports;

import com.opensymphony.xwork2.ActionContext;
import com.opensymphony.xwork2.mock.MockActionInvocation;
import com.opensymphony.xwork2.security.NotExcludedAcceptedPatternsChecker;
import com.opensymphony.xwork2.util.ClassLoaderUtil;
import com.opensymphony.xwork2.util.ValueStack;
import net.sf.jasperreports.engine.JasperCompileManager;
import org.apache.struts2.StrutsStatics;
import org.apache.struts2.StrutsTestCase;

import javax.servlet.ServletException;
import java.net.URL;
import java.sql.Connection;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;
import java.util.regex.Pattern;

import static net.sf.jasperreports.engine.JRExporterParameter.OUTPUT_STRING_BUFFER;
import static org.easymock.EasyMock.createMock;
import static org.easymock.EasyMock.expectLastCall;
import static org.easymock.EasyMock.replay;
import static org.easymock.EasyMock.verify;
import static org.junit.Assert.assertNotEquals;

public class JasperReportsResultTest extends StrutsTestCase {
    private MockActionInvocation invocation;
    private ValueStack stack;
    private JasperReportsResult result;

    public void testConnClose() throws Exception {
        Connection connection = createMock(Connection.class);
        final Boolean[] closed = {false};
        connection.close();
        expectLastCall().andAnswer(() -> {
            closed[0] = true;
            return null;
        });
        replay(connection);

        stack.push(connection);
        result.setConnection("top");

        assertFalse(closed[0]);
        result.execute(this.invocation);
        verify(connection);
        assertTrue(closed[0]);
    }

    public void testDataSourceNotAccepted() throws Exception {
        stack.push(new Object() {
            public String getDatasourceName() {
                return "getDatasource()";
            }

            public Map<String, String>[] getDatasource() {
                return JR_MAP_ARRAY_DATA_SOURCE;
            }
        });
        result.setDataSource("${datasourceName}");

        try {
            result.execute(this.invocation);
        } catch (ServletException e) {
            assertEquals("Error building dataSource for excluded or not accepted [getDatasource()]",
                    e.getMessage());
        }

        // verify that above test has really effect
        result.setNotExcludedAcceptedPatterns(NO_EXCLUSION_ACCEPT_ALL_PATTERNS_CHECKER);
        result.execute(this.invocation);
        assertTrue(response.getContentAsString().contains("Hello Foo Bar!"));
    }

    public void testDataSourceAccepted() throws Exception {
        stack.push(new Object() {
            public String getDatasourceName() {
                return "datasource";
            }

            public Map<String, String>[] getDatasource() {
                return JR_MAP_ARRAY_DATA_SOURCE;
            }
        });
        result.setDataSource("${datasourceName}");

        result.execute(this.invocation);
        assertTrue(response.getContentAsString().contains("Hello Foo Bar!"));
    }

    public void testDataSourceExpressionAccepted() throws Exception {
        result.setDataSource("{#{'firstName':'Qux', 'lastName':'Quux'}}");

        result.execute(this.invocation);
        assertTrue(response.getContentAsString().contains("Hello Qux Quux!"));
    }

    public void testReportParametersNotAccepted() throws Exception {
        result.setDataSource("{#{'firstName':'ignore', 'lastName':'ignore'}}");

        stack.push(new Object() {
            public String getReportParametersName() {
                return "getReportParameters()";
            }

            public Map<String, String> getReportParameters() {
                return new HashMap<String, String>() {{
                    put("title", "Baz");
                }};
            }
        });

        result.setReportParameters("${reportParametersName}");
        result.execute(this.invocation);
        assertTrue(response.getContentAsString().contains("null Report"));

        // verify that above test has really effect
        response.setCommitted(false);
        response.reset();
        result.setNotExcludedAcceptedPatterns(NO_EXCLUSION_ACCEPT_ALL_PATTERNS_CHECKER);
        result.execute(this.invocation);
        assertTrue(response.getContentAsString().contains("Baz Report"));
    }

    public void testReportParametersAccepted() throws Exception {
        result.setDataSource("{#{'firstName':'ignore', 'lastName':'ignore'}}");

        stack.push(new Object() {
            public String getReportParametersName() {
                return "reportParameters";
            }

            public Map<String, String> getReportParameters() {
                return new HashMap<String, String>() {{
                    put("title", "Baz");
                }};
            }
        });

        result.setReportParameters("${reportParametersName}");
        result.execute(this.invocation);
        assertTrue(response.getContentAsString().contains("Baz Report"));
    }

    public void testReportParametersExpressionAccepted() throws Exception {
        result.setDataSource("{#{'firstName':'ignore', 'lastName':'ignore'}}");

        result.setReportParameters("#{'title':'Qux'}");
        result.execute(this.invocation);
        assertTrue(response.getContentAsString().contains("Qux Report"));
    }

    public void testExportParametersNotAccepted() throws Exception {
        result.setDataSource("{#{'firstName':'ignore', 'lastName':'ignore'}}");

        final StringBuffer sb = new StringBuffer();
        stack.push(new Object() {
            public String getExportParametersName() {
                return "getExportParameters()";
            }

            public Map<Object, Object> getExportParameters() {
                return new HashMap<Object, Object>() {{
                    put(OUTPUT_STRING_BUFFER, sb);
                }};
            }
        });

        result.setExportParameters("${exportParametersName}");
        result.execute(this.invocation);
        assertEquals(0, sb.length());

        // verify that above test has really effect
        response.setCommitted(false);
        response.reset();
        result.setNotExcludedAcceptedPatterns(NO_EXCLUSION_ACCEPT_ALL_PATTERNS_CHECKER);
        result.execute(this.invocation);
        assertNotEquals(0, sb.length());
    }

    public void testExportParametersAccepted() throws Exception {
        result.setDataSource("{#{'firstName':'Qux', 'lastName':'Quux'}}");

        final StringBuffer sb = new StringBuffer();
        stack.push(new Object() {
            public String getExportParametersName() {
                return "exportParameters";
            }

            public Map<Object, Object> getExportParameters() {
                return new HashMap<Object, Object>() {{
                    put(OUTPUT_STRING_BUFFER, sb);
                }};
            }
        });

        result.setExportParameters("${exportParametersName}");
        result.execute(this.invocation);
        assertTrue(sb.toString().contains("Hello Qux Quux!"));
    }

    @Override
    protected void setUp() throws Exception {
        super.setUp();

        request.setRequestURI("http://someuri");
        ActionContext context = ActionContext.getContext();
        context.put(StrutsStatics.HTTP_RESPONSE, response);
        context.put(StrutsStatics.HTTP_REQUEST, request);
        context.put(StrutsStatics.SERVLET_CONTEXT, servletContext);
        this.stack = context.getValueStack();
        this.invocation = new MockActionInvocation();
        this.invocation.setInvocationContext(context);
        this.invocation.setStack(this.stack);

        result = new JasperReportsResult();
        container.inject(result);
        URL url = ClassLoaderUtil.getResource("org/apache/struts2/views/jasperreports/simple.jrxml", this.getClass());
        JasperCompileManager.compileReportToFile(url.getFile(), url.getFile() + ".jasper");
        result.setLocation("org/apache/struts2/views/jasperreports/simple.jrxml.jasper");
        result.setFormat(JasperReportConstants.FORMAT_XML);
    }


    private static final Map<String, String>[] JR_MAP_ARRAY_DATA_SOURCE = new Map[]{
            new HashMap<String, String>() {{
                put("firstName", "Foo");
                put("lastName", "Bar");
            }}
    };

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
