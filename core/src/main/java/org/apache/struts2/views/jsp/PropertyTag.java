/*
 * $Id$
 *
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

package org.apache.struts2.views.jsp;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.struts2.components.Component;
import org.apache.struts2.components.Property;

import com.opensymphony.xwork2.util.ValueStack;


/**
 * @see Property
 */
public class PropertyTag extends ComponentTagSupport {

    private static final long serialVersionUID = 435308349113743852L;

    private String defaultValue;
    private String value;
    private boolean escapeHtml = true;
    private boolean escapeJavaScript = false;
    private boolean escapeXml = false;
    private boolean escapeCsv = false;

    public Component getBean(ValueStack stack, HttpServletRequest req, HttpServletResponse res) {
        return new Property(stack);
    }

    protected void populateParams() {
        super.populateParams();

        Property tag = (Property) component;
        tag.setDefault(defaultValue);
        tag.setValue(value);
        tag.setEscape(escapeHtml);
        tag.setEscapeJavaScript(escapeJavaScript);
        tag.setEscapeXml(escapeXml);
        tag.setEscapeCsv(escapeCsv);
    }

    public void setDefault(String defaultValue) {
        this.defaultValue = defaultValue;
    }

    public void setEscape(boolean escape) {
        this.escapeHtml = escape;
    }

    public void setEscapeHtml(boolean escapeHtml) {
        this.escapeHtml = escapeHtml;
    }

    public void setEscapeJavaScript(boolean escapeJavaScript) {
        this.escapeJavaScript = escapeJavaScript;
    }
    
    public void setValue(String value) {
        this.value = value;
    }

    public void setDefaultValue(String defaultValue) {
        this.defaultValue = defaultValue;
    }

    public void setEscapeCsv(boolean escapeCsv) {
        this.escapeCsv = escapeCsv;
    }

    public void setEscapeXml(boolean escapeXml) {
        this.escapeXml = escapeXml;
    }
}
