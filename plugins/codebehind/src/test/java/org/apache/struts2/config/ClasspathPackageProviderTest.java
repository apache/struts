/*
 * $Id$
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

import com.opensymphony.xwork2.config.Configuration;
import com.opensymphony.xwork2.config.entities.ActionConfig;
import com.opensymphony.xwork2.config.entities.PackageConfig;
import com.opensymphony.xwork2.config.entities.ResultTypeConfig;
import com.opensymphony.xwork2.config.impl.DefaultConfiguration;
import junit.framework.TestCase;
import org.apache.struts2.dispatcher.ServletDispatcherResult;

import java.util.Map;

public class ClasspathPackageProviderTest extends TestCase {

    ClasspathPackageProvider provider;
    Configuration config;

    public void setUp() throws Exception {
        provider = new ClasspathPackageProvider();
        provider.setActionPackages("org.apache.struts2.config");
        config = createNewConfiguration();
        provider.init(config);
        provider.loadPackages();
    }

    private Configuration createNewConfiguration() {
        Configuration config = new DefaultConfiguration();
        PackageConfig strutsDefault = new PackageConfig.Builder("struts-default")
                .addResultTypeConfig(new ResultTypeConfig.Builder("dispatcher", ServletDispatcherResult.class.getName())
                        .defaultResultParam("location")
                        .build())
                .defaultResultType("dispatcher")
                .build();
        config.addPackageConfig("struts-default", strutsDefault);
        PackageConfig customPackage = new PackageConfig.Builder("custom-package")
            .namespace("/custom")
            .build();
        config.addPackageConfig("custom-package", customPackage);
        return config;
    }

    public void tearDown() throws Exception {
        provider = null;
        config = null;
    }

    public void testFoundRootPackages() {
        assertEquals(7, config.getPackageConfigs().size());
        PackageConfig pkg = config.getPackageConfig("org.apache.struts2.config");
        assertNotNull(pkg);
        Map configs = pkg.getActionConfigs();
        assertNotNull(configs);
        // assertEquals(1, configs.size());
        ActionConfig actionConfig = (ActionConfig) configs.get("customParentPackage");
        assertNotNull(actionConfig);
    }
    
    public void testDisableScanning() {
        provider = new ClasspathPackageProvider();
        provider.setActionPackages("org.apache.struts2.config");
        provider.setDisableActionScanning("true");
        config = new DefaultConfiguration();
        provider.init(config);
        provider.loadPackages();
        
        assertEquals(0, config.getPackageConfigs().size());
    }

    public void testParentPackage() {
        PackageConfig pkg = config.getPackageConfig("org.apache.struts2.config");
        // assertEquals(2, pkg.getParents().size());
        Map configs = pkg.getActionConfigs();
        ActionConfig config = (ActionConfig) configs.get("customParentPackage");
        assertNotNull(config);
        assertEquals("/custom", pkg.getNamespace());
    }

    public void testParentPackageOnPackage() {
        provider = new ClasspathPackageProvider();
        provider.setActionPackages("org.apache.struts2.config.parenttest");
        provider.init(createNewConfiguration());
        provider.loadPackages();


        PackageConfig pkg = config.getPackageConfig("org.apache.struts2.config.parenttest");
        // assertEquals(2, pkg.getParents().size());
        assertNotNull(pkg);

        assertEquals("custom-package", pkg.getParents().get(0).getName());
        Map configs = pkg.getActionConfigs();
        ActionConfig config = (ActionConfig) configs.get("some");
        assertNotNull(config);
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
    
    public void testCustomActionAnnotation() {
        PackageConfig pkg = config.getPackageConfig("org.apache.struts2.config.AnnotatedAction");
        Map configs = pkg.getAllActionConfigs();
        // assertEquals(2, configs.size());
        ActionConfig config = (ActionConfig) configs.get("myaction");
        assertNotNull(config);
    }
    
    public void testCustomActionAnnotationOfAnyName() {
        PackageConfig pkg = config.getPackageConfig("org.apache.struts2.config");
        Map configs = pkg.getAllActionConfigs();
        // assertEquals(2, configs.size());
        ActionConfig config = (ActionConfig) configs.get("myaction2");
        assertNotNull(config);
    }
    
    public void testResultAnnotations() {
        PackageConfig pkg = config.getPackageConfig("org.apache.struts2.config.cltest");
        assertEquals("/cltest", pkg.getNamespace());
        ActionConfig acfg = pkg.getActionConfigs().get("twoResult");
        assertNotNull(acfg);
        assertEquals(2, acfg.getResults().size());
        assertEquals("input.jsp", acfg.getResults().get("input").getParams().get("location"));
        assertEquals("bob", acfg.getResults().get("chain").getParams().get("location"));

        acfg = pkg.getActionConfigs().get("oneResult");
        assertNotNull(acfg);
        assertEquals(1, acfg.getResults().size());
        assertEquals("input-parent.jsp", acfg.getResults().get("input").getParams().get("location"));
    }

    public void testActionImplementation() {
        PackageConfig pkg = config.getPackageConfig("org.apache.struts2.config.cltest");
        assertEquals("/cltest", pkg.getNamespace());
        ActionConfig acfg = pkg.getActionConfigs().get("actionImpl");
        assertNotNull(acfg);
    }
}
