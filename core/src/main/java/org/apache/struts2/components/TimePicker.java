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
package org.apache.struts2.components;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import com.opensymphony.xwork2.util.ValueStack;

/**
 * <!-- START SNIPPET: javadoc -->
 * Renders timepicker element.</p>
 * Format supported by this component are:-
 * <table border="1">
 *   <tr>
 *      <td>Format</td>
 *      <td>Description</td>
 *   </tr>
 *   <tr>
 *      <td>#HH</td>
 *      <td>Display hour in two digit format</td>
 *   </tr>
 *   <tr>
 *      <td>#H</td>
 *      <td>Try to display hour in one digit format, if cannot use 2 digits</td>
 *   </tr>
 *   <tr>
 *      <td>#hh</td>
 *      <td>Display hour in two digit format</td>
 *   </tr>
 *   <tr>
 *      <td>#h</td>
 *      <td>Try to display hour in one digit format, if cannot use 2 digits</td>
 *   </tr>
 *   <tr>
 *      <td>#mm</td>
 *      <td>Display minutes in 2 digits format</td>
 *   </tr>
 *   <tr>
 *      <td>#m</td>
 *      <td>Try to display minutes in 2 digits fomrat, if cannot use 2 digits</td>
 *   </tr>
 * </table>
 *
 * <!-- END SNIPPET: javadoc -->
 *
 *
 * <pre>
 * <!-- START SNIPPET: example -->
 *
 * &lt;s:timepicker label="Show Time" name="showTime" value="05:00" format="#hh:#mm" /&gt;
 *
 * &lt;s:timepicker label="Dinner Time" name="dinnerTime" format="#hh-#mm" /&gt;
 *
 * <!-- END SNIPPET: example -->
 * </pre>
 *
 * @version $Date$ $Id$
 */
public class TimePicker extends TextField {

    final public static String TEMPLATE = "timepicker";

    protected String format;
    protected String templatePath;
    protected String templateCssPath;
    protected String timeIconPath;
    protected String size;

    public TimePicker(ValueStack stack, HttpServletRequest request, HttpServletResponse response) {
        super(stack, request, response);
    }

    protected void evaluateExtraParams() {
        super.evaluateExtraParams();

        if (format != null) {
            addParameter("format", findString(format));
        }
        if (timeIconPath != null) {
            addParameter("timeIconPath", timeIconPath);
        }
        if (templatePath != null) {
            addParameter("templatePath", templatePath);
        }
        if (templateCssPath != null) {
            addParameter("templateCssPath", templateCssPath);
        }
        if (size != null) {
            addParameter("size", findValue(size, Integer.class));
        }
    }

    protected String getDefaultTemplate() {
        return TEMPLATE;
    }

    /**
     * The format to use for time field.
     * @s.tagattribute required="false" type="String" default="Dateformat specified by language preset (%Y/%m/%d for en)"
     */
    public void setFormat(String format) {
        this.format = format;
    }

    /**
     * The time picker icon path
     * @s.tagattribute required="false" type="String" default="/struts/dojo/struts/widgets/dateIcon.gif"
     */
    public void setTimeIconPath(String timeIconPath) {
        this.timeIconPath = timeIconPath;
    }

    /**
     * The time picker template path.
     * @s.tagattribute required="false" type="String"
     */
    public void setTemplatePath(String templatePath) {
        this.templatePath = templatePath;
    }

    /**
     * The time picker template css path.
     * @s.tagattribute required="false" type="String"
     */
    public void setTemplateCssPath(String templateCssPath) {
        this.templateCssPath = templateCssPath;
    }

    /**
     * The time picker text field size.
     * @s.tagattribute required="false" type="String"
     */
    public void setSize(String size) {
        this.size = size;
    }

}
