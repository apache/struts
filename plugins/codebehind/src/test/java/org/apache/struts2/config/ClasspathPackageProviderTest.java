/*
 * $Id: ClasspathPackageProviderTest.java 501717 2007-01-31 03:51:11Z mrdon $
 *
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

import java.util.Map;

import org.apache.struts2.dispatcher.ServletDispatcherResult;

import com.opensymphony.xwork2.config.Configuration;
import com.opensymphony.xwork2.config.entities.ActionConfig;
import com.opensymphony.xwork2.config.entities.PackageConfig;
import com.opensymphony.xwork2.config.entities.ResultConfig;
import com.opensymphony.xwork2.config.entities.ResultTypeConfig;
import com.opensymphony.xwork2.config.impl.DefaultConfiguration;

import junit.framework.TestCase;

public class ClasspathPackageProviderTest extends TestCase {

    ClasspathPackageProvider provider;
    Configuration config;

    public void setUp() {
        provider = new ClasspathPackageProvider();
        provider.setActionPackages("org.apache.struts2.config");
        config = new DefaultConfiguration();
        PackageConfig strutsDefault = new PackageConfig("struts-default");
        strutsDefault.addResultTypeConfig(new ResultTypeConfig("dispatcher", ServletDispatcherResult.class.getName(), "location"));
        strutsDefault.setDefaultResultType("dispatcher");
        config.addPackageConfig("struts-default", strutsDefault);
        PackageConfig customPackage = new PackageConfig("custom-package");
        customPackage.setNamespace("/custom");
        config.addPackageConfig("custom-package", customPackage);
        provider.init(config);
        provider.loadPackages();
    }

    public void testFoundRootPackages() {
        assertEquals(5, config.getPackageConfigs().size());
        PackageConfig pkg = config.getPackageConfig("org.apache.struts2.config");
        assertNotNull(pkg);
        Map configs = pkg.getActionConfigs();
        assertNotNull(configs);
        // assertEquals(1, configs.size());
        ActionConfig actionConfig = (ActionConfig) configs.get("customParentPackage");
        assertNotNull(actionConfig);
    }

    public void testParentPackage() {
        PackageConfig pkg = config.getPackageConfig("org.apache.struts2.config");
        // assertEquals(2, pkg.getParents().size());
        Map configs = pkg.getActionConfigs();
        ActionConfig config = (ActionConfig) configs.get("customParentPackage");
        assertNotNull(config);
        assertEquals("/custom", pkg.getNamespace());
    }

    public void testCustomNamespace() {
        PackageConfig pkg = config.getPackageConfig("org.apache.struts2.config.CustomNamespaceAction");
        Map configs = pkg.getAllActionConfigs();
        // assertEquals(2, configs.size());
        ActionConfig config = (ActionConfig) configs.get("customNamespace");
        assertEquals(config.getPackageName(), pkg.getName());
        assertEquals(1, pkg.getParents().size());
        assertNotNull(config);
        assertEquals("/mynamespace", pkg.getNamespace());
        ActionConfig ac = (ActionConfig) configs.get("customParentPackage");
        assertNotNull(ac);
    }

    public void testResultAnnotations() {
        PackageConfig pkg = config.getPackageConfig("org.apache.struts2.config.cltest");
        assertEquals("/cltest", pkg.getNamespace());
        ActionConfig acfg = pkg.getActionConfigs().get("twoResult");
        assertNotNull(acfg);
        assertEquals(3, acfg.getResults().size());
    }

    public void testActionImplementation() {
        PackageConfig pkg = config.getPackageConfig("org.apache.struts2.config.cltest");
        assertEquals("/cltest", pkg.getNamespace());
        ActionConfig acfg = pkg.getActionConfigs().get("actionImpl");
        assertNotNull(acfg);
    }
}
