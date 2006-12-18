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
import org.apache.struts2.dispatcher.Dispatcher;
import org.springframework.mock.web.MockServletContext;

import com.opensymphony.xwork2.config.Configuration;
import com.opensymphony.xwork2.config.ConfigurationManager;
import com.opensymphony.xwork2.config.entities.*;
import com.opensymphony.xwork2.config.impl.DefaultConfiguration;
import com.opensymphony.xwork2.ActionSupport;

import junit.framework.TestCase;

import java.util.Map;
import java.util.HashMap;
import java.util.List;

/**
 * MethodConfigurationProviderTest exercises the MethodConfigurationProvider
 * to confirm that only the expected methods are generated.
 */
public class MethodConfigurationProviderTest extends TestCase {

    /**
     * Object under test.
     */
    MethodConfigurationProvider provider;

    /**
     * Set of packages and ActionConfigs to exercise.
     */
    Configuration configuration;

    /**
     * Mock dispatcher.
     */
    Dispatcher dispatcher;

    /**
     * Creates a mock Dispatcher and seeds Configuration.
     */
    public void setUp() {

        InternalConfigurationManager configurationManager = new InternalConfigurationManager();
        dispatcher = new Dispatcher(new MockServletContext(), new HashMap<String, String>());
        dispatcher.setConfigurationManager(configurationManager);
        dispatcher.init();
        Dispatcher.setInstance(dispatcher);

        configuration = new DefaultConfiguration();
        // empty package for the "default" namespace of empty String
        PackageConfig strutsDefault = new PackageConfig("struts-default");
        strutsDefault.addResultTypeConfig(new ResultTypeConfig("dispatcher", ServletDispatcherResult.class.getName(), "location"));
        strutsDefault.setDefaultResultType("dispatcher");
        configuration.addPackageConfig("struts-default", strutsDefault);

        // custom package with various actions
        PackageConfig customPackage = new PackageConfig("trick-package");
        customPackage.setNamespace("/trick");
        // action that specifies ActionSupport (not empty) but with no methods
        ActionConfig action = new ActionConfig(null, ActionSupport.class, null, null, null);
        customPackage.addActionConfig("action",action);
        // action that species a custom Action with a manual method
        ActionConfig custom = new ActionConfig(null, Custom.class, null, null, null);
        customPackage.addActionConfig("custom",custom);
        // action for manual method, with params, to prove it is not overwritten
        Map params = new HashMap();
        params.put("name","value");
        ActionConfig manual = new ActionConfig("manual", Custom.class, params, null, null);
        customPackage.addActionConfig("custom!manual",manual);
        configuration.addPackageConfig("trick-package", customPackage);

        provider = new MethodConfigurationProvider();
        provider.init(configuration);
        provider.loadPackages();
    }

    /**
     * Provides the "custom-package" configuration.
     * @return the "custom-package" configuration.
     */
    private PackageConfig getCustom() {
        return configuration.getPackageConfig("trick-package");
    }

    /**
     * Confirms baseline setup works as expected.
     */
    public void testSetup() {
        assertEquals(2, configuration.getPackageConfigs().size());
        PackageConfig struts = configuration.getPackageConfig("struts-default");
        assertNotNull(struts);
        assertTrue("testSetup: Expected struts-default to be empty!", struts.getActionConfigs().size() == 0);

        PackageConfig custom = getCustom();
        assertNotNull(custom);
        assertTrue("testSetup: Expected ActionConfigs to be added!", custom.getActionConfigs().size() > 0);
    }

    /**
     * Confirms that system detects no-argument methods that return Strings
     * and generates the appropriate ActionConfigs.
     */
    public void testQualifyingMethods() {

        PackageConfig config = getCustom();

        boolean action = config.getActionConfigs().containsKey("action");
        assertTrue("The root action is missing!",action);

        boolean custom = config.getActionConfigs().containsKey("custom");
        assertTrue("The custom action is missing!",custom);

        boolean action_input = getCustom().getActionConfigs().containsKey("action!input");
        assertTrue("The Action.input method should have an action mapping!",action_input);

        boolean custom_input = getCustom().getActionConfigs().containsKey("custom!input");
        assertTrue("The Custom.input method should have an action mapping!",custom_input);

        boolean custom_auto = getCustom().getActionConfigs().containsKey("custom!auto");
        assertTrue("The Custom.auto method should have an action mapping!",custom_auto);

        boolean custom_gettysburg = getCustom().getActionConfigs().containsKey("custom!gettysburg");
        assertTrue("The Custom.gettysburg method should have an action mapping!",custom_gettysburg);
    }

    /**
     * Confirms system excludes methods that do not return Strings
     * and no-argument or begin with "getx" or "isX".
     */
    public void testExcludedMethods() {

        PackageConfig custom = getCustom();

        boolean action_toString = custom.getActionConfigs().containsKey("action!toString");
        assertFalse("The toString has an ActionConfig!",action_toString);

        boolean action_execute = custom.getActionConfigs().containsKey("action!execute");
        assertFalse("The execute has an ActionConfig!",action_execute);

        boolean action_get_method = custom.getActionConfigs().containsKey("action!getLocale");
        assertFalse("A 'getX' method has an ActionConfig!",action_get_method);

        boolean action_is_method = custom.getActionConfigs().containsKey("custom!isIt");
        assertFalse("A 'isX' method has an ActionConfig!",action_is_method);

        boolean void_method = custom.getActionConfigs().containsKey("action!validate");
        assertFalse("A void method has an ActionConfig!",void_method);

        boolean void_with_parameters = custom.getActionConfigs().containsKey("action!addActionMessage");
        assertFalse("A void method with parameters has an ActionConfig!",void_with_parameters);

        boolean return_method = custom.getActionConfigs().containsKey("action!hasActionErrors");
        assertFalse("A method with a return type other than String has an ActionConfig!",return_method);

        ActionConfig manual = getCustom().getActionConfigs().get("custom!manual");
        Object val = manual.getParams().get("name");
        assertTrue("The custom.Manual method was generated!","value".equals(val.toString()));
    }

    /**
     * Custom is a test Action class.
     */
    public class Custom extends ActionSupport {

        /**
         * Tests ordinary methods.
         * @return SUCCESS
         */
        public String custom() {
            return SUCCESS;
        }

        /**
         * Tests JavaBean property.
         * @return SUCCESS
         */
        public boolean isIt() {
            return true;
        }

        /**
         * Tests manual override.
         * @return SUCCESS
         */
        public String manual() {
            return SUCCESS;
        }

        /**
         * Tests dynamic configuration.
         * @return SUCCESS
         */
        public String auto() {
            return SUCCESS;
        }

        /**
         * Tests method that looks like a JavaBean property.
         * @return SUCCESS
         */
        public String gettysburg() {
            return SUCCESS;
        }
    }

    /**
     * InternalConfigurationManager is a mock ConfigurationManager.
     */
    class InternalConfigurationManager extends ConfigurationManager {
    	public boolean destroyConfiguration = false;

    	@Override
    	public synchronized void destroyConfiguration() {
    		super.destroyConfiguration();
    		destroyConfiguration = true;
    	}
    }

}
