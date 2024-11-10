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
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import net.sf.jasperreports.engine.JRException;
import net.sf.jasperreports.engine.JRParameter;
import net.sf.jasperreports.engine.JasperFillManager;
import net.sf.jasperreports.engine.JasperPrint;
import net.sf.jasperreports.engine.JasperReport;
import net.sf.jasperreports.engine.export.HtmlExporter;
import net.sf.jasperreports.engine.export.HtmlResourceHandler;
import net.sf.jasperreports.engine.export.JRCsvExporter;
import net.sf.jasperreports.engine.export.JRRtfExporter;
import net.sf.jasperreports.engine.export.JRXmlExporter;
import net.sf.jasperreports.engine.export.ooxml.JRXlsxExporter;
import net.sf.jasperreports.engine.util.JRLoader;
import net.sf.jasperreports.export.Exporter;
import net.sf.jasperreports.export.OutputStreamExporterOutput;
import net.sf.jasperreports.export.SimpleCsvExporterConfiguration;
import net.sf.jasperreports.export.SimpleExporterInput;
import net.sf.jasperreports.export.SimpleHtmlExporterOutput;
import net.sf.jasperreports.export.SimpleOutputStreamExporterOutput;
import net.sf.jasperreports.export.SimpleWriterExporterOutput;
import net.sf.jasperreports.export.SimpleXmlExporterOutput;
import net.sf.jasperreports.export.WriterExporterOutput;
import net.sf.jasperreports.export.XmlExporterOutput;
import net.sf.jasperreports.pdf.JRPdfExporter;
import net.sf.jasperreports.web.util.WebHtmlResourceHandler;
import org.apache.commons.lang3.StringUtils;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.apache.struts2.ActionInvocation;
import org.apache.struts2.inject.Inject;
import org.apache.struts2.result.StrutsResultSupport;
import org.apache.struts2.security.NotExcludedAcceptedPatternsChecker;
import org.apache.struts2.util.ValueStack;

import java.io.File;
import java.io.IOException;
import java.io.OutputStream;
import java.sql.Connection;
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
 * <li><b>parse</b> - true by default. If set to false, the location param will
 * not be parsed for EL expressions.</li>
 * <li><b>format</b> - the format in which the report should be generated. Valid
 * values can be found in {@link JasperReportConstants}. If no format is
 * specified, PDF will be used.</li>
 * <li><b>contentDisposition</b> - disposition (defaults to "inline", values are
 * typically <i>filename="document.pdf"</i>).</li>
 * <li><b>documentName</b> - name of the document (will generate the http header
 * <code>Content-disposition = X; filename=X.[format]</code>).</li>
 * <li><b>delimiter</b> - the delimiter used when generating CSV reports. By
 * default, the character used is ",".</li>
 * <li><b>imageServletUrl</b> - name of the url that, when prefixed with the
 * context page, can return report images.</li>
 * <li>
 * <b>reportParameters</b> - (2.1.2+) OGNL expression used to retrieve a map of
 * report parameters from the value stack. The parameters may be accessed
 * in the report via the usual JR mechanism and might include data not
 * part of the dataSource, such as the user name of the report creator, etc.
 * </li>
 * <li>
 * <b>connection</b> - (2.1.7+) JDBC Connection which can be passed to the
 * report instead of dataSource
 * </li>
 * <li><b>wrapField</b> - (2.3.18+) defines if fields should warp with ValueStackDataSource
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
 * &lt;result name="success" type="jasper"&gt;
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
 * &lt;result name="success" type="jasper"&gt;
 *   &lt;param name="location"&gt;foo.jasper&lt;/param&gt;
 *   &lt;param name="dataSource"&gt;mySource&lt;/param&gt;
 * &lt;/result&gt;
 * <!-- END SNIPPET: example2 -->
 * </pre>
 */
public class JasperReports7Result extends StrutsResultSupport implements JasperReportConstants {

    private static final Logger LOG = LogManager.getLogger(JasperReports7Result.class);

    protected String dataSource;
    private String parsedDataSource;

    protected String format;
    protected String documentName;
    protected String contentDisposition;
    protected String delimiter;
    protected String imageServletUrl = "/images/";
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

    public JasperReports7Result() {
        super();
    }

    @Inject
    public void setNotExcludedAcceptedPatterns(NotExcludedAcceptedPatternsChecker notExcludedAcceptedPatterns) {
        this.notExcludedAcceptedPatterns = notExcludedAcceptedPatterns;
    }

    protected void doExecute(String finalLocation, ActionInvocation invocation) throws Exception {
        // Will throw a runtime exception if no "datasource" property. TODO Best place for that is...?
        initializeProperties(invocation);

        LOG.debug("Creating JasperReport for dataSource = {}, format = {}", dataSource, format);

        HttpServletRequest request = invocation.getInvocationContext().getServletRequest();
        HttpServletResponse response = invocation.getInvocationContext().getServletResponse();

        // Handle IE special case: it sends a "contype" request first.
        // TODO Set content type to config settings?
        if ("contype".equals(request.getHeader("User-Agent"))) {
            try (OutputStream outputStream = response.getOutputStream()) {
                response.setContentType("application/pdf");
                response.setContentLength(0);
            } catch (IOException e) {
                LOG.error("Error writing report output", e);
                throw new ServletException(e.getMessage(), e);
            }
            return;
        }

        // Construct the data source for the report.
        ValueStack stack = invocation.getStack();
        ValueStackDataSource stackDataSource = null;

        Connection conn = (Connection) stack.findValue(connection);
        if (conn == null) {
            boolean evaluated = parsedDataSource != null && !parsedDataSource.equals(dataSource);
            boolean reevaluate = !evaluated || isAcceptableExpression(parsedDataSource);
            if (reevaluate) {
                stackDataSource = new ValueStackDataSource(stack, parsedDataSource, wrapField);
            } else {
                throw new ServletException(String.format("Error building dataSource for excluded or not accepted [%s]",
                        parsedDataSource));
            }
        }

        if ("https".equalsIgnoreCase(request.getScheme())) {
            // set the HTTP Header to work around IE SSL weirdness
            response.setHeader("CACHE-CONTROL", "PRIVATE");
            response.setHeader("Cache-Control", "maxage=3600");
            response.setHeader("Pragma", "public");
            response.setHeader("Accept-Ranges", "none");
        }

        ServletContext servletContext = invocation.getInvocationContext().getServletContext();
        String systemId = servletContext.getRealPath(finalLocation);
        Map<String, Object> parameters = new ValueStackShadowMap(stack);
        File directory = new File(systemId.substring(0, systemId.lastIndexOf(File.separator)));
        parameters.put("reportDirectory", directory);
        parameters.put(JRParameter.REPORT_LOCALE, invocation.getInvocationContext().getLocale());

        // put timezone in jasper report parameter
        if (timeZone != null) {
            timeZone = conditionalParse(timeZone, invocation);
            final TimeZone tz = TimeZone.getTimeZone(timeZone);
            if (tz != null) {
                // put the report time zone
                parameters.put(JRParameter.REPORT_TIME_ZONE, tz);
            }
        }

        // Add any report parameters from action to param map.
        boolean evaluated = parsedReportParameters != null && !parsedReportParameters.equals(reportParameters);
        boolean reevaluate = !evaluated || isAcceptableExpression(parsedReportParameters);
        Map<String, Object> reportParams = reevaluate ? (Map<String, Object>) stack.findValue(parsedReportParameters) : null;
        if (reportParams != null) {
            LOG.debug("Found report parameters; adding to parameters...");
            parameters.putAll(reportParams);
        }

        JasperPrint jasperPrint;

        // Fill the report and produce a print object
        try {
            JasperReport jasperReport = (JasperReport) JRLoader.loadObject(new File(systemId));
            if (conn == null) {
                jasperPrint = JasperFillManager.fillReport(jasperReport, parameters, stackDataSource);
            } else {
                jasperPrint = JasperFillManager.fillReport(jasperReport, parameters, conn);
            }
        } catch (JRException e) {
            LOG.error("Error building report for uri {}", systemId, e);
            throw new ServletException(e.getMessage(), e);
        }

        LOG.debug("Export the print object to the desired output format: {}", format);
        try {
            if (contentDisposition != null || documentName != null) {
                final StringBuilder tmp = new StringBuilder();
                tmp.append((contentDisposition == null) ? "inline" : contentDisposition);

                if (documentName != null) {
                    tmp.append("; filename=");
                    tmp.append(documentName);
                    tmp.append(".");
                    tmp.append(format.toLowerCase());
                }

                response.setHeader("Content-disposition", tmp.toString());
            }

            Exporter<?, ?, ?, ?> exporter = switch (format) {
                case FORMAT_PDF -> createPdfExporter(response, jasperPrint);
                case FORMAT_CSV -> createCsvExporter(response, jasperPrint);
                case FORMAT_HTML -> createHtmlExporter(request, response, jasperPrint);
                case FORMAT_XLSX -> createXlsExporter(response, jasperPrint);
                case FORMAT_XML -> createXmlExporter(response, jasperPrint);
                case FORMAT_RTF -> createRtfExporter(response, jasperPrint);
                default -> throw new ServletException("Unknown report format: " + format);
            };

            LOG.debug("Exporting report: {} as: {} and flushing response stream", jasperPrint.getName(), format);
            exporter.exportReport();

            response.getOutputStream().flush();
        } catch (JRException e) {
            LOG.error("Error producing {} report for uri {}", format, systemId, e);
            throw new ServletException(e.getMessage(), e);
        } finally {
            try {
                if (conn != null) {
                    // avoid NPE if connection was not used for the report
                    conn.close();
                }
            } catch (Exception e) {
                LOG.warn("Could not close db connection properly", e);
            }
        }
    }

    protected JRPdfExporter createPdfExporter(HttpServletResponse response, JasperPrint jasperPrint) throws ServletException {
        response.setContentType("application/pdf");

        JRPdfExporter exporter = new JRPdfExporter();

        SimpleExporterInput input = new SimpleExporterInput(jasperPrint);
        exporter.setExporterInput(input);

        try (OutputStream responseStream = response.getOutputStream()) {
            OutputStreamExporterOutput exporterOutput = new SimpleOutputStreamExporterOutput(responseStream);
            exporter.setExporterOutput(exporterOutput);
        } catch (IOException e) {
            LOG.error("Error writing report output", e);
            throw new ServletException(e.getMessage(), e);
        }

        return exporter;
    }

    protected JRCsvExporter createCsvExporter(HttpServletResponse response, JasperPrint jasperPrint) throws ServletException {
        response.setContentType("text/csv");
        JRCsvExporter exporter = new JRCsvExporter();

        SimpleCsvExporterConfiguration config = new SimpleCsvExporterConfiguration();
        config.setFieldDelimiter(delimiter);
        config.setRecordDelimiter(delimiter);
        exporter.setConfiguration(config);

        SimpleExporterInput input = new SimpleExporterInput(jasperPrint);
        exporter.setExporterInput(input);

        try (OutputStream responseStream = response.getOutputStream()) {
            WriterExporterOutput exporterOutput = new SimpleWriterExporterOutput(responseStream);
            exporter.setExporterOutput(exporterOutput);
        } catch (IOException e) {
            LOG.error("Error writing report output", e);
            throw new ServletException(e.getMessage(), e);
        }

        return exporter;
    }

    protected HtmlExporter createHtmlExporter(HttpServletRequest request, HttpServletResponse response, JasperPrint jasperPrint) throws ServletException {
        response.setContentType("text/html");
        HtmlExporter exporter = new HtmlExporter();

        SimpleExporterInput input = new SimpleExporterInput(jasperPrint);
        exporter.setExporterInput(input);

        try (OutputStream responseStream = response.getOutputStream()) {
            SimpleHtmlExporterOutput exporterOutput = new SimpleHtmlExporterOutput(responseStream);
            HtmlResourceHandler imageHandler = new WebHtmlResourceHandler(request.getContextPath() + imageServletUrl + "%s");
            exporterOutput.setImageHandler(imageHandler);
            exporter.setExporterOutput(exporterOutput);
        } catch (IOException e) {
            LOG.error("Error writing report output", e);
            throw new ServletException(e.getMessage(), e);
        }

        return exporter;
    }

    protected JRXlsxExporter createXlsExporter(HttpServletResponse response, JasperPrint jasperPrint) throws ServletException {
        response.setContentType("application/vnd.ms-excel");

        JRXlsxExporter exporter = new JRXlsxExporter();

        SimpleExporterInput input = new SimpleExporterInput(jasperPrint);
        exporter.setExporterInput(input);

        try (OutputStream responseStream = response.getOutputStream()) {
            OutputStreamExporterOutput exporterOutput = new SimpleOutputStreamExporterOutput(responseStream);
            exporter.setExporterOutput(exporterOutput);
        } catch (IOException e) {
            LOG.error("Error writing report output", e);
            throw new ServletException(e.getMessage(), e);
        }

        return exporter;
    }

    protected JRXmlExporter createXmlExporter(HttpServletResponse response, JasperPrint jasperPrint) throws ServletException {
        response.setContentType("text/xml");

        JRXmlExporter exporter = new JRXmlExporter();

        SimpleExporterInput input = new SimpleExporterInput(jasperPrint);
        exporter.setExporterInput(input);

        try (OutputStream responseOutput = response.getOutputStream()) {
            XmlExporterOutput exporterOutput = new SimpleXmlExporterOutput(responseOutput);
            exporter.setExporterOutput(exporterOutput);
        } catch (IOException e) {
            LOG.error("Error writing report output using: {}", JRXmlExporter.class.getName(), e);
            throw new ServletException(e.getMessage(), e);
        }

        return exporter;
    }

    protected JRRtfExporter createRtfExporter(HttpServletResponse response, JasperPrint jasperPrint) throws ServletException {
        response.setContentType("application/rtf");

        JRRtfExporter exporter = new JRRtfExporter();

        SimpleExporterInput input = new SimpleExporterInput(jasperPrint);
        exporter.setExporterInput(input);

        try (OutputStream responseStream = response.getOutputStream()) {
            WriterExporterOutput exporterOutput = new SimpleWriterExporterOutput(responseStream);
            exporter.setExporterOutput(exporterOutput);
        } catch (IOException e) {
            LOG.error("Error writing report output", e);
            throw new ServletException(e.getMessage(), e);
        }

        return exporter;
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


    /**
     * SETTERS
     **/

    public void setImageServletUrl(final String imageServletUrl) {
        this.imageServletUrl = imageServletUrl;
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

    public void setDelimiter(String delimiter) {
        this.delimiter = delimiter;
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
