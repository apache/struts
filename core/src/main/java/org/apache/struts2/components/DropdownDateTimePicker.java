/*
 * $Id$
 *
 * Copyright 2006 The Apache Software Foundation.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.apache.struts2.components;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import com.opensymphony.xwork2.util.ValueStack;

/**
 * <!-- START SNIPPET: javadoc -->
 * <p>
 * Renders picker(datepicker or timepicker) in a dropdown container.
 * </p>
 * <p>
 * It is possible to customize the user-visible formatting
 * with either the formatLength or displayFormat attributes.  The value sent to the server is
 * typically a locale-independent value in a hidden field as defined by the name attribute.
 * RFC3339 representation is used by default, but other options are available with saveFormat
 * </p>
 *
 * @s.tag name="dropdowndatetimepicker" tld-body-content="JSP"
 *        tld-tag-class="org.apache.struts2.views.jsp.ui.DropdownDateTimePickerTag" description="Render
 *        a dropdown picker(datepicker or timepicker)"
 */
public class DropdownDateTimePicker extends DatePicker {
    final public static String TEMPLATE = "dropdowndatetimepicker";

    protected String iconPath;
    protected String formatLength;
    protected String displayFormat;
    protected String saveFormat;
    protected String toggleType;
    protected String toggleDuration;
    protected String type;

    public DropdownDateTimePicker(ValueStack stack, HttpServletRequest request, HttpServletResponse response) {
        super(stack, request, response);
    }

    protected String getDefaultTemplate() {
        return TEMPLATE;
    }

    public void evaluateParams() {
        super.evaluateParams();

        if(iconPath != null)
            addParameter("iconPath", iconPath);
        if(formatLength != null)
            addParameter("formatLength", findString(formatLength));
        if(displayFormat != null)
            addParameter("displayFormat", findString(displayFormat));
        if(saveFormat != null)
            addParameter("saveFormat", findString(saveFormat));
        if(toggleType != null)
            addParameter("toggleType", findString(toggleType));
        if(toggleDuration != null)
            addParameter("toggleDuration", findValue(toggleDuration, Integer.class));
        if(type != null)
            addParameter("type", findString(type));
        else
            addParameter("type", "date");
    }

    /**
     * A pattern used for the visual display of the formatted date, e.g. dd/MM/yyyy.
     *
     * @s.tagattribute required="false" type="String"
     */
    public void setDisplayFormat(String displayFormat) {
        this.displayFormat = displayFormat;
    }

    /**
     * Type of formatting used for visual display, appropriate to locale (choice of long, short, medium or full)
     *
     * @s.tagattribute required="false" type="String"
     */
    public void setFormatLength(String formatLength) {
        this.formatLength = formatLength;
    }

    /**
     * Path to icon used for the dropdown
     *
     * @s.tagattribute required="false" type="String"
     */
    public void setIconPath(String iconPath) {
        this.iconPath = iconPath;
    }

    /**
     * Formatting scheme used when submitting the form element.
     * Possible values are rfc,iso,posix and unix
     * @s.tagattribute required="false" type="String" default="rfc"
     */
    public void setSaveFormat(String saveFormat) {
        this.saveFormat = saveFormat;
    }

    /**
     * Duration of toggle in seconds
     *
     * @s.tagattribute required="false" type="Integer" default="100"
     */
    public void setToggleDuration(String toggleDuration) {
        this.toggleDuration = toggleDuration;
    }

    /**
     * Defines the type of the picker on the dropdown.
     * Possible values are "date" for a DatePicker, and "time" for a timePicker
     *
     * @s.tagattribute required="false" type="String" default="date"
     */
    public void setType(String type) {
        this.type = type;
    }

    /**
     * Toggle type of the dropdown.
     * Possible values are plain,wipe,explode,fade
     * @s.tagattribute required="false" type="String" default="plain"
     */
    public void setToggleType(String toggleType) {
        this.toggleType = toggleType;
    }
}
