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

package org.apache.struts2.views.jsp.ui;

import com.opensymphony.xwork2.ActionContext;
import org.apache.struts2.views.jsp.AbstractTagTest;
import org.apache.struts2.views.jsp.DateTag;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.TimeZone;

/**
 * Unit test for {@link org.apache.struts2.components.Date}.
 *
 */
public class DateTagTest extends AbstractTagTest {

    private DateTag tag;

    public void testCustomFormat() throws Exception {
        String format = "yyyy/MM/dd hh:mm:ss";
        Date now = new Date();
        String formatted = new SimpleDateFormat(format).format(now);
        context.put("myDate", now);

        tag.setName("myDate");
        tag.setNice(false);
        tag.setFormat(format);
        tag.doStartTag();
        tag.doEndTag();
        assertEquals(formatted, writer.toString());
    }

    public void testCustomFormatWithTimezone() throws Exception {
        String format = "yyyy/MM/dd hh:mm:ss";
        Date now = Calendar.getInstance(TimeZone.getTimeZone("UTC+1")).getTime();
        SimpleDateFormat sdf = new SimpleDateFormat(format);
        sdf.setTimeZone(TimeZone.getTimeZone("UTC+1"));
        String formatted = sdf.format(now);
        context.put("myDate", now);

        tag.setName("myDate");
        tag.setNice(false);
        tag.setFormat(format);
        tag.setTimezone("UTC+1");
        tag.doStartTag();
        tag.doEndTag();
        assertEquals(formatted, writer.toString());
    }

    public void testCustomFormatWithTimezoneAsExpression() throws Exception {
        String format = "yyyy/MM/dd hh:mm:ss";
        Date now = Calendar.getInstance(TimeZone.getTimeZone("UTC+2")).getTime();
        SimpleDateFormat sdf = new SimpleDateFormat(format);
        sdf.setTimeZone(TimeZone.getTimeZone("UTC+2"));
        String formatted = sdf.format(now);
        context.put("myDate", now);
        context.put("myTimezone", "UTC+2");

        tag.setName("myDate");
        tag.setNice(false);
        tag.setFormat(format);
        tag.setTimezone("myTimezone");
        tag.doStartTag();
        tag.doEndTag();
        assertEquals(formatted, writer.toString());
    }

    public void testCustomFormatCalendar() throws Exception {
        String format = "yyyy/MM/dd hh:mm:ss";
        Calendar calendar = Calendar.getInstance();
        String formatted = new SimpleDateFormat(format).format(calendar.getTime());
        context.put("myDate", calendar);

        tag.setName("myDate");
        tag.setNice(false);
        tag.setFormat(format);
        tag.doStartTag();
        tag.doEndTag();
        assertEquals(formatted, writer.toString());
    }

    public void testDefaultFormat() throws Exception {
        Date now = new Date();
        String formatted = DateFormat.getDateTimeInstance(DateFormat.MEDIUM, DateFormat.MEDIUM,
                ActionContext.getContext().getLocale()).format(now);

        context.put("myDate", now);
        tag.setName("myDate");
        tag.setNice(false);
        tag.doStartTag();
        tag.doEndTag();
        assertEquals(formatted, writer.toString());
    }

    public void testCustomFormatAndComponent() throws Exception {
        String format = "yyyy/MM/dd hh:mm:ss";
        Date now = new Date();
        String formatted = new SimpleDateFormat(format).format(now);
        context.put("myDate", now);

        tag.setName("myDate");
        tag.setFormat(format);
        tag.setNice(false);

        tag.doStartTag();

        // component test must be done between start and end tag
        org.apache.struts2.components.Date component = (org.apache.struts2.components.Date) tag.getComponent();
        assertEquals("myDate", component.getName());
        assertEquals(format, component.getFormat());
        assertEquals(false, component.isNice());

        tag.doEndTag();

        assertEquals(formatted, writer.toString());
    }

    public void testSetId() throws Exception {
        String format = "yyyy/MM/dd hh:mm:ss";
        Date now = new Date();
        String formatted = new SimpleDateFormat(format).format(now);
        context.put("myDate", now);

        tag.setName("myDate");
        tag.setNice(false);
        tag.setFormat(format);
        tag.setId("myId");
        tag.doStartTag();
        tag.doEndTag();
        assertEquals(formatted, context.get("myId"));
    }

    public void testFutureNiceHour() throws Exception {
        Date now = new Date();
        Calendar future = Calendar.getInstance();
        future.setTime(now);
        future.add(Calendar.HOUR, 1);
        future.add(Calendar.SECOND, 5); // always add a little slack otherwise we could calculate wrong

        context.put("myDate", future.getTime());
        tag.setName("myDate");
        tag.setNice(true);
        tag.doStartTag();
        tag.doEndTag();
        assertEquals("in one hour", writer.toString());
    }

    public void testPastNiceHour() throws Exception {
        Date now = new Date();
        Calendar future = Calendar.getInstance();
        future.setTime(now);
        future.add(Calendar.HOUR, -1);
        future.add(Calendar.SECOND, -5); // always add a little slack otherwise we could calculate wrong

        context.put("myDate", future.getTime());
        tag.setName("myDate");
        tag.setNice(true);
        tag.doStartTag();
        tag.doEndTag();
        assertEquals("one hour ago", writer.toString());
    }

    public void testFutureNiceHourMinSec() throws Exception {
        Date now = new Date();
        Calendar future = Calendar.getInstance();
        future.setTime(now);
        future.add(Calendar.HOUR, 2);
        future.add(Calendar.MINUTE, 33);
        future.add(Calendar.SECOND, 5); // always add a little slack otherwise we could calculate wrong

        context.put("myDate", future.getTime());
        tag.setName("myDate");
        tag.setNice(true);
        tag.doStartTag();
        tag.doEndTag();
        assertEquals("in 2 hours, 33 minutes", writer.toString());
    }

    public void testPastNiceHourMin() throws Exception {
        Date now = new Date();
        Calendar past = Calendar.getInstance();
        past.setTime(now);
        past.add(Calendar.HOUR, -4);
        past.add(Calendar.MINUTE, -55);
        past.add(Calendar.SECOND, -5); // always add a little slack otherwise we could calculate wrong

        context.put("myDate", past.getTime());
        tag.setName("myDate");
        tag.setNice(true);
        tag.doStartTag();
        tag.doEndTag();
        assertEquals("4 hours, 55 minutes ago", writer.toString());
    }

    public void testFutureLessOneMin() throws Exception {
        Date now = new Date();
        Calendar future = Calendar.getInstance();
        future.setTime(now);
        future.add(Calendar.SECOND, 47);
        future.add(Calendar.SECOND, 5); // always add a little slack otherwise we could calculate wrong

        context.put("myDate", future.getTime());
        tag.setName("myDate");
        tag.setNice(true);
        tag.doStartTag();
        tag.doEndTag();
        assertEquals("in an instant", writer.toString());
    }

    public void testFutureLessOneHour() throws Exception {
        Date now = new Date();
        Calendar future = Calendar.getInstance();
        future.setTime(now);
        future.add(Calendar.MINUTE, 36);
        future.add(Calendar.SECOND, 5); // always add a little slack otherwise we could calculate wrong

        context.put("myDate", future.getTime());
        tag.setName("myDate");
        tag.setNice(true);
        tag.doStartTag();
        tag.doEndTag();
        assertEquals("in 36 minutes", writer.toString());
    }

    public void testFutureLessOneYear() throws Exception {
        Date now = new Date();
        Calendar future = Calendar.getInstance();
        future.setTime(now);
        future.add(Calendar.HOUR, 40 * 24);
        future.add(Calendar.SECOND, 5); // always add a little slack otherwise we could calculate wrong

        context.put("myDate", future.getTime());
        tag.setName("myDate");
        tag.setNice(true);
        tag.doStartTag();
        tag.doEndTag();
        assertEquals("in 40 days", writer.toString());
    }

    public void testFutureTwoYears() throws Exception {
        Date now = new Date();
        Calendar future = Calendar.getInstance();
        future.setTime(now);
        future.add(Calendar.YEAR, 2);
        future.add(Calendar.DATE, 1);
        future.add(Calendar.SECOND, 5); // always add a little slack otherwise we could calculate wrong

        context.put("myDate", future.getTime());
        tag.setName("myDate");
        tag.setNice(true);
        tag.doStartTag();
        tag.doEndTag();

        // hmmm the Date component isn't the best to calculate the excat difference so we'll just check
        // that it starts with in 2 years
        assertTrue(writer.toString().startsWith("in 2 years"));
    }

    public void testNoDateObjectInContext() throws Exception {
        context.put("myDate", "this is not a java.util.Date object");
        tag.setName("myDate");
        tag.setNice(true);
        tag.doStartTag();
        tag.doEndTag();
        //should return a blank
        assertEquals("", writer.toString());
    }

    protected void setUp() throws Exception {
        super.setUp();
        tag = new DateTag();
        tag.setPageContext(pageContext);
    }

    protected void tearDown() throws Exception {
        super.tearDown();
        tag = null;
    }

}
