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
package com.opensymphony.xwork2.util;

import com.opensymphony.xwork2.ActionContext;
import com.opensymphony.xwork2.config.*;
import com.opensymphony.xwork2.config.providers.XWorkConfigurationProvider;
import com.opensymphony.xwork2.config.providers.XmlConfigurationProvider;
import com.opensymphony.xwork2.inject.Container;
import com.opensymphony.xwork2.inject.ContainerBuilder;
import com.opensymphony.xwork2.util.location.LocatableProperties;

/**
 * Generic test setup methods to be used with any unit testing framework. 
 */
public class XWorkTestCaseHelper {

    public static ConfigurationManager setUp() throws Exception {
        ConfigurationManager configurationManager = new ConfigurationManager();
        configurationManager.addContainerProvider(new XWorkConfigurationProvider());
        Configuration config = configurationManager.getConfiguration();
        Container container = config.getContainer();
        
        // Reset the value stack
        ValueStack stack = container.getInstance(ValueStackFactory.class).createValueStack();
        stack.getContext().put(ActionContext.CONTAINER, container);
        ActionContext.setContext(new ActionContext(stack.getContext()));
    
        // clear out localization
        LocalizedTextUtil.reset();
        
    
        //ObjectFactory.setObjectFactory(container.getInstance(ObjectFactory.class));
        return configurationManager;
    }

    public static ConfigurationManager loadConfigurationProviders(ConfigurationManager configurationManager,
            ConfigurationProvider... providers) {
        try {
            tearDown(configurationManager);
        } catch (Exception e) {
            throw new RuntimeException("Cannot clean old configuration", e);
        }
        configurationManager = new ConfigurationManager();
        configurationManager.addContainerProvider(new ContainerProvider() {
            public void destroy() {}
            public void init(Configuration configuration) throws ConfigurationException {}
            public boolean needsReload() { return false; }

            public void register(ContainerBuilder builder,
                    LocatableProperties props) throws ConfigurationException {
                builder.setAllowDuplicates(true);
            }
            
        });
        configurationManager.addContainerProvider(new XWorkConfigurationProvider());
        for (ConfigurationProvider prov : providers) {
            if (prov instanceof XmlConfigurationProvider) {
                ((XmlConfigurationProvider)prov).setThrowExceptionOnDuplicateBeans(false);
            }
            configurationManager.addContainerProvider(prov);
        }
        Container container = configurationManager.getConfiguration().getContainer();
        
        // Reset the value stack
        ValueStack stack = container.getInstance(ValueStackFactory.class).createValueStack();
        stack.getContext().put(ActionContext.CONTAINER, container);
        ActionContext.setContext(new ActionContext(stack.getContext()));
        
        return configurationManager;
    }

    public static void tearDown(ConfigurationManager configurationManager) throws Exception {
    
        //  clear out configuration
        if (configurationManager != null) {
            configurationManager.destroyConfiguration();
            configurationManager = null;
        }
        ActionContext.setContext(null);
    }
}