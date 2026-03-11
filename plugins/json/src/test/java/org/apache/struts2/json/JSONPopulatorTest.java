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

import org.apache.struts2.junit.util.TestUtils;
import org.junit.Test;

import java.beans.IntrospectionException;
import java.io.StringReader;
import java.lang.reflect.InvocationTargetException;
import java.math.BigDecimal;
import java.math.BigInteger;
import java.time.Instant;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.time.OffsetDateTime;
import java.time.ZoneId;
import java.time.ZoneOffset;
import java.time.ZonedDateTime;
import java.util.Calendar;
import java.util.HashMap;
import java.util.Map;
import java.util.TimeZone;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertTrue;
import static org.junit.Assert.fail;

public class JSONPopulatorTest {

    @Test
    public void testNulls() throws IntrospectionException, InvocationTargetException, NoSuchMethodException,
            JSONException, InstantiationException, IllegalAccessException {
        JSONPopulator populator = new JSONPopulator();
        OtherBean bean = new OtherBean();
        Map<String, ?> jsonMap = new HashMap<>();

        jsonMap.put("intField", null);
        jsonMap.put("booleanField", null);
        jsonMap.put("charField", null);
        jsonMap.put("longField", null);
        jsonMap.put("floatField", null);
        jsonMap.put("doubleField", null);
        jsonMap.put("byteField", null);

        populator.populateObject(bean, jsonMap);
        assertNull(bean.getIntField());
        assertNull(bean.isBooleanField());
        assertNull(bean.getCharField());
        assertNull(bean.getLongField());
        assertNull(bean.getDoubleField());
        assertNull(bean.getByteField());
    }

    @Test
    public void testPrimitiveBean() throws Exception {
        StringReader stringReader = new StringReader(TestUtils.readContent(JSONInterceptorTest.class
                .getResource("json-7.txt")));
        Object json = JSONUtil.deserialize(stringReader);
        assertNotNull(json);
        assertTrue(json instanceof Map);
        Map<?, ?> jsonMap = (Map<?, ?>) json;
        JSONPopulator populator = new JSONPopulator();
        Bean bean = new Bean();
        populator.populateObject(bean, jsonMap);
        assertTrue(bean.isBooleanField());
        assertEquals("test\u000E\u000f", bean.getStringField());
        assertEquals(10, bean.getIntField());
        assertEquals('s', bean.getCharField());
        assertEquals(10.1d, bean.getDoubleField(), 0d);
        assertEquals(3, bean.getByteField());
        assertEquals(BigDecimal.valueOf(111111.5d), bean.getBigDecimal());
        assertEquals(new BigInteger("111111"), bean.getBigInteger());
    }

    @Test
    public void testObjectBean() throws Exception {
        String text = TestUtils.readContent(JSONInterceptorTest.class.getResource("json-7.txt"));
        Object json = JSONUtil.deserialize(text);
        assertNotNull(json);
        assertTrue(json instanceof Map);
        Map<?, ?> jsonMap = (Map<?, ?>) json;
        JSONPopulator populator = new JSONPopulator();
        WrapperClassBean bean = new WrapperClassBean();
        populator.populateObject(bean, jsonMap);
        assertEquals(Boolean.TRUE, bean.getBooleanField());
        assertTrue(bean.isPrimitiveBooleanField1());
        assertFalse(bean.isPrimitiveBooleanField2());
        assertFalse(bean.isPrimitiveBooleanField3());
        assertEquals("test\u000E\u000f", bean.getStringField());
        assertEquals(Integer.valueOf(10), bean.getIntField());
        assertEquals(0, bean.getNullIntField());
        assertEquals(Character.valueOf('s'), bean.getCharField());
        assertEquals(Double.valueOf(10.1d), bean.getDoubleField());
        assertEquals(Byte.valueOf((byte) 3), bean.getByteField());

        assertEquals(2, bean.getListField().size());
        assertEquals("1", bean.getListField().get(0).getValue());
        assertEquals("2", bean.getListField().get(1).getValue());

        assertEquals(1, bean.getListMapField().size());
        assertEquals(2, bean.getListMapField().get(0).size());
        assertEquals(Long.valueOf(2073501L), bean.getListMapField().get(0).get("id1"));
        assertEquals(Long.valueOf(3L), bean.getListMapField().get(0).get("id2"));

        assertEquals(2, bean.getMapListField().size());
        assertEquals(3, bean.getMapListField().get("id1").size());
        assertEquals(Long.valueOf(2L), bean.getMapListField().get("id1").get(1));
        assertEquals(4, bean.getMapListField().get("id2").size());
        assertEquals(Long.valueOf(3L), bean.getMapListField().get("id2").get(1));

        assertEquals(1, bean.getArrayMapField().length);
        assertEquals(2, bean.getArrayMapField()[0].size());
        assertEquals(Long.valueOf(2073501L), bean.getArrayMapField()[0].get("id1"));
        assertEquals(Long.valueOf(3L), bean.getArrayMapField()[0].get("id2"));

        assertEquals(3, bean.getSetField().size());
        assertTrue(bean.getSetField().contains("A"));
        assertTrue(bean.getSetField().contains("B"));
        assertTrue(bean.getSetField().contains("C"));

        assertEquals(3, bean.getSortedSetField().size());
        assertEquals("A", bean.getSortedSetField().first());
        assertTrue(bean.getSortedSetField().contains("B"));
        assertEquals("C", bean.getSortedSetField().last());

        assertEquals(3, bean.getNavigableSetField().size());
        assertEquals("A", bean.getNavigableSetField().first());
        assertTrue(bean.getNavigableSetField().contains("B"));
        assertEquals("C", bean.getNavigableSetField().last());

        assertEquals(3, bean.getQueueField().size());
        assertEquals("A", bean.getQueueField().poll());
        assertEquals("B", bean.getQueueField().poll());
        assertEquals("C", bean.getQueueField().poll());

        assertEquals(3, bean.getDequeField().size());
        assertEquals("A", bean.getDequeField().pollFirst());
        assertEquals("B", bean.getDequeField().pollFirst());
        assertEquals("C", bean.getDequeField().pollFirst());
    }

    @Test
    public void testObjectBeanWithStrings() throws Exception {
        StringReader stringReader = new StringReader(TestUtils.readContent(JSONInterceptorTest.class
                .getResource("json-8.txt")));
        Object json = JSONUtil.deserialize(stringReader);
        assertNotNull(json);
        assertTrue(json instanceof Map);
        Map<?, ?> jsonMap = (Map<?, ?>) json;
        JSONPopulator populator = new JSONPopulator();
        WrapperClassBean bean = new WrapperClassBean();
        populator.populateObject(bean, jsonMap);
        assertEquals(Boolean.TRUE, bean.getBooleanField());
        assertEquals("test", bean.getStringField());
        assertEquals(Integer.valueOf(10), bean.getIntField());
        assertEquals(Character.valueOf('s'), bean.getCharField());
        assertEquals(Double.valueOf(10.1d), bean.getDoubleField());
        assertEquals(Byte.valueOf((byte) 3), bean.getByteField());

        assertNull(bean.getListField());
        assertNull(bean.getListMapField());
        assertNull(bean.getMapListField());
        assertNull(bean.getArrayMapField());
    }

    @Test
    public void testInfiniteLoop() {
        try {
            JSONReader reader = new JSONReader();
            reader.read("[1,\"a]");
            fail("Should have thrown an exception");
        } catch (JSONException e) {
            assertEquals("Input string is not well formed JSON (invalid char \uFFFF)", e.getMessage());
        }
    }

    @Test
    public void testParseBadInput() {
        try {
            JSONReader reader = new JSONReader();
            reader.read("[1,\"a\"1]");
            fail("Should have thrown an exception");
        } catch (JSONException e) {
            // I can't get JUnit to ignore the exception
            // @Test(expected = JSONException.class)
        }
    }

    @Test
    public void testDeserializeLocalDate() throws Exception {
        JSONPopulator populator = new JSONPopulator();
        TemporalBean bean = new TemporalBean();
        Map<String, Object> jsonMap = new HashMap<>();
        jsonMap.put("localDate", "2026-02-27");
        populator.populateObject(bean, jsonMap);
        assertEquals(LocalDate.of(2026, 2, 27), bean.getLocalDate());
    }

    @Test
    public void testDeserializeLocalDateTime() throws Exception {
        JSONPopulator populator = new JSONPopulator();
        TemporalBean bean = new TemporalBean();
        Map<String, Object> jsonMap = new HashMap<>();
        jsonMap.put("localDateTime", "2026-02-27T12:00:00");
        populator.populateObject(bean, jsonMap);
        assertEquals(LocalDateTime.of(2026, 2, 27, 12, 0, 0), bean.getLocalDateTime());
    }

    @Test
    public void testDeserializeLocalTime() throws Exception {
        JSONPopulator populator = new JSONPopulator();
        TemporalBean bean = new TemporalBean();
        Map<String, Object> jsonMap = new HashMap<>();
        jsonMap.put("localTime", "12:00:00");
        populator.populateObject(bean, jsonMap);
        assertEquals(LocalTime.of(12, 0, 0), bean.getLocalTime());
    }

    @Test
    public void testDeserializeZonedDateTime() throws Exception {
        JSONPopulator populator = new JSONPopulator();
        TemporalBean bean = new TemporalBean();
        Map<String, Object> jsonMap = new HashMap<>();
        jsonMap.put("zonedDateTime", "2026-02-27T12:00:00+01:00[Europe/Paris]");
        populator.populateObject(bean, jsonMap);
        assertEquals(ZonedDateTime.of(2026, 2, 27, 12, 0, 0, 0, ZoneId.of("Europe/Paris")), bean.getZonedDateTime());
    }

    @Test
    public void testDeserializeOffsetDateTime() throws Exception {
        JSONPopulator populator = new JSONPopulator();
        TemporalBean bean = new TemporalBean();
        Map<String, Object> jsonMap = new HashMap<>();
        jsonMap.put("offsetDateTime", "2026-02-27T12:00:00+01:00");
        populator.populateObject(bean, jsonMap);
        assertEquals(OffsetDateTime.of(2026, 2, 27, 12, 0, 0, 0, ZoneOffset.ofHours(1)), bean.getOffsetDateTime());
    }

    @Test
    public void testDeserializeInstant() throws Exception {
        JSONPopulator populator = new JSONPopulator();
        TemporalBean bean = new TemporalBean();
        Map<String, Object> jsonMap = new HashMap<>();
        jsonMap.put("instant", "2026-02-27T11:00:00Z");
        populator.populateObject(bean, jsonMap);
        assertEquals(Instant.parse("2026-02-27T11:00:00Z"), bean.getInstant());
    }

    @Test
    public void testDeserializeCalendar() throws Exception {
        JSONPopulator populator = new JSONPopulator();
        TemporalBean bean = new TemporalBean();
        Map<String, Object> jsonMap = new HashMap<>();
        jsonMap.put("calendar", "2012-12-23T10:10:10");
        populator.populateObject(bean, jsonMap);
        assertNotNull(bean.getCalendar());
        Calendar expected = Calendar.getInstance();
        expected.setTimeZone(TimeZone.getDefault());
        expected.set(2012, Calendar.DECEMBER, 23, 10, 10, 10);
        expected.set(Calendar.MILLISECOND, 0);
        assertEquals(expected.getTimeInMillis() / 1000, bean.getCalendar().getTimeInMillis() / 1000);
    }

    @Test
    public void testDeserializeLocalDateWithCustomFormat() throws Exception {
        JSONPopulator populator = new JSONPopulator();
        TemporalBean bean = new TemporalBean();
        Map<String, Object> jsonMap = new HashMap<>();
        jsonMap.put("customFormatDate", "27/02/2026");
        populator.populateObject(bean, jsonMap);
        assertEquals(LocalDate.of(2026, 2, 27), bean.getCustomFormatDate());
    }

    @Test
    public void testDeserializeInstantWithCustomFormat() throws Exception {
        JSONPopulator populator = new JSONPopulator();
        TemporalBean bean = new TemporalBean();
        Map<String, Object> jsonMap = new HashMap<>();
        jsonMap.put("customFormatInstant", "2026-02-27 11:00:00");
        populator.populateObject(bean, jsonMap);
        assertEquals(Instant.parse("2026-02-27T11:00:00Z"), bean.getCustomFormatInstant());
    }

    @Test(expected = JSONException.class)
    public void testDeserializeMalformedTemporalThrowsException() throws Exception {
        JSONPopulator populator = new JSONPopulator();
        TemporalBean bean = new TemporalBean();
        Map<String, Object> jsonMap = new HashMap<>();
        jsonMap.put("localDate", "not-a-date");
        populator.populateObject(bean, jsonMap);
    }

    @Test(expected = JSONException.class)
    public void testDeserializeMalformedInstantThrowsException() throws Exception {
        JSONPopulator populator = new JSONPopulator();
        TemporalBean bean = new TemporalBean();
        Map<String, Object> jsonMap = new HashMap<>();
        jsonMap.put("instant", "not-an-instant");
        populator.populateObject(bean, jsonMap);
    }

    @Test
    public void testDeserializeLocalDateTimeWithCustomFormat() throws Exception {
        JSONPopulator populator = new JSONPopulator();
        TemporalBean bean = new TemporalBean();
        Map<String, Object> jsonMap = new HashMap<>();
        jsonMap.put("customFormatDateTime", "27/02/2026 14:30");
        populator.populateObject(bean, jsonMap);
        assertEquals(LocalDateTime.of(2026, 2, 27, 14, 30), bean.getCustomFormatDateTime());
    }
}
