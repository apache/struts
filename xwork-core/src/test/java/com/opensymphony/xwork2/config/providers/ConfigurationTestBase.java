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
package com.opensymphony.xwork2.config.providers;

import com.opensymphony.xwork2.ObjectFactory;
import com.opensymphony.xwork2.XWorkTestCase;
import com.opensymphony.xwork2.config.ConfigurationProvider;
import com.opensymphony.xwork2.config.impl.MockConfiguration;


/**
 * ConfigurationTestBase
 *
 * @author Jason Carreira
 *         Created Jun 9, 2003 7:42:12 AM
 */
public abstract class ConfigurationTestBase extends XWorkTestCase {

    protected ConfigurationProvider buildConfigurationProvider(final String filename) {
        configuration = new MockConfiguration();
        ((MockConfiguration)configuration).selfRegister();
        container = configuration.getContainer();

        XmlConfigurationProvider prov = new XmlConfigurationProvider(filename, true);
        prov.setObjectFactory(container.getInstance(ObjectFactory.class));
        prov.init(configuration);
        prov.loadPackages();
        return prov;
    }
}
