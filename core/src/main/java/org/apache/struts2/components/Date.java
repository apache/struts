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

import com.opensymphony.xwork2.ActionContext;
import com.opensymphony.xwork2.TextProvider;
import com.opensymphony.xwork2.util.ValueStack;
import com.opensymphony.xwork2.util.logging.Logger;
import com.opensymphony.xwork2.util.logging.LoggerFactory;
import org.apache.struts2.views.annotations.StrutsTag;
import org.apache.struts2.views.annotations.StrutsTagAttribute;

import java.io.IOException;
import java.io.Writer;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Iterator;
import java.util.List;
import java.util.TimeZone;

/**
 * <!-- START SNIPPET: javadoc -->
 *
 * Format Date object in different ways.
 * <p>
 * The date tag will allow you to format a Date in a quick and easy way.
 * You can specify a <b>custom format</b> (eg. "dd/MM/yyyy hh:mm"), you can generate
 * <b>easy readable notations</b> (like "in 2 hours, 14 minutes"), or you can just fall back
 * on a <b>predefined format</b> with key 'struts.date.format' in your properties file.
 *
 * If that key is not defined, it will finally fall back to the default DateFormat.MEDIUM
 * formatting.
 *
 * <b>Note</b>: If the requested Date object isn't found on the stack, a blank will be returned.
 * </p>
 *
 * Configurable attributes are :-
 * <ul>
 *    <li>name</li>
 *    <li>nice</li>
 *    <li>format</li>
 * </ul>
 *
 * <p/>
 *
 * Following how the date component will work, depending on the value of nice attribute
 * (which by default is false) and the format attribute.
 *
 * <p/>
 *
 * <b><u>Condition 1: With nice attribute as true</u></b>
 * <table border="1">
 *   <tr>
 *      <td>i18n key</td>
 *      <td>default</td>
 *   </tr>
 *   <tr>
 *      <td>struts.date.format.past</td>
 *      <td>{0} ago</td>
 *   </tr>
 *   <tr>
 *      <td>struts.date.format.future</td>
 *      <td>in {0}</td>
 *   </tr>
 *   <tr>
 *      <td>struts.date.format.seconds</td>
 *      <td>an instant</td>
 *   </tr>
 *   <tr>
 *      <td>struts.date.format.minutes</td>
 *      <td>{0,choice,1#one minute|1<{0} minutes}</td>
 *   </tr>
 *   <tr>
 *      <td>struts.date.format.hours</td>
 *      <td>{0,choice,1#one hour|1<{0} hours}{1,choice,0#|1#, one minute|1<, {1} minutes}</td>
 *   </tr>
 *   <tr>
 *      <td>struts.date.format.days</td>
 *      <td>{0,choice,1#one day|1<{0} days}{1,choice,0#|1#, one hour|1<, {1} hours}</td>
 *   </tr>
 *   <tr>
 *      <td>struts.date.format.years</td>
 *      <td>{0,choice,1#one year|1<{0} years}{1,choice,0#|1#, one day|1<, {1} days}</td>
 *   </tr>
 * </table>
 *
 * <p/>
 *
 * <b><u>Condition 2: With nice attribute as false and format attribute is specified eg. dd/MM/yyyyy </u></b>
 * <p>In this case the format attribute will be used.</p>
 *
 * <p/>
 *
 * <b><u>Condition 3: With nice attribute as false and no format attribute is specified </u></b>
 * <table border="1">
 *    <tr>
 *      <td>i18n key</td>
 *      <td>default</td>
 *   </tr>
 *   <tr>
 *      <td>struts.date.format</td>
 *      <td>if one is not found DateFormat.MEDIUM format will be used</td>
 *   </tr>
 * </table>
 *
 *
 * <!-- END SNIPPET: javadoc -->
 *
 * <p/> <b>Examples</b>
 * <pre>
 *  <!-- START SNIPPET: example -->
 *  &lt;s:date name="person.birthday" format="dd/MM/yyyy" /&gt;
 *  &lt;s:date name="person.birthday" format="%{getText('some.i18n.key')}" /&gt;
 *  &lt;s:date name="person.birthday" nice="true" /&gt;
 *  &lt;s:date name="person.birthday" /&gt;
 *  <!-- END SNIPPET: example -->
 * </pre>
 *
 * <code>Date</code>
 *
 */
@StrutsTag(name="date", tldBodyContent="empty", tldTagClass="org.apache.struts2.views.jsp.DateTag", description="Render a formatted date.")
public class Date extends ContextBean {

    private static final Logger LOG = LoggerFactory.getLogger(Date.class);
    /**
     * Property name to fall back when no format is specified
     */
    public static final String DATETAG_PROPERTY = "struts.date.format";
    /**
     * Property name that defines the past notation (default: {0} ago)
     */
    public static final String DATETAG_PROPERTY_PAST = "struts.date.format.past";
    private static final String DATETAG_DEFAULT_PAST = "{0} ago";
    /**
     * Property name that defines the future notation (default: in {0})
     */
    public static final String DATETAG_PROPERTY_FUTURE = "struts.date.format.future";
    private static final String DATETAG_DEFAULT_FUTURE = "in {0}";
    /**
     * Property name that defines the seconds notation (default: in instant)
     */
    public static final String DATETAG_PROPERTY_SECONDS = "struts.date.format.seconds";
    private static final String DATETAG_DEFAULT_SECONDS = "an instant";
    /**
     * Property name that defines the minutes notation (default: {0,choice,1#one minute|1<{0} minutes})
     */
    public static final String DATETAG_PROPERTY_MINUTES = "struts.date.format.minutes";
    private static final String DATETAG_DEFAULT_MINUTES = "{0,choice,1#one minute|1<{0} minutes}";
    /**
     * Property name that defines the hours notation (default: {0,choice,1#one hour|1<{0} hours}{1,choice,0#|1#, one
     * minute|1<, {1} minutes})
     */
    public static final String DATETAG_PROPERTY_HOURS = "struts.date.format.hours";
    private static final String DATETAG_DEFAULT_HOURS = "{0,choice,1#one hour|1<{0} hours}{1,choice,0#|1#, one minute|1<, {1} minutes}";
    /**
     * Property name that defines the days notation (default: {0,choice,1#one day|1<{0} days}{1,choice,0#|1#, one hour|1<,
     * {1} hours})
     */
    public static final String DATETAG_PROPERTY_DAYS = "struts.date.format.days";
    private static final String DATETAG_DEFAULT_DAYS = "{0,choice,1#one day|1<{0} days}{1,choice,0#|1#, one hour|1<, {1} hours}";
    /**
     * Property name that defines the years notation (default: {0,choice,1#one year|1<{0} years}{1,choice,0#|1#, one
     * day|1<, {1} days})
     */
    public static final String DATETAG_PROPERTY_YEARS = "struts.date.format.years";
    private static final String DATETAG_DEFAULT_YEARS = "{0,choice,1#one year|1<{0} years}{1,choice,0#|1#, one day|1<, {1} days}";

    private String name;

    private String format;

    private boolean nice;

    private String timezone;

    public Date(ValueStack stack) {
        super(stack);
    }

    private TextProvider findProviderInStack() {
        for (Iterator iterator = getStack().getRoot().iterator(); iterator
                .hasNext();) {
            Object o = iterator.next();

            if (o instanceof TextProvider) {
                return (TextProvider) o;
            }
        }
        return null;
    }

    /**
     * Calculates the difference in time from now to the given date, and outputs it nicely. <p/> An example: <br/>Now =
     * 2006/03/12 13:38:00, date = 2006/03/12 15:50:00 will output "in 1 hour, 12 minutes".
     *
     * @param tp   text provider
     * @param date the date
     * @return the date nicely
     */
    public String formatTime(TextProvider tp, java.util.Date date) {
        java.util.Date now = new java.util.Date();
        StringBuilder sb = new StringBuilder();
        List args = new ArrayList();
        long secs = Math.abs((now.getTime() - date.getTime()) / 1000);
        long mins = secs / 60;
        long sec = secs % 60;
        int min = (int) mins % 60;
        long hours = mins / 60;
        int hour = (int) hours % 24;
        int days = (int) hours / 24;
        int day = days % 365;
        int years = days / 365;

        if (years > 0) {
            args.add(Long.valueOf(years));
            args.add(Long.valueOf(day));
            args.add(sb);
            args.add(null);
            sb.append(tp.getText(DATETAG_PROPERTY_YEARS, DATETAG_DEFAULT_YEARS, args));
        } else if (day > 0) {
            args.add(Long.valueOf(day));
            args.add(Long.valueOf(hour));
            args.add(sb);
            args.add(null);
            sb.append(tp.getText(DATETAG_PROPERTY_DAYS, DATETAG_DEFAULT_DAYS, args));
        } else if (hour > 0) {
            args.add(Long.valueOf(hour));
            args.add(Long.valueOf(min));
            args.add(sb);
            args.add(null);
            sb.append(tp.getText(DATETAG_PROPERTY_HOURS, DATETAG_DEFAULT_HOURS, args));
        } else if (min > 0) {
            args.add(Long.valueOf(min));
            args.add(Long.valueOf(sec));
            args.add(sb);
            args.add(null);
            sb.append(tp.getText(DATETAG_PROPERTY_MINUTES, DATETAG_DEFAULT_MINUTES, args));
        } else {
            args.add(Long.valueOf(sec));
            args.add(sb);
            args.add(null);
            sb.append(tp.getText(DATETAG_PROPERTY_SECONDS, DATETAG_DEFAULT_SECONDS, args));
        }

        args.clear();
        args.add(sb.toString());
        if (date.before(now)) {
            // looks like this date is passed
            return tp.getText(DATETAG_PROPERTY_PAST, DATETAG_DEFAULT_PAST, args);
        } else {
            return tp.getText(DATETAG_PROPERTY_FUTURE, DATETAG_DEFAULT_FUTURE, args);
        }
    }

    public boolean end(Writer writer, String body) {
        String msg;
        java.util.Date date = null;
        // find the name on the valueStack
        try {
            //suport Calendar also
            Object dateObject = findValue(name);
            if (dateObject instanceof java.util.Date) {
                date = (java.util.Date) dateObject;
            } else if(dateObject instanceof Calendar){
                date = ((Calendar) dateObject).getTime();
            } else {
                if (devMode) {
                    LOG.error("Expression [#0] passed to <s:date/> tag which was evaluated to [#1](#2) isn't instance of java.util.Date nor java.util.Calendar!",
                            name, dateObject, (dateObject != null ? dateObject.getClass() : "null"));
                } else {
                    LOG.debug("Expression [#0] passed to <s:date/> tag which was evaluated to [#1](#2) isn't instance of java.util.Date nor java.util.Calendar!",
                            name, dateObject, (dateObject != null ? dateObject.getClass() : "null"));
                }
            }
        } catch (Exception e) {
            LOG.error("Could not convert object with key '#0' to a java.util.Date instance", name);
        }

        //try to find the format on the stack
        if (format != null) {
            format = findString(format);
        }
        if (date != null) {
            TextProvider tp = findProviderInStack();
            if (tp != null) {
                if (nice) {
                    msg = formatTime(tp, date);
                } else {
                    TimeZone tz = getTimeZone();
                    if (format == null) {
                        String globalFormat = null;

                        // if the format is not specified, fall back using the
                        // defined property DATETAG_PROPERTY
                        globalFormat = tp.getText(DATETAG_PROPERTY);

                        // if tp.getText can not find the property then the
                        // returned string is the same as input =
                        // DATETAG_PROPERTY
                        if (globalFormat != null
                                && !DATETAG_PROPERTY.equals(globalFormat)) {
                            SimpleDateFormat sdf = new SimpleDateFormat(globalFormat,
                                    ActionContext.getContext().getLocale());
                            sdf.setTimeZone(tz);
                            msg = sdf.format(date);
                        } else {
                            DateFormat df = DateFormat.getDateTimeInstance(
                                    DateFormat.MEDIUM, DateFormat.MEDIUM,
                                    ActionContext.getContext().getLocale());
                            df.setTimeZone(tz);
                            msg = df.format(date);
                        }
                    } else {
                        SimpleDateFormat sdf = new SimpleDateFormat(format, ActionContext
                                .getContext().getLocale());
                        sdf.setTimeZone(tz);
                        msg = sdf.format(date);
                    }
                }
                if (msg != null) {
                    try {
                        if (getVar() == null) {
                            writer.write(msg);
                        } else {
                            putInContext(msg);
                        }
                    } catch (IOException e) {
                        LOG.error("Could not write out Date tag", e);
                    }
                }
            }
        }
        return super.end(writer, "");
    }

    private TimeZone getTimeZone() {
        TimeZone tz = TimeZone.getDefault();
        if (timezone != null) {
            timezone = stripExpressionIfAltSyntax(timezone);
            String actualTimezone = (String) getStack().findValue(timezone, String.class);
            if (actualTimezone != null) {
                timezone = actualTimezone;
            }
            tz = TimeZone.getTimeZone(timezone);
        }
        return tz;
    }

    @StrutsTagAttribute(description="Date or DateTime format pattern", rtexprvalue=false)
    public void setFormat(String format) {
        this.format = format;
    }

    @StrutsTagAttribute(description="Whether to print out the date nicely", type="Boolean", defaultValue="false")
    public void setNice(boolean nice) {
        this.nice = nice;
    }

    /**
     * @return Returns the name.
     */
    public String getName() {
        return name;
    }

    @StrutsTagAttribute(description="The date value to format", required=true)
    public void setName(String name) {
        this.name = name;
    }

    /**
     * @return Returns the format.
     */
    public String getFormat() {
        return format;
    }

    /**
     * @return Returns the nice.
     */
    public boolean isNice() {
        return nice;
    }

    /**
     * @return Returns the name.
     */
    public String getTimezone() {
        return timezone;
    }

    @StrutsTagAttribute(description = "The specific timezone in which to format the date", required = false)
    public void setTimezone(String timezone) {
        this.timezone = timezone;
    }

}
