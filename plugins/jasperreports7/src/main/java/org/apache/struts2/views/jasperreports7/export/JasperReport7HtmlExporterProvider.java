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

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import net.sf.jasperreports.engine.JasperPrint;
import net.sf.jasperreports.engine.export.HtmlExporter;
import net.sf.jasperreports.engine.export.HtmlResourceHandler;
import net.sf.jasperreports.export.SimpleExporterInput;
import net.sf.jasperreports.export.SimpleHtmlExporterOutput;
import net.sf.jasperreports.web.util.WebHtmlResourceHandler;
import org.apache.commons.lang3.StringUtils;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.apache.struts2.ActionInvocation;
import org.apache.struts2.StrutsException;
import org.apache.struts2.inject.Inject;
import org.apache.struts2.views.jasperreports7.JasperReport7Constants;

import java.io.IOException;
import java.io.OutputStream;

public class JasperReport7HtmlExporterProvider implements JasperReport7ExporterProvider<HtmlExporter> {

    private static final Logger LOG = LogManager.getLogger(JasperReport7HtmlExporterProvider.class);

    /**
     * Name of the url that, when prefixed with the context page, can return report images
     */
    private String imageServletUrl = "/images/";

    @Inject
    public JasperReport7HtmlExporterProvider(
            @Inject(value = JasperReport7Constants.STRUTS_JASPER_REPORT_HTML_IMAGE_SERVLET_URL, required = false)
            String imageServletUrl
    ) {
        if (StringUtils.isNoneEmpty(imageServletUrl)) {
            LOG.debug("Using custom image servlet url: {}", imageServletUrl);
            this.imageServletUrl = imageServletUrl;
        }
    }

    @Override
    public HtmlExporter createExporter(ActionInvocation invocation, JasperPrint jasperPrint) throws StrutsException {
        LOG.debug("Creating: {} exporter with image servlet url: {}", this.getClass().getSimpleName(), imageServletUrl);

        HttpServletRequest request = invocation.getInvocationContext().getServletRequest();
        HttpServletResponse response = invocation.getInvocationContext().getServletResponse();

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
            LOG.error("Error writing HTML report output using: {}", JasperReport7HtmlExporterProvider.class.getName(), e);
            throw new StrutsException(e.getMessage(), e);
        }

        return exporter;
    }
}
