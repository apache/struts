/*
 + * $Id$
 + *
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

import com.opensymphony.xwork2.ActionContext;
import com.opensymphony.xwork2.mock.MockActionInvocation;
import com.opensymphony.xwork2.util.ValueStack;
import org.apache.struts2.StrutsStatics;
import org.apache.struts2.StrutsTestCase;
import org.springframework.mock.web.MockHttpServletRequest;
import org.springframework.mock.web.MockHttpServletResponse;
import org.springframework.mock.web.MockServletContext;

import javax.servlet.http.HttpServletResponse;
import java.math.BigDecimal;
import java.math.BigInteger;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.regex.Pattern;

/**
 * JSONResultTest
 */
public class JSONResultTest extends StrutsTestCase {
    MockActionInvocation invocation;
    MockHttpServletResponse response;
    MockServletContext servletContext;
    ActionContext context;
    ValueStack stack;
    MockHttpServletRequest request;

    public void testJSONUtilNPEOnNullMehtod() {
        Map map = new HashMap();
        map.put("createtime", new Date());
        try {
            JSONUtil.serialize(map);
        } catch (JSONException e) {
            fail(e.getMessage());
        }
    }

    public void testJSONWriterEndlessLoopOnExludedProperties() throws JSONException {
        Pattern all = Pattern.compile(".*");

        JSONWriter writer = new JSONWriter();
        writer.write(Arrays.asList("a", "b"), Arrays.asList(all), null, false);
    }

    public void testSMDDisabledSMD() throws Exception {
        JSONResult result = new JSONResult();
        SMDActionTest1 action = new SMDActionTest1();
        stack.push(action);

        this.invocation.setAction(action);
        result.execute(this.invocation);

        String smd = response.getContentAsString();

        String normalizedActual = TestUtils.normalize(smd, true);
        String normalizedExpected = TestUtils.normalize(JSONResultTest.class.getResource("smd-8.txt"));
        assertEquals(normalizedExpected, normalizedActual);
    }

    public void testSMDDefault() throws Exception {
        JSONResult result = new JSONResult();
        result.setEnableSMD(true);
        SMDActionTest1 action = new SMDActionTest1();
        stack.push(action);

        this.invocation.setAction(action);
        result.execute(this.invocation);

        String smd = response.getContentAsString();

        String normalizedActual = TestUtils.normalize(smd, true);
        String normalizedExpected = TestUtils.normalize(JSONResultTest.class.getResource("smd-1.txt"));
        assertEquals(normalizedExpected, normalizedActual);
        assertEquals("application/json;charset=ISO-8859-1", response.getContentType());
    }

    public void testSMDDefaultAnnotations() throws Exception {
        JSONResult result = new JSONResult();
        result.setEnableSMD(true);
        SMDActionTest2 action = new SMDActionTest2();
        stack.push(action);

        this.invocation.setAction(action);
        result.execute(this.invocation);

        String smd = response.getContentAsString();

        String normalizedActual = TestUtils.normalize(smd, true);
        String normalizedExpected = TestUtils.normalize(JSONResultTest.class.getResource("smd-2.txt"));
        assertEquals(normalizedExpected, normalizedActual);
        assertEquals("application/json;charset=ISO-8859-1", response.getContentType());
    }

    public void testExcludeNullPropeties() throws Exception {
        JSONResult result = new JSONResult();
        result.setExcludeNullProperties(true);
        TestAction action = new TestAction();
        stack.push(action);
        action.setFoo("fool");

        this.invocation.setAction(action);
        result.execute(this.invocation);

        String smd = response.getContentAsString();

        String normalizedActual = TestUtils.normalize(smd, true);
        String normalizedExpected = TestUtils.normalize(JSONResultTest.class.getResource("nulls-1.txt"));
        assertEquals(normalizedExpected, normalizedActual);
    }

    public void testWrapPrefix() throws Exception {
        JSONResult result = new JSONResult();
        result.setWrapPrefix("_prefix_");
        TestAction2 action = new TestAction2();
        stack.push(action);

        this.invocation.setAction(action);
        result.execute(this.invocation);

        String out = response.getContentAsString();

        String normalizedActual = TestUtils.normalize(out, true);
        String normalizedExpected = "_prefix_{\"name\":\"name\"}";
        assertEquals(normalizedExpected, normalizedActual);
    }

    public void testSuffix() throws Exception {
        JSONResult result = new JSONResult();
        result.setWrapSuffix("_suffix_");
        TestAction2 action = new TestAction2();
        stack.push(action);

        this.invocation.setAction(action);
        result.execute(this.invocation);

        String out = response.getContentAsString();

        String normalizedActual = TestUtils.normalize(out, true);
        String normalizedExpected = "{\"name\":\"name\"}_suffix_";
        assertEquals(normalizedExpected, normalizedActual);
    }

    public void testCustomDateFormat() throws Exception {
        JSONResult result = new JSONResult();
        result.setDefaultDateFormat("MM-dd-yyyy");
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss z");

        SingleDateBean dateBean = new SingleDateBean();
        dateBean.setDate(sdf.parse("2012-12-23 10:10:10 GMT"));

        stack.push(dateBean);

        this.invocation.setAction(dateBean);
        result.execute(this.invocation);

        String out = response.getContentAsString();
        assertEquals("{\"date\":\"12-23-2012\"}", out);
    }

    public void testPrefixAndSuffix() throws Exception {
        JSONResult result = new JSONResult();
        result.setWrapPrefix("_prefix_");
        result.setWrapSuffix("_suffix_");
        TestAction2 action = new TestAction2();
        stack.push(action);

        this.invocation.setAction(action);
        result.execute(this.invocation);

        String out = response.getContentAsString();

        String normalizedActual = TestUtils.normalize(out, true);
        String normalizedExpected = "_prefix_{\"name\":\"name\"}_suffix_";
        assertEquals(normalizedExpected, normalizedActual);
    }

    public void testPrefix() throws Exception {
        JSONResult result = new JSONResult();
        result.setExcludeNullProperties(true);
        result.setPrefix(true);
        TestAction action = new TestAction();
        stack.push(action);
        action.setFoo("fool");

        this.invocation.setAction(action);
        result.execute(this.invocation);

        String smd = response.getContentAsString();

        String normalizedActual = TestUtils.normalize(smd, true);
        String normalizedExpected = TestUtils.normalize(JSONResultTest.class.getResource("prefix-1.txt"));
        assertEquals(normalizedExpected, normalizedActual);
    }

    @SuppressWarnings("unchecked")
    public void test() throws Exception {
        JSONResult result = new JSONResult();

        TestAction action = new TestAction();
        stack.push(action);

        // test scape characters
        action.setArray(new String[] { "a", "a", "\"", "\\", "/", "\b", "\f", "\n", "\r", "\t" });

        List list = new ArrayList();

        list.add("b");
        list.add(1);
        list.add(new int[] { 10, 12 });
        action.setCollection(list);

        // beans
        List collection2 = new ArrayList();
        Bean bean1 = new Bean();

        bean1.setBigDecimal(new BigDecimal("111111.111111"));
        bean1.setBigInteger(new BigInteger("111111111111"));
        bean1.setStringField("str");
        bean1.setBooleanField(true);
        bean1.setCharField('s');
        bean1.setDoubleField(10.1);
        bean1.setFloatField(1.5f);
        bean1.setIntField(10);
        bean1.setLongField(100);
        bean1.setEnumField(AnEnum.ValueA);
        bean1.setEnumBean(AnEnumBean.One);

        Bean bean2 = new Bean();

        bean2.setStringField("  ");
        bean2.setBooleanField(false);
        bean2.setFloatField(1.1f);
        bean2.setDoubleField(2.2);
        bean2.setEnumField(AnEnum.ValueB);
        bean2.setEnumBean(AnEnumBean.Two);

        // circular reference
        bean1.setObjectField(bean2);
        bean2.setObjectField(bean1);

        collection2.add(bean1);
        action.setCollection2(collection2);

        // keep order in map
        Map map = new LinkedHashMap();

        map.put("a", 1);
        map.put("c", new float[] { 1.0f, 2.0f });
        action.setMap(map);

        action.setFoo("foo");
        // should be ignored, marked 'transient'
        action.setBar("bar");

        // date
        Calendar calendar = Calendar.getInstance();
        calendar.set(Calendar.YEAR, 1999);
        calendar.set(Calendar.MONTH, Calendar.DECEMBER);
        calendar.set(Calendar.DAY_OF_MONTH, 31);
        calendar.set(Calendar.HOUR_OF_DAY, 11);
        calendar.set(Calendar.MINUTE, 59);
        calendar.set(Calendar.SECOND, 59);
        action.setDate(calendar.getTime());
        action.setDate2(calendar.getTime());

        this.invocation.setAction(action);
        result.execute(this.invocation);

        String json = response.getContentAsString();

        String normalizedActual = TestUtils.normalize(json, true);
        String normalizedExpected = TestUtils.normalize(JSONResultTest.class.getResource("json.txt"));
        assertEquals(normalizedExpected, normalizedActual);
        assertEquals("application/json;charset=ISO-8859-1", response.getContentType());
    }

    @SuppressWarnings("unchecked")
    public void testHierarchy() throws Exception {
        JSONResult result = new JSONResult();
        result.setIgnoreHierarchy(false);

        TestAction3 action = new TestAction3();
        stack.push(action);
        this.invocation.setAction(action);
        result.execute(this.invocation);

        String json = response.getContentAsString();
        String normalizedActual = TestUtils.normalize(json, true);
        String normalizedExpected = TestUtils.normalize(JSONResultTest.class.getResource("json-4.txt"));
        assertEquals(normalizedExpected, normalizedActual);
        assertEquals("application/json;charset=ISO-8859-1", response.getContentType());
    }

    @SuppressWarnings("unchecked")
    public void testCommentWrap() throws Exception {
        JSONResult result = new JSONResult();

        TestAction action = new TestAction();
        stack.push(action);

        // test scape characters
        action.setArray(new String[] { "a", "a", "\"", "\\", "/", "\b", "\f", "\n", "\r", "\t" });

        List list = new ArrayList();

        list.add("b");
        list.add(1);
        list.add(new int[] { 10, 12 });
        action.setCollection(list);

        // beans
        List collection2 = new ArrayList();
        Bean bean1 = new Bean();

        bean1.setStringField("str");
        bean1.setBooleanField(true);
        bean1.setCharField('s');
        bean1.setDoubleField(10.1);
        bean1.setFloatField(1.5f);
        bean1.setIntField(10);
        bean1.setLongField(100);
        bean1.setEnumField(null);
        bean1.setEnumBean(null);

        Bean bean2 = new Bean();

        bean2.setStringField("  ");
        bean2.setBooleanField(false);
        bean2.setFloatField(1.1f);
        bean2.setDoubleField(2.2);
        bean2.setEnumField(AnEnum.ValueC);
        bean2.setEnumBean(AnEnumBean.Three);

        // circular reference
        bean1.setObjectField(bean2);
        bean2.setObjectField(bean1);

        collection2.add(bean1);
        action.setCollection2(collection2);

        // keep order in map
        Map map = new LinkedHashMap();

        map.put("a", 1);
        map.put("c", new float[] { 1.0f, 2.0f });
        action.setMap(map);

        action.setFoo("foo");
        // should be ignored, marked 'transient'
        action.setBar("bar");

        // date
        Calendar calendar = Calendar.getInstance();
        calendar.set(Calendar.YEAR, 1999);
        calendar.set(Calendar.MONTH, Calendar.DECEMBER);
        calendar.set(Calendar.DAY_OF_MONTH, 31);
        calendar.set(Calendar.HOUR_OF_DAY, 11);
        calendar.set(Calendar.MINUTE, 59);
        calendar.set(Calendar.SECOND, 59);
        action.setDate(calendar.getTime());
        action.setDate2(calendar.getTime());

        this.invocation.setAction(action);
        result.setWrapWithComments(true);
        result.execute(this.invocation);

        String json = response.getContentAsString();

        String normalizedActual = TestUtils.normalize(json, true);
        String normalizedExpected = TestUtils.normalize(JSONResultTest.class.getResource("json-3.txt"));
        assertEquals(normalizedExpected, normalizedActual);
        assertEquals("application/json;charset=ISO-8859-1", response.getContentType());
    }

    private void executeTest2Action(JSONResult result) throws Exception {
        TestAction action = new TestAction();
        stack.push(action);

        // beans
        Bean bean1 = new Bean();

        bean1.setStringField("str");
        bean1.setBooleanField(true);
        bean1.setCharField('s');
        bean1.setDoubleField(10.1);
        bean1.setFloatField(1.5f);
        bean1.setIntField(10);
        bean1.setLongField(100);
        bean1.setEnumField(AnEnum.ValueA);
        bean1.setEnumBean(AnEnumBean.One);

        // set root
        action.setBean(bean1);
        result.setRoot("bean");

        stack.push(action);
        this.invocation.setStack(stack);
        this.invocation.setAction(action);

        result.execute(this.invocation);
    }

    public void test2() throws Exception {
        JSONResult result = new JSONResult();

        executeTest2Action(result);
        String json = response.getContentAsString();

        String normalizedActual = TestUtils.normalize(json, true);
        String normalizedExpected = TestUtils.normalize(JSONResultTest.class.getResource("json-2.txt"));
        assertEquals(normalizedExpected, normalizedActual);
        assertEquals("application/json;charset=ISO-8859-1", response.getContentType());
    }

    public void testJSONP() throws Exception {
        JSONResult result = new JSONResult();
        result.setCallbackParameter("callback");
        request.addParameter("callback", "exec");

        executeTest2Action(result);
        String json = response.getContentAsString();

        String normalizedActual = TestUtils.normalize(json, true);
        String normalizedExpected = TestUtils.normalize(JSONResultTest.class.getResource("jsonp-1.txt"));
        assertEquals(normalizedExpected, normalizedActual);
        assertEquals("application/json;charset=ISO-8859-1", response.getContentType());
    }

    public void testNoCache() throws Exception {
        JSONResult result = new JSONResult();
        result.setNoCache(true);

        executeTest2Action(result);

        assertEquals("no-cache", response.getHeader("Cache-Control"));
        assertEquals("0", response.getHeader("Expires"));
        assertEquals("No-cache", response.getHeader("Pragma"));
    }

    public void testContentType() throws Exception {
        JSONResult result = new JSONResult();
        result.setContentType("some_super_content");

        executeTest2Action(result);

        assertEquals("some_super_content;charset=ISO-8859-1", response.getContentType());
    }

    public void testStatusCode() throws Exception {
        JSONResult result = new JSONResult();
        result.setStatusCode(HttpServletResponse.SC_CONTINUE);

        executeTest2Action(result);

        assertEquals(HttpServletResponse.SC_CONTINUE, response.getStatus());
    }

    /**
     * Repeats test2 but with the Enum serialized as a bean
     */
    public void test2WithEnumBean() throws Exception {
        JSONResult result = new JSONResult();
        result.setEnumAsBean(true);

        executeTest2Action(result);

        String json = response.getContentAsString();

        String normalizedActual = TestUtils.normalize(json, true);
        String normalizedExpected = TestUtils.normalize(JSONResultTest.class.getResource("json-2-enum.txt"));
        assertEquals(normalizedExpected, normalizedActual);
        assertEquals("application/json;charset=ISO-8859-1", response.getContentType());
    }

    /**
     * Ensures that properties of given root object are read as shallow
     * (non-recursive) unless specifically included.
     */
    public void testIncludeProperties() throws Exception {
        JSONResult result = new JSONResult();
        result.setIncludeProperties("foo");
        TestAction action = new TestAction();
        stack.push(action);
        action.setFoo("fooValue");
        action.setBean(new Bean());
        this.invocation.setAction(action);
        result.execute(this.invocation);

        String json = response.getContentAsString();
        String normalizedActual = TestUtils.normalize(json, true);
        String normalizedExpected = TestUtils.normalize(JSONResultTest.class.getResource("json-9.txt"));
        assertEquals(normalizedExpected, normalizedActual);
        assertEquals("application/json;charset=ISO-8859-1", response.getContentType());
    }

    public void testIncludePropertiesWithList() throws Exception {
        JSONResult result = new JSONResult();
        result.setIncludeProperties("^list\\[\\d+\\]\\.booleanField");
        TestAction action = new TestAction();
        stack.push(action);

        List list = new ArrayList();

        list.add(new Bean());
        list.add(new Bean());
        list.add(new Bean());

        action.setList(list);

        this.invocation.setAction(action);
        result.execute(this.invocation);

        String json = response.getContentAsString();
        String normalizedActual = TestUtils.normalize(json, true);
        String normalizedExpected = TestUtils.normalize(JSONResultTest.class.getResource("json-10.txt"));
        assertEquals(normalizedExpected, normalizedActual);
        assertEquals("application/json;charset=ISO-8859-1", response.getContentType());
    }

    public void testIncludePropertiesWithSetList() throws Exception {
        JSONResult result = new JSONResult();
        result.setIncludeProperties("^set\\[\\d+\\]\\.list\\[\\d+\\]\\.booleanField");
        TestAction action = new TestAction();
        stack.push(action);

        Set set = new LinkedHashSet();

        TestAction a1 = new TestAction();

        List list = new ArrayList();

        list.add(new Bean());
        list.add(new Bean());
        list.add(new Bean());

        a1.setList(list);
        set.add(a1);

        TestAction a2 = new TestAction();

        list = new ArrayList();

        list.add(new Bean());
        list.add(new Bean());

        a2.setList(list);
        set.add(a2);

        action.setSet(set);

        this.invocation.setAction(action);
        result.execute(this.invocation);

        String json = response.getContentAsString();
        String normalizedActual = TestUtils.normalize(json, true);
        String normalizedExpected = TestUtils.normalize(JSONResultTest.class.getResource("json-11.txt"));
        assertEquals(normalizedExpected, normalizedActual);
        assertEquals("application/json;charset=ISO-8859-1", response.getContentType());
    }

    public void testDefaultEncoding() throws Exception {
        // given
        JSONResult json = new JSONResult();
        json.setDefaultEncoding("UTF-16");

        // when
        String encoding = json.getEncoding();

        // thn
        assertEquals("UTF-16", encoding);
    }

    public void testEncoding() throws Exception {
        // given
        JSONResult json = new JSONResult();
        json.setEncoding("UTF-8");
        json.setDefaultEncoding("UTF-8");

        // when
        String encoding = json.getEncoding();

        // thn
        assertEquals("UTF-8", encoding);
    }

    @Override
    protected void setUp() throws Exception {
        super.setUp();
        this.response = new MockHttpServletResponse();
        this.request = new MockHttpServletRequest();
        this.request.setRequestURI("http://sumeruri");
        this.context = ActionContext.getContext();
        this.context.put(StrutsStatics.HTTP_RESPONSE, this.response);
        this.context.put(StrutsStatics.HTTP_REQUEST, this.request);
        this.stack = context.getValueStack();
        this.servletContext = new MockServletContext();
        this.context.put(StrutsStatics.SERVLET_CONTEXT, this.servletContext);
        this.invocation = new MockActionInvocation();
        this.invocation.setInvocationContext(this.context);
        this.invocation.setStack(this.stack);
    }
}
