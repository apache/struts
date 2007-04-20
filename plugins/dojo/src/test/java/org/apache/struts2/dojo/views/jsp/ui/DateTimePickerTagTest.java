/*
 * $Id: DateTimePickerTagTest.java 508605 2007-02-16 21:56:50Z musachy $
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
package org.apache.struts2.dojo.views.jsp.ui;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;

import javax.servlet.jsp.JspException;

import org.apache.struts2.dojo.TestAction;
import org.apache.struts2.dojo.components.DateTimePicker;

/**
 */
public class DateTimePickerTagTest extends AbstractUITagTest {
    final private static SimpleDateFormat RFC3339_FORMAT = new SimpleDateFormat(
        "yyyy-MM-dd'T'HH:mm:ss");

    public void testSimple() throws Exception {
        DateTimePickerTag tag = new DateTimePickerTag();
        tag.setPageContext(pageContext);

        tag.setId("id");

        tag.setAdjustWeeks("true");
        tag.setDayWidth("b");
        tag.setDisplayWeeks("true");
        tag.setEndDate("01-01-2008");
        tag.setStartDate("01-01-2007");
        tag.setStaticDisplay("false");
        tag.setWeekStartsOn("g");
        tag.setName("h");
        tag.setLanguage("i");
        tag.setTemplateCssPath("j");
        tag.setValueNotifyTopics("k");
        tag.setValue("l");
        tag.doStartTag();
        tag.doEndTag();

        verify(DateTimePickerTagTest.class
            .getResource("DateTimePickerTagTest-1.txt"));
    }

    public void testDateParsing() throws Exception {
        DateTimePickerTag tag = new DateTimePickerTag();
        tag.setPageContext(pageContext);

        Calendar calendar = Calendar.getInstance();
        calendar.set(Calendar.YEAR, 2007);
        calendar.set(Calendar.MONTH, Calendar.JANUARY);
        calendar.set(Calendar.DAY_OF_MONTH, 1);
        calendar.set(Calendar.HOUR_OF_DAY, 10);
        calendar.set(Calendar.MINUTE, 30);
        calendar.set(Calendar.SECOND, 10);
        calendar.set(Calendar.MILLISECOND, 20);
        calendar.set(Calendar.AM_PM, Calendar.AM);
        Date date = calendar.getTime();

        //test 'nameValue'
        stack.set("date", "01-01-2007");
        tag.setValue("%{date}");
        tag.setDisplayFormat("MM-dd-yyyy");
        assertDateValue("nameValue", tag, date, true, false);
        assertDateProperty("nameValue", tag, date);
        
        tag.setDisplayFormat(null);

        //test 'startDate'
        tag.setStartDate("%{date}");
        assertDateProperty("startDate", tag, date);
        
        //test 'endDate'
        tag.setEndDate("%{date}");
        assertDateProperty("endDate", tag, date);

    }

    private void assertDateProperty(String property, DateTimePickerTag tag, Date date) throws Exception {
        DateFormat shortTimeFormat = DateFormat.getTimeInstance(DateFormat.SHORT);
        DateFormat shortFormat = DateFormat.getDateInstance(DateFormat.SHORT);
        DateFormat mediumFormat = DateFormat.getDateInstance(DateFormat.MEDIUM);
        DateFormat longFormat = DateFormat.getDateInstance(DateFormat.LONG);
        DateFormat fullFormat = DateFormat.getDateInstance(DateFormat.FULL);

        // try short format on 'value'
        stack.set("date", shortFormat.format(date));
        assertDateValue(property, tag, date, true, false);

        //try medium format on 'value'
        stack.set("date", mediumFormat.format(date));
        assertDateValue(property, tag, date, true, false);

        //try long format on 'value'
        stack.set("date", longFormat.format(date));
        assertDateValue(property, tag, date, true, false);

        //try long format on 'value'
        stack.set("date", fullFormat.format(date));
        assertDateValue(property, tag, date, true, false);

        //try RFC 3339 format on 'value'
        stack.set("date", RFC3339_FORMAT.format(date));
        assertDateValue(property, tag, date, true, false);
        
        //try short time format on 'value'
        stack.set("date", shortTimeFormat.format(date));
        assertDateValue(property, tag, date, false, true);
    }
    
    private void assertDateValue(String property, DateTimePickerTag tag, Date toCompareDate,
        boolean compareDate, boolean compareTime) throws Exception {
        tag.doStartTag();
        DateTimePicker picker = (DateTimePicker) tag.getComponent();
        picker.evaluateParams();

        String dateStr = (String) tag.getComponent().getParameters()
            .get(property);
        Date date = RFC3339_FORMAT.parse(dateStr);
        assertNotNull(date);
        Calendar calendar = Calendar.getInstance();
        calendar.setTime(date);

        Calendar toCompareCalendar = Calendar.getInstance();
        toCompareCalendar.setTime(toCompareDate);

        if (compareDate) {
            assertEquals(toCompareCalendar.get(Calendar.YEAR), calendar
                .get(Calendar.YEAR));
            assertEquals(toCompareCalendar.get(Calendar.MONTH), calendar
                .get(Calendar.MONTH));
            assertEquals(toCompareCalendar.get(Calendar.DAY_OF_MONTH), calendar
                .get(Calendar.DAY_OF_MONTH));
        }
        if (compareTime) {
            assertEquals(toCompareCalendar.get(Calendar.HOUR_OF_DAY), calendar
                .get(Calendar.HOUR_OF_DAY));
            assertEquals(toCompareCalendar.get(Calendar.MINUTE), calendar
                .get(Calendar.MINUTE));
            assertEquals(toCompareCalendar.get(Calendar.AM_PM), calendar
                .get(Calendar.AM_PM));
        }

    }

}
