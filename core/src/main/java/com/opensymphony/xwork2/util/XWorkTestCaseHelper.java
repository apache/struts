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
package com.opensymphony.xwork2.util;

import com.opensymphony.xwork2.ActionContext;
import com.opensymphony.xwork2.config.*;
import com.opensymphony.xwork2.config.providers.StrutsDefaultConfigurationProvider;
import com.opensymphony.xwork2.config.providers.XmlConfigurationProvider;
import com.opensymphony.xwork2.inject.Container;
import com.opensymphony.xwork2.inject.ContainerBuilder;
import com.opensymphony.xwork2.util.location.LocatableProperties;

/**
 * Generic test setup methods to be used with any unit testing framework. 
 */
public class XWorkTestCaseHelper {

    public static ConfigurationManager setUp() throws Exception {
        ConfigurationManager configurationManager = new ConfigurationManager(Container.DEFAULT_NAME);
        configurationManager.addContainerProvider(new StrutsDefaultConfigurationProvider());
        Configuration config = configurationManager.getConfiguration();
        Container container = config.getContainer();
        
        // Reset the value stack
        ValueStack stack = container.getInstance(ValueStackFactory.class).createValueStack();
        stack.getActionContext().withContainer(container).withValueStack(stack).bind();

        return configurationManager;
    }

    public static ConfigurationManager loadConfigurationProviders(ConfigurationManager configurationManager,
            ConfigurationProvider... providers) {
        try {
            tearDown(configurationManager);
        } catch (Exception e) {
            throw new RuntimeException("Cannot clean old configuration", e);
        }
        configurationManager = new ConfigurationManager(Container.DEFAULT_NAME);
        configurationManager.addContainerProvider(new ContainerProvider() {
            public void destroy() {}
            public void init(Configuration configuration) throws ConfigurationException {}
            public boolean needsReload() { return false; }

            public void register(ContainerBuilder builder, LocatableProperties props) throws ConfigurationException {
                builder.setAllowDuplicates(true);
            }
            
        });
        configurationManager.addContainerProvider(new StrutsDefaultConfigurationProvider());
        for (ConfigurationProvider prov : providers) {
            if (prov instanceof XmlConfigurationProvider) {
                ((XmlConfigurationProvider)prov).setThrowExceptionOnDuplicateBeans(false);
            }
            configurationManager.addContainerProvider(prov);
        }
        Container container = configurationManager.getConfiguration().getContainer();
        
        // Reset the value stack
        ValueStack stack = container.getInstance(ValueStackFactory.class).createValueStack();
        stack.getActionContext().withContainer(container).withValueStack(stack).bind();

        return configurationManager;
    }

    public static void tearDown(ConfigurationManager configurationManager) throws Exception {
        //  clear out configuration
        if (configurationManager != null) {
            configurationManager.destroyConfiguration();
        }
        ActionContext.clear();
    }
}