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
package org.apache.struts2.views.jasperreports7.export;

import jakarta.servlet.http.HttpServletResponse;
import net.sf.jasperreports.engine.JasperPrint;
import net.sf.jasperreports.engine.export.JRCsvExporter;
import net.sf.jasperreports.export.SimpleCsvExporterConfiguration;
import net.sf.jasperreports.export.SimpleExporterInput;
import net.sf.jasperreports.export.SimpleWriterExporterOutput;
import net.sf.jasperreports.export.WriterExporterOutput;
import org.apache.commons.lang3.StringUtils;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.apache.struts2.ActionInvocation;
import org.apache.struts2.StrutsException;
import org.apache.struts2.inject.Inject;
import org.apache.struts2.views.jasperreports7.JasperReport7Aware;
import org.apache.struts2.views.jasperreports7.JasperReport7Constants;

import java.io.IOException;
import java.io.OutputStream;

public class JasperReport7CsvExporterProvider implements JasperReport7ExporterProvider<JRCsvExporter> {

    private static final Logger LOG = LogManager.getLogger(JasperReport7CsvExporterProvider.class);

    /**
     * A delimiter used when generating CSV report. By default, "," is used.
     */
    private String defaultDelimiter = ",";

    @Inject
    public JasperReport7CsvExporterProvider(
            @Inject(value = JasperReport7Constants.STRUTS_JASPER_REPORT_CSV_DELIMITER, required = false)
            String defaultDelimiter
    ) {
        if (StringUtils.isNoneEmpty(defaultDelimiter)) {
            LOG.debug("Using custom default delimiter [{}]", defaultDelimiter);
            this.defaultDelimiter = defaultDelimiter;
        }
    }

    @Override
    public JRCsvExporter createExporter(ActionInvocation invocation, JasperPrint jasperPrint) throws StrutsException {
        LOG.debug("Creating: {} exporter", this.getClass().getSimpleName());

        HttpServletResponse response = invocation.getInvocationContext().getServletResponse();
        response.setContentType("text/csv");

        JRCsvExporter exporter = new JRCsvExporter();

        String reportDelimiter = null;
        if (invocation.getAction() instanceof JasperReport7Aware action) {
            reportDelimiter = action.getCsvDelimiter(invocation);
        }
        if (StringUtils.isEmpty(reportDelimiter)) {
            reportDelimiter = defaultDelimiter;
        }
        LOG.debug("Using delimiter: [{}]", reportDelimiter);

        SimpleCsvExporterConfiguration config = new SimpleCsvExporterConfiguration();
        config.setFieldDelimiter(reportDelimiter);
        config.setRecordDelimiter(reportDelimiter);
        exporter.setConfiguration(config);

        SimpleExporterInput input = new SimpleExporterInput(jasperPrint);
        exporter.setExporterInput(input);

        try (OutputStream responseStream = response.getOutputStream()) {
            WriterExporterOutput exporterOutput = new SimpleWriterExporterOutput(responseStream);
            exporter.setExporterOutput(exporterOutput);
        } catch (IOException e) {
            LOG.error("Error writing CSV report output using: {}", JasperReport7CsvExporterProvider.class.getName(), e);
            throw new StrutsException(e.getMessage(), e);
        }

        return exporter;
    }
}
