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

import ognl.Ognl;
import ognl.OgnlContext;
import org.apache.struts2.cdi.CdiProxyService;
import org.apache.struts2.cdi.ProxiedFooService;
import org.apache.struts2.util.ProxyService;
import org.jboss.weld.bootstrap.api.helpers.RegistrySingletonProvider;
import org.jboss.weld.environment.se.Weld;
import org.jboss.weld.environment.se.WeldContainer;
import org.jboss.weld.proxy.WeldClientProxy;
import org.junit.AfterClass;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;

import java.lang.reflect.Member;
import java.lang.reflect.Method;

import static org.assertj.core.api.Assertions.assertThat;

public class CdiSecurityMemberAccessProxyTest {

    private static WeldContainer container;

    private OgnlContext context;
    private SecurityMemberAccess sma;
    private ProxiedFooService proxy;
    private Member proxyMember;   // WeldClientProxy#getMetadata — a proxy member
    private Member realMember;    // ProxiedFooService#getHello — a real bean member

    @BeforeClass
    public static void startContainer() {
        container = new Weld().containerId(RegistrySingletonProvider.STATIC_INSTANCE).initialize();
    }

    @AfterClass
    public static void stopContainer() {
        container.shutdown();
    }

    @Before
    public void setUp() throws Exception {
        ProxyService proxyService = new CdiProxyService(new StrutsProxyCacheFactory<>("1000", "basic"));
        sma = new SecurityMemberAccess(null, null);
        sma.setProxyService(proxyService);

        context = (OgnlContext) Ognl.createDefaultContext(null);
        proxy = container.select(ProxiedFooService.class).get();
        proxyMember = proxy.getClass().getMethod("getMetadata");
        realMember = proxy.getClass().getMethod("getHello");

        assertThat(proxy).isInstanceOf(WeldClientProxy.class); // sanity
    }

    @Test
    public void disallowProxyObjectAccessBlocksWeldProxy() {
        sma.useDisallowProxyObjectAccess(Boolean.TRUE.toString());
        // Object-level proxy access disallowed -> any member on the proxy is blocked.
        assertThat(sma.isAccessible(context, proxy, realMember, "")).isFalse();
        assertThat(sma.isAccessible(context, proxy, proxyMember, "")).isFalse();
    }

    @Test
    public void disallowProxyMemberAccessBlocksWeldProxyMember() {
        sma.useDisallowProxyObjectAccess(Boolean.FALSE.toString());
        sma.useDisallowProxyMemberAccess(Boolean.TRUE.toString());
        // The Weld proxy accessor is a proxy member -> blocked.
        assertThat(sma.isAccessible(context, proxy, proxyMember, "")).isFalse();
    }

    /**
     * When the allowlist is enabled and proxy object access is allowed, a Weld client proxy must be allowlisted based
     * on its underlying target class ({@code ProxiedFooService}), not the proxy class. This is the core WW-5604 scenario.
     */
    @Test
    public void classInclusion_weldProxy_allowProxyObjectAccess() throws Exception {
        Method realMethod = proxy.getClass().getMethod("getHello");

        sma.useEnforceAllowlistEnabled(Boolean.TRUE.toString());
        sma.useDisallowProxyObjectAccess(Boolean.FALSE.toString());
        sma.useAllowlistClasses(ProxiedFooService.class.getName());

        assertThat(sma.checkAllowlist(proxy, realMethod)).isTrue();
    }
}
