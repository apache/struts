/*
 * $Id: $
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

import org.apache.struts2.dispatcher.ServletDispatcherResult;

import com.opensymphony.xwork2.config.Configuration;
import com.opensymphony.xwork2.config.entities.PackageConfig;
import com.opensymphony.xwork2.config.entities.ResultTypeConfig;
import com.opensymphony.xwork2.config.entities.ActionConfig;
import com.opensymphony.xwork2.config.impl.DefaultConfiguration;
import com.opensymphony.xwork2.ActionSupport;

import junit.framework.TestCase;

/**
 * Preliminary test to define parameters of MethodConfigurationProvide. WORK IN PROGESS.
 */
public class MethodConfigurationProviderTest extends TestCase {

    MethodConfigurationProvider provider;
    Configuration config;

    public void setUp() {

        config = new DefaultConfiguration();

        PackageConfig strutsDefault = new PackageConfig("struts-default");
        strutsDefault.addResultTypeConfig(new ResultTypeConfig("dispatcher", ServletDispatcherResult.class.getName(), "location"));
        strutsDefault.setDefaultResultType("dispatcher");
        config.addPackageConfig("struts-default", strutsDefault);

        PackageConfig customPackage = new PackageConfig("custom-package");
        customPackage.setNamespace("/custom");
        ActionConfig action = new ActionConfig(null, ActionSupport.class, null, null, null);
        customPackage.addActionConfig("action",action);
        config.addPackageConfig("custom-package", customPackage);

        provider = new MethodConfigurationProvider();
        provider.init(config);
        provider.loadPackages();
    }

    private PackageConfig getCustom() {
        return config.getPackageConfig("custom-package");
    }

    /**
     * Confirms baseline setup works as expected.
     */
    public void off_testSetup() {
        assertEquals(2, config.getPackageConfigs().size());
        PackageConfig struts = config.getPackageConfig("struts-default");
        assertNotNull(struts);
        PackageConfig custom = getCustom();
        assertNotNull(custom);
        assertEquals(0,struts.getActionConfigs().size());
        assertTrue("testSetup: Expected ActionConfigs to be added!", 1 <struts.getActionConfigs().size());
    }

    /**
     * Confirms system detects other non-void, no-argument methods
     * on the class of default actions, and that it creates an ActionConfig
     * matching the default action.
     */
    public void off_testQualifyingMethods() {

        PackageConfig custom = getCustom();

        boolean baseline = custom.getActionConfigs().containsKey("action");
        assertTrue("The root action is missing!",baseline);

        boolean action_execute = custom.getActionConfigs().containsKey("action!execute");
        assertFalse("The execute method should not have an ActionConfig!",action_execute);

        boolean action_validate = custom.getActionConfigs().containsKey("action!validate");
        assertTrue("Expected an ActionConfig for the validate method",action_validate);

    }

    /**
     * Confirms system excludes methods that are not non-void and no-argument or begin with "get".
     */
    public void testExcludedMethods() {

        PackageConfig custom = getCustom();

        boolean action_getLocale = custom.getActionConfigs().containsKey("action!getLocale");
        assertFalse("A 'get' method has an ActionConfig!",action_getLocale);

        boolean action_pause = custom.getActionConfigs().containsKey("action!pause");
        assertFalse("A void method with arguments has an ActionConfig!",action_pause);
                
    }

    /**
     * Confirms system does not create an ActionConfig for
     * methods that already have an action.
     */
    public void testCustomMethods() {

        ActionConfig action_validate = getCustom().getActionConfigs().get("action!validate");
        // TODO

    }

    /**
     * Confirms system creates an ActionConfig that matches default action.
      */
    public void testActionConfig() {

        ActionConfig action_input = getCustom().getActionConfigs().get("action!input");
        // TODO

    }

}

