/*
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
package org.apache.struts2.conversion.impl;

import org.apache.struts2.ActionContext;
import org.apache.struts2.text.StubTextProvider;
import org.apache.struts2.StubValueStack;
import org.apache.struts2.util.ValueStack;
import org.apache.struts2.StrutsInternalTestCase;
import org.apache.struts2.conversion.TypeConversionException;

import java.sql.Time;
import java.sql.Timestamp;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.time.ZoneId;
import java.util.Date;
import java.util.HashMap;
import java.util.Locale;
import java.util.Map;

public class DateConverterTest extends StrutsInternalTestCase {

    private String INPUT_TIME_STAMP_STR;
    private String INPUT_WHEN_LONG_CONSTRUCTOR_STR;
    private final Locale mxLocale = new Locale("es_MX", "MX");
    private final static String RES_TIME_STAMP_STR = "2020-03-20 00:00:00.0";
    private final static String DATE_STR = "2020-03-20";
    private final static String DATE_CONVERTED = "Fri Mar 20 00:00:00";
    private final static String INVALID_DATE = "99/99/2010";
    private final static String LOCALDATETIME_STR = "2020-03-20T00:00:00.000000";
    private final static String LOCALDATETIME1_STR = "12:00 AM Fri Mar 20, 2020";
    private final static String LOCALDATETIME_CONVERTED = "2020-03-20T00:00";
    private final static String LOCALDATE_STR = "2020-03-20";
    private final static String LOCALTIME_STR = "01:59:10";

    private final static String INVALID_LOCALDATETIME = "2010-99-99T00:00";
    private final static Locale DUTCH = Locale.forLanguageTag("nl-NL");
    private final static String MESSAGE_PARSE_ERROR = "Could not parse date";
    private final static String MESSAGE_DEFAULT_CONSTRUCTOR_ERROR = "Couldn't create class null using default (long) constructor";

    public void testSqlTimeType() {
        DateConverter converter = new DateConverter();

        ActionContext context = ActionContext.of().withLocale(mxLocale);

        Object value = converter.convertValue(context.getContextMap(), null, null, null, DateFormat.getTimeInstance(DateFormat.MEDIUM, mxLocale).format(Time.valueOf("01:59:10")), Time.class);
        assertEquals("01:59:10", value.toString());
    }

    public void testSqlTimestampType() {
        DateConverter converter = new DateConverter();

        ActionContext context = ActionContext.of().withLocale(mxLocale);

        Object value = converter.convertValue(context.getContextMap(), null, null, null, INPUT_TIME_STAMP_STR,
                Timestamp.class);
        assertEquals(RES_TIME_STAMP_STR, value.toString());
    }

    public void testDateType() {
        DateConverter converter = new DateConverter();

        Map<String, String> map = new HashMap<>();
        map.put(org.apache.struts2.components.Date.DATETAG_PROPERTY, "yyyy-MM-dd");
        ValueStack stack = new StubValueStack();
        stack.push(new StubTextProvider(map));

        ActionContext context = ActionContext.of()
            .withLocale(new Locale("es_MX", "MX"))
            .withValueStack(stack);

        Object value = converter.convertValue(context.getContextMap(), null, null, null, DATE_STR, Date.class);
        assertTrue(value.toString().startsWith(DATE_CONVERTED));
    }

    public void testTypeConversionExceptionWhenParseError() {
        DateConverter converter = new DateConverter();

        Map<String, String> map = new HashMap<>();
        map.put(org.apache.struts2.components.Date.DATETAG_PROPERTY, "yyyy-MM-dd");
        ValueStack stack = new StubValueStack();
        stack.push(new StubTextProvider(map));

        ActionContext context = ActionContext.of().withLocale(new Locale("es_MX", "MX"))
                .withValueStack(stack);

        try {
            converter.convertValue(context.getContextMap(), null, null, null, INVALID_DATE, Date.class);
            fail("TypeConversionException expected - Conversion error occurred");
        } catch (Exception ex) {
            assertEquals(TypeConversionException.class, ex.getClass());
            assertEquals(MESSAGE_PARSE_ERROR, ex.getMessage());
        }
    }

    public void testTypeConversionExceptionWhenUsingLongConstructor() {
        DateConverter converter = new DateConverter();

        ActionContext context = ActionContext.of().withLocale(mxLocale);

        try {
            converter.convertValue(context.getContextMap(), null, null, null, INPUT_WHEN_LONG_CONSTRUCTOR_STR, null);
            fail("TypeConversionException expected - Error using default (long) constructor");
        } catch (Exception ex) {
            assertEquals(TypeConversionException.class, ex.getClass());
            assertEquals(MESSAGE_DEFAULT_CONSTRUCTOR_ERROR, ex.getMessage());
        }
    }

    public void testLocalDateTimeType() {
        DateConverter converter = new DateConverter();

        ActionContext context = ActionContext.of();

        Object value = converter.convertValue(context.getContextMap(), null, null, null, LOCALDATETIME_STR,
                LocalDateTime.class);
        assertTrue(value.toString().startsWith(LOCALDATETIME_CONVERTED));
    }

    public void testLocalDateTime1Type() {
        DateConverter converter = new DateConverter();

        Map<String, String> map = new HashMap<>();
        map.put(org.apache.struts2.components.Date.DATETAG_PROPERTY, "hh:mm a EEE MMM dd, yyyy");
        ValueStack stack = new StubValueStack();
        stack.push(new StubTextProvider(map));

        ActionContext context = ActionContext.of().withLocale(mxLocale)
                .withValueStack(stack);

        Object value = converter.convertValue(context.getContextMap(), null, null, null, LOCALDATETIME1_STR,
                LocalDateTime.class);
        assertTrue(value.toString().startsWith(LOCALDATETIME_CONVERTED));
    }

    public void testLocalDateTimeTypeConversionExceptionWhenParseError() {
        DateConverter converter = new DateConverter();

        ActionContext context = ActionContext.of();

        try {
            converter.convertValue(context.getContextMap(), null, null, null, INVALID_LOCALDATETIME,
                    LocalDateTime.class);
            fail("TypeConversionException expected - Conversion error occurred");
        } catch (Exception ex) {
            assertEquals(TypeConversionException.class, ex.getClass());
            assertEquals(MESSAGE_PARSE_ERROR, ex.getMessage());
        }
    }

    public void testLocalDateType() {
        DateConverter converter = new DateConverter();

        ActionContext context = ActionContext.of();

        Object value = converter.convertValue(context.getContextMap(), null, null, null, LOCALDATE_STR,
                LocalDate.class);
        assertTrue(value.toString().startsWith(LOCALDATE_STR));
    }

    public void testLocalTimeType() {
        DateConverter converter = new DateConverter();

        ActionContext context = ActionContext.of();

        Object value = converter.convertValue(context.getContextMap(), null, null, null, LOCALTIME_STR,
                LocalTime.class);
        assertTrue(value.toString().startsWith(LOCALTIME_STR));
    }

    public void testIsoDateUnderALocaleWithDashSeparators() {
        Object value = convertToDate("2026-10-24", DUTCH, null);

        assertEquals(LocalDateTime.of(2026, 10, 24, 0, 0), toLocalDateTime(value));
    }

    public void testIsoDateUnderADateOnlyGlobalFormat() {
        Object value = convertToDate("2026-10-24", Locale.US, "dd-MM-yyyy");

        assertEquals(LocalDateTime.of(2026, 10, 24, 0, 0), toLocalDateTime(value));
    }

    public void testIsoDateTimeUnderADateOnlyGlobalFormat() {
        Object value = convertToDate("2026-10-24T10:15:30", Locale.US, "dd-MM-yyyy");

        assertEquals(LocalDateTime.of(2026, 10, 24, 10, 15, 30), toLocalDateTime(value));
    }

    public void testDateTimeKeepsItsTimeUnderADateOnlyGlobalFormat() {
        Object value = convertToDate(dutchDateTime("10:15:30"), DUTCH, "dd-MM-yyyy");

        assertEquals(LocalDateTime.of(2026, 10, 24, 10, 15, 30), toLocalDateTime(value));
    }

    public void testInvalidTimeIsStillRejected() {
        try {
            convertToDate(dutchDateTime("25:00:00"), DUTCH, null);
            fail("TypeConversionException expected");
        } catch (TypeConversionException e) {
            assertEquals(MESSAGE_PARSE_ERROR, e.getMessage());
        }
    }

    public void testSqlTimestampUnderALocaleWithDashSeparators() {
        ActionContext context = ActionContext.of().withLocale(DUTCH);

        Object value = new DateConverter().convertValue(context.getContextMap(), null, null, null, dutchDateTime("10:15:30") + ".123", Timestamp.class);

        assertEquals(Timestamp.valueOf("2026-10-24 10:15:30.123"), value);
    }

    public void testDateWithTrailingTextIsRejected() {
        try {
            convertToDate("24-10-2026 at 10:15:30", DUTCH, null);
            fail("TypeConversionException expected");
        } catch (TypeConversionException e) {
            assertEquals(MESSAGE_PARSE_ERROR, e.getMessage());
        }
    }

    public void testIsoDateTimeWithATrailingZ() {
        Object value = convertToDate("2026-10-24T10:15:30Z", Locale.US, null);

        assertEquals(LocalDateTime.of(2026, 10, 24, 10, 15, 30), toLocalDateTime(value));
    }

    public void testIsoDateTimeWithMillisAndATrailingZ() {
        Object value = convertToDate("2026-10-24T10:15:30.123Z", Locale.US, null);

        assertEquals(LocalDateTime.of(2026, 10, 24, 10, 15, 30, 123_000_000), toLocalDateTime(value));
    }

    public void testSqlTimestampWithTrailingTextIsRejected() {
        ActionContext context = ActionContext.of().withLocale(DUTCH);

        try {
            new DateConverter().convertValue(context.getContextMap(), null, null, null, "24-10-2026 at 10:15:30", Timestamp.class);
            fail("TypeConversionException expected");
        } catch (TypeConversionException e) {
            assertEquals(MESSAGE_PARSE_ERROR, e.getMessage());
        }
    }

    public void testSqlTimeWithTrailingTextIsRejected() {
        ActionContext context = ActionContext.of().withLocale(DUTCH);

        try {
            new DateConverter().convertValue(context.getContextMap(), null, null, null, "10:15:30 tomorrow", Time.class);
            fail("TypeConversionException expected");
        } catch (TypeConversionException e) {
            assertEquals(MESSAGE_PARSE_ERROR, e.getMessage());
        }
    }

    private Object convertToDate(String input, Locale locale, String globalFormat) {
        ValueStack stack = new StubValueStack();
        Map<String, String> texts = new HashMap<>();
        if (globalFormat != null) {
            texts.put(org.apache.struts2.components.Date.DATETAG_PROPERTY, globalFormat);
        }
        stack.push(new StubTextProvider(texts));
        ActionContext context = ActionContext.of().withLocale(locale).withValueStack(stack);

        return new DateConverter().convertValue(context.getContextMap(), null, null, null, input, Date.class);
    }

    private static String dutchDateTime(String time) {
        Date date = Date.from(LocalDateTime.of(2026, 10, 24, 10, 15, 30).atZone(ZoneId.systemDefault()).toInstant());
        return DateFormat.getDateTimeInstance(DateFormat.SHORT, DateFormat.MEDIUM, DUTCH).format(date).replace("10:15:30", time);
    }

    private static LocalDateTime toLocalDateTime(Object date) {
        return LocalDateTime.ofInstant(((Date) date).toInstant(), ZoneId.systemDefault());
    }

    public void testDateObjectToLocalDate() {
        Date date = Date.from(LocalDateTime.of(2020, 3, 20, 10, 15).atZone(ZoneId.systemDefault()).toInstant());

        Object value = new DateConverter().convertValue(ActionContext.of().getContextMap(), null, null, null, date, LocalDate.class);

        assertEquals(LocalDate.of(2020, 3, 20), value);
    }

    public void testSqlDateObjectToLocalDate() {
        Object value = new DateConverter().convertValue(ActionContext.of().getContextMap(), null, null, null,
            java.sql.Date.valueOf(LocalDate.of(2020, 3, 20)), LocalDate.class);

        assertEquals(LocalDate.of(2020, 3, 20), value);
    }

    public void testDateObjectToLocalDateTime() {
        Date date = Date.from(LocalDateTime.of(2020, 3, 20, 10, 15).atZone(ZoneId.systemDefault()).toInstant());

        Object value = new DateConverter().convertValue(ActionContext.of().getContextMap(), null, null, null, date, LocalDateTime.class);

        assertEquals(LocalDateTime.of(2020, 3, 20, 10, 15), value);
    }

    public void testDateObjectToLocalTime() {
        Date date = Date.from(LocalDateTime.of(2020, 3, 20, 10, 15).atZone(ZoneId.systemDefault()).toInstant());

        Object value = new DateConverter().convertValue(ActionContext.of().getContextMap(), null, null, null, date, LocalTime.class);

        assertEquals(LocalTime.of(10, 15), value);
    }

    public void testDateObjectToDateIsUnchanged() {
        Date date = new Date();

        Object value = new DateConverter().convertValue(ActionContext.of().getContextMap(), null, null, null, date, Date.class);

        assertSame(date, value);
    }

    @Override
    protected void setUp() throws Exception {
        super.setUp();
        // Due to JEP 252: Use CLDR Locale Data by Default
        DateFormat dFormat = DateFormat.getDateInstance(DateFormat.SHORT, mxLocale);
        if (dFormat.format(new Date()).contains("-")) { // Format when Java 9 or greater
            INPUT_WHEN_LONG_CONSTRUCTOR_STR = "2020-03-20";
        } else {// Format when Java 8 or lower
            INPUT_WHEN_LONG_CONSTRUCTOR_STR = "03/31/20";
        }
        SimpleDateFormat dateTime = (SimpleDateFormat) DateFormat.getDateTimeInstance(DateFormat.SHORT, DateFormat.MEDIUM, mxLocale);
        INPUT_TIME_STAMP_STR = new SimpleDateFormat(dateTime.toPattern() + ".SSS", mxLocale).format(Timestamp.valueOf(RES_TIME_STAMP_STR));
    }

}
