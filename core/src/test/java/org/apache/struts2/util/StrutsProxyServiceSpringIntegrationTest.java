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
import org.springframework.aop.SpringProxy;

import java.lang.reflect.Method;

import static org.assertj.core.api.Assertions.assertThat;

/**
 * Integration tests for {@link StrutsProxyService} with Spring AOP proxies.
 * These tests verify the proxy service correctly handles various Spring proxy scenarios.
 */
public class StrutsProxyServiceSpringIntegrationTest {

    private StrutsProxyService proxyService;

    @Before
    public void setUp() {
        StrutsProxyCacheFactory<?, ?> factory = new StrutsProxyCacheFactory<>("1000", "basic");
        proxyService = new StrutsProxyService(factory);
    }

    @Test
    public void testJdkDynamicProxyIsDetectedAsProxy() {
        SimpleService proxy = createJdkDynamicProxy(new SimpleServiceImpl());

        assertThat(proxyService.isProxy(proxy)).isTrue();
        assertThat(proxy).isInstanceOf(SpringProxy.class);
        assertThat(proxy).isInstanceOf(Advised.class);
    }

    @Test
    public void testJdkDynamicProxyUltimateTargetClass() {
        SimpleService proxy = createJdkDynamicProxy(new SimpleServiceImpl());

        Class<?> targetClass = proxyService.ultimateTargetClass(proxy);

        assertThat(targetClass).isEqualTo(SimpleServiceImpl.class);
    }

    @Test
    public void testJdkDynamicProxyMemberDetection() throws NoSuchMethodException {
        SimpleService proxy = createJdkDynamicProxy(new SimpleServiceImpl());

        // Advised interface method should be detected as proxy member
        Method isExposeProxy = proxy.getClass().getMethod("isExposeProxy");
        assertThat(proxyService.isProxyMember(isExposeProxy, proxy)).isTrue();

        // Business method should not be detected as proxy member
        Method getValue = proxy.getClass().getMethod("getValue");
        assertThat(proxyService.isProxyMember(getValue, proxy)).isFalse();
    }

    @Test
    public void testJdkDynamicProxyResolveTargetMember() throws NoSuchMethodException {
        SimpleService proxy = createJdkDynamicProxy(new SimpleServiceImpl());
        Method proxyMethod = proxy.getClass().getMethod("getValue");

        // Resolve the method to the target class
        Class<?> targetClass = proxyService.ultimateTargetClass(proxy);
        var resolved = proxyService.resolveTargetMember(proxyMethod, targetClass);

        assertThat(resolved).isNotNull();
        assertThat(resolved.getName()).isEqualTo("getValue");
        assertThat(resolved.getDeclaringClass()).isEqualTo(SimpleServiceImpl.class);
    }

    @Test
    public void testCglibProxyIsDetectedAsProxy() {
        SimpleServiceImpl proxy = createCglibProxy(new SimpleServiceImpl());

        assertThat(proxyService.isProxy(proxy)).isTrue();
    }

    @Test
    public void testCglibProxyUltimateTargetClass() {
        SimpleServiceImpl proxy = createCglibProxy(new SimpleServiceImpl());

        Class<?> targetClass = proxyService.ultimateTargetClass(proxy);

        assertThat(targetClass).isEqualTo(SimpleServiceImpl.class);
    }

    @Test
    public void testCglibProxyMemberDetection() throws NoSuchMethodException {
        SimpleServiceImpl proxy = createCglibProxy(new SimpleServiceImpl());

        // Advised interface method should be detected as proxy member
        Method isExposeProxy = proxy.getClass().getMethod("isExposeProxy");
        assertThat(proxyService.isProxyMember(isExposeProxy, proxy)).isTrue();

        // Business method should not be detected as proxy member
        Method getValue = proxy.getClass().getMethod("getValue");
        assertThat(proxyService.isProxyMember(getValue, proxy)).isFalse();
    }

    @Test
    public void testCglibProxyResolveTargetMember() throws NoSuchMethodException {
        SimpleServiceImpl proxy = createCglibProxy(new SimpleServiceImpl());
        Method proxyMethod = proxy.getClass().getMethod("getValue");

        // Resolve the method to the target class
        Class<?> targetClass = proxyService.ultimateTargetClass(proxy);
        var resolved = proxyService.resolveTargetMember(proxyMethod, targetClass);

        assertThat(resolved).isNotNull();
        assertThat(resolved.getName()).isEqualTo("getValue");
        assertThat(resolved.getDeclaringClass()).isEqualTo(SimpleServiceImpl.class);
    }

    @Test
    public void testNestedProxyIsDetectedAsProxy() {
        SimpleService innerProxy = createJdkDynamicProxy(new SimpleServiceImpl());
        SimpleService outerProxy = createJdkDynamicProxy(innerProxy);

        assertThat(proxyService.isProxy(outerProxy)).isTrue();
    }

    @Test
    public void testNestedProxyUltimateTargetClass() {
        SimpleService innerProxy = createJdkDynamicProxy(new SimpleServiceImpl());
        SimpleService outerProxy = createJdkDynamicProxy(innerProxy);

        Class<?> targetClass = proxyService.ultimateTargetClass(outerProxy);

        // Should resolve through all proxy layers to the ultimate target
        assertThat(targetClass).isEqualTo(SimpleServiceImpl.class);
    }

    @Test
    public void testProxyWithMultipleInterfacesIsDetectedAsProxy() {
        MultiInterfaceServiceImpl target = new MultiInterfaceServiceImpl();
        Object proxy = createProxyWithMultipleInterfaces(target);

        assertThat(proxyService.isProxy(proxy)).isTrue();
    }

    @Test
    public void testProxyWithMultipleInterfacesUltimateTargetClass() {
        MultiInterfaceServiceImpl target = new MultiInterfaceServiceImpl();
        Object proxy = createProxyWithMultipleInterfaces(target);

        Class<?> targetClass = proxyService.ultimateTargetClass(proxy);

        assertThat(targetClass).isEqualTo(MultiInterfaceServiceImpl.class);
    }

    @Test
    public void testProxyWithMultipleInterfacesMemberResolution() throws NoSuchMethodException {
        MultiInterfaceServiceImpl target = new MultiInterfaceServiceImpl();
        Object proxy = createProxyWithMultipleInterfaces(target);

        // Get method from FirstInterface
        Method getFirst = proxy.getClass().getMethod("getFirst");
        Class<?> targetClass = proxyService.ultimateTargetClass(proxy);
        var resolved = proxyService.resolveTargetMember(getFirst, targetClass);

        assertThat(resolved).isNotNull();
        assertThat(resolved.getName()).isEqualTo("getFirst");
        assertThat(resolved.getDeclaringClass()).isEqualTo(MultiInterfaceServiceImpl.class);

        // Get method from SecondInterface
        Method getSecond = proxy.getClass().getMethod("getSecond");
        var resolvedSecond = proxyService.resolveTargetMember(getSecond, targetClass);

        assertThat(resolvedSecond).isNotNull();
        assertThat(resolvedSecond.getName()).isEqualTo("getSecond");
    }

    @Test
    public void testNonProxyObjectNotDetectedAsProxy() {
        SimpleServiceImpl nonProxy = new SimpleServiceImpl();

        assertThat(proxyService.isProxy(nonProxy)).isFalse();
    }

    @Test
    public void testNonProxyObjectUltimateTargetClass() {
        SimpleServiceImpl nonProxy = new SimpleServiceImpl();

        Class<?> targetClass = proxyService.ultimateTargetClass(nonProxy);

        assertThat(targetClass).isEqualTo(SimpleServiceImpl.class);
    }

    @Test
    public void testNonProxyObjectMemberNotDetectedAsProxyMember() throws NoSuchMethodException {
        SimpleServiceImpl nonProxy = new SimpleServiceImpl();
        Method getValue = SimpleServiceImpl.class.getMethod("getValue");

        assertThat(proxyService.isProxyMember(getValue, nonProxy)).isFalse();
    }

    private SimpleService createJdkDynamicProxy(SimpleService target) {
        ProxyFactory proxyFactory = new ProxyFactory(target);
        proxyFactory.addAdvice(createNoOpAdvice());
        return (SimpleService) proxyFactory.getProxy();
    }

    private SimpleServiceImpl createCglibProxy(SimpleServiceImpl target) {
        ProxyFactory proxyFactory = new ProxyFactory(target);
        proxyFactory.setProxyTargetClass(true);
        proxyFactory.addAdvice(createNoOpAdvice());
        return (SimpleServiceImpl) proxyFactory.getProxy();
    }

    private Object createProxyWithMultipleInterfaces(MultiInterfaceServiceImpl target) {
        ProxyFactory proxyFactory = new ProxyFactory(target);
        proxyFactory.addInterface(FirstInterface.class);
        proxyFactory.addInterface(SecondInterface.class);
        proxyFactory.addAdvice(createNoOpAdvice());
        return proxyFactory.getProxy();
    }

    private MethodBeforeAdvice createNoOpAdvice() {
        return (method, args, target) -> {
            // No-op advice for testing
        };
    }

    public interface SimpleService {
        String getValue();
    }

    public static class SimpleServiceImpl implements SimpleService {
        @Override
        public String getValue() {
            return "value";
        }
    }

    public interface FirstInterface {
        String getFirst();
    }

    public interface SecondInterface {
        String getSecond();
    }

    public static class MultiInterfaceServiceImpl implements FirstInterface, SecondInterface {
        @Override
        public String getFirst() {
            return "first";
        }

        @Override
        public String getSecond() {
            return "second";
        }
    }
}
