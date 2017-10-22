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
package com.opensymphony.xwork2.spring;

import com.opensymphony.xwork2.*;
import com.opensymphony.xwork2.config.providers.XmlConfigurationProvider;
import com.opensymphony.xwork2.util.ProxyUtil;
import org.springframework.context.ApplicationContext;

/**
 * Test various utility methods dealing with spring proxies.
 *
 */
public class SpringProxyUtilTest extends XWorkTestCase {
    private ApplicationContext appContext;

    @Override public void setUp() throws Exception {
        super.setUp();

        // Set up XWork
        XmlConfigurationProvider provider = new XmlConfigurationProvider("com/opensymphony/xwork2/spring/actionContext-xwork.xml");
        container.inject(provider);
        loadConfigurationProviders(provider);
        appContext = ((SpringObjectFactory)container.getInstance(ObjectFactory.class)).appContext;
    }

    public void testIsProxy() throws Exception {
        Object simpleAction = appContext.getBean("simple-action");
        assertFalse(ProxyUtil.isProxy(simpleAction));

        Object proxiedAction = appContext.getBean("proxied-action");
        assertTrue(ProxyUtil.isProxy(proxiedAction));

        Object autoProxiedAction = appContext.getBean("auto-proxied-action");
        assertTrue(ProxyUtil.isProxy(autoProxiedAction));

        Object pointcuttedTestBean = appContext.getBean("pointcutted-test-bean");
        assertTrue(ProxyUtil.isProxy(pointcuttedTestBean));

        Object pointcuttedTestSubBean = appContext.getBean("pointcutted-test-sub-bean");
        assertTrue(ProxyUtil.isProxy(pointcuttedTestSubBean));

        Object testAspect = appContext.getBean("test-aspect");
        assertFalse(ProxyUtil.isProxy(testAspect));
    }

    public void testUltimateTargetClass() throws Exception {
        Object simpleAction = appContext.getBean("simple-action");
        Class<?> simpleActionUltimateTargetClass = ProxyUtil.ultimateTargetClass(simpleAction);
        assertEquals(SimpleAction.class, simpleActionUltimateTargetClass);

        Object proxiedAction = appContext.getBean("proxied-action");
        Class<?> proxiedActionUltimateTargetClass = ProxyUtil.ultimateTargetClass(proxiedAction);
        assertEquals(SimpleAction.class, proxiedActionUltimateTargetClass);

        Object autoProxiedAction = appContext.getBean("auto-proxied-action");
        Class<?> autoProxiedActionUltimateTargetClass = ProxyUtil.ultimateTargetClass(autoProxiedAction);
        assertEquals(SimpleAction.class, autoProxiedActionUltimateTargetClass);

        Object pointcuttedTestBean = appContext.getBean("pointcutted-test-bean");
        Class<?> pointcuttedTestBeanUltimateTargetClass = ProxyUtil.ultimateTargetClass(pointcuttedTestBean);
        assertEquals(TestBean.class, pointcuttedTestBeanUltimateTargetClass);

        Object pointcuttedTestSubBean = appContext.getBean("pointcutted-test-sub-bean");
        Class<?> pointcuttedTestSubBeanUltimateTargetClass = ProxyUtil.ultimateTargetClass(pointcuttedTestSubBean);
        assertEquals(TestSubBean.class, pointcuttedTestSubBeanUltimateTargetClass);

        Object testAspect = appContext.getBean("test-aspect");
        Class<?> testAspectUltimateTargetClass = ProxyUtil.ultimateTargetClass(testAspect);
        assertEquals(TestAspect.class, testAspectUltimateTargetClass);
    }

    public void testIsProxyMember() throws Exception {
        Object simpleAction = appContext.getBean("simple-action");
        assertFalse(ProxyUtil.isProxyMember(
                simpleAction.getClass().getMethod("setName", String.class), simpleAction));

        Object proxiedAction = appContext.getBean("proxied-action");
        assertTrue(ProxyUtil.isProxyMember(
                proxiedAction.getClass().getMethod("setExposeProxy", boolean.class), proxiedAction));

        Object autoProxiedAction = appContext.getBean("auto-proxied-action");
        assertTrue(ProxyUtil.isProxyMember(
                autoProxiedAction.getClass().getMethod("getTargetClass"), autoProxiedAction));

        Object pointcuttedTestBean = appContext.getBean("pointcutted-test-bean");
        assertTrue(ProxyUtil.isProxyMember(
                pointcuttedTestBean.getClass().getMethod("getTargetSource"), pointcuttedTestBean));

        Object pointcuttedTestSubBean = appContext.getBean("pointcutted-test-sub-bean");
        assertFalse(ProxyUtil.isProxyMember(
                pointcuttedTestSubBean.getClass().getConstructor(), pointcuttedTestSubBean));

        Object testAspect = appContext.getBean("test-aspect");
        assertFalse(ProxyUtil.isProxyMember(
                testAspect.getClass().getMethod("setExposeProxy", boolean.class), testAspect));
    }
}
