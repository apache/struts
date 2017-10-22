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

import java.lang.reflect.Member;
import java.util.HashMap;
import java.util.Map;

import com.opensymphony.xwork2.ActionProxy;
import com.opensymphony.xwork2.XWorkTestCase;
import com.opensymphony.xwork2.config.providers.XmlConfigurationProvider;

public class SecurityMemberAccessProxyTest extends XWorkTestCase {
    private Map<String, Object> context;

    @Override
    public void setUp() throws Exception {
        super.setUp();

        context = new HashMap<>();
        // Set up XWork
        XmlConfigurationProvider provider = new XmlConfigurationProvider("com/opensymphony/xwork2/spring/actionContext-xwork.xml");
        container.inject(provider);
        loadConfigurationProviders(provider);
    }

    public void testProxyAccessIsBlocked() throws Exception {
        ActionProxy proxy = actionProxyFactory.createActionProxy(null,
                "chaintoAOPedTestSubBeanAction", null, context);

        SecurityMemberAccess sma = new SecurityMemberAccess(false);
        sma.setDisallowProxyMemberAccess(true);

        Member member = proxy.getAction().getClass().getMethod("isExposeProxy");

        boolean accessible = sma.isAccessible(context, proxy.getAction(), member, "");
        assertFalse(accessible);
    }

    public void testProxyAccessIsAccessible() throws Exception {
        ActionProxy proxy = actionProxyFactory.createActionProxy(null,
                "chaintoAOPedTestSubBeanAction", null, context);

        SecurityMemberAccess sma = new SecurityMemberAccess(false);

        Member member = proxy.getAction().getClass().getMethod("isExposeProxy");

        boolean accessible = sma.isAccessible(context, proxy.getAction(), member, "");
        assertTrue(accessible);
    }
}
