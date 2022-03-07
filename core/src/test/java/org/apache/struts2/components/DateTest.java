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
package org.apache.struts2.components;

import com.opensymphony.xwork2.ActionContext;
import com.opensymphony.xwork2.util.ValueStack;
import com.opensymphony.xwork2.util.ValueStackFactory;
import org.apache.struts2.StrutsInternalTestCase;
import org.apache.struts2.components.date.SimpleDateFormatAdapter;

import java.io.StringWriter;
import java.io.Writer;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Map;

public class DateTest extends StrutsInternalTestCase {

    private Map<String, Object> context;
    private ValueStack stack;

    public void testSupportSimpleDateTimeFormat() {
        // given
        Date date = new Date(stack);
        date.setDateFormatter(new SimpleDateFormatAdapter());

        String format = "EEEE MMMM dd, hh:mm aa";
        java.util.Date now = new java.util.Date();

        String expected = new SimpleDateFormat(format, ActionContext.getContext().getLocale()).format(now);
        context.put("myDate", now);

        Writer writer = new StringWriter();

        // when
        date.setFormat(format);
        date.setName("myDate");
        date.setNice(false);
        date.start(writer);
        date.end(writer, "");

        // then
        assertEquals(expected, writer.toString());
    }

    public void testDefaultFormat() {
        // given
        Date date = new Date(stack);
        date.setDateFormatter(new SimpleDateFormatAdapter());

        java.util.Date now = new java.util.Date();

        String expected = SimpleDateFormat.getDateInstance(DateFormat.MEDIUM, ActionContext.getContext().getLocale()).format(now);
        context.put("myDate", now);

        Writer writer = new StringWriter();

        // when
        date.setName("myDate");
        date.setNice(false);
        date.start(writer);
        date.end(writer, "");

        // then
        assertEquals(expected, writer.toString());
    }

    @Override
    public void setUp() throws Exception {
        super.setUp();
        stack = container.getInstance(ValueStackFactory.class).createValueStack();
        context = stack.getContext();
    }
}
