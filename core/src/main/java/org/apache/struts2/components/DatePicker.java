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

import com.opensymphony.xwork.ActionContext;
import com.opensymphony.xwork.util.OgnlValueStack;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.util.Locale;

/**
 * <!-- START SNIPPET: javadoc -->
 *
 * Renders datepicker element.</p>
 *
 * Implementation was changed in WebWork 2.2 to use <a href="http://www.dynarch.com/projects/calendar/">jscalendar</a>
 * instead of non locale aware tigracalendar. Check locale and format settings if you used the old widget in your
 * applications. Be sure to include proper stylesheet as described below if you don't want the calender widget to look
 * transparent.</p>
 *
 * <b>Important:</b> Be sure to set the id attributs if not used within a &lt;a:form /&gt; tag, as it takes care of
 * setting the id for you, being required to copy selected date to text input element.</p>
 *
 * Following a reference for the format parameter (copied from jscalendar documentation):
 * <table border=0><tr><td valign=top ></td></tr>
 * <tr><td valign=top ><tt>%a</tt> </td><td valign=top >abbreviated weekday name </td></tr>
 * <tr><td valign=top ><tt>%A</tt> </td><td valign=top >full weekday name </td></tr>
 * <tr><td valign=top ><tt>%b</tt> </td><td valign=top >abbreviated month name </td></tr>
 * <tr><td valign=top ><tt>%B</tt> </td><td valign=top >full month name </td></tr>
 * <tr><td valign=top ><tt>%C</tt> </td><td valign=top >century number </td></tr>
 * <tr><td valign=top ><tt>%d</tt> </td><td valign=top >the day of the month ( 00 .. 31 ) </td></tr>
 * <tr><td valign=top ><tt>%e</tt> </td><td valign=top >the day of the month ( 0 .. 31 ) </td></tr>
 * <tr><td valign=top ><tt>%H</tt> </td><td valign=top >hour ( 00 .. 23 ) </td></tr>
 * <tr><td valign=top ><tt>%I</tt> </td><td valign=top >hour ( 01 .. 12 ) </td></tr>
 * <tr><td valign=top ><tt>%j</tt> </td><td valign=top >day of the year ( 000 .. 366 ) </td></tr>
 * <tr><td valign=top ><tt>%k</tt> </td><td valign=top >hour ( 0 .. 23 ) </td></tr>
 * <tr><td valign=top ><tt>%l</tt> </td><td valign=top >hour ( 1 .. 12 ) </td></tr>
 * <tr><td valign=top ><tt>%m</tt> </td><td valign=top >month ( 01 .. 12 ) </td></tr>
 * <tr><td valign=top ><tt>%M</tt> </td><td valign=top >minute ( 00 .. 59 ) </td></tr>
 * <tr><td valign=top ><tt>%n</tt> </td><td valign=top >a newline character </td></tr>
 * <tr><td valign=top ><tt>%p</tt> </td><td valign=top >``PM'' or ``AM'' </td></tr>
 * <tr><td valign=top ><tt>%P</tt> </td><td valign=top >``pm'' or ``am'' </td></tr>
 * <tr><td valign=top ><tt>%S</tt> </td><td valign=top >second ( 00 .. 59 ) </td></tr>
 * <tr><td valign=top ><tt>%s</tt> </td><td valign=top >number of seconds since Epoch (since Jan 01 1970 00:00:00 UTC) </td></tr>
 * <tr><td valign=top ><tt>%t</tt> </td><td valign=top >a tab character </td></tr>
 * <tr><td valign=top ><tt>%U, %W, %V</tt> </td><td valign=top >the week number</td></tr>
 * <tr><td valign=top ><tt>%u</tt> </td><td valign=top >the day of the week ( 1 .. 7, 1 = MON )</td></tr>
 * <tr><td valign=top ><tt>%w</tt> </td><td valign=top >the day of the week ( 0 .. 6, 0 = SUN )</td></tr>
 * <tr><td valign=top ><tt>%y</tt> </td><td valign=top >year without the century ( 00 .. 99 )</td></tr>
 * <tr><td valign=top ><tt>%Y</tt> </td><td valign=top >year including the century ( ex. 1979 )</td></tr>
 * <tr><td valign=top ><tt>%%</tt> </td><td valign=top >a literal <tt>%</tt> character
 * </td></tr></table><p>
 *
 * <!-- END SNIPPET: javadoc -->
 *
 * <p/> <b>Examples</b>
 *
 * <pre>
 * <!-- START SNIPPET: expl1 -->
 * Date in application's locale format:
 *     &lt;a:datepicker name="order.date" id="order.date" /&gt;
 * Date in german locale, with german texts:
 *     &lt;a:datepicker name="delivery.date" id="delivery.date" template="datepicker_js.ftl" language="de" /&gt;
 * Date in german locale, with german texts and custom date format, including time:
 *     &lt;a:datepicker name="invoice.date" id="invoice.date" template="datepicker_js.ftl" language="de" format="%d. %b &Y %H:%M" showstime="true" /&gt;
 * <!-- END SNIPPET: expl1 -->
 * </pre>
 * <p/>
 *
 * <!-- START SNIPPET: expldesc2 -->
 *
 * If you use this jscalendar based datepicker widget, you might want to use one of the standard stylesheets provided
 * with jscalendar (all distribution stylesheets are included in struts jar). The easiest way to do so is to place the
 * &lt;a:head/&gt; tag in the head of your html page, as it takes care of including calendar css.
 * Otherwise, to manually activate the calendar-blue style, include the following in your stylesheet definition:
 *
 * <!-- END SNIPPET: expldesc2 -->
 * <pre>
 * <!-- START SNIPPET: expl2 -->
 * &lt;link href="&lt;a:url value="/struts/jscalendar/calendar-blue.css" /&gt;" rel="stylesheet" type="text/css" media="all"/&gt;
 * <!-- END SNIPPET: expl2 -->
 * </pre>
 *
 * @a2.tag name="datepicker" tld-body-content="JSP" tld-tag-class="org.apache.struts2.views.jsp.ui.DatePickerTag"
 * description="Render datepicker"
 */
public class DatePicker extends TextField {

    final public static String TEMPLATE = "datepicker";

    protected String language;
    protected String format;
    protected String showstime;
    protected String singleclick;

    public DatePicker(OgnlValueStack stack, HttpServletRequest request, HttpServletResponse response) {
        super(stack, request, response);
    }

    protected String getDefaultTemplate() {
        return TEMPLATE;
    }

    public void evaluateParams() {
        super.evaluateParams();

        if (language != null) {
            addParameter("language", findString(language));
        } else {
            final Locale locale = (Locale) getStack().getContext().get(ActionContext.LOCALE);
            if (locale != null) {
                addParameter("language", locale.getLanguage());
            } else {
                addParameter("language", Locale.getDefault().getLanguage());
            }

        }

        if (format != null) {
            addParameter("format", findString(format));
        }

        if (showstime != null) {
            addParameter("showstime", findString(showstime));
        }

        if (singleclick != null) {
            addParameter("singleclick", findString(singleclick));
        }

    }

    /**
     * The language to use for the widget texts and localization presets.
     * @a2.tagattribute required="false" type="String" default="The language of the current Locale"
     */
    public void setLanguage(String language) {
        this.language = language;
    }

    /**
     * The format to use for date field.
     * @a2.tagattribute required="false" type="String" default="Dateformat specified by language preset (%Y/%m/%d for en)"
     */
    public void setFormat(String format) {
        this.format = format;
    }

    /**
     * Whether time selector is to be shown. Valid values are &quot;true&quot;, &quot;false&quot;, &quot;24&quot; and &quot;12&quot;.
     * @a2.tagattribute required="false" type="String" default="false"
     */
    public void setShowstime(String showstime) {
        this.showstime = showstime;
    }

    /**
     * Whether to use selected value after single or double click.
     * @a2.tagattribute required="false" type="Boolean" default="true"
     */
    public void setSingleclick(String singleclick) {
        this.singleclick = singleclick;
    }

}
