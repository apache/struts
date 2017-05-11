/*
 * Copyright 2017 The Apache Software Foundation.
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

    public void testIsSpringAopProxy() throws Exception {
        Object simpleAction = appContext.getBean("simple-action");
        assertFalse(ProxyUtil.isSpringAopProxy(simpleAction));

        Object proxiedAction = appContext.getBean("proxied-action");
        assertTrue(ProxyUtil.isSpringAopProxy(proxiedAction));

        Object autoProxiedAction = appContext.getBean("auto-proxied-action");
        assertTrue(ProxyUtil.isSpringAopProxy(autoProxiedAction));

        Object pointcuttedTestBean = appContext.getBean("pointcutted-test-bean");
        assertTrue(ProxyUtil.isSpringAopProxy(pointcuttedTestBean));

        Object pointcuttedTestSubBean = appContext.getBean("pointcutted-test-sub-bean");
        assertTrue(ProxyUtil.isSpringAopProxy(pointcuttedTestSubBean));

        Object aspectedTestSubBean = appContext.getBean("aspected-test-sub-bean");
        assertFalse(ProxyUtil.isSpringAopProxy(aspectedTestSubBean));
    }

    public void testGetSpringUltimateTargetObject() throws Exception {
        Object simpleAction = appContext.getBean("simple-action");
        Object simpleActionUltimateTargetObject = ProxyUtil.getSpringUltimateTargetObject(simpleAction);
        assertEquals(simpleAction, simpleActionUltimateTargetObject);

        Object proxiedAction = appContext.getBean("proxied-action");
        Object proxiedActionUltimateTargetObject = ProxyUtil.getSpringUltimateTargetObject(proxiedAction);
        assertEquals(SimpleAction.class, proxiedActionUltimateTargetObject.getClass());

        Object autoProxiedAction = appContext.getBean("auto-proxied-action");
        Object autoProxiedActionUltimateTargetObject = ProxyUtil.getSpringUltimateTargetObject(autoProxiedAction);
        assertEquals(SimpleAction.class, autoProxiedActionUltimateTargetObject.getClass());

        Object pointcuttedTestBean = appContext.getBean("pointcutted-test-bean");
        Object pointcuttedTestBeanUltimateTargetObject = ProxyUtil.getSpringUltimateTargetObject(pointcuttedTestBean);
        assertEquals(TestBean.class, pointcuttedTestBeanUltimateTargetObject.getClass());

        Object pointcuttedTestSubBean = appContext.getBean("pointcutted-test-sub-bean");
        Object pointcuttedTestSubBeanUltimateTargetObject = ProxyUtil.getSpringUltimateTargetObject(pointcuttedTestSubBean);
        assertEquals(TestSubBean.class, pointcuttedTestSubBeanUltimateTargetObject.getClass());

        Object aspectedTestSubBean = appContext.getBean("aspected-test-sub-bean");
        Object aspectedTestSubBeanUltimateTargetObject = ProxyUtil.getSpringUltimateTargetObject(aspectedTestSubBean);
        assertEquals(aspectedTestSubBean, aspectedTestSubBeanUltimateTargetObject);
    }
}
