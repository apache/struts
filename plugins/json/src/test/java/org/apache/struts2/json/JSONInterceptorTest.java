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

import java.util.Calendar;
import java.util.List;
import java.util.Map;

import org.apache.struts2.StrutsStatics;
import org.apache.struts2.StrutsTestCase;
import org.springframework.mock.web.MockHttpServletRequest;
import org.springframework.mock.web.MockHttpServletResponse;
import org.springframework.mock.web.MockServletContext;

import com.opensymphony.xwork2.ActionContext;
import com.opensymphony.xwork2.mock.MockActionInvocation;
import com.opensymphony.xwork2.util.ValueStack;

public class JSONInterceptorTest extends StrutsTestCase {
    private MockActionInvocationEx invocation;
    private MockHttpServletRequest request;
    private MockHttpServletResponse response;

    private void setRequestContent(String fileName) throws Exception {
        String content = TestUtils.readContent(JSONInterceptorTest.class.getResource(fileName));
        this.request.setContent(content.getBytes());
    }

    public void testBadJSON1() throws Exception {
        tryBadJSON("bad-1.txt");
    }

    public void testBadJSON2() throws Exception {
        tryBadJSON("bad-2.txt");
    }

    public void testBadJSON3() throws Exception {
        tryBadJSON("bad-3.txt");
    }

    public void testBadJSON4() throws Exception {
        tryBadJSON("bad-4.txt");
    }

    public void testBadJSON5() throws Exception {
        tryBadJSON("bad-5.txt");
    }

    public void testBadToTheBoneJSON4() throws Exception {
        tryBadJSON("bad-to-the-bone.txt");
    }

    private void tryBadJSON(String fileName) throws Exception {
        // request
        setRequestContent(fileName);
        this.request.addHeader("content-type", "application/json-rpc");

        JSONInterceptor interceptor = new JSONInterceptor();
        interceptor.setEnableSMD(true);
        SMDActionTest1 action = new SMDActionTest1();

        this.invocation.setAction(action);

        // JSON is not well formed, throw exception
        try {
            interceptor.intercept(this.invocation);
            fail("Should have thrown an exception");
        } catch (JSONException e) {
            // I can't get JUnit to ignore the exception
            // @Test(expected = JSONException.class)
        }
    }

    public void testSMDDisabledSMD() throws Exception {
        // request
        setRequestContent("smd-3.txt");
        this.request.addHeader("content-type", "application/json-rpc");

        JSONInterceptor interceptor = new JSONInterceptor();
        SMDActionTest1 action = new SMDActionTest1();

        this.invocation.setAction(action);

        // SMD was not enabled so invocation must happen
        try {
            interceptor.intercept(this.invocation);
        } catch (JSONException e) {
            fail("Should have not thrown an exception");
        }

    }

    public void testSMDAliasedMethodCall1() throws Exception {
        // request
        setRequestContent("smd-14.txt");
        this.request.addHeader("content-type", "application/json-rpc");

        JSONInterceptor interceptor = new JSONInterceptor();
        interceptor.setEnableSMD(true);
        SMDActionTest2 action = new SMDActionTest2();

        this.invocation.setAction(action);

        interceptor.intercept(this.invocation);
        // method was aliased, but was invoked with the regular name
        // so method must not be invoked
        assertFalse(this.invocation.isInvoked());
        assertFalse(action.isDoSomethingInvoked());
    }

    public void testSMDAliasedMethodCall2() throws Exception {
        // request
        setRequestContent("smd-15.txt");
        this.request.addHeader("content-type", "application/json-rpc");

        JSONInterceptor interceptor = new JSONInterceptor();
        interceptor.setEnableSMD(true);
        SMDActionTest2 action = new SMDActionTest2();

        this.invocation.setAction(action);

        interceptor.intercept(this.invocation);
        // method was aliased, but was invoked with the aliased name
        // so method must be invoked
        assertFalse(this.invocation.isInvoked());
        assertTrue(action.isDoSomethingInvoked());
    }

    public void testSMDNoMethod() throws Exception {
        // request
        setRequestContent("smd-4.txt");
        this.request.addHeader("content-type", "application/json-rpc");

        JSONInterceptor interceptor = new JSONInterceptor();
        interceptor.setEnableSMD(true);
        SMDActionTest1 action = new SMDActionTest1();

        this.invocation.setAction(action);

        // SMD was enabled so invocation must happen

        interceptor.intercept(this.invocation);

        String json = response.getContentAsString();

        String normalizedActual = TestUtils.normalize(json, true);
        String normalizedExpected = TestUtils.normalize(JSONResultTest.class.getResource("smd-13.txt"));
        assertEquals(normalizedExpected, normalizedActual);

        assertFalse(this.invocation.isInvoked());
    }

    public void testSMDMethodWithoutAnnotations() throws Exception {
        // request
        setRequestContent("smd-9.txt");
        this.request.addHeader("content-type", "application/json-rpc");

        JSONInterceptor interceptor = new JSONInterceptor();
        interceptor.setEnableSMD(true);
        SMDActionTest1 action = new SMDActionTest1();

        this.invocation.setAction(action);

        // SMD was enabled so invocation must happen
        try {
            interceptor.intercept(this.invocation);
            assertTrue("Exception was expected here!", true);
        } catch (Exception e) {
            // ok
        }
        assertFalse(this.invocation.isInvoked());
    }

    public void testSMDPrimitivesNoResult() throws Exception {
        // request
        setRequestContent("smd-6.txt");
        this.request.addHeader("content-type", "application/json-rpc");

        JSONInterceptor interceptor = new JSONInterceptor();
        interceptor.setEnableSMD(true);
        SMDActionTest1 action = new SMDActionTest1();

        this.invocation.setAction(action);

        // can't be invoked
        interceptor.intercept(this.invocation);
        assertFalse(this.invocation.isInvoked());

        // asert values were passed properly
        assertEquals("string", action.getStringParam());
        assertEquals(1, action.getIntParam());
        assertEquals(true, action.isBooleanParam());
        assertEquals('c', action.getCharParam());
        assertEquals(2, action.getLongParam());
        assertEquals(new Float(3.3), action.getFloatParam());
        assertEquals(4.4, action.getDoubleParam());
        assertEquals(5, action.getShortParam());
        assertEquals(6, action.getByteParam());

        String json = response.getContentAsString();

        String normalizedActual = TestUtils.normalize(json, true);
        String normalizedExpected = TestUtils.normalize(JSONResultTest.class.getResource("smd-11.txt"));
        assertEquals(normalizedExpected, normalizedActual);

        assertEquals("application/json;charset=ISO-8859-1", response.getContentType());
    }

    public void testSMDReturnObject() throws Exception {
        // request
        setRequestContent("smd-10.txt");
        this.request.addHeader("content-type", "application/json-rpc");

        JSONInterceptor interceptor = new JSONInterceptor();
        interceptor.setEnableSMD(true);
        SMDActionTest2 action = new SMDActionTest2();

        this.invocation.setAction(action);

        // can't be invoked
        interceptor.intercept(this.invocation);
        assertFalse(this.invocation.isInvoked());

        String json = response.getContentAsString();

        String normalizedActual = TestUtils.normalize(json, true);
        String normalizedExpected = TestUtils.normalize(JSONResultTest.class.getResource("smd-12.txt"));
        assertEquals(normalizedExpected, normalizedActual);

        assertEquals("application/json;charset=ISO-8859-1", response.getContentType());
    }

    @SuppressWarnings("unchecked")
    public void testSMDObjectsNoResult() throws Exception {
        // request
        setRequestContent("smd-7.txt");
        this.request.addHeader("content-type", "application/json-rpc");

        JSONInterceptor interceptor = new JSONInterceptor();
        interceptor.setEnableSMD(true);
        SMDActionTest1 action = new SMDActionTest1();

        this.invocation.setAction(action);

        // can't be invoked
        interceptor.intercept(this.invocation);
        assertFalse(this.invocation.isInvoked());

        // asert values were passed properly
        Bean bean = action.getBeanParam();
        assertNotNull(bean);
        assertTrue(bean.isBooleanField());
        assertEquals(bean.getStringField(), "test");
        assertEquals(bean.getIntField(), 10);
        assertEquals(bean.getCharField(), 's');
        assertEquals(bean.getDoubleField(), 10.1);
        assertEquals(bean.getByteField(), 3);

        List list = action.getListParam();
        assertNotNull(list);
        assertEquals("str0", list.get(0));
        assertEquals("str1", list.get(1));

        Map map = action.getMapParam();
        assertNotNull(map);
        assertNotNull(map.get("a"));
        assertEquals(new Long(1), map.get("a"));
        assertNotNull(map.get("c"));
        List insideList = (List) map.get("c");
        assertEquals(1.0d, insideList.get(0));
        assertEquals(2.0d, insideList.get(1));

        String json = response.getContentAsString();
        String normalizedActual = TestUtils.normalize(json, true);
        String normalizedExpected = TestUtils.normalize(JSONResultTest.class.getResource("smd-11.txt"));
        assertEquals(normalizedExpected, normalizedActual);

        assertEquals("application/json;charset=ISO-8859-1", response.getContentType());
    }

    @SuppressWarnings( { "unchecked", "unchecked" })
    public void testReadEmpty() throws Exception {
        // request
        setRequestContent("json-6.txt");
        this.request.addHeader("content-type", "application/json");

        // interceptor
        JSONInterceptor interceptor = new JSONInterceptor();
        TestAction action = new TestAction();

        this.invocation.setAction(action);

        interceptor.intercept(this.invocation);
    }

    @SuppressWarnings( { "unchecked", "unchecked" })
    public void test() throws Exception {
        // request
        setRequestContent("json-1.txt");
        this.request.addHeader("content-type", "application/json");

        // interceptor
        JSONInterceptor interceptor = new JSONInterceptor();
        TestAction action = new TestAction();

        this.invocation.setAction(action);
        this.invocation.getStack().push(action);

        interceptor.intercept(this.invocation);

        // serialize and compare
        List list = action.getList();

        assertNotNull(list);
        assertEquals(list.size(), 10);

        list = action.getCollection();
        assertNotNull(list);
        assertEquals(list.size(), 3);
        assertEquals(list.get(0), "b");
        assertEquals(list.get(1), 1L);
        list = (List) list.get(2);
        assertNotNull(list);
        assertEquals(list.size(), 2);
        assertEquals(list.get(0), 10L);
        assertEquals(list.get(1), 12L);

        list = action.getCollection2();
        assertNotNull(list);
        assertEquals(list.size(), 1);

        // inside a map any primitive is either: String, Long, Boolean or Double
        Map bean = (Map) list.get(0);

        assertNotNull(bean);
        assertTrue((Boolean) bean.get("booleanField"));
        assertEquals(bean.get("charField"), "s");
        assertEquals(bean.get("doubleField"), 10.1);
        assertEquals(bean.get("floatField"), 1.5);
        assertEquals(bean.get("intField"), 10L);
        assertEquals(bean.get("longField"), 100L);
        assertEquals(bean.get("stringField"), "str");

        bean = (Map) bean.get("objectField");
        assertNotNull(bean);
        assertFalse((Boolean) bean.get("booleanField"));
        assertEquals(bean.get("charField"), "\u0000");
        assertEquals(bean.get("doubleField"), 2.2);
        assertEquals(bean.get("floatField"), 1.1);
        assertEquals(bean.get("intField"), 0L);
        assertEquals(bean.get("longField"), 0L);
        assertEquals(bean.get("stringField"), "  ");

        assertEquals(action.getFoo(), "foo");

        Map map = action.getMap();

        assertNotNull(map);
        assertEquals(map.size(), 2);
        assertEquals(map.get("a"), 1L);
        list = (List) map.get("c");
        assertNotNull(list);
        assertEquals(list.size(), 2);
        assertEquals(list.get(0), 1.0);
        assertEquals(list.get(1), 2.0);

        assertEquals(action.getResult(), null);

        Bean bean2 = action.getBean();

        assertNotNull(bean2);
        assertTrue(bean2.isBooleanField());
        assertEquals(bean2.getStringField(), "test");
        assertEquals(bean2.getIntField(), 10);
        assertEquals(bean2.getCharField(), 's');
        assertEquals(bean2.getDoubleField(), 10.1);
        assertEquals(bean2.getByteField(), 3);

        String[] strArray = action.getArray();

        assertNotNull(strArray);
        assertEquals(strArray.length, 2);
        assertEquals(strArray[0], "str0");
        assertEquals(strArray[1], "str1");

        int[] intArray = action.getIntArray();

        assertNotNull(intArray);
        assertEquals(intArray.length, 2);
        assertEquals(intArray[0], 1);
        assertEquals(intArray[1], 2);

        Bean[] beanArray = action.getBeanArray();

        assertNotNull(beanArray);
        assertNotNull(beanArray[0]);
        assertEquals(beanArray[0].getStringField(), "bean1");
        assertNotNull(beanArray[1]);
        assertEquals(beanArray[1].getStringField(), "bean2");

        Calendar calendar = Calendar.getInstance();
        calendar.setTime(action.getDate());

        assertEquals(calendar.get(Calendar.YEAR), 1999);
        assertEquals(calendar.get(Calendar.MONTH), Calendar.DECEMBER);
        assertEquals(calendar.get(Calendar.DAY_OF_MONTH), 31);
        assertEquals(calendar.get(Calendar.HOUR), 11);
        assertEquals(calendar.get(Calendar.MINUTE), 59);
        assertEquals(calendar.get(Calendar.SECOND), 59);

        calendar.setTime(action.getDate2());
        assertEquals(calendar.get(Calendar.YEAR), 1999);
        assertEquals(calendar.get(Calendar.MONTH), Calendar.DECEMBER);
        assertEquals(calendar.get(Calendar.DAY_OF_MONTH), 31);

        // test desrialize=false
        assertNull(action.getFoo2());
    }

    public void testRoot() throws Exception {
        setRequestContent("json-5.txt");
        this.request.addHeader("content-type", "application/json");

        // interceptor
        JSONInterceptor interceptor = new JSONInterceptor();
        interceptor.setRoot("bean");
        TestAction4 action = new TestAction4();

        this.invocation.setAction(action);
        this.invocation.getStack().push(action);

        interceptor.intercept(this.invocation);

        Bean bean2 = action.getBean();

        assertNotNull(bean2);
        assertTrue(bean2.isBooleanField());
        assertEquals(bean2.getStringField(), "test");
        assertEquals(bean2.getIntField(), 10);
        assertEquals(bean2.getCharField(), 's');
        assertEquals(bean2.getDoubleField(), 10.1);
        assertEquals(bean2.getByteField(), 3);
    }
    
    public void testJSONArray() throws Exception {
        setRequestContent("json-12.txt");
        this.request.addHeader("content-type", "application/json");

        // interceptor
        JSONInterceptor interceptor = new JSONInterceptor();
        interceptor.setRoot("beans");
        TestAction5 action = new TestAction5();

        this.invocation.setAction(action);
        this.invocation.getStack().push(action);

        interceptor.intercept(this.invocation);

        List<Bean> beans = action.getBeans();

        assertNotNull(beans);
        assertEquals(1, beans.size());
        assertTrue(beans.get(0).isBooleanField());
        assertEquals(beans.get(0).getStringField(), "test");
        assertEquals(beans.get(0).getIntField(), 10);
        assertEquals(beans.get(0).getCharField(), 's');
        assertEquals(beans.get(0).getDoubleField(), 10.1);
        assertEquals(beans.get(0).getByteField(), 3);
    }

    public void testJSONArray2() throws Exception {
        setRequestContent("json-12.txt");
        this.request.addHeader("content-type", "application/json");

        // interceptor
        JSONInterceptor interceptor = new JSONInterceptor();
        interceptor.setRoot("anotherBean.yetAnotherBean.beans");
        TestAction5 action = new TestAction5();

        this.invocation.setAction(action);
        this.invocation.getStack().push(action);

        interceptor.intercept(this.invocation);

        List<Bean> beans = action.getAnotherBean().getYetAnotherBean().getBeans();

        assertNotNull(beans);
        assertEquals(1, beans.size());
        assertTrue(beans.get(0).isBooleanField());
        assertEquals(beans.get(0).getStringField(), "test");
        assertEquals(beans.get(0).getIntField(), 10);
        assertEquals(beans.get(0).getCharField(), 's');
        assertEquals(beans.get(0).getDoubleField(), 10.1);
        assertEquals(beans.get(0).getByteField(), 3);
    }
    
    @Override
    protected void setUp() throws Exception {
        super.setUp();

        this.request = new MockHttpServletRequest();
        this.response = new MockHttpServletResponse();

        ActionContext context = ActionContext.getContext();
        ValueStack stack = context.getValueStack();

        ActionContext.setContext(context);
        context.put(StrutsStatics.HTTP_REQUEST, this.request);
        context.put(StrutsStatics.HTTP_RESPONSE, this.response);

        MockServletContext servletContext = new MockServletContext();

        context.put(StrutsStatics.SERVLET_CONTEXT, servletContext);
        this.invocation = new MockActionInvocationEx();
        this.invocation.setInvocationContext(context);
        this.invocation.setStack(stack);
    }
}

class MockActionInvocationEx extends MockActionInvocation {
    private boolean invoked;

    @Override
    public String invoke() throws Exception {
        this.invoked = true;
        return super.invoke();
    }

    public boolean isInvoked() {
        return this.invoked;
    }

    public void setInvoked(boolean invoked) {
        this.invoked = invoked;
    }

}
