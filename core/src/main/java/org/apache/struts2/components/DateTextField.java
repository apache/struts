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
package org.apache.struts2.components;

import org.apache.struts2.util.ValueStack;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.apache.struts2.views.annotations.StrutsTag;
import org.apache.struts2.views.annotations.StrutsTagAttribute;

import java.time.Instant;
import java.time.ZoneId;
import java.util.Date;

import static java.time.format.DateTimeFormatter.ISO_LOCAL_DATE;

@StrutsTag(
    name="datetextfield",
    tldTagClass="org.apache.struts2.views.jsp.ui.DateTextFieldTag",
    description="Render an HTML input for a date: a native input type=\"date\" in the html5 theme,"
        + " split input fields with the JavaTemplates plugin",
    allowDynamicAttributes=true)
public class DateTextField extends UIBean {
    /**
     * The name of the default template for the DateTextFieldTag
     */
    final public static String TEMPLATE = "datetextfield";

    protected String format;

    public DateTextField(ValueStack stack, HttpServletRequest request, HttpServletResponse response) {
        super(stack, request, response);
    }

    protected String getDefaultTemplate() {
        return TEMPLATE;
    }

    protected void evaluateExtraParams() {
        super.evaluateExtraParams();

        if (format != null) {
            addParameter("format", findValue(format, String.class));
        }

        if (getAttributes().get(ATTR_NAME_VALUE) instanceof Date date) {
            addParameter("isoDate", ISO_LOCAL_DATE.format(Instant.ofEpochMilli(date.getTime()).atZone(ZoneId.systemDefault())));
        }
    }

    @Override
    protected HtmlControlType getControlType() {
        return HtmlControlType.DATE;
    }

    @StrutsTagAttribute(description="Date format used to lay out the split input fields of the JavaTemplates plugin;"
        + " ignored by the html5 theme, which always uses yyyy-MM-dd")
    public void setFormat(String format) {
        this.format = format;
    }

	@Override
	protected Class getValueClassType() {
		return null;
	}

}
