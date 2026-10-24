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
package org.apache.struts2.views.java.simple;

import org.apache.struts2.ActionContext;
import org.apache.struts2.components.DateTextField;
import org.apache.struts2.conversion.impl.DateConverter;
import org.apache.struts2.dispatcher.HttpParameters;
import org.apache.struts2.interceptor.DateTextFieldInterceptor;
import org.apache.struts2.mock.MockActionInvocation;
import org.apache.struts2.components.UIBean;
import org.apache.struts2.interceptor.csp.CspNonceSource;
import org.apache.struts2.interceptor.csp.StrutsCspNonceReader;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.time.ZoneId;
import java.util.Date;
import java.util.HashMap;
import java.util.Locale;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class DateTextFieldTest extends AbstractCommonAttributesTest {

    private DateTextField tag;

    public void testRenderDateTextField() {
    	tag.setId("id");
        tag.setName("name");
        tag.setFormat("yyyy-MM-dd");

        tag.evaluateParams();
        map.putAll(tag.getAttributes());
        theme.renderTag(getTagName(), context);
        String output = writer.getBuffer().toString();
        String expected = s("<div id='id'>" +
        		"<input type='text' class='date_year' size='4' maxlength='4' id='__year_id' name='__year_name'></input>" +
        		"-<input type='text' class='date_month' size='2' maxlength='2' id='__month_id' name='__month_name'></input>" +
        		"-<input type='text' class='date_day' size='2' maxlength='2' id='__day_id' name='__day_name'></input></div>");
        assertEquals(expected, output);
    }

    public void testRenderDateValue() {
        Date date = Date.from(LocalDate.of(2026, 10, 24).atStartOfDay(ZoneId.systemDefault()).toInstant());

        String output = render("yyyy-MM-dd", date);

        assertEquals(s("<div id='id'>" + year("2026") + "-" + month("10") + "-" + day("24") + "</div>"), output);
    }

    public void testRenderLocalDateValue() {
        String output = render("yyyy-MM-dd", LocalDate.of(2026, 10, 24));

        assertEquals(s("<div id='id'>" + year("2026") + "-" + month("10") + "-" + day("24") + "</div>"), output);
    }

    public void testRenderLocalDateTimeValue() {
        String output = render("yyyy-MM-dd HH:mm", LocalDateTime.of(2026, 10, 24, 9, 5));

        assertEquals(s("<div id='id'>" + year("2026") + "-" + month("10") + "-" + day("24") + " "
            + "<input type='text' class='date_hour' size='2' maxlength='2' id='__hour_id' name='__hour_name' value='09'></input>"
            + ":<input type='text' class='date_minute' size='2' maxlength='2' id='__minute_id' name='__minute_name' value='05'></input>"
            + "</div>"), output);
    }

    public void testRenderLocalTimeValue() {
        String output = render("HH:mm", LocalTime.of(9, 5));

        assertEquals(s("<div id='id'>"
            + "<input type='text' class='date_hour' size='2' maxlength='2' id='__hour_id' name='__hour_name' value='09'></input>"
            + ":<input type='text' class='date_minute' size='2' maxlength='2' id='__minute_id' name='__minute_name' value='05'></input>"
            + "</div>"), output);
    }

    public void testRenderLocalDateLeavesTimeFieldsEmpty() {
        String output = render("yyyy-MM-dd HH", LocalDate.of(2026, 10, 24));

        assertEquals(s("<div id='id'>" + year("2026") + "-" + month("10") + "-" + day("24") + " "
            + "<input type='text' class='date_hour' size='2' maxlength='2' id='__hour_id' name='__hour_name'></input>"
            + "</div>"), output);
    }

    public void testLocalDateRoundTripsUnderABuddhistCalendarLocale() throws Exception {
        Locale previous = Locale.getDefault(Locale.Category.FORMAT);
        Locale.setDefault(Locale.Category.FORMAT, Locale.forLanguageTag("th-TH"));
        try {
            String output = render("yyyy-MM-dd", LocalDate.of(2026, 10, 24));

            assertEquals(LocalDate.of(2026, 10, 24), submit(output, LocalDate.class));
        } finally {
            Locale.setDefault(Locale.Category.FORMAT, previous);
        }
    }

    private Object submit(String output, Class<?> toType) throws Exception {
        Map<String, Object> params = new HashMap<>();
        Matcher field = Pattern.compile("name=\"(__[a-z]+_name)\" value=\"([^\"]*)\"").matcher(output);
        while (field.find()) {
            params.put(field.group(1), new String[]{field.group(2)});
        }
        ActionContext actionContext = ActionContext.of().withParameters(HttpParameters.create(params).build());
        MockActionInvocation invocation = new MockActionInvocation();
        invocation.setInvocationContext(actionContext);

        new DateTextFieldInterceptor().intercept(invocation);

        Object bound = actionContext.getParameters().get("name").getObject();
        return new DateConverter().convertValue(actionContext.getContextMap(), null, null, null, bound, toType);
    }

    private String render(String format, Object value) {
        tag.setId("id");
        tag.setName("name");
        tag.setFormat(format);

        tag.evaluateParams();
        map.putAll(tag.getAttributes());
        map.put("nameValue", value);
        theme.renderTag(getTagName(), context);
        return writer.getBuffer().toString();
    }

    private static String year(String value) {
        return "<input type='text' class='date_year' size='4' maxlength='4' id='__year_id' name='__year_name' value='" + value + "'></input>";
    }

    private static String month(String value) {
        return "<input type='text' class='date_month' size='2' maxlength='2' id='__month_id' name='__month_name' value='" + value + "'></input>";
    }

    private static String day(String value) {
        return "<input type='text' class='date_day' size='2' maxlength='2' id='__day_id' name='__day_name' value='" + value + "'></input>";
    }

    @Override
    public void testRenderTextFieldScriptingAttrs() throws Exception { }

    @Override
    public void testRenderTextFieldCommonAttrs() throws Exception { }

    @Override
    public void testRenderTextFieldDynamicAttrs() throws Exception { }

    @Override
    protected void setUp() throws Exception {
        super.setUp();
        this.tag = new DateTextField(stack, request, response);
        this.tag.setCspNonceReader(new StrutsCspNonceReader(CspNonceSource.SESSION.name()));
        this.tag.setScopedConstantProvider(name -> null);
    }

    @Override
    protected UIBean getUIBean() {
        return tag;
    }

    @Override
    protected String getTagName() {
        return "datetextfield";
    }

}
