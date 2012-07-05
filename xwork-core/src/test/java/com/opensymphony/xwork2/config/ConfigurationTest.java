/*
 * Copyright 2002-2003,2009 The Apache Software Foundation.
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
package com.opensymphony.xwork2.config;

import com.mockobjects.dynamic.C;
import com.mockobjects.dynamic.Mock;
import com.opensymphony.xwork2.ActionContext;
import com.opensymphony.xwork2.ActionProxy;
import com.opensymphony.xwork2.SimpleAction;
import com.opensymphony.xwork2.XWorkTestCase;
import com.opensymphony.xwork2.config.entities.ActionConfig;
import com.opensymphony.xwork2.config.providers.MockConfigurationProvider;
import com.opensymphony.xwork2.config.providers.XmlConfigurationProvider;
import com.opensymphony.xwork2.inject.ContainerBuilder;
import com.opensymphony.xwork2.mock.MockInterceptor;
import com.opensymphony.xwork2.test.StubConfigurationProvider;
import com.opensymphony.xwork2.util.location.LocatableProperties;

import java.util.HashMap;
import java.util.Map;


/**
 * ConfigurationTest
 * <p/>
 * Created : Jan 27, 2003 1:30:08 AM
 *
 * @author Jason Carreira
 */
public class ConfigurationTest extends XWorkTestCase {

    public void testAbstract() {
        try {
            actionProxyFactory.createActionProxy("/abstract", "test", null);
            fail();
        } catch (Exception e) {
            // this is what we expected
        }

        try {
            ActionProxy proxy = actionProxyFactory.createActionProxy("/nonAbstract", "test", null);
            assertTrue(proxy.getActionName().equals("test"));
            assertTrue(proxy.getConfig().getClassName().equals(SimpleAction.class.getName()));
        } catch (Exception e) {
            e.printStackTrace();
            fail();
        }
    }

    public void testDefaultNamespace() {
        HashMap<String, String> params = new HashMap<String, String>();
        params.put("blah", "this is blah");

        HashMap<String, Object> extraContext = new HashMap<String, Object>();
        extraContext.put(ActionContext.PARAMETERS, params);

        try {
            ActionProxy proxy = actionProxyFactory.createActionProxy("/does/not/exist", "Foo", extraContext);
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
        assertTrue("Wrong class name, "+config.getClassName(), 
                "com.opensymphony.xwork2.SimpleAction".equals(config.getClassName()));
        assertTrue("Wrong method name", "input".equals(config.getMethodName()));
        
        Map<String, String> p = config.getParams();
        assertTrue("Wrong parameter, "+p.get("foo"), "Simple".equals(p.get("foo")));
        assertTrue("Wrong parameter, "+p.get("bar"), "input".equals(p.get("bar")));
    }

    public void testWildcardNamespace() {
        RuntimeConfiguration configuration = configurationManager.getConfiguration().getRuntimeConfiguration();

        ActionConfig config = configuration.getActionConfig("/animals/dog", "commandTest");

        assertNotNull(config);
        assertTrue("Wrong class name, "+config.getClassName(),
                "com.opensymphony.xwork2.SimpleAction".equals(config.getClassName()));

        Map<String, String> p = config.getParams();
        assertTrue("Wrong parameter, "+p.get("0"), "/animals/dog".equals(p.get("0")));
        assertTrue("Wrong parameter, "+p.get("1"), "dog".equals(p.get("1")));
    }

    public void testGlobalResults() {
        try {
            ActionProxy proxy = actionProxyFactory.createActionProxy("", "Foo", null);
            assertNotNull(proxy.getConfig().getResults().get("login"));
        } catch (Exception e) {
            e.printStackTrace();
            fail();
        }
    }

    public void testInterceptorParamInehritanceOverride() {
        try {
            ActionProxy proxy = actionProxyFactory.createActionProxy("/foo/bar", "TestInterceptorParamInehritanceOverride", null);
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
            ActionProxy proxy = actionProxyFactory.createActionProxy("/foo/bar", "TestInterceptorParamInheritance", null);
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
            ActionProxy proxy = actionProxyFactory.createActionProxy("", "TestInterceptorParamOverride", null);
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
            ActionProxy proxy = actionProxyFactory.createActionProxy("", "TestInterceptorParam", null);
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
    
    public void testMultipleContainerProviders() throws Exception {
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
        XmlConfigurationProvider provider = new XmlConfigurationProvider("xwork-sample.xml");
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
            proxy = actionProxyFactory.createActionProxy("multipleInheritance", "test", null);
            assertNotNull(proxy);
            proxy = actionProxyFactory.createActionProxy("multipleInheritance", "Foo", null);
            assertNotNull(proxy);
            proxy = actionProxyFactory.createActionProxy("multipleInheritance", "testMultipleInheritance", null);
            assertNotNull(proxy);
            assertEquals(5, proxy.getConfig().getInterceptors().size());
            assertEquals(2, proxy.getConfig().getResults().size());
        } catch (Exception e) {
            e.printStackTrace();
            fail();
        }
    }

    public void testPackageExtension() {
        try {
            ActionProxy proxy = actionProxyFactory.createActionProxy("/foo/bar", "Bar", null);
            assertEquals(5, proxy.getConfig().getInterceptors().size());
        } catch (Exception e) {
            e.printStackTrace();
            fail();
        }
    }
    

    @Override
    protected void setUp() throws Exception {
        super.setUp();

        // ensure we're using the default configuration, not simple config
        XmlConfigurationProvider provider = new XmlConfigurationProvider("xwork-sample.xml");
        container.inject(provider);
        loadConfigurationProviders(provider);
    }

    public static class MyPackageProvider implements PackageProvider {
        static Configuration config;
        public void loadPackages() throws ConfigurationException {}
        public boolean needsReload() { return config != null; }
        
        public static Configuration getConfiguration() {
            return config;
        }
        public void init(Configuration configuration)
                throws ConfigurationException {
            config = configuration;
        }
        
    }
}
