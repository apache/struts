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

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.struts2.views.annotations.StrutsTag;
import org.apache.struts2.views.annotations.StrutsTagAttribute;

import com.opensymphony.xwork2.util.ValueStack;

@StrutsTag(
    name="datetextfield",
    tldTagClass="org.apache.struts2.views.jsp.ui.DateTextFieldTag",
    description="Render an HTML input fields with the date time",
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
    }

    @StrutsTagAttribute(description="Date format attribute", required=true, type="String")
    public void setFormat(String format) {
        this.format = format;
    }

	@SuppressWarnings("unchecked")
	@Override
	protected Class getValueClassType() {
		return null;
	}
    
}
