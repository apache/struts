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

import com.opensymphony.xwork2.config.ConfigurationProvider;
import com.opensymphony.xwork2.config.entities.PackageConfig;

public class XmlConfigurationProviderWildCardIncludeTest extends ConfigurationTestBase {

    
    public void testWildCardInclude() throws Exception {
        final String filename = "com/opensymphony/xwork2/config/providers/xwork-test-wildcard-include.xml";
        ConfigurationProvider provider = buildConfigurationProvider(filename);

        provider.init(configuration);
        provider.loadPackages();

        PackageConfig defaultWildcardPackage = configuration.getPackageConfig("default-wildcard");
        assertNotNull(defaultWildcardPackage);
        assertEquals("default-wildcard", defaultWildcardPackage.getName());


        PackageConfig defaultOnePackage = configuration.getPackageConfig("default-1");
        assertNotNull(defaultOnePackage);
        assertEquals("default-1", defaultOnePackage.getName());
        
        PackageConfig defaultTwoPackage = configuration.getPackageConfig("default-2");
        assertNotNull(defaultTwoPackage);
        assertEquals("default-2", defaultTwoPackage.getName());       

        configurationManager.addContainerProvider(provider);
        configurationManager.reload();

    }
}
