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
package org.apache.struts2.factory;

import com.opensymphony.xwork2.ActionProxy;
import com.opensymphony.xwork2.ActionProxyFactory;
import com.opensymphony.xwork2.DefaultActionProxyFactory;
import com.opensymphony.xwork2.config.ConfigurationException;
import com.opensymphony.xwork2.config.ConfigurationProvider;
import com.opensymphony.xwork2.config.providers.XmlConfigurationProvider;
import com.opensymphony.xwork2.inject.ContainerBuilder;
import com.opensymphony.xwork2.inject.Context;
import com.opensymphony.xwork2.inject.Factory;
import com.opensymphony.xwork2.inject.Scope;
import com.opensymphony.xwork2.mock.MockActionProxy;
import com.opensymphony.xwork2.test.StubConfigurationProvider;
import com.opensymphony.xwork2.util.location.LocatableProperties;
import org.apache.struts2.StrutsInternalTestCase;

import java.util.Collections;
import java.util.Map;

public class PrefixBasedActionProxyFactoryTest extends StrutsInternalTestCase {

    private PrefixBasedActionProxyFactory factory;

    public void testDifferentPrefixes() throws Exception {
        initFactory("/ns1:prefix1,/ns2:prefix2");

        ActionProxy proxy1 = factory.createActionProxy("/ns1", "", "", Collections.<String, Object>emptyMap(), false, true);
        assertTrue(proxy1 instanceof Prefix1ActionProxy);

        ActionProxy proxy2 = factory.createActionProxy("/ns2", "", "", Collections.<String, Object>emptyMap(), false, true);
        assertTrue(proxy2 instanceof Prefix2ActionProxy);
    }

    public void testFallbackToDefault() throws Exception {
        initFactory("/ns1:prefix1");

        ActionProxy proxy1 = factory.createActionProxy("/ns1", "", "", Collections.<String, Object>emptyMap(), false, true);
        assertTrue(proxy1 instanceof Prefix1ActionProxy);

        ActionProxy proxy2 = factory.createActionProxy("", "Foo", "", Collections.<String, Object>emptyMap(), false, true);
        assertTrue(proxy2 instanceof StrutsActionProxy);
    }

    public void testEmptyPrefix() throws Exception {
        initFactory(":prefix1");

        ActionProxy proxy1 = factory.createActionProxy("/ns1", "", "", Collections.<String, Object>emptyMap(), false, true);
        assertTrue(proxy1 instanceof Prefix1ActionProxy);

        ActionProxy proxy2 = factory.createActionProxy("/ns2", "", "", Collections.<String, Object>emptyMap(), false, true);
        assertTrue(proxy2 instanceof Prefix1ActionProxy);
    }

    @Override
    public void setUp() throws Exception {
        ConfigurationProvider[] providers = new ConfigurationProvider[]{
                new XmlConfigurationProvider("xwork-sample.xml"),
                new StubConfigurationProvider() {
                    @Override
                    public void register(ContainerBuilder builder, LocatableProperties props) throws ConfigurationException {
                        builder.factory(ActionProxyFactory.class, "prefix1", new Factory() {
                            public Object create(Context context) throws Exception {
                                return new Prefix1Factory();
                            }
                            public Class type() {
                                return Prefix1Factory.class;
                            }
                        }, Scope.SINGLETON);
                    }
                },
                new StubConfigurationProvider() {
                    @Override
                    public void register(ContainerBuilder builder, LocatableProperties props) throws ConfigurationException {
                        builder.factory(ActionProxyFactory.class, "prefix2", new Factory() {
                            public Object create(Context context) throws Exception {
                                return new Prefix2Factory();
                            }
                            public Class type() {
                                return Prefix2Factory.class;
                            }
                        }, Scope.SINGLETON);
                    }
                }
        };

        loadConfigurationProviders(providers);

        factory = new PrefixBasedActionProxyFactory();
        factory.setContainer(container);
    }

    void initFactory(String list) {
        factory.setPrefixBasedActionProxyFactories(list);
        factory.init();
    }

    @Override
    public void tearDown() throws Exception {
        super.tearDown();
        factory = null;
    }

    public static class Prefix1Factory extends DefaultActionProxyFactory {
        public ActionProxy createActionProxy(String namespace, String actionName, String methodName, Map<String, Object> extraContext, boolean executeResult, boolean cleanupContext) {
            return new Prefix1ActionProxy();
        }

    }

    public static class Prefix1ActionProxy extends MockActionProxy {
    }

    public static class Prefix2Factory extends DefaultActionProxyFactory {
        public ActionProxy createActionProxy(String namespace, String actionName, String methodName, Map<String, Object> extraContext, boolean executeResult, boolean cleanupContext) {
            return new Prefix2ActionProxy();
        }
    }

    public static class Prefix2ActionProxy extends MockActionProxy {
    }

}