/*
 * Copyright 2002-2006,2009 The Apache Software Foundation.
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

package com.opensymphony.xwork2;

import com.opensymphony.xwork2.config.Configuration;
import com.opensymphony.xwork2.config.ConfigurationException;
import com.opensymphony.xwork2.config.ConfigurationManager;
import com.opensymphony.xwork2.config.ConfigurationProvider;
import com.opensymphony.xwork2.inject.*;
import com.opensymphony.xwork2.test.StubConfigurationProvider;
import com.opensymphony.xwork2.util.XWorkTestCaseHelper;
import com.opensymphony.xwork2.util.location.LocatableProperties;
import junit.framework.TestCase;


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
        configuration = configurationManager.getConfiguration();
        container = configuration.getContainer();
        actionProxyFactory = container.getInstance(ActionProxyFactory.class);
    }
    
    @Override
    protected void tearDown() throws Exception {
        XWorkTestCaseHelper.tearDown(configurationManager);
        configurationManager = null;
        configuration = null;
        container = null;
        actionProxyFactory = null;
    }
    
    protected void loadConfigurationProviders(ConfigurationProvider... providers) {
        configurationManager = XWorkTestCaseHelper.loadConfigurationProviders(configurationManager, providers);
        configuration = configurationManager.getConfiguration();
        container = configuration.getContainer();
        actionProxyFactory = container.getInstance(ActionProxyFactory.class);
    }
    
    protected void loadButAdd(final Class<?> type, final Object impl) {
        loadButAdd(type, Container.DEFAULT_NAME, impl);
    }
    
    protected void loadButAdd(final Class<?> type, final String name, final Object impl) {
        loadConfigurationProviders(new StubConfigurationProvider() {
            @Override
            public void register(ContainerBuilder builder,
                    LocatableProperties props) throws ConfigurationException {
                builder.factory(type, name, new Factory() {
                    public Object create(Context context) throws Exception {
                        return impl;
                    }
                    
                }, Scope.SINGLETON);
            }
        });
    }
    
}
