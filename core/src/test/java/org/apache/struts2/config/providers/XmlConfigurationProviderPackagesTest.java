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
package org.apache.struts2.config.providers;

import org.apache.struts2.config.ConfigurationException;
import org.apache.struts2.config.ConfigurationProvider;
import org.apache.struts2.config.RuntimeConfiguration;
import org.apache.struts2.config.entities.PackageConfig;

import java.util.List;


/**
 * Created by IntelliJ IDEA.
 * User: Mike
 * Date: May 6, 2003
 * Time: 3:10:16 PM
 * To change this template use Options | File Templates.
 */
public class XmlConfigurationProviderPackagesTest extends ConfigurationTestBase {

    public void testBadInheritance() throws ConfigurationException {
        ConfigurationProvider provider = null;
        try {
	    	provider = buildConfigurationProvider(getXmlConfigFilePath("xwork-test-bad-inheritance.xml"));
	    	fail("Should have thrown a ConfigurationException");
	        provider.init(configuration);
	        provider.loadPackages();
        } catch (ConfigurationException e) {
        	// Expected
        }
    }

    public void testBasicPackages() throws ConfigurationException {
        ConfigurationProvider provider = buildConfigurationProvider(getXmlConfigFilePath("xwork-test-basic-packages.xml"));
        provider.init(configuration);
        provider.loadPackages();

        // setup our expectations
        PackageConfig expectedNamespacePackage = new PackageConfig.Builder("namespacepkg")
            .namespace("/namespace/set")
            .isAbstract(false)
            .build();
        PackageConfig expectedAbstractPackage = new PackageConfig.Builder("abstractpkg")
            .isAbstract(true)
            .build();

        // test expectations
        assertEquals(3, configuration.getPackageConfigs().size());
        assertEquals(expectedNamespacePackage, configuration.getPackageConfig("namespacepkg"));
        assertEquals(expectedAbstractPackage, configuration.getPackageConfig("abstractpkg"));
    }

    public void testDefaultPackage() throws ConfigurationException {
        ConfigurationProvider provider = buildConfigurationProvider(getXmlConfigFilePath("xwork-test-default-package.xml"));
        provider.init(configuration);
        provider.loadPackages();

        // setup our expectations
        PackageConfig expectedPackageConfig = new PackageConfig.Builder("default").build();

        // test expectations
        assertEquals(1, configuration.getPackageConfigs().size());
        assertEquals(expectedPackageConfig, configuration.getPackageConfig("default"));
    }

    public void testPackageInheritance() throws ConfigurationException {
        ConfigurationProvider provider = buildConfigurationProvider(getXmlConfigFilePath("xwork-test-package-inheritance.xml"));

        provider.init(configuration);
        provider.loadPackages();

        // test expectations
        assertEquals(5, configuration.getPackageConfigs().size());
        PackageConfig defaultPackage = configuration.getPackageConfig("default");
        assertNotNull(defaultPackage);
        assertEquals("default", defaultPackage.getName());
        PackageConfig abstractPackage = configuration.getPackageConfig("abstractPackage");
        assertNotNull(abstractPackage);
        assertEquals("abstractPackage", abstractPackage.getName());
        PackageConfig singlePackage = configuration.getPackageConfig("singleInheritance");
        assertNotNull(singlePackage);
        assertEquals("singleInheritance", singlePackage.getName());
        assertEquals(1, singlePackage.getParents().size());
        assertEquals(defaultPackage, singlePackage.getParents().get(0));
        PackageConfig multiplePackage = configuration.getPackageConfig("multipleInheritance");
        assertNotNull(multiplePackage);
        assertEquals("multipleInheritance", multiplePackage.getName());
        assertEquals(3, multiplePackage.getParents().size());
        List<PackageConfig> multipleParents = multiplePackage.getParents();
        assertTrue(multipleParents.contains(defaultPackage));
        assertTrue(multipleParents.contains(abstractPackage));
        assertTrue(multipleParents.contains(singlePackage));

        PackageConfig parentBelow = configuration.getPackageConfig("testParentBelow");
        assertEquals(1, parentBelow.getParents().size());
        List<PackageConfig> parentBelowParents = parentBelow.getParents();
        assertTrue(parentBelowParents.contains(multiplePackage));

        configurationManager.addContainerProvider(provider);
        configurationManager.reload();

        RuntimeConfiguration runtimeConfiguration = configurationManager.getConfiguration().getRuntimeConfiguration();
        assertNotNull(runtimeConfiguration.getActionConfig("/multiple", "default"));
        assertNotNull(runtimeConfiguration.getActionConfig("/multiple", "abstract"));
        assertNotNull(runtimeConfiguration.getActionConfig("/multiple", "single"));
        assertNotNull(runtimeConfiguration.getActionConfig("/multiple", "multiple"));
        assertNotNull(runtimeConfiguration.getActionConfig("/single", "default"));
        assertNull(runtimeConfiguration.getActionConfig("/single", "abstract"));
        assertNotNull(runtimeConfiguration.getActionConfig("/single", "single"));
        assertNull(runtimeConfiguration.getActionConfig("/single", "multiple"));

        assertNotNull(runtimeConfiguration.getActionConfig("/parentBelow", "default"));
        assertNotNull(runtimeConfiguration.getActionConfig("/parentBelow", "abstract"));
        assertNotNull(runtimeConfiguration.getActionConfig("/parentBelow", "single"));
        assertNotNull(runtimeConfiguration.getActionConfig("/parentBelow", "multiple"));
        assertNotNull(runtimeConfiguration.getActionConfig("/parentBelow", "testParentBelowAction"));

    }

    public void testPackageWithFinalAttributeLoads() throws ConfigurationException {
        ConfigurationProvider provider = buildConfigurationProvider(getXmlConfigFilePath("xwork-test-package-final.xml"));

        provider.init(configuration);
        provider.loadPackages();

        // test expectations
        assertEquals(3, configuration.getPackageConfigs().size());
        PackageConfig defaultPackage = configuration.getPackageConfig("default");
        assertNotNull(defaultPackage);
        assertEquals("default", defaultPackage.getName());

        // final package extends default
        PackageConfig finalPackage = configuration.getPackageConfig("finalPackage");
        assertNotNull(finalPackage);
        assertEquals("finalPackage", finalPackage.getName());
        assertEquals(1, finalPackage.getParents().size());
        assertEquals(defaultPackage, finalPackage.getParents().get(0));

        // normal package extends default
        PackageConfig normalPackage = configuration.getPackageConfig("normalPackage");
        assertNotNull(normalPackage);
        assertEquals("normalPackage", normalPackage.getName());
        assertEquals(1, normalPackage.getParents().size());
        assertEquals(defaultPackage, normalPackage.getParents().get(0));

        configurationManager.addContainerProvider(provider);
        configurationManager.reload();

        RuntimeConfiguration runtimeConfiguration = configurationManager.getConfiguration().getRuntimeConfiguration();
        assertNotNull(runtimeConfiguration.getActionConfig("/final", "default"));
        assertNotNull(runtimeConfiguration.getActionConfig("/final", "actionFinal"));

        assertNotNull(runtimeConfiguration.getActionConfig("/normal", "default"));
        assertNotNull(runtimeConfiguration.getActionConfig("/normal", "actionNormal"));
    }

    public void testExtendsFinalPackageThrowsConfigurationException() throws ConfigurationException {
        try {
            buildConfigurationProvider(getXmlConfigFilePath("xwork-test-package-extends-final.xml"));
        } catch (ConfigurationException e) {
            assertEquals("Parent package is final and unextendable: parentLevelTwo", e.getMessage());
        }
    }

    public void testDefaultClassRef() throws ConfigurationException {
        final String hasDefaultClassRefPkgName = "hasDefaultClassRef";
        final String noDefaultClassRefPkgName = "noDefaultClassRef";
        final String testDefaultClassRef = "org.apache.struts2.ActionSupport";

        ConfigurationProvider provider = buildConfigurationProvider(getXmlConfigFilePath("xwork-test-defaultclassref-package.xml"));
        provider.init(configuration);

        // setup our expectations
        PackageConfig expectedDefaultClassRefPackage = new PackageConfig.Builder(hasDefaultClassRefPkgName).defaultClassRef(testDefaultClassRef).build();

        PackageConfig expectedNoDefaultClassRefPackage = new PackageConfig.Builder(noDefaultClassRefPkgName).build();

        // test expectations
        assertEquals(2, configuration.getPackageConfigs().size());
        assertEquals(expectedDefaultClassRefPackage, configuration.getPackageConfig(hasDefaultClassRefPkgName));
        assertEquals(expectedNoDefaultClassRefPackage, configuration.getPackageConfig(noDefaultClassRefPkgName));
    }

    private String getXmlConfigFilePath(String fileName) {
        return "org/apache/struts2/config/providers/" + fileName;
    }
}
