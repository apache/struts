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
package org.apache.struts2.ognl;

import ognl.OgnlException;
import org.apache.commons.lang3.StringUtils;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.core.LogEvent;
import org.apache.logging.log4j.core.Logger;
import org.apache.logging.log4j.core.appender.AbstractAppender;
import org.apache.struts2.SimpleAction;
import org.apache.struts2.StrutsConstants;
import org.apache.struts2.StrutsException;
import org.apache.struts2.TestBean;
import org.apache.struts2.XWorkTestCase;
import org.apache.struts2.config.ConfigurationException;
import org.apache.struts2.config.DefaultPropertiesProvider;
import org.apache.struts2.conversion.impl.ConversionData;
import org.apache.struts2.conversion.impl.XWorkConverter;
import org.apache.struts2.inject.ContainerBuilder;
import org.apache.struts2.ognl.accessor.RootAccessor;
import org.apache.struts2.test.StubConfigurationProvider;
import org.apache.struts2.test.TestBean2;
import org.apache.struts2.text.TextProvider;
import org.apache.struts2.util.Bar;
import org.apache.struts2.util.BarJunior;
import org.apache.struts2.util.Cat;
import org.apache.struts2.util.Dog;
import org.apache.struts2.util.Foo;
import org.apache.struts2.util.ValueStackFactory;
import org.apache.struts2.util.location.LocatableProperties;
import org.apache.struts2.util.reflection.ReflectionContextState;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.math.RoundingMode;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import static org.apache.struts2.ognl.SecurityMemberAccessTest.reflectField;

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

    private OgnlValueStack vs;

    @Override
    public void setUp() throws Exception {
        super.setUp();
        refreshContainerFields();
    }

    protected void refreshContainerFields() {
        ognlUtil = container.getInstance(OgnlUtil.class);
        vs = (OgnlValueStack) container.getInstance(ValueStackFactory.class).createValueStack();
    }

    /**
     * Reloads container and sets a new OgnlValueStackFactory with specified new configuration.
     * Intended for testing OgnlValueStack instance(s) that are minimally configured.
     * This should help ensure no underlying configuration/injection side-effects are responsible
     * for the behaviour of fundamental access control flags).
     *
     * @param allowStaticField new allowStaticField configuration
     */
    private void reloadContainer(boolean allowStaticField) {
        Map<String, String> properties = new HashMap<>();
        properties.put(StrutsConstants.STRUTS_ALLOW_STATIC_FIELD_ACCESS, Boolean.toString(allowStaticField));
        loadButSet(properties);
        refreshContainerFields();
    }

    public void testExpOverridesCanStackExpUp() throws Exception {
        Map expr1 = new LinkedHashMap();
        expr1.put("expr1", "'expr1value'");

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
        final String propertyValue = "Rover";
        vs.getContext().put(propertyName, new String[]{propertyValue});

        assertEquals(propertyValue, vs.findValue(propertyName, String.class));
    }

    public void testNullValueFromContextGetsConverted() {
        testNullValueFromContextGetsConverted("dogName");
        testNullValueFromContextGetsConverted("dog.name");
    }

    private void testNullValueFromContextGetsConverted(String propertyName) {
        final String propertyValue = null;
        vs.getContext().put(propertyName, propertyValue);

        assertEquals(propertyValue, vs.findValue(propertyName, String.class));
    }

    public void testFailOnException() {
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
        Foo foo = new Foo();
        BarJunior barjr = new BarJunior();
        foo.setBarJunior(barjr);
        vs.push(foo);

        assertNull(barjr.getTitle());
        vs.findValue("barJunior.title", true);
    }

    public void testSuccessFailOnErrorOnInheritedPropertiesWithMethods() {
        //this shuld not fail as the property is defined on a parent class
        Foo foo = new Foo();
        BarJunior barjr = new BarJunior();
        foo.setBarJunior(barjr);
        vs.push(foo);

        assertNull(barjr.getTitle());
        vs.findValue("getBarJunior().title", true);
    }

    public void testFailFailOnErrorOnInheritedPropertiesWithMethods() {
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
     *
     * @since 2.5.21
     */
    public void testLogMissingProperties() {
        testLogMissingProperties(true);
        testLogMissingProperties(false);
    }

    private void testLogMissingProperties(boolean logMissingProperties) {
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
     *
     * @since 2.5.21
     */
    public void testNotLogUserExceptionsAsMissingProperties() {
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
            int repeat = Integer.parseInt(
                container.getInstance(String.class, StrutsConstants.STRUTS_OGNL_EXPRESSION_MAX_LENGTH));

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

        String defaultMaxLengthFromConfiguration = container.getInstance(String.class, StrutsConstants.STRUTS_OGNL_EXPRESSION_MAX_LENGTH);
        assertNotNull(defaultMaxLengthFromConfiguration);

        int defaultValue = 256;

        try {
            vs.findValue(StringUtils.repeat('.', defaultValue + 1), true);
            fail("findValue did not throw any exception (should either fail as invalid expression syntax or security exception) ?");
        } catch (Exception ex) {
            assertTrue(ex.getCause() instanceof OgnlException);
            assertTrue(((OgnlException) ex.getCause()).getReason() instanceof SecurityException);
            assertTrue(((OgnlException) ex.getCause()).getReason().getMessage().startsWith("This expression exceeded maximum allowed length"));
        }
    }

    public void testNotFailOnTooLongValueWithDefaultProperties() {
        loadConfigurationProviders(new DefaultPropertiesProvider());

        Object defaultMaxLengthFromConfiguration = container.getInstance(String.class, StrutsConstants.STRUTS_OGNL_EXPRESSION_MAX_LENGTH);
        assertNotNull(defaultMaxLengthFromConfiguration);

        int defaultValue = 256;

        Dog dog = new Dog();
        vs.push(dog);

        String value = StringUtils.repeat('.', defaultValue);

        vs.setValue("name", value);

        assertEquals(value, dog.getName());
    }

    public void testFailsOnMethodThatThrowsException() {
        SimpleAction action = new SimpleAction();
        vs.push(action);

        action.setThrowException(true);
        try {
            vs.findValue("exceptionMethod12()", true);
            fail("Failed to throw exception on EL method exception");
        } catch (Exception ex) {
            //ok
        }
    }


    public void testDoesNotFailOnNonActionObjects() {
        //if a value is not found, then it will check for missing properties
        //it needs to check in all objects in the stack, not only actions, see WW-3306
        Dog dog = new Dog();
        dog.setHates(null);
        vs.push(dog);
        vs.findValue("hates", true);
    }


    public void testFailOnMissingNestedProperty() {
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
        Dog dog = new Dog();
        dog.setAge(12);
        dog.setName("Rover");

        vs.push(dog);
        assertEquals("Rover", vs.findValue("name", String.class));
    }

    public void testStatic() {
        Dog dog = new Dog();
        dog.setDeity("fido");
        vs.push(dog);
        assertNull(vs.findValue("@org.apache.struts2.util.Dog@getDeity()", String.class));
    }

    /**
     * Enum methods should also be banned alongside static methods
     */
    public void testEnum() throws Exception {
        assertNull("ONE", vs.findValue("@org.apache.struts2.ognl.MyNumbers@values()[0]", String.class));
        assertNull("TWO", vs.findValue("@org.apache.struts2.ognl.MyNumbers@values()[1]", String.class));
        assertNull("THREE", vs.findValue("@org.apache.struts2.ognl.MyNumbers@values()[2]", String.class));
    }

    public void testStaticMethodDisallow() {
        Dog dog = new Dog();
        dog.setDeity("fido");
        vs.push(dog);
        assertNull(vs.findValue("@org.apache.struts2.util.Dog@getDeity()", String.class));
    }

    public void testBasicSet() {
        Dog dog = new Dog();
        dog.setAge(12);
        dog.setName("Rover");

        vs.set("dog", dog);
        assertEquals("Rover", vs.findValue("dog.name", String.class));
    }

    public void testCallMethodOnNullObject() {
        assertNull(vs.findValue("foo.size()"));
    }

    public void testCallMethodThatThrowsExceptionTwice() {
        SimpleAction action = new SimpleAction();
        vs.push(action);

        action.setThrowException(true);
        assertNull(vs.findValue("exceptionMethod1()"));
        action.setThrowException(false);
        assertEquals("OK", vs.findValue("exceptionMethod()"));
    }


    public void testCallMethodWithNullArg() {
        SimpleAction action = new SimpleAction();
        vs.push(action);

        vs.findValue("setName(blah)");
        assertNull(action.getName());

        action.setBlah("blah");
        vs.findValue("setName(blah)");
        assertEquals("blah", action.getName());
    }

    public void testConvertStringArrayToList() {
        Foo foo = new Foo();
        vs.push(foo);

        vs.setValue("strings", new String[]{"one", "two"});

        assertNotNull(foo.getStrings());
        assertEquals("one", foo.getStrings().get(0));
        assertEquals("two", foo.getStrings().get(1));
    }

    public void testFindValueWithConversion() {

        // register converter
        TestBean2 tb2 = new TestBean2();

        vs.push(tb2);
        Map myContext = vs.getContext();

        Map props = new HashMap();
        props.put("cat", "Kitty");
        ognlUtil.setProperties(props, tb2, myContext);
        // expect String to be converted into a Cat
        assertEquals("Kitty", tb2.getCat().getName());

        // findValue should be able to access the name
        Object value = vs.findValue("cat.name == 'Kitty'", Boolean.class);
        assertNotNull(value);
        assertEquals(Boolean.class, value.getClass());
        assertEquals(Boolean.TRUE, value);

        value = vs.findValue("cat == null", Boolean.class);
        assertNotNull(value);
        assertEquals(Boolean.class, value.getClass());
        assertEquals(Boolean.FALSE, value);
    }


    public void testDeepProperties() {
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
        vs.push(dog);
        assertNull(vs.findValue("name"));
    }

    public void testMapEntriesAvailableByKey() {
        Foo foo = new Foo();
        String title = "a title";
        foo.setTitle(title);

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
        Dog dog = new Dog();
        dog.setName("Rover");

        vs.push(dog);
        assertEquals("Rover", vs.findValue("name", String.class));

        vs.push(null);
        assertEquals("Rover", vs.findValue("name", String.class));
    }

    public void testNullMethod() {
        Dog dog = new Dog();
        vs.push(dog);
        assertNull(vs.findValue("nullMethod()"));
        assertNull(vs.findValue("@org.apache.struts2.ognl.OgnlValueStackTest@staticNullMethod()"));
    }

    public void testPetSoarBug() {
        Cat cat = new Cat();
        cat.setFoo(new Foo());

        Bar bar = new Bar();
        bar.setTitle("bar");
        bar.setSomethingElse(123);
        cat.getFoo().setBar(bar);

        vs.push(cat);

        assertEquals("bar:123", vs.findValue("foo.bar", String.class));
    }

    public void testPrimitiveSettingWithInvalidValueAddsFieldErrorInDevMode() throws Exception {
        SimpleAction action = new SimpleAction();
        vs.getContext().put(XWorkConverter.REPORT_CONVERSION_ERRORS, Boolean.TRUE);
        vs.setDevMode("true");
        vs.push(action);

        try {
            vs.setValue("bar", "3x");
            fail("Attempt to set 'bar' int property to '3x' should result in RuntimeException");
        } catch (RuntimeException re) {
            assertTrue(true);
        }

        Map<String, ConversionData> conversionErrors = vs.getActionContext().getConversionErrors();
        assertTrue(conversionErrors.containsKey("bar"));
    }

    public void testPrimitiveSettingWithInvalidValueAddsFieldErrorInNonDevMode() {
        SimpleAction action = new SimpleAction();
        vs.getContext().put(XWorkConverter.REPORT_CONVERSION_ERRORS, Boolean.TRUE);
        vs.setDevMode("false");
        vs.push(action);
        vs.setValue("bar", "3x");

        Map<String, ConversionData> conversionErrors = vs.getActionContext().getConversionErrors();
        assertTrue(conversionErrors.containsKey("bar"));
    }


    public void testObjectSettingWithInvalidValueDoesNotCauseSetCalledWithNull() {
        SimpleAction action = new SimpleAction();
        action.setBean(new TestBean());
        vs.getContext().put(XWorkConverter.REPORT_CONVERSION_ERRORS, Boolean.TRUE);
        vs.push(action);
        try {
            vs.setValue("bean", "foobar", true);
            fail("Should have thrown a type conversion exception");
        } catch (StrutsException e) {
            // expected
        }

        Map<String, ConversionData> conversionErrors = vs.getActionContext().getConversionErrors();
        assertTrue(conversionErrors.containsKey("bean"));
        assertNotNull(action.getBean());
    }


    public void testSerializable() throws IOException, ClassNotFoundException {
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
        Dog d = new Dog();
        d.setName("Rover");
        vs.push(d);

        vs.set("name", "Bill");

        assertEquals("Bill", vs.findValue("name"));

    }

    public void testSetBarAsString() {
        Foo foo = new Foo();

        vs.push(foo);

        vs.setValue("bar", "bar:123");

        assertEquals("bar", foo.getBar().getTitle());
        assertEquals(123, foo.getBar().getSomethingElse());
    }

    public void testSetBeforePush() {
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

        vs.push(foo);

        vs.setValue("child.bar", "bar:123");

        assertEquals("bar", foo.getChild().getBar().getTitle());
        assertEquals(123, foo.getChild().getBar().getSomethingElse());
    }

    public void testSetNullList() {
        Foo foo = new Foo();
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
        int origSize = vs.getRoot().size();
        vs.set("something", new Object());
        vs.set("somethingElse", new Object());
        vs.set("yetSomethingElse", new Object());
        assertEquals(origSize + 1, vs.getRoot().size());

    }

    public void testSetNullMap() {
        Foo foo = new Foo();
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

        vs.push(foo);

        vs.setValue("child.child.bar", "bar:123");

        assertEquals("bar", foo.getChild().getChild().getBar().getTitle());
        assertEquals(123, foo.getChild().getChild().getBar().getSomethingElse());
    }

    public void testSettingDogGender() {
        Dog dog = new Dog();
        vs.push(dog);

        vs.setValue("male", "false");

        assertFalse(dog.isMale());
    }

    public void testStatics() {
        Cat cat = new Cat();
        vs.push(cat);

        Dog dog = new Dog();
        dog.setAge(12);
        dog.setName("Rover");
        vs.push(dog);

        assertEquals("Canine", vs.findValue("@vs@SCIENTIFIC_NAME"));
        assertEquals("Canine", vs.findValue("@vs1@SCIENTIFIC_NAME"));
        assertEquals("Feline", vs.findValue("@vs2@SCIENTIFIC_NAME"));
        assertEquals(RoundingMode.HALF_DOWN, vs.findValue("@java.math.RoundingMode@HALF_DOWN"));
        assertNull(vs.findValue("@vs3@BLAH"));
        assertNull(vs.findValue("@com.nothing.here.Nothing@BLAH"));
    }

    /**
     * Fails on 2.5.20 and earlier - tested on 2.5 (5/5/2016) and failed
     *
     * @since 2.5.21
     */
    public void testNotThrowExceptionOnTopMissingProperty() {
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
     *
     * @since 2.5.21
     */
    public void testNotSkipUserReturnedNullValues() {
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
        assertEquals(container.getInstance(TextProvider.class, "system"), vs.findValue("top"));
    }

    public void testTwoDogs() {
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
        vs.push(bean);
        vs.getContext().put(XWorkConverter.REPORT_CONVERSION_ERRORS, Boolean.TRUE);
        try {
            vs.setValue("count", "a", true);
            fail("Should have thrown a type conversion exception");
        } catch (StrutsException e) {
            // expected
        }

        Map<String, ConversionData> conversionErrors = vs.getActionContext().getConversionErrors();
        assertTrue(conversionErrors.containsKey("count"));
    }

    public void testConstructorWithAStack() {
        vs.push("Hello World");

        OgnlValueStack stack2 = new OgnlValueStack(
                vs,
                container.getInstance(XWorkConverter.class),
                container.getInstance(RootAccessor.class),
                new SecurityMemberAccess(null, null));
        container.inject(stack2);

        assertEquals(vs.getRoot(), stack2.getRoot());
        assertEquals(vs.peek(), stack2.peek());
        assertEquals("Hello World", stack2.pop());

    }

    public void testDefaultType() {
        vs.setDefaultType(String.class);
        vs.push("Hello World");

        assertEquals("Hello World", vs.findValue("top"));
        assertNull(vs.findValue(null));

        vs.setDefaultType(Integer.class);
        vs.push(123);
        assertEquals(123, vs.findValue("top"));
    }

    public void testFindString() {
        vs.setDefaultType(Integer.class);
        vs.push("Hello World");

        assertEquals("Hello World", vs.findString("top"));
        assertNull(vs.findString(null));
    }

    public void testExpOverrides() {
        Map<Object, Object> overrides = new HashMap<>();
        overrides.put("claus", "top");

        vs.setExprOverrides(overrides);
        vs.push("Hello World");

        assertEquals("Hello World", vs.findValue("claus"));
        assertEquals("Hello World", vs.findString("claus"));
        assertEquals("Hello World", vs.findValue("top"));
        assertEquals("Hello World", vs.findString("top"));

        assertEquals("Hello World", vs.findValue("claus", String.class));
        assertEquals("Hello World", vs.findValue("top", String.class));

        assertNull(vs.findValue("unknown", String.class));
    }

    public void testExprFallbackToContext() {
        vs.getContext().put("santa", "Hello Santa");
        assertEquals("Hello Santa", vs.findValue("santa", String.class));
    }

    public void testExprFallbackToContext_disabled() {
        vs.setShouldFallbackToContext("false");
        vs.getContext().put("santa", "Hello Santa");
        assertNull(vs.findValue("santa", String.class));
    }

    public void testWarnAboutInvalidProperties() {
        MyAction action = new MyAction();
        action.setName("Don");
        vs.push(action);

        // how to test the warning was logged?
        assertEquals("Don", vs.findValue("name", String.class));
        assertNull(vs.findValue("address", String.class));
        // should log warning
        assertNull(vs.findValue("address.invalidProperty", String.class));

        // if country is null, OGNL throws an exception
        /*action.setAddress(new Address());
        stack.push(action);*/
        // should log warning
        assertNull(vs.findValue("address.country.id", String.class));
        assertNull(vs.findValue("address.country.name", String.class));
    }

    /**
     * Test a default OgnlValueStackFactory and OgnlValueStack generated by it
     * when a default configuration is used.
     */
    public void testOgnlValueStackFromOgnlValueStackFactoryDefaultConfig() throws IllegalAccessException {
        Object accessedValue;

        assertTrue("OgnlValueStackFactory staticFieldAccess (default flags) not true?",
                reflectField(vs.securityMemberAccess, "allowStaticFieldAccess"));
        // An OgnlValueStack created from the above OgnlValueStackFactory should allow public field access,
        // but prevent non-public field access.  It should also deny static method access.
        accessedValue = vs.findValue("@org.apache.struts2.ognl.OgnlValueStackTest@staticInteger100Method()");
        assertNull("able to access static method (result not null) ?", accessedValue);
        accessedValue = vs.findValue("@org.apache.struts2.ognl.OgnlValueStackTest@STATIC_FINAL_PUBLIC_ATTRIBUTE");
        assertEquals("accessed static final public field value not equal to actual?", accessedValue, STATIC_FINAL_PUBLIC_ATTRIBUTE);
        accessedValue = vs.findValue("@org.apache.struts2.ognl.OgnlValueStackTest@STATIC_PUBLIC_ATTRIBUTE");
        assertEquals("accessed static public field value not equal to actual?", accessedValue, STATIC_PUBLIC_ATTRIBUTE);
        accessedValue = vs.findValue("@org.apache.struts2.ognl.OgnlValueStackTest@STATIC_FINAL_PACKAGE_ATTRIBUTE");
        assertNull("accessed final package field (result not null) ?", accessedValue);
        accessedValue = vs.findValue("@org.apache.struts2.ognl.OgnlValueStackTest@STATIC_PACKAGE_ATTRIBUTE");
        assertNull("accessed package field (result not null) ?", accessedValue);
        accessedValue = vs.findValue("@org.apache.struts2.ognl.OgnlValueStackTest@STATIC_FINAL_PROTECTED_ATTRIBUTE");
        assertNull("accessed final protected field (result not null) ?", accessedValue);
        accessedValue = vs.findValue("@org.apache.struts2.ognl.OgnlValueStackTest@STATIC_PROTECTED_ATTRIBUTE");
        assertNull("accessed protected field (result not null) ?", accessedValue);
        accessedValue = vs.findValue("@org.apache.struts2.ognl.OgnlValueStackTest@STATIC_FINAL_PRIVATE_ATTRIBUTE");
        assertNull("accessed final private field (result not null) ?", accessedValue);
        accessedValue = vs.findValue("@org.apache.struts2.ognl.OgnlValueStackTest@STATIC_PRIVATE_ATTRIBUTE");
        assertNull("accessed private field (result not null) ?", accessedValue);
    }

    /**
     * Test a raw OgnlValueStackFactory and OgnlValueStack generated by it
     * when static access flag is set to false.
     */
    public void testOgnlValueStackFromOgnlValueStackFactoryNoStaticAccess() throws IllegalAccessException {
        reloadContainer(false);
        Object accessedValue;

        assertFalse("OgnlValueStackFactory staticFieldAccess (set false) not false?",
                reflectField(vs.securityMemberAccess, "allowStaticFieldAccess"));
        // An OgnlValueStack created from the above OgnlValueStackFactory should prevent public field access,
        // and prevent non-public field access.  It should also deny static method access.
        accessedValue = vs.findValue("@org.apache.struts2.ognl.OgnlValueStackTest@staticInteger100Method()");
        assertNull("able to access static method (result not null) ?", accessedValue);
        accessedValue = vs.findValue("@org.apache.struts2.ognl.OgnlValueStackTest@STATIC_FINAL_PUBLIC_ATTRIBUTE");
        assertNull("able to access static final public field (result not null) ?", accessedValue);
        accessedValue = vs.findValue("@org.apache.struts2.ognl.OgnlValueStackTest@STATIC_PUBLIC_ATTRIBUTE");
        assertNull("able to access static public field (result not null) ?", accessedValue);
        accessedValue = vs.findValue("@org.apache.struts2.ognl.OgnlValueStackTest@STATIC_FINAL_PACKAGE_ATTRIBUTE");
        assertNull("accessed final package field (result not null) ?", accessedValue);
        accessedValue = vs.findValue("@org.apache.struts2.ognl.OgnlValueStackTest@STATIC_PACKAGE_ATTRIBUTE");
        assertNull("accessed package field (result not null) ?", accessedValue);
        accessedValue = vs.findValue("@org.apache.struts2.ognl.OgnlValueStackTest@STATIC_FINAL_PROTECTED_ATTRIBUTE");
        assertNull("accessed final protected field (result not null) ?", accessedValue);
        accessedValue = vs.findValue("@org.apache.struts2.ognl.OgnlValueStackTest@STATIC_PROTECTED_ATTRIBUTE");
        assertNull("accessed protected field (result not null) ?", accessedValue);
        accessedValue = vs.findValue("@org.apache.struts2.ognl.OgnlValueStackTest@STATIC_FINAL_PRIVATE_ATTRIBUTE");
        assertNull("accessed final private field (result not null) ?", accessedValue);
        accessedValue = vs.findValue("@org.apache.struts2.ognl.OgnlValueStackTest@STATIC_PRIVATE_ATTRIBUTE");
        assertNull("accessed private field (result not null) ?", accessedValue);
    }

    /**
     * Test a raw OgnlValueStackFactory and OgnlValueStack generated by it
     * when static access flag is set to true.
     */
    public void testOgnlValueStackFromOgnlValueStackFactoryAllStaticAccess() throws IllegalAccessException {
        reloadContainer(true);
        Object accessedValue;

        assertTrue("OgnlValueStackFactory staticFieldAccess (set true) not true?",
                reflectField(vs.securityMemberAccess, "allowStaticFieldAccess"));
        // An OgnlValueStack created from the above OgnlValueStackFactory should allow public field access,
        // but prevent non-public field access.  It should also allow static method access.
        accessedValue = vs.findValue("@org.apache.struts2.ognl.OgnlValueStackTest@staticInteger100Method()");
        assertNull("able to access static method (result non-null)!!!", accessedValue);
        accessedValue = vs.findValue("@org.apache.struts2.ognl.OgnlValueStackTest@STATIC_FINAL_PUBLIC_ATTRIBUTE");
        assertEquals("accessed static final public field value not equal to actual?", accessedValue, STATIC_FINAL_PUBLIC_ATTRIBUTE);
        accessedValue = vs.findValue("@org.apache.struts2.ognl.OgnlValueStackTest@STATIC_PUBLIC_ATTRIBUTE");
        assertEquals("accessed static public field value not equal to actual?", accessedValue, STATIC_PUBLIC_ATTRIBUTE);
        accessedValue = vs.findValue("@org.apache.struts2.ognl.OgnlValueStackTest@STATIC_FINAL_PACKAGE_ATTRIBUTE");
        assertNull("accessed final package field (result not null) ?", accessedValue);
        accessedValue = vs.findValue("@org.apache.struts2.ognl.OgnlValueStackTest@STATIC_PACKAGE_ATTRIBUTE");
        assertNull("accessed package field (result not null) ?", accessedValue);
        accessedValue = vs.findValue("@org.apache.struts2.ognl.OgnlValueStackTest@STATIC_FINAL_PROTECTED_ATTRIBUTE");
        assertNull("accessed final protected field (result not null) ?", accessedValue);
        accessedValue = vs.findValue("@org.apache.struts2.ognl.OgnlValueStackTest@STATIC_PROTECTED_ATTRIBUTE");
        assertNull("accessed protected field (result not null) ?", accessedValue);
        accessedValue = vs.findValue("@org.apache.struts2.ognl.OgnlValueStackTest@STATIC_FINAL_PRIVATE_ATTRIBUTE");
        assertNull("accessed final private field (result not null) ?", accessedValue);
        accessedValue = vs.findValue("@org.apache.struts2.ognl.OgnlValueStackTest@STATIC_PRIVATE_ATTRIBUTE");
        assertNull("accessed private field (result not null) ?", accessedValue);
    }

    public void testFindValueWithConstructorAndProxyChecks() {
        loadButSet(Map.of(
                StrutsConstants.STRUTS_DISALLOW_PROXY_OBJECT_ACCESS, Boolean.TRUE.toString(),
                StrutsConstants.STRUTS_DISALLOW_PROXY_MEMBER_ACCESS, Boolean.TRUE.toString()));
        refreshContainerFields();

        String value = "test";
        String ognlResult = (String) vs.findValue(
                "new org.apache.struts2.ognl.OgnlValueStackTest$ValueHolder('" + value + "').value", String.class);

        assertEquals(value, ognlResult);
    }

    @SuppressWarnings({"unused", "ClassCanBeRecord"})
    public static class ValueHolder {
        // See testFindValueWithConstructorAndProxyChecks
        private final String value;

        public ValueHolder(String value) {
            this.value = value;
        }

        public String getValue() {
            return value;
        }
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
