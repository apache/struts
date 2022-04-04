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
package org.apache.struts2.json;

import java.beans.IntrospectionException;
import java.io.StringReader;
import java.lang.reflect.InvocationTargetException;
import java.math.BigDecimal;
import java.math.BigInteger;
import java.util.HashMap;
import java.util.Map;

import junit.framework.TestCase;

public class JSONPopulatorTest extends TestCase {

    public void testNulls() throws IntrospectionException, InvocationTargetException, NoSuchMethodException,
            JSONException, InstantiationException, IllegalAccessException {
        JSONPopulator populator = new JSONPopulator();
        OtherBean bean = new OtherBean();
        Map jsonMap = new HashMap();

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

    public void testPrimitiveBean() throws Exception {
        StringReader stringReader = new StringReader(TestUtils.readContent(JSONInterceptorTest.class
                .getResource("json-7.txt")));
        Object json = JSONUtil.deserialize(stringReader);
        assertNotNull(json);
        assertTrue(json instanceof Map);
        Map jsonMap = (Map) json;
        JSONPopulator populator = new JSONPopulator();
        Bean bean = new Bean();
        populator.populateObject(bean, jsonMap);
        assertTrue(bean.isBooleanField());
        assertEquals("test\u000E\u000f", bean.getStringField());
        assertEquals(10, bean.getIntField());
        assertEquals('s', bean.getCharField());
        assertEquals(10.1d, bean.getDoubleField(), 0d);
        assertEquals(3, bean.getByteField());
        assertEquals(new BigDecimal(111111.5d), bean.getBigDecimal());
        assertEquals(new BigInteger("111111"), bean.getBigInteger());
    }

    public void testObjectBean() throws Exception {
        String text = TestUtils.readContent(JSONInterceptorTest.class.getResource("json-7.txt"));
        Object json = JSONUtil.deserialize(text);
        assertNotNull(json);
        assertTrue(json instanceof Map);
        Map jsonMap = (Map) json;
        JSONPopulator populator = new JSONPopulator();
        WrapperClassBean bean = new WrapperClassBean();
        populator.populateObject(bean, jsonMap);
        assertEquals(Boolean.TRUE, bean.getBooleanField());
        assertEquals(true, bean.isPrimitiveBooleanField1());
        assertEquals(false, bean.isPrimitiveBooleanField2());
        assertEquals(false, bean.isPrimitiveBooleanField3());
        assertEquals("test\u000E\u000f", bean.getStringField());
        assertEquals(new Integer(10), bean.getIntField());
        assertEquals(0, bean.getNullIntField());
        assertEquals(new Character('s'), bean.getCharField());
        assertEquals(10.1d, bean.getDoubleField());
        assertEquals(new Byte((byte) 3), bean.getByteField());

        assertEquals(2, bean.getListField().size());
        assertEquals("1", bean.getListField().get(0).getValue());
        assertEquals("2", bean.getListField().get(1).getValue());

        assertEquals(1, bean.getListMapField().size());
        assertEquals(2, bean.getListMapField().get(0).size());
        assertEquals(new Long(2073501), bean.getListMapField().get(0).get("id1"));
        assertEquals(new Long(3), bean.getListMapField().get(0).get("id2"));

        assertEquals(2, bean.getMapListField().size());
        assertEquals(3, bean.getMapListField().get("id1").size());
        assertEquals(new Long(2), bean.getMapListField().get("id1").get(1));
        assertEquals(4, bean.getMapListField().get("id2").size());
        assertEquals(new Long(3), bean.getMapListField().get("id2").get(1));

        assertEquals(1, bean.getArrayMapField().length);
        assertEquals(2, bean.getArrayMapField()[0].size());
        assertEquals(new Long(2073501), bean.getArrayMapField()[0].get("id1"));
        assertEquals(new Long(3), bean.getArrayMapField()[0].get("id2"));
    }

    public void testObjectBeanWithStrings() throws Exception {
        StringReader stringReader = new StringReader(TestUtils.readContent(JSONInterceptorTest.class
                .getResource("json-8.txt")));
        Object json = JSONUtil.deserialize(stringReader);
        assertNotNull(json);
        assertTrue(json instanceof Map);
        Map jsonMap = (Map) json;
        JSONPopulator populator = new JSONPopulator();
        WrapperClassBean bean = new WrapperClassBean();
        populator.populateObject(bean, jsonMap);
        assertEquals(Boolean.TRUE, bean.getBooleanField());
        assertEquals("test", bean.getStringField());
        assertEquals(new Integer(10), bean.getIntField());
        assertEquals(new Character('s'), bean.getCharField());
        assertEquals(10.1d, bean.getDoubleField());
        assertEquals(new Byte((byte) 3), bean.getByteField());

        assertEquals(null, bean.getListField());
        assertEquals(null, bean.getListMapField());
        assertEquals(null, bean.getMapListField());
        assertEquals(null, bean.getArrayMapField());
    }

    public void testInfiniteLoop() throws JSONException {
        try {
            JSONReader reader = new JSONReader();
            reader.read("[1,\"a]");
            fail("Should have thrown an exception");
        } catch (JSONException e) {
            // I can't get JUnit to ignore the exception
            // @Test(expected = JSONException.class)
        }
    }

    public void testParseBadInput() throws JSONException {
        try {
            JSONReader reader = new JSONReader();
            reader.read("[1,\"a\"1]");
            fail("Should have thrown an exception");
        } catch (JSONException e) {
            // I can't get JUnit to ignore the exception
            // @Test(expected = JSONException.class)
        }
    }
}
