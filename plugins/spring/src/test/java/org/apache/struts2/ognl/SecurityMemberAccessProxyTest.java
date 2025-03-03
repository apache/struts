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

import org.apache.struts2.ActionProxy;
import org.apache.struts2.XWorkJUnit4TestCase;
import org.apache.struts2.config.StrutsXmlConfigurationProvider;
import org.apache.struts2.config.providers.XmlConfigurationProvider;
import org.junit.Before;
import org.junit.Test;
import org.springframework.aop.MethodBeforeAdvice;
import org.springframework.aop.framework.ProxyFactory;

import java.lang.reflect.Member;
import java.lang.reflect.Method;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertThrows;
import static org.junit.Assert.assertTrue;

public class SecurityMemberAccessProxyTest extends XWorkJUnit4TestCase {

    private static final String PROXY_MEMBER_METHOD = "isExposeProxy";
    private static final String TEST_SUB_BEAN_CLASS_METHOD = "getIssueId";

    private Map<String, Object> context;
    private ActionProxy proxy;
    private final SecurityMemberAccess sma = new SecurityMemberAccess(null, null);

    private Member proxyObjectProxyMember;
    private Member proxyObjectNonProxyMember;

    @Before
    @Override
    public void setUp() throws Exception {
        XmlConfigurationProvider provider = new StrutsXmlConfigurationProvider("org/apache/struts2/spring/actionContext-xwork.xml");
        loadConfigurationProviders(provider);

        context = new HashMap<>();
        proxy = actionProxyFactory.createActionProxy(null, "chaintoAOPedTestSubBeanAction", null, context);
        proxyObjectProxyMember = proxy.getAction().getClass().getMethod(PROXY_MEMBER_METHOD);
        proxyObjectNonProxyMember = proxy.getAction().getClass().getMethod(TEST_SUB_BEAN_CLASS_METHOD);
    }

    /**
     * When {@code disallowProxyObjectAccess} is {@code true}, proxy access is blocked irrespective of
     * {@code disallowProxyMemberAccess} value and irrespective of whether the member itself originates from the proxy.
     */
    @Test
    public void disallowProxyObjectAccess() {
        sma.useDisallowProxyObjectAccess(Boolean.TRUE.toString());
        Arrays.asList(proxyObjectProxyMember, proxyObjectNonProxyMember).forEach(member ->
                Arrays.asList(Boolean.TRUE, Boolean.FALSE).forEach(disallowProxyMemberAccess -> {
                    sma.useDisallowProxyMemberAccess(disallowProxyMemberAccess.toString());
                    assertFalse(sma.isAccessible(context, proxy.getAction(), member, ""));
                })
        );
    }

    @Test
    public void disallowProxyMemberAccess() {
        sma.useDisallowProxyObjectAccess(Boolean.FALSE.toString());
        sma.useDisallowProxyMemberAccess(Boolean.TRUE.toString());
        assertFalse(sma.isAccessible(context, proxy.getAction(), proxyObjectProxyMember, ""));
        assertTrue(sma.isAccessible(context, proxy.getAction(), proxyObjectNonProxyMember, ""));
    }

    @Test
    public void allowAllProxyAccess() {
        sma.useDisallowProxyObjectAccess(Boolean.FALSE.toString());
        sma.useDisallowProxyMemberAccess(Boolean.FALSE.toString());
        assertTrue(sma.isAccessible(context, proxy.getAction(), proxyObjectProxyMember, ""));
        assertTrue(sma.isAccessible(context, proxy.getAction(), proxyObjectNonProxyMember, ""));
    }

    @Test
    public void nullTargetAndTargetAndMemberNotAllowed() {
        sma.useDisallowProxyObjectAccess(Boolean.TRUE.toString());
        sma.useDisallowProxyMemberAccess(Boolean.TRUE.toString());
        assertTrue(sma.isAccessible(context, null, proxyObjectProxyMember, ""));
    }

    @Test
    public void nullTargetAndTargetAllowedAndMemberNotAllowed() {
        sma.useDisallowProxyObjectAccess(Boolean.FALSE.toString());
        sma.useDisallowProxyMemberAccess(Boolean.TRUE.toString());
        assertTrue(sma.isAccessible(context, null, proxyObjectProxyMember, ""));
    }

    @Test
    public void nullTargetAndTargetAndMemberAllowed() {
        sma.useDisallowProxyObjectAccess(Boolean.FALSE.toString());
        sma.useDisallowProxyMemberAccess(Boolean.FALSE.toString());
        assertTrue(sma.isAccessible(context, null, proxyObjectProxyMember, ""));
    }

    @Test
    public void nullMemberAndTargetAndMemberNotAllowed() {
        sma.useDisallowProxyObjectAccess(Boolean.TRUE.toString());
        sma.useDisallowProxyMemberAccess(Boolean.TRUE.toString());
        Object action = proxy.getAction();
        assertThrows("Member cannot be null!", IllegalArgumentException.class,
                () -> sma.isAccessible(context, action, null, ""));
    }

    @Test
    public void nullMemberAndTargetAllowedAndMemberNotAllowed() {
        sma.useDisallowProxyObjectAccess(Boolean.FALSE.toString());
        sma.useDisallowProxyMemberAccess(Boolean.TRUE.toString());
        Object action = proxy.getAction();
        assertThrows("Member cannot be null!", IllegalArgumentException.class,
                () -> sma.isAccessible(context, action, null, ""));
    }

    @Test
    public void nullMemberAndTargetNotAllowedAndMemberAllowed() {
        sma.useDisallowProxyObjectAccess(Boolean.TRUE.toString());
        sma.useDisallowProxyMemberAccess(Boolean.FALSE.toString());
        Object action = proxy.getAction();
        assertThrows("Member cannot be null!", IllegalArgumentException.class,
                () -> sma.isAccessible(context, action, null, ""));
    }

    @Test
    public void nullTargetAndMemberAndTargetAndMemberNotAllowed() {
        sma.useDisallowProxyObjectAccess(Boolean.TRUE.toString());
        sma.useDisallowProxyMemberAccess(Boolean.TRUE.toString());
        assertThrows("Member cannot be null!", IllegalArgumentException.class,
                () -> sma.isAccessible(context, null, null, ""));
    }

    @Test
    public void nullTargetAndMemberAndTargetNotAllowedAndMemberAllowed() {
        sma.useDisallowProxyObjectAccess(Boolean.TRUE.toString());
        sma.useDisallowProxyMemberAccess(Boolean.FALSE.toString());
        assertThrows("Member cannot be null!", IllegalArgumentException.class,
                () -> sma.isAccessible(context, null, null, ""));
    }

    @Test
    public void nullTargetAndMemberAndTargetAllowedAndMemberNotAllowed() {
        sma.useDisallowProxyObjectAccess(Boolean.FALSE.toString());
        sma.useDisallowProxyMemberAccess(Boolean.TRUE.toString());
        assertThrows("Member cannot be null!", IllegalArgumentException.class,
                () -> sma.isAccessible(context, null, null, ""));
    }

    @Test
    public void nullTargetAndMemberAndTargetAndMemberAllowed() {
        sma.useDisallowProxyObjectAccess(Boolean.FALSE.toString());
        sma.useDisallowProxyMemberAccess(Boolean.FALSE.toString());
        assertThrows("Member cannot be null!", IllegalArgumentException.class,
                () -> sma.isAccessible(context, null, null, ""));
    }

    @Test
    public void nullPropertyName() {
        sma.useDisallowProxyMemberAccess(Boolean.FALSE.toString());
        Object action = proxy.getAction();
        assertTrue(sma.isAccessible(context, action, proxyObjectProxyMember, null));
    }

    /**
     * When the allowlist is enabled and proxy object access is allowed, Spring proxies should be allowlisted based
     * on their underlying target object. Class allowlisting should work as expected.
     */
    @Test
    public void classInclusion_springProxy_allowProxyObjectAccess() throws Exception {
        SpringService proxyObject = newSpringService();
        Method proxyMethod = proxyObject.getClass().getMethod("doSomething");

        sma.useEnforceAllowlistEnabled(Boolean.TRUE.toString());
        sma.useDisallowProxyObjectAccess(Boolean.FALSE.toString());
        sma.useAllowlistClasses(SpringServiceImpl.class.getName());

        assertTrue(sma.checkAllowlist(proxyObject, proxyMethod));
    }

    private static SpringService newSpringService() {
        SpringService target = new SpringServiceImpl();
        ProxyFactory proxyFactory = new ProxyFactory(target);
        proxyFactory.addAdvice(((MethodBeforeAdvice) (method, args, target1) -> {
            System.out.println("Intercepting: " + method.getName());
        }));
        return (SpringService) proxyFactory.getProxy();
    }
}

interface SpringService {
    void doSomething();
}

class SpringServiceImpl implements SpringService {
    @Override
    public void doSomething() {
        System.out.println("Doing something...");
    }
}
