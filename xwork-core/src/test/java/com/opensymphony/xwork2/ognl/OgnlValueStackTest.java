/*
 * Copyright 2002-2006,2009 The Apache Software Foundation.
 * 
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 * 
 *      http://www.apache.org/licenses/LICENSE-2.0
 * 
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package com.opensymphony.xwork2.ognl;

import com.opensymphony.xwork2.*;
import com.opensymphony.xwork2.conversion.impl.XWorkConverter;
import com.opensymphony.xwork2.ognl.accessor.CompoundRootAccessor;
import com.opensymphony.xwork2.test.TestBean2;
import com.opensymphony.xwork2.util.*;
import com.opensymphony.xwork2.util.Foo;
import com.opensymphony.xwork2.util.reflection.ReflectionContextState;
import ognl.PropertyAccessor;

import java.io.*;
import java.math.BigDecimal;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.Map;


/**
 * Unit test for OgnlValueStack.
 */
public class OgnlValueStackTest extends XWorkTestCase {

    public static Integer staticNullMethod() {
        return null;
    }

    private OgnlUtil ognlUtil;

    @Override
    public void setUp() throws Exception {
        super.setUp();
        ognlUtil = container.getInstance(OgnlUtil.class);
    }

    private OgnlValueStack createValueStack() {
        return createValueStack(true);
    }

    private OgnlValueStack createValueStack(boolean allowStaticMethodAccess) {
        OgnlValueStack stack = new OgnlValueStack(
                container.getInstance(XWorkConverter.class),
                (CompoundRootAccessor) container.getInstance(PropertyAccessor.class, CompoundRoot.class.getName()),
                container.getInstance(TextProvider.class, "system"), allowStaticMethodAccess);
        container.inject(stack);
        return stack;
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

    public void testStaticMethodDisallow() {
        OgnlValueStack vs = createValueStack(false);

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
        } catch (XWorkException e) {
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
        } catch (XWorkException e) {
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
        assertNull(stack.findValue("@com.opensymphony.xwork2.util.OgnlValueStackTest@staticNullMethod()"));
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

    public void testPrimitiveSettingWithInvalidValueAddsFieldErrorInDevMode() {
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

        Map conversionErrors = (Map) stack.getContext().get(ActionContext.CONVERSION_ERRORS);
        assertTrue(conversionErrors.containsKey("bar"));
    }

    public void testPrimitiveSettingWithInvalidValueAddsFieldErrorInNonDevMode() {
        SimpleAction action = new SimpleAction();
        OgnlValueStack stack = createValueStack();
        stack.getContext().put(XWorkConverter.REPORT_CONVERSION_ERRORS, Boolean.TRUE);
        stack.setDevMode("false");
        stack.push(action);
        stack.setValue("bar", "3x");

        Map conversionErrors = (Map) stack.getContext().get(ActionContext.CONVERSION_ERRORS);
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
        } catch (XWorkException e) {
            // expected
        }

        Map conversionErrors = (Map) stack.getContext().get(ActionContext.CONVERSION_ERRORS);
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

        vs.setValue("cats[0].foo.cats[1].name", "Deep null cat");
        assertNotNull(((Cat) foo.getCats().get(0)).getFoo());
        assertNotNull(((Cat) foo.getCats().get(0)).getFoo().getCats());
        assertNotNull(((Cat) foo.getCats().get(0)).getFoo().getCats().get(1));
        assertEquals("Deep null cat", ((Cat) ((Cat) foo.getCats().get(0)).getFoo().getCats().get(1)).getName());
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

        assertEquals(false, dog.isMale());
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
        assertEquals(new Integer(BigDecimal.ROUND_HALF_DOWN), vs.findValue("@java.math.BigDecimal@ROUND_HALF_DOWN"));
        assertNull(vs.findValue("@vs3@BLAH"));
        assertNull(vs.findValue("@com.nothing.here.Nothing@BLAH"));
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
        } catch (XWorkException e) {
            // expected
        }

        Map conversionErrors = (Map) stack.getContext().get(ActionContext.CONVERSION_ERRORS);
        assertTrue(conversionErrors.containsKey("count"));
    }

    public void testConstructorWithAStack() {
        OgnlValueStack stack = createValueStack();
        stack.push("Hello World");

        OgnlValueStack stack2 = new OgnlValueStack(stack,
                container.getInstance(XWorkConverter.class),
                (CompoundRootAccessor) container.getInstance(PropertyAccessor.class, CompoundRoot.class.getName()), true);
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
        assertEquals(null, stack.findValue(null));

        stack.setDefaultType(Integer.class);
        stack.push(new Integer(123));
        assertEquals(new Integer(123), stack.findValue("top"));
    }

    public void testFindString() {
        OgnlValueStack stack = createValueStack();
        stack.setDefaultType(Integer.class);
        stack.push("Hello World");

        assertEquals("Hello World", stack.findString("top"));
        assertEquals(null, stack.findString(null));
    }

    public void testExpOverrides() {
        Map overrides = new HashMap();
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
        assertEquals(null, stack.findValue("unknown", String.class));
    }

    public void testWarnAboutInvalidProperties() {
        OgnlValueStack stack = createValueStack();
        MyAction action = new MyAction();
        action.setName("Don");
        stack.push(action);

        // how to test the warning was logged?
        assertEquals("Don", stack.findValue("name", String.class));
        assertEquals(null, stack.findValue("address", String.class));
        // should log warning
        assertEquals(null, stack.findValue("address.invalidProperty", String.class));

        // if country is null, OGNL throws an exception
        /*action.setAddress(new Address());
        stack.push(action);*/
        // should log warning
        assertEquals(null, stack.findValue("address.country.id", String.class));
        assertEquals(null, stack.findValue("address.country.name", String.class));
    }

    class BadJavaBean {
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

    class MyAction {
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

    class Address {
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

    class Country {
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
}
