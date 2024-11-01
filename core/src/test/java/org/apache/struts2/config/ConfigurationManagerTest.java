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
import org.apache.struts2.XWorkTestCase;
import org.apache.struts2.config.providers.StrutsDefaultConfigurationProvider;
import org.apache.struts2.conversion.TypeConverterHolder;
import org.apache.struts2.inject.Container;
import org.apache.struts2.inject.ContainerBuilder;
import org.apache.struts2.util.location.LocatableProperties;
import org.mockito.Mockito;

import java.util.Properties;

import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;


/**
 * ConfigurationManagerTest
 *
 * @author Jason Carreira
 *         Created May 6, 2003 10:59:59 PM
 */
public class ConfigurationManagerTest extends XWorkTestCase {

    Mock configProviderMock;

    @Override
    protected void setUp() throws Exception {
        super.setUp();
        configurationManager.destroyConfiguration();

        configProviderMock = new Mock(ConfigurationProvider.class);
        configProviderMock.matchAndReturn("equals", C.ANY_ARGS, false);

        ConfigurationProvider mockProvider = (ConfigurationProvider) configProviderMock.proxy();
        configurationManager.addContainerProvider(new StrutsDefaultConfigurationProvider());
        configurationManager.addContainerProvider(mockProvider);
    }

    @Override
    protected void tearDown() throws Exception {
        configProviderMock.expect("destroy");
        super.tearDown();
    }

    public void testInit() {
        configProviderMock.expect("init", C.isA(Configuration.class));
        configProviderMock.expect("register", C.ANY_ARGS);
        configProviderMock.expect("loadPackages", C.ANY_ARGS);
        configProviderMock.matchAndReturn("toString", "mock");

        configuration = configurationManager.getConfiguration();
    }

    public void testDestroyConfiguration() throws Exception {
        class State {
            public boolean isDestroyed1 =false;
            public boolean isDestroyed2 =false;
        }

        final State state = new State();
        ConfigurationManager configurationManager = new ConfigurationManager(Container.DEFAULT_NAME);
        configurationManager.addContainerProvider(new ConfigurationProvider() {
            public void destroy() {
                throw new RuntimeException("testing testing 123");
            }
            public void init(Configuration configuration) throws ConfigurationException {
            }
            public void loadPackages() throws ConfigurationException {
            }
            public boolean needsReload() { return false;
            }
            public void register(ContainerBuilder builder, Properties props) throws ConfigurationException {
            }
            public void register(ContainerBuilder builder, LocatableProperties props) throws ConfigurationException {
            }
        });
        configurationManager.addContainerProvider(new ConfigurationProvider() {
            public void destroy() {
                state.isDestroyed1 = true;
            }
            public void init(Configuration configuration) throws ConfigurationException {
            }
            public void loadPackages() throws ConfigurationException {
            }
            public boolean needsReload() { return false;
            }
            public void register(ContainerBuilder builder, Properties props) throws ConfigurationException {
            }
            public void register(ContainerBuilder builder, LocatableProperties props) throws ConfigurationException {
            }
        });
        configurationManager.addContainerProvider(new ConfigurationProvider() {
            public void destroy() {
                throw new RuntimeException("testing testing 123");
            }
            public void init(Configuration configuration) throws ConfigurationException {
            }
            public void loadPackages() throws ConfigurationException {
            }
            public boolean needsReload() { return false;
            }
            public void register(ContainerBuilder builder, Properties props) throws ConfigurationException {
            }
            public void register(ContainerBuilder builder, LocatableProperties props) throws ConfigurationException {
            }
        });
        configurationManager.addContainerProvider(new ConfigurationProvider() {
            public void destroy() {
                state.isDestroyed2 = true;
            }
            public void init(Configuration configuration) throws ConfigurationException {
            }
            public void loadPackages() throws ConfigurationException {
            }
            public boolean needsReload() { return false;
            }
            public void register(ContainerBuilder builder, Properties props) throws ConfigurationException {
            }
            public void register(ContainerBuilder builder, LocatableProperties props) throws ConfigurationException {
            }
        });

        assertFalse(state.isDestroyed1);
        assertFalse(state.isDestroyed2);

        configurationManager.clearContainerProviders();

        assertTrue(state.isDestroyed1);
        assertTrue(state.isDestroyed2);
    }

    public void testRemoveConfigurationProvider() throws Exception {
        ConfigurationProvider configProvider1 = Mockito.mock(ConfigurationProvider.class);
        ConfigurationProvider configProvider2 = Mockito.mock(ConfigurationProvider.class);
        configurationManager.addContainerProvider(configProvider1);
        configurationManager.addContainerProvider(configProvider2);

        configurationManager.removeContainerProvider(configProvider1);

        verify(configProvider1).destroy();
        assertFalse(configurationManager.getContainerProviders().contains(configProvider1));
        assertTrue(configurationManager.getContainerProviders().contains(configProvider2));
    }

    public void testClearConfigurationProviders() throws Exception {
        configProviderMock.expect("destroy");
        configurationManager.clearContainerProviders();
        configProviderMock.verify();
    }

    public void testEarlyInitializable() throws Exception {
        TypeConverterHolder converterHolder = container.getInstance(TypeConverterHolder.class);
        assertTrue("java.io.File mapping should being putted by DefaultConversionPropertiesProcessor.init()",
                converterHolder.containsDefaultMapping("java.io.File"));
    }
}
