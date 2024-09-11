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
package org.apache.struts2.config;

import com.mockobjects.dynamic.C;
import com.mockobjects.dynamic.Mock;
import org.apache.struts2.ActionContext;
import org.apache.struts2.ActionProxy;
import org.apache.struts2.SimpleAction;
import org.apache.struts2.XWorkTestCase;
import org.apache.struts2.config.entities.ActionConfig;
import org.apache.struts2.config.providers.MockConfigurationProvider;
import org.apache.struts2.config.providers.XmlConfigurationProvider;
import org.apache.struts2.inject.ContainerBuilder;
import org.apache.struts2.mock.MockInterceptor;
import org.apache.struts2.test.StubConfigurationProvider;
import org.apache.struts2.util.location.LocatableProperties;
import org.apache.struts2.StrutsConstants;
import org.apache.struts2.dispatcher.HttpParameters;

import java.util.HashMap;
import java.util.Map;


/**
 * @author Jason Carreira
 */
public class ConfigurationTest extends XWorkTestCase {

    public void testAbstract() {
        try {
            actionProxyFactory.createActionProxy("/abstract", "test", null, null);
            fail();
        } catch (Exception e) {
            // this is what we expected
        }

        try {
            ActionProxy proxy = actionProxyFactory.createActionProxy("/nonAbstract", "test", null, null);
            assertEquals("test", proxy.getActionName());
            assertEquals(proxy.getConfig().getClassName(), SimpleAction.class.getName());
        } catch (Exception e) {
            e.printStackTrace();
            fail();
        }
    }

    public void testDefaultNamespace() {
        HashMap<String, String> params = new HashMap<>();
        params.put("blah", "this is blah");

        ActionContext extraContext = ActionContext.of()
                .withParameters(HttpParameters.create(params).build());

        try {
            ActionProxy proxy = actionProxyFactory.createActionProxy("/does/not/exist", "Foo", null, extraContext.getContextMap());
            proxy.execute();
            assertEquals("this is blah", proxy.getInvocation().getStack().findValue("[1].blah"));
        } catch (Exception e) {
            e.printStackTrace();
            fail();
        }
    }

    public void testFileIncludeLoader() {
        RuntimeConfiguration configuration = configurationManager.getConfiguration().getRuntimeConfiguration();

        // check entityTest package
        assertNotNull(configuration.getActionConfig("includeTest", "includeTest"));

        // check inheritance from Default
        assertNotNull(configuration.getActionConfig("includeTest", "Foo"));
    }

    public void testWildcardName() {
        RuntimeConfiguration configuration = configurationManager.getConfiguration().getRuntimeConfiguration();

        ActionConfig config = configuration.getActionConfig("", "WildCard/Simple/input");

        assertNotNull(config);
        assertEquals("Wrong class name, " + config.getClassName(), "org.apache.struts2.SimpleAction", config.getClassName());
        assertEquals("Wrong method name", "input", config.getMethodName());

        Map<String, String> p = config.getParams();
        assertEquals("Wrong parameter, " + p.get("foo"), "Simple", p.get("foo"));
        assertEquals("Wrong parameter, " + p.get("bar"), "input", p.get("bar"));
    }

    public void testWildcardNamespace() {
        RuntimeConfiguration configuration = configurationManager.getConfiguration().getRuntimeConfiguration();

        ActionConfig config = configuration.getActionConfig("/animals/dog", "commandTest");

        assertNotNull(config);
        assertEquals("Wrong class name, " + config.getClassName(), "org.apache.struts2.SimpleAction", config.getClassName());

        Map<String, String> p = config.getParams();
        assertEquals("Wrong parameter, " + p.get("0"), "/animals/dog", p.get("0"));
        assertEquals("Wrong parameter, " + p.get("1"), "dog", p.get("1"));
    }

    public void testGlobalResults() {
        try {
            ActionProxy proxy = actionProxyFactory.createActionProxy("", "Foo", null, null);
            assertNotNull(proxy.getConfig().getResults().get("login"));
        } catch (Exception e) {
            e.printStackTrace();
            fail();
        }
    }

    public void testInterceptorParamInehritanceOverride() {
        try {
            ActionProxy proxy = actionProxyFactory.createActionProxy("/foo/bar", "TestInterceptorParamInehritanceOverride", null, null);
            assertEquals(1, proxy.getConfig().getInterceptors().size());

            MockInterceptor testInterceptor = (MockInterceptor) proxy.getConfig().getInterceptors().get(0).getInterceptor();
            assertEquals("foo123", testInterceptor.getExpectedFoo());
            proxy.execute();
            assertTrue(testInterceptor.isExecuted());
        } catch (Exception e) {
            e.printStackTrace();
            fail();
        }
    }

    public void testInterceptorParamInheritance() {
        try {
            ActionProxy proxy = actionProxyFactory.createActionProxy("/foo/bar", "TestInterceptorParamInheritance", null, null);
            assertEquals(1, proxy.getConfig().getInterceptors().size());

            MockInterceptor testInterceptor = (MockInterceptor) proxy.getConfig().getInterceptors().get(0).getInterceptor();
            assertEquals("expectedFoo", testInterceptor.getExpectedFoo());
            proxy.execute();
            assertTrue(testInterceptor.isExecuted());
        } catch (Exception e) {
            e.printStackTrace();
            fail();
        }
    }

    public void testInterceptorParamOverride() {
        try {
            ActionProxy proxy = actionProxyFactory.createActionProxy("", "TestInterceptorParamOverride", null, null);
            assertEquals(1, proxy.getConfig().getInterceptors().size());

            MockInterceptor testInterceptor = (MockInterceptor) proxy.getConfig().getInterceptors().get(0).getInterceptor();
            assertEquals("foo123", testInterceptor.getExpectedFoo());
            proxy.execute();
            assertTrue(testInterceptor.isExecuted());
        } catch (Exception e) {
            e.printStackTrace();
            fail();
        }
    }

    public void testInterceptorParams() {
        try {
            ActionProxy proxy = actionProxyFactory.createActionProxy("", "TestInterceptorParam", null, null);
            assertEquals(1, proxy.getConfig().getInterceptors().size());

            MockInterceptor testInterceptor = (MockInterceptor) proxy.getConfig().getInterceptors().get(0).getInterceptor();
            assertEquals("expectedFoo", testInterceptor.getExpectedFoo());
            proxy.execute();
            assertTrue(testInterceptor.isExecuted());
        } catch (Exception e) {
            e.printStackTrace();
            fail();
        }
    }

    public void testMultipleConfigProviders() {
        configurationManager.addContainerProvider(new MockConfigurationProvider());

        try {
            configurationManager.reload();
        } catch (ConfigurationException e) {
            e.printStackTrace();
            fail();
        }

        RuntimeConfiguration configuration = configurationManager.getConfiguration().getRuntimeConfiguration();

        // check that it has configuration from xml
        assertNotNull(configuration.getActionConfig("/foo/bar", "Bar"));

        // check that it has configuration from MockConfigurationProvider
        assertNotNull(configuration.getActionConfig("", MockConfigurationProvider.FOO_ACTION_NAME));
    }

    public void testMultipleContainerProviders() {
        // to start from scratch
        configurationManager.destroyConfiguration();
        // to build basic configuration
        configurationManager.getConfiguration();

        Mock mockContainerProvider = new Mock(ContainerProvider.class);
        mockContainerProvider.expect("init", C.ANY_ARGS);
        mockContainerProvider.expect("register", C.ANY_ARGS);
        mockContainerProvider.matchAndReturn("equals", C.ANY_ARGS, false);
        mockContainerProvider.matchAndReturn("toString", "foo");
        mockContainerProvider.matchAndReturn("destroy", null);
        mockContainerProvider.expectAndReturn("needsReload", true);
        // the order of providers must be changed as just first is checked if reload is needed
        configurationManager.addContainerProvider((ContainerProvider) mockContainerProvider.proxy());
        XmlConfigurationProvider provider = new StrutsXmlConfigurationProvider("xwork-sample.xml");
        container.inject(provider);
        configurationManager.addContainerProvider(provider);

        Configuration config = null;
        try {
            config = configurationManager.getConfiguration();
        } catch (ConfigurationException e) {
            e.printStackTrace();
            fail();
        }

        RuntimeConfiguration configuration = config.getRuntimeConfiguration();

        // check that it has configuration from xml
        assertNotNull(configuration.getActionConfig("/foo/bar", "Bar"));

        mockContainerProvider.verify();
    }

    public void testGetActionConfigFallbackToEmptyNamespaceWhenNamespaceDontMatchAndEmptyNamespaceFallbackIsEnabled() {
        // struts.actionConfig.fallbackToEmptyNamespace default to true, so it is enabled
        RuntimeConfiguration configuration = configurationManager.getConfiguration().getRuntimeConfiguration();

        // check namespace that doesn't match fallback to empty namespace
        ActionConfig actionConfig = configuration.getActionConfig("/something/that/is/not/in/the/namespace/config", "LazyFoo");
        assertEquals("default", actionConfig.getPackageName()); // fallback to empty namespace (package name is default)
        assertEquals("LazyFoo", actionConfig.getName());

        // check non-empty namespace and name in config still matches
        assertNotNull(configuration.getActionConfig("includeTest", "Foo"));

        // check root namespace and name in config still matches
        actionConfig = configuration.getActionConfig("/", "LazyFoo");
        assertEquals("default", actionConfig.getPackageName());
        assertEquals("LazyFoo", actionConfig.getName());
    }

    public void testGetActionConfigReturnNullWhenNamespaceDontMatchAndEmptyNamespaceFallbackIsDisabled() {
        // set the struts.actionConfig.fallbackToEmptyNamespace to false and reload the configuration
        setStrutsConstant(StrutsConstants.STRUTS_ACTION_CONFIG_FALLBACK_TO_EMPTY_NAMESPACE, "false");
        RuntimeConfiguration configuration = configurationManager.getConfiguration().getRuntimeConfiguration();

        // check namespace that doesn't match NOT fallback to empty namespace and return null
        assertNull(configuration.getActionConfig("/something/that/is/not/in/the/namespace/config", "LazyFoo"));

        // check non-empty namespace and name in config still matches
        assertNotNull(configuration.getActionConfig("includeTest", "Foo"));

        // check root namespace and name in config still matches
        ActionConfig actionConfig = configuration.getActionConfig("/", "LazyFoo");
        assertEquals("default", actionConfig.getPackageName());
        assertEquals("LazyFoo", actionConfig.getName());
    }

    public void testInitForPackageProviders() {

        loadConfigurationProviders(new StubConfigurationProvider() {
            @Override
            public void register(ContainerBuilder builder,
                                 LocatableProperties props) throws ConfigurationException {
                builder.factory(PackageProvider.class, "foo", MyPackageProvider.class);
            }
        });

        assertEquals(configuration, MyPackageProvider.getConfiguration());
    }

    public void testInitOnceForConfigurationProviders() {

        loadConfigurationProviders(new StubConfigurationProvider() {
            boolean called = false;

            @Override
            public void init(Configuration config) {
                if (called) {
                    fail("Called twice");
                }
                called = true;
            }

            @Override
            public void loadPackages() {
                if (!called) {
                    fail("Never called");
                }
            }
        });
    }

    public void testMultipleInheritance() {
        try {
            ActionProxy proxy;
            proxy = actionProxyFactory.createActionProxy("multipleInheritance", "test", null, null);
            assertNotNull(proxy);
            proxy = actionProxyFactory.createActionProxy("multipleInheritance", "Foo", null, null);
            assertNotNull(proxy);
            proxy = actionProxyFactory.createActionProxy("multipleInheritance", "testMultipleInheritance", null, null);
            assertNotNull(proxy);
            assertEquals(4, proxy.getConfig().getInterceptors().size());
            assertEquals(2, proxy.getConfig().getResults().size());
        } catch (Exception e) {
            e.printStackTrace();
            fail();
        }
    }

    public void testPackageExtension() {
        try {
            ActionProxy proxy = actionProxyFactory.createActionProxy("/foo/bar", "Bar", null, null);
            assertEquals(4, proxy.getConfig().getInterceptors().size());
        } catch (Exception e) {
            e.printStackTrace();
            fail();
        }
    }


    @Override
    protected void setUp() throws Exception {
        super.setUp();

        // ensure we're using the default configuration, not simple config
        XmlConfigurationProvider provider = new StrutsXmlConfigurationProvider("xwork-sample.xml");
        container.inject(provider);
        loadConfigurationProviders(provider);
    }

    public static class MyPackageProvider implements PackageProvider {
        static Configuration config;

        public void loadPackages() throws ConfigurationException {
        }

        public boolean needsReload() {
            return config != null;
        }

        public static Configuration getConfiguration() {
            return config;
        }

        public void init(Configuration configuration)
            throws ConfigurationException {
            config = configuration;
        }

    }
}
