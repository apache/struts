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
package org.apache.struts2;

import com.opensymphony.xwork2.XWorkTestCase;
import org.apache.commons.lang3.builder.EqualsBuilder;
import org.apache.struts2.dispatcher.Dispatcher;
import org.apache.struts2.dispatcher.PrepareOperations;
import org.apache.struts2.util.StrutsTestCaseHelper;
import org.apache.struts2.views.jsp.StrutsMockServletContext;
import java.util.HashMap;
import java.util.Map;

/**
 * Base test case for JUnit testing Struts.
 */
public abstract class StrutsInternalTestCase extends XWorkTestCase {

    protected StrutsMockServletContext servletContext;
    protected Dispatcher dispatcher;

    /**
     * Sets up the configuration settings, XWork configuration, and
     * message resources
     * 
     * @throws java.lang.Exception
     */
    @Override
    protected void setUp() throws Exception {
        super.setUp();
        PrepareOperations.clearDevModeOverride();  // Clear DevMode override every time (consistent ThreadLocal state for tests).
        initDispatcher(null);
    }
    
    protected Dispatcher initDispatcher(Map<String,String> params) {
        servletContext = new StrutsMockServletContext();
        dispatcher = StrutsTestCaseHelper.initDispatcher(servletContext, params);
        configurationManager = dispatcher.getConfigurationManager();
        configuration = configurationManager.getConfiguration();
        container = configuration.getContainer();
        container.inject(dispatcher);
        return dispatcher;
    }

    /**
     * Init Dispatcher with provided comma delimited list of xml configs to use, ie:
     * initDispatcherWithConfigs("struts-default.xml,test-struts-config.xml")
     *
     * @param configs comma delimited list of config files
     * @return instance of {@see Dispatcher}
     */
    protected Dispatcher initDispatcherWithConfigs(String configs) {
        Map<String, String> params = new HashMap<String, String>();
        params.put("config", configs);
        return initDispatcher(params);
    }

    @Override
    protected void tearDown() throws Exception {
        super.tearDown();
        // maybe someone else already destroyed Dispatcher
        if (dispatcher != null && dispatcher.getConfigurationManager() != null) {
            dispatcher.cleanup();
            dispatcher = null;
        }
        StrutsTestCaseHelper.tearDown();
    }

    /**
     * Compare if two objects are considered equal according to their fields as accessed 
     * via reflection.
     * 
     * Utilizes {@link EqualsBuilder#reflectionEquals(java.lang.Object, java.lang.Object, boolean)} to perform 
     * the check, and compares transient fields as well.  This may fail when run while a security manager is
     * active, due to a need to user reflection.
     * 
     * 
     * @param obj1 the first {@link Object} to compare against the other.
     * @param obj2 the second {@link Object} to compare against the other.
     * @return true if the objects are equal based on field comparisons by reflection, false otherwise.
     */
    protected boolean objectsAreReflectionEqual(Object obj1, Object obj2) {
        boolean result = false;
        if (obj1 == obj2) {
            result = true;
        } else if (obj1 != null && obj2 != null) {
            result = EqualsBuilder.reflectionEquals(obj1, obj2, true);
        }
        return result;
    }
}
