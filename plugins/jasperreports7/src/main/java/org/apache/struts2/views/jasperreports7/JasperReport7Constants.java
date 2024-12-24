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

public interface JasperReport7Constants {

    /**
     * PDF format constant
     */
    String FORMAT_PDF = "pdf";

    /**
     * XML format constant
     */
    String FORMAT_XML = "xml";

    /**
     * HTML format constant
     */
    String FORMAT_HTML = "html";

    /**
     * XLSX format constant
     */
    String FORMAT_XLSX = "xlsx";

    /**
     * CSV format constant
     */
    String FORMAT_CSV = "csv";

    /**
     * RTF format constant
     */
    String FORMAT_RTF = "rtf";

    /**
     * Allows to define a custom default delimiter when exporting report into CSV file
     */
    String STRUTS_JASPER_REPORT_CSV_DELIMITER = "struts.jasperReport7.csv.defaultDelimiter";

    /**
     * Allows to define a custom url to image servlet used when exporting report into HTML
     */
    String STRUTS_JASPER_REPORT_HTML_IMAGE_SERVLET_URL = "struts.jasperReport7.html.imageServletUrl";

}
