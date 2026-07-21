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
package org.apache.struts2.cdi;

import jakarta.enterprise.inject.spi.Bean;
import org.apache.struts2.ognl.StrutsProxyCacheFactory;
import org.apache.struts2.util.ProxyService;
import org.jboss.weld.bootstrap.api.helpers.RegistrySingletonProvider;
import org.jboss.weld.environment.se.Weld;
import org.jboss.weld.environment.se.WeldContainer;
import org.jboss.weld.proxy.WeldClientProxy;
import org.junit.AfterClass;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;

import java.lang.reflect.Field;
import java.lang.reflect.Method;

import static org.assertj.core.api.Assertions.assertThat;

public class CdiProxyServiceTest {

    private static WeldContainer container;

    private ProxyService proxyService;
    private ProxiedFooService proxy;

    @BeforeClass
    public static void startContainer() {
        container = new Weld().containerId(RegistrySingletonProvider.STATIC_INSTANCE).initialize();
    }

    @AfterClass
    public static void stopContainer() {
        container.shutdown();
    }

    @Before
    public void setUp() {
        proxyService = new CdiProxyService(new StrutsProxyCacheFactory<>("1000", "basic"));
        proxy = container.select(ProxiedFooService.class).get();
    }

    @Test
    public void weldClientProxyIsRecognisedAsProxy() {
        assertThat(proxy).isInstanceOf(WeldClientProxy.class); // sanity: Weld really produced a proxy
        assertThat(proxyService.isProxy(proxy)).isTrue();
    }

    @Test
    public void ultimateTargetClassResolvesRealClass() {
        assertThat(proxyService.ultimateTargetClass(proxy)).isEqualTo(ProxiedFooService.class);
    }

    @Test
    public void weldProxyAccessorIsProxyMember() throws Exception {
        Method getMetadata = proxy.getClass().getMethod("getMetadata");
        assertThat(proxyService.isProxyMember(getMetadata, proxy)).isTrue();
    }

    @Test
    public void realBeanMethodIsNotProxyMember() throws Exception {
        Method getHello = proxy.getClass().getMethod("getHello");
        assertThat(proxyService.isProxyMember(getHello, proxy)).isFalse();
    }

    @Test
    public void plainObjectIsNotProxy() {
        Object plain = new Object();
        assertThat(proxyService.isProxy(plain)).isFalse();
        assertThat(proxyService.ultimateTargetClass(plain)).isEqualTo(Object.class);
    }

    @Test
    public void nullIsNotProxy() {
        assertThat(proxyService.isProxy(null)).isFalse();
    }

    @Test
    public void memberOfNonProxyObjectIsNotProxyMember() throws Exception {
        Method getHello = ProxiedFooService.class.getMethod("getHello");
        assertThat(proxyService.isProxyMember(getHello, new Object())).isFalse();
    }

    @Test
    public void nonMethodMemberIsNotProxyMember() throws Exception {
        Field field = SomeHolder.class.getDeclaredField("value");
        assertThat(proxyService.isProxyMember(field, proxy)).isFalse();
    }

    @Test
    public void withoutWeldFallsBackToBaseBehaviour() throws Exception {
        ProxyService noWeld = new CdiProxyService(new StrutsProxyCacheFactory<>("1000", "basic"), false);
        Method getMetadata = proxy.getClass().getMethod("getMetadata");

        assertThat(noWeld.isProxy(proxy)).isFalse();
        assertThat(noWeld.ultimateTargetClass(proxy)).isEqualTo(proxy.getClass());
        assertThat(noWeld.isProxyMember(getMetadata, proxy)).isFalse();
    }

    @Test
    public void ultimateTargetClassFallsBackToProxyClassWhenContextualInstanceIsNull() {
        // Contract: a null contextual instance from Weld's Metadata must fall back to the proxy's own class, not NPE or return null
        NullContextualInstanceProxy stub = new NullContextualInstanceProxy();
        assertThat(proxyService.ultimateTargetClass(stub)).isEqualTo(stub.getClass());
    }

    @Test
    public void ultimateTargetClassFallsBackToProxyClassWhenUnwrappingThrowsLinkageError() {
        // Contract: a LinkageError while unwrapping the proxy must fall back to the proxy's own class rather than propagate
        LinkageErrorProxy stub = new LinkageErrorProxy();
        assertThat(proxyService.ultimateTargetClass(stub)).isEqualTo(stub.getClass());
    }

    private static class SomeHolder {
        @SuppressWarnings("unused")
        private String value;
    }

    private static class NullContextualInstanceProxy implements WeldClientProxy {
        @Override
        public Metadata getMetadata() {
            return new Metadata() {
                @Override
                public Bean<?> getBean() {
                    return null;
                }

                @Override
                public Object getContextualInstance() {
                    return null;
                }
            };
        }
    }

    private static class LinkageErrorProxy implements WeldClientProxy {
        @Override
        public Metadata getMetadata() {
            throw new NoClassDefFoundError("simulated missing Weld class");
        }
    }
}
