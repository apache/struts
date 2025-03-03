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

import ognl.MemberAccess;
import org.apache.commons.lang3.reflect.FieldUtils;
import org.apache.struts2.TestBean;
import org.apache.struts2.config.ConfigurationException;
import org.apache.struts2.test.TestBean2;
import org.apache.struts2.util.Foo;
import org.hibernate.proxy.HibernateProxy;
import org.hibernate.proxy.LazyInitializer;
import org.junit.Before;
import org.junit.Test;

import java.lang.reflect.Field;
import java.lang.reflect.InvocationHandler;
import java.lang.reflect.Member;
import java.lang.reflect.Method;
import java.lang.reflect.Proxy;
import java.util.Arrays;
import java.util.Collection;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Set;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertThrows;
import static org.junit.Assert.assertTrue;
import static org.junit.Assert.fail;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

public class SecurityMemberAccessTest {

    private Map context;
    private FooBar target;
    protected SecurityMemberAccess sma;
    protected ProviderAllowlist mockedProviderAllowlist;
    protected ThreadAllowlist mockedThreadAllowlist;

    @Before
    public void setUp() throws Exception {
        context = new HashMap<>();
        target = new FooBar();
        mockedProviderAllowlist = mock(ProviderAllowlist.class);
        mockedThreadAllowlist = mock(ThreadAllowlist.class);
        assignNewSma(true);
    }

    protected void assignNewSma(boolean allowStaticFieldAccess) {
        when(mockedProviderAllowlist.getProviderAllowlist()).thenReturn(new HashSet<>());
        when(mockedThreadAllowlist.getAllowlist()).thenReturn(new HashSet<>());
        assignNewSmaHelper();
        sma.useAllowStaticFieldAccess(String.valueOf(allowStaticFieldAccess));
    }

    protected void assignNewSmaHelper() {
        sma = new SecurityMemberAccess(mockedProviderAllowlist, mockedThreadAllowlist);
    }

    private <T> T reflectField(String fieldName) throws IllegalAccessException {
        return reflectField(sma, fieldName);
    }

    public static <T> T reflectField(Object instance, String fieldName) throws IllegalAccessException {
        return (T) FieldUtils.readField(instance, fieldName, true);
    }

    @Test
    public void defaultExclusionList() throws Exception {
        Set<String> excludedClasses = reflectField("excludedClasses");
        assertThat(excludedClasses).containsExactly(Object.class.getName());

        assignNewSma(false);
        excludedClasses = reflectField("excludedClasses");
        assertThat(excludedClasses).containsExactlyInAnyOrder(Object.class.getName(), Class.class.getName());
    }

    @Test
    public void configurationCollectionsImmutable() throws Exception {
        List<String> fields = Arrays.asList(
                "excludedClasses",
                "excludedPackageNames",
                "excludedPackageNamePatterns",
                "excludedPackageExemptClasses",
                "allowlistClasses",
                "allowlistPackageNames",
                "excludeProperties",
                "acceptProperties");
        for (String field : fields) {
            Collection<String> fieldVal = reflectField(field);
            assertThrows(UnsupportedOperationException.class, () -> fieldVal.add("foo"));
            if (!fieldVal.isEmpty()) {
                String firstVal = fieldVal.iterator().next();
                assertThrows(UnsupportedOperationException.class, () -> fieldVal.remove(firstVal));
                assertThrows(UnsupportedOperationException.class, fieldVal::clear);
            }
        }
    }

    @Test
    public void exclusionListsAreAdditive_classes() throws Exception {
        Collection<String> fieldVal = reflectField("excludedClasses");
        Set<String> existing = new HashSet<>(fieldVal);

        Collection<String> newExcludedClasses = Arrays.asList(FooBar.class.getName(), String.class.getName());
        sma.useExcludedClasses(String.join(",", newExcludedClasses));
        existing.addAll(newExcludedClasses);

        fieldVal = reflectField("excludedClasses");
        assertThat(fieldVal).containsExactlyInAnyOrderElementsOf(existing);
    }

    @Test
    public void exclusionListsAreAdditive_packages() throws Exception {
        sma.useExcludedPackageNames(Foo.class.getPackage().getName());
        Collection<String> fieldVal = reflectField("excludedPackageNames");
        Set<String> existing = new HashSet<>(fieldVal);

        Collection<String> newExcludedPackages = Arrays.asList(FooBar.class.getPackage().getName(), String.class.getPackage().getName());
        sma.useExcludedPackageNames(String.join(",", newExcludedPackages));
        existing.addAll(newExcludedPackages);

        fieldVal = reflectField("excludedPackageNames");
        assertThat(fieldVal).containsExactlyInAnyOrderElementsOf(existing);
    }

    @Test
    public void useExcludedPackageNames() {
        assertThrows(ConfigurationException.class, () -> sma.useExcludedPackageNames("java.lang\njava.awt"));
        assertThrows(ConfigurationException.class, () -> sma.useExcludedPackageNames("java.lang\tjava.awt"));
        ConfigurationException e = assertThrows(ConfigurationException.class, () -> sma.useExcludedPackageNames("java.lang java.awt"));
        assertTrue(e.getMessage().contains("erroneous whitespace characters"));
    }

    @Test
    public void useExcludedPackagePatterns() {
        ConfigurationException e = assertThrows(ConfigurationException.class, () -> sma.useExcludedPackageNamePatterns("["));
        assertTrue(e.getMessage().contains("invalid regex"));
    }

    @Test
    public void testWithoutClassExclusion() throws Exception {
        // given
        String propertyName = "stringField";
        Member member = FooBar.class.getMethod(formGetterName(propertyName));

        // when
        boolean accessible = sma.isAccessible(context, target, member, propertyName);

        // then
        assertTrue(accessible);
    }

    @Test
    public void testClassExclusion() throws Exception {
        // given
        String propertyName = "stringField";
        Member member = FooBar.class.getDeclaredMethod(formGetterName(propertyName));

        sma.useExcludedClasses(FooBar.class.getName());

        // when
        boolean accessible = sma.isAccessible(context, target, member, propertyName);

        // then
        assertFalse(accessible);
    }

    @Test
    public void testObjectClassExclusion() throws Exception {
        // given
        String propertyName = "toString";
        Member member = FooBar.class.getMethod(propertyName);

        // when
        boolean accessible = sma.isAccessible(context, target, member, propertyName);

        // then
        assertFalse("toString() from Object is accessible!!!", accessible);
    }

    @Test
    public void testObjectOverwrittenMethodsExclusion() throws Exception {
        // given
        String propertyName = "hashCode";
        Member member = FooBar.class.getMethod(propertyName);

        // when
        boolean accessible = sma.isAccessible(context, target, member, propertyName);

        // then
        assertTrue("hashCode() from FooBar isn't accessible!!!", accessible);
    }

    @Test
    public void testInterfaceInheritanceExclusion() throws Exception {
        // given
        String propertyName = "barLogic";
        Member member = BarInterface.class.getMethod(propertyName);

        sma.useExcludedClasses(BarInterface.class.getName());

        // when
        boolean accessible = sma.isAccessible(context, target, member, propertyName);

        // then
        assertFalse("barLogic() from BarInterface is accessible!!!", accessible);
    }

    @Test
    public void testMiddleOfInheritanceExclusion1() throws Exception {
        // given
        String propertyName = "fooLogic";
        Member member = FooBar.class.getMethod(propertyName);

        sma.useExcludedClasses(BarInterface.class.getName());

        // when
        boolean accessible = sma.isAccessible(context, target, member, propertyName);

        // then
        assertTrue("fooLogic() from FooInterface isn't accessible!!!", accessible);
    }

    @Test
    public void testMiddleOfInheritanceExclusion2() throws Exception {
        // given
        String propertyName = "barLogic";
        Member member = BarInterface.class.getMethod(propertyName);

        sma.useExcludedClasses(BarInterface.class.getName());

        // when
        boolean accessible = sma.isAccessible(context, target, member, propertyName);

        // then
        assertFalse("barLogic() from BarInterface is accessible!!!", accessible);
    }

    @Test
    public void testMiddleOfInheritanceExclusion3() throws Exception {
        // given
        String propertyName = "barLogic";
        Member member = BarInterface.class.getMethod(propertyName);

        sma.useExcludedClasses(FooInterface.class.getName());

        // when
        boolean accessible = sma.isAccessible(context, target, member, propertyName);

        // then
        assertTrue("barLogic() from BarInterface isn't accessible!!!", accessible);
    }

    @Test
    public void testPackageExclusion() throws Exception {
        // given
        sma.useExcludedPackageNamePatterns("^" + FooBar.class.getPackage().getName().replaceAll("\\.", "\\\\.") + ".*");

        String propertyName = "stringField";
        Member member = FooBar.class.getMethod(formGetterName(propertyName));

        // when
        boolean actual = sma.isAccessible(context, target, member, propertyName);

        // then
        assertFalse("stringField is accessible!", actual);
    }

    @Test
    public void testPackageExclusionExemption() throws Exception {
        // given
        sma.useExcludedPackageNamePatterns("^" + FooBar.class.getPackage().getName().replaceAll("\\.", "\\\\.") + ".*");

        sma.useExcludedPackageExemptClasses(FooBar.class.getName());

        String propertyName = "stringField";
        Member member = FooBar.class.getMethod(formGetterName(propertyName));

        // when
        boolean actual = sma.isAccessible(context, target, member, propertyName);

        // then
        assertTrue("stringField isn't accessible!", actual);
    }

    @Test
    public void testPackageNameExclusion() throws Exception {
        // given
        sma.useExcludedPackageNames(FooBar.class.getPackage().getName());

        String propertyName = "stringField";
        Member member = FooBar.class.getMethod(formGetterName(propertyName));

        // when
        boolean actual = sma.isAccessible(context, target, member, propertyName);

        // then
        assertFalse("stringField is accessible!", actual);
    }

    @Test
    public void testPackageNameExclusionExemption() throws Exception {
        // given
        sma.useExcludedPackageNames(FooBar.class.getPackage().getName());

        sma.useExcludedPackageExemptClasses(FooBar.class.getName());

        String propertyName = "stringField";
        Member member = FooBar.class.getMethod(formGetterName(propertyName));

        // when
        boolean actual = sma.isAccessible(context, target, member, propertyName);

        // then
        assertTrue("stringField isn't accessible!", actual);
    }

    @Test
    public void testPackageNameExclusionExemption2() throws Exception {
        // given
        sma.useExcludedPackageNames(FooBar.class.getPackage().getName());

        // Exemption must exist for both classes (target and member) if they both match a banned package
        sma.useExcludedPackageExemptClasses(BarInterface.class.getName());

        String propertyName = "barLogic";
        Member member = BarInterface.class.getMethod(propertyName);

        // when
        boolean actual = sma.isAccessible(context, target, member, propertyName);

        // then
        assertFalse("barLogic is accessible!", actual);
    }

    @Test
    public void testPackageNameExclusionExemption3() throws Exception {
        // given
        sma.useExcludedPackageNames(FooBar.class.getPackage().getName());

        // Exemption must exist for both classes (target and member) if they both match a banned package
        sma.useExcludedPackageExemptClasses(String.join(",", BarInterface.class.getName(), FooBar.class.getName()));

        String propertyName = "barLogic";
        Member member = BarInterface.class.getMethod(propertyName);

        // when
        boolean actual = sma.isAccessible(context, target, member, propertyName);

        // then
        assertTrue("barLogic isn't accessible!", actual);
    }

    @Test
    public void testDefaultPackageExclusion() throws Exception {
        // given
        sma.useExcludedPackageNamePatterns("^" + FooBar.class.getPackage().getName().replaceAll("\\.", "\\\\.") + ".*");

        Class<?> clazz = Class.forName("PackagelessAction");

        // when
        boolean actual = sma.isPackageExcluded(clazz);

        // then
        assertFalse("default package is excluded!", actual);
    }

    @Test
    public void testDefaultPackageExclusionSetting() throws Exception {
        sma.useDisallowDefaultPackageAccess(Boolean.TRUE.toString());

        Class<?> clazz = Class.forName("PackagelessAction");
        boolean actual = sma.isAccessible(null, clazz.getConstructor().newInstance(), clazz.getMethod("execute"), null);

        assertFalse("default package isn't excluded!", actual);
    }

    @Test
    public void testDefaultPackageExclusion2() throws Exception {
        // given
        sma.useExcludedPackageNamePatterns("^$");

        Class<?> clazz = Class.forName("PackagelessAction");

        // when
        boolean actual = sma.isPackageExcluded(clazz);

        // then
        assertTrue("default package isn't excluded!", actual);
    }

    @Test
    public void testAccessEnum() throws Exception {
        // when
        Member values = MyValues.class.getMethod("values");
        boolean actual = sma.isAccessible(context, MyValues.class, values, null);

        // then
        assertFalse("Access to enums is allowed!", actual);
    }

    @Test
    public void testAccessEnum_alternateValues() throws Exception {
        // when
        Member alternateValues = MyValues.class.getMethod("values", String.class);
        boolean actual = sma.isAccessible(context, MyValues.class, alternateValues, null);

        // then
        assertFalse("Access to unrelated #values method not blocked!", actual);
    }

    @Test
    public void testAccessStaticMethod() throws Exception {
        // given
        sma.useExcludedClasses(Class.class.getName());

        // when
        Member method = StaticTester.class.getMethod("sayHello");
        boolean actual = sma.isAccessible(context, StaticTester.class, method, null);

        // then
        assertFalse("Access to static method is not blocked!", actual);
    }

    @Test
    public void testAccessStaticField() throws Exception {
        // given
        sma.useExcludedClasses(Class.class.getName());

        // when
        Member method = StaticTester.class.getField("MAX_VALUE");
        boolean actual = sma.isAccessible(context, null, method, null);

        // then
        assertTrue("Access to static field is blocked!", actual);
    }

    @Test
    public void testBlockedStaticFieldWhenFlagIsTrue() throws Exception {
        // given
        assignNewSma(true);
        sma.useExcludedClasses(Class.class.getName());

        // when
        Member method = StaticTester.class.getField("MAX_VALUE");
        boolean actual = sma.isAccessible(context, null, method, null);

        // then
        assertTrue("Access to public static field is blocked?", actual);

        // public static final test
        // given
        assignNewSma(true);
        sma.useExcludedClasses(Class.class.getName());

        // when
        method = StaticTester.class.getField("MIN_VALUE");
        actual = sma.isAccessible(context, null, method, null);

        // then
        assertTrue("Access to public final static field is blocked?", actual);

        // package static test
        // given
        assignNewSma(true);
        sma.useExcludedClasses(Class.class.getName());

        // when
        method = StaticTester.getFieldByName("PACKAGE_STRING");
        actual = sma.isAccessible(context, null, method, null);

        // then
        assertFalse("Access to package static field is allowed?", actual);

        // package final static test
        // given
        assignNewSma(true);
        sma.useExcludedClasses(Class.class.getName());

        // when
        method = StaticTester.getFieldByName("FINAL_PACKAGE_STRING");
        actual = sma.isAccessible(context, null, method, null);

        // then
        assertFalse("Access to package final static field is allowed?", actual);

        // protected static test
        // given
        assignNewSma(true);
        sma.useExcludedClasses(Class.class.getName());

        // when
        method = StaticTester.getFieldByName("PROTECTED_STRING");
        actual = sma.isAccessible(context, null, method, null);

        // then
        assertFalse("Access to protected static field is allowed?", actual);

        // protected final static test
        // given
        assignNewSma(true);
        sma.useExcludedClasses(Class.class.getName());

        // when
        method = StaticTester.getFieldByName("FINAL_PROTECTED_STRING");
        actual = sma.isAccessible(context, null, method, null);

        // then
        assertFalse("Access to protected final static field is allowed?", actual);

        // private static test
        // given
        assignNewSma(true);
        sma.useExcludedClasses(Class.class.getName());

        // when
        method = StaticTester.getFieldByName("PRIVATE_STRING");
        actual = sma.isAccessible(context, null, method, null);

        // then
        assertFalse("Access to private static field is allowed?", actual);

        // private final static test
        // given
        assignNewSma(true);
        sma.useExcludedClasses(Class.class.getName());

        // when
        method = StaticTester.getFieldByName("FINAL_PRIVATE_STRING");
        actual = sma.isAccessible(context, null, method, null);

        // then
        assertFalse("Access to private final static field is allowed?", actual);
    }

    @Test
    public void testBlockedStaticFieldWhenFlagIsFalse() throws Exception {
        // given
        assignNewSma(false);

        // when
        Member method = StaticTester.class.getField("MAX_VALUE");
        boolean actual = sma.isAccessible(context, null, method, null);

        // then
        assertFalse("Access to public static field is allowed when flag false?", actual);

        // public static final test
        // given
        assignNewSma(false);

        // when
        method = StaticTester.class.getField("MIN_VALUE");
        actual = sma.isAccessible(context, null, method, null);

        // then
        assertFalse("Access to public final static field is allowed when flag is false?", actual);

        // package static test
        // given
        assignNewSma(false);

        // when
        method = StaticTester.getFieldByName("PACKAGE_STRING");
        actual = sma.isAccessible(context, null, method, null);

        // then
        assertFalse("Access to package static field is allowed?", actual);

        // package final static test
        // given
        assignNewSma(false);

        // when
        method = StaticTester.getFieldByName("FINAL_PACKAGE_STRING");
        actual = sma.isAccessible(context, null, method, null);

        // then
        assertFalse("Access to package final static field is allowed?", actual);

        // protected static test
        // given
        assignNewSma(false);

        // when
        method = StaticTester.getFieldByName("PROTECTED_STRING");
        actual = sma.isAccessible(context, null, method, null);

        // then
        assertFalse("Access to protected static field is allowed?", actual);

        // protected final static test
        // given
        assignNewSma(false);

        // when
        method = StaticTester.getFieldByName("FINAL_PROTECTED_STRING");
        actual = sma.isAccessible(context, null, method, null);

        // then
        assertFalse("Access to protected final static field is allowed?", actual);

        // private static test
        // given
        assignNewSma(false);

        // when
        method = StaticTester.getFieldByName("PRIVATE_STRING");
        actual = sma.isAccessible(context, null, method, null);

        // then
        assertFalse("Access to private static field is allowed?", actual);

        // private final static test
        // given
        assignNewSma(false);

        // when
        method = StaticTester.getFieldByName("FINAL_PRIVATE_STRING");
        actual = sma.isAccessible(context, null, method, null);

        // then
        assertFalse("Access to private final static field is allowed?", actual);
    }

    @Test
    public void testBlockedStaticFieldWhenClassIsExcluded() throws Exception {
        // given
        sma.useExcludedClasses(String.join(",", Class.class.getName(), StaticTester.class.getName()));

        // when
        Member method = StaticTester.class.getField("MAX_VALUE");
        boolean actual = sma.isAccessible(context, null, method, null);

        // then
        assertFalse("Access to static field isn't blocked!", actual);
    }

    @Test
    public void testBlockStaticMethodAccess() throws Exception {
        // given
        sma.useExcludedClasses(Class.class.getName());

        // when
        Member method = StaticTester.class.getMethod("sayHello");
        boolean actual = sma.isAccessible(context, StaticTester.class, method, null);

        // then
        assertFalse("Access to static isn't blocked!", actual);
    }

    @Test
    public void testBlockAccessIfClassIsExcluded() throws Exception {
        // given
        sma.useExcludedClasses(Class.class.getName());

        // when
        Member method = Class.class.getMethod("getClassLoader");
        boolean actual = sma.isAccessible(context, Class.class, method, null);

        // then
        assertFalse("Access to method of excluded class isn't blocked!", actual);
    }

   @Test
    public void testBlockAccessIfClassIsExcluded_2() throws Exception {
        // given
        sma.useExcludedClasses(ClassLoader.class.getName());

        // when
        Member method = ClassLoader.class.getMethod("loadClass", String.class);
        ClassLoader classLoaderTarget = this.getClass().getClassLoader();
        boolean actual = sma.isAccessible(context, classLoaderTarget, method, null);

        // then
        assertFalse("Invalid test! Access to method of excluded class isn't blocked!", actual);
    }

    @Test
    public void testAllowAccessIfClassIsNotExcluded() throws Exception {
        // given
        sma.useExcludedClasses(ClassLoader.class.getName());

        // when
        Member method = Class.class.getMethod("getClassLoader");
        boolean actual = sma.isAccessible(context, Class.class, method, null);

        // then
        assertTrue("Invalid test! Access to method of non-excluded class is blocked!", actual);
    }

   @Test
    public void testIllegalArgumentExceptionExpectedForTargetMemberMismatch() throws Exception {
        // given
        sma.useExcludedClasses(Class.class.getName());

        // when
        Member method = ClassLoader.class.getMethod("loadClass", String.class);
        String mismatchTarget = "misMatchTargetObject";
        try {
            boolean actual = sma.isAccessible(context, mismatchTarget, method, null);

            // then
            assertFalse("Invalid test! Access to method of excluded class isn't blocked!", actual);
            fail("Mismatch between target and member did not cause IllegalArgumentException?");
        } catch (IllegalArgumentException iex) {
            // Expected result is this exception
        }
    }

    @Test
    public void testAccessPrimitiveInt() throws Exception {
        // given
        sma.useExcludedPackageNames("java.lang.,ognl,javax");

        String propertyName = "intField";
        Member member = FooBar.class.getMethod(formGetterName(propertyName));

        // when
        boolean accessible = sma.isAccessible(context, target, member, propertyName);

        // then
        assertTrue(accessible);
    }

    @Test
    public void testAccessPrimitiveDoubleWithNames() throws Exception {
        // given
        sma.useExcludedPackageNames("ognl.,javax.");


        Set<String> excluded = new HashSet<>();
        excluded.add(Object.class.getName());
        excluded.add(Runtime.class.getName());
        excluded.add(System.class.getName());
        excluded.add(Class.class.getName());
        excluded.add(ClassLoader.class.getName());
        sma.useExcludedClasses(String.join(",", excluded));

        String propertyName = "doubleValue";
        double myDouble = 1;
        Member member = Double.class.getMethod(propertyName);

        // when
        boolean accessible = sma.isAccessible(context, myDouble, member, propertyName);

        // then
        assertTrue(accessible);

        // given
        propertyName = "exit";
        member = System.class.getMethod(propertyName, int.class);

        // when
        accessible = sma.isAccessible(context, System.class, member, propertyName);

        // then
        assertFalse(accessible);

        // given
        propertyName = "intField";
        member = FooBar.class.getMethod(formGetterName(propertyName));

        // when
        accessible = sma.isAccessible(context, target, member, propertyName);
        // then
        assertTrue(accessible);

        // given
        propertyName = "doubleField";
        member = FooBar.class.getMethod(formGetterName(propertyName));

        // when
        accessible = sma.isAccessible(context, target, member, propertyName);
        // then
        assertTrue(accessible);
    }

    @Test
    public void testAccessPrimitiveDoubleWithPackageRegExs() throws Exception {
        // given
        sma.useExcludedPackageNamePatterns("^java\\.lang\\..*");

        String propertyName = "doubleValue";
        double myDouble = 1;
        Member member = Double.class.getMethod(propertyName);

        // when
        boolean accessible = sma.isAccessible(context, myDouble, member, propertyName);

        // then
        assertTrue(accessible);
    }

    @Test
    public void testAccessMemberAccessIsAccessible() throws Exception {
        // given
        sma.useExcludedClasses(MemberAccess.class.getName());

        String propertyName = "excludedClasses";
        String setter = "useExcludedClasses";
        Member member = SecurityMemberAccess.class.getMethod(setter, String.class);

        // when
        boolean accessible = sma.isAccessible(context, sma, member, propertyName);

        // then
        assertTrue(accessible);
    }

    @Test
    public void testAccessMemberAccessIsBlocked() throws Exception {
        // given
        sma.useExcludedClasses(SecurityMemberAccess.class.getName());

        String propertyName = "excludedClasses";
        String setter = "useExcludedClasses";
        Member member = SecurityMemberAccess.class.getMethod(setter, String.class);

        // when
        boolean accessible = sma.isAccessible(context, sma, member, propertyName);

        // then
        assertFalse(accessible);
    }

    @Test
    public void testPackageNameExclusionAsCommaDelimited() {
        // given
        sma.useExcludedPackageNames("java.lang");

        // when
        boolean actual = sma.isPackageExcluded(String.class);

        // then
        assertTrue("package java.lang. is accessible!", actual);
    }

    /**
     * Test that the allowlist is enforced correctly for classes.
     */
    @Test
    public void classInclusion() throws Exception {
        sma.useEnforceAllowlistEnabled(Boolean.TRUE.toString());

        TestBean2 bean = new TestBean2();
        Method method = TestBean2.class.getMethod("getData");

        assertFalse(sma.checkAllowlist(bean, method));

        sma.useAllowlistClasses(TestBean2.class.getName());

        assertTrue(sma.checkAllowlist(bean, method));
    }

    /**
     * Test that the allowlist is enforced correctly for packages.
     */
    @Test
    public void packageInclusion() throws Exception {
        sma.useEnforceAllowlistEnabled(Boolean.TRUE.toString());

        TestBean2 bean = new TestBean2();
        Method method = TestBean2.class.getMethod("getData");

        assertFalse(sma.checkAllowlist(bean, method));

        sma.useAllowlistPackageNames(TestBean2.class.getPackage().getName());

        assertTrue(sma.checkAllowlist(bean, method));
    }

    /**
     * Test that the allowlist doesn't allow inherited methods unless the declaring class is also allowlisted.
     */
    @Test
    public void classInclusion_subclass() throws Exception {
        sma.useEnforceAllowlistEnabled(Boolean.TRUE.toString());
        sma.useAllowlistClasses(TestBean2.class.getName());

        TestBean2 bean = new TestBean2();
        Method method = TestBean2.class.getMethod("getName");

        assertFalse(sma.checkAllowlist(bean, method));
    }

    /**
     * Test that the allowlist allows inherited methods when both the target and declaring class are allowlisted.
     */
    @Test
    public void classInclusion_subclass_both() throws Exception {
        sma.useEnforceAllowlistEnabled(Boolean.TRUE.toString());
        sma.useAllowlistClasses(String.join(",", TestBean.class.getName(), TestBean2.class.getName()));

        TestBean2 bean = new TestBean2();
        Method method = TestBean2.class.getMethod("getName");

        assertTrue(sma.checkAllowlist(bean, method));
    }

    /**
     * Test that the allowlist doesn't allow inherited methods unless the package of the declaring class is also
     * allowlisted.
     */
    @Test
    public void packageInclusion_subclass() throws Exception {
        sma.useEnforceAllowlistEnabled(Boolean.TRUE.toString());
        sma.useAllowlistPackageNames(TestBean2.class.getPackage().getName());

        TestBean2 bean = new TestBean2();
        Method method = TestBean2.class.getMethod("getName");

        assertFalse(sma.checkAllowlist(bean, method));
    }

    /**
     * When the allowlist is enabled and proxy object access is disallowed, Hibernate proxies should not be allowed.
     */
    @Test
    public void classInclusion_hibernateProxy_disallowProxyObjectAccess() throws Exception {
        FooBarInterface proxyObject = mockHibernateProxy(new FooBar(), FooBarInterface.class);
        Method proxyMethod = proxyObject.getClass().getMethod("fooLogic");

        sma.useEnforceAllowlistEnabled(Boolean.TRUE.toString());
        sma.useDisallowProxyObjectAccess(Boolean.TRUE.toString());
        sma.useAllowlistClasses(FooBar.class.getName());

        assertFalse(sma.checkAllowlist(proxyObject, proxyMethod));
    }

    /**
     * When the allowlist is enabled and proxy object access is allowed, Hibernate proxies should be allowlisted based
     * on their underlying target object. Class allowlisting should work as expected.
     */
    @Test
    public void classInclusion_hibernateProxy_allowProxyObjectAccess() throws Exception {
        FooBarInterface proxyObject = mockHibernateProxy(new FooBar(), FooBarInterface.class);
        Method proxyMethod = proxyObject.getClass().getMethod("fooLogic");

        sma.useEnforceAllowlistEnabled(Boolean.TRUE.toString());
        sma.useDisallowProxyObjectAccess(Boolean.FALSE.toString());
        sma.useAllowlistClasses(FooBar.class.getName());

        assertTrue(sma.checkAllowlist(proxyObject, proxyMethod));
    }

    @Test
    public void packageInclusion_subclass_both() throws Exception {
        sma.useEnforceAllowlistEnabled(Boolean.TRUE.toString());
        sma.useAllowlistPackageNames(String.join(",",
                TestBean.class.getPackage().getName(),
                TestBean2.class.getPackage().getName()));

        TestBean2 bean = new TestBean2();
        Method method = TestBean2.class.getMethod("getName");

        assertTrue(sma.checkAllowlist(bean, method));
    }

    private static String formGetterName(String propertyName) {
        return "get" + propertyName.substring(0, 1).toUpperCase() + propertyName.substring(1);
    }

    @SuppressWarnings("unchecked")
    private static <T> T mockHibernateProxy(T originalObject, Class<T> proxyInterface) {
        return (T) Proxy.newProxyInstance(
                proxyInterface.getClassLoader(),
                new Class<?>[]{proxyInterface, HibernateProxy.class},
                new DummyHibernateProxyHandler(originalObject)
        );
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

    @Override
    public String fooLogic() {
        return "fooLogic";
    }

    @Override
    public String barLogic() {
        return "barLogic";
    }

    @Override
    public int hashCode() {
        return 1;
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj) {
            return true;
        }
        if (obj == null) {
            return false;
        }
        if (getClass() != obj.getClass()) {
            return false;
        }
        final FooBar other = (FooBar) obj;
        if (this.intField != other.intField) {
            return false;
        }
        if (!Objects.equals(this.stringField, other.stringField)) {
            return false;
        }
        return Objects.equals(this.doubleField, other.doubleField);
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
    ONE, TWO, THREE;

    public static MyValues[] values(String notUsed) {
        return new MyValues[] {ONE, TWO, THREE};
    }
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
        if (fieldName != null && !fieldName.isEmpty()) {
            return StaticTester.class.getDeclaredField(fieldName);
        } else {
            throw new NoSuchFieldException("field: " + fieldName + " does not exist");
        }
    }
}

class DummyHibernateProxyHandler implements InvocationHandler {
    private final Object instance;

    public DummyHibernateProxyHandler(Object instance) {
        this.instance = instance;
    }

    @Override
    public Object invoke(Object proxy, Method method, Object[] args) throws Throwable {
        if (HibernateProxy.class.getMethod("getHibernateLazyInitializer").equals(method)) {
            LazyInitializer initializer = mock(LazyInitializer.class);
            when(initializer.getImplementation()).thenReturn(instance);
            return initializer;
        }
        return method.invoke(instance, args);
    }
}
