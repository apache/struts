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
package org.apache.struts2.util;

import org.apache.struts2.ognl.StrutsProxyCacheFactory;
import org.junit.Before;
import org.junit.Test;
import org.springframework.aop.MethodBeforeAdvice;
import org.springframework.aop.framework.Advised;
import org.springframework.aop.framework.ProxyFactory;

import java.lang.reflect.Constructor;
import java.lang.reflect.Field;
import java.lang.reflect.Member;
import java.lang.reflect.Method;

import static org.assertj.core.api.Assertions.assertThat;

/**
 * Tests for {@link StrutsProxyService}.
 */
public class StrutsProxyServiceTest {

    private StrutsProxyService proxyService;

    @Before
    public void setUp() {
        StrutsProxyCacheFactory<?, ?> factory = new StrutsProxyCacheFactory<>("1000", "basic");
        proxyService = new StrutsProxyService(factory);
    }

    @Test
    public void isProxyWithNull() {
        assertThat(proxyService.isProxy(null)).isFalse();
    }

    @Test
    public void isProxyWithRegularObject() {
        Object regularObject = new Object();
        assertThat(proxyService.isProxy(regularObject)).isFalse();
    }

    @Test
    public void isProxyWithString() {
        String str = "test";
        assertThat(proxyService.isProxy(str)).isFalse();
    }

    @Test
    public void isProxyWithSpringAopProxy() {
        TestService proxy = createSpringProxy(new TestServiceImpl());
        assertThat(proxyService.isProxy(proxy)).isTrue();
    }

    @Test
    public void isProxyWithSpringCglibProxy() {
        TestServiceImpl proxy = createSpringCglibProxy(new TestServiceImpl());
        assertThat(proxyService.isProxy(proxy)).isTrue();
    }

    @Test
    public void isProxyCachesResultByClass() {
        Object obj1 = new TestServiceImpl();
        Object obj2 = new TestServiceImpl();

        // First call should populate cache
        boolean result1 = proxyService.isProxy(obj1);
        // Second call with same class should use cached result
        boolean result2 = proxyService.isProxy(obj2);

        assertThat(result1).isEqualTo(result2);
        assertThat(result1).isFalse();
    }

    @Test
    public void ultimateTargetClassWithRegularObject() {
        Object regularObject = new Object();
        Class<?> targetClass = proxyService.ultimateTargetClass(regularObject);
        assertThat(targetClass).isEqualTo(Object.class);
    }

    @Test
    public void ultimateTargetClassWithString() {
        String str = "test";
        Class<?> targetClass = proxyService.ultimateTargetClass(str);
        assertThat(targetClass).isEqualTo(String.class);
    }

    @Test
    public void ultimateTargetClassWithSpringAopProxy() {
        TestService proxy = createSpringProxy(new TestServiceImpl());
        Class<?> targetClass = proxyService.ultimateTargetClass(proxy);
        assertThat(targetClass).isEqualTo(TestServiceImpl.class);
    }

    @Test
    public void ultimateTargetClassWithSpringCglibProxy() {
        TestServiceImpl proxy = createSpringCglibProxy(new TestServiceImpl());
        Class<?> targetClass = proxyService.ultimateTargetClass(proxy);
        assertThat(targetClass).isEqualTo(TestServiceImpl.class);
    }

    @Test
    public void ultimateTargetClassCachesResult() {
        TestService proxy = createSpringProxy(new TestServiceImpl());

        // First call should populate cache
        Class<?> result1 = proxyService.ultimateTargetClass(proxy);
        // Second call with same object should use cached result
        Class<?> result2 = proxyService.ultimateTargetClass(proxy);

        assertThat(result1).isEqualTo(result2);
        assertThat(result1).isEqualTo(TestServiceImpl.class);
    }

    @Test
    public void isHibernateProxyWithNull() {
        assertThat(proxyService.isHibernateProxy(null)).isFalse();
    }

    @Test
    public void isHibernateProxyWithRegularObject() {
        Object regularObject = new Object();
        assertThat(proxyService.isHibernateProxy(regularObject)).isFalse();
    }

    @Test
    public void isHibernateProxyWithSpringProxy() {
        TestService proxy = createSpringProxy(new TestServiceImpl());
        assertThat(proxyService.isHibernateProxy(proxy)).isFalse();
    }

    @Test
    public void isHibernateProxyMemberWithRegularMethod() throws NoSuchMethodException {
        Method method = Object.class.getMethod("toString");
        assertThat(proxyService.isHibernateProxyMember(method)).isFalse();
    }

    @Test
    public void isHibernateProxyMemberWithTestServiceMethod() throws NoSuchMethodException {
        Method method = TestService.class.getMethod("doSomething");
        assertThat(proxyService.isHibernateProxyMember(method)).isFalse();
    }

    @Test
    public void getHibernateProxyTargetWithRegularObject() {
        Object regularObject = new Object();
        Object result = proxyService.getHibernateProxyTarget(regularObject);
        assertThat(result).isSameAs(regularObject);
    }

    @Test
    public void getHibernateProxyTargetWithString() {
        String str = "test";
        Object result = proxyService.getHibernateProxyTarget(str);
        assertThat(result).isSameAs(str);
    }

    @Test
    public void isProxyMemberWithNonProxy() throws NoSuchMethodException {
        Object regularObject = new Object();
        Method method = Object.class.getMethod("toString");
        assertThat(proxyService.isProxyMember(method, regularObject)).isFalse();
    }

    @Test
    public void isProxyMemberWithSpringProxyAndAdvisedMember() throws NoSuchMethodException {
        TestService proxy = createSpringProxy(new TestServiceImpl());
        Method advisedMethod = Advised.class.getMethod("isExposeProxy");
        assertThat(proxyService.isProxyMember(advisedMethod, proxy)).isTrue();
    }

    @Test
    public void isProxyMemberWithSpringProxyAndNonProxyMember() throws NoSuchMethodException {
        TestService proxy = createSpringProxy(new TestServiceImpl());
        Method doSomethingMethod = proxy.getClass().getMethod("doSomething");
        assertThat(proxyService.isProxyMember(doSomethingMethod, proxy)).isFalse();
    }

    @Test
    public void isProxyMemberWithStaticMemberOnNonProxy() throws NoSuchMethodException {
        Object regularObject = new TestServiceImpl();
        Method staticMethod = TestServiceImpl.class.getMethod("staticMethod");
        // Static members are checked regardless of proxy status
        assertThat(proxyService.isProxyMember(staticMethod, regularObject)).isFalse();
    }

    @Test
    public void isProxyMemberWithNullObject() throws NoSuchMethodException {
        Method method = Object.class.getMethod("toString");
        assertThat(proxyService.isProxyMember(method, null)).isFalse();
    }

    @Test
    public void isProxyMemberCachesResult() throws NoSuchMethodException {
        TestService proxy = createSpringProxy(new TestServiceImpl());
        Method advisedMethod = Advised.class.getMethod("isExposeProxy");

        // First call should populate cache
        boolean result1 = proxyService.isProxyMember(advisedMethod, proxy);
        // Second call should use cached result
        boolean result2 = proxyService.isProxyMember(advisedMethod, proxy);

        assertThat(result1).isEqualTo(result2);
        assertThat(result1).isTrue();
    }

    @Test
    public void resolveTargetMemberReturnsMethodOnTargetClass() throws NoSuchMethodException {
        Method toStringMethod = Object.class.getMethod("toString");
        Member resolved = proxyService.resolveTargetMember(toStringMethod, String.class);

        assertThat(resolved).isNotNull();
        assertThat(resolved.getName()).isEqualTo("toString");
        assertThat(resolved.getDeclaringClass()).isEqualTo(String.class);
    }

    @Test
    public void resolveTargetMemberDeprecatedMethod() throws NoSuchMethodException {
        Method toStringMethod = Object.class.getMethod("toString");
        String target = "test";

        @SuppressWarnings("deprecation")
        Member resolved = proxyService.resolveTargetMember(toStringMethod, target);

        assertThat(resolved).isNotNull();
        assertThat(resolved.getName()).isEqualTo("toString");
    }

    @Test
    public void resolveTargetMemberWithPrivateMethod() throws NoSuchMethodException {
        Method privateMethod = TestServiceImpl.class.getDeclaredMethod("privateMethod");
        Member resolved = proxyService.resolveTargetMember(privateMethod, TestServiceImpl.class);

        assertThat(resolved).isNotNull();
        assertThat(resolved.getName()).isEqualTo("privateMethod");
    }

    @Test
    public void resolveTargetMemberWithMethodNotFoundReturnsNull() throws NoSuchMethodException {
        Method charAtMethod = String.class.getMethod("charAt", int.class);
        Member resolved = proxyService.resolveTargetMember(charAtMethod, Object.class);

        // Method doesn't exist on Object.class, should return null
        assertThat(resolved).isNull();
    }

    @Test
    public void resolveTargetMemberWithOverloadedMethod() throws NoSuchMethodException {
        Method valueOfInt = String.class.getMethod("valueOf", int.class);
        Member resolved = proxyService.resolveTargetMember(valueOfInt, String.class);

        assertThat(resolved).isNotNull();
        assertThat(resolved.getName()).isEqualTo("valueOf");
        assertThat(((Method) resolved).getParameterTypes()).containsExactly(int.class);
    }

    @Test
    public void resolveTargetMemberWithPublicField() throws NoSuchFieldException {
        Field publicField = TestBeanWithFields.class.getField("publicField");
        Member resolved = proxyService.resolveTargetMember(publicField, TestBeanWithFields.class);

        assertThat(resolved).isNotNull();
        assertThat(resolved.getName()).isEqualTo("publicField");
        assertThat(resolved).isInstanceOf(Field.class);
    }

    @Test
    public void resolveTargetMemberWithPrivateFieldReturnsNull() throws NoSuchFieldException {
        Field privateField = TestBeanWithFields.class.getDeclaredField("privateField");
        Member resolved = proxyService.resolveTargetMember(privateField, TestBeanWithFields.class);

        // Current implementation: non-public fields use forceAccess=false, so they are not found
        // This returns null because FieldUtils.getField with forceAccess=false only finds public fields
        assertThat(resolved).isNull();
    }

    @Test
    public void resolveTargetMemberWithProtectedFieldReturnsNull() throws NoSuchFieldException {
        Field protectedField = TestBeanWithFields.class.getDeclaredField("protectedField");
        Member resolved = proxyService.resolveTargetMember(protectedField, TestBeanWithFields.class);

        // Current implementation: non-public fields use forceAccess=false, so they are not found
        // This returns null because FieldUtils.getField with forceAccess=false only finds public fields
        assertThat(resolved).isNull();
    }

    @Test
    public void resolveTargetMemberWithFieldNotFoundReturnsNull() throws NoSuchFieldException {
        Field publicField = TestBeanWithFields.class.getField("publicField");
        Member resolved = proxyService.resolveTargetMember(publicField, Object.class);

        // Field doesn't exist on Object.class
        assertThat(resolved).isNull();
    }

    @Test
    public void resolveTargetMemberWithDefaultConstructor() throws NoSuchMethodException {
        Constructor<?> constructor = TestServiceImpl.class.getConstructor();
        Member resolved = proxyService.resolveTargetMember(constructor, TestServiceImpl.class);

        assertThat(resolved).isNotNull();
        assertThat(resolved).isInstanceOf(Constructor.class);
    }

    @Test
    public void resolveTargetMemberWithParameterizedConstructor() throws NoSuchMethodException {
        Constructor<?> constructor = TestBeanWithConstructor.class.getConstructor(String.class, int.class);
        Member resolved = proxyService.resolveTargetMember(constructor, TestBeanWithConstructor.class);

        assertThat(resolved).isNotNull();
        assertThat(resolved).isInstanceOf(Constructor.class);
        assertThat(((Constructor<?>) resolved).getParameterTypes()).containsExactly(String.class, int.class);
    }

    @Test
    public void resolveTargetMemberWithConstructorNotFoundReturnsNull() throws NoSuchMethodException {
        Constructor<?> constructor = TestBeanWithConstructor.class.getConstructor(String.class, int.class);
        Member resolved = proxyService.resolveTargetMember(constructor, TestServiceImpl.class);

        // Constructor with those params doesn't exist on TestServiceImpl, returns null
        assertThat(resolved).isNull();
    }

    @Test
    public void resolveTargetMemberWithPrivateConstructorReturnsOriginal() throws NoSuchMethodException {
        Constructor<?> privateConstructor = TestBeanWithPrivateConstructor.class.getDeclaredConstructor(String.class);
        Member resolved = proxyService.resolveTargetMember(privateConstructor, TestBeanWithPrivateConstructor.class);

        // Private constructor is not accessible, returns original
        assertThat(resolved).isSameAs(privateConstructor);
    }

    private TestService createSpringProxy(TestService target) {
        ProxyFactory proxyFactory = new ProxyFactory(target);
        proxyFactory.addAdvice((MethodBeforeAdvice) (method, args, t) -> {
            // No-op advice
        });
        return (TestService) proxyFactory.getProxy();
    }

    private TestServiceImpl createSpringCglibProxy(TestServiceImpl target) {
        ProxyFactory proxyFactory = new ProxyFactory(target);
        proxyFactory.setProxyTargetClass(true);
        proxyFactory.addAdvice((MethodBeforeAdvice) (method, args, t) -> {
            // No-op advice
        });
        return (TestServiceImpl) proxyFactory.getProxy();
    }

    public interface TestService {
        void doSomething();
    }

    public static class TestServiceImpl implements TestService {
        @Override
        public void doSomething() {
            // No-op
        }

        public static void staticMethod() {
            // Static method for testing
        }

        private void privateMethod() {
            // Private method for testing
        }
    }

    public static class TestBeanWithFields {
        public String publicField;
        protected String protectedField;
        private String privateField;
        String packagePrivateField;
    }

    public static class TestBeanWithConstructor {
        private final String name;
        private final int value;

        public TestBeanWithConstructor(String name, int value) {
            this.name = name;
            this.value = value;
        }
    }

    public static class TestBeanWithPrivateConstructor {
        private TestBeanWithPrivateConstructor(String value) {
            // Private constructor
        }

        public TestBeanWithPrivateConstructor() {
            // Public default constructor
        }
    }
}
