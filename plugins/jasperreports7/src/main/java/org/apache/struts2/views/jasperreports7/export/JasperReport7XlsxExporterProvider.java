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
import net.sf.jasperreports.engine.export.ooxml.JRXlsxExporter;
import net.sf.jasperreports.export.OutputStreamExporterOutput;
import net.sf.jasperreports.export.SimpleExporterInput;
import net.sf.jasperreports.export.SimpleOutputStreamExporterOutput;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.apache.struts2.ActionInvocation;
import org.apache.struts2.StrutsException;

import java.io.IOException;
import java.io.OutputStream;

public class JasperReport7XlsxExporterProvider implements JasperReport7ExporterProvider<JRXlsxExporter> {

    private static final Logger LOG = LogManager.getLogger(JasperReport7XlsxExporterProvider.class);

    @Override
    public JRXlsxExporter createExporter(ActionInvocation invocation, JasperPrint jasperPrint) throws StrutsException {
        LOG.debug("Creating: {} exporter", this.getClass().getSimpleName());

        HttpServletResponse response = invocation.getInvocationContext().getServletResponse();
        response.setContentType("application/vnd.openxmlformats-officedocument.spreadsheetml.sheet");

        JRXlsxExporter exporter = new JRXlsxExporter();

        SimpleExporterInput input = new SimpleExporterInput(jasperPrint);
        exporter.setExporterInput(input);

        try (OutputStream responseStream = response.getOutputStream()) {
            OutputStreamExporterOutput exporterOutput = new SimpleOutputStreamExporterOutput(responseStream);
            exporter.setExporterOutput(exporterOutput);
        } catch (IOException e) {
            LOG.error("Error writing XLSX report output using: {}", JasperReport7XlsxExporterProvider.class.getName(), e);
            throw new StrutsException(e.getMessage(), e);
        }

        return exporter;
    }
}
