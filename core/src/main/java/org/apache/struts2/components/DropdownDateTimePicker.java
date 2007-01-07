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

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.apache.struts.annotations.StrutsTag;
import org.apache.struts.annotations.StrutsTagAttribute;

import com.opensymphony.xwork2.util.ValueStack;

/**
 * <!-- START SNIPPET: javadoc -->
 * <p>
 * Renders picker(datepicker or timepicker) in a dropdown container.
 * </p>
 * <p>
 * It is possible to customize the user-visible formatting with either the
 * formatLength or displayFormat attributes. The value sent to the server is
 * typically a locale-independent value in a hidden field as defined by the name
 * attribute. RFC3339 representation is used by default, but other options are
 * available with saveFormat
 * </p>
 *
 */
@StrutsTag(name="dropdowndatetimepicker", tldTagClass="org.apache.struts2.views.jsp.ui.DropdownDateTimePickerTag",
    description="Renders a dropdown picker(datepicker or timepicker)")
public class DropdownDateTimePicker extends DatePicker {
    final public static String TEMPLATE = "dropdowndatetimepicker";
    final private static SimpleDateFormat RFC3399_FORMAT = new SimpleDateFormat(
            "yyyy-MM-dd'T'HH:mm:ss");

    protected String iconPath;
    protected String formatLength;
    protected String displayFormat;
    protected String toggleType;
    protected String toggleDuration;
    protected String type;

    protected static Log log = LogFactory.getLog(DropdownDateTimePicker.class);

    public DropdownDateTimePicker(ValueStack stack, HttpServletRequest request,
            HttpServletResponse response) {
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
        if(toggleType != null)
            addParameter("toggleType", findString(toggleType));
        if(toggleDuration != null)
            addParameter("toggleDuration", findValue(toggleDuration,
                    Integer.class));
        if(type != null)
            addParameter("type", findString(type));
        else
            addParameter("type", "date");

        // format the value to RFC 3399
        if(parameters.containsKey("value")) {
            parameters.put("nameValue", format(parameters.get("value")));
        } else {
            if(name != null) {
                String expr = name;
                if(altSyntax()) {
                    expr = "%{" + expr + "}";
                }
                addParameter("nameValue", format(findValue(expr)));
            }
        }

    }


    private String format(Object obj) {
        if(obj == null)
            return null;

        if(obj instanceof Date) {
            return RFC3399_FORMAT.format((Date) obj);
        } else {
            // try to parse a date
            String dateStr = obj.toString();
            if(dateStr.equalsIgnoreCase("today"))
                return  RFC3399_FORMAT.format(new Date());

            try {
                Date date = null;
                if(this.displayFormat != null) {
                    SimpleDateFormat format = new SimpleDateFormat(
                            this.displayFormat);
                    date = format.parse(dateStr);
                } else {
                    // last resource
                    date = SimpleDateFormat.getInstance().parse(dateStr);
                }
                return RFC3399_FORMAT.format(date);
            } catch (ParseException e) {
                log.error("Could not parse date", e);
                return dateStr;
            }
        }
    }

    @StrutsTagAttribute(description="A pattern used for the visual display of the formatted date, e.g. dd/MM/yyyy")
    public void setDisplayFormat(String displayFormat) {
        this.displayFormat = displayFormat;
    }

    @StrutsTagAttribute(description="Type of formatting used for visual display. Possible values are " +
                "long, short, medium or full", defaultValue="short")
    public void setFormatLength(String formatLength) {
        this.formatLength = formatLength;
    }

    @StrutsTagAttribute(description=" Path to icon used for the dropdown")
    public void setIconPath(String iconPath) {
        this.iconPath = iconPath;
    }

    @StrutsTagAttribute(description="Duration of toggle in milliseconds", type="Integer", defaultValue="100")
    public void setToggleDuration(String toggleDuration) {
        this.toggleDuration = toggleDuration;
    }

    @StrutsTagAttribute(description="Defines the type of the picker on the dropdown. Possible values are 'date'" +
                " for a DatePicker, and 'time' for a timePicker", defaultValue="date")
    public void setType(String type) {
        this.type = type;
    }

    @StrutsTagAttribute(description="oggle type of the dropdown. Possible values are plain,wipe,explode,fade", defaultValue="plain")
    public void setToggleType(String toggleType) {
        this.toggleType = toggleType;
    }
}
