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

import com.opensymphony.xwork2.ActionProxy;
import com.opensymphony.xwork2.XWorkTestCase;
import com.opensymphony.xwork2.config.providers.XmlConfigurationProvider;
import org.apache.struts2.config.StrutsXmlConfigurationProvider;

import java.lang.reflect.Member;
import java.util.HashMap;
import java.util.Map;

public class SecurityMemberAccessProxyTest extends XWorkTestCase {
    private Map<String, Object> context;
    private ActionProxy proxy;
    private Map<String, Member> members;
    private final SecurityMemberAccess sma = new SecurityMemberAccess(true);
    private final String PROXY_MEMBER_METHOD = "isExposeProxy";
    private final String TEST_SUB_BEAN_CLASS_METHOD = "setIssueId";

    @Override
    public void setUp() throws Exception {
        super.setUp();

        context = new HashMap<>();
        // Set up XWork
        XmlConfigurationProvider provider = new StrutsXmlConfigurationProvider("com/opensymphony/xwork2/spring/actionContext-xwork.xml");
        container.inject(provider);
        loadConfigurationProviders(provider);

        // Setup proxy object
        setupProxy();
    }

    public void testProxyAccessIsBlocked() throws Exception {
        members.values().forEach(member -> {
            // When disallowProxyObjectAccess is set to true, and disallowProxyMemberAccess is set to false, the proxy access is blocked
            sma.useDisallowProxyObjectAccess(Boolean.TRUE.toString());
            sma.useDisallowProxyMemberAccess(Boolean.FALSE.toString());
            assertFalse(sma.isAccessible(context, proxy.getAction(), member, ""));

            // When disallowProxyObjectAccess is set to true, and disallowProxyMemberAccess is set to true, the proxy access is blocked
            sma.useDisallowProxyObjectAccess(Boolean.TRUE.toString());
            sma.useDisallowProxyMemberAccess(Boolean.TRUE.toString());
            assertFalse(sma.isAccessible(context, proxy.getAction(), member, ""));
        });

        // When disallowProxyObjectAccess is set to false, and disallowProxyMemberAccess is set to true, the proxy member access is blocked
        sma.useDisallowProxyObjectAccess(Boolean.FALSE.toString());
        sma.useDisallowProxyMemberAccess(Boolean.TRUE.toString());
        assertFalse(sma.isAccessible(context, proxy.getAction(), members.get(PROXY_MEMBER_METHOD), ""));
    }

    public void testProxyAccessIsAccessible() throws Exception {
        members.values().forEach(member -> {
            // When disallowProxyObjectAccess is set to false, and disallowProxyMemberAccess is set to false, the proxy access is allowed
            sma.useDisallowProxyObjectAccess(Boolean.FALSE.toString());
            sma.useDisallowProxyMemberAccess(Boolean.FALSE.toString());
            assertTrue(sma.isAccessible(context, proxy.getAction(), member, ""));
        });

        // When disallowProxyObjectAccess is set to false, and disallowProxyMemberAccess is set to true, the original class member access is allowed
        sma.useDisallowProxyObjectAccess(Boolean.FALSE.toString());
        sma.useDisallowProxyMemberAccess(Boolean.TRUE.toString());
        assertTrue(sma.isAccessible(context, proxy.getAction(), members.get(TEST_SUB_BEAN_CLASS_METHOD), ""));
    }

    private void setupProxy() throws NoSuchMethodException {
        proxy = actionProxyFactory.createActionProxy(null, "chaintoAOPedTestSubBeanAction", null, context);

        members = new HashMap<>();
        // method is proxy member
        members.put(PROXY_MEMBER_METHOD, proxy.getAction().getClass().getMethod(PROXY_MEMBER_METHOD));
        // method is not proxy member but from POJO class
        members.put(TEST_SUB_BEAN_CLASS_METHOD, proxy.getAction().getClass().getMethod(TEST_SUB_BEAN_CLASS_METHOD, String.class));
    }
}
