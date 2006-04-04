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
package org.apache.struts.action2.components;

import com.opensymphony.xwork.ActionContext;
import com.opensymphony.xwork.TextProvider;
import com.opensymphony.xwork.util.OgnlValueStack;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import java.io.IOException;
import java.io.Writer;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

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
 *   	<td>struts.date.format.past</td>
 *   	<td>{0} ago</td>
 *   </tr>
 *   <tr>
 *   	<td>struts.date.format.future</td>
 *      <td>in {0}</td>
 *   </tr>
 *   <tr>
 *   	<td>struts.date.format.seconds</td>
 *      <td>an instant</td>
 *   </tr>
 *   <tr>
 *   	<td>struts.date.format.minutes</td>
 *      <td>{0,choice,1#one minute|1<{0} minutes}</td>
 *   </tr>
 *   <tr>
 *   	<td>struts.date.format.hours</td>
 *   	<td>{0,choice,1#one hour|1<{0} hours}{1,choice,0#|1#, one minute|1<, {1} minutes}</td>
 *   </tr>
 *   <tr>
 *   	<td>struts.date.format.days</td>
 *   	<td>{0,choice,1#one day|1<{0} days}{1,choice,0#|1#, one hour|1<, {1} hours}</td>
 *   </tr>
 *   <tr>
 *   	<td>struts.date.format.years</td>
 *   	<td>{0,choice,1#one year|1<{0} years}{1,choice,0#|1#, one day|1<, {1} days}</td>
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
 *   	<td>struts.date.format</td>
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
 *  &lt;a:date name="person.birthday" format="dd/MM/yyyy" /&gt;
 *  &lt;a:date name="person.birthday" format="%{getText('some.i18n.key')}" /&gt;
 *  &lt;a:date name="person.birthday" nice="true" /&gt;
 *  &lt;a:date name="person.birthday" /&gt;
 *  <!-- END SNIPPET: example -->
 * </pre>
 *
 * <code>Date</code>
 *
 * @author <a href="mailto:philip.luppens@gmail.com">Philip Luppens</a>
 * @author <a href="mailto:hermanns@aixcept.de">Rainer Hermanns</a>
 * @version $Id$
 *
 *
 * @a2.tag name="date" tld-body-content="empty"
 *         tld-tag-class="org.apache.struts.action2.views.jsp.DateTag"
 *         description="Render a formatted date."
 */
public class Date extends Component {

    private static final Log LOG = LogFactory.getLog(Date.class);
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

    public Date(OgnlValueStack stack) {
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
        StringBuffer sb = new StringBuffer();
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
            args.add(new Long(years));
            args.add(new Long(day));
            args.add(sb);
            args.add(null);
            sb.append(tp.getText(DATETAG_PROPERTY_YEARS, DATETAG_DEFAULT_YEARS, args));
        } else if (day > 0) {
            args.add(new Long(day));
            args.add(new Long(hour));
            args.add(sb);
            args.add(null);
            sb.append(tp.getText(DATETAG_PROPERTY_DAYS, DATETAG_DEFAULT_DAYS, args));
        } else if (hour > 0) {
            args.add(new Long(hour));
            args.add(new Long(min));
            args.add(sb);
            args.add(null);
            sb.append(tp.getText(DATETAG_PROPERTY_HOURS, DATETAG_DEFAULT_HOURS, args));
        } else if (min > 0) {
            args.add(new Long(min));
            args.add(new Long(sec));
            args.add(sb);
            args.add(null);
            sb.append(tp.getText(DATETAG_PROPERTY_MINUTES, DATETAG_DEFAULT_MINUTES, args));
        } else {
            args.add(new Long(sec));
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
        String msg = null;
        OgnlValueStack stack = getStack();
        java.util.Date date = null;
        // find the name on the valueStack, and cast it to a date
        try {
            date = (java.util.Date) findValue(name);
        } catch (Exception e) {
            LOG.error("Could not convert object with key '" + name
                    + "' to a java.util.Date instance");
            // bad date, return a blank instead ?
            msg = "";
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
                            msg = new SimpleDateFormat(globalFormat,
                                    ActionContext.getContext().getLocale())
                                    .format(date);
                        } else {
                            msg = DateFormat.getDateTimeInstance(
                                    DateFormat.MEDIUM, DateFormat.MEDIUM,
                                    ActionContext.getContext().getLocale())
                                    .format(date);
                        }
                    } else {
                        msg = new SimpleDateFormat(format, ActionContext
                                .getContext().getLocale()).format(date);
                    }
                }
                if (msg != null) {
                    try {
                        if (getId() == null) {
                            writer.write(msg);
                        } else {
                            stack.getContext().put(getId(), msg);
                        }
                    } catch (IOException e) {
                        LOG.error("Could not write out Date tag", e);
                    }
                }
            }
        }
        return super.end(writer, "");
    }

    /**
     * Date or DateTime format pattern
     *
     * @a2.tagattribute required="false" rtexprvalue="false"
     */
    public void setFormat(String format) {
        this.format = format;
    }

    /**
     * Whether to print out the date nicely
     *
     * @a2.tagattribute required="false" type="Boolean" default="false"
     */
    public void setNice(boolean nice) {
        this.nice = nice;
    }

    /**
     * @return Returns the name.
     */
    public String getName() {
        return name;
    }

    /**
     * The date value to format
     *
     * @a2.tagattribute required="true" type="String"
     */
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
}
