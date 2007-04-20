/*
 * $Id: DateTimePicker.java 512580 2007-02-28 02:48:06Z musachy $
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
package org.apache.struts2.dojo.components;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.apache.struts2.components.UIBean;
import org.apache.struts2.views.annotations.StrutsTag;
import org.apache.struts2.views.annotations.StrutsTagAttribute;
import org.apache.struts2.views.annotations.StrutsTagSkipInheritance;

import com.opensymphony.xwork2.util.ValueStack;

/**
 * <!-- START SNIPPET: javadoc -->
 * <p>
 * Renders a date/time picker in a dropdown container.
 * </p>
 * <p>
 * A stand-alone DateTimePicker widget that makes it easy to select a date/time, or increment by week, month,
 * and/or year.
 * </p>
 *
 * <p>
 * It is possible to customize the user-visible formatting with either the
 * 'formatLength' (long, short, medium or full) or 'displayFormat' attributes. By defaulty current
 * locale will be used.</p>
 * </p>
 * 
 * Syntax supported by 'displayFormat' is (http://www.unicode.org/reports/tr35/tr35-4.html#Date_Format_Patterns):-
 * <table border="1">
 *   <tr>
 *      <td>Format</td>
 *      <td>Description</td>
 *   </tr>
 *   <tr>
 *      <td>d</td>
 *      <td>Day of the month</td>
 *   </tr>
 *   <tr>
 *      <td>D</td>
 *      <td>Day of year</td>
 *   </tr>
 *   <tr>
 *      <td>M</td>
 *      <td>Month - Use one or two for the numerical month, three for the abbreviation, or four for the full name, or 5 for the narrow name.</td>
 *   </tr>
 *   <tr>
 *      <td>h</td>
 *      <td>Hour [1-12].</td>
 *   </tr>
 *   <tr>
 *      <td>H</td>
 *      <td>Hour [0-23].</td>
 *   </tr>
 *   <tr>
 *      <td>m</td>
 *      <td>Minute. Use one or two for zero padding.</td>
 *   </tr>
 *   <tr>
 *      <td>s</td>
 *      <td>Second. Use one or two for zero padding.</td>
 *   </tr>
 * </table>
 * 
 * <p>
 * The value sent to the server is a locale-independent value, in a hidden field as defined 
 * by the name attribute. The value will be formatted conforming to RFC3 339 
 * (yyyy-MM-dd'T'HH:mm:ss)
 * </p>
 * <p>
 * The following formats(in order) will be used to parse the values of the attributes 'value', 
 * 'startDate' and 'endDate':
 * </p>
 * <ul>
 *   <li>RFC 3339
 *   <li>SimpleDateFormat.getTimeInstance(DateFormat.SHORT
 *   <li>SimpleDateFormat.getDateInstance(DateFormat.SHORT
 *   <li>SimpleDateFormat.getDateInstance(DateFormat.MEDIUM
 *   <li>SimpleDateFormat.getDateInstance(DateFormat.FULL
 *   <li>SimpleDateFormat.getDateInstance(DateFormat.LONG
 *   <li>'displayFormat' attribute value
 * </ul>
 * <!-- END SNIPPET: javadoc -->
 *
 * <b>Examples</b>
 *
 * <pre>
 * <!-- START SNIPPET: expl1 -->
 *
 * Example 1:
 *     &lt;s:datetimepicker name="order.date" label="Order Date" /&gt;
 * Example 2:
 *     &lt;s:datetimepicker name="delivery.date" label="Delivery Date" displayFormat="yyyy-MM-dd"  /&gt;
 * Example 3:    
 *      &lt;s:datetimepicker name="delivery.date" label="Delivery Date" value="%{date}"  /&gt;
 * <!-- END SNIPPET: expl1 -->
 * </pre>
 * <p/>
 *
 */
@StrutsTag(name="datetimepicker", tldTagClass="org.apache.struts2.dojo.views.jsp.ui.DateTimePickerTag", description="Render datetimepicker")
public class DateTimePicker extends UIBean {

    final public static String TEMPLATE = "datetimepicker";
    final private static SimpleDateFormat RFC3339_FORMAT = new SimpleDateFormat(
    "yyyy-MM-dd'T'HH:mm:ss");
    final protected static Log LOG = LogFactory.getLog(DateTimePicker.class);
    
    protected String iconPath;
    protected String formatLength;
    protected String displayFormat;
    protected String toggleType;
    protected String toggleDuration;
    protected String type;

    protected String displayWeeks;
    protected String adjustWeeks;
    protected String startDate;
    protected String endDate;
    protected String weekStartsOn;
    protected String staticDisplay;
    protected String dayWidth;
    protected String language;
    protected String templateCssPath;
    protected String valueNotifyTopics;

    public DateTimePicker(ValueStack stack, HttpServletRequest request, HttpServletResponse response) {
        super(stack, request, response);
    }

    protected String getDefaultTemplate() {
        return TEMPLATE;
    }

    public void evaluateParams() {
        super.evaluateParams();

        if(displayWeeks != null)
            addParameter("displayWeeks", findString(displayWeeks));
        if(adjustWeeks != null)
            addParameter("adjustWeeks", findValue(adjustWeeks, Boolean.class));
        if(startDate != null)
            addParameter("startDate", format(findString(startDate)));
        if(endDate != null)
            addParameter("endDate", format(findString(endDate)));
        if(weekStartsOn != null)
            addParameter("weekStartsOn", findString(weekStartsOn));
        if(staticDisplay != null)
            addParameter("staticDisplay", findValue(staticDisplay, Boolean.class));
        if(dayWidth != null)
            addParameter("dayWidth", findValue(dayWidth, Integer.class));
        if(language != null)
            addParameter("language", findString(language));
        if(value != null)
            addParameter("value", findString(value));
        if(iconPath != null)
            addParameter("iconPath", findString(iconPath));
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
        if(templateCssPath != null)
            addParameter("templateCssPath", findString(templateCssPath));
        if(valueNotifyTopics != null)
            addParameter("valueNotifyTopics", findString(valueNotifyTopics));
        
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
    
    @Override
    @StrutsTagSkipInheritance
    public void setTheme(String theme) {
        super.setTheme(theme);
    }

    @Override
    public String getTheme() {
        return "ajax";
    }
    
    @StrutsTagAttribute(description="If true, weekly size of calendar changes to acomodate the month if false," +
                " 42 day format is used", type="Boolean", defaultValue="false")
    public void setAdjustWeeks(String adjustWeeks) {
        this.adjustWeeks = adjustWeeks;
    }

    @StrutsTagAttribute(description="How to render the names of the days in the header(narrow, abbr or wide)", defaultValue="narrow")
    public void setDayWidth(String dayWidth) {
        this.dayWidth = dayWidth;
    }

    @StrutsTagAttribute(description="Total weeks to display", type="Integer", defaultValue="6")
    public void setDisplayWeeks(String displayWeeks) {
        this.displayWeeks = displayWeeks;
    }

    @StrutsTagAttribute(description="Last available date in the calendar set", type="Date", defaultValue="2941-10-12")
    public void setEndDate(String endDate) {
        this.endDate = endDate;
    }

    @StrutsTagAttribute(description="First available date in the calendar set", type="Date", defaultValue="1492-10-12")
    public void setStartDate(String startDate) {
        this.startDate = startDate;
    }

    @StrutsTagAttribute(description="Disable all incremental controls, must pick a date in the current display", type="Boolean", defaultValue="false")
    public void setStaticDisplay(String staticDisplay) {
        this.staticDisplay = staticDisplay;
    }

    @StrutsTagAttribute(description="Adjusts the first day of the week 0==Sunday..6==Saturday", type="Integer", defaultValue="0")
    public void setWeekStartsOn(String weekStartsOn) {
        this.weekStartsOn = weekStartsOn;
    }

    @StrutsTagAttribute(description="Language to display this widget in", defaultValue="brower's specified preferred language")
    public void setLanguage(String language) {
        this.language = language;
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

    @StrutsTagAttribute(description="Path to icon used for the dropdown")
    public void setIconPath(String iconPath) {
        this.iconPath = iconPath;
    }

    @StrutsTagAttribute(description="Duration of toggle in milliseconds", type="Integer", defaultValue="100")
    public void setToggleDuration(String toggleDuration) {
        this.toggleDuration = toggleDuration;
    }

    @StrutsTagAttribute(description="Defines the type of the picker on the dropdown. Possible values are 'date'" +
                " for a DateTimePicker, and 'time' for a timePicker", defaultValue="date")
    public void setType(String type) {
        this.type = type;
    }

    @StrutsTagAttribute(description="oggle type of the dropdown. Possible values are plain,wipe,explode,fade", defaultValue="plain")
    public void setToggleType(String toggleType) {
        this.toggleType = toggleType;
    }
    
    @StrutsTagAttribute(description="Template css path")
    public void setTemplateCssPath(String templateCssPath) {
        this.templateCssPath = templateCssPath;
    }
    
    @StrutsTagAttribute(description="Preset the value of input element")
    public void setValue(String arg0) {
        super.setValue(arg0);
    }
    
    @StrutsTagAttribute(description="Comma delimmited list of topics that will published when a value is selected")
    public void setValueNotifyTopics(String valueNotifyTopics) {
        this.valueNotifyTopics = valueNotifyTopics;
    }
    
    private String format(Object obj) {
        if(obj == null)
            return null;

        if(obj instanceof Date) {
            return RFC3339_FORMAT.format((Date) obj);
        } else {
            // try to parse a date
            String dateStr = obj.toString();
            if(dateStr.equalsIgnoreCase("today"))
                return RFC3339_FORMAT.format(new Date());

            
            Date date = null;
            //formats used to parse the date
            List<DateFormat> formats = new ArrayList<DateFormat>();
            formats.add(RFC3339_FORMAT);
            formats.add(SimpleDateFormat.getTimeInstance(DateFormat.SHORT));
            formats.add(SimpleDateFormat.getDateInstance(DateFormat.SHORT));
            formats.add(SimpleDateFormat.getDateInstance(DateFormat.MEDIUM));
            formats.add(SimpleDateFormat.getDateInstance(DateFormat.FULL));
            formats.add(SimpleDateFormat.getDateInstance(DateFormat.LONG));
            if (this.displayFormat != null) {
                SimpleDateFormat displayFormat = new SimpleDateFormat(
                        (String) getParameters().get("displayFormat"));
                formats.add(displayFormat);
            }
            
            for (DateFormat format : formats) {
                try {
                    date = format.parse(dateStr);
                    if (date != null)
                        return RFC3339_FORMAT.format(date);
                } catch (Exception e) {
                    //keep going
                }
            }
            
           // last resource, assume already in correct/default format
           if (LOG.isDebugEnabled())
               LOG.debug("Unable to parse date " + dateStr);
           return dateStr;
        }
    }

}
