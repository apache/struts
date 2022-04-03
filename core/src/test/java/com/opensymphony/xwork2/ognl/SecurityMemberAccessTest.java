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

import com.opensymphony.xwork2.util.TextParseUtil;
import junit.framework.TestCase;

import java.lang.reflect.Field;
import java.lang.reflect.Member;
import java.lang.reflect.Modifier;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;
import java.util.regex.Pattern;

public class SecurityMemberAccessTest extends TestCase {

    private Map context;
    private FooBar target;

    @Override
    public void setUp() throws Exception {
        context = new HashMap();
        target = new FooBar();
    }

    public void testWithoutClassExclusion() throws Exception {
        // given
        SecurityMemberAccess sma = new SecurityMemberAccess(false, true);

        String propertyName = "stringField";
        Member member = FooBar.class.getMethod("get" + propertyName.substring(0, 1).toUpperCase() + propertyName.substring(1));

        // when
        boolean accessible = sma.isAccessible(context, target, member, propertyName);

        // then
        assertTrue(accessible);
    }

    public void testClassExclusion() throws Exception {
        // given
        SecurityMemberAccess sma = new SecurityMemberAccess(false, true);

        String propertyName = "stringField";
        Member member = FooBar.class.getDeclaredMethod("get" + propertyName.substring(0, 1).toUpperCase() + propertyName.substring(1));

        Set<Class<?>> excluded = new HashSet<>();
        excluded.add(FooBar.class);
        sma.setExcludedClasses(excluded);

        // when
        boolean accessible = sma.isAccessible(context, target, member, propertyName);

        // then
        assertFalse(accessible);
    }

    public void testObjectClassExclusion() throws Exception {
        // given
        SecurityMemberAccess sma = new SecurityMemberAccess(false, true);

        String propertyName = "toString";
        Member member = FooBar.class.getMethod(propertyName);

        // when
        boolean accessible = sma.isAccessible(context, target, member, propertyName);

        // then
        assertFalse("toString() from Object is accessible!!!", accessible);
    }

    public void testObjectOverwrittenMethodsExclusion() throws Exception {
        // given
        SecurityMemberAccess sma = new SecurityMemberAccess(false, true);

        String propertyName = "hashCode";
        Member member = FooBar.class.getMethod(propertyName);

        // when
        boolean accessible = sma.isAccessible(context, target, member, propertyName);

        // then
        assertTrue("hashCode() from FooBar isn't accessible!!!", accessible);
    }

    public void testInterfaceInheritanceExclusion() throws Exception {
        // given
        SecurityMemberAccess sma = new SecurityMemberAccess(false, true);

        String propertyName = "barLogic";
        Member member = BarInterface.class.getMethod(propertyName);

        Set<Class<?>> excluded = new HashSet<>();
        excluded.add(BarInterface.class);
        sma.setExcludedClasses(excluded);

        // when
        boolean accessible = sma.isAccessible(context, target, member, propertyName);

        // then
        assertFalse("barLogic() from BarInterface is accessible!!!", accessible);
    }

    public void testMiddleOfInheritanceExclusion1() throws Exception {
        // given
        SecurityMemberAccess sma = new SecurityMemberAccess(false, true);

        String propertyName = "fooLogic";
        Member member = FooBar.class.getMethod(propertyName);

        Set<Class<?>> excluded = new HashSet<>();
        excluded.add(BarInterface.class);
        sma.setExcludedClasses(excluded);

        // when
        boolean accessible = sma.isAccessible(context, target, member, propertyName);

        // then
        assertTrue("fooLogic() from FooInterface isn't accessible!!!", accessible);
    }

    public void testMiddleOfInheritanceExclusion3() throws Exception {
        // given
        SecurityMemberAccess sma = new SecurityMemberAccess(false, true);

        String propertyName = "barLogic";
        Member member = BarInterface.class.getMethod(propertyName);

        // when
        boolean accessible = sma.isAccessible(context, target, member, propertyName);

        // then
        assertTrue("barLogic() from BarInterface isn't accessible!!!", accessible);
    }

    public void testMiddleOfInheritanceExclusion4() throws Exception {
        // given
        SecurityMemberAccess sma = new SecurityMemberAccess(false, true);

        String propertyName = "barLogic";
        Member member = BarInterface.class.getMethod(propertyName);

        Set<Class<?>> excluded = new HashSet<>();
        excluded.add(FooBarInterface.class);
        sma.setExcludedClasses(excluded);

        // when
        boolean accessible = sma.isAccessible(context, target, member, propertyName);

        // then
        assertFalse("barLogic() from BarInterface is accessible!!!", accessible);
    }

    public void testPackageExclusion() throws Exception {
        // given
        SecurityMemberAccess sma = new SecurityMemberAccess(false, true);

        Set<Pattern> excluded = new HashSet<>();
        excluded.add(Pattern.compile("^" + FooBar.class.getPackage().getName().replaceAll("\\.", "\\\\.") + ".*"));
        sma.setExcludedPackageNamePatterns(excluded);

        String propertyName = "stringField";
        Member member = FooBar.class.getMethod("get" + propertyName.substring(0, 1).toUpperCase() + propertyName.substring(1));

        // when
        boolean actual = sma.isAccessible(context, target, member, propertyName);

        // then
        assertFalse("stringField is accessible!", actual);
    }
    
    public void testPackageNameExclusion() throws Exception {
        // given
        SecurityMemberAccess sma = new SecurityMemberAccess(false, true);

        Set<String> excluded = new HashSet<>();
        excluded.add(FooBar.class.getPackage().getName());
        sma.setExcludedPackageNames(excluded);

        String propertyName = "stringField";
        Member member = FooBar.class.getMethod("get" + propertyName.substring(0, 1).toUpperCase() + propertyName.substring(1));

        // when
        boolean actual = sma.isAccessible(context, target, member, propertyName);

        // then
        assertFalse("stringField is accessible!", actual);
    }

    public void testDefaultPackageExclusion() {
        // given
        SecurityMemberAccess sma = new SecurityMemberAccess(false, true);

        Set<Pattern> excluded = new HashSet<>();
        excluded.add(Pattern.compile("^" + FooBar.class.getPackage().getName().replaceAll("\\.", "\\\\.") + ".*"));
        sma.setExcludedPackageNamePatterns(excluded);
        
        // when
        boolean actual = sma.isPackageExcluded(null, null);

        // then
        assertFalse("default package is excluded!", actual);
    }
    
    public void testDefaultPackageExclusion2() {
        // given
        SecurityMemberAccess sma = new SecurityMemberAccess(false, true);

        Set<Pattern> excluded = new HashSet<>();
        excluded.add(Pattern.compile("^$"));
        sma.setExcludedPackageNamePatterns(excluded);
        
        // when
        boolean actual = sma.isPackageExcluded(null, null);

        // then
        assertTrue("default package isn't excluded!", actual);
    }

    public void testAccessEnum() throws Exception {
        // given
        SecurityMemberAccess sma = new SecurityMemberAccess(false, true);

        // when
        Member values = MyValues.class.getMethod("values");
        boolean actual = sma.isAccessible(context, MyValues.class, values, null);

        // then
        assertTrue("Access to enums is blocked!", actual);
    }

    public void testAccessStatic() throws Exception {
        // given
        SecurityMemberAccess sma = new SecurityMemberAccess(true, true);
        sma.setExcludedClasses(new HashSet<Class<?>>(Collections.singletonList(Class.class)));

        // when
        Member method = StaticTester.class.getMethod("sayHello");
        boolean actual = sma.isAccessible(context, Class.class, method, null);

        // then
        assertTrue("Access to static is blocked!", actual);
    }

    public void testAccessStaticField() throws Exception {
        // given
        SecurityMemberAccess sma = new SecurityMemberAccess(true, true);
        sma.setExcludedClasses(new HashSet<Class<?>>(Collections.singletonList(Class.class)));

        // when
        Member method = StaticTester.class.getField("MAX_VALUE");
        boolean actual = sma.isAccessible(context, null, method, null);

        // then
        assertTrue("Access to static field is blocked!", actual);
    }

    public void testBlockedStaticFieldWhenFlagIsFalse() throws Exception {
        // given
        SecurityMemberAccess sma = new SecurityMemberAccess(false, true);
        sma.setExcludedClasses(new HashSet<Class<?>>(Collections.singletonList(Class.class)));

        // when
        Member method = StaticTester.class.getField("MAX_VALUE");
        boolean actual = sma.isAccessible(context, null, method, null);

        // then
        assertTrue("Access to public static field is blocked?", actual);

        // public static final test
        // given
        sma = new SecurityMemberAccess(false, true);
        sma.setExcludedClasses(new HashSet<Class<?>>(Collections.singletonList(Class.class)));

        // when
        method = StaticTester.class.getField("MIN_VALUE");
        actual = sma.isAccessible(context, null, method, null);

        // then
        assertTrue("Access to public final static field is blocked?", actual);

        // package static test
        // given
        sma = new SecurityMemberAccess(false, true);
        sma.setExcludedClasses(new HashSet<Class<?>>(Collections.singletonList(Class.class)));

        // when
        method = StaticTester.getFieldByName("PACKAGE_STRING");
        actual = sma.isAccessible(context, null, method, null);

        // then
        assertFalse("Access to package static field is allowed?", actual);

        // package final static test
        // given
        sma = new SecurityMemberAccess(false, true);
        sma.setExcludedClasses(new HashSet<Class<?>>(Collections.singletonList(Class.class)));

        // when
        method = StaticTester.getFieldByName("FINAL_PACKAGE_STRING");
        actual = sma.isAccessible(context, null, method, null);

        // then
        assertFalse("Access to package final static field is allowed?", actual);

        // protected static test
        // given
        sma = new SecurityMemberAccess(false, true);
        sma.setExcludedClasses(new HashSet<Class<?>>(Collections.singletonList(Class.class)));

        // when
        method = StaticTester.getFieldByName("PROTECTED_STRING");
        actual = sma.isAccessible(context, null, method, null);

        // then
        assertFalse("Access to protected static field is allowed?", actual);

        // protected final static test
        // given
        sma = new SecurityMemberAccess(false, true);
        sma.setExcludedClasses(new HashSet<Class<?>>(Collections.singletonList(Class.class)));

        // when
        method = StaticTester.getFieldByName("FINAL_PROTECTED_STRING");
        actual = sma.isAccessible(context, null, method, null);

        // then
        assertFalse("Access to protected final static field is allowed?", actual);

        // private static test
        // given
        sma = new SecurityMemberAccess(false, true);
        sma.setExcludedClasses(new HashSet<Class<?>>(Collections.singletonList(Class.class)));

        // when
        method = StaticTester.getFieldByName("PRIVATE_STRING");
        actual = sma.isAccessible(context, null, method, null);

        // then
        assertFalse("Access to private static field is allowed?", actual);

        // private final static test
        // given
        sma = new SecurityMemberAccess(false, true);
        sma.setExcludedClasses(new HashSet<Class<?>>(Collections.singletonList(Class.class)));

        // when
        method = StaticTester.getFieldByName("FINAL_PRIVATE_STRING");
        actual = sma.isAccessible(context, null, method, null);

        // then
        assertFalse("Access to private final static field is allowed?", actual);
    }

    public void testBlockedStaticFieldWhenClassIsExcluded() throws Exception {
        // given
        SecurityMemberAccess sma = new SecurityMemberAccess(true, true);
        sma.setExcludedClasses(new HashSet<>(Arrays.asList(Class.class, StaticTester.class)));

        // when
        Member method = StaticTester.class.getField("MAX_VALUE");
        boolean actual = sma.isAccessible(context, null, method, null);

        // then
        assertFalse("Access to static field isn't blocked!", actual);
    }

    public void testBlockStaticAccess() throws Exception {
        // given
        SecurityMemberAccess sma = new SecurityMemberAccess(false, true);
        sma.setExcludedClasses(new HashSet<Class<?>>(Collections.singletonList(Class.class)));

        // when
        Member method = StaticTester.class.getMethod("sayHello");
        boolean actual = sma.isAccessible(context, Class.class, method, null);

        // then
        assertFalse("Access to static isn't blocked!", actual);
    }

    public void testBlockStaticAccessIfClassIsExcluded() throws Exception {
        // given
        SecurityMemberAccess sma = new SecurityMemberAccess(false, true);
        sma.setExcludedClasses(new HashSet<Class<?>>(Collections.singletonList(Class.class)));

        // when
        Member method = Class.class.getMethod("getClassLoader");
        boolean actual = sma.isAccessible(context, Class.class, method, null);

        // then
        assertFalse("Access to static method of excluded class isn't blocked!", actual);
    }

    public void testAllowStaticAccessIfClassIsNotExcluded() throws Exception {
        // given
        SecurityMemberAccess sma = new SecurityMemberAccess(true, true);
        sma.setExcludedClasses(new HashSet<Class<?>>(Collections.singletonList(ClassLoader.class)));

        // when
        Member method = Class.class.getMethod("getClassLoader");
        boolean actual = sma.isAccessible(context, Class.class, method, null);

        // then
        assertTrue("Invalid test! Access to static method of excluded class is blocked!", actual);
    }

    public void testAccessPrimitiveInt() throws Exception {
        // given
        SecurityMemberAccess sma = new SecurityMemberAccess(false, true);
        sma.setExcludedPackageNames(TextParseUtil.commaDelimitedStringToSet("java.lang.,ognl,javax"));

        String propertyName = "intField";
        Member member = FooBar.class.getMethod("get" + propertyName.substring(0, 1).toUpperCase() + propertyName.substring(1));

        // when
        boolean accessible = sma.isAccessible(context, target, member, propertyName);

        // then
        assertTrue(accessible);
    }

    public void testAccessPrimitiveDoubleWithNames() throws Exception {
        // given
        SecurityMemberAccess sma = new SecurityMemberAccess(false, true);
        sma.setExcludedPackageNames(TextParseUtil.commaDelimitedStringToSet("ognl.,javax."));


        Set<Class<?>> excluded = new HashSet<>();
        excluded.add(Object.class);
        excluded.add(Runtime.class);
        excluded.add(System.class);
        excluded.add(Class.class);
        excluded.add(ClassLoader.class);
        sma.setExcludedClasses(excluded);

        String propertyName = "doubleValue";
        Member member = Double.class.getMethod(propertyName);

        // when
        boolean accessible = sma.isAccessible(context, target, member, propertyName);

        // then
        assertTrue(accessible);

        // given
        propertyName = "exit";
        member = System.class.getMethod(propertyName, int.class);

        // when
        accessible = sma.isAccessible(context, target, member, propertyName);

        // then
        assertFalse(accessible);

        // given
        propertyName = "intField";
        member = FooBar.class.getMethod("get" + propertyName.substring(0, 1).toUpperCase() + propertyName.substring(1));

        // when
        accessible = sma.isAccessible(context, target, member, propertyName);
        // then
        assertTrue(accessible);

        // given
        propertyName = "doubleField";
        member = FooBar.class.getMethod("get" + propertyName.substring(0, 1).toUpperCase() + propertyName.substring(1));

        // when
        accessible = sma.isAccessible(context, target, member, propertyName);
        // then
        assertTrue(accessible);
    }

    public void testAccessPrimitiveDoubleWithPackageRegExs() throws Exception {
        // given
        SecurityMemberAccess sma = new SecurityMemberAccess(false, true);
        Set<Pattern> patterns = new HashSet<>();
        patterns.add(Pattern.compile("^java\\.lang\\..*"));
        sma.setExcludedPackageNamePatterns(patterns);

        String propertyName = "doubleValue";
        Member member = Double.class.getMethod(propertyName);

        // when
        boolean accessible = sma.isAccessible(context, target, member, propertyName);

        // then
        assertTrue(accessible);
    }

    public void testAccessMemberAccessIsAccessible() throws Exception {
        // given
        SecurityMemberAccess sma = new SecurityMemberAccess(false, true);
        Set<Class<?>> excluded = new HashSet<>();
        excluded.add(ognl.MemberAccess.class);
        sma.setExcludedClasses(excluded);

        String propertyName = "excludedClasses";
        String setter = "setExcludedClasses";
        Member member = SecurityMemberAccess.class.getMethod(setter, Set.class);

        // when
        boolean accessible = sma.isAccessible(context, target, member, propertyName);

        // then
        assertTrue(accessible);
    }

    public void testAccessMemberAccessIsBlocked() throws Exception {
        // given
        SecurityMemberAccess sma = new SecurityMemberAccess(false, true);
        Set<Class<?>> excluded = new HashSet<>();
        excluded.add(SecurityMemberAccess.class);
        sma.setExcludedClasses(excluded);

        String propertyName = "excludedClasses";
        String setter = "setExcludedClasses";
        Member member = SecurityMemberAccess.class.getMethod(setter, Set.class);

        // when
        boolean accessible = sma.isAccessible(context, target, member, propertyName);

        // then
        assertFalse(accessible);
    }

    public void testPackageNameExclusionAsCommaDelimited() {
        // given
        SecurityMemberAccess sma = new SecurityMemberAccess(false, true);


        sma.setExcludedPackageNames(TextParseUtil.commaDelimitedStringToSet("java.lang."));

        // when
        boolean actual = sma.isPackageExcluded(String.class.getPackage(), null);
        actual &= sma.isPackageExcluded(null, String.class.getPackage());

        // then
        assertTrue("package java.lang. is accessible!", actual);
    }

}

class FooBar implements FooBarInterface {

    private String stringField;

    private int intField;

    private Double doubleField;

    public String getStringField() {
        return stringField;
    }

    public void setStringField(String stringField) {
        this.stringField = stringField;
    }

    public String fooLogic() {
        return "fooLogic";
    }

    public String barLogic() {
        return "barLogic";
    }

    @Override
    public int hashCode() {
        return 1;
    }

    public int getIntField() {
        return intField;
    }

    public void setIntField(int intField) {
        this.intField = intField;
    }

    public Double getDoubleField() {
        return doubleField;
    }

    public void setDoubleField(Double doubleField) {
        this.doubleField = doubleField;
    }
}

interface FooInterface {

    String fooLogic();

}

interface BarInterface {

    String barLogic();

}

interface FooBarInterface extends FooInterface, BarInterface {

}

enum MyValues {
    ONE, TWO, THREE
}

class StaticTester {

    public static int MAX_VALUE = 0;
    public static final int MIN_VALUE = 0;
    static String PACKAGE_STRING = "package_string";
    static final String FINAL_PACKAGE_STRING = "final_package_string";
    static String PROTECTED_STRING = "protected_string";
    static final String FINAL_PROTECTED_STRING = "final_protected_string";
    static String PRIVATE_STRING = "private_string";
    static final String FINAL_PRIVATE_STRING = "final_private_string";

    public static String sayHello() {
        return "Hello";
    }

    protected static Field getFieldByName(String fieldName) throws NoSuchFieldException {
        if (fieldName != null && fieldName.length() > 0) {
            return StaticTester.class.getDeclaredField(fieldName);
        } else {
            throw new NoSuchFieldException("field: " + fieldName + " does not exist");
        }
    }
}
