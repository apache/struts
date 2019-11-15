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
package com.opensymphony.xwork2.ognl;

import com.opensymphony.xwork2.ActionContext;
import com.opensymphony.xwork2.XWorkConstants;
import com.opensymphony.xwork2.XWorkException;
import com.opensymphony.xwork2.XWorkTestCase;
import com.opensymphony.xwork2.config.ConfigurationException;
import com.opensymphony.xwork2.conversion.impl.XWorkConverter;
import com.opensymphony.xwork2.inject.ContainerBuilder;
import com.opensymphony.xwork2.interceptor.ChainingInterceptor;
import com.opensymphony.xwork2.test.StubConfigurationProvider;
import com.opensymphony.xwork2.test.User;
import com.opensymphony.xwork2.util.*;
import com.opensymphony.xwork2.util.location.LocatableProperties;
import com.opensymphony.xwork2.util.reflection.ReflectionContextState;
import java.beans.IntrospectionException;
import ognl.*;
import org.apache.struts2.StrutsConstants;

import java.lang.reflect.Method;
import java.text.DateFormat;
import java.util.*;
import java.util.regex.Pattern;

public class OgnlUtilTest extends XWorkTestCase {

    // Fields for static field access test
    public static final String STATIC_FINAL_PUBLIC_ATTRIBUTE = "Static_Final_Public_Attribute";
    static final String STATIC_FINAL_PACKAGE_ATTRIBUTE = "Static_Final_Package_Attribute";
    protected static final String STATIC_FINAL_PROTECTED_ATTRIBUTE = "Static_Final_Protected_Attribute";
    private static final String STATIC_FINAL_PRIVATE_ATTRIBUTE = "Static_Final_Private_Attribute";
    public static String STATIC_PUBLIC_ATTRIBUTE = "Static_Public_Attribute";
    static String STATIC_PACKAGE_ATTRIBUTE = "Static_Package_Attribute";
    protected static String STATIC_PROTECTED_ATTRIBUTE = "Static_Protected_Attribute";
    private static String STATIC_PRIVATE_ATTRIBUTE = "Static_Private_Attribute";

    private OgnlUtil ognlUtil;
    
    @Override
    public void setUp() throws Exception {
        super.setUp();
        ognlUtil = container.getInstance(OgnlUtil.class);
    }
    
    public void testCanSetADependentObject() {
        String dogName = "fido";

        OgnlRuntime.setNullHandler(Owner.class, new NullHandler() {
            public Object nullMethodResult(Map map, Object o, String s, Object[] objects) {
                return null;
            }

            public Object nullPropertyValue(Map map, Object o, Object o1) {
                String methodName = o1.toString();
                String getter = "set" + methodName.substring(0, 1).toUpperCase() + methodName.substring(1);
                Method[] methods = o.getClass().getDeclaredMethods();
                System.out.println(getter);

                for (Method method : methods) {
                    String name = method.getName();

                    if (!getter.equals(name) || (method.getParameterTypes().length != 1)) {
                        continue;
                    } else {
                        Class clazz = method.getParameterTypes()[0];

                        try {
                            Object param = clazz.newInstance();
                            method.invoke(o, new Object[]{param});

                            return param;
                        } catch (Exception e) {
                            throw new RuntimeException(e);
                        }
                    }
                }

                return null;
            }
        });

        Owner owner = new Owner();
        Map context = ognlUtil.createDefaultContext(owner);
        Map props = new HashMap();
        props.put("dog.name", dogName);

        ognlUtil.setProperties(props, owner, context);
        assertNotNull("expected Ognl to create an instance of Dog", owner.getDog());
        assertEquals(dogName, owner.getDog().getName());
    }

    public void testCacheEnabled() throws OgnlException {
        ognlUtil.setEnableExpressionCache("true");
        Object expr0 = ognlUtil.compile("test");
        Object expr2 = ognlUtil.compile("test");
        assertSame(expr0, expr2);
    }

    public void testClearExpressionCache() throws OgnlException {
        ognlUtil.setEnableExpressionCache("true");
        // Test that the expression cache is functioning as expected.
        Object expr0 = ognlUtil.compile("test");
        Object expr1 = ognlUtil.compile("test");
        Object expr2 = ognlUtil.compile("test");
        // Cache in effect, so expr0, expr1, expr2 should be the same.
        assertSame(expr0, expr1);
        assertSame(expr0, expr2);
        assertTrue("Expression cache empty before clear ?", ognlUtil.expressionCacheSize() > 0);
        // Clear the Epxression cache and confirm subsequent requests are new.
        ognlUtil.clearExpressionCache();
        assertTrue("Expression cache not empty after clear ?", ognlUtil.expressionCacheSize() == 0);
        Object expr3 = ognlUtil.compile("test");
        Object expr4 = ognlUtil.compile("test");
        Object expr5 = ognlUtil.compile("test");
        // Cache cleared, expr3 should be a new instance.
        assertNotSame(expr0, expr3);
        // Cache still in effect, so expr3, expr4, expr5 should be the same.
        assertSame(expr3, expr4);
        assertSame(expr3, expr5);
        assertTrue("Expression cache empty after usage ?", ognlUtil.expressionCacheSize() > 0);
    }

    public void testClearBeanInfoCache() throws IntrospectionException {
        final TestBean1 testBean1 = new TestBean1();
        final TestBean2 testBean2 = new TestBean2();
        // Test that the BeanInfo cache is functioning as expected.
        Object beanInfo1_1 = ognlUtil.getBeanInfo(testBean1);
        Object beanInfo1_2 = ognlUtil.getBeanInfo(testBean1);
        Object beanInfo1_3 = ognlUtil.getBeanInfo(testBean1);
        // Cache in effect, so beanInfo1_1, beanInfo1_2, beanInfo1_3 should be the same.
        assertSame(beanInfo1_1, beanInfo1_2);
        assertSame(beanInfo1_1, beanInfo1_3);
        Object beanInfo2_1 = ognlUtil.getBeanInfo(testBean2);
        Object beanInfo2_2 = ognlUtil.getBeanInfo(testBean2);
        Object beanInfo2_3 = ognlUtil.getBeanInfo(testBean2);
        // Cache in effect, so beanInfo2_1, beanInfo2_2, beanInfo2_3 should be the same.
        assertSame(beanInfo2_1, beanInfo2_2);
        assertSame(beanInfo2_1, beanInfo2_3);
        // BeanInfo for TestBean1 and TestBean2 should always be different.
        assertNotSame(beanInfo1_1, beanInfo2_1);
        assertTrue("BeanInfo cache empty before clear ?", ognlUtil.beanInfoCacheSize() > 0);
        // Clear the BeanInfo cache and confirm subsequent requests are new.
        ognlUtil.clearBeanInfoCache();
        assertTrue("BeanInfo cache not empty after clear ?", ognlUtil.beanInfoCacheSize() == 0);
        Object beanInfo1_4 = ognlUtil.getBeanInfo(testBean1);
        Object beanInfo1_5 = ognlUtil.getBeanInfo(testBean1);
        Object beanInfo1_6 = ognlUtil.getBeanInfo(testBean1);
        // Cache in effect, so beanInfo1_4, beanInfo1_5, beanInfo1_6 should be the same.
        assertSame(beanInfo1_4, beanInfo1_5);
        assertSame(beanInfo1_4, beanInfo1_6);
        // Cache was cleared in-between, so beanInfo1_1/beanInfo1_2/beanInfo1_3 should differ
        // from beanInfo1_4/beanInfo1_5/beanInfo1_6.
        assertNotSame(beanInfo1_1, beanInfo1_4);
        assertNotSame(beanInfo1_2, beanInfo1_5);
        assertNotSame(beanInfo1_3, beanInfo1_6);
        Object beanInfo2_4 = ognlUtil.getBeanInfo(testBean2);
        Object beanInfo2_5 = ognlUtil.getBeanInfo(testBean2);
        Object beanInfo2_6 = ognlUtil.getBeanInfo(testBean2);
        // Cache in effect, so beanInfo2_4, beanInfo2_5, beanInfo2_6 should be the same.
        assertSame(beanInfo2_4, beanInfo2_5);
        assertSame(beanInfo2_4, beanInfo2_6);
        // Cache was cleared in-between, so beanInfo2_1/beanInfo2_2/beanInfo2_3 should differ
        // from beanInfo2_4/beanInfo2_5/beanInfo2_6.
        assertNotSame(beanInfo2_1, beanInfo2_4);
        assertNotSame(beanInfo2_2, beanInfo2_5);
        assertNotSame(beanInfo2_3, beanInfo2_6);
        // BeanInfo for TestBean1 and TestBean2 should always be different.
        assertNotSame(beanInfo1_4, beanInfo2_4);
        assertTrue("BeanInfo cache empty after usage ?", ognlUtil.beanInfoCacheSize() > 0);
    }

    public void testClearRuntimeCache() {
        // Confirm that no exceptions or failures arise when calling the convenience global clear method.
        OgnlUtil.clearRuntimeCache();
    }

     public void testCacheDisabled() throws OgnlException {
        ognlUtil.setEnableExpressionCache("false");
        Object expr0 = ognlUtil.compile("test");
        Object expr2 = ognlUtil.compile("test");
        assertNotSame(expr0, expr2);
    }

    public void testCanSetDependentObjectArray() {
        EmailAction action = new EmailAction();
        Map<String, Object> context = ognlUtil.createDefaultContext(action);

        Map<String, Object> props = new HashMap<String, Object>();
        props.put("email[0].address", "addr1");
        props.put("email[1].address", "addr2");
        props.put("email[2].address", "addr3");

        ognlUtil.setProperties(props, action, context);
        assertEquals(3, action.email.size());
        assertEquals("addr1", action.email.get(0).toString());
        assertEquals("addr2", action.email.get(1).toString());
        assertEquals("addr3", action.email.get(2).toString());
    }

    public void testCopySameType() {
        Foo foo1 = new Foo();
        Foo foo2 = new Foo();

        Map context = ognlUtil.createDefaultContext(foo1);

        Calendar cal = Calendar.getInstance();
        cal.clear();
        cal.set(Calendar.MONTH, Calendar.FEBRUARY);
        cal.set(Calendar.DAY_OF_MONTH, 12);
        cal.set(Calendar.YEAR, 1982);

        foo1.setTitle("blah");
        foo1.setNumber(1);
        foo1.setPoints(new long[]{1, 2, 3});
        foo1.setBirthday(cal.getTime());
        foo1.setUseful(false);

        ognlUtil.copy(foo1, foo2, context);

        assertEquals(foo1.getTitle(), foo2.getTitle());
        assertEquals(foo1.getNumber(), foo2.getNumber());
        assertEquals(foo1.getPoints(), foo2.getPoints());
        assertEquals(foo1.getBirthday(), foo2.getBirthday());
        assertEquals(foo1.isUseful(), foo2.isUseful());
    }


    public void testIncudeExcludes() {

        Foo foo1 = new Foo();
        Foo foo2 = new Foo();

        Calendar cal = Calendar.getInstance();
        cal.clear();
        cal.set(Calendar.MONTH, Calendar.FEBRUARY);
        cal.set(Calendar.DAY_OF_MONTH, 12);
        cal.set(Calendar.YEAR, 1982);

        foo1.setPoints(new long[]{1, 2, 3});
        foo1.setBirthday(cal.getTime());
        foo1.setUseful(false);


        foo1.setTitle("foo1 title");
        foo1.setNumber(1);

        foo2.setTitle("foo2 title");
        foo2.setNumber(2);

        Map<String, Object> context = ognlUtil.createDefaultContext(foo1);

        List<String> excludes = new ArrayList<String>();
        excludes.add("title");
        excludes.add("number");

        ognlUtil.copy(foo1, foo2, context, excludes, null);
        // these values should remain unchanged in foo2
        assertEquals(foo2.getTitle(), "foo2 title");
        assertEquals(foo2.getNumber(), 2);

        // these values should be changed/copied
        assertEquals(foo1.getPoints(), foo2.getPoints());
        assertEquals(foo1.getBirthday(), foo2.getBirthday());
        assertEquals(foo1.isUseful(), foo2.isUseful());


        Bar b1 = new Bar();
        Bar b2 = new Bar();

        b1.setTitle("bar1 title");
        b1.setSomethingElse(10);


        b1.setId(new Long(1));

        b2.setTitle("");
        b2.setId(new Long(2));

        context = ognlUtil.createDefaultContext(b1);
        List<String> includes = new ArrayList<String>();
        includes.add("title");
        includes.add("somethingElse");

        ognlUtil.copy(b1, b2, context, null, includes);
        // includes properties got copied
        assertEquals(b1.getTitle(), b2.getTitle());
        assertEquals(b1.getSomethingElse(), b2.getSomethingElse());

        // id properties did not
        assertEquals(b2.getId(), new Long(2));

    }

    public void testCopyEditable() {
        Foo foo1 = new Foo();
        Foo foo2 = new Foo();

        Map<String, Object> context = ognlUtil.createDefaultContext(foo1);

        Calendar cal = Calendar.getInstance();
        cal.clear();
        cal.set(Calendar.MONTH, Calendar.MAY);
        cal.set(Calendar.DAY_OF_MONTH, 29);
        cal.set(Calendar.YEAR, 2017);

        foo1.setTitle("blah");
        foo1.setNumber(1);
        foo1.setPoints(new long[]{1, 2, 3});
        foo1.setBirthday(cal.getTime());
        foo1.setUseful(false);

        ognlUtil.copy(foo1, foo2, context, null, null, Bar.class);

        assertEquals(foo1.getTitle(), foo2.getTitle());
        assertEquals(0, foo2.getNumber());
        assertNull(foo2.getPoints());
        assertNull(foo2.getBirthday());
    }


    public void testCopyUnevenObjects() {
        Foo foo = new Foo();
        Bar bar = new Bar();

        Map<String, Object> context = ognlUtil.createDefaultContext(foo);

        Calendar cal = Calendar.getInstance();
        cal.clear();
        cal.set(Calendar.MONTH, Calendar.FEBRUARY);
        cal.set(Calendar.DAY_OF_MONTH, 12);
        cal.set(Calendar.YEAR, 1982);

        foo.setTitle("blah");
        foo.setNumber(1);
        foo.setPoints(new long[]{1, 2, 3});
        foo.setBirthday(cal.getTime());
        foo.setUseful(false);

        ognlUtil.copy(foo, bar, context);

        assertEquals(foo.getTitle(), bar.getTitle());
        assertEquals(0, bar.getSomethingElse());
    }

    public void testDeepSetting() {
        Foo foo = new Foo();
        foo.setBar(new Bar());

        Map<String, Object> context = ognlUtil.createDefaultContext(foo);

        Map<String, Object> props = new HashMap();
        props.put("bar.title", "i am barbaz");
        ognlUtil.setProperties(props, foo, context);

        assertEquals(foo.getBar().getTitle(), "i am barbaz");
    }

    public void testNoExceptionForUnmatchedGetterAndSetterWithThrowPropertyException() {
        Map<String, Object> props = new HashMap<String, Object>();
        props.put("myIntegerProperty", new Integer(1234));

        TestObject testObject = new TestObject();

        //this used to fail in OGNL versions < 2.7
        ognlUtil.setProperties(props, testObject, true);
        assertEquals(1234, props.get("myIntegerProperty"));
    }

    public void testExceptionForWrongPropertyNameWithThrowPropertyException() {
        Map<String, Object> props = new HashMap<String, Object>();
        props.put("myStringProperty", "testString");

        TestObject testObject = new TestObject();

        try {
            ognlUtil.setProperties(props, testObject, true);
            fail("Should rise NoSuchPropertyException because of wrong property name");
        } catch (Exception e) {
            //expected
        }
    }

    public void testOgnlHandlesCrapAtTheEndOfANumber() {
        Foo foo = new Foo();
        Map<String, Object> context = ognlUtil.createDefaultContext(foo);

        Map<String, Object> props = new HashMap<String, Object>();
        props.put("aLong", "123a");

        ognlUtil.setProperties(props, foo, context);
        assertEquals(0, foo.getALong());
    }

    /**
     * Test that type conversion is performed on indexed collection properties.
     */
    public void testSetIndexedValue() {
        ValueStack stack = ActionContext.getContext().getValueStack();
        Map<String, Object> stackContext = stack.getContext();
        stackContext.put(ReflectionContextState.CREATE_NULL_OBJECTS, Boolean.TRUE);
        stackContext.put(ReflectionContextState.DENY_METHOD_EXECUTION, Boolean.TRUE);
        stackContext.put(XWorkConverter.REPORT_CONVERSION_ERRORS, Boolean.TRUE);

        User user = new User();
        stack.push(user);

        // indexed string w/ existing array
        user.setList(new ArrayList<String>());
        user.getList().add("");

        String[] foo = new String[]{"asdf"};
        stack.setValue("list[0]", foo);
        assertNotNull(user.getList());
        assertEquals(1, user.getList().size());
        assertEquals(String.class, user.getList().get(0).getClass());
        assertEquals("asdf", user.getList().get(0));
    }

    public void testSetPropertiesBoolean() {
        Foo foo = new Foo();

        Map context = ognlUtil.createDefaultContext(foo);

        Map props = new HashMap();
        props.put("useful", "true");
        ognlUtil.setProperties(props, foo, context);

        assertEquals(true, foo.isUseful());

        props = new HashMap();
        props.put("useful", "false");
        ognlUtil.setProperties(props, foo, context);

        assertEquals(false, foo.isUseful());
    }

    public void testSetPropertiesDate() {
        Foo foo = new Foo();

        Map context = ognlUtil.createDefaultContext(foo);

        Map props = new HashMap();
        props.put("birthday", "02/12/1982");
        // US style test
        context.put(ActionContext.LOCALE, Locale.US);
        ognlUtil.setProperties(props, foo, context);

        Calendar cal = Calendar.getInstance(Locale.US);
        cal.clear();
        cal.set(Calendar.MONTH, Calendar.FEBRUARY);
        cal.set(Calendar.DAY_OF_MONTH, 12);
        cal.set(Calendar.YEAR, 1982);

        assertEquals(cal.getTime(), foo.getBirthday());

        //UK style test
        cal = Calendar.getInstance(Locale.UK);
        cal.clear();
        cal.set(Calendar.MONTH, Calendar.OCTOBER);
        cal.set(Calendar.DAY_OF_MONTH, 18);
        cal.set(Calendar.YEAR, 2006);
        cal.set(Calendar.HOUR_OF_DAY, 14);
        cal.set(Calendar.MINUTE, 23);
        cal.set(Calendar.SECOND, 45);

        Date eventTime = cal.getTime();
        String formatted = DateFormat.getDateTimeInstance(DateFormat.SHORT, DateFormat.MEDIUM, Locale.UK)
                .format(eventTime);
        props.put("event", formatted);

        cal = Calendar.getInstance(Locale.UK);
        cal.clear();
        cal.set(Calendar.MONTH, Calendar.SEPTEMBER);
        cal.set(Calendar.DAY_OF_MONTH, 9);
        cal.set(Calendar.YEAR, 2006);
        cal.set(Calendar.HOUR_OF_DAY, 14);
        cal.set(Calendar.MINUTE, 30);

        Date meetingTime = cal.getTime();
        formatted = DateFormat.getDateTimeInstance(DateFormat.SHORT, DateFormat.MEDIUM, Locale.UK)
                .format(meetingTime);
        props.put("meeting", formatted);

        context.put(ActionContext.LOCALE, Locale.UK);

        ognlUtil.setProperties(props, foo, context);

        assertEquals(eventTime, foo.getEvent());

        assertEquals(meetingTime, foo.getMeeting());
        
        //test RFC 3339 date format for JSON
        props.put("event", "1996-12-19T16:39:57Z");
        context.put(ActionContext.LOCALE, Locale.US);
        ognlUtil.setProperties(props, foo, context);
        
        cal = Calendar.getInstance(Locale.US);
        cal.clear();
        cal.set(Calendar.MONTH, Calendar.DECEMBER);
        cal.set(Calendar.DAY_OF_MONTH, 19);
        cal.set(Calendar.YEAR, 1996);
        cal.set(Calendar.HOUR_OF_DAY, 16);
        cal.set(Calendar.MINUTE, 39);
        cal.set(Calendar.SECOND, 57);
        
        assertEquals(cal.getTime(), foo.getEvent());
        
        //test setting a calendar property
        props.put("calendar", "1996-12-19T16:39:57Z");
        context.put(ActionContext.LOCALE, Locale.US);
        ognlUtil.setProperties(props, foo, context);
        assertEquals(cal, foo.getCalendar());
    }

    public void testSetPropertiesInt() {
        Foo foo = new Foo();

        Map context = ognlUtil.createDefaultContext(foo);

        Map props = new HashMap();
        props.put("number", "2");
        ognlUtil.setProperties(props, foo, context);

        assertEquals(2, foo.getNumber());
    }

    public void testSetPropertiesLongArray() {
        Foo foo = new Foo();

        Map context = ognlUtil.createDefaultContext(foo);

        Map props = new HashMap();
        props.put("points", new String[]{"1", "2"});
        ognlUtil.setProperties(props, foo, context);

        assertNotNull(foo.getPoints());
        assertEquals(2, foo.getPoints().length);
        assertEquals(1, foo.getPoints()[0]);
        assertEquals(2, foo.getPoints()[1]);
    }

    public void testSetPropertiesString() {
        Foo foo = new Foo();

        Map context = ognlUtil.createDefaultContext(foo);

        Map props = new HashMap();
        props.put("title", "this is a title");
        ognlUtil.setProperties(props, foo, context);

        assertEquals(foo.getTitle(), "this is a title");
    }

    public void testSetProperty() {
        Foo foo = new Foo();
        Map context = ognlUtil.createDefaultContext(foo);
        assertFalse(123456 == foo.getNumber());
        ognlUtil.setProperty("number", "123456", foo, context);
        assertEquals(123456, foo.getNumber());
    }


    public void testSetList() throws Exception {
        ChainingInterceptor foo = new ChainingInterceptor();
        ChainingInterceptor foo2 = new ChainingInterceptor();

        OgnlContext context = (OgnlContext) ognlUtil.createDefaultContext(null);
        SimpleNode expression = (SimpleNode) Ognl.parseExpression("{'a','ruby','b','tom'}");


        Ognl.getValue(expression, context, "aksdj");

        final ValueStack stack = ActionContext.getContext().getValueStack();

        Object result = Ognl.getValue(ognlUtil.compile("{\"foo\",'ruby','b','tom'}"), context, foo);
        foo.setIncludesCollection((Collection) result);

        assertEquals(4, foo.getIncludes().size());
        assertEquals("foo", foo.getIncludes().toArray()[0]);
        assertEquals("ruby", foo.getIncludes().toArray()[1]);
        assertEquals("b", "" + foo.getIncludes().toArray()[2]);
        assertEquals("tom", foo.getIncludes().toArray()[3]);

        Object result2 = Ognl.getValue(ognlUtil.compile("{\"foo\",'ruby','b','tom'}"), context, foo2);
        ognlUtil.setProperty("includes", result2, foo2, context);

        assertEquals(4, foo.getIncludes().size());
        assertEquals("foo", foo.getIncludes().toArray()[0]);
        assertEquals("ruby", foo.getIncludes().toArray()[1]);
        assertEquals("b", "" + foo.getIncludes().toArray()[2]);
        assertEquals("tom", foo.getIncludes().toArray()[3]);

        result = ActionContext.getContext().getValueStack().findValue("{\"foo\",'ruby','b','tom'}");

        foo.setIncludesCollection((Collection) result);
        assertEquals(ArrayList.class, result.getClass());

        assertEquals(4, foo.getIncludes().size());
        assertEquals("foo", foo.getIncludes().toArray()[0]);
        assertEquals("ruby", foo.getIncludes().toArray()[1]);
        assertEquals("b", "" + foo.getIncludes().toArray()[2]);
        assertEquals("tom", foo.getIncludes().toArray()[3]);
    }


    public void testStringToLong() {
        Foo foo = new Foo();

        Map context = ognlUtil.createDefaultContext(foo);

        Map props = new HashMap();
        props.put("ALong", "123");

        ognlUtil.setProperties(props, foo, context);
        assertEquals(123, foo.getALong());

        props.put("ALong", new String[]{"123"});

        foo.setALong(0);
        ognlUtil.setProperties(props, foo, context);
        assertEquals(123, foo.getALong());
    }

    public void testNullProperties() {
        Foo foo = new Foo();
        foo.setALong(88);

        Map context = ognlUtil.createDefaultContext(foo);

        ognlUtil.setProperties(null, foo, context);
        assertEquals(88, foo.getALong());

        Map props = new HashMap();
        props.put("ALong", "99");
        ognlUtil.setProperties(props, foo, context);
        assertEquals(99, foo.getALong());
    }
    
    public void testCopyNull() {
        Foo foo = new Foo();
        Map context = ognlUtil.createDefaultContext(foo);
   		ognlUtil.copy(null, null, context);

   		ognlUtil.copy(foo, null, context);
   		ognlUtil.copy(null, foo, context);
    }
    
    public void testGetTopTarget() throws Exception {
        Foo foo = new Foo();
        Map context = ognlUtil.createDefaultContext(foo);

        CompoundRoot root = new CompoundRoot();
        Object top = ognlUtil.getRealTarget("top", context, root);
        assertEquals(root, top); // top should be root
        
        root.push(foo);
        Object val = ognlUtil.getRealTarget("unknown", context, root);
        assertNull(val); // not found
    }
    
    public void testGetBeanMap() throws Exception {
    	Bar bar = new Bar();
    	bar.setTitle("I have beer");
        
    	Foo foo = new Foo();
        foo.setALong(123);
        foo.setNumber(44);
        foo.setBar(bar);
        foo.setTitle("Hello Santa");
        foo.setUseful(true);
        
        // just do some of the 15 tests
        Map beans = ognlUtil.getBeanMap(foo);
        assertNotNull(beans);
        assertEquals(21, beans.size());
        assertEquals("Hello Santa", beans.get("title"));
        assertEquals(new Long("123"), beans.get("ALong"));
        assertEquals(new Integer("44"), beans.get("number"));
        assertEquals(bar, beans.get("bar"));
        assertEquals(Boolean.TRUE, beans.get("useful"));
    }

    public void testGetBeanMapNoReadMethod() throws Exception {
    	MyWriteBar bar = new MyWriteBar();
    	bar.setBar("Sams");
    	
    	Map beans = ognlUtil.getBeanMap(bar);
    	assertEquals(2, beans.size());
    	assertEquals(new Integer("1"), beans.get("id"));
    	assertEquals("There is no read method for bar", beans.get("bar"));
    }

    /**
	 * XW-281
	 */
    public void testSetBigIndexedValue() {
        ValueStack stack = ActionContext.getContext().getValueStack();
        Map stackContext = stack.getContext();
        stackContext.put(ReflectionContextState.CREATE_NULL_OBJECTS, Boolean.FALSE);
        stackContext.put(ReflectionContextState.DENY_METHOD_EXECUTION, Boolean.TRUE);
        stackContext.put(XWorkConverter.REPORT_CONVERSION_ERRORS, Boolean.TRUE);

        User user = new User();
        stack.push(user);

        // indexed string w/ existing array
        user.setList(new ArrayList());

        String[] foo = new String[]{"asdf"};
        ((OgnlValueStack)stack).setDevMode("true");
        try {
            stack.setValue("list.1114778947765", foo);
            fail("non-valid expression: list.1114778947765"); 
        }
        catch(RuntimeException ex) {
            ; // it's oke
        }
        
        try {
            stack.setValue("1114778947765", foo);
            fail("non-valid expression: 1114778947765"); 
        }
        catch(RuntimeException ex) {
            ;
        }
        
        try {
            stack.setValue("1234", foo);
            fail("non-valid expression: 1234");
        }
        catch(RuntimeException ex) {
            ;
        }
        
        ((OgnlValueStack)stack).setDevMode("false");
        stack.setValue("list.1114778947765", foo);
        stack.setValue("1114778947765", foo);
        stack.setValue("1234", foo);
    }

    public void testStackValueDevModeChange() throws Exception {

        try {
            reloadTestContainerConfiguration(false, false);  // Set dev mode false
        }
        catch (Exception ex) {
            fail("Unable to reload container configuration - exception: " + ex);
        }

        ValueStack stack = ActionContext.getContext().getValueStack();
        Map stackContext = stack.getContext();
        stackContext.put(ReflectionContextState.CREATE_NULL_OBJECTS, Boolean.FALSE);
        stackContext.put(ReflectionContextState.DENY_METHOD_EXECUTION, Boolean.TRUE);
        stackContext.put(XWorkConverter.REPORT_CONVERSION_ERRORS, Boolean.TRUE);

        String[] foo = new String[]{"asdf"};

        // With dev mode false, the following set values should not cause failures
        stack.setValue("list.1114778947765", foo);
        stack.setValue("1114778947765", foo);
        stack.setValue("1234", foo);

        try {
            reloadTestContainerConfiguration(true, false);  // Set dev mode true
        }
        catch (Exception ex) {
            fail("Unable to reload container configuration - exception: " + ex);
        }

        // Repeat stack/context set after retrieving updated stack
        stack = ActionContext.getContext().getValueStack();
        stackContext = stack.getContext();
        stackContext.put(ReflectionContextState.CREATE_NULL_OBJECTS, Boolean.FALSE);
        stackContext.put(ReflectionContextState.DENY_METHOD_EXECUTION, Boolean.TRUE);
        stackContext.put(XWorkConverter.REPORT_CONVERSION_ERRORS, Boolean.TRUE);

        try {
            stack.setValue("list.1114778947765", foo);
            fail("non-valid expression: list.1114778947765");
        }
        catch(RuntimeException ex) {
            // Expected with dev mode true
        }
        try {
            stack.setValue("1114778947765", foo);
            fail("non-valid expression: 1114778947765");
        }
        catch(RuntimeException ex) {
            // Expected with dev mode true
        }
        try {
            stack.setValue("1234", foo);
            fail("non-valid expression: 1234");
        }
        catch(RuntimeException ex) {
            // Expected with dev mode true
        }

    }

    public void testDevModeChange() throws Exception {

        try {
            reloadTestContainerConfiguration(false, false);  // Set dev mode false
        }
        catch (Exception ex) {
            fail("Unable to reload container configuration - exception: " + ex);
        }

        ValueStack stack = ActionContext.getContext().getValueStack();
        Map stackContext = stack.getContext();
        stackContext.put(ReflectionContextState.CREATE_NULL_OBJECTS, Boolean.FALSE);
        stackContext.put(ReflectionContextState.DENY_METHOD_EXECUTION, Boolean.TRUE);
        stackContext.put(XWorkConverter.REPORT_CONVERSION_ERRORS, Boolean.TRUE);

        String[] foo = new String[]{"asdf"};

        // With dev mode false, the following set values should not cause failures
        stack.setValue("list.1114778947765", foo);
        stack.setValue("1114778947765", foo);
        stack.setValue("1234", foo);

        try {
            reloadTestContainerConfiguration(true, false);  // Set dev mode true
        }
        catch (Exception ex) {
            fail("Unable to reload container configuration - exception: " + ex);
        }

        // Repeat stack/context set after retrieving updated stack
        stack = ActionContext.getContext().getValueStack();
        stackContext = stack.getContext();
        stackContext.put(ReflectionContextState.CREATE_NULL_OBJECTS, Boolean.FALSE);
        stackContext.put(ReflectionContextState.DENY_METHOD_EXECUTION, Boolean.TRUE);
        stackContext.put(XWorkConverter.REPORT_CONVERSION_ERRORS, Boolean.TRUE);

        try {
            stack.setValue("list.1114778947765", foo);
            fail("non-valid expression: list.1114778947765");
        }
        catch(RuntimeException ex) {
            // Expected with dev mode true
        }
        try {
            stack.setValue("1114778947765", foo);
            fail("non-valid expression: 1114778947765");
        }
        catch(RuntimeException ex) {
            // Expected with dev mode true
        }
        try {
            stack.setValue("1234", foo);
            fail("non-valid expression: 1234");
        }
        catch(RuntimeException ex) {
            // Expected with dev mode true
        }

    }

    public void testAvoidCallingMethodsOnObjectClass() throws Exception {
        Foo foo = new Foo();

        Exception expected = null;
        try {
            ognlUtil.setExcludedClasses(Object.class.getName());
            ognlUtil.setValue("class.classLoader.defaultAssertionStatus", ognlUtil.createDefaultContext(foo), foo, true);
            fail();
        } catch (OgnlException e) {
            expected = e;
        }
        assertNotNull(expected);
        assertSame(NoSuchPropertyException.class, expected.getClass());
        assertEquals("com.opensymphony.xwork2.util.Foo.class", expected.getMessage());
    }

    public void testAvoidCallingMethodsOnObjectClassUpperCased() throws Exception {
        Foo foo = new Foo();

        Exception expected = null;
        try {
            ognlUtil.setExcludedClasses(Object.class.getName());
            ognlUtil.setValue("Class.ClassLoader.DefaultAssertionStatus", ognlUtil.createDefaultContext(foo), foo, true);
            fail();
        } catch (OgnlException e) {
            expected = e;
        }
        assertNotNull(expected);
        assertSame(NoSuchPropertyException.class, expected.getClass());
        assertEquals("com.opensymphony.xwork2.util.Foo.Class", expected.getMessage());
    }

    public void testAvoidCallingMethodsOnObjectClassAsMap() throws Exception {
        Foo foo = new Foo();

        Exception expected = null;
        try {
            ognlUtil.setExcludedClasses(Object.class.getName());
            ognlUtil.setValue("class['classLoader']['defaultAssertionStatus']", ognlUtil.createDefaultContext(foo), foo, true);
            fail();
        } catch (OgnlException e) {
            expected = e;
        }
        assertNotNull(expected);
        assertSame(NoSuchPropertyException.class, expected.getClass());
        assertEquals("com.opensymphony.xwork2.util.Foo.class", expected.getMessage());
    }

    public void testAvoidCallingMethodsOnObjectClassAsMap2() throws Exception {
        Foo foo = new Foo();

        Exception expected = null;
        try {
            ognlUtil.setValue("foo['class']['classLoader']['defaultAssertionStatus']", ognlUtil.createDefaultContext(foo), foo, true);
            fail();
        } catch (OgnlException e) {
            expected = e;
        }
        assertNotNull(expected);
        assertSame(NoSuchPropertyException.class, expected.getClass());
        assertEquals("com.opensymphony.xwork2.util.Foo.foo", expected.getMessage());
    }

    public void testAvoidCallingMethodsOnObjectClassAsMapWithQuotes() throws Exception {
        Foo foo = new Foo();

        Exception expected = null;
        try {
            ognlUtil.setExcludedClasses(Object.class.getName());
            ognlUtil.setValue("class[\"classLoader\"]['defaultAssertionStatus']", ognlUtil.createDefaultContext(foo), foo, true);
            fail();
        } catch (OgnlException e) {
            expected = e;
        }
        assertNotNull(expected);
        assertSame(NoSuchPropertyException.class, expected.getClass());
        assertEquals("com.opensymphony.xwork2.util.Foo.class", expected.getMessage());
    }

    public void testAvoidCallingToString() throws Exception {
        Foo foo = new Foo();

        Exception expected = null;
        try {
            ognlUtil.setValue("toString", ognlUtil.createDefaultContext(foo), foo, null);
            fail();
        } catch (OgnlException e) {
            expected = e;
        }
        assertNotNull(expected);
        assertSame(OgnlException.class, expected.getClass());
        assertEquals("toString", expected.getMessage());
    }

    public void testAvoidCallingMethodsWithBraces() throws Exception {
        Foo foo = new Foo();

        Exception expected = null;
        try {
            ognlUtil.setValue("toString()", ognlUtil.createDefaultContext(foo), foo, true);
            fail();
        } catch (OgnlException e) {
            expected = e;
        }
        assertNotNull(expected);
        assertSame(InappropriateExpressionException.class, expected.getClass());
        assertEquals(expected.getMessage(), "Inappropriate OGNL expression: toString()");
    }

    public void testAvoidCallingSomeClasses() throws Exception {
        Foo foo = new Foo();

        Exception expected = null;
        try {
            ognlUtil.setExcludedClasses(Runtime.class.getName());
            ognlUtil.setValue("@java.lang.Runtime@getRuntime().exec('mate')", ognlUtil.createDefaultContext(foo), foo, true);
            fail();
        } catch (OgnlException e) {
            expected = e;
        }
        assertNotNull(expected);
        assertSame(MethodFailedException.class, expected.getClass());
        assertEquals(expected.getMessage(), "Method \"getRuntime\" failed for object class java.lang.Runtime");
    }

    public void testBlockSequenceOfExpressions() throws Exception {
        Foo foo = new Foo();

        Exception expected = null;
        try {
            ognlUtil.setValue("#booScope=@myclass@DEFAULT_SCOPE,#bootScope.init()", ognlUtil.createDefaultContext(foo), foo, true);
            fail();
        } catch (OgnlException e) {
            expected = e;
        }
        assertNotNull(expected);
        assertSame(OgnlException.class, expected.getClass());
        assertEquals(expected.getMessage(), "Eval expressions/chained expressions have been disabled!");
    }

    public void testCallMethod() throws Exception {
        Foo foo = new Foo();

        Exception expected = null;
        try {
            ognlUtil.callMethod("#booScope=@myclass@DEFAULT_SCOPE,#bootScope.init()", ognlUtil.createDefaultContext(foo), foo);
            fail();
        } catch (OgnlException e) {
            expected = e;
        }
        assertNotNull(expected);
        assertSame(OgnlException.class, expected.getClass());
        assertEquals(expected.getMessage(), "It isn't a simple method which can be called!");
    }

    public void testXworkTestCaseOgnlUtilExclusions() throws Exception {
        internalTestInitialEmptyOgnlUtilExclusions(ognlUtil);
        internalTestOgnlUtilExclusionsImmutable(ognlUtil);
    }

    public void testDefaultOgnlUtilExclusions() throws Exception {
        OgnlUtil basicOgnlUtil = new OgnlUtil();

        internalTestInitialEmptyOgnlUtilExclusions(basicOgnlUtil);
        internalTestOgnlUtilExclusionsImmutable(basicOgnlUtil);
    }

    public void testOgnlUtilExcludedAdditivity() throws Exception {
        Set<Class<?>> excludedClasses;
        Set<Pattern> excludedPackageNamePatterns;
        Iterator<Pattern> excludedPackageNamePatternsIterator;
        Set<String> excludedPackageNames;
        Set<String> patternStrings = new HashSet<>();

        ognlUtil.setExcludedClasses("java.lang.String,java.lang.Integer");
        internalTestOgnlUtilExclusionsImmutable(ognlUtil);
        excludedClasses = ognlUtil.getExcludedClasses();
        assertNotNull("initial exluded classes null?", excludedClasses);
        assertTrue("initial exluded classes size not 2 after adds?", excludedClasses.size() == 2);
        assertTrue("String not in exclusions?", excludedClasses.contains(String.class));
        assertTrue("Integer not in exclusions?", excludedClasses.contains(Integer.class));
        ognlUtil.setExcludedClasses("java.lang.Boolean,java.lang.Double");
        internalTestOgnlUtilExclusionsImmutable(ognlUtil);
        excludedClasses = ognlUtil.getExcludedClasses();
        assertNotNull("updated exluded classes null?", excludedClasses);
        assertTrue("updated exluded classes size not 4 after adds?", excludedClasses.size() == 4);
        assertTrue("String not in exclusions?", excludedClasses.contains(String.class));
        assertTrue("Integer not in exclusions?", excludedClasses.contains(Integer.class));
        assertTrue("String not in exclusions?", excludedClasses.contains(Boolean.class));
        assertTrue("Integer not in exclusions?", excludedClasses.contains(Double.class));

        ognlUtil.setExcludedPackageNamePatterns("fakepackage1.*,fakepackage2.*");
        internalTestOgnlUtilExclusionsImmutable(ognlUtil);
        excludedPackageNamePatterns = ognlUtil.getExcludedPackageNamePatterns();
        assertNotNull("initial exluded package name patterns null?", excludedPackageNamePatterns);
        assertTrue("initial exluded package name patterns size not 2 after adds?", excludedPackageNamePatterns.size() == 2);
        excludedPackageNamePatternsIterator = excludedPackageNamePatterns.iterator();
        patternStrings.clear();
        while (excludedPackageNamePatternsIterator.hasNext()) {
            Pattern pattern = excludedPackageNamePatternsIterator.next();
            patternStrings.add(pattern.pattern());
        }
        assertTrue("fakepackage1.* not in exclusions?", patternStrings.contains("fakepackage1.*"));
        assertTrue("fakepackage2.* not in exclusions?", patternStrings.contains("fakepackage2.*"));
        ognlUtil.setExcludedPackageNamePatterns("fakepackage3.*,fakepackage4.*");
        internalTestOgnlUtilExclusionsImmutable(ognlUtil);
        excludedPackageNamePatterns = ognlUtil.getExcludedPackageNamePatterns();
        assertNotNull("updated exluded package name patterns null?", excludedPackageNamePatterns);
        assertTrue("updated exluded package name patterns size not 4 after adds?", excludedPackageNamePatterns.size() == 4);
        excludedPackageNamePatternsIterator = excludedPackageNamePatterns.iterator();
        patternStrings.clear();
        while (excludedPackageNamePatternsIterator.hasNext()) {
            Pattern pattern = excludedPackageNamePatternsIterator.next();
            patternStrings.add(pattern.pattern());
        }
        assertTrue("fakepackage1.* not in exclusions?", patternStrings.contains("fakepackage1.*"));
        assertTrue("fakepackage2.* not in exclusions?", patternStrings.contains("fakepackage2.*"));
        assertTrue("fakepackage3.* not in exclusions?", patternStrings.contains("fakepackage3.*"));
        assertTrue("fakepackage4.* not in exclusions?", patternStrings.contains("fakepackage4.*"));

        ognlUtil.setExcludedPackageNames("fakepackage1.package,fakepackage2.package");
        internalTestOgnlUtilExclusionsImmutable(ognlUtil);
        excludedPackageNames = ognlUtil.getExcludedPackageNames();
        assertNotNull("initial exluded package names null?", excludedPackageNames);
        assertTrue("initial exluded package names not 2 after adds?", excludedPackageNames.size() == 2);
        assertTrue("fakepackage1.package not in exclusions?", excludedPackageNames.contains("fakepackage1.package"));
        assertTrue("fakepackage2.package not in exclusions?", excludedPackageNames.contains("fakepackage2.package"));
        ognlUtil.setExcludedPackageNames("fakepackage3.package,fakepackage4.package");
        internalTestOgnlUtilExclusionsImmutable(ognlUtil);
        excludedPackageNames = ognlUtil.getExcludedPackageNames();
        assertNotNull("updated exluded package names null?", excludedPackageNames);
        assertTrue("updated exluded package names not 4 after adds?", excludedPackageNames.size() == 4);
        assertTrue("fakepackage1.package not in exclusions?", excludedPackageNames.contains("fakepackage1.package"));
        assertTrue("fakepackage2.package not in exclusions?", excludedPackageNames.contains("fakepackage2.package"));
        assertTrue("fakepackage3.package not in exclusions?", excludedPackageNames.contains("fakepackage3.package"));
        assertTrue("fakepackage4.package not in exclusions?", excludedPackageNames.contains("fakepackage4.package"));
    }

    /**
     * Ensure getValue permits public static field access, but prevents non-public static field access
     */
    public void testStaticFieldGetValue() {
        OgnlContext context = null;
        Object accessedValue;

        try {
            reloadTestContainerConfiguration(false, false);  // Test with allow static methods false
            context = (OgnlContext) ognlUtil.createDefaultContext(null);
        } catch (Exception ex) {
            fail("unable to reload test configuration? Exception: " + ex);
        }
        try {
            accessedValue = ognlUtil.getValue("@com.opensymphony.xwork2.ognl.OgnlUtilTest@STATIC_FINAL_PUBLIC_ATTRIBUTE", context, null);
            assertEquals("accessed field value not equal to actual?", accessedValue, STATIC_FINAL_PUBLIC_ATTRIBUTE);
        } catch (Exception ex) {
            fail("static final public field access failed ? Exception: " + ex);
        }
        try {
            accessedValue = ognlUtil.getValue("@com.opensymphony.xwork2.ognl.OgnlUtilTest@STATIC_PUBLIC_ATTRIBUTE", context, null);
            assertEquals("accessed field value not equal to actual?", accessedValue, STATIC_PUBLIC_ATTRIBUTE);
        } catch (Exception ex) {
            fail("static public field access failed ? Exception: " + ex);
        }
        try {
            accessedValue = ognlUtil.getValue("@com.opensymphony.xwork2.ognl.OgnlUtilTest@STATIC_FINAL_PACKAGE_ATTRIBUTE", context, null);
            fail("static final package field access succeeded?");
        } catch (Exception ex) {
            assertTrue("Exception not an OgnlException?", ex instanceof OgnlException);
        }
        try {
            accessedValue = ognlUtil.getValue("@com.opensymphony.xwork2.ognl.OgnlUtilTest@STATIC_PACKAGE_ATTRIBUTE", context, null);
            fail("static package field access succeeded?");
        } catch (Exception ex) {
            assertTrue("Exception not an OgnlException?", ex instanceof OgnlException);
        }
        try {
            accessedValue = ognlUtil.getValue("@com.opensymphony.xwork2.ognl.OgnlUtilTest@STATIC_FINAL_PROTECTED_ATTRIBUTE", context, null);
            fail("static final protected field access succeeded?");
        } catch (Exception ex) {
            assertTrue("Exception not an OgnlException?", ex instanceof OgnlException);
        }
        try {
            accessedValue = ognlUtil.getValue("@com.opensymphony.xwork2.ognl.OgnlUtilTest@STATIC_PROTECTED_ATTRIBUTE", context, null);
            fail("static protected field access succeeded?");
        } catch (Exception ex) {
            assertTrue("Exception not an OgnlException?", ex instanceof OgnlException);
        }
        try {
            accessedValue = ognlUtil.getValue("@com.opensymphony.xwork2.ognl.OgnlUtilTest@STATIC_FINAL_PRIVATE_ATTRIBUTE", context, null);
            fail("static final private field access succeeded?");
        } catch (Exception ex) {
            assertTrue("Exception not an OgnlException?", ex instanceof OgnlException);
        }
        try {
            accessedValue = ognlUtil.getValue("@com.opensymphony.xwork2.ognl.OgnlUtilTest@STATIC_PRIVATE_ATTRIBUTE", context, null);
            fail("static private field access succeeded?");
        } catch (Exception ex) {
            assertTrue("Exception not an OgnlException?", ex instanceof OgnlException);
        }

        try {
            reloadTestContainerConfiguration(false, true);  // Re-test with allow static methods true
            context = (OgnlContext) ognlUtil.createDefaultContext(null);
        } catch (Exception ex) {
            fail("unable to reload test configuration? Exception: " + ex);
        }
        try {
            accessedValue = ognlUtil.getValue("@com.opensymphony.xwork2.ognl.OgnlUtilTest@STATIC_FINAL_PUBLIC_ATTRIBUTE", context, null);
            assertEquals("accessed value not equal to actual?", accessedValue, STATIC_FINAL_PUBLIC_ATTRIBUTE);
        } catch (Exception ex) {
            fail("static final public field access failed ? Exception: " + ex);
        }
        try {
            accessedValue = ognlUtil.getValue("@com.opensymphony.xwork2.ognl.OgnlUtilTest@STATIC_PUBLIC_ATTRIBUTE", context, null);
            assertEquals("accessed value not equal to actual?", accessedValue, STATIC_PUBLIC_ATTRIBUTE);
        } catch (Exception ex) {
            fail("static public field access failed ? Exception: " + ex);
        }
        try {
            accessedValue = ognlUtil.getValue("@com.opensymphony.xwork2.ognl.OgnlUtilTest@STATIC_FINAL_PACKAGE_ATTRIBUTE", context, null);
            fail("static final package field access succeeded?");
        } catch (Exception ex) {
            assertTrue("Exception not an OgnlException?", ex instanceof OgnlException);
        }
        try {
            accessedValue = ognlUtil.getValue("@com.opensymphony.xwork2.ognl.OgnlUtilTest@STATIC_PACKAGE_ATTRIBUTE", context, null);
            fail("static package field access succeeded?");
        } catch (Exception ex) {
            assertTrue("Exception not an OgnlException?", ex instanceof OgnlException);
        }
        try {
            accessedValue = ognlUtil.getValue("@com.opensymphony.xwork2.ognl.OgnlUtilTest@STATIC_FINAL_PROTECTED_ATTRIBUTE", context, null);
            fail("static final protected field access succeeded?");
        } catch (Exception ex) {
            assertTrue("Exception not an OgnlException?", ex instanceof OgnlException);
        }
        try {
            accessedValue = ognlUtil.getValue("@com.opensymphony.xwork2.ognl.OgnlUtilTest@STATIC_PROTECTED_ATTRIBUTE", context, null);
            fail("static protected field access succeeded?");
        } catch (Exception ex) {
            assertTrue("Exception not an OgnlException?", ex instanceof OgnlException);
        }
        try {
            accessedValue = ognlUtil.getValue("@com.opensymphony.xwork2.ognl.OgnlUtilTest@STATIC_FINAL_PRIVATE_ATTRIBUTE", context, null);
            fail("static final private field access succeeded?");
        } catch (Exception ex) {
            assertTrue("Exception not an OgnlException?", ex instanceof OgnlException);
        }
        try {
            accessedValue = ognlUtil.getValue("@com.opensymphony.xwork2.ognl.OgnlUtilTest@STATIC_PRIVATE_ATTRIBUTE", context, null);
            fail("static private field access succeeded?");
        } catch (Exception ex) {
            assertTrue("Exception not an OgnlException?", ex instanceof OgnlException);
        }
    }

    /**
     * Test OGNL Expression Max Length feature setting via OgnlUtil is disabled by default (in default.properties).
     * 
     * @since 2.5.21
     */
    public void testDefaultExpressionMaxLengthDisabled() {
        final String LONG_OGNL_EXPRESSION = "true == ThisIsAReallyLongOGNLExpressionOfRepeatedGarbageText." + new String(new char[65535]).replace('\0', 'A');  // Expression larger than 64KB.
        try {
            Object compileResult = ognlUtil.compile(LONG_OGNL_EXPRESSION);
            assertNotNull("Long OGNL expression compilation produced a null result ?", compileResult);
        } catch (OgnlException oex) {
             if (oex.getReason() instanceof SecurityException) {
                 fail ("Unable to compile expression (unexpected).  'struts.ognl.expressionMaxLength' may have accidentally been enabled by default.  Exception: " + oex);
             } else {
                 fail ("Unable to compile expression (unexpected).  Exception: " + oex);
             }
        } catch (Exception ex) {
            fail ("Unable to compile expression (unexpected).  Exception: " + ex);
        }
    }

    /**
     * Test OGNL Expression Max Length feature setting via OgnlUtil.
     * 
     * @since 2.5.21
     */
    public void testApplyExpressionMaxLength() {
        try {
            try {
                ognlUtil.applyExpressionMaxLength(null);
            } catch (Exception ex) {
                fail ("applyExpressionMaxLength did not accept null maxlength string (disable feature) ?");
            }
            try {
                ognlUtil.applyExpressionMaxLength("");
            } catch (Exception ex) {
                fail ("applyExpressionMaxLength did not accept empty maxlength string (disable feature) ?");
            }
            try {
                ognlUtil.applyExpressionMaxLength("-1");
                fail ("applyExpressionMaxLength accepted negative maxlength string ?");
            } catch (IllegalArgumentException iae) {
                // Expected rejection of -ive length.
            }
            try {
                ognlUtil.applyExpressionMaxLength("0");
            } catch (Exception ex) {
                fail ("applyExpressionMaxLength did not accept maxlength string 0 ?");
            }
            try {
                ognlUtil.applyExpressionMaxLength(Integer.toString(Integer.MAX_VALUE, 10));
            } catch (Exception ex) {
                fail ("applyExpressionMaxLength did not accept MAX_VALUE maxlength string ?");
            }
        } finally {
            // Reset expressionMaxLength value to default (disabled)
            ognlUtil.applyExpressionMaxLength(null);
        }
    }

    private void internalTestInitialEmptyOgnlUtilExclusions(OgnlUtil ognlUtilParam) throws Exception {
        Set<Class<?>> excludedClasses = ognlUtilParam.getExcludedClasses();
        assertNotNull("parameter (default) exluded classes null?", excludedClasses);
        assertTrue("parameter (default) exluded classes not empty?", excludedClasses.isEmpty());

        Set<Pattern> excludedPackageNamePatterns = ognlUtilParam.getExcludedPackageNamePatterns();
        assertNotNull("parameter (default) exluded package name patterns null?", excludedPackageNamePatterns);
        assertTrue("parameter (default) exluded package name patterns not empty?", excludedPackageNamePatterns.isEmpty());

        Set<String> excludedPackageNames = ognlUtilParam.getExcludedPackageNames();
        assertNotNull("parameter (default) exluded package names null?", excludedPackageNames);
        assertTrue("parameter (default) exluded package names not empty?", excludedPackageNames.isEmpty());
    }

    private void internalTestOgnlUtilExclusionsImmutable(OgnlUtil ognlUtilParam) throws Exception {
        Pattern somePattern = Pattern.compile("SomeRegexPattern");
        Set<Class<?>> excludedClasses = ognlUtilParam.getExcludedClasses();
        assertNotNull("parameter exluded classes null?", excludedClasses);
        try {
            excludedClasses.clear();
            fail("parameter excluded classes modifiable?");
        } catch (UnsupportedOperationException uoe) {
            // Expected failure
        }
        try {
            excludedClasses.add(Integer.class);
            fail("parameter excluded classes modifiable?");
        } catch (UnsupportedOperationException uoe) {
            // Expected failure
        }
        try {
            excludedClasses.remove(Integer.class);
            fail("parameter excluded classes modifiable?");
        } catch (UnsupportedOperationException uoe) {
            // Expected failure
        }

        Set<Pattern> excludedPackageNamePatterns = ognlUtilParam.getExcludedPackageNamePatterns();
        assertNotNull("parameter exluded package name patterns null?", excludedPackageNamePatterns);
        try {
            excludedPackageNamePatterns.clear();
            fail("parameter excluded package name patterns modifiable?");
        } catch (UnsupportedOperationException uoe) {
            // Expected failure
        }
        try {
            excludedPackageNamePatterns.add(somePattern);
            fail("parameter excluded package name patterns modifiable?");
        } catch (UnsupportedOperationException uoe) {
            // Expected failure
        }
        try {
            excludedPackageNamePatterns.remove(somePattern);
            fail("parameter excluded package name patterns modifiable?");
        } catch (UnsupportedOperationException uoe) {
            // Expected failure
        }

        Set<String> excludedPackageNames = ognlUtilParam.getExcludedPackageNames();
        assertNotNull("parameter exluded package names null?", excludedPackageNames);
        try {
            excludedPackageNames.clear();
            fail("parameter excluded package names modifiable?");
        } catch (UnsupportedOperationException uoe) {
            // Expected failure
        }
        try {
            excludedPackageNames.add("somepackagename");
            fail("parameter excluded package names modifiable?");
        } catch (UnsupportedOperationException uoe) {
            // Expected failure
        }
        try {
            excludedPackageNames.remove("somepackagename");
            fail("parameter excluded package names modifiable?");
        } catch (UnsupportedOperationException uoe) {
            // Expected failure
        }
    }

    private void reloadTestContainerConfiguration(final boolean devMode, final boolean allowStatic) throws Exception {
        loadConfigurationProviders(new StubConfigurationProvider() {
            @Override
            public void register(ContainerBuilder builder,
                                 LocatableProperties props) throws ConfigurationException {
                props.setProperty(StrutsConstants.STRUTS_DEVMODE, "" + devMode);
                props.setProperty(XWorkConstants.DEV_MODE, "" + devMode);
                props.setProperty(StrutsConstants.STRUTS_ALLOW_STATIC_METHOD_ACCESS, "" + allowStatic);
                props.setProperty(XWorkConstants.ALLOW_STATIC_METHOD_ACCESS, "" + allowStatic);
            }
        });
    }

    public static class Email {
        String address;

        public void setAddress(String address) {
            this.address = address;
        }

        @Override
        public String toString() {
            return address;
        }
    }

    static class TestObject {
        private Integer myIntegerProperty;
        private Long myLongProperty;
        private String myStrProperty;

        public void setMyIntegerProperty(Integer myIntegerProperty) {
            this.myIntegerProperty = myIntegerProperty;
        }

        public String getMyIntegerProperty() {
            return myIntegerProperty.toString();
        }

        public void setMyLongProperty(Long myLongProperty) {
            this.myLongProperty = myLongProperty;
        }

        public Long getMyLongProperty() {
            return myLongProperty;
        }

        public void setMyStrProperty(String myStrProperty) {
            this.myStrProperty = myStrProperty;
        }

        public String getMyStrProperty() {
            return myStrProperty;
        }
    }

    class EmailAction {
        public List email = new OgnlList(Email.class);

        public List getEmail() {
            return this.email;
        }
    }

    class OgnlList extends ArrayList {
        private Class clazz;

        public OgnlList(Class clazz) {
            this.clazz = clazz;
        }

        @Override
        public synchronized Object get(int index) {
            while (index >= this.size()) {
                try {
                    this.add(clazz.newInstance());
                } catch (Exception e) {
                    throw new XWorkException(e);
                }
            }

            return super.get(index);
        }
    }
    
    private class MyWriteBar {
    	private int id;
    	
    	public int getId() {
    		return id;
    	}
    	
    	public void setBar(String name) {
    		if ("Sams".equals(name))
    			id = 1;
    		else
    			id = 999;
    	}
    	
    }

    class TestBean1 {
        private String testBeanProperty;

        public TestBean1() {
            testBeanProperty = "defaultTestBean1Property";
        }

        public String getTestBeanProperty() {
            return testBeanProperty;
        }

        public void setTestBeanProperty(String testBeanProperty) {
            this.testBeanProperty = testBeanProperty;
        }
    }

    class TestBean2 {
        private String testBeanProperty;

        public TestBean2() {
            testBeanProperty = "defaultTestBean2Property";
        }

        public String getTestBeanProperty() {
            return testBeanProperty;
        }

        public void setTestBeanProperty(String testBeanProperty) {
            this.testBeanProperty = testBeanProperty;
        }
    }

}
