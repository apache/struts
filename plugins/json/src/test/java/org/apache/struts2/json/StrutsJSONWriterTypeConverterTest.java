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

import org.apache.struts2.conversion.TypeConversionException;
import org.apache.struts2.conversion.impl.XWorkConverter;
import org.apache.struts2.json.annotations.JSONFieldBridge;
import org.apache.struts2.json.bridge.FieldBridge;
import org.apache.struts2.junit.StrutsTestCase;
import org.apache.struts2.util.StrutsTypeConverter;

import java.io.File;
import java.util.Calendar;
import java.util.Date;
import java.util.Map;

public class StrutsJSONWriterTypeConverterTest extends StrutsTestCase {

    private XWorkConverter converter;
    private StrutsJSONWriter writer;

    @Override
    protected void setUp() throws Exception {
        super.setUp();
        converter = container.getInstance(XWorkConverter.class);
        writer = (StrutsJSONWriter) container.getInstance(JSONWriter.class);
        writer.setUseTypeConverters(true);
    }

    public void testDisabledByDefault() throws Exception {
        assertEquals("false", container.getInstance(String.class, JSONConstants.JSON_WRITER_USE_TYPE_CONVERTERS));
        converter.registerConverter(Money.class.getName(), new MoneyConverter());

        String json = container.getInstance(JSONWriter.class).write(new Money("12.50", "EUR"));

        assertEquals("{\"amount\":\"12.50\",\"currency\":\"EUR\"}", json);
    }

    public void testRegisteredConverterWritesString() throws Exception {
        converter.registerConverter(Money.class.getName(), new MoneyConverter());

        assertEquals("\"12.50 EUR\"", writer.write(new Money("12.50", "EUR")));
    }

    public void testConverterAppliesToNestedProperty() throws Exception {
        converter.registerConverter(Money.class.getName(), new MoneyConverter());

        assertEquals("{\"price\":\"12.50 EUR\"}", writer.write(new Order(new Money("12.50", "EUR"))));
    }

    public void testConverterRegisteredForInterface() throws Exception {
        converter.registerConverter(Identifier.class.getName(), new IdentifierConverter());

        assertEquals("\"ID-42\"", writer.write(new OrderId(42)));
    }

    public void testConverterRegisteredForRecord() throws Exception {
        converter.registerConverter(Temperature.class.getName(), new TemperatureConverter());

        assertEquals("\"21.5C\"", writer.write(new Temperature(21.5)));
    }

    public void testWithoutConverterWritesBean() throws Exception {
        assertEquals("{\"amount\":\"12.50\",\"currency\":\"EUR\"}", writer.write(new Money("12.50", "EUR")));
    }

    public void testFieldBridgeWinsOverConverter() throws Exception {
        converter.registerConverter(Money.class.getName(), new MoneyConverter());

        assertEquals("{\"price\":\"bridged\"}", writer.write(new BridgedOrder(new Money("12.50", "EUR"))));
    }

    public void testFailingConverterFallsBackToBean() throws Exception {
        converter.registerConverter(Money.class.getName(), new FailingConverter());

        assertEquals("{\"amount\":\"12.50\",\"currency\":\"EUR\"}", writer.write(new Money("12.50", "EUR")));
    }

    public void testBuiltInTypesIgnoreRegisteredConverters() throws Exception {
        converter.registerConverter(Date.class.getName(), new ConstantConverter());
        converter.registerConverter(Integer.class.getName(), new ConstantConverter());
        writer.setDateFormatter("yyyy");
        Calendar calendar = Calendar.getInstance();
        calendar.set(2026, Calendar.OCTOBER, 10);

        assertEquals("\"2026\"", writer.write(calendar.getTime()));
        assertEquals("7", writer.write(7));
    }

    public void testUploadedFileConverterIsNotUsedForFiles() throws Exception {
        String json = writer.write(new File("upload.txt"));

        assertTrue(json, json.startsWith("{"));
    }

    public static class Money {
        private final String amount;
        private final String currency;

        public Money(String amount, String currency) {
            this.amount = amount;
            this.currency = currency;
        }

        public String getAmount() {
            return amount;
        }

        public String getCurrency() {
            return currency;
        }
    }

    public static class Order {
        private final Money price;

        public Order(Money price) {
            this.price = price;
        }

        public Money getPrice() {
            return price;
        }
    }

    public static class BridgedOrder {
        private final Money price;

        public BridgedOrder(Money price) {
            this.price = price;
        }

        @JSONFieldBridge(impl = ConstantBridge.class)
        public Money getPrice() {
            return price;
        }
    }

    public static class ConstantBridge implements FieldBridge {
        @Override
        public String objectToString(Object object) {
            return "bridged";
        }
    }

    public interface Identifier {
        long getValue();
    }

    public static class OrderId implements Identifier {
        private final long value;

        public OrderId(long value) {
            this.value = value;
        }

        @Override
        public long getValue() {
            return value;
        }
    }

    public record Temperature(double celsius) {
    }

    public static class MoneyConverter extends StrutsTypeConverter {
        @Override
        public Object convertFromString(Map context, String[] values, Class toClass) {
            throw new UnsupportedOperationException();
        }

        @Override
        public String convertToString(Map context, Object o) {
            Money money = (Money) o;
            return money.getAmount() + " " + money.getCurrency();
        }
    }

    public static class IdentifierConverter extends StrutsTypeConverter {
        @Override
        public Object convertFromString(Map context, String[] values, Class toClass) {
            throw new UnsupportedOperationException();
        }

        @Override
        public String convertToString(Map context, Object o) {
            return "ID-" + ((Identifier) o).getValue();
        }
    }

    public static class TemperatureConverter extends StrutsTypeConverter {
        @Override
        public Object convertFromString(Map context, String[] values, Class toClass) {
            throw new UnsupportedOperationException();
        }

        @Override
        public String convertToString(Map context, Object o) {
            return ((Temperature) o).celsius() + "C";
        }
    }

    public static class ConstantConverter extends StrutsTypeConverter {
        @Override
        public Object convertFromString(Map context, String[] values, Class toClass) {
            throw new UnsupportedOperationException();
        }

        @Override
        public String convertToString(Map context, Object o) {
            return "converted";
        }
    }

    public static class FailingConverter extends StrutsTypeConverter {
        @Override
        public Object convertFromString(Map context, String[] values, Class toClass) {
            throw new UnsupportedOperationException();
        }

        @Override
        public String convertToString(Map context, Object o) {
            throw new TypeConversionException("cannot convert " + o);
        }
    }
}
