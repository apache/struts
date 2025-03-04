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

import jakarta.servlet.ServletContext;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletResponse;
import net.sf.jasperreports.engine.JRException;
import net.sf.jasperreports.engine.JRParameter;
import net.sf.jasperreports.engine.JasperFillManager;
import net.sf.jasperreports.engine.JasperPrint;
import net.sf.jasperreports.engine.JasperReport;
import net.sf.jasperreports.engine.util.JRLoader;
import net.sf.jasperreports.export.Exporter;
import org.apache.commons.lang3.StringUtils;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.apache.struts2.ActionInvocation;
import org.apache.struts2.StrutsException;
import org.apache.struts2.inject.Inject;
import org.apache.struts2.result.StrutsResultSupport;
import org.apache.struts2.security.NotExcludedAcceptedPatternsChecker;
import org.apache.struts2.util.ValueStack;
import org.apache.struts2.views.jasperreports7.export.JasperReport7ExporterProvider;

import java.io.File;
import java.sql.Connection;
import java.util.Locale;
import java.util.Map;
import java.util.TimeZone;

/**
 * <!-- START SNIPPET: description -->
 * <p>
 * Generates a JasperReports report using the specified format or PDF if no
 * format is specified.
 * </p>
 * <!-- END SNIPPET: description -->
 * <p>
 * <b>This result type takes the following parameters:</b>
 * </p>
 * <!-- START SNIPPET: params -->
 *
 * <ul>
 *
 * <li><b>location (default)</b> - the location where the compiled jasper report
 * definition is (foo.jasper), relative from current URL.</li>
 * <li><b>dataSource (required)</b> - the EL expression used to retrieve the
 * datasource from the value stack (usually a List).</li>
 * <li><b>parse</b> - true by default. If set to false, all the parameters will
 * not be parsed for EL expressions.</li>
 * <li><b>format</b> - the format in which the report should be generated. Valid
 * values can be found in {@link JasperReport7Constants}. If no format is
 * specified, PDF will be used.</li>
 * <li><b>contentDisposition</b> - disposition (defaults to "inline", values are
 * typically <i>filename="document.pdf"</i>).</li>
 * <li><b>documentName</b> - name of the document (will generate the http header
 * <code>Content-disposition = X; filename=X.[format]</code>).</li>
 * <li>
 * <b>reportParameters</b> - an expression used to retrieve a map of
 * report parameters from the value stack. The parameters may be accessed
 * in the report via the usual JR mechanism and might include data not
 * part of the dataSource, such as the user name of the report creator, etc.
 * </li>
 * <li>
 * <b>connection</b> - a JDBC Connection which can be passed to the
 * report instead of dataSource
 * </li>
 * <li><b>wrapField</b> - defines if fields should warp with ValueStackDataSource
 * see <a href="https://issues.apache.org/jira/browse/WW-3698">WW-3698</a> for more details
 * </li>
 * </ul>
 * <p>
 * This result follows the same rules from {@link StrutsResultSupport}.
 * Specifically, all parameters will be parsed if the "parse" parameter
 * is not set to false.
 * </p>
 * <!-- END SNIPPET: params -->
 * <p><b>Example:</b></p>
 * <pre>
 * <!-- START SNIPPET: example1 -->
 * &lt;result name="success" type="jasperReport7"&gt;
 *   &lt;param name="location"&gt;foo.jasper&lt;/param&gt;
 *   &lt;param name="dataSource"&gt;mySource&lt;/param&gt;
 *   &lt;param name="format"&gt;CSV&lt;/param&gt;
 * &lt;/result&gt;
 * <!-- END SNIPPET: example1 -->
 * </pre>
 * <p>
 * or for pdf
 *
 * <pre>
 * <!-- START SNIPPET: example2 -->
 * &lt;result name="success" type="jasperReport7"&gt;
 *   &lt;param name="location"&gt;foo.jasper&lt;/param&gt;
 *   &lt;param name="dataSource"&gt;mySource&lt;/param&gt;
 * &lt;/result&gt;
 * <!-- END SNIPPET: example2 -->
 * </pre>
 */
public class JasperReport7Result extends StrutsResultSupport implements JasperReport7Constants {

    private static final Logger LOG = LogManager.getLogger(JasperReport7Result.class);

    private String parsedDataSource;

    protected String dataSource;
    protected String format;
    protected String documentName;
    protected String contentDisposition;
    protected String timeZone;

    protected boolean wrapField = true;

    /**
     * Connection can be passed to the report instead of dataSource.
     */
    protected String connection;

    /**
     * Names a report parameters map stack value, allowing additional report parameters from the action.
     */
    protected String reportParameters;
    private String parsedReportParameters;

    /**
     * Parameters validator, excludes not accepted params
     */
    private NotExcludedAcceptedPatternsChecker notExcludedAcceptedPatterns;

    public JasperReport7Result() {
        super();
    }

    @Inject
    public void setNotExcludedAcceptedPatterns(NotExcludedAcceptedPatternsChecker notExcludedAcceptedPatterns) {
        this.notExcludedAcceptedPatterns = notExcludedAcceptedPatterns;
    }

    protected void doExecute(String finalLocation, ActionInvocation invocation) throws Exception {
        initializeProperties(invocation);

        LOG.debug("Creating JasperReport for dataSource: {} and format: {}", dataSource, format);
        // Construct the data source for the report.
        ValueStack stack = invocation.getStack();
        Connection reportConnection = (Connection) stack.findValue(connection);
        ValueStackDataSource reportDataSource = null;
        if (reportConnection == null) {
            reportDataSource = prepareDataSource(stack);
        }

        if (invocation.getAction() instanceof JasperReport7Aware action) {
            LOG.debug("Passing control to action: {} before generating report", invocation.getInvocationContext().getActionName());
            action.beforeReportGeneration(invocation);
        }

        ServletContext servletContext = invocation.getInvocationContext().getServletContext();
        String systemId = servletContext.getRealPath(finalLocation);
        Map<String, Object> parameters = new ValueStackShadowMap(stack);
        File directory = new File(systemId.substring(0, systemId.lastIndexOf(File.separator)));
        parameters.put("reportDirectory", directory);

        applyLocale(invocation, parameters);
        applyTimeZone(invocation, parameters);
        applyCustomParameters(stack, parameters);

        JasperPrint jasperPrint;

        // Fill the report and produce a print object
        try {
            JasperReport jasperReport = (JasperReport) JRLoader.loadObject(new File(systemId));
            if (reportConnection == null) {
                jasperPrint = JasperFillManager.fillReport(jasperReport, parameters, reportDataSource);
            } else {
                jasperPrint = JasperFillManager.fillReport(jasperReport, parameters, reportConnection);
            }

            if (invocation.getAction() instanceof JasperReport7Aware action) {
                LOG.debug("Passing control to action: {} after generating report: {}",
                        invocation.getInvocationContext().getActionName(), jasperReport.getName());
                action.afterReportGeneration(invocation, jasperReport);
            }
        } catch (JRException e) {
            LOG.error("Error building report for uri: {}", systemId, e);
            throw new ServletException(e.getMessage(), e);
        }

        try {
            LOG.debug("Export the print object to the desired output format: {}", format);
            JasperReport7ExporterProvider<?> exporterProvider = invocation.getInvocationContext().getContainer().getInstance(JasperReport7ExporterProvider.class, format);
            if (exporterProvider == null) {
                throw new StrutsException("No exporter found for format: " + format);
            }
            exportReport(invocation, jasperPrint, exporterProvider);
        } catch (StrutsException e) {
            LOG.error("Error producing: {} report for uri: {}", format, systemId, e);
            throw new ServletException(e.getMessage(), e);
        } finally {
            try {
                if (reportConnection != null) {
                    reportConnection.close();
                }
            } catch (Exception e) {
                LOG.warn("Could not close db connection properly", e);
            }
        }
    }

    protected ValueStackDataSource prepareDataSource(ValueStack stack) throws ServletException {
        boolean evaluated = parsedDataSource != null && !parsedDataSource.equals(dataSource);
        boolean reevaluate = !evaluated || isAcceptableExpression(parsedDataSource);
        if (reevaluate) {
            return new ValueStackDataSource(stack, parsedDataSource, wrapField);
        } else {
            throw new ServletException(String.format("Unaccepted dataSource expression [%s]", parsedDataSource));
        }
    }

    protected void applyLocale(ActionInvocation invocation, Map<String, Object> parameters) {
        Locale locale = null;
        if (invocation.getAction() instanceof JasperReport7Aware action) {
            locale = action.getReportLocale(invocation);
        }
        if (locale == null) {
            locale = invocation.getInvocationContext().getLocale();
        }

        LOG.debug("Using locale: {} to generate report", locale);
        parameters.put(JRParameter.REPORT_LOCALE, locale);
    }

    protected void applyTimeZone(ActionInvocation invocation, Map<String, Object> parameters) {
        if (timeZone != null) {
            timeZone = conditionalParse(timeZone, invocation);
            LOG.debug("Puts timezone in jasper report parameter: {}", timeZone);
            final TimeZone tz = TimeZone.getTimeZone(timeZone);
            if (tz != null) {
                parameters.put(JRParameter.REPORT_TIME_ZONE, tz);
            }
        }
    }

    @SuppressWarnings("unchecked")
    protected void applyCustomParameters(ValueStack stack, Map<String, Object> parameters) {
        boolean evaluated = parsedReportParameters != null && !parsedReportParameters.equals(reportParameters);
        boolean reevaluate = !evaluated || isAcceptableExpression(parsedReportParameters);
        Map<String, Object> reportParams = reevaluate ? (Map<String, Object>) stack.findValue(parsedReportParameters) : null;
        if (reportParams != null) {
            LOG.debug("Found report parameters: {}", reportParams);
            parameters.putAll(reportParams);
        }
    }

    protected void exportReport(ActionInvocation invocation, JasperPrint jasperPrint, JasperReport7ExporterProvider<?> exporterProvider) throws StrutsException {
        HttpServletResponse response = prepapreHttpServletResponse(invocation);
        try {
            Exporter<?, ?, ?, ?> exporter = exporterProvider.createExporter(invocation, jasperPrint);

            LOG.debug("Exporting report: {} as: {} and flushing response stream", jasperPrint.getName(), format);
            exporter.exportReport();
            response.getOutputStream().flush();
        } catch (Exception e) {
            throw new StrutsException(e);
        }
    }

    private HttpServletResponse prepapreHttpServletResponse(ActionInvocation invocation) {
        HttpServletResponse response = invocation.getInvocationContext().getServletResponse();

        if (contentDisposition != null || documentName != null) {
            final StringBuilder tmp = new StringBuilder();
            tmp.append((contentDisposition == null) ? "inline" : contentDisposition);

            if (documentName != null) {
                tmp.append("; filename=");
                tmp.append(documentName);
                tmp.append(".");
                tmp.append(format);
            }

            response.setHeader("Content-disposition", tmp.toString());
        }
        return response;
    }

    /**
     * Sets up result properties, parsing etc.
     *
     * @param invocation Current invocation.
     */
    private void initializeProperties(ActionInvocation invocation) {
        if (dataSource == null && connection == null) {
            String message = "No dataSource specified...";
            LOG.error(message);
            throw new RuntimeException(message);
        }
        if (dataSource != null) {
            parsedDataSource = conditionalParse(dataSource, invocation);
        }

        format = conditionalParse(format, invocation);
        if (StringUtils.isEmpty(format)) {
            format = FORMAT_PDF;
        }

        if (contentDisposition != null) {
            contentDisposition = conditionalParse(contentDisposition, invocation);
        }

        if (documentName != null) {
            documentName = conditionalParse(documentName, invocation);
        }

        parsedReportParameters = conditionalParse(reportParameters, invocation);
    }

    /**
     * Checks if expression doesn't contain vulnerable code
     *
     * @param expression of result
     * @return true|false
     * @since 6.0.0
     */
    protected boolean isAcceptableExpression(String expression) {
        NotExcludedAcceptedPatternsChecker.IsAllowed isAllowed = notExcludedAcceptedPatterns.isAllowed(expression);
        if (isAllowed.isAllowed()) {
            return true;
        }

        LOG.warn("Expression [{}] isn't allowed by pattern [{}]! See Accepted / Excluded patterns at\n" +
                "https://struts.apache.org/security/", expression, isAllowed.getAllowedPattern());

        return false;
    }

    public void setDataSource(String dataSource) {
        this.dataSource = dataSource;
    }

    public void setFormat(String format) {
        this.format = format;
    }

    public void setDocumentName(String documentName) {
        this.documentName = documentName;
    }

    public void setContentDisposition(String contentDisposition) {
        this.contentDisposition = contentDisposition;
    }

    public void setTimeZone(final String timeZone) {
        this.timeZone = timeZone;
    }

    public void setWrapField(boolean wrapField) {
        this.wrapField = wrapField;
    }

    public void setReportParameters(String reportParameters) {
        this.reportParameters = reportParameters;
    }

    public void setConnection(String connection) {
        this.connection = connection;
    }

}
