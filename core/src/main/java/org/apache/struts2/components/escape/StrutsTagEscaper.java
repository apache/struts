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
package org.apache.struts2.components.escape;

import org.apache.commons.text.StringEscapeUtils;

/**
 * Default {@link TagEscaper}: applies the selected escapers in the order HTML, JavaScript, XML, CSV. XML escaping
 * replaces HTML escaping, as it already covers the HTML-sensitive characters.
 * <p>
 * Each step is a protected hook, so an application needing different handling for one context (e.g. letting a
 * safelist of HTML markup through) can extend this class and override that hook only.
 */
public class StrutsTagEscaper implements TagEscaper {

    @Override
    public String escape(EscapeConfig config, String value) {
        String result = value;
        if (config.html() && !config.xml()) {
            result = escapeHtml(result);
        }
        if (config.javaScript()) {
            result = escapeJavaScript(result);
        }
        if (config.xml()) {
            result = escapeXml(result);
        }
        if (config.csv()) {
            result = escapeCsv(result);
        }
        return result;
    }

    protected String escapeHtml(String value) {
        return StringEscapeUtils.escapeHtml4(value);
    }

    protected String escapeJavaScript(String value) {
        return StringEscapeUtils.escapeEcmaScript(value);
    }

    protected String escapeXml(String value) {
        return StringEscapeUtils.escapeXml10(value);
    }

    protected String escapeCsv(String value) {
        return StringEscapeUtils.escapeCsv(value);
    }
}
