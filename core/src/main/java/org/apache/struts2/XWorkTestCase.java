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
package org.apache.struts2;

import org.apache.struts2.config.Configuration;
import org.apache.struts2.config.ConfigurationException;
import org.apache.struts2.config.ConfigurationManager;
import org.apache.struts2.config.ConfigurationProvider;
import org.apache.struts2.inject.Container;
import org.apache.struts2.inject.ContainerBuilder;
import org.apache.struts2.inject.Context;
import org.apache.struts2.inject.Factory;
import org.apache.struts2.inject.Scope;
import org.apache.struts2.test.StubConfigurationProvider;
import org.apache.struts2.util.XWorkTestCaseHelper;
import org.apache.struts2.util.location.LocatableProperties;
import junit.framework.TestCase;
import org.apache.commons.lang3.ClassUtils;

import java.util.Locale;
import java.util.Map;

import static java.util.Collections.singletonMap;

/**
 * Base JUnit TestCase to extend for XWork specific JUnit tests. Uses
 * the generic test setup for logic.
 *
 * @author plightbo
 */
public abstract class XWorkTestCase extends TestCase {

    protected ConfigurationManager configurationManager;
    protected Configuration configuration;
    protected Container container;
    protected ActionProxyFactory actionProxyFactory;

    public XWorkTestCase() {
        super();
    }

    @Override
    protected void setUp() throws Exception {
        configurationManager = XWorkTestCaseHelper.setUp();
        reloadConfiguration(configurationManager);
    }

    @Override
    protected void tearDown() throws Exception {
        XWorkTestCaseHelper.tearDown(configurationManager);
    }

    private void reloadConfiguration(ConfigurationManager configurationManager) {
        configuration = configurationManager.getConfiguration();
        container = configuration.getContainer();
        actionProxyFactory = container.getInstance(ActionProxyFactory.class);
    }

    protected void loadConfigurationProviders(ConfigurationProvider... providers) {
        configurationManager = XWorkTestCaseHelper.loadConfigurationProviders(configurationManager, providers);
        reloadConfiguration(configurationManager);
    }

    protected void loadButSet(Map<String, ?> properties) {
        loadConfigurationProviders(new StubConfigurationProvider() {
            @Override
            public void register(ContainerBuilder builder,
                                 LocatableProperties props) throws ConfigurationException {
                properties.forEach((k, v) -> props.setProperty(k, String.valueOf(v)));
            }
        });
    }

    protected <T> void loadButAdd(final Class<T> type, final T impl) {
        loadButAdd(type, Container.DEFAULT_NAME, impl);
    }

    protected <T> void loadButAdd(final Class<T> type, final String name, final T impl) {
        loadConfigurationProviders(new StubConfigurationProvider() {
            @Override
            public void register(ContainerBuilder builder, LocatableProperties props) throws ConfigurationException {
                if (impl instanceof String || ClassUtils.isPrimitiveOrWrapper(impl.getClass())) {
                    props.setProperty(name, "" + impl);
                } else {
                    builder.factory(type, name, new Factory<>() {
                        @Override
                        public T create(Context context) throws Exception {
                            return impl;
                        }

                        @Override
                        public Class<T> type() {
                            return (Class<T>) impl.getClass();
                        }
                    }, Scope.SINGLETON);
                }
            }
        });
    }

    protected Map<String, Object> createContextWithLocale(Locale locale) {
        return ActionContext.of()
            .withLocale(locale)
            .getContextMap();
    }

    protected void setStrutsConstant(String constant, String value) {
        setStrutsConstant(singletonMap(constant, value));
    }

    protected void setStrutsConstant(final Map<String, String> overwritePropeties) {
        configurationManager.addContainerProvider(new StubConfigurationProvider() {
            @Override
            public void register(ContainerBuilder builder, LocatableProperties props) throws ConfigurationException {
                for (Map.Entry<String, String> stringStringEntry : overwritePropeties.entrySet()) {
                    props.setProperty(stringStringEntry.getKey(), stringStringEntry.getValue(), null);
                }
            }

            @Override
            public void destroy() {
            }
        });

        configurationManager.reload();
        reloadConfiguration(configurationManager);
    }
}
