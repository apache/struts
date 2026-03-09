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
import org.apache.struts2.junit.StrutsTestCase;
import org.apache.struts2.junit.util.TestUtils;


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
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.TimeZone;

public class DefaultJSONWriterTest extends StrutsTestCase {
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

        Map m = new LinkedHashMap();
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
        private Map map;

        public Map getMap() {
            return map;
        }

        public void setMap(Map map) {
            this.map = map;
        }
    }

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
        List<String> errors = new ArrayList<String>();
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

        @JSONFieldBridge
        public URL getUrl() {
            return url;
        }

        public void setUrl(URL url) {
            this.url = url;
        }
    }

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

    public void testSerializeLocalDate() throws Exception {
        TemporalBean bean = new TemporalBean();
        bean.setLocalDate(LocalDate.of(2026, 2, 27));

        JSONWriter jsonWriter = new DefaultJSONWriter();
        String json = jsonWriter.write(bean);
        assertTrue(json.contains("\"localDate\":\"2026-02-27\""));
    }

    public void testSerializeLocalDateTime() throws Exception {
        TemporalBean bean = new TemporalBean();
        bean.setLocalDateTime(LocalDateTime.of(2026, 2, 27, 12, 0, 0));

        JSONWriter jsonWriter = new DefaultJSONWriter();
        String json = jsonWriter.write(bean);
        assertTrue(json.contains("\"localDateTime\":\"2026-02-27T12:00:00\""));
    }

    public void testSerializeLocalTime() throws Exception {
        TemporalBean bean = new TemporalBean();
        bean.setLocalTime(LocalTime.of(12, 0, 0));

        JSONWriter jsonWriter = new DefaultJSONWriter();
        String json = jsonWriter.write(bean);
        assertTrue(json.contains("\"localTime\":\"12:00:00\""));
    }

    public void testSerializeZonedDateTime() throws Exception {
        TemporalBean bean = new TemporalBean();
        bean.setZonedDateTime(ZonedDateTime.of(2026, 2, 27, 12, 0, 0, 0, ZoneId.of("Europe/Paris")));

        JSONWriter jsonWriter = new DefaultJSONWriter();
        String json = jsonWriter.write(bean);
        assertTrue(json.contains("\"zonedDateTime\":\"2026-02-27T12:00:00+01:00[Europe\\/Paris]\""));
    }

    public void testSerializeOffsetDateTime() throws Exception {
        TemporalBean bean = new TemporalBean();
        bean.setOffsetDateTime(OffsetDateTime.of(2026, 2, 27, 12, 0, 0, 0, ZoneOffset.ofHours(1)));

        JSONWriter jsonWriter = new DefaultJSONWriter();
        String json = jsonWriter.write(bean);
        assertTrue(json.contains("\"offsetDateTime\":\"2026-02-27T12:00:00+01:00\""));
    }

    public void testSerializeInstant() throws Exception {
        TemporalBean bean = new TemporalBean();
        bean.setInstant(Instant.parse("2026-02-27T11:00:00Z"));

        JSONWriter jsonWriter = new DefaultJSONWriter();
        String json = jsonWriter.write(bean);
        assertTrue(json.contains("\"instant\":\"2026-02-27T11:00:00Z\""));
    }

    public void testSerializeLocalDateWithCustomFormat() throws Exception {
        TemporalBean bean = new TemporalBean();
        bean.setCustomFormatDate(LocalDate.of(2026, 2, 27));

        JSONWriter jsonWriter = new DefaultJSONWriter();
        String json = jsonWriter.write(bean);
        assertTrue(json.contains("\"customFormatDate\":\"27\\/02\\/2026\""));
    }

    public void testSerializeLocalDateTimeWithCustomFormat() throws Exception {
        TemporalBean bean = new TemporalBean();
        bean.setCustomFormatDateTime(LocalDateTime.of(2026, 2, 27, 14, 30));

        JSONWriter jsonWriter = new DefaultJSONWriter();
        String json = jsonWriter.write(bean);
        assertTrue(json.contains("\"customFormatDateTime\":\"27\\/02\\/2026 14:30\""));
    }

    public void testSerializeNullTemporalFields() throws Exception {
        TemporalBean bean = new TemporalBean();

        JSONWriter jsonWriter = new DefaultJSONWriter();
        String json = jsonWriter.write(bean, null, null, true);
        assertEquals("{}", json);
    }

    public void testSerializeInstantWithCustomFormat() throws Exception {
        TemporalBean bean = new TemporalBean();
        bean.setCustomFormatInstant(Instant.parse("2026-02-27T11:00:00Z"));

        JSONWriter jsonWriter = new DefaultJSONWriter();
        String json = jsonWriter.write(bean);
        assertTrue(json.contains("\"customFormatInstant\":\"2026-02-27 11:00:00\""));
    }

    public void testSerializeOffsetDateTimeWithCustomFormat() throws Exception {
        TemporalBean bean = new TemporalBean();
        bean.setCustomFormatOffsetDateTime(OffsetDateTime.of(2026, 2, 27, 14, 30, 0, 0, ZoneOffset.ofHours(1)));

        JSONWriter jsonWriter = new DefaultJSONWriter();
        String json = jsonWriter.write(bean);
        assertTrue(json.contains("\"customFormatOffsetDateTime\":\"27\\/02\\/2026 14:30:00+01:00\""));
    }

    public void testRoundTripLocalDate() throws Exception {
        LocalDate original = LocalDate.of(2026, 2, 27);
        TemporalBean writeBean = new TemporalBean();
        writeBean.setLocalDate(original);

        JSONWriter jsonWriter = new DefaultJSONWriter();
        String json = jsonWriter.write(writeBean);

        Object parsed = JSONUtil.deserialize(json);
        TemporalBean readBean = new TemporalBean();
        new JSONPopulator().populateObject(readBean, (Map) parsed);
        assertEquals(original, readBean.getLocalDate());
    }

    public void testRoundTripInstant() throws Exception {
        Instant original = Instant.parse("2026-02-27T11:00:00Z");
        TemporalBean writeBean = new TemporalBean();
        writeBean.setInstant(original);

        JSONWriter jsonWriter = new DefaultJSONWriter();
        String json = jsonWriter.write(writeBean);

        Object parsed = JSONUtil.deserialize(json);
        TemporalBean readBean = new TemporalBean();
        new JSONPopulator().populateObject(readBean, (Map) parsed);
        assertEquals(original, readBean.getInstant());
    }

    public void testRoundTripZonedDateTime() throws Exception {
        ZonedDateTime original = ZonedDateTime.of(2026, 2, 27, 12, 0, 0, 0, ZoneId.of("Europe/Paris"));
        TemporalBean writeBean = new TemporalBean();
        writeBean.setZonedDateTime(original);

        JSONWriter jsonWriter = new DefaultJSONWriter();
        String json = jsonWriter.write(writeBean);

        Object parsed = JSONUtil.deserialize(json);
        TemporalBean readBean = new TemporalBean();
        new JSONPopulator().populateObject(readBean, (Map) parsed);
        assertEquals(original, readBean.getZonedDateTime());
    }

}
