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
 * <p>
 * Renders datepicker element.
 * </p>
 * <p>
 * A stand-alone DatePicker widget that makes it easy to select a date, or increment by week, month,
 * and/or year.
 * </p>
 * Dates attributes passed in the `RFC 3339` format:
 *
 * Renders datepicker element.</p>
 * Format supported by this component are:-
 * <table border="1">
 *   <tr>
 *      <td>Format</td>
 *      <td>Description</td>
 *   </tr>
 *   <tr>
 *      <td>#dd</td>
 *      <td>Display day in two digits format</td>
 *   </tr>
 *   <tr>
 *      <td>#d</td>
 *      <td>Try to display day in one digit format, if cannot use 2 digit format</td>
 *   </tr>
 *   <tr>
 *      <td>#MM</td>
 *      <td>Display month in two digits format</td>
 *   </tr>
 *   <tr>
 *      <td>#M</td>
 *      <td>Try to display month in one digits format, if cannot use 2 digit format</td>
 *   </tr>
 *   <tr>
 *      <td>#yyyy</td>
 *      <td>Display year in four digits format</td>
 *   </tr>
 *   <tr>
 *      <td>#yy</td>
 *      <td>Display the last two digits of the yaer</td>
 *   </tr>
 *   <tr>
 *      <td>#y</td>
 *      <td>Display the last digits of the year</td>
 *   </tr>
 * </table>
 *
 * <p/>
 *
 * <!-- END SNIPPET: javadoc -->
 *
 * <b>Examples</b>
 *
 * <pre>
 * <!-- START SNIPPET: expl1 -->
 *
 * Example 1:
 *     &lt;s:datepicker name="order.date" label="Order Date" /&gt;
 * Example 2:
 *     &lt;s:datepicker name="delivery.date" label="Delivery Date" format="#yyyy-#MM-#dd"  /&gt;
 *
 * <!-- END SNIPPET: expl1 -->
 * </pre>
 * <p/>
 *
 * <!-- START SNIPPET: expldesc2 -->
 *
 * The css could be changed by using the following :-
 *
 * <!-- END SNIPPET: expldesc2 -->
 *
 * <pre>
 * <!-- START SNIPPET: expl2 -->
 *
 * &lt;s:datepicker name="birthday" label="Birthday" templateCss="...." /&gt;
 *
 * <!-- END SNIPPET: expl2 -->
 * </pre>
 *
 * @s.tag name="datepicker" tld-body-content="JSP" tld-tag-class="org.apache.struts2.views.jsp.ui.DatePickerTag"
 * description="Render datepicker"
 */
public class DatePicker extends UIBean {

    final public static String TEMPLATE = "datepicker";

    protected String displayWeeks;
    protected String adjustWeeks;
    protected String startDate;
    protected String endDate;
    protected String weekStartsOn;
    protected String staticDisplay;
    protected String dayWidth;
    protected String language;

    public DatePicker(ValueStack stack, HttpServletRequest request, HttpServletResponse response) {
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
            addParameter("startDate", findString(startDate));
        if(endDate != null)
            addParameter("endDate", findString(endDate));
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
    }

    /**
     * If true, weekly size of calendar changes to acomodate the month if false, 42 day format is
     * used
     *
     * @s.tagattribute required="false" type="Boolean" default="false"
     */
    public void setAdjustWeeks(String adjustWeeks) {
        this.adjustWeeks = adjustWeeks;
    }

    /**
     * How to render the names of the days in the header(narrow, abbr or wide)
     *
     * @s.tagattribute required="false" type="String" default="narrow"
     */
    public void setDayWidth(String dayWidth) {
        this.dayWidth = dayWidth;
    }

    /**
     * Total weeks to display
     *
     * @s.tagattribute required="false" type="Integer" default="6"
     */
    public void setDisplayWeeks(String displayWeeks) {
        this.displayWeeks = displayWeeks;
    }

    /**
     * Last available date in the calendar set
     *
     * @s.tagattribute required="false" type="Date" default="2941-10-12"
     */
    public void setEndDate(String endDate) {
        this.endDate = endDate;
    }

    /**
     * First available date in the calendar set
     *
     * @s.tagattribute required="false" type="Date" default="1492-10-12"
     */
    public void setStartDate(String startDate) {
        this.startDate = startDate;
    }

    /**
     * Disable all incremental controls, must pick a date in the current display
     *
     * @s.tagattribute required="false" type="Boolean" default="false"
     */
    public void setStaticDisplay(String staticDisplay) {
        this.staticDisplay = staticDisplay;
    }

    /**
     * Adjusts the first day of the week 0==Sunday..6==Saturday
     *
     * @s.tagattribute required="false" type="Integer" default="0"
     */
    public void setWeekStartsOn(String weekStartsOn) {
        this.weekStartsOn = weekStartsOn;
    }

    /**
     * Language to display this widget in (like en-us).
     *
     * @s.tagattribute required="false" type="String" default="brower's specified preferred language"
     */
    public void setLanguage(String language) {
        this.language = language;
    }

}
