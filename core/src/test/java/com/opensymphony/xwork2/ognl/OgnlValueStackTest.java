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

import com.opensymphony.xwork2.*;
import com.opensymphony.xwork2.config.ConfigurationException;
import com.opensymphony.xwork2.conversion.impl.ConversionData;
import com.opensymphony.xwork2.conversion.impl.XWorkConverter;
import com.opensymphony.xwork2.inject.ContainerBuilder;
import com.opensymphony.xwork2.ognl.accessor.CompoundRootAccessor;
import com.opensymphony.xwork2.test.StubConfigurationProvider;
import com.opensymphony.xwork2.test.TestBean2;
import com.opensymphony.xwork2.util.*;
import com.opensymphony.xwork2.util.Foo;
import com.opensymphony.xwork2.util.location.LocatableProperties;
import com.opensymphony.xwork2.util.reflection.ReflectionContextState;
import ognl.OgnlException;
import ognl.PropertyAccessor;

import java.io.*;
import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import ognl.ParseException;

import org.apache.commons.lang3.StringUtils;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.core.LogEvent;
import org.apache.logging.log4j.core.Logger;
import org.apache.logging.log4j.core.appender.AbstractAppender;
import org.apache.struts2.StrutsConstants;
import org.apache.struts2.StrutsException;
import org.apache.struts2.config.DefaultPropertiesProvider;


/**
 * Unit test for OgnlValueStack.
 */
public class OgnlValueStackTest extends XWorkTestCase {

    // Fields for static field access test
    public static final String STATIC_FINAL_PUBLIC_ATTRIBUTE = "Static_Final_Public_Attribute";
    static final String STATIC_FINAL_PACKAGE_ATTRIBUTE = "Static_Final_Package_Attribute";
    protected static final String STATIC_FINAL_PROTECTED_ATTRIBUTE = "Static_Final_Protected_Attribute";
    private static final String STATIC_FINAL_PRIVATE_ATTRIBUTE = "Static_Final_Private_Attribute";
    public static String STATIC_PUBLIC_ATTRIBUTE = "Static_Public_Attribute";
    static String STATIC_PACKAGE_ATTRIBUTE = "Static_Package_Attribute";
    protected static String STATIC_PROTECTED_ATTRIBUTE = "Static_Protected_Attribute";
    private static String STATIC_PRIVATE_ATTRIBUTE = "Static_Private_Attribute";


    public static Integer staticNullMethod() {
        return null;
    }

    public static Integer staticInteger100Method() {
        return 100;
    }

    private OgnlUtil ognlUtil;

    @Override
    public void setUp() throws Exception {
        super.setUp();
        ognlUtil = container.getInstance(OgnlUtil.class);
    }

    private OgnlValueStack createValueStack() {
        return createValueStack(true, true);
    }

    private OgnlValueStack createValueStack(boolean allowStaticMethodAccess, boolean allowStaticFieldAccess) {
        OgnlValueStack stack = new OgnlValueStack(
                container.getInstance(XWorkConverter.class),
                (CompoundRootAccessor) container.getInstance(PropertyAccessor.class, CompoundRoot.class.getName()),
                container.getInstance(TextProvider.class, "system"), allowStaticMethodAccess, allowStaticFieldAccess);
        container.inject(stack);
        ognlUtil.setAllowStaticMethodAccess(Boolean.toString(allowStaticMethodAccess));
        ognlUtil.setAllowStaticFieldAccess(Boolean.toString(allowStaticFieldAccess));
        return stack;
    }

    /**
     * @return current OgnlValueStackFactory instance from current container
     */
    private OgnlValueStackFactory getValueStackFactory() {
        return (OgnlValueStackFactory) container.getInstance(ValueStackFactory.class);
    }

    /**
     * Reloads container and gets a new OgnlValueStackFactory with specified new configuration.
     * Intended for testing OgnlValueStack instance(s) that are minimally configured.
     * This should help ensure no underlying configuration/injection side-effects are responsible
     * for the behaviour of fundamental access control flags).
     * 
     * @param allowStaticMethod new allowStaticMethod configuration
     * @param allowStaticField new allowStaticField configuration
     * @return a new OgnlValueStackFactory with specified new configuration
     */
    private OgnlValueStackFactory reloadValueStackFactory(Boolean allowStaticMethod, Boolean allowStaticField) {
        try {
            reloadTestContainerConfiguration(allowStaticMethod, allowStaticField);
        }
        catch (Exception ex) {
            fail("Unable to reload container configuration and configure ognlValueStackFactory - exception: " + ex);
        }

        return getValueStackFactory();
    }

    public void testExpOverridesCanStackExpUp() throws Exception {
        Map expr1 = new LinkedHashMap();
        expr1.put("expr1", "'expr1value'");

        OgnlValueStack vs = createValueStack();
        vs.setExprOverrides(expr1);

        assertEquals(vs.findValue("expr1"), "expr1value");

        Map expr2 = new LinkedHashMap();
        expr2.put("expr2", "'expr2value'");
        expr2.put("expr3", "'expr3value'");
        vs.setExprOverrides(expr2);

        assertEquals(vs.findValue("expr2"), "expr2value");
        assertEquals(vs.findValue("expr3"), "expr3value");
    }


    public void testArrayAsString() {
        OgnlValueStack vs = createValueStack();

        Dog dog = new Dog();
        dog.setAge(12);
        dog.setName("Rover");
        dog.setChildAges(new int[]{1, 2});

        vs.push(dog);
        assertEquals("1, 2", vs.findValue("childAges", String.class));
    }

    public void testValuesFromContextAreConverted() {
        testValuesFromContextAreConverted("dogName");
        testValuesFromContextAreConverted("dog.name");
    }

    private void testValuesFromContextAreConverted(String propertyName) {
        final OgnlValueStack vs = createValueStack();
        final String propertyValue = "Rover";
        vs.getContext().put(propertyName, new String[]{propertyValue});

        assertEquals(propertyValue, vs.findValue(propertyName, String.class));
    }

    public void testNullValueFromContextGetsConverted() {
        testNullValueFromContextGetsConverted("dogName");
        testNullValueFromContextGetsConverted("dog.name");
    }

    private void testNullValueFromContextGetsConverted(String propertyName) {
        final OgnlValueStack vs = createValueStack();
        final String propertyValue = null;
        vs.getContext().put(propertyName, propertyValue);

        assertEquals(propertyValue, vs.findValue(propertyName, String.class));
    }

    public void testFailOnException() {
        OgnlValueStack vs = createValueStack();

        Dog dog = new Dog();
        vs.push(dog);
        try {
            vs.findValue("bite", true);
            fail("Failed to throw exception on EL error");
        } catch (Exception ex) {
            //ok
        }
    }

    public void testFailOnErrorOnInheritedProperties() {
        //this shuld not fail as the property is defined on a parent class
        OgnlValueStack vs = createValueStack();

        Foo foo = new Foo();
        BarJunior barjr = new BarJunior();
        foo.setBarJunior(barjr);
        vs.push(foo);

        assertNull(barjr.getTitle());
        vs.findValue("barJunior.title", true);
    }

     public void testSuccessFailOnErrorOnInheritedPropertiesWithMethods() {
        //this shuld not fail as the property is defined on a parent class
        OgnlValueStack vs = createValueStack();

        Foo foo = new Foo();
        BarJunior barjr = new BarJunior();
        foo.setBarJunior(barjr);
        vs.push(foo);

        assertNull(barjr.getTitle());
        vs.findValue("getBarJunior().title", true);
    }

    public void testFailFailOnErrorOnInheritedPropertiesWithMethods() {
        OgnlValueStack vs = createValueStack();

        Foo foo = new Foo();
        BarJunior barjr = new BarJunior();
        foo.setBarJunior(barjr);
        vs.push(foo);

        assertNull(barjr.getTitle());
        try {
            vs.findValue("getBarJunior().title2", true);
            fail("should have failed on missing property");
        } catch (Exception e) {
        }
    }

    public void testFailOnMissingProperty() {
        OgnlValueStack vs = createValueStack();

        Dog dog = new Dog();
        vs.push(dog);
        try {
            vs.findValue("someprop", true);
            fail("Failed to throw exception on EL missing property");
        } catch (Exception ex) {
            //ok
        }
    }

    /**
     * monitors the resolution of WW-4999
     * @since 2.5.21
     */
    public void testLogMissingProperties() {
        testLogMissingProperties(true);
        testLogMissingProperties(false);
    }

    private void testLogMissingProperties(boolean logMissingProperties) {
        OgnlValueStack vs = createValueStack();
        vs.setLogMissingProperties("" + logMissingProperties);

        Dog dog = new Dog();
        vs.push(dog);

        TestAppender testAppender = new TestAppender();
        Logger logger = (Logger) LogManager.getLogger(OgnlValueStack.class);
        logger.addAppender(testAppender);
        testAppender.start();

        try {
            vs.setValue("missingProp1", "missingProp1Value", false);
            vs.findValue("missingProp2", false);
            vs.findValue("missingProp3", Integer.class, false);

            if (logMissingProperties) {
                assertEquals(3, testAppender.logEvents.size());
                assertEquals("Error setting value [missingProp1Value] with expression [missingProp1]",
                        testAppender.logEvents.get(0).getMessage().getFormattedMessage());
                assertEquals("Could not find property [missingProp2]!",
                        testAppender.logEvents.get(1).getMessage().getFormattedMessage());
                assertEquals("Could not find property [missingProp3]!",
                        testAppender.logEvents.get(2).getMessage().getFormattedMessage());
            } else {
                assertEquals(0, testAppender.logEvents.size());
            }
        } finally {
            testAppender.stop();
            logger.removeAppender(testAppender);
        }
    }

    /**
     * tests the correctness of distinguishing between user exception and NoSuchMethodException
     * @since 2.5.21
     */
    public void testNotLogUserExceptionsAsMissingProperties() {
        OgnlValueStack vs = createValueStack();
        vs.setLogMissingProperties("true");

        Dog dog = new Dog();
        vs.push(dog);

        TestAppender testAppender = new TestAppender();
        Logger logger = (Logger) LogManager.getLogger(OgnlValueStack.class);
        logger.addAppender(testAppender);
        testAppender.start();

        try {
            vs.setValue("exception", "exceptionValue", false);
            vs.findValue("exception", false);
            vs.findValue("exception", String.class, false);
            vs.findValue("getException()", false);
            vs.findValue("getException()", String.class, false);
            vs.findValue("bite", false);
            vs.findValue("bite", void.class, false);
            vs.findValue("getBite()", false);
            vs.findValue("getBite()", void.class, false);

            vs.setLogMissingProperties("false");

            vs.setValue("exception", "exceptionValue", false);
            vs.findValue("exception", false);
            vs.findValue("exception", String.class, false);
            vs.findValue("getException()", false);
            vs.findValue("getException()", String.class, false);
            vs.findValue("bite", false);
            vs.findValue("bite", void.class, false);
            vs.findValue("getBite()", false);
            vs.findValue("getBite()", void.class, false);

            assertEquals(0, testAppender.logEvents.size());
        } finally {
            testAppender.stop();
            logger.removeAppender(testAppender);
        }
    }

    public void testFailOnMissingMethod() {
        OgnlValueStack vs = createValueStack();

        Dog dog = new Dog();
        vs.push(dog);
        try {
            vs.findValue("someprop()", true);
            fail("Failed to throw exception on EL missing method");
        } catch (Exception ex) {
            //ok
        }
    }

    public void testFailOnTooLongExpressionLongerThan192_ViaOverriddenProperty() {
        try {
            loadConfigurationProviders(new StubConfigurationProvider() {
                @Override
                public void register(ContainerBuilder builder,
                                     LocatableProperties props) throws ConfigurationException {
                    props.setProperty(StrutsConstants.STRUTS_OGNL_EXPRESSION_MAX_LENGTH, "192");
                }
            });
            Integer repeat = Integer.parseInt(
                    container.getInstance(String.class, StrutsConstants.STRUTS_OGNL_EXPRESSION_MAX_LENGTH));

            OgnlValueStack vs = createValueStack();
            try {
                vs.findValue(StringUtils.repeat('.', repeat + 1), true);
                fail("Failed to throw exception on too long expression");
            } catch (Exception ex) {
                assertTrue(ex.getCause() instanceof OgnlException);
                assertTrue(((OgnlException) ex.getCause()).getReason() instanceof SecurityException);
            }
        } finally {
            // Reset expressionMaxLength value to default (disabled)
            ognlUtil.applyExpressionMaxLength(null);
        }
    }

    public void testNotFailOnTooLongExpressionWithDefaultProperties() {
        loadConfigurationProviders(new DefaultPropertiesProvider());

        Object defaultMaxLengthFromConfiguration = container.getInstance(String.class, StrutsConstants.STRUTS_OGNL_EXPRESSION_MAX_LENGTH);
        if (defaultMaxLengthFromConfiguration != null) {
            assertTrue("non-null defaultMaxLengthFromConfiguration not a String ?", defaultMaxLengthFromConfiguration instanceof String);
            assertTrue("non-null defaultMaxLengthFromConfiguration not empty string by default ?", ((String) defaultMaxLengthFromConfiguration).length() == 0);
        } else {
            assertNull("defaultMaxLengthFromConfiguration not null ?", defaultMaxLengthFromConfiguration);
        }
        // Original test logic was to confirm failure of exceeding the default value.  Now the feature should be disabled by default,
        // so this test's expectations are now changed.
        Integer repeat = Integer.valueOf(256);  // Since maxlength is disabled by default, just choose an arbitrary value for test

        OgnlValueStack vs = createValueStack();
        try {
            vs.findValue(StringUtils.repeat('.', repeat + 1), true);
            fail("findValue did not throw any exception (should either fail as invalid expression syntax or security exception) ?");
        } catch (Exception ex) {
            // If STRUTS_OGNL_EXPRESSION_MAX_LENGTH feature is disabled (default), the parse should fail due to a reason of invalid expression syntax
            // with ParseException.  Previously when it was enabled the reason for the failure would have been SecurityException.
            assertTrue(ex.getCause() instanceof OgnlException);
            assertTrue(((OgnlException) ex.getCause()).getReason() instanceof ParseException);
        }
    }

    public void testNotFailOnTooLongValueWithDefaultProperties() {
        try {
            loadConfigurationProviders(new DefaultPropertiesProvider());

            Object defaultMaxLengthFromConfiguration = container.getInstance(String.class, StrutsConstants.STRUTS_OGNL_EXPRESSION_MAX_LENGTH);
            if (defaultMaxLengthFromConfiguration != null) {
                assertTrue("non-null defaultMaxLengthFromConfiguration not a String ?", defaultMaxLengthFromConfiguration instanceof String);
                assertTrue("non-null defaultMaxLengthFromConfiguration not empty string by default ?", ((String) defaultMaxLengthFromConfiguration).length() == 0);
            } else {
                assertNull("defaultMaxLengthFromConfiguration not null ?", defaultMaxLengthFromConfiguration);
            }
            // Original test logic is unchanged (testing that values can be larger than maximum expression length), but since the feature is disabled by
            // default we will now have to enable it with an arbitrary value, test, and reset it to disabled.
            Integer repeat = Integer.valueOf(256);  // Since maxlength is disabled by default, just choose an arbitrary value for test

            // Apply a non-default value for expressionMaxLength (as it should be disabled by default)
            try {
                ognlUtil.applyExpressionMaxLength(repeat.toString());
            } catch (Exception ex) {
                fail ("applyExpressionMaxLength did not accept maxlength string " + repeat.toString() + " ?");
            }

            OgnlValueStack vs = createValueStack();

            Dog dog = new Dog();
            vs.push(dog);

            String value = StringUtils.repeat('.', repeat + 1);

            vs.setValue("name", value);

            assertEquals(value, dog.getName());
        } finally {
            // Reset expressionMaxLength value to default (disabled)
            ognlUtil.applyExpressionMaxLength(null);
        }
    }

    public void testFailsOnMethodThatThrowsException() {
        SimpleAction action = new SimpleAction();
        OgnlValueStack stack = createValueStack();
        stack.push(action);

        action.setThrowException(true);
        try {
            stack.findValue("exceptionMethod12()", true);
            fail("Failed to throw exception on EL method exception");
        } catch (Exception ex) {
            //ok
        }
    }


    public void testDoesNotFailOnNonActionObjects() {
        //if a value is not found, then it will check for missing properties
        //it needs to check in all objects in the stack, not only actions, see WW-3306
        OgnlValueStack vs = createValueStack();

        Dog dog = new Dog();
        dog.setHates(null);
        vs.push(dog);
        vs.findValue("hates", true);
    }


    public void testFailOnMissingNestedProperty() {
        OgnlValueStack vs = createValueStack();

        Dog dog = new Dog();
        dog.setHates(new Cat());
        vs.push(dog);
        try {
            vs.findValue("hates.someprop", true);
            fail("Failed to throw exception on EL missing nested property");
        } catch (Exception ex) {
            //ok
        }
    }

    public void testBasic() {
        OgnlValueStack vs = createValueStack();

        Dog dog = new Dog();
        dog.setAge(12);
        dog.setName("Rover");

        vs.push(dog);
        assertEquals("Rover", vs.findValue("name", String.class));
    }

    public void testStatic() {
        OgnlValueStack vs = createValueStack();

        Dog dog = new Dog();
        dog.setDeity("fido");
        vs.push(dog);
        assertEquals("fido", vs.findValue("@com.opensymphony.xwork2.util.Dog@getDeity()", String.class));
    }

    /**
     * Allow access Enums without enabling access to static methods
     */
    public void testEnum() throws Exception {
        OgnlValueStack vs = createValueStack();

        assertEquals("ONE", vs.findValue("@com.opensymphony.xwork2.ognl.MyNumbers@values()[0]", String.class));
        assertEquals("TWO", vs.findValue("@com.opensymphony.xwork2.ognl.MyNumbers@values()[1]", String.class));
        assertEquals("THREE", vs.findValue("@com.opensymphony.xwork2.ognl.MyNumbers@values()[2]", String.class));
    }

    public void testStaticMethodDisallow() {
        OgnlValueStack vs = createValueStack(false, true);

        Dog dog = new Dog();
        dog.setDeity("fido");
        vs.push(dog);
        assertNull(vs.findValue("@com.opensymphony.xwork2.util.Dog@getDeity()", String.class));
    }

    public void testBasicSet() {
        OgnlValueStack vs = createValueStack();

        Dog dog = new Dog();
        dog.setAge(12);
        dog.setName("Rover");

        vs.set("dog", dog);
        assertEquals("Rover", vs.findValue("dog.name", String.class));
    }

    public void testCallMethodOnNullObject() {
        OgnlValueStack stack = createValueStack();
        assertNull(stack.findValue("foo.size()"));
    }

    public void testCallMethodThatThrowsExceptionTwice() {
        SimpleAction action = new SimpleAction();
        OgnlValueStack stack = createValueStack();
        stack.push(action);

        action.setThrowException(true);
        assertNull(stack.findValue("exceptionMethod1()"));
        action.setThrowException(false);
        assertEquals("OK", stack.findValue("exceptionMethod()"));
    }


    public void testCallMethodWithNullArg() {
        SimpleAction action = new SimpleAction();
        OgnlValueStack stack = createValueStack();
        stack.push(action);

        stack.findValue("setName(blah)");
        assertNull(action.getName());

        action.setBlah("blah");
        stack.findValue("setName(blah)");
        assertEquals("blah", action.getName());
    }

    public void testConvertStringArrayToList() {
        Foo foo = new Foo();
        OgnlValueStack vs = createValueStack();
        vs.push(foo);

        vs.setValue("strings", new String[]{"one", "two"});

        assertNotNull(foo.getStrings());
        assertEquals("one", foo.getStrings().get(0));
        assertEquals("two", foo.getStrings().get(1));
    }

    public void testFindValueWithConversion() {

        // register converter
        TestBean2 tb2 = new TestBean2();

        OgnlValueStack stack = createValueStack();
        stack.push(tb2);
        Map myContext = stack.getContext();

        Map props = new HashMap();
        props.put("cat", "Kitty");
        ognlUtil.setProperties(props, tb2, myContext);
        // expect String to be converted into a Cat
        assertEquals("Kitty", tb2.getCat().getName());

        // findValue should be able to access the name
        Object value = stack.findValue("cat.name == 'Kitty'", Boolean.class);
        assertNotNull(value);
        assertEquals(Boolean.class, value.getClass());
        assertEquals(Boolean.TRUE, value);

        value = stack.findValue("cat == null", Boolean.class);
        assertNotNull(value);
        assertEquals(Boolean.class, value.getClass());
        assertEquals(Boolean.FALSE, value);
    }


    public void testDeepProperties() {
        OgnlValueStack vs = createValueStack();

        Cat cat = new Cat();
        cat.setName("Smokey");

        Dog dog = new Dog();
        dog.setAge(12);
        dog.setName("Rover");
        dog.setChildAges(new int[]{1, 2});
        dog.setHates(cat);

        vs.push(dog);
        assertEquals("Smokey", vs.findValue("hates.name", String.class));
    }

    public void testFooBarAsString() {
        OgnlValueStack vs = createValueStack();
        Foo foo = new Foo();
        Bar bar = new Bar();
        bar.setTitle("blah");
        bar.setSomethingElse(123);
        foo.setBar(bar);

        vs.push(foo);
        assertEquals("blah:123", vs.findValue("bar", String.class));
    }

    public void testGetBarAsString() {
        Foo foo = new Foo();
        Bar bar = new Bar();
        bar.setTitle("bar");
        bar.setSomethingElse(123);
        foo.setBar(bar);

        OgnlValueStack vs = createValueStack();
        vs.push(foo);

        String output = (String) vs.findValue("bar", String.class);
        assertEquals("bar:123", output);
    }

    public void testGetComplexBarAsString() {
        // children foo->foo->foo
        Foo foo = new Foo();
        Foo foo2 = new Foo();
        foo.setChild(foo2);

        Foo foo3 = new Foo();
        foo2.setChild(foo3);

        // relatives
        Foo fooA = new Foo();
        foo.setRelatives(new Foo[]{fooA});

        Foo fooB = new Foo();
        foo2.setRelatives(new Foo[]{fooB});

        Foo fooC = new Foo();
        foo3.setRelatives(new Foo[]{fooC});

        // the bar
        Bar bar = new Bar();
        bar.setTitle("bar");
        bar.setSomethingElse(123);

        // now place the bar all over
        foo.setBar(bar);
        foo2.setBar(bar);
        foo3.setBar(bar);
        fooA.setBar(bar);
        fooB.setBar(bar);
        fooC.setBar(bar);

        OgnlValueStack vs = createValueStack();
        vs.push(foo);

        vs.getContext().put("foo", foo);

        assertEquals("bar:123", vs.findValue("#foo.bar", String.class));
        assertEquals("bar:123", vs.findValue("bar", String.class));
        assertEquals("bar:123", vs.findValue("child.bar", String.class));
        assertEquals("bar:123", vs.findValue("child.child.bar", String.class));
        assertEquals("bar:123", vs.findValue("relatives[0].bar", String.class));
        assertEquals("bar:123", vs.findValue("child.relatives[0].bar", String.class));
        assertEquals("bar:123", vs.findValue("child.child.relatives[0].bar", String.class));

        vs.push(vs.findValue("child"));
        assertEquals("bar:123", vs.findValue("bar", String.class));
        assertEquals("bar:123", vs.findValue("child.bar", String.class));
        assertEquals("bar:123", vs.findValue("relatives[0].bar", String.class));
        assertEquals("bar:123", vs.findValue("child.relatives[0].bar", String.class));
    }

    public void testGetNullValue() {
        Dog dog = new Dog();
        OgnlValueStack stack = createValueStack();
        stack.push(dog);
        assertNull(stack.findValue("name"));
    }

    public void testMapEntriesAvailableByKey() {
        Foo foo = new Foo();
        String title = "a title";
        foo.setTitle(title);

        OgnlValueStack vs = createValueStack();
        vs.push(foo);

        Map map = new HashMap();
        String a_key = "a";
        String a_value = "A";
        map.put(a_key, a_value);

        String b_key = "b";
        String b_value = "B";
        map.put(b_key, b_value);

        vs.push(map);

        assertEquals(title, vs.findValue("title"));
        assertEquals(a_value, vs.findValue(a_key));
        assertEquals(b_value, vs.findValue(b_key));
    }

    public void testMethodCalls() {
        OgnlValueStack vs = createValueStack();

        Dog dog1 = new Dog();
        dog1.setAge(12);
        dog1.setName("Rover");

        Dog dog2 = new Dog();
        dog2.setAge(1);
        dog2.setName("Jack");
        vs.push(dog1);
        vs.push(dog2);

        //assertEquals(new Boolean(false), vs.findValue("'Rover'.endsWith('Jack')"));
        //assertEquals(new Boolean(false), vs.findValue("'Rover'.endsWith(name)"));
        //assertEquals("RoverJack", vs.findValue("[1].name + name"));
        assertEquals(new Boolean(false), vs.findValue("[1].name.endsWith(name)"));

        assertEquals(new Integer(1 * 7), vs.findValue("computeDogYears()"));
        assertEquals(new Integer(1 * 2), vs.findValue("multiplyAge(2)"));
        assertEquals(new Integer(12 * 7), vs.findValue("[1].computeDogYears()"));
        assertEquals(new Integer(12 * 5), vs.findValue("[1].multiplyAge(5)"));
        assertNull(vs.findValue("thisMethodIsBunk()"));
        assertEquals(new Integer(12 * 1), vs.findValue("[1].multiplyAge(age)"));

        assertEquals("Jack", vs.findValue("name"));
        assertEquals("Rover", vs.findValue("[1].name"));

        //hates will be null
        assertEquals(Boolean.TRUE, vs.findValue("nullSafeMethod(hates)"));
    }

    public void testMismatchedGettersAndSettersCauseExceptionInSet() {
        OgnlValueStack vs = createValueStack();

        BadJavaBean bean = new BadJavaBean();
        vs.push(bean);

        //this used to fail in OGNl versdion < 2.7
        vs.setValue("count", "1", true);
        assertEquals("1", bean.getCount());

        try {
            vs.setValue("count2", "a", true);
            fail("Expected an exception for mismatched getter and setter");
        } catch (StrutsException e) {
            //expected
        }
    }

    public void testNoExceptionInSetForDefault() {
        OgnlValueStack vs = createValueStack();

        BadJavaBean bean = new BadJavaBean();
        vs.push(bean);

        //this used to fail in OGNl versdion < 2.7
        vs.setValue("count", "1", true);
        assertEquals("1", bean.getCount());

        try {
            vs.setValue("count2", "a", true);
            fail("Expected an exception for mismatched getter and setter");
        } catch (StrutsException e) {
            //expected
        }
    }

    public void testNullEntry() {
        OgnlValueStack vs = createValueStack();

        Dog dog = new Dog();
        dog.setName("Rover");

        vs.push(dog);
        assertEquals("Rover", vs.findValue("name", String.class));

        vs.push(null);
        assertEquals("Rover", vs.findValue("name", String.class));
    }

    public void testNullMethod() {
        Dog dog = new Dog();
        OgnlValueStack stack = createValueStack();
        stack.push(dog);
        assertNull(stack.findValue("nullMethod()"));
        assertNull(stack.findValue("@com.opensymphony.xwork2.ognl.OgnlValueStackTest@staticNullMethod()"));
    }

    public void testPetSoarBug() {
        Cat cat = new Cat();
        cat.setFoo(new Foo());

        Bar bar = new Bar();
        bar.setTitle("bar");
        bar.setSomethingElse(123);
        cat.getFoo().setBar(bar);

        OgnlValueStack vs = createValueStack();
        vs.push(cat);

        assertEquals("bar:123", vs.findValue("foo.bar", String.class));
    }

    public void testPrimitiveSettingWithInvalidValueAddsFieldErrorInDevMode() throws Exception {
        SimpleAction action = new SimpleAction();
        OgnlValueStack stack = createValueStack();
        stack.getContext().put(XWorkConverter.REPORT_CONVERSION_ERRORS, Boolean.TRUE);
        stack.setDevMode("true");
        stack.push(action);

        try {
            stack.setValue("bar", "3x");
            fail("Attempt to set 'bar' int property to '3x' should result in RuntimeException");
        }
        catch (RuntimeException re) {
            assertTrue(true);
        }

        Map<String, ConversionData> conversionErrors = stack.getActionContext().getConversionErrors();
        assertTrue(conversionErrors.containsKey("bar"));
    }

    public void testPrimitiveSettingWithInvalidValueAddsFieldErrorInNonDevMode() {
        SimpleAction action = new SimpleAction();
        OgnlValueStack stack = createValueStack();
        stack.getContext().put(XWorkConverter.REPORT_CONVERSION_ERRORS, Boolean.TRUE);
        stack.setDevMode("false");
        stack.push(action);
        stack.setValue("bar", "3x");

        Map<String, ConversionData> conversionErrors = stack.getActionContext().getConversionErrors();
        assertTrue(conversionErrors.containsKey("bar"));
    }


    public void testObjectSettingWithInvalidValueDoesNotCauseSetCalledWithNull() {
        SimpleAction action = new SimpleAction();
        action.setBean(new TestBean());
        OgnlValueStack stack = createValueStack();
        stack.getContext().put(XWorkConverter.REPORT_CONVERSION_ERRORS, Boolean.TRUE);
        stack.push(action);
        try {
            stack.setValue("bean", "foobar", true);
            fail("Should have thrown a type conversion exception");
        } catch (StrutsException e) {
            // expected
        }

        Map<String, ConversionData> conversionErrors = stack.getActionContext().getConversionErrors();
        assertTrue(conversionErrors.containsKey("bean"));
        assertNotNull(action.getBean());
    }


    public void testSerializable() throws IOException, ClassNotFoundException {
        OgnlValueStack vs = createValueStack();

        Dog dog = new Dog();
        dog.setAge(12);
        dog.setName("Rover");

        vs.push(dog);
        assertEquals("Rover", vs.findValue("name", String.class));

        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        ObjectOutputStream oos = new ObjectOutputStream(baos);

        oos.writeObject(vs);
        oos.flush();

        ByteArrayInputStream bais = new ByteArrayInputStream(baos.toByteArray());
        ObjectInputStream ois = new ObjectInputStream(bais);

        OgnlValueStack newVs = (OgnlValueStack) ois.readObject();
        assertEquals("Rover", newVs.findValue("name", String.class));
    }

    public void testSetAfterPush() {
        OgnlValueStack vs = createValueStack();

        Dog d = new Dog();
        d.setName("Rover");
        vs.push(d);

        vs.set("name", "Bill");

        assertEquals("Bill", vs.findValue("name"));

    }

    public void testSetBarAsString() {
        Foo foo = new Foo();

        OgnlValueStack vs = createValueStack();
        vs.push(foo);

        vs.setValue("bar", "bar:123");

        assertEquals("bar", foo.getBar().getTitle());
        assertEquals(123, foo.getBar().getSomethingElse());
    }

    public void testSetBeforePush() {
        OgnlValueStack vs = createValueStack();

        vs.set("name", "Bill");
        Dog d = new Dog();
        d.setName("Rover");
        vs.push(d);

        assertEquals("Rover", vs.findValue("name"));

    }

    public void testSetDeepBarAsString() {
        Foo foo = new Foo();
        Foo foo2 = new Foo();
        foo.setChild(foo2);

        OgnlValueStack vs = createValueStack();
        vs.push(foo);

        vs.setValue("child.bar", "bar:123");

        assertEquals("bar", foo.getChild().getBar().getTitle());
        assertEquals(123, foo.getChild().getBar().getSomethingElse());
    }

    public void testSetNullList() {
        Foo foo = new Foo();
        OgnlValueStack vs = createValueStack();
        vs.getContext().put(ReflectionContextState.CREATE_NULL_OBJECTS, Boolean.TRUE);
        vs.push(foo);

        vs.setValue("cats[0].name", "Cat One");
        vs.setValue("cats[1].name", "Cat Two");

        assertNotNull(foo.getCats());
        assertEquals(2, foo.getCats().size());
        assertEquals("Cat One", ((Cat) foo.getCats().get(0)).getName());
        assertEquals("Cat Two", ((Cat) foo.getCats().get(1)).getName());

        //test when both Key and Value types of Map are interfaces but concrete classes are defined in .properties file
        vs.setValue("animalMap[3].name", "Cat Three by interface");
        vs.setValue("animalMap[6].name", "Cat Six by interface");
        assertNotNull(foo.getAnimalMap());
        assertEquals(2, foo.getAnimalMap().size());
        assertEquals("Cat Three by interface", foo.getAnimalMap().get(3L).getName());
        assertEquals("Cat Six by interface", foo.getAnimalMap().get(6L).getName());

        vs.setValue("annotatedCats[0].name", "Cat One By Annotation");
        vs.setValue("annotatedCats[1].name", "Cat Two By Annotation");
        assertNotNull(foo.getAnnotatedCats());
        assertEquals(2, foo.getAnnotatedCats().size());
        assertEquals("Cat One By Annotation", ((Cat) foo.getAnnotatedCats().get(0)).getName());
        assertEquals("Cat Two By Annotation", ((Cat) foo.getAnnotatedCats().get(1)).getName());

        vs.setValue("cats[0].foo.cats[1].name", "Deep null cat");
        assertNotNull(((Cat) foo.getCats().get(0)).getFoo());
        assertNotNull(((Cat) foo.getCats().get(0)).getFoo().getCats());
        assertNotNull(((Cat) foo.getCats().get(0)).getFoo().getCats().get(1));
        assertEquals("Deep null cat", ((Cat) ((Cat) foo.getCats().get(0)).getFoo().getCats().get(1)).getName());

        vs.setValue("annotatedCats[0].foo.annotatedCats[1].name", "Deep null cat by annotation");
        assertNotNull(((Cat) foo.getAnnotatedCats().get(0)).getFoo());
        assertNotNull(((Cat) foo.getAnnotatedCats().get(0)).getFoo().getAnnotatedCats());
        assertNotNull(((Cat) foo.getAnnotatedCats().get(0)).getFoo().getAnnotatedCats().get(1));
        assertEquals("Deep null cat by annotation", ((Cat) ((Cat) foo.getAnnotatedCats().get(0)).getFoo().getAnnotatedCats().get(1)).getName());
    }

    public void testSetMultiple() {
        OgnlValueStack vs = createValueStack();
        int origSize = vs.getRoot().size();
        vs.set("something", new Object());
        vs.set("somethingElse", new Object());
        vs.set("yetSomethingElse", new Object());
        assertEquals(origSize + 1, vs.getRoot().size());

    }

    public void testSetNullMap() {
        Foo foo = new Foo();
        OgnlValueStack vs = createValueStack();
        vs.getContext().put(ReflectionContextState.CREATE_NULL_OBJECTS, Boolean.TRUE);
        vs.push(foo);

        vs.setValue("catMap['One'].name", "Cat One");
        vs.setValue("catMap['Two'].name", "Cat Two");

        assertNotNull(foo.getCatMap());
        assertEquals(2, foo.getCatMap().size());
        assertEquals("Cat One", ((Cat) foo.getCatMap().get("One")).getName());
        assertEquals("Cat Two", ((Cat) foo.getCatMap().get("Two")).getName());

        vs.setValue("catMap['One'].foo.catMap['Two'].name", "Deep null cat");
        assertNotNull(((Cat) foo.getCatMap().get("One")).getFoo());
        assertNotNull(((Cat) foo.getCatMap().get("One")).getFoo().getCatMap());
        assertNotNull(((Cat) foo.getCatMap().get("One")).getFoo().getCatMap().get("Two"));
        assertEquals("Deep null cat", ((Cat) ((Cat) foo.getCatMap().get("One")).getFoo().getCatMap().get("Two")).getName());
    }

    public void testSetReallyDeepBarAsString() {
        Foo foo = new Foo();
        Foo foo2 = new Foo();
        foo.setChild(foo2);

        Foo foo3 = new Foo();
        foo2.setChild(foo3);

        OgnlValueStack vs = createValueStack();
        vs.push(foo);

        vs.setValue("child.child.bar", "bar:123");

        assertEquals("bar", foo.getChild().getChild().getBar().getTitle());
        assertEquals(123, foo.getChild().getChild().getBar().getSomethingElse());
    }

    public void testSettingDogGender() {
        OgnlValueStack vs = createValueStack();

        Dog dog = new Dog();
        vs.push(dog);

        vs.setValue("male", "false");

        assertFalse(dog.isMale());
    }

    public void testStatics() {
        OgnlValueStack vs = createValueStack();

        Cat cat = new Cat();
        vs.push(cat);

        Dog dog = new Dog();
        dog.setAge(12);
        dog.setName("Rover");
        vs.push(dog);

        assertEquals("Canine", vs.findValue("@vs@SCIENTIFIC_NAME"));
        assertEquals("Canine", vs.findValue("@vs1@SCIENTIFIC_NAME"));
        assertEquals("Feline", vs.findValue("@vs2@SCIENTIFIC_NAME"));
        assertEquals(BigDecimal.ROUND_HALF_DOWN, vs.findValue("@java.math.BigDecimal@ROUND_HALF_DOWN"));
        assertNull(vs.findValue("@vs3@BLAH"));
        assertNull(vs.findValue("@com.nothing.here.Nothing@BLAH"));
    }

    /**
     * Fails on 2.5.20 and earlier - tested on 2.5 (5/5/2016) and failed
     * @since 2.5.21
     */
    public void testNotThrowExceptionOnTopMissingProperty() {
        OgnlValueStack vs = createValueStack();

        Dog dog = new Dog();
        dog.setName("Rover");
        vs.push(dog);

        Cat cat = new Cat();
        vs.push(cat);

        vs.setValue("age", 12, true);

        assertEquals(12, vs.findValue("age", true));
        assertEquals(12, vs.findValue("age", Integer.class, true));
        assertEquals(12, vs.findValue("getAge()", true));
        assertEquals(12, vs.findValue("getAge()", Integer.class, true));
    }

    /**
     * Fails on 2.5.20 and earlier - tested on 2.5 (5/5/2016) and failed
     * @since 2.5.21
     */
    public void testNotSkipUserReturnedNullValues() {
        OgnlValueStack vs = createValueStack();

        Dog dog = new Dog();
        dog.setName("Rover");
        vs.push(dog);

        Cat cat = new Cat();
        vs.push(cat);

        // should not skip returned null values from cat.name
        assertNull(vs.findValue("name", true));
        assertNull(vs.findValue("name", String.class, true));
        assertNull(vs.findValue("getName()", true));
        assertNull(vs.findValue("getName()", String.class, true));
    }

    public void testTop() {
        OgnlValueStack vs = createValueStack();

        Dog dog1 = new Dog();
        dog1.setAge(12);
        dog1.setName("Rover");

        Dog dog2 = new Dog();
        dog2.setAge(1);
        dog2.setName("Jack");
        vs.push(dog1);
        vs.push(dog2);

        assertEquals(dog2, vs.findValue("top"));
        assertEquals("Jack", vs.findValue("top.name"));
    }

    public void testTopIsDefaultTextProvider() {
        OgnlValueStack vs = createValueStack();

        assertEquals(container.getInstance(TextProvider.class, "system"), vs.findValue("top"));
    }

    public void testTwoDogs() {
        OgnlValueStack vs = createValueStack();

        Dog dog1 = new Dog();
        dog1.setAge(12);
        dog1.setName("Rover");

        Dog dog2 = new Dog();
        dog2.setAge(1);
        dog2.setName("Jack");
        vs.push(dog1);
        vs.push(dog2);

        assertEquals("Jack", vs.findValue("name"));
        assertEquals("Rover", vs.findValue("[1].name"));

        assertEquals(dog2, vs.pop());
        assertEquals("Rover", vs.findValue("name"));
    }

    public void testTypeConversionError() {
        TestBean bean = new TestBean();
        OgnlValueStack stack = createValueStack();
        stack.push(bean);
        stack.getContext().put(XWorkConverter.REPORT_CONVERSION_ERRORS, Boolean.TRUE);
        try {
            stack.setValue("count", "a", true);
            fail("Should have thrown a type conversion exception");
        } catch (StrutsException e) {
            // expected
        }

        Map<String, ConversionData> conversionErrors = stack.getActionContext().getConversionErrors();
        assertTrue(conversionErrors.containsKey("count"));
    }

    public void testConstructorWithAStack() {
        OgnlValueStack stack = createValueStack();
        stack.push("Hello World");

        OgnlValueStack stack2 = new OgnlValueStack(stack,
                container.getInstance(XWorkConverter.class),
                (CompoundRootAccessor) container.getInstance(PropertyAccessor.class, CompoundRoot.class.getName()), true, true);
        container.inject(stack2);

        assertEquals(stack.getRoot(), stack2.getRoot());
        assertEquals(stack.peek(), stack2.peek());
        assertEquals("Hello World", stack2.pop());

    }

    public void testDefaultType() {
        OgnlValueStack stack = createValueStack();
        stack.setDefaultType(String.class);
        stack.push("Hello World");

        assertEquals("Hello World", stack.findValue("top"));
        assertNull(stack.findValue(null));

        stack.setDefaultType(Integer.class);
        stack.push(123);
        assertEquals(123, stack.findValue("top"));
    }

    public void testFindString() {
        OgnlValueStack stack = createValueStack();
        stack.setDefaultType(Integer.class);
        stack.push("Hello World");

        assertEquals("Hello World", stack.findString("top"));
        assertNull(stack.findString(null));
    }

    public void testExpOverrides() {
        Map<Object, Object> overrides = new HashMap<>();
        overrides.put("claus", "top");

        OgnlValueStack stack = createValueStack();
        stack.setExprOverrides(overrides);
        stack.push("Hello World");

        assertEquals("Hello World", stack.findValue("claus"));
        assertEquals("Hello World", stack.findString("claus"));
        assertEquals("Hello World", stack.findValue("top"));
        assertEquals("Hello World", stack.findString("top"));

        assertEquals("Hello World", stack.findValue("claus", String.class));
        assertEquals("Hello World", stack.findValue("top", String.class));

        stack.getContext().put("santa", "Hello Santa");
        assertEquals("Hello Santa", stack.findValue("santa", String.class));
        assertNull(stack.findValue("unknown", String.class));
    }

    public void testWarnAboutInvalidProperties() {
        OgnlValueStack stack = createValueStack();
        MyAction action = new MyAction();
        action.setName("Don");
        stack.push(action);

        // how to test the warning was logged?
        assertEquals("Don", stack.findValue("name", String.class));
        assertNull(stack.findValue("address", String.class));
        // should log warning
        assertNull(stack.findValue("address.invalidProperty", String.class));

        // if country is null, OGNL throws an exception
        /*action.setAddress(new Address());
        stack.push(action);*/
        // should log warning
        assertNull(stack.findValue("address.country.id", String.class));
        assertNull(stack.findValue("address.country.name", String.class));
    }

   /**
     * Test a default OgnlValueStackFactory and OgnlValueStack generated by it
     * when a default configuration is used.
     */
    public void testOgnlValueStackFromOgnlValueStackFactoryDefaultConfig() {
        OgnlValueStackFactory ognlValueStackFactory = getValueStackFactory();
        OgnlValueStack ognlValueStack = (OgnlValueStack) ognlValueStackFactory.createValueStack();
        Object accessedValue;

        // An OgnlValueStackFactory using a container config with default (from XWorkConfigurationProvider)
        // static access flag values present should prevent staticMethodAccess but allow staticFieldAccess.
        assertFalse("OgnlValueStackFactory staticMethodAccess (default flags) not false?", ognlValueStackFactory.containerAllowsStaticMethodAccess());
        assertTrue("OgnlValueStackFactory staticFieldAccess (default flags) not true?", ognlValueStackFactory.containerAllowsStaticFieldAccess());
        // An OgnlValueStack created from the above OgnlValueStackFactory should allow public field access,
        // but prevent non-public field access.  It should also deny static method access.
        accessedValue = ognlValueStack.findValue("@com.opensymphony.xwork2.ognl.OgnlValueStackTest@staticInteger100Method()");
        assertNull("able to access static method (result not null) ?", accessedValue);
        accessedValue = ognlValueStack.findValue("@com.opensymphony.xwork2.ognl.OgnlValueStackTest@STATIC_FINAL_PUBLIC_ATTRIBUTE");
        assertEquals("accessed static final public field value not equal to actual?", accessedValue, STATIC_FINAL_PUBLIC_ATTRIBUTE);
        accessedValue = ognlValueStack.findValue("@com.opensymphony.xwork2.ognl.OgnlValueStackTest@STATIC_PUBLIC_ATTRIBUTE");
        assertEquals("accessed static public field value not equal to actual?", accessedValue, STATIC_PUBLIC_ATTRIBUTE);
        accessedValue = ognlValueStack.findValue("@com.opensymphony.xwork2.ognl.OgnlValueStackTest@STATIC_FINAL_PACKAGE_ATTRIBUTE");
        assertNull("accessed final package field (result not null) ?", accessedValue);
        accessedValue = ognlValueStack.findValue("@com.opensymphony.xwork2.ognl.OgnlValueStackTest@STATIC_PACKAGE_ATTRIBUTE");
        assertNull("accessed package field (result not null) ?", accessedValue);
        accessedValue = ognlValueStack.findValue("@com.opensymphony.xwork2.ognl.OgnlValueStackTest@STATIC_FINAL_PROTECTED_ATTRIBUTE");
        assertNull("accessed final protected field (result not null) ?", accessedValue);
        accessedValue = ognlValueStack.findValue("@com.opensymphony.xwork2.ognl.OgnlValueStackTest@STATIC_PROTECTED_ATTRIBUTE");
        assertNull("accessed protected field (result not null) ?", accessedValue);
        accessedValue = ognlValueStack.findValue("@com.opensymphony.xwork2.ognl.OgnlValueStackTest@STATIC_FINAL_PRIVATE_ATTRIBUTE");
        assertNull("accessed final private field (result not null) ?", accessedValue);
        accessedValue = ognlValueStack.findValue("@com.opensymphony.xwork2.ognl.OgnlValueStackTest@STATIC_PRIVATE_ATTRIBUTE");
        assertNull("accessed private field (result not null) ?", accessedValue);
    }

    /**
     * Test a raw OgnlValueStackFactory and OgnlValueStack generated by it
     * when no static access flags are set (not present in configuration).
     */
    public void testOgnlValueStackFromOgnlValueStackFactoryNoFlagsSet() {
        OgnlValueStackFactory ognlValueStackFactory = reloadValueStackFactory(null, null);
        OgnlValueStack ognlValueStack = (OgnlValueStack) ognlValueStackFactory.createValueStack();
        Object accessedValue;

        // An OgnlValueStackFactory using a container config with no static access flag values present
        // (such as from a DefaultConfiguration vs. XWorkConfigurationProvider) should
        // prevent staticMethodAccess AND prevent staticFieldAccess.
        // Note: Under normal circumstances, explicit static access configuration flags should be present,
        // but this specific check verifies what happens if those configuration flags are not present.
        assertFalse("OgnlValueStackFactory staticMethodAccess (no flag present) not false?", ognlValueStackFactory.containerAllowsStaticMethodAccess());
        assertFalse("OgnlValueStackFactory staticFieldAccess (no flag present) not false?", ognlValueStackFactory.containerAllowsStaticFieldAccess());
        // An OgnlValueStack created from the above OgnlValueStackFactory should prevent public field access,
        // and prevent non-public field access.  It should also deny static method access.
        accessedValue = ognlValueStack.findValue("@com.opensymphony.xwork2.ognl.OgnlValueStackTest@staticInteger100Method()");
        assertNull("able to access static method (result not null) ?", accessedValue);
        accessedValue = ognlValueStack.findValue("@com.opensymphony.xwork2.ognl.OgnlValueStackTest@STATIC_FINAL_PUBLIC_ATTRIBUTE");
        assertNull("able to access static final public field (result not null) ?", accessedValue);
        accessedValue = ognlValueStack.findValue("@com.opensymphony.xwork2.ognl.OgnlValueStackTest@STATIC_PUBLIC_ATTRIBUTE");
        assertNull("able to access static public field (result not null) ?", accessedValue);
        accessedValue = ognlValueStack.findValue("@com.opensymphony.xwork2.ognl.OgnlValueStackTest@STATIC_FINAL_PACKAGE_ATTRIBUTE");
        assertNull("accessed final package field (result not null) ?", accessedValue);
        accessedValue = ognlValueStack.findValue("@com.opensymphony.xwork2.ognl.OgnlValueStackTest@STATIC_PACKAGE_ATTRIBUTE");
        assertNull("accessed package field (result not null) ?", accessedValue);
        accessedValue = ognlValueStack.findValue("@com.opensymphony.xwork2.ognl.OgnlValueStackTest@STATIC_FINAL_PROTECTED_ATTRIBUTE");
        assertNull("accessed final protected field (result not null) ?", accessedValue);
        accessedValue = ognlValueStack.findValue("@com.opensymphony.xwork2.ognl.OgnlValueStackTest@STATIC_PROTECTED_ATTRIBUTE");
        assertNull("accessed protected field (result not null) ?", accessedValue);
        accessedValue = ognlValueStack.findValue("@com.opensymphony.xwork2.ognl.OgnlValueStackTest@STATIC_FINAL_PRIVATE_ATTRIBUTE");
        assertNull("accessed final private field (result not null) ?", accessedValue);
        accessedValue = ognlValueStack.findValue("@com.opensymphony.xwork2.ognl.OgnlValueStackTest@STATIC_PRIVATE_ATTRIBUTE");
        assertNull("accessed private field (result not null) ?", accessedValue);
    }

    /**
     * Test a raw OgnlValueStackFactory and OgnlValueStack generated by it
     * when both static access flags are set to false.
     */
    public void testOgnlValueStackFromOgnlValueStackFactoryNoStaticAccess() {
        OgnlValueStackFactory ognlValueStackFactory = reloadValueStackFactory(false, false);
        OgnlValueStack ognlValueStack = (OgnlValueStack) ognlValueStackFactory.createValueStack();
        Object accessedValue;

        // An OgnlValueStackFactory using a container config with both static access flags set false should
        // prevent staticMethodAccess AND prevent staticFieldAccess.
        assertFalse("OgnlValueStackFactory staticMethodAccess (set false) not false?", ognlValueStackFactory.containerAllowsStaticMethodAccess());
        assertFalse("OgnlValueStackFactory staticFieldAccess (set false) not false?", ognlValueStackFactory.containerAllowsStaticFieldAccess());
        // An OgnlValueStack created from the above OgnlValueStackFactory should prevent public field access,
        // and prevent non-public field access.  It should also deny static method access.
        accessedValue = ognlValueStack.findValue("@com.opensymphony.xwork2.ognl.OgnlValueStackTest@staticInteger100Method()");
        assertNull("able to access static method (result not null) ?", accessedValue);
        accessedValue = ognlValueStack.findValue("@com.opensymphony.xwork2.ognl.OgnlValueStackTest@STATIC_FINAL_PUBLIC_ATTRIBUTE");
        assertNull("able to access static final public field (result not null) ?", accessedValue);
        accessedValue = ognlValueStack.findValue("@com.opensymphony.xwork2.ognl.OgnlValueStackTest@STATIC_PUBLIC_ATTRIBUTE");
        assertNull("able to access static public field (result not null) ?", accessedValue);
        accessedValue = ognlValueStack.findValue("@com.opensymphony.xwork2.ognl.OgnlValueStackTest@STATIC_FINAL_PACKAGE_ATTRIBUTE");
        assertNull("accessed final package field (result not null) ?", accessedValue);
        accessedValue = ognlValueStack.findValue("@com.opensymphony.xwork2.ognl.OgnlValueStackTest@STATIC_PACKAGE_ATTRIBUTE");
        assertNull("accessed package field (result not null) ?", accessedValue);
        accessedValue = ognlValueStack.findValue("@com.opensymphony.xwork2.ognl.OgnlValueStackTest@STATIC_FINAL_PROTECTED_ATTRIBUTE");
        assertNull("accessed final protected field (result not null) ?", accessedValue);
        accessedValue = ognlValueStack.findValue("@com.opensymphony.xwork2.ognl.OgnlValueStackTest@STATIC_PROTECTED_ATTRIBUTE");
        assertNull("accessed protected field (result not null) ?", accessedValue);
        accessedValue = ognlValueStack.findValue("@com.opensymphony.xwork2.ognl.OgnlValueStackTest@STATIC_FINAL_PRIVATE_ATTRIBUTE");
        assertNull("accessed final private field (result not null) ?", accessedValue);
        accessedValue = ognlValueStack.findValue("@com.opensymphony.xwork2.ognl.OgnlValueStackTest@STATIC_PRIVATE_ATTRIBUTE");
        assertNull("accessed private field (result not null) ?", accessedValue);
    }

    /**
     * Test a raw OgnlValueStackFactory and OgnlValueStack generated by it
     * when both static access flags are set to true.
     */
    public void testOgnlValueStackFromOgnlValueStackFactoryAllStaticAccess() {
        OgnlValueStackFactory ognlValueStackFactory = reloadValueStackFactory(true, true);
        OgnlValueStack ognlValueStack = (OgnlValueStack) ognlValueStackFactory.createValueStack();
        Object accessedValue;

        // An OgnlValueStackFactory using a container config with both static access flags set true should
        // allow both staticMethodAccess AND staticFieldAccess.
        assertTrue("OgnlValueStackFactory staticMethodAccess (set true) not true?", ognlValueStackFactory.containerAllowsStaticMethodAccess());
        assertTrue("OgnlValueStackFactory staticFieldAccess (set true) not true?", ognlValueStackFactory.containerAllowsStaticFieldAccess());
        // An OgnlValueStack created from the above OgnlValueStackFactory should allow public field access,
        // but prevent non-public field access.  It should also allow static method access.
        accessedValue = ognlValueStack.findValue("@com.opensymphony.xwork2.ognl.OgnlValueStackTest@staticInteger100Method()");
        assertNotNull("unable to access static method (result null) ?", accessedValue);
        assertEquals("accessed static method result not equal to expected?", accessedValue, staticInteger100Method());
        accessedValue = ognlValueStack.findValue("@com.opensymphony.xwork2.ognl.OgnlValueStackTest@STATIC_FINAL_PUBLIC_ATTRIBUTE");
        assertEquals("accessed static final public field value not equal to actual?", accessedValue, STATIC_FINAL_PUBLIC_ATTRIBUTE);
        accessedValue = ognlValueStack.findValue("@com.opensymphony.xwork2.ognl.OgnlValueStackTest@STATIC_PUBLIC_ATTRIBUTE");
        assertEquals("accessed static public field value not equal to actual?", accessedValue, STATIC_PUBLIC_ATTRIBUTE);
        accessedValue = ognlValueStack.findValue("@com.opensymphony.xwork2.ognl.OgnlValueStackTest@STATIC_FINAL_PACKAGE_ATTRIBUTE");
        assertNull("accessed final package field (result not null) ?", accessedValue);
        accessedValue = ognlValueStack.findValue("@com.opensymphony.xwork2.ognl.OgnlValueStackTest@STATIC_PACKAGE_ATTRIBUTE");
        assertNull("accessed package field (result not null) ?", accessedValue);
        accessedValue = ognlValueStack.findValue("@com.opensymphony.xwork2.ognl.OgnlValueStackTest@STATIC_FINAL_PROTECTED_ATTRIBUTE");
        assertNull("accessed final protected field (result not null) ?", accessedValue);
        accessedValue = ognlValueStack.findValue("@com.opensymphony.xwork2.ognl.OgnlValueStackTest@STATIC_PROTECTED_ATTRIBUTE");
        assertNull("accessed protected field (result not null) ?", accessedValue);
        accessedValue = ognlValueStack.findValue("@com.opensymphony.xwork2.ognl.OgnlValueStackTest@STATIC_FINAL_PRIVATE_ATTRIBUTE");
        assertNull("accessed final private field (result not null) ?", accessedValue);
        accessedValue = ognlValueStack.findValue("@com.opensymphony.xwork2.ognl.OgnlValueStackTest@STATIC_PRIVATE_ATTRIBUTE");
        assertNull("accessed private field (result not null) ?", accessedValue);
    }

    /**
     * Test a raw OgnlValueStackFactory and OgnlValueStack generated by it
     * when static method access flag is true, static field access flag is false.
     */
    public void testOgnlValueStackFromOgnlValueStackFactoryOnlyStaticMethodAccess() {
        OgnlValueStackFactory ognlValueStackFactory = reloadValueStackFactory(true, false);
        OgnlValueStack ognlValueStack = (OgnlValueStack) ognlValueStackFactory.createValueStack();
        Object accessedValue;

        // An OgnlValueStackFactory using a container config with static method access flag true, static field access false should
        // allow staticMethodAccess but deny staticFieldAccess.
        assertTrue("OgnlValueStackFactory staticMethodAccess (set true) not true?", ognlValueStackFactory.containerAllowsStaticMethodAccess());
        assertFalse("OgnlValueStackFactory staticFieldAccess (set false) not false?", ognlValueStackFactory.containerAllowsStaticFieldAccess());
        // An OgnlValueStack created from the above OgnlValueStackFactory should deny public field access,
        // and also prevent non-public field access.  It should also allow static method access.
        accessedValue = ognlValueStack.findValue("@com.opensymphony.xwork2.ognl.OgnlValueStackTest@staticInteger100Method()");
        assertNotNull("unable to access static method (result null) ?", accessedValue);
        assertEquals("accessed static method result not equal to expected?", accessedValue, staticInteger100Method());
        accessedValue = ognlValueStack.findValue("@com.opensymphony.xwork2.ognl.OgnlValueStackTest@STATIC_FINAL_PUBLIC_ATTRIBUTE");
        assertNull("able to access static final public field (result not null) ?", accessedValue);
        accessedValue = ognlValueStack.findValue("@com.opensymphony.xwork2.ognl.OgnlValueStackTest@STATIC_PUBLIC_ATTRIBUTE");
        assertNull("able to access static public field (result not null) ?", accessedValue);
        accessedValue = ognlValueStack.findValue("@com.opensymphony.xwork2.ognl.OgnlValueStackTest@STATIC_FINAL_PACKAGE_ATTRIBUTE");
        assertNull("accessed final package field (result not null) ?", accessedValue);
        accessedValue = ognlValueStack.findValue("@com.opensymphony.xwork2.ognl.OgnlValueStackTest@STATIC_PACKAGE_ATTRIBUTE");
        assertNull("accessed package field (result not null) ?", accessedValue);
        accessedValue = ognlValueStack.findValue("@com.opensymphony.xwork2.ognl.OgnlValueStackTest@STATIC_FINAL_PROTECTED_ATTRIBUTE");
        assertNull("accessed final protected field (result not null) ?", accessedValue);
        accessedValue = ognlValueStack.findValue("@com.opensymphony.xwork2.ognl.OgnlValueStackTest@STATIC_PROTECTED_ATTRIBUTE");
        assertNull("accessed protected field (result not null) ?", accessedValue);
        accessedValue = ognlValueStack.findValue("@com.opensymphony.xwork2.ognl.OgnlValueStackTest@STATIC_FINAL_PRIVATE_ATTRIBUTE");
        assertNull("accessed final private field (result not null) ?", accessedValue);
        accessedValue = ognlValueStack.findValue("@com.opensymphony.xwork2.ognl.OgnlValueStackTest@STATIC_PRIVATE_ATTRIBUTE");
        assertNull("accessed private field (result not null) ?", accessedValue);
    }

    /**
     * Test a raw OgnlValueStackFactory and OgnlValueStack generated by it
     * when static method access flag is false, static field access flag is true.
     */
    public void testOgnlValueStackFromOgnlValueStackFactoryOnlyStaticFieldAccess() {
        OgnlValueStackFactory ognlValueStackFactory = reloadValueStackFactory(false, true);
        OgnlValueStack ognlValueStack = (OgnlValueStack) ognlValueStackFactory.createValueStack();
        Object accessedValue;

        // An OgnlValueStackFactory using a container config with static method access flag false, static field access true should
        // deny staticMethodAccess but allow staticFieldAccess.
        assertFalse("OgnlValueStackFactory staticMethodAccess (set false) not false?", ognlValueStackFactory.containerAllowsStaticMethodAccess());
        assertTrue("OgnlValueStackFactory staticFieldAccess (set true) not true?", ognlValueStackFactory.containerAllowsStaticFieldAccess());
        // An OgnlValueStack created from the above OgnlValueStackFactory should allow public field access,
        // but prevent non-public field access.  It should also deny static method access.
        accessedValue = ognlValueStack.findValue("@com.opensymphony.xwork2.ognl.OgnlValueStackTest@staticInteger100Method()");
        assertNull("able to access static method (result not null) ?", accessedValue);
        accessedValue = ognlValueStack.findValue("@com.opensymphony.xwork2.ognl.OgnlValueStackTest@STATIC_FINAL_PUBLIC_ATTRIBUTE");
        assertEquals("accessed static final public field value not equal to actual?", accessedValue, STATIC_FINAL_PUBLIC_ATTRIBUTE);
        accessedValue = ognlValueStack.findValue("@com.opensymphony.xwork2.ognl.OgnlValueStackTest@STATIC_PUBLIC_ATTRIBUTE");
        assertEquals("accessed static public field value not equal to actual?", accessedValue, STATIC_PUBLIC_ATTRIBUTE);
        accessedValue = ognlValueStack.findValue("@com.opensymphony.xwork2.ognl.OgnlValueStackTest@STATIC_FINAL_PACKAGE_ATTRIBUTE");
        assertNull("accessed final package field (result not null) ?", accessedValue);
        accessedValue = ognlValueStack.findValue("@com.opensymphony.xwork2.ognl.OgnlValueStackTest@STATIC_PACKAGE_ATTRIBUTE");
        assertNull("accessed package field (result not null) ?", accessedValue);
        accessedValue = ognlValueStack.findValue("@com.opensymphony.xwork2.ognl.OgnlValueStackTest@STATIC_FINAL_PROTECTED_ATTRIBUTE");
        assertNull("accessed final protected field (result not null) ?", accessedValue);
        accessedValue = ognlValueStack.findValue("@com.opensymphony.xwork2.ognl.OgnlValueStackTest@STATIC_PROTECTED_ATTRIBUTE");
        assertNull("accessed protected field (result not null) ?", accessedValue);
        accessedValue = ognlValueStack.findValue("@com.opensymphony.xwork2.ognl.OgnlValueStackTest@STATIC_FINAL_PRIVATE_ATTRIBUTE");
        assertNull("accessed final private field (result not null) ?", accessedValue);
        accessedValue = ognlValueStack.findValue("@com.opensymphony.xwork2.ognl.OgnlValueStackTest@STATIC_PRIVATE_ATTRIBUTE");
        assertNull("accessed private field (result not null) ?", accessedValue);
    }

    private void reloadTestContainerConfiguration(Boolean allowStaticMethod, Boolean allowStaticField) throws Exception {
        loadConfigurationProviders(new StubConfigurationProvider() {
            @Override
            public void register(ContainerBuilder builder,
                                 LocatableProperties props) throws ConfigurationException {
                // null values simulate undefined (by removing).
                // undefined values then should be evaluated to false
                if (props.containsKey(StrutsConstants.STRUTS_ALLOW_STATIC_METHOD_ACCESS)) {
                    props.remove(StrutsConstants.STRUTS_ALLOW_STATIC_METHOD_ACCESS);
                }
                if (props.containsKey(StrutsConstants.STRUTS_ALLOW_STATIC_FIELD_ACCESS)) {
                    props.remove(StrutsConstants.STRUTS_ALLOW_STATIC_FIELD_ACCESS);
                }
                if (allowStaticMethod != null) {
                    props.setProperty(StrutsConstants.STRUTS_ALLOW_STATIC_METHOD_ACCESS, "" + allowStaticMethod);
                }
                if (allowStaticField != null) {
                    props.setProperty(StrutsConstants.STRUTS_ALLOW_STATIC_FIELD_ACCESS, "" + allowStaticField);
                }
            }
        });
        ognlUtil = container.getInstance(OgnlUtil.class);
    }

    static class BadJavaBean {
        private int count;
        private int count2;

        public void setCount(int count) {
            this.count = count;
        }

        public String getCount() {
            return "" + count;
        }

        public void setCount2(String count2) {
            this.count2 = Integer.parseInt(count2);
        }

        public int getCount2() {
            return count2;
        }
    }

    static class MyAction {
        private Long id;
        private String name;
        private Address address;

        public Long getId() {
            return id;
        }

        public void setId(Long id) {
            this.id = id;
        }

        public String getName() {
            return name;
        }

        public void setName(String name) {
            this.name = name;
        }

        public Address getAddress() {
            return address;
        }

        public void setAddress(Address address) {
            this.address = address;
        }
    }

    static class Address {
        private String address;
        private Country country;
        private String city;

        public String getAddress() {
            return address;
        }

        public void setAddress(String address) {
            this.address = address;
        }

        public String getCity() {
            return city;
        }

        public void setCity(String city) {
            this.city = city;
        }

        public Country getCountry() {
            return country;
        }

        public void setCountry(Country country) {
            this.country = country;
        }
    }

    static class Country {
        private String iso;
        private String name;
        private String displayName;

        public String getIso() {
            return iso;
        }

        public void setIso(String iso) {
            this.iso = iso;
        }

        public String getName() {
            return name;
        }

        public void setName(String name) {
            this.name = name;
        }

        public String getDisplayName() {
            return displayName;
        }

        public void setDisplayName(String displayName) {
            this.displayName = displayName;
        }
    }

    static class TestAppender extends AbstractAppender {
        List<LogEvent> logEvents = new ArrayList<>();

        TestAppender() {
            super("TestAppender", null, null, false);
        }

        @Override
        public void append(LogEvent logEvent) {
            logEvents.add(logEvent);
        }
    }
}

enum MyNumbers {
    ONE, TWO, THREE
}
