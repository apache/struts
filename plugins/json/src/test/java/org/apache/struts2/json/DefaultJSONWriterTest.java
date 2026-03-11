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
package org.apache.struts2.json;

import org.apache.struts2.json.annotations.JSONFieldBridge;
import org.apache.struts2.junit.util.TestUtils;
import org.junit.Test;

import java.net.URL;
import java.text.SimpleDateFormat;
import java.time.Instant;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.time.OffsetDateTime;
import java.time.ZoneId;
import java.time.ZoneOffset;
import java.time.ZonedDateTime;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.TimeZone;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

public class DefaultJSONWriterTest {

    @Test
    public void testWrite() throws Exception {
        Bean bean1 = new Bean();
        bean1.setStringField("str");
        bean1.setBooleanField(true);
        bean1.setCharField('s');
        bean1.setDoubleField(10.1);
        bean1.setFloatField(1.5f);
        bean1.setIntField(10);
        bean1.setLongField(100);
        bean1.setEnumField(AnEnum.ValueA);
        bean1.setEnumBean(AnEnumBean.Two);

        JSONWriter jsonWriter = new DefaultJSONWriter();
        jsonWriter.setEnumAsBean(false);
        String json = jsonWriter.write(bean1);
        TestUtils.assertEquals(DefaultJSONWriter.class.getResource("jsonwriter-write-bean-01.txt"), json);
    }

    @Test
    public void testWriteExcludeNull() throws Exception {
        BeanWithMap bean1 = new BeanWithMap();
        bean1.setStringField("str");
        bean1.setBooleanField(true);
        bean1.setCharField('s');
        bean1.setDoubleField(10.1);
        bean1.setFloatField(1.5f);
        bean1.setIntField(10);
        bean1.setLongField(100);
        bean1.setEnumField(AnEnum.ValueA);
        bean1.setEnumBean(AnEnumBean.Two);

        Map<String, String> m = new LinkedHashMap<>();
        m.put("a", "x");
        m.put("b", null);
        m.put("c", "z");
        bean1.setMap(m);

        JSONWriter jsonWriter = new DefaultJSONWriter();
        jsonWriter.setEnumAsBean(false);
        jsonWriter.setIgnoreHierarchy(false);
        String json = jsonWriter.write(bean1, null, null, true);
        TestUtils.assertEquals(DefaultJSONWriter.class.getResource("jsonwriter-write-bean-03.txt"), json);
    }

    private static class BeanWithMap extends Bean {
        private Map<?, ?> map;

        public Map<?, ?> getMap() {
            return map;
        }

        public void setMap(Map<?, ?> map) {
            this.map = map;
        }
    }

    @Test
    public void testWriteAnnotatedBean() throws Exception {
        AnnotatedBean bean1 = new AnnotatedBean();
        bean1.setStringField("str");
        bean1.setBooleanField(true);
        bean1.setCharField('s');
        bean1.setDoubleField(10.1);
        bean1.setFloatField(1.5f);
        bean1.setIntField(10);
        bean1.setLongField(100);
        bean1.setEnumField(AnEnum.ValueA);
        bean1.setEnumBean(AnEnumBean.Two);
        bean1.setUrl(new URL("http://www.google.com"));

        JSONWriter jsonWriter = new DefaultJSONWriter();
        jsonWriter.setEnumAsBean(false);
        jsonWriter.setIgnoreHierarchy(false);
        String json = jsonWriter.write(bean1);
        TestUtils.assertEquals(DefaultJSONWriter.class.getResource("jsonwriter-write-bean-02.txt"), json);
    }

    @Test
    public void testWriteBeanWithList() throws Exception {
        BeanWithList bean1 = new BeanWithList();
        bean1.setStringField("str");
        bean1.setBooleanField(true);
        bean1.setCharField('s');
        bean1.setDoubleField(10.1);
        bean1.setFloatField(1.5f);
        bean1.setIntField(10);
        bean1.setLongField(100);
        bean1.setEnumField(AnEnum.ValueA);
        bean1.setEnumBean(AnEnumBean.Two);
        List<String> errors = new ArrayList<>();
        errors.add("Field is required");
        bean1.setErrors(errors);

        JSONWriter jsonWriter = new DefaultJSONWriter();
        jsonWriter.setEnumAsBean(false);
        jsonWriter.setIgnoreHierarchy(false);
        String json = jsonWriter.write(bean1);
        TestUtils.assertEquals(DefaultJSONWriter.class.getResource("jsonwriter-write-bean-04.txt"), json);
    }

    private static class BeanWithList extends Bean {
        private List<String> errors;

        public List<String> getErrors() {
            return errors;
        }

        public void setErrors(List<String> errors) {
            this.errors = errors;
        }
    }

    private static class AnnotatedBean extends Bean {
        private URL url;

        @JSONFieldBridge()
        public URL getUrl() {
            return url;
        }

        public void setUrl(URL url) {
            this.url = url;
        }
    }

    @Test
    public void testCanSerializeADate() throws Exception {
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss z");

        SingleDateBean dateBean = new SingleDateBean();
        dateBean.setDate(sdf.parse("2012-12-23 10:10:10 GMT"));

        JSONWriter jsonWriter = new DefaultJSONWriter();
        jsonWriter.setEnumAsBean(false);

        TimeZone.setDefault(TimeZone.getTimeZone("GMT"));
        String json = jsonWriter.write(dateBean);
        assertEquals("{\"date\":\"2012-12-23T10:10:10\"}", json);
    }

    @Test
    public void testCanSetDefaultDateFormat() throws Exception {
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss z");

        SingleDateBean dateBean = new SingleDateBean();
        dateBean.setDate(sdf.parse("2012-12-23 10:10:10 GMT"));

        JSONWriter jsonWriter = new DefaultJSONWriter();
        jsonWriter.setEnumAsBean(false);
        jsonWriter.setDateFormatter("MM-dd-yyyy");
        String json = jsonWriter.write(dateBean);
        assertEquals("{\"date\":\"12-23-2012\"}", json);
    }

    @Test
    public void testSerializeLocalDate() throws Exception {
        TemporalBean bean = new TemporalBean();
        bean.setLocalDate(LocalDate.of(2026, 2, 27));

        JSONWriter jsonWriter = new DefaultJSONWriter();
        String json = jsonWriter.write(bean);
        assertTrue(json.contains("\"localDate\":\"2026-02-27\""));
    }

    @Test
    public void testSerializeLocalDateTime() throws Exception {
        TemporalBean bean = new TemporalBean();
        bean.setLocalDateTime(LocalDateTime.of(2026, 2, 27, 12, 0, 0));

        JSONWriter jsonWriter = new DefaultJSONWriter();
        String json = jsonWriter.write(bean);
        assertTrue(json.contains("\"localDateTime\":\"2026-02-27T12:00:00\""));
    }

    @Test
    public void testSerializeLocalTime() throws Exception {
        TemporalBean bean = new TemporalBean();
        bean.setLocalTime(LocalTime.of(12, 0, 0));

        JSONWriter jsonWriter = new DefaultJSONWriter();
        String json = jsonWriter.write(bean);
        assertTrue(json.contains("\"localTime\":\"12:00:00\""));
    }

    @Test
    public void testSerializeZonedDateTime() throws Exception {
        TemporalBean bean = new TemporalBean();
        bean.setZonedDateTime(ZonedDateTime.of(2026, 2, 27, 12, 0, 0, 0, ZoneId.of("Europe/Paris")));

        JSONWriter jsonWriter = new DefaultJSONWriter();
        String json = jsonWriter.write(bean);
        assertTrue(json.contains("\"zonedDateTime\":\"2026-02-27T12:00:00+01:00[Europe\\/Paris]\""));
    }

    @Test
    public void testSerializeOffsetDateTime() throws Exception {
        TemporalBean bean = new TemporalBean();
        bean.setOffsetDateTime(OffsetDateTime.of(2026, 2, 27, 12, 0, 0, 0, ZoneOffset.ofHours(1)));

        JSONWriter jsonWriter = new DefaultJSONWriter();
        String json = jsonWriter.write(bean);
        assertTrue(json.contains("\"offsetDateTime\":\"2026-02-27T12:00:00+01:00\""));
    }

    @Test
    public void testSerializeInstant() throws Exception {
        TemporalBean bean = new TemporalBean();
        bean.setInstant(Instant.parse("2026-02-27T11:00:00Z"));

        JSONWriter jsonWriter = new DefaultJSONWriter();
        String json = jsonWriter.write(bean);
        assertTrue(json.contains("\"instant\":\"2026-02-27T11:00:00Z\""));
    }

    @Test
    public void testSerializeLocalDateWithCustomFormat() throws Exception {
        TemporalBean bean = new TemporalBean();
        bean.setCustomFormatDate(LocalDate.of(2026, 2, 27));

        JSONWriter jsonWriter = new DefaultJSONWriter();
        String json = jsonWriter.write(bean);
        assertTrue(json.contains("\"customFormatDate\":\"27\\/02\\/2026\""));
    }

    @Test
    public void testSerializeLocalDateTimeWithCustomFormat() throws Exception {
        TemporalBean bean = new TemporalBean();
        bean.setCustomFormatDateTime(LocalDateTime.of(2026, 2, 27, 14, 30));

        JSONWriter jsonWriter = new DefaultJSONWriter();
        String json = jsonWriter.write(bean);
        assertTrue(json.contains("\"customFormatDateTime\":\"27\\/02\\/2026 14:30\""));
    }

    @Test
    public void testSerializeNullTemporalField() throws Exception {
        TemporalBean bean = new TemporalBean();

        JSONWriter jsonWriter = new DefaultJSONWriter();
        String json = jsonWriter.write(bean, null, null, true);
        assertFalse(json.contains("\"localDate\""));
    }

    @Test
    public void testSerializeInstantWithCustomFormat() throws Exception {
        TemporalBean bean = new TemporalBean();
        bean.setCustomFormatInstant(Instant.parse("2026-02-27T11:00:00Z"));

        JSONWriter jsonWriter = new DefaultJSONWriter();
        String json = jsonWriter.write(bean);
        assertTrue(json.contains("\"customFormatInstant\":\"2026-02-27 11:00:00\""));
    }

    @Test
    public void testSerializeCalendar() throws Exception {
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss z");
        Calendar cal = Calendar.getInstance();
        cal.setTime(sdf.parse("2012-12-23 10:10:10 GMT"));

        TemporalBean bean = new TemporalBean();
        bean.setCalendar(cal);

        JSONWriter jsonWriter = new DefaultJSONWriter();
        TimeZone.setDefault(TimeZone.getTimeZone("GMT"));
        String json = jsonWriter.write(bean);
        assertTrue(json.contains("\"calendar\":\"2012-12-23T10:10:10\""));
    }

}
